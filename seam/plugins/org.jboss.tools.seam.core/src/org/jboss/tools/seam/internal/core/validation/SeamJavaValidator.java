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
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
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

/**
 * Validator for Java files.
 * @author Alexey Kazakov
 */
public class SeamJavaValidator extends SeamValidator {

	private static final String NONUNIQUE_NAME_MESSAGE_GROUP = "nonuniqueName";
	private Map<String, Set<IResource>> markedNonuniqueNamedResources = new HashMap<String, Set<IResource>>();
	private Map<IResource, String> nonuniqueNames = new HashMap<IResource, String>();

	public static final String NONUNIQUE_COMPONENT_NAME_MESSAGE_ID = "NONUNIQUE_COMPONENT_NAME_MESSAGE";

	public ISchedulingRule getSchedulingRule(IValidationContext helper) {
		// TODO
		return null;
	}

	public IStatus validateInJob(IValidationContext helper, IReporter reporter)	throws ValidationException {
		super.validateInJob(helper, reporter);
		SeamJavaHelper seamJavaHelper = (SeamJavaHelper)helper;
		String[] uris = seamJavaHelper.getURIs();
		ISeamProject project = seamJavaHelper.getSeamProject();
		if (uris.length > 0) {
			IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
			IFile currentFile = null;
			Set<IResource> checkedResource = new HashSet<IResource>();
			Set<ISeamComponent> checkedComponent = new HashSet<ISeamComponent>();
			for (int i = 0; i < uris.length && !reporter.isCancelled(); i++) {
				currentFile = root.getFile(new Path(uris[i]));
				if (currentFile != null && currentFile.exists()) {
					String oldComponentNameOfChangedFile = nonuniqueNames.get(currentFile);
					if(oldComponentNameOfChangedFile!=null) {
						Set<IResource> resources = new HashSet<IResource>(); // Resources which we have to validate.

						// Check if component name was changed in java file
						String newComponentNameOfChangedFile = getComponentNameByResource(currentFile, project);
						if(newComponentNameOfChangedFile!=null && !oldComponentNameOfChangedFile.equals(newComponentNameOfChangedFile)) {
							// Name was changed. Remove markers from resources with new component name.
							Set<IResource> rs = markedNonuniqueNamedResources.get(newComponentNameOfChangedFile);
							if(rs!=null) {
								for (IResource resource : rs) {
									reporter.removeMessageSubset(this, resource, NONUNIQUE_NAME_MESSAGE_GROUP);
									resources.add(resource);
								}
							}
						}

						Set<IResource> linkedResources = markedNonuniqueNamedResources.get(oldComponentNameOfChangedFile);
						if(linkedResources!=null) {
							resources.addAll(linkedResources);
						}

						// Validate all collected linked resources.
						for (IResource linkedResource : resources) {
							if(checkedResource.contains(linkedResource)) {
								continue;
							}
							reporter.removeMessageSubset(this, linkedResource, NONUNIQUE_NAME_MESSAGE_GROUP); // Remove markers from java file
							Set<ISeamComponent> components = project.getComponentsByResource(linkedResource);
							for (ISeamComponent component : components) {
								if(checkedComponent.contains(component)) {
									continue;
								}
								validateUniqueComponentName(project, component, helper, reporter);
								checkedComponent.add(component);
							}
							checkedResource.add(linkedResource);
						}
					} else {
						// Validate new (unmarked) Java file.
						// TODO
					}
//					reporter.removeAllMessages(this, currentFile); // Remove all markers from java file
//					Set<ISeamComponent> components = project.getComponentsByResource(currentFile);
//					for (ISeamComponent component : components) {
//						validateUniqueComponentName(project, component, helper, reporter);
//					}
					// TODO
				}
			}
		} else {
			return validateAll(project, helper, reporter);
		}

		return OK_STATUS;
	}

	public String getComponentNameByResource(IResource resource, ISeamProject project) {
		Set<ISeamComponent> components = project.getComponentsByResource(resource);
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
							ISeamTextSourceReference target = ((SeamComponentDeclaration)checkedDeclaration).getLocationFor(SeamComponentDeclaration.PATH_OF_NAME);
							addError(NONUNIQUE_COMPONENT_NAME_MESSAGE_ID, target, NONUNIQUE_NAME_MESSAGE_GROUP);
							markedDeclarations.add(checkedDeclaration);
							addLinkedResource(checkedDeclaration.getName(), checkedDeclaration.getResource());
						}
						// Mark next wrong declaration with that name
						markedDeclarations.add(javaDeclaration);
						addLinkedResource(javaDeclaration.getName(), javaDeclaration.getResource());
						ISeamTextSourceReference target = ((SeamComponentDeclaration)javaDeclaration).getLocationFor(SeamComponentDeclaration.PATH_OF_NAME);
						addError(NONUNIQUE_COMPONENT_NAME_MESSAGE_ID, target, NONUNIQUE_NAME_MESSAGE_GROUP);
					}
				}
			}
		}
	}

	/*
	 * Save linked resources of component name that we marked.
	 * It's needed for incremental validation because we must save all linked resources of changed java file.
	 */
	private void addLinkedResource(String componentName, IResource linkedResource) {
		Set<IResource> linkedResources = markedNonuniqueNamedResources.get(componentName);
		if(linkedResources==null) {
			// create set of linked resources with component name that we must mark.
			linkedResources = new HashSet<IResource>();
			markedNonuniqueNamedResources.put(componentName, linkedResources);
		}
		if(!linkedResources.contains(linkedResource)) {
			// save linked resources that we must mark.
			linkedResources.add(linkedResource);
		}
		// Save link between component name and marked resource. It's needed if component name changes in java file.
		nonuniqueNames.put(linkedResource, componentName);
	}
}