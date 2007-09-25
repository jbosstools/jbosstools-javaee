/*******************************************************************************
 * Copyright (c) 2007 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.jboss.tools.seam.ui.wizard;

import java.util.ArrayList;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ListDialog;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.core.project.facet.SeamFacetPreference;
import org.jboss.tools.seam.internal.core.SeamProject;

/**
 * @author eskimo
 *
 */
public class SeamProjectSelectionDialog extends ListDialog implements ISelectionChangedListener {

	/**
	 * @param parent
	 */
	public SeamProjectSelectionDialog(Shell parent) {
		super(parent);
		setTitle("Seam Web Projects");
		setMessage("Select Seam Web Project");
		setLabelProvider(new WorkbenchLabelProvider());
		setInput(new Object());
		setContentProvider(new IStructuredContentProvider() {
			public Object[] getElements(Object inputElement) {
				ArrayList<IProject> seamProjects = new ArrayList<IProject>();
				for (IProject project : ResourcesPlugin.getWorkspace().getRoot().getProjects()) {
					try {
						if(project.hasNature(SeamProject.NATURE_ID) 
								&& SeamCorePlugin.getSeamPreferences(project)!=null
								&& project.getAdapter(IFacetedProject.class)!=null
								&& ((IFacetedProject)project.getAdapter(IFacetedProject.class)).hasProjectFacet(ProjectFacetsManager.getProjectFacet("jst.web"))) {
							seamProjects.add(project);
						}
					} catch (CoreException e) {
						SeamCorePlugin.getPluginLog().logError(e);
					}
				}
				return seamProjects.toArray();
			}
			public void dispose() {
			}
			public void inputChanged(Viewer viewer,
					Object oldInput, Object newInput) {
			}
		});
	}
	
	/**
	 * 
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		super.createButtonsForButtonBar(parent);
		getOkButton().setEnabled(false);
		getTableViewer().addSelectionChangedListener(this);
	}

	/**
	 * 
	 */
	public void selectionChanged(SelectionChangedEvent event) {
		getOkButton().setEnabled(!event.getSelection().isEmpty());
	}
}
