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
import java.util.Iterator;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.wst.validation.internal.core.ValidationException;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.eclipse.wst.validation.internal.provisional.core.IValidationContext;
import org.jboss.tools.seam.core.BijectedAttributeType;
import org.jboss.tools.seam.core.IBijectedAttribute;
import org.jboss.tools.seam.core.ISeamComponent;
import org.jboss.tools.seam.core.ISeamComponentDeclaration;
import org.jboss.tools.seam.core.ISeamContextVariable;
import org.jboss.tools.seam.core.ISeamJavaComponentDeclaration;
import org.jboss.tools.seam.core.ISeamProject;
import org.jboss.tools.seam.core.ISeamTextSourceReference;
import org.jboss.tools.seam.internal.core.SeamComponentDeclaration;
import org.jboss.tools.seam.internal.core.SeamProject;

/**
 * Validator for Java files.
 * @author Alexey Kazakov
 */
public class SeamJavaValidator extends SeamValidator {

	private static final String MARKED_COMPONENT_MESSAGE_GROUP = "markedComponent";

	public static final String NONUNIQUE_COMPONENT_NAME_MESSAGE_ID = "NONUNIQUE_COMPONENT_NAME_MESSAGE";

	public static final String UNKNOWN_INJECTION_NAME_MESSAGE_ID = "UNKNOWN_INJECTION_NAME";

	private SeamValidationContext validationContext;

	public ISchedulingRule getSchedulingRule(IValidationContext helper) {
		return null;
	}

	public IStatus validateInJob(IValidationContext helper, IReporter reporter)	throws ValidationException {
		super.validateInJob(helper, reporter);
		SeamJavaHelper seamJavaHelper = (SeamJavaHelper)helper;
		String[] uris = seamJavaHelper.getURIs();
		ISeamProject project = seamJavaHelper.getSeamProject();
		validationContext = ((SeamProject)project).getValidationContext();
		if (uris.length > 0) {
			IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
			IFile currentFile = null;
			Set<IPath> checkedResources = new HashSet<IPath>();
			Set<ISeamComponent> checkedComponents = new HashSet<ISeamComponent>();
			for (int i = 0; i < uris.length && !reporter.isCancelled(); i++) {
				currentFile = root.getFile(new Path(uris[i]));
				// Don't validate one resource twice.
				if(checkedResources.contains(currentFile)) {
					continue;
				}
				if (currentFile != null && currentFile.exists()) {
					// Get all variable names that were linked with this resource.
					Set<String> oldVariablesNamesOfChangedFile = validationContext.getVariableNamesByResource(currentFile.getFullPath());
					if(oldVariablesNamesOfChangedFile!=null) {
						Set<IPath> resources = new HashSet<IPath>(); // Resources which we have to validate.

						// Check if variable name was changed in java file
						Set<String> newVariableNamesOfChangedFile = getVariablesNameByResource(currentFile.getFullPath(), project);
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
						// Check if changed file is not linked to any variables anymore.
						if(newVariableNamesOfChangedFile.size() == 0) {
							resources.add(currentFile.getFullPath());
						}

						// Collect all linked resources with old variable names.
						for (String name : oldVariablesNamesOfChangedFile) {
							Set<IPath> linkedResources = validationContext.getResourcesByVariableName(name);
							if(linkedResources!=null) {
								resources.addAll(linkedResources);
							}
						}

						// Validate all collected linked resources.
						// Remove all links between resources and variables names because they will be linked again during validation.
						validationContext.clear();
						for (IPath linkedResource : resources) {
							// Don't validate one resource twice.
							if(checkedResources.contains(linkedResource)) {
								continue;
							}
							// Remove markers from collected java file
							reporter.removeMessageSubset(this, linkedResource, MARKED_COMPONENT_MESSAGE_GROUP);
							validateComponent(project, linkedResource, checkedComponents, helper, reporter);
							checkedResources.add(linkedResource);
						}
					} else {
						// Validate new (unmarked) Java file.
						validateComponent(project, currentFile.getFullPath(), checkedComponents, helper, reporter);
					}
					// TODO
				}
			}
		} else {
			return validateAll(project, helper, reporter);
		}

		return OK_STATUS;
	}

	private void validateComponent(ISeamProject project, IPath sourceFilePath, Set<ISeamComponent> checkedComponents, IValidationContext helper, IReporter reporter) {
		Set<ISeamComponent> components = project.getComponentsByPath(sourceFilePath);
		for (ISeamComponent component : components) {
			// Don't validate one component twice.
			if(!checkedComponents.contains(component)) {
				validateComponent(project, component, helper, reporter);
				checkedComponents.add(component);
			}
		}
	}

	/*
	 * Returns set of variables which are linked with this resource
	 */
	private Set<String> getVariablesNameByResource(IPath resourcePath, ISeamProject project) {
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

	private IStatus validateAll(ISeamProject project, IValidationContext helper, IReporter reporter) {
		reporter.removeAllMessages(this);
		validationContext.clear();
		Set<ISeamComponent> components = project.getComponents();
		for (ISeamComponent component : components) {
			validateComponent(project, component, helper, reporter);
			// TODO
		}
		return OK_STATUS;
	}

	/*
	 * Validates the component 
	 */
	private void validateComponent(ISeamProject project, ISeamComponent component, IValidationContext helper, IReporter reporter) {
		ISeamJavaComponentDeclaration firstJavaDeclaration = component.getJavaDeclaration();
		if(firstJavaDeclaration!=null) {
			HashMap<Integer, ISeamJavaComponentDeclaration> usedPrecedences = new HashMap<Integer, ISeamJavaComponentDeclaration>();
			Set<ISeamJavaComponentDeclaration> markedDeclarations = new HashSet<ISeamJavaComponentDeclaration>();
			int firstJavaDeclarationPrecedence = firstJavaDeclaration.getPrecedence();
			usedPrecedences.put(firstJavaDeclarationPrecedence, firstJavaDeclaration);
			Set<ISeamComponentDeclaration> declarations = component.getAllDeclarations();
			for (ISeamComponentDeclaration declaration : declarations) {
				if(declaration instanceof ISeamJavaComponentDeclaration && declaration.getResource() instanceof IFile) {
					// Save link between component name and java source file.
					validationContext.addLinkedResource(declaration.getName(), declaration.getSourcePath());
					// Validate all elements in declaration but @Name. 
					validateInjections(project, firstJavaDeclaration, helper, reporter);
				}
				if(declaration instanceof ISeamJavaComponentDeclaration && declaration!=firstJavaDeclaration) {
					// Validate @Name
					// Component class with the same component name. Check precedence.
					ISeamJavaComponentDeclaration javaDeclaration = (ISeamJavaComponentDeclaration)declaration;
					int javaDeclarationPrecedence = javaDeclaration.getPrecedence();
					ISeamJavaComponentDeclaration checkedDeclaration = usedPrecedences.get(javaDeclarationPrecedence);
					if(checkedDeclaration==null) {
						usedPrecedences.put(javaDeclarationPrecedence, javaDeclaration);
					} else {
						IResource javaDeclarationResource = javaDeclaration.getResource();
						// Mark nonunique name.
						if(!markedDeclarations.contains(checkedDeclaration)) {
							// Mark first wrong declaration with that name
							IResource checkedDeclarationResource = checkedDeclaration.getResource();
							ISeamTextSourceReference location = ((SeamComponentDeclaration)checkedDeclaration).getLocationFor(SeamComponentDeclaration.PATH_OF_NAME);
							if(location!=null) {
								addError(NONUNIQUE_COMPONENT_NAME_MESSAGE_ID, location, checkedDeclarationResource, MARKED_COMPONENT_MESSAGE_GROUP);
							}
							markedDeclarations.add(checkedDeclaration);
						}
						// Mark next wrong declaration with that name
						markedDeclarations.add(javaDeclaration);
						ISeamTextSourceReference location = ((SeamComponentDeclaration)javaDeclaration).getLocationFor(SeamComponentDeclaration.PATH_OF_NAME);
						if(location!=null) {
							addError(NONUNIQUE_COMPONENT_NAME_MESSAGE_ID, location, javaDeclarationResource, MARKED_COMPONENT_MESSAGE_GROUP);
						}
					}
				}
			}
		}
	}

	private void validateJavaDeclaration(ISeamProject project, ISeamJavaComponentDeclaration declaration, IValidationContext helper, IReporter reporter) {
		validateInjections(project, declaration, helper, reporter);
	}

	private void validateInjections(ISeamProject project, ISeamJavaComponentDeclaration declaration, IValidationContext helper, IReporter reporter) {
		Set<IBijectedAttribute> injections = declaration.getBijectedAttributesByType(BijectedAttributeType.IN);
		for (IBijectedAttribute injection : injections) {
			String name = injection.getName();
			if(name.startsWith("#{")) {
				// TODO Validate EL
			} else {
				Set<ISeamContextVariable> variables = project.getVariablesByName(name);
				for (ISeamContextVariable variable : variables) {
					// save link between java source and variable name
					validationContext.addLinkedResource(name, declaration.getSourcePath());
				}
				if(variables.size()<1) {
					// Injection has unknown name. Mark it.
					// TODO check preferences to mark it as Error or Warning or ignore it.
					IResource declarationResource = declaration.getResource();
					addError(NONUNIQUE_COMPONENT_NAME_MESSAGE_ID, injection, declarationResource, MARKED_COMPONENT_MESSAGE_GROUP);
				}
			}
		}
	}
}