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
import org.jboss.tools.jsf.ui.editor.model.ILink;

public class ReconnectSourceLinkCommand	extends Command{
	
		static ILink child = null;
	
	public ReconnectSourceLinkCommand(){
		super("ReconnectSourceLinkCommand"); //$NON-NLS-1$
	}
	
	public void setLink(ILink child){
		ReconnectSourceLinkCommand.child= child;
	}
	public boolean canExecute(){
		return DndHelper.isDropEnabled(child.getSource());
	}
	
	public void execute(){
		if(child != null)DndHelper.drop(child.getSource());
		child = null;
	}
	
	public boolean canUndo() {
		return false;
	}
}
