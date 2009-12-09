/******************************************************************************* 
 * Copyright (c) 2007-2009 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.jsf.vpe.seam.template;

/**
 * @author yzhishko
 */

import java.util.ArrayList;
import java.util.List;

import org.jboss.tools.jsf.vpe.seam.template.util.SeamUtil;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.util.HTML;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMNode;
import org.mozilla.interfaces.nsIDOMNodeList;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class SeamPdfDocumentTemplate extends SeamPdfAbstractTemplate {

	private nsIDOMElement headElement;
	private Element sourceElement;

	public VpeCreationData create(VpePageContext pageContext, Node sourceNode,
			nsIDOMDocument visualDocument) {
		nsIDOMNode headNode = visualDocument.createElement(HTML.TAG_DIV);
		headElement = (nsIDOMElement) headNode
				.queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID);
		sourceElement = (Element) sourceNode;
		setMarginValues();
		return new VpeCreationData(headElement);
	}

	private boolean setMarginValues(float... marginValues) {
		if (marginValues.length != 4) {
			return false;
		}
		headElement.removeAttribute("style"); //$NON-NLS-1$
		headElement.setAttribute("style", "margin-left:" //$NON-NLS-1$//$NON-NLS-2$
				+ Float.toString(marginValues[0]) + ";margin-right:" //$NON-NLS-1$
				+ Float.toString(marginValues[1]) + ";margin-top:" //$NON-NLS-1$
				+ Float.toString(marginValues[2]) + ";margin-bottom:" //$NON-NLS-1$
				+ Float.toString(marginValues[3]) + ";"); //$NON-NLS-1$
		return true;
	}

	private float[] parseMarginValues(String stringMarginValues)
			throws NumberFormatException {
		String[] parts = stringMarginValues.split("\\s"); //$NON-NLS-1$
		float[] values = new float[parts.length];
		for (int i = 0; i < parts.length; i++) {
			values[i] = Float.valueOf(parts[i]);
		}
		return values;
	}

	private void setDefaultMargingValues() {
		setMarginValues(36.0f, 36.0f, 36.0f, 36.0f);
	}

	private void setMarginValues() {
		String marginsAttrValue = sourceElement
				.getAttribute(SeamUtil.SEAM_ATTR_MARGINS);
		try {
			if (marginsAttrValue != null && !"".equals(marginsAttrValue)) { //$NON-NLS-1$
				float[] floatMarginValues = parseMarginValues(marginsAttrValue);
				if (!setMarginValues(floatMarginValues)) {
					setDefaultMargingValues();
				}
			} else {
				setDefaultMargingValues();
			}
		} catch (NumberFormatException e) {
			setDefaultMargingValues();
		}
	}

	@Override
	public void validate(VpePageContext pageContext, Node sourceNode,
			nsIDOMDocument visualDocument, VpeCreationData data) {
		setFooter(pageContext, sourceNode, data);
	}

	private void setFooter(VpePageContext pageContext, Node sourceNode,
			VpeCreationData data) {
		nsIDOMNode[] footers = findFootersForVisualDoc((nsIDOMElement) data
				.getNode().queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID));
		int footersLength = footers.length;
		if (footersLength == 0) {
			return;
		}
		nsIDOMNode visualFooter = footers[footersLength-1];
		if (visualFooter != null) {
			for (int i = 0; i < footers.length; i++) {
				nsIDOMNode visualFootersRepresent = footers[i];
				if (visualFootersRepresent != null
						&& visualFootersRepresent != visualFooter) {
					nsIDOMNode parentNode = visualFootersRepresent
							.getParentNode();
					parentNode.removeChild(visualFootersRepresent);
				}
			}
			nsIDOMNode parentNode = visualFooter.getParentNode();
			if (parentNode != null) {
				parentNode.removeChild(visualFooter);
				data.getNode().appendChild(visualFooter);
			}
		}
	}

	private nsIDOMNode[] findFootersForVisualDoc(nsIDOMElement visualElement) {
		nsIDOMNodeList children = visualElement
				.getElementsByTagName(HTML.TAG_DIV);
		List<nsIDOMElement> childrenElements = new ArrayList<nsIDOMElement>();
		for (int i = 0; i < children.getLength(); i++) {
			nsIDOMElement childElement = (nsIDOMElement) children.item(i)
					.queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID);
			String attrType = childElement
					.getAttribute(SeamUtil.SEAM_ATTR_TYPE_ID);
			if (attrType != null
					&& SeamUtil.SEAM_ATTR_TYPE_ID_VALUE_PDF_FOOTER
							.equalsIgnoreCase(attrType)) {
				childrenElements.add(childElement);
			}
		}
		return childrenElements.toArray(new nsIDOMElement[0]);
	}

}
