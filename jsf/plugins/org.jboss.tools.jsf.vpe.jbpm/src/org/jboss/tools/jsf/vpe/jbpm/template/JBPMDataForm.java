/*******************************************************************************
 * Copyright (c) 2007-2010 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.jboss.tools.jsf.vpe.jbpm.template;

import java.util.ArrayList;
import java.util.List;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeAbstractTemplate;
import org.jboss.tools.vpe.editor.template.VpeChildrenInfo;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.util.HTML;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMNode;
import org.mozilla.interfaces.nsIDOMNodeList;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * 
 * @author yzhishko
 * 
 */

public class JBPMDataForm extends VpeAbstractTemplate {

	private static final String JBPM_FORM_HEADER_TYPE = "vpe-jbpm-form-header"; //$NON-NLS-1$

	public VpeCreationData create(VpePageContext pageContext, Node sourceNode,
			nsIDOMDocument visualDocument) {
		nsIDOMElement element = visualDocument.createElement(HTML.TAG_TABLE);
		Element sourceElement = (Element) sourceNode;
		element
				.setAttribute(
						HTML.ATTR_STYLE,
						computeBaseTableStyleValue()
								+ (sourceElement.getAttribute(HTML.ATTR_STYLE) == null ? "" : sourceElement.getAttribute(HTML.ATTR_STYLE))); //$NON-NLS-1$
		VpeCreationData creationData = new VpeCreationData(element);
		VpeChildrenInfo childrenInfo = new VpeChildrenInfo(element);
		NodeList children = sourceNode.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node childNode = children.item(i);
			if (childNode instanceof Element) {
				if (childNode.getNodeName().endsWith(":datacell") || childNode.getNodeName().endsWith(":facet")) { //$NON-NLS-1$ //$NON-NLS-2$
					childrenInfo.addSourceChild(childNode);
				}
			}
		}
		creationData.addChildrenInfo(childrenInfo);
		return creationData;
	}

	@Override
	public void validate(VpePageContext pageContext, Node sourceNode,
			nsIDOMDocument visualDocument, VpeCreationData data) {
		nsIDOMNode visualNode = data.getNode();
		nsIDOMElement visualTable = (nsIDOMElement) visualNode
				.queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID);
		nsIDOMElement[] tableRows = getTableRows(visualTable);
		nsIDOMElement formHeader = getDataFormHeader(visualTable);
		while (visualTable.hasChildNodes()) {
			visualTable.removeChild(visualTable.getFirstChild());
		}
		nsIDOMElement colFroupElement = visualDocument
				.createElement(HTML.TAG_COLGROUP);
		colFroupElement.setAttribute(HTML.ATTR_SPAN, "2"); //$NON-NLS-1$
		visualTable.appendChild(colFroupElement);
		nsIDOMElement theadElement = visualDocument
				.createElement(HTML.TAG_THEAD);
		nsIDOMElement trHeadElement = visualDocument.createElement(HTML.TAG_TR);
		theadElement.appendChild(trHeadElement);
		visualTable.appendChild(theadElement);
		if (formHeader != null) {
			trHeadElement.appendChild(formHeader);
		}
		nsIDOMElement tbodyElement = visualDocument
				.createElement(HTML.TAG_TBODY);
		visualTable.appendChild(tbodyElement);
		for (int i = 0; i < tableRows.length; i++) {
			tbodyElement.appendChild(tableRows[i]);
		}
	}

	private nsIDOMElement[] getTableRows(nsIDOMElement tableElement) {
		List<nsIDOMElement> rowElements = new ArrayList<nsIDOMElement>(0);
		nsIDOMNodeList tableList = tableElement
				.getElementsByTagName(HTML.TAG_TR);
		if (tableList != null) {
			for (int i = 0; i < tableList.getLength(); i++) {
				rowElements.add((nsIDOMElement) tableList.item(i)
						.queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID));
			}
		}
		return rowElements.toArray(new nsIDOMElement[0]);
	}

	private nsIDOMElement getDataFormHeader(nsIDOMElement tableElement) {
		nsIDOMNodeList headsList = tableElement
				.getElementsByTagName(HTML.TAG_TH);
		if (headsList == null) {
			return null;
		}
		for (int i = 0; i < headsList.getLength(); i++) {
			nsIDOMElement element = (nsIDOMElement) headsList.item(i)
					.queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID);
			if (JBPM_FORM_HEADER_TYPE.equals(element
					.getAttribute("vpe-pseudo-type"))) { //$NON-NLS-1$
				return element;
			}
		}
		return null;
	}

	private String computeBaseTableStyleValue() {
		StringBuilder builder = new StringBuilder(""); //$NON-NLS-1$
		builder.append("font-family:verdana,sans-serif;"); //$NON-NLS-1$
		builder.append("font-size:10pt;"); //$NON-NLS-1$
		builder.append("font-weight:normal;"); //$NON-NLS-1$
		builder.append("margin-right:auto;"); //$NON-NLS-1$
		builder.append("margin-left:auto;"); //$NON-NLS-1$
		builder.append("border-collapse:collapse;"); //$NON-NLS-1$
		builder.append("border:1px solid #000000"); //$NON-NLS-1$
		return builder.toString();
	}

}
