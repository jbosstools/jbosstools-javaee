/*******************************************************************************
 * Copyright (c) 2007 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.jsf.vpe.richfaces.template;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.tools.jsf.vpe.richfaces.ComponentUtil;
import org.jboss.tools.vpe.editor.VpeVisualDomBuilder;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeAbstractTemplate;
import org.jboss.tools.vpe.editor.template.VpeChildrenInfo;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.template.VpeToggableTemplate;
import org.jboss.tools.vpe.editor.util.Constants;
import org.jboss.tools.vpe.editor.util.HTML;
import org.jboss.tools.vpe.editor.util.SourceDomUtil;
import org.jboss.tools.vpe.editor.util.VisualDomUtil;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class RichFacesSimpleTogglePanelTemplate extends VpeAbstractTemplate
	implements VpeToggableTemplate {

    private static final String CSS_STYLE_PATH = "simpleTogglePanel/simpleTogglePanel.css"; //$NON-NLS-1$
    private static final String COMPONENT_NAME = "richFacesSimpleTogglePanel"; //$NON-NLS-1$
    private static final String ATTR_LABEL = "label"; //$NON-NLS-1$
    private static final String ATTR_BODY_CLASS = "bodyClass"; //$NON-NLS-1$
    private static final String ATTR_OPENED = "opened"; //$NON-NLS-1$
    private static final String OPEN_MARKER_FACET_NAME = "openMarker"; //$NON-NLS-1$
    private static final String CLOSE_MARKER_FACET_NAME = "closeMarker"; //$NON-NLS-1$
    private static final String CSS_DR_STGLPANEL = "dr-stglpnl"; //$NON-NLS-1$
    private static final String CSS_DR_STGLPANEL_HEADER = "dr-stglpnl-h"; //$NON-NLS-1$
    private static final String CSS_DR_STGLPANEL_BODY = "dr-stglpnl-b"; //$NON-NLS-1$
    private static final String CSS_RICH_STGLPANEL = "rich-stglpanel"; //$NON-NLS-1$
    private static final String CSS_RICH_STGLPANEL_HEADER = "rich-stglpanel-header"; //$NON-NLS-1$
    private static final String CSS_RICH_STGLPNL_MARKER = "rich-stglpnl-marker"; //$NON-NLS-1$
    private static final String CSS_RICH_STGLPANEL_BODY = "rich-stglpanel-body"; //$NON-NLS-1$
    private static final String COLLAPSED_STYLE = "; display: none;"; //$NON-NLS-1$
    private static final String SWITCH_DIV_STYLE = "position : absolute; top: 0px; right: 5px;"; //$NON-NLS-1$

    private static Map toggleMap = new HashMap();
    private nsIDOMElement storedHeaderDiv = null;


    public VpeCreationData create(VpePageContext pageContext, Node sourceNode,
	    nsIDOMDocument visualDocument) {

	Element sourceElement = (Element) sourceNode;

	nsIDOMElement div = visualDocument.createElement(HTML.TAG_DIV);

	VpeCreationData creationData = new VpeCreationData(div);

	ComponentUtil.setCSSLink(pageContext, CSS_STYLE_PATH, COMPONENT_NAME);
	div.setAttribute(HTML.ATTR_CLASS, CSS_DR_STGLPANEL
		+ Constants.WHITE_SPACE
		+ CSS_RICH_STGLPANEL
		+ Constants.WHITE_SPACE
		+ ComponentUtil.getAttribute(sourceElement,
			RichFaces.ATTR_STYLE_CLASS));
	div.setAttribute(HTML.ATTR_STYLE, "width: " //$NON-NLS-1$
		+ ComponentUtil.getAttribute(sourceElement, HTML.ATTR_WIDTH)
		+ Constants.SEMICOLON
		+ ComponentUtil.getAttribute(sourceElement, HTML.ATTR_STYLE));

	// Encode Header
	nsIDOMElement headerDiv = visualDocument.createElement(HTML.TAG_DIV);
	div.appendChild(headerDiv);

	headerDiv.setAttribute(HTML.ATTR_CLASS, CSS_DR_STGLPANEL_HEADER
		+ Constants.WHITE_SPACE
		+ CSS_RICH_STGLPANEL_HEADER
		+ Constants.WHITE_SPACE
		+ ComponentUtil.getAttribute(sourceElement,
			RichFaces.ATTR_HEADER_CLASS));
	headerDiv.setAttribute(HTML.ATTR_STYLE, "position : relative; " //$NON-NLS-1$
		+ ComponentUtil.getHeaderBackgoundImgStyle());

	/*
	 * http://jira.jboss.com/jira/browse/JBIDE-791
	 * https://jira.jboss.org/jira/browse/JBIDE-3373
	 * 
	 * Encode the Header Facet
	 * Find elements from the f:facet 
	 */
	Map<String, List<Node>> headerFacetChildren = null;
	Element headerFacet = SourceDomUtil.getFacetByName(pageContext,
			sourceElement, RichFaces.NAME_FACET_HEADER);
	if (headerFacet != null) {
		headerFacetChildren = VisualDomUtil.findFacetElements(headerFacet, pageContext);
		/*
		 * By adding attribute VPE-FACET to this visual node 
		 * we force JsfFacet to be rendered inside it
		 * without creating an additional and superfluous visual tag.
		 */
		headerDiv.setAttribute(VpeVisualDomBuilder.VPE_FACET, RichFaces.NAME_FACET_HEADER);
		/*
		 * Add header facet to the ChildrenInfo
		 */
		VpeChildrenInfo headerInfo = new VpeChildrenInfo(headerDiv);
	    headerInfo.addSourceChild(headerFacet);
	    creationData.addChildrenInfo(headerInfo);
	} else {
		/*
		 * Otherwise show label attribute value as panel header 
		 */
		headerDiv.appendChild(visualDocument.createTextNode(ComponentUtil
			    .getAttribute(sourceElement, ATTR_LABEL)));
	}

	nsIDOMElement switchDiv = visualDocument.createElement(HTML.TAG_DIV);
	headerDiv.appendChild(switchDiv);
	switchDiv.setAttribute(HTML.ATTR_STYLE, SWITCH_DIV_STYLE);

	String markerName = OPEN_MARKER_FACET_NAME;
	char defaultMarkerCode = 187;
	boolean opened = getActiveState(sourceElement);

	headerDiv.setAttribute(VpeVisualDomBuilder.VPE_USER_TOGGLE_ID,
		(opened ? Constants.FALSE : Constants.TRUE));
	storedHeaderDiv = headerDiv;

	if (opened) {
	    markerName = CLOSE_MARKER_FACET_NAME;
	    defaultMarkerCode = 171;
	}
	Element markerFacet = ComponentUtil.getFacet(sourceElement, markerName);
	if (markerFacet == null) {
	    switchDiv.appendChild(visualDocument.createTextNode(Constants.EMPTY
		    + defaultMarkerCode));
	} else {
	    VpeChildrenInfo switchInfo = new VpeChildrenInfo(switchDiv);
	    switchInfo.addSourceChild(markerFacet);
	    creationData.addChildrenInfo(switchInfo);
	}

	// Encode Body
	// if(opened) {
	nsIDOMElement bodyDiv = visualDocument.createElement(HTML.TAG_DIV);
	div.appendChild(bodyDiv);
	bodyDiv.setAttribute(HTML.ATTR_STYLE, "overflow: hidden; height: " //$NON-NLS-1$
		+ ComponentUtil.getAttribute(sourceElement, HTML.ATTR_HEIGHT)
		+ "; width: 100%;"); //$NON-NLS-1$

	nsIDOMElement table = visualDocument.createElement(HTML.TAG_TABLE);
	bodyDiv.appendChild(table);
	table.setAttribute(HTML.ATTR_CELLPADDING, Constants.ZERO_STRING);
	table.setAttribute(HTML.ATTR_STYLE, "width: 100%"); //$NON-NLS-1$
	nsIDOMElement tr = visualDocument.createElement(HTML.TAG_TR);
	table.appendChild(tr);
	nsIDOMElement td = visualDocument.createElement(HTML.TAG_TD);
	tr.appendChild(td);
	td.setAttribute(HTML.ATTR_CLASS, CSS_DR_STGLPANEL_BODY
		+ Constants.WHITE_SPACE + CSS_RICH_STGLPANEL_BODY
		+ Constants.WHITE_SPACE
		+ ComponentUtil.getAttribute(sourceElement, ATTR_BODY_CLASS));
	
	/*
	 * If there are some odd HTML elements from facet
	 * add them to the panel body first.
	 */
	boolean headerHtmlElementsPresents = ((headerFacetChildren != null) && (headerFacetChildren
			.get(VisualDomUtil.FACET_HTML_TAGS).size() > 0));
	VpeChildrenInfo bodyInfo = new VpeChildrenInfo(td);
	if (headerHtmlElementsPresents) {
			for (Node node : headerFacetChildren.get(VisualDomUtil.FACET_HTML_TAGS)) {
				bodyInfo.addSourceChild(node);
			}
	}
	
	/*
	 * Add the rest panel's content
	 */
	List<Node> children = ComponentUtil.getChildren(sourceElement, true);
	for (Node child : children) {
	    bodyInfo.addSourceChild(child);
	}
	creationData.addChildrenInfo(bodyInfo);

	// http://jira.jboss.com/jira/browse/JBIDE-791
	if (!opened) {
	    String newStyle = bodyDiv.getAttribute(HTML.ATTR_STYLE);
	    newStyle += COLLAPSED_STYLE;
	    bodyDiv.setAttribute(HTML.ATTR_STYLE, newStyle);
	}
	// -------------------------
	// }
	return creationData;
    }

    /**
     * Is invoked after construction of all child nodes of the current visual
     * node.
     * 
     * @param pageContext
     *            Contains the information on edited page.
     * @param sourceNode
     *            The current node of the source tree.
     * @param visualDocument
     *            The document of the visual tree.
     * @param data
     *            Object <code>VpeCreationData</code>, built by a method
     *            <code>create</code>
     */
    public void validate(VpePageContext pageContext, Node sourceNode,
	    nsIDOMDocument visualDocument, VpeCreationData data) {
	super.validate(pageContext, sourceNode, visualDocument, data);
	if (storedHeaderDiv == null)
	    return;
	String value = storedHeaderDiv
		.getAttribute(VpeVisualDomBuilder.VPE_USER_TOGGLE_ID);
	if (Constants.TRUE.equals(value) || Constants.FALSE.equals(value)) {
	    ComponentUtil.applyAttributeValueOnChildren(
		    VpeVisualDomBuilder.VPE_USER_TOGGLE_ID, value,
		    ComponentUtil.getElementChildren(storedHeaderDiv));
	    ComponentUtil.applyAttributeValueOnChildren(
		    VpeVisualDomBuilder.VPE_USER_TOGGLE_LOOKUP_PARENT,
		    Constants.TRUE, ComponentUtil
			    .getElementChildren(storedHeaderDiv));
	}
    }

    private boolean getActiveState(Element sourceElement) {
	String opennedStr;
	opennedStr = (String) toggleMap.get(sourceElement);
	if (opennedStr == null) {
	    opennedStr = ComponentUtil.getAttribute(sourceElement, ATTR_OPENED);
	}
	if (opennedStr == null || Constants.EMPTY.equals(opennedStr)) {
	    opennedStr = Constants.TRUE;
	}
	return (!Constants.FALSE.equals(opennedStr));
    }

    public void toggle(VpeVisualDomBuilder builder, Node sourceNode,
	    String toggleId) {
	toggleMap.put(sourceNode, toggleId);
    }

    public void stopToggling(Node sourceNode) {
	toggleMap.remove(sourceNode);
    }

    public boolean recreateAtAttrChange(VpePageContext pageContext,
	    Element sourceElement, nsIDOMDocument visualDocument,
	    nsIDOMElement visualNode, Object data, String name, String value) {
	return true;
    }
}