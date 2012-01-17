/******************************************************************************* 
 * Copyright (c) 2007 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.jboss.tools.jsf.vpe.jsf.template;

import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.mapping.AttributeData;
import org.jboss.tools.vpe.editor.mapping.VpeElementData;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.util.HTML;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMHTMLTextAreaElement;
import org.mozilla.interfaces.nsIDOMNode;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class JsfInputTextAreaTemplate extends AbstractEditableJsfTemplate {

	public VpeCreationData create(VpePageContext pageContext, Node sourceNode,
			nsIDOMDocument visualDocument) {

		Element sourceElement = (Element) sourceNode;

		nsIDOMElement textArea = visualDocument
				.createElement(HTML.TAG_TEXTAREA);
		
// Commented as fix for JBIDE-3012.		
//		((nsIDOMHTMLTextAreaElement) textArea
//				.queryInterface(nsIDOMHTMLTextAreaElement.NS_IDOMHTMLTEXTAREAELEMENT_IID))
//				.setReadOnly(true);

		VpeCreationData creationData = new VpeCreationData(textArea);

		copyGeneralJsfAttributes(sourceElement, textArea);
		ComponentUtil.copyDisabled(sourceElement, textArea);

		copyAttribute(textArea, sourceElement, JSF.ATTR_DIR, HTML.ATTR_DIR);
		copyAttribute(textArea, sourceElement, JSF.ATTR_ROWS, HTML.ATTR_ROWS);
		copyAttribute(textArea, sourceElement, JSF.ATTR_COLS, HTML.ATTR_COLS);

		VpeElementData elementData = new VpeElementData();
		nsIDOMNode text = null;
		if (sourceElement.hasAttribute(JSF.ATTR_VALUE)) {

			Attr attr = sourceElement.getAttributeNode(JSF.ATTR_VALUE);
			text = visualDocument.createTextNode(sourceElement
					.getAttribute(JSF.ATTR_VALUE));
			elementData.addNodeData(new AttributeData(attr, textArea,
					true));

		} else {
			text = visualDocument.createTextNode(""); //$NON-NLS-1$
			elementData.addNodeData(new AttributeData(JSF.ATTR_VALUE,
					textArea, true));

		}
		textArea.appendChild(text);
		creationData.setElementData(elementData);

		return creationData;
	}

	@Override
	public boolean recreateAtAttrChange(VpePageContext pageContext,
			Element sourceElement, nsIDOMDocument visualDocument,
			nsIDOMElement visualNode, Object data, String name, String value) {
		return true;
	}

}
