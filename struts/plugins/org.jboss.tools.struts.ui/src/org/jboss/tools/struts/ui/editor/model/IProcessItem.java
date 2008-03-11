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

import java.util.Vector;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

public interface IProcessItem extends IStrutsElement {

   public ILink[] getLinks();
   public ILink[] getInputLinks();
   public Vector getListInputLinks();
   
   public void addInputLink(ILink link);
   public void removeInputLink(ILink link);
   public ILink[] getOutputLinks();
   public Vector getListOutputLinks();

   public void setHeaderForegroundColor(Color color);
   public Color getHeaderForegroundColor();

   public void setHeaderBackgroundColor(Color color);
   public Color getHeaderBackgroundColor();

   public String getVisiblePath();
   public String getName();
   public String getPath();
   public String getViewClassName();
   public Image getImage();

   public IStrutsElementList getForwardList();

   public IForward getForward(String name);

   public void removeFromStrutsModel();

   public void addProcessItemListener(IProcessItemListener l);
   public void removeProcessItemListener(IProcessItemListener l);

   public boolean isSelected();
   public void setSelected(boolean set);
   public void clearSelection();

   public boolean isPage();
   public boolean isAction();
   public boolean isSwitchAction();

   public boolean isGlobal();
   public boolean isComment();
   public boolean isAnotherModule();
   
   public boolean hasErrors();

   public boolean hasPageHiddenLinks();

   public IProcessItem getCommentTarget();
   
   public IBreakPoint getBreakPoint();

   /*public boolean isDebugMode();
   public int getBreakpointStatus();
   public boolean isBreakpointActive();
   public int getBreakpointActiveStatus();*/
}
