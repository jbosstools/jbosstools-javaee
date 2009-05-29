package org.jboss.tools.jsf.vpe.richfaces.template;

import org.jboss.tools.jsf.vpe.richfaces.ComponentUtil;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeAbstractTemplate;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.util.HTML;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class RichFacesLayoutTemplate extends VpeAbstractTemplate {

	/**
	 * Constructor
	 */
	public RichFacesLayoutTemplate() {
		super();
	}

	public VpeCreationData create(VpePageContext pageContext, Node sourceNode,
			nsIDOMDocument visualDocument) {
		VpeCreationData creationData = null;
		Element sourceElement = (Element)sourceNode;
		nsIDOMElement mainDiv = visualDocument.createElement(HTML.TAG_DIV);
		String style = sourceElement.getAttribute(HTML.ATTR_STYLE);
		if (ComponentUtil.isNotBlank(style)) {
			mainDiv.setAttribute(HTML.ATTR_STYLE, style);
		}
		creationData = new VpeCreationData(mainDiv);
		return creationData;
	}

}
