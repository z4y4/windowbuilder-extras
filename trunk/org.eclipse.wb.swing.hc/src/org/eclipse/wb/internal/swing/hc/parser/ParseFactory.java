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
package org.eclipse.wb.internal.swing.hc.parser;

import org.eclipse.wb.internal.core.parser.IParseFactory;
import org.eclipse.wb.internal.core.utils.ast.AstEditor;
import org.eclipse.wb.internal.core.utils.ast.AstNodeUtils;
import org.eclipse.wb.internal.swing.hc.Activator;

import org.eclipse.jdt.core.dom.ITypeBinding;

/**
 * {@link IParseFactory} for HUK-Coburg Swing-based toolkit.
 * 
 * @author sablin_aa
 * @coverage core.model.parser
 */
public final class ParseFactory extends org.eclipse.wb.internal.core.parser.AbstractParseFactory {
  ////////////////////////////////////////////////////////////////////////////
  //
  // IParseFactory
  //
  ////////////////////////////////////////////////////////////////////////////
  @Override
  public boolean isToolkitObject(AstEditor editor, ITypeBinding typeBinding) throws Exception {
    return isHUKCoburgObject(typeBinding);
  }

  /**
   * @return <code>true</code> if given type binding is HUK-Coburg Toolkit object.
   */
  private static boolean isHUKCoburgObject(ITypeBinding typeBinding) throws Exception {
    if (typeBinding == null) {
      return false;
    }
    // SafirGridBagConstraints
    if (AstNodeUtils.isSuccessorOf(typeBinding, "fw.gui.layout.SafirGridBagConstraints")) {
      return true;
    }
    //
    return false;
  }

  @Override
  protected String getToolkitId() {
    return Activator.PLUGIN_ID;
  }
}
