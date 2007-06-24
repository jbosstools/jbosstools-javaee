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

import java.util.Vector;

import org.eclipse.draw2d.Connection;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Ray;
import org.eclipse.draw2d.geometry.Rectangle;

import org.jboss.tools.struts.ui.editor.edit.xpl.DefaultRouter;
import org.jboss.tools.struts.ui.editor.figures.ConnectionFigure;
import org.jboss.tools.struts.ui.editor.figures.ForwardFigure;
import org.jboss.tools.struts.ui.editor.figures.ProcessItemFigure;

final public class StrutsConnectionRouter extends DefaultRouter {

	protected Object getNearLink(Connection conn, int index, int size) {

		IFigure fig = conn.getSourceAnchor().getOwner();
		ProcessItemFigure processItem = null;
		if (fig instanceof ProcessItemFigure)
			processItem = (ProcessItemFigure) fig;
		else if (fig instanceof ForwardFigure)
			processItem = (ProcessItemFigure) fig.getParent();
		Object link = ((ConnectionFigure) conn).getLinkModel();

		if (processItem != null) {
			index = processItem.processItem.getListOutputLinks().indexOf(link);
			size = processItem.processItem.getListOutputLinks().size();
			return link;
		}
		return null;
	}

	protected boolean preRoute(Connection conn) {
		if ((conn.getSourceAnchor() == null)
				|| (conn.getTargetAnchor() == null)) {
			return true;
		}
		if (((ConnectionFigure) conn).getLinkModel() != null
				&& ((ConnectionFigure) conn).getLinkModel().isShortcut()) {
			Point startPoint = getStartPoint(conn);
			conn.translateToRelative(startPoint);
			Vector positions = new Vector(5);
			Ray start = new Ray(startPoint);
			positions.add(new Integer(start.y));

			Point endPoint = new Point(startPoint.x + 18, startPoint.y);
			Ray end = new Ray(endPoint);
			processPositions(start, end, positions, true, conn);
			return true;
		}

		if (((ConnectionFigure) conn).isManual()) {
			if (conn.getPoints().size() < 4) {
				PointList list = ((ConnectionFigure) conn).getLinkModel()
						.getPointList();
				((ConnectionFigure) conn).setOldPoints(list.getFirstPoint(),
						list.getLastPoint());
				conn.setPoints(list);
			}
			if (hold((ConnectionFigure) conn))
				return true;
			else {
				((ConnectionFigure) conn).setManual(false);
				((ConnectionFigure) conn).clear();
			}
		}

		return false;
	}

	protected void postRoute(Connection conn, Ray start, Ray end,
			Point startPoint, Point endPoint, Vector positions) {
		processPositions(start, end, positions, true, conn);
		((ConnectionFigure) conn).setOldPoints(startPoint, endPoint);

	}

	protected int check(ConnectionFigure conn) {
		Point startPoint = getStartPoint(conn);
		conn.translateToRelative(startPoint);

		Point endPoint = getEndPoint(conn);
		conn.translateToRelative(endPoint);

		Point oldStartPoint = ((ConnectionFigure) conn).getOldStartPoint();
		// conn.translateToRelative(oldStartPoint);

		Point oldEndPoint = ((ConnectionFigure) conn).getOldEndPoint();
		// conn.translateToRelative(oldEndPoint);

		if (startPoint.x == oldStartPoint.x && startPoint.y == oldStartPoint.y
				&& endPoint.x == oldEndPoint.x && endPoint.y == oldEndPoint.y)
			return STATUS_NOTHING;

		if ((startPoint.x - oldStartPoint.x) == (endPoint.x - oldEndPoint.x)
				&& (startPoint.y - oldStartPoint.y) == (endPoint.y - oldEndPoint.y)) {
			return STATUS_SHIFT;
		}
		return STATUS_HOLD;
	}

	protected boolean hold(ConnectionFigure conn) {
		Point p1, p2;

		Point startPoint = getStartPoint(conn);
		conn.translateToRelative(startPoint);

		Point endPoint = getEndPoint(conn);
		conn.translateToRelative(endPoint);

		int status = check(conn);
		if (status == STATUS_NOTHING)
			return true;
		else if (status == STATUS_SHIFT) {
			shift(conn);
			conn.setOldPoints(startPoint, endPoint);
			conn.save();
			return true;
		}

		PointList list = conn.getPoints();

		list.removePoint(0);
		list.insertPoint(startPoint, 0);

		list.removePoint(list.size() - 1);
		list.addPoint(endPoint);

		if (list.size() > 2) {
			p1 = list.getPoint(1);

			p1.y = startPoint.y;
			if (p1.x <= startPoint.x)
				return false;

			p2 = list.getPoint(list.size() - 2);

			p2.y = endPoint.y;
			if (p2.x >= endPoint.x)
				return false;

			list.removePoint(1);
			list.insertPoint(p1, 1);
			int index = list.size() - 2;

			list.removePoint(index);
			list.insertPoint(p2, index);
		}
		conn.setPoints(list);
		conn.setOldPoints(startPoint, endPoint);
		conn.save();
		return true;
	}

	private void shift(ConnectionFigure conn) {
		Point startPoint = getStartPoint(conn);
		conn.translateToRelative(startPoint);

		Point oldStartPoint = conn.getOldStartPoint();
		// conn.translateToRelative(oldStartPoint);

		Point shiftPoint = new Point(startPoint.x - oldStartPoint.x,
				startPoint.y - oldStartPoint.y);
		Point point;

		PointList list = conn.getPoints();
		if (list.getPoint(0).x == startPoint.x
				&& list.getPoint(0).y == startPoint.y)
			return;

		for (int i = 0; i < list.size(); i++) {
			point = list.getPoint(i);
			point.x += shiftPoint.x;
			point.y += shiftPoint.y;
			list.removePoint(i);
			list.insertPoint(point, i);
		}
		conn.setPoints(list);
	}

}