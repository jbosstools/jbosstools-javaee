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

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.widgets.Shell;
import org.jboss.tools.seam.ui.pages.editor.ecore.pages.PagesElement;

public class SetConstraintCommand extends org.eclipse.gef.commands.Command {
	private Point newPos;
	private Dimension newSize;
	private Point oldPos;
	private Dimension oldSize;
	private PagesElement part;

	public void execute() {
		oldSize = part.getSize();
		oldPos = part.getLocation();
		
		part.changeLocationAndSize(newPos, newSize);
	}

	public void setShell(Shell shell) {
	}

	public void redo() {
		part.changeLocationAndSize(newPos, newSize);
	}

	public void setLocation(Rectangle r) {
		setLocation(r.getLocation());
		setSize(r.getSize());
	}

	public void setLocation(Point p) {
		newPos = p;
	}

	public void setPart(PagesElement part) {
		this.part = part;
	}

	public void setSize(Dimension p) {
		newSize = p;
	}

	public void undo() {
		part.changeLocationAndSize(oldPos, oldSize);
	}

}
