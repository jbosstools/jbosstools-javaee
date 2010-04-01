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
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.util.HTML;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMNode;
import org.mozilla.interfaces.nsIDOMNodeList;
import org.w3c.dom.Node;

/**
 * 
 * @author yzhishko
 * 
 */

public class JBPMDataCell extends VpeAbstractTemplate {

	private static final String JBPM_CELL_HEADER_TYPE = "vpe-jbpm-cell-header"; //$NON-NLS-1$

	public VpeCreationData create(VpePageContext pageContext, Node sourceNode,
			nsIDOMDocument visualDocument) {
		return new VpeCreationData(visualDocument.createElement(HTML.TAG_TR));
	}

	@Override
	public void validate(VpePageContext pageContext, Node sourceNode,
			nsIDOMDocument visualDocument, VpeCreationData data) {
		nsIDOMElement trElement = (nsIDOMElement) data.getNode()
				.queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID);
		nsIDOMElement cellHeader = getCellHeader(trElement);
		if (cellHeader != null) {
			trElement.removeChild(cellHeader);
		}
		nsIDOMNodeList children = trElement.getChildNodes();
		List<nsIDOMNode> childNodeList = new ArrayList<nsIDOMNode>(0);
		for (int i = 0; i < children.getLength(); i++) {
			childNodeList.add(children.item(i));
		}
		while (trElement.hasChildNodes()) {
			trElement.removeChild(trElement.getFirstChild());
		}
		nsIDOMElement secondColumn = visualDocument.createElement(HTML.TAG_TD);
		secondColumn.setAttribute(HTML.ATTR_STYLE, computeColumnStyleValue());
		if (cellHeader != null) {
			trElement.appendChild(cellHeader);
		}
		trElement.appendChild(secondColumn);
		for (nsIDOMNode nsIDOMNode : childNodeList) {
			secondColumn.appendChild(nsIDOMNode);
		}
	}

	private nsIDOMElement getCellHeader(nsIDOMElement trElement) {
		nsIDOMNodeList headsList = trElement.getElementsByTagName(HTML.TAG_TH);
		if (headsList == null) {
			return null;
		}
		for (int i = 0; i < headsList.getLength(); i++) {
			nsIDOMElement element = (nsIDOMElement) headsList.item(i)
					.queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID);
			if (JBPM_CELL_HEADER_TYPE.equals(element
					.getAttribute("vpe-pseudo-type"))) { //$NON-NLS-1$
				return element;
			}
		}
		return null;
	}

	private String computeColumnStyleValue() {
		StringBuilder builder = new StringBuilder(""); //$NON-NLS-1$
		builder.append("background:none repeat scroll 0 0 #DDDDDD;"); //$NON-NLS-1$
		builder.append("border-bottom:1px solid #000000;"); //$NON-NLS-1$
		builder.append("border-collapse:collapse;"); //$NON-NLS-1$
		builder.append("color:#000000;"); //$NON-NLS-1$
		builder.append("font-size:11px;"); //$NON-NLS-1$
		builder.append("text-align:left;"); //$NON-NLS-1$
		builder.append("text-decoration:none;"); //$NON-NLS-1$
		builder.append("white-space:nowrap;"); //$NON-NLS-1$
		builder.append("width:260px;"); //$NON-NLS-1$
		builder.append("padding:3px 5px;"); //$NON-NLS-1$
		builder.append("margin:0"); //$NON-NLS-1$
		return builder.toString();
	}
}
