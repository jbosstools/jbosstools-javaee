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
import java.util.Map;

import org.jboss.tools.jsf.vpe.richfaces.ComponentUtil;
import org.jboss.tools.jsf.vpe.richfaces.template.util.RichFaces;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeAbstractTemplate;
import org.jboss.tools.vpe.editor.template.VpeChildrenInfo;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.util.Constants;
import org.jboss.tools.vpe.editor.util.HTML;
import org.jboss.tools.vpe.editor.util.SourceDomUtil;
import org.jboss.tools.vpe.editor.util.VisualDomUtil;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


/**
 * Template for <rich:progressBar/> tag.
 * 
 * @author dmaliarevich
 */
public class RichFacesProgressBarTemplate extends VpeAbstractTemplate /*AbstractRichFacesTemplate*/ {

    private static final String CSS_EXTENSION = "progressBar"; //$NON-NLS-1$
    private static final String CSS_PATH = "progressBar/progressBar.css"; //$NON-NLS-1$
    private static final String NBSP = "\u00A0"; //$NON-NLS-1$

    private static final String DEFAULT_HEIGHT = "height: 13px;"; //$NON-NLS-1$
    private static final String DEFAULT_UPLOADED_STATUS = "60%"; //$NON-NLS-1$
    
    private static final String CSS_PB_BLOCK = "rich-progress-bar-block"; //$NON-NLS-1$
    private static final String CSS_PB_SHELL = "rich-progress-bar-shell"; //$NON-NLS-1$
    private static final String CSS_PB_UPLOADED = "rich-progress-bar-uploaded"; //$NON-NLS-1$
    private static final String CSS_PB_HEIGHT = "rich-progress-bar-height"; //$NON-NLS-1$
    private static final String CSS_PB_WIDTH = "rich-progress-bar-width"; //$NON-NLS-1$
    private static final String CSS_PB_SHELL_DIG = "rich-progress-bar-shell-dig"; //$NON-NLS-1$
    private static final String CSS_PB_UPLOADED_DIG = "rich-progress-bar-uploaded-dig"; //$NON-NLS-1$
    private static final String CSS_PB_REMAINED = "rich-progress-bar-remained"; //$NON-NLS-1$
    private static final String CSS_PB_PADDING = "rich-progress-bar-padding"; //$NON-NLS-1$
    private static final String CSS_PB_COMPLETED = "rich-progress-bar-completed"; //$NON-NLS-1$
    private static final String CSS_PB_HEIGHT_DIG = "rich-progress-bar-height-dig"; //$NON-NLS-1$
    private static final String CSS_PB_VPE_TEXT = "rich-progress-bar-vpe-text"; //$NON-NLS-1$
    
    public VpeCreationData create(VpePageContext pageContext, Node sourceNode, nsIDOMDocument visualDocument) {
    	/*
    	 * Add CSS link to the current page
    	 */
    	ComponentUtil.setCSSLink(pageContext, CSS_PATH, CSS_EXTENSION);
    	Element sourceElement = (Element) sourceNode;
    	
    	/*
    	 * Get source element attributes
    	 */
    	String style = ComponentUtil.getAttribute(sourceElement, HTML.ATTR_STYLE);
    	String styleClass = ComponentUtil.getAttribute(sourceElement, RichFaces.ATTR_STYLE_CLASS);
    	String sourceLabel = ComponentUtil.getAttribute(sourceElement, RichFaces.ATTR_LABEL);
    	if (ComponentUtil.isBlank(styleClass)) {
    		styleClass = Constants.EMPTY;
    	}
    	if (ComponentUtil.isBlank(style)) {
    		style = DEFAULT_HEIGHT;
    	}
    	
    	/*
    	 * Create tags
    	 */
    	nsIDOMElement progressDiv = visualDocument.createElement(HTML.TAG_DIV);
    	nsIDOMElement remainDiv = visualDocument.createElement(HTML.TAG_DIV);
    	nsIDOMElement uploadDiv = visualDocument.createElement(HTML.TAG_DIV);
    	nsIDOMElement completeDiv = visualDocument.createElement(HTML.TAG_DIV);
    	nsIDOMElement vpeTextDiv = visualDocument.createElement(HTML.TAG_DIV);

    	/*
    	 * if there are any suitable facets or lable value
    	 * or supplementary HTML tags from facets 
    	 * then progress bar has more divs than usual.
    	 */
    	Element initialFacet = SourceDomUtil.getFacetByName(sourceElement, "initial"); //$NON-NLS-1$
    	Map<String, List<Node>> initialFacetChildren = VisualDomUtil.findFacetElements(initialFacet, pageContext);
    	boolean initialFacetHtmlChildrenPresent = initialFacetChildren
				.get(VisualDomUtil.FACET_HTML_TAGS).size() > 0;

		Element completeFacet = SourceDomUtil.getFacetByName(sourceElement, "complete"); //$NON-NLS-1$
    	Map<String, List<Node>> completeFacetChildren = VisualDomUtil.findFacetElements(completeFacet, pageContext);
    	boolean completeFacetHtmlChildrenPresent = completeFacetChildren
    	.get(VisualDomUtil.FACET_HTML_TAGS).size() > 0;

    	List<Node> children = ComponentUtil.getChildren(sourceElement, true);
		boolean progressBarWithLabel = initialFacetHtmlChildrenPresent
				|| completeFacetHtmlChildrenPresent
				|| (children.size() > 0)
				|| ComponentUtil.isNotBlank(sourceLabel);
    	
    	/*
    	 * Create VpeCreationData 
    	 */
    	VpeCreationData creationData = new VpeCreationData(progressDiv);
//    	VpeChildrenInfo vpeTextInfo = new VpeChildrenInfo(progressDiv);
//		for (Node child : children) {
//			System.out.println("--add child=[" + child.getNodeName() + ", "
//					+ child.getNodeValue() + "]");
//			vpeTextInfo.addSourceChild(child);
//		}
//		creationData.addChildrenInfo(vpeTextInfo);
    	
    	/*
    	 * Filling in the divs
    	 */
    	if (progressBarWithLabel) {
    		progressDiv.setAttribute(HTML.ATTR_CLASS, CSS_PB_BLOCK
					+ Constants.WHITE_SPACE + CSS_PB_WIDTH 
					+ Constants.WHITE_SPACE + CSS_PB_SHELL_DIG
					+ Constants.WHITE_SPACE + styleClass);
			remainDiv.setAttribute(HTML.ATTR_CLASS, CSS_PB_WIDTH
					+ Constants.WHITE_SPACE + CSS_PB_REMAINED 
					+ Constants.WHITE_SPACE + CSS_PB_PADDING);
			uploadDiv.setAttribute(HTML.ATTR_CLASS, CSS_PB_UPLOADED_DIG);
			vpeTextDiv.setAttribute(HTML.ATTR_CLASS, CSS_PB_HEIGHT_DIG
					+ Constants.WHITE_SPACE + CSS_PB_UPLOADED_DIG
					+ Constants.WHITE_SPACE + CSS_PB_VPE_TEXT);
			completeDiv.setAttribute(HTML.ATTR_CLASS, CSS_PB_HEIGHT_DIG
					+ Constants.WHITE_SPACE + CSS_PB_WIDTH
					+ Constants.WHITE_SPACE + CSS_PB_COMPLETED
					+ Constants.WHITE_SPACE + CSS_PB_PADDING);
			/*
			 * Adding facets HTML elements
			 */
			VpeChildrenInfo vpeTextInfo = new VpeChildrenInfo(vpeTextDiv);
			if (initialFacetHtmlChildrenPresent) {
					for (Node node : initialFacetChildren.get(VisualDomUtil.FACET_HTML_TAGS)) {
						vpeTextInfo.addSourceChild(node);
					}
			}
			if (completeFacetHtmlChildrenPresent) {
				for (Node node : completeFacetChildren.get(VisualDomUtil.FACET_HTML_TAGS)) {
					vpeTextInfo.addSourceChild(node);
				}
			}
			
	    	/*
			 * Add the rest bar's content
			 */
			for (Node child : children) {
				vpeTextInfo.addSourceChild(child);
			}
			
			/*
			 * Adding ChildrenInfo to CreationData
			 */
			creationData.addChildrenInfo(vpeTextInfo);
			
			/*
			 * Adding label to the bar's content
			 */
			if (ComponentUtil.isNotBlank(sourceLabel)) {
				vpeTextDiv.appendChild(visualDocument.createTextNode(sourceLabel));
			}
			
			/*
			 * Creating tags structure
			 */
			progressDiv.appendChild(remainDiv);
			progressDiv.appendChild(uploadDiv);
			progressDiv.appendChild(vpeTextDiv);
			uploadDiv.appendChild(completeDiv);
			/*
			 * Add nbsp; for correct div height
			 */
			remainDiv.appendChild(visualDocument.createTextNode(NBSP));
			completeDiv.appendChild(visualDocument.createTextNode(NBSP));
		} else {
			progressDiv.setAttribute(HTML.ATTR_CLASS, CSS_PB_BLOCK
					+ Constants.WHITE_SPACE + CSS_PB_WIDTH 
					+ Constants.WHITE_SPACE + CSS_PB_SHELL
					+ Constants.WHITE_SPACE + styleClass);
			uploadDiv.setAttribute(HTML.ATTR_CLASS, CSS_PB_HEIGHT
					+ Constants.WHITE_SPACE + CSS_PB_UPLOADED);
			/*
			 * Creating tags structure
			 */
			progressDiv.appendChild(uploadDiv);
		}

    	/*
    	 * Adding common styles 
    	 */
    	remainDiv.setAttribute(HTML.ATTR_STYLE, style);
		uploadDiv.setAttribute(HTML.ATTR_STYLE, "width: " //$NON-NLS-1$
				+ DEFAULT_UPLOADED_STATUS + "; " + style); //$NON-NLS-1$
		completeDiv.setAttribute(HTML.ATTR_STYLE, style);
		vpeTextDiv.setAttribute(HTML.ATTR_STYLE, style);
        
        return creationData;
    }

    @Override
    public boolean recreateAtAttrChange(VpePageContext pageContext, Element sourceElement, nsIDOMDocument visualDocument,
            nsIDOMElement visualNode, Object data, String name, String value) {
        return true;
    }

}
