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
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;

import org.jboss.tools.struts.ui.editor.edit.ProcessItemEditPart;
import org.jboss.tools.struts.ui.editor.model.IProcessItem;

public class GlobalForwardFigure extends ProcessItemFigure {
	protected Color bgColor = new Color(null, 0xdb, 0xd0, 0xfb);
	protected Color fgColor = blackColor;
	public Font font;
	
	public GlobalForwardFigure(IProcessItem processItem, ProcessItemEditPart part) {
		super(processItem, part);
		setBorder(new GlobalForwardBorder(blackColor));
		SIZE = new Dimension(21, 100);
		VERTICAL_ANCHOR_OFFSET = 8;
		if(processItem != null)font = processItem.getStrutsModel().getOptions().getActionFont();
		init(1);
	}
	
	protected void paintFigure(Graphics g) {
		Rectangle r = getBounds().getCopy();
		g.translate(r.getLocation());
		int width = r.width-1;
//		int height = r.height-1;
		int start=0;
		
		g.setBackgroundColor(whiteColor);
		
		g.fillRectangle(start+1,1,22,19);
		
		if(icon != null)g.drawImage(icon, start+4, 2);
		if(processItem.hasErrors())g.drawImage(errorIcon, 4, 10);
		
		g.setBackgroundColor(bgColor);
		g.fillRectangle(24,1,width-25,19);
		
		g.setForegroundColor(fgColor);
		g.setFont(font);
		g.drawString(dottedString(processItem.getName(), width-37, font), 31, 3);
	}
	
	class GlobalForwardBorder extends LineBorder{
		public GlobalForwardBorder(Color color) {
			super(color);
		}
		public void paint(IFigure figure, Graphics graphics, Insets insets) {
			Rectangle r = getPaintRectangle(figure, insets).getCopy();
			graphics.translate(r.getLocation());
			int width = r.width-1;
//			int height = r.height-1;
			
			if(processItem != null && processItem.isConfirmed())
				graphics.setForegroundColor(blackColor);
			else 
				graphics.setForegroundColor(darkGrayColor);
			
			graphics.drawLine(1 , 0, width-2,0);
			graphics.drawLine(width-1,1,width-1, 19);
			graphics.drawLine(23, 0, 23, 20);
			graphics.drawLine(0, 1, 0, 19);
			graphics.drawLine(1, 20, width-2, 20);
		}
	}
	
}
