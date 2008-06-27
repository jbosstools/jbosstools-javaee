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

import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.tools.ConnectionEndpointTracker;

import org.jboss.tools.jsf.ui.editor.figures.ConnectionFigure;

public class JSFConnectionDragTracker extends ConnectionEndpointTracker {
	private boolean vertical;
	private PointList list;
	private int index1, index2;
	private Point point1, point2;
	private boolean first = true;

	public JSFConnectionDragTracker(ConnectionEditPart cep, boolean vertical,
			int id) {
		super(cep);
		this.vertical = vertical;
		list = getConnection().getPoints();
		index1 = id;
		index2 = id + 1;
		point1 = list.getPoint(index1);
		point2 = list.getPoint(index2);
	}

	public void commitDrag() {
	}

	protected boolean handleDragInProgress() {
		if (first) {
			list = getConnection().getPoints();
			point1 = list.getPoint(index1);
			point2 = list.getPoint(index2);

			first = false;
		}
		Dimension delta = getDragMoveDelta();
		Point p1, p2;
		if (vertical) {
			p1 = new Point(point1.x, point1.y + delta.height);
			p1.y -= p1.y % 8;
			p2 = new Point(point2.x, point2.y + delta.height);
			p2.y -= p2.y % 8;
		} else {
			p1 = new Point(point1.x + delta.width, point1.y);
			p1.x -= p1.x % 8;
			p2 = new Point(point2.x + delta.width, point2.y);
			p2.x -= p2.x % 8;
			if (index1 == 1 && p1.x < list.getPoint(0).x + 5) {
				p1.x = list.getPoint(0).x + 5;
				p2.x = list.getPoint(0).x + 5;
			}
			if (index2 == list.size() - 2
					&& p1.x > list.getPoint(list.size() - 1).x - 5) {
				p1.x = list.getPoint(list.size() - 1).x - 5;
				p2.x = list.getPoint(list.size() - 1).x - 5;
			}

		}
		list.removePoint(index1);
		list.removePoint(index1);
		list.insertPoint(p1, index1);
		list.insertPoint(p2, index2);

		getConnection().setPoints(list);
		((ConnectionFigure) getConnection()).setManual(true);

		return true;
	}

	protected boolean handleButtonUp(int button) {
		if (stateTransition(STATE_DRAG_IN_PROGRESS, STATE_TERMINAL)) {
			first = true;

			((LinkEditPart) getConnectionEditPart()).save();
		}
		return true;
	}

	protected Point getLocation() {
		Point p = new Point(getCurrentInput().getMouseLocation());
		if (getFlag(1)) {
			p.x -= p.x % 8;
			p.y -= p.y % 8;
		}
		return p;
	}

	protected Point getStartLocation() {
		Point p = super.getStartLocation().getCopy();
		if (getFlag(1)) {
			p.x -= p.x % 8;
			p.y -= p.y % 8;
		}
		return p;
	}

}
