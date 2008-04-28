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

public interface IProcessItemListener {
   public boolean isProcessItemListenerEnable();
   public void processItemChange();
   public void processItemForwardAdd(IProcessItem processItem,IForward forward);
   public void processItemForwardRemove(IProcessItem processItem,IForward forward);
   public void processItemForwardChange(IProcessItem processItem,IForward forward, PropertyChangeEvent evet);
   public void linkAdd(ILink link);
   public void linkRemove(ILink link);
   

}
