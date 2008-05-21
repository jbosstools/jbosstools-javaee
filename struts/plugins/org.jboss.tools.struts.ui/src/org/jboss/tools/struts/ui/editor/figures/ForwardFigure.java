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

import org.eclipse.draw2d.*;
import org.eclipse.draw2d.geometry.*;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.handles.HandleBounds;
import org.eclipse.swt.graphics.Color;

import org.jboss.tools.struts.ui.editor.edit.ForwardEditPart;
import org.jboss.tools.struts.ui.editor.figures.xpl.FixedConnectionAnchor;
import org.jboss.tools.struts.ui.editor.model.IForward;

public class ForwardFigure
	extends NodeFigure
	implements HandleBounds
{
private static final Dimension SIZE = new Dimension(56, 16); 
private Color exColor = new Color(null, 0xca, 0x00, 0x02);
private Color selColor = new Color(null, 0x00, 0x00, 0x00);

private ForwardEditPart pagePart;
private IForward page;
//private IProcessItem group;

public void setConstraint(IFigure child, Object constraint) {
	super.setConstraint(child, constraint);
}

public void init(){
	FixedConnectionAnchor c;
		c = new FixedConnectionAnchor(this);
		c.offsetV = LINK_HEIGHT-13;
		//c.offsetH = -2;
		c.leftToRight = false;
		connectionAnchors.put("1_OUT", c);
		outputConnectionAnchors.addElement(c);
}

public void removeConnectionAnchor(){
	if(outputConnectionAnchors.size() == 1)return;
	outputConnectionAnchors.remove(outputConnectionAnchors.size()-1);
}

public void removeAllConnectionAnchor(){
	outputConnectionAnchors.removeAllElements();
}

public ForwardFigure(ForwardEditPart pagePart) {
	setBorder(new ForwardBorder(blackColor));
	this.pagePart = pagePart;
	page = pagePart.getForwardModel();
//	group = (IProcessItem)page.getParentStrutsElement();
	
	setOpaque(false);
	init();
}

public Rectangle getHandleBounds() {
	return getBounds().getCropped(new Insets(2,0,2,0));
}

public Dimension getPreferredSize(int wHint, int hHint) {
	return SIZE;
}

protected void paintFigure(Graphics g) {
	Rectangle r = getBounds().getCopy();
	g.translate(r.getLocation());
	int width = r.width;
	int height = r.height;

	
    if(page.getProcessItem().isConfirmed()){
        if(page.isException()) g.setForegroundColor(exColor);
        else  g.setForegroundColor(blackColor);
        g.setBackgroundColor(whiteColor);
     }else{
        if(page.isException()) g.setForegroundColor(exColor);
        else g.setForegroundColor(darkGrayColor);
        g.setBackgroundColor(lightGrayColor);
     }
	
	
	g.fillRectangle(0,0,width-1,height-1);
	
	g.setFont(page.getStrutsModel().getOptions().getForwardFont());
	
	g.drawString(dottedString(page.getName(), width-30, page.getStrutsModel().getOptions().getForwardFont()), 4, 1);
	if(pagePart.getSelected() == EditPart.SELECTED_PRIMARY || pagePart.getSelected() == EditPart.SELECTED){
		g.setForegroundColor(selColor);
		g.drawRectangle(0,0,width-3,height-3);
	}
	
}
class ForwardBorder extends LineBorder{
	public ForwardBorder(Color color) {
		super(color);
	}
	
	public void paint(IFigure figure, Graphics graphics, Insets insets) {
		Rectangle r = getPaintRectangle(figure, insets).getCopy();
		graphics.translate(r.getLocation());
		int width = r.width-1;
		int height = r.height-1;
		
		graphics.setForegroundColor(blackColor);
		
		//graphics.drawLine(0 , 0, width-1,0);
		//graphics.drawLine(width-1,0,width-1, height-1);
		//graphics.drawLine(0, 0, 0, height-1);
		graphics.drawLine(0, height-1, width-1, height-1);
	}
}

}