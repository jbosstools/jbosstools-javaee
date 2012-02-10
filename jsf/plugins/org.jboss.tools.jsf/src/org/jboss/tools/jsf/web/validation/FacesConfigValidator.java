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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IType;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.eclipse.wst.validation.internal.core.ValidationException;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.jboss.tools.common.EclipseUtil;
import org.jboss.tools.common.model.XModel;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.XModelObjectConstants;
import org.jboss.tools.common.model.filesystems.FileSystemsHelper;
import org.jboss.tools.common.model.impl.XModelImpl;
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
import org.jboss.tools.jsf.JSFModelPlugin;
import org.jboss.tools.jsf.model.JSFConstants;
import org.jboss.tools.jsf.model.pv.JSFProjectsTree;
import org.jboss.tools.jsf.web.JSFWebHelper;
import org.jboss.tools.jsf.web.JSFWebProject;
import org.jboss.tools.jsf.web.pattern.JSFUrlPattern;
import org.jboss.tools.jsf.web.validation.composite.CompositeComponentValidator;
import org.jboss.tools.jst.web.WebModelPlugin;
import org.jboss.tools.jst.web.kb.IKbProject;
import org.jboss.tools.jst.web.kb.KbProjectFactory;
import org.jboss.tools.jst.web.model.helpers.WebAppHelper;
import org.jboss.tools.jst.web.model.pv.WebProjectNode;
import org.jboss.tools.jst.web.validation.Check;
import org.jboss.tools.jst.web.validation.CheckClass;

/**
 * @author Viacheslav Kabanovich
 */
public class FacesConfigValidator extends ValidationErrorManager implements IValidator, JSFConstants {
	public static final String ID = "org.jboss.tools.esb.validator.ESBCoreValidator"; //$NON-NLS-1$
	public static final String PROBLEM_TYPE = "org.jboss.tools.jsf.facesconfigproblem"; //$NON-NLS-1$
	public static final String PREFERENCE_PAGE_ID = CompositeComponentValidator.PREFERENCE_PAGE_ID;

	public static String SHORT_ID = "jsf-verification"; //$NON-NLS-1$

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
		return JSFSeverityPreferences.getInstance().getProjectPreference(project, preferenceKey);
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.jst.web.kb.internal.validation.ValidationErrorManager#getMaxNumberOfMarkersPerFile(org.eclipse.core.resources.IProject)
	 */
	@Override
	public int getMaxNumberOfMarkersPerFile(IProject project) {
		return JSFSeverityPreferences.getMaxNumberOfProblemMarkersPerFile(project);
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

	static class JSFCheckClass extends CheckClass {

		public JSFCheckClass(ValidationErrorManager manager, String preference, String attr, boolean allowsPrimitive, String implementsType, String extendsType) {
			super(manager, preference, attr, allowsPrimitive, implementsType, extendsType);
		}

		protected String getShortId() {
			return SHORT_ID;
		}
		
	}

	void createChecks() {
		String ENT_APP = "JSFApplication", ENT_APP_12 = ENT_APP + SUFF_12, ENT_APP_20 = ENT_APP + SUFF_20;
		addCheck(new JSFCheckClass(this, JSFSeverityPreferences.INVALID_ACTION_LISTENER, "action-listener", false, "javax.faces.event.ActionListener", null), ENT_APP, ENT_APP_12, ENT_APP_20);
		addCheck(new JSFCheckClass(this, JSFSeverityPreferences.INVALID_NAVIGATION_HANDLER, "navigation-handler", false, "javax.faces.application.NavigationHandler", null), ENT_APP, ENT_APP_12, ENT_APP_20);
		addCheck(new JSFCheckClass(this, JSFSeverityPreferences.INVALID_PROPERTY_RESOLVER, "class name", false, "javax.faces.el.PropertyResolver", null).setVisualAttribute("property-resolver"), "JSFPropertyResolver");
		addCheck(new JSFCheckClass(this, JSFSeverityPreferences.INVALID_STATE_MANAGER, "state-manager", false, "javax.faces.application.StateManager", null), ENT_APP, ENT_APP_12, ENT_APP_20);
		addCheck(new JSFCheckClass(this, JSFSeverityPreferences.INVALID_VARIABLE_RESOLVER, "class name", false, null, "javax.el.ELResolver").setVisualAttribute("el-resolver"), "JSFELResolver");
		addCheck(new JSFCheckClass(this, JSFSeverityPreferences.INVALID_VARIABLE_RESOLVER, "class name", false, "javax.faces.el.VariableResolver", null).setVisualAttribute("variable-resolver"), "JSFVariableResolver");
		addCheck(new JSFCheckClass(this, JSFSeverityPreferences.INVALID_VIEW_HANDLER, "view-handler", false, "javax.faces.application.ViewHandler", null), ENT_APP, ENT_APP_12, ENT_APP_20);

		String ENT_COMPONENT = "JSFComponent", ENT_COMPONENT_11 = ENT_COMPONENT + SUFF_11;
		addCheck(new CheckClass(this, JSFSeverityPreferences.INVALID_COMPONENT_CLASS, "component-class", false, null, "javax.faces.component.UIComponent"), ENT_COMPONENT, ENT_COMPONENT_11);

		String ENT_CONVERTER = "JSFConverter";
		addCheck(new JSFCheckClass(this, JSFSeverityPreferences.INVALID_CONVERTER_CLASS, "converter-class", false, "javax.faces.convert.Converter", null), ENT_CONVERTER);
		addCheck(new JSFCheckClass(this, JSFSeverityPreferences.INVALID_CONVERTER_FOR_CLASS, "converter-for-class", true, null, null), ENT_CONVERTER);

		String ENT_FACTORY = "JSFFactory", ENT_FACTORY_20 = ENT_FACTORY + SUFF_20;
		addCheck(new JSFCheckClass(this, JSFSeverityPreferences.INVALID_APPLICATION_FACTORY, "application-factory", false, null, "javax.faces.application.ApplicationFactory"), ENT_FACTORY, ENT_FACTORY_20);
		addCheck(new JSFCheckClass(this, JSFSeverityPreferences.INVALID_FACES_CONTEXT_FACTORY, "faces-context-factory", false, null, "javax.faces.context.FacesContextFactory"), ENT_FACTORY, ENT_FACTORY_20);
		addCheck(new JSFCheckClass(this, JSFSeverityPreferences.INVALID_LIFECYCLE_FACTORY, "lifecycle-factory", false, null, "javax.faces.lifecycle.LifecycleFactory"), ENT_FACTORY, ENT_FACTORY_20);
		addCheck(new JSFCheckClass(this, JSFSeverityPreferences.INVALID_RENDER_KIT_FACTORY, "render-kit-factory", false, null, "javax.faces.render.RenderKitFactory"), ENT_FACTORY, ENT_FACTORY_20);

		String ENT_LIST_ENTRIES = "JSFListEntries", ENT_MAP_ENTRIES = "JSFMapEntries";
		addCheck(new JSFCheckClass(this, JSFSeverityPreferences.INVALID_KEY_CLASS, "key-class", true, null, null), ENT_MAP_ENTRIES);
		addCheck(new JSFCheckClass(this, JSFSeverityPreferences.INVALID_KEY_CLASS, "value-class", true, null, null), ENT_LIST_ENTRIES, ENT_MAP_ENTRIES);

		String ENT_MANAGED_BEAN = "JSFManagedBean", ENT_MANAGED_BEAN_20 = ENT_MANAGED_BEAN + SUFF_20;
		addCheck(new JSFCheckClass(this, JSFSeverityPreferences.INVALID_BEAN_CLASS, "managed-bean-class", false, null, null), ENT_MANAGED_BEAN, ENT_MANAGED_BEAN_20);
		String ENT_MANAGED_PROPERTY = "JSFManagedProperty";
		addCheck(new JSFCheckClass(this, JSFSeverityPreferences.INVALID_PROPERTY_CLASS, "property-class", false, null, null), ENT_MANAGED_PROPERTY);
		String ENT_REFERENCED_BEAN = "JSFReferencedBean";
		addCheck(new CheckClass(this, JSFSeverityPreferences.INVALID_BEAN_CLASS, "referenced-bean-class", false, null, null), ENT_REFERENCED_BEAN);

		String ENT_PHASE_LISTENER = "JSFPhaseListener";
		addCheck(new JSFCheckClass(this, JSFSeverityPreferences.INVALID_PHASE_LISTENER, "phase-listener", false, null, "javax.faces.event.PhaseListener"), ENT_PHASE_LISTENER);
		String ENT_RENDER_KIT = "JSFRenderKit", ENT_RENDER_KIT_11 = ENT_RENDER_KIT + SUFF_11, ENT_RENDER_KIT_12 = ENT_RENDER_KIT + SUFF_12, ENT_RENDER_KIT_20 = ENT_RENDER_KIT + SUFF_20;
		addCheck(new JSFCheckClass(this, JSFSeverityPreferences.INVALID_RENDER_KIT_CLASS, "render-kit-class", false, null, "javax.faces.render.RenderKit"), ENT_RENDER_KIT, ENT_RENDER_KIT_11, ENT_RENDER_KIT_12, ENT_RENDER_KIT_20);
		String ENT_RENDERER = "JSFRenderer", ENT_RENDERER_11 = ENT_RENDERER + SUFF_11;
		addCheck(new JSFCheckClass(this, JSFSeverityPreferences.INVALID_RENDERER_CLASS, "renderer-class", false, null, "javax.faces.render.Renderer"), ENT_RENDERER, ENT_RENDERER_11);
		String ENT_VALIDATOR = "JSFValidator", ENT_VALIDATOR_12 = ENT_VALIDATOR + SUFF_12;
		addCheck(new JSFCheckClass(this, JSFSeverityPreferences.INVALID_VALIDATOR_CLASS, "validator-class", false, null, "javax.faces.validator.Validator"), ENT_VALIDATOR, ENT_VALIDATOR_12);

		addCheck(new JSFCheckFromViewId(this), new String[]{ENT_NAVIGATION_CASE, ENT_NAVIGATION_CASE_20, ENT_NAVIGATION_RULE, ENT_NAVIGATION_RULE_20});
		addCheck(new JSFCheckToViewId(this), new String[]{ENT_NAVIGATION_CASE, ENT_NAVIGATION_CASE_20, ENT_NAVIGATION_RULE, ENT_NAVIGATION_RULE_20});
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
			IKbProject kb = KbProjectFactory.getKbProject(project, true);
			if(kb != null) {
				rootContext = kb.getValidationContext();
			} else {
				rootContext = new ProjectValidationContext();
			}
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

		Set<IPath> resourcesToClean = new HashSet<IPath>(); // Resource which we should remove from validation context
		for (IFile file: changedFiles) {
			resourcesToClean.add(file.getFullPath());
		}

		changedFiles = collectFiles(project, changedFiles, context);
		
		for(IFile file: changedFiles) {
			removeAllMessagesFromResource(file);
			resourcesToClean.add(file.getFullPath());
		}

		getValidationContext().removeLinkedCoreResources(SHORT_ID, resourcesToClean);

		for (IFile file: changedFiles) {
			if(file.getName().endsWith(XML_EXT)) {
				XModelObject o = EclipseResourceUtil.createObjectForResource(file);
				if(o != null) {
					String entity = o.getModelEntity().getName();
					if(entity.startsWith("FacesConfig")) {
						validateFile(o, file);
					} else if(entity.startsWith("FileWebApp")) {
						new CheckContextParam(this).check(o);
					}
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
			webInf = project.getFolder(webContentPath.removeFirstSegments(1).append("WEB-INF")); //$NON-NLS-1$
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
									new CheckContextParam(this).check(o);
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

	private Set<IFile> collectFiles(IProject project, Set<IFile> changedFiles, IProjectValidationContext context) {
		Set<IFile> files = new HashSet<IFile>();
		if(context == null) {
			files.addAll(changedFiles);
			return files;
		}
		Set<IFile> direct = new HashSet<IFile>();
		Set<IFile> dependent = new HashSet<IFile>();
		for (IFile f: changedFiles) {
			if(f != null && f.getProject() == project) {
				Set<IPath> paths = context.getCoreResourcesByVariableName(SHORT_ID, f.getFullPath().toOSString(), true);
				String name = f.getName();
				
				if(name.endsWith(".java")) {
					try {
						ICompilationUnit unit = EclipseUtil.getCompilationUnit(f);
						if(unit != null) {
							IType[] ts = unit.getTypes();
							for (IType t: ts) {
								String type = t.getFullyQualifiedName();
								Set<IPath> paths1 = context.getCoreResourcesByVariableName(SHORT_ID, type, true);
								if(paths1 != null) {
									if(paths != null) {
										paths.addAll(paths1);
									} else {
										paths = paths1;
									}
								}
							}
						}
					} catch (CoreException e) {
						JSFModelPlugin.getDefault().logError(e);
					}
				} else {
					IPath[] ps = WebUtils.getWebContentPaths(project);
					for (IPath rootPath: ps) {
						if(rootPath.isPrefixOf(f.getFullPath())) {
							String s = f.getFullPath().removeFirstSegments(rootPath.segmentCount()).toString();
							if(!s.startsWith("/")) s = "/" + s;
							Set<IPath> paths1 = context.getCoreResourcesByVariableName(SHORT_ID, s, true);
							if(paths1 != null) {
								if(paths != null) {
									paths.addAll(paths1);
								} else {
									paths = paths1;
								}
							}
						}
					}
				}

				if(name.endsWith(".xml") && f.exists()) { //$NON-NLS-1$
					if(!direct.contains(f) && !dependent.contains(f)) {
						files.add(f);
					}
					direct.add(f);
					dependent.remove(f);
				}

				if(paths != null) {
					for (IPath path: paths) {
						IFile f1 = project.getParent().getFile(path);
						if(f1.exists()) {
							if(direct.contains(f1) || dependent.contains(f1)) continue;
							dependent.add(f1);
							files.add(f1);
						}
					}
				}
			}
		}

		return files;
	}

}

class JSFCheckFromViewId extends Check {

	public JSFCheckFromViewId(ValidationErrorManager manager) {
		super(manager, JSFSeverityPreferences.INVALID_FROM_VIEW_ID, JSFConstants.ATT_FROM_VIEW_ID);
	}
	
	public void check(XModelObject object) {			
		String value = object.getAttributeValue(attr);
		if(value == null) {
			return;
		}
		if(value != null && value.length() > 0 && !value.startsWith("*") && !value.startsWith("/")) {
			fireMessage(object, NLS.bind(JSFValidationMessage.VIEW_ID_NO_SLASH, attr));
		}
	}
}

class JSFCheckToViewId extends Check {

	public JSFCheckToViewId(ValidationErrorManager manager) {
		super(manager, JSFSeverityPreferences.INVALID_TO_VIEW_ID, JSFConstants.ATT_TO_VIEW_ID);
	}
	
	public void check(XModelObject object) {			
		String value = object.getAttributeValue(attr);
		if(value == null) {
			return;
		}
		if(value.length() == 0) {
			fireMessage(object, NLS.bind(JSFValidationMessage.TO_VIEW_ID_EMPTY, attr));
		} else if(!value.startsWith("/")) {
			fireMessage(object, NLS.bind(JSFValidationMessage.VIEW_ID_NO_SLASH, attr));
		} else if(value.indexOf("*") >= 0) {
			fireMessage(object, NLS.bind(JSFValidationMessage.TO_VIEW_ID_STAR, attr));
		} else {
			checkEsists(object, value);
		}
	}

	void checkEsists(XModelObject object, String value) {
		if(value.indexOf('?') >= 0) {
			value = value.substring(0, value.indexOf('?'));
		}
		XModel model = object.getModel();
		XModelObject o = model.getByPath(value);
		if(o == null) {
			JSFUrlPattern pattern = JSFWebProject.getInstance(model).getUrlPattern();
			if(pattern != null && pattern.isJSFUrl(value)) {
				value = pattern.getJSFPath(value);
				o = model.getByPath(value);
			}
		}

		IFile f = (IFile)object.getAdapter(IFile.class);
		if(f != null) {
			IProjectValidationContext context = manager.getValidationContext();
			if(context != null) {
				context.addLinkedCoreResource(FacesConfigValidator.SHORT_ID, value, f.getFullPath(), true);
			}
		}

		if(o != null) {
			IFile f2 = (IFile)o.getAdapter(IFile.class);
			if(f2 != null) {
				String path = f2.getLocation().toOSString().replace('\\', '/');
				IProjectValidationContext context = manager.getValidationContext();
				if(context != null) {
					context.addLinkedCoreResource(FacesConfigValidator.SHORT_ID, f2.getFullPath().toOSString(), f.getFullPath(), true);
				}
				if(path.endsWith(value)) {
					return;
				}
			}
		} else if(checkTiles(model, value)) {
			return;
		}
		fireMessage(object, NLS.bind(JSFValidationMessage.VIEW_NOT_EXISTS, attr, value));
	}
	
	private boolean checkTiles(XModel model, String path) {
		XModelObject root = JSFProjectsTree.getProjectsRoot(model);
		if(root == null) return false;
		XModelObject tiles = root.getChildByPath("Tiles"); //$NON-NLS-1$
		if(tiles == null) return false;
		XModelObject[] ts = ((WebProjectNode)tiles).getTreeChildren();
		if(ts.length == 0) return false;
		int d = path.lastIndexOf('.');
		if(d < 0) return false;
		String tileName = path.substring(0, d + 1) + "tiles"; //$NON-NLS-1$
		tileName = tileName.replace('/', '#');
		for (int i = 0; i < ts.length; i++) {
			if(ts[i].getChildByPath(tileName) != null) return true;
		}
		return false;
	}
}

class CheckContextParam extends Check {
	static String CONFIG_FILES_PARAM = JSFWebHelper.FACES_CONFIG_DATA.param;

	public CheckContextParam(ValidationErrorManager manager) {
		super(manager, JSFSeverityPreferences.INVALID_CONFIG_FILES, "param-value");
	}
	
	public void check(XModelObject webapp) {
		XModelObject object = WebAppHelper.findWebAppContextParam(webapp, CONFIG_FILES_PARAM);
		if(object == null) return;
//		if(!CONFIG_FILES_PARAM.equals(object.getAttributeValue("param-name"))) return; //$NON-NLS-1$
		String value = object.getAttributeValue("param-value"); //$NON-NLS-1$
		if(value == null || value.length() == 0) return;
		XModel model = object.getModel();
		List<XModelObject> webRoots = new ArrayList<XModelObject>();
		XModelObject[] fss = FileSystemsHelper.getFileSystems(model).getChildren();
		for (XModelObject s: fss) {
			String n = s.getAttributeValue(XModelObjectConstants.ATTR_NAME);
			if("WEB-ROOT".equals(n) || n.startsWith("WEB-ROOT-")) {
				webRoots.add(s);
			}
		}
		if(webRoots.isEmpty()) return;
		StringTokenizer st = new StringTokenizer(value, ","); //$NON-NLS-1$
		while(st.hasMoreTokens()) {
			String path = st.nextToken().trim();
			if(path.length() == 0) continue;
			XModelObject fc = XModelImpl.getByRelativePath(model, path);
			if(fc == null) {
				fireMessage(object, NLS.bind(JSFValidationMessage.INVALID_FACES_CONFIG_REFERENCE, "param-value", path));
				return;
			}
			String path2 = path.startsWith("/") ? path.substring(1) : path; //$NON-NLS-1$
			XModelObject fc2 = null;
			for (XModelObject s: webRoots) {
				fc2 = s.getChildByPath(path2);
				if(fc2 != null) break;
			}
			if(fc2 == null) {
				fireMessage(object, NLS.bind(JSFValidationMessage.INVALID_FACES_CONFIG_REFERENCE, "param-value", path));
				return;
			}
			if(!fc2.getModelEntity().getName().startsWith("FacesConfig")) { //$NON-NLS-1$
				fireMessage(object, NLS.bind(JSFValidationMessage.INVALID_FACES_CONFIG_REFERENCE, "param-value", path));
				return;
			}
		}		
	}
}
