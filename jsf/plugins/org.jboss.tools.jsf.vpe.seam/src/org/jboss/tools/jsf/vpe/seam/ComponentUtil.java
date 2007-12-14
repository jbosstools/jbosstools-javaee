package org.jboss.tools.jsf.vpe.seam;

import org.mozilla.interfaces.nsIDOMElement;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class ComponentUtil {

	/**
	 * Copies all attributes from source node to visual node.
	 * 
	 * @param sourceNode
	 * @param visualNode
	 */
	public static void copyAttributes(Node sourceNode,
			nsIDOMElement visualElement) {
		NamedNodeMap namedNodeMap = sourceNode.getAttributes();
		for (int i = 0; i < namedNodeMap.getLength(); i++) {
			Node attribute = namedNodeMap.item(i);
			visualElement.setAttribute(attribute.getNodeName(), attribute
					.getNodeValue());
		}
	}

}
