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

import org.eclipse.draw2d.Connection;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editpolicies.GraphicalNodeEditPolicy;
import org.eclipse.gef.requests.CreateConnectionRequest;
import org.eclipse.gef.requests.ReconnectRequest;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.jsf.ui.editor.dnd.DndHelper;
import org.jboss.tools.jsf.ui.editor.figures.FigureFactory;
import org.jboss.tools.jsf.ui.editor.figures.NodeFigure;
import org.jboss.tools.jsf.ui.editor.model.commands.ConnectionCommand;

/**
 * 
 * @author eskimo(dgolovin@exadel.com)
 *
 */
public class JSFNodeEditPolicy extends
		GraphicalNodeEditPolicy {

	/**
	 * 
	 */
	protected Connection createDummyConnection(Request req) {
		PolylineConnection dummyConn = FigureFactory.createNewLink(null);
		return dummyConn;
	}

	/**
	 * 
	 */
	protected Command getConnectionCompleteCommand(
			CreateConnectionRequest request) {
		if (getJSFEditPart() instanceof PageEditPart) {
			if (!DndHelper
					.isDropEnabled((XModelObject) ((PageEditPart) getJSFEditPart())
							.getPageModel().getSource()))
				return null;
		} else if (getJSFEditPart() instanceof GroupEditPart) {
			if (!DndHelper
					.isDropEnabled((XModelObject) ((GroupEditPart) getJSFEditPart())
							.getGroupModel().getSource()))
				return null;
		}
		ConnectionCommand command = (ConnectionCommand) request
				.getStartCommand();
		command.setTarget((JSFEditPart) getJSFEditPart());
		ConnectionAnchor ancor = getJSFEditPart().getTargetConnectionAnchor(
				request);
		if (ancor == null)
			return null;
		command.setTargetTerminal(getJSFEditPart()
				.mapConnectionAnchorToTerminal(ancor));
		return command;
	}

	/**
	 * 
	 */
	protected Command getConnectionCreateCommand(CreateConnectionRequest request) {
		if (getJSFEditPart() instanceof PageEditPart) {
			if (!DndHelper
					.drag((XModelObject) ((PageEditPart) getJSFEditPart())
							.getPageModel().getSource()))
				return null;
		} else if (getJSFEditPart() instanceof GroupEditPart) {
			if (((GroupEditPart) getJSFEditPart()).getGroupModel()
					.getPageList().size() > 1)
				return null;
			if (!DndHelper
					.drag((XModelObject) ((GroupEditPart) getJSFEditPart())
							.getGroupModel().getSource()))
				return null;
		}
		ConnectionCommand command = new ConnectionCommand();
		command.setLink(null);
		command.setSource((JSFEditPart) getJSFEditPart());
		ConnectionAnchor ancor = getJSFEditPart().getSourceConnectionAnchor(
				request);
		command.setSourceTerminal(getJSFEditPart()
				.mapConnectionAnchorToTerminal(ancor));
		request.setStartCommand(command);
		return command;
	}

	/**
	 * 
	 * @return
	 */
	protected JSFEditPart getJSFEditPart() {
		return (JSFEditPart) getHost();
	}

	/**
	 * 
	 */
	protected Command getReconnectTargetCommand(ReconnectRequest request) {
		return null;
	}

	/**
	 * 
	 */
	protected Command getReconnectSourceCommand(ReconnectRequest request) {
		return null;
	}

	/**
	 * 
	 * @return
	 */
	protected NodeFigure getNodeFigure() {
		return (NodeFigure) ((GraphicalEditPart) getHost()).getFigure();
	}

}