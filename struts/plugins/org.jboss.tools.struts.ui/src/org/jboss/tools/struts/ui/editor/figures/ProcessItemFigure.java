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

import org.eclipse.swt.graphics.Image;

import org.eclipse.draw2d.*;
import org.eclipse.draw2d.geometry.*;

import org.eclipse.gef.handles.HandleBounds;

import org.jboss.tools.common.gef.GEFGraphicalViewer;
import org.jboss.tools.struts.ui.editor.edit.ProcessItemEditPart;
import org.jboss.tools.struts.ui.editor.figures.xpl.FixedConnectionAnchor;
import org.jboss.tools.struts.ui.editor.model.IProcessItem;
import org.jboss.tools.struts.ui.editor.print.PrintIconHelper;

public class ProcessItemFigure
	extends NodeFigure
	implements HandleBounds, FigureListener, MouseListener
{
protected Dimension SIZE = new Dimension(56, 100); 
protected Image icon = null;
//protected boolean jump=false;
private BreakPointFigure bp=null;

public IProcessItem processItem;

protected Label label=null;

protected int VERTICAL_ANCHOR_OFFSET = 32;




String path;

ProcessItemEditPart editPart;

public void setBounds(Rectangle rect) {
	super.setBounds(rect);
	resizeFigure();
}

public void setConstraint(IFigure child, Object constraint) {
	super.setConstraint(child, constraint);
}

public void setPath(String path){
	this.path = path;
	if(label != null){
		label.setText(path);
		label.setSize(label.getPreferredSize());
	}
}

public void refreshFont(){
	if(label != null){
		label.setFont(processItem.getStrutsModel().getOptions().getPathFont());
		label.setSize(label.getPreferredSize());
		label.setLocation(new Point(getLocation().x-5, getLocation().y-(12+processItem.getStrutsModel().getOptions().getPathFont().getFontData()[0].getHeight())));
	}
}

public void setIcon(Image i){
	icon = PrintIconHelper.getPrintImage(i);
}

public void addNotify(){
		if(processItem == null || processItem.isGlobal())return;
		label.setFont(processItem.getStrutsModel().getOptions().getPathFont());
		getParent().add(label);
		//add(label);
		label.setForegroundColor(ColorConstants.black);
		label.setOpaque(false);
		label.setText(path);
		label.setVisible(true);
		label.setSize(label.getPreferredSize());
	    label.setLocation(new Point(getLocation().x-5, getLocation().y-(12+processItem.getStrutsModel().getOptions().getPathFont().getFontData()[0].getHeight())));
	    label.addMouseListener(this);
		getParent().add(bp);
		bp.setLocation(new Point(getLocation().x-10, getLocation().y+4));
	    
}
public void removeNotify(){
	if(processItem == null || label == null)return;
	label.removeMouseListener(this);
	getParent().remove(label);
	getParent().remove(bp);
}

public void figureMoved(IFigure source){
	if(processItem != null && label != null){
		label.setLocation(new Point(getLocation().x-5, getLocation().y-20));
		bp.setLocation(new Point(getLocation().x-10, getLocation().y+4));
	}
}

public void init(int number){
	FixedConnectionAnchor c;
	if(number == 0) number = 1;
	for(int i=0;i<number;i++){
		c = new FixedConnectionAnchor(this);
		c.offsetV = VERTICAL_ANCHOR_OFFSET+LINK_HEIGHT*i;
		//c.offsetH = -1;
		c.leftToRight = false;
		connectionAnchors.put((i+1)+"_OUT", c);
		outputConnectionAnchors.addElement(c);
	}
}

public void addConnectionAnchor(int number){
	FixedConnectionAnchor c;
	if(number == 1) return;
	c = new FixedConnectionAnchor(this);
	c.offsetV = VERTICAL_ANCHOR_OFFSET+LINK_HEIGHT*(number-1);
	//c.offsetH = -1;
	c.leftToRight = false;
	connectionAnchors.put(number+"_OUT", c);
	outputConnectionAnchors.addElement(c);
}

public void removeConnectionAnchor(){
	if(outputConnectionAnchors.size() == 1)return;
	outputConnectionAnchors.remove(outputConnectionAnchors.size()-1);
}

public void removeAllConnectionAnchor(){
	outputConnectionAnchors.removeAllElements();
}
 
public ProcessItemFigure(IProcessItem processItem, ProcessItemEditPart part) {
	this.processItem = processItem;
	this.editPart = part;
	
	if(processItem != null){
	  setIcon(processItem.getImage());
	  setPath(processItem.getVisiblePath());
	  if(!processItem.isGlobal())label = new Label(path);
	}
	
	setOpaque(false);
	setLayoutManager(new XYLayout());
	
	resizeFigure();
	addFigureListener(this);
	if(processItem != null)bp = new BreakPointFigure(editPart,processItem.getBreakPoint(),true);
	//addMouseListener(this);
	
	
	if(processItem != null){
		FixedConnectionAnchor c;
		c = new FixedConnectionAnchor(this);
		c.offsetV = 8;
		c.offsetH = -1;
		connectionAnchors.put("1_IN", c);
		inputConnectionAnchors.addElement(c);
	
		//if(processItem.getForwardList().size() <= 1)init(processItem.getOutputLinks().length);
	}
}

/**
 * @see org.eclipse.gef.handles.HandleBounds#getHandleBounds()
 */
public Rectangle getHandleBounds() {
	return getBounds().getCropped(new Insets(0,0,0,0));
}

/**
 * @see org.eclipse.draw2d.Figure#getPreferredSize(int, int)
 */
//public Dimension getPreferredSize(int wHint, int hHint) {
//	return SIZE;
//}
protected int width,height;

protected void resizeFigure(){
}

/**
 * @see org.eclipse.draw2d.Figure#paintFigure(Graphics)
 */
 
	public void mouseDoubleClicked(MouseEvent me) {
	}

	public void mousePressed(MouseEvent me) {
		if(me.button == 3){
			((GEFGraphicalViewer)editPart.getViewer()).setNoDeselect();
			editPart.getViewer().select(editPart);
		}
	}

	public void mouseReleased(MouseEvent me) {
	}

}