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

public interface IJSFElementListListener {
   public boolean isElementListListenerEnable();
   public void setElementListListenerEnable(boolean set);
   public void listElementMove(IJSFElementList list, IJSFElement element,int newIndex,int oldIndex);
   public void listElementAdd(IJSFElementList list,IJSFElement element,int index);
   public void listElementRemove(IJSFElementList list,IJSFElement element,int index);
   public void listElementChange(IJSFElementList list,IJSFElement element,int index,PropertyChangeEvent event);
}

