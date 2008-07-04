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
package org.jboss.tools.seam.ui.pages.editor.figures;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.ScaledGraphics;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.handles.HandleBounds;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Pattern;
import org.eclipse.swt.widgets.Display;
import org.jboss.tools.seam.ui.pages.editor.ecore.pages.Page;
import org.jboss.tools.seam.ui.pages.editor.edit.PageEditPart;
import org.jboss.tools.seam.ui.pages.editor.figures.xpl.FixedConnectionAnchor;
import org.jboss.tools.seam.ui.pages.editor.print.PrintIconHelper;

public class PageFigure extends NodeFigure implements HandleBounds{
	private static final Dimension SIZE = new Dimension(56, 100);

	private Image icon = null;

	public Page page;


	String path;

	PageEditPart editPart;

	public void setGroupEditPart(PageEditPart part) {
		editPart = part;
	}

	public void setConstraint(IFigure child, Object constraint) {
		super.setConstraint(child, constraint);
	}

	public void setIcon(Image i) {
		icon = PrintIconHelper.getPrintImage(i);
	}

		
	public PageFigure(Page group) {
		this.page = group;

		if (group != null) {
			setIcon(group.getImage());
			initConnectionAnchors(group.getOutputLinks().size());
		}

		setOpaque(false);
		setLayoutManager(new XYLayout());

		setBorder(new GroupBorder(blackColor));

		if (group != null) {
			FixedConnectionAnchor c;
			c = new FixedConnectionAnchor(this);
			c.offsetV = 8;
			c.offsetH = -1;
			connectionAnchors.put("1_IN", c);
			inputConnectionAnchors.addElement(c);

		}
	}
	public ConnectionAnchor getConnectionAnchor(String terminal) {
		ConnectionAnchor anchor = (ConnectionAnchor)connectionAnchors.get(terminal);
		while(anchor == null){
			addConnectionAnchor(outputConnectionAnchors.size());
			anchor = (ConnectionAnchor)connectionAnchors.get(terminal);
		}
		return anchor;
	}
	/**
	 * @see org.eclipse.gef.handles.HandleBounds#getHandleBounds()
	 */
	public Rectangle getHandleBounds() {
		return getBounds().getCropped(new Insets(0, 0, 0, 0));
	}

	/**
	 * @see org.eclipse.draw2d.Figure#getPreferredSize(int, int)
	 */
	public Dimension getPreferredSize(int wHint, int hHint) {
		return SIZE;
	}

	//int width, height;


	/**
	 * @see org.eclipse.draw2d.Figure#paintFigure(Graphics)
	 */
	protected void paintFigure(Graphics g) {
		Rectangle r = getBounds().getCopy();
		g.translate(r.getLocation());

		int height = r.height - 1;
		int start = 0;
		

		g.setBackgroundColor(whiteColor);

		g.fillRectangle(start + 1, 1, 22, 190); // fill left part

		// drawIcon
		if (icon != null)
			g.drawImage(icon, start + getInsetX(), getInsetY());

		
		//color the page 
		if (page != null /*&& group.isConfirmed()*/) {
			g.setBackgroundColor(new Color(null, 0xff, 0xff, 0xc2));			
		} else {
			g.setBackgroundColor(lightGrayColor);
		}
		
		Rectangle boundingRect = new Rectangle(22, 1, r.width, r.height);
		
		g.fillRectangle(boundingRect);
		
//		if(g instanceof ScaledGraphics) {
//			// scaled graphcis does not support gradients ;(			
//			g.fillRectangle(boundingRect);
//		} else {
//			Display display = Display.getCurrent();
//			
//
//			Point topLeft = boundingRect.getTopLeft();
//			Point bottomRight = boundingRect.getBottomRight();
//
//			Pattern pattern = new Pattern(display, topLeft.x, topLeft.y,
//					bottomRight.x, bottomRight.y,
//					ColorConstants.white, g.getBackgroundColor());
//			g.setBackgroundPattern(pattern);
//			g.fillRectangle(boundingRect);
//			g.setBackgroundPattern(null);		
//			pattern.dispose();
//		}
		
		if(page != null){
			g.setFont(nodeLabelFont);
			g.drawString(page.getName(), 27, 3);			
		}
		
		if(page.getChildren().size() != 0){
			if(page.isParamsVisible()){
				g.setForegroundColor(blackColor);
				g.drawLine(4, height-13, 11, height-13);
				g.drawLine(4, height-13, 4, height-6);
				
				g.drawLine(6, height-9, 10, height-9);
	 			
				g.setForegroundColor(button2Color);
				g.drawLine(12, height-13, 12, height-5);
				g.drawLine(4, height-5, 12, height-5);
				
				g.setForegroundColor(button3Color);
				g.setBackgroundColor(button3Color);
				g.drawLine(5, height-4, 13, height-4);
				g.drawLine(13, height-4, 13, height-12);
				g.fillRectangle(6, height-11, 5, 2);
				g.fillRectangle(6, height-8, 5, 2);
				
				g.setForegroundColor(button4Color);
				g.drawLine(5, height-7, 5, height-12);
				g.drawLine(5, height-12, 10, height-12);
			}else{
				
				g.setForegroundColor(button2Color);
				g.drawLine(4, height-13, 11, height-13);
				g.drawLine(4, height-13, 4, height-6);
				
				g.setForegroundColor(blackColor);
				
				g.drawLine(6, height-9, 10, height-9);
				g.drawLine(8, height-11, 8, height-7);
				
				g.drawLine(12, height-13, 12, height-5);
				g.drawLine(4, height-5, 12, height-5);
				
				g.setForegroundColor(button3Color);
				g.drawLine(5, height-4, 13, height-4);
				g.drawLine(13, height-4, 13, height-12);
				
				g.drawLine(6, height-6, 11, height-6);
				g.drawLine(11, height-6, 11, height-12);
				
				g.setForegroundColor(whiteColor);
				g.drawLine(5, height-6, 5, height-12);
				g.drawLine(5, height-12, 11, height-12);
			}
		}
	}

	/** distance from e.g. icon to border Y-axis*/
	private int getInsetY() {
		return 2;
	}

	/** distance from e.g. icon to border X-axis*/
	private int getInsetX() {
		return 4;
	}

	/** the one drawing the "bend corner rectangle" **/
	class GroupBorder extends LineBorder {
		public GroupBorder(Color color) {
			super(color);
		}

		public void paint(IFigure figure, Graphics graphics, Insets insets) {
			Rectangle r = getPaintRectangle(figure, insets).getCopy();
			graphics.translate(r.getLocation());
			int width = r.width - 1;
			int height = r.height - 1;
			
			if (page != null /*&& group.isConfirmed()*/)
				graphics.setForegroundColor(blackColor);
			else
				graphics.setForegroundColor(darkGrayColor);

			graphics.drawLine(1, 0, width-1, 0);
			graphics.drawLine(0, 1, 0, height - 1);
			graphics.drawLine(1, height, width-1, height);
			graphics.drawLine(width, 1, width, height - 1);
			graphics.drawLine(23 , 0, 23, height); 
	}

	


}
}