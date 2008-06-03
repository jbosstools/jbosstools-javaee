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

import org.jboss.tools.jsf.model.helpers.JSFProcessStructureHelper;

public interface IJSFModel extends IJSFElement {

   public JSFProcessStructureHelper getHelper();
   
   public void updateLinks();

   public IGroup getSelectedProcessItem();
   public void  setSelectedProcessItem(IGroup processItem);

   public IJSFElement findElement(String key);
   public IGroup getGroup(String name);
   public IGroup getGroup(Object source);
   public IJSFElementList getGroupList();

   public IGroup addGroup(String name);
   public IGroup addGroup(IJSFElement element);
   public IGroup addGroup(Object source,int x,int y);
   public IGroup addGroup(IGroup group);
   public void removeGroup(IGroup group);
   public void removeGroup(String groupName);

   public void setData(Object object) throws Exception;

   public void addJSFModelListener(IJSFModelListener listener);
   public void removeJSFModelListener(IJSFModelListener listener);

   public boolean isModified();
   public void setModified(boolean set);

   public boolean areCommentsVisible();

   public boolean isEditable();
   
   public IJSFOptions getOptions();
   public boolean isBorderPaint();
}

