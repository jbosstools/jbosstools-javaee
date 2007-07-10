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
import org.eclipse.wst.validation.internal.core.ValidationException;
import org.eclipse.wst.validation.internal.provisional.core.IReporter;
import org.eclipse.wst.validation.internal.provisional.core.IValidationContext;
import org.jboss.tools.seam.core.ISeamComponent;
import org.jboss.tools.seam.core.ISeamComponentDeclaration;
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

	private static final String NONUNIQUE_NAME_MESSAGE_GROUP = "nonuniqueName";

	public static final String NONUNIQUE_COMPONENT_NAME_MESSAGE_ID = "NONUNIQUE_COMPONENT_NAME_MESSAGE";

	private SeamValidationContext validationContext;

	public ISchedulingRule getSchedulingRule(IValidationContext helper) {
		// TODO
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
					String oldComponentNameOfChangedFile = validationContext.getNonuniqueNameOfComponent(currentFile.getLocation());
					if(oldComponentNameOfChangedFile!=null) {
						Set<IPath> resources = new HashSet<IPath>(); // Resources which we have to validate.

						// Check if component name was changed in java file
						String newComponentNameOfChangedFile = getComponentNameByResource(currentFile.getLocation(), project);
						if(newComponentNameOfChangedFile!=null && !oldComponentNameOfChangedFile.equals(newComponentNameOfChangedFile)) {
							// Name was changed.
							// Collect resources with new component name.
							Set<IPath> linkedResources = validationContext.getMarkedNonuniqueNamedResources(newComponentNameOfChangedFile);
							if(linkedResources!=null) {
								resources.addAll(linkedResources);
							}
						}

						// Collect resources with old component name.
						Set<IPath> linkedResources = validationContext.getMarkedNonuniqueNamedResources(oldComponentNameOfChangedFile);
						if(linkedResources!=null) {
							resources.addAll(linkedResources);
						}

						// Validate all collected linked resources.
						for (IPath linkedResource : resources) {
							// Don't validate one resource twice.
							if(checkedResources.contains(linkedResource)) {
								continue;
							}
							// Remove markers from collected java file
							reporter.removeMessageSubset(this, linkedResource, NONUNIQUE_NAME_MESSAGE_GROUP);
							validateUniqueComponentName(project, linkedResource, checkedComponents, helper, reporter);
							checkedResources.add(linkedResource);
						}
					} else {
						// Validate new (unmarked) Java file.
						validateUniqueComponentName(project, currentFile.getLocation(), checkedComponents, helper, reporter);
					}
					// TODO
				}
			}
		} else {
			return validateAll(project, helper, reporter);
		}

		return OK_STATUS;
	}

	private void validateUniqueComponentName(ISeamProject project, IPath sourceFilePath, Set<ISeamComponent> checkedComponents, IValidationContext helper, IReporter reporter) {
		Set<ISeamComponent> components = project.getComponentsByPath(sourceFilePath);
		for (ISeamComponent component : components) {
			// Don't validate one component twice.
			if(!checkedComponents.contains(component)) {
				validateUniqueComponentName(project, component, helper, reporter);
				checkedComponents.add(component);
			}
		}
	}

	public String getComponentNameByResource(IPath resourcePath, ISeamProject project) {
		Set<ISeamComponent> components = project.getComponentsByPath(resourcePath);
		for (ISeamComponent component : components) {
			return component.getName();
		}
		return null;
	}

	public void cleanup(IReporter reporter) {
	}

	private IStatus validateAll(ISeamProject project, IValidationContext helper, IReporter reporter) {
		Set<ISeamComponent> components = project.getComponents();
		for (ISeamComponent component : components) {
			validateUniqueComponentName(project, component, helper, reporter);
			// TODO
		}
		return OK_STATUS;
	}

	/*
	 * Validates that component has unique name 
	 */
	private void validateUniqueComponentName(ISeamProject project, ISeamComponent component, IValidationContext helper, IReporter reporter) {
		ISeamJavaComponentDeclaration firstJavaDeclaration = component.getJavaDeclaration();
		if(firstJavaDeclaration!=null) {
			HashMap<Integer, ISeamJavaComponentDeclaration> usedPrecedences = new HashMap<Integer, ISeamJavaComponentDeclaration>();
			Set<ISeamJavaComponentDeclaration> markedDeclarations = new HashSet<ISeamJavaComponentDeclaration>();
			int firstJavaDeclarationPrecedence = firstJavaDeclaration.getPrecedence();
			usedPrecedences.put(firstJavaDeclarationPrecedence, firstJavaDeclaration);
			Set<ISeamComponentDeclaration> declarations = component.getAllDeclarations();
			for (ISeamComponentDeclaration declaration : declarations) {
				if(declaration instanceof ISeamJavaComponentDeclaration && declaration!=firstJavaDeclaration) {
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
							addError(NONUNIQUE_COMPONENT_NAME_MESSAGE_ID, location, checkedDeclarationResource, NONUNIQUE_NAME_MESSAGE_GROUP);
							markedDeclarations.add(checkedDeclaration);
							validationContext.addLinkedResource(checkedDeclaration.getName(), checkedDeclarationResource.getLocation());
						}
						// Mark next wrong declaration with that name
						markedDeclarations.add(javaDeclaration);
						validationContext.addLinkedResource(javaDeclaration.getName(), javaDeclaration.getResource().getLocation());
						ISeamTextSourceReference location = ((SeamComponentDeclaration)javaDeclaration).getLocationFor(SeamComponentDeclaration.PATH_OF_NAME);
						addError(NONUNIQUE_COMPONENT_NAME_MESSAGE_ID, location, javaDeclarationResource, NONUNIQUE_NAME_MESSAGE_GROUP);
					}
				}
			}
		}
	}
}