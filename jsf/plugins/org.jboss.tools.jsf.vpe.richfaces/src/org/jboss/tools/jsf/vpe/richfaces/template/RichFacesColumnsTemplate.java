/*******************************************************************************
 * Copyright (c) 2007-2008 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/


package org.jboss.tools.jsf.vpe.richfaces.template;


import java.util.List;

import org.jboss.tools.jsf.vpe.richfaces.ComponentUtil;
import org.jboss.tools.jsf.vpe.richfaces.template.util.RichFaces;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeChildrenInfo;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.util.Constants;
import org.jboss.tools.vpe.editor.util.HTML;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


/**
 * Template for the <rich:columns/>.
 * 
 * @author Eugene Stherbin
 */
public class RichFacesColumnsTemplate extends AbstractRichFacesTemplate {

    /** The Constant DEFAULT_CLASSES. */
    private static final String DEFAULT_CLASSES = "dr-table-cell rich-table-cell"; //$NON-NLS-1$

    /**
     * Create.
     * 
     * @param visualDocument the visual document
     * @param sourceNode the source node
     * @param pageContext the page context
     * 
     * @return the vpe creation data
     * 
     * @see
     * org.jboss.tools.vpe.editor.template.VpeTemplate#create(org.jboss.tools
     * .vpe.editor.context.VpePageContext, org.w3c.dom.Node,
     * org.mozilla.interfaces.nsIDOMDocument)
     */
    public VpeCreationData create(VpePageContext pageContext, Node sourceNode, nsIDOMDocument visualDocument) {
        final Element sourceElement = (Element) sourceNode;
        final nsIDOMElement td = visualDocument.createElement(HTML.TAG_TD);
        prepareData(sourceElement);
        final VpeCreationData data = new VpeCreationData(td);
        // Create mapping to Encode body
        final VpeChildrenInfo tdInfo = new VpeChildrenInfo(td);
        
        String clazz = DEFAULT_CLASSES;
        if(ComponentUtil.isNotBlank(this.sourceStyleClass)){
            clazz = clazz+Constants.WHITE_SPACE+this.sourceStyleClass;
        }
        if(ComponentUtil.isNotBlank(this.sourceWidth)){
            td.setAttribute(RichFaces.ATTR_WIDTH, this.sourceWidth);
        }
        td.setAttribute(HTML.ATTR_CLASS, clazz);
        td.setAttribute(HTML.ATTR_STYLE, this.sourceStyle);
        
        final List<Node> children = ComponentUtil.getChildren(sourceElement, true);
        for (Node child : children) {
            tdInfo.addSourceChild(child);
        }
        data.addChildrenInfo(tdInfo);
        return data;
    }

    /**
     * @param sourceElement
     */
    private void prepareData(Element sourceElement) {
       this.sourceStyleClass = ComponentUtil.getAttribute(sourceElement, RichFaces.ATTR_STYLE_CLASS);
       this.sourceStyle = ComponentUtil.getAttribute(sourceElement, RichFaces.ATTR_STYLE);
       this.sourceWidth = ComponentUtil.getAttribute(sourceElement, RichFaces.ATTR_WIDTH);
        
    }

    /**
     * Checks if is recreate at attr change.
     * 
     * @param sourceElement the source element
     * @param value the value
     * @param visualDocument the visual document
     * @param visualNode the visual node
     * @param data the data
     * @param pageContext the page context
     * @param name the name
     * 
     * @return true, if is recreate at attr change
     */
    @Override
    public boolean isRecreateAtAttrChange(VpePageContext pageContext, Element sourceElement, nsIDOMDocument visualDocument,
            nsIDOMElement visualNode, Object data, String name, String value) {
        return true;
    }

}
