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

public interface IJSFModelListener {
   public boolean isStrutsModelListenerEnabled();

   public void processChanged(boolean flag);

   public void groupAdd(IGroup group);
   public void groupRemove(IGroup group);

   public void linkAdd(ILink link);
   public void linkRemove(ILink link);

}
