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
import org.jboss.tools.jsf.web.validation.jsf2.util.JSF2ComponentModelManager;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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

	public static IJSF2ValidationComponent createCompositeTempComponent(
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

	public static IJSF2ValidationComponent[] createUnfixableAttrTempComponents(
			JarEntryFile container, IDOMElement elementWithAttrs) {
		List<IJSF2ValidationComponent> validationComponents = new ArrayList<IJSF2ValidationComponent>(
				0);
		IDOMDocument containerDocument = JSF2ComponentModelManager
				.getReadableDOMDocument(container);
		IDOMElement interfaceElement = JSF2ComponentModelManager.getManager()
				.checkCompositeInterface(containerDocument);
		Set<String> interfaceAttrs = getInterfaceAttrs(interfaceElement);
		interfaceAttrs.addAll(uncheckedAttrs);
		IDOMAttr[] existingAttrs = getExistingAttrs(elementWithAttrs);
		for (int i = 0; i < existingAttrs.length; i++) {
			if (!interfaceAttrs.contains(existingAttrs[i].getName())) {
				IDOMAttr attr = existingAttrs[i];
				JSF2AttrTempComponent component = new JSF2AttrTempComponent(
						attr, (ElementImpl) elementWithAttrs);
				component
						.setType(IJSF2ValidationComponent.JSF2_UNFIXABLE_ATTR_TYPE);
				component.setStartOffSet(attr.getStartOffset());
				component.setLine(attr.getStructuredDocument().getLineOfOffset(
						component.getStartOffSet()) + 1);
				component.setLength(attr.getName().length());
				component.createValidationMessage();
				component.createMessageParams();
				validationComponents.add(component);
			}
		}
		return validationComponents.toArray(new IJSF2ValidationComponent[0]);
	}

	public static IJSF2ValidationComponent[] createFixableAttrTempComponents(
			IFile compContainerFile, IDOMElement elementWithAttrs) {
		List<IJSF2ValidationComponent> components = new ArrayList<IJSF2ValidationComponent>(
				0);
		IDOMDocument document = JSF2ComponentModelManager
				.getReadableDOMDocument(compContainerFile);
		IDOMElement interfaceElement = JSF2ComponentModelManager.getManager()
				.checkCompositeInterface(document);
		Set<String> interfaceAttrs = getInterfaceAttrs(interfaceElement);
		interfaceAttrs.addAll(uncheckedAttrs);
		IDOMAttr[] existingAttrs = getExistingAttrs(elementWithAttrs);
		for (int i = 0; i < existingAttrs.length; i++) {
			if (!interfaceAttrs.contains(existingAttrs[i].getName())) {
				IDOMAttr attr = existingAttrs[i];
				JSF2AttrTempComponent component = new JSF2AttrTempComponent(
						attr, (ElementImpl) elementWithAttrs);
				component
						.setType(IJSF2ValidationComponent.JSF2_FIXABLE_ATTR_TYPE);
				component.setStartOffSet(attr.getStartOffset());
				component.setLine(attr.getStructuredDocument().getLineOfOffset(
						component.getStartOffSet()) + 1);
				component.setLength(attr.getName().length());
				component.createValidationMessage();
				component.createMessageParams();
				components.add(component);
			}
		}
		return components.toArray(new IJSF2ValidationComponent[0]);
	}

	public static IJSF2ValidationComponent createURITempComponent(
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

	private static Set<String> getInterfaceAttrs(IDOMElement interfaceElement) {
		Set<String> interfaceAttrs = new HashSet<String>(0);
		if (interfaceElement != null) {
			String prefix = interfaceElement.getPrefix();
			String nodeName = "attribute"; //$NON-NLS-1$
			if (prefix != null && !"".equals(prefix)) { //$NON-NLS-1$
				nodeName = prefix + ":" + nodeName; //$NON-NLS-1$
			}
			NodeList attrsElements = interfaceElement
					.getElementsByTagName(nodeName);
			if (attrsElements != null) {
				for (int i = 0; i < attrsElements.getLength(); i++) {
					Node el = attrsElements.item(i);
					if (el instanceof IDOMElement) {
						IDOMElement element = (IDOMElement) el;
						String attrvalue = element.getAttribute("name"); //$NON-NLS-1$
						if (attrvalue != null && !"".equals(attrvalue)) { //$NON-NLS-1$
							interfaceAttrs.add(attrvalue);
						}
					}
				}
			}
		}
		return interfaceAttrs;
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
