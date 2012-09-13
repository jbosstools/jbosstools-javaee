/******************************************************************************* 
 * Copyright (c) 2009 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.cdi.internal.core.validation;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeParameter;
import org.eclipse.jdt.core.JavaConventions;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jface.text.IRegion;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualFile;
import org.eclipse.wst.validation.internal.core.ValidationException;
import org.eclipse.wst.validation.internal.operations.WorkbenchReporter;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.eclipse.wst.validation.internal.provisional.core.IValidationContext;
import org.eclipse.wst.validation.internal.provisional.core.IValidator;
import org.jboss.tools.cdi.core.CDIConstants;
import org.jboss.tools.cdi.core.CDICoreBuilder;
import org.jboss.tools.cdi.core.CDICoreNature;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.core.CDIUtil;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.IBeanMethod;
import org.jboss.tools.cdi.core.ICDIAnnotation;
import org.jboss.tools.cdi.core.ICDIProject;
import org.jboss.tools.cdi.core.IClassBean;
import org.jboss.tools.cdi.core.IDecorator;
import org.jboss.tools.cdi.core.IInitializerMethod;
import org.jboss.tools.cdi.core.IInjectionPoint;
import org.jboss.tools.cdi.core.IInjectionPointField;
import org.jboss.tools.cdi.core.IInjectionPointParameter;
import org.jboss.tools.cdi.core.IInterceptor;
import org.jboss.tools.cdi.core.IInterceptorBinded;
import org.jboss.tools.cdi.core.IInterceptorBinding;
import org.jboss.tools.cdi.core.IInterceptorBindingDeclaration;
import org.jboss.tools.cdi.core.IParameter;
import org.jboss.tools.cdi.core.IProducer;
import org.jboss.tools.cdi.core.IProducerField;
import org.jboss.tools.cdi.core.IProducerMethod;
import org.jboss.tools.cdi.core.IQualifier;
import org.jboss.tools.cdi.core.IQualifierDeclaration;
import org.jboss.tools.cdi.core.IScope;
import org.jboss.tools.cdi.core.IScopeDeclaration;
import org.jboss.tools.cdi.core.ISessionBean;
import org.jboss.tools.cdi.core.IStereotype;
import org.jboss.tools.cdi.core.IStereotypeDeclaration;
import org.jboss.tools.cdi.core.IStereotyped;
import org.jboss.tools.cdi.core.extension.feature.IBeanKeyProvider;
import org.jboss.tools.cdi.core.extension.feature.IInjectionPointValidatorFeature;
import org.jboss.tools.cdi.core.extension.feature.IValidatorFeature;
import org.jboss.tools.cdi.core.preferences.CDIPreferences;
import org.jboss.tools.cdi.internal.core.impl.CDIProject;
import org.jboss.tools.cdi.internal.core.impl.CDIProjectAsYouType;
import org.jboss.tools.cdi.internal.core.impl.SessionBean;
import org.jboss.tools.cdi.internal.core.impl.definition.Dependencies;
import org.jboss.tools.common.EclipseUtil;
import org.jboss.tools.common.java.IAnnotationDeclaration;
import org.jboss.tools.common.java.IJavaReference;
import org.jboss.tools.common.java.IParametedType;
import org.jboss.tools.common.java.ITypeDeclaration;
import org.jboss.tools.common.java.ParametedType;
import org.jboss.tools.common.model.util.EclipseJavaUtil;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.common.text.INodeReference;
import org.jboss.tools.common.text.ITextSourceReference;
import org.jboss.tools.common.validation.ContextValidationHelper;
import org.jboss.tools.common.validation.EditorValidationContext;
import org.jboss.tools.common.validation.IJavaElementValidator;
import org.jboss.tools.common.validation.IProjectValidationContext;
import org.jboss.tools.common.validation.IStringValidator;
import org.jboss.tools.common.validation.ITypedReporter;
import org.jboss.tools.common.validation.IValidatingProjectSet;
import org.jboss.tools.common.validation.IValidatingProjectTree;
import org.jboss.tools.common.validation.ValidationUtil;
import org.jboss.tools.common.validation.ValidatorManager;
import org.jboss.tools.jst.web.kb.internal.validation.KBValidator;

/**
 * @author Alexey Kazakov
 */
public class CDICoreValidator extends CDIValidationErrorManager implements IJavaElementValidator, IStringValidator {
	public static final String ID = "org.jboss.tools.cdi.core.CoreValidator"; //$NON-NLS-1$
	public static final String PROBLEM_TYPE = "org.jboss.tools.cdi.core.cdiproblem"; //$NON-NLS-1$
	public static final String PREFERENCE_PAGE_ID = "org.jboss.tools.cdi.ui.preferences.CDIValidatorPreferencePage"; //$NON-NLS-1$

	ICDIProject rootCdiProject;
	Map<IProject, CDIValidationContext> cdiContexts = new HashMap<IProject, CDIValidationContext>();
	String rootProjectName;
	IValidatingProjectTree projectTree;
	IValidatingProjectSet projectSet;
	Set<IFolder> sourceFolders;
	List<IFile> allBeansXmls;

	private BeansXmlValidationDelegate beansXmlValidator = new BeansXmlValidationDelegate(this);
	private AnnotationValidationDelegate annotationValidator = new AnnotationValidationDelegate(this);

	public static final String SHORT_ID = "jboss.cdi.core"; //$NON-NLS-1$

	public static class CDIValidationContext {
		private ICDIProject cdiProject;
		private IProject project;
		private Dependencies dependencies;
		private Set<IValidatorFeature> extensions;
		private Set<IInjectionPointValidatorFeature> injectionValidationFeatures;

		public CDIValidationContext(IProject project, ICDIProject cdiProject) {
			this.project = project;
			this.cdiProject = cdiProject;
			dependencies = new Dependencies();
			extensions = Collections.emptySet();
			injectionValidationFeatures = Collections.emptySet();
		}

		public CDIValidationContext(IProject project) {
			this.project = project;
			CDICoreNature nature = CDICorePlugin.getCDI(project, true);
			cdiProject =  nature.getDelegate();
			dependencies = nature.getDefinitions().getAllDependencies();
			extensions = nature.getExtensionManager().getValidatorFeatures();
			injectionValidationFeatures = nature.getExtensionManager().getFeatures(IInjectionPointValidatorFeature.class);
		}

		/**
		 * @return the cdiProject
		 */
		public ICDIProject getCdiProject() {
			return cdiProject;
		}

		/**
		 * @return the project
		 */
		public IProject getProject() {
			return project;
		}

		/**
		 * @return the dependencies
		 */
		public Dependencies getDependencies() {
			return dependencies;
		}

		/**
		 * @return the extensions
		 */
		public Set<IValidatorFeature> getExtensions() {
			return extensions;
		}

		/**
		 * @return the injection validation features
		 */
		public Set<IInjectionPointValidatorFeature> getInjectionValidationFeatures() {
			return injectionValidationFeatures;
		}
	}

	CDIValidationContext getCDIContext(IResource resource) {
		IProject project = resource.getProject();
		CDIValidationContext context = cdiContexts.get(project);
		if(context==null) {
			context = new CDIValidationContext(project);
			cdiContexts.put(project, context);
		}
		return context;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.jst.web.kb.internal.validation.ValidationErrorManager#getMarkerType()
	 */
	@Override
	public String getMarkerType() {
		return PROBLEM_TYPE;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jboss.tools.jst.web.kb.validation.IValidator#getId()
	 */
	public String getId() {
		return ID;
	}

	public String getBuilderId() {
		return CDICoreBuilder.BUILDER_ID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jboss.tools.jst.web.kb.validation.IValidator#getValidatingProjects
	 * (org.eclipse.core.resources.IProject)
	 */
	public IValidatingProjectTree getValidatingProjects(IProject project) {
		projectTree = getProjectTree(project);
		return projectTree;
	}

	public static IValidatingProjectTree getProjectTree(IProject project) {
		return new CDIProjectTree(project);
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.jst.web.kb.validation.IValidator#isEnabled(org.eclipse.core.resources.IProject)
	 */
	public boolean isEnabled(IProject project) {
		return CDIPreferences.isValidationEnabled(project);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.jboss.tools.jst.web.kb.validation.IValidator#shouldValidate(org.eclipse
	 * .core.resources.IProject)
	 */
	public boolean shouldValidate(IProject project) {
		try {
			return project.isAccessible() 
					&& project.hasNature(CDICoreNature.NATURE_ID) 
					&& validateBuilderOrder(project)
					&& isEnabled(project);
		} catch (CoreException e) {
			CDICorePlugin.getDefault().logError(e);
		}
		return false;
	}

	private boolean validateBuilderOrder(IProject project) throws CoreException {
		return KBValidator.validateBuilderOrder(project, getBuilderId(), getId(), CDIPreferences.getInstance());
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.jst.web.kb.internal.validation.ValidationErrorManager#init(org.eclipse.core.resources.IProject, org.jboss.tools.jst.web.kb.internal.validation.ContextValidationHelper, org.jboss.tools.jst.web.kb.validation.IProjectValidationContext, org.eclipse.wst.validation.internal.provisional.core.IValidator, org.eclipse.wst.validation.internal.provisional.core.IReporter)
	 */
	@Override
	public void init(IProject rootProject, ContextValidationHelper validationHelper, IProjectValidationContext context, org.eclipse.wst.validation.internal.provisional.core.IValidator manager,
			IReporter reporter) {
		super.init(rootProject, validationHelper, context, manager, reporter);
		setAsYouTypeValidation(false);
		validatatingAll = false;
		projectTree = validationHelper.getValidationContextManager().getValidatingProjectTree(this);
		projectSet = projectTree.getBrunches().get(rootProject);
		rootCdiProject = null;
		allBeansXmls = null;
		CDICoreNature nature = CDICorePlugin.getCDI(projectSet.getRootProject(), true);
		if(nature!=null) {
			rootCdiProject =  nature.getDelegate();
			if(rootCdiProject==null) {
				CDICorePlugin.getDefault().logError("Trying to validate " + rootProject + " but CDI Tools model for the project is not built.");
			}
		} else {
			CDICorePlugin.getDefault().logError("Trying to validate " + rootProject + " but there is no CDI Nature in the project.");
		}
		rootProjectName = projectSet.getRootProject().getName();
		cdiContexts.clear();
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.jst.web.kb.validation.IValidator#validate(java.util.Set, org.eclipse.core.resources.IProject, org.jboss.tools.jst.web.kb.internal.validation.ContextValidationHelper, org.jboss.tools.jst.web.kb.validation.IProjectValidationContext, org.jboss.tools.jst.web.kb.internal.validation.ValidatorManager, org.eclipse.wst.validation.internal.provisional.core.IReporter)
	 */
	public IStatus validate(Set<IFile> changedFiles, IProject project, ContextValidationHelper validationHelper, IProjectValidationContext context, ValidatorManager manager, IReporter reporter)
			throws ValidationException {
		init(project, validationHelper, context, manager, reporter);
		displaySubtask(CDIValidationMessages.SEARCHING_RESOURCES, new String[]{project.getName()});
		if (rootCdiProject == null) {
			return OK_STATUS;
		}
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		Set<IPath> resources = new HashSet<IPath>(); // Resources which we have
														// to validate.
		Set<IPath> resourcesToClean = new HashSet<IPath>(); // Resource which we should remove from validation context
		for(IFile file: changedFiles) {
			resourcesToClean.add(file.getFullPath());
			Set<IPath> dd = getCDIContext(file).getDependencies().getDirectDependencies(file.getFullPath());
			if(dd != null) {
				for (IPath p: dd) {
					IFile f = root.getFile(p);
					if(f.exists() && !changedFiles.contains(f)) {
						resources.add(p);
						collectAllRelatedInjections(f, resources);
					}
				}
			}
		}

		for (IFile currentFile : changedFiles) {
			if (reporter.isCancelled()) {
				break;
			}
			if (ValidationUtil.checkFileExtensionForJavaAndXml(currentFile)) {
				resources.add(currentFile.getFullPath());

				Set<String> newElNamesOfChangedFile = getELNamesByResource(getCDIContext(currentFile), currentFile.getFullPath());
				for (String newElName : newElNamesOfChangedFile) {
					// Collect resources that had EL names (in previous validation session) declared in this changed resource.
					Set<IPath> linkedResources = validationContext.getCoreResourcesByVariableName(SHORT_ID, newElName, true);
					if(linkedResources!=null) {
						resources.addAll(linkedResources);
					}
				}
				// Get old EL names which were linked with this resource in previous validation session.
				Set<String> oldElNamesOfChangedFile = validationContext.getVariableNamesByCoreResource(SHORT_ID, currentFile.getFullPath(), true);
				if(oldElNamesOfChangedFile!=null) {
					for (String name : oldElNamesOfChangedFile) {
						Set<IPath> linkedResources = validationContext.getCoreResourcesByVariableName(SHORT_ID, name, true);
						if(linkedResources!=null) {
							resources.addAll(linkedResources);
						}
						// Save old (from previous validation session) EL names. We need to validate all the resources which use this old EL name in case the name has been changed.
						validationContext.addVariableNameForELValidation(SHORT_ID, name);
					}
				}

				// Get all the paths of related resources for given file. These
				// links were saved in previous validation process.
				Set<String> oldReletedResources = getValidationContext().getVariableNamesByCoreResource(SHORT_ID, currentFile.getFullPath(), false);
				if (oldReletedResources != null) {
					for (String resourcePath : oldReletedResources) {
						if(resourcePath.startsWith("/")) {
							resources.add(Path.fromOSString(resourcePath));
						}
					}
				}

				collectAllRelatedInjections(currentFile, resources);
			}
		}

		Set<IFile> filesToValidate = new HashSet<IFile>();
		for (IPath linkedResource : resources) {
			IFile file = root.getFile(linkedResource);
			if(shouldBeValidated(file)) {
				IProject pr = file.getProject();
				if(!validationHelper.getValidationContextManager().projectHasBeenValidated(this, pr)) {
					filesToValidate.add(file);
				}
			}
		}

		// Validate all collected linked resources.
		// Remove all links between collected resources because they will be
		// linked again during validation.
		resourcesToClean.addAll(resources);
		getValidationContext().removeLinkedCoreResources(SHORT_ID, resourcesToClean);

		// We should remove markers from the source files at first
		for(IFile file: filesToValidate) {
			removeAllMessagesFromResource(file);
		}
		// Then we can validate them
		for (IFile file : filesToValidate) {
			validateResource(file);
		}

		cleanSavedMarkers();
		return OK_STATUS;
	}

	private boolean validatatingAll;

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.jst.web.kb.validation.IValidator#validateAll(org.eclipse.core.resources.IProject, org.jboss.tools.jst.web.kb.internal.validation.ContextValidationHelper, org.jboss.tools.jst.web.kb.validation.IProjectValidationContext, org.jboss.tools.jst.web.kb.internal.validation.ValidatorManager, org.eclipse.wst.validation.internal.provisional.core.IReporter)
	 */
	public IStatus validateAll(IProject project, ContextValidationHelper validationHelper, IProjectValidationContext context, ValidatorManager manager, IReporter reporter)
			throws ValidationException {
		init(project, validationHelper, context, manager, reporter);
		validatatingAll = true;

		if (rootCdiProject == null) {
			return OK_STATUS;
		}

		displaySubtask(CDIValidationMessages.VALIDATING_PROJECT, new String[] {rootProjectName});

		Set<IFile> filesToValidate = new HashSet<IFile>();

		IBean[] beans = rootCdiProject.getBeans();
		for (IBean bean : beans) {
			IResource resource = bean.getResource();
			if(resource!=null && shouldValidateType(bean.getBeanClass()) && notValidatedYet(resource)) {
				filesToValidate.add((IFile)resource);
			}
		}

		IStereotype[] stereotypes = rootCdiProject.getStereotypes();
		for (IStereotype stereotype : stereotypes) {
			IResource resource = stereotype.getResource();
			if(shouldValidateResourceOfElement(resource) && notValidatedYet(resource)) {
				filesToValidate.add((IFile)resource);
			}
		}

		IQualifier[] qualifiers = rootCdiProject.getQualifiers();
		for (IQualifier qualifier : qualifiers) {
			IResource resource = qualifier.getResource();
			if(shouldValidateResourceOfElement(resource) && notValidatedYet(resource)) {
				filesToValidate.add((IFile)resource);
			}
		}

		IInterceptorBinding[] bindings = rootCdiProject.getInterceptorBindings();
		for (IInterceptorBinding binding : bindings) {
			IResource resource = binding.getResource();
			if(shouldValidateResourceOfElement(resource) && notValidatedYet(resource)) {
				filesToValidate.add((IFile)resource);
			}
		}

		for (String scopeName: rootCdiProject.getScopeNames()) {
			IScope scope = rootCdiProject.getScope(scopeName);
			IResource resource = scope.getResource();
			if(shouldValidateResourceOfElement(resource) && notValidatedYet(resource)) {
				filesToValidate.add((IFile)resource);
			}
		}

		List<IFile> beansXmls = getAllBeansXmls();
		for (IFile beansXml : beansXmls) {
			if(notValidatedYet(beansXml)) {
				filesToValidate.add(beansXml);
			}
		}

		// We should remove markers from the source files at first
		for(IFile file: filesToValidate) {
			removeAllMessagesFromResource(file);
		}
		for (IFile file : filesToValidate) {
			validateResource(file);
		}

		cleanSavedMarkers();
		return OK_STATUS;
	}

	/**
	 * Removes all the validation problems created by this validator
	 * @param project
	 */
	public static void cleanProject(IProject project) {
		WorkbenchReporter.removeAllMessages(project, new String[]{CDICoreValidator.class.getName()}, null);
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.common.validation.IAsYouTypeValidator#validate(org.eclipse.wst.validation.internal.provisional.core.IValidator, org.eclipse.core.resources.IProject, org.eclipse.jface.text.IRegion, org.eclipse.wst.validation.internal.provisional.core.IValidationContext, org.eclipse.wst.validation.internal.provisional.core.IReporter, org.jboss.tools.common.validation.EditorValidationContext, org.jboss.tools.common.validation.IProjectValidationContext, org.eclipse.core.resources.IFile)
	 */
	@Override
	public void validate(IValidator validatorManager, IProject rootProject, Collection<IRegion> dirtyRegions, IValidationContext helper, IReporter reporter, EditorValidationContext validationContext, IProjectValidationContext projectContext, final IFile file) {
		ContextValidationHelper validationHelper = new ContextValidationHelper();
		validationHelper.setProject(rootProject);
		validationHelper.setValidationContextManager(validationContext);
		init(rootProject, validationHelper, projectContext, validatorManager, reporter);
		setAsYouTypeValidation(true);
		this.document = validationContext.getDocument();
		if(rootCdiProject == null) {
			return;
		}
		rootCdiProject = new CDIProjectAsYouType(rootCdiProject, file);
		validateResource(file);
		if(reporter instanceof ITypedReporter) {
			((ITypedReporter)reporter).addTypeForFile(getMarkerType());
		}
		disableProblemAnnotations(new ITextSourceReference() {
			@Override
			public int getStartPosition() {
				return 0;
			}

			@Override
			public IResource getResource() {
				return file;
			}

			@Override
			public int getLength() {
				return document.getLength();
			}
		}, reporter);
	}

	/**
	 * Validates a resource.
	 * 
	 * @param file
	 */
	private void validateResource(IFile file) {
		if (reporter.isCancelled() || !file.isAccessible()) {
			return;
		}
		displaySubtask(CDIValidationMessages.VALIDATING_RESOURCE, new String[] {file.getProject().getName(), file.getName()});

		if(!isAsYouTypeValidation()) {
			coreHelper.getValidationContextManager().addValidatedProject(this, file.getProject());
	
			Set<IPath> dd = getCDIContext(file).getDependencies().getDirectDependencies(file.getFullPath());
			if(dd != null && !dd.isEmpty()) {
				Set<IPath> resources = new HashSet<IPath>();
				for (IPath p: dd) {
					IFile f = ResourcesPlugin.getWorkspace().getRoot().getFile(p);
					if(f.exists()) {
						resources.add(p);
						collectAllRelatedInjections(f, resources);
					}
				}
				for (IPath p: resources) {
					getValidationContext().addLinkedCoreResource(SHORT_ID, p.toOSString(), file.getFullPath(), false);
				}					
			}
		}

		CDIValidationContext context = null;
		ICDIProject cdiProject = null;
		if(isAsYouTypeValidation()) {
			context = new CDIValidationContext(file.getProject(), rootCdiProject);
			cdiProject = rootCdiProject;
		} else {
			context = getCDIContext(file);
			cdiProject = context.getCdiProject();
		}
		if("beans.xml".equalsIgnoreCase(file.getName()) && CDIPreferences.shouldValidateBeansXml(file.getProject())) {
			List<IFile> allBaensXmls = getAllBeansXmls();
			if(allBaensXmls.contains(file)) { // See https://issues.jboss.org/browse/JBIDE-10166
				beansXmlValidator.validateBeansXml(context, file);
			}
		} else {
			Collection<IBean> beans = cdiProject.getBeans(file.getFullPath());
			for (IBean bean : beans) {
				validateBean(context, bean);
			}
			IStereotype stereotype = cdiProject.getStereotype(file.getFullPath());
			validateStereotype(stereotype);

			IQualifier qualifier = cdiProject.getQualifier(file.getFullPath());
			validateQualifier(qualifier);

			IScope scope = cdiProject.getScope(file.getFullPath());
			annotationValidator.validateScopeType(scope);

			IInterceptorBinding binding = cdiProject.getInterceptorBinding(file.getFullPath());
			validateInterceptorBinding(binding);
		}
		Set<IValidatorFeature> extensions = context.getExtensions();
		for (IValidatorFeature v: extensions) {
			setSeverityPreferences(v.getSeverityPreferences());
			v.validateResource(file, this);
			setSeverityPreferences(null);
		}
	}

	Set<IFolder> getSourceFoldersForProjectsSet() {
		if(sourceFolders==null) {
			sourceFolders = new HashSet<IFolder>();
			Set<IProject> projects = projectSet.getAllProjects();
			for (IProject project : projects) {
				sourceFolders.addAll(EclipseResourceUtil.getSourceFolders(project));
			}
		}
		return sourceFolders;
	}

	/**
	 * Returns all the beans.xml from META-INF and WEB-INF folders
	 * 
	 * @return
	 */
	private List<IFile> getAllBeansXmls() {
		if(allBeansXmls==null) {
			allBeansXmls = new ArrayList<IFile>();
			// From source folders
			Set<IFolder> sourceFolders = getSourceFoldersForProjectsSet();
			for (IFolder source : sourceFolders) {
				IResource beansXml = source.findMember(new Path("/META-INF/beans.xml")); //$NON-NLS-1$
				if(beansXml instanceof IFile) {
					allBeansXmls.add((IFile)beansXml);
				}
			}
			Set<IProject> allProjects = projectSet.getAllProjects();
			for (IProject project : allProjects) {
				// From WEB-INF folder
				IVirtualComponent com = ComponentCore.createComponent(project);
				if(com!=null) {
					IVirtualFile beansXml = com.getRootFolder().getFile(new Path("/WEB-INF/beans.xml")); //$NON-NLS-1$
					if(beansXml!=null && beansXml.getUnderlyingFile().isAccessible()) {
						allBeansXmls.add(beansXml.getUnderlyingFile());
					}
				}
			}
		}
		return allBeansXmls;
	}

	/**
	 * Validates a bean.
	 * 
	 * @param bean
	 */
	private void validateBean(CDIValidationContext context, IBean bean) {
		if (reporter.isCancelled()) {
			return;
		}
		if(!bean.exists() || !shouldValidateType(bean.getBeanClass())) {
			return;
		}
		String beanPath = null;
		if(!isAsYouTypeValidation()) {
			beanPath = bean.getSourcePath().toOSString();
			Collection<IScopeDeclaration> scopeDeclarations = bean.getScopeDeclarations();
			for (IScopeDeclaration scopeDeclaration : scopeDeclarations) {
				IScope scope = scopeDeclaration.getScope();
				if (shouldValidateType(scope.getSourceType())) {
					getValidationContext().addLinkedCoreResource(SHORT_ID, beanPath, scope.getSourcePath(), false);
				}
			}
			addLinkedStereotypes(beanPath, bean);
			Collection<IQualifierDeclaration> qualifierDeclarations = bean.getQualifierDeclarations();
			for (IQualifierDeclaration qualifierDeclaration : qualifierDeclarations) {
				IQualifier qualifier = qualifierDeclaration.getQualifier();
				if (shouldValidateType(qualifier.getSourceType())) {
					getValidationContext().addLinkedCoreResource(SHORT_ID, beanPath, qualifier.getSourcePath(), false);
				}
			}
		}

		// validate
		validateTyped(bean);
		validateBeanScope(bean);
		validateNormalBeanScope(bean);

		if (bean instanceof IProducer) {
			validateProducer(context, (IProducer) bean);
		}

		Collection<IInjectionPoint> points = bean instanceof IClassBean? ((IClassBean)bean).getInjectionPoints(false):bean.getInjectionPoints();
		for (IInjectionPoint point : points) {
			if(!isAsYouTypeValidation()) {
				IType type = getTypeOfInjection(point);
				if(type!=null && !type.isBinary()) {
					getValidationContext().addLinkedCoreResource(SHORT_ID, beanPath, type.getPath(), false);
				}
			}
			if(point.exists()) {
				validateInjectionPoint(context, point);
			}
		}

		if (bean instanceof IInterceptor) {
			validateInterceptor((IInterceptor) bean);
		}
		if (bean instanceof IDecorator) {
			validateDecorator(context, (IDecorator) bean);
		}
		if (bean instanceof IClassBean) {
			IClassBean classBean = (IClassBean)bean;
			if(!isAsYouTypeValidation()) {
				addLinkedInterceptorBindings(beanPath, classBean);
				Collection<IBeanMethod> methods = classBean.getAllMethods();
				for (IBeanMethod method : methods) {
					addLinkedStereotypes(beanPath, method);
					addLinkedInterceptorBindings(beanPath, method);
				}
			}
			validateClassBean(classBean);
		}

		validateSpecializingBean(bean);

		validateBeanName(context, bean);
	}

	/**
	 * Validates a bean EL name.
	 * 
	 * @param bean
	 */
	private void validateBeanName(CDIValidationContext context, IBean bean) {
		String name = bean.getName();
		if(name!=null && !name.startsWith("/")) {
			if(!isAsYouTypeValidation()) {
				// Collect all relations between the bean and other CDI elements.
				getValidationContext().addVariableNameForELValidation(SHORT_ID, name);
				getValidationContext().addLinkedCoreResource(SHORT_ID, name, bean.getSourcePath(), true);
			}
			/*
			 *	5.3.1. Ambiguous EL names
			 *	- All unresolvable ambiguous EL names are detected by the container when the application is initialized.
			 *    Suppose two beans are both available for injection in a certain war, and either:
			 *     • the two beans have the same EL name and the name is not resolvable, or
			 */
			Collection<IBean> beans = context.getCdiProject().getBeans(name, true);
			if(beans.size()>1 && beans.contains(bean)) {
				// We need to sort bean element names to make sure we report the same problem message for the same bean name for every validation process.
				IBean[] sortedBeans = beans.toArray(new IBean[beans.size()]);
				Arrays.sort(sortedBeans, new Comparator<IBean>() {
					@Override
					public int compare(IBean o1, IBean o2) {
						return o1.getElementName().compareTo(o2.getElementName());
					}
				});
				ITextSourceReference reference = bean.getNameLocation(true);
				Set<String> names = new HashSet<String>();
				String bName = bean.getElementName();
				names.add(bName);
				StringBuffer sb = new StringBuffer(bName);
				for (IBean iBean : sortedBeans) {
					if(!isAsYouTypeValidation()) {
						getValidationContext().addLinkedCoreResource(SHORT_ID, name, iBean.getSourcePath(), true);
					}
					bName = iBean.getElementName();
					if(bean!=iBean && !names.contains(bName)) {
						names.add(bName);
						sb.append(", ").append(bName);
					}
				}
				addProblem(MessageFormat.format(CDIValidationMessages.DUPLCICATE_EL_NAME, sb.toString()), CDIPreferences.AMBIGUOUS_EL_NAMES, reference, bean.getResource());
			} else {
				/*
				 *     • the EL name of one bean is of the form x.y, where y is a valid bean EL name, and x is the EL name of the other bean,
				 *       the container automatically detects the problem and treats it as a deployment problem. 
				 */
				if(name.indexOf('.')>0) {
					StringTokenizer st = new StringTokenizer(name, ".", false);
					StringBuffer xName = new StringBuffer();
					while(st.hasMoreTokens()) {
						if(xName.length()>0) {
							xName.append('.');
						}
						xName.append(st.nextToken());
						if(st.hasMoreTokens()) {
							String xNameAsString = xName.toString();
							Collection<IBean> xBeans = context.getCdiProject().getBeans(xNameAsString, true);
							if(!xBeans.isEmpty()) {
								String yName = name.substring(xNameAsString.length()+1);
								IStatus status = JavaConventions.validateJavaTypeName(yName, CompilerOptions.VERSION_1_6, CompilerOptions.VERSION_1_6);
								if (status.getSeverity() != IStatus.ERROR) {
									ITextSourceReference reference = bean.getNameLocation(true);
									if(reference==null) {
										reference = CDIUtil.getNamedDeclaration(bean);
									}
									addProblem(MessageFormat.format(CDIValidationMessages.UNRESOLVABLE_EL_NAME, name, yName, xNameAsString, xBeans.iterator().next().getElementName()), CDIPreferences.AMBIGUOUS_EL_NAMES, reference, bean.getResource());
									break;
								}
							}
						}
					}
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.jst.web.kb.internal.validation.ValidationErrorManager#shouldCheckDuplicateMarkers()
	 */
	@Override
	protected boolean shouldCheckDuplicateMarkers() {
		return true;
	}

	/*
	 * Returns set of EL names which are declared in the resource
	 */
	private Set<String> getELNamesByResource(CDIValidationContext context, IPath resourcePath) {
		Collection<IBean> beans = context.getCdiProject().getBeans(resourcePath);
		if(beans.isEmpty()) {
			return Collections.emptySet();
		}
		Set<IBeanKeyProvider> ps = context.getCdiProject().getNature().getExtensionManager().getFeatures(IBeanKeyProvider.class);
		Set<String> result = new HashSet<String>();
		for (IBean bean : beans) {
			String name = bean.getName();
			if(name!=null) {
				result.add(name);
			}
			for (IBeanKeyProvider p: ps) {
				String key = p.getKey(bean);
				if(key != null) {
					result.add(key);
				}
			}
		}
		return result;
	}

	private IType getTypeOfInjection(IInjectionPoint injection) {
		IParametedType parametedType = injection.getType();
		return parametedType==null?null:parametedType.getType();
	}

	private void addLinkedStereotypes(String beanPath, IStereotyped stereotyped) {
		if(!isAsYouTypeValidation()) {
			for (IStereotypeDeclaration stereotypeDeclaration : stereotyped.getStereotypeDeclarations()) {
				IStereotype stereotype = stereotypeDeclaration.getStereotype();
				if (shouldValidateType(stereotype.getSourceType())) {
					getValidationContext().addLinkedCoreResource(SHORT_ID, beanPath, stereotype.getSourcePath(), false);
				}
			}
		}
	}

	private void addLinkedInterceptorBindings(String beanPath, IInterceptorBinded binded) {
		if(!isAsYouTypeValidation()) {
			for (IInterceptorBindingDeclaration bindingDeclaration : CDIUtil.getAllInterceptorBindingDeclaratios(binded)) {
				IInterceptorBinding binding = bindingDeclaration.getInterceptorBinding();
				if (shouldValidateType(binding.getSourceType())) {
					getValidationContext().addLinkedCoreResource(SHORT_ID, beanPath, binding.getSourcePath(), false);
				}
			}
		}
	}

	private void validateClassBean(IClassBean bean) {
		validateInitializers(bean);
		validateDisposers(bean);
		validateObserves(bean);
		if (!(bean instanceof ISessionBean)) {
			validateManagedBean(bean);
		} else {
			validateSessionBean((ISessionBean) bean);
		}
		validateMixedClassBean(bean);
		validateConstructors(bean);
		validateInterceptorBindings(bean);
	}

	private void validateInterceptorBindings(IClassBean bean) {
		/*
		 * 9.5.2. Interceptor binding types with members
		 *  - the set of interceptor bindings of a bean or interceptor, including bindings
		 *    inherited from stereotypes and other interceptor bindings, has two instances
		 *    of a certain interceptor binding type and the instances have different values
		 *    of some annotation member
		 */
		try {
			if(hasConflictedInterceptorBindings(bean)) {
				//TODO consider putting markers to interceptor bindings/stereotype declarations.
				ITextSourceReference reference = CDIUtil.convertToSourceReference(bean.getBeanClass().getNameRange(), bean.getResource(), bean.getBeanClass());
				addProblem(CDIValidationMessages.CONFLICTING_INTERCEPTOR_BINDINGS, CDIPreferences.CONFLICTING_INTERCEPTOR_BINDINGS, reference, bean.getResource());
			}
			for (IBeanMethod method : bean.getAllMethods()) {
				if(hasConflictedInterceptorBindings(method)) {
					//TODO consider putting markers to interceptor bindings/stereotype declarations.
					ITextSourceReference reference = CDIUtil.convertToSourceReference(method.getMethod().getNameRange(), bean.getResource(), method.getMethod());
					addProblem(CDIValidationMessages.CONFLICTING_INTERCEPTOR_BINDINGS, CDIPreferences.CONFLICTING_INTERCEPTOR_BINDINGS, reference, bean.getResource());
				}
			}
		} catch (JavaModelException e) {
			CDICorePlugin.getDefault().logError(e);
		} catch (CoreException e) {
			CDICorePlugin.getDefault().logError(e);
		}
	}

	private boolean hasConflictedInterceptorBindings(IInterceptorBinded binded) throws CoreException {
		Collection<IInterceptorBindingDeclaration> declarations = CDIUtil.getAllInterceptorBindingDeclaratios(binded);
		if(declarations.size()>1) {
			Map<String, String> keys = new HashMap<String, String>();
			for (IInterceptorBindingDeclaration declaration : declarations) {
				IType type = declaration.getInterceptorBinding().getSourceType();
				if(type!=null) {
					String name = type.getFullyQualifiedName();
					String key = CDIProject.getAnnotationDeclarationKey(declaration);
					String anotherKey = keys.get(name);
					if(anotherKey!=null) {
						if(!anotherKey.equals(key)) {
							return true;
						}
					} else {
						keys.put(name, key);
					}
				}
			}
		}
		return false;
	}

	private void validateSpecializingBean(IBean bean) {
		/*
		 * 4.3.1. Direct and indirect specialization
		 *  - decorator or interceptor is annotated @Specializes (Non-Portable behavior)
		 */
		IAnnotationDeclaration specializesDeclaration = bean.getSpecializesAnnotationDeclaration();
		if(specializesDeclaration!=null) {
			if(bean instanceof IDecorator) {
				addProblem(CDIValidationMessages.DECORATOR_ANNOTATED_SPECIALIZES, CDIPreferences.INTERCEPTOR_ANNOTATED_SPECIALIZES, specializesDeclaration, bean.getResource(), DECORATOR_ANNOTATED_SPECIALIZES_ID);
			} else if(bean instanceof IInterceptor) {
				addProblem(CDIValidationMessages.INTERCEPTOR_ANNOTATED_SPECIALIZES, CDIPreferences.INTERCEPTOR_ANNOTATED_SPECIALIZES, specializesDeclaration, bean.getResource(), INTERCEPTOR_ANNOTATED_SPECIALIZES_ID);
			}
		}
		IBean specializedBean = bean.getSpecializedBean();
		if(specializedBean!=null) {
			if(!isAsYouTypeValidation() && shouldValidateType(specializedBean.getBeanClass())) {
				getValidationContext().addLinkedCoreResource(SHORT_ID, bean.getSourcePath().toOSString(), specializedBean.getSourcePath(), false);
			}

			String beanClassName = bean.getBeanClass().getElementName();
			String beanName = bean instanceof IBeanMethod?beanClassName + "." + ((IBeanMethod)bean).getSourceMember().getElementName() + "()":beanClassName;
			String specializingBeanClassName = specializedBean.getBeanClass().getElementName();
			String specializingBeanName = specializedBean instanceof IBeanMethod?specializingBeanClassName + "." + ((IBeanMethod)specializedBean).getSourceMember().getElementName() + "()":specializingBeanClassName;
			/*
			 * 4.3.1. Direct and indirect specialization
			 *  - X specializes Y but does not have some bean type of Y
			 */
			Set<String> legalTypes = new HashSet<String>();
			for (IParametedType type : bean.getLegalTypes()) {
				if(type.getType() != null) legalTypes.add(type.getType().getFullyQualifiedName());
			}
			Set<String> missingTypesSet = new TreeSet<String>();
			for (IParametedType specializingType : specializedBean.getLegalTypes()) {
				if(!legalTypes.contains(specializingType.getType().getFullyQualifiedName())) {
					missingTypesSet.add(specializingType.getType().getElementName());
				}
			}
			StringBuffer missingTypes = new StringBuffer();
			for (String type: missingTypesSet) {
				if(missingTypes.length() > 0) {
					missingTypes.append(", ");
				}
				missingTypes.append(type);
			}
			if(missingTypes.length()>0) {
				addProblem(CDIValidationMessages.MISSING_TYPE_IN_SPECIALIZING_BEAN, CDIPreferences.MISSING_TYPE_IN_SPECIALIZING_BEAN,
						new String[]{beanName, specializingBeanName, missingTypes.toString()},
						bean.getSpecializesAnnotationDeclaration(), bean.getResource());
			}

			/*
			 * 4.3.1. Direct and indirect specialization
			 *  - X specializes Y and Y has a name and X declares a name explicitly, using @Named
			 */
			if(specializedBean.getName()!=null) {
				IAnnotationDeclaration nameDeclaration = bean.getAnnotation(CDIConstants.NAMED_QUALIFIER_TYPE_NAME);
				if(nameDeclaration!=null) {
					addProblem(CDIValidationMessages.CONFLICTING_NAME_IN_SPECIALIZING_BEAN, CDIPreferences.CONFLICTING_NAME_IN_SPECIALIZING_BEAN,
							new String[]{beanName, specializingBeanName},
							nameDeclaration, bean.getResource());
				}
			}
			/*
			 * 5.1.3. Inconsistent specialization
			 *  - Suppose an enabled bean X specializes a second bean Y. If there is another enabled bean that specializes Y we say that inconsistent
			 *    specialization exists. The container automatically detects inconsistent specialization and treats it as a deployment problem.
			 */
			if(bean.isEnabled() && specializedBean instanceof IClassBean) {
				IClassBean supperClassBean = (IClassBean)specializedBean;
				Collection<? extends IClassBean> allSpecializingBeans = supperClassBean.getSpecializingBeans();
				if(allSpecializingBeans.size()>1) {
					Set<String> specializingBeanNames = new TreeSet<String>();
					for (IClassBean specializingBean : allSpecializingBeans) {
						if(specializingBean != bean && specializingBean.isEnabled()) {
							specializingBeanNames.add(specializingBean.getElementName());
							if(!isAsYouTypeValidation() && shouldValidateType(specializingBean.getBeanClass())) {
								getValidationContext().addLinkedCoreResource(SHORT_ID, specializingBean.getSourcePath().toOSString(), bean.getSourcePath(), false);
								getValidationContext().addLinkedCoreResource(SHORT_ID, bean.getSourcePath().toOSString(), specializingBean.getSourcePath(), false);
							}
						}
					}
					if(!specializingBeanNames.isEmpty() && specializesDeclaration!=null) {
						StringBuffer sb = new StringBuffer(bean.getElementName());
						for (String name: specializingBeanNames) {
							sb.append(", ").append(name);
						}
						addProblem(CDIValidationMessages.INCONSISTENT_SPECIALIZATION, CDIPreferences.INCONSISTENT_SPECIALIZATION,
								new String[]{sb.toString(), supperClassBean.getElementName()},
								specializesDeclaration, bean.getResource());
					}
				}
			}
		}
	}

	private void validateConstructors(IClassBean bean) {
		Collection<IBeanMethod> constructors = bean.getBeanConstructors();
		if(constructors.size()>1) {
			Collection<IAnnotationDeclaration> injects = new ArrayList<IAnnotationDeclaration>();
			for (IBeanMethod constructor : constructors) {
				IAnnotationDeclaration inject = constructor.getAnnotation(CDIConstants.INJECT_ANNOTATION_TYPE_NAME);
				if(inject!=null) {
					injects.add(inject);
				}
			}
			/*
			 * 3.7.1. Declaring a bean constructor
			 * 	- bean class has more than one constructor annotated @Inject
			 */
			if(injects.size()>1) {
				for (IAnnotationDeclaration inject : injects) {
					addProblem(CDIValidationMessages.MULTIPLE_INJECTION_CONSTRUCTORS, CDIPreferences.MULTIPLE_INJECTION_CONSTRUCTORS, inject, bean.getResource(), MULTIPLE_INJECTION_CONSTRUCTORS_ID);
				}
			}
		}
	}

	private void validateObserves(IClassBean bean) {
		Collection<IBeanMethod> observes = bean.getAllMethods();
		if (observes.isEmpty()) {
			return;
		}
		for (IBeanMethod observer : observes) {
			if(!observer.isObserver()) {
				continue;
			}
			List<IParameter> params = observer.getParameters();
			Collection<ITextSourceReference> declarations = new ArrayList<ITextSourceReference>();
			for (IParameter param : params) {
				ITextSourceReference declaration = param.getAnnotationPosition(CDIConstants.OBSERVERS_ANNOTATION_TYPE_NAME);
				if (declaration != null) {
					declarations.add(declaration);

					/*
					 * 10.4.2. Declaring an observer method
					 *  - bean with scope @Dependent has an observer method declared notifyObserver=IF_EXISTS
					 */
					if(bean.getScope()!=null && CDIConstants.DEPENDENT_ANNOTATION_TYPE_NAME.equals(bean.getScope().getSourceType().getFullyQualifiedName())) {
						ICompilationUnit unit = observer.getMethod().getCompilationUnit();
						if(unit!=null) {
							try {
								String source = unit.getSource();
								ISourceRange unitRange = unit.getSourceRange();
								int start = declaration.getStartPosition() - unitRange.getOffset();
								int end = start + declaration.getLength();
								int position = source.substring(start, end).indexOf("IF_EXISTS");
								// TODO Shecks if IF_EXISTS as a string. But this string may be in a comment then we will show incorrect error message. 
								if(position>11) {
									addProblem(CDIValidationMessages.ILLEGAL_CONDITIONAL_OBSERVER, CDIPreferences.ILLEGAL_CONDITIONAL_OBSERVER, declaration, bean.getResource());									
								}
							} catch (JavaModelException e) {
								CDICorePlugin.getDefault().logError(e);
							}
						}
					}
				}
			}
			/*
			 * 10.4.2. Declaring an observer method
			 *  - method has more than one parameter annotated @Observes
			 */
			if(declarations.size()>1) {
				for (ITextSourceReference declaration : declarations) {
					addProblem(CDIValidationMessages.MULTIPLE_OBSERVING_PARAMETERS, CDIPreferences.MULTIPLE_OBSERVING_PARAMETERS, declaration, bean.getResource(), MULTIPLE_OBSERVING_PARAMETERS_ID);
				}
			}
			/*
			 * 3.7.1. Declaring a bean constructor
			 * 	- bean constructor has a parameter annotated @Observes
			 * 
			 * 10.4.2. Declaring an observer method
			 *  - observer method is annotated @Inject
			 */
			IAnnotationDeclaration injectDeclaration = observer.getAnnotation(CDIConstants.INJECT_ANNOTATION_TYPE_NAME);
			try {
				if (injectDeclaration != null) {
					String pref = observer.getMethod().isConstructor()?CDIPreferences.CONSTRUCTOR_PARAMETER_ILLEGALLY_ANNOTATED:CDIPreferences.OBSERVER_ANNOTATED_INJECT;
					String message = observer.getMethod().isConstructor()?CDIValidationMessages.CONSTRUCTOR_PARAMETER_ANNOTATED_OBSERVES:CDIValidationMessages.OBSERVER_ANNOTATED_INJECT;
					int messageId = observer.getMethod().isConstructor()?CONSTRUCTOR_PARAMETER_ANNOTATED_OBSERVES_ID:OBSERVER_ANNOTATED_INJECT_ID;
					addProblem(message, pref, injectDeclaration, bean.getResource(), messageId);
					for (ITextSourceReference declaration : declarations) {
						addProblem(message, pref, declaration, bean.getResource(), messageId);
					}
				}
			} catch (JavaModelException e) {
				CDICorePlugin.getDefault().logError(e);
			}
			/*
			 * 10.4.2. Declaring an observer method
			 *  - interceptor or decorator has a method with a parameter annotated @Observes
			 */
			if(bean instanceof IDecorator) {
				for (ITextSourceReference declaration : declarations) {
					addProblem(CDIValidationMessages.OBSERVER_IN_DECORATOR, CDIPreferences.OBSERVER_IN_INTERCEPTOR_OR_DECORATOR, declaration, bean.getResource(), OBSERVER_IN_DECORATOR_ID);
				}
			} else if(bean instanceof IInterceptor) {
				for (ITextSourceReference declaration : declarations) {
					addProblem(CDIValidationMessages.OBSERVER_IN_INTERCEPTOR, CDIPreferences.OBSERVER_IN_INTERCEPTOR_OR_DECORATOR, declaration, bean.getResource(), OBSERVER_IN_INTERCEPTOR_ID);
				}
			}

			validateSessionBeanMethod(bean, observer, declarations, CDIValidationMessages.ILLEGAL_OBSERVER_IN_SESSION_BEAN,	CDIPreferences.ILLEGAL_OBSERVER_IN_SESSION_BEAN, ILLEGAL_OBSERVER_IN_SESSION_BEAN_ID);
		}
	}

	private void validateDisposers(IClassBean bean) {
		Collection<IBeanMethod> disposers = bean.getDisposers();
		if (disposers.isEmpty()) {
			return;
		}

		Set<IBeanMethod> boundDisposers = new HashSet<IBeanMethod>();
		for (IProducer producer : bean.getProducers()) {
			if (producer instanceof IProducerMethod && producer.exists()) {
				IProducerMethod producerMethod = (IProducerMethod) producer;
				Collection<IBeanMethod> disposerMethods = producer.getCDIProject().resolveDisposers(producerMethod);
				boundDisposers.addAll(disposerMethods);
				if (disposerMethods.size() > 1) {
					/*
					 * 3.3.7. Disposer method resolution
					 *  - there are multiple disposer methods for a single producer method
					 */
					for (IBeanMethod disposerMethod : disposerMethods) {
						Collection<ITextSourceReference> disposerDeclarations = CDIUtil.getAnnotationPossitions(disposerMethod, CDIConstants.DISPOSES_ANNOTATION_TYPE_NAME);
						for (ITextSourceReference declaration : disposerDeclarations) {
							addProblem(CDIValidationMessages.MULTIPLE_DISPOSERS_FOR_PRODUCER, CDIPreferences.MULTIPLE_DISPOSERS_FOR_PRODUCER, declaration, bean.getResource(), MULTIPLE_DISPOSERS_FOR_PRODUCER_ID);
						}
					}
				}
			}
		}

		for (IBeanMethod disposer : disposers) {
			if(!disposer.exists()) {
				continue;
			}
			List<IParameter> params = disposer.getParameters();

			/*
			 * 3.3.6. Declaring a disposer method
			 *  - method has more than one parameter annotated @Disposes
			 */
			Collection<ITextSourceReference> disposerDeclarations = new ArrayList<ITextSourceReference>();
			for (IParameter param : params) {
				ITextSourceReference declaration = param.getAnnotationPosition(CDIConstants.DISPOSES_ANNOTATION_TYPE_NAME);
				if (declaration != null  && param.exists()) {
					disposerDeclarations.add(declaration);
				}
			}
			if (disposerDeclarations.size() > 1) {
				for (ITextSourceReference declaration : disposerDeclarations) {
					addProblem(CDIValidationMessages.MULTIPLE_DISPOSING_PARAMETERS, CDIPreferences.MULTIPLE_DISPOSING_PARAMETERS, declaration, bean.getResource(), MULTIPLE_DISPOSING_PARAMETERS_ID);
				}
			}

			/*
			 * 3.3.6. Declaring a disposer method
			 *  - a disposer method has a parameter annotated @Observes.
			 * 
			 * 10.4.2. Declaring an observer method
			 *  - a observer method has a parameter annotated @Disposes.
			 */
			Collection<ITextSourceReference> declarations = new ArrayList<ITextSourceReference>();
			boolean observesExists = false;
			declarations.addAll(disposerDeclarations);
			for (IParameter param : params) {
				ITextSourceReference declaration = param.getAnnotationPosition(CDIConstants.OBSERVERS_ANNOTATION_TYPE_NAME);
				if (declaration != null && param.exists()) {
					declarations.add(declaration);
					observesExists = true;
				}
			}
			if (observesExists) {
				for (ITextSourceReference declaration : declarations) {
					addProblem(CDIValidationMessages.OBSERVER_PARAMETER_ILLEGALLY_ANNOTATED, CDIPreferences.OBSERVER_PARAMETER_ILLEGALLY_ANNOTATED, declaration, bean.getResource(), OBSERVER_PARAMETER_ILLEGALLY_ANNOTATED_ID);
				}
			}

			/*
			 * 3.3.6. Declaring a disposer method
			 *  - a disposer method is annotated @Inject.
			 * 
			 * 3.9.1. Declaring an initializer method
			 *  - an initializer method has a parameter annotated @Disposes
			 * 
			 * 3.7.1. Declaring a bean constructor
			 * 	- bean constructor has a parameter annotated @Disposes
			 */
			IAnnotationDeclaration injectDeclaration = disposer.getAnnotation(CDIConstants.INJECT_ANNOTATION_TYPE_NAME);
			try {
				if (injectDeclaration != null) {
					String pref = disposer.getMethod().isConstructor()?CDIPreferences.CONSTRUCTOR_PARAMETER_ILLEGALLY_ANNOTATED:CDIPreferences.DISPOSER_ANNOTATED_INJECT;
					String message = disposer.getMethod().isConstructor()?CDIValidationMessages.CONSTRUCTOR_PARAMETER_ANNOTATED_DISPOSES:CDIValidationMessages.DISPOSER_ANNOTATED_INJECT;
					int messageId = disposer.getMethod().isConstructor()?CONSTRUCTOR_PARAMETER_ANNOTATED_DISPOSES_ID:DISPOSER_ANNOTATED_INJECT_ID;
					addProblem(message, pref, injectDeclaration, bean.getResource(), messageId);
					for (ITextSourceReference declaration : disposerDeclarations) {
						addProblem(message, pref, declaration, bean.getResource(), messageId);
					}
				}
			} catch (JavaModelException e) {
				CDICorePlugin.getDefault().logError(e);
			}

			/*
			 * 3.3.6. Declaring a disposer method
			 *  - a non-static method of a session bean class has a parameter annotated @Disposes, and the method is not a business method of the session bean
			 */
			validateSessionBeanMethod(bean, disposer, disposerDeclarations, CDIValidationMessages.ILLEGAL_DISPOSER_IN_SESSION_BEAN,
					CDIPreferences.ILLEGAL_DISPOSER_IN_SESSION_BEAN, ILLEGAL_DISPOSER_IN_SESSION_BEAN_ID);

			/*
			 * 3.3.6. Declaring a disposer method
			 *  - decorators may not declare disposer methods
			 */
			if (bean instanceof IDecorator) {
				IDecorator decorator = (IDecorator) bean;
				ITextSourceReference decoratorDeclaration = decorator.getDecoratorAnnotation();
				if(decoratorDeclaration == null) {
					//for custom implementations
					decoratorDeclaration = decorator.getNameLocation(true);
				}
				addProblem(CDIValidationMessages.DISPOSER_IN_DECORATOR, CDIPreferences.DISPOSER_IN_INTERCEPTOR_OR_DECORATOR, decoratorDeclaration, bean.getResource(), DISPOSER_IN_DECORATOR_ID);
				for (ITextSourceReference declaration : disposerDeclarations) {
					addProblem(CDIValidationMessages.DISPOSER_IN_DECORATOR, CDIPreferences.DISPOSER_IN_INTERCEPTOR_OR_DECORATOR, declaration, bean.getResource(), DISPOSER_IN_DECORATOR_ID);
				}
			}

			/*
			 * 3.3.6. Declaring a disposer method
			 *  - interceptors may not declare disposer methods
			 */
			if (bean instanceof IInterceptor) {
				IInterceptor interceptor = (IInterceptor) bean;
				ITextSourceReference interceptorDeclaration = interceptor.getInterceptorAnnotation();
				if(interceptorDeclaration == null) {
					//for custom implementations
					interceptorDeclaration = interceptor.getNameLocation(true);
				}
				addProblem(CDIValidationMessages.DISPOSER_IN_INTERCEPTOR, CDIPreferences.DISPOSER_IN_INTERCEPTOR_OR_DECORATOR, interceptorDeclaration, bean
						.getResource(), DISPOSER_IN_INTERCEPTOR_ID);
				for (ITextSourceReference declaration : disposerDeclarations) {
					addProblem(CDIValidationMessages.DISPOSER_IN_INTERCEPTOR, CDIPreferences.DISPOSER_IN_INTERCEPTOR_OR_DECORATOR, declaration, bean
							.getResource(), DISPOSER_IN_INTERCEPTOR_ID);
				}
			}

			/*
			 * 3.3.7. Disposer method resolution
			 *  - there is no producer method declared by the (same) bean class that is assignable to the disposed parameter of a disposer method
			 */
			if (!boundDisposers.contains(disposer)) {
				for (ITextSourceReference declaration : disposerDeclarations) {
					addProblem(CDIValidationMessages.NO_PRODUCER_MATCHING_DISPOSER, CDIPreferences.NO_PRODUCER_MATCHING_DISPOSER, declaration, bean.getResource());
				}
			}
		}
	}

	/**
	 * If the method is not a static method and is not a business method of the
	 * session bean and is observer or disposer then mark it as incorrect.
	 * 
	 * @param bean
	 * @param method
	 * @param annotatedParams
	 * @param errorKey
	 */
	private void validateSessionBeanMethod(IClassBean bean, IBeanMethod method, Collection<ITextSourceReference> annotatedParams, String errorMessage, String preferencesKey, int id) {
		if (bean instanceof ISessionBean && annotatedParams != null) {
			IMethod iMethod = CDIUtil.getBusinessMethodDeclaration((SessionBean)bean, method);
			if(iMethod==null) {
				saveAllSuperTypesAsLinkedResources(bean);
				for (ITextSourceReference declaration : annotatedParams) {
					String bindedErrorMessage = NLS.bind(errorMessage, new String[]{method.getMethod().getElementName(), bean.getBeanClass().getElementName()});
					addProblem(bindedErrorMessage, preferencesKey, declaration, bean.getResource(), id);
				}
			} else if (!isAsYouTypeValidation() && iMethod != method.getMethod() && !iMethod.isBinary()) {
				getValidationContext().addLinkedCoreResource(SHORT_ID, bean.getSourcePath().toOSString(), iMethod.getResource().getFullPath(), false);
			}
		}
	}

	private static final String[] RESOURCE_ANNOTATIONS = { CDIConstants.RESOURCE_ANNOTATION_TYPE_NAME, CDIConstants.WEB_SERVICE_REF_ANNOTATION_TYPE_NAME, CDIConstants.EJB_ANNOTATION_TYPE_NAME, CDIConstants.PERSISTENCE_CONTEXT_ANNOTATION_TYPE_NAME, CDIConstants.PERSISTENCE_UNIT_ANNOTATION_TYPE_NAME };

	private void validateProducer(CDIValidationContext context, IProducer producer) {
		try {
			Collection<ITypeDeclaration> typeDeclarations = producer.getAllTypeDeclarations();
			String[] typeVariables = producer.getBeanClass().getTypeParameterSignatures();
			ITypeDeclaration typeDeclaration = null;
			ITextSourceReference typeDeclarationReference = null;
			if (!typeDeclarations.isEmpty()) {
				/*
				 * 3.3. Producer methods
				 *  - producer method return type contains a wildcard type parameter
				 * 
				 * 2.2.1 Legal bean types
				 *  - a parameterized type that contains a wildcard type parameter is not a legal bean type.
				 * 
				 * 3.4. Producer fields
				 *  - producer field type contains a wildcard type parameter
				 */
				typeDeclaration = typeDeclarations.iterator().next();
				
				typeDeclarationReference = CDIUtil.convertToJavaSourceReference(typeDeclaration, producer.getSourceMember());
				
				
				String[] paramTypes = Signature.getTypeArguments(typeDeclaration.getSignature());
				boolean variable = false;
				for (String paramType : paramTypes) {
					if (Signature.getTypeSignatureKind(paramType) == Signature.WILDCARD_TYPE_SIGNATURE) {
						if (producer instanceof IProducerField) {
							addProblem(CDIValidationMessages.PRODUCER_FIELD_TYPE_HAS_WILDCARD, CDIPreferences.PRODUCER_METHOD_RETURN_TYPE_HAS_WILDCARD_OR_VARIABLE, typeDeclarationReference,
									producer.getResource());
						} else {
							addProblem(CDIValidationMessages.PRODUCER_METHOD_RETURN_TYPE_HAS_WILDCARD, CDIPreferences.PRODUCER_METHOD_RETURN_TYPE_HAS_WILDCARD_OR_VARIABLE,
									typeDeclarationReference, producer.getResource());
						}
					} else if(!variable && isTypeVariable(producer, Signature.toString(paramType), typeVariables)) {
						/*
						 * 3.3. Producer methods
						 *  - producer method with a parameterized return type with a type variable declares any scope other than @Dependent
						 * 
						 * 3.4. Producer fields
						 *  - producer field with a parameterized type with a type variable declares any scope other than @Dependent
						 */
						variable = true;
						IAnnotationDeclaration scopeOrStereotypeDeclaration = CDIUtil.getDifferentScopeDeclarationThanDepentend(producer);
						if (scopeOrStereotypeDeclaration != null) {
							boolean field = producer instanceof IProducerField;
							addProblem(field ? CDIValidationMessages.ILLEGAL_SCOPE_FOR_PRODUCER_FIELD : CDIValidationMessages.ILLEGAL_SCOPE_FOR_PRODUCER_METHOD,
									field ? CDIPreferences.ILLEGAL_SCOPE_FOR_BEAN : CDIPreferences.ILLEGAL_SCOPE_FOR_BEAN,
									scopeOrStereotypeDeclaration, producer.getResource());
						}
						break;
					}
				}
			}

			/*
			 * 3.3.2. Declaring a producer method
			 *  - producer method is annotated @Inject
			 */
			IAnnotationDeclaration inject = producer.getAnnotation(CDIConstants.INJECT_ANNOTATION_TYPE_NAME);
			if (inject != null) {
				addProblem(CDIValidationMessages.PRODUCER_ANNOTATED_INJECT, CDIPreferences.PRODUCER_ANNOTATED_INJECT, inject, inject.getResource() != null ? inject.getResource() : producer.getResource(), PRODUCER_ANNOTATED_INJECT_ID);
			}

			if (producer instanceof IProducerField) {
				/*
				 * 3.5.1. Declaring a resource
				 *  - producer field declaration specifies an EL name (together with one of @Resource, @PersistenceContext, @PersistenceUnit, @EJB, @WebServiceRef)
				 */
				IProducerField producerField = (IProducerField) producer;
				if (producerField.getName() != null) {
					IAnnotationDeclaration declaration;
					for (String annotationType : RESOURCE_ANNOTATIONS) {
						declaration = producerField.getAnnotation(annotationType);
						if (declaration != null) {
							IAnnotationDeclaration nameDeclaration = producerField.getAnnotation(CDIConstants.NAMED_QUALIFIER_TYPE_NAME);
							if (nameDeclaration != null) {
								declaration = nameDeclaration;
							}
							addProblem(CDIValidationMessages.RESOURCE_PRODUCER_FIELD_SETS_EL_NAME, CDIPreferences.RESOURCE_PRODUCER_FIELD_SETS_EL_NAME, declaration, producer.getResource());
						}
					}
				}
				/*
				 * 3.4. Producer fields
				 *  - producer field type is a type variable
				 */
				if (typeVariables.length > 0) {
					String typeSign = producerField.getField().getTypeSignature();
					String typeString = Signature.toString(typeSign);
					for (String variableSig : typeVariables) {
						String variableName = Signature.getTypeVariable(variableSig);
						if (typeString.equals(variableName)) {
							addProblem(CDIValidationMessages.PRODUCER_FIELD_TYPE_IS_VARIABLE, CDIPreferences.PRODUCER_METHOD_RETURN_TYPE_HAS_WILDCARD_OR_VARIABLE, typeDeclaration != null ? typeDeclarationReference : producer, producer.getResource());
						}
					}
				}
				/*
				 * 3.4.2. Declaring a producer field
				 *  - non-static field of a session bean class is annotated @Produces
				 */
				if(producer.getClassBean() instanceof ISessionBean && !Flags.isStatic(producerField.getField().getFlags())) {
					addProblem(CDIValidationMessages.ILLEGAL_PRODUCER_FIELD_IN_SESSION_BEAN, CDIPreferences.ILLEGAL_PRODUCER_METHOD_IN_SESSION_BEAN, producer.getProducesAnnotation(), producer.getResource(), ILLEGAL_PRODUCER_FIELD_IN_SESSION_BEAN_ID);
				}
			} else {
				IProducerMethod producerMethod = (IProducerMethod) producer;
				List<IParameter> params = producerMethod.getParameters();
				Collection<ITextSourceReference> observesDeclarations = new ArrayList<ITextSourceReference>();
				Collection<ITextSourceReference> disposalDeclarations = new ArrayList<ITextSourceReference>();
				IAnnotationDeclaration producesDeclaration = producerMethod.getAnnotation(CDIConstants.PRODUCES_ANNOTATION_TYPE_NAME);
				if(producesDeclaration != null) {
					observesDeclarations.add(producesDeclaration);
					disposalDeclarations.add(producesDeclaration);
				}
				for (IParameter param : params) {
					/*
					 * 3.3.6. Declaring a disposer method
					 *  - a disposer method is annotated @Produces.
					 * 
					 * 3.3.2. Declaring a producer method
					 *  - a has a parameter annotated @Disposes
					 */
					ITextSourceReference declaration = param.getAnnotationPosition(CDIConstants.DISPOSES_ANNOTATION_TYPE_NAME);
					if (declaration != null) {
						disposalDeclarations.add(declaration);
					}
					/*
					 * 3.3.2. Declaring a producer method
					 *  - a has a parameter annotated @Observers
					 * 
					 * 10.4.2. Declaring an observer method
					 *  - an observer method is annotated @Produces
					 */
					declaration = param.getAnnotationPosition(CDIConstants.OBSERVERS_ANNOTATION_TYPE_NAME);
					if (declaration != null) {
						observesDeclarations.add(declaration);
					}
				}
				if (observesDeclarations.size() > 1) {
					for (ITextSourceReference declaration : observesDeclarations) {
						addProblem(CDIValidationMessages.PRODUCER_PARAMETER_ILLEGALLY_ANNOTATED_OBSERVES, CDIPreferences.PRODUCER_PARAMETER_ILLEGALLY_ANNOTATED,
								declaration, producer.getResource(), PRODUCER_PARAMETER_ILLEGALLY_ANNOTATED_OBSERVES_ID);
					}
				}
				if (disposalDeclarations.size() > 1) {
					for (ITextSourceReference declaration : disposalDeclarations) {
						addProblem(CDIValidationMessages.PRODUCER_PARAMETER_ILLEGALLY_ANNOTATED_DISPOSES, CDIPreferences.PRODUCER_PARAMETER_ILLEGALLY_ANNOTATED,
								declaration, producer.getResource(), PRODUCER_PARAMETER_ILLEGALLY_ANNOTATED_DISPOSES_ID);
					}
				}

				/*
				 * 3.3. Producer methods
				 *  - producer method return type is a type variable
				 * 
				 * 2.2.1 - Legal bean types
				 *  - a type variable is not a legal bean type
				 */
				String typeSign = producerMethod.getMethod().getReturnType();
				String typeString = Signature.toString(typeSign);
				if(isTypeVariable(producerMethod, typeString, typeVariables)) {
					addProblem(CDIValidationMessages.PRODUCER_METHOD_RETURN_TYPE_IS_VARIABLE, CDIPreferences.PRODUCER_METHOD_RETURN_TYPE_HAS_WILDCARD_OR_VARIABLE,
							typeDeclaration != null ? typeDeclarationReference : producer, producer.getResource());
				}
				/*
				 * 3.3.2. Declaring a producer method
				 *  - non-static method of a session bean class is annotated @Produces, and the method is not a business method of the session bean
				 */
				IClassBean classBean = producer.getClassBean();
				if(classBean instanceof ISessionBean) {
					IMethod method = CDIUtil.getBusinessMethodDeclaration((SessionBean)classBean, producerMethod);
					if(method==null) {
						String bindedErrorMessage = NLS.bind(CDIValidationMessages.ILLEGAL_PRODUCER_METHOD_IN_SESSION_BEAN, new String[]{producerMethod.getMethod().getElementName(), producer.getBeanClass().getElementName()});
						addProblem(bindedErrorMessage, CDIPreferences.ILLEGAL_PRODUCER_METHOD_IN_SESSION_BEAN, producer.getProducesAnnotation(), producer.getResource(), ILLEGAL_PRODUCER_METHOD_IN_SESSION_BEAN_ID);
						saveAllSuperTypesAsLinkedResources(classBean);
					} else if (!isAsYouTypeValidation() && method != producerMethod.getMethod() && method.exists() && !method.isReadOnly()) {
						getValidationContext().addLinkedCoreResource(SHORT_ID, classBean.getSourcePath().toOSString(), method.getResource().getFullPath(), false);
					}
				}

				IAnnotationDeclaration sDeclaration = producerMethod.getSpecializesAnnotationDeclaration();
				if(sDeclaration!=null) {
					if(Flags.isStatic(producerMethod.getMethod().getFlags())) {
						/*
						 * 3.3.3. Specializing a producer method
						 *  - method annotated @Specializes is static
						 */
						addProblem(CDIValidationMessages.ILLEGAL_SPECIALIZING_PRODUCER_STATIC, CDIPreferences.ILLEGAL_SPECIALIZING_BEAN, sDeclaration, producer.getResource());
					} else {
						/*
						 * 3.3.3. Specializing a producer method
						 *  - method annotated @Specializes does not directly override another producer method
						 */
						IMethod superMethod = CDIUtil.getDirectOverridingMethodDeclaration(producerMethod);
						boolean overrides = false;
						if(superMethod!=null) {
							IType superType = superMethod.getDeclaringType();
							if(superType.isBinary()) {
								IAnnotation[] ants = superMethod.getAnnotations();
								for (IAnnotation an : ants) {
									if(CDIConstants.PRODUCES_ANNOTATION_TYPE_NAME.equals(an.getElementName())) {
										overrides = true;
									}
								}
							} else {
								Collection<IBean> beans = context.getCdiProject().getBeans(superType.getResource().getFullPath());
								for (IBean iBean : beans) {
									if(iBean instanceof IProducerMethod) {
										IProducerMethod prMethod = (IProducerMethod)iBean;
										if(prMethod.getMethod().isSimilar(superMethod)) {
											overrides = true;
										}
									}
								}
							}
						}
						if(!overrides) {
							addProblem(CDIValidationMessages.ILLEGAL_SPECIALIZING_PRODUCER_OVERRIDE, CDIPreferences.ILLEGAL_SPECIALIZING_BEAN, sDeclaration, producer.getResource());
						}
						saveAllSuperTypesAsLinkedResources(producer.getClassBean());
					}
				}
			}
		} catch (JavaModelException e) {
			CDICorePlugin.getDefault().logError(e);
		}
	}

	private boolean isTypeVariable(IProducer producer, String type, String[] typeVariables) throws JavaModelException {
		if(producer instanceof IProducerMethod) {
			ITypeParameter[] paramTypes = ((IProducerMethod)producer).getMethod().getTypeParameters();
			for (ITypeParameter param : paramTypes) {
				String variableName = param.getElementName();
				if (variableName.equals(type)) {
					return true;
				}
			}
		}
		if (typeVariables.length > 0) {
			for (String variableSig : typeVariables) {
				String variableName = Signature.getTypeVariable(variableSig);
				if (type.equals(variableName)) {
					return true;
				}
			}
		}
		return false;
	}

	private void saveAllSuperTypesAsLinkedResources(IBean bean) {
		if(!isAsYouTypeValidation()) {
			for (IParametedType type : bean.getAllTypes()) {
				IType superType = type.getType();
				if(superType!=null && !superType.isBinary() && superType.getResource()!=null && superType!=bean.getBeanClass()) {
					getValidationContext().addLinkedCoreResource(SHORT_ID, bean.getSourcePath().toOSString(), superType.getResource().getFullPath(), false);
				}
			}
		}
	}

	private void collectAllRelatedInjections(IFile validatingResource, Set<IPath> relatedResources) {
		if(!asYouTypeValidation) {
			CDIValidationContext context = getCDIContext(validatingResource);
			ICDIProject cdiProject = context.getCdiProject();
			collectAllRelatedInjectionsForBean(validatingResource, relatedResources);
			if("beans.xml".equals(validatingResource.getName().toLowerCase())) {
				List<INodeReference> nodes = cdiProject.getAlternativeClasses();
				collectAllRelatedInjectionsForNode(nodes, relatedResources);
				nodes = cdiProject.getDecoratorClasses();
				collectAllRelatedInjectionsForNode(nodes, relatedResources);
				nodes = cdiProject.getInterceptorClasses();
				collectAllRelatedInjectionsForNode(nodes, relatedResources);
			}
		}
	}

	private void collectAllRelatedInjectionsForNode(List<INodeReference> nodes, Set<IPath> relatedResources) {
		try {
			for (INodeReference node : nodes) {
				String className = node.getValue();
				IType type = EclipseJavaUtil.findType(beansXmlValidator.getJavaProject(node.getResource()), className);
				if(type!=null && !type.isBinary()) {
					IResource resource = type.getResource();
					if(type!=null && resource instanceof IFile) {
						collectAllRelatedInjectionsForBean((IFile)resource, relatedResources);
					}
				}
			}
		} catch (JavaModelException e) {
			CDICorePlugin.getDefault().logError(e);
		}
	}

	void collectAllRelatedInjectionsForBean(IFile validatingResource, Set<IPath> relatedResources) {
		if(!asYouTypeValidation) {
			CDIValidationContext context = getCDIContext(validatingResource);
			ICDIProject cdiProject = context.getCdiProject();
			Collection<IBean> beans = cdiProject.getBeans(validatingResource.getFullPath());
			if(!beans.isEmpty()) {
				for (IBean bean : beans) {
					for (IParametedType type : bean.getAllTypes()) {
						IType superType = type.getType();
						if(superType!=null) {
							collectAllRelatedInjectionsForType(cdiProject, superType, bean, relatedResources);
						}
					}
				}
			} else if(validatingResource.getName().toLowerCase().endsWith(".java")) {
				ICompilationUnit unit = EclipseUtil.getCompilationUnit(validatingResource);
				if(unit!=null) {
					try {
						IType[] types = unit.getAllTypes();
						for (IType type : types) {
							ParametedType parametedType = ((CDIProject)cdiProject).getNature().getTypeFactory().newParametedType(type);
							Collection<IParametedType> allTypes = parametedType.getAllTypes();
							for (IParametedType iParametedType : allTypes) {
								IType t = iParametedType.getType();
								if(t!=null) {
									collectAllRelatedInjectionsForType(cdiProject, t, null, relatedResources);
								}
							}
						}
					} catch (JavaModelException e) {
						CDICorePlugin.getDefault().logError(e);
					}
				}
			}
		}
	}

	private void collectAllRelatedInjectionsForType(ICDIProject cdiProject, IType type, IBean bean, Set<IPath> relatedResources) {
		for (IInjectionPoint injection : cdiProject.getInjections(type.getFullyQualifiedName())) {
			if(!injection.getClassBean().getBeanClass().isBinary() && injection.getClassBean()!=bean) {
				relatedResources.add(injection.getSourcePath());
			}
		}
	}

	/**
	 * Checks if the injection point injects some bean from a CDI extension and should be ignored by the validator during lookup validation.
	 * @param typeOfInjectionPoint
	 * @param injection
	 * @return
	 */
	private boolean shouldIgnoreInjection(CDIValidationContext context, IType typeOfInjectionPoint, IInjectionPoint injection) {
		for (IInjectionPointValidatorFeature feature : context.getInjectionValidationFeatures()) {
			if(feature.shouldIgnoreInjection(typeOfInjectionPoint, injection)) {
				return true;
			}
		}
		return false;
	}

	private void validateInitializers(IClassBean bean) {
		for (IInitializerMethod initializer: bean.getInitializers()) {
			validateInitializerMethod(initializer);
		}
	}

	private void validateInitializerMethod(IInitializerMethod initializer) {
		IAnnotationDeclaration named = initializer.getAnnotation(CDIConstants.NAMED_QUALIFIER_TYPE_NAME);
		if (named != null) {
			boolean valueExists = named.getMemberValue(null) != null;
			if (!valueExists) {
				addProblem(CDIValidationMessages.PARAM_INJECTION_DECLARES_EMPTY_NAME, CDIPreferences.PARAM_INJECTION_DECLARES_EMPTY_NAME, named, initializer.getResource(), PARAM_INJECTION_DECLARES_EMPTY_NAME_ID);
			}
		}

		IAnnotationDeclaration declaration = initializer.getInjectAnnotation();
		/*
		 * 3.9.1. Declaring an initializer method
		 *  - generic method of a bean is annotated @Inject
		 */
		if(CDIUtil.isMethodGeneric(initializer)) {
			addProblem(CDIValidationMessages.GENERIC_METHOD_ANNOTATED_INJECT, CDIPreferences.GENERIC_METHOD_ANNOTATED_INJECT, declaration, initializer.getResource());
		}
		/*
		 * 3.9. Initializer methods
		 *  - initializer method may not be static
		 */
		if(CDIUtil.isMethodStatic(initializer)) {
			addProblem(CDIValidationMessages.STATIC_METHOD_ANNOTATED_INJECT, CDIPreferences.GENERIC_METHOD_ANNOTATED_INJECT, declaration, initializer.getResource());
		}
	}

	private void validateInjectionPoint(CDIValidationContext context, IInjectionPoint injection) {
		ICDIProject cdiProject = context.getCdiProject();
		if(injection instanceof IInjectionPointParameter && injection.isAnnotationPresent(CDIConstants.DISPOSES_ANNOTATION_TYPE_NAME)) {
			//Disposer is validated separately
			return;
		}
		/*
		 * 3.11. The qualifier @Named at injection points
		 *  - injection point other than injected field declares a @Named annotation that does not specify the value member
		 */
		if(injection instanceof IInjectionPointParameter) {
			IAnnotationDeclaration named = injection.getAnnotation(CDIConstants.NAMED_QUALIFIER_TYPE_NAME);
			if (named != null) {
				Object value = named.getMemberValue(null);
				boolean valueExists = value != null && value.toString().trim().length() > 0;
				if (!valueExists) {
					addProblem(CDIValidationMessages.PARAM_INJECTION_DECLARES_EMPTY_NAME, 
							CDIPreferences.PARAM_INJECTION_DECLARES_EMPTY_NAME, 
							named,
							injection.getResource(),
							PARAM_INJECTION_DECLARES_EMPTY_NAME_ID);
				}
			}
		}

		ITextSourceReference declaration = injection.getInjectAnnotation();
		if(declaration == null && injection instanceof IInjectionPointParameter) {
			declaration = injection;
		}

		/*
		 * 5.2.2. Legal injection point types
		 *  - injection point type is a type variable
		 */
		if(CDIUtil.isTypeVariable(injection, false)) {
			addProblem(CDIValidationMessages.INJECTION_TYPE_IS_VARIABLE, CDIPreferences.INJECTION_TYPE_IS_VARIABLE, declaration, injection.getResource());
		}

		if(declaration!=null) {
			Collection<IBean> beans = cdiProject.getBeans(true, injection);
			ITextSourceReference reference = injection instanceof IInjectionPointParameter?injection:declaration;
			/*
			 * 5.2.1. Unsatisfied and ambiguous dependencies
			 *  - If an unsatisfied or unresolvable ambiguous dependency exists, the container automatically detects the problem and treats it as a deployment problem.
			 */
			IType type = getTypeOfInjection(injection);
			if(!shouldIgnoreInjection(context, type, injection)) {
				boolean instance = type!=null && CDIConstants.INSTANCE_TYPE_NAME.equals(type.getFullyQualifiedName());
				if(!isAsYouTypeValidation()) {
					String injectionFilePath = injection.getSourcePath().toOSString();
					for (IBean bean : cdiProject.getBeans(false, injection)) {
						if(shouldValidateType(bean.getBeanClass())) {
							try {
								getValidationContext().addLinkedCoreResource(SHORT_ID, injectionFilePath, bean.getSourcePath(), false);
							} catch (NullPointerException e) {
								throw new RuntimeException("bean exists=" + bean.getBeanClass().exists() + " resource= " + bean.getResource() + " injection= " + injection.getSourcePath(),e);
							}
							for (IParametedType parametedType : bean.getAllTypes()) {
								IType beanType = parametedType.getType();
								if(beanType!=null && !beanType.isBinary()) {
									getValidationContext().addLinkedCoreResource(SHORT_ID, injectionFilePath, beanType.getPath(), false);
								}
							}
						}
					}
				}
				if(type!=null && beans.isEmpty() && !instance) {
					addProblem(CDIValidationMessages.UNSATISFIED_INJECTION_POINTS, CDIPreferences.UNSATISFIED_OR_AMBIGUOUS_INJECTION_POINTS, reference, injection.getResource(), UNSATISFIED_INJECTION_POINTS_ID);
				} else if(beans.size()>1  && !instance) {
					addProblem(CDIValidationMessages.AMBIGUOUS_INJECTION_POINTS, CDIPreferences.UNSATISFIED_OR_AMBIGUOUS_INJECTION_POINTS, reference, injection.getResource(), AMBIGUOUS_INJECTION_POINTS_ID);
				} else if(beans.size()==1) {
					IBean bean = beans.iterator().next();
					/*
					 * 5.2.4. Primitive types and null values
					 *  - injection point of primitive type resolves to a bean that may have null values, such as a producer method with a non-primitive return type or a producer field with a non-primitive type
					 */
					if(bean.isNullable() && injection.getType()!=null && injection.getType().isPrimitive()) {
						addProblem(CDIValidationMessages.INJECT_RESOLVES_TO_NULLABLE_BEAN, CDIPreferences.INJECT_RESOLVES_TO_NULLABLE_BEAN, reference, injection.getResource());
					}
					/*
					 * 5.1.4. Inter-module injection
					 *  - a decorator can not be injected
					 *  - an interceptor can not be injected
					 *  It is not an error - container just never attempts to inject them.
					 */
					/*
					 * 	5.4.1. Unproxyable bean types
					 * 	- If an injection point whose declared type cannot be proxied by the container resolves to a bean with a normal scope,
					 * 	  the container automatically detects the problem and treats it as a deployment problem.
					 */
					if(bean.getScope()!=null && bean.getScope().isNorlmalScope() && injection.getType()!=null) {
						 // - Array types cannot be proxied by the container.
						String typeSignature = injection.getType().getSignature();
						int kind = Signature.getTypeSignatureKind(typeSignature);
						if(kind == Signature.ARRAY_TYPE_SIGNATURE) {
							addProblem(MessageFormat.format(CDIValidationMessages.UNPROXYABLE_BEAN_ARRAY_TYPE, injection.getType().getSimpleName(), bean.getElementName()), CDIPreferences.UNPROXYABLE_BEAN_TYPE, reference, injection.getResource());
						} else if(injection.getType().isPrimitive()) {
							// - Primitive types cannot be proxied by the container.
							addProblem(MessageFormat.format(CDIValidationMessages.UNPROXYABLE_BEAN_PRIMITIVE_TYPE, injection.getType().getSimpleName(), bean.getElementName()), CDIPreferences.UNPROXYABLE_BEAN_TYPE, reference, injection.getResource());
						} else if(injection.getType().getType().exists()){
							try {
								if(Flags.isFinal(injection.getType().getType().getFlags())) {
									// - Classes which are declared final cannot be proxied by the container.
									addProblem(MessageFormat.format(CDIValidationMessages.UNPROXYABLE_BEAN_FINAL_TYPE, injection.getType().getSimpleName(), bean.getElementName()), CDIPreferences.UNPROXYABLE_BEAN_TYPE, reference, injection.getResource());
								} else {
									IMethod[] methods = injection.getType().getType().getMethods();
									boolean hasDefaultConstructor = false;
									boolean hasConstructor = false;
									for (IMethod method : methods) {
										hasConstructor = hasConstructor || method.isConstructor();
										hasDefaultConstructor = hasDefaultConstructor || (method.isConstructor() && !Flags.isPrivate(method.getFlags()) && method.getParameterNames().length==0);
										if(Flags.isFinal(method.getFlags())) {
											// - Classes which have final methods cannot be proxied by the container.
											addProblem(MessageFormat.format(CDIValidationMessages.UNPROXYABLE_BEAN_TYPE_WITH_FM, injection.getType().getSimpleName(), bean.getElementName()), CDIPreferences.UNPROXYABLE_BEAN_TYPE, reference, injection.getResource());
											hasDefaultConstructor = true;
											break;
										}
									}
									if(!hasDefaultConstructor && hasConstructor) {
										// - Classes which don't have a non-private constructor with no parameters cannot be proxied by the container.
										addProblem(MessageFormat.format(CDIValidationMessages.UNPROXYABLE_BEAN_TYPE_WITH_NPC, injection.getType().getSimpleName(), bean.getElementName()), CDIPreferences.UNPROXYABLE_BEAN_TYPE, reference, injection.getResource());
									}
								}
							} catch (JavaModelException e) {
								CDICorePlugin.getDefault().logError(e);
							}
						}
					}
					if(injection.getClassBean() instanceof IDecorator && injection.isDelegate() && bean instanceof IClassBean && bean.getBeanClass().exists()) {
						try {
							IType beanClass = bean.getBeanClass();
							if(Flags.isFinal(beanClass.getFlags())) {
								//	8.3. Decorator resolution 
								//	- If a decorator matches a managed bean, and the managed bean class is declared final, the container automatically detects 
								//	  the problem and treats it as a deployment problem.
								addProblem(MessageFormat.format(CDIValidationMessages.DECORATOR_RESOLVES_TO_FINAL_CLASS, bean.getElementName()), CDIPreferences.DECORATOR_RESOLVES_TO_FINAL_BEAN, reference, injection.getResource());
							} else {
								//	8.3. Decorator resolution 
								//	- If a decorator matches a managed bean with a non-static, non-private, final method, and the decorator also implements that method,
								//    the container automatically detects  the problem and treats it as a deployment problem. 
								IType decoratorClass = injection.getClassBean().getBeanClass();
								IMethod[] methods = decoratorClass.getMethods();
								boolean reported = false;
								if(methods!=null) {
									for (IMethod method : methods) {
										if(!Flags.isPrivate(method.getFlags()) && !Flags.isStatic(method.getFlags())) {
											IMethod[] beanMethods = beanClass.findMethods(method);
											if(beanMethods!=null) {
												for (IMethod beanMethod : beanMethods) {
													int flags = beanMethod.getFlags();
													if(!Flags.isPrivate(flags) && !Flags.isStatic(flags) && Flags.isFinal(flags)) {
														String methodName = Signature.toString(beanMethod.getSignature(), beanMethod.getElementName(), beanMethod.getParameterNames(), false, false);
														addProblem(MessageFormat.format(CDIValidationMessages.DECORATOR_RESOLVES_TO_FINAL_METHOD, bean.getElementName(), methodName), CDIPreferences.DECORATOR_RESOLVES_TO_FINAL_BEAN, reference, injection.getResource());
														reported = true;
														break;
													}
												}
												if(reported) {
													break;
												}
											}
										}
									}
								}
							}
						} catch (JavaModelException e) {
							CDICorePlugin.getDefault().logError(e);
						}
					}
				}
			}
			/*
			 * 5.5.7. Injection point metadata
			 *  - bean that declares any scope other than @Dependent has an injection point of type InjectionPoint and qualifier @Default
			 */
			if(type!=null && CDIConstants.INJECTIONPOINT_TYPE_NAME.equals(type.getFullyQualifiedName())) {
				IScope beanScope = injection.getBean().getScope();
				if(injection.hasDefaultQualifier() && beanScope!=null && !CDIConstants.DEPENDENT_ANNOTATION_TYPE_NAME.equals(beanScope.getSourceType().getFullyQualifiedName())) {
					addProblem(CDIValidationMessages.ILLEGAL_SCOPE_WHEN_TYPE_INJECTIONPOINT_IS_INJECTED, CDIPreferences.ILLEGAL_SCOPE_WHEN_TYPE_INJECTIONPOINT_IS_INJECTED, reference, injection.getResource());
				}
			}
		}
		/*
		 * 8.1.2. Decorator delegate injection points
		 *  - bean class that is not a decorator has an injection point annotated @Delegate
		 */
		if(!(injection.getClassBean() instanceof IDecorator) && injection.isDelegate()) {
			ITextSourceReference reference = injection.getDelegateAnnotation();
			addProblem(CDIValidationMessages.ILLEGAL_BEAN_DECLARING_DELEGATE, CDIPreferences.ILLEGAL_BEAN_DECLARING_DELEGATE, reference, injection.getResource());
		}
	}

	private void validateNormalBeanScope(IBean bean) {
		if(bean.getScope()!=null && bean.getScope().isNorlmalScope()) {
			ITextSourceReference reference = null;
			Collection<IScopeDeclaration> scopes = bean.getScopeDeclarations();
			if(!scopes.isEmpty()) {
				reference = scopes.iterator().next();
			} else {
				reference = bean.getNameLocation(false);
			}
			if(reference == null) {
				return;
			}
			for (IParametedType type: bean.getLegalTypes()) {
			 // - Array types cannot be proxied by the container.
			String typeSignature = type.getSignature();
			int kind = Signature.getTypeSignatureKind(typeSignature);
			if(kind == Signature.ARRAY_TYPE_SIGNATURE) {
				if("Object[]".equals(type.getSimpleName()) && bean.getLegalTypes().size() > 1) continue; //There is another type
				addProblem(MessageFormat.format(CDIValidationMessages.UNPROXYABLE_BEAN_ARRAY_TYPE_2, type.getSimpleName(), bean.getElementName()), CDIPreferences.UNPROXYABLE_BEAN_TYPE, reference, bean.getResource());
			} else if(type.isPrimitive()) {
				// - Primitive types cannot be proxied by the container.
				addProblem(MessageFormat.format(CDIValidationMessages.UNPROXYABLE_BEAN_PRIMITIVE_TYPE_2, type.getSimpleName(), bean.getElementName()), CDIPreferences.UNPROXYABLE_BEAN_TYPE, reference, bean.getResource());
			} else if(type.getType().exists() && !"java.lang.Object".equals(type.getType().getFullyQualifiedName())) {
				try {
					if(Flags.isFinal(type.getType().getFlags())) {
						// - Classes which are declared final cannot be proxied by the container.
						addProblem(MessageFormat.format(CDIValidationMessages.UNPROXYABLE_BEAN_FINAL_TYPE_2, type.getSimpleName(), bean.getElementName()), CDIPreferences.UNPROXYABLE_BEAN_TYPE, reference, bean.getResource());
					} else {
						IMethod[] methods = type.getType().getMethods();
						boolean hasDefaultConstructor = false;
						boolean hasConstructor = false;
						for (IMethod method : methods) {
							hasConstructor = hasConstructor || method.isConstructor();
							hasDefaultConstructor = hasDefaultConstructor || (method.isConstructor() && !Flags.isPrivate(method.getFlags()) && method.getParameterNames().length==0);
							if(Flags.isFinal(method.getFlags())) {
								// - Classes which have final methods cannot be proxied by the container.
								addProblem(MessageFormat.format(CDIValidationMessages.UNPROXYABLE_BEAN_TYPE_WITH_FM_2, type.getSimpleName(), bean.getElementName()), CDIPreferences.UNPROXYABLE_BEAN_TYPE, reference, bean.getResource());
								hasDefaultConstructor = true;
								break;
							}
						}
						if(!hasDefaultConstructor && hasConstructor) {
							// - Classes which don't have a non-private constructor with no parameters cannot be proxied by the container.
							addProblem(MessageFormat.format(CDIValidationMessages.UNPROXYABLE_BEAN_TYPE_WITH_NPC_2, type.getSimpleName(), bean.getElementName()), CDIPreferences.UNPROXYABLE_BEAN_TYPE, reference, bean.getResource());
						}
					}
				} catch (JavaModelException e) {
					CDICorePlugin.getDefault().logError(e);
				}
			}

			}
		}
		
	}

	/**
	 * Validates class bean which may be both a session and decorator (or interceptor).
	 * 
	 * @param bean
	 */
	private void validateMixedClassBean(IClassBean bean) {
		ITextSourceReference sessionDeclaration = CDIUtil.getSessionDeclaration(bean);
		ITextSourceReference decoratorDeclaration = bean.getAnnotation(CDIConstants.DECORATOR_STEREOTYPE_TYPE_NAME);
		ITextSourceReference interceptorDeclaration = bean.getAnnotation(CDIConstants.INTERCEPTOR_ANNOTATION_TYPE_NAME);

		if (sessionDeclaration != null) {
			/*
			 * 3.2. Session beans
			 *  - bean class of a session bean is annotated @Decorator
			 */
			if (decoratorDeclaration != null) {
				addProblem(CDIValidationMessages.SESSION_BEAN_ANNOTATED_DECORATOR, CDIPreferences.SESSION_BEAN_ANNOTATED_INTERCEPTOR_OR_DECORATOR,
						sessionDeclaration, bean.getResource(), SESSION_BEAN_ANNOTATED_DECORATOR_ID);
				addProblem(CDIValidationMessages.SESSION_BEAN_ANNOTATED_DECORATOR, CDIPreferences.SESSION_BEAN_ANNOTATED_INTERCEPTOR_OR_DECORATOR,
						decoratorDeclaration, bean.getResource(), SESSION_BEAN_ANNOTATED_DECORATOR_ID);
			}
			/*
			 * 3.2. Session beans
			 *  - bean class of a session bean is annotated @Interceptor
			 */
			if (interceptorDeclaration != null) {
				addProblem(CDIValidationMessages.SESSION_BEAN_ANNOTATED_INTERCEPTOR, CDIPreferences.SESSION_BEAN_ANNOTATED_INTERCEPTOR_OR_DECORATOR,
						sessionDeclaration, bean.getResource(), SESSION_BEAN_ANNOTATED_INTERCEPTOR_ID);
				addProblem(CDIValidationMessages.SESSION_BEAN_ANNOTATED_INTERCEPTOR, CDIPreferences.SESSION_BEAN_ANNOTATED_INTERCEPTOR_OR_DECORATOR,
						interceptorDeclaration, bean.getResource(), SESSION_BEAN_ANNOTATED_INTERCEPTOR_ID);
			}
		}
	}

	private void validateSessionBean(ISessionBean bean) {
		IAnnotationDeclaration declaration = CDIUtil.getDifferentScopeDeclarationThanDepentend(bean);
		if (declaration != null) {
			IType type = bean.getBeanClass();
			try {
				/*
				 * 3.2. Session beans
				 *  - session bean with a parameterized bean class declares any scope other than @Dependent
				 */
				String[] typeVariables = type.getTypeParameterSignatures();
				if (typeVariables.length > 0) {
					addProblem(CDIValidationMessages.ILLEGAL_SCOPE_FOR_SESSION_BEAN_WITH_GENERIC_TYPE, CDIPreferences.ILLEGAL_SCOPE_FOR_BEAN,
							declaration, bean.getResource());
				} else {
					if (bean.isStateless()) {
						/*
						 * 3.2. Session beans
						 *  - session bean specifies an illegal scope (a stateless session bean must belong to the @Dependent pseudo-scope)
						 */
						if (declaration != null) {
							addProblem(CDIValidationMessages.ILLEGAL_SCOPE_FOR_STATELESS_SESSION_BEAN, CDIPreferences.ILLEGAL_SCOPE_FOR_BEAN,
									declaration, bean.getResource());
						}
					} else if (bean.isSingleton()) {
						/*
						 * 3.2. Session beans
						 *  - session bean specifies an illegal scope (a singleton bean must belong to either the @ApplicationScoped scope or to the @Dependent pseudo-scope)
						 */
						if (declaration != null) {
							declaration = CDIUtil.getDifferentScopeDeclarationThanApplicationScoped(bean);
						}
						if (declaration != null) {
							addProblem(CDIValidationMessages.ILLEGAL_SCOPE_FOR_SINGLETON_SESSION_BEAN, CDIPreferences.ILLEGAL_SCOPE_FOR_BEAN,
									declaration, bean.getResource());
						}
					}
				}
			} catch (JavaModelException e) {
				CDICorePlugin.getDefault().logError(e);
			}
		}
		/*
		 * 3.2.4. Specializing a session bean
		 *  - session bean class annotated @Specializes does not directly extend the bean class of another session bean
		 */
		IAnnotationDeclaration specializesDeclaration = bean.getSpecializesAnnotationDeclaration();
		if (specializesDeclaration != null) {
			saveAllSuperTypesAsLinkedResources(bean);
			IBean sBean = bean.getSpecializedBean();
			if (sBean == null) {
				// The specializing bean extends nothing
				addProblem(CDIValidationMessages.ILLEGAL_SPECIALIZING_SESSION_BEAN, CDIPreferences.ILLEGAL_SPECIALIZING_BEAN, specializesDeclaration,
						bean.getResource());
			} else if (!CDIUtil.isSessionBean(sBean)) {
				// The specializing bean directly extends a non-session bean class
				addProblem(CDIValidationMessages.ILLEGAL_SPECIALIZING_SESSION_BEAN, CDIPreferences.ILLEGAL_SPECIALIZING_BEAN, specializesDeclaration,
						bean.getResource());
			}
		}
	}

	private void validateManagedBean(IClassBean bean) {
		/*
		 * 3.1. Managed beans
		 *  - the bean class of a managed bean is annotated with both the @Interceptor and @Decorator stereotypes
		 */
		IAnnotationDeclaration decorator = bean.getAnnotation(CDIConstants.DECORATOR_STEREOTYPE_TYPE_NAME);
		IAnnotationDeclaration interceptor = bean.getAnnotation(CDIConstants.INTERCEPTOR_ANNOTATION_TYPE_NAME);
		if (decorator != null && interceptor != null) {
			addProblem(CDIValidationMessages.BOTH_INTERCEPTOR_AND_DECORATOR, CDIPreferences.BOTH_INTERCEPTOR_AND_DECORATOR, decorator, bean.getResource());
			addProblem(CDIValidationMessages.BOTH_INTERCEPTOR_AND_DECORATOR, CDIPreferences.BOTH_INTERCEPTOR_AND_DECORATOR, interceptor, bean.getResource());
		}

		IAnnotationDeclaration declaration = CDIUtil.getDifferentScopeDeclarationThanDepentend(bean);
		if (declaration != null) {
			IType type = bean.getBeanClass();
			try {
				/*
				 * 3.1. Managed beans
				 *  - managed bean with a public field declares any scope other than @Dependent
				 */
				IField[] fields = type.getFields();
				for (IField field : fields) {
					if (Flags.isPublic(field.getFlags()) && !Flags.isStatic(field.getFlags())) {
						ITextSourceReference fieldReference = CDIUtil.convertToSourceReference(field.getNameRange(), bean.getResource(), field);
						addProblem(CDIValidationMessages.ILLEGAL_SCOPE_FOR_MANAGED_BEAN_WITH_PUBLIC_FIELD, CDIPreferences.ILLEGAL_SCOPE_FOR_BEAN,
								fieldReference, bean.getResource(), ILLEGAL_SCOPE_FOR_MANAGED_BEAN_WITH_PUBLIC_FIELD_ID);
					}
				}
				/*
				 * 3.1. Managed beans
				 *  - managed bean with a parameterized bean class declares any scope other than @Dependent
				 */
				String[] typeVariables = type.getTypeParameterSignatures();
				if (typeVariables.length > 0) {
					addProblem(CDIValidationMessages.ILLEGAL_SCOPE_FOR_MANAGED_BEAN_WITH_GENERIC_TYPE, CDIPreferences.ILLEGAL_SCOPE_FOR_BEAN,
							declaration, bean.getResource());
				}
			} catch (JavaModelException e) {
				CDICorePlugin.getDefault().logError(e);
			}
		}
		/*
		 * 3.1.4. Specializing a managed bean
		 *  - managed bean class annotated @Specializes does not directly extend the bean class of another managed bean
		 */
		IAnnotationDeclaration specializesDeclaration = bean.getSpecializesAnnotationDeclaration();
		if (specializesDeclaration != null) {
			saveAllSuperTypesAsLinkedResources(bean);
			try {
				IBean sBean = bean.getSpecializedBean();
				if (sBean != null) {
					if (sBean instanceof ISessionBean || sBean.getAnnotation(CDIConstants.STATELESS_ANNOTATION_TYPE_NAME) != null
							|| sBean.getAnnotation(CDIConstants.SINGLETON_ANNOTATION_TYPE_NAME) != null) {
						// The specializing bean directly extends an enterprise bean class
						addProblem(CDIValidationMessages.ILLEGAL_SPECIALIZING_MANAGED_BEAN, CDIPreferences.ILLEGAL_SPECIALIZING_BEAN,
								specializesDeclaration, bean.getResource());
					} else {
						// Validate the specializing bean extends a non simple bean
						boolean hasDefaultConstructor = true;
						IMethod[] methods = sBean.getBeanClass().getMethods();
						for (IMethod method : methods) {
							if (method.isConstructor()) {
								if (Flags.isPublic(method.getFlags()) && method.getParameterNames().length == 0) {
									hasDefaultConstructor = true;
									break;
								}
								hasDefaultConstructor = false;
							}
						}
						if (!hasDefaultConstructor) {
							addProblem(CDIValidationMessages.ILLEGAL_SPECIALIZING_MANAGED_BEAN, CDIPreferences.ILLEGAL_SPECIALIZING_BEAN,	specializesDeclaration, bean.getResource());
						}
					}
				} else {
					// The specializing bean extends nothing
					addProblem(CDIValidationMessages.ILLEGAL_SPECIALIZING_MANAGED_BEAN, CDIPreferences.ILLEGAL_SPECIALIZING_BEAN, specializesDeclaration, bean.getResource());
				}
			} catch (JavaModelException e) {
				CDICorePlugin.getDefault().logError(e);
			}
		}

		try {
			/*
			 * 9.3. Binding an interceptor to a bean
			 *  - managed bean has a class level interceptor binding and is declared final or has a non-static, non-private, final method
			 *  - non-static, non-private, final method of a managed bean has a method level interceptor binding
			 */
			Collection<IInterceptorBinding> bindings = bean.getInterceptorBindings();
			if(!bindings.isEmpty()) {
				if(Flags.isFinal(bean.getBeanClass().getFlags())) {
					ITextSourceReference reference = CDIUtil.convertToSourceReference(bean.getBeanClass().getNameRange(), bean.getResource(), bean.getBeanClass());
					addProblem(CDIValidationMessages.ILLEGAL_INTERCEPTOR_BINDING_CLASS, CDIPreferences.ILLEGAL_INTERCEPTOR_BINDING_METHOD, reference, bean.getResource());
				} else {
					IMethod[] methods = bean.getBeanClass().getMethods();
					for (int i = 0; i < methods.length; i++) {
						int flags = methods[i].getFlags();
						if(Flags.isFinal(flags) && !Flags.isStatic(flags) && !Flags.isPrivate(flags)) {
							ITextSourceReference reference = CDIUtil.convertToSourceReference(methods[i].getNameRange(), bean.getResource(), methods[i]);
							addProblem(CDIValidationMessages.ILLEGAL_INTERCEPTOR_BINDING_METHOD, CDIPreferences.ILLEGAL_INTERCEPTOR_BINDING_METHOD, reference, bean.getResource());
						}
					}
				}
			} else {
				for (IBeanMethod method : bean.getAllMethods()) {
					if(!method.getInterceptorBindings().isEmpty()) {
						if(Flags.isFinal(bean.getBeanClass().getFlags())) {
							ITextSourceReference reference = CDIUtil.convertToSourceReference(bean.getBeanClass().getNameRange(), bean.getResource(), bean.getBeanClass());
							addProblem(CDIValidationMessages.ILLEGAL_INTERCEPTOR_BINDING_CLASS, CDIPreferences.ILLEGAL_INTERCEPTOR_BINDING_METHOD, reference, bean.getResource());
						} else {
							IMethod sourceMethod = method.getMethod();
							int flags = sourceMethod.getFlags();
							if(Flags.isFinal(flags) && !Flags.isStatic(flags) && !Flags.isPrivate(flags)) {
								ITextSourceReference reference = CDIUtil.convertToSourceReference(sourceMethod.getNameRange(), bean.getResource(), sourceMethod);
								addProblem(CDIValidationMessages.ILLEGAL_INTERCEPTOR_BINDING_METHOD, CDIPreferences.ILLEGAL_INTERCEPTOR_BINDING_METHOD, reference, bean.getResource());
							}
						}
					}
				}
			}

			/*
			 * 6.6.4 Validation of passivation capable beans and dependencies
			 * - If a managed bean which declares a passivating scope is not passivation capable, then the container automatically detects the problem and treats it as a deployment problem.
	 		 */
			if(bean.getScopeDeclarations().size()<2) { // Ignore broken beans with multiple scope declarations.
				IScope scope = bean.getScope();
				if(scope!=null && scope.isNorlmalScope()) {
					IAnnotationDeclaration normalScopeDeclaration = scope.getAnnotationDeclaration(CDIConstants.NORMAL_SCOPE_ANNOTATION_TYPE_NAME);
					if(normalScopeDeclaration != null) {
						boolean passivatingScope = "true".equalsIgnoreCase("" + normalScopeDeclaration.getMemberValue("passivating"));
						if(passivatingScope) {
							boolean passivatingCapable = false;
							for (IParametedType type : bean.getAllTypes()) {
								if("java.io.Serializable".equals(type.getType().getFullyQualifiedName())) {
									passivatingCapable = true;
									break;
								}
							}
							if(!passivatingCapable) {
								ITextSourceReference reference = CDIUtil.convertToSourceReference(bean.getBeanClass().getNameRange(), bean.getResource(), bean.getBeanClass());
								addProblem(MessageFormat.format(CDIValidationMessages.NOT_PASSIVATION_CAPABLE_BEAN, bean.getElementName(), scope.getSourceType().getElementName()), CDIPreferences.NOT_PASSIVATION_CAPABLE_BEAN, reference, bean.getResource(), NOT_PASSIVATION_CAPABLE_BEAN_ID);
							}
						}
					}
				}
			}
		} catch (JavaModelException e) {
			CDICorePlugin.getDefault().logError(e);
		}
	}

	private void validateInterceptor(IInterceptor interceptor) {
		/*
		 * 2.5.3. Beans with no EL name
		 *  - interceptor has a name (Non-Portable behavior)
		 */
		if (interceptor.getName() != null) {
			ITextSourceReference declaration = interceptor.getAnnotation(CDIConstants.NAMED_QUALIFIER_TYPE_NAME);
			if (declaration == null) {
				declaration = interceptor.getAnnotation(CDIConstants.INTERCEPTOR_ANNOTATION_TYPE_NAME);
			}
			if (declaration == null) {
				declaration = CDIUtil.getNamedStereotypeDeclaration(interceptor);
			}
			addProblem(CDIValidationMessages.INTERCEPTOR_HAS_NAME, CDIPreferences.INTERCEPTOR_OR_DECORATOR_HAS_NAME, declaration, interceptor.getResource(), INTERCEPTOR_HAS_NAME_ID);
		}

		/*
		 * 2.6.1. Declaring an alternative
		 *  - interceptor is an alternative (Non-Portable behavior)
		 */
		if (interceptor.isAlternative()) {
			ITextSourceReference declaration = interceptor.getAlternativeDeclaration();
			if (declaration == null) {
				declaration = interceptor.getInterceptorAnnotation();
			}
			if(declaration == null) {
				//for custom implementations
				declaration = interceptor.getNameLocation(true);
			}
			addProblem(CDIValidationMessages.INTERCEPTOR_IS_ALTERNATIVE, CDIPreferences.INTERCEPTOR_OR_DECORATOR_IS_ALTERNATIVE, declaration, interceptor
					.getResource());
		}
		/*
		 * 3.3.2. Declaring a producer method
		 *  - interceptor has a method annotated @Produces
		 *  
		 * 3.4.2. Declaring a producer field
		 *  - interceptor has a field annotated @Produces
		 */
		for (IProducer producer : interceptor.getProducers()) {
			addProblem(CDIValidationMessages.PRODUCER_IN_INTERCEPTOR, CDIPreferences.PRODUCER_IN_INTERCEPTOR_OR_DECORATOR, producer.getProducesAnnotation(), interceptor.getResource(), PRODUCER_IN_INTERCEPTOR_ID);
		}
		/*
		 * 9.2. Declaring the interceptor bindings of an interceptor
		 *  - interceptor declared using @Interceptor does not declare any interceptor binding (Non-Portable behavior)
		 */
		Collection<IInterceptorBinding> bindings = interceptor.getInterceptorBindings();
		if(bindings.isEmpty()) {
			ITextSourceReference declaration = interceptor.getAnnotation(CDIConstants.INTERCEPTOR_ANNOTATION_TYPE_NAME);
			if(declaration!=null) {
				addProblem(CDIValidationMessages.MISSING_INTERCEPTOR_BINDING, CDIPreferences.MISSING_INTERCEPTOR_BINDING, declaration, interceptor.getResource());
			}
		} else {
			/*
			 * 9.2. Declaring the interceptor bindings of an interceptor
			 *  - interceptor for lifecycle callbacks declares an interceptor binding type that is defined @Target({TYPE, METHOD})
			 */
			for (IInterceptorBinding binding : bindings) {
				boolean markedAsWrong = false;
				IAnnotationDeclaration target = binding.getAnnotationDeclaration(CDIConstants.TARGET_ANNOTATION_TYPE_NAME);
				if(target!=null) {
					Object value = target.getMemberValue(null);
					if(value instanceof Object[]) {
						Object[] values = (Object[]) value;
						if(values.length>1) {
							for (IBeanMethod method : interceptor.getAllMethods()) {
								if(method.isLifeCycleCallbackMethod()) {
									ITextSourceReference declaration = CDIUtil.getAnnotationDeclaration(interceptor, binding);
									if(declaration==null) {
										declaration = interceptor.getInterceptorAnnotation();
									}
									addProblem(CDIValidationMessages.ILLEGAL_LIFECYCLE_CALLBACK_INTERCEPTOR_BINDING, CDIPreferences.ILLEGAL_LIFECYCLE_CALLBACK_INTERCEPTOR_BINDING, declaration, interceptor.getResource());
									markedAsWrong = true;
									break;
								}
							}
						}
					}
				}
				if(markedAsWrong) {
					break;
				}
			}
		}
	}

	private void validateDecorator(CDIValidationContext context, IDecorator decorator) {
		/*
		 * 2.5.3. Beans with no EL name
		 *  - decorator has a name (Non-Portable behavior)
		 */
		if (decorator.getName() != null) {
			ITextSourceReference declaration = decorator.getAnnotation(CDIConstants.NAMED_QUALIFIER_TYPE_NAME);
			if (declaration == null) {
				declaration = decorator.getAnnotation(CDIConstants.DECORATOR_STEREOTYPE_TYPE_NAME);
			}
			if (declaration == null) {
				declaration = CDIUtil.getNamedStereotypeDeclaration(decorator);
			}
			addProblem(CDIValidationMessages.DECORATOR_HAS_NAME, CDIPreferences.INTERCEPTOR_OR_DECORATOR_HAS_NAME, declaration, decorator.getResource(), DECORATOR_HAS_NAME_ID);
		}

		/*
		 * 2.6.1. Declaring an alternative
		 *  - decorator is an alternative (Non-Portable behavior)
		 */
		if (decorator.isAlternative()) {
			ITextSourceReference declaration = decorator.getAlternativeDeclaration();
			if (declaration == null) {
				declaration = decorator.getDecoratorAnnotation();
			}
			if(declaration == null) {
				//for custom implementations
				declaration = decorator.getNameLocation(true);
			}
			addProblem(CDIValidationMessages.DECORATOR_IS_ALTERNATIVE, CDIPreferences.INTERCEPTOR_OR_DECORATOR_IS_ALTERNATIVE, declaration, decorator.getResource());
		}

		/*
		 * 3.3.2. Declaring a producer method
		 *  - decorator has a method annotated @Produces
		 *  
		 * 3.4.2. Declaring a producer field
		 *  - decorator has a field annotated @Produces
		 */
		for (IProducer producer : decorator.getProducers()) {
			addProblem(CDIValidationMessages.PRODUCER_IN_DECORATOR, CDIPreferences.PRODUCER_IN_INTERCEPTOR_OR_DECORATOR, producer.getProducesAnnotation(), decorator.getResource(), PRODUCER_IN_DECORATOR_ID);
		}

		Set<ITextSourceReference> delegates = new HashSet<ITextSourceReference>();
		IInjectionPoint delegate = null;
		for (IInjectionPoint injection : decorator.getInjectionPoints(true)) {
			ITextSourceReference delegateAnnotation = injection.getDelegateAnnotation();
			if(delegateAnnotation!=null) {
				if(injection instanceof IInjectionPointField) {
					delegate = injection;
					delegates.add(delegateAnnotation);
				}
				if(injection instanceof IInjectionPointParameter) {
					if(((IInjectionPointParameter) injection).getBeanMethod().getAnnotation(CDIConstants.PRODUCES_ANNOTATION_TYPE_NAME)==null) {
						delegate = injection;
						delegates.add(delegateAnnotation);
					} else {
						/*
						 * 8.1.2. Decorator delegate injection points
						 *  - injection point that is not an injected field, initializer method parameter or bean constructor method parameter is annotated @Delegate
						 */
						addProblem(CDIValidationMessages.ILLEGAL_INJECTION_POINT_DELEGATE, CDIPreferences.ILLEGAL_INJECTION_POINT_DELEGATE, delegateAnnotation, decorator.getResource());
					}
				}
			}
		}
		if(delegates.size()>1) {
			/*
			 * 8.1.2. Decorator delegate injection points
			 *  - decorator has more than one delegate injection point
			 */
			for (ITextSourceReference declaration : delegates) {
				addProblem(CDIValidationMessages.MULTIPLE_DELEGATE, CDIPreferences.MULTIPLE_OR_MISSING_DELEGATE, declaration, decorator.getResource());
			}
		} else if(delegates.isEmpty()) {
			/*
			 * 8.1.2. Decorator delegate injection points
			 *  - decorator does not have a delegate injection point
			 */
			IAnnotationDeclaration declaration = decorator.getDecoratorAnnotation();
			addProblem(CDIValidationMessages.MISSING_DELEGATE, CDIPreferences.MULTIPLE_OR_MISSING_DELEGATE, declaration, decorator.getResource());
		}

		/*
		 * 8.1.3. Decorator delegate injection points
		 *  - delegate type does not implement or extend a decorated type of the decorator, or specifies different type parameters
		 */
		if(delegate!=null) {
			IParametedType delegateParametedType = delegate.getType();
			if(delegateParametedType!=null) {
				IType delegateType = delegateParametedType.getType();
				if(delegateType != null) {
					if(!checkTheOnlySuper(context, decorator, delegateParametedType)) {
						List<String> supers = null;
						if(!isAsYouTypeValidation() && shouldValidateType(delegateType)) {
							getValidationContext().addLinkedCoreResource(SHORT_ID, decorator.getSourcePath().toOSString(), delegateType.getResource().getFullPath(), false);
						}
						for (IParametedType decoratedParametedType : decorator.getDecoratedTypes()) {
							IType decoratedType = decoratedParametedType.getType();
							if(decoratedType==null) {
								continue;
							}
							if(!isAsYouTypeValidation() && shouldValidateType(decoratedType)) {
								getValidationContext().addLinkedCoreResource(SHORT_ID, decorator.getSourcePath().toOSString(), decoratedType.getResource().getFullPath(), false);
							}
							String decoratedTypeName = decoratedType.getFullyQualifiedName();
							// Ignore the type of the decorator class bean
							if(decoratedTypeName.equals(decorator.getBeanClass().getFullyQualifiedName())) {
								continue;
							}
							if(decoratedTypeName.equals("java.lang.Object")) { //$NON-NLS-1$
								continue;
							}
							if(supers==null) {
								supers = getSuppers(delegateParametedType);
							}
							if(supers.contains(decoratedParametedType.getSignature())) {
								continue;
							} else {
								ITextSourceReference declaration = delegate.getDelegateAnnotation();
								if(delegateParametedType instanceof ITypeDeclaration) {
									declaration = CDIUtil.convertToJavaSourceReference((ITypeDeclaration)delegateParametedType, delegate.getSourceMember());
								}
								String typeName = Signature.getSignatureSimpleName(decoratedParametedType.getSignature());
								addProblem(MessageFormat.format(CDIValidationMessages.DELEGATE_HAS_ILLEGAL_TYPE, typeName), CDIPreferences.DELEGATE_HAS_ILLEGAL_TYPE, declaration, decorator.getResource());
								break;
							}
						}
					}
				}
			}
		}
	}

	private boolean checkTheOnlySuper(CDIValidationContext context, IDecorator decorator, IParametedType delegatedType) {
		ICDIProject cdiProject = context.getCdiProject();
		try {
			String superClassSignature = decorator.getBeanClass().getSuperclassTypeSignature();
			String[] superInterfaceSignatures = decorator.getBeanClass().getSuperInterfaceTypeSignatures();
			if(superClassSignature==null) {
				if(superInterfaceSignatures.length==0) {
					return true;
				}
				if(superInterfaceSignatures.length>1) {
					return false;
				}
				IParametedType superType = cdiProject.getNature().getTypeFactory().getParametedType(decorator.getBeanClass(), superInterfaceSignatures[0]);
				return superType==null?true:superType.getSignature().equals(delegatedType.getSignature());
			} else if(superInterfaceSignatures.length>0) {
				return false;
			}
			IParametedType superType = cdiProject.getNature().getTypeFactory().getParametedType(decorator.getBeanClass(), superClassSignature);
			if(superType!=null) {
				superType.getSignature().equals(delegatedType.getSignature());
			}
		} catch (JavaModelException e) {
			CDICorePlugin.getDefault().logError(e);
		}
		return true;
	}

	private List<String> getSuppers(IParametedType type) {
		List<String> signatures = new ArrayList<String>();
		for (IParametedType superType : ((ParametedType)type).getAllTypes()) {
			signatures.add(superType.getSignature());
		}
		signatures.add(type.getSignature());
		return signatures;
	}

	/*
	 * 2.2.2. Restricting the bean types of a bean
	 *  - bean class or producer method or field specifies a @Typed annotation, and the value member specifies a class which does not correspond to a type in the unrestricted set of bean types of a bean
	 */
	private void validateTyped(IBean bean) {
		Collection<ITypeDeclaration> typedDeclarations = bean.getRestrictedTypeDeclaratios();
		if (!typedDeclarations.isEmpty()) {
			saveAllSuperTypesAsLinkedResources(bean);
			Set<String> allTypeNames = new HashSet<String>();
			for (IParametedType type : bean.getAllTypes()) {
				if(type.getType() != null) allTypeNames.add(type.getType().getFullyQualifiedName());
			}
			for (ITypeDeclaration typedDeclaration : typedDeclarations) {
				IType typedType = typedDeclaration.getType();
				if (typedType != null && !allTypeNames.contains(typedType.getFullyQualifiedName())) {
					IMember e = bean instanceof IJavaReference ? ((IJavaReference)bean).getSourceMember() : bean.getBeanClass();
					ITextSourceReference typedDeclarationReference = CDIUtil.convertToJavaSourceReference(typedDeclaration, e);

					String message = CDIValidationMessages.ILLEGAL_TYPE_IN_TYPED_DECLARATION;
					addProblem(message, CDIPreferences.ILLEGAL_TYPE_IN_TYPED_DECLARATION, typedDeclarationReference, bean.getResource());
				}
			}
		}
	}

	private void validateBeanScope(IBean bean) {
		Collection<IScopeDeclaration> scopes = bean.getScopeDeclarations();
		// 2.4.3. Declaring the bean scope
		//   - bean class or producer method or field specifies multiple scope type annotations
		//
		if (scopes.size() > 1) {
			String message = bean instanceof IClassBean
				? CDIValidationMessages.MULTIPLE_SCOPE_TYPE_ANNOTATIONS_IN_BEAN_CLASS
				: bean instanceof IProducerField
				? CDIValidationMessages.MULTIPLE_SCOPE_TYPE_ANNOTATIONS_IN_PRODUCER_FIELD
				: bean instanceof IProducerMethod
				? CDIValidationMessages.MULTIPLE_SCOPE_TYPE_ANNOTATIONS_IN_PRODUCER_METHOD
				: CDIValidationMessages.MULTIPLE_SCOPE_TYPE_ANNOTATIONS;
			for (IScopeDeclaration scope : scopes) {
				addProblem(message, CDIPreferences.MULTIPLE_SCOPE_TYPE_ANNOTATIONS, scope, bean.getResource());
			}
		}

		// 2.4.4. Default scope
		// - bean does not explicitly declare a scope when there is no default scope (there are two different stereotypes declared by the bean that declare different default scopes)
		// 
		// Such bean definitions are invalid because they declares two
		// stereotypes that have different default scopes and the bean does not
		// explictly define a scope to resolve the conflict.
		Collection<IStereotypeDeclaration> stereotypeDeclarations = bean.getStereotypeDeclarations();
		if (!stereotypeDeclarations.isEmpty() && scopes.isEmpty()) {
			Map<String, IStereotypeDeclaration> declarationMap = new HashMap<String, IStereotypeDeclaration>();
			for (IStereotypeDeclaration stereotypeDeclaration : stereotypeDeclarations) {
				IStereotype stereotype = stereotypeDeclaration.getStereotype();
				IScope scope = stereotype.getScope();
				if (scope != null) {
					declarationMap.put(scope.getSourceType().getFullyQualifiedName(), stereotypeDeclaration);
				}
			}
			if (declarationMap.size() > 1) {
				for (IStereotypeDeclaration stereotypeDeclaration : declarationMap.values()) {
					addProblem(CDIValidationMessages.MISSING_SCOPE_WHEN_THERE_IS_NO_DEFAULT_SCOPE, CDIPreferences.MISSING_SCOPE_WHEN_THERE_IS_NO_DEFAULT_SCOPE, stereotypeDeclaration, bean.getResource());
				}
			}
		}

		/*
		 * 2.4.1. Built-in scope types
		 *  - interceptor or decorator has any scope other than @Dependent (Non-Portable behavior)
		 */
		boolean interceptor = bean instanceof IInterceptor;
		boolean decorator = bean instanceof IDecorator;
		if (interceptor || decorator) {
			IAnnotationDeclaration scopeOrStereotypeDeclaration = CDIUtil.getDifferentScopeDeclarationThanDepentend(bean);
			if (scopeOrStereotypeDeclaration != null) {
				String message = interceptor?CDIValidationMessages.ILLEGAL_SCOPE_FOR_INTERCEPTOR:CDIValidationMessages.ILLEGAL_SCOPE_FOR_DECORATOR;
				addProblem(message, CDIPreferences.ILLEGAL_SCOPE_FOR_INTERCEPTOR_OR_DECORATOR, scopeOrStereotypeDeclaration, bean.getResource());
			}
		}
	}

	boolean shouldValidateResourceOfElement(IResource resource) {
		// validate existing sources only
		if(resource instanceof IFile && !shouldBeValidated((IFile)resource)) {
			return false;
		}
		return resource != null && resource.exists() && resource.getName().toLowerCase().endsWith(".java");
	}

	boolean shouldValidateType(IType type) {
		return type.exists() && !type.isReadOnly();
	}

	/**
	 * Validates a stereotype.
	 * 
	 * @param type
	 */
	private void validateStereotype(IStereotype stereotype) {
		// 2.7.1.3. Declaring a @Named stereotype
		// - stereotype declares a non-empty @Named annotation (Non-Portable
		// behavior)
		// - stereotype declares any other qualifier annotation
		// - stereotype is annotated @Typed
		if(stereotype==null) {
			return;
		}
		IResource resource = stereotype.getResource();
		if(!shouldValidateResourceOfElement(resource)) {
			return;
		}
		addLinkedStereotypes(stereotype.getSourcePath().toOSString(), stereotype);
		List<IAnnotationDeclaration> as = stereotype.getAnnotationDeclarations();

		// 1. non-empty name
		IAnnotationDeclaration nameDeclaration = stereotype.getNameDeclaration();
		if (nameDeclaration != null) {
			Object name = nameDeclaration.getMemberValue(null);
			if (name != null && name.toString().length() > 0) {
				ITextSourceReference location = nameDeclaration;
				addProblem(CDIValidationMessages.STEREOTYPE_DECLARES_NON_EMPTY_NAME, CDIPreferences.STEREOTYPE_DECLARES_NON_EMPTY_NAME, location, resource, STEREOTYPE_DECLARES_NON_EMPTY_NAME_ID);
			}
		}

		// 2. typed annotation
		IAnnotationDeclaration typedDeclaration = stereotype.getAnnotationDeclaration(CDIConstants.TYPED_ANNOTATION_TYPE_NAME);
		if (typedDeclaration != null) {
			ITextSourceReference location = typedDeclaration;
			addProblem(CDIValidationMessages.STEREOTYPE_IS_ANNOTATED_TYPED, CDIPreferences.STEREOTYPE_IS_ANNOTATED_TYPED, location, resource, STEREOTYPE_IS_ANNOTATED_TYPED_ID);
		}

		// 3. Qualifier other than @Named
		for (IAnnotationDeclaration a : as) {
			if (a instanceof IQualifierDeclaration && a != nameDeclaration) {
				ITextSourceReference location = a;
				addProblem(CDIValidationMessages.ILLEGAL_QUALIFIER_IN_STEREOTYPE, CDIPreferences.ILLEGAL_QUALIFIER_IN_STEREOTYPE, location, resource);
			}
		}

		// 2.7.1.1. Declaring the default scope for a stereotype
		// - stereotype declares more than one scope
		Collection<IScopeDeclaration> scopeDeclarations = stereotype.getScopeDeclarations();
		if (scopeDeclarations.size() > 1) {
			for (IScopeDeclaration scope : scopeDeclarations) {
				addProblem(CDIValidationMessages.STEREOTYPE_DECLARES_MORE_THAN_ONE_SCOPE, CDIPreferences.STEREOTYPE_DECLARES_MORE_THAN_ONE_SCOPE, scope, stereotype.getResource());
			}
		}

		try {
			annotationValidator.validateStereotypeAnnotationTypeAnnotations(stereotype, resource);
		} catch (JavaModelException e) {
			CDICorePlugin.getDefault().logError(e);
		}
	}

	boolean shouldValidateAnnotation(ICDIAnnotation annotation) {
		return annotation!=null && annotation.getSourceType() != null && shouldValidateType(annotation.getSourceType());
	}

	private void validateInterceptorBinding(IInterceptorBinding binding) {
		if(binding==null || !shouldValidateAnnotation(binding)) {
			return;
		}

		addLinkedInterceptorBindings(binding.getSourcePath().toOSString(), binding);

		/*
		 * 9.5.2. Interceptor binding types with members
		 *  array-valued or annotation-valued member of an interceptor binding type is not annotated @Nonbinding (Non-Portable behavior)
		 */
		validateAnnotationMembers(
				binding,
				CDIValidationMessages.MISSING_NONBINDING_FOR_ARRAY_VALUE_IN_INTERCEPTOR_BINDING_TYPE_MEMBER,
				CDIValidationMessages.MISSING_NONBINDING_FOR_ANNOTATION_VALUE_IN_INTERCEPTOR_BINDING_TYPE_MEMBER,
				CDIPreferences.MISSING_NONBINDING_IN_INTERCEPTOR_BINDING_TYPE_MEMBER,
				MISSING_NONBINDING_FOR_ARRAY_VALUE_IN_INTERCEPTOR_BINDING_TYPE_MEMBER_ID,
				MISSING_NONBINDING_FOR_ANNOTATION_VALUE_IN_INTERCEPTOR_BINDING_TYPE_MEMBER_ID);

		try {
			annotationValidator.validateInterceptorBindingAnnotationTypeAnnotations(binding);
		} catch (JavaModelException e) {
			CDICorePlugin.getDefault().logError(e);
		}
	}

	/**
	 * Validates a qualifier.
	 * 
	 * @param qualifier
	 */
	private void validateQualifier(IQualifier qualifier) {
		if(qualifier==null) {
			return;
		}
		IResource resource = qualifier.getResource();
		if(!shouldValidateResourceOfElement(resource)) {
			return;
		}
		/*
		 * 5.2.5. Qualifier annotations with members
		 *  - array-valued or annotation-valued member of a qualifier type is not annotated @Nonbinding (Non-Portable behavior)
		 */
		validateAnnotationMembers(
				qualifier,
				CDIValidationMessages.MISSING_NONBINDING_FOR_ARRAY_VALUE_IN_QUALIFIER_TYPE_MEMBER,
				CDIValidationMessages.MISSING_NONBINDING_FOR_ANNOTATION_VALUE_IN_QUALIFIER_TYPE_MEMBER,
				CDIPreferences.MISSING_NONBINDING_IN_QUALIFIER_TYPE_MEMBER,
				MISSING_NONBINDING_FOR_ARRAY_VALUE_IN_QUALIFIER_TYPE_MEMBER_ID,
				MISSING_NONBINDING_FOR_ANNOTATION_VALUE_IN_QUALIFIER_TYPE_MEMBER_ID);

		/*
		 * Qualifier annotation type should be annotated with @Target({METHOD, FIELD, PARAMETER, TYPE})
		 */
		try {
			annotationValidator.validateQualifierAnnotationTypeAnnotations(qualifier, resource);
		} catch (JavaModelException e) {
			CDICorePlugin.getDefault().logError(e);
		}
	}

	void validateAnnotationMembers(ICDIAnnotation annotation, String arrayMessageErrorKey, String annotationValueErrorKey, String preferencesKey, int arrayMessageId, int annotationValueId) {
		IType type = annotation.getSourceType();
		try {
			IMethod[] methods = type.getMethods();
			for (IMethod method : methods) {
				String returnTypeSignature = method.getReturnType();
				int kind = Signature.getTypeSignatureKind(returnTypeSignature);
				if(kind == Signature.ARRAY_TYPE_SIGNATURE) {
					if(!annotation.getNonBindingMethods().contains(method)) {
						ITextSourceReference reference = CDIUtil.convertToSourceReference(method.getNameRange(), annotation.getResource(), method);
						addProblem(arrayMessageErrorKey, preferencesKey, reference, annotation.getResource(), arrayMessageId);
					}
				} else if(kind == Signature.CLASS_TYPE_SIGNATURE) {
					String typeName = Signature.getSignatureSimpleName(returnTypeSignature);
					String packageName = Signature.getSignatureQualifier(returnTypeSignature);
					if(packageName.length()>0) {
						typeName = packageName + "." + typeName;
					} else {
						typeName = EclipseJavaUtil.resolveType(type, typeName);
					}
					if(typeName!=null) {
						IType memberType = type.getJavaProject().findType(typeName);
						if(memberType!=null && memberType.isAnnotation()) {
							if(!annotation.getNonBindingMethods().contains(method)) {
								ITextSourceReference reference = CDIUtil.convertToSourceReference(method.getNameRange(), annotation.getResource(), method);
								addProblem(annotationValueErrorKey, preferencesKey, reference, annotation.getResource(), annotationValueId);
							}
						}
					}
				}
			}
		} catch (JavaModelException e) {
			CDICorePlugin.getDefault().logError(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.common.validation.ValidationErrorManager#getPreferencePageId()
	 */
	@Override
	protected String getPreferencePageId() {
		return PREFERENCE_PAGE_ID;
	}

	private static final String BUNDLE_NAME = "org.jboss.tools.cdi.internal.core.validation.messages";

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.common.validation.TempMarkerManager#getMessageBundleName()
	 */
	@Override
	protected String getMessageBundleName() {
		return BUNDLE_NAME;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.common.validation.TempMarkerManager#shouldCleanAllAnnotations()
	 */
	@Override
	protected boolean shouldCleanAllAnnotations() {
		return true;
	}
}