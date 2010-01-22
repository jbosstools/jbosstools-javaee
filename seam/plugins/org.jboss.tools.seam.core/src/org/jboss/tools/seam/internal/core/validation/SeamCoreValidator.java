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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.Flags;
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
import org.jboss.tools.jst.web.kb.IKbProject;
import org.jboss.tools.jst.web.kb.KbProjectFactory;
import org.jboss.tools.jst.web.kb.internal.KbProject;
import org.jboss.tools.jst.web.kb.internal.validation.ContextValidationHelper;
import org.jboss.tools.jst.web.kb.internal.validation.ValidatingProjectSet;
import org.jboss.tools.jst.web.kb.internal.validation.ValidatorManager;
import org.jboss.tools.jst.web.kb.validation.IValidatingProjectSet;
import org.jboss.tools.jst.web.kb.validation.IValidationContext;
import org.jboss.tools.jst.web.kb.validation.IValidator;
import org.jboss.tools.jst.web.kb.validation.ValidatorUtil;
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

/**
 * Validator for Java and XML files.
 * @author Alexey Kazakov
 */
public class SeamCoreValidator extends SeamValidationErrorManager implements IValidator {

	public static final String ID = "org.jboss.tools.seam.core.CoreValidator";

	private ISeamProject seamProject;
	private String projectName;

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.jst.web.kb.validation.IValidator#getId()
	 */
	public String getId() {
		return ID;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.jst.web.kb.validation.IValidator#getValidatingProjects(org.eclipse.core.resources.IProject)
	 */
	public IValidatingProjectSet getValidatingProjects(IProject project) {
		return getSeamValidatingProjects(project);
	}

	public static IValidatingProjectSet getSeamValidatingProjects(IProject project) {
		SeamProjectsSet set = new SeamProjectsSet(project);
		IProject war = set.getWarProject();
		IValidationContext rootContext = null;
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
			}
		}

		List<IProject> projects = new ArrayList<IProject>();
		IProject[] array = set.getAllProjects();
		for (int i = 0; i < array.length; i++) {
			if(array[i].isAccessible()) {
				projects.add(array[i]);
			}
		}
		return new ValidatingProjectSet(project, projects, rootContext);
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.jst.web.kb.validation.IValidator#shouldValidate(org.eclipse.core.resources.IProject)
	 */
	public boolean shouldValidate(IProject project) {
		try {
			// TODO check preferences
			return project!=null && project.isAccessible() && project.hasNature(ISeamProject.NATURE_ID);
		} catch (CoreException e) {
			SeamCorePlugin.getDefault().logError(e);
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.jst.web.kb.internal.validation.ValidationErrorManager#init(org.eclipse.core.resources.IProject, org.jboss.tools.jst.web.kb.internal.validation.ContextValidationHelper, org.jboss.tools.jst.web.kb.internal.validation.ValidatorManager, org.eclipse.wst.validation.internal.provisional.core.IReporter, org.jboss.tools.jst.web.kb.validation.IValidationContext)
	 */
	@Override
	public void init(IProject project, ContextValidationHelper validationHelper, org.eclipse.wst.validation.internal.provisional.core.IValidator manager, IReporter reporter) {
		super.init(project, validationHelper, manager, reporter);

		SeamProjectsSet set = new SeamProjectsSet(project);
		IProject warProject = set.getWarProject();
		seamProject = SeamCorePlugin.getSeamProject(warProject, false);
		projectName = seamProject.getProject().getName();
	}

	private boolean isPreferencesEnabled(IProject project) {
		return SeamPreferences.shouldValidateCore(project);
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.jst.web.kb.validation.IValidator#validate(java.util.Set, org.eclipse.core.resources.IProject, org.jboss.tools.jst.web.kb.internal.validation.ContextValidationHelper, org.jboss.tools.jst.web.kb.internal.validation.ValidatorManager, org.eclipse.wst.validation.internal.provisional.core.IReporter, org.jboss.tools.jst.web.kb.validation.IValidationContext)
	 */
	public IStatus validate(Set<IFile> changedFiles, IProject project, ContextValidationHelper validationHelper, ValidatorManager manager, IReporter reporter) throws ValidationException {
		init(project, validationHelper, manager, reporter);
		displaySubtask(SeamValidationMessages.SEARCHING_RESOURCES);

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
			if (ValidatorUtil.checkFileExtensionForJavaAndXml(currentFile)) {
				resources.add(currentFile.getFullPath());
				// Get new variable names from model
				Set<String> newVariableNamesOfChangedFile = getVariablesNameByResource(currentFile.getFullPath());
				Set<String> oldDeclarationsOfChangedFile = validationContext.getVariableNamesByCoreResource(currentFile.getFullPath(), true);
				for (String newVariableName : newVariableNamesOfChangedFile) {
					// Collect resources with new variable name.
					Set<IPath> linkedResources = validationContext.getCoreResourcesByVariableName(newVariableName, false);
					if(linkedResources!=null) {
						resources.addAll(linkedResources);
					}
					resources.addAll(getAllResourceOfComponent(currentFile.getFullPath()));
				}
				// Get old variable names which were linked with this resource.
				Set<String> oldVariablesNamesOfChangedFile = validationContext.getVariableNamesByCoreResource(currentFile.getFullPath(), false);
				if(oldVariablesNamesOfChangedFile!=null) {
					for (String name : oldVariablesNamesOfChangedFile) {
						Set<IPath> linkedResources = validationContext.getCoreResourcesByVariableName(name, false);
						if(linkedResources!=null) {
							resources.addAll(linkedResources);
						}
					}
				}
				// Save old declarations for EL validation. We need to validate all EL resources which use this variable name but only if the variable has been changed.
				if(oldDeclarationsOfChangedFile!=null) {
					for (String name : oldDeclarationsOfChangedFile) {
						validationContext.addVariableNameForELValidation(name);
					}
				}
				newResources.add(currentFile.getFullPath());
			}
		}
		// Validate all collected linked resources.
		// Remove all links between collected resources and variables names because they will be linked again during validation.
		validationContext.removeLinkedCoreResources(resources);

		IFile[] filesToValidate = new IFile[resources.size()];
		int i = 0;
		// We have to remove markers from all collected source files first
		for (IPath linkedResource : resources) {
			filesToValidate[i] = root.getFile(linkedResource);
			removeAllMessagesFromResource(filesToValidate[i++]);
		}
		i = 0;
		// Then we can validate them
		for (IPath linkedResource : resources) {
			validateComponent(linkedResource, checkedComponents, newResources);
			validateFactory(linkedResource, markedDuplicateFactoryNames);
			validateXMLVersion(filesToValidate[i++]);
		}

		// If changed files are *.java or component.xml then re-validate all unnamed resources.
		if(validateUnnamedResources) {
			Set<IPath> unnamedResources = validationContext.getUnnamedCoreResources();
			newResources.addAll(unnamedResources);
			for (IPath path : newResources) {
				IFile file = root.getFile(path);
				if(file!=null && file.exists()) {
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
	 * @see org.jboss.tools.seam.internal.core.validation.ISeamValidator#validateAll()
	 */
	public IStatus validateAll(IProject project, ContextValidationHelper validationHelper, ValidatorManager manager, IReporter reporter) throws ValidationException {
		init(project, validationHelper, manager, reporter);
		removeAllMessagesFromResource(seamProject.getProject());
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

		return OK_STATUS;
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
						validationContext.addLinkedCoreResource(factoryName, factory.getSourcePath(), true);
						location = SeamUtil.getLocationOfName(factory);
						this.addError(SeamValidationMessages.DUPLICATE_VARIABLE_NAME, SeamPreferences.DUPLICATE_VARIABLE_NAME, new String[]{factoryName}, location, factory.getResource());
					}
					// Mark duplicate variable.
					if(!SeamUtil.isJar(variable.getSourcePath())) {
						IResource resource = SeamUtil.getComponentResourceWithName(variable);
						validationContext.addLinkedCoreResource(factoryName, resource.getFullPath(), true);
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
			validationContext.addLinkedCoreResource(factoryName, factory.getSourcePath(), true);
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
						validationContext.addLinkedCoreResource(componentName, declaration.getSourcePath(), true);
						// Save link between component name and all supers of java declaration.
						try {
							IType[] superTypes = TypeInfoCollector.getSuperTypes(type).getSuperTypes();
							for (int i = 0; superTypes != null && i < superTypes.length; i++) {
								if(!superTypes[i].isBinary()) {
									IPath path = superTypes[i].getResource().getFullPath();
									validationContext.addLinkedCoreResource(componentName, path, true);
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
									addError(SeamValidationMessages.NONUNIQUE_COMPONENT_NAME_MESSAGE, SeamPreferences.NONUNIQUE_COMPONENT_NAME, new String[]{componentName}, location, checkedDeclarationResource);
								}
								markedDeclarations.add(checkedDeclaration);
							}
							// Mark next wrong declaration with that name
							markedDeclarations.add(javaDeclaration);
							ITextSourceReference location = ((SeamComponentDeclaration)javaDeclaration).getLocationFor(SeamComponentDeclaration.PATH_OF_NAME);
							if(!SeamUtil.isEmptyLocation(location)) {
								addError(SeamValidationMessages.NONUNIQUE_COMPONENT_NAME_MESSAGE, SeamPreferences.NONUNIQUE_COMPONENT_NAME, new String[]{componentName}, location, javaDeclarationResource);
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

				validationContext.addLinkedCoreResource(componentName, declaration.getSourcePath(), true);

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
								addError(SeamValidationMessages.NONUNIQUE_COMPONENT_NAME_MESSAGE, SeamPreferences.NONUNIQUE_COMPONENT_NAME, new String[]{componentName}, location, checkedDeclarationResource);
							}
							markedDeclarations.add(checkedDeclaration);
						}
						// Mark next wrong declaration with that name
						markedDeclarations.add(declaration);
						ITextSourceReference location = ((SeamComponentDeclaration)declaration).getLocationFor(SeamComponentDeclaration.PATH_OF_NAME);
						if(!SeamUtil.isEmptyLocation(location)) {
							addError(SeamValidationMessages.NONUNIQUE_COMPONENT_NAME_MESSAGE, SeamPreferences.NONUNIQUE_COMPONENT_NAME, new String[]{componentName}, location, declaration.getResource());
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
									if(javaPrecedence.equals(precedence)) {
										if(!markedJavaDeclarations.contains(javaDec)) {
											markedJavaDeclarations.add(javaDec);
											ITextSourceReference location = ((SeamComponentDeclaration)javaDec).getLocationFor(SeamComponentDeclaration.PATH_OF_NAME);
											addError(SeamValidationMessages.NONUNIQUE_COMPONENT_NAME_MESSAGE, SeamPreferences.NONUNIQUE_COMPONENT_NAME, new String[]{componentName}, location, javaDec.getResource());
										}
										if(!markedDeclarations.contains(declaration)) {
											markedDeclarations.add(declaration);
											ITextSourceReference location = ((SeamComponentDeclaration)declaration).getLocationFor(SeamComponentDeclaration.PATH_OF_NAME);
											addError(SeamValidationMessages.NONUNIQUE_COMPONENT_NAME_MESSAGE, SeamPreferences.NONUNIQUE_COMPONENT_NAME, new String[]{componentName}, location, declaration.getResource());
										}
									}
								}
							}
						}
					}
				}

				String className = declaration.getClassName();
				if(className!=null) {
					IType type = null;
					// validate class name
					try {
						IProject p = seamProject.getProject();
//						type = EclipseJavaUtil.findType(EclipseResourceUtil.getJavaProject(p), className);
						type = EclipseResourceUtil.getJavaProject(p).findType(className);
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
							return;
						} else if(!type.isBinary()) {
							validationContext.addLinkedCoreResource(componentName, type.getResource().getFullPath(), true);
						}
					} catch (JavaModelException e) {
						SeamCorePlugin.getDefault().logError(SeamCoreMessages.SEAM_CORE_VALIDATOR_ERROR_VALIDATING_SEAM_CORE, e);
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
						boolean ok = type.isBinary() || SeamUtil.findProperty(type, name)!=null;
						if(!ok) {
							addError(SeamValidationMessages.UNKNOWN_COMPONENT_PROPERTY, SeamPreferences.UNKNOWN_COMPONENT_PROPERTY, new String[]{type.getElementName(), componentName, name}, property, declaration.getResource());
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
				addError(SeamValidationMessages.ENTITY_COMPONENT_WRONG_SCOPE, SeamPreferences.ENTITY_COMPONENT_WRONG_SCOPE, new String[]{component.getName()}, location, javaDeclaration.getResource());
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
		return new SeamTextSourceReference(length, offset);
	}

	private void validateStatefulComponent(ISeamComponent component) {
		if(component.isStateful()) {
			ISeamJavaComponentDeclaration javaDeclaration = component.getJavaDeclaration();
			validateStatefulComponentMethods(SeamComponentMethodType.DESTROY, component, SeamValidationMessages.STATEFUL_COMPONENT_DOES_NOT_CONTAIN_DESTROY, SeamPreferences.STATEFUL_COMPONENT_DOES_NOT_CONTENT_DESTROY);
			validateStatefulComponentMethods(SeamComponentMethodType.REMOVE, component, SeamValidationMessages.STATEFUL_COMPONENT_DOES_NOT_CONTAIN_REMOVE, SeamPreferences.STATEFUL_COMPONENT_DOES_NOT_CONTENT_REMOVE);
			ScopeType scope = component.getScope();
			if(scope == ScopeType.PAGE || scope == ScopeType.STATELESS) {
				ITextSourceReference location = getScopeLocation(component);
				addError(SeamValidationMessages.STATEFUL_COMPONENT_WRONG_SCOPE, SeamPreferences.STATEFUL_COMPONENT_WRONG_SCOPE, new String[]{component.getName()}, location, javaDeclaration.getResource());
			}
			validateDuplicateComponentMethod(SeamComponentMethodType.REMOVE, component, SeamValidationMessages.DUPLICATE_REMOVE, SeamPreferences.DUPLICATE_REMOVE);
		}
	}

	private void validateStatefulComponentMethods(SeamComponentMethodType methodType, ISeamComponent component, String message, String preferenceKey) {
		ISeamJavaComponentDeclaration javaDeclaration = component.getJavaDeclaration();
		ITextSourceReference classNameLocation = getNameLocation(javaDeclaration);
		Set<ISeamComponentMethod> methods = javaDeclaration.getMethodsByType(methodType);
		if(methods==null || methods.isEmpty()) {
			addError(message, preferenceKey, new String[]{component.getName()}, classNameLocation, javaDeclaration.getResource());
		}
	}

	private void validateDuplicateComponentMethods(ISeamComponent component) {
		validateDuplicateComponentMethod(SeamComponentMethodType.DESTROY, component, SeamValidationMessages.DUPLICATE_DESTROY, SeamPreferences.DUPLICATE_DESTROY);
		validateDuplicateComponentMethod(SeamComponentMethodType.CREATE, component, SeamValidationMessages.DUPLICATE_CREATE, SeamPreferences.DUPLICATE_CREATE);
		validateDuplicateComponentMethod(SeamComponentMethodType.UNWRAP, component, SeamValidationMessages.DUPLICATE_UNWRAP, SeamPreferences.DUPLICATE_UNWRAP);
	}

	private void validateDuplicateComponentMethod(SeamComponentMethodType methodType, ISeamComponent component, String message, String preferenceKey) {
		ISeamJavaComponentDeclaration javaDeclaration = component.getJavaDeclaration();
		Set<ISeamComponentMethod> methods = javaDeclaration.getMethodsByType(methodType);
		if(methods!=null && methods.size()>1) {
			for (ISeamComponentMethod method : methods) {
				if(javaDeclaration.getSourcePath().equals(method.getSourcePath())) {
					IMethod javaMethod = (IMethod)method.getSourceMember();
					String methodName = javaMethod.getElementName();
					ITextSourceReference methodNameLocation = getNameLocation(method);
					addError(message, preferenceKey, new String[]{methodName}, methodNameLocation, javaDeclaration.getResource());
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
			validationContext.addLinkedCoreResource(name, declaration.getSourcePath(), false);

			Set<ISeamContextVariable> variables = seamProject.getVariablesByName(name);
			if(variables==null || variables.size()<1) {
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
			validationContext.addLinkedCoreResource(name, declaration.getSourcePath(), true);
		}
	}

	private void validateDataModelSelection(ISeamJavaComponentDeclaration declaration, IBijectedAttribute bijection) {
		String dataModelName = bijection.getValue();
		String selectionName = bijection.getName();
		// save link between java source and variable name
		validationContext.addLinkedCoreResource(selectionName, declaration.getSourcePath(), false);
		if(dataModelName==null) {
			// here must be the only one @DataModel in the component
			Set<IBijectedAttribute> dataBinders = declaration.getBijectedAttributesByType(BijectedAttributeType.DATA_BINDER);
			if(dataBinders!=null && dataBinders.size()>1) {
				for (IBijectedAttribute dataBinder : dataBinders) {
					addError(SeamValidationMessages.MULTIPLE_DATA_BINDER, SeamPreferences.MULTIPLE_DATA_BINDER, dataBinder, declaration.getResource());
				}
			}
		} else {
			// save link between java source and Data Model name
			validationContext.addLinkedCoreResource(dataModelName, declaration.getSourcePath(), true);
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
					validateMethodOfUnknownComponent(SeamComponentMethodType.CREATE, declaration, SeamValidationMessages.CREATE_DOESNT_BELONG_TO_COMPONENT, SeamPreferences.CREATE_DOESNT_BELONG_TO_COMPONENT);
					validateMethodOfUnknownComponent(SeamComponentMethodType.UNWRAP, declaration, SeamValidationMessages.UNWRAP_DOESNT_BELONG_TO_COMPONENT, SeamPreferences.UNWRAP_DOESNT_BELONG_TO_COMPONENT);
					validateMethodOfUnknownComponent(SeamComponentMethodType.OBSERVER, declaration, SeamValidationMessages.OBSERVER_DOESNT_BELONG_TO_COMPONENT, SeamPreferences.OBSERVER_DOESNT_BELONG_TO_COMPONENT);
				}
			} catch (JavaModelException e) {
				SeamCorePlugin.getPluginLog().logError(e);
			}
		}
		validationContext.removeUnnamedCoreResource(declaration.getSourcePath());
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
					validationContext.addLinkedCoreResource(component.getName(), javaDeclaration.getSourcePath(), true);
					ITextSourceReference methodNameLocation = getNameLocation(method);
					addError(SeamValidationMessages.DESTROY_METHOD_BELONGS_TO_STATELESS_SESSION_BEAN, SeamPreferences.DESTROY_METHOD_BELONGS_TO_STATELESS_SESSION_BEAN, new String[]{methodName}, methodNameLocation, method.getResource());
				}
			}
		}
	}

	private void validateMethodOfUnknownComponent(SeamComponentMethodType methodType, ISeamJavaComponentDeclaration declaration, String message, String preferenceKey) {
		Set<ISeamComponentMethod> methods = declaration.getMethodsByType(methodType);
		if(methods!=null && !methods.isEmpty()) {
			for (ISeamComponentMethod method : methods) {
				IMethod javaMethod = (IMethod)method.getSourceMember();
				String methodName = javaMethod.getElementName();
				if(declaration.getSourcePath().equals(javaMethod.getPath())) {
					ITextSourceReference methodNameLocation = getNameLocation(method);
					addError(message, preferenceKey, new String[]{methodName}, methodNameLocation, method.getResource());
					validationContext.addUnnamedCoreResource(declaration.getSourcePath());
				}
			}
		} else {
			validationContext.removeUnnamedCoreResource(declaration.getSourcePath());
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
}