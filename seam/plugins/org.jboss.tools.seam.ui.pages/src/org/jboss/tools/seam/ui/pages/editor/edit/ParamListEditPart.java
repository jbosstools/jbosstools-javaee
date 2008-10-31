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
package org.jboss.tools.seam.ui.pages.editor.edit;

import java.beans.PropertyChangeListener;
import java.util.List;

import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.FigureUtilities;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.gef.AccessibleEditPart;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.requests.DropRequest;
import org.eclipse.swt.accessibility.AccessibleControlEvent;
import org.eclipse.swt.accessibility.AccessibleEvent;
import org.jboss.tools.seam.ui.pages.editor.ecore.pages.Param;
import org.jboss.tools.seam.ui.pages.editor.figures.NodeFigure;
import org.jboss.tools.seam.ui.pages.editor.figures.ParamFigure;
import org.jboss.tools.seam.ui.pages.editor.figures.ParamListFigure;

public class ParamListEditPart extends PagesEditPart implements PropertyChangeListener, Adapter {
	private ParamListFigure fig = null;

	private boolean single = true;

	public boolean isSingle() {
		return single;
	}


	protected AccessibleEditPart createAccessible() {
		return new AccessibleGraphicalEditPart() {

			public void getName(AccessibleEvent e) {
				e.result = "EditPart";
			}

			public void getValue(AccessibleControlEvent e) {
			}

		};
	}

	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.NODE_ROLE, null);
		installEditPolicy(EditPolicy.SELECTION_FEEDBACK_ROLE, null);
	}

	/**
	 * Returns a newly created Figure to represent this.
	 * 
	 * @return Figure of this.
	 */

	protected IFigure createFigure() {
		fig = new ParamListFigure(getPageWrapperModel());
		((ParamListFigure) fig).setEditPart(this);
		return fig;
	}

	/**
	 * Returns the model of this as a LED.
	 * 
	 * @return Model of this as an LED.
	 */
	public PageWrapper getPageWrapperModel() {
		return (PageWrapper) getModel();
	}

	Dimension size;

	private int calculateListWidth(){
		int width = 0;
		int nameWidth = 0;
		
		for(int i = 0; i < getPageWrapperModel().getPage().getChildren().size(); i++){
			Param param = (Param)getPageWrapperModel().getPage().getChildren().get(i);
			String name;
			if(param.getName() != null){
				name = param.getName();
				name += ":";
			}else
				name = "Param:";
			
			String value;
			if(param.getValue() != null){
				value = param.getValue();
			}else
				value = "value";
			
			int curName = FigureUtilities.getTextExtents(name, ParamFigure.nameParamFont).width;
			
			if(curName > nameWidth)
				nameWidth = curName;
			
			int current = FigureUtilities.getTextExtents(name, ParamFigure.nameParamFont).width+
			FigureUtilities.getTextExtents(value, ParamFigure.valueParamFont).width + 20;
			
			if(current > width)
				width = current;
		}
		
		((ParamListFigure)getFigure()).setNameWidth(nameWidth);
		
		return width;
	}

	protected void refreshVisuals() {
		refreshSizeAndLocation();

		if(getParent() != null){
			((PagesDiagramEditPart) ParamListEditPart.this.getParent())
				.setToFront(this);
		}
		
	}

	void refreshSizeAndLocation() {
		Point loc = getPageWrapperModel().getPage().getLocation().getCopy();
		int links = getPageWrapperModel().getPage().getOutputLinks().size();
		if(links == 0)
			links = 1;
		loc.y += 25+links*NodeFigure.LINK_HEIGHT;
		size = new Dimension(calculateListWidth(), getPageWrapperModel().getPage().getChildren().size()*19+2);
		adjustForGrid(loc);

		Rectangle r = new Rectangle(loc, size);

		if(getParent() != null){
			((GraphicalEditPart) getParent()).setLayoutConstraint(this,
				getFigure(), r);
		}
	}

	protected List getModelChildren() {
		return getPageWrapperModel().getPage().getChildren();
	}

	protected void refreshChildren() {
		super.refreshChildren();
		for (int i = 0; i < getChildren().size(); i++) {
			((ParamEditPart) getChildren().get(i)).refresh();

		}
	}
	
	/**
	 * @see org.eclipse.gef.EditPart#activate()
	 */
	public void activate() {
		if (isActive())
			return;
		((Notifier) getPageWrapperModel().getPage()).eAdapters().add(this);
		super.activate();
	}
	
	public void deactivate(){
		if (!isActive())
			return;
		((Notifier) getPageWrapperModel().getPage()).eAdapters().remove(this);
		super.deactivate();
	}
	
	/**
	 * @see org.eclipse.emf.common.notify.Adapter#notifyChanged(org.eclipse.emf.common.notify.Notification)
	 */
	public void notifyChanged(Notification notification) {
		refresh();
		refreshVisuals();
	}
	/**
	 * )
	 * 
	 * @see org.eclipse.emf.common.notify.Adapter#getTarget()
	 */
	public Notifier getTarget() {

		return null;
	}

	/**
	 * @see org.eclipse.emf.common.notify.Adapter#isAdapterForType(java.lang.Object)
	 */
	public boolean isAdapterForType(Object type) {
		return false;
	}

	/**
	 * @see org.eclipse.emf.common.notify.Adapter#setTarget(org.eclipse.emf.common.notify.Notifier)
	 */
	public void setTarget(Notifier newTarget) {
	}
}
