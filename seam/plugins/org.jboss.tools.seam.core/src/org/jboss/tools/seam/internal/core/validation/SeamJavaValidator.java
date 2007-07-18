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
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.wst.validation.internal.core.ValidationException;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.eclipse.wst.validation.internal.provisional.core.IValidationContext;
import org.jboss.tools.seam.core.BijectedAttributeType;
import org.jboss.tools.seam.core.IBijectedAttribute;
import org.jboss.tools.seam.core.IRole;
import org.jboss.tools.seam.core.ISeamAnnotatedFactory;
import org.jboss.tools.seam.core.ISeamComponent;
import org.jboss.tools.seam.core.ISeamComponentDeclaration;
import org.jboss.tools.seam.core.ISeamComponentMethod;
import org.jboss.tools.seam.core.ISeamContextVariable;
import org.jboss.tools.seam.core.ISeamElement;
import org.jboss.tools.seam.core.ISeamFactory;
import org.jboss.tools.seam.core.ISeamJavaComponentDeclaration;
import org.jboss.tools.seam.core.ISeamProject;
import org.jboss.tools.seam.core.ISeamTextSourceReference;
import org.jboss.tools.seam.core.ScopeType;
import org.jboss.tools.seam.core.SeamComponentMethodType;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.internal.core.AbstractContextVariable;
import org.jboss.tools.seam.internal.core.SeamComponentDeclaration;
import org.jboss.tools.seam.internal.core.SeamJavaComponentDeclaration;
import org.jboss.tools.seam.internal.core.SeamProject;
import org.jboss.tools.seam.internal.core.SeamTextSourceReference;

/**
 * Validator for Java files.
 * @author Alexey Kazakov
 */
public class SeamJavaValidator extends SeamValidator {

	private static final String MARKED_SEAM_RESOURCE_MESSAGE_GROUP = "markedSeamResource";

	private static final String NONUNIQUE_COMPONENT_NAME_MESSAGE_ID = "NONUNIQUE_COMPONENT_NAME_MESSAGE";
	private static final String UNKNOWN_INJECTION_NAME_MESSAGE_ID = "UNKNOWN_INJECTION_NAME";
	private static final String STATEFUL_COMPONENT_DOES_NOT_CONTENT_METHOD_SUFIX_MESSAGE_ID = "STATEFUL_COMPONENT_DOES_NOT_CONTENT_";
	private static final String DUPLICATE_METHOD_PREFIX_MESSAGE_ID = "DUPLICATE_";
	private static final String REMOVE_METHOD_SUFIX_MESSAGE_ID = "REMOVE";
	private static final String DESTROY_METHOD_SUFIX_MESSAGE_ID = "DESTROY";
	private static final String CREATE_METHOD_SUFIX_MESSAGE_ID = "CREATE";
	private static final String UNWRAP_METHOD_SUFIX_MESSAGE_ID = "UNWRAP";
	private static final String OBSERVER_METHOD_SUFIX_MESSAGE_ID = "OBSERVER";
	private static final String NONCOMPONENTS_METHOD_SUFIX_MESSAGE_ID = "_DOESNT_BELONG_TO_COMPONENT";
	private static final String STATEFUL_COMPONENT_WRONG_SCOPE_MESSAGE_ID = "STATEFUL_COMPONENT_WRONG_SCOPE";
	private static final String ENTITY_COMPONENT_WRONG_SCOPE_MESSAGE_ID = "ENTITY_COMPONENT_WRONG_SCOPE";
	private static final String UNKNOWN_FACTORY_NAME_MESSAGE_ID = "UNKNOWN_FACTORY_NAME";
	private static final String MULTIPLE_DATA_BINDER_MESSAGE_ID = "MULTIPLE_DATA_BINDER";
	private static final String DUPLICATE_VARIABLE_NAME_MESSAGE_ID = "DUPLICATE_VARIABLE_NAME";
	private static final String UNKNOWN_DATA_MODEL_MESSAGE_ID = "UNKNOWN_DATA_MODEL";

	private SeamValidationContext validationContext;
	private ISeamProject project;

	public ISchedulingRule getSchedulingRule(IValidationContext helper) {
		return null;
	}

	public IStatus validateInJob(IValidationContext helper, IReporter reporter)	throws ValidationException {
		super.validateInJob(helper, reporter);
		SeamJavaHelper seamJavaHelper = (SeamJavaHelper)helper;
		String[] uris = seamJavaHelper.getURIs();
		project = seamJavaHelper.getSeamProject();
		validationContext = ((SeamProject)project).getValidationContext();
		if (uris.length > 0) {
			IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
			IFile currentFile = null;
			Set<ISeamComponent> checkedComponents = new HashSet<ISeamComponent>();
			Set<String> markedDuplicateFactoryNames = new HashSet<String>();
			// Collect all resources which we must validate.
			Set<IPath> resources = new HashSet<IPath>(); // Resources which we have to validate.
			Set<IPath> newResources = new HashSet<IPath>(); // New (unlinked) resources file
			for (int i = 0; i < uris.length && !reporter.isCancelled(); i++) {
				currentFile = root.getFile(new Path(uris[i]));
				// Don't handle one resource twice.
				if (currentFile != null && currentFile.exists()) {
					// Get all variable names that were linked with this resource.
					Set<String> oldVariablesNamesOfChangedFile = validationContext.getVariableNamesByResource(currentFile.getFullPath());
					if(oldVariablesNamesOfChangedFile!=null) {
						// Check if variable name was changed in java file
						Set<String> newVariableNamesOfChangedFile = getVariablesNameByResource(currentFile.getFullPath());
						for (String newVariableName : newVariableNamesOfChangedFile) {
							if(!oldVariablesNamesOfChangedFile.contains(newVariableName)) {
								// Name was changed.
								// Collect resources with new component name.
								Set<IPath> linkedResources = validationContext.getResourcesByVariableName(newVariableName);
								if(linkedResources!=null) {
									resources.addAll(linkedResources);
								}
							}
						}
						resources.add(currentFile.getFullPath());

						// Collect all linked resources with old variable names.
						for (String name : oldVariablesNamesOfChangedFile) {
							Set<IPath> linkedResources = validationContext.getResourcesByVariableName(name);
							if(linkedResources!=null) {
								resources.addAll(linkedResources);
							}
						}
					} else {
						// Validate new (unlinked) Java file.
						resources.add(currentFile.getFullPath());
					}
					newResources.add(currentFile.getFullPath());
				}
			}
			// Validate all collected linked resources.
			// Remove all links between collected resources and variables names because they will be linked again during validation.
			validationContext.removeLinkedResources(resources);
			for (IPath linkedResource : resources) {
				// Remove markers from collected java file
				IFile sourceFile = root.getFile(linkedResource);
				reporter.removeMessageSubset(this, sourceFile, MARKED_SEAM_RESOURCE_MESSAGE_GROUP);
				validateComponent(linkedResource, checkedComponents);
				validateFactory(linkedResource, markedDuplicateFactoryNames);
				// TODO
			}

			// Validate all unnamed resources.
			Set<IPath> unnamedResources = validationContext.getUnnamedResources();
			newResources.addAll(unnamedResources);
			for (IPath path : newResources) {
				Set<SeamJavaComponentDeclaration> declarations = ((SeamProject)project).findJavaDeclarations(path);
				for (SeamJavaComponentDeclaration d : declarations) {
					validateMethodsOfUnknownComponent(d);
				}
			}
		} else {
			return validateAll();
		}

		return OK_STATUS;
	}

	private void validateFactory(IPath sourceFilePath, Set<String> markedDuplicateFactoryNames) {
		Set<ISeamFactory> factories = project.getFactoriesByPath(sourceFilePath);
		for (ISeamFactory factory : factories) {
			if(factory instanceof ISeamAnnotatedFactory) {
				validateFactory((ISeamAnnotatedFactory)factory, markedDuplicateFactoryNames);
			}
		}
	}

	private void validateFactory(ISeamAnnotatedFactory factory, Set<String> markedDuplicateFactoryNames) {
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
							// TODO
							// Unknown factory name
							factoryName = methodName;
						}
					}
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
										validationContext.addLinkedResource(factoryName, factory.getSourcePath());
										location = getLocationOfName(factory);
										this.addError(DUPLICATE_VARIABLE_NAME_MESSAGE_ID, new String[]{factoryName}, location, factory.getResource(), MARKED_SEAM_RESOURCE_MESSAGE_GROUP);
									}
									// mark duplicate variable
									IResource resource = getComponentResourceWithName(variable);
									validationContext.addLinkedResource(factoryName, resource.getFullPath());
									location = getLocationOfName(variable);
									this.addError(DUPLICATE_VARIABLE_NAME_MESSAGE_ID, new String[]{factoryName}, location, resource, MARKED_SEAM_RESOURCE_MESSAGE_GROUP);

									markedDuplicateFactoryNames.add(factoryName);
								}
							} else {
								// We have that variable name
								unknownVariable = false;
							}
						}
					}
					if(unknownVariable) {
						// mark unknown factory name
						// save link to factory resource
						validationContext.addLinkedResource(factoryName, factory.getSourcePath());
						this.addError(UNKNOWN_FACTORY_NAME_MESSAGE_ID, new String[]{factoryName}, getLocationOfName(factory), factory.getResource(), MARKED_SEAM_RESOURCE_MESSAGE_GROUP);
					}
				}
			} catch (JavaModelException e) {
				SeamCorePlugin.getDefault().logError(e);
			}
		} else {
			// factory must be java method!
			// JDT should mark it.
		}
	}

	private static IResource getComponentResourceWithName(ISeamElement element) {
		if(element instanceof ISeamComponent) {
			Set declarations = ((ISeamComponent)element).getAllDeclarations();
			for (Object o : declarations) {
				SeamComponentDeclaration d = (SeamComponentDeclaration)o;
				if(d.getLocationFor(SeamComponentDeclaration.PATH_OF_NAME)!=null) {
					return d.getResource();
				}
			}
		}
		return element.getResource();
	}

	private static ISeamTextSourceReference getLocationOfName(ISeamElement element) {
		ISeamTextSourceReference location = null;
		if(element instanceof AbstractContextVariable) {
			location = ((AbstractContextVariable)element).getLocationFor("name");
		} else if(element instanceof ISeamComponent) {
			Set declarations = ((ISeamComponent)element).getAllDeclarations();
			for (Object d : declarations) {
				location = ((SeamComponentDeclaration)d).getLocationFor(SeamComponentDeclaration.PATH_OF_NAME);
				if(location!=null) {
					break;
				}
			}
		}
		if(location==null && element instanceof ISeamTextSourceReference) {
			location = (ISeamTextSourceReference)element;
		}
		return location;
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
			if(!result.contains(name)) {
				result.add(name);
			}
		}
		return result;
	}

	public void cleanup(IReporter reporter) {
		super.cleanup(reporter);
	}

	private IStatus validateAll() {
		reporter.removeAllMessages(this);
		validationContext.clear();
		Set<ISeamComponent> components = project.getComponents();
		for (ISeamComponent component : components) {
			validateComponent(component);
		}
		Set<ISeamFactory> factories = project.getFactories();
		Set<String> markedDuplicateFactoryNames = new HashSet<String>();
		for (ISeamFactory factory : factories) {
			if(factory instanceof ISeamAnnotatedFactory) {
				validateFactory((ISeamAnnotatedFactory)factory, markedDuplicateFactoryNames);
			}
		}

		Map<String,SeamJavaComponentDeclaration> declarations = ((SeamProject)project).getAllJavaComponentDeclarations();
		Collection<SeamJavaComponentDeclaration> values = declarations.values();
		for (SeamJavaComponentDeclaration d : values) {
			validateMethodsOfUnknownComponent(d);
		}

		// TODO
		return OK_STATUS;
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
						validationContext.addLinkedResource(declaration.getName(), declaration.getSourcePath());
						// Validate all elements in declaration but @Name. 
						validateJavaDeclaration(firstJavaDeclaration);
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
									addError(NONUNIQUE_COMPONENT_NAME_MESSAGE_ID, new String[]{component.getName()}, location, checkedDeclarationResource, MARKED_SEAM_RESOURCE_MESSAGE_GROUP);
								}
								markedDeclarations.add(checkedDeclaration);
							}
							// Mark next wrong declaration with that name
							markedDeclarations.add(javaDeclaration);
							ISeamTextSourceReference location = ((SeamComponentDeclaration)javaDeclaration).getLocationFor(SeamComponentDeclaration.PATH_OF_NAME);
							if(location!=null) {
								addError(NONUNIQUE_COMPONENT_NAME_MESSAGE_ID, new String[]{component.getName()}, location, javaDeclarationResource, MARKED_SEAM_RESOURCE_MESSAGE_GROUP);
							}
						}
					}
				}
			}
			boolean source = !((IType)firstJavaDeclaration.getSourceMember()).isBinary();
			if(source) {
				validateStatefulComponent(component);
				validateDuplicateComponentMethods(component);
				validateEntityComponent(component);
			}
		}
	}

	private void validateEntityComponent(ISeamComponent component) {
		if(component.isEntity()) {
			ISeamJavaComponentDeclaration javaDeclaration = component.getJavaDeclaration();
			ScopeType scope = component.getScope();
			if(scope == ScopeType.STATELESS) {
				ISeamTextSourceReference location = getScopeLocation(component);
				addError(ENTITY_COMPONENT_WRONG_SCOPE_MESSAGE_ID, new String[]{component.getName()}, location, javaDeclaration.getResource(), MARKED_SEAM_RESOURCE_MESSAGE_GROUP);
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
			SeamCorePlugin.getDefault().logError(e);
		}
		return new SeamTextSourceReference(length, offset);
	}

	private void validateStatefulComponent(ISeamComponent component) {
		if(component.isStateful()) {
			ISeamJavaComponentDeclaration javaDeclaration = component.getJavaDeclaration();
			validateStatefulComponentMethods(SeamComponentMethodType.DESTROY, component, DESTROY_METHOD_SUFIX_MESSAGE_ID);
			validateStatefulComponentMethods(SeamComponentMethodType.REMOVE, component, REMOVE_METHOD_SUFIX_MESSAGE_ID);
			ScopeType scope = component.getScope();
			if(scope == ScopeType.PAGE || scope == ScopeType.STATELESS) {
				ISeamTextSourceReference location = getScopeLocation(component);
				addError(STATEFUL_COMPONENT_WRONG_SCOPE_MESSAGE_ID, new String[]{component.getName()}, location, javaDeclaration.getResource(), MARKED_SEAM_RESOURCE_MESSAGE_GROUP);
			}
		}
	}

	private void validateStatefulComponentMethods(SeamComponentMethodType methodType, ISeamComponent component, String postfixMessageId) {
		ISeamJavaComponentDeclaration javaDeclaration = component.getJavaDeclaration();
		ISeamTextSourceReference classNameLocation = getClassNameLocation(javaDeclaration);
		Set<ISeamComponentMethod> methods = javaDeclaration.getMethodsByType(methodType);
		if(methods==null || methods.size()==0) {
			addError(STATEFUL_COMPONENT_DOES_NOT_CONTENT_METHOD_SUFIX_MESSAGE_ID + postfixMessageId, new String[]{component.getName()}, classNameLocation, javaDeclaration.getResource(), MARKED_SEAM_RESOURCE_MESSAGE_GROUP);
		}
	}

	private void validateDuplicateComponentMethods(ISeamComponent component) {
		validateDuplicateComponentMethod(SeamComponentMethodType.DESTROY, component, DESTROY_METHOD_SUFIX_MESSAGE_ID);
		validateDuplicateComponentMethod(SeamComponentMethodType.REMOVE, component, REMOVE_METHOD_SUFIX_MESSAGE_ID);
		validateDuplicateComponentMethod(SeamComponentMethodType.CREATE, component, CREATE_METHOD_SUFIX_MESSAGE_ID);
		validateDuplicateComponentMethod(SeamComponentMethodType.UNWRAP, component, UNWRAP_METHOD_SUFIX_MESSAGE_ID);
	}

	private void validateDuplicateComponentMethod(SeamComponentMethodType methodType, ISeamComponent component, String postfixMessageId) {
		ISeamJavaComponentDeclaration javaDeclaration = component.getJavaDeclaration();
		Set<ISeamComponentMethod> methods = javaDeclaration.getMethodsByType(methodType);
		if(methods!=null && methods.size()>1) {
			for (ISeamComponentMethod method : methods) {
				IMethod javaMethod = (IMethod)method.getSourceMember();
				String methodName = javaMethod.getElementName();
				addError(DUPLICATE_METHOD_PREFIX_MESSAGE_ID + postfixMessageId, new String[]{methodName}, method, javaDeclaration.getResource(), MARKED_SEAM_RESOURCE_MESSAGE_GROUP);
			}
		}
	}

	private void validateJavaDeclaration(ISeamJavaComponentDeclaration declaration) {
		validateBijections(declaration);
		// TODO
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
		if(name==null || name.startsWith("#{")) {
			return;
		}
		// save link between java source and variable name
		validationContext.addLinkedResource(name, declaration.getSourcePath());

		// Validate @In
		if(bijection.isOfType(BijectedAttributeType.IN)) {
			Set<ISeamContextVariable> variables = project.getVariablesByName(name);
			if(variables==null || variables.size()<1) {
				// Injection has unknown name. Mark it.
				// TODO check preferences to mark it as Error or Warning or ignore it.
				IResource declarationResource = declaration.getResource();
				addError(UNKNOWN_INJECTION_NAME_MESSAGE_ID, new String[]{name}, bijection, declarationResource, MARKED_SEAM_RESOURCE_MESSAGE_GROUP);
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
					addError(MULTIPLE_DATA_BINDER_MESSAGE_ID, dataBinder, declaration.getResource(), MARKED_SEAM_RESOURCE_MESSAGE_GROUP);
				}
			}
		} else {
			// save link between java source and variable name
			validationContext.addLinkedResource(name, declaration.getSourcePath());
			Set<IBijectedAttribute> dataBinders = declaration.getBijectedAttributesByName(name);
			for (IBijectedAttribute dataBinder : dataBinders) {
				if(dataBinder.isOfType(BijectedAttributeType.DATA_BINDER) || dataBinder.isOfType(BijectedAttributeType.OUT)) {
					return;
				}
			}
			addError(UNKNOWN_DATA_MODEL_MESSAGE_ID, new String[]{name}, getLocationOfName(bijection), declaration.getResource(), MARKED_SEAM_RESOURCE_MESSAGE_GROUP);
		}
	}

	/*
	 *  Validates methods of java classes. They must belong components.
	 */
	private void validateMethodsOfUnknownComponent(ISeamJavaComponentDeclaration declaration) {
		if(project.getComponentsByPath(declaration.getSourcePath()).size()>0) {
			validationContext.removeUnnamedResource(declaration.getSourcePath());
			return;
		}
		validateMethodOfUnknownComponent(SeamComponentMethodType.DESTROY, declaration, DESTROY_METHOD_SUFIX_MESSAGE_ID);
		validateMethodOfUnknownComponent(SeamComponentMethodType.CREATE, declaration, CREATE_METHOD_SUFIX_MESSAGE_ID);
		validateMethodOfUnknownComponent(SeamComponentMethodType.UNWRAP, declaration, UNWRAP_METHOD_SUFIX_MESSAGE_ID);
		validateMethodOfUnknownComponent(SeamComponentMethodType.OBSERVER, declaration, OBSERVER_METHOD_SUFIX_MESSAGE_ID);
	}

	private void validateMethodOfUnknownComponent(SeamComponentMethodType methodType, ISeamJavaComponentDeclaration declaration, String sufixMessageId) {
		Set<ISeamComponentMethod> methods = declaration.getMethodsByType(methodType);
		if(methods!=null && methods.size()>0) {
			for (ISeamComponentMethod method : methods) {
				IMethod javaMethod = (IMethod)method.getSourceMember();
				String methodName = javaMethod.getElementName();
				addError(sufixMessageId + NONCOMPONENTS_METHOD_SUFIX_MESSAGE_ID, new String[]{methodName}, method, declaration.getResource(), MARKED_SEAM_RESOURCE_MESSAGE_GROUP);
				validationContext.addUnnamedResource(declaration.getSourcePath());
			}
		} else {
			validationContext.removeUnnamedResource(declaration.getSourcePath());
		}
	}
}