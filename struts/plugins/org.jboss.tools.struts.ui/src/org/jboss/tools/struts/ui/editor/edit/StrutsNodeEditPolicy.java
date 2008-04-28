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

import org.eclipse.draw2d.Connection;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.requests.CreateConnectionRequest;
import org.eclipse.gef.requests.ReconnectRequest;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.struts.ui.editor.dnd.DndHelper;
import org.jboss.tools.struts.ui.editor.figures.FigureFactory;
import org.jboss.tools.struts.ui.editor.figures.NodeFigure;
import org.jboss.tools.struts.ui.editor.model.commands.ConnectionCommand;

public class StrutsNodeEditPolicy extends
		org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy {

	protected Connection createDummyConnection(Request request) {
		PolylineConnection connection = FigureFactory.createNewLink(null);
		return connection;
	}

	protected Command getConnectionCompleteCommand(
			CreateConnectionRequest request) {
		if (getStrutsEditPart() instanceof ForwardEditPart) {
			if (!DndHelper
					.isDropEnabled((XModelObject) ((ForwardEditPart) getStrutsEditPart())
							.getForwardModel().getSource()))
				return null;
		} else if (getStrutsEditPart() instanceof ProcessItemEditPart) {
			if (!DndHelper
					.isDropEnabled((XModelObject) ((ProcessItemEditPart) getStrutsEditPart())
							.getProcessItemModel().getSource()))
				return null;
		}
		ConnectionCommand command = (ConnectionCommand) request
				.getStartCommand();
		command.setTarget((StrutsEditPart) getStrutsEditPart());
		ConnectionAnchor anchor = getStrutsEditPart().getTargetConnectionAnchor(
				request);
		if (anchor == null)
			return null;
		command.setTargetTerminal(getStrutsEditPart()
				.mapConnectionAnchorToTerminal(anchor));
		return command;
	}

	protected Command getConnectionCreateCommand(CreateConnectionRequest request) {
		if (getStrutsEditPart() instanceof ForwardEditPart) {
			if (!DndHelper
					.drag((XModelObject) ((ForwardEditPart) getStrutsEditPart())
							.getForwardModel().getSource()))
				return null;
		} else if (getStrutsEditPart() instanceof ProcessItemEditPart) {
			if (getStrutsEditPart() instanceof CommentEditPart)
				return null;
			if (!DndHelper
					.drag((XModelObject) ((ProcessItemEditPart) getStrutsEditPart())
							.getProcessItemModel().getSource()))
				return null;
		}
		ConnectionCommand command = new ConnectionCommand();
		command.setLink(null);
		command.setSource((StrutsEditPart) getStrutsEditPart());
		ConnectionAnchor anchor = getStrutsEditPart().getSourceConnectionAnchor(
				request);
		command.setSourceTerminal(getStrutsEditPart()
				.mapConnectionAnchorToTerminal(anchor));
		request.setStartCommand(command);
		return command;
	}

	protected StrutsEditPart getStrutsEditPart() {
		return (StrutsEditPart) getHost();
	}

	protected Command getReconnectTargetCommand(ReconnectRequest request) {
		return null;
	}

	protected Command getReconnectSourceCommand(ReconnectRequest request) {
		return null;
	}

	protected NodeFigure getNodeFigure() {
		return (NodeFigure) ((GraphicalEditPart) getHost()).getFigure();
	}

}