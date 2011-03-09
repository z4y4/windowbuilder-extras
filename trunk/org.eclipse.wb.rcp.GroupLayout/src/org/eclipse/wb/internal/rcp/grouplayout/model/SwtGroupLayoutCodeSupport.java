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
package org.eclipse.wb.internal.rcp.grouplayout.model;

import org.eclipse.wb.core.model.AbstractComponentInfo;
import org.eclipse.wb.core.model.ObjectInfoUtils;
import org.eclipse.wb.internal.core.model.JavaInfoEvaluationHelper;
import org.eclipse.wb.internal.core.utils.ast.AstNodeUtils;
import org.eclipse.wb.internal.layout.group.model.GroupLayoutCodeSupport;
import org.eclipse.wb.internal.swt.model.widgets.ControlInfo;
import org.eclipse.wb.swt.layout.grouplayout.GroupLayout;
import org.eclipse.wb.swt.layout.grouplayout.LayoutStyle;

import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.swt.widgets.Control;

import org.netbeans.modules.form.layoutdesign.LayoutConstants;
import org.netbeans.modules.form.layoutdesign.LayoutInterval;

import java.io.StringWriter;
import java.util.List;

/**
 * SWT implementation.
 * 
 * @author mitin_aa
 */
public final class SwtGroupLayoutCodeSupport extends GroupLayoutCodeSupport
    implements
      LayoutConstants {
  ////////////////////////////////////////////////////////////////////////////
  //
  // Constructor
  //
  ////////////////////////////////////////////////////////////////////////////
  public SwtGroupLayoutCodeSupport(GroupLayoutInfo2 layout) {
    super(layout);
    // initialize names and signatures
    GROUP_LAYOUT_CLASS_NAME = "org.eclipse.wb.swt.layout.grouplayout.GroupLayout";
    GROUP_LAYOUT_GROUP_CLASS_NAME = GROUP_LAYOUT_CLASS_NAME + ".Group";
    ID_ADD_GAP = "add";
    ID_ADD_COMPONENT = "add";
    ID_ADD_GROUP = "add";
    SIGNATURE_SET_HORIZONTAL_GROUP =
        ID_SET_HORIZONTAL_GROUP + "(" + GROUP_LAYOUT_GROUP_CLASS_NAME + ")";
    SIGNATURE_SET_VERTICAL_GROUP =
        ID_SET_VERTICAL_GROUP + "(" + GROUP_LAYOUT_GROUP_CLASS_NAME + ")";
    SIGNATURE_LINK_SIZE = ID_LINK_SIZE + "(org.eclipse.swt.widgets.Control[])";
    SIGNATURE_LINK_SIZE_AXIS = "linkSize(org.eclipse.swt.widgets.Control[],int)";
  }

  @Override
  protected final String prepareLayoutCode(List<AbstractComponentInfo> components) throws Exception {
    SwtLayoutCodeGenerator.ComponentInfo[] infos =
        new SwtLayoutCodeGenerator.ComponentInfo[components.size()];
    int i = 0;
    for (AbstractComponentInfo abstractComponent : components) {
      ControlInfo component = (ControlInfo) abstractComponent;
      SwtLayoutCodeGenerator.ComponentInfo info = new SwtLayoutCodeGenerator.ComponentInfo();
      info.id = ObjectInfoUtils.getId(component);
      info.variableName = "j:" + info.id;
      info.clazz = component.getDescription().getComponentClass();
      infos[i++] = info;
    }
    String contVarName = getLayoutReference();
    StringWriter stringWriter = new StringWriter();
    SwtLayoutCodeGenerator swtGenerator = new SwtLayoutCodeGenerator(getLayout().getLayoutModel());
    swtGenerator.generateContainerLayout(stringWriter, getRootComponent(), contVarName, infos);
    return stringWriter.toString();
  }

  @Override
  protected boolean isComponent(Expression arg) {
    return AstNodeUtils.isSuccessorOf(arg, Control.class);
  }

  @Override
  protected void setAlignment(LayoutInterval interval, Expression arg) {
    Number value = (Number) JavaInfoEvaluationHelper.getValue(arg);
    interval.setAlignment(convertAlignment(value.intValue()));
  }

  @Override
  protected void setGroupAlignment(LayoutInterval group, Expression arg) {
    Number value = (Number) JavaInfoEvaluationHelper.getValue(arg);
    group.setGroupAlignment(convertAlignment(value.intValue()));
  }

  @Override
  protected void setPaddingType(LayoutInterval gap, Expression arg) {
    Number placement = (Number) JavaInfoEvaluationHelper.getValue(arg);
    gap.setPaddingType(convertPadding(placement.intValue()));
  }

  @Override
  protected void checkComponent(AbstractComponentInfo component, int dimension) {
  }

  @Override
  protected int convertDimension(int dimension) {
    if (dimension == GroupLayout.HORIZONTAL) {
      return HORIZONTAL;
    } else if (dimension == GroupLayout.VERTICAL) {
      return VERTICAL;
    }
    return super.convertDimension(dimension);
  }

  ////////////////////////////////////////////////////////////////////////////
  //
  // Misc
  //
  ////////////////////////////////////////////////////////////////////////////
  private int convertAlignment(int value) {
    switch (value) {
      case GroupLayout.CENTER :
        return CENTER;
      case GroupLayout.BASELINE :
        return BASELINE;
      case GroupLayout.LEADING :
        return LEADING;
      case GroupLayout.TRAILING :
        return TRAILING;
      default :
        return DEFAULT;
    }
  }

  private PaddingType convertPadding(int placement) {
    switch (placement) {
      case LayoutStyle.RELATED :
        return PaddingType.RELATED;
      case LayoutStyle.UNRELATED :
        return PaddingType.UNRELATED;
      case LayoutStyle.INDENT :
        return PaddingType.INDENT;
    }
    return PaddingType.SEPARATE;
  }
}
