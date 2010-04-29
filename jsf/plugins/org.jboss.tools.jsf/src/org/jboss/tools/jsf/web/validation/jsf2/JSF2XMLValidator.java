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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import org.eclipse.core.resources.IFile;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMAttr;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.eclipse.wst.xml.core.internal.validation.core.ValidationReport;
import org.jboss.tools.common.el.core.resolver.ELContext;
import org.jboss.tools.jsf.JSFModelPlugin;
import org.jboss.tools.jsf.web.validation.jsf2.components.IJSF2ValidationComponent;
import org.jboss.tools.jsf.web.validation.jsf2.util.JSF2ComponentRecognizer;
import org.jboss.tools.jsf.web.validation.jsf2.util.JSF2ComponentUtil;
import org.jboss.tools.jst.web.kb.IPageContext;
import org.jboss.tools.jst.web.kb.PageContextFactory;
import org.jboss.tools.jst.web.kb.internal.XmlContextImpl;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * 
 * @author yzhishko
 * 
 */

@SuppressWarnings("restriction")
public class JSF2XMLValidator {

	public static String JSF2_PROBLEM_ID = JSFModelPlugin.PLUGIN_ID
			+ ".jsf2problemmarker"; //$NON-NLS-1$

	private JSF2ValidationInfo validationInfo;

	private static JSF2XMLValidator instance = new JSF2XMLValidator();

	public ValidationReport validate(IFile file, String uri) {
		validationInfo = new JSF2ValidationInfo(uri);
		validate(file);
		return validationInfo;
	}

	private JSF2XMLValidator() {

	}

	public static JSF2XMLValidator getInstance() {
		return instance;
	}

	private void validate(IFile file) {
		ELContext elContext = PageContextFactory.createPageContext(file);
		if (elContext instanceof IPageContext) {
			validateAsDOM(file);
		} else if (elContext instanceof XmlContextImpl) {
			if ("xhtml".equals(file.getFileExtension())) { //$NON-NLS-1$
				validateAsDOM(file);
			}
		}
	}

	private void validateAsDOM(IFile file) {
		IDOMDocument document = JSF2ComponentUtil
				.getReadableDocumentForFile(file);
		IJSF2ValidationComponent[] components = getValidationComponents(
				document, file);
		if (components != null) {
			for (int i = 0; i < components.length; i++) {
				createMarkerForComponent(components[i]);
			}
		}
	}

	private void createMarkerForComponent(
			IJSF2ValidationComponent jsf2ValidationComponent) {
		validationInfo.addWarning(jsf2ValidationComponent
				.getValidationMessage(), jsf2ValidationComponent.getLine(), 0,
				validationInfo.getFileURI(), null, jsf2ValidationComponent
						.getMessageParams());
	}

	public static IJSF2ValidationComponent[] getValidationComponents(Node node,
			IFile file) {
		List<IJSF2ValidationComponent> components = new ArrayList<IJSF2ValidationComponent>(
				0);
		Set<String> tagNameSet = new HashSet<String>(0);
		Map<String, List<Element>> compositeComponentsMap = JSF2ComponentUtil
				.findCompositeComponents(node);
		Set<Entry<String, List<Element>>> entries = compositeComponentsMap
				.entrySet();
		for (Entry<String, List<Element>> entry : entries) {
			List<Element> elements = entry.getValue();
			for (Element element : elements) {
				if (!(tagNameSet.contains(element.getNodeName()))) {
					tagNameSet.add(element.getNodeName());
					IJSF2ValidationComponent[] validationComponents = JSF2ComponentRecognizer
							.recognizeCompositeValidationComponents(file,
									(IDOMElement) element);
					for (int i = 0; i < validationComponents.length; i++) {
						components.add(validationComponents[i]);
					}
				}
			}
		}
		IDOMAttr[] attrs = JSF2ComponentUtil.findURIContainers(node);
		Set<String> attrValuesSet = new HashSet<String>(0);
		for (int i = 0; i < attrs.length; i++) {
			if (!attrValuesSet.contains(attrs[i].getValue())) {
				attrValuesSet.add(attrs[i].getValue());
				IJSF2ValidationComponent validationComponent = JSF2ComponentRecognizer
						.recognizeURIValidationComponent(file.getProject(),
								attrs[i]);
				if (validationComponent != null) {
					components.add(validationComponent);
				}
			}
		}
		return components.toArray(new IJSF2ValidationComponent[0]);
	}
}
