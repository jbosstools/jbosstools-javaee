/*******************************************************************************
 * Copyright (c) 2007 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.jsf.ui.editor.model;

import java.beans.*;

import org.eclipse.draw2d.geometry.Rectangle;

public interface IPage extends IJSFElement {

   public void remove();

   public void addPageListener(IPageListener listener);
   public void removePageListener(IPageListener listener);

   public IGroup getGroup();

   public IJSFElementList getLinkList();

   public boolean canRename();
   public boolean canDelete();

   public void rename(String newName) throws PropertyVetoException;

   public boolean isLinkAllowed();
   public void setBounds(Rectangle rect);
   public Rectangle getBounds();
   public boolean hasErrors();
}
