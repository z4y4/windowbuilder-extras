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

import org.eclipse.wb.core.editor.structure.property.PropertyCategoryProviderProvider;
import org.eclipse.wb.core.model.JavaInfo;
import org.eclipse.wb.core.model.ObjectInfo;
import org.eclipse.wb.internal.core.model.property.Property;
import org.eclipse.wb.internal.core.model.property.category.PropertyCategory;
import org.eclipse.wb.internal.core.model.property.category.PropertyCategoryProvider;
import org.eclipse.wb.internal.core.utils.execution.ExecutionUtils;
import org.eclipse.wb.internal.core.utils.execution.RunnableObjectEx;

import org.apache.commons.lang.StringUtils;

import java.util.List;

/**
 * {@link PropertyCategoryProviderProvider} for AMS specific configuration.
 * 
 * @author scheglov_ke
 * @coverage swing.AMS
 */
public final class AmsPropertyCategoryProvider2 implements PropertyCategoryProviderProvider {
  ////////////////////////////////////////////////////////////////////////////
  //
  // Instance
  //
  ////////////////////////////////////////////////////////////////////////////
  public static final PropertyCategoryProviderProvider INSTANCE =
      new AmsPropertyCategoryProvider2();

  private AmsPropertyCategoryProvider2() {
  }

  ////////////////////////////////////////////////////////////////////////////
  //
  // PropertyCategoryProvider_Provider
  //
  ////////////////////////////////////////////////////////////////////////////
  public PropertyCategoryProvider get(List<ObjectInfo> objects) {
    final JavaInfo javaInfo = ConfigurationReader.getJavaInfoAMS(objects);
    if (javaInfo != null) {
      return ExecutionUtils.runObjectLog(new RunnableObjectEx<PropertyCategoryProvider>() {
        public PropertyCategoryProvider runObject() throws Exception {
          return new AmsPropertyCategoryProvider(javaInfo);
        }
      }, null);
    }
    return null;
  }

  ////////////////////////////////////////////////////////////////////////////
  //
  // AMS_PropertyCategoryProvider
  //
  ////////////////////////////////////////////////////////////////////////////
  private static final class AmsPropertyCategoryProvider implements PropertyCategoryProvider {
    private final List<PropertyGroup> groups;

    ////////////////////////////////////////////////////////////////////////////
    //
    // Constructor
    //
    ////////////////////////////////////////////////////////////////////////////
    public AmsPropertyCategoryProvider(JavaInfo javaInfo) throws Exception {
      groups = ConfigurationReader.getPropertyGroups(javaInfo);
    }

    ////////////////////////////////////////////////////////////////////////////
    //
    // PropertyCategoryProvider
    //
    ////////////////////////////////////////////////////////////////////////////
    public PropertyCategory getCategory(Property property) {
      for (PropertyGroup group : groups) {
        for (PropertyConfiguration propertyConfiguration : group.getProperties()) {
          if (StringUtils.equals(propertyConfiguration.getName(), property.getTitle())) {
            PropertyCategory category = propertyConfiguration.getCategory();
            if (category != null) {
              return category;
            }
          }
        }
      }
      return property.getCategory();
    }
  }
}
