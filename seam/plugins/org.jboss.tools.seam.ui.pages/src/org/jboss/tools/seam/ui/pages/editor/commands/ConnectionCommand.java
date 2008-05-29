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
import org.jboss.tools.seam.ui.pages.editor.ecore.pages.Link;
import org.jboss.tools.seam.ui.pages.editor.ecore.pages.PagesElement;
import org.jboss.tools.seam.ui.pages.editor.edit.PagesEditPart;


public class ConnectionCommand extends Command {
	protected PagesEditPart oldSource;
	protected String oldSourceTerminal;
	protected PagesEditPart oldTarget;
	protected String oldTargetTerminal;
	protected PagesEditPart source;
	protected String sourceTerminal;
	protected PagesEditPart target;
	protected String targetTerminal;
	protected Link link;

	public ConnectionCommand() {
		super("connection command");
	}

	public boolean canExecute() {
		if (target == null)
			return false;
		if (target.getModel() == null)
			return false;
		return DndHelper
				.isDropEnabled(((PagesElement) target.getModel()).getData());
	}

	public void execute() {
		DndHelper.drop(((PagesElement) target.getModel()).getData());
	}

	public String getLabel() {
		return "connection command";
	}

	public PagesEditPart getSource() {
		return source;
	}

	public java.lang.String getSourceTerminal() {
		return sourceTerminal;
	}

	public PagesEditPart getTarget() {
		return target;
	}

	public String getTargetTerminal() {
		return targetTerminal;
	}

	public Link getLink() {
		return link;
	}

	public void setSource(PagesEditPart newSource) {
		source = newSource;
	}

	public void setSourceTerminal(String newSourceTerminal) {
		sourceTerminal = newSourceTerminal;
	}

	public void setTarget(PagesEditPart newTarget) {
		target = newTarget;
	}

	public void setTargetTerminal(String newTargetTerminal) {
		targetTerminal = newTargetTerminal;
	}

	public void setLink(Link l) {
		link = l;
	}

	public boolean canUndo() {
		return false;
	}

}
