package org.jboss.tools.jsf.vpe.seam.template;

import org.jboss.tools.jsf.vpe.seam.template.util.SeamUtil;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.util.HTML;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMNode;
import org.w3c.dom.Node;

public class SeamPdfFontTemplate extends SeamPdfAbstractTemplate {

	@Override
	public VpeCreationData create(VpePageContext pageContext, Node sourceNode,
			nsIDOMDocument visualDocument) {
		nsIDOMNode visualNode = visualDocument.createElement(HTML.TAG_SPAN);
		String styleAttrValue = SeamUtil.getStyleAttr(sourceNode);
		nsIDOMElement visualElement = (nsIDOMElement) visualNode
				.queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID);
		visualElement.setAttribute(HTML.ATTR_STYLE, styleAttrValue);
		return new VpeCreationData(visualElement);
	}

}
