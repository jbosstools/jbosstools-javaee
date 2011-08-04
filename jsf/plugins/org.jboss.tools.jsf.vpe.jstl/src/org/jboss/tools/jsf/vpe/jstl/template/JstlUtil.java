package org.jboss.tools.jsf.vpe.jstl.template;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Class encapsulates JSTL utils.
 * 
 * @author dmaliarevich
 *
 */
public class JstlUtil {

    /**
     * Gets all children of the source element 
     * including text nodes.
     * 
     * @param sourceElement
     *            the source element
     * 
     * @return the children
     */
    public static List<Node> getChildren(Element sourceElement) {
	ArrayList<Node> children = new ArrayList<Node>();
	NodeList nodeList = sourceElement.getChildNodes();
	for (int i = 0; i < nodeList.getLength(); i++) {
	    Node child = nodeList.item(i);
	    children.add(child);
	}
	return children;
    }
}
