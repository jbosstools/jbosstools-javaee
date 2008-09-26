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
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.handles.HandleBounds;
import org.eclipse.swt.graphics.Color;
import org.jboss.tools.seam.ui.pages.editor.edit.PageWrapper;
import org.jboss.tools.seam.ui.pages.editor.edit.ParamListEditPart;

public class ParamListFigure extends NodeFigure implements HandleBounds {
	private static final Dimension SIZE = new Dimension(56, 100);
	
	private static final Color darkGrayColor = new Color(null, 0xb3, 0xb3, 0xb3);

	public PageWrapper paramList;

	ParamListEditPart editPart;

	public void setEditPart(ParamListEditPart part) {
		editPart = part;
	}

	public void setConstraint(IFigure child, Object constraint) {
		super.setConstraint(child, constraint);
	}

	public ParamListFigure(PageWrapper paramList) {
		this.paramList = paramList;

		setOpaque(false);
		setLayoutManager(new ParamListLayout());

		setBorder(new GroupBorder(ColorConstants.black));
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

	int width, height;

	/**
	 * @see org.eclipse.draw2d.Figure#paintFigure(Graphics)
	 */
	protected void paintFigure(Graphics g) {
		//g.setXORMode(true);
		g.setBackgroundColor(lightGrayColor);
		
		Rectangle r = getBounds().getCopy();
		g.translate(r.getLocation());
		
		g.fillRectangle(1, 1, r.width-2, r.height-2);
	}

	class GroupBorder extends LineBorder {
		public GroupBorder(Color color) {
			super(color);
		}

		public void paint(IFigure figure, Graphics graphics, Insets insets) {
			Rectangle r = getPaintRectangle(figure, insets).getCopy();
			graphics.translate(r.getLocation());
			int width = r.width - 1;
			int height = r.height - 1;
			
			graphics.setForegroundColor(darkGrayColor);

			graphics.drawLine(1, 0, width-1, 0);
			graphics.drawLine(0, 1, 0, height - 1);
			graphics.drawLine(1, height, width-1, height);
			graphics.drawLine(width, 1, width, height - 1);
		}

	}
	
	private int nameWidth = 0;
	
	public void setNameWidth(int nameWidth){
		this.nameWidth = nameWidth;
	}
	
	public int getNameWidth(){
		return nameWidth;
	}
}