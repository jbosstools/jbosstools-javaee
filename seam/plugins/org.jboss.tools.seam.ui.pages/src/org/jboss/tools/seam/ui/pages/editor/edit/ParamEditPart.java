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

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.gef.AccessibleEditPart;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartListener;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.editparts.AbstractEditPart;
import org.eclipse.swt.accessibility.AccessibleControlEvent;
import org.eclipse.swt.accessibility.AccessibleEvent;
import org.jboss.tools.seam.ui.pages.editor.ecore.pages.Param;
import org.jboss.tools.seam.ui.pages.editor.figures.ParamFigure;

public class ParamEditPart extends PagesEditPart implements PropertyChangeListener, EditPartListener, Adapter {
	private ParamFigure fig = null;

	private boolean single = true;

	public boolean isSingle() {
		return single;
	}

	public void doControlUp() {
	}

	public void doControlDown() {
	}

	public void doMouseHover(boolean cf) {
	}

	public void childAdded(EditPart child, int index) {
	}

	public void partActivated(EditPart editpart) {
	}

	public void partDeactivated(EditPart editpart) {
	}

	public void removingChild(EditPart child, int index) {
	}

	public void selectedStateChanged(EditPart editpart) {
		fig.repaint();
	}

	public boolean isGroupListenerEnable() {
		return true;
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

	protected List getModelTargetConnections() {
		return getParamModel().getInputLinks();
	}

	protected List getModelSourceConnections() {
		return getParamModel().getOutputLinks();
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
		fig = new ParamFigure(getParamModel());
		((ParamFigure) fig).setEditPart(this);
		return fig;
	}

	/**
	 * Returns the model of this as a LED.
	 * 
	 * @return Model of this as an LED.
	 */
	public Param getParamModel() {
		return (Param) getModel();
	}

	Dimension size;

	

	protected void refreshVisuals() {
		Point loc = new Point(1,1);
		size = new Dimension(200, getVisualHeight());
		adjustForGrid(loc);

		Rectangle r = new Rectangle(loc, size);

		((GraphicalEditPart) getParent()).setLayoutConstraint(this,
				getFigure(), r);
	}

	private int getVisualHeight() {
		return 21;
	}

	protected List getModelChildren() {
		return getParamModel().getChildren();
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
		((Notifier) getModel()).eAdapters().add(this);
		addEditPartListener(this);
		super.activate();
	}
	
	public void deactivate(){
		if (!isActive())
			return;
		((Notifier) getModel()).eAdapters().remove(this);
		removeEditPartListener(this);
		super.deactivate();
	}
	
	/**
	 * @see org.eclipse.emf.common.notify.Adapter#notifyChanged(org.eclipse.emf.common.notify.Notification)
	 */
	public void notifyChanged(Notification notification) {
		refresh();
		refreshVisuals();
		if(getParent() instanceof ParamListEditPart) {
			((ParamListEditPart)getParent()).refreshSizeAndLocation();
		}
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
