package org.jboss.tools.jsf.vpe.seam.template;

import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.template.VpeDefineContainerTemplate;
import org.mozilla.interfaces.nsIDOMDocument;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class SeamDecorateTemplate extends VpeDefineContainerTemplate {

    private final String ATTR_TEMPLATE = "template"; //$NON-NLS-1$
    
    public VpeCreationData create(VpePageContext pageContext, Node sourceNode,
	    nsIDOMDocument visualDocument) {
	String fileName = ((Element) sourceNode).getAttribute(ATTR_TEMPLATE);
	return createTemplate(fileName, pageContext, sourceNode, visualDocument);
    }

}
