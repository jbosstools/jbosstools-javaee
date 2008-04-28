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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;

import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.*;
import org.eclipse.gef.requests.DropRequest;
import org.eclipse.swt.accessibility.AccessibleControlEvent;
import org.eclipse.swt.accessibility.AccessibleEvent;

import org.jboss.tools.struts.ui.editor.figures.ProcessItemFigure;
import org.jboss.tools.struts.ui.editor.model.IForward;
import org.jboss.tools.struts.ui.editor.model.ILink;
import org.jboss.tools.struts.ui.editor.model.IProcessItem;
import org.jboss.tools.struts.ui.editor.model.IProcessItemListener;
import org.jboss.tools.struts.ui.editor.model.IStrutsElement;
import org.jboss.tools.struts.ui.editor.model.IStrutsElementList;
import org.jboss.tools.struts.ui.editor.model.IStrutsElementListListener;

public abstract class ProcessItemEditPart
	extends StrutsEditPart implements PropertyChangeListener, IProcessItemListener, IStrutsElementListListener, EditPartListener
{
	public boolean isElementListListenerEnable() {
		return true;
	}

	public void listElementAdd(IStrutsElementList list, IStrutsElement element,	int index) {
		layoutForwards();
		refresh();
		List editParts = getSourceConnections();
		for(int i=0;i<editParts.size();i++){
			((ConnectionEditPart)editParts.get(i)).refresh();
		}
		if(fig != null){
			fig.refreshFont();
			fig.repaint();
		}
		
	}

	public void listElementChange(IStrutsElementList list, IStrutsElement element, int index, PropertyChangeEvent event) {
		layoutForwards();
		refresh();
		List editParts = getSourceConnections();
		for(int i=0;i<editParts.size();i++){
			((ConnectionEditPart)editParts.get(i)).refresh();
		}
		if(fig != null){
			fig.refreshFont();
			fig.repaint();
		}
		
	}

	public void listElementMove(IStrutsElementList list, IStrutsElement element, int newIndex, int oldIndex) {
		layoutForwards();
		refresh();
		List editParts = getSourceConnections();
		for(int i=0;i<editParts.size();i++){
			((ConnectionEditPart)editParts.get(i)).refresh();
		}
	}
	
	public void linkAdd(ILink link){
		layoutForwards();
		refreshTargetLink(link);
		refresh();
		List editParts = getSourceConnections();
		for(int i=0;i<editParts.size();i++){
			((ConnectionEditPart)editParts.get(i)).refresh();
		}
		if(fig != null){
			fig.refreshFont();
			fig.repaint();
		}
		
	}
	
	public void linkRemove(ILink link){
		layoutForwards();
		refresh();
		List editParts = getSourceConnections();
		for(int i=0;i<editParts.size();i++){
			((ConnectionEditPart)editParts.get(i)).refresh();
		}
		if(fig != null){
			fig.refreshFont();
			fig.repaint();
		}
		
	}

	public void listElementRemove(IStrutsElementList list, IStrutsElement element, int index) {
		layoutForwards();
		refresh();
		List editParts = getSourceConnections();
		for(int i=0;i<editParts.size();i++){
			((ConnectionEditPart)editParts.get(i)).refresh();
		}
	}

	public void setElementListListenerEnable(boolean set) {
	}

	protected ProcessItemFigure fig=null;
	
	protected Dimension prefferedSize = new Dimension (150,21);
	
	protected Dimension calculatePreffSize(){
		return prefferedSize;
	}
	
	public void doDoubleClick(boolean cf){
	}

	public void doMouseDown(boolean cf){
	}

	public void doMouseUp(boolean cf){
	}
	
	public void setModel(Object model){
		super.setModel(model);
		((IProcessItem)model).addPropertyChangeListener(this);
		((IProcessItem)model).addProcessItemListener(this);
		addEditPartListener(this);
		if(getProcessItemModel().getForwardList() != null) getProcessItemModel().getForwardList().addStrutsElementListListener(this);
		
		layoutForwards();
	}
	
	public void childAdded(EditPart child, int index){ 
	}
	public void partActivated(EditPart editpart) {
	}
	public void partDeactivated(EditPart editpart){
	} 
	public  void removingChild(EditPart child, int index){ 
	}
	public void selectedStateChanged(EditPart editpart){
		if(this.getSelected() == EditPart.SELECTED_PRIMARY) {
			((StrutsDiagramEditPart)ProcessItemEditPart.this.getParent()).setToFront(this);
		}
	}
	
	public void propertyChange(PropertyChangeEvent evt){
	   if(evt.getPropertyName().equals("name")){
		 fig.setPath(getProcessItemModel().getVisiblePath());
		  //setToolTipText(processItem.getName());
	   } else if(evt.getPropertyName().equals("path")){
		 fig.setPath(getProcessItemModel().getVisiblePath());
		  //pathLabel.setText(processItem.getVisiblePath());
		  //pathLabel.redraw();
	   } else if(evt.getPropertyName().equals("selected")){
		  //select(((Boolean)evt.getNewValue()).booleanValue());
	   } else if(evt.getPropertyName().equals("shape")){
		 refreshVisuals();
	   }
	   //redraw();
	}
	
	public boolean isProcessItemListenerEnable(){
		return true;
	}
	
	public void processItemChange(){
		layoutForwards();
		refresh();
		if(fig != null){
			fig.refreshFont();
			fig.repaint();
		}
	}
	
	public void processItemForwardAdd(IProcessItem processItem, IForward forward){
		if(fig != null)fig.addConnectionAnchor(getProcessItemModel().getListOutputLinks().size());
		layoutForwards();
		refreshTargetLink(forward.getLink());
		refresh();
		
	}
	
	public void processItemForwardChange(IProcessItem processItem, IForward forward, PropertyChangeEvent evt){
		layoutForwards();
		refresh();
	}
	
	public void processItemForwardRemove(IProcessItem processItem, IForward forward){
		layoutForwards();
		refreshTargetLink(forward.getLink());
		refresh();
		if(fig != null)fig.removeConnectionAnchor();
	}

	/*public void linkAdd(IProcessItem processItem, ILink link){
		//refresh();
		layoutForwards();
		refreshTargetLink(link);
		refresh();
	}
	
	public void linkRemove(ILink link){
		layoutForwards();
		refresh();
		
		refreshTargetLink(link);
		refresh();
	}
	
	public void linkChange(ILink link, PropertyChangeEvent evet){
		refresh();
	}*/

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


protected List getModelTargetConnections() {
	return getProcessItemModel().getListInputLinks();
}

protected List getModelSourceConnections() {
	
	if(getProcessItemModel().isGlobal() || getProcessItemModel().isPage()){
		return getProcessItemModel().getListOutputLinks();
	}else{
		return Collections.EMPTY_LIST;
	} 
}

protected void createEditPolicies(){
	super.createEditPolicies();
	installEditPolicy(EditPolicy.NODE_ROLE, null);
	installEditPolicy(EditPolicy.SELECTION_FEEDBACK_ROLE, null);	
	installEditPolicy(EditPolicy.COMPONENT_ROLE, new ProcessItemEditPolicy());
	installEditPolicy(EditPolicy.LAYOUT_ROLE, new StrutsFlowEditPolicy());
	installEditPolicy(EditPolicy.CONNECTION_ENDPOINTS_ROLE, new ProcessItemEditPolicy());
}

/*public DragTracker getDragTracker(Request req) {
	// The drawing cannot be dragged.
	return new CustomDragTracker(this);
}*/
/**
 * Returns a newly created Figure to represent this.
 *
 * @return  Figure of this.
 */

//protected ChopboxAnchor ca;

abstract protected IFigure createFigure();

public ProcessItemFigure getProcessItemFigure() {
	//PageFigure fig 
	return (ProcessItemFigure)getFigure();
}

/**
 * Returns the model of this as a LED.
 *
 * @return  Model of this as an LED.
 */
public IProcessItem getProcessItemModel() {
	return (IProcessItem)getModel();
}



protected void layoutForwards(){
}

protected void refreshVisuals() {
	if(getParent() == null)return;
	Point loc = getProcessItemModel().getPosition();
	loc.x -= loc.x%8;
	loc.y -= loc.y%8;
	
	Dimension size= getProcessItemModel().getSize();
	if(size.width == 0)size = calculatePreffSize();
	else size.height = calculatePreffSize().height;
	
	Rectangle r = new Rectangle(loc ,size);

	((GraphicalEditPart) getParent()).setLayoutConstraint(
		this,
		getFigure(),
		r);
}


public ConnectionAnchor getTargetConnectionAnchor(ConnectionEditPart connEditPart) {
	//ILink wire = (ILink) connEditPart.getModel();
	ConnectionAnchor anc = getNodeFigure().getConnectionAnchor("1_IN");
	return anc;
}

public ConnectionAnchor getTargetConnectionAnchor(Request request) {
	Point pt = new Point(((DropRequest)request).getLocation());
	return getNodeFigure().getTargetConnectionAnchorAt(pt);
}

public ConnectionAnchor getSourceConnectionAnchor(ConnectionEditPart connEditPart) {
	if(getProcessItemModel().isGlobal() || getProcessItemModel().isPage()){
		ILink link = (ILink) connEditPart.getModel();
		int index = getProcessItemModel().getListOutputLinks().indexOf(link);
		return getNodeFigure().getConnectionAnchor((index+1)+"_OUT");
	}else return super.getSourceConnectionAnchor(connEditPart);
}

public ConnectionAnchor getSourceConnectionAnchor(Request request) {
	if(getProcessItemModel().isGlobal() || getProcessItemModel().isPage() || getProcessItemModel().isAction()){
		Point pt = new Point(((DropRequest)request).getLocation());
		return getNodeFigure().getSourceConnectionAnchorAt(pt);
	}else return super.getSourceConnectionAnchor(request);
}

protected List getModelChildren() {
	if(getProcessItemModel().isGlobal() || getProcessItemModel().isPage()){
	  return Collections.EMPTY_LIST;
	}else{ 
		return getProcessItemModel().getForwardList().getElements();
	}
}

	protected void refreshChildren() {
		super.refreshChildren();
		for(int i=0;i < getChildren().size();i++){
			((ForwardEditPart)getChildren().get(i)).refresh();
		
		}
	}

}
