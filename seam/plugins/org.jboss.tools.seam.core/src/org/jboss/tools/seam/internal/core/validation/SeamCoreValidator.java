/******************************************************************************* 
 * Copyright (c) 2007 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.seam.internal.core.validation;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.validation.internal.core.ValidationException;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.jboss.tools.common.el.core.resolver.TypeInfoCollector;
import org.jboss.tools.common.java.IJavaSourceReference;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.common.text.ITextSourceReference;
import org.jboss.tools.common.validation.ContextValidationHelper;
import org.jboss.tools.common.validation.IProjectValidationContext;
import org.jboss.tools.common.validation.IValidatingProjectSet;
import org.jboss.tools.common.validation.IValidatingProjectTree;
import org.jboss.tools.common.validation.IValidator;
import org.jboss.tools.common.validation.ValidationUtil;
import org.jboss.tools.common.validation.ValidatorManager;
import org.jboss.tools.common.validation.internal.SimpleValidatingProjectTree;
import org.jboss.tools.common.validation.internal.ValidatingProjectSet;
import org.jboss.tools.jst.web.kb.IKbProject;
import org.jboss.tools.jst.web.kb.KbProjectFactory;
import org.jboss.tools.jst.web.kb.internal.KbProject;
import org.jboss.tools.jst.web.kb.internal.validation.KBValidator;
import org.jboss.tools.jst.web.model.project.ext.store.XMLValueInfo;
import org.jboss.tools.seam.core.BijectedAttributeType;
import org.jboss.tools.seam.core.IBijectedAttribute;
import org.jboss.tools.seam.core.ISeamAnnotatedFactory;
import org.jboss.tools.seam.core.ISeamComponent;
import org.jboss.tools.seam.core.ISeamComponentDeclaration;
import org.jboss.tools.seam.core.ISeamComponentMethod;
import org.jboss.tools.seam.core.ISeamContextVariable;
import org.jboss.tools.seam.core.ISeamFactory;
import org.jboss.tools.seam.core.ISeamJavaComponentDeclaration;
import org.jboss.tools.seam.core.ISeamProject;
import org.jboss.tools.seam.core.ISeamProperty;
import org.jboss.tools.seam.core.ISeamXmlComponentDeclaration;
import org.jboss.tools.seam.core.ISeamXmlFactory;
import org.jboss.tools.seam.core.ScopeType;
import org.jboss.tools.seam.core.SeamComponentMethodType;
import org.jboss.tools.seam.core.SeamCoreBuilder;
import org.jboss.tools.seam.core.SeamCoreMessages;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.core.SeamPreferences;
import org.jboss.tools.seam.core.SeamProjectsSet;
import org.jboss.tools.seam.core.SeamUtil;
import org.jboss.tools.seam.core.project.facet.SeamRuntime;
import org.jboss.tools.seam.internal.core.DataModelSelectionAttribute;
import org.jboss.tools.seam.internal.core.SeamComponentDeclaration;
import org.jboss.tools.seam.internal.core.SeamProject;
import org.jboss.tools.seam.internal.core.SeamTextSourceReference;
import org.jboss.tools.seam.pages.xml.model.SeamPagesConstants;

/**
 * Validator for Java and XML files.
 * @author Alexey Kazakov
 */
public class SeamCoreValidator extends SeamValidationErrorManager implements IValidator {
	public static final String ID = "org.jboss.tools.seam.core.CoreValidator"; //$NON-NLS-1$
	public static final String PROBLEM_TYPE = "org.jboss.tools.seam.core.seamproblem"; //$NON-NLS-1$

	public static final String MESSAGE_ID_ATTRIBUTE_NAME = "Seam_message_id"; //$NON-NLS-1$
	
	public static final int NONUNIQUE_COMPONENT_NAME_MESSAGE_ID = 1;
	public static final int DUPLICATE_REMOVE_MESSAGE_ID = 2;
	public static final int DUPLICATE_DESTROY_MESSAGE_ID = 3;
	public static final int DUPLICATE_CREATE_MESSAGE_ID = 4;
	public static final int DUPLICATE_UNWRAP_MESSAGE_ID = 5;
	public static final int DESTROY_METHOD_BELONGS_TO_STATELESS_SESSION_BEAN_MESSAGE_ID = 6;
	public static final int CREATE_DOESNT_BELONG_TO_COMPONENT_MESSAGE_ID = 7;
	public static final int UNWRAP_DOESNT_BELONG_TO_COMPONENT_MESSAGE_ID = 8;
	public static final int OBSERVER_DOESNT_BELONG_TO_COMPONENT_MESSAGE_ID = 9;
	public static final int STATEFUL_COMPONENT_DOES_NOT_CONTAIN_REMOVE_ID = 10;
	public static final int STATEFUL_COMPONENT_DOES_NOT_CONTAIN_DESTROY_ID = 11;
	public static final int STATEFUL_COMPONENT_WRONG_SCOPE_ID = 12;
	public static final int ENTITY_COMPONENT_WRONG_SCOPE_ID = 13;
	public static final int UNKNOWN_COMPONENT_PROPERTY_ID = 14;
	
	private ISeamProject seamProject;
	private SeamProjectsSet set;
	private String projectName;

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
	 * @see org.jboss.tools.jst.web.kb.validation.IValidator#getId()
	 */
	public String getId() {
		return ID;
	}

	public String getBuilderId() {
		return SeamCoreBuilder.BUILDER_ID;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.jst.web.kb.validation.IValidator#getValidatingProjects(org.eclipse.core.resources.IProject)
	 */
	public IValidatingProjectTree getValidatingProjects(IProject project) {
		return getSeamValidatingProjects(project);
	}

	private static final String SHORT_ID = "jboss.seam.core"; //$NON-NLS-1$

	public static IValidatingProjectTree getSeamValidatingProjects(IProject project) {
		SeamProjectsSet set = new SeamProjectsSet(project);
		IProject war = set.getWarProject();
		IProjectValidationContext rootContext = null;
		if(war!=null && war.isAccessible()) {
			IKbProject kbProject = KbProjectFactory.getKbProject(war, false);
			if(kbProject!=null) {
				rootContext = kbProject.getValidationContext();
			} else {
				KbProject.checkKBBuilderInstalled(war);
				ISeamProject seamProject = SeamCorePlugin.getSeamProject(war, false);
				if(seamProject!=null) {
					rootContext = seamProject.getValidationContext();
				}
			}
		}
		if(rootContext==null) {
			ISeamProject seamProject = SeamCorePlugin.getSeamProject(project, false);
			if(seamProject!=null) {
				rootContext = seamProject.getValidationContext();
				war = project;
			}
		}

		Set<IProject> projects = new HashSet<IProject>();
		IProject[] array = set.getAllProjects();
		for (int i = 0; i < array.length; i++) {
			if(array[i].isAccessible()) {
				projects.add(array[i]);
			}
		}
		IValidatingProjectSet projectSet = new ValidatingProjectSet(war, projects, rootContext);
		return new SimpleValidatingProjectTree(projectSet);
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.jst.web.kb.validation.IValidator#isEnabled(org.eclipse.core.resources.IProject)
	 */
	public boolean isEnabled(IProject project) {
		return SeamPreferences.isValidationEnabled(project);
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.jst.web.kb.validation.IValidator#shouldValidate(org.eclipse.core.resources.IProject)
	 */
	public boolean shouldValidate(IProject project) {
		try {
			return project != null 
					&& project.isAccessible() 
					&& project.hasNature(ISeamProject.NATURE_ID)
					&& validateBuilderOrder(project)
					&& isPreferencesEnabled(project);
		} catch (CoreException e) {
			SeamCorePlugin.getDefault().logError(e);
		}
		return false;
	}

	private boolean validateBuilderOrder(IProject project) throws CoreException {
		return KBValidator.validateBuilderOrder(project, getBuilderId(), getId(), SeamPreferences.getInstance());
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.jst.web.kb.internal.validation.ValidationErrorManager#init(org.eclipse.core.resources.IProject, org.jboss.tools.jst.web.kb.internal.validation.ContextValidationHelper, org.jboss.tools.jst.web.kb.internal.validation.ValidatorManager, org.eclipse.wst.validation.internal.provisional.core.IReporter, org.jboss.tools.jst.web.kb.validation.IValidationContext)
	 */
	@Override
	public void init(IProject project, ContextValidationHelper validationHelper, IProjectValidationContext validationContext, org.eclipse.wst.validation.internal.provisional.core.IValidator manager, IReporter reporter) {
		super.init(project, validationHelper, validationContext, manager, reporter);

		set = new SeamProjectsSet(project);
		IProject warProject = set.getWarProject();
		
		// Fix for JBIDE-7622: set these variables to null due to prevent wrong project validations --->>>
		seamProject = null;
		projectName = null;
		// <<<---
		
		if(warProject.isAccessible()) {
			seamProject = SeamCorePlugin.getSeamProject(warProject, false);
			projectName = seamProject.getProject().getName();
		}
	}

	private boolean isPreferencesEnabled(IProject project) {
		return isEnabled(project) && SeamPreferences.shouldValidateCore(project);
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.jst.web.kb.validation.IValidator#validate(java.util.Set, org.eclipse.core.resources.IProject, org.jboss.tools.jst.web.kb.internal.validation.ContextValidationHelper, org.jboss.tools.jst.web.kb.validation.IProjectValidationContext, org.jboss.tools.jst.web.kb.internal.validation.ValidatorManager, org.eclipse.wst.validation.internal.provisional.core.IReporter)
	 */
	public IStatus validate(Set<IFile> changedFiles, IProject project, ContextValidationHelper validationHelper, IProjectValidationContext context, ValidatorManager manager, IReporter reporter) throws ValidationException {
		init(project, validationHelper, context, manager, reporter);
		if(seamProject==null) {
			return OK_STATUS;
		}
		displaySubtask(SeamValidationMessages.SEARCHING_RESOURCES, new String[]{projectName});

		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		Set<ISeamComponent> checkedComponents = new HashSet<ISeamComponent>();
		Set<String> markedDuplicateFactoryNames = new HashSet<String>();
		// Collect all resources which we must validate.
		Set<IPath> resources = new HashSet<IPath>(); // Resources which we have to validate.
		Set<IPath> newResources = new HashSet<IPath>(); // New (unlinked) resources file
		boolean validateUnnamedResources = false;
		for(IFile currentFile : changedFiles) {
			if(reporter.isCancelled()) {
				break;
			}
			if(!validateUnnamedResources) {
				String fileName = currentFile.getName().toLowerCase();
				// We need to check only file names here. 
				validateUnnamedResources = fileName.endsWith(".java") || fileName.endsWith(".properties") || fileName.equals("components.xml"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}
			if (ValidationUtil.checkFileExtensionForJavaAndXml(currentFile)) {
				resources.add(currentFile.getFullPath());
				// Get new variable names from model
				Set<String> newVariableNamesOfChangedFile = getVariablesNameByResource(currentFile.getFullPath());
				Set<String> oldDeclarationsOfChangedFile = validationContext.getVariableNamesByCoreResource(SHORT_ID, currentFile.getFullPath(), true);
				for (String newVariableName : newVariableNamesOfChangedFile) {
					// Collect resources with new variable name.
					Set<IPath> linkedResources = validationContext.getCoreResourcesByVariableName(SHORT_ID, newVariableName, false);
					if(linkedResources!=null) {
						resources.addAll(linkedResources);
					}
					resources.addAll(getAllResourceOfComponent(currentFile.getFullPath()));
				}
				// Get old variable names which were linked with this resource.
				Set<String> oldVariablesNamesOfChangedFile = validationContext.getVariableNamesByCoreResource(SHORT_ID, currentFile.getFullPath(), false);
				if(oldVariablesNamesOfChangedFile!=null) {
					for (String name : oldVariablesNamesOfChangedFile) {
						Set<IPath> linkedResources = validationContext.getCoreResourcesByVariableName(SHORT_ID, name, false);
						if(linkedResources!=null) {
							resources.addAll(linkedResources);
						}
					}
				}
				// Save old declarations for EL validation. We need to validate all EL resources which use this variable name but only if the variable has been changed.
				if(oldDeclarationsOfChangedFile!=null) {
					for (String name : oldDeclarationsOfChangedFile) {
						validationContext.addVariableNameForELValidation(SHORT_ID, name);
					}
				}
				newResources.add(currentFile.getFullPath());
			}
		}
		// Validate all collected linked resources.
		// Remove all links between collected resources and variables names because they will be linked again during validation.
		validationContext.removeLinkedCoreResources(SHORT_ID, resources);

		Set<IFile> filesToValidate = new HashSet<IFile>();
		// We have to remove markers from all collected source files first
		for (IPath linkedResource : resources) {
			IFile file = root.getFile(linkedResource);
			if(file!=null && file.isAccessible()) {
				filesToValidate.add(file);
				removeAllMessagesFromResource(file);
			}
		}
		// Then we can validate them
		for (IFile file : filesToValidate) {
			validateComponent(file.getFullPath(), checkedComponents, newResources);
			validateFactory(file.getFullPath(), markedDuplicateFactoryNames);
			validatePageXML(file);
			validateXMLVersion(file);
		}

		// If changed files are *.java or component.xml then re-validate all unnamed resources.
		if(validateUnnamedResources) {
			Set<IPath> unnamedResources = validationContext.getUnnamedCoreResources(SHORT_ID);
			newResources.addAll(unnamedResources);
			for (IPath path : newResources) {
				IFile file = root.getFile(path);
				if(file!=null && file.isAccessible()) {
					if(!resources.contains(path)) {
						removeAllMessagesFromResource(file);
					}
					displaySubtask(SeamValidationMessages.VALIDATING_RESOURCE, new String[]{projectName, path.toString()});
					Set<ISeamJavaComponentDeclaration> declarations = ((SeamProject)seamProject).findJavaDeclarations(path);
					for (ISeamJavaComponentDeclaration d : declarations) {
						validateMethodsOfUnknownComponent(d);
					}
				}
			}
		}

		return OK_STATUS;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.jst.web.kb.validation.IValidator#validateAll(org.eclipse.core.resources.IProject, org.jboss.tools.jst.web.kb.internal.validation.ContextValidationHelper, org.jboss.tools.jst.web.kb.validation.IProjectValidationContext, org.jboss.tools.jst.web.kb.internal.validation.ValidatorManager, org.eclipse.wst.validation.internal.provisional.core.IReporter)
	 */
	public IStatus validateAll(IProject project, ContextValidationHelper validationHelper, IProjectValidationContext context, ValidatorManager manager, IReporter reporter) throws ValidationException {
		init(project, validationHelper, context, manager, reporter);
		if(seamProject==null) {
			return OK_STATUS;
		}

		for (IProject iProject : set.getAllProjects()) {
			removeAllMessagesFromResource(iProject);
		}

		ISeamComponent[] components = seamProject.getComponents();
		for (ISeamComponent component : components) {
			if(reporter.isCancelled()) {
				return OK_STATUS;
			}
			Set<ISeamComponentDeclaration> declarations = component.getAllDeclarations();
			for (ISeamComponentDeclaration seamComponentDeclaration : declarations) {
				validateComponent(component);
				break;
			}
		}
		ISeamFactory[] factories = seamProject.getFactories();
		Set<String> markedDuplicateFactoryNames = new HashSet<String>();
		for (ISeamFactory factory : factories) {
			if(reporter.isCancelled()) {
				return OK_STATUS;
			}
			validateFactory(factory, markedDuplicateFactoryNames);
		}

		ISeamJavaComponentDeclaration[] values = ((SeamProject)seamProject).getAllJavaComponentDeclarations();
		for (ISeamJavaComponentDeclaration d : values) {
			if(reporter.isCancelled()) {
				return OK_STATUS;
			}
			displaySubtask(SeamValidationMessages.VALIDATING_CLASS, new String[]{projectName, d.getClassName()});
			validateMethodsOfUnknownComponent(d);
		}
		IResource webContent = set.getViewsFolder();
		if(webContent instanceof IContainer && webContent.isAccessible()) {
			validateAllPageXMLFiles((IContainer)webContent);
		}

		return OK_STATUS;
	}

	void validateAllPageXMLFiles(IContainer c) {
		if(c.isAccessible()) {
			IResource[] rs = null;
			try {
				rs = c.members();
			} catch (CoreException e) {
				SeamCorePlugin.getDefault().logError(e);
				return;
			}
			for (int i = 0; i < rs.length; i++) {
				if(rs[i] instanceof IContainer) {
					 validateAllPageXMLFiles((IContainer)rs[i]);
				} else if(rs[i] instanceof IFile) {
					validatePageXML((IFile)rs[i]);
				}
			}
		}
	}

	private void validateFactory(IPath sourceFilePath, Set<String> markedDuplicateFactoryNames) {
		Set<ISeamFactory> factories = seamProject.getFactoriesByPath(sourceFilePath);
		for (ISeamFactory factory : factories) {
			validateFactory(factory, markedDuplicateFactoryNames);
		}
	}

	private void validateFactory(ISeamFactory factory, Set<String> markedDuplicateFactoryNames) {
		if(SeamUtil.isJar(factory.getSourcePath())) {
			return;
		}
		String factoryName = factory.getName();
		if(factoryName!=null) {
			displaySubtask(SeamValidationMessages.VALIDATING_FACTORY, new String[]{projectName, factoryName});
		}
		if(factory instanceof ISeamAnnotatedFactory) {
			validateAnnotatedFactory((ISeamAnnotatedFactory)factory, markedDuplicateFactoryNames);
		} else {
			validateXmlFactory((ISeamXmlFactory)factory, markedDuplicateFactoryNames);
		}
	}

	private void validateXmlFactory(ISeamXmlFactory factory, Set<String> markedDuplicateFactoryNames) {
		String name = factory.getName();
		if(name==null) {
			SeamCorePlugin.getDefault().logError(NLS.bind(SeamCoreMessages.SEAM_CORE_VALIDATOR_FACTORY_METHOD_MUST_HAVE_NAME,factory.getResource()));
			return;
		}
		validateFactoryName(factory, name, markedDuplicateFactoryNames, false);
	}

	private void validateAnnotatedFactory(ISeamAnnotatedFactory factory, Set<String> markedDuplicateFactoryNames) {
		IMember sourceMember = factory.getSourceMember();
		if(sourceMember instanceof IMethod) {
			String factoryName = factory.getName();
			if(factoryName==null) {
				// Unknown factory name
				SeamCorePlugin.getDefault().logError(NLS.bind(SeamCoreMessages.SEAM_CORE_VALIDATOR_FACTORY_METHOD_MUST_HAVE_NAME,factory.getResource()));
				return;
			}
			validateFactoryName(factory, factoryName, markedDuplicateFactoryNames, true);
		} else {
			// factory must be java method!
			// JDT should mark it.
		}
	}

	private void validateFactoryName(ISeamFactory factory, String factoryName, Set<String> markedDuplicateFactoryNames, boolean validateUnknownName) {
		ScopeType factoryScope = factory.getScope();
		Set<ISeamContextVariable> variables = seamProject.getVariablesByName(factoryName);
		boolean unknownVariable = true;
		boolean firstDuplicateVariableWasMarked = false;
		for (ISeamContextVariable variable : variables) {
			if(variable instanceof ISeamFactory) {
				if(variable!=factory && !markedDuplicateFactoryNames.contains(factoryName) && 
					(factoryScope == variable.getScope() || factoryScope.getPriority()>variable.getScope().getPriority())) {
					// Duplicate factory name. Mark it.
					// Save link to factory resource.
					ITextSourceReference location = null;
					if(!firstDuplicateVariableWasMarked) {
						firstDuplicateVariableWasMarked = true;
						// mark original factory
						validationContext.addLinkedCoreResource(SHORT_ID, factoryName, factory.getSourcePath(), true);
						location = SeamUtil.getLocationOfName(factory);
						this.addError(SeamValidationMessages.DUPLICATE_VARIABLE_NAME, SeamPreferences.DUPLICATE_VARIABLE_NAME, new String[]{factoryName}, location, factory.getResource());
					}
					// Mark duplicate variable.
					if(!SeamUtil.isJar(variable.getSourcePath())) {
						IResource resource = SeamUtil.getComponentResourceWithName(variable);
						validationContext.addLinkedCoreResource(SHORT_ID, factoryName, resource.getFullPath(), true);
						location = SeamUtil.getLocationOfName(variable);
						this.addError(SeamValidationMessages.DUPLICATE_VARIABLE_NAME, SeamPreferences.DUPLICATE_VARIABLE_NAME, new String[]{factoryName}, location, resource);
					}
				}
			} else {
				// We know that variable name
				unknownVariable = false;
			}
		}
		if(firstDuplicateVariableWasMarked) {
			markedDuplicateFactoryNames.add(factoryName);
		}
		boolean voidReturnType = false;
		if(factory instanceof ISeamAnnotatedFactory) {
			IMember sourceMember = ((ISeamAnnotatedFactory)factory).getSourceMember();
			if(sourceMember instanceof IMethod) {
				IMethod method = (IMethod)sourceMember;
				try {
					String returnType = method.getReturnType();
					if("V".equals(returnType)) { //$NON-NLS-1$
						// return type is void
						voidReturnType = true;
					}
				} catch (JavaModelException e) {
					SeamCorePlugin.getDefault().logError(SeamCoreMessages.SEAM_CORE_VALIDATOR_ERROR_VALIDATING_SEAM_CORE, e);
				}
			}
		}
		if(unknownVariable && validateUnknownName && voidReturnType) {
			// mark unknown factory name
			// save link to factory resource
			validationContext.addLinkedCoreResource(SHORT_ID, factoryName, factory.getSourcePath(), true);
			this.addError(SeamValidationMessages.UNKNOWN_FACTORY_NAME, SeamPreferences.UNKNOWN_FACTORY_NAME, new String[]{factoryName}, SeamUtil.getLocationOfName(factory), factory.getResource());
		}
	}

	private void validateComponent(IPath sourceFilePath, Set<ISeamComponent> checkedComponents, Set<IPath> unnamedResources) {
		Set<ISeamComponent> components = seamProject.getComponentsByPath(sourceFilePath);
		if(components.isEmpty()) {
			unnamedResources.add(sourceFilePath);
			return;
		}
		for (ISeamComponent component : components) {
			// Don't validate one component twice.
			if(!checkedComponents.contains(component)) {
				validateComponent(component);
				checkedComponents.add(component);
			}
		}
	}

	/*
	 * Returns set of variables which are linked with this resource
	 */
	private Set<String> getVariablesNameByResource(IPath resourcePath) {
		Set<ISeamContextVariable> variables = seamProject.getVariablesByPath(resourcePath);
		Set<String> result = new HashSet<String>();
		if(variables!=null) {
			for (ISeamContextVariable variable : variables) {
				String name = variable.getName();
				result.add(name);
			}
		}
		return result;
	}

	/*
	 * Collect all resources of all declarations of all components which is declared in the source.
	 */
	private Set<IPath> getAllResourceOfComponent(IPath sourceComponentFilePath) {
		Set<IPath> result = new HashSet<IPath>();
		Set<ISeamComponent> components = seamProject.getComponentsByPath(sourceComponentFilePath);
		for (ISeamComponent component : components) {
			Set<ISeamComponentDeclaration> declarations = component.getAllDeclarations();
			for (ISeamComponentDeclaration seamComponentDeclaration : declarations) {
				result.add(seamComponentDeclaration.getResource().getFullPath());
			}
		}
		return result;
	}

	/*
	 * Validates the component 
	 */
	private void validateComponent(ISeamComponent component) {
		ISeamJavaComponentDeclaration firstJavaDeclaration = component.getJavaDeclaration();
		if(firstJavaDeclaration!=null) {
			String componentName = component.getName();
			if(componentName!=null) {
				displaySubtask(SeamValidationMessages.VALIDATING_COMPONENT, new String[]{projectName, componentName});
			}
			HashMap<Integer, ISeamJavaComponentDeclaration> usedPrecedences = new HashMap<Integer, ISeamJavaComponentDeclaration>();
			Set<ISeamJavaComponentDeclaration> markedDeclarations = new HashSet<ISeamJavaComponentDeclaration>();
			int firstJavaDeclarationPrecedence = firstJavaDeclaration.getPrecedence();
			usedPrecedences.put(firstJavaDeclarationPrecedence, firstJavaDeclaration);
			Set<ISeamComponentDeclaration> declarations = component.getAllDeclarations();
			for (ISeamComponentDeclaration declaration : declarations) {
				if(declaration instanceof ISeamJavaComponentDeclaration) {
					ISeamJavaComponentDeclaration jd = (ISeamJavaComponentDeclaration)declaration;

					//do not check files declared in another project
//					if(jd.getSeamProject() != seamProject) continue;

					IType type = (IType)jd.getSourceMember();
					boolean sourceJavaDeclaration = !type.isBinary();
					if(sourceJavaDeclaration) {
						// Save link between component name and java source file.
						validationContext.addLinkedCoreResource(SHORT_ID, componentName, declaration.getSourcePath(), true);
						// Save link between component name and all supers of java declaration.
						try {
							IType[] superTypes = TypeInfoCollector.getSuperTypes(type).getSuperTypes();
							for (int i = 0; superTypes != null && i < superTypes.length; i++) {
								if(!superTypes[i].isBinary()) {
									IPath path = superTypes[i].getResource().getFullPath();
									validationContext.addLinkedCoreResource(SHORT_ID, componentName, path, true);
								}
							}
						} catch (JavaModelException e) {
							SeamCorePlugin.getPluginLog().logError(e);
						}
					}
					if(declaration!=firstJavaDeclaration) {
						// Validate @Name
						// Component class with the same component name. Check precedence.
						ISeamJavaComponentDeclaration javaDeclaration = (ISeamJavaComponentDeclaration)declaration;
						int javaDeclarationPrecedence = javaDeclaration.getPrecedence();
						ISeamJavaComponentDeclaration checkedDeclaration = usedPrecedences.get(javaDeclarationPrecedence);
						if(checkedDeclaration==null) {
							usedPrecedences.put(javaDeclarationPrecedence, javaDeclaration);
						} else if(sourceJavaDeclaration) {
							boolean sourceCheckedDeclaration = !((IType)checkedDeclaration.getSourceMember()).isBinary();
							IResource javaDeclarationResource = javaDeclaration.getResource();
							// Mark nonunique name.
							if(!markedDeclarations.contains(checkedDeclaration) && sourceCheckedDeclaration) {
								// Mark first wrong declaration with that name
								IResource checkedDeclarationResource = checkedDeclaration.getResource();
								ITextSourceReference location = ((SeamComponentDeclaration)checkedDeclaration).getLocationFor(SeamComponentDeclaration.PATH_OF_NAME);
								if(!SeamUtil.isEmptyLocation(location)) {
									addError(SeamValidationMessages.NONUNIQUE_COMPONENT_NAME_MESSAGE, SeamPreferences.NONUNIQUE_COMPONENT_NAME, new String[]{componentName}, location, checkedDeclarationResource, NONUNIQUE_COMPONENT_NAME_MESSAGE_ID);
								}
								markedDeclarations.add(checkedDeclaration);
							}
							// Mark next wrong declaration with that name
							markedDeclarations.add(javaDeclaration);
							ITextSourceReference location = ((SeamComponentDeclaration)javaDeclaration).getLocationFor(SeamComponentDeclaration.PATH_OF_NAME);
							if(!SeamUtil.isEmptyLocation(location)) {
								addError(SeamValidationMessages.NONUNIQUE_COMPONENT_NAME_MESSAGE, SeamPreferences.NONUNIQUE_COMPONENT_NAME, new String[]{componentName}, location, javaDeclarationResource, NONUNIQUE_COMPONENT_NAME_MESSAGE_ID);
							}
						}
					}
				}
			}
			boolean source = !((IType)firstJavaDeclaration.getSourceMember()).isBinary();
			if(source) {
				// Validate all elements in first java declaration but @Name.
				validateJavaDeclaration(component, firstJavaDeclaration);
			}
		}
		validateXmlComponentDeclarations(component);
	}

	private static final String BUILT_IN_COMPONENT_NAME_PREFIX = "org.jboss.seam.";

	private boolean isBuiltInComponentName(String componentName) {
		return componentName.startsWith(BUILT_IN_COMPONENT_NAME_PREFIX);
	}

	private void validateXmlComponentDeclarations(ISeamComponent component) {
		String componentName = component.getName();
		if(componentName!=null) {
			HashMap<String, ISeamXmlComponentDeclaration> usedPrecedences = new HashMap<String, ISeamXmlComponentDeclaration>();
			Set<ISeamXmlComponentDeclaration> markedDeclarations = new HashSet<ISeamXmlComponentDeclaration>();
			Set<ISeamJavaComponentDeclaration> markedJavaDeclarations = new HashSet<ISeamJavaComponentDeclaration>();
			Set<ISeamXmlComponentDeclaration> declarations = component.getXmlDeclarations();
			ISeamXmlComponentDeclaration firstNamedDeclaration = null;
			for (ISeamXmlComponentDeclaration declaration : declarations) {
				if(SeamUtil.isJar(declaration)) {
					return;
				}
				//do not check files declared in another project
//				if(declaration.getSeamProject() != seamProject) continue;

				validationContext.addLinkedCoreResource(SHORT_ID, componentName, declaration.getSourcePath(), true);

				String precedence = declaration.getPrecedence();
				if(firstNamedDeclaration == null && declaration.getName()!=null) {
					firstNamedDeclaration = declaration;
					usedPrecedences.put(precedence, declaration);
				}
				if(declaration.getName()!=null && firstNamedDeclaration!=declaration) {
					// Check precedence
					ISeamXmlComponentDeclaration checkedDeclaration = usedPrecedences.get(precedence);
					if(checkedDeclaration==null) {
						usedPrecedences.put(precedence, declaration);
					} else {
						// Mark not-unique name.
						if(!markedDeclarations.contains(checkedDeclaration)) {
							// Mark first wrong declaration with that name
							IResource checkedDeclarationResource = checkedDeclaration.getResource();
							ITextSourceReference location = ((SeamComponentDeclaration)checkedDeclaration).getLocationFor(SeamComponentDeclaration.PATH_OF_NAME);
							if(!SeamUtil.isEmptyLocation(location)) {
								addError(SeamValidationMessages.NONUNIQUE_COMPONENT_NAME_MESSAGE, SeamPreferences.NONUNIQUE_COMPONENT_NAME, new String[]{componentName}, location, checkedDeclarationResource, NONUNIQUE_COMPONENT_NAME_MESSAGE_ID);
							}
							markedDeclarations.add(checkedDeclaration);
						}
						// Mark next wrong declaration with that name
						markedDeclarations.add(declaration);
						ITextSourceReference location = ((SeamComponentDeclaration)declaration).getLocationFor(SeamComponentDeclaration.PATH_OF_NAME);
						if(!SeamUtil.isEmptyLocation(location)) {
							addError(SeamValidationMessages.NONUNIQUE_COMPONENT_NAME_MESSAGE, SeamPreferences.NONUNIQUE_COMPONENT_NAME, new String[]{componentName}, location, declaration.getResource(), NONUNIQUE_COMPONENT_NAME_MESSAGE_ID);
						}
					}
				}

				// Check Java declarations with the same name
				Set<ISeamContextVariable> vars = seamProject.getVariablesByName(componentName);
				for (ISeamContextVariable variable : vars) {
					if(variable instanceof ISeamComponent) {
						ISeamComponent c = (ISeamComponent)variable;
						Set<ISeamComponentDeclaration> decls = c.getAllDeclarations();
						for (ISeamComponentDeclaration dec : decls) {
							if(dec instanceof ISeamJavaComponentDeclaration) {
								ISeamJavaComponentDeclaration javaDec = (ISeamJavaComponentDeclaration)dec;
								// Check names
								if(declaration.getClassName()!=null && javaDec.getName()!=null && javaDec.getName().equals(declaration.getName())) {
									// Check precedences
									String javaPrecedence = "" + javaDec.getPrecedence();
									if(javaPrecedence.equals(precedence) && !isBuiltInComponentName(componentName)) {
										if(!markedJavaDeclarations.contains(javaDec)) {
											markedJavaDeclarations.add(javaDec);
											ITextSourceReference location = ((SeamComponentDeclaration)javaDec).getLocationFor(SeamComponentDeclaration.PATH_OF_NAME);
											addError(SeamValidationMessages.NONUNIQUE_COMPONENT_NAME_MESSAGE, SeamPreferences.NONUNIQUE_COMPONENT_NAME, new String[]{componentName}, location, javaDec.getResource(), NONUNIQUE_COMPONENT_NAME_MESSAGE_ID);
										}
										if(!markedDeclarations.contains(declaration)) {
											markedDeclarations.add(declaration);
											ITextSourceReference location = ((SeamComponentDeclaration)declaration).getLocationFor(SeamComponentDeclaration.PATH_OF_NAME);
											addError(SeamValidationMessages.NONUNIQUE_COMPONENT_NAME_MESSAGE, SeamPreferences.NONUNIQUE_COMPONENT_NAME, new String[]{componentName}, location, declaration.getResource(), NONUNIQUE_COMPONENT_NAME_MESSAGE_ID);
										}
									}
								}
							}
						}
					}
				}

				String className = declaration.getClassName();
				IType type = null;
				if(className!=null) {
					// validate class name
					try {
						IProject p = seamProject.getProject();
//						type = EclipseJavaUtil.findType(EclipseResourceUtil.getJavaProject(p), className);
						IJavaProject javaProject = EclipseResourceUtil.getJavaProject(p);
						if(javaProject==null) {
							SeamCorePlugin.getDefault().logWarning("Can't get Java project for " + seamProject.getProject()!=null?seamProject.getProject().getName():"" + " Seam project.");
							return;
						}
						type = javaProject.findType(className);
						if(type==null) {
							// Mark wrong class name
							ITextSourceReference location = ((SeamComponentDeclaration)declaration).getLocationFor(ISeamXmlComponentDeclaration.CLASS);
							if(SeamUtil.isEmptyLocation(location)) {
								location = ((SeamComponentDeclaration)declaration).getLocationFor(ISeamXmlComponentDeclaration.NAME);
							}
							if(SeamUtil.isEmptyLocation(location)) {
								location = declaration;
							}
							if(!declaration.isClassNameGuessed()) {
								addError(SeamValidationMessages.UNKNOWN_COMPONENT_CLASS_NAME, SeamPreferences.UNKNOWN_COMPONENT_CLASS_NAME, new String[]{className}, location, declaration.getResource());
							} else {
								addError(SeamValidationMessages.UNKNOWN_COMPONENT_CLASS_NAME, SeamPreferences.UNKNOWN_COMPONENT_CLASS_NAME_GUESS, new String[]{className}, location, declaration.getResource());
							}
						} else if(!type.isBinary()) {
							validationContext.addLinkedCoreResource(SHORT_ID, componentName, type.getResource().getFullPath(), true);
						}
					} catch (JavaModelException e) {
						SeamCorePlugin.getDefault().logError(SeamCoreMessages.SEAM_CORE_VALIDATOR_ERROR_VALIDATING_SEAM_CORE, e);
					}
				}
				// validate properties
				Collection<ISeamProperty> properties = declaration.getProperties();
				for (ISeamProperty property : properties) {
					if(SeamUtil.isJar(property)) {
						return;
					}
					String name = property.getName();
					if(name==null) {
						return;
					}
					if(type==null && component.getJavaDeclaration()!=null) {
						IMember member = component.getJavaDeclaration().getSourceMember();
						if(member instanceof IType) {
							type = (IType)member;
						}
					}
					if(type!=null) {
						boolean ok = type.isBinary() || SeamUtil.findProperty(type, name)!=null;
						if(!ok) {
							addError(SeamValidationMessages.UNKNOWN_COMPONENT_PROPERTY, SeamPreferences.UNKNOWN_COMPONENT_PROPERTY, new String[]{type.getElementName(), componentName, name}, property, declaration.getResource(), UNKNOWN_COMPONENT_PROPERTY_ID);
						}
					}
				}
			}
		}
	}
	
	private void validateEntityComponent(ISeamComponent component) {
		if(component.isEntity()) {
			ISeamJavaComponentDeclaration javaDeclaration = component.getJavaDeclaration();
			ScopeType scope = component.getScope();
			if(scope == ScopeType.STATELESS) {
				ITextSourceReference location = getScopeLocation(component);
				addError(SeamValidationMessages.ENTITY_COMPONENT_WRONG_SCOPE, SeamPreferences.ENTITY_COMPONENT_WRONG_SCOPE, new String[]{component.getName()}, location, javaDeclaration.getResource(), ENTITY_COMPONENT_WRONG_SCOPE_ID);
			}
		}
	}

	private ITextSourceReference getScopeLocation(ISeamComponent component) {
		ISeamJavaComponentDeclaration javaDeclaration = component.getJavaDeclaration();
		ITextSourceReference location = ((SeamComponentDeclaration)javaDeclaration).getLocationFor(SeamComponentDeclaration.PATH_OF_SCOPE);
		if(SeamUtil.isEmptyLocation(location)) {
			location = getNameLocation(javaDeclaration);
		}
		return location;
	}

	private ITextSourceReference getNameLocation(IJavaSourceReference source) {
		int length = 0;
		int offset = 0;
		try {
			length = source.getSourceMember().getNameRange().getLength();
			offset = source.getSourceMember().getNameRange().getOffset();
		} catch (JavaModelException e) {
			SeamCorePlugin.getDefault().logError(SeamCoreMessages.SEAM_CORE_VALIDATOR_ERROR_VALIDATING_SEAM_CORE, e);
		}
		return new SeamTextSourceReference(length, offset, source.getResource());
	}

	private void validateStatefulComponent(ISeamComponent component) {
		if(component.isStateful()) {
			ISeamJavaComponentDeclaration javaDeclaration = component.getJavaDeclaration();
			validateStatefulComponentMethods(SeamComponentMethodType.DESTROY, component, SeamValidationMessages.STATEFUL_COMPONENT_DOES_NOT_CONTAIN_DESTROY, SeamPreferences.STATEFUL_COMPONENT_DOES_NOT_CONTENT_DESTROY, STATEFUL_COMPONENT_DOES_NOT_CONTAIN_DESTROY_ID);
			validateStatefulComponentMethods(SeamComponentMethodType.REMOVE, component, SeamValidationMessages.STATEFUL_COMPONENT_DOES_NOT_CONTAIN_REMOVE, SeamPreferences.STATEFUL_COMPONENT_DOES_NOT_CONTENT_REMOVE, STATEFUL_COMPONENT_DOES_NOT_CONTAIN_REMOVE_ID);
			ScopeType scope = component.getScope();
			if(scope == ScopeType.PAGE || scope == ScopeType.STATELESS) {
				ITextSourceReference location = getScopeLocation(component);
				addError(SeamValidationMessages.STATEFUL_COMPONENT_WRONG_SCOPE, SeamPreferences.STATEFUL_COMPONENT_WRONG_SCOPE, new String[]{component.getName()}, location, javaDeclaration.getResource(), STATEFUL_COMPONENT_WRONG_SCOPE_ID);
			}
			validateDuplicateComponentMethod(SeamComponentMethodType.REMOVE, component, SeamValidationMessages.DUPLICATE_REMOVE, SeamPreferences.DUPLICATE_REMOVE, DUPLICATE_REMOVE_MESSAGE_ID);
		}
	}

	private void validateStatefulComponentMethods(SeamComponentMethodType methodType, ISeamComponent component, String message, String preferenceKey, int id) {
		ISeamJavaComponentDeclaration javaDeclaration = component.getJavaDeclaration();
		ITextSourceReference classNameLocation = getNameLocation(javaDeclaration);
		Set<ISeamComponentMethod> methods = javaDeclaration.getMethodsByType(methodType);
		if(methods==null || methods.isEmpty()) {
			addError(message, preferenceKey, new String[]{component.getName()}, classNameLocation, javaDeclaration.getResource(), id);
		}
	}

	private void validateDuplicateComponentMethods(ISeamComponent component) {
		validateDuplicateComponentMethod(SeamComponentMethodType.DESTROY, component, SeamValidationMessages.DUPLICATE_DESTROY, SeamPreferences.DUPLICATE_DESTROY, DUPLICATE_DESTROY_MESSAGE_ID);
		validateDuplicateComponentMethod(SeamComponentMethodType.CREATE, component, SeamValidationMessages.DUPLICATE_CREATE, SeamPreferences.DUPLICATE_CREATE, DUPLICATE_CREATE_MESSAGE_ID);
		validateDuplicateComponentMethod(SeamComponentMethodType.UNWRAP, component, SeamValidationMessages.DUPLICATE_UNWRAP, SeamPreferences.DUPLICATE_UNWRAP, DUPLICATE_UNWRAP_MESSAGE_ID);
	}

	private void validateDuplicateComponentMethod(SeamComponentMethodType methodType, ISeamComponent component, String message, String preferenceKey, int message_id) {
		ISeamJavaComponentDeclaration javaDeclaration = component.getJavaDeclaration();
		Set<ISeamComponentMethod> methods = javaDeclaration.getMethodsByType(methodType);
		if(methods!=null && methods.size()>1) {
			for (ISeamComponentMethod method : methods) {
				if(javaDeclaration.getSourcePath().equals(method.getSourcePath())) {
					IMethod javaMethod = (IMethod)method.getSourceMember();
					String methodName = javaMethod.getElementName();
					ITextSourceReference methodNameLocation = getNameLocation(method);
					addError(message, preferenceKey, new String[]{methodName}, methodNameLocation, javaDeclaration.getResource(), message_id);
				}
			}
		}
	}

	private void validateJavaDeclaration(ISeamComponent component, ISeamJavaComponentDeclaration declaration) {
		validateBijections(declaration);
		validateStatefulComponent(component);
		validateDuplicateComponentMethods(component);
		validateEntityComponent(component);
		validateDestroyMethod(component);
	}

	private void validateBijections(ISeamJavaComponentDeclaration declaration) {
		Set<IBijectedAttribute> bijections = declaration.getBijectedAttributes();
		if(bijections==null) {
			return;
		}
		for (IBijectedAttribute bijection : bijections) {
			if(bijection.isOfType(BijectedAttributeType.DATA_MODEL_SELECTION) || bijection.isOfType(BijectedAttributeType.DATA_MODEL_SELECTION_INDEX)) {
				validateDataModelSelection(declaration, bijection);
			} else {
				validateInAndOut(declaration, bijection);
			}
		}
	}

	private void validateInAndOut(ISeamJavaComponentDeclaration declaration, IBijectedAttribute bijection) {
		String name = bijection.getName();
		if(name==null || name.startsWith("#{") || name.startsWith("${")) { //$NON-NLS-1$ //$NON-NLS-2$
			return;
		}
		// Validate @In
		if(bijection.isOfType(BijectedAttributeType.IN)) {
			// save link between java source and variable name
			validationContext.addLinkedCoreResource(SHORT_ID, name, declaration.getSourcePath(), false);

			Set<ISeamContextVariable> variables = declaration.getVariablesByName(name);
			if(variables == null || variables.isEmpty()) variables = seamProject.getVariablesByName(name);
			if(variables == null || variables.isEmpty()) {
				ISeamProject parentProject = seamProject.getParentProject();
				if(parentProject != null) {
					variables = parentProject.getVariablesByName(name);
				}
			}
			if(variables==null || variables.size()<1) {
				// Injection has unknown name. Mark it.
				IResource declarationResource = declaration.getResource();
				ITextSourceReference nameRef = getNameLocation(bijection);
				if(nameRef == null) {
					nameRef = bijection;
				}
				addError(SeamValidationMessages.UNKNOWN_VARIABLE_NAME, SeamPreferences.UNKNOWN_VARIABLE_NAME, new String[]{name}, nameRef, declarationResource);
			}
		} else {
			// save link between java source and variable name
			validationContext.addLinkedCoreResource(SHORT_ID, name, declaration.getSourcePath(), true);
		}
	}

	private void validateDataModelSelection(ISeamJavaComponentDeclaration declaration, IBijectedAttribute bijection) {
		String dataModelName = bijection.getValue();
		String selectionName = bijection.getName();
		// save link between java source and variable name
		validationContext.addLinkedCoreResource(SHORT_ID, selectionName, declaration.getSourcePath(), false);
		if(dataModelName==null) {
			// here must be the only one @DataModel in the component
			Set<IBijectedAttribute> dataBinders = declaration.getBijectedAttributesByType(BijectedAttributeType.DATA_BINDER);
			ITextSourceReference location = null;
			if(dataBinders!=null && dataBinders.size()>1) {
				for (IBijectedAttribute dataBinder : dataBinders) {
					location = dataBinder.getLocationFor(SeamComponentDeclaration.PATH_OF_NAME);
					addError(SeamValidationMessages.MULTIPLE_DATA_BINDER, SeamPreferences.MULTIPLE_DATA_BINDER, location, declaration.getResource());
				}
			}
		} else {
			// save link between java source and Data Model name
			validationContext.addLinkedCoreResource(SHORT_ID, dataModelName, declaration.getSourcePath(), true);
			Set<IBijectedAttribute> dataBinders = declaration.getBijectedAttributesByName(dataModelName);
			if(dataBinders!=null) {
				for (IBijectedAttribute dataBinder : dataBinders) {
					if(dataBinder.isOfType(BijectedAttributeType.DATA_BINDER) || dataBinder.isOfType(BijectedAttributeType.OUT)) {
						return;
					}
				}
			}
			addError(SeamValidationMessages.UNKNOWN_DATA_MODEL, SeamPreferences.UNKNOWN_DATA_MODEL, new String[]{dataModelName}, SeamUtil.getLocationOfAttribute(bijection, DataModelSelectionAttribute.VALUE), declaration.getResource());
		}
	}

	/*
	 *  Validates methods of java classes. They must belong components.
	 */
	private void validateMethodsOfUnknownComponent(ISeamJavaComponentDeclaration declaration) {
		if(seamProject.getComponentsByPath(declaration.getSourcePath()).isEmpty()) {
			IMember member = declaration.getSourceMember();
			try {
				if(member!=null && !Flags.isAbstract(member.getFlags())) {
					validateMethodOfUnknownComponent(SeamComponentMethodType.CREATE, declaration, SeamValidationMessages.CREATE_DOESNT_BELONG_TO_COMPONENT, SeamPreferences.CREATE_DOESNT_BELONG_TO_COMPONENT, CREATE_DOESNT_BELONG_TO_COMPONENT_MESSAGE_ID);
					validateMethodOfUnknownComponent(SeamComponentMethodType.UNWRAP, declaration, SeamValidationMessages.UNWRAP_DOESNT_BELONG_TO_COMPONENT, SeamPreferences.UNWRAP_DOESNT_BELONG_TO_COMPONENT, UNWRAP_DOESNT_BELONG_TO_COMPONENT_MESSAGE_ID);
					validateMethodOfUnknownComponent(SeamComponentMethodType.OBSERVER, declaration, SeamValidationMessages.OBSERVER_DOESNT_BELONG_TO_COMPONENT, SeamPreferences.OBSERVER_DOESNT_BELONG_TO_COMPONENT, OBSERVER_DOESNT_BELONG_TO_COMPONENT_MESSAGE_ID);
				}
			} catch (JavaModelException e) {
				SeamCorePlugin.getPluginLog().logError(e);
			}
		}
		validationContext.removeUnnamedCoreResource(SHORT_ID, declaration.getSourcePath());
	}

	private void validateDestroyMethod(ISeamComponent component) {
		if(component.isStateless()) {
			ISeamJavaComponentDeclaration javaDeclaration = component.getJavaDeclaration();
			Set<ISeamComponentMethod> methods = javaDeclaration.getMethodsByType(SeamComponentMethodType.DESTROY);
			if(methods==null) {
				return;
			}
			for (ISeamComponentMethod method : methods) {
				IMethod javaMethod = (IMethod)method.getSourceMember();
				String methodName = javaMethod.getElementName();
				if(javaDeclaration.getSourcePath().equals(javaMethod.getPath())) {
					validationContext.addLinkedCoreResource(SHORT_ID, component.getName(), javaDeclaration.getSourcePath(), true);
					ITextSourceReference methodNameLocation = getNameLocation(method);
					addError(SeamValidationMessages.DESTROY_METHOD_BELONGS_TO_STATELESS_SESSION_BEAN, SeamPreferences.DESTROY_METHOD_BELONGS_TO_STATELESS_SESSION_BEAN, new String[]{methodName}, methodNameLocation, method.getResource(), DESTROY_METHOD_BELONGS_TO_STATELESS_SESSION_BEAN_MESSAGE_ID);
				}
			}
		}
	}

	private void validateMethodOfUnknownComponent(SeamComponentMethodType methodType, ISeamJavaComponentDeclaration declaration, String message, String preferenceKey, int message_id) {
		Set<ISeamComponentMethod> methods = declaration.getMethodsByType(methodType);
		if(methods!=null && !methods.isEmpty()) {
			for (ISeamComponentMethod method : methods) {
				IMethod javaMethod = (IMethod)method.getSourceMember();
				String methodName = javaMethod.getElementName();
				if(declaration.getSourcePath().equals(javaMethod.getPath())) {
					ITextSourceReference methodNameLocation = getNameLocation(method);
					addError(message, preferenceKey, new String[]{methodName}, methodNameLocation, method.getResource(), message_id);
					validationContext.addUnnamedCoreResource(SHORT_ID, declaration.getSourcePath());
				}
			}
		} else {
			validationContext.removeUnnamedCoreResource(SHORT_ID, declaration.getSourcePath());
		}
	}

	private void validateXMLVersion(IFile file) {
		String ext = file.getFileExtension();
		if(!"xml".equals(ext)) return;
		
		XModelObject o = EclipseResourceUtil.createObjectForResource(file);
		
		if(o == null) return;
		if(!o.getModelEntity().getName().startsWith("FileSeamComponent")) return;
		
		Set<String> vs = getXMLVersions(o);
		
		SeamRuntime runtime = seamProject.getRuntime();
		if(runtime == null) return;
		
		String version = runtime.getVersion().toString();
		String wrongVersion = null;
		for (String v: vs) {
			if(!v.startsWith(version)) {
				wrongVersion = v;
				break;
			}
		}
		if(wrongVersion != null) {
			addError(
				SeamValidationMessages.INVALID_XML_VERSION, 
				SeamPreferences.INVALID_XML_VERSION, 
				new String[]{wrongVersion, version}, 
				file);
		}
	}

	private Set<String> getXMLVersions(XModelObject o) {
		Set<String> result = new HashSet<String>();
		if(o.getModelEntity().getName().endsWith("11")) {
			result.add("1.1");
			return result;
		}
		String sl = o.getAttributeValue("xsi:schemaLocation");
		int i = 0;
		while(i >= 0 && i < sl.length()) {
			int j = sl.indexOf('-', i);
			if(j < 0) break;
			int k = sl.indexOf(".xsd", j);
			if(k < 0) break;
			String v = sl.substring(j + 1, k);
			if(isVersion(v)) result.add(v);
			i = k;
		}

		return result;
	}

	private boolean isVersion(String s) {
		if(s.length() == 0) return false;
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if(c != '.' && !Character.isDigit(c)) return false;
		}
		return true;
	}

	private void validatePageXML(IFile f) {
		if(f.isAccessible() && (f.getName().equals("pages.xml") || f.getName().endsWith(".page.xml"))) {
			XModelObject object = EclipseResourceUtil.createObjectForResource(f);
			if(object == null) return;
			if(object.getModelEntity().getName().startsWith(SeamPagesConstants.ENT_FILE_SEAM_PAGE)) {
				validatePageViewIds(object, f);
			}
		}
	}

	private void validatePageViewIds(XModelObject o, IFile f) {
		String entity = o.getModelEntity().getName();
		validatePageViewId(o, f);
		if(entity.startsWith(SeamPagesConstants.ENT_REDIRECT) || entity.startsWith(SeamPagesConstants.ENT_RENDER)) {
		} else {
			XModelObject[] cs = o.getChildren();
			for (XModelObject c: cs) {
				validatePageViewIds(c, f);
			}
		}
	}

	static String ATTR_NO_CONVERSATION_VIEW_ID = "no conversation view id";
	static String ATTR_LOGIN_VIEW_ID = "login view id";

	private void validatePageViewId(XModelObject object, IFile f) {
		validatePageViewId(object, f, SeamPagesConstants.ATTR_VIEW_ID);
		validatePageViewId(object, f, ATTR_NO_CONVERSATION_VIEW_ID);
		validatePageViewId(object, f, ATTR_LOGIN_VIEW_ID);
	}

	private void validatePageViewId(XModelObject object, IFile f, String attr) {
		if(object.getModelEntity().getAttribute(attr) == null) return;
		String path = object.getAttributeValue(attr);
		if(path == null || path.length() == 0 || path.indexOf('*') >= 0) return;
		path = path.replace('\\', '/');
		if(path.indexOf('?') >= 0) {
			path = path.substring(0, path.indexOf('?'));
		}
		XModelObject target = object.getModel().getByPath(path);
		if(target == null) {
			XMLValueInfo i = new XMLValueInfo(object, attr);
			addError(NLS.bind(SeamValidationMessages.UNRESOLVED_VIEW_ID, path), SeamPreferences.UNRESOLVED_VIEW_ID, i, f);
		}
		
	}
	
	public IMarker addError(String message, String preferenceKey,
			String[] messageArguments, ITextSourceReference location,
			IResource target, int messageId) {
		IMarker marker = addError(message, preferenceKey, messageArguments, location, target);
		try{
			if(marker!=null) {
				marker.setAttribute(MESSAGE_ID_ATTRIBUTE_NAME, new Integer(messageId));
			}
		}catch(CoreException ex){
			SeamCorePlugin.getDefault().logError(ex);
		}
		return marker;
	}

}
