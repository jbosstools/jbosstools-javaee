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
package org.jboss.tools.seam.internal.core.validation;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.jdt.core.JavaConventions;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.wst.validation.internal.core.ValidationException;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.eclipse.wst.validation.internal.provisional.core.IValidationContext;
import org.eclipse.wst.validation.internal.provisional.core.IValidatorJob;
import org.jboss.tools.seam.core.ISeamProject;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.core.project.facet.SeamRuntimeManager;
import org.jboss.tools.seam.internal.core.project.facet.ISeamFacetDataModelProperties;
import org.jboss.tools.seam.internal.core.refactoring.SeamProjectChange;

/**
 * Validates seam project properties. 
 * @author Alexey Kazakov
 */
public class SeamProjectPropertyValidator implements IValidatorJob {

	/* (non-Javadoc)
	 * @see org.eclipse.wst.validation.internal.provisional.core.IValidatorJob#getSchedulingRule(org.eclipse.wst.validation.internal.provisional.core.IValidationContext)
	 */
	public ISchedulingRule getSchedulingRule(IValidationContext helper) {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.validation.internal.provisional.core.IValidatorJob#validateInJob(org.eclipse.wst.validation.internal.provisional.core.IValidationContext, org.eclipse.wst.validation.internal.provisional.core.IReporter)
	 */
	public IStatus validateInJob(IValidationContext helper, IReporter reporter)	throws ValidationException {
		SeamValidationHelper seamHelper = (SeamValidationHelper)helper;
		IProject project = seamHelper.getProject();
		ISeamProject seamProject = SeamCorePlugin.getSeamProject(project, false);
		if(seamProject!=null) {
			validateSeamProject(project);
		}

		IProject[] ps = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		String projectName = project.getName();
		for (int i = 0; i < ps.length; i++) {
			if(ps[i]!=project) {
				validateProject(projectName, ps[i]);
			}
		}

		return OK_STATUS;
	}

	private void validateProject(String nameOfChangedProject, IProject checkingProject) {
		ISeamProject seamProject = SeamCorePlugin.getSeamProject(checkingProject, false);
		if(seamProject==null) {
			return;
		}
		IEclipsePreferences pref = SeamCorePlugin.getSeamPreferences(checkingProject);
		for (int i = 0; i < SeamProjectChange.PROJECT_NAME_PROPERTIES.length; i++) {
			if(nameOfChangedProject.equals(pref.get(SeamProjectChange.PROJECT_NAME_PROPERTIES[i], null))) {
				validateSeamProject(checkingProject);
				return;
			} 
		}
		for (int i = 0; i < SeamProjectChange.FOLDER_PROPERTIES.length; i++) {
			if(pref.get(SeamProjectChange.FOLDER_PROPERTIES[i], "").startsWith("/" + nameOfChangedProject + "/")) {
				validateSeamProject(checkingProject);
				return;
			}
		}
	}

	private void validateProjectName(String projectName, boolean canBeEmpty) {
		if((canBeEmpty || (projectName!=null && projectName.length()==0)) && new Path("/").isValidSegment(projectName)) {
			IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
			if(project.exists()) {
				return;
			}
		}
		// Mark invalid project name
		// TODO
	}

	private boolean isFolderPathValid(String folderPath, boolean canBeEmpty) {
		if((canBeEmpty || (folderPath!=null && folderPath.length()==0)) && new Path("/").isValidSegment(folderPath)) {
			IResource folder = ResourcesPlugin.getWorkspace().getRoot().findMember(folderPath);
			if(folder!=null && (folder instanceof IContainer) && folder.exists()) {
				return true;
			}
		}
		return false;
	}

	private void validateSorceFolder(String folderPath, String packageName) {
		if(isFolderPathValid(folderPath, true)) {
			if(!isPackageNameValid(packageName)) {
				// Mark invalid source folder path
				// TODO
			}
			return;
		}
		// Mark invalid source folder path
		// TODO
	}

	private boolean isPackageNameValid(String packageName) {
		if(packageName==null || packageName.length()==0) {
			return false;
		}
		IStatus status = JavaConventions.validatePackageName(packageName, CompilerOptions.VERSION_1_5, CompilerOptions.VERSION_1_5);
		if(status.getSeverity()==IStatus.ERROR) {
			return false;
		}
		return true;
	}

	private boolean validateSeamProject(IProject project) {
		IEclipsePreferences pref = SeamCorePlugin.getSeamPreferences(project);
		String parentProject = pref.get(ISeamFacetDataModelProperties.SEAM_PARENT_PROJECT, null);
		if(parentProject!=null) {
			// EJB or Test project
			validateProjectName(parentProject, false);
		} else {
			// War project
			String settingVersion = pref.get(ISeamFacetDataModelProperties.SEAM_SETTINGS_VERSION, ISeamFacetDataModelProperties.SEAM_SETTINGS_VERSION_1_0);
			String seamRuntimeName = pref.get(ISeamFacetDataModelProperties.SEAM_RUNTIME_NAME, null);
			if(seamRuntimeName==null || seamRuntimeName.length()==0 || (SeamRuntimeManager.getInstance().findRuntimeByName(seamRuntimeName) == null)) {
				// Mark unknown runtime
				// TODO
			}

			if(ISeamFacetDataModelProperties.DEPLOY_AS_EAR.equals(pref.get(ISeamFacetDataModelProperties.JBOSS_AS_DEPLOY_AS, null))) {
				validateProjectName(pref.get(ISeamFacetDataModelProperties.SEAM_EJB_PROJECT, null), true);
			}
			String viewFolder = pref.get(ISeamFacetDataModelProperties.WEB_CONTENTS_FOLDER, null);
			if(isFolderPathValid(viewFolder, true)) {
				// Mark unknown View folder
				// TODO
			}
			validateSorceFolder(pref.get(ISeamFacetDataModelProperties.ENTITY_BEAN_SOURCE_FOLDER, null),
					pref.get(ISeamFacetDataModelProperties.ENTITY_BEAN_PACKAGE_NAME, null));

			validateSorceFolder(pref.get(ISeamFacetDataModelProperties.SESSION_BEAN_SOURCE_FOLDER, null),
					pref.get(ISeamFacetDataModelProperties.SESSION_BEAN_PACKAGE_NAME, null));

			String createTestString = pref.get(ISeamFacetDataModelProperties.TEST_CREATING, null);
			if(settingVersion.equals(ISeamFacetDataModelProperties.SEAM_SETTINGS_VERSION_1_0) || 
					"true".equals(createTestString)) {
				validateProjectName(pref.get(ISeamFacetDataModelProperties.SEAM_TEST_PROJECT, null), true);
				validateSorceFolder(pref.get(ISeamFacetDataModelProperties.TEST_SOURCE_FOLDER, null),
						pref.get(ISeamFacetDataModelProperties.TEST_CASES_PACKAGE_NAME, null));
			}
		}

		return true;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.validation.internal.provisional.core.IValidator#cleanup(org.eclipse.wst.validation.internal.provisional.core.IReporter)
	 */
	public void cleanup(IReporter reporter) {
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.validation.internal.provisional.core.IValidator#validate(org.eclipse.wst.validation.internal.provisional.core.IValidationContext, org.eclipse.wst.validation.internal.provisional.core.IReporter)
	 */
	public void validate(IValidationContext helper, IReporter reporter) throws ValidationException {
		validateInJob(helper, reporter);
	}
}