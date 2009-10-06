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

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.datatools.connectivity.IConnectionProfile;
import org.eclipse.datatools.connectivity.ProfileManager;
import org.eclipse.jdt.core.JavaConventions;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.wst.validation.internal.core.ValidationException;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.eclipse.wst.validation.internal.provisional.core.IValidationContext;
import org.eclipse.wst.validation.internal.provisional.core.IValidatorJob;
import org.jboss.tools.seam.core.ISeamProject;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.core.SeamPreferences;
import org.jboss.tools.seam.core.project.facet.SeamRuntimeManager;
import org.jboss.tools.seam.internal.core.project.facet.ISeamFacetDataModelProperties;
import org.jboss.tools.seam.internal.core.refactoring.SeamProjectChange;

/**
 * Validates seam project properties. 
 * @author Alexey Kazakov
 */
public class SeamProjectPropertyValidator implements IValidatorJob {

	protected static final String VALIDATING_PROJECT = "VALIDATING_PROJECT";
	public static final String INVALID_SEAM_RUNTIME = "INVALID_SEAM_RUNTIME";
	protected static final String INVALID_WEBFOLDER = "INVALID_WEBFOLDER";
	protected static final String INVALID_PARENT_PROJECT = "INVALID_PARENT_PROJECT";
	protected static final String INVALID_EJB_PROJECT = "INVALID_EJB_PROJECT";
	protected static final String INVALID_TEST_PROJECT = "INVALID_TEST_PROJECT";
	protected static final String INVALID_MODEL_SRC = "INVALID_MODEL_SRC";
	protected static final String INVALID_ACTION_SRC = "INVALID_ACTION_SRC";
	protected static final String INVALID_TEST_SRC = "INVALID_TEST_SRC";
	protected static final String INVALID_MODEL_PACKAGE_NAME = "INVALID_MODEL_PACKAGE_NAME";
	protected static final String INVALID_ACTION_PACKAGE_NAME = "INVALID_ACTION_PACKAGE_NAME";
	protected static final String INVALID_TEST_PACKAGE_NAME = "INVALID_TEST_PACKAGE_NAME";
	protected static final String INVALID_CONNECTION_NAME = "INVALID_CONNECTION_NAME";

	private IValidationErrorManager errorManager;
	private Set<String> validatedProjects = new HashSet<String>();
	private static Set<String> beingValidatedProjects = new HashSet<String>();

	/* (non-Javadoc)
	 * @see org.eclipse.wst.validation.internal.provisional.core.IValidatorJob#getSchedulingRule(org.eclipse.wst.validation.internal.provisional.core.IValidationContext)
	 */
	public ISchedulingRule getSchedulingRule(IValidationContext helper) {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.validation.internal.provisional.core.IValidatorJob#validateInJob(org.eclipse.wst.validation.internal.provisional.core.IValidationContext, org.eclipse.wst.validation.internal.provisional.core.IReporter)
	 */
	public IStatus validateInJob(IValidationContext helper, IReporter reporter) throws ValidationException {
		validatedProjects.clear();
		SeamValidationHelper seamHelper = (SeamValidationHelper)helper;
		IProject project = seamHelper.getProject();
		if(!project.isAccessible()) {
			return OK_STATUS;
		}
		ISeamProject seamProject = SeamCorePlugin.getSeamProject(project, false);
		errorManager = new ValidationErrorManager(this, null, reporter, seamProject, project, ISeamValidator.MARKED_SEAM_PROJECT_MESSAGE_GROUP);
		if(seamProject!=null) {
			validateSeamProject(seamProject);
		}

		IProject[] ps = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		String projectName = project.getName();
		for (int i = 0; i < ps.length; i++) {
			if(ps[i]!=project) {
				validateProject(projectName, ps[i]);
			}
		}

		finishValidating();
		return OK_STATUS;
	}

	synchronized private void finishValidating() {
		beingValidatedProjects.removeAll(validatedProjects);
	}

	synchronized private boolean canStartValidating(IProject project) {
		String name = project.getName();
		if(validatedProjects.contains(name) || beingValidatedProjects.contains(name)) {
			return false;
		}
		validatedProjects.add(name);
		beingValidatedProjects.add(name);
		return true;
	}

	private void validateProject(String nameOfChangedProject, IProject checkingProject) {
		ISeamProject seamProject = SeamCorePlugin.getSeamProject(checkingProject, false);
		if(seamProject==null) {
			return;
		}
		IEclipsePreferences pref = SeamCorePlugin.getSeamPreferences(checkingProject);
		for (int i = 0; i < SeamProjectChange.PROJECT_NAME_PROPERTIES.length; i++) {
			if(nameOfChangedProject.equals(pref.get(SeamProjectChange.PROJECT_NAME_PROPERTIES[i], null))) {
				validateSeamProject(seamProject);
				return;
			} 
		}
		for (int i = 0; i < SeamProjectChange.FOLDER_PROPERTIES.length; i++) {
			if(pref.get(SeamProjectChange.FOLDER_PROPERTIES[i], "").startsWith("/" + nameOfChangedProject + "/")) {
				validateSeamProject(seamProject);
				return;
			}
		}
	}

	/**
	 * Returns true if the project with this name exists.
	 * @param projectName
	 * @param canBeEmpty if "true" and the project name is empty (null or "") then it is valid.  
	 * @return
	 */
	public static boolean isProjectNameValid(String projectName, boolean canBeEmpty) {
		if(projectName==null || projectName.length()==0) {
			return canBeEmpty;
		}
		if(new Path("/").isValidSegment(projectName)) {
			IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
			if(project.exists()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns true if the folder with this name exists.
	 * @param folderPath
	 * @param canBeEmpty if "true" and the folder path is empty (null or "") then it is valid.
	 * @return
	 */
	public static boolean isFolderPathValid(String folderPath, boolean canBeEmpty) {
		if(folderPath==null || folderPath.length()==0) {
			return canBeEmpty;
		}
		if(new Path("/").isValidPath(folderPath)) {
			IResource folder = ResourcesPlugin.getWorkspace().getRoot().findMember(folderPath);
			if(folder!=null && (folder instanceof IContainer) && folder.exists()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns true if the package name is valid (it does not have to exist).
	 * @param packageName
	 * @param canBeEmpty if "true" and the package name is empty (null or "") then it is valid.
	 * @return
	 */
	public static boolean isPackageNameValid(String packageName, boolean canBeEmpty) {
		if(packageName==null || packageName.length()==0) {
			return canBeEmpty;
		}
		IStatus status = JavaConventions.validatePackageName(packageName, CompilerOptions.VERSION_1_5, CompilerOptions.VERSION_1_5);
		if(status.getSeverity()==IStatus.ERROR) {
			return false;
		}
		return true;
	}

	/**
	 * Returns true if the profiler exists.
	 * @param profilerName
	 * @param canBeEmpty if "true" and the profiler name is empty (null or "") then it is valid.
	 * @return
	 */
	public static boolean isConnectionProfileValid(String profilerName, boolean canBeEmpty) {
		if(profilerName==null || profilerName.length()==0) {
			return canBeEmpty;
		}
		IConnectionProfile[] profiles = ProfileManager.getInstance().getProfilesByCategory("org.eclipse.datatools.connectivity.db.category"); //$NON-NLS-1$
		for (IConnectionProfile connectionProfile : profiles) {
			if(profilerName.equals(connectionProfile.getName())) {
				return true;
			}
		}
		return false;
	}

	private void validateProjectName(IProject targetProject, String projectName, boolean canBeEmpty, String messageId) {
		if(!isProjectNameValid(projectName, canBeEmpty)) {
			// Mark invalid project name
			errorManager.addError(messageId, SeamPreferences.INVALID_PROJECT_SETTINGS, new String[]{projectName!=null?projectName:"", targetProject.getName()}, targetProject);
		}
	}

	private void validateSorceFolder(IProject targetProject, String folderPath, String packageName, String srcFolderMessageID, String packageMessageId) {
		if(!isFolderPathValid(folderPath, true)) {
			// Mark invalid source folder path
			errorManager.addError(srcFolderMessageID, SeamPreferences.INVALID_PROJECT_SETTINGS, new String[]{folderPath!=null?folderPath:"", targetProject.getName()}, targetProject);
		}
		if(!isPackageNameValid(packageName, true)) {
			// Mark invalid source folder path
			errorManager.addError(packageMessageId, SeamPreferences.INVALID_PROJECT_SETTINGS, new String[]{packageName!=null?packageName:"", targetProject.getName()}, targetProject);
		}
	}

	private void validateSeamProject(ISeamProject seamProject) {
		errorManager.setProject(seamProject);
		IProject project = seamProject.getProject();
		if(!project.isAccessible()) {
			return;
		}
		if(!canStartValidating(project)) {
			return;
		}
		errorManager.removeAllMessagesFromResource(project);
		if(!SeamPreferences.shouldValidateSettings(project)) {
			return;
		}
		errorManager.displaySubtask(VALIDATING_PROJECT, new String[]{project.getName()});

		IEclipsePreferences pref = SeamCorePlugin.getSeamPreferences(project);
		String parentProject = pref.get(ISeamFacetDataModelProperties.SEAM_PARENT_PROJECT, null);
		if(parentProject!=null) {
			// EJB or Test project
			validateProjectName(project, parentProject, false, INVALID_PARENT_PROJECT);
		} else {
			// War project
			String settingVersion = pref.get(ISeamFacetDataModelProperties.SEAM_SETTINGS_VERSION, ISeamFacetDataModelProperties.SEAM_SETTINGS_VERSION_1_0);
			String seamRuntimeName = pref.get(ISeamFacetDataModelProperties.SEAM_RUNTIME_NAME, null);
			if(seamRuntimeName!=null && seamRuntimeName.length()>0 && (SeamRuntimeManager.getInstance().findRuntimeByName(seamRuntimeName) == null)) {
				// Mark unknown runtime
				errorManager.addError(INVALID_SEAM_RUNTIME, SeamPreferences.INVALID_PROJECT_SETTINGS, new String[]{seamRuntimeName!=null?seamRuntimeName:"", project.getName()}, project);
			}

			if(ISeamFacetDataModelProperties.DEPLOY_AS_EAR.equals(pref.get(ISeamFacetDataModelProperties.JBOSS_AS_DEPLOY_AS, null))) {
				validateProjectName(project, pref.get(ISeamFacetDataModelProperties.SEAM_EJB_PROJECT, null), true, INVALID_EJB_PROJECT);
			}
			String viewFolder = pref.get(ISeamFacetDataModelProperties.WEB_CONTENTS_FOLDER, null);
			if(!isFolderPathValid(viewFolder, true)) {
				// Mark unknown View folder
				errorManager.addError(INVALID_WEBFOLDER, SeamPreferences.INVALID_PROJECT_SETTINGS, new String[]{viewFolder!=null?viewFolder:"", project.getName()}, project);
			}
			validateSorceFolder(project, pref.get(ISeamFacetDataModelProperties.ENTITY_BEAN_SOURCE_FOLDER, null),
					pref.get(ISeamFacetDataModelProperties.ENTITY_BEAN_PACKAGE_NAME, null),
					INVALID_MODEL_SRC,
					INVALID_MODEL_PACKAGE_NAME);

			validateSorceFolder(project, pref.get(ISeamFacetDataModelProperties.SESSION_BEAN_SOURCE_FOLDER, null),
					pref.get(ISeamFacetDataModelProperties.SESSION_BEAN_PACKAGE_NAME, null),
					INVALID_ACTION_SRC,
					INVALID_ACTION_PACKAGE_NAME);

			String createTestString = pref.get(ISeamFacetDataModelProperties.TEST_CREATING, null);
			if(settingVersion.equals(ISeamFacetDataModelProperties.SEAM_SETTINGS_VERSION_1_0) || 
					"true".equals(createTestString)) {
				validateProjectName(project, pref.get(ISeamFacetDataModelProperties.SEAM_TEST_PROJECT, null), true, INVALID_TEST_PROJECT);
				validateSorceFolder(project, pref.get(ISeamFacetDataModelProperties.TEST_SOURCE_FOLDER, null),
						pref.get(ISeamFacetDataModelProperties.TEST_CASES_PACKAGE_NAME, null),
						INVALID_TEST_SRC,
						INVALID_TEST_PACKAGE_NAME);
			}
//			String connectionProfile = pref.get(ISeamFacetDataModelProperties.SEAM_CONNECTION_PROFILE, null);
//			if(!isConnectionProfileValid(connectionProfile, true)) {
//				errorManager.addError(INVALID_CONNECTION_NAME, SeamPreferences.INVALID_PROJECT_SETTINGS, new String[]{connectionProfile!=null?connectionProfile:"", project.getName()}, project);
//			}
		}

		return;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.validation.internal.provisional.core.IValidator#cleanup(org.eclipse.wst.validation.internal.provisional.core.IReporter)
	 */
	public void cleanup(IReporter reporter) {
//		finishValidating();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.validation.internal.provisional.core.IValidator#validate(org.eclipse.wst.validation.internal.provisional.core.IValidationContext, org.eclipse.wst.validation.internal.provisional.core.IReporter)
	 */
	public void validate(IValidationContext helper, IReporter reporter) throws ValidationException {
		validateInJob(helper, reporter);
	}
}