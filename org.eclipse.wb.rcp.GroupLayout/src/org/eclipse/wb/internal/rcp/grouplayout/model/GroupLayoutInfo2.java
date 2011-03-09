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
import org.eclipse.wb.core.model.JavaInfo;
import org.eclipse.wb.core.model.ObjectInfo;
import org.eclipse.wb.core.model.association.AssociationObject;
import org.eclipse.wb.draw2d.geometry.Insets;
import org.eclipse.wb.internal.core.model.clipboard.ClipboardCommand;
import org.eclipse.wb.internal.core.model.creation.CreationSupport;
import org.eclipse.wb.internal.core.model.description.ComponentDescription;
import org.eclipse.wb.internal.core.model.layout.absolute.IImageProvider;
import org.eclipse.wb.internal.core.utils.IAdaptable;
import org.eclipse.wb.internal.core.utils.ast.AstEditor;
import org.eclipse.wb.internal.layout.group.model.GroupLayoutClipboardCommand;
import org.eclipse.wb.internal.layout.group.model.GroupLayoutSupport;
import org.eclipse.wb.internal.layout.group.model.IGroupLayoutInfo;
import org.eclipse.wb.internal.rcp.grouplayout.Activator;
import org.eclipse.wb.internal.swt.model.layout.LayoutClipboardCommand;
import org.eclipse.wb.internal.swt.model.layout.LayoutInfo;
import org.eclipse.wb.internal.swt.model.widgets.CompositeInfo;
import org.eclipse.wb.internal.swt.model.widgets.ControlInfo;

import org.eclipse.swt.graphics.Image;

import java.util.List;

/**
 * SWT GroupLayout support.
 * 
 * @author mitin_aa
 */
public final class GroupLayoutInfo2 extends LayoutInfo implements IAdaptable {
  private final GroupLayoutSupport m_layoutSupport;

  ////////////////////////////////////////////////////////////////////////////
  //
  // Constructor
  //
  ////////////////////////////////////////////////////////////////////////////
  public GroupLayoutInfo2(AstEditor editor,
      ComponentDescription description,
      CreationSupport creationSupport) throws Exception {
    super(editor, description, creationSupport);
    m_layoutSupport =
        new GroupLayoutSupport(this, new SwtGroupLayoutCodeSupport(this), new SwtVisualMapper(this)) {
          @Override
          protected List<?> getComponents() {
            return GroupLayoutInfo2.this.getControls();
          }

          public Insets getContainerInsets() {
            org.eclipse.wb.draw2d.geometry.Rectangle clientArea =
                getComposite().getClientArea();
            return new Insets(clientArea.x, clientArea.y, 0, 0);
          }

          public AbstractComponentInfo getLayoutContainer() {
            return getComposite();
          }

          public boolean isRelatedComponent(ObjectInfo component) {
            return isManagedObject(component);
          }

          @Override
          protected AssociationObject getAssociationObject() {
            return null;
          }

          @Override
          protected IImageProvider getImageProvider() {
            return ImageProvider.INSTANCE;
          }
        };
  }

  ////////////////////////////////////////////////////////////////////////////
  //
  // Copy/Paste
  //
  ////////////////////////////////////////////////////////////////////////////
  @Override
  protected void clipboardCopy_addCompositeCommands(List<ClipboardCommand> commands)
      throws Exception {
    super.clipboardCopy_addCompositeCommands(commands);
    commands.add(new GroupLayoutClipboardCommand(m_layoutSupport) {
      private static final long serialVersionUID = 0L;

      @Override
      protected GroupLayoutSupport getLayoutSupport(JavaInfo container) {
        CompositeInfo host = (CompositeInfo) container;
        GroupLayoutInfo2 layout = (GroupLayoutInfo2) host.getLayout();
        return layout.m_layoutSupport;
      }
    });
  }

  @Override
  protected void clipboardCopy_addControlCommands(ControlInfo control,
      List<ClipboardCommand> commands) throws Exception {
    commands.add(new LayoutClipboardCommand<GroupLayoutInfo2>(control) {
      private static final long serialVersionUID = 0L;

      @Override
      protected void add(GroupLayoutInfo2 layout, ControlInfo control) throws Exception {
        layout.m_layoutSupport.addComponentImpl(control);
      }
    });
  }

  ////////////////////////////////////////////////////////////////////////////
  //
  // New layout setup
  //
  ////////////////////////////////////////////////////////////////////////////
  @Override
  public void onSet() throws Exception {
    m_layoutSupport.onSet();
  }

  ////////////////////////////////////////////////////////////////////////////
  //
  // IAdaptable
  //
  ////////////////////////////////////////////////////////////////////////////
  public <T> T getAdapter(Class<T> adapter) {
    if (JavaInfo.class.isAssignableFrom(adapter)) {
      return adapter.cast(this);
    } else if (GroupLayoutSupport.class.isAssignableFrom(adapter)
        || IGroupLayoutInfo.class.isAssignableFrom(adapter)) {
      return adapter.cast(m_layoutSupport);
    }
    return null;
  }

  ////////////////////////////////////////////////////////////////////////////
  //
  // IImageProvider
  //
  ////////////////////////////////////////////////////////////////////////////
  private static final class ImageProvider implements IImageProvider {
    static final IImageProvider INSTANCE = new ImageProvider();

    public Image getImage(String path) {
      return Activator.getImage(path);
    }
  }
}
