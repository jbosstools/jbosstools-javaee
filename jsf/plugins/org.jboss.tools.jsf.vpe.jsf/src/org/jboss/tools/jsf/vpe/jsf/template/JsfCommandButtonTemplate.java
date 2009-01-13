package org.jboss.tools.jsf.vpe.jsf.template;

import org.jboss.tools.jsf.vpe.jsf.template.util.ComponentUtil;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeChildrenInfo;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.util.HTML;
import org.jboss.tools.vpe.editor.util.VisualDomUtil;
import org.jboss.tools.vpe.editor.util.VpeStyleUtil;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class JsfCommandButtonTemplate extends AbstractOutputJsfTemplate {

    public VpeCreationData create(VpePageContext pageContext, Node sourceNode,
	    nsIDOMDocument visualDocument) {
	
	Element sourceElement = (Element) sourceNode;
	
	nsIDOMElement contentSpan = VisualDomUtil.createBorderlessContainer(visualDocument); 
	nsIDOMElement firstSpan = VisualDomUtil.createBorderlessContainer(visualDocument);
	nsIDOMElement lastSpan = VisualDomUtil.createBorderlessContainer(visualDocument);
	nsIDOMElement input = visualDocument.createElement(HTML.TAG_INPUT);

	VpeCreationData creationData = new VpeCreationData(contentSpan);
	
	boolean disabled = ComponentUtil.string2boolean(ComponentUtil
		.getAttribute(sourceElement, HTML.ATTR_DISABLED));
	String type = sourceElement.getAttribute(HTML.ATTR_TYPE);
	String image = sourceElement.getAttribute(HTML.VALUE_TYPE_IMAGE);
	String value = sourceElement.getAttribute(HTML.ATTR_VALUE);
	String style = sourceElement.getAttribute(HTML.ATTR_STYLE);
	String clazz = sourceElement.getAttribute(HTML.ATTR_CLASS);
	String dir = sourceElement.getAttribute(HTML.ATTR_DIR);
	
	if (ComponentUtil.isNotBlank(image)) {
	    type =  HTML.VALUE_TYPE_IMAGE;
	    String imgFullPath = VpeStyleUtil.addFullPathToImgSrc(image, pageContext, true);
	    input.setAttribute(HTML.ATTR_SRC, imgFullPath);
	}
	
	if (ComponentUtil.isBlank(type)) {
	    type = HTML.VALUE_TYPE_BUTTON;
	}
	input.setAttribute(HTML.ATTR_TYPE, type);
	
	if (ComponentUtil.isNotBlank(value)) {
	    input.setAttribute(HTML.ATTR_VALUE, value);
	}
	if (ComponentUtil.isNotBlank(style)) {
	    input.setAttribute(HTML.ATTR_VALUE, style);
	}
	if (ComponentUtil.isNotBlank(clazz)) {
	    input.setAttribute(HTML.ATTR_VALUE, clazz);
	}
	if (ComponentUtil.isNotBlank(dir)) {
	    input.setAttribute(HTML.ATTR_VALUE, dir);
	}
	
	if (disabled) {
	    input.setAttribute(HTML.ATTR_DISABLED, HTML.ATTR_DISABLED);
	}
	
	VpeChildrenInfo spanInfo = new VpeChildrenInfo(firstSpan);
	creationData.addChildrenInfo(spanInfo);
	NodeList nodeList = sourceElement.getChildNodes();
	for (int i = 0; i < nodeList.getLength(); i++) {
	    Node child = nodeList.item(i);
	    spanInfo.addSourceChild(child);
	}
	contentSpan.appendChild(firstSpan);
	contentSpan.appendChild(lastSpan);
	firstSpan.appendChild(input);
	return creationData;
    }

}
