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

import org.eclipse.wb.core.editor.structure.property.PropertyListProcessor;
import org.eclipse.wb.core.model.JavaInfo;
import org.eclipse.wb.core.model.ObjectInfo;
import org.eclipse.wb.internal.core.model.property.ComplexProperty;
import org.eclipse.wb.internal.core.model.property.Property;
import org.eclipse.wb.internal.core.model.property.category.PropertyCategory;
import org.eclipse.wb.internal.core.model.util.PropertyUtils;
import org.eclipse.wb.internal.core.utils.execution.ExecutionUtils;
import org.eclipse.wb.internal.core.utils.execution.RunnableEx;

import java.util.List;

/**
 * {@link PropertyListProcessor} for AMS specific configuration.
 * 
 * @author scheglov_ke
 * @coverage swing.AMS
 */
public final class AmsPropertyListProcessor implements PropertyListProcessor {
  ////////////////////////////////////////////////////////////////////////////
  //
  // Instance
  //
  ////////////////////////////////////////////////////////////////////////////
  public static final PropertyListProcessor INSTANCE = new AmsPropertyListProcessor();

  private AmsPropertyListProcessor() {
  }

  ////////////////////////////////////////////////////////////////////////////
  //
  // PropertyList_Processor
  //
  ////////////////////////////////////////////////////////////////////////////
  public void process(List<ObjectInfo> objects, final List<Property> properties) {
    final JavaInfo javaInfo = ConfigurationReader.getJavaInfoAMS(objects);
    if (javaInfo != null) {
      ExecutionUtils.runLog(new RunnableEx() {
        public void run() throws Exception {
          process(javaInfo, properties);
        }
      });
    }
  }

  /**
   * Processes {@link Property}-s of AMS component.
   */
  private void process(JavaInfo javaInfo, List<Property> properties) throws Exception {
    List<PropertyGroup> groups = ConfigurationReader.getPropertyGroups(javaInfo);
    if (!groups.isEmpty()) {
      List<Property> newProperties = Lists.newArrayList();
      for (PropertyGroup group : groups) {
        ComplexProperty groupProperty = getGroupProperty(javaInfo, group);
        List<Property> groupProperties = extractGroupProperties(properties, group);
        // set properties
        groupProperty.setProperties(groupProperties);
        newProperties.add(groupProperty);
      }
      // replace properties
      properties.clear();
      properties.addAll(newProperties);
    }
  }

  private static ComplexProperty getGroupProperty(JavaInfo javaInfo, PropertyGroup group) {
    ComplexProperty groupProperty = (ComplexProperty) javaInfo.getArbitraryValue(group);
    if (groupProperty == null) {
      groupProperty = new ComplexProperty(group.getName(), group.getDescription());
      {
        PropertyCategory category = group.getCategory();
        if (category != null) {
          groupProperty.setCategory(category);
        }
      }
      javaInfo.putArbitraryValue(group, groupProperty);
    }
    return groupProperty;
  }

  private static List<Property> extractGroupProperties(List<Property> properties,
      PropertyGroup group) {
    List<Property> groupProperties = Lists.newArrayList();
    for (PropertyConfiguration propertyConfiguration : group.getProperties()) {
      String propertyName = propertyConfiguration.getName();
      if (propertyName != null) {
        Property property = PropertyUtils.getByTitle(properties, propertyName);
        if (property != null) {
          properties.remove(property);
          groupProperties.add(property);
        }
      } else {
        groupProperties.addAll(properties);
      }
    }
    return groupProperties;
  }
}
