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

import java.beans.*;

public interface IForward extends IStrutsElement {
   //public void initLink();

   public void remove();

   public void addForwardListener(IForwardListener listener);
   public void removeForwardListener(IForwardListener listener);

   public IProcessItem getProcessItem();

   public IStrutsElementList getPartList();

   public boolean canRename();
   public boolean canDelete();

   public void rename(String newName) throws PropertyVetoException;

   public ILink addLink(IProcessItem toProcessItem);
   public void removeLink(IForward forward);
   public ILink getLink();

   public boolean isLinkAllowed();
   public boolean isException();

   public boolean isHidden();
}
