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
package org.jboss.tools.seam.ui.pages.editor.commands;

import org.eclipse.gef.commands.Command;
import org.jboss.tools.seam.ui.pages.editor.dnd.DndHelper;
import org.jboss.tools.seam.ui.pages.editor.ecore.pages.PagesElement;


public class ReconnectTargetLinkCommand extends Command{
	
		PagesElement child = null;
	
	public ReconnectTargetLinkCommand(){
		super("ReconnectTargetLinkCommand");
	}
	
	public void setChild(PagesElement child){
		this.child= child;
	}
	
	public boolean canExecute(){
		return DndHelper.isDropEnabled(child.getData());
	}
	
	public void execute(){
		DndHelper.drop(child.getData());
	}
	
	public boolean canUndo() {
		return false;
	}
}
