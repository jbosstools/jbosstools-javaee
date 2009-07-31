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
package org.jboss.tools.jsf.ui.editor.figures;

import org.eclipse.draw2d.*;
import org.eclipse.draw2d.geometry.*;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.handles.HandleBounds;

import org.jboss.tools.jsf.ui.editor.edit.PageEditPart;
import org.jboss.tools.jsf.ui.editor.figures.xpl.FixedConnectionAnchor;
import org.jboss.tools.jsf.ui.editor.model.IGroup;
import org.jboss.tools.jsf.ui.editor.model.IPage;

public class PageFigure
	extends NodeFigure
	implements HandleBounds
{
private static final Dimension SIZE = new Dimension(56, 100); 


private PageEditPart pagePart;
private IPage page;
private IGroup group;

public void setConstraint(IFigure child, Object constraint) {
	super.setConstraint(child, constraint);
}

public void init(int number){
	FixedConnectionAnchor c;
	if(number == 0) number = 1;
	for(int i=0;i<number;i++){
		c = new FixedConnectionAnchor(this);
		c.offsetV = 8+LINK_HEIGHT*i;
		c.offsetH = -2;
		c.leftToRight = false;
		connectionAnchors.put((i+1)+"_OUT", c); //$NON-NLS-1$
		outputConnectionAnchors.addElement(c);
	}
}

public void addConnectionAnchor(int number){
	if(number == 1) return;
	FixedConnectionAnchor c;
	c = new FixedConnectionAnchor(this);
	c.offsetV = 8+LINK_HEIGHT*(number-1);
	c.offsetH = -2;
	c.leftToRight = false;
	connectionAnchors.put(number+"_OUT", c); //$NON-NLS-1$
	outputConnectionAnchors.addElement(c);
}

public void removeConnectionAnchor(){
	if(outputConnectionAnchors.size() == 1)return;
	outputConnectionAnchors.remove(outputConnectionAnchors.size()-1);
}

public void removeAllConnectionAnchor(){
	outputConnectionAnchors.removeAllElements();
}

public PageFigure(PageEditPart pagePart) {
	this.pagePart = pagePart;
	page = pagePart.getPageModel();
	group = (IGroup)page.getParentJSFElement();
	
	setOpaque(false);
	init(page.getLinkList().size());
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
	
	if(pagePart.getSelected() == EditPart.SELECTED_PRIMARY || pagePart.getSelected() == EditPart.SELECTED){
		if(page.hasErrors())
			g.setBackgroundColor(errorSelected);
		else if(group.isPattern())
			g.setBackgroundColor(pattSelected);
		else if(group.isConfirmed())
			g.setBackgroundColor(brownColor);
		else
			g.setBackgroundColor(lightGrayColor);
	}else{
		if(page.hasErrors())
			g.setBackgroundColor(errorColor);
		else if(group.isPattern())
			g.setBackgroundColor(lightBlueColor);
		else if(group.isConfirmed())
			g.setBackgroundColor(yellowColor);
		else
			g.setBackgroundColor(lightGrayColor);
		
	}
	
	g.fillRectangle(0,0,width,height);
	
	if(pagePart.getSelected() == EditPart.SELECTED_PRIMARY || pagePart.getSelected() == EditPart.SELECTED){
		if(page.hasErrors())
			g.setForegroundColor(errorBorder);
		else if(group.isPattern())
			g.setForegroundColor(pattBorder);
		else if(group.isConfirmed())
			g.setForegroundColor(borderColor);
		else
			g.setForegroundColor(darkGrayColor);
	
		g.drawRectangle(0,0,width-1, height-1);
		
		if(group.getPageList().indexOf(page) == 0) g.drawLine(0,1,width-1, 1);
	}
}
}