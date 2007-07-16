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

import java.util.HashMap;
import java.util.HashSet;
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
import org.jboss.tools.seam.core.ISeamAnnotatedFactory;
import org.jboss.tools.seam.core.ISeamComponent;
import org.jboss.tools.seam.core.ISeamComponentDeclaration;
import org.jboss.tools.seam.core.ISeamComponentMethod;
import org.jboss.tools.seam.core.ISeamContextVariable;
import org.jboss.tools.seam.core.ISeamFactory;
import org.jboss.tools.seam.core.ISeamJavaComponentDeclaration;
import org.jboss.tools.seam.core.ISeamProject;
import org.jboss.tools.seam.core.ISeamTextSourceReference;
import org.jboss.tools.seam.core.ScopeType;
import org.jboss.tools.seam.core.SeamComponentMethodType;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.internal.core.SeamComponentDeclaration;
import org.jboss.tools.seam.internal.core.SeamProject;
import org.jboss.tools.seam.internal.core.SeamTextSourceReference;

/**
 * Validator for Java files.
 * @author Alexey Kazakov
 */
public class SeamJavaValidator extends SeamValidator {

	private static final String MARKED_COMPONENT_MESSAGE_GROUP = "markedComponent";

	private static final String NONUNIQUE_COMPONENT_NAME_MESSAGE_ID = "NONUNIQUE_COMPONENT_NAME_MESSAGE";
	private static final String UNKNOWN_INJECTION_NAME_MESSAGE_ID = "UNKNOWN_INJECTION_NAME";
	private static final String STATEFUL_COMPONENT_DOES_NOT_CONTENT_METHOD_SUFIX_MESSAGE_ID = "STATEFUL_COMPONENT_DOES_NOT_CONTENT_";
	private static final String DUPLICATE_METHOD_SUFIX_MESSAGE_ID = "DUPLICATE_";
	private static final String REMOVE_METHOD_POSTFIX_MESSAGE_ID = "REMOVE";
	private static final String DESTROY_METHOD_POSTFIX_MESSAGE_ID = "DESTROY";
	private static final String CREATE_METHOD_POSTFIX_MESSAGE_ID = "CREATE";
	private static final String STATEFUL_COMPONENT_WRONG_SCOPE_MESSAGE_ID = "STATEFUL_COMPONENT_WRONG_SCOPE";
	private static final String ENTITY_COMPONENT_WRONG_SCOPE_MESSAGE_ID = "ENTITY_COMPONENT_WRONG_SCOPE";

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
			// Collect all resources which we must validate.
			Set<IPath> resources = new HashSet<IPath>(); // Resources which we have to validate.
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
				}
			}
			// Validate all collected linked resources.
			// Remove all links between collected resources and variables names because they will be linked again during validation.
			validationContext.removeLinkedResources(resources);
			for (IPath linkedResource : resources) {
				// Remove markers from collected java file
				IFile sourceFile = root.getFile(linkedResource);
				reporter.removeMessageSubset(this, sourceFile, MARKED_COMPONENT_MESSAGE_GROUP);
				validateComponent(linkedResource, checkedComponents);
				validateFactory(linkedResource);
				// TODO
			}
		} else {
			return validateAll();
		}

		return OK_STATUS;
	}

	private void validateFactory(IPath sourceFilePath) {
		Set<ISeamFactory> factories = project.getFactoriesByPath(sourceFilePath);
		for (ISeamFactory factory : factories) {
			if(factory instanceof ISeamAnnotatedFactory) {
				validateFactory((ISeamAnnotatedFactory)factory);
			}
		}
	}

	private void validateFactory(ISeamAnnotatedFactory factory) {
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
					for (ISeamContextVariable variable : variables) {
						if((factoryScope == variable.getScope() || factoryScope.getPriority()>variable.getScope().getPriority()) && !(variable instanceof ISeamFactory)) {
							// It's OK. We have that variable name
							unknownVariable = false;
							break;
						}
						if(unknownVariable) {
							// TODO
							// mark unknown factory name
						}
					}
				}
			} catch (Exception e) {
				SeamCorePlugin.getDefault().logError(e);
			}
		} else {
			// factory must be java method!
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
		for (ISeamFactory factory : factories) {
			if(factory instanceof ISeamAnnotatedFactory) {
				validateFactory((ISeamAnnotatedFactory)factory);
			}
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
									addError(NONUNIQUE_COMPONENT_NAME_MESSAGE_ID, new String[]{component.getName()}, location, checkedDeclarationResource, MARKED_COMPONENT_MESSAGE_GROUP);
								}
								markedDeclarations.add(checkedDeclaration);
							}
							// Mark next wrong declaration with that name
							markedDeclarations.add(javaDeclaration);
							ISeamTextSourceReference location = ((SeamComponentDeclaration)javaDeclaration).getLocationFor(SeamComponentDeclaration.PATH_OF_NAME);
							if(location!=null) {
								addError(NONUNIQUE_COMPONENT_NAME_MESSAGE_ID, new String[]{component.getName()}, location, javaDeclarationResource, MARKED_COMPONENT_MESSAGE_GROUP);
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
				addError(ENTITY_COMPONENT_WRONG_SCOPE_MESSAGE_ID, new String[]{component.getName()}, location, javaDeclaration.getResource(), MARKED_COMPONENT_MESSAGE_GROUP);
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
		} catch (Exception e) {
			SeamCorePlugin.getDefault().logError(e);
		}
		return new SeamTextSourceReference(length, offset);
	}

	private void validateStatefulComponent(ISeamComponent component) {
		if(component.isStateful()) {
			ISeamJavaComponentDeclaration javaDeclaration = component.getJavaDeclaration();
			validateStatefulComponentMethods(SeamComponentMethodType.DESTROY, component, DESTROY_METHOD_POSTFIX_MESSAGE_ID);
			validateStatefulComponentMethods(SeamComponentMethodType.REMOVE, component, REMOVE_METHOD_POSTFIX_MESSAGE_ID);
			ScopeType scope = component.getScope();
			if(scope == ScopeType.PAGE || scope == ScopeType.STATELESS) {
				ISeamTextSourceReference location = getScopeLocation(component);
				addError(STATEFUL_COMPONENT_WRONG_SCOPE_MESSAGE_ID, new String[]{component.getName()}, location, javaDeclaration.getResource(), MARKED_COMPONENT_MESSAGE_GROUP);
			}
		}
	}

	private void validateStatefulComponentMethods(SeamComponentMethodType methodType, ISeamComponent component, String postfixMessageId) {
		ISeamJavaComponentDeclaration javaDeclaration = component.getJavaDeclaration();
		ISeamTextSourceReference classNameLocation = getClassNameLocation(javaDeclaration);
		Set<ISeamComponentMethod> methods = javaDeclaration.getMethodsByType(methodType);
		if(methods==null || methods.size()==0) {
			addError(STATEFUL_COMPONENT_DOES_NOT_CONTENT_METHOD_SUFIX_MESSAGE_ID + postfixMessageId, new String[]{component.getName()}, classNameLocation, javaDeclaration.getResource(), MARKED_COMPONENT_MESSAGE_GROUP);
		}
	}

	private void validateDuplicateComponentMethods(ISeamComponent component) {
		validateDuplicateComponentMethod(SeamComponentMethodType.DESTROY, component, DESTROY_METHOD_POSTFIX_MESSAGE_ID);
		validateDuplicateComponentMethod(SeamComponentMethodType.REMOVE, component, REMOVE_METHOD_POSTFIX_MESSAGE_ID);
		validateDuplicateComponentMethod(SeamComponentMethodType.CREATE, component, CREATE_METHOD_POSTFIX_MESSAGE_ID);
	}

	private void validateDuplicateComponentMethod(SeamComponentMethodType methodType, ISeamComponent component, String postfixMessageId) {
		ISeamJavaComponentDeclaration javaDeclaration = component.getJavaDeclaration();
		Set<ISeamComponentMethod> methods = javaDeclaration.getMethodsByType(methodType);
		if(methods!=null && methods.size()>1) {
			for (ISeamComponentMethod method : methods) {
				try {
					IMethod javaMethod = (IMethod)method.getSourceMember();
					String methodName = javaMethod.getElementName();
					addError(DUPLICATE_METHOD_SUFIX_MESSAGE_ID + postfixMessageId, new String[]{methodName}, method, javaDeclaration.getResource(), MARKED_COMPONENT_MESSAGE_GROUP);
				} catch (Exception e) {
					SeamCorePlugin.getDefault().logError(e);
				}
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
			String name = bijection.getName();
			if(name.startsWith("#{")) {
				// TODO Validate EL
			} else {
				// save link between java source and variable name
				validationContext.addLinkedResource(name, declaration.getSourcePath());

				if(bijection.isOfType(BijectedAttributeType.IN)) {
					// Validate injection
					Set<ISeamContextVariable> variables = project.getVariablesByName(name);
					if(variables==null || variables.size()<1) {
						// Injection has unknown name. Mark it.
						// TODO check preferences to mark it as Error or Warning or ignore it.
						IResource declarationResource = declaration.getResource();
						addError(UNKNOWN_INJECTION_NAME_MESSAGE_ID, new String[]{name}, bijection, declarationResource, MARKED_COMPONENT_MESSAGE_GROUP);
					}
				}
			}
		}
	}
}