/*******************************************************************************
 * Copyright (c) 2007 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.jsf.vpe.jsf.template;

import org.jboss.tools.jsf.vpe.jsf.template.selectitem.AbstractRadioSelectItemTemplate;
import org.jboss.tools.jsf.vpe.jsf.template.util.ComponentUtil;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeAbstractTemplate;
import org.jboss.tools.vpe.editor.template.VpeChildrenInfo;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.util.HTML;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMNode;
import org.mozilla.interfaces.nsIDOMNodeList;
import org.mozilla.xpcom.XPCOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Class for creating selectOneRadio template
 * 
 * @author Dzmitry Sakovich (dsakovich@exadel.com)
 * 
 */

public class JsfSelectOneRadioTemplate extends VpeAbstractTemplate {

    private static final String VAL_PAGE_DIRECTION = "pageDirection"; //$NON-NLS-1$
    private static final String ATTR_LAYOUT = "layout"; //$NON-NLS-1$
    private static final String ATTR_BORDER = "border"; //$NON-NLS-1$
    private static final String ATTR_DISABLED_CLASS = "disabledClass"; //$NON-NLS-1$
    private static final String ATTR_ENABLED_CLASS = "enabledClass"; //$NON-NLS-1$
    private static final String ATTR_STYLE_CLASS = "styleClass"; //$NON-NLS-1$

    private static final String ATTR_DIR_RIGHT_TO_LEFT = "rtl"; //$NON-NLS-1$
    private static final String ATTR_DIR_LEFT_TO_RIGHT = "ltr"; //$NON-NLS-1$
    private static final String ATTR_DISABLED_VALUE = "disabled"; //$NON-NLS-1$

    public JsfSelectOneRadioTemplate() {
	super();

    }

    public VpeCreationData create(VpePageContext pageContext, Node sourceNode,
	    nsIDOMDocument visualDocument) {

	Element sourceElement = (Element) sourceNode;

	boolean layoutHorizontal = true;

	String layout = sourceElement.getAttribute(ATTR_LAYOUT);

	layoutHorizontal = !VAL_PAGE_DIRECTION.equalsIgnoreCase(layout);

	nsIDOMElement div = visualDocument.createElement(HTML.TAG_DIV);
	nsIDOMElement visualTable = visualDocument.createElement(HTML.TAG_TABLE);
	div.appendChild(visualTable);
	VpeCreationData creatorInfo = new VpeCreationData(div);

	visualTable.setAttribute(HTML.ATTR_CLASS, ComponentUtil.getAttribute(
		sourceElement, ATTR_STYLE_CLASS));
	visualTable.setAttribute(HTML.ATTR_STYLE, ComponentUtil.getAttribute(
		sourceElement, HTML.ATTR_STYLE));
	String border = sourceElement.getAttribute(ATTR_BORDER);
	if (border != null)
	    visualTable.setAttribute(ATTR_BORDER, border);

	NodeList children = sourceNode.getChildNodes();
	int count = children != null ? children.getLength() : 0;
	if (count > 0) {
	    Node[] sourceChildren = new Node[count];
	    int childrenCount = 0;
	    for (int i = 0; i < count; i++) {
		Node node = children.item(i);
		int type = node.getNodeType();
		if (type == Node.ELEMENT_NODE || type == Node.TEXT_NODE
			&& node.getNodeValue().trim().length() > 0) {
		    sourceChildren[childrenCount] = node;
		    childrenCount++;
		}
	    }
	    if (childrenCount > 0) {
		int rowCount;
		int rowLength;
		int tableSize = childrenCount;
		if (layoutHorizontal) {
		    rowCount = (childrenCount + tableSize - 1) / tableSize;
		    rowLength = tableSize;
		} else {
		    rowCount = tableSize;
		    rowLength = (childrenCount + tableSize - 1) / tableSize;
		}
		for (int i = 0; i < rowCount; i++) {
		    nsIDOMElement visualRow = visualDocument
			    .createElement(HTML.TAG_TR);
		    for (int j = 0; j < rowLength; j++) {
			nsIDOMElement visualCell = visualDocument
				.createElement(HTML.TAG_TD);
			visualRow.appendChild(visualCell);
			int sourceIndex = layoutHorizontal ? rowLength * i + j
				: rowCount * j + i;
			if (sourceIndex < childrenCount) {
			    VpeChildrenInfo childrenInfo = new VpeChildrenInfo(
				    visualCell);
			    childrenInfo
				    .addSourceChild(sourceChildren[sourceIndex]);
			    creatorInfo.addChildrenInfo(childrenInfo);
			}
		    }
		    visualTable.appendChild(visualRow);
		}
	    }
	}
	return creatorInfo;
    }

    @Override
    public void validate(VpePageContext pageContext, Node sourceNode,
	    nsIDOMDocument visualDocument, VpeCreationData data) {
	if (data.getNode() != null) {
	    applyChildAttributes((Element) sourceNode, data.getNode());
	}
    }

    private void applyChildAttributes(Element sourceElement, nsIDOMNode node) {
	boolean disabled = false;
	try {
	    nsIDOMNodeList list = node.getChildNodes();
	    nsIDOMElement element = (nsIDOMElement) node
		    .queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID);
	    disabled = ComponentUtil.string2boolean(ComponentUtil.getAttribute(
		    sourceElement, HTML.ATTR_DISABLED));
	    if (node.getNodeName().equalsIgnoreCase(HTML.TAG_INPUT)) {
		element
			.setAttribute(
				HTML.ATTR_DIR,
				(ComponentUtil.getAttribute(sourceElement,
					HTML.ATTR_DIR).trim()
					.equalsIgnoreCase(ATTR_DIR_RIGHT_TO_LEFT)) ? ATTR_DIR_RIGHT_TO_LEFT
					: ((ComponentUtil.getAttribute(
						sourceElement, HTML.ATTR_DIR)
						.trim()
						.equalsIgnoreCase(ATTR_DIR_LEFT_TO_RIGHT)) ? ATTR_DIR_LEFT_TO_RIGHT
						: "")); //$NON-NLS-1$
		element.setAttribute(HTML.ATTR_SIZE, ComponentUtil
			.getAttribute(sourceElement, HTML.ATTR_SIZE));
		if (disabled
			|| ComponentUtil
				.string2boolean(ComponentUtil
					.getAttribute(
						element,
						AbstractRadioSelectItemTemplate.ITEM_DISABLED)))
		    element.setAttribute(HTML.ATTR_DISABLED,
			    ATTR_DISABLED_VALUE);

	    }
	    if (node.getNodeName().equalsIgnoreCase(HTML.TAG_LABEL)) {
		element
			.setAttribute(
				HTML.ATTR_CLASS,
				(disabled || ComponentUtil
					.string2boolean(ComponentUtil
						.getAttribute(
							element,
							AbstractRadioSelectItemTemplate.ITEM_DISABLED))) ? ComponentUtil
					.getAttribute(sourceElement,
						ATTR_DISABLED_CLASS)
					: ComponentUtil.getAttribute(
						sourceElement,
						ATTR_ENABLED_CLASS));
	    }

	    if (node.getNodeName().equalsIgnoreCase(HTML.TAG_TABLE)) {
		element.setAttribute(HTML.ATTR_STYLE, ComponentUtil
			.getAttribute(sourceElement, HTML.ATTR_STYLE));
	    }

	    for (int i = 0; i < list.getLength(); i++) {
		applyChildAttributes(sourceElement, list.item(i));
	    }
	} catch (XPCOMException e) {
	    // Ignore
	    return;
	}
    }

}
