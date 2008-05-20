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

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.widgets.Shell;

import org.jboss.tools.struts.ui.editor.StrutsEditor;
import org.jboss.tools.struts.ui.editor.model.IProcessItem;
import org.jboss.tools.struts.ui.editor.model.IStrutsElement;

public class SetConstraintCommand extends org.eclipse.gef.commands.Command {

private Point newPos;
private Dimension newSize;
private Point oldPos;
private Dimension oldSize;
private IStrutsElement part;
private Shell shell=null;

public void execute() {
	boolean flag = false;
	oldSize = part.getSize();
	oldPos  = part.getPosition();
	if(part instanceof IProcessItem && part.getStrutsModel().isBorderPaint()){
		if(newPos.x < 0){newPos.x = 0;flag = true;}
		if(newPos.y < 0){newPos.y = 0;flag = true;}
		if(newPos.x > 2000){newPos.x = 2000;flag = true;}
		if(newPos.y > 2000){newPos.y = 2000;flag = true;}
	}
	part.setJump(false);
	part.setPosition(newPos);
	part.setSize(newSize);
}

public void setShell(Shell shell){
	this.shell = shell;
}

public void redo() {
	part.setSize(newSize);
	part.setPosition(newPos);
}

public void setLocation(Rectangle r){
	setLocation(r.getLocation());
	setSize(r.getSize());
}

public void setLocation(Point p) {
	newPos = p;
}

public void setPart(IStrutsElement part) {
	this.part = part;
}

public void setSize(Dimension p) {
	newSize = p;
}

public void undo() {
	part.setSize(oldSize);
	part.setPosition(oldPos);
}

}
