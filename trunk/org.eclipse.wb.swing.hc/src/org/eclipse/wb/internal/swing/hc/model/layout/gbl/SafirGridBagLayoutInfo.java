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

import org.eclipse.wb.core.model.association.EmptyAssociation;
import org.eclipse.wb.internal.core.model.JavaInfoUtils;
import org.eclipse.wb.internal.core.model.creation.CreationSupport;
import org.eclipse.wb.internal.core.model.description.ComponentDescription;
import org.eclipse.wb.internal.core.utils.ast.AstEditor;
import org.eclipse.wb.internal.core.utils.check.Assert;
import org.eclipse.wb.internal.core.utils.execution.ExecutionUtils;
import org.eclipse.wb.internal.core.utils.execution.RunnableObjectEx;
import org.eclipse.wb.internal.core.utils.reflect.ReflectionUtils;
import org.eclipse.wb.internal.core.utils.state.EditorState;
import org.eclipse.wb.internal.swing.model.component.ComponentInfo;
import org.eclipse.wb.internal.swing.model.layout.gbl.AbstractGridBagConstraintsInfo;
import org.eclipse.wb.internal.swing.model.layout.gbl.AbstractGridBagLayoutInfo;
import org.eclipse.wb.internal.swing.model.layout.gbl.VirtualConstraintsCreationSupport;
import org.eclipse.wb.internal.swing.model.layout.gbl.VirtualConstraintsVariableSupport;

import java.awt.GridBagLayout;
import java.awt.Point;
import java.lang.reflect.Constructor;
import java.util.List;

/**
 * Model for Safir {@link GridBagLayout}.
 * 
 * @author sablin_aa
 * @coverage swing.model.layout
 */
public final class SafirGridBagLayoutInfo extends AbstractGridBagLayoutInfo {
  public static final String SAFIR_GBL_NAME = "fw.gui.layout.SafirGridBagLayout";

  ////////////////////////////////////////////////////////////////////////////
  //
  // Constructor
  //
  ////////////////////////////////////////////////////////////////////////////
  public SafirGridBagLayoutInfo(AstEditor editor,
      ComponentDescription description,
      CreationSupport creationSupport) throws Exception {
    super(editor, description, creationSupport);
  }

  ////////////////////////////////////////////////////////////////////////////
  //
  // GridBagConstraintsInfo access
  //
  ////////////////////////////////////////////////////////////////////////////
  /**
   * @return the {@link AbstractGridBagConstraintsInfo} for given {@link ComponentInfo}.
   */
  @Override
  public AbstractGridBagConstraintsInfo getConstraints(final ComponentInfo component) {
    return getConstraintsFor(component);
  }

  public static SafirGridBagConstraintsInfo getConstraintsFor(final ComponentInfo component) {
    return ExecutionUtils.runObject(new RunnableObjectEx<SafirGridBagConstraintsInfo>() {
      public SafirGridBagConstraintsInfo runObject() throws Exception {
        // prepare constraints
        SafirGridBagConstraintsInfo constraints;
        {
          List<SafirGridBagConstraintsInfo> constraintsList =
              component.getChildren(SafirGridBagConstraintsInfo.class);
          Assert.isLegal(constraintsList.size() <= 1);
          if (constraintsList.size() == 1) {
            constraints = constraintsList.get(0);
          } else {
            constraints =
                (SafirGridBagConstraintsInfo) JavaInfoUtils.createJavaInfo(
                    component.getEditor(),
                    SafirGridBagConstraintsInfo.SAFIR_GBC_NAME,
                    new VirtualConstraintsCreationSupport(component));
            constraints.setVariableSupport(new VirtualConstraintsVariableSupport(constraints));
            constraints.setAssociation(new EmptyAssociation());
            component.addChild(constraints);
          }
        }
        // initialize and return
        constraints.init();
        return constraints;
      }
    });
  }

  @Override
  public Object getConstraintsObject(final java.awt.Component component) throws Exception {
    Object constraints;
    if (component == null) {
      // no component instance, we probably add new component, so use just some GridBagConstraints
      Class<?> constraintsClass =
          EditorState.get(getEditor()).getEditorLoader().loadClass(
              SafirGridBagConstraintsInfo.SAFIR_GBC_NAME);
      Constructor<?> constructor =
          ReflectionUtils.getConstructorBySignature(constraintsClass, "<init>()");
      constraints = constructor.newInstance();
    } else {
      // component is bound to parent, get constraints from layout
      constraints =
          ReflectionUtils.invokeMethod(
              getObject(),
              "getConstraintsFor(java.awt.Component)",
              component);
    }
    return constraints;
  }

  ////////////////////////////////////////////////////////////////////////////
  //
  // Dimensions
  //
  ////////////////////////////////////////////////////////////////////////////
  @Override
  protected int[][] getLayoutDimensions() throws Exception {
    return (int[][]) ReflectionUtils.invokeMethod(getObject(), "getLayoutDimensions()");
  }

  @Override
  protected Point getLayoutOrigin() throws Exception {
    return (Point) ReflectionUtils.invokeMethod(getObject(), "getLayoutOrigin()");
  }
}
