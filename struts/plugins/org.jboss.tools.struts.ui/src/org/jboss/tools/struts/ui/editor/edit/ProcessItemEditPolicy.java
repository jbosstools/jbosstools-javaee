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
package org.jboss.tools.struts.ui.editor.edit;

import java.util.List;

import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.requests.ReconnectRequest;

import org.jboss.tools.struts.ui.editor.dnd.DndHelper;
import org.jboss.tools.struts.ui.editor.model.IProcessItem;
import org.jboss.tools.struts.ui.editor.model.commands.ReconnectSourceLinkCommand;
import org.jboss.tools.struts.ui.editor.model.commands.ReconnectTargetLinkCommand;


public class ProcessItemEditPolicy
	extends StrutsElementEditPolicy
{
	
private ProcessItemEditPart getProcessItemEditPart(){
	return (ProcessItemEditPart)getHost();
}

public Command getCommand(Request request) {
	if (RequestConstants.REQ_CONNECTION_END.equals(request.getType()))
		return getConnectionEndCommand();
	else if (RequestConstants.REQ_RECONNECT_SOURCE.equals(request.getType())){
		if(((ProcessItemEditPart)getHost()).getProcessItemModel().isPage())
			return getReconnectionSourceCommand((ReconnectRequest)request);
	}else if (RequestConstants.REQ_RECONNECT_TARGET.equals(request.getType()))
		return getReconnectionTargetCommand((ReconnectRequest)request);
		
	return super.getCommand(request);
}

protected Command getConnectionEndCommand(){
	if(!DndHelper.isDropEnabled(getProcessItemEditPart().getProcessItemModel().getSource())) return null;
	ConnectionEndCommand command = new ConnectionEndCommand();
	command.setChild(getProcessItemEditPart().getProcessItemModel());
	return command;
}

protected Command getReconnectionSourceCommand(ReconnectRequest request){
	ReconnectSourceLinkCommand command = new ReconnectSourceLinkCommand();
	ConnectionAnchor ctor = getProcessItemEditPart().getSourceConnectionAnchor(request);
	List list = getProcessItemEditPart().getSourceConnections();
	LinkEditPart part;
	for(int i=0;i<list.size();i++){
		part = (LinkEditPart)list.get(i);
		if(part.equals(request.getConnectionEditPart())) continue;
		if(part != null){
			if(part.getConnectionFigure().getSourceAnchor().equals(ctor)){
				command.setLink(part.getLink());
				return command;
			}
		}
	}
	return null;
}

protected Command getReconnectionTargetCommand(ReconnectRequest request){
	ReconnectTargetLinkCommand command = new ReconnectTargetLinkCommand();
	
	command.setChild(getProcessItemEditPart().getProcessItemModel());
	return command;
}

static class ConnectionEndCommand 
	extends org.eclipse.gef.commands.Command{
	
		IProcessItem child = null;
	
	public ConnectionEndCommand(){
		super("ConnectionEndCommand");
	}
	
	public void setChild(IProcessItem child){
		this.child= child;
	}
	
	public void execute(){
	}
	
	public boolean canUndo() {
		return false;
	}
}


	
}