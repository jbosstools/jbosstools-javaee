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
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;

import org.jboss.tools.struts.ui.editor.edit.ProcessItemEditPart;
import org.jboss.tools.struts.ui.editor.model.IProcessItem;

public class ActionFigure extends ProcessItemFigure {
	public Font font;
	
	public ActionFigure(IProcessItem processItem, ProcessItemEditPart part) {
		super(processItem, part);
		setBorder(new ActionBorder(blackColor));
		setBackgroundColor(new Color(null, 0xe2, 0xef, 0xdb));
		setForegroundColor(blackColor);
		if(processItem != null)font = processItem.getStrutsModel().getOptions().getActionFont();
		VERTICAL_ANCHOR_OFFSET = 8;
		
		init(0);
	}
	
	protected void paintFigure(Graphics g) {
		Rectangle r = getBounds().getCopy();
		g.translate(r.getLocation());
		int width = r.width-1;
		int height = r.height-1;
		
        if(processItem.isConfirmed()) g.setBackgroundColor(whiteColor);
        else if(processItem.isAnotherModule()) g.setBackgroundColor(whiteColor);
        else g.setBackgroundColor(lightGrayColor);
		
		g.fillRectangle(1,1,22,19);
		
		if(icon != null)g.drawImage(icon, 4, 2);
		if(processItem.hasErrors())g.drawImage(errorIcon, 4, 10);
		
        if(processItem.isConfirmed()) g.setBackgroundColor(getLocalBackgroundColor());
        else if(processItem.isAnotherModule()) g.setBackgroundColor(anotherModuleColor);
        else g.setBackgroundColor(lightGrayColor);
		
		g.fillRectangle(24,1,width-25,19);
		g.fillRectangle(1,21,width-2,height-21);
		
        if(processItem.isConfirmed()) g.setForegroundColor(blackColor);
        else g.setForegroundColor(darkGrayColor);
		
		
		g.setFont(font);
		g.drawString(dottedString(processItem.getName(), width-37, font), 31, 3);
	}
	
	class ActionBorder extends LineBorder{
		public ActionBorder(Color color) {
			super(color);
		}
		public void paint(IFigure figure, Graphics graphics, Insets insets) {
			Rectangle r = getPaintRectangle(figure, insets).getCopy();
			graphics.translate(r.getLocation());
			int width = r.width-1;
			int height = r.height-1;
			if(processItem != null && processItem.isSwitchAction()){
				graphics.setForegroundColor(blackColor);
				
				graphics.drawLine(23, 0, 23, height-1);
				graphics.drawLine(0, 1, 0, height-2);
				graphics.drawLine(1 , 0, 23,0);
				graphics.drawLine(1, 20, 23, 20);
				graphics.drawLine(1, height-1, 23, height-1);
		           
		        graphics.setLineStyle(Graphics.LINE_DOT);
		        
		        graphics.drawLine(width-1,1,width-1, height-2);
		        graphics.drawLine(24 , 0, width-2,0);
		        graphics.drawLine(24, 20, width-2, 20);
				graphics.drawLine(24, height-1, width-2, height-1);
			}else{
				if(processItem != null && processItem.isConfirmed())
					graphics.setForegroundColor(blackColor);
				else{
					if(processItem != null && processItem.isAnotherModule())graphics.setLineStyle(Graphics.LINE_DOT);
					graphics.setForegroundColor(darkGrayColor);
				}
			
				graphics.drawLine(1 , 0, width-2,0);
				graphics.drawLine(width-1,1,width-1, height-2);
				graphics.drawLine(23, 0, 23, height-1);
				graphics.drawLine(0, 1, 0, height-2);
				graphics.drawLine(1, 20, width-2, 20);
				graphics.drawLine(1, height-1, width-2, height-1);
			}
		}
	}
	
}
