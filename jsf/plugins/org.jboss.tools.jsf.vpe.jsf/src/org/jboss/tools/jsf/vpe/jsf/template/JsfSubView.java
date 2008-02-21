package org.jboss.tools.jsf.vpe.jsf.template;

import java.util.ArrayList;
import java.util.List;

import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeAbstractTemplate;
import org.jboss.tools.vpe.editor.template.VpeChildrenInfo;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.util.HTML;
import org.jboss.tools.vpe.editor.util.VpeStyleUtil;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

// TODO: Auto-generated Javadoc
/**
 * The Class JsfSubView.
 */
public class JsfSubView extends VpeAbstractTemplate {

	private static String STYLE_FOR_DIV = "border: 1px dotted #FF6600; padding: 5px;";
	
	/**
	 * Instantiates a new jsf sub view.
	 */
	public JsfSubView() {
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.vpe.editor.template.VpeTemplate#create(org.jboss.tools.vpe.editor.context.VpePageContext, org.w3c.dom.Node, org.mozilla.interfaces.nsIDOMDocument)
	 */
	public VpeCreationData create(VpePageContext pageContext, Node sourceNode,
			nsIDOMDocument visualDocument) {
		Element sourceElement = (Element)sourceNode;
		nsIDOMElement table = visualDocument.createElement(HTML.TAG_TABLE);
		nsIDOMElement tr = visualDocument.createElement(HTML.TAG_TR);
		nsIDOMElement td = visualDocument.createElement(HTML.TAG_TD);
		nsIDOMElement div = visualDocument.createElement(HTML.TAG_DIV);
		
		div.setAttribute(VpeStyleUtil.ATTRIBUTE_STYLE, STYLE_FOR_DIV);
		td.appendChild(div);
		tr.appendChild(td);
		table.appendChild(tr);
		
		VpeCreationData creationData = new VpeCreationData(table);
		VpeChildrenInfo divInfo = new VpeChildrenInfo(div);
		creationData.addChildrenInfo(divInfo);
		
		for (Node child : getChildren(sourceElement)) {
			divInfo.addSourceChild(child);
		}

		return creationData;
	}
	
	/**
	 * Gets the children.
	 * 
	 * @param sourceElement the source element
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
	
	/* (non-Javadoc)
	 * @see org.jboss.tools.vpe.editor.template.VpeAbstractTemplate#isRecreateAtAttrChange(org.jboss.tools.vpe.editor.context.VpePageContext, org.w3c.dom.Element, org.mozilla.interfaces.nsIDOMDocument, org.mozilla.interfaces.nsIDOMElement, java.lang.Object, java.lang.String, java.lang.String)
	 */
	public boolean isRecreateAtAttrChange(VpePageContext pageContext,
			Element sourceElement, nsIDOMDocument visualDocument,
			nsIDOMElement visualNode, Object data, String name, String value) {
		return true;
	}

}
