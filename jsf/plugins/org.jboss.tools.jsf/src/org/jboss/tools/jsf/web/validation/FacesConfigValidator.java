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
package org.jboss.tools.jsf.web.validation;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.eclipse.wst.validation.internal.core.ValidationException;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.common.validation.ContextValidationHelper;
import org.jboss.tools.common.validation.IProjectValidationContext;
import org.jboss.tools.common.validation.IValidatingProjectSet;
import org.jboss.tools.common.validation.IValidatingProjectTree;
import org.jboss.tools.common.validation.IValidator;
import org.jboss.tools.common.validation.ValidationErrorManager;
import org.jboss.tools.common.validation.ValidatorManager;
import org.jboss.tools.common.validation.internal.ProjectValidationContext;
import org.jboss.tools.common.validation.internal.SimpleValidatingProjectTree;
import org.jboss.tools.common.validation.internal.ValidatingProjectSet;
import org.jboss.tools.common.web.WebUtils;
import org.jboss.tools.jsf.model.JSFConstants;
import org.jboss.tools.jst.web.WebModelPlugin;
import org.jboss.tools.jst.web.validation.Check;
import org.jboss.tools.jst.web.validation.CheckClass;
import org.jboss.tools.jst.web.validation.WebXMLPreferences;

/**
 * @author Viacheslav Kabanovich
 */
public class FacesConfigValidator extends ValidationErrorManager implements IValidator, JSFConstants {
	public static final String ID = "org.jboss.tools.esb.validator.ESBCoreValidator"; //$NON-NLS-1$
	public static final String PROBLEM_TYPE = "org.jboss.tools.jsf.facesconfigproblem"; //$NON-NLS-1$
	public static final String PREFERENCE_PAGE_ID = "org.jboss.tools.jsf.ui.FacesConfigValidatorPreferencePage"; //$NON-NLS-1$

	public static String SHORT_ID = "verification"; //$NON-NLS-1$

	static String XML_EXT = ".xml"; //$NON-NLS-1$

	String projectName;
	Map<IProject, IProjectValidationContext> contexts = new HashMap<IProject, IProjectValidationContext>();

	Map<String, Set<Check>> checks = new HashMap<String, Set<Check>>();

	public FacesConfigValidator() {
		createChecks();
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.jst.web.kb.internal.validation.ValidationErrorManager#getPreference(org.eclipse.core.resources.IProject, java.lang.String)
	 */
	@Override
	protected String getPreference(IProject project, String preferenceKey) {
		return WebXMLPreferences.getInstance().getProjectPreference(project, preferenceKey);
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.jst.web.kb.internal.validation.ValidationErrorManager#getMaxNumberOfMarkersPerFile(org.eclipse.core.resources.IProject)
	 */
	@Override
	public int getMaxNumberOfMarkersPerFile(IProject project) {
		return WebXMLPreferences.getMaxNumberOfProblemMarkersPerFile(project);
	}

	private void addCheck(Check check, String... entities) {
		for (String entity: entities) {
			Set<Check> cs = checks.get(entity);
			if(cs == null) {
				cs = new HashSet<Check>();
				checks.put(entity, cs);
			}
			cs.add(check);
		}
	}

	void createChecks() {
		String ENT_APP = "JSFApplication", ENT_APP_12 = ENT_APP + SUFF_12, ENT_APP_20 = ENT_APP + SUFF_20;
		addCheck(new CheckClass(this, JSFSeverityPreferences.INVALID_ACTION_LISTENER, "action-listener", false, "javax.faces.event.ActionListener", null), ENT_APP, ENT_APP_12, ENT_APP_20);
		addCheck(new CheckClass(this, JSFSeverityPreferences.INVALID_NAVIGATION_HANDLER, "navigation-handler", false, "javax.faces.application.NavigationHandler", null), ENT_APP, ENT_APP_12, ENT_APP_20);
		addCheck(new CheckClass(this, JSFSeverityPreferences.INVALID_PROPERTY_RESOLVER, "property-resolver", false, "javax.faces.el.PropertyResolver", null), ENT_APP, ENT_APP_12, ENT_APP_20);
		addCheck(new CheckClass(this, JSFSeverityPreferences.INVALID_STATE_MANAGER, "state-manager", false, "javax.faces.application.StateManager", null), ENT_APP, ENT_APP_12, ENT_APP_20);
		addCheck(new CheckClass(this, JSFSeverityPreferences.INVALID_VARIABLE_RESOLVER, "variable-resolver", false, "javax.faces.el.VariableResolver", null), ENT_APP, ENT_APP_12, ENT_APP_20);
		addCheck(new CheckClass(this, JSFSeverityPreferences.INVALID_VIEW_HANDLER, "view-handler", false, "javax.faces.application.ViewHandler", null), ENT_APP, ENT_APP_12, ENT_APP_20);
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.jst.web.kb.internal.validation.ValidationErrorManager#getMarkerType()
	 */
	@Override
	public String getMarkerType() {
		return PROBLEM_TYPE;
	}

	public String getId() {
		return ID;
	}

	public String getBuilderId() {
		return null;
	}

	public IValidatingProjectTree getValidatingProjects(IProject project) {
		IProjectValidationContext rootContext = contexts.get(project);
		if(rootContext == null) {
			rootContext = new ProjectValidationContext();
			contexts.put(project, rootContext);
		}

		Set<IProject> projects = new HashSet<IProject>();
		projects.add(project);

		IValidatingProjectSet projectSet = new ValidatingProjectSet(project, projects, rootContext);
		return new SimpleValidatingProjectTree(projectSet);
	}

	public boolean shouldValidate(IProject project) {
		if(!project.isAccessible()) {
			return false;
		}

		try {
			IFacetedProject fp = ProjectFacetsManager.create(project);
			IProjectFacet f = ProjectFacetsManager.getProjectFacet("jst.web");
			if(fp != null && f != null && fp.getInstalledVersion(f) != null) {
				return true;
			}
		} catch (CoreException e) {
			WebModelPlugin.getDefault().logError(e);
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.jst.web.kb.internal.validation.ValidationErrorManager#init(org.eclipse.core.resources.IProject, org.jboss.tools.jst.web.kb.internal.validation.ContextValidationHelper, org.jboss.tools.jst.web.kb.validation.IProjectValidationContext, org.eclipse.wst.validation.internal.provisional.core.IValidator, org.eclipse.wst.validation.internal.provisional.core.IReporter)
	 */
	@Override
	public void init(IProject project, ContextValidationHelper validationHelper, IProjectValidationContext context, org.eclipse.wst.validation.internal.provisional.core.IValidator manager, IReporter reporter) {
		super.init(project, validationHelper, context, manager, reporter);
		projectName = project.getName();
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.jst.web.kb.validation.IValidator#validate(java.util.Set, org.eclipse.core.resources.IProject, org.jboss.tools.jst.web.kb.internal.validation.ContextValidationHelper, org.jboss.tools.jst.web.kb.validation.IProjectValidationContext, org.jboss.tools.jst.web.kb.internal.validation.ValidatorManager, org.eclipse.wst.validation.internal.provisional.core.IReporter)
	 */
	public IStatus validate(Set<IFile> changedFiles, IProject project,
			ContextValidationHelper validationHelper, IProjectValidationContext context, ValidatorManager manager,
			IReporter reporter) throws ValidationException {
		init(project, validationHelper, context, manager, reporter);

		for (IFile file: changedFiles) {
			if(file.getName().endsWith(XML_EXT)) {
				XModelObject o = EclipseResourceUtil.createObjectForResource(file);
				if(o != null && o.getModelEntity().getName().startsWith("FacesConfig")) {
					validateFile(o, file);
				}
			}
		}
		return OK_STATUS;
	}

	private void validateFile(XModelObject object, IFile file) {
		validateObject(object);
	}

	private void validateObject(XModelObject object) {
		String entity = object.getModelEntity().getName();
		Set<Check> ch = checks.get(entity);
		if(ch != null) {
			for (Check c: ch) {
				c.check(object);
			}
		}
		XModelObject[] cs = object.getChildren();
		for (XModelObject c: cs) {
			validateObject(c);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.jst.web.kb.validation.IValidator#validateAll(org.eclipse.core.resources.IProject, org.jboss.tools.jst.web.kb.internal.validation.ContextValidationHelper, org.jboss.tools.jst.web.kb.validation.IProjectValidationContext, org.jboss.tools.jst.web.kb.internal.validation.ValidatorManager, org.eclipse.wst.validation.internal.provisional.core.IReporter)
	 */
	public IStatus validateAll(IProject project,
			ContextValidationHelper validationHelper, IProjectValidationContext context, ValidatorManager manager,
			IReporter reporter) throws ValidationException {
		init(project, validationHelper, context, manager, reporter);
		displaySubtask(JSFValidationMessage.VALIDATING_PROJECT, new String[]{projectName});

		IPath webContentPath = WebUtils.getFirstWebContentPath(project);
		IFolder webInf = null;
		try {
			
			// This code line never return null
			webInf = project.getFolder(webContentPath.append("WEB-INF")); //$NON-NLS-1$
			// so never check it for null
			if(webInf.isAccessible()) {
				IResource[] rs = webInf.members();
				// exception is not required here because if esbContent is not exist control
				// never gets here
				for (IResource r: rs) {
					if(r instanceof IFile) {
						IFile file = (IFile)r;
						String name = file.getName();
						if(name.endsWith(XML_EXT)) {
							XModelObject o = EclipseResourceUtil.createObjectForResource(file);
							if(o != null) {
								String entity = o.getModelEntity().getName();
								if(entity.startsWith("FacesConfig")) {
									validateFile(o, file);
								} else if(entity.startsWith("FileWebApp")) {
									//TODO
								}
							}
						}
					}
				}
			}
		} catch (CoreException e) {
			// hiding exceptions is the evil so lets return EROOR Status with exception
			return new Status(IStatus.ERROR,WebModelPlugin.PLUGIN_ID,MessageFormat.format("Validation error for project {0}",project.getLocation().toString()),e);
		}
		
		return OK_STATUS;
	}

	

	public boolean isEnabled(IProject project) {
		return JSFSeverityPreferences.isValidationEnabled(project);
	}

	@Override
	protected String getPreferencePageId() {
		return PREFERENCE_PAGE_ID;
	}

}
