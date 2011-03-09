/*******************************************************************************
 * Copyright (c) 2011 Google, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Google, Inc. - initial API and implementation
 *******************************************************************************/
package org.eclipse.wb.internal.swing.ams.model.property;

import com.google.common.collect.Lists;

import org.eclipse.wb.internal.core.model.property.category.PropertyCategory;

import java.util.List;

/**
 * Group of {@link PropertyConfiguration}.
 * 
 * @author scheglov_ke
 * @coverage swing.AMS
 */
public final class PropertyGroup {
  private final String m_name;
  private final String m_description;
  private final PropertyCategory m_category;
  private final List<PropertyConfiguration> m_properties = Lists.newArrayList();

  ////////////////////////////////////////////////////////////////////////////
  //
  // Constructor
  //
  ////////////////////////////////////////////////////////////////////////////
  public PropertyGroup(String name, String description, PropertyCategory category) {
    m_name = name;
    m_description = description != null ? description : "";
    m_category = category;
  }

  ////////////////////////////////////////////////////////////////////////////
  //
  // Object
  //
  ////////////////////////////////////////////////////////////////////////////
  @Override
  public String toString() {
    return "Group(" + m_name + "," + m_category + "," + m_properties + ")";
  }

  ////////////////////////////////////////////////////////////////////////////
  //
  // Access
  //
  ////////////////////////////////////////////////////////////////////////////
  public String getName() {
    return m_name;
  }

  public String getDescription() {
    return m_description;
  }

  public PropertyCategory getCategory() {
    return m_category;
  }

  ////////////////////////////////////////////////////////////////////////////
  //
  // Properties
  //
  ////////////////////////////////////////////////////////////////////////////
  public List<PropertyConfiguration> getProperties() {
    return m_properties;
  }

  public void addProperty(PropertyConfiguration property) {
    m_properties.add(property);
  }
}
