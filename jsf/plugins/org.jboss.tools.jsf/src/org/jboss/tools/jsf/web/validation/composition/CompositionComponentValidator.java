/******************************************************************************* 
 * Copyright (c) 2011 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.jsf.web.validation.composition;

import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.jst.j2ee.project.facet.IJ2EEFacetConstants;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualFolder;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.eclipse.wst.validation.internal.core.ValidationException;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.jboss.tools.common.el.core.resolver.ELContext;
import org.jboss.tools.jsf.JSFModelPlugin;
import org.jboss.tools.jsf.project.JSFNature;
import org.jboss.tools.jsf.web.validation.JSFSeverityPreferences;
import org.jboss.tools.jsf.web.validation.JSFValidationMessage;
import org.jboss.tools.jst.web.kb.IPageContext;
import org.jboss.tools.jst.web.kb.PageContextFactory;
import org.jboss.tools.jst.web.kb.internal.KbBuilder;
import org.jboss.tools.jst.web.kb.internal.validation.ContextValidationHelper;
import org.jboss.tools.jst.web.kb.internal.validation.KBValidator;
import org.jboss.tools.jst.web.kb.internal.validation.ValidatorManager;
import org.jboss.tools.jst.web.kb.validation.IProjectValidationContext;
import org.jboss.tools.jst.web.kb.validation.IValidatingProjectTree;

/**
 * JSF 2 composition component validator.
 * 
 * @author Alexey Kazakov
 */
public class CompositionComponentValidator extends KBValidator {

	public static final String ID = "org.jboss.tools.jsf.CompositionComponentValidator"; //$NON-NLS-1$
	public static final String PROBLEM_TYPE = "org.jboss.tools.jsf.compositionproblem"; //$NON-NLS-1$

	private IProject currentProject;
	private IContainer webRootFolder;

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.jst.web.kb.internal.validation.ValidationErrorManager#init(org.eclipse.core.resources.IProject, org.jboss.tools.jst.web.kb.internal.validation.ContextValidationHelper, org.jboss.tools.jst.web.kb.validation.IProjectValidationContext, org.eclipse.wst.validation.internal.provisional.core.IValidator, org.eclipse.wst.validation.internal.provisional.core.IReporter)
	 */
	@Override
	public void init(IProject project, ContextValidationHelper validationHelper, IProjectValidationContext context, org.eclipse.wst.validation.internal.provisional.core.IValidator manager, IReporter reporter) {
		super.init(project, validationHelper, context, manager, reporter);
		currentProject = null;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.jst.web.kb.validation.IValidator#validate(java.util.Set, org.eclipse.core.resources.IProject, org.jboss.tools.jst.web.kb.internal.validation.ContextValidationHelper, org.jboss.tools.jst.web.kb.validation.IProjectValidationContext, org.jboss.tools.jst.web.kb.internal.validation.ValidatorManager, org.eclipse.wst.validation.internal.provisional.core.IReporter)
	 */
	public IStatus validate(Set<IFile> changedFiles, IProject project, ContextValidationHelper validationHelper, IProjectValidationContext validationContext, ValidatorManager manager, IReporter reporter)	throws ValidationException {
		displaySubtask(JSFValidationMessage.SEARCHING_RESOURCES);
		init(project, validationHelper, validationContext, manager, reporter);
		webRootFolder = null;

		return OK_STATUS;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.jst.web.kb.validation.IValidator#validateAll(org.eclipse.core.resources.IProject, org.jboss.tools.jst.web.kb.internal.validation.ContextValidationHelper, org.jboss.tools.jst.web.kb.validation.IProjectValidationContext, org.jboss.tools.jst.web.kb.internal.validation.ValidatorManager, org.eclipse.wst.validation.internal.provisional.core.IReporter)
	 */
	public IStatus validateAll(IProject project, ContextValidationHelper validationHelper, IProjectValidationContext validationContext, ValidatorManager manager, IReporter reporter) throws ValidationException {
		displaySubtask(JSFValidationMessage.SEARCHING_RESOURCES);
		init(project, validationHelper, validationContext, manager, reporter);
		webRootFolder = null;

		return OK_STATUS;
	}

	private void validateResource(IFile file) {
		if(shouldFileBeValidated(file)) {
			coreHelper.getValidationContextManager().addValidatedProject(this, file.getProject());
			removeAllMessagesFromResource(file);
			ELContext context = PageContextFactory.createPageContext(file);
			if(context!=null && context instanceof IPageContext) {
				IPageContext pageContext = (IPageContext)context;
			}
		}
	}

	private boolean enabled = true;

	private boolean shouldFileBeValidated(IFile file) {
		if(!file.isAccessible()) {
			return false;
		}
		IProject project = file.getProject();
		if(!file.isSynchronized(IResource.DEPTH_ZERO)) {
			// The resource is out of sync with the file system
			// Just ignore this resource.
			return false;
		}
		if(!project.equals(currentProject)) {
			currentProject = project;
			enabled = isEnabled(project);	
			if(!enabled) {
				return false;
			}
			if(webRootFolder!=null && !project.equals(webRootFolder.getProject())) {
				webRootFolder = null;
			}
			if(webRootFolder==null) {
				IFacetedProject facetedProject = null;
				try {
					facetedProject = ProjectFacetsManager.create(project);
				} catch (CoreException e) {
					JSFModelPlugin.getDefault().logError(e);
				}
				if(facetedProject!=null && facetedProject.getProjectFacetVersion(IJ2EEFacetConstants.DYNAMIC_WEB_FACET)!=null) {
					IVirtualComponent component = ComponentCore.createComponent(project);
					if(component!=null) {
						IVirtualFolder webRootVirtFolder = component.getRootFolder().getFolder(new Path("/")); //$NON-NLS-1$
						webRootFolder = webRootVirtFolder.getUnderlyingFolder();
					}
				}
			}
			currentProject = project;
		}

		// Validate files from Web-Content only (in case of WTP project)
		return enabled && webRootFolder!=null && webRootFolder.getLocation().isPrefixOf(file.getLocation()) && PageContextFactory.isPage(file);
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.jst.web.kb.validation.IValidator#getId()
	 */
	public String getId() {
		return ID;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.jst.web.kb.validation.IValidator#getBuilderId()
	 */
	public String getBuilderId() {
		return KbBuilder.BUILDER_ID;
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
		return ValidatorManager.validateBuilderOrder(project, getBuilderId(), getId(), JSFSeverityPreferences.getInstance());
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.jst.web.kb.validation.IValidator#isEnabled(org.eclipse.core.resources.IProject)
	 */
	public boolean isEnabled(IProject project) {
		return JSFSeverityPreferences.isValidationEnabled(project);
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

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.jst.web.kb.internal.validation.ValidationErrorManager#getMarkerType()
	 */
	public String getMarkerType() {
		return PROBLEM_TYPE;
	}
}