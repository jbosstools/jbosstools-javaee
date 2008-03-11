/*******************************************************************************
 * Copyright (c) 2000, 2003 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.jboss.tools.struts.ui.editor.model.commands;

import org.eclipse.gef.commands.Command;

import org.jboss.tools.struts.ui.editor.dnd.DndHelper;
import org.jboss.tools.struts.ui.editor.model.IForward;
import org.jboss.tools.struts.ui.editor.model.IProcessItem;


public class ReorderPartCommand extends Command {
	
//private boolean valuesInitialized;
private int oldIndex, newIndex;
private IForward child;
private IProcessItem parent;

public ReorderPartCommand(IForward child, IProcessItem parent, int oldIndex, int newIndex){
	super("reorder command");
	this.child = child;
	this.parent = parent;
	this.oldIndex = oldIndex;
	this.newIndex = newIndex;
}

public void execute() {
	if(newIndex > parent.getForwardList().size()-1 || newIndex < 0) newIndex = parent.getForwardList().size()-1;
	if(oldIndex == newIndex) return;
	if(DndHelper.drag(child.getSource())){
		Object obj = ((IForward)parent.getForwardList().get(newIndex)).getSource();
		if(DndHelper.isDropEnabled(obj)){
			DndHelper.drop(obj);
		}
	}
}

public void undo() {
}

}