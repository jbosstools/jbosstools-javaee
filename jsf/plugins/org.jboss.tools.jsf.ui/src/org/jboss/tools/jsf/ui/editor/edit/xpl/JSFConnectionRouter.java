/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
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
package org.jboss.tools.jsf.ui.editor.edit.xpl;

import java.util.*;

import org.eclipse.draw2d.AbstractRouter;
import org.eclipse.draw2d.Connection;
import org.eclipse.draw2d.IFigure;
//import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.geometry.*;

import org.jboss.tools.jsf.ui.editor.figures.ConnectionFigure;
import org.jboss.tools.jsf.ui.editor.figures.GroupFigure;
import org.jboss.tools.jsf.ui.editor.figures.PageFigure;

final public class JSFConnectionRouter
	extends AbstractRouter
{

private Set<Integer> rowsUsed = new HashSet<Integer>();
private Set<Integer> colsUsed = new HashSet<Integer>();

private final static int STEP = 8;

private final static int STATUS_NOTHING = 0;
private final static int STATUS_HOLD = 2;
private final static int STATUS_SHIFT = 3;

private Map<Connection,ReservedInfo> reservedInfo = new HashMap<Connection,ReservedInfo>();

private class ReservedInfo {
	public List<Integer> reservedRows = new ArrayList<Integer>(2);
	public List<Integer> reservedCols = new ArrayList<Integer>(2);
}

private static Ray 	UP	= new Ray(0,-1),
				DOWN	= new Ray(0,1),
				LEFT	= new Ray(-1,0),
				RIGHT	= new Ray(1,0);


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
			IFigure fig = conn.getSourceAnchor().getOwner();
			GroupFigure group=null;
			if(fig instanceof GroupFigure)group = (GroupFigure)fig;
			else if(fig instanceof PageFigure)group = (GroupFigure)fig.getParent();
			if(group != null){
				index = group.group.getListOutputLinks().indexOf(((ConnectionFigure)conn).getLinkModel());
				size = group.group.getListOutputLinks().size();
			}else flag = false;
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
		if (!colsUsed.contains(i)){
			colsUsed.add(i);
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

protected Ray getEndDirection(Connection conn){
	return LEFT;
}

protected int getRowNear(Connection connection, int r, int n, int x){
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

	int proximity = 0;
	int direction = -1;
	if (r%2 == 1)
		r--;
	Integer i;
	while (proximity < r){
		i = new Integer(r + proximity*direction);
		if (!rowsUsed.contains(i)){
			rowsUsed.add(i);
			reserveRow(connection, i);
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
protected Ray getStartDirection(Connection conn){
	return RIGHT;
}

protected void processPositions(Ray start, Ray end, List positions, 
					  boolean horizontal, Connection conn) {
	removeReservedLines(conn);

	int pos[] = new int[positions.size()+2];
	if (horizontal)
		pos[0] = start.x;
	else
		pos[0] = start.y;
	int i;
	for (i=0; i< positions.size(); i++){
		pos[i+1] = ((Integer)positions.get(i)).intValue();
	}
	if (horizontal == (positions.size()%2 == 1))
		pos[++i] = end.x;
	else
		pos[++i] = end.y;

	PointList points = new PointList();
	points.addPoint(new Point(start.x, start.y));
	Point p;
	int current, prev, min, max;
	boolean adjust;
	for (i=2; i < pos.length - 1; i++){
		horizontal = !horizontal;
		prev = pos[i-1];
		current = pos[i];

		adjust = (i != pos.length-2);
		if (horizontal){
			if (adjust){
				min = pos[i-2];
				max = pos[i+2];
				pos[i] = current = getRowNear(conn,current,min,max);
				current -= current%STEP;
				pos[i] -= pos[i]%STEP;
			}
			p = new Point(prev,current);
		}
		else{
			if (adjust){
				min = pos[i-2];
				max = pos[i+2];
				boolean flag;
				if(i == 2 && pos.length == 5){ 
					flag = true;
				}else flag = false;
				pos[i] = current = getColumnNear(conn, current,min,max,flag);
				current -= current%STEP;
				pos[i] -= pos[i]%STEP;
			}
			p = new Point(current,prev);
		}
		points.addPoint(p);
	}
	points.addPoint(new Point(end.x, end.y));
	conn.setPoints(points);
}

/**
 * Removes the given connection from the map of constraints.
 *
 * @param connection The connection to remove.
 */
public void remove(Connection connection){
	removeReservedLines(connection);
}

protected void removeReservedLines(Connection connection) {
	ReservedInfo rInfo = (ReservedInfo) reservedInfo.get(connection);
	if (rInfo == null) return;
	
	for (int i = 0; i < rInfo.reservedRows.size(); i++){
		rowsUsed.remove(rInfo.reservedRows.get(i));
	}
	for (int i = 0; i < rInfo.reservedCols.size(); i++){
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

/**
 * Routes the {@link Connection}.
 *
 * @param conn The {@link Connection} to route.
 */
public void route(Connection conn) {
	if ((conn.getSourceAnchor() == null) || (conn.getTargetAnchor() == null)){
			return;
	}
	if(((ConnectionFigure)conn).getLinkModel() != null && ((ConnectionFigure)conn).getLinkModel().isShortcut()){
		Point startPoint = getStartPoint(conn);
		conn.translateToRelative(startPoint);
		List<Integer> positions = new ArrayList<Integer>(5);
		Ray start = new Ray(startPoint);
		positions.add(new Integer(start.y));
		
		Point endPoint = new Point(startPoint.x+18, startPoint.y);
		Ray end = new Ray(endPoint);
		processPositions(start, end, positions,true, conn);
		return;
	}
		
	if(((ConnectionFigure)conn).isManual()){
		if(conn.getPoints().size() < 4){
			PointList list = ((ConnectionFigure)conn).getLinkModel().getPointList();
			((ConnectionFigure)conn).setOldPoints(list.getFirstPoint(), list.getLastPoint());
			conn.setPoints(list);
		}
		if(hold((ConnectionFigure)conn)) return;
		else{
			((ConnectionFigure)conn).setManual(false);
			((ConnectionFigure)conn).clear();
		}
	}
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

	List<Integer> positions = new ArrayList<Integer>(5);
	boolean horizontal = true;
	// start horizontal segment
	positions.add(new Integer(start.y));
	horizontal = !horizontal;

	if((start.x > (end.x+20)) && (Math.abs(end.y-start.y) < 100)){
		i = startNormal.similarity(start.getAdded(startNormal.getScaled(10)));
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

		i = endNormal.similarity(end.getAdded(endNormal.getScaled(10)));
		i -= i%STEP;
		positions.add(new Integer(i));
		horizontal = !horizontal;
	}else{
		// vertical segment
		if (startNormal.dotProduct(direction) < STEP*2){
			i = startNormal.similarity(start.getAdded(startNormal.getScaled(10)));
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
			i = endNormal.similarity(end.getAdded(endNormal.getScaled(10)));
			i -= i%STEP;
			positions.add(new Integer(i));
			horizontal = !horizontal;
		}
	}
	// end horizontal segment
	positions.add(new Integer(end.y)); 
	
	processPositions(start, end, positions,true, conn);
	((ConnectionFigure)conn).setOldPoints(startPoint, endPoint);
}

private boolean hold(ConnectionFigure conn){
	Point p1,p2;
	
	Point startPoint = getStartPoint(conn);
	conn.translateToRelative(startPoint);
	
	Point endPoint = getEndPoint(conn);
	conn.translateToRelative(endPoint);
	
	int status = check(conn);
	if(status == STATUS_NOTHING) return true;
	else if(status == STATUS_SHIFT){
		shift(conn);
		conn.setOldPoints(startPoint, endPoint);
		conn.save();
		return true; 
	}

	PointList list = conn.getPoints();
	
	list.removePoint(0);
	list.insertPoint(startPoint, 0);
	
	list.removePoint(list.size()-1);
	list.addPoint(endPoint);
	
	if(list.size() > 2){
		p1 = list.getPoint(1);
		
		p1.y = startPoint.y;
		if(p1.x <= startPoint.x) return false;
		
		p2 = list.getPoint(list.size()-2);

		p2.y = endPoint.y;
		if(p2.x >= endPoint.x) return false;
		
		list.removePoint(1);
		list.insertPoint(p1, 1);
		int index = list.size()-2;

		list.removePoint(index);
		list.insertPoint(p2, index);
	}
	conn.setPoints(list);
	conn.setOldPoints(startPoint, endPoint);
	conn.save();
	return true;
}

private void shift(ConnectionFigure conn){
	Point startPoint = getStartPoint(conn);
	conn.translateToRelative(startPoint);
	
	Point oldStartPoint = conn.getOldStartPoint();
	
	Point shiftPoint = new Point(startPoint.x-oldStartPoint.x, startPoint.y-oldStartPoint.y);
	Point point;
	
	PointList list = conn.getPoints();
	if(list.getPoint(0).x == startPoint.x && list.getPoint(0).y == startPoint.y) return;
	
	for(int i=0;i<list.size();i++){
		point = list.getPoint(i);
		point.x += shiftPoint.x;
		point.y += shiftPoint.y;
		list.removePoint(i);
		list.insertPoint(point, i);
	}
	conn.setPoints(list);
}

private int check(ConnectionFigure conn){
	Point startPoint = getStartPoint(conn);
	conn.translateToRelative(startPoint);
	
	Point endPoint = getEndPoint(conn);
	conn.translateToRelative(endPoint);

	Point oldStartPoint = ((ConnectionFigure)conn).getOldStartPoint();
	
	Point oldEndPoint = ((ConnectionFigure)conn).getOldEndPoint();
	
	if(startPoint.x == oldStartPoint.x && startPoint.y == oldStartPoint.y && endPoint.x == oldEndPoint.x && endPoint.y == oldEndPoint.y)
		return STATUS_NOTHING;
	
	if((startPoint.x - oldStartPoint.x) == (endPoint.x - oldEndPoint.x) && (startPoint.y - oldStartPoint.y) == (endPoint.y - oldEndPoint.y)){
		return STATUS_SHIFT;
	}
	return STATUS_HOLD; 
}
}