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
package org.jboss.tools.jsf.ui.editor.edit;

import java.util.List;

import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.requests.ReconnectRequest;

import org.jboss.tools.jsf.ui.editor.dnd.DndHelper;
import org.jboss.tools.jsf.ui.editor.model.IGroup;
import org.jboss.tools.jsf.ui.editor.model.commands.ReconnectSourceLinkCommand;
import org.jboss.tools.jsf.ui.editor.model.commands.ReconnectSourceLinkCommand2;
import org.jboss.tools.jsf.ui.editor.model.commands.ReconnectTargetLinkCommand;

public class GroupEditPolicy extends JSFElementEditPolicy {
	
	private GroupEditPart getGroupEditPart() {
		return (GroupEditPart) getHost();
	}

	public Command getCommand(Request request) {
		if (RequestConstants.REQ_CONNECTION_END.equals(request.getType()))
			return getConnectionEndCommand();
		else if (RequestConstants.REQ_RECONNECT_SOURCE
				.equals(request.getType())) {
			if (((GroupEditPart) getHost()).isSingle())
				return getReconnectionSourceCommand((ReconnectRequest) request);
		} else if (RequestConstants.REQ_RECONNECT_TARGET.equals(request
				.getType()))
			return getReconnectionTargetCommand((ReconnectRequest) request);

		return super.getCommand(request);
	}

	protected Command getConnectionEndCommand() {
		if (!DndHelper.isDropEnabled(getGroupEditPart().getGroupModel()
				.getSource()))
			return null;
		ConnectionEndCommand command = new ConnectionEndCommand();
		command.setChild(getGroupEditPart().getGroupModel());
		return command;
	}

	protected Command getReconnectionSourceCommand(ReconnectRequest request) {
		ReconnectSourceLinkCommand command = new ReconnectSourceLinkCommand();
		ConnectionAnchor ctor = getGroupEditPart().getSourceConnectionAnchor(
				request);
		List list = getGroupEditPart().getSourceConnections();
		LinkEditPart part;
		for (int i = 0; i < list.size(); i++) {
			part = (LinkEditPart) list.get(i);
			if (part.equals(request.getConnectionEditPart()))
				continue;
			if (part != null) {
				if (part.getConnectionFigure().getSourceAnchor().equals(ctor)) {
					command.setLink(part.getLink());
					if (command.canExecute())
						return command;
				}
			}
		}
		ReconnectSourceLinkCommand2 command2 = new ReconnectSourceLinkCommand2();
		command2.setGroup(getGroupEditPart().getGroupModel());
		return command2;
	}

	protected Command getReconnectionTargetCommand(ReconnectRequest request) {
		ReconnectTargetLinkCommand command = new ReconnectTargetLinkCommand();

		command.setChild(getGroupEditPart().getGroupModel());
		return command;
	}

	static class ConnectionEndCommand extends org.eclipse.gef.commands.Command {

		IGroup child = null;

		public ConnectionEndCommand() {
			super("ConnectionEndCommand"); //$NON-NLS-1$
		}

		public void setChild(IGroup child) {
			this.child = child;
		}

		public void execute() {
		}

		public boolean canUndo() {
			return false;
		}
	}
	
}
