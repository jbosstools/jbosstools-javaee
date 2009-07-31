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
package org.jboss.tools.jsf.ui.editor.model.commands;

import org.eclipse.gef.commands.Command;

import org.jboss.tools.jsf.ui.editor.dnd.DndHelper;
import org.jboss.tools.jsf.ui.editor.model.IGroup;
import org.jboss.tools.jsf.ui.editor.model.IPage;

public class ReorderPartCommand extends Command {
	private int oldIndex, newIndex;
	private IPage child;
	private IGroup parent;

	public ReorderPartCommand(IPage child, IGroup parent, int oldIndex,
			int newIndex) {
		super("reorder command"); //$NON-NLS-1$
		this.child = child;
		this.parent = parent;
		this.oldIndex = oldIndex;
		this.newIndex = newIndex;
	}

	public void execute() {
		if (newIndex > parent.getPageList().size() - 1 || newIndex < 0)
			newIndex = parent.getPageList().size() - 1;
		if (oldIndex == newIndex)
			return;
		if (DndHelper.drag(child.getSource())) {
			Object obj = ((IPage) parent.getPageList().get(newIndex))
					.getSource();
			if (DndHelper.isDropEnabled(obj)) {
				DndHelper.drop(obj);
			}
		}
	}

	public void undo() {
	}

}
