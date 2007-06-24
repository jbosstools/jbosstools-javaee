/*******************************************************************************
 * Copyright (c) 2000, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Exadel, Inc.
 *     Red Hat, Inc. 
 *******************************************************************************/
package org.jboss.tools.struts.ui.editor.edit.xpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.eclipse.draw2d.AbstractRouter;
import org.eclipse.draw2d.Connection;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Ray;
import org.eclipse.draw2d.geometry.Rectangle;

public class DefaultRouter extends AbstractRouter {

	protected final static int STEP = 8;

	protected final static int INTEGER = 10;

	private static Ray 
			UP = new Ray(0, -1), 
			DOWN = new Ray(0, 1),
			LEFT = new Ray(-1, 0), 
			RIGHT = new Ray(1, 0);

	private Map rowsUsed = new HashMap();

	private Map colsUsed = new HashMap();

	protected final static int STATUS_NOTHING = 0;

	// private final static int STATUS_AUTOLAYOUT = 1;
	protected final static int STATUS_HOLD = 2;

	protected final static int STATUS_SHIFT = 3;

	// private Hashtable offsets = new Hashtable(7);

	private Map reservedInfo = new HashMap();

	private class ReservedInfo {
		public List reservedRows = new ArrayList(2);

		public List reservedCols = new ArrayList(2);
	}

	public void invalidate(Connection connection) {
		removeReservedLines(connection);
	}

	private int getColumnNear(Connection conn, int r, int n, int x, boolean flag){
		int index=0, size=0;
		int min = Math.min(n,x),
			max = Math.max(n,x);
		if (min > r){
			max = min;
			min = r - (min-r);
		}
		if (max < r){
			min = max;
			max = r + (r-max);
		}
		if(flag){
			if(conn.getSourceAnchor().getOwner() != null){
				if(getNearLink(conn, index, size) == null) flag=false;
			}else flag = false;
		}
		
		if(flag){
			int value = min+100+(size-index)*STEP-size/2*STEP;
			if(value <= min) return min+STEP;
			if(value >= max) return max-STEP;
			return value;
		}

		int proximity = 0;
		int direction = -1;
		if (r%2 == 1)
			r--;
		Integer i;
		while (proximity < r){
			i = new Integer(r + proximity*direction);
			if (!colsUsed.containsKey(i)){
				colsUsed.put(i,i);
				reserveColumn(conn, i);
				return i.intValue();
			}
			int j = i.intValue();
			if (j <= min)
				return j+STEP;
			if (j >= max)
				return j-STEP;
			if (direction == 1)
				direction = -1;
			else {
				direction = 1;
				proximity += STEP;
			}
		}
		return r;
	}


	protected Object getNearLink(Connection conn, int index, int size) {
		return null;
	}	
	
	/**
	 * Routes the {@link Connection}.
	 *
	 * @param conn The {@link Connection} to route.
	 */
	public void route(Connection conn) {
		if(preRoute(conn)) return;
		int i;
		Point startPoint = getStartPoint(conn);
		conn.translateToRelative(startPoint);
		
		Point endPoint = getEndPoint(conn);
		conn.translateToRelative(endPoint);

		Ray start = new Ray(startPoint);
		Ray end = new Ray(endPoint);
		Ray average = start.getAveraged(end);

		Ray direction = new Ray(start, end);
		Ray startNormal = getStartDirection(conn);
		Ray endNormal   = getEndDirection(conn);

		Vector positions = new Vector(5);
		boolean horizontal = true;//startNormal.isHorizontal();
		// start horizontal segment
		positions.add(new Integer(start.y));
		horizontal = !horizontal;

		if((start.x > (end.x+20)) && (Math.abs(end.y-start.y) < 100)){
			i = startNormal.similarity(start.getAdded(startNormal.getScaled(INTEGER)));
			positions.add(new Integer(i));
			horizontal = !horizontal;

			if(conn.getSourceAnchor().getOwner() == null){
				i = average.y;
			}else{
				Rectangle rec = conn.getSourceAnchor().getOwner().getBounds();
			
				i = rec.y+rec.height+8;
			}
			i -= i%STEP;
			positions.add(new Integer(i));
			horizontal = !horizontal;

			i = endNormal.similarity(end.getAdded(endNormal.getScaled(INTEGER)));
			i -= i%STEP;
			positions.add(new Integer(i));
			horizontal = !horizontal;
		}else{
			// vertical segment
			if (startNormal.dotProduct(direction) < STEP*2){
				i = startNormal.similarity(start.getAdded(startNormal.getScaled(INTEGER)));
				i -= i%STEP;
				positions.add(new Integer(i));
				horizontal = !horizontal;
			}

			//	middle vertical or horizontal segment
			if (horizontal) i = average.y;
			else i = average.x;
			i -= i%STEP;
			positions.add(new Integer(i));
			horizontal = !horizontal;

			//	vertical segment
			if (startNormal.dotProduct(direction) < STEP*2){
				i = endNormal.similarity(end.getAdded(endNormal.getScaled(INTEGER)));
				i -= i%STEP;
				positions.add(new Integer(i));
				horizontal = !horizontal;
			}
		}
		// end horizontal segment
		positions.add(new Integer(end.y)); 
		
		postRoute(conn, start, end, startPoint, endPoint, positions);
	}

	protected void postRoute(Connection conn, Ray start, Ray end, Point startPoint, Point endPoint, Vector positions) {
		
	}

	protected boolean preRoute(Connection conn) {
		return false;
	}
	
	
	
	public void calcPositions(Connection conn, Vector positions) {
		int i;
		Point startPoint = getStartPoint(conn);
		conn.translateToRelative(startPoint);

		Point endPoint = getEndPoint(conn);
		conn.translateToRelative(endPoint);

		Ray start = new Ray(startPoint);
		Ray end = new Ray(endPoint);
		Ray average = start.getAveraged(end);

		Ray direction = new Ray(start, end);
		Ray startNormal = getStartDirection(conn);
		Ray endNormal = getEndDirection(conn);

		boolean horizontal = true;// startNormal.isHorizontal();
		// start horizontal segment
		positions.add(new Integer(start.y));
		horizontal = !horizontal;

		if ((start.x > (end.x + 20)) && (Math.abs(end.y - start.y) < 100)) {
			i = startNormal.similarity(start.getAdded(startNormal
					.getScaled(INTEGER)));
			positions.add(new Integer(i));
			horizontal = !horizontal;

			if (conn.getSourceAnchor().getOwner() == null) {
				i = average.y;
			} else {
				Rectangle rec = conn.getSourceAnchor().getOwner().getBounds();

				i = rec.y + rec.height + 8;
			}
			i -= i % STEP;
			positions.add(new Integer(i));
			horizontal = !horizontal;

			i = endNormal
					.similarity(end.getAdded(endNormal.getScaled(INTEGER)));
			i -= i % STEP;
			positions.add(new Integer(i));
			horizontal = !horizontal;
		} else {
			// vertical segment
			if (startNormal.dotProduct(direction) < STEP * 2) {
				i = startNormal.similarity(start.getAdded(startNormal
						.getScaled(INTEGER)));
				i -= i % STEP;
				positions.add(new Integer(i));
				horizontal = !horizontal;
			}

			// middle vertical or horizontal segment
			if (horizontal)
				i = average.y;
			else
				i = average.x;
			i -= i % STEP;
			positions.add(new Integer(i));
			horizontal = !horizontal;

			// vertical segment
			if (startNormal.dotProduct(direction) < STEP * 2) {
				i = endNormal.similarity(end.getAdded(endNormal
						.getScaled(INTEGER)));
				i -= i % STEP;
				positions.add(new Integer(i));
				horizontal = !horizontal;
			}
		}
		// end horizontal segment
		positions.add(new Integer(end.y));
	}

	protected Ray getStartDirection(Connection conn) {
		return RIGHT;
	}

	protected Ray getEndDirection(Connection conn) {
		return LEFT;
	}

	protected int getRowNear(Connection connection, int r, int n, int x) {
		int min = Math.min(n, x), max = Math.max(n, x);
		if (min > r) {
			max = min;
			min = r - (min - r);
		}
		if (max < r) {
			min = max;
			max = r + (r - max);
		}

		int proximity = 0;
		int direction = -1;
		if (r % 2 == 1)
			r--;
		Integer i;
		while (proximity < r) {
			i = new Integer(r + proximity * direction);
			if (!rowsUsed.containsKey(i)) {
				rowsUsed.put(i, i);
				reserveRow(connection, i);
				return i.intValue();
			}
			int j = i.intValue();
			if (j <= min)
				return j + STEP;
			if (j >= max)
				return j - STEP;
			if (direction == 1)
				direction = -1;
			else {
				direction = 1;
				proximity += STEP;
			}
		}
		return r;
	}

	/**
	 * Removes the given connection from the map of constraints.
	 * 
	 * @param connection
	 *            The connection to remove.
	 */
	public void remove(Connection connection) {
		removeReservedLines(connection);
	}

	protected void removeReservedLines(Connection connection) {
		ReservedInfo rInfo = (ReservedInfo) reservedInfo.get(connection);
		if (rInfo == null)
			return;

		for (int i = 0; i < rInfo.reservedRows.size(); i++) {
			rowsUsed.remove(rInfo.reservedRows.get(i));
		}
		for (int i = 0; i < rInfo.reservedCols.size(); i++) {
			colsUsed.remove(rInfo.reservedCols.get(i));
		}
		reservedInfo.remove(connection);
	}

	protected void reserveColumn(Connection connection, Integer column) {
		ReservedInfo info = (ReservedInfo) reservedInfo.get(connection);
		if (info == null) {
			info = new ReservedInfo();
			reservedInfo.put(connection, info);
		}
		info.reservedCols.add(column);
	}

	protected void reserveRow(Connection connection, Integer row) {
		ReservedInfo info = (ReservedInfo) reservedInfo.get(connection);
		if (info == null) {
			info = new ReservedInfo();
			reservedInfo.put(connection, info);
		}
		info.reservedRows.add(row);
	}

	protected void processPositions(Ray start, Ray end, List positions,
			boolean horizontal, Connection conn) {
		removeReservedLines(conn);

		int pos[] = new int[positions.size() + 2];
		if (horizontal)
			pos[0] = start.x;
		else
			pos[0] = start.y;
		int i;
		for (i = 0; i < positions.size(); i++) {
			pos[i + 1] = ((Integer) positions.get(i)).intValue();
		}
		if (horizontal == (positions.size() % 2 == 1))
			pos[++i] = end.x;
		else
			pos[++i] = end.y;

		PointList points = new PointList();
		points.addPoint(new Point(start.x, start.y));
		Point p;
		int current, prev, min, max;
		boolean adjust;
		for (i = 2; i < pos.length - 1; i++) {
			horizontal = !horizontal;
			prev = pos[i - 1];
			current = pos[i];

			adjust = (i != pos.length - 2);
			if (horizontal) {
				if (adjust) {
					min = pos[i - 2];
					max = pos[i + 2];
					pos[i] = current = getRowNear(conn, current, min, max);
					current -= current % STEP;
					pos[i] -= pos[i] % STEP;
				}
				p = new Point(prev, current);
			} else {
				if (adjust) {
					min = pos[i - 2];
					max = pos[i + 2];
					boolean flag;
					if (i == 2 && pos.length == 5) {
						flag = true;
					} else
						flag = false;
					pos[i] = current = getColumnNear(conn, current, min + STEP
							- 1, max - STEP + 1, flag);
					current -= current % STEP;
					pos[i] -= pos[i] % STEP;
				}
				p = new Point(current, prev);
			}
			points.addPoint(p);
		}
		points.addPoint(new Point(end.x, end.y));
		conn.setPoints(points);

	}
	
	protected Ray getDirection(Rectangle r, Point p){
		int i, distance = Math.abs(r.x - p.x);
		Ray direction;
		
		direction = LEFT;

		i = Math.abs(r.y - p.y);
		if (i <= distance){
			distance = i;
			direction = UP;
		}

		i = Math.abs(r.bottom()-p.y);
		if (i <= distance){
			distance = i;
			direction = DOWN;
		}

		i = Math.abs(r.right()-p.x);
		if (i < distance){
			distance = i;
			direction = RIGHT;
		}

		return direction;
	}

}
