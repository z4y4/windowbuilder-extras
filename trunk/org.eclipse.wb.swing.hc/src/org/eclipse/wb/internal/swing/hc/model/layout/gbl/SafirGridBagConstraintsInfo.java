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
package org.eclipse.wb.internal.swing.hc.model.layout.gbl;

import org.eclipse.wb.internal.core.model.creation.CreationSupport;
import org.eclipse.wb.internal.core.model.description.ComponentDescription;
import org.eclipse.wb.internal.core.utils.ast.AstEditor;
import org.eclipse.wb.internal.core.utils.reflect.ReflectionUtils;
import org.eclipse.wb.internal.swing.hc.HCMessages;
import org.eclipse.wb.internal.swing.model.CoordinateUtils;
import org.eclipse.wb.internal.swing.model.component.ComponentInfo;
import org.eclipse.wb.internal.swing.model.layout.gbl.AbstractGridBagConstraintsInfo;
import org.eclipse.wb.internal.swing.model.layout.gbl.ColumnInfo;
import org.eclipse.wb.internal.swing.model.layout.gbl.RowInfo;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.LayoutManager;
import java.text.MessageFormat;

/**
 * Model for Safir {@link GridBagConstraints}.
 * 
 * @author sablin_aa
 * @coverage swing.model.layout
 */
public final class SafirGridBagConstraintsInfo extends AbstractGridBagConstraintsInfo {
  public static final String SAFIR_GBC_NAME = "fw.gui.layout.SafirGridBagConstraints";

  ////////////////////////////////////////////////////////////////////////////
  //
  // Constructor
  //
  ////////////////////////////////////////////////////////////////////////////
  public SafirGridBagConstraintsInfo(AstEditor editor,
      ComponentDescription description,
      CreationSupport creationSupport) throws Exception {
    super(editor, description, creationSupport);
  }

  ////////////////////////////////////////////////////////////////////////////
  //
  // Access
  //
  ////////////////////////////////////////////////////////////////////////////
  @Override
  public void getCurrentObjectFields(boolean init) throws Exception {
    Object constraints;
    if (init) {
      constraints = getObject();
      // location
      x = (Integer) ReflectionUtils.invokeMethod(constraints, "getGridx()");
      y = (Integer) ReflectionUtils.invokeMethod(constraints, "getGridy()");
      width = (Integer) ReflectionUtils.invokeMethod(constraints, "getGridwidth()");
      height = (Integer) ReflectionUtils.invokeMethod(constraints, "getGridheight()");
    } else {
      Component component = ((ComponentInfo) getParent()).getComponent();
      // prepare Layout
      LayoutManager layout = component.getParent().getLayout();
      if (!ReflectionUtils.isSuccessorOf(layout.getClass(), SafirGridBagLayoutInfo.SAFIR_GBL_NAME)) {
        return;
      }
      // get constraints
      constraints =
          ReflectionUtils.invokeMethod(layout, "lookupConstraints(java.awt.Component)", component);
      // location
      x = (Integer) ReflectionUtils.invokeMethod(constraints, "getTempX()");
      y = (Integer) ReflectionUtils.invokeMethod(constraints, "getTempY()");
      width = (Integer) ReflectionUtils.invokeMethod(constraints, "getTempWidth()");
      height = (Integer) ReflectionUtils.invokeMethod(constraints, "getTempHeight()");
    }
    // fetch fields
    insets =
        CoordinateUtils.get((java.awt.Insets) ReflectionUtils.invokeMethod(
            constraints,
            "getInsets()"));
    anchor = (Integer) ReflectionUtils.invokeMethod(constraints, "getAnchor()");
    fill = (Integer) ReflectionUtils.invokeMethod(constraints, "getFill()");
  }

  ////////////////////////////////////////////////////////////////////////////
  //
  // Access: location
  //
  ////////////////////////////////////////////////////////////////////////////
  @Override
  public void materializeLocation() throws Exception {
    Object constraints = getObject();
    int relativeAlignmentValue = getClassStaticFieldByName("RELATIVE");
    if ((Integer) ReflectionUtils.invokeMethod(constraints, "getGridy()") == relativeAlignmentValue) {
      ReflectionUtils.invokeMethod(constraints, "setGridy(int)", y);
      setY(y);
    }
    if ((Integer) ReflectionUtils.invokeMethod(constraints, "getGridx()") == relativeAlignmentValue) {
      ReflectionUtils.invokeMethod(constraints, "setGridx(int)", x);
      setX(x);
    }
  }

  ////////////////////////////////////////////////////////////////////////////
  //
  // Access: alignment
  //
  ////////////////////////////////////////////////////////////////////////////
  @Override
  public ColumnInfo.Alignment getHorizontalAlignment() {
    if (fill == getClassStaticFieldByNameSoft("BOTH")
        || fill == getClassStaticFieldByNameSoft("HORIZONTAL")) {
      return ColumnInfo.Alignment.FILL;
    }
    for (AlignmentInfoEx alignment : ALIGNMENTS) {
      if (getClassStaticFieldByNameSoft(alignment.anchor) == anchor) {
        return alignment.hAlignment;
      }
    }
    throw new IllegalArgumentException(MessageFormat.format(
        HCMessages.SafirGridBagConstraintsInfo_unknownFillAnchorCombination,
        fill,
        anchor));
  }

  @Override
  public RowInfo.Alignment getVerticalAlignment() {
    if (fill == getClassStaticFieldByNameSoft("BOTH")
        || fill == getClassStaticFieldByNameSoft("VERTICAL")) {
      return RowInfo.Alignment.FILL;
    }
    for (AlignmentInfoEx alignment : ALIGNMENTS) {
      if (getClassStaticFieldByNameSoft(alignment.anchor) == anchor) {
        return alignment.vAlignment;
      }
    }
    throw new IllegalArgumentException(MessageFormat.format(
        HCMessages.SafirGridBagConstraintsInfo_unknownFillAnchorCombination,
        fill,
        anchor));
  }

  ////////////////////////////////////////////////////////////////////////////
  //
  // AlignmentInfo
  //
  ////////////////////////////////////////////////////////////////////////////
  protected static final class AlignmentInfoEx extends AlignmentInfo {
    ////////////////////////////////////////////////////////////////////////////
    //
    // Constructor
    //
    ////////////////////////////////////////////////////////////////////////////
    public AlignmentInfoEx(String alignmentString, final String fill, final String anchor) {
      super(alignmentString, fill, anchor);
    }
  }

  private static final AlignmentInfoEx[] ALIGNMENTS = {
      /* NORTH */
      new AlignmentInfoEx("LT", "NONE", "NORTHWEST"),
      new AlignmentInfoEx("CT", "NONE", "NORTH"),
      new AlignmentInfoEx("RT", "NONE", "NORTHEAST"),
      new AlignmentInfoEx("FT", "HORIZONTAL", "NORTH"),
      /* CENTER */
      new AlignmentInfoEx("LC", "NONE", "WEST"),
      new AlignmentInfoEx("CC", "NONE", "CENTER"),
      new AlignmentInfoEx("RC", "NONE", "EAST"),
      new AlignmentInfoEx("FC", "HORIZONTAL", "CENTER"),
      /* SOUTH */
      new AlignmentInfoEx("LB", "NONE", "SOUTHWEST"),
      new AlignmentInfoEx("CB", "NONE", "SOUTH"),
      new AlignmentInfoEx("RB", "NONE", "SOUTHEAST"),
      new AlignmentInfoEx("FB", "HORIZONTAL", "SOUTH"),
      /* FILL */
      new AlignmentInfoEx("LF", "VERTICAL", "WEST"),
      new AlignmentInfoEx("CF", "VERTICAL", "CENTER"),
      new AlignmentInfoEx("RF", "VERTICAL", "EAST"),
      new AlignmentInfoEx("FF", "BOTH", "CENTER"),
      /* PAGE_START */
      new AlignmentInfoEx("LT", "NONE", "FIRST_LINE_START"),
      new AlignmentInfoEx("CT", "NONE", "PAGE_START"),
      new AlignmentInfoEx("RT", "NONE", "FIRST_LINE_END"),
      new AlignmentInfoEx("FT", "HORIZONTAL", "PAGE_START"),
      /* PAGE_END */
      new AlignmentInfoEx("LB", "NONE", "PAGE_END"),
      new AlignmentInfoEx("CB", "NONE", "LAST_LINE_START"),
      new AlignmentInfoEx("RB", "NONE", "LAST_LINE_END"),
      new AlignmentInfoEx("FB", "HORIZONTAL", "PAGE_END"),
      /* PAGE_CENTER */
      new AlignmentInfoEx("LC", "NONE", "LINE_START"),
      new AlignmentInfoEx("RC", "NONE", "LINE_END"),
      /* BASELINE: s */
      new AlignmentInfoEx("Ls", "NONE", "BASELINE_LEADING"),
      new AlignmentInfoEx("Cs", "NONE", "BASELINE"),
      new AlignmentInfoEx("Rs", "NONE", "BASELINE_TRAILING"),
      new AlignmentInfoEx("Fs", "HORIZONTAL", "BASELINE"),
      /* ABOVE_BASELINE: a */
      new AlignmentInfoEx("La", "NONE", "ABOVE_BASELINE_LEADING"),
      new AlignmentInfoEx("Ca", "NONE", "ABOVE_BASELINE"),
      new AlignmentInfoEx("Ra", "NONE", "ABOVE_BASELINE_TRAILING"),
      new AlignmentInfoEx("Fa", "HORIZONTAL", "ABOVE_BASELINE"),
      /* BELOW_BASELINE: b */
      new AlignmentInfoEx("Lb", "NONE", "BELOW_BASELINE_LEADING"),
      new AlignmentInfoEx("Cb", "NONE", "BELOW_BASELINE"),
      new AlignmentInfoEx("Rb", "NONE", "BELOW_BASELINE_TRAILING"),
      new AlignmentInfoEx("Fb", "HORIZONTAL", "BELOW_BASELINE"),};

  @Override
  protected AlignmentInfo[] getAlignments() {
    return ALIGNMENTS;
  }

  ////////////////////////////////////////////////////////////////////////////
  //
  // Source utils 
  //
  ////////////////////////////////////////////////////////////////////////////
  @Override
  public String newInstanceSourceLong() {
    return "new fw.gui.layout.SafirGridBagConstraints("
        + "fw.gui.layout.SafirGridBagConstraints.RELATIVE, "
        + "fw.gui.layout.SafirGridBagConstraints.RELATIVE, "
        + "1, 1, 0.0, 0.0, "
        + "fw.gui.layout.SafirGridBagConstraints.CENTER, "
        + "fw.gui.layout.SafirGridBagConstraints.NONE, "
        + "new java.awt.Insets(0, 0, 0, 0), "
        + "0, 0)";
  }

  @Override
  public String newInstanceSourceShort() {
    return "new fw.gui.layout.SafirGridBagConstraints()";
  }
}
