/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 * 
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 * 
 * The contents of this file are subject to the terms of either the GNU General Public License
 * Version 2 only ("GPL") or the Common Development and Distribution License("CDDL") (collectively,
 * the "License"). You may not use this file except in compliance with the License. You can obtain a
 * copy of the License at http://www.netbeans.org/cddl-gplv2.html or nbbuild/licenses/CDDL-GPL-2-CP.
 * See the License for the specific language governing permissions and limitations under the
 * License. When distributing the software, include this License Header Notice in each file and
 * include the License file at nbbuild/licenses/CDDL-GPL-2-CP. Sun designates this particular file
 * as subject to the "Classpath" exception as provided by Sun in the GPL Version 2 section of the
 * License file that accompanied this code. If applicable, add the following below the License
 * Header, with the fields enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 * 
 * Contributor(s):
 * 
 * The Original Software is NetBeans. The Initial Developer of the Original Software is Sun
 * Microsystems, Inc. Portions Copyright 1997-2006 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * If you wish your version of this file to be governed by only the CDDL or only the GPL Version 2,
 * indicate your decision by adding "[Contributor] elects to include this software in this
 * distribution under the [CDDL or GPL Version 2] license." If you do not indicate a single choice
 * of license, a recipient has the option to distribute your version of this file under either the
 * CDDL, the GPL Version 2 or to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL Version 2 license, then the
 * option applies only if the new code is made subject to such option by the copyright holder.
 */
package org.eclipse.wb.internal.rcp.grouplayout.model;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import org.netbeans.modules.form.layoutdesign.LayoutComponent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utilities for SWT GroupLayout support.
 * 
 * @author Jan Stola
 */
public class SwtLayoutUtils {
  /** The default resizability of the component is not known. */
  public static final int STATUS_UNKNOWN = -1;
  /** The component is not resizable by default. */
  public static final int STATUS_NON_RESIZABLE = 0;
  /** The component is resizable by default. */
  public static final int STATUS_RESIZABLE = 1;

  public static int getResizableStatus(Class<?> componentClass) {
    if (Label.class.isAssignableFrom(componentClass)
        || org.eclipse.swt.widgets.List.class.isAssignableFrom(componentClass)
        || Button.class.isAssignableFrom(componentClass)
        || org.eclipse.swt.widgets.Link.class.isAssignableFrom(componentClass)) {
      return STATUS_NON_RESIZABLE;
    }
    if (Composite.class.isAssignableFrom(componentClass)
        || Text.class.isAssignableFrom(componentClass)) {
      return STATUS_RESIZABLE;
    }
    return STATUS_UNKNOWN;
  }

  public static Map<Integer, List<String>> createLinkSizeGroups(LayoutComponent layoutComponent,
      int dimension) {
    Map<Integer, List<String>> linkSizeGroup = new HashMap<Integer, List<String>>();
    if (layoutComponent.isLayoutContainer()) {
      for (LayoutComponent lc : layoutComponent.getSubcomponents()) {
        if (lc != null) {
          if (lc.isLinkSized(dimension)) {
            String cid = lc.getId();
            Integer id = Integer.valueOf(lc.getLinkSizeId(dimension));
            List<String> l = linkSizeGroup.get(id);
            if (l == null) {
              l = new ArrayList<String>();
              l.add(cid);
              linkSizeGroup.put(id, l);
            } else {
              l.add(cid);
            }
          }
        }
      }
    }
    return linkSizeGroup;
  }
}
