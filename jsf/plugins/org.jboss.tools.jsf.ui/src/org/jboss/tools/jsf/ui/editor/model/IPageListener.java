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

import java.beans.PropertyChangeEvent;

public interface IPageListener {
   public boolean isPageListenerEnable();
   public void pageRemoved(IPage page);
   public void pageChange();
   public void linkAdd(IPage page,ILink link);
   public void linkRemove(IPage page,ILink link);
   public void linkChange(IPage page,ILink link, PropertyChangeEvent event);

}