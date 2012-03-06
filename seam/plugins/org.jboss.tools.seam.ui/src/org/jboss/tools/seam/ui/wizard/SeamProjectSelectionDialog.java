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
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ListDialog;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.jboss.tools.seam.core.ISeamProject;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.core.SeamCoreMessages;
import org.jboss.tools.seam.internal.core.project.facet.ISeamFacetDataModelProperties;

/**
 * @author eskimo
 *
 */
public class SeamProjectSelectionDialog extends ListDialog implements ISelectionChangedListener {
	Button check;
	boolean allowAllProjects = false;

	/**
	 * @param parent
	 * @param allowAllProjects If "false" show only projects with seam nature.
	 */
	public SeamProjectSelectionDialog(Shell parent, final boolean allowAllProjects) {
		super(parent);
		this.allowAllProjects = allowAllProjects;
		setTitle(SeamCoreMessages.SEAM_PROJECT_SELECTION_DIALOG_SEAM_WEB_PROJECT);
		setMessage(SeamCoreMessages.SEAM_PROJECT_SELECTION_DIALOG_SELECT_SEAM_WEB_PROJECT);
		setLabelProvider(new WorkbenchLabelProvider());
		setInput(new Object());
		setContentProvider(new IStructuredContentProvider() {
			public Object[] getElements(Object inputElement) {
				ArrayList<IProject> seamProjects = new ArrayList<IProject>();
				for (IProject project : ResourcesPlugin.getWorkspace().getRoot().getProjects()) {
					try {
						if(project.isAccessible() && (
								(allowAllProjects || (check != null && check.getSelection())) 
							|| (project.hasNature(ISeamProject.NATURE_ID) 
								&& SeamCorePlugin.getSeamPreferences(project)!=null
								&& project.getAdapter(IFacetedProject.class)!=null
								&& ((IFacetedProject)project.getAdapter(IFacetedProject.class)).hasProjectFacet(ProjectFacetsManager.getProjectFacet("jst.web"))
								&& !"".equals(SeamCorePlugin.getSeamPreferences(project).get(ISeamFacetDataModelProperties.JBOSS_AS_DEPLOY_AS, ""))))) { //$NON-NLS-1$
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
	 * @param parent
	 */
	public SeamProjectSelectionDialog(Shell parent) {
		this(parent, false);
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

    protected Control createDialogArea(Composite container) {
    	Composite parent = (Composite) super.createDialogArea(container);
    	if(!allowAllProjects) {
    		check = new Button(parent, SWT.CHECK);
    		check.setText(SeamCoreMessages.SEAM_PROJECT_SELECTION_DIALOG_SHOW_ALL_PROJECTS);
    		check.setSelection(false);
    		check.addSelectionListener(new SelectionListener() {
				public void widgetDefaultSelected(SelectionEvent e) {
					widgetSelected(e);
				}
				public void widgetSelected(SelectionEvent e) {
					TableViewer v = getTableViewer();
					v.refresh();
				}    			
    		});
    	}
    	return parent;
    }
}