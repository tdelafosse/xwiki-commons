/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.xwiki.environment.internal;

import java.io.File;

import javax.servlet.ServletContext;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.xwiki.component.embed.EmbeddableComponentManager;
import org.xwiki.component.util.ReflectionUtils;
import org.xwiki.environment.Environment;

import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link ServletEnvironment}.
 *
 * @version $Id$
 * @since 3.5M1
 */
public class ServletEnvironmentTest
{
    private File servletTmpDir;

    private File systemTmpDir;

    private ServletEnvironment environment;

    @Before
    public void setUp() throws Exception
    {
        this.servletTmpDir = new File(System.getProperty("java.io.tmpdir"), "ServletEnvironmentTest-tmpDir");
        this.systemTmpDir = new File(System.getProperty("java.io.tmpdir"), "xwiki-temp");

        EmbeddableComponentManager ecm = new EmbeddableComponentManager();
        ecm.initialize(getClass().getClassLoader());
        this.environment = (ServletEnvironment) ecm.getInstance(Environment.class);
    }

    @After
    public void tearDown() throws Exception
    {
        FileUtils.deleteQuietly(this.servletTmpDir);
    }

    @Test
    public void getResourceWhenServletContextNotSet()
    {
        try {
            this.environment.getResource("/whatever");
            Assert.fail();
        } catch (RuntimeException expected) {
            Assert.assertEquals("The Servlet Environment has not been properly initialized "
                + "(The Servlet Context is not set)", expected.getMessage());
        }
    }
    
    @Test
    public void getResourceOk() throws Exception
    {
        final ServletContext servletContext = mock(ServletContext.class);
        this.environment.setServletContext(servletContext);
        this.environment.getResource("/test");

        verify(servletContext).getResource("/test");
    }

    @Test
    public void getResourceAsStreamOk() throws Exception
    {
        final ServletContext servletContext = mock(ServletContext.class);
        this.environment.setServletContext(servletContext);
        this.environment.getResourceAsStream("/test");

        verify(servletContext).getResourceAsStream("/test");
    }


    @Test
    public void getPermanentDirectoryWhenSetWithAPI() throws Exception
    {
        File permanentDirectory = new File("/permanent");
        this.environment.setPermanentDirectory(permanentDirectory);
        Assert.assertEquals(permanentDirectory.getCanonicalFile(),
            this.environment.getPermanentDirectory().getCanonicalFile());
    }

    @Test
    public void getPermanentDirectoryWhenSetWithSystemProperty() throws Exception
    {
        File expectedPermanentDirectory = new File(System.getProperty("java.io.tmpdir"), "permanent");
        System.setProperty("xwiki.data.dir", expectedPermanentDirectory.toString());

        try {
            this.environment.setServletContext(mock(ServletContext.class));

            Assert.assertEquals(expectedPermanentDirectory.getCanonicalFile(),
                this.environment.getPermanentDirectory().getCanonicalFile());
        } finally {
            System.clearProperty("xwiki.data.dir");
        }
    }

    @Test
    public void getPermanentDirectoryWhenNotSet() throws Exception
    {
        final ServletContext servletContext = mock(ServletContext.class);
        when(servletContext.getAttribute("javax.servlet.context.tempdir")).thenReturn(this.servletTmpDir);
        this.environment.setServletContext(servletContext);

        final Logger logger = mock(Logger.class);
        ReflectionUtils.setFieldValue(this.environment, "logger", logger);

        Assert.assertEquals(this.servletTmpDir.getCanonicalFile(),
            this.environment.getPermanentDirectory().getCanonicalFile());

        // Also verify that we log a warning!
        verify(logger).warn("No permanent directory configured. Using temporary directory [{}].",
            System.getProperty("java.io.tmpdir"));
    }

    @Test
    public void getTemporaryDirectory() throws Exception
    {
        File tmpDir = new File("tmpdir");
        this.environment.setTemporaryDirectory(tmpDir);
        Assert.assertEquals(tmpDir.getCanonicalFile(), this.environment.getTemporaryDirectory().getCanonicalFile());
    }

    @Test
    public void getTemporaryDirectoryWhenNotSet() throws Exception
    {
        final ServletContext servletContext = mock(ServletContext.class);
        when(servletContext.getAttribute("javax.servlet.context.tempdir")).thenReturn(this.servletTmpDir);
        this.environment.setServletContext(servletContext);

        final File tmpDir = this.environment.getTemporaryDirectory();

        // Make sure it is the "xwiki-temp" dir which is under the main temp dir.
        Assert.assertEquals(this.servletTmpDir.listFiles()[0].getCanonicalFile(), tmpDir.getCanonicalFile());
    }

    /**
     * Verify we default to the system tmp dir if the Servlet tmp dir is not set.
     */
    @Test
    public void getTemporaryDirectoryWhenServletTempDirNotSet() throws Exception
    {
        final ServletContext servletContext = mock(ServletContext.class);
        this.environment.setServletContext(servletContext);

        Assert.assertEquals(this.systemTmpDir.getCanonicalFile(),
            this.environment.getTemporaryDirectory().getCanonicalFile());

        // Verify that servletContext.getAttribute was called (and that we returned null - this happens because we
        // didn't set any stubbing on servletContext and null is the default returned by Mockito).
        verify(servletContext).getAttribute("javax.servlet.context.tempdir");
    }
}
