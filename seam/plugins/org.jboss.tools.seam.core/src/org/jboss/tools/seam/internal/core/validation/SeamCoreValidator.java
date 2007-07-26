/*******************************************************************************
 * Copyright (c) 2007 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.seam.internal.core.validation;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.wst.validation.internal.core.ValidationException;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.seam.core.BijectedAttributeType;
import org.jboss.tools.seam.core.IBijectedAttribute;
import org.jboss.tools.seam.core.IRole;
import org.jboss.tools.seam.core.ISeamAnnotatedFactory;
import org.jboss.tools.seam.core.ISeamComponent;
import org.jboss.tools.seam.core.ISeamComponentDeclaration;
import org.jboss.tools.seam.core.ISeamComponentMethod;
import org.jboss.tools.seam.core.ISeamContextVariable;
import org.jboss.tools.seam.core.ISeamFactory;
import org.jboss.tools.seam.core.ISeamJavaComponentDeclaration;
import org.jboss.tools.seam.core.ISeamProject;
import org.jboss.tools.seam.core.ISeamProperty;
import org.jboss.tools.seam.core.ISeamTextSourceReference;
import org.jboss.tools.seam.core.ISeamXmlComponentDeclaration;
import org.jboss.tools.seam.core.ISeamXmlFactory;
import org.jboss.tools.seam.core.ScopeType;
import org.jboss.tools.seam.core.SeamComponentMethodType;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.core.SeamPreferences;
import org.jboss.tools.seam.internal.core.SeamComponentDeclaration;
import org.jboss.tools.seam.internal.core.SeamJavaComponentDeclaration;
import org.jboss.tools.seam.internal.core.SeamProject;
import org.jboss.tools.seam.internal.core.SeamTextSourceReference;

/**
 * Validator for Java and XML files.
 * @author Alexey Kazakov
 */
public class SeamCoreValidator extends SeamValidator {

	protected static final String NONUNIQUE_COMPONENT_NAME_MESSAGE_ID = "NONUNIQUE_COMPONENT_NAME_MESSAGE";
	protected static final String UNKNOWN_VARIABLE_NAME_MESSAGE_ID = "UNKNOWN_VARIABLE_NAME";
	protected static final String STATEFUL_COMPONENT_DOES_NOT_CONTAIN_METHOD_SUFIX_MESSAGE_ID = "STATEFUL_COMPONENT_DOES_NOT_CONTAIN_";
	protected static final String DUPLICATE_METHOD_PREFIX_MESSAGE_ID = "DUPLICATE_";
	protected static final String REMOVE_METHOD_SUFIX_MESSAGE_ID = "REMOVE";
	protected static final String DESTROY_METHOD_SUFIX_MESSAGE_ID = "DESTROY";
	protected static final String CREATE_METHOD_SUFIX_MESSAGE_ID = "CREATE";
	protected static final String UNWRAP_METHOD_SUFIX_MESSAGE_ID = "UNWRAP";
	protected static final String OBSERVER_METHOD_SUFIX_MESSAGE_ID = "OBSERVER";
	protected static final String NONCOMPONENTS_METHOD_SUFIX_MESSAGE_ID = "_DOESNT_BELONG_TO_COMPONENT";
	protected static final String STATEFUL_COMPONENT_WRONG_SCOPE_MESSAGE_ID = "STATEFUL_COMPONENT_WRONG_SCOPE";
	protected static final String ENTITY_COMPONENT_WRONG_SCOPE_MESSAGE_ID = "ENTITY_COMPONENT_WRONG_SCOPE";
	protected static final String UNKNOWN_FACTORY_NAME_MESSAGE_ID = "UNKNOWN_FACTORY_NAME";
	protected static final String MULTIPLE_DATA_BINDER_MESSAGE_ID = "MULTIPLE_DATA_BINDER";
	protected static final String DUPLICATE_VARIABLE_NAME_MESSAGE_ID = "DUPLICATE_VARIABLE_NAME";
	protected static final String UNKNOWN_DATA_MODEL_MESSAGE_ID = "UNKNOWN_DATA_MODEL";
	protected static final String UNKNOWN_COMPONENT_CLASS_NAME_MESSAGE_ID = "UNKNOWN_COMPONENT_CLASS_NAME";
	protected static final String UNKNOWN_COMPONENT_PROPERTY_MESSAGE_ID = "UNKNOWN_COMPONENT_PROPERTY";

	public SeamCoreValidator(SeamValidatorManager validatorManager,
			SeamValidationHelper coreHelper, IReporter reporter,
			SeamValidationContext validationContext, ISeamProject project) {
		super(validatorManager, coreHelper, reporter, validationContext, project);
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.seam.internal.core.validation.ISeamValidator#isEnabled()
	 */
	public boolean isEnabled() {
		return SeamPreferences.isValidateCore(project);
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.seam.internal.core.validation.ISeamValidator#validate(java.util.Set)
	 */
	public IStatus validate(Set<IFile> changedFiles) throws ValidationException {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		Set<ISeamComponent> checkedComponents = new HashSet<ISeamComponent>();
		Set<String> markedDuplicateFactoryNames = new HashSet<String>();
		// Collect all resources which we must validate.
		Set<IPath> resources = new HashSet<IPath>(); // Resources which we have to validate.
		Set<IPath> newResources = new HashSet<IPath>(); // New (unlinked) resources file
		for(IFile currentFile : changedFiles) {
			if(reporter.isCancelled()) {
				break;
			}
			if (checkFileExtension(currentFile)) {
				// Get all variable names which were linked with this resource.
				Set<String> oldVariablesNamesOfChangedFile = validationContext.getVariableNamesByCoreResource(currentFile.getFullPath());
				if(oldVariablesNamesOfChangedFile!=null) {
					// Check if variable name was changed in source file
					Set<String> newVariableNamesOfChangedFile = getVariablesNameByResource(currentFile.getFullPath());
					for (String newVariableName : newVariableNamesOfChangedFile) {
						if(!oldVariablesNamesOfChangedFile.contains(newVariableName)) {
							// Name was changed.
							// Collect resources with new component name.
							Set<IPath> linkedResources = validationContext.getCoreResourcesByVariableName(newVariableName);
							if(linkedResources!=null) {
								resources.addAll(linkedResources);
							}
						}
					}
					resources.add(currentFile.getFullPath());

					// Collect all linked resources with old variable names.
					for (String name : oldVariablesNamesOfChangedFile) {
						Set<IPath> linkedResources = validationContext.getCoreResourcesByVariableName(name);
						if(linkedResources!=null) {
							resources.addAll(linkedResources);
						}
						// Save old names for EL validation. We need to validate all EL resources which use this variable name.
						validationContext.addVariableNameForELValidation(name);
					}
				} else {
					// Validate new (unlinked) source file.
					resources.add(currentFile.getFullPath());
				}
				newResources.add(currentFile.getFullPath());
			}
		}
		// Validate all collected linked resources.
		// Remove all links between collected resources and variables names because they will be linked again during validation.
		validationContext.removeLinkedCoreResources(resources);
		for (IPath linkedResource : resources) {
			// Remove markers from collected source file
			IFile sourceFile = root.getFile(linkedResource);
			reporter.removeMessageSubset(validationManager, sourceFile, ISeamValidator.MARKED_SEAM_RESOURCE_MESSAGE_GROUP);
			validateComponent(linkedResource, checkedComponents);
			validateFactory(linkedResource, markedDuplicateFactoryNames);
		}

		// Validate all unnamed resources.
		Set<IPath> unnamedResources = validationContext.getUnnamedCoreResources();
		newResources.addAll(unnamedResources);
		for (IPath path : newResources) {
			Set<SeamJavaComponentDeclaration> declarations = ((SeamProject)project).findJavaDeclarations(path);
			for (SeamJavaComponentDeclaration d : declarations) {
				validateMethodsOfUnknownComponent(d);
			}
		}

		return OK_STATUS;
	}

	/*
	 * (non-Javadoc)
	 * @see org.jboss.tools.seam.internal.core.validation.ISeamValidator#validateAll()
	 */
	public IStatus validateAll() throws ValidationException {
		Set<ISeamComponent> components = project.getComponents();
		for (ISeamComponent component : components) {
			if(reporter.isCancelled()) {
				return OK_STATUS;
			}
			validateComponent(component);
		}
		Set<ISeamFactory> factories = project.getFactories();
		Set<String> markedDuplicateFactoryNames = new HashSet<String>();
		for (ISeamFactory factory : factories) {
			if(reporter.isCancelled()) {
				return OK_STATUS;
			}
			validateFactory(factory, markedDuplicateFactoryNames);
		}

		Map<String,SeamJavaComponentDeclaration> declarations = ((SeamProject)project).getAllJavaComponentDeclarations();
		Collection<SeamJavaComponentDeclaration> values = declarations.values();
		for (SeamJavaComponentDeclaration d : values) {
			if(reporter.isCancelled()) {
				return OK_STATUS;
			}
			validateMethodsOfUnknownComponent(d);
		}

		return OK_STATUS;
	}

	private void validateFactory(IPath sourceFilePath, Set<String> markedDuplicateFactoryNames) {
		Set<ISeamFactory> factories = project.getFactoriesByPath(sourceFilePath);
		for (ISeamFactory factory : factories) {
			validateFactory(factory, markedDuplicateFactoryNames);
		}
	}

	private void validateFactory(ISeamFactory factory, Set<String> markedDuplicateFactoryNames) {
		if(coreHelper.isJar(factory.getResource())) {
			return;
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
			SeamCorePlugin.getDefault().logError("Factory method must have name: " + factory.getResource());
			return;
		}
		validateFactoryName(factory, name, markedDuplicateFactoryNames, false);
	}

	private void validateAnnotatedFactory(ISeamAnnotatedFactory factory, Set<String> markedDuplicateFactoryNames) {
		IMember sourceMember = factory.getSourceMember();
		if(sourceMember instanceof IMethod) {
			IMethod method = (IMethod)sourceMember;
			try {
				String returnType = method.getReturnType();
				if("V".equals(returnType)) {
					// return type is void
					String factoryName = factory.getName();
					if(factoryName==null) {
						String methodName = method.getElementName();
						if(methodName.startsWith("get") && methodName.length()>3) {
							// This is getter
							factoryName = methodName.substring(3);
						} else {
							// Unknown factory name
							SeamCorePlugin.getDefault().logError("Factory method must have name: " + factory.getResource());
							//factoryName = methodName;
							return;
						}
					}
					validateFactoryName(factory, factoryName, markedDuplicateFactoryNames, true);
				}
			} catch (JavaModelException e) {
				SeamCorePlugin.getDefault().logError("Error validating Seam Core", e);
			}
		} else {
			// factory must be java method!
			// JDT should mark it.
		}
	}

	private void validateFactoryName(ISeamFactory factory, String factoryName, Set<String> markedDuplicateFactoryNames, boolean validateUnknownName) {
		ScopeType factoryScope = factory.getScope();
		Set<ISeamContextVariable> variables = project.getVariablesByName(factoryName);
		boolean unknownVariable = true;
		boolean firstDuplicateVariableWasMarked = false;
		for (ISeamContextVariable variable : variables) {
			if((factoryScope == variable.getScope() || factoryScope.getPriority()>variable.getScope().getPriority())) {
				if(variable instanceof ISeamFactory || variable instanceof ISeamComponent || variable instanceof IRole) {
					if(variable!=factory && !markedDuplicateFactoryNames.contains(factoryName)) {
						// Duplicate factory name. Mark it.
						// save link to factory resource
						ISeamTextSourceReference location = null;
						if(!firstDuplicateVariableWasMarked) {
							firstDuplicateVariableWasMarked = true;
							// mark original factory
							validationContext.addLinkedCoreResource(factoryName, factory.getSourcePath());
							location = coreHelper.getLocationOfName(factory);
							this.addError(DUPLICATE_VARIABLE_NAME_MESSAGE_ID, SeamPreferences.DUPLICATE_VARIABLE_NAME, new String[]{factoryName}, location, factory.getResource());
						}
						// mark duplicate variable
						IResource resource = coreHelper.getComponentResourceWithName(variable);
						if(!coreHelper.isJar(resource)) {
							validationContext.addLinkedCoreResource(factoryName, resource.getFullPath());
							location = coreHelper.getLocationOfName(variable);
							this.addError(DUPLICATE_VARIABLE_NAME_MESSAGE_ID, SeamPreferences.DUPLICATE_VARIABLE_NAME, new String[]{factoryName}, location, resource);
						}
						markedDuplicateFactoryNames.add(factoryName);
					}
				} else {
					// We have that variable name
					unknownVariable = false;
				}
			}
		}
		if(unknownVariable && validateUnknownName) {
			// mark unknown factory name
			// save link to factory resource
			validationContext.addLinkedCoreResource(factoryName, factory.getSourcePath());
			this.addError(UNKNOWN_FACTORY_NAME_MESSAGE_ID, SeamPreferences.UNKNOWN_FACTORY_NAME, new String[]{factoryName}, coreHelper.getLocationOfName(factory), factory.getResource());
		}
	}

	private void validateComponent(IPath sourceFilePath, Set<ISeamComponent> checkedComponents) {
		Set<ISeamComponent> components = project.getComponentsByPath(sourceFilePath);
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
		Set<ISeamContextVariable> variables = project.getVariablesByPath(resourcePath);
		Set<String> result = new HashSet<String>();
		for (ISeamContextVariable variable : variables) {
			String name = variable.getName();
			result.add(name);
		}
		return result;
	}

	/*
	 * Validates the component 
	 */
	private void validateComponent(ISeamComponent component) {
		ISeamJavaComponentDeclaration firstJavaDeclaration = component.getJavaDeclaration();
		if(firstJavaDeclaration!=null) {
			HashMap<Integer, ISeamJavaComponentDeclaration> usedPrecedences = new HashMap<Integer, ISeamJavaComponentDeclaration>();
			Set<ISeamJavaComponentDeclaration> markedDeclarations = new HashSet<ISeamJavaComponentDeclaration>();
			int firstJavaDeclarationPrecedence = firstJavaDeclaration.getPrecedence();
			usedPrecedences.put(firstJavaDeclarationPrecedence, firstJavaDeclaration);
			Set<ISeamComponentDeclaration> declarations = component.getAllDeclarations();
			for (ISeamComponentDeclaration declaration : declarations) {
				if(declaration instanceof ISeamJavaComponentDeclaration) {
					ISeamJavaComponentDeclaration jd = (ISeamJavaComponentDeclaration)declaration;
					boolean sourceJavaDeclaration = !((IType)jd.getSourceMember()).isBinary();
					if(sourceJavaDeclaration) {
						// Save link between component name and java source file.
						validationContext.addLinkedCoreResource(component.getName(), declaration.getSourcePath());
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
								ISeamTextSourceReference location = ((SeamComponentDeclaration)checkedDeclaration).getLocationFor(SeamComponentDeclaration.PATH_OF_NAME);
								if(location!=null) {
									addError(NONUNIQUE_COMPONENT_NAME_MESSAGE_ID, SeamPreferences.NONUNIQUE_COMPONENT_NAME, new String[]{component.getName()}, location, checkedDeclarationResource);
								}
								markedDeclarations.add(checkedDeclaration);
							}
							// Mark next wrong declaration with that name
							markedDeclarations.add(javaDeclaration);
							ISeamTextSourceReference location = ((SeamComponentDeclaration)javaDeclaration).getLocationFor(SeamComponentDeclaration.PATH_OF_NAME);
							if(location!=null) {
								addError(NONUNIQUE_COMPONENT_NAME_MESSAGE_ID, SeamPreferences.NONUNIQUE_COMPONENT_NAME, new String[]{component.getName()}, location, javaDeclarationResource);
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
			Set<ISeamXmlComponentDeclaration> declarations = component.getXmlDeclarations();
			for (ISeamXmlComponentDeclaration declaration : declarations) {
				if(coreHelper.isJar(declaration)) {
					return;
				}
				validationContext.addLinkedCoreResource(componentName, declaration.getSourcePath());
				String className = component.getClassName();
				if(className!=null) {
					IType type = null;
					// validate class name
					try {
						IProject p = project.getProject();
//						type = EclipseJavaUtil.findType(EclipseResourceUtil.getJavaProject(p), className);
						type = EclipseResourceUtil.getJavaProject(p).findType(className);
						if(type==null) {
							// Mark wrong class name
							ISeamTextSourceReference location = ((SeamComponentDeclaration)declaration).getLocationFor(ISeamXmlComponentDeclaration.CLASS);
							if(location==null) {
								location = ((SeamComponentDeclaration)declaration).getLocationFor(ISeamXmlComponentDeclaration.NAME);
							}
							if(location==null) {
								location = declaration;
							}
							addError(UNKNOWN_COMPONENT_CLASS_NAME_MESSAGE_ID, SeamPreferences.UNKNOWN_COMPONENT_CLASS_NAME, new String[]{className}, location, declaration.getResource());
						} else if(!type.isBinary()) {
							validationContext.addLinkedCoreResource(componentName, type.getResource().getFullPath());
						}
					} catch (JavaModelException e) {
						SeamCorePlugin.getDefault().logError("Error validating Seam Core", e);
					}
					// validate properties
					Collection<ISeamProperty> properties = declaration.getProperties();
					for (ISeamProperty property : properties) {
						if(coreHelper.isJar(property)) {
							return;
						}
						String name = property.getName();
						if(name==null) {
							return;
						}
						boolean ok = type.isBinary() || coreHelper.findSetter(type, name)!=null;
						if(!ok) {
							addError(UNKNOWN_COMPONENT_PROPERTY_MESSAGE_ID, SeamPreferences.UNKNOWN_COMPONENT_PROPERTY, new String[]{type.getElementName(), componentName, name}, property, declaration.getResource());
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
				ISeamTextSourceReference location = getScopeLocation(component);
				addError(ENTITY_COMPONENT_WRONG_SCOPE_MESSAGE_ID, SeamPreferences.ENTITY_COMPONENT_WRONG_SCOPE, new String[]{component.getName()}, location, javaDeclaration.getResource());
			}
		}
	}

	private ISeamTextSourceReference getScopeLocation(ISeamComponent component) {
		ISeamJavaComponentDeclaration javaDeclaration = component.getJavaDeclaration();
		ISeamTextSourceReference location = ((SeamComponentDeclaration)javaDeclaration).getLocationFor(SeamComponentDeclaration.PATH_OF_SCOPE);
		if(location==null) {
			location = getClassNameLocation(javaDeclaration);
		}
		return location;
	}

	private ISeamTextSourceReference getClassNameLocation(ISeamJavaComponentDeclaration declaration) {
		int length = 0;
		int offset = 0;
		try {
			length = declaration.getSourceMember().getNameRange().getLength();
			offset = declaration.getSourceMember().getNameRange().getOffset();
		} catch (JavaModelException e) {
			SeamCorePlugin.getDefault().logError("Error validating Seam Core", e);
		}
		return new SeamTextSourceReference(length, offset);
	}

	private void validateStatefulComponent(ISeamComponent component) {
		if(component.isStateful()) {
			ISeamJavaComponentDeclaration javaDeclaration = component.getJavaDeclaration();
			validateStatefulComponentMethods(SeamComponentMethodType.DESTROY, component, DESTROY_METHOD_SUFIX_MESSAGE_ID, SeamPreferences.STATEFUL_COMPONENT_DOES_NOT_CONTENT_DESTROY);
			validateStatefulComponentMethods(SeamComponentMethodType.REMOVE, component, REMOVE_METHOD_SUFIX_MESSAGE_ID, SeamPreferences.STATEFUL_COMPONENT_DOES_NOT_CONTENT_REMOVE);
			ScopeType scope = component.getScope();
			if(scope == ScopeType.PAGE || scope == ScopeType.STATELESS) {
				ISeamTextSourceReference location = getScopeLocation(component);
				addError(STATEFUL_COMPONENT_WRONG_SCOPE_MESSAGE_ID, SeamPreferences.STATEFUL_COMPONENT_WRONG_SCOPE, new String[]{component.getName()}, location, javaDeclaration.getResource());
			}
		}
	}

	private void validateStatefulComponentMethods(SeamComponentMethodType methodType, ISeamComponent component, String postfixMessageId, String preferenceKey) {
		ISeamJavaComponentDeclaration javaDeclaration = component.getJavaDeclaration();
		ISeamTextSourceReference classNameLocation = getClassNameLocation(javaDeclaration);
		Set<ISeamComponentMethod> methods = javaDeclaration.getMethodsByType(methodType);
		if(methods==null || methods.size()==0) {
			addError(STATEFUL_COMPONENT_DOES_NOT_CONTAIN_METHOD_SUFIX_MESSAGE_ID + postfixMessageId, preferenceKey, new String[]{component.getName()}, classNameLocation, javaDeclaration.getResource());
		}
	}

	private void validateDuplicateComponentMethods(ISeamComponent component) {
		validateDuplicateComponentMethod(SeamComponentMethodType.DESTROY, component, DESTROY_METHOD_SUFIX_MESSAGE_ID, SeamPreferences.DUPLICATE_DESTROY);
		validateDuplicateComponentMethod(SeamComponentMethodType.REMOVE, component, REMOVE_METHOD_SUFIX_MESSAGE_ID, SeamPreferences.DUPLICATE_REMOVE);
		validateDuplicateComponentMethod(SeamComponentMethodType.CREATE, component, CREATE_METHOD_SUFIX_MESSAGE_ID, SeamPreferences.DUPLICATE_CREATE);
		validateDuplicateComponentMethod(SeamComponentMethodType.UNWRAP, component, UNWRAP_METHOD_SUFIX_MESSAGE_ID, SeamPreferences.DUPLICATE_UNWRAP);
	}

	private void validateDuplicateComponentMethod(SeamComponentMethodType methodType, ISeamComponent component, String postfixMessageId, String preferenceKey) {
		ISeamJavaComponentDeclaration javaDeclaration = component.getJavaDeclaration();
		Set<ISeamComponentMethod> methods = javaDeclaration.getMethodsByType(methodType);
		if(methods!=null && methods.size()>1) {
			for (ISeamComponentMethod method : methods) {
				IMethod javaMethod = (IMethod)method.getSourceMember();
				String methodName = javaMethod.getElementName();
				addError(DUPLICATE_METHOD_PREFIX_MESSAGE_ID + postfixMessageId, preferenceKey, new String[]{methodName}, method, javaDeclaration.getResource());
			}
		}
	}

	private void validateJavaDeclaration(ISeamComponent component, ISeamJavaComponentDeclaration declaration) {
		validateBijections(declaration);
		validateStatefulComponent(component);
		validateDuplicateComponentMethods(component);
		validateEntityComponent(component);

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
		if(name==null || name.startsWith("#{") || name.startsWith("${")) {
			return;
		}
		// save link between java source and variable name
		validationContext.addLinkedCoreResource(name, declaration.getSourcePath());

		// Validate @In
		if(bijection.isOfType(BijectedAttributeType.IN)) {
			Set<ISeamContextVariable> variables = project.getVariablesByName(name);
			if(variables==null || variables.size()<1) {
				// Injection has unknown name. Mark it.
				IResource declarationResource = declaration.getResource();
				addError(UNKNOWN_VARIABLE_NAME_MESSAGE_ID, SeamPreferences.UNKNOWN_VARIABLE_NAME, new String[]{name}, bijection, declarationResource);
			}
		}
	}

	private void validateDataModelSelection(ISeamJavaComponentDeclaration declaration, IBijectedAttribute bijection) {
		String name = bijection.getName();
		if(name==null) {
			// here must be the only one @DataModel in the component
			Set<IBijectedAttribute> dataBinders = declaration.getBijectedAttributesByType(BijectedAttributeType.DATA_BINDER);
			if(dataBinders.size()>0) {
				for (IBijectedAttribute dataBinder : dataBinders) {
					addError(MULTIPLE_DATA_BINDER_MESSAGE_ID, SeamPreferences.MULTIPLE_DATA_BINDER, dataBinder, declaration.getResource());
				}
			}
		} else {
			// save link between java source and variable name
			validationContext.addLinkedCoreResource(name, declaration.getSourcePath());
			Set<IBijectedAttribute> dataBinders = declaration.getBijectedAttributesByName(name);
			for (IBijectedAttribute dataBinder : dataBinders) {
				if(dataBinder.isOfType(BijectedAttributeType.DATA_BINDER) || dataBinder.isOfType(BijectedAttributeType.OUT)) {
					return;
				}
			}
			addError(UNKNOWN_DATA_MODEL_MESSAGE_ID, SeamPreferences.UNKNOWN_DATA_MODEL, new String[]{name}, coreHelper.getLocationOfName(bijection), declaration.getResource());
		}
	}

	/*
	 *  Validates methods of java classes. They must belong components.
	 */
	private void validateMethodsOfUnknownComponent(ISeamJavaComponentDeclaration declaration) {
		if(project.getComponentsByPath(declaration.getSourcePath()).size()>0) {
			validationContext.removeUnnamedCoreResource(declaration.getSourcePath());
			return;
		}
		validateMethodOfUnknownComponent(SeamComponentMethodType.DESTROY, declaration, DESTROY_METHOD_SUFIX_MESSAGE_ID, SeamPreferences.DESTROY_DOESNT_BELONG_TO_COMPONENT);
		validateMethodOfUnknownComponent(SeamComponentMethodType.CREATE, declaration, CREATE_METHOD_SUFIX_MESSAGE_ID, SeamPreferences.CREATE_DOESNT_BELONG_TO_COMPONENT);
		validateMethodOfUnknownComponent(SeamComponentMethodType.UNWRAP, declaration, UNWRAP_METHOD_SUFIX_MESSAGE_ID, SeamPreferences.UNWRAP_DOESNT_BELONG_TO_COMPONENT);
		validateMethodOfUnknownComponent(SeamComponentMethodType.OBSERVER, declaration, OBSERVER_METHOD_SUFIX_MESSAGE_ID, SeamPreferences.OBSERVER_DOESNT_BELONG_TO_COMPONENT);
	}

	private void validateMethodOfUnknownComponent(SeamComponentMethodType methodType, ISeamJavaComponentDeclaration declaration, String sufixMessageId, String preferenceKey) {
		Set<ISeamComponentMethod> methods = declaration.getMethodsByType(methodType);
		if(methods!=null && methods.size()>0) {
			for (ISeamComponentMethod method : methods) {
				IMethod javaMethod = (IMethod)method.getSourceMember();
				String methodName = javaMethod.getElementName();
				addError(sufixMessageId + NONCOMPONENTS_METHOD_SUFIX_MESSAGE_ID, preferenceKey, new String[]{methodName}, method, declaration.getResource());
				validationContext.addUnnamedCoreResource(declaration.getSourcePath());
			}
		} else {
			validationContext.removeUnnamedCoreResource(declaration.getSourcePath());
		}
	}

	private final static String[] extns = new String[]{"java", "xml"};

	private boolean checkFileExtension(IFile file) {
		String ext = file.getFileExtension();
		for (int i = 0; i < extns.length; i++) {
			if(extns[i].equalsIgnoreCase(ext)) {
				return true;
			}
		}
		return false;
	}
}