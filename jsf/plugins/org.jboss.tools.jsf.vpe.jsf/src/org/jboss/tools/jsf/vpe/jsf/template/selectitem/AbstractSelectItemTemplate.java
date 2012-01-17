/*******************************************************************************
  * Copyright (c) 2007-2008 Red Hat, Inc.
  * Distributed under license by Red Hat, Inc. All rights reserved.
  * This program is made available under the terms of the
  * Eclipse Public License v1.0 which accompanies this distribution,
  * and is available at http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributor:
  *     Red Hat, Inc. - initial API and implementation
  ******************************************************************************/
package org.jboss.tools.jsf.vpe.jsf.template.selectitem;

import org.eclipse.wst.xml.core.internal.provisional.document.IDOMAttr;
import org.jboss.tools.jsf.vpe.jsf.VpeElementProxyData;
import org.jboss.tools.jsf.vpe.jsf.template.AbstractOutputJsfTemplate;
import org.jboss.tools.jsf.vpe.jsf.template.JSF;
import org.jboss.tools.jsf.vpe.jsf.template.NodeProxyUtil;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.mapping.AttributeData;
import org.jboss.tools.vpe.editor.template.VpeChildrenInfo;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.util.Constants;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMText;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This class is the base class for all templates of 
 * {@code <h:selectItem>} and {@code <h:selectItems>}.
 * 
 * @author yradtsevich
 */
public abstract class AbstractSelectItemTemplate extends AbstractOutputJsfTemplate {

	/**
	 * This field is used to differ templates of 
	 * {@code <h:selectItem>} and {@code <h:selectItems>}.
	 * 
	 * @see SelectItemType
	 */
	protected final SelectItemType selectItemType;
	
	protected AbstractSelectItemTemplate(SelectItemType selectItemType) {
		this.selectItemType = selectItemType;
	}
	
	@Override
	public final Attr getOutputAttributeNode(Element element) {
		return selectItemType.getOutputAttributeNode(element);
	}
	
	protected void processOutputAttribute(VpePageContext pageContext,
			nsIDOMDocument visualDocument, Element sourceElement,
			nsIDOMElement targetVisualElement, VpeCreationData creationData) {

		VpeElementProxyData elementData = new VpeElementProxyData();

		Attr outputAttr = getOutputAttributeNode(sourceElement);

		if (outputAttr != null) {

			// prepare value
			String newValue = outputAttr.getValue();

			// if escape then contents of value (or other attribute) is only
			// text
			if (!sourceElement.hasAttribute(JSF.ATTR_ESCAPE)
					|| Constants.TRUE.equalsIgnoreCase(sourceElement
							.getAttribute(JSF.ATTR_ESCAPE))) {

				String value = outputAttr.getNodeValue();

				nsIDOMText text;
				// if bundleValue differ from value then will be represent
				// bundleValue, but text will be not edit
				boolean isEditable = value.equals(newValue);

				text = visualDocument.createTextNode(newValue);
				// add attribute for ability of editing

				elementData.addNodeData(new AttributeData(outputAttr,
						targetVisualElement, isEditable));

				targetVisualElement.appendChild(text);

			}
			// then text can be html code
			else {

				// create info
				VpeChildrenInfo targetVisualInfo = new VpeChildrenInfo(
						targetVisualElement);

				// get atribute's offset
				int offset = ((IDOMAttr) outputAttr)
						.getValueRegionStartOffset();

				// reparse attribute's value
				NodeList list = NodeProxyUtil.reparseAttributeValue(
						elementData, newValue, offset + 1);

				// add children to info
				for (int i = 0; i < list.getLength(); i++) {

					Node child = list.item(i);

					// add info to creation data
					targetVisualInfo.addSourceChild(child);
				}

				elementData.addNodeData(new AttributeData(outputAttr,
						targetVisualElement, false));

				creationData.addChildrenInfo(targetVisualInfo);

			}

		}

		creationData.setElementData(elementData);
	}
}
