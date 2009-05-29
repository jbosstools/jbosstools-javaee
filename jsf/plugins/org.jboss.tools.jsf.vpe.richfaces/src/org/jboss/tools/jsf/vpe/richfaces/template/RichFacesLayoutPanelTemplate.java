package org.jboss.tools.jsf.vpe.richfaces.template;

import org.jboss.tools.jsf.vpe.richfaces.ComponentUtil;
import org.jboss.tools.jsf.vpe.richfaces.template.util.RichFaces;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeAbstractTemplate;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.util.HTML;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class RichFacesLayoutPanelTemplate extends VpeAbstractTemplate {

	private static final String FLOAT_LEFT_STYLE = ";float: left;"; //$NON-NLS-1$
	private static final String FLOAT_RIGHT_STYLE = ";float: right;"; //$NON-NLS-1$
	
	/**
	 * Constructor
	 */
	public RichFacesLayoutPanelTemplate() {
		super();
	}

	public VpeCreationData create(VpePageContext pageContext, Node sourceNode,
			nsIDOMDocument visualDocument) {
		VpeCreationData creationData = null;
		Element sourceElement = (Element)sourceNode;
		nsIDOMElement mainDiv = visualDocument.createElement(HTML.TAG_DIV);
		String style = sourceElement.getAttribute(HTML.ATTR_STYLE);
		String width = sourceElement.getAttribute(HTML.ATTR_WIDTH);
		String position = sourceElement.getAttribute(RichFaces.ATTR_POSITION);
		if (ComponentUtil.isNotBlank(width)) {
			mainDiv.setAttribute(HTML.ATTR_WIDTH, width);
			style += ";width: " + width + ";"; //$NON-NLS-1$ //$NON-NLS-2$
		}
		if (RichFaces.VALUE_LEFT.equalsIgnoreCase(position)
				|| RichFaces.VALUE_CENTER.equalsIgnoreCase(position)) {
			style += FLOAT_LEFT_STYLE;
		} else if (RichFaces.VALUE_RIGHT.equalsIgnoreCase(position)) {
			style += FLOAT_RIGHT_STYLE;
		} else if (RichFaces.VALUE_BOTTOM.equalsIgnoreCase(position)) {
			nsIDOMElement bottomDiv = visualDocument.createElement(HTML.TAG_DIV);
			bottomDiv.setAttribute(HTML.ATTR_STYLE, "display: block; height: 0; clear: both; visibility: hidden;"); //$NON-NLS-1$
			bottomDiv.appendChild(visualDocument.createTextNode(".")); //$NON-NLS-1$
			mainDiv.appendChild(bottomDiv);
		}
		if (ComponentUtil.isNotBlank(style)) {
			mainDiv.setAttribute(HTML.ATTR_STYLE, style);
		}
		creationData = new VpeCreationData(mainDiv);
		return creationData;
	}

}
