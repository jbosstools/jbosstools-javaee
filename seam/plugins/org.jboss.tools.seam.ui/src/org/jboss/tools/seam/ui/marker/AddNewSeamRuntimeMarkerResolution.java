/*******************************************************************************
 * Copyright (c) 2010 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.seam.ui.marker;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IMarkerResolution2;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.core.project.facet.SeamRuntime;
import org.jboss.tools.seam.core.project.facet.SeamRuntimeManager;
import org.jboss.tools.seam.core.project.facet.SeamVersion;
import org.jboss.tools.seam.internal.core.project.facet.ISeamFacetDataModelProperties;
import org.jboss.tools.seam.ui.SeamGuiPlugin;
import org.jboss.tools.seam.ui.SeamUIMessages;
import org.jboss.tools.seam.ui.preferences.SeamPreferencesMessages;
import org.jboss.tools.seam.ui.widget.editor.SeamRuntimeListFieldEditor;


/**
 * @author Daniel Azarov
 */
public class AddNewSeamRuntimeMarkerResolution implements IMarkerResolution2{
	private IProject project;
	private String runtimeName;
	private String label;
	
	public AddNewSeamRuntimeMarkerResolution(IMarker marker){
		project = (IProject)marker.getResource();
		
		IEclipsePreferences preferences = SeamCorePlugin.getSeamPreferences(project);
		runtimeName = preferences.get(ISeamFacetDataModelProperties.SEAM_RUNTIME_NAME,"");
		this.label = MessageFormat.format(SeamUIMessages.ADD_NEW_SEAM_RUNTIME_MARKER_RESOLUTION_TITLE, new Object[]{runtimeName});
	}

	public String getLabel() {
		return label;
	}

	public void run(IMarker marker) {
		if(marker.getResource() instanceof IProject){
			String runtimeVersion = "";
			
			try{
				IProjectFacet facet = ProjectFacetsManager.getProjectFacet(ISeamFacetDataModelProperties.SEAM_FACET_ID);
				
				IFacetedProject facetedProject = ProjectFacetsManager.create(project);
				if(facetedProject!=null) {
					IProjectFacetVersion facetVersion = facetedProject.getInstalledVersion(facet);
					if(facetVersion!=null){
						runtimeVersion = SeamVersion.parseFromString(facetVersion.getVersionString()).toString();
					}
				}
			}catch(CoreException ex){
				SeamGuiPlugin.getPluginLog().logError(ex);
			}
			
			SeamRuntimeListFieldEditor seamRuntimes = new SeamRuntimeListFieldEditor(
					"rtlist", SeamPreferencesMessages.SEAM_PREFERENCE_PAGE_SEAM_RUNTIMES, new ArrayList<SeamRuntime>(Arrays.asList(SeamRuntimeManager.getInstance().getRuntimes()))); //$NON-NLS-1$
			Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
			Composite root = new Composite(shell, SWT.NONE);
			root.setVisible(false);
			seamRuntimes.getEditorControls(root);

			
			seamRuntimes.getAddAction().run(runtimeName, runtimeVersion);
			
			for (SeamRuntime rt : seamRuntimes.getAddedSeamRuntimes()) {
				SeamRuntimeManager.getInstance().addRuntime(rt);
			}
			SeamRuntimeManager.getInstance().save();
			seamRuntimes.dispose();
		}
	}
	
	public String getDescription() {
		return null;
	}

	public Image getImage() {
		return null;
	}

}
