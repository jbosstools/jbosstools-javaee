 /*******************************************************************************
  * Copyright (c) 2007-2010 Red Hat, Inc.
  * Distributed under license by Red Hat, Inc. All rights reserved.
  * This program is made available under the terms of the
  * Eclipse Public License v1.0 which accompanies this distribution,
  * and is available at http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributors:
  *     Red Hat, Inc. - initial API and implementation
  ******************************************************************************/

package org.jboss.tools.jsf.web.validation.jsf2;

import java.io.InputStream;
import java.util.Scanner;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.validation.ValidationResult;
import org.eclipse.wst.validation.ValidationState;
import org.eclipse.wst.validation.internal.provisional.core.IMessage;
import org.eclipse.wst.xml.core.internal.validation.core.NestedValidatorContext;
import org.eclipse.wst.xml.core.internal.validation.core.ValidationMessage;
import org.eclipse.wst.xml.core.internal.validation.core.ValidationReport;
import org.eclipse.wst.xml.core.internal.validation.eclipse.Validator;
import org.jboss.tools.jsf.JSFModelPlugin;
import org.jboss.tools.jsf.web.validation.jsf2.util.JSF2ComponentParams;
import org.jboss.tools.jsf.web.validation.jsf2.util.JSF2ResourceUtil;
import org.jboss.tools.jst.web.kb.IKbProject;
import org.jboss.tools.jst.web.kb.KbProjectFactory;

/**
 * 
 * @author yzhishko
 *
 */

@SuppressWarnings("restriction")
public class JSF2ComponentsValidator extends Validator {

	private IKbProject kbProject;
	private IFile file;

	@Override
	public ValidationResult validate(IResource resource, int kind,
			ValidationState state, IProgressMonitor monitor) {
		ValidationResult result = new ValidationResult();
		if (resource instanceof IFile) {
			IFile file = (IFile) resource;
			if (!isValidate(file)) {
				return result;
			}
			IProject project = file.getProject();
			kbProject = KbProjectFactory.getKbProject(project, false);
			if (kbProject != null) {
				this.file = file;
				return super.validate(resource, kind, state, monitor);
			}
		}
		return result;
	}

	@Override
	public ValidationReport validate(String uri, InputStream inputstream,
			NestedValidatorContext context, ValidationResult result) {
		JSF2Validator validator = JSF2Validator.getInstance();
		return validator.validate(file, uri);
	}

	protected boolean isValidate(IFile file) {
		boolean isValidate = false;
		try {
			InputStream is = file.getContents();
			Scanner scanner = new Scanner(is);
			while (scanner.hasNextLine()) {
				if (scanner.nextLine().indexOf(
						JSF2ResourceUtil.JSF2_URI_PREFIX) != -1) {
					isValidate = true;
					scanner.close();
					break;
				}
			}
		} catch (CoreException e) {
			JSFModelPlugin.getPluginLog().logError(e);
		}
		return isValidate;
	}

	@Override
	protected void addInfoToMessage(ValidationMessage validationMessage,
			IMessage message) {
		Object[] args = validationMessage.getMessageArguments();
		if (args == null) {
			return;
		}
		for (int i = 0; i < args.length; i++) {
			if (args[i] instanceof JSF2ComponentParams) {
				message
						.setAttribute(
								JSF2ResourceUtil.COMPONENT_RESOURCE_PATH_KEY,
								((JSF2ComponentParams) args[i])
										.getRelativateLocation());
				break;
			}
		}
	}

}
