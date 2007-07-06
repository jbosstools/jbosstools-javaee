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
import org.jboss.tools.seam.core.SeamComponentPrecedenceType;
import org.jboss.tools.seam.internal.core.SeamComponentDeclaration;

public class SeamJavaValidator extends SeamValidator {

	// TODO
	public static final String NONUNIQUE_COMPONENT_NAME_MESSAGE_ID="";

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
					System.out.println(currentFile);
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
			int firstJavaDeclarationPrecedence = firstJavaDeclaration.getPrecedence();
			if(firstJavaDeclarationPrecedence < 0) {
				firstJavaDeclarationPrecedence = ISeamJavaComponentDeclaration.DEFAULT_PRECEDENCE;
			}
			usedPrecedences.put(firstJavaDeclarationPrecedence, firstJavaDeclaration);
			Set<ISeamComponentDeclaration> declarations = component.getAllDeclarations();
			for (ISeamComponentDeclaration declaration : declarations) {
				if(declaration instanceof ISeamJavaComponentDeclaration && declaration!=firstJavaDeclaration) {
					// Component class with the same component name. Check precedence.
					ISeamJavaComponentDeclaration javaDeclaration = (ISeamJavaComponentDeclaration)declaration;
					int javaDeclarationPrecedence = javaDeclaration.getPrecedence();
					ISeamJavaComponentDeclaration usedDeclaration = usedPrecedences.get(javaDeclarationPrecedence);
					if(usedDeclaration==null) {
						usedPrecedences.put(javaDeclarationPrecedence, javaDeclaration);
					} else {
						// Mark nonunique name.
						ISeamTextSourceReference target = ((SeamComponentDeclaration)javaDeclaration).getLocationFor(SeamComponentDeclaration.PATH_OF_NAME);
						addError(NONUNIQUE_COMPONENT_NAME_MESSAGE_ID, target);
					}
				}
			}
		}
	}
}