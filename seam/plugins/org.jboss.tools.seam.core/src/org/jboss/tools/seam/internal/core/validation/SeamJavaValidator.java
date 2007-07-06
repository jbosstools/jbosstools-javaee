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

	public static final String NONUNIQUE_COMPONENT_NAME_MESSAGE_ID="NONUNIQUE_COMPONENT_NAME_MESSAGE";

	public ISchedulingRule getSchedulingRule(IValidationContext helper) {
		// TODO
		return null;
	}

	public IStatus validateInJob(IValidationContext helper, IReporter reporter)	throws ValidationException {
		super.validateInJob(helper, reporter);
		System.out.println("Seam validation in job.");
		SeamJavaHelper seamJavaHelper = (SeamJavaHelper)helper;
		String[] uris = seamJavaHelper.getURIs();
		ISeamProject project = seamJavaHelper.getSeamProject();
		if (uris.length > 0) {
			IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
			IFile currentFile = null;
			for (int i = 0; i < uris.length && !reporter.isCancelled(); i++) {
				currentFile = root.getFile(new Path(uris[i]));
				if (currentFile != null && currentFile.exists()) {
					Set<ISeamComponent> components = project.getComponentsByResource(currentFile);
					for (ISeamComponent component : components) {
						validateUniqueComponentName(project, component, helper, reporter);
					}
					// TODO
				}
			}
		} else {
			return validateAll(project, helper, reporter);
		}

		return OK_STATUS;
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
						// Mark nonunique name.
						if(!markedDeclarations.contains(checkedDeclaration)) {
							// Mark first wrong declaration
							ISeamTextSourceReference target = ((SeamComponentDeclaration)checkedDeclaration).getLocationFor(SeamComponentDeclaration.PATH_OF_NAME);
							addError(NONUNIQUE_COMPONENT_NAME_MESSAGE_ID, target);
							markedDeclarations.add(checkedDeclaration);
						}
						// Mark next wrong declaration
						markedDeclarations.add(javaDeclaration);
						ISeamTextSourceReference target = ((SeamComponentDeclaration)javaDeclaration).getLocationFor(SeamComponentDeclaration.PATH_OF_NAME);
						addError(NONUNIQUE_COMPONENT_NAME_MESSAGE_ID, target);
					}
				}
			}
		}
	}
}