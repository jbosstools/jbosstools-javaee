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
import org.jboss.tools.jsf.ui.editor.edit.JSFEditPart;
import org.jboss.tools.jsf.ui.editor.model.IGroup;
import org.jboss.tools.jsf.ui.editor.model.ILink;

public class ConnectionCommand extends Command {
	protected JSFEditPart oldSource;
	protected String oldSourceTerminal;
	protected JSFEditPart oldTarget;
	protected String oldTargetTerminal;
	protected JSFEditPart source;
	protected String sourceTerminal;
	protected JSFEditPart target;
	protected String targetTerminal;
	protected ILink link;

	public ConnectionCommand() {
		super("connection command"); //$NON-NLS-1$
	}

	public boolean canExecute() {
		if (target == null)
			return false;
		if (target.getModel() == null)
			return false;
		return DndHelper
				.isDropEnabled(((IGroup) target.getModel()).getSource());
	}

	public void execute() {
		DndHelper.drop(((IGroup) target.getModel()).getSource());
	}

	public String getLabel() {
		return "connection command"; //$NON-NLS-1$
	}

	public JSFEditPart getSource() {
		return source;
	}

	public java.lang.String getSourceTerminal() {
		return sourceTerminal;
	}

	public JSFEditPart getTarget() {
		return target;
	}

	public String getTargetTerminal() {
		return targetTerminal;
	}

	public ILink getLink() {
		return link;
	}

	public void setSource(JSFEditPart newSource) {
		source = newSource;
	}

	public void setSourceTerminal(String newSourceTerminal) {
		sourceTerminal = newSourceTerminal;
	}

	public void setTarget(JSFEditPart newTarget) {
		target = newTarget;
	}

	public void setTargetTerminal(String newTargetTerminal) {
		targetTerminal = newTargetTerminal;
	}

	public void setLink(ILink l) {
		link = l;
	}

	public boolean canUndo() {
		return false;
	}

}
