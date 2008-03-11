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
package org.jboss.tools.struts.ui.editor.figures;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Rectangle;

public class PageFeedbackFigure extends PageFigure {
	public PageFeedbackFigure(){
		super(null,null);
	}
	protected void resizeFigure(){
		if(width == getSize().width && height == getSize().height) return;
		
		int start=0;
	 	width = getSize().width;
	 	height = getSize().height;
	 	
		fillPointlist = new PointList();
		
		fillPointlist.addPoint(start,20);
		fillPointlist.addPoint(start+23, 20);
		fillPointlist.addPoint(start+23, 0);
		fillPointlist.addPoint(width-15, 0);
		fillPointlist.addPoint(width-1, 14);
		fillPointlist.addPoint(width-1, height-1);
		fillPointlist.addPoint(start, height-1);
	}
	
	protected void paintFigure(Graphics g) {
		g.setXORMode(true);
		g.setForegroundColor(whiteColor);
		g.setBackgroundColor(ghostFillColor);

		Rectangle r = getBounds().getCopy();
		g.translate(r.getLocation());
//		int width = r.width;
//		int height = r.height;
		int start=0;
		
		g.fillRectangle(start+1,1,22,19);

		g.fillPolygon(fillPointlist);
	}
}
