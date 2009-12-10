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
import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.*;
import org.eclipse.gef.*;

import org.eclipse.gef.requests.SelectionRequest;
import org.eclipse.gef.rulers.RulerProvider;
import org.eclipse.gef.tools.DeselectAllTracker;
import org.eclipse.gef.tools.MarqueeDragTracker;
import org.eclipse.swt.accessibility.AccessibleEvent;

import org.jboss.tools.jsf.ui.JsfUIMessages;
import org.jboss.tools.jsf.ui.editor.figures.DiagramFigure;
import org.jboss.tools.jsf.ui.editor.model.IGroup;
import org.jboss.tools.jsf.ui.editor.model.IJSFModel;
import org.jboss.tools.jsf.ui.editor.model.IJSFModelListener;
import org.jboss.tools.jsf.ui.editor.model.ILink;

/**
 * 
 * @author eskimo(dgolovin@exadel.com)
 *
 */
public class JSFDiagramEditPart extends JSFContainerEditPart implements
		LayerConstants, IJSFModelListener {
	/*
	 * 
	 */
	private boolean gridVisual = false;

	/*
	 * 
	 */
	private int gridVisualStep = 8;

	/*
	 * 
	 */
	private DiagramFigure fig;

	/**
	 * Public constructor
	 * @return
	 */
	public boolean isGridVisible() {
		return gridVisual;
	}

	/**
	 * 
	 * @return
	 */
	public int getVisualGridStep() {
		return gridVisualStep;
	}

	/**
	 * 
	 */
	public void setModel(Object model) {
		super.setModel(model);
		((IJSFModel) model).addJSFModelListener(this);
		gridVisual = getJSFModel().getOptions().isGridVisible();
		gridVisualStep = getJSFModel().getOptions().getVisualGridStep();
	}

	/**
	 * 
	 */
	public IJSFModel getJSFModel() {
		return (IJSFModel) getModel();
	}

	/**
	 * 
	 */
	public boolean isStrutsModelListenerEnabled() {
		return true;
	}

	/**
	 * 
	 */
	public void processChanged(boolean flag) {
		if (gridVisual != getJSFModel().getOptions().isGridVisible()
				|| gridVisualStep != getJSFModel().getOptions()
						.getVisualGridStep()) {
			gridVisual = getJSFModel().getOptions().isGridVisible();
			gridVisualStep = getJSFModel().getOptions().getVisualGridStep();
			fig.repaint();
		}
	}

	/**
	 * 
	 * @param editPart
	 */
	public void setToFront(EditPart editPart) {
		int index = getChildren().indexOf(editPart);
		if (index == -1)
			return;
		if (index != getChildren().size() - 1)
			reorderChild(editPart, getChildren().size() - 1);
	}

	/**
	 * 
	 */
	public void groupAdd(IGroup group) {
		refresh();
	}

	/**
	 * 
	 */
	public void groupRemove(IGroup group) {
		refresh();
	}

	/**
	 * 
	 */
	public void linkAdd(ILink link) { }

	/**
	 * 
	 */
	public void linkRemove(ILink link) { }

	protected AccessibleEditPart createAccessible() {
		return new AccessibleGraphicalEditPart() {
			public void getName(AccessibleEvent event) {
				event.result = JsfUIMessages.JSFDiagramEditPart_JSFDiagram;
			}
		};
	}

	/**
	 * Installs edit policies for this part.
	 */
	protected void createEditPolicies() {
		super.createEditPolicies();
		installEditPolicy(EditPolicy.NODE_ROLE, null);
		installEditPolicy(EditPolicy.GRAPHICAL_NODE_ROLE, null);
		installEditPolicy(EditPolicy.SELECTION_FEEDBACK_ROLE, null);
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new JSFDiagramEditPolicy());
	}

	/**
	 * Returns a figure
	 * 
	 * @return Figure.
	 */
	protected IFigure createFigure() {
		fig = new DiagramFigure(this);
		return fig;
	}

	/**
	 * 
	 * @return
	 */
	public FreeformViewport getFreeformViewport() {
		return (FreeformViewport) getAncestor(fig, FreeformViewport.class);
	}

	/**
	 * 
	 * @param figure
	 * @param cls
	 * @return
	 */
	public IFigure getAncestor(IFigure figure, Class cls) {
		IFigure parent = fig;
		while (parent != null) {
			if (parent.getClass().equals(cls))
				return parent;
			parent = parent.getParent();
		}
		return null;
	}

	/**
	 * 
	 */
	public DragTracker getDragTracker(Request req) {
		if (req instanceof SelectionRequest
				&& ((SelectionRequest) req).getLastButtonPressed() == 3)
			return new DeselectAllTracker(this);
		return new MarqueeDragTracker();
	}

	/**
	 * it does not hold any connections. .
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
	
	/**
	 * 
	 */
	public void propertyChange(PropertyChangeEvent evt) {
	}

	/**
	 * 
	 */
	protected void refreshVisuals() {
	}

	/**
	 * 
	 */
	protected List getModelChildren() {
		return getJSFModel().getGroupList().getElements();
	}

	/**
	 * 
	 */
	public Object getAdapter(Class adapter) {
		if (adapter == SnapToHelper.class) {
			List<Object> snapStrategies = new ArrayList<Object>();
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
