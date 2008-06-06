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
import org.jboss.tools.seam.ui.pages.editor.commands.ConnectionCommand;
import org.jboss.tools.seam.ui.pages.editor.dnd.DndHelper;
import org.jboss.tools.seam.ui.pages.editor.figures.FigureFactory;
import org.jboss.tools.seam.ui.pages.editor.figures.NodeFigure;

public class PagesNodeEditPolicy extends
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
		if (getPagesEditPart() instanceof PageEditPart) {
			if (!DndHelper
					.isDropEnabled((XModelObject) ((PageEditPart) getPagesEditPart())
							.getPageModel().getData()))
				return null;
		} else if (getPagesEditPart() instanceof PagesEditPart) {
			if (!DndHelper
					.isDropEnabled((XModelObject) ((PagesEditPart) getPagesEditPart())
							.getElementModel().getData()))
				return null;
		}
		ConnectionCommand command = (ConnectionCommand) request
				.getStartCommand();
		command.setTarget((PagesEditPart) getPagesEditPart());
		ConnectionAnchor ancor = getPagesEditPart().getTargetConnectionAnchor(
				request);
		if (ancor == null)
			return null;
		command.setTargetTerminal(getPagesEditPart()
				.mapConnectionAnchorToTerminal(ancor));
		return command;
	}

	/**
	 * 
	 */
	protected Command getConnectionCreateCommand(CreateConnectionRequest request) {
		if (getPagesEditPart() instanceof PageEditPart) {
			if (!DndHelper
					.drag((XModelObject) ((PageEditPart) getPagesEditPart())
							.getPageModel().getData()))
				return null;
		} else if (getPagesEditPart() instanceof PagesEditPart) {
			/*if (((PagesEditPart) getPagesEditPart()).getElementModel()
					.getPageList().size() > 1)
				return null;*/
			if (!DndHelper
					.drag((XModelObject) ((PagesEditPart) getPagesEditPart())
							.getElementModel().getData()))
				return null;
		}
		ConnectionCommand command = new ConnectionCommand();
		command.setLink(null);
		command.setSource((PagesEditPart) getPagesEditPart());
		ConnectionAnchor ancor = getPagesEditPart().getSourceConnectionAnchor(
				request);
		command.setSourceTerminal(getPagesEditPart()
				.mapConnectionAnchorToTerminal(ancor));
		request.setStartCommand(command);
		return command;
	}

	/**
	 * 
	 * @return
	 */
	protected PagesEditPart getPagesEditPart() {
		return (PagesEditPart) getHost();
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