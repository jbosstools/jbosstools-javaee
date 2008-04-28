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
import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.*;
import org.eclipse.gef.*;

import org.eclipse.gef.requests.SelectionRequest;
import org.eclipse.gef.rulers.RulerProvider;
import org.eclipse.gef.tools.DeselectAllTracker;
import org.eclipse.gef.tools.MarqueeDragTracker;
import org.eclipse.swt.accessibility.AccessibleEvent;

import org.jboss.tools.struts.ui.editor.figures.DiagramFigure;
import org.jboss.tools.struts.ui.editor.model.ILink;
import org.jboss.tools.struts.ui.editor.model.IProcessItem;
import org.jboss.tools.struts.ui.editor.model.IStrutsModel;
import org.jboss.tools.struts.ui.editor.model.IStrutsModelListener;

public class StrutsDiagramEditPart extends StrutsContainerEditPart implements
		LayerConstants, IStrutsModelListener {
	private boolean gridVisual = false;

	private int gridVisualStep = 8;

	private DiagramFigure fig;

	public boolean isGridVisible() {
		return gridVisual;
	}

	public int getVisualGridStep() {
		return gridVisualStep;
	}

	public void doDoubleClick(boolean cf) {
	}

	public void doMouseDown(boolean cf) {
	}

	public void doMouseUp(boolean cf) {
	}

	public void setModel(Object model) {
		super.setModel(model);
		((IStrutsModel) model).addStrutsModelListener(this);
		gridVisual = getStrutsModel().getOptions().isGridVisible();
		gridVisualStep = getStrutsModel().getOptions().getGridStep();
	}

	public IStrutsModel getStrutsModel() {
		return (IStrutsModel) getModel();
	}

	public boolean isStrutsModelListenerEnabled() {
		return true;
	}

	public void processChanged(boolean flag) {
		if (gridVisual != getStrutsModel().getOptions().isGridVisible()
				|| gridVisualStep != getStrutsModel().getOptions()
						.getGridStep()) {
			gridVisual = getStrutsModel().getOptions().isGridVisible();
			gridVisualStep = getStrutsModel().getOptions().getGridStep();
			fig.repaint();
		}
	}

	public void setToFront(EditPart ep) {
		int index = getChildren().indexOf(ep);
		if (index == -1)
			return;
		if (index != getChildren().size() - 1)
			reorderChild(ep, getChildren().size() - 1);
	}

	public void processItemAdd(IProcessItem processItem) {
		refresh();
	}

	public void processItemRemove(IProcessItem group) {
		refresh();
	}

	public void linkAdd(ILink link) {
	}

	public void linkRemove(ILink link) {
	}

	protected AccessibleEditPart createAccessible() {
		return new AccessibleGraphicalEditPart() {
			public void getName(AccessibleEvent e) {
				e.result = "JSF Diagram";
			}
		};
	}

	/**
	 * 
	 */
	protected void createEditPolicies() {
		super.createEditPolicies();
		installEditPolicy(EditPolicy.NODE_ROLE, null);
		installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE, null);
		installEditPolicy(EditPolicy.SELECTION_FEEDBACK_ROLE, null);
		installEditPolicy(EditPolicy.COMPONENT_ROLE,
				new StrutsDiagramEditPolicy());
	}

	/**
	 *
	 * 
	 * @return Figure.
	 */
	protected IFigure createFigure() {
		fig = new DiagramFigure(this);
		return fig;
	}

	public FreeformViewport getFreeformViewport() {
		return (FreeformViewport) getAncestor(fig, FreeformViewport.class);
	}

	public IFigure getAncestor(IFigure figure, Class cls) {
		IFigure parent = fig;
		while (parent != null) {
			if (parent.getClass().equals(cls))
				return parent;
			parent = parent.getParent();
		}
		return null;
	}

	public DragTracker getDragTracker(Request req) {
		if (req instanceof SelectionRequest
				&& ((SelectionRequest) req).getLastButtonPressed() == 3)
			return new DeselectAllTracker(this);
		return new MarqueeDragTracker();
	}

	/**
	 * It does not hold any connections.
	 * 
	 * @return <code>NULL</code>
	 */
	public ConnectionAnchor getSourceConnectionAnchor(
			ConnectionEditPart editPart) {
		return null;
	}

	/**
	 * It does not hold any connections.
	 * 
	 * @return <code>NULL</code>
	 */
	public ConnectionAnchor getSourceConnectionAnchor(int x, int y) {
		return null;
	}

	/**
	 * It does not hold any connections.
	 * 
	 * @return <code>NULL</code>
	 */
	public ConnectionAnchor getTargetConnectionAnchor(
			ConnectionEditPart editPart) {
		return null;
	}

	/**
	 * It does not hold any connections.
	 * 
	 * @return <code>NULL</code>
	 */
	public ConnectionAnchor getTargetConnectionAnchor(int x, int y) {
		return null;
	}

	public void propertyChange(PropertyChangeEvent evt) {
	}

	protected List getModelChildren() {
		return getStrutsModel().getProcessItemList().getElements();
	}

	public Object getAdapter(Class adapter) {
		if (adapter == SnapToHelper.class) {
			List snapStrategies = new ArrayList();
			Boolean val = (Boolean) getViewer().getProperty(
					RulerProvider.PROPERTY_RULER_VISIBILITY);
			if (val != null && val.booleanValue())
				snapStrategies.add(new SnapToGuides(this));
			val = (Boolean) getViewer().getProperty(
					SnapToGeometry.PROPERTY_SNAP_ENABLED);
			if (val != null && val.booleanValue())
				snapStrategies.add(new SnapToGeometry(this));
			val = (Boolean) getViewer().getProperty(
					SnapToGrid.PROPERTY_GRID_ENABLED);
			if (val != null && val.booleanValue())
				snapStrategies.add(new SnapToGrid(this));

			if (snapStrategies.size() == 0)
				return null;
			if (snapStrategies.size() == 1)
				return (SnapToHelper) snapStrategies.get(0);

			SnapToHelper ss[] = new SnapToHelper[snapStrategies.size()];
			for (int i = 0; i < snapStrategies.size(); i++)
				ss[i] = (SnapToHelper) snapStrategies.get(i);
			return new CompoundSnapToHelper(ss);
		}
		return super.getAdapter(adapter);
	}

}
