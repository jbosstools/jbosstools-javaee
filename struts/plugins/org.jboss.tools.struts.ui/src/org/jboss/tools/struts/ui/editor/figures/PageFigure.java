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
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;

import org.jboss.tools.struts.ui.editor.edit.ProcessItemEditPart;
import org.jboss.tools.struts.ui.editor.model.IProcessItem;

public class PageFigure extends ProcessItemFigure {
	protected PointList fillPointlist, fill2Pointlist, shadowPointlist, shadow2Pointlist;	
	public PageFigure(IProcessItem processItem, ProcessItemEditPart part) {
		super(processItem, part);
		setBorder(new PageBorder(blackColor));
		VERTICAL_ANCHOR_OFFSET = 24;
		if(processItem != null)init(processItem.getOutputLinks().length);
	}
	
	protected void resizeFigure(){
		if(width == getSize().width && height == getSize().height) return;
		
		int start=0;
	 	width = getSize().width-1;
	 	height = getSize().height-1;
	 	
		fillPointlist = new PointList();
		
		fillPointlist.addPoint(start,20);
		fillPointlist.addPoint(start+23, 20);
		fillPointlist.addPoint(start+23, 0);
		fillPointlist.addPoint(width-15, 0);
		fillPointlist.addPoint(width-1, 14);
		fillPointlist.addPoint(width-1, height-1);
		fillPointlist.addPoint(start, height-1);
		
		shadowPointlist = new PointList();
		
		shadowPointlist.addPoint(width-15, 0);
		shadowPointlist.addPoint(width-14, 4);
		shadowPointlist.addPoint(width-15, 7);
		shadowPointlist.addPoint(width-18, 10);
		shadowPointlist.addPoint(width-1, 14);
		
		shadowPointlist.addPoint(width-9, 14);
		shadowPointlist.addPoint(width-16, 13);
		
		shadowPointlist.addPoint(width-21, 11);
		shadowPointlist.addPoint(width-18, 8);
		shadowPointlist.addPoint(width-16, 4);

		shadow2Pointlist = new PointList();
		
		shadow2Pointlist.addPoint(width-15, 0);
		shadow2Pointlist.addPoint(width-1, 14);
		shadow2Pointlist.addPoint(width-3, 14);
		shadow2Pointlist.addPoint(width-15, 2);
	}
	
	protected void paintFigure(Graphics g) {
		Rectangle r = getBounds().getCopy();
		g.translate(r.getLocation());
		int width = r.width-1;
		int height = r.height-1;
		
		g.setBackgroundColor(whiteColor);
		
		g.fillRectangle(1,1,22,19);
		
		if(icon != null)g.drawImage(icon, 4, 2);
		if(processItem.hasErrors())g.drawImage(errorIcon, 4, 10);
		
		if(processItem != null && processItem.isConfirmed()){
			g.setBackgroundColor(yellowColor);
		}else{ 
			g.setBackgroundColor(lightGrayColor);
		}

		g.fillPolygon(fillPointlist);
		if(processItem != null && processItem.isConfirmed()){
		  g.setBackgroundColor(orangeColor);
		}else{ 
		  g.setBackgroundColor(lightGrayColor);
		}
		
		g.fillPolygon(shadowPointlist);
		g.fillPolygon(shadow2Pointlist);
		   if(processItem.hasPageHiddenLinks()){
			  g.setForegroundColor(mediumGrayColor);
			  g.drawLine(width-20, height-12, width-13, height-19);
			  g.drawLine(width-13, height-19, width-6, height-12);
			  g.drawLine(width-6, height-12, width-13, height-5);
			  g.drawLine(width-13, height-5, width-20, height-12);
			  g.drawLine(width-19, height-12, width-13, height-18);
			  g.drawLine(width-13, height-18, width-7, height-12);
			  g.drawLine(width-7, height-12, width-13, height-6);
			  g.drawLine(width-13, height-6, width-19, height-12);
			  g.setBackgroundColor(whiteColor);
			  g.setForegroundColor(whiteColor);
			  int[] points = new int[]{
				width-18, height-12,
				width-13, height-17,
				width-8, height-12,
				width-13, height-7
			  };
			  g.fillPolygon(points);
			  g.drawLine(width-13, height-17, width-8, height-12);
			  g.drawLine(width-8, height-12, width-13, height-7);
			  g.setForegroundColor(blackColor);
			  g.drawLine(width-15, height-14, width-15, height-10);
			  g.drawLine(width-11, height-14, width-11, height-10);
			  g.drawLine(width-14, height-12, width-12, height-12);
		   }
		
	}
	
	class PageBorder extends LineBorder{
		public PageBorder(Color color) {
			super(color);
		}
		public void paint(IFigure figure, Graphics graphics, Insets insets) {
			Rectangle r = getPaintRectangle(figure, insets).getCopy();
			graphics.translate(r.getLocation());
			int width = r.width-1;
			int height = r.height-1;
			if(processItem != null && processItem.isConfirmed())
				graphics.setForegroundColor(blackColor);
			else 
				graphics.setForegroundColor(darkGrayColor);
				
			
			graphics.drawLine(1 , 0, width-15,0);
			graphics.drawLine(0, 1, 0, height-2);
			graphics.drawLine(1, height-1, width-2, height-1);
			graphics.drawLine(width-1, 14, width-1, height-2);
			graphics.drawLine(width-15, 0, width-1, 14);
			
			graphics.drawLine(0 , 1, 1,0);
			graphics.drawLine(0 , height-2, 1,height-1);
			graphics.drawLine(width-2 , height-1, width-1,height-2);
			
			graphics.drawLine(width-15, 0, width-14, 4);
			graphics.drawLine(width-14, 4, width-15, 7);
			graphics.drawLine(width-15, 7, width-18, 10);

			graphics.drawLine(width-18, 10, width-1, 14);
			
			graphics.drawLine(23, 0, 23, 19);
			graphics.drawLine(0, 20, 22, 20);
			graphics.drawLine(22, 20, 23, 19);
		}
	}
	
}
