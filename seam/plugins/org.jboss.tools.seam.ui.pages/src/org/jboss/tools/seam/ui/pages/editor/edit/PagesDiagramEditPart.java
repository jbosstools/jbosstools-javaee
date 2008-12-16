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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.FreeformViewport;
import org.eclipse.draw2d.IFigure;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.gef.AccessibleEditPart;
import org.eclipse.gef.CompoundSnapToHelper;
import org.eclipse.gef.ConnectionEditPart;
import org.eclipse.gef.DragTracker;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.Request;
import org.eclipse.gef.SnapToGeometry;
import org.eclipse.gef.SnapToGrid;
import org.eclipse.gef.SnapToGuides;
import org.eclipse.gef.SnapToHelper;
import org.eclipse.gef.requests.SelectionRequest;
import org.eclipse.gef.rulers.RulerProvider;
import org.eclipse.gef.tools.DeselectAllTracker;
import org.eclipse.gef.tools.MarqueeDragTracker;
import org.eclipse.swt.accessibility.AccessibleEvent;
import org.jboss.tools.common.model.event.XModelTreeEvent;
import org.jboss.tools.common.model.event.XModelTreeListener;
import org.jboss.tools.common.model.options.PreferenceModelUtilities;
import org.jboss.tools.seam.pages.xml.model.SeamPagesPreference;
import org.jboss.tools.seam.ui.pages.editor.ecore.pages.Page;
import org.jboss.tools.seam.ui.pages.editor.ecore.pages.PagesModel;
import org.jboss.tools.seam.ui.pages.editor.ecore.pages.PagesModelListener;
import org.jboss.tools.seam.ui.pages.editor.figures.DiagramFigure;

public class PagesDiagramEditPart extends ContainerEditPart implements
		LayerConstants, PagesModelListener, Adapter, XModelTreeListener{


	/*
	 * 
	 */
	private DiagramFigure fig;

	/**
	 * Public constructor
	 * @return
	 */
	public boolean isGridVisible() {
		return SeamPagesPreference.SHOW_GRID.getValue().equals("yes");
	}

	/**
	 * 
	 * @return
	 */
	public int getVisualGridStep() {
		return Integer.parseInt(SeamPagesPreference.GRID_STEP.getValue());
	}

	/**
	 * 
	 */
	public void setModel(Object model) {
		super.setModel(model);
	}

	/**
	 * 
	 */
	public PagesModel getPagesModel() {
		return (PagesModel) getModel();
	}

	/**
	 * 
	 */
	public boolean isStrutsModelListenerEnabled() {
		return true;
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

	
	protected AccessibleEditPart createAccessible() {
		return new AccessibleGraphicalEditPart() {
			public void getName(AccessibleEvent event) {
				event.result = "Pages Diagram";
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
		installEditPolicy(EditPolicy.COMPONENT_ROLE, new PagesDiagramEditPolicy());
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
	
	public void nodeChanged(XModelTreeEvent event){
		String path = event.getModelObject().getPath();
		if(path.equals(SeamPagesPreference.SEAM_PAGES_EDITOR_PATH)){
			refresh();
		}
	}
	
    public void structureChanged(XModelTreeEvent event){
    	
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
		ArrayList list = new ArrayList();
		for(int i = 0; i < getPagesModel().getChildren().size(); i++){
			list.add(getPagesModel().getChildren().get(i));
			
			if(getPagesModel().getChildren().get(i) instanceof Page){
				Page page = (Page)getPagesModel().getChildren().get(i);
				if(page.getChildren().size() > 0 && page.isParamsVisible()){
					PageWrapper wrapper = page.getParamList();
					list.add(wrapper);
				}
			}
		}
		return list;
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
	
	/**
	 * @see org.eclipse.gef.EditPart#activate()
	 */
	public void activate() {
		if (isActive())
			return;
		((Notifier) getModel()).eAdapters().add(this);
		PreferenceModelUtilities.getPreferenceModel().addModelTreeListener(this);
		super.activate();
	}
	
	public void deactivate(){
		if (!isActive())
			return;
		((Notifier) getModel()).eAdapters().remove(this);
		PreferenceModelUtilities.getPreferenceModel().removeModelTreeListener(this);
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
