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

import org.jboss.tools.jsf.ui.editor.model.commands.ReconnectSourceLinkCommand;

public class PageEditPolicy extends JSFElementEditPolicy {

	private PageEditPart getPageEditPart() {
		return (PageEditPart) getHost();
	}

	public Command getCommand(Request request) {
		if (RequestConstants.REQ_RECONNECT_SOURCE.equals(request.getType())) {
			if (!((GroupEditPart) ((PageEditPart) getHost()).getParent())
					.isSingle())
				return getReconnectionSourceCommand((ReconnectRequest) request);
		}
		return super.getCommand(request);
	}

	protected Command getReconnectionSourceCommand(ReconnectRequest request) {
		ReconnectSourceLinkCommand command = new ReconnectSourceLinkCommand();
		ConnectionAnchor ctor = getPageEditPart().getSourceConnectionAnchor(
				request);
		List list = getPageEditPart().getSourceConnections();
		LinkEditPart part;
		for (int i = 0; i < list.size(); i++) {
			part = (LinkEditPart) list.get(i);
			if (part.equals(request.getConnectionEditPart()))
				continue;
			if (part != null) {
				if (part.getConnectionFigure().getSourceAnchor().equals(ctor)) {
					command.setLink(part.getLink());
					return command;
				}
			}
		}
		return null;
	}
	
}