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
package org.eclipse.wb.internal.rcp.grouplayout.gef;

import org.eclipse.wb.core.gef.policy.layout.ILayoutEditPolicyFactory;
import org.eclipse.wb.gef.core.EditPart;
import org.eclipse.wb.gef.graphical.policies.LayoutEditPolicy;
import org.eclipse.wb.internal.core.utils.IAdaptable;
import org.eclipse.wb.internal.layout.group.model.IGroupLayoutInfo;
import org.eclipse.wb.internal.rcp.grouplayout.model.GroupLayoutInfo2;

/**
 * Implementation of {@link ILayoutEditPolicyFactory} for SWT. "2" is for developers to not confuse
 * this with main SWT factory.
 * 
 * @author mitin_aa
 * @coverage swt.gef.policy
 */
public final class LayoutEditPolicyFactory2 implements ILayoutEditPolicyFactory {
  ////////////////////////////////////////////////////////////////////////////
  //
  // ILayoutEditPolicyFactory
  //
  ////////////////////////////////////////////////////////////////////////////
  public LayoutEditPolicy createLayoutEditPolicy(EditPart context, Object model) {
    if (model instanceof GroupLayoutInfo2) {
      IAdaptable adaptable = (IAdaptable) model;
      IGroupLayoutInfo layoutInfo = adaptable.getAdapter(IGroupLayoutInfo.class);
      if (layoutInfo != null) {
        return new SwtGroupLayoutEditPolicy2(layoutInfo);
      }
    }
    // not found
    return null;
  }
}
