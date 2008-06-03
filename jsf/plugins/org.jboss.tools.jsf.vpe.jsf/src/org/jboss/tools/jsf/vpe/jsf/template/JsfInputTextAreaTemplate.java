package org.jboss.tools.jsf.vpe.jsf.template;

import org.jboss.tools.jsf.vpe.jsf.template.util.JSF;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.mapping.VpeAttributeData;
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
		((nsIDOMHTMLTextAreaElement) textArea
				.queryInterface(nsIDOMHTMLTextAreaElement.NS_IDOMHTMLTEXTAREAELEMENT_IID))
				.setReadOnly(true);

		VpeCreationData creationData = new VpeCreationData(textArea);

		copyGeneralJsfAttributes(textArea, sourceElement);

		copyAttribute(textArea, sourceElement, JSF.ATTR_DIR, HTML.ATTR_DIR);
		copyAttribute(textArea, sourceElement, JSF.ATTR_ROWS, HTML.ATTR_ROWS);
		copyAttribute(textArea, sourceElement, JSF.ATTR_COLS, HTML.ATTR_COLS);

		VpeElementData elementData = new VpeElementData();
		nsIDOMNode text = null;
		if (sourceElement.hasAttribute(JSF.ATTR_VALUE)) {

			Attr attr = sourceElement.getAttributeNode(JSF.ATTR_VALUE);
			text = visualDocument.createTextNode(sourceElement
					.getAttribute(JSF.ATTR_VALUE));
			elementData.addAttributeData(new VpeAttributeData(attr, textArea,
					true));

		} else {
			text = visualDocument.createTextNode(""); //$NON-NLS-1$
			elementData.addAttributeData(new VpeAttributeData(JSF.ATTR_VALUE,
					textArea, true));

		}
		textArea.appendChild(text);
		creationData.setElementData(elementData);

		return creationData;
	}

	@Override
	public boolean isRecreateAtAttrChange(VpePageContext pageContext,
			Element sourceElement, nsIDOMDocument visualDocument,
			nsIDOMElement visualNode, Object data, String name, String value) {
		return true;
	}

}
