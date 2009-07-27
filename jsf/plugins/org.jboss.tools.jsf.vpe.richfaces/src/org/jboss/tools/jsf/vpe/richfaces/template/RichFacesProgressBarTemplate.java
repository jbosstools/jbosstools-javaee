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


import java.util.ArrayList;
import java.util.List;

import org.jboss.tools.jsf.vpe.richfaces.ComponentUtil;
import org.jboss.tools.jsf.vpe.richfaces.HtmlComponentUtil;
import org.jboss.tools.jsf.vpe.richfaces.template.util.RichFaces;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeChildrenInfo;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.util.HTML;
import org.jboss.tools.vpe.editor.util.VpeStyleUtil;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * Template for <rich:progressBar/> tag.
 * 
 * @author Eugene Stherbin
 */
public class RichFacesProgressBarTemplate extends AbstractRichFacesTemplate {

    /** The Constant DEFAULT_HEIGHT. */
    private static final String DEFAULT_HEIGHT = " height:13px;"; //$NON-NLS-1$

    /** The Constant CSS_EXTENSION. */
    private static final String CSS_EXTENSION = "progressBar"; //$NON-NLS-1$

    /** The Constant CSS_STYLE. */
    private static final String CSS_STYLE = "/progressBar.css"; //$NON-NLS-1$

    /** The Constant FACET. */
    private static final String FACET = "facet"; //$NON-NLS-1$

    /** The Constant OUTPUT_TEXT. */
    private static final String OUTPUT_TEXT = "outputText"; //$NON-NLS-1$

    /** The Constant PROGRESS_DIV_STYLE_CLASSES. */
    private static final String PROGRESS_DIV_STYLE_CLASSES = "rich-progress-bar-block rich-progress-bar-width rich-progress-bar-shell"; //$NON-NLS-1$

    /** The Constant TEXT_ALIGN_LEFT. */
    private static final String TEXT_ALIGN_LEFT = "; text-align:left;"; //$NON-NLS-1$

    /** The Constant UPLOADED_DIV. */
    private static final String UPLOADED_DIV = "rich-progress-bar-height rich-progress-bar-uploaded null"; //$NON-NLS-1$

    /** The percentage. */
    private String percentage = "60%"; //$NON-NLS-1$

    /** The style. */
    private String style;

    /** The style class. */
    private String styleClass;
    
    private String sourceLabel;

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

        ComponentUtil.setCSSLink(pageContext, getCssStyle(), getCssExtension());
        final Element source = (Element) sourceNode;
        prepareData(source);

        final nsIDOMElement progressDiv = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_DIV);
        final nsIDOMElement uploadDiv = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_DIV);
     
        String clazz = PROGRESS_DIV_STYLE_CLASSES;
        if (ComponentUtil.isNotBlank(this.styleClass)) {
            clazz = clazz + " " + this.styleClass; //$NON-NLS-1$
        }
        progressDiv.setAttribute(HTML.ATTR_CLASS, clazz);
        progressDiv.setAttribute(HTML.ATTR_STYLE, this.style + TEXT_ALIGN_LEFT);
        final List<Node> elements = new ArrayList<Node>();
        final NodeList list = sourceNode.getChildNodes();
        
        for(int i = 0 ; i < list.getLength() ; i ++ ){
            if(list.item(i).getNodeName().equalsIgnoreCase("h:outputText")){ //$NON-NLS-1$
                elements.add(list.item(i));
            }
        }
        
        if(ComponentUtil.isNotBlank(this.sourceLabel) || elements.size() > 0){
            final nsIDOMElement labelDiv = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_DIV);
         //   labelDiv.setAttribute(HTML.ATTR_CLASS, "rich-progress-bar-width rich-progress-bar-remained rich-progress-bar-padding");
            labelDiv.setAttribute(HTML.ATTR_STYLE,this.style+"; font-weight: bold; position: relative; text-align: center; "); //$NON-NLS-1$
            uploadDiv.appendChild(labelDiv);
            if (elements.size() > 0) {
                final StringBuffer sb = new StringBuffer();
                
                for (Node n : elements) {
                    sb.append(ComponentUtil.getAttribute((Element)n, "value")); //$NON-NLS-1$
                }
                labelDiv.appendChild(visualDocument.createTextNode(sb.toString()));
            } else {
                labelDiv.appendChild(visualDocument.createTextNode(this.sourceLabel));
            }
        }
        uploadDiv.setAttribute(HTML.ATTR_CLASS, UPLOADED_DIV);

        uploadDiv.setAttribute(HTML.ATTR_STYLE, this.style + VpeStyleUtil.SEMICOLON_STRING + VpeStyleUtil.PARAMETER_WIDTH
                + VpeStyleUtil.COLON_STRING + this.percentage);
        // rootDiv.appendChild(progressDiv);
        progressDiv.appendChild(uploadDiv);
        List<Node> childrens = ComponentUtil.getChildren(source);
        final VpeCreationData data = new VpeCreationData(progressDiv);
        data.addChildrenInfo(new VpeChildrenInfo(null));
        
        if (childrens.size() > 0) {
            final VpeChildrenInfo info = new VpeChildrenInfo(progressDiv);
            data.addChildrenInfo(info);
            for (Node n : childrens) {
                if (n.getNodeName().indexOf(FACET) > 1 
                		|| n.getNodeName().indexOf(OUTPUT_TEXT) > 1) {
                    info.addSourceChild(n);
                }
            }
        }
//
//        DOMTreeDumper dump = new DOMTreeDumper();
//        dump.dumpToStream(System.err, progressDiv);
        return data;
    }

    /**
     * Gets the css extension.
     * 
     * @return the css extension
     */
    private String getCssExtension() {
        return CSS_EXTENSION;
    }

    /**
     * Gets the css style.
     * 
     * @return the css style
     */
    private String getCssStyle() {
        return getCssExtension() + CSS_STYLE;
    }

    /**
     * Checks if is recreate at attr change.
     * 
     * @param sourceElement the source element
     * @param visualDocument the visual document
     * @param value the value
     * @param visualNode the visual node
     * @param data the data
     * @param pageContext the page context
     * @param name the name
     * 
     * @return true, if is recreate at attr change
     */
    @Override
    public boolean recreateAtAttrChange(VpePageContext pageContext, Element sourceElement, nsIDOMDocument visualDocument,
            nsIDOMElement visualNode, Object data, String name, String value) {
        return true;
    }

    /**
     * Prepare data.
     * 
     * @param source the source
     */
    private void prepareData(Element source) {
        this.styleClass = ComponentUtil.getAttribute(source, RichFaces.ATTR_STYLE_CLASS);
        this.style = ComponentUtil.getAttribute(source, HTML.ATTR_STYLE);
        this.sourceLabel = ComponentUtil.getAttribute(source, "label"); //$NON-NLS-1$
        if (ComponentUtil.isBlank(this.style)) {
            this.style = DEFAULT_HEIGHT;
        }

    }

}
