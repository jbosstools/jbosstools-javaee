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
package org.jboss.tools.struts.ui.editor.model;

//import javax.swing.tree.*;
//import org.xml.sax.*;
//import java.io.*;
//import org.jboss.tools.common.model.*;
//import java.util.*;
import org.jboss.tools.struts.model.helpers.StrutsProcessStructureHelper;

public interface IStrutsModel extends IStrutsElement {

   public StrutsProcessStructureHelper getHelper();

   public IProcessItem getSelectedProcessItem();
   public void  setSelectedProcessItem(IProcessItem processItem);

   public IStrutsElement findElement(String key);
   public IProcessItem getProcessItem(String name);
   public IProcessItem getProcessItem(Object source);
   public IStrutsElementList getProcessItemList();

   public IProcessItem addProcessItem(String name);
   public IProcessItem addProcessItem(IStrutsElement element);
   public IProcessItem addProcessItem(Object source,int x,int y);
   public IProcessItem addProcessItem(IProcessItem processItem);
   public void removeProcessItem(IProcessItem processItem);
   public void removeProcessItem(String processItemName);

   public void setData(Object object) throws Exception;

   public void addStrutsModelListener(IStrutsModelListener listener);
   public void removeStrutsModelListener(IStrutsModelListener listener);

   public boolean isModified();
   public void setModified(boolean set);

   public boolean areCommentsVisible();

   public boolean isEditable();
   
   public IStrutsOptions getOptions();
   public boolean isBorderPaint();
}

