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

import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.ui.INewWizard;
import org.hibernate.eclipse.console.ExtensionManager;
import org.hibernate.eclipse.console.model.impl.ExporterDefinition;
import org.hibernate.eclipse.launch.HibernateLaunchConstants;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.core.project.facet.SeamFacetPreference;
import org.jboss.tools.seam.core.project.facet.SeamRuntime;
import org.jboss.tools.seam.core.project.facet.SeamRuntimeManager;
import org.jboss.tools.seam.internal.core.project.facet.ISeamFacetDataModelProperties;
import org.jboss.tools.seam.ui.SeamUIMessages;

/**
 * Seam Generate Entities Wizard.
 * @author Alexey Kazakov
 */
public class SeamGenerateEnitiesWizard extends SeamBaseWizard implements INewWizard {

	public SeamGenerateEnitiesWizard() {
		super(GENERATE_SEAM_ENTITIES);
		setWindowTitle(SeamUIMessages.GENERATE_SEAM_ENTITIES_WIZARD_TITLE);
		addPage(new SeamGenerateEnitiesWizardPage());
	}

	public static final IUndoableOperation GENERATE_SEAM_ENTITIES = new SeamBaseOperation("Action creating operation") {

		@Override
		public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
			Map<String, String> params = (Map)info.getAdapter(Map.class);	
			IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(
					params.get(IParameter.SEAM_PROJECT_NAME));
			
			try {
				ILaunchManager launchManager = DebugPlugin.getDefault().getLaunchManager();
				ILaunchConfigurationType launchConfigurationType = 
					launchManager.getLaunchConfigurationType(
							"org.hibernate.eclipse.launch.CodeGenerationLaunchConfigurationType");
				ILaunchConfigurationWorkingCopy wc = 
					launchConfigurationType.newInstance(project, project.getName() + "generate-entities");
				
				//Main
				wc.setAttribute(
						HibernateLaunchConstants.ATTR_CONSOLE_CONFIGURATION_NAME, 
						params.get(IParameter.HIBERNATE_CONFIGURATION_NAME));
				
				IPath src = getSourceFolder(project);
				if(src == null) {
					throw new CoreException(
							new Status(IStatus.ERROR, SeamCorePlugin.PLUGIN_ID, "Source folder not found in project " + project.getName()));
				}
				wc.setAttribute(HibernateLaunchConstants.ATTR_OUTPUT_DIR, src.toString());

				boolean isReverseEngineer = "true".equals(params.get(HibernateLaunchConstants.ATTR_REVERSE_ENGINEER));
				wc.setAttribute(HibernateLaunchConstants.ATTR_REVERSE_ENGINEER, isReverseEngineer);

				if(isReverseEngineer) {
					wc.setAttribute(HibernateLaunchConstants.ATTR_PACKAGE_NAME, "seamtest");
					wc.setAttribute(HibernateLaunchConstants.ATTR_PREFER_BASIC_COMPOSITE_IDS, true);
					wc.setAttribute(HibernateLaunchConstants.ATTR_AUTOMATIC_MANY_TO_MANY, true);
					wc.setAttribute(HibernateLaunchConstants.ATTR_AUTOMATIC_VERSIONING, true);
				}
				
				wc.setAttribute(HibernateLaunchConstants.ATTR_USE_OWN_TEMPLATES, true);
				SeamRuntime seamRt = SeamRuntimeManager.getInstance().getDefaultRuntime();
				
				String runtimeName = SeamCorePlugin.getSeamFacetPreferences(project)
					.get(ISeamFacetDataModelProperties.SEAM_RUNTIME_NAME,"");
				
				if(!"".equals(runtimeName)) {
					seamRt = SeamRuntimeManager.getInstance().findRuntimeByName(runtimeName);
				}
				
				String template = "" + seamRt.getHomeDir() + "/seam-gen/view";
				wc.setAttribute(HibernateLaunchConstants.ATTR_TEMPLATE_DIR, template);
				
				wc.setAttribute(HibernateLaunchConstants.ATTR_ENABLE_JDK5, true);
				wc.setAttribute(HibernateLaunchConstants.ATTR_ENABLE_EJB3_ANNOTATIONS, true);
				
				ExporterDefinition[] ds = ExtensionManager.findExporterDefinitions();
				wc.setAttribute("org.hibernate.tools.hbm2java", true);

				wc.doSave();
				launchManager.addLaunch(wc.launch(ILaunchManager.RUN_MODE, monitor));
				
			} catch (CoreException e) {
				SeamCorePlugin.getPluginLog().logError(e);
			}
			return Status.OK_STATUS;
		}

		@Override
		public List<String[]> getFileMappings(Map<String, Object> vars) {
			throw new UnsupportedOperationException("This method is not relevant in generating seam entities.");
		}
		
	};
	
	static IPath getSourceFolder(IProject project) throws CoreException {
		if(!project.hasNature(JavaCore.NATURE_ID)) return null;
		IJavaProject javaProject = JavaCore.create(project);		
		IClasspathEntry[] es = javaProject.getRawClasspath();
		for (int i = 0; i < es.length; i++) {
			if(es[i].getEntryKind() != IClasspathEntry.CPE_SOURCE) continue;
			IPath p = es[i].getPath();
			if(p == null) continue;
			IFolder f = ResourcesPlugin.getWorkspace().getRoot().getFolder(p);
			if(f != null && f.exists()) {
				return p;
			}
		}
		return null;
	}

}
