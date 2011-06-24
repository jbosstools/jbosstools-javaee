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

package org.jboss.tools.jsf.web.validation.jsf2.components;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.internal.core.JarEntryFile;
import org.eclipse.wst.xml.core.internal.document.ElementImpl;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMAttr;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.jboss.tools.jsf.jsf2.model.JSF2ComponentModelManager;
import org.jboss.tools.jsf.web.validation.IJSFValidationComponent;
import org.jboss.tools.jsf.web.validation.jsf2.util.JSF2ValidatorConstants;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * 
 * @author yzhishko
 * 
 */

@SuppressWarnings("restriction")
public class JSF2ComponentFactory {

	private static Set<String> uncheckedAttrs = new HashSet<String>(0);

	static {
		uncheckedAttrs.add("id"); //$NON-NLS-1$
	}

	public static IJSFValidationComponent createCompositeTempComponent(
			IDOMElement element) {
		JSF2CompositeTempComponent component = new JSF2CompositeTempComponent(
				(ElementImpl) element);
		component.setStartOffSet(element.getStartOffset());
		component.setLength(element.getStartEndOffset()
				- component.getStartOffSet());
		component.setLine(element.getStructuredDocument().getLineOfOffset(
				component.getStartOffSet()) + 1);
		component.createValidationMessage();
		component.createMessageParams();
		return component;
	}

	public static IJSFValidationComponent[] createUnfixableAttrTempComponents(
			JarEntryFile container, IDOMElement elementWithAttrs) {
		List<IJSFValidationComponent> validationComponents = new ArrayList<IJSFValidationComponent>(
				0);
		IDOMDocument containerDocument = JSF2ComponentModelManager
				.getReadableDOMDocument(container);
		Element interfaceElement = JSF2ComponentModelManager.getManager()
				.checkCompositeInterface(containerDocument);
		Set<String> interfaceAttrs = JSF2ComponentModelManager.getManager()
				.getInterfaceAttrs(interfaceElement);
		interfaceAttrs.addAll(uncheckedAttrs);
		IDOMAttr[] existingAttrs = getExistingAttrs(elementWithAttrs);
		for (int i = 0; i < existingAttrs.length; i++) {
			if (!interfaceAttrs.contains(existingAttrs[i].getName())) {
				IDOMAttr attr = existingAttrs[i];
				JSF2AttrTempComponent component = new JSF2AttrTempComponent(
						attr, (ElementImpl) elementWithAttrs);
				component
						.setType(JSF2ValidatorConstants.JSF2_UNFIXABLE_ATTR_TYPE);
				component.setStartOffSet(attr.getStartOffset());
				component.setLine(attr.getStructuredDocument().getLineOfOffset(
						component.getStartOffSet()) + 1);
				component.setLength(attr.getName().length());
				component.createValidationMessage();
				component.createMessageParams();
				validationComponents.add(component);
			}
		}
		return validationComponents.toArray(new IJSFValidationComponent[0]);
	}

	public static IJSFValidationComponent[] createFixableAttrTempComponents(
			IFile compContainerFile, IDOMElement elementWithAttrs) {
		List<IJSFValidationComponent> components = new ArrayList<IJSFValidationComponent>(
				0);
		IDOMDocument document = JSF2ComponentModelManager
				.getReadableDOMDocument(compContainerFile);
		Element interfaceElement = JSF2ComponentModelManager.getManager()
				.checkCompositeInterface(document);
		Set<String> interfaceAttrs = JSF2ComponentModelManager.getManager()
				.getInterfaceAttrs(interfaceElement);
		interfaceAttrs.addAll(uncheckedAttrs);
		IDOMAttr[] existingAttrs = getExistingAttrs(elementWithAttrs);
		for (int i = 0; i < existingAttrs.length; i++) {
			if (!interfaceAttrs.contains(existingAttrs[i].getName())) {
				IDOMAttr attr = existingAttrs[i];
				JSF2AttrTempComponent component = new JSF2AttrTempComponent(
						attr, (ElementImpl) elementWithAttrs);
				component
						.setType(JSF2ValidatorConstants.JSF2_FIXABLE_ATTR_TYPE);
				component.setStartOffSet(attr.getStartOffset());
				component.setLine(attr.getStructuredDocument().getLineOfOffset(
						component.getStartOffSet()) + 1);
				component.setLength(attr.getName().length());
				component.createValidationMessage();
				component.createMessageParams();
				components.add(component);
			}
		}
		return components.toArray(new IJSFValidationComponent[0]);
	}

	public static IJSFValidationComponent createURITempComponent(
			IDOMAttr attrContainer) {
		JSF2URITempComponent component = new JSF2URITempComponent(attrContainer
				.getValue());
		component.setStartOffSet(attrContainer.getStartOffset());
		component.setLength(attrContainer.getValueRegionStartOffset()
				- component.getStartOffSet()
				+ attrContainer.getValue().length() + 2);
		component.setLine(attrContainer.getStructuredDocument()
				.getLineOfOffset(component.getStartOffSet()) + 1);
		component.createMessageParams();
		component.createValidationMessage();
		return component;
	}

	private static IDOMAttr[] getExistingAttrs(IDOMElement validateElement) {
		List<IDOMAttr> existingAttrs = new ArrayList<IDOMAttr>(0);
		NamedNodeMap attrsMap = validateElement.getAttributes();
		if (attrsMap != null) {
			for (int i = 0; i < attrsMap.getLength(); i++) {
				Node node = attrsMap.item(i);
				if (node instanceof IDOMAttr) {
					existingAttrs.add((IDOMAttr) node);
				}
			}
		}
		return existingAttrs.toArray(new IDOMAttr[0]);
	}

}
