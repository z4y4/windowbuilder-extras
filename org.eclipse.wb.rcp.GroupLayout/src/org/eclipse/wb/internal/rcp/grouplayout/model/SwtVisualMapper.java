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

import org.eclipse.wb.core.model.ObjectInfoUtils;
import org.eclipse.wb.draw2d.geometry.Rectangle;
import org.eclipse.wb.internal.layout.group.model.GroupLayoutUtils;
import org.eclipse.wb.internal.layout.group.model.IGroupLayoutInfo;
import org.eclipse.wb.internal.swt.model.widgets.CompositeInfo;
import org.eclipse.wb.internal.swt.model.widgets.ControlInfo;
import org.eclipse.wb.swt.layout.grouplayout.LayoutStyle;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Scrollable;
import org.eclipse.swt.widgets.Text;

import org.netbeans.modules.form.layoutdesign.VisualMapper;

import java.awt.Dimension;

/**
 * The VisualMapper for SWT.
 * 
 * @author mitin_aa
 */
public class SwtVisualMapper implements VisualMapper {
  private final GroupLayoutInfo2 m_layout;

  ////////////////////////////////////////////////////////////////////////////
  //
  // Constructor
  //
  ////////////////////////////////////////////////////////////////////////////
  public SwtVisualMapper(GroupLayoutInfo2 layout) {
    m_layout = layout;
  }

  ////////////////////////////////////////////////////////////////////////////
  //
  // VisualMapper
  //
  ////////////////////////////////////////////////////////////////////////////
  public int getBaselinePosition(String componentId, int width, int height) {
    return getControlInfo(componentId).getBaseline();
  }

  public java.awt.Rectangle getComponentBounds(String componentId) {
    return GroupLayoutUtils.getBoundsInLayout(getLayoutInfo(), getControlInfo(componentId));
  }

  public Dimension getComponentMinimumSize(String componentId) {
    return new Dimension();
  }

  public Dimension getComponentPreferredSize(String componentId) {
    org.eclipse.wb.draw2d.geometry.Dimension size =
        getControlInfo(componentId).getPreferredSize();
    return new Dimension(size.width, size.height);
  }

  public java.awt.Rectangle getContainerInterior(String componentId) {
    CompositeInfo composite = getComposite();
    Rectangle bounds = composite.getClientArea();
    if (bounds != null) {
      return new java.awt.Rectangle(0, 0, bounds.width, bounds.height);
    }
    // no refresh yet (paste, ex.)
    Scrollable scrollable = (Scrollable) composite.getObject();
    org.eclipse.swt.graphics.Rectangle clientArea = scrollable.getClientArea();
    return new java.awt.Rectangle(0, 0, clientArea.width, clientArea.height);
  }

  public int getPreferredPadding(String component1Id,
      String component2Id,
      int dimension,
      int comp2Alignment,
      PaddingType paddingType) {
    ControlInfo comp1 = getControlInfo(component1Id);
    ControlInfo comp2 = getControlInfo(component2Id);
    if (comp1 == null || comp2 == null) {
      return 10; // default distance between components
    }
    assert dimension == HORIZONTAL || dimension == VERTICAL;
    assert comp2Alignment == LEADING || comp2Alignment == TRAILING;
    int type =
        paddingType == PaddingType.INDENT ? LayoutStyle.INDENT : paddingType == PaddingType.RELATED
            ? LayoutStyle.RELATED
            : LayoutStyle.UNRELATED;
    int position = 0;
    if (dimension == HORIZONTAL) {
      if (paddingType == PaddingType.INDENT) {
        position = comp2Alignment == LEADING ? SWT.LEFT : SWT.RIGHT;
      } else {
        position = comp2Alignment == LEADING ? SWT.RIGHT : SWT.LEFT;
      }
    } else {
      position = comp2Alignment == LEADING ? SWT.DOWN : SWT.UP;
    }
    int prefPadding =
        paddingType != PaddingType.SEPARATE
            ? getComponentGapValue(comp1, comp2, type, position)
            : PADDING_SEPARATE_VALUE;
    return prefPadding;
  }

  public void setComponentVisibility(String componentId, boolean visible) {
  }

  public int getPreferredPaddingInParent(String parentId,
      String componentId,
      int dimension,
      int compAlignment) {
    int alignment;
    if (dimension == HORIZONTAL) {
      if (compAlignment == LEADING) {
        alignment = SWT.LEFT;
      } else {
        alignment = SWT.RIGHT;
      }
    } else {
      if (compAlignment == LEADING) {
        alignment = SWT.UP;
      } else {
        alignment = SWT.DOWN;
      }
    }
    return getContainerGapValue(getControlInfo(componentId), alignment);
  }

  public boolean hasExplicitPreferredSize(String componentId) {
    ControlInfo controlInfo = getControlInfo(componentId);
    // special case for Text
    return Text.class.isAssignableFrom(controlInfo.getDescription().getComponentClass());
  }

  public void rebuildLayout(String containerId) {
  }

  public boolean[] getComponentResizability(String compId, boolean[] resizability) {
    return null;
  }

  ////////////////////////////////////////////////////////////////////////////
  //
  // Helpers/Misc
  //
  ////////////////////////////////////////////////////////////////////////////
  private int getContainerGapValue(ControlInfo controlInfo, int position) {
    Class<?> controlClass = controlInfo.getDescription().getComponentClass();
    return LayoutStyle.getSharedInstance().getContainerGap(
        controlClass,
        controlInfo.getStyle(),
        position);
  }

  public int getComponentGapValue(ControlInfo sourceControlInfo,
      ControlInfo targetTargetInfo,
      int paddingType,
      int position) {
    Class<?> srcControlInfoClass = sourceControlInfo.getDescription().getComponentClass();
    Class<?> tgtControlInfoClass = targetTargetInfo.getDescription().getComponentClass();
    return LayoutStyle.getSharedInstance().getPreferredGap(
        srcControlInfoClass,
        sourceControlInfo.getStyle(),
        tgtControlInfoClass,
        targetTargetInfo.getStyle(),
        paddingType,
        position);
  }

  private ControlInfo getControlInfo(String id) {
    return (ControlInfo) ObjectInfoUtils.getById(id);
  }

  private CompositeInfo getComposite() {
    return m_layout.getComposite();
  }

  private IGroupLayoutInfo getLayoutInfo() {
    return m_layout.getAdapter(IGroupLayoutInfo.class);
  }
}
