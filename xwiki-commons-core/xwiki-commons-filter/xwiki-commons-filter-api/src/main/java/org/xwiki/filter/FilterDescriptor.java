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
package org.xwiki.filter;

import java.util.HashMap;
import java.util.Map;

import org.xwiki.stability.Unstable;

/**
 * List all event found in a filter.
 * 
 * @version $Id$
 * @since 5.2M1
 */
@Unstable
public class FilterDescriptor
{
    /**
     * @see #getElements()
     */
    private Map<String, FilterElement> elements = new HashMap<String, FilterElement>();

    /**
     * @return the filter elements
     */
    public Map<String, FilterElement> getElements()
    {
        return this.elements;
    }

    /**
     * @param name the name of the filter element
     * @return the filter element
     */
    public FilterElement getElement(String name)
    {
        return this.elements.get(name.toLowerCase());
    }
}
