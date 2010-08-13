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
import org.jboss.tools.jsf.jsf2.util.JSF2ResourceUtil;
import org.jboss.tools.jsf.web.validation.jsf2.components.IJSF2ValidationComponent;
import org.jboss.tools.jsf.web.validation.jsf2.components.JSF2AttrTempComponent;
import org.jboss.tools.jsf.web.validation.jsf2.components.JSF2CompositeTempComponent;
import org.jboss.tools.jsf.web.validation.jsf2.components.JSF2URITempComponent;
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
			this.file = file;
			return super.validate(resource, kind, state, monitor);
		}
		return result;
	}

	@Override
	public ValidationReport validate(String uri, InputStream inputstream,
			NestedValidatorContext context, ValidationResult result) {
		JSF2XMLValidator validator = JSF2XMLValidator.getInstance();
		return validator.validate(file, uri);
	}

	protected boolean isValidate(IFile file) {
		boolean isValidate = false;
		if (file.getProject() == null
				|| file.getProject().isAccessible() == false) {
			return false;
		}
		try {
			InputStream is = file.getContents();
			Scanner scanner = new Scanner(is);
			while (scanner.hasNextLine()) {
				if (scanner.nextLine()
						.indexOf(JSF2ResourceUtil.JSF2_URI_PREFIX) != -1) {
					isValidate = true;
					scanner.close();
					break;
				}
			}
		} catch (CoreException e) {
			JSFModelPlugin.getPluginLog().logError(e);
			return isValidate;
		}
		IProject project = file.getProject();
		kbProject = KbProjectFactory.getKbProject(project, false);
		if (kbProject == null) {
			isValidate = false;
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
		if (args[0] instanceof JSF2CompositeTempComponent) {
			JSF2CompositeTempComponent component = (JSF2CompositeTempComponent) args[0];
			message.setAttribute(IJSF2ValidationComponent.JSF2_TYPE_KEY,
					component.getType());
			message.setAttribute(JSF2ResourceUtil.COMPONENT_RESOURCE_PATH_KEY,
					component.getComponentResourceLocation());
			message.setAttribute(JSF2ResourceUtil.JSF2_COMPONENT_NAME, component.getElement().getLocalName());
			String[] attrNames = component.getAttrNames();
			if (attrNames != null) {
				for (int i = 0; i < attrNames.length; i++) {
					message.setAttribute(
							IJSF2ValidationComponent.JSF2_ATTR_NAME_KEY
									+ String.valueOf(i), attrNames[i]);
				}
			}
			return;
		}
		if (args[0] instanceof JSF2AttrTempComponent) {
			JSF2AttrTempComponent component = (JSF2AttrTempComponent) args[0];
			message.setAttribute(IJSF2ValidationComponent.JSF2_TYPE_KEY,
					component.getType());
			message.setAttribute(IJSF2ValidationComponent.JSF2_ATTR_NAME_KEY,
					component.getName());
			message.setAttribute(JSF2ResourceUtil.COMPONENT_RESOURCE_PATH_KEY,
					component.getComponentResourceLocation());
			return;
		}
		if (args[0] instanceof JSF2URITempComponent) {
			JSF2URITempComponent component = (JSF2URITempComponent) args[0];
			message.setAttribute(IJSF2ValidationComponent.JSF2_TYPE_KEY,
					IJSF2ValidationComponent.JSF2_URI_TYPE);
			message.setAttribute(IJSF2ValidationComponent.JSF2_URI_NAME_KEY,
					component.getURI());
			message.setAttribute(JSF2ResourceUtil.COMPONENT_RESOURCE_PATH_KEY,
					component.getResourcesFolder());
		}
	}

}
