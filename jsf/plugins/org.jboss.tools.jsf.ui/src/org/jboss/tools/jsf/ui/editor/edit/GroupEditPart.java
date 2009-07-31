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
import java.beans.PropertyChangeListener;
import java.util.*;

import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.jboss.tools.common.model.ui.dnd.DnDUtil;
import org.eclipse.gef.*;
import org.eclipse.gef.requests.DropRequest;
import org.eclipse.swt.accessibility.AccessibleControlEvent;
import org.eclipse.swt.accessibility.AccessibleEvent;

import org.jboss.tools.common.meta.action.XAction;
import org.jboss.tools.common.model.XModelException;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.jsf.ui.JsfUiPlugin;
import org.jboss.tools.jsf.ui.editor.figures.GroupFigure;
import org.jboss.tools.jsf.ui.editor.figures.NodeFigure;
import org.jboss.tools.jsf.ui.editor.model.IGroup;
import org.jboss.tools.jsf.ui.editor.model.IGroupListener;
import org.jboss.tools.jsf.ui.editor.model.ILink;
import org.jboss.tools.jsf.ui.editor.model.IPage;

public class GroupEditPart extends JSFEditPart implements PropertyChangeListener, IGroupListener, EditPartListener {
	private GroupFigure fig = null;

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

	public void doDoubleClick(boolean cf) {
		try {
			XAction action = DnDUtil.getEnabledAction(
					(XModelObject) getGroupModel().getSource(), null,
					"OpenPage"); //$NON-NLS-1$
			if (action != null)
				action.executeHandler((XModelObject) getGroupModel()
						.getSource(), null);
		} catch (XModelException e) {
			JsfUiPlugin.getPluginLog().logError(e);
		}
	}

	public void setModel(Object model) {
		super.setModel(model);
		((IGroup) model).addPropertyChangeListener(this);
		((IGroup) model).addGroupListener(this);
		addEditPartListener(this);

		if (getGroupModel().getPageList().size() <= 1)
			single = true;
		else
			single = false;

		layoutPages();
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
		if (this.getSelected() == EditPart.SELECTED_PRIMARY) {
			((JSFDiagramEditPart) GroupEditPart.this.getParent())
					.setToFront(this);

		}
	}

	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals("name")) { //$NON-NLS-1$
			fig.setPath(getGroupModel().getVisiblePath());
		} else if (evt.getPropertyName().equals("path")) { //$NON-NLS-1$
			fig.setPath(getGroupModel().getVisiblePath());
		} else if (evt.getPropertyName().equals("selected")) { //$NON-NLS-1$
		} else if (evt.getPropertyName().equals("shape")) { //$NON-NLS-1$
			refreshVisuals();
		}
	}

	public boolean isGroupListenerEnable() {
		return true;
	}

	public void groupChange() {
		layoutPages();
		refresh();
		fig.setIcon(getGroupModel().getImage());
		fig.refreshFont();
		fig.repaint();
	}

	public void pageAdd(IGroup group, IPage page) {
		if (getGroupModel().getPageList().size() > 1) {
			if (single) {
				single = false;
			}
			layoutPages();
			refresh();
		}
	}

	public void pageRemove(IGroup group, IPage page) {
		if (getGroupModel().getPageList().size() == 1 && !single) {
			fig.init(getGroupModel().getListOutputLinks().size());
			single = true;
		}
		layoutPages();
		refresh();
	}

	public void pageChange(IGroup group, IPage page, PropertyChangeEvent evet) {
		layoutPages();
		refresh();
	}

	public void linkAdd(IPage page, ILink link) {
		layoutPages();

		if (single) {
			fig
					.addConnectionAnchor(getGroupModel().getListOutputLinks()
							.size());
		}
		refreshTargetLink(link);
		refresh();
	}

	private void refreshTargetLink(ILink link) {
		if (link == null)
			return;
		GroupEditPart gep = (GroupEditPart) getViewer().getEditPartRegistry()
				.get(link.getToGroup());
		if (gep == null)
			return;
		gep.refreshTargetConnections();
	}

	public void linkRemove(IPage page, ILink link) {
		layoutPages();
		refresh();

		if (single) {
			fig.removeConnectionAnchor();
		}
		refreshTargetLink(link);
		refresh();
	}

	public void linkChange(IPage page, ILink link, PropertyChangeEvent evet) {
		refresh();
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

	protected List getModelTargetConnections() {
		return getGroupModel().getListInputLinks();
	}

	protected List getModelSourceConnections() {
		if (single) {
			return getGroupModel().getListOutputLinks();
		} else {
			return Collections.EMPTY_LIST;
		}
	}

	protected void createEditPolicies() {
		super.createEditPolicies();
		installEditPolicy(EditPolicy.NODE_ROLE, null);
		installEditPolicy(EditPolicy.SELECTION_FEEDBACK_ROLE, null);
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new GroupEditPolicy());
		installEditPolicy(EditPolicy.LAYOUT_ROLE, new JSFFlowEditPolicy());
		installEditPolicy(EditPolicy.CONNECTION_ENDPOINTS_ROLE,
				new GroupEditPolicy());
	}

	/**
	 * Returns a newly created Figure to represent this.
	 * 
	 * @return Figure of this.
	 */

	protected IFigure createFigure() {
		fig = new GroupFigure(getGroupModel());
		((GroupFigure) fig).setGroupEditPart(this);
		return fig;
	}

	public GroupFigure getGroupFigure() {
		return (GroupFigure) getFigure();
	}

	/**
	 * Returns the model of this as a LED.
	 * 
	 * @return Model of this as an LED.
	 */
	public IGroup getGroupModel() {
		return (IGroup) getModel();
	}

	Dimension size;

	public void layoutPages() {
		size = new Dimension();
		int start = 0;
		int height;
		size.width = 50;
		size.height = 23;

		if (getGroupModel().isPattern()) {
			size.width += 3;
			start += 3;
		}

		IPage page;
		for (int i = 0; i < getGroupModel().getPageList().size(); i++) {
			page = (IPage) getGroupModel().getPageList().get(i);

			height = page.getLinkList().size() * NodeFigure.LINK_HEIGHT - 1;
			if (page.getLinkList().size() == 0)
				height = NodeFigure.LINK_HEIGHT - 1;
			if (getGroupModel().isPattern())
				page.setBounds(start, size.height, size.width - 6, height);
			else
				page.setBounds(start, size.height, size.width - 3, height);

			size.height += height + 1;
		}
		size.height += 2;
		if (getGroupModel().isPattern()) {
			size.height += 1;
		}
	}

	protected void refreshVisuals() {
		Point loc = getGroupModel().getPosition();
		loc.x -= loc.x % 8;
		loc.y -= loc.y % 8;

		Rectangle r = new Rectangle(loc, size);

		((GraphicalEditPart) getParent()).setLayoutConstraint(this,
				getFigure(), r);
	}

	public ConnectionAnchor getTargetConnectionAnchor(
			ConnectionEditPart connEditPart) {
		ConnectionAnchor anc = getNodeFigure().getConnectionAnchor("1_IN"); //$NON-NLS-1$
		return anc;
	}

	public ConnectionAnchor getTargetConnectionAnchor(Request request) {
		Point pt = new Point(((DropRequest) request).getLocation());
		return getNodeFigure().getTargetConnectionAnchorAt(pt);
	}

	public ConnectionAnchor getSourceConnectionAnchor(
			ConnectionEditPart connEditPart) {
		if (single) {
			ILink link = (ILink) connEditPart.getModel();
			int index = getGroupModel().getListOutputLinks().indexOf(link);
			return getNodeFigure().getConnectionAnchor((index + 1) + "_OUT"); //$NON-NLS-1$
		} else
			return super.getSourceConnectionAnchor(connEditPart);
	}

	public ConnectionAnchor getSourceConnectionAnchor(Request request) {
		if (single) {
			Point pt = new Point(((DropRequest) request).getLocation());
			return getNodeFigure().getSourceConnectionAnchorAt(pt);
		} else
			return super.getSourceConnectionAnchor(request);
	}

	protected List getModelChildren() {
		return (single) ? Collections.EMPTY_LIST : getGroupModel()
				.getPageList().getElements();
	}

	protected void refreshChildren() {
		super.refreshChildren();
		for (int i = 0; i < getChildren().size(); i++) {
			((PageEditPart) getChildren().get(i)).refresh();

		}
	}

}
