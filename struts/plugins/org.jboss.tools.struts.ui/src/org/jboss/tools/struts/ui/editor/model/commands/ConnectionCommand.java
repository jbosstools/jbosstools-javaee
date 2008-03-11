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
import org.jboss.tools.struts.ui.editor.edit.StrutsEditPart;
import org.jboss.tools.struts.ui.editor.model.ILink;
import org.jboss.tools.struts.ui.editor.model.IProcessItem;

//import org.eclipse.gef.examples.logicdesigner.LogicMessages;
//import org.eclipse.gef.examples.logicdesigner.model.LogicSubpart;
//import org.eclipse.gef.examples.logicdesigner.model.Wire;

public class ConnectionCommand
	extends Command
{

protected StrutsEditPart oldSource;
protected String oldSourceTerminal;
protected StrutsEditPart oldTarget;
protected String oldTargetTerminal;
protected StrutsEditPart source;
protected String sourceTerminal;
protected StrutsEditPart target; 
protected String targetTerminal; 
protected ILink link;

public ConnectionCommand() {
	super("connection command");
}

public boolean canExecute(){
	if(target == null) return false;
	if(target.getModel() == null) return false;
	return DndHelper.isDropEnabled(((IProcessItem)target.getModel()).getSource());
}

public void execute() {
	//DndHelper.drag(source.getPageModel().getSource());
	DndHelper.drop(((IProcessItem)target.getModel()).getSource());
}

public String getLabel() {
	return "connection command";
}

public StrutsEditPart getSource() {
	return source;
}

public java.lang.String getSourceTerminal() {
	return sourceTerminal;
}

public StrutsEditPart getTarget() {
	return target;
}

public String getTargetTerminal() {
	return targetTerminal;
}

public ILink getLink() {
	return link;
}

public void setSource(StrutsEditPart newSource) {
	source = newSource;
}

public void setSourceTerminal(String newSourceTerminal) {
	sourceTerminal = newSourceTerminal;
}

public void setTarget(StrutsEditPart newTarget) {
	target = newTarget;
}

public void setTargetTerminal(String newTargetTerminal) {
	targetTerminal = newTargetTerminal;
}

public void setLink(ILink l) {
	link = l;
	/*oldSource = w.getSource();
	oldTarget = w.getTarget();
	oldSourceTerminal = w.getSourceTerminal();
	oldTargetTerminal = w.getTargetTerminal();*/	
}

public boolean canUndo(){
	return false;
}
}
