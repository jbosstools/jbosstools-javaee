/******************************************************************************* 
 * Copyright (c) 2013 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.jsf.web.validation;

import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.wst.validation.internal.core.ValidationException;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.jboss.tools.common.validation.ContextValidationHelper;
import org.jboss.tools.common.validation.IProjectValidationContext;
import org.jboss.tools.common.validation.IValidatingProjectTree;
import org.jboss.tools.common.validation.PreferenceInfoManager;
import org.jboss.tools.common.validation.ValidatorManager;
import org.jboss.tools.jsf.JSFModelPlugin;
import org.jboss.tools.jsf.project.JSFNature;
import org.jboss.tools.jsf.web.validation.composite.CompositeComponentValidator;
import org.jboss.tools.jst.web.kb.internal.KbBuilder;
import org.jboss.tools.jst.web.kb.internal.validation.KBValidator;
import org.jboss.tools.jst.web.kb.internal.validation.WebValidator;

/**
 * @author Alexey Kazakov
 */
public class StrictTaglibValidator extends WebValidator {

	public static final String ID = "org.jboss.tools.jsf.StrictTagLibValidator"; //$NON-NLS-1$

	/* (non-Javadoc)
	 * @see org.jboss.tools.common.validation.IValidator#validate(java.util.Set, org.eclipse.core.resources.IProject, org.jboss.tools.common.validation.ContextValidationHelper, org.jboss.tools.common.validation.IProjectValidationContext, org.jboss.tools.common.validation.ValidatorManager, org.eclipse.wst.validation.internal.provisional.core.IReporter)
	 */
	@Override
	public IStatus validate(Set<IFile> changedFiles, IProject project,
			ContextValidationHelper validationHelper,
			IProjectValidationContext validationContext,
			ValidatorManager manager, IReporter reporter)
			throws ValidationException {

		// TODO
		return null;
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.common.validation.IValidator#validateAll(org.eclipse.core.resources.IProject, org.jboss.tools.common.validation.ContextValidationHelper, org.jboss.tools.common.validation.IProjectValidationContext, org.jboss.tools.common.validation.ValidatorManager, org.eclipse.wst.validation.internal.provisional.core.IReporter)
	 */
	@Override
	public IStatus validateAll(IProject project,
			ContextValidationHelper validationHelper,
			IProjectValidationContext validationContext,
			ValidatorManager manager, IReporter reporter)
			throws ValidationException {
		// TODO
		return null;
	}

	private void unknownAttribute(IFile target, int offset, int length, String tagName, String attributeName) {
		addMessage(target, offset, length, JSFSeverityPreferences.UNKNOWN_TAGLIB_ATTRIBUTE, JSFValidationMessage.UNKNOWN_TAGLIB_COMPONENT_ATTRIBUTE, new String[]{tagName, attributeName});
	}

	private void unknownTag(IFile target, int offset, int length, String tagName) {
		addMessage(target, offset, length, JSFSeverityPreferences.UNKNOWN_TAGLIB_COMPONENT, JSFValidationMessage.UNKNOWN_TAGLIB_COMPONENT_NAME, new String[]{tagName});
	}

	private void addMessage(IFile target, int offset, int length, String preferenceKey, String message, String[] arguments) {
		addMessage(target, offset, length, preferenceKey, message, arguments);
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.common.validation.IValidator#getId()
	 */
	@Override
	public String getId() {
		return ID;
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.common.validation.IValidator#getBuilderId()
	 */
	@Override
	public String getBuilderId() {
		return KbBuilder.BUILDER_ID;
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.jst.web.kb.internal.validation.WebValidator#shouldValidateJavaSources()
	 */
	@Override
	protected boolean shouldValidateJavaSources() {
		return false;
	}

	private static final String BUNDLE_NAME = "org.jboss.tools.jsf.web.validation.messages";

	/* (non-Javadoc)
	 * @see org.jboss.tools.common.validation.TempMarkerManager#getMessageBundleName()
	 */
	@Override
	protected String getMessageBundleName() {
		return BUNDLE_NAME;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.jst.web.kb.validation.IValidator#getValidatingProjects(org.eclipse.core.resources.IProject)
	 */
	public IValidatingProjectTree getValidatingProjects(IProject project) {
		return createSimpleValidatingProjectTree(project);
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.jst.web.kb.validation.IValidator#shouldValidate(org.eclipse.core.resources.IProject)
	 */
	public boolean shouldValidate(IProject project) {
		try {
			return project != null 
					&& project.isAccessible() 
					&& project.hasNature(JSFNature.NATURE_ID) 
					&& validateBuilderOrder(project)
					&& isEnabled(project);
		} catch (CoreException e) {
			JSFModelPlugin.getDefault().logError(e);
		}
		return false;
	}

	private boolean validateBuilderOrder(IProject project) throws CoreException {
		return KBValidator.validateBuilderOrder(project, getBuilderId(), getId(), JSFSeverityPreferences.getInstance());
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.jst.web.kb.validation.IValidator#isEnabled(org.eclipse.core.resources.IProject)
	 */
	public boolean isEnabled(IProject project) {
		return JSFSeverityPreferences.isValidationEnabled(project) && JSFSeverityPreferences.shouldValidateTagLibs(project);
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.jst.web.kb.internal.validation.ValidationErrorManager#getPreference(org.eclipse.core.resources.IProject, java.lang.String)
	 */
	protected String getPreference(IProject project, String preferenceKey) {
		return JSFSeverityPreferences.getInstance().getProjectPreference(project, preferenceKey);
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.jst.web.kb.internal.validation.ValidationErrorManager#getMaxNumberOfMarkersPerFile(org.eclipse.core.resources.IProject)
	 */
	public int getMaxNumberOfMarkersPerFile(IProject project) {
		return JSFSeverityPreferences.getMaxNumberOfProblemMarkersPerFile(project);
	}

	@Override
	public void registerPreferenceInfo() {
		PreferenceInfoManager.register(getProblemType(), new CompositeComponentValidator.CompositeComponentPreferenceInfo());
	}
}