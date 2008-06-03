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
package org.jboss.tools.struts.ui.editor.edit;

import java.util.Collections;
import java.util.List;

import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.*;
import org.eclipse.gef.requests.DropRequest;
import org.eclipse.swt.accessibility.AccessibleControlEvent;
import org.eclipse.swt.accessibility.AccessibleEvent;

import org.jboss.tools.struts.ui.editor.figures.ForwardFigure;
import org.jboss.tools.struts.ui.editor.model.IForward;
import org.jboss.tools.struts.ui.editor.model.IForwardListener;

public class ForwardEditPart
	extends StrutsEditPart implements EditPartListener, IForwardListener
{
	private ForwardFigure fig=null;
//	private int oldSelected = EditPart.SELECTED_NONE;

	public void doDoubleClick(boolean cf){
	}

	public void doMouseDown(boolean cf){
	}

	public void doMouseUp(boolean cf){
	}
	
	public void setModel(Object model){
		super.setModel(model);
		addEditPartListener(this);
		getForwardModel().addForwardListener(this);
		
	}
 
	public void childAdded(EditPart child, int index){ 
	}
	public void partActivated(EditPart editpart) {
		refreshTargetLink(getForwardModel().getLink());
	}
	public void partDeactivated(EditPart editpart){
	} 
	public  void removingChild(EditPart child, int index){ 
	}
	public void selectedStateChanged(EditPart editpart){
		fig.repaint();
	}
	
public boolean isPageListenerEnable(){
	return true;
}

public void forwardRemoved(IForward forward){
	//refreshTargetLink(getForwardModel().getLink());
	
}

public void forwardChanged(IForward forward){
	if(getForwardModel().getLink()!= null)getForwardModel().getLink().setTarget();
	refreshTargetLink(getForwardModel().getLink());
	refresh();
}

protected AccessibleEditPart createAccessible() {
	return new AccessibleGraphicalEditPart(){

		public void getName(AccessibleEvent e) {
			e.result = "EditPart";
		}
		
		public void getValue(AccessibleControlEvent e) {
			//e.result = Integer.toString(getPageModel().getValue());
		}

	};
}

protected void createEditPolicies(){
	super.createEditPolicies();
	installEditPolicy(EditPolicy.COMPONENT_ROLE, new ForwardEditPolicy());
	installEditPolicy(EditPolicy.CONNECTION_ENDPOINTS_ROLE, new ForwardEditPolicy());
}

protected IFigure createFigure() {
	fig = new ForwardFigure(this);
	
	return fig;
}

public ForwardFigure getPageFigure() {
	return (ForwardFigure)getFigure();
}

public IForward getForwardModel() {
	return (IForward)getModel();
}


/**
 * Apart from the usual visual update, it also
 * updates the numeric contents of the LED.
 */
protected List getModelSourceConnections() {
	return Collections.singletonList(getForwardModel().getLink());
}

protected List getModelTargetConnections() {
	return Collections.EMPTY_LIST;
}

public ConnectionAnchor getSourceConnectionAnchor(ConnectionEditPart connEditPart) {
	
	ConnectionAnchor anc = getNodeFigure().getConnectionAnchor("1_OUT");
	return anc;
}

public ConnectionAnchor getSourceConnectionAnchor(Request request) {
	Point pt = new Point(((DropRequest)request).getLocation());
	return getNodeFigure().getSourceConnectionAnchorAt(pt);
}

public void setSelected(int i){
	super.setSelected(i);
	refreshVisuals();
}

protected void refreshVisuals() {
	//getFigure().setBounds(getPageModel().getBounds());
	if(getParent() == null) return;
	
	((GraphicalEditPart) getParent()).setLayoutConstraint(
		this,
		getFigure(),
	    getForwardModel().getBounds());
}
}
