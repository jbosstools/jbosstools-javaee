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
package org.jboss.tools.struts.ui.editor.model.commands;

import org.eclipse.gef.commands.Command;

import org.jboss.tools.struts.ui.editor.dnd.DndHelper;
import org.jboss.tools.struts.ui.editor.model.IProcessItem;

public class ReconnectTargetLinkCommand extends Command{
	
		static IProcessItem child = null;
	
	public ReconnectTargetLinkCommand(){
		super("ReconnectTargetLinkCommand");
		//Thread.dumpStack();
	}
	
	public void setChild(IProcessItem child){
		ReconnectTargetLinkCommand.child= child;
	}
	
	public boolean canExecute(){
		if(child == null) return false;
		return DndHelper.isDropEnabled(child.getSource());
	}
	
	public void execute(){
		//Thread.dumpStack();
		if(child != null)
			DndHelper.drop(child.getSource());
		child = null;
	}
	
	public boolean canUndo() {
		return false;
	}
}
