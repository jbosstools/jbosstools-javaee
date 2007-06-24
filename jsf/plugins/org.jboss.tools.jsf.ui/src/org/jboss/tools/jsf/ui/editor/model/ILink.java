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

import org.eclipse.draw2d.geometry.PointList;

public interface ILink extends IJSFElement {

   public void setTarget();

   public IPage getFromPage();
   public IGroup getFromGroup();

   public IGroup getToGroup();
   public String getLinkName();

   public void addLinkListener(ILinkListener l);
   public void removeLinkListener(ILinkListener l);

   public boolean isConfirmed();
   public boolean isHidden();
   public boolean isShortcut();

   public String getPathFromModel();
   
   public boolean isPreferredMode();
   public int getPreferredLength();
   
   public PointList getPointList();
   public void savePointList(PointList list);
   public void clearPointList();
}

