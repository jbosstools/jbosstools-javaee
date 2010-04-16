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

import org.jboss.tools.jsf.vpe.richfaces.ComponentUtil;
import org.jboss.tools.jsf.vpe.richfaces.template.util.RichFaces;
import org.jboss.tools.vpe.editor.VpeSourceDomBuilder;
import org.jboss.tools.vpe.editor.VpeVisualDomBuilder;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeAbstractTemplate;
import org.jboss.tools.vpe.editor.template.VpeChildrenInfo;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.util.HTML;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMText;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * 
 * @author ezheleznyakov@exadel.com
 * 
 */
public class RichFacesMessageTemplate extends VpeAbstractTemplate {

    protected static String VALIDATION_MESSAGE = "Validation message"; //$NON-NLS-1$
    protected static String ERROR_MESSAGE = "Error message"; //$NON-NLS-1$
    protected static String FATAL_MESSAGE = "Fatal message"; //$NON-NLS-1$
    protected static String INFO_MESSAGE = "Info message"; //$NON-NLS-1$
    protected static String WARNING_MESSAGE = "Warning message"; //$NON-NLS-1$
    protected static String FACET_TAG_NAME = ":facet"; //$NON-NLS-1$

    protected static String[] markers = { "passedMarker", "errorMarker", //$NON-NLS-1$ //$NON-NLS-2$
	    "fatalMarker", "infoMarker", "warnMarker" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

    private final static String MESSAGE_STYLE = "padding-left: 1px;padding-right: 1px;padding-top: 1px;padding-bottom: 1px"; //$NON-NLS-1$

    public VpeCreationData create(VpePageContext pageContext, Node sourceNode,
	    nsIDOMDocument visualDocument) {

	Element sourceElement = (Element) sourceNode;
	final Attributes attrs = new Attributes(sourceElement);

	VpeCreationData creationData;

	HashMap<String, Node> facets = getFacelets(sourceElement);

	if (facets.size() != 0) {
	    creationData = createVisualFacets(visualDocument, sourceElement,
		    facets, attrs);
	} else {
	    nsIDOMElement span = visualDocument.createElement(HTML.TAG_SPAN);

	    if (ComponentUtil.isNotBlank(attrs.getStyleValue())) {
		span.setAttribute(HTML.ATTR_STYLE, attrs.getStyleValue());
	    }
	    if (ComponentUtil.isNotBlank(attrs.getStyleClassValue())) {
		span.setAttribute(HTML.ATTR_CLASS, attrs.getStyleClassValue());
	    }
	    if (ComponentUtil.isNotBlank(attrs.getLabelClassValue())) {
		span.setAttribute(HTML.ATTR_CLASS, attrs.getLabelClassValue());
	    }
	    creationData = new VpeCreationData(span);

	    nsIDOMText passedText = visualDocument
		    .createTextNode(VALIDATION_MESSAGE);
	    span.appendChild(passedText);
	}

	return creationData;
    }

    /**
     * Checks, whether it is necessary to re-create an element at change of
     * attribute
     * 
     * @param pageContext
     *            Contains the information on edited page.
     * @param sourceElement
     *            The current element of the source tree.
     * @param visualDocument
     *            The document of the visual tree.
     * @param visualNode
     *            The current node of the visual tree.
     * @param data
     *            The arbitrary data, built by a method <code>create</code>
     * @param name
     *            Atrribute name
     * @param value
     *            Attribute value
     * @return <code>true</code> if it is required to re-create an element at a
     *         modification of attribute, <code>false</code> otherwise.
     */
    public boolean recreateAtAttrChange(VpePageContext pageContext,
	    Element sourceElement, nsIDOMDocument visualDocument,
	    nsIDOMElement visualNode, Object data, String name, String value) {
	return true;
    }

    /**
     * 
     * @param markerName
     *            Marker name
     * @return True if marker name correct or false
     */
    protected boolean searchInMarker(String markerName) {

	if (markerName == null)
	    return false;

	for (int i = 0; i < markers.length; i++)
	    if (markers[i].equalsIgnoreCase(markerName.trim()))
		return true;
	return false;
    }

    /**
     * Method for creating rich:message template if rich:message has facets
     * 
     * @param visualDocument
     * @param sourceElement
     * @param facets
     * @return
     */
    private VpeCreationData createVisualFacets(nsIDOMDocument visualDocument,
	    Element sourceElement, HashMap<String, Node> facets,
	    Attributes attrs) {

	nsIDOMElement tableHeader = visualDocument
		.createElement(HTML.TAG_TABLE);
	tableHeader.setAttribute(HTML.ATTR_STYLE, MESSAGE_STYLE);

	VpeCreationData creationData = new VpeCreationData(tableHeader);

	nsIDOMElement tbody = visualDocument.createElement(HTML.TAG_TBODY);
	tbody.setAttribute(HTML.ATTR_VALIGN, "top"); //$NON-NLS-1$
	tableHeader.appendChild(tbody);

	nsIDOMElement tr = visualDocument.createElement(HTML.TAG_TR);

	tbody.appendChild(tr);

	if (ComponentUtil.isNotBlank(attrs.getStyleValue())) {
	    tableHeader.setAttribute(HTML.ATTR_STYLE, attrs.getStyleValue());
	}

	if (ComponentUtil.isNotBlank(attrs.getStyleClassValue())) {
	    tableHeader.setAttribute(HTML.ATTR_CLASS, attrs
		    .getStyleClassValue());
	}
	for (int i = 0; i < markers.length; i++) {

	    if (facets.containsKey(markers[i])) {

		nsIDOMElement td = visualDocument.createElement(HTML.TAG_TD);

		switch (i) {
		case 0: // passed

		    if (ComponentUtil.isNotBlank(attrs.getMarkerClassValue())) {
			td.setAttribute(HTML.ATTR_CLASS, attrs
				.getMarkerClassValue());
		    }
		    if (ComponentUtil.isNotBlank(attrs.getMarkerStyleValue())) {
			td.setAttribute(HTML.ATTR_STYLE, attrs
				.getMarkerStyleValue());
		    }
		    break;
		case 1: // error
		    if (ComponentUtil.isNotBlank(attrs.getErrorClassValue())) {
			td.setAttribute(HTML.ATTR_CLASS, attrs
				.getErrorClassValue());
		    }
		    if (ComponentUtil.isNotBlank(attrs
			    .getErrorMarkerClassValue())) {
			td.setAttribute(HTML.ATTR_CLASS, attrs
				.getErrorMarkerClassValue());
		    }
		    break;
		case 2: // fatal
		    if (ComponentUtil.isNotBlank(attrs.getFatalClassValue())) {
			td.setAttribute(HTML.ATTR_CLASS, attrs
				.getFatalClassValue());
		    }
		    if (ComponentUtil.isNotBlank(attrs
			    .getFatalMarkerClassValue())) {
			td.setAttribute(HTML.ATTR_CLASS, attrs
				.getFatalMarkerClassValue());
		    }
		    break;
		case 3: // info
		    if (ComponentUtil.isNotBlank(attrs.getInfoClassValue())) {
			td.setAttribute(HTML.ATTR_CLASS, attrs
				.getInfoClassValue());
		    }
		    if (ComponentUtil.isNotBlank(attrs
			    .getInfoMarkerClassValue())) {
			td.setAttribute(HTML.ATTR_CLASS, attrs
				.getInfoMarkerClassValue());
		    }
		    break;
		case 4: // warn
		    if (ComponentUtil.isNotBlank(attrs.getWarnClassValue())) {
			td.setAttribute(HTML.ATTR_CLASS, attrs
				.getWarnClassValue());
		    }
		    if (ComponentUtil.isNotBlank(attrs
			    .getWarnMarkerClassValue())) {
			td.setAttribute(HTML.ATTR_CLASS, attrs
				.getWarnMarkerClassValue());
		    }
		    break;
		default:
		    break;
		}

		VpeChildrenInfo childrenInfo = new VpeChildrenInfo(td);
		creationData.addChildrenInfo(childrenInfo);

		if (!(facets.get(markers[i]) instanceof Element))
		    continue;
		else {
			td.setAttribute(VpeVisualDomBuilder.VPE_FACET, markers[i]);
		    childrenInfo.addSourceChild(facets.get(markers[i]));
		}
		tr.appendChild(td);
	    }
	}

	nsIDOMElement td1 = visualDocument.createElement(HTML.TAG_TD);

	if (ComponentUtil.isNotBlank(attrs.getLabelClassValue())) {
	    td1.setAttribute(HTML.ATTR_CLASS, attrs.getLabelClassValue());
	}
	nsIDOMText passedText = visualDocument
		.createTextNode(VALIDATION_MESSAGE);
	tr.appendChild(td1);
	td1.appendChild(passedText);

	return creationData;
    }

    @Override
    public void setSourceAttributeSelection(VpePageContext pageContext,
	    Element sourceElement, int offset, int length, Object data) {
	VpeSourceDomBuilder sourceBuilder = pageContext.getSourceBuilder();
	sourceBuilder.setSelection(sourceElement, 0, 0);
    }

    /**
     * Method for getting message facets
     * 
     * @param sourceElement
     * @return List of facets
     */
    protected HashMap<String, Node> getFacelets(Element sourceElement) {

	NodeList nodeList = sourceElement.getChildNodes();
	HashMap<String, Node> facets = new HashMap<String, Node>();
	for (int i = 0; i < nodeList.getLength(); i++) {
	    if (!(nodeList.item(i) instanceof Element)){
	    	continue;
	    }
	    if (nodeList.item(i).getNodeName().endsWith(FACET_TAG_NAME)
		    && searchInMarker(((Element) nodeList.item(i))
			    .getAttribute(RichFaces.ATTR_NAME))) {
		facets.put(((Element) nodeList.item(i))
			.getAttribute(RichFaces.ATTR_NAME), nodeList.item(i));
	    }
	}

	return facets;
    }

    class Attributes {

	private String PASSED_LABEL_ATTRIBUTE_NAME = "passedLabel"; //$NON-NLS-1$
	private String LABEL_CLASS_ATTRIBUTE_NAME = "labelClass"; //$NON-NLS-1$
	private String MARKER_CLASS_ATTRIBUTE_NAME = "markerClass"; //$NON-NLS-1$
	private String MARKER_STYLE_ATTRIBUTE_NAME = "markerStyle"; //$NON-NLS-1$

	private String ERROR_MARKER_CLASS_ATTRIBUTE_NAME = "errorMarkerClass"; //$NON-NLS-1$
	private String ERROR_LABEL_CLASS_ATTRIBUTE_NAME = "errorLabelClass"; //$NON-NLS-1$
	private String ERROR_CLASS_ATTRIBUTE_NAME = "errorClass"; //$NON-NLS-1$

	private String FATAL_MARKER_CLASS_ATTRIBUTE_NAME = "fatalMarkerClass"; //$NON-NLS-1$
	private String FATAL_LABEL_CLASS_ATTRIBUTE_NAME = "fatalLabelClass"; //$NON-NLS-1$
	private String FATAL_CLASS_ATTRIBUTE_NAME = "fatalClass"; //$NON-NLS-1$

	private String INFO_MARKER_CLASS_ATTRIBUTE_NAME = "infoMarkerClass"; //$NON-NLS-1$
	private String INFO_LABEL_CLASS_ATTRIBUTE_NAME = "infoLabelClass"; //$NON-NLS-1$
	private String INFO_CLASS_ATTRIBUTE_NAME = "infoClass"; //$NON-NLS-1$

	private String WARN_MARKER_CLASS_ATTRIBUTE_NAME = "warnMarkerClass"; //$NON-NLS-1$
	private String WARN_LABEL_CLASS_ATTRIBUTE_NAME = "warnLabelClass"; //$NON-NLS-1$
	private String WARN_CLASS_ATTRIBUTE_NAME = "warnClass"; //$NON-NLS-1$

	private String passedLabelValue;
	private String labelClassValue;
	private String markerClassValue;
	private String markerStyleValue;
	private String errorMarkerClassValue;
	private String errorLabelClassValue;
	private String errorClassValue;
	private String fatalMarkerClassValue;
	private String fatalLabelClassValue;
	private String fatalClassValue;
	private String infoMarkerClassValue;
	private String infoLabelClassValue;
	private String infoClassValue;
	private String warnMarkerClassValue;
	private String warnLabelClassValue;
	private String warnClassValue;
	private String styleValue;
	private String styleClassValue;

	public Attributes(final Element sourceElement) {
	    passedLabelValue = sourceElement
		    .getAttribute(PASSED_LABEL_ATTRIBUTE_NAME);
	    labelClassValue = sourceElement
		    .getAttribute(LABEL_CLASS_ATTRIBUTE_NAME);
	    markerClassValue = sourceElement
		    .getAttribute(MARKER_CLASS_ATTRIBUTE_NAME);
	    markerStyleValue = sourceElement
		    .getAttribute(MARKER_STYLE_ATTRIBUTE_NAME);

	    errorMarkerClassValue = sourceElement
		    .getAttribute(ERROR_MARKER_CLASS_ATTRIBUTE_NAME);
	    errorLabelClassValue = sourceElement
		    .getAttribute(ERROR_LABEL_CLASS_ATTRIBUTE_NAME);
	    errorClassValue = sourceElement
		    .getAttribute(ERROR_CLASS_ATTRIBUTE_NAME);

	    fatalMarkerClassValue = sourceElement
		    .getAttribute(FATAL_MARKER_CLASS_ATTRIBUTE_NAME);
	    fatalLabelClassValue = sourceElement
		    .getAttribute(FATAL_LABEL_CLASS_ATTRIBUTE_NAME);
	    fatalClassValue = sourceElement
		    .getAttribute(FATAL_CLASS_ATTRIBUTE_NAME);

	    infoMarkerClassValue = sourceElement
		    .getAttribute(INFO_MARKER_CLASS_ATTRIBUTE_NAME);
	    infoLabelClassValue = sourceElement
		    .getAttribute(INFO_LABEL_CLASS_ATTRIBUTE_NAME);
	    infoClassValue = sourceElement
		    .getAttribute(INFO_CLASS_ATTRIBUTE_NAME);

	    warnMarkerClassValue = sourceElement
		    .getAttribute(WARN_MARKER_CLASS_ATTRIBUTE_NAME);
	    warnLabelClassValue = sourceElement
		    .getAttribute(WARN_LABEL_CLASS_ATTRIBUTE_NAME);
	    warnClassValue = sourceElement
		    .getAttribute(WARN_CLASS_ATTRIBUTE_NAME);

	    styleValue = sourceElement.getAttribute(HTML.ATTR_STYLE);
	    styleClassValue = sourceElement
		    .getAttribute(RichFaces.ATTR_STYLE_CLASS);

	}

	public String getErrorMarkerClassValue() {
	    return errorMarkerClassValue;
	}

	public String getErrorLabelClassValue() {
	    return errorLabelClassValue;
	}

	public String getErrorClassValue() {
	    return errorClassValue;
	}

	public String getPassedLabelValue() {
	    return passedLabelValue;
	}

	public String getLabelClassValue() {
	    return labelClassValue;
	}

	public String getMarkerClassValue() {
	    return markerClassValue;
	}

	public String getMarkerStyleValue() {
	    return markerStyleValue;
	}

	public String getFatalMarkerClassValue() {
	    return fatalMarkerClassValue;
	}

	public String getFatalLabelClassValue() {
	    return fatalLabelClassValue;
	}

	public String getFatalClassValue() {
	    return fatalClassValue;
	}

	public String getInfoMarkerClassValue() {
	    return infoMarkerClassValue;
	}

	public String getInfoLabelClassValue() {
	    return infoLabelClassValue;
	}

	public String getInfoClassValue() {
	    return infoClassValue;
	}

	public String getWarnMarkerClassValue() {
	    return warnMarkerClassValue;
	}

	public String getWarnLabelClassValue() {
	    return warnLabelClassValue;
	}

	public String getWarnClassValue() {
	    return warnClassValue;
	}

	public String getStyleValue() {
	    return styleValue;
	}

	public String getStyleClassValue() {
	    return styleClassValue;
	}

    }
}
