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
package org.jboss.tools.seam.ui.pages.editor.edit;

import java.util.List;

import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.gef.Request;
import org.eclipse.gef.RequestConstants;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.requests.ReconnectRequest;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.seam.ui.pages.editor.commands.ReconnectSourceLinkCommand;
import org.jboss.tools.seam.ui.pages.editor.commands.ReconnectSourceLinkCommand2;
import org.jboss.tools.seam.ui.pages.editor.commands.ReconnectTargetLinkCommand;
import org.jboss.tools.seam.ui.pages.editor.dnd.DndHelper;
import org.jboss.tools.seam.ui.pages.editor.ecore.pages.Page;

public class PageEditPolicy extends org.eclipse.gef.editpolicies.ComponentEditPolicy {
	
	private PageEditPart getPageEditPart() {
		return (PageEditPart) getHost();
	}

	public Command getCommand(Request request) {
		if (RequestConstants.REQ_CONNECTION_END.equals(request.getType()))
			return getConnectionEndCommand();
		else if (RequestConstants.REQ_RECONNECT_SOURCE
				.equals(request.getType())) {
				return getReconnectionSourceCommand((ReconnectRequest) request);
		} else if (RequestConstants.REQ_RECONNECT_TARGET.equals(request
				.getType()))
			return getReconnectionTargetCommand((ReconnectRequest) request);

		return super.getCommand(request);
	}

	protected Command getConnectionEndCommand() {
		Page p = (Page)getPageEditPart().getModel();
		if (!DndHelper.isDropEnabled((XModelObject)p.getData()))
			return null;
		ConnectionEndCommand command = new ConnectionEndCommand();
		command.setChild(p);
		return command;
	}

	protected Command getReconnectionSourceCommand(ReconnectRequest request) {
		ReconnectSourceLinkCommand command = new ReconnectSourceLinkCommand();
		ConnectionAnchor ctor = getPageEditPart().getSourceConnectionAnchor(
				request);
		List list = getPageEditPart().getSourceConnections();
		LinkEditPart part;
		for (int i = 0; i < list.size(); i++) {
			part = (LinkEditPart) list.get(i);
			if (part.equals(request.getConnectionEditPart())) {
				continue;
			}
			if (part != null) {
				if (part.getConnectionFigure().getSourceAnchor().equals(ctor)) {
					command.setLink(part.getLink());
					return command;
				}
			}
		}
		ReconnectSourceLinkCommand2 command2 = new ReconnectSourceLinkCommand2();
		command2.setPage(getPageEditPart().getPageModel());
		return command2;
	}

	protected Command getReconnectionTargetCommand(ReconnectRequest request) {
		Page p = (Page)getPageEditPart().getModel();
		ReconnectTargetLinkCommand command = new ReconnectTargetLinkCommand();
		command.setChild(p);
		return command;
	}

	static class ConnectionEndCommand extends org.eclipse.gef.commands.Command {

		Page child = null;

		public ConnectionEndCommand() {
			super("ConnectionEndCommand");
		}

		public void setChild(Page child) {
			this.child = child;
		}

		public void execute() {
		}

		public boolean canUndo() {
			return false;
		}
	}
	
}
