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
package org.jboss.tools.jsf.ui.editor.edit;

import java.beans.PropertyChangeEvent;
import java.util.Collections;
import java.util.List;

import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Point;
import org.jboss.tools.common.model.ui.dnd.DnDUtil;
import org.eclipse.gef.*;
import org.eclipse.gef.requests.DropRequest;
import org.eclipse.swt.accessibility.AccessibleControlEvent;
import org.eclipse.swt.accessibility.AccessibleEvent;

import org.jboss.tools.common.meta.action.XAction;
import org.jboss.tools.common.model.XModelException;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.jsf.ui.JsfUiPlugin;
import org.jboss.tools.jsf.ui.editor.figures.PageFigure;
import org.jboss.tools.jsf.ui.editor.model.ILink;
import org.jboss.tools.jsf.ui.editor.model.IPage;
import org.jboss.tools.jsf.ui.editor.model.IPageListener;

public class PageEditPart extends JSFEditPart implements EditPartListener, IPageListener {
	private PageFigure fig = null;

	public void doMouseUp(boolean cf) {

	}

	public void doMouseDown(boolean cf) {

	}

	public void doMouseHover(boolean cf) {

	}

	public void doDoubleClick(boolean cf) {
		try {
			XAction action = DnDUtil
					.getEnabledAction(
							(XModelObject) getPageModel().getSource(), null,
							"OpenPage"); //$NON-NLS-1$
			if (action != null)
				action.executeHandler(
						(XModelObject) getPageModel().getSource(), null);
		} catch (XModelException e) {
			JsfUiPlugin.getPluginLog().logError(e);
		}
	}

	public void setModel(Object model) {
		super.setModel(model);
		addEditPartListener(this);
		getPageModel().addPageListener(this);
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

	public boolean isPageListenerEnable() {
		return true;
	}

	public void pageRemoved(IPage page) {

	}

	public void pageChange() {
		refresh();
	}

	public void linkAdd(IPage page, ILink link) {
		fig.addConnectionAnchor(getPageModel().getLinkList().size());
		refresh();
	}

	public void linkRemove(IPage page, ILink link) {
		fig.removeConnectionAnchor();
		refresh();
	}

	public void linkChange(IPage page, ILink link, PropertyChangeEvent event) {
		refreshSourceConnections();
	}

	protected AccessibleEditPart createAccessible() {
		return new AccessibleGraphicalEditPart() {

			public void getName(AccessibleEvent e) {
				e.result = "EditPart"; //$NON-NLS-1$
			}

			public void getValue(AccessibleControlEvent e) {
			}

		};
	}

	protected void createEditPolicies() {
		super.createEditPolicies();
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new PageEditPolicy());
		installEditPolicy(EditPolicy.CONNECTION_ENDPOINTS_ROLE,
				new PageEditPolicy());
	}

	protected IFigure createFigure() {
		fig = new PageFigure(this);

		return fig;
	}

	public PageFigure getPageFigure() {
		return (PageFigure) getFigure();
	}

	public IPage getPageModel() {
		return (IPage) getModel();
	}

	/**
	 * Apart from the usual visual update, it also updates the numeric contents
	 * of the LED.
	 */
	protected List getModelSourceConnections() {

		if (getParent() == null)
			return Collections.EMPTY_LIST;

		if (((GroupEditPart) getParent()).isSingle()) {
			// - PageEditPart getModelSourceConnections - EMPTY");
			return Collections.EMPTY_LIST;
		} else {
			// - PageEditPart getModelSourceConnections
			// "+getPageModel().getLinkList().size());
			return getPageModel().getLinkList().getElements();
		}
	}

	protected List getModelTargetConnections() {
		// - PageEditPart getModelTargetConnections - EMPTY");
		return Collections.EMPTY_LIST;
	}

	public ConnectionAnchor getSourceConnectionAnchor(
			ConnectionEditPart connEditPart) {

		ILink link = (ILink) connEditPart.getModel();
		int index = getPageModel().getLinkList().indexOf(link);
		ConnectionAnchor anc = getNodeFigure().getConnectionAnchor(
				(index + 1) + "_OUT"); //$NON-NLS-1$
		// - PageEditPart getSourceConnectionAnchor "+index+" link
		// -"+link.getLinkName()+" "+anc);
		return anc;
	}

	public ConnectionAnchor getSourceConnectionAnchor(Request request) {
		// - PageEditPart getSourceConnectionAnchor by Request");
		Point pt = new Point(((DropRequest) request).getLocation());
		return getNodeFigure().getSourceConnectionAnchorAt(pt);
	}

	public void setSelected(int i) {
		super.setSelected(i);
		refreshVisuals();
	}

	protected void refreshVisuals() {
		if (getParent() == null)
			return;

		((GraphicalEditPart) getParent()).setLayoutConstraint(this,
				getFigure(), getPageModel().getBounds());
	}
}
