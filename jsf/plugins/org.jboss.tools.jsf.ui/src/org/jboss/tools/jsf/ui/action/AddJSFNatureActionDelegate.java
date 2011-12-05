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
package org.jboss.tools.jsf.ui.action;

import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.jboss.tools.common.meta.key.WizardKeys;
import org.jboss.tools.common.model.ui.util.ExtensionPointUtils;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IFacetedProjectWorkingCopy;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.eclipse.wst.common.project.facet.ui.internal.ConvertProjectToFacetedFormRunnable;
import org.eclipse.wst.common.project.facet.ui.internal.FacetsPropertyPage;
import org.eclipse.wst.common.project.facet.ui.internal.SharedWorkingCopyManager;
import org.jboss.tools.common.model.ui.ModelUIPlugin;
import org.jboss.tools.common.model.ui.action.AddNatureActionDelegate;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.jsf.JSFModelPlugin;
import org.jboss.tools.jsf.project.JSFNature;
import org.jboss.tools.jsf.ui.JsfUiPlugin;
import org.jboss.tools.jsf.ui.wizard.project.ImportProjectWizard;
import org.jboss.tools.jst.web.WebModelPlugin;
import org.jboss.tools.jst.web.kb.internal.KbBuilder;
import org.jboss.tools.jst.web.kb.internal.KbProject;

public class AddJSFNatureActionDelegate extends AddNatureActionDelegate {
	boolean showDialog = true;
	
	public AddJSFNatureActionDelegate() {}
	
	public AddJSFNatureActionDelegate(boolean showDialog) {
		this.showDialog = showDialog;
	}
	
	protected IWizard getWizard(IProject project) {
		ImportProjectWizard wizard = (ImportProjectWizard)ExtensionPointUtils.findImportWizardsItem(
				JSFModelPlugin.PLUGIN_ID,
				"org.jboss.tools.jsf.ui.wizard.project.ImportProjectWizard" //$NON-NLS-1$
		);
		if (wizard == null) throw new IllegalArgumentException("Wizard org.jboss.tools.common.model.ui.wizards.ImportProjectWizard is not found.");	 //$NON-NLS-1$
		wizard.setInitialName(project.getName());
		wizard.setInitialLocation(findWebXML(project.getLocation().toString()));
		wizard.init(ModelUIPlugin.getDefault().getWorkbench(), null);
		wizard.setWindowTitle(WizardKeys.getString("ADD_JSF_NATURE")); //$NON-NLS-1$
		return wizard;
	}

	protected String getNatureID() {
		return JSFNature.NATURE_ID;
	}
	
	public void run(IAction action) {
		ConvertProjectToFacetedFormRunnable.runInProgressDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), project);
		addJSFNature(project, showDialog);
	}

	/**
	 * Convenience method that adds JBoss Tools JSF nature to the project.
	 * These are the limitations for the use of the method.
	 * 1) It should be run from UI thread.
	 * 2) It should be run on a faceted project.
	 * 
	 * @param project
	 * @param showDialog
	 */
	public static void addJSFNature(IProject project, boolean showDialog) {
		try {
			IFacetedProject fp = ProjectFacetsManager.create(project);
			IFacetedProjectWorkingCopy wc = SharedWorkingCopyManager.getWorkingCopy(fp);

			Set<IProjectFacetVersion> vs = wc.getProjectFacets();
			IProjectFacetVersion web = null;
			IProjectFacetVersion jsf = null;
			for (IProjectFacetVersion v: vs) {
				String id = v.getProjectFacet().getId();
				if("jst.web".equals(id)) { //$NON-NLS-1$
					web = v;
				} else if("jst.jsf".equals(id)) { //$NON-NLS-1$
					jsf = v;
				}
			}
			
			if(web != null && jsf != null && wc.validate().isOK()) {
				WebModelPlugin.addNatureToProjectWithValidationSupport(project, KbBuilder.BUILDER_ID, KbProject.NATURE_ID);
				EclipseResourceUtil.addNatureToProject(project, JSFNature.NATURE_ID);
				SharedWorkingCopyManager.releaseWorkingCopy(fp);
				return;
			}

			if(web == null) {
				web = ProjectFacetsManager.getProjectFacet("jst.web").getLatestVersion(); //$NON-NLS-1$
				wc.addProjectFacet(web);
			}
			String webVersion = web.getVersionString();
			if("2.2".equals(webVersion)) { //$NON-NLS-1$
				web = ProjectFacetsManager.getProjectFacet("jst.web").getVersion("2.3"); //$NON-NLS-1$ //$NON-NLS-2$
				wc.changeProjectFacetVersion(web);
				webVersion = web.getVersionString();
			}
			if(jsf == null) {
				jsf = ProjectFacetsManager.getProjectFacet("jst.jsf").getLatestVersion(); //$NON-NLS-1$
				if("2.3".equals(webVersion) || "2.4".equals(webVersion)) { //$NON-NLS-1$ //$NON-NLS-2$
					jsf = ProjectFacetsManager.getProjectFacet("jst.jsf").getVersion("1.1"); //$NON-NLS-1$ //$NON-NLS-2$
				}
				wc.addProjectFacet(jsf);
			}

			if(!showDialog) {
				wc.commitChanges(new NullProgressMonitor());
			} else {
				PreferenceDialog dialog = PreferencesUtil.createPropertyDialogOn(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), project, FacetsPropertyPage.ID, new String[] {FacetsPropertyPage.ID}, null);
				dialog.open();
			}

			SharedWorkingCopyManager.releaseWorkingCopy(fp);
		} catch (CoreException e) {
			JsfUiPlugin.getDefault().logError(e);
		}

	}

}
