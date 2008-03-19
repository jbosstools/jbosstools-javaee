package org.jboss.tools.jsf.vpe.jsf.template;

import org.eclipse.wst.xml.core.internal.provisional.document.IDOMAttr;
import org.jboss.tools.jsf.vpe.jsf.template.util.ComponentUtil;
import org.jboss.tools.jsf.vpe.jsf.template.util.JSF;
import org.jboss.tools.jsf.vpe.jsf.template.util.NodeProxyUtil;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.mapping.VpeAttributeData;
import org.jboss.tools.vpe.editor.mapping.VpeElementData;
import org.jboss.tools.vpe.editor.template.VpeChildrenInfo;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.util.HTML;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMText;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public abstract class AbstractOutputJsfTemplate extends
		AbstractEditableJsfTemplate {

	/**
	 * 
	 * @param element
	 * @return
	 */
	protected Attr getOutputAttributeNode(Element element) {

		if (element.hasAttribute(JSF.ATTR_VALUE))
			return element.getAttributeNode(JSF.ATTR_VALUE);
		else if (element.hasAttribute(JSF.ATTR_BINDING))
			return element.getAttributeNode(JSF.ATTR_BINDING);

		return null;

	}

	/**
	 * copy outputAttributes
	 * 
	 * @param visualElement
	 * @param sourceElement
	 */
	protected void copyOutputJsfAttributes(nsIDOMElement visualElement,
			Element sourceElement) {
		copyGeneralJsfAttributes(visualElement, sourceElement);
		copyAttribute(visualElement, sourceElement, JSF.ATTR_DIR, HTML.ATTR_DIR);

	}

	/**
	 * 
	 * @param pageContext
	 * @param visualDocument
	 * @param sourceElement
	 * @param targetVisualElement
	 * @param creationData
	 */
	protected void processOutputAttribute(VpePageContext pageContext,
			nsIDOMDocument visualDocument, Element sourceElement,
			nsIDOMElement targetVisualElement, VpeCreationData creationData) {

		VpeElementData elementData = new VpeElementData();

		Attr outputAttr = getOutputAttributeNode(sourceElement);

		// prepare value
		String newValue = prepareAttrValue(pageContext, sourceElement,
				outputAttr);

		if (outputAttr != null) {
			// if escape then contents of value (or other attribute) is only
			// text
			if (!sourceElement.hasAttribute(JSF.ATTR_ESCAPE)
					|| "true".equalsIgnoreCase(sourceElement //$NON-NLS-1$
							.getAttribute(JSF.ATTR_ESCAPE))) {

				String value = outputAttr.getNodeValue();

				nsIDOMText text;
				// if bundleValue differ from value then will be represent
				// bundleValue, but text will be not edit
				boolean isEditable = value.equals(newValue);

				text = visualDocument.createTextNode(newValue);
				// add attribute for ability of editing

				elementData.addAttributeData(new VpeAttributeData(outputAttr,
						text, isEditable));

				targetVisualElement.appendChild(text);

			}
			// then text can be html code
			else {

				// create info
				VpeChildrenInfo spanInfo = new VpeChildrenInfo(
						targetVisualElement);

				// get atribute's offset
				int offset = ((IDOMAttr) outputAttr)
						.getValueRegionStartOffset();

				// reparse attribute's value
				NodeList list = NodeProxyUtil.reparseAttributeValue(newValue,
						offset);

				// add children to info
				for (int i = 0; i < list.getLength(); i++) {

					Node child = list.item(i);

					// add info to creation data
					spanInfo.addSourceChild(child);
				}

				elementData.addAttributeData(new VpeAttributeData(outputAttr,
						targetVisualElement, false));

				creationData.addChildrenInfo(spanInfo);

			}

			creationData.setElementData(elementData);

		}

	}

	protected String prepareAttrValue(VpePageContext pageContext,
			Element parent, Attr attr) {

		return ComponentUtil.getBundleValue(pageContext, attr);
	}
}
