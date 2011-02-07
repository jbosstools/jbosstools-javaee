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
package org.jboss.tools.jsf.web.validation.jsf2.util;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.internal.core.JarEntryFile;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMAttr;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.jboss.tools.jsf.jsf2.util.JSF2ResourceUtil;
import org.jboss.tools.jsf.web.validation.jsf2.components.IJSFValidationComponent;
import org.jboss.tools.jsf.web.validation.jsf2.components.JSF2ComponentFactory;

/**
 * 
 * @author yzhishko
 * 
 */

@SuppressWarnings("restriction")
public class JSF2ComponentRecognizer {

	public static IJSFValidationComponent[] recognizeCompositeValidationComponents(
			IFile file, IDOMElement element) {
		List<IJSFValidationComponent> validationComponents = new ArrayList<IJSFValidationComponent>(
				0);
		Object container = JSF2ResourceUtil.findCompositeComponentContainer(
				file.getProject(), element);
		if (container == null) {
			IJSFValidationComponent component = JSF2ComponentFactory
					.createCompositeTempComponent(element);
			validationComponents.add(component);
		} else if (container instanceof IFile) {
			IJSFValidationComponent[] components = JSF2ComponentFactory
					.createFixableAttrTempComponents((IFile) container, element);
			for (int i = 0; i < components.length; i++) {
				validationComponents.add(components[i]);
			}
		} else if (container instanceof JarEntryFile) {
			IJSFValidationComponent[] components = JSF2ComponentFactory
					.createUnfixableAttrTempComponents(
							(JarEntryFile) container, element);
			for (int i = 0; i < components.length; i++) {
				validationComponents.add(components[i]);
			}
		}
		return validationComponents.toArray(new IJSFValidationComponent[0]);
	}

	public static IJSFValidationComponent recognizeURIValidationComponent(
			IProject project, IDOMAttr attrContainer) {
		if (!JSF2ResourceUtil.isResourcesFolderExists(project, attrContainer
				.getValue())) {
			return JSF2ComponentFactory.createURITempComponent(attrContainer);
		}
		return null;
	}

}
