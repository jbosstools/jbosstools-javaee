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

import java.util.*;

import org.eclipse.swt.graphics.Image;

public interface IGroup extends IJSFElement {

   public ILink[] getLinks();

   public ILink[] getInputLinks();
   public List getListInputLinks();

   public void addInputLink(ILink link);
   public void removeInputLink(ILink link);

   public ILink[] getOutputLinks();
   public List getListOutputLinks();
   
   public void addOutputLink(ILink link);
   public void addOutputLink(ILink link, int index);
   public void removeOutputLink(ILink link);

   public String getVisiblePath();
   public String getName();
   public String getPath();
   public String getViewClassName();
   public Image getImage();

   public IJSFElementList getPageList();

   public IPage getPage(String name);

   public void removeFromJSFModel();

   public void addGroupListener(IGroupListener l);
   public void removeGroupListener(IGroupListener l);

   public boolean isSelected();
   public void setSelected(boolean set);
   public void clearSelection();
   
   public boolean hasPageHiddenLinks();
   public boolean isConfirmed();
   public boolean isPattern();

   public boolean isComment();
   public IGroup getCommentTarget();
   
   public boolean hasErrors();
}
