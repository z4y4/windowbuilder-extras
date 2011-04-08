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

import org.eclipse.wb.core.model.JavaInfo;
import org.eclipse.wb.core.model.ObjectInfo;
import org.eclipse.wb.internal.core.model.JavaInfoUtils;
import org.eclipse.wb.internal.core.model.property.category.PropertyCategory;
import org.eclipse.wb.internal.core.utils.check.Assert;
import org.eclipse.wb.internal.swing.ams.Activator;
import org.eclipse.wb.internal.swing.ams.Messages;

import org.eclipse.jdt.core.IJavaProject;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.Rule;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.impl.NoOpLog;
import org.xml.sax.Attributes;

import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;

/**
 * Reader for configuration bound to {@link JavaInfo}.
 * 
 * @author scheglov_ke
 * @coverage swing.AMS
 */
public final class ConfigurationReader {
  private static final Object KEY_GROUPS = ConfigurationReader.class.getName() + ".KEY_GROUPS";

  /**
   * @return the {@link JavaInfo} of AMS component, may be <code>null</code>.
   */
  public static JavaInfo getJavaInfoAMS(List<ObjectInfo> objects) {
    if (objects.size() == 1 && objects.get(0) instanceof JavaInfo) {
      final JavaInfo javaInfo = (JavaInfo) objects.get(0);
      Class<?> componentClass = javaInfo.getDescription().getComponentClass();
      if (componentClass != null && componentClass.getName().startsWith("ams.zpointcs.")) {
        return javaInfo;
      }
    }
    return null;
  }

  /**
   * @return {@link PropertyGroup}-s specific for {@link IJavaProject} of {@link JavaInfo} or
   *         global.
   */
  @SuppressWarnings("unchecked")
  public static List<PropertyGroup> getPropertyGroups(JavaInfo javaInfo) throws Exception {
    List<PropertyGroup> groups =
        (List<PropertyGroup>) javaInfo.getRoot().getArbitraryValue(KEY_GROUPS);
    if (groups == null) {
      groups = createPropertyGroups(javaInfo);
      javaInfo.getRoot().putArbitraryValue(KEY_GROUPS, groups);
    }
    return groups;
  }

  private static List<PropertyGroup> createPropertyGroups(JavaInfo javaInfo) throws Exception {
    String resourcePath = "wbp-meta/AMS.property-tweaks.xml";
    // try to find configuration in ClassLoader
    {
      Enumeration<URL> resources =
          JavaInfoUtils.getClassLoader(javaInfo).getResources(resourcePath);
      while (resources.hasMoreElements()) {
        URL url = resources.nextElement();
        InputStream input = url.openStream();
        return parsePropertyGroups(input);
      }
    }
    // use configuration from Bundle
    InputStream input = Activator.getFile(resourcePath);
    return parsePropertyGroups(input);
  }

  private static List<PropertyGroup> parsePropertyGroups(InputStream input) throws Exception {
    final List<PropertyGroup> groups = Lists.newArrayList();
    // prepare Digester
    Digester digester;
    {
      digester = new Digester();
      digester.setLogger(new NoOpLog());
      // groups/group
      {
        String pattern = "groups/group";
        digester.addRule(pattern, new Rule() {
          @Override
          public void begin(String namespace, String element, Attributes attributes)
              throws Exception {
            // prepare required name
            String name = attributes.getValue("name");
            Assert.isNotNull(name, Messages.ConfigurationReader_errGroup_noNameAttribute);
            // prepare optional description
            String description = attributes.getValue("description");
            Assert.isNotNull(name, Messages.ConfigurationReader_errGroup_noDescriptionAttribute);
            // prepare optional category
            PropertyCategory category = null;
            {
              String categoryString = attributes.getValue("category");
              if (categoryString != null) {
                category = PropertyCategory.get(categoryString, null);
              }
            }
            // add group
            PropertyGroup group = new PropertyGroup(name, description, category);
            digester.push(group);
            groups.add(group);
          }

          @Override
          public void end(String namespace, String name) throws Exception {
            digester.pop();
            super.end(namespace, name);
          }
        });
      }
      // groups/group/property
      {
        String pattern = "groups/group/property";
        digester.addRule(pattern, new Rule() {
          @Override
          public void begin(String namespace, String name, Attributes attributes) throws Exception {
            // prepare required name
            String propertyName = attributes.getValue("name");
            Assert.isNotNull(propertyName, Messages.ConfigurationReader_errProperty_noNameAttribute);
            // prepare optional category
            PropertyCategory category = null;
            {
              String categoryString = attributes.getValue("category");
              if (categoryString != null) {
                category = PropertyCategory.get(categoryString, null);
              }
            }
            // add property
            PropertyGroup group = (PropertyGroup) digester.peek();
            group.addProperty(new PropertyConfiguration(propertyName, category));
          }
        });
      }
      // groups/group/other-properties
      {
        String pattern = "groups/group/other-properties";
        digester.addRule(pattern, new Rule() {
          @Override
          public void begin(String namespace, String name, Attributes attributes) throws Exception {
            PropertyGroup group = (PropertyGroup) digester.peek();
            group.addProperty(new PropertyConfiguration(null, null));
          }
        });
      }
    }
    // read XML
    try {
      digester.push(groups);
      digester.parse(input);
    } finally {
      IOUtils.closeQuietly(input);
    }
    // done
    return groups;
  }
}
