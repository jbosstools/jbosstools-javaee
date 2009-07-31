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
package org.jboss.tools.jsf.ui.editor.model.commands;

import org.eclipse.gef.commands.Command;

import org.jboss.tools.jsf.ui.editor.dnd.DndHelper;
import org.jboss.tools.jsf.ui.editor.model.IGroup;

public class ReconnectTargetLinkCommand extends Command{
	
		IGroup child = null;
	
	public ReconnectTargetLinkCommand(){
		super("ReconnectTargetLinkCommand"); //$NON-NLS-1$
	}
	
	public void setChild(IGroup child){
		this.child= child;
	}
	
	public boolean canExecute(){
		return DndHelper.isDropEnabled(child.getSource());
	}
	
	public void execute(){
		DndHelper.drop(child.getSource());
	}
	
	public boolean canUndo() {
		return false;
	}
}
