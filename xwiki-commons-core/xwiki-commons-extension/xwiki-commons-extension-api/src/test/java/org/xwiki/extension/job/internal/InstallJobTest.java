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
package org.xwiki.extension.job.internal;

import org.junit.Assert;
import org.junit.Test;
import org.xwiki.extension.InstalledExtension;
import org.xwiki.extension.LocalExtension;
import org.xwiki.extension.TestResources;
import org.xwiki.extension.handler.ExtensionHandler;
import org.xwiki.extension.test.AbstractExtensionHandlerTest;
import org.xwiki.extension.test.TestExtensionHandler;

public class InstallJobTest extends AbstractExtensionHandlerTest
{
    private TestExtensionHandler handler;

    @Override
    public void setUp() throws Exception
    {
        super.setUp();

        // lookup

        this.handler = (TestExtensionHandler) getComponentManager().getInstance(ExtensionHandler.class, "test");
    }

    @Test
    public void testInstallOnRoot() throws Throwable
    {
        install(TestResources.REMOTE_WITHRANDCDEPENDENCIES_ID);

        // Is extension installed
        InstalledExtension installedExtension =
            this.installedExtensionRepository.getInstalledExtension(
                TestResources.REMOTE_WITHRANDCDEPENDENCIES_ID.getId(), null);
        Assert.assertNotNull(installedExtension);
        Assert.assertTrue(this.handler.getExtensions().get(null).contains(installedExtension));
        Assert.assertNotNull(this.installedExtensionRepository.getInstalledExtension(
            TestResources.REMOTE_WITHRANDCDEPENDENCIES_ID.getId(), "namespace"));
        Assert.assertFalse(installedExtension.isDependency(null));
        Assert.assertFalse(installedExtension.isDependency("namespace"));

        // Is dependency installed
        installedExtension =
            this.installedExtensionRepository.getInstalledExtension(TestResources.REMOTE_SIMPLE_ID.getId(), null);
        Assert.assertNotNull(installedExtension);
        Assert.assertTrue(this.handler.getExtensions().get(null).contains(installedExtension));
        Assert.assertNotNull(this.installedExtensionRepository.getInstalledExtension(
            TestResources.REMOTE_SIMPLE_ID.getId(), "namespace"));
        Assert.assertTrue(installedExtension.isDependency(null));
        Assert.assertTrue(installedExtension.isDependency("namespace"));
    }

    @Test
    public void testInstallRemoteOnNamespace() throws Throwable
    {
        install(TestResources.REMOTE_WITHRANDCDEPENDENCIES_ID, "namespace");

        // Is extension installed
        InstalledExtension installedExtension =
            this.installedExtensionRepository.getInstalledExtension(
                TestResources.REMOTE_WITHRANDCDEPENDENCIES_ID.getId(), "namespace");
        Assert.assertNotNull(installedExtension);
        Assert.assertTrue(this.handler.getExtensions().get("namespace").contains(installedExtension));
        Assert.assertFalse(installedExtension.isDependency(null));
        Assert.assertFalse(installedExtension.isDependency("namespace"));

        // Is dependency installed
        installedExtension =
            this.installedExtensionRepository
                .getInstalledExtension(TestResources.REMOTE_SIMPLE_ID.getId(), "namespace");
        Assert.assertNotNull(installedExtension);
        Assert.assertTrue(this.handler.getExtensions().get("namespace").contains(installedExtension));
        Assert.assertFalse(installedExtension.isDependency(null));
        Assert.assertTrue(installedExtension.isDependency("namespace"));
    }

    @Test
    public void testInstallRemoteOnNamespaces() throws Throwable
    {
        install(TestResources.REMOTE_WITHRANDCDEPENDENCIES_ID, new String[] {"namespace1", "namespace2"});

        LocalExtension installedExtension =
            this.installedExtensionRepository.getInstalledExtension(
                TestResources.REMOTE_WITHRANDCDEPENDENCIES_ID.getId(), "namespace1");
        Assert.assertNotNull(installedExtension);
        Assert.assertNotNull(this.installedExtensionRepository.getInstalledExtension(
            TestResources.REMOTE_WITHRANDCDEPENDENCIES_ID.getId(), "namespace2"));
        Assert.assertTrue(this.handler.getExtensions().get("namespace1").contains(installedExtension));
        Assert.assertTrue(this.handler.getExtensions().get("namespace2").contains(installedExtension));

        installedExtension =
            this.installedExtensionRepository.getInstalledExtension(TestResources.REMOTE_SIMPLE_ID.getId(),
                "namespace1");
        Assert.assertNotNull(installedExtension);
        Assert.assertNotNull(this.installedExtensionRepository.getInstalledExtension(
            TestResources.REMOTE_SIMPLE_ID.getId(), "namespace2"));
        Assert.assertTrue(this.handler.getExtensions().get("namespace1").contains(installedExtension));
        Assert.assertTrue(this.handler.getExtensions().get("namespace2").contains(installedExtension));
    }

    @Test
    public void testUpgradeFirstOnRoot() throws Throwable
    {
        install(TestResources.REMOTE_UPGRADE10_ID);

        LocalExtension installedExtension;

        // Test upgrade

        install(TestResources.REMOTE_UPGRADE20_ID);

        installedExtension =
            this.installedExtensionRepository.getInstalledExtension(TestResources.REMOTE_UPGRADE20_ID.getId(), null);
        Assert.assertNotNull(installedExtension);
        Assert.assertTrue(this.handler.getExtensions().get(null).contains(installedExtension));
        Assert.assertNotNull(this.installedExtensionRepository.getInstalledExtension(
            TestResources.REMOTE_UPGRADE20_ID.getId(), "namespace"));

        Assert.assertNull(installedExtension =
            this.installedExtensionRepository.getInstalledExtension(TestResources.REMOTE_UPGRADE10_ID));

        // Test downgrade

        install(TestResources.REMOTE_UPGRADE10_ID);

        installedExtension =
            this.installedExtensionRepository.getInstalledExtension(TestResources.REMOTE_UPGRADE10_ID.getId(), null);
        Assert.assertNotNull(installedExtension);
        Assert.assertTrue(this.handler.getExtensions().get(null).contains(installedExtension));
        Assert.assertNotNull(this.installedExtensionRepository.getInstalledExtension(
            TestResources.REMOTE_UPGRADE10_ID.getId(), "namespace"));
    }

    @Test
    public void testDowngradeFirstOnRoot() throws Throwable
    {
        install(TestResources.REMOTE_UPGRADE20_ID);

        LocalExtension installedExtension;

        // //////////////////
        // Test downgrade

        install(TestResources.REMOTE_UPGRADE10_ID);

        installedExtension =
            this.installedExtensionRepository.getInstalledExtension(TestResources.REMOTE_UPGRADE10_ID.getId(), null);
        Assert.assertNotNull(installedExtension);
        Assert.assertTrue(this.handler.getExtensions().get(null).contains(installedExtension));
        Assert.assertNotNull(this.installedExtensionRepository.getInstalledExtension(
            TestResources.REMOTE_UPGRADE10_ID.getId(), "namespace"));

        // Test upgrade

        install(TestResources.REMOTE_UPGRADE20_ID);

        installedExtension =
            this.installedExtensionRepository.getInstalledExtension(TestResources.REMOTE_UPGRADE20_ID.getId(), null);
        Assert.assertNotNull(installedExtension);
        Assert.assertTrue(this.handler.getExtensions().get(null).contains(installedExtension));
        Assert.assertNotNull(this.installedExtensionRepository.getInstalledExtension(
            TestResources.REMOTE_UPGRADE20_ID.getId(), "namespace"));
    }
}
