package org.jboss.tools.jsf.vpe.seam.template;

import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.util.VisualDomUtil;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMNode;
import org.w3c.dom.Node;

public class SeamPdfTitleTemplate extends SeamPdfAbstractTemplate {

	@Override
	public VpeCreationData create(VpePageContext pageContext, Node sourceNode,
			nsIDOMDocument visualDocument) {
		nsIDOMNode visualNode = VisualDomUtil.createBorderlessContainer(visualDocument);
		return new VpeCreationData(visualNode);
	}

}
