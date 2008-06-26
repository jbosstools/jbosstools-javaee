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

import org.eclipse.draw2d.geometry.PointList;

public interface ILink extends IStrutsElement {
   public void setTarget();

   public void setHeadSegment(ISegment segment);
   public void setTailSegment(ISegment segment);

   public ISegment getHeadSegment();
   public ISegment getTailSegment();

   public IForward getFromForward();
   public IProcessItem getFromProcessItem();

   public IProcessItem getToProcessItem();
   public String getToProcessItemName();

   public void addLinkListener(ILinkListener l);
   public void removeLinkListener(ILinkListener l);

   public ISegment createSegment(int length, ISegment prevSegment);
   public void update();

   public boolean isConfirmed();
   public boolean isHidden();
   public boolean isShortcut();

   public String getPathFromModel();
   
   public boolean isPreferredMode();
   public int getPreferredLength();

   public void clearPreferred();
   public PointList getPointList();
   public void savePointList(PointList list);
   public void clearPointList();
   
   public IBreakPoint getHeadBreakPoint();
   public IBreakPoint getTailBreakPoint();

   //public boolean isDebugMode();
   
   /*public int getHeadBreakpointStatus();
   public boolean isHeadBreakpointActive();
   public int getHeadBreakpointActiveStatus();
   
   public int getTailBreakpointStatus();
   public boolean isTailBreakpointActive();
   public int getTailBreakpointActiveStatus();*/
}

