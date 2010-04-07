package org.jboss.tools.jsf.vpe.jsf.template;

import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeAbstractTemplate;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.util.HTML;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.w3c.dom.Node;

public class JsfFacetInDataCell extends VpeAbstractTemplate{

	public VpeCreationData create(VpePageContext pageContext, Node sourceNode,
			nsIDOMDocument visualDocument) {
		nsIDOMElement visualElement = visualDocument.createElement(HTML.TAG_TH);
		visualElement.setAttribute(HTML.ATTR_STYLE, computeStyleValue());
		visualElement.setAttribute(HTML.ATTR_SCOPE, "colgroup"); //$NON-NLS-1$
		visualElement.setAttribute("vpe-pseudo-type", "vpe-jbpm-cell-header"); //$NON-NLS-1$ //$NON-NLS-2$
		return new VpeCreationData(visualElement);
	}

	private String computeStyleValue() {
		StringBuilder builder = new StringBuilder(""); //$NON-NLS-1$
		builder.append("background:none repeat scroll 0 0 #444444;"); //$NON-NLS-1$
		builder.append("border-bottom:1px solid #000000;"); //$NON-NLS-1$
		builder.append("border-collapse:collapse;"); //$NON-NLS-1$
		builder.append("color:#FFFFFF;"); //$NON-NLS-1$
		builder.append("font-size:11px;"); //$NON-NLS-1$
		builder.append("text-align:left;"); //$NON-NLS-1$
		builder.append("text-decoration:none;"); //$NON-NLS-1$
		builder.append("white-space:nowrap;"); //$NON-NLS-1$
		builder.append("width:130px;"); //$NON-NLS-1$
		builder.append("padding:3px 5px"); //$NON-NLS-1$
		return builder.toString();
	}
	
}
