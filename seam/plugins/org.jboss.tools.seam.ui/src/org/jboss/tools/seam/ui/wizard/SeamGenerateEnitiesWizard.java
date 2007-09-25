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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.IUndoableOperation;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.ui.INewWizard;
import org.hibernate.eclipse.launch.HibernateLaunchConstants;
import org.jboss.tools.seam.core.J2EEProjects;
import org.jboss.tools.seam.core.SeamCorePlugin;
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
					launchManager.getLaunchConfigurationType("org.hibernate.eclipse.launch.CodeGenerationLaunchConfigurationType");
				ILaunchConfigurationWorkingCopy wc = 
					launchConfigurationType.newInstance(project, project.getName() + "generate-entities");

				//Main
				wc.setAttribute(
					HibernateLaunchConstants.ATTR_CONSOLE_CONFIGURATION_NAME, 
					params.get(IParameter.HIBERNATE_CONFIGURATION_NAME));

				J2EEProjects seamProjectUtil = J2EEProjects.create(project);
				IPath webContentPath = null;
				IFolder webContent = seamProjectUtil.getWARContentFolder();
				IProject webProject = null;
				if(webContent!=null && webContent.exists()) {
					webContentPath = webContent.getFullPath();
					webProject = seamProjectUtil.getWARProjects().get(0);
				}

				if(webContentPath == null) {
					throw new CoreException(new Status(IStatus.ERROR, SeamCorePlugin.PLUGIN_ID, "WebContent folder not found in project " + project.getName()));
				}
				wc.setAttribute(HibernateLaunchConstants.ATTR_OUTPUT_DIR, webContentPath.toString());

				boolean isReverseEngineer = "true".equals(params.get(HibernateLaunchConstants.ATTR_REVERSE_ENGINEER));
				wc.setAttribute(HibernateLaunchConstants.ATTR_REVERSE_ENGINEER, isReverseEngineer);

				if(isReverseEngineer) {
					wc.setAttribute(HibernateLaunchConstants.ATTR_PACKAGE_NAME, "seamtest");
					wc.setAttribute(HibernateLaunchConstants.ATTR_PREFER_BASIC_COMPOSITE_IDS, true);
					wc.setAttribute(HibernateLaunchConstants.ATTR_AUTOMATIC_MANY_TO_MANY, true);
					wc.setAttribute(HibernateLaunchConstants.ATTR_AUTOMATIC_VERSIONING, true);
				}

				wc.setAttribute(HibernateLaunchConstants.ATTR_USE_OWN_TEMPLATES, true);
				SeamRuntime seamRt = getRuntime(project);
				if(seamRt==null) {
					seamRt = getRuntime(webProject);
				}
				if(seamRt == null) {
					throw new CoreException(new Status(IStatus.ERROR, SeamCorePlugin.PLUGIN_ID, "Can't find seam runtime for project " + project.getName()));
				}

				IResource[] resources = seamProjectUtil.getEJBSourceRoots();
				IPath javaSource = null;
				if(resources!=null && resources.length>0) {
					javaSource = resources[0].getFullPath();
				}
				if(javaSource == null) {
					throw new CoreException(new Status(IStatus.ERROR, SeamCorePlugin.PLUGIN_ID, "Source folder not found in project " + project.getName()));
				}

				String template = "" + seamRt.getHomeDir() + "/seam-gen/view";
				wc.setAttribute(HibernateLaunchConstants.ATTR_TEMPLATE_DIR, template);

				wc.setAttribute(HibernateLaunchConstants.ATTR_ENABLE_JDK5, true);
				wc.setAttribute(HibernateLaunchConstants.ATTR_ENABLE_EJB3_ANNOTATIONS, true);

				// Create exporters
				// TODO Add others exporters
				List<String> exporters = new ArrayList<String>();
				exporters.add("hbmtemplate1");
				exporters.add("hbmtemplate2");
				wc.setAttribute(HibernateLaunchConstants.ATTR_EXPORTERS, exporters);

				wc.setAttribute(HibernateLaunchConstants.ATTR_EXPORTERS + ".hbmtemplate1", true);
				wc.setAttribute(HibernateLaunchConstants.ATTR_EXPORTERS + ".hbmtemplate1.extension_id", "org.hibernate.tools.hbmtemplate");
				wc.setAttribute(HibernateLaunchConstants.ATTR_EXPORTERS + ".hbmtemplate2", true);
				wc.setAttribute(HibernateLaunchConstants.ATTR_EXPORTERS + ".hbmtemplate2.extension_id", "org.hibernate.tools.hbmtemplate");

//				ExporterDefinition[] ds = ExtensionManager.findExporterDefinitions();

				// Set properties:
				//         	<hbmtemplate filepattern="{class-name}List.xhtml"
				//                       template="view/list.xhtml.ftl" 
		        //                       destdir="${project.home}/view"
	            //                       foreach="entity"/>

				Map<String, String> hbmtemplate1Attributes = new HashMap<String, String>();
				hbmtemplate1Attributes.put("file_pattern", "{class-name}List.xhtml");
				hbmtemplate1Attributes.put("template_path", template);
				hbmtemplate1Attributes.put("template_name", "list.xhtml.ftl");
				// TODO create "view" folder
				hbmtemplate1Attributes.put("outputdir", webContentPath.toString() + "/view");
				hbmtemplate1Attributes.put("for_each", "entity");
				wc.setAttribute(HibernateLaunchConstants.ATTR_EXPORTERS + ".hbmtemplate1.properties", hbmtemplate1Attributes);

				// Set properties:
				//       	<hbmtemplate filepattern="{class-name}.page.xml"
				//                       template="view/view.page.xml.ftl" 
				//		                 destdir="${project.home}/view"
				//                       foreach="entity"/>

				Map<String, String> hbmtemplate2Attributes = new HashMap<String, String>();
				hbmtemplate2Attributes.put("file_pattern", "{class-name}.page.xml");
				hbmtemplate1Attributes.put("template_path", template);
				hbmtemplate1Attributes.put("template_name", "view.page.xml.ftl");
				hbmtemplate2Attributes.put("outputdir", webContentPath.toString() + "/view");
				hbmtemplate2Attributes.put("for_each", "entity");
				wc.setAttribute(HibernateLaunchConstants.ATTR_EXPORTERS + ".hbmtemplate2.properties", hbmtemplate2Attributes);

				// TODO Set properties for others exporters

				wc.doSave();
				launchManager.addLaunch(wc.launch(ILaunchManager.RUN_MODE, monitor));
			} catch (CoreException e) {
				SeamCorePlugin.getDefault().showError("Can't generate seam entities", e);
			}
			return Status.OK_STATUS;
		}

		@Override
		public List<String[]> getFileMappings(Map<String, Object> vars) {
			throw new UnsupportedOperationException("This method is not relevant in generating seam entities.");
		}
	};

	static SeamRuntime getRuntime(IProject project) {
		if(project==null) {
			return null;
		}
		SeamRuntime seamRt = SeamRuntimeManager.getInstance().getDefaultRuntime();

		String runtimeName = SeamCorePlugin.getSeamPreferences(project)
			.get(ISeamFacetDataModelProperties.SEAM_RUNTIME_NAME,"");

		if(!"".equals(runtimeName)) {
			seamRt = SeamRuntimeManager.getInstance().findRuntimeByName(runtimeName);
		}
		return seamRt;
	}
}