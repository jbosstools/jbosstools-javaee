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

import org.jboss.tools.jsf.model.handlers.GroupAdopt;
import org.jboss.tools.jsf.ui.editor.dnd.DndHelper;
import org.jboss.tools.jsf.ui.editor.model.IGroup;

public class ReconnectSourceLinkCommand2 extends Command{
	
	static IGroup group = null;
	
	public ReconnectSourceLinkCommand2(){
		super("ReconnectSourceLinkCommand2"); //$NON-NLS-1$
	}
	
	public void setGroup(IGroup group){
		ReconnectSourceLinkCommand2.group = group;
	}
	public boolean canExecute() {
		GroupAdopt.move_case = true;
		try {
			return DndHelper.isDropEnabled(group.getSource());
		} finally {
			GroupAdopt.move_case = false;
		}
	}
	
	public void execute(){
		if(group != null) {
			GroupAdopt.move_case = true;
			try {
				DndHelper.drop(group.getSource());
			} finally {
				GroupAdopt.move_case = false;
			}
		}
		group = null;
	}
	
	public boolean canUndo() {
		return false;
	}
}
