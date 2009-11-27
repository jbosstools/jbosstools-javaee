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

import org.jboss.tools.jsf.vpe.seam.template.util.SeamUtil;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.util.HTML;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMNode;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class SeamPdfDocumentTemplate extends SeamPdfAbstractTemplate {

	private nsIDOMElement headElement;
	private Element sourceElement;

	@Override
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
		headElement.removeAttribute("style");
		headElement.setAttribute("style", "margin-left:"
				+ Float.toString(marginValues[0]) + ";margin-right:"
				+ Float.toString(marginValues[1]) + ";margin-top:"
				+ Float.toString(marginValues[2]) + ";margin-bottom:"
				+ Float.toString(marginValues[3]) + ";");
		return true;
	}

	private float[] parseMarginValues(String stringMarginValues)
			throws NumberFormatException {
		String[] parts = stringMarginValues.split("\\s");
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
			if (marginsAttrValue != null && !"".equals(marginsAttrValue)) {
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
		Node[] footers = SeamUtil.getChildsByName(pageContext, sourceNode,
				"p:footer");
		nsIDOMNode visualFooter = null;
		if (footers != null && footers.length != 0) {
			visualFooter = pageContext.getDomMapping().getVisualNode(
					footers[footers.length - 1]);

		}
		if (visualFooter != null) {
			for (int i = 0; i < footers.length; i++) {
				nsIDOMNode visualFootersRepresent = pageContext.getDomMapping()
						.getVisualNode(footers[i]);
				if (visualFootersRepresent != null
						&& visualFootersRepresent != visualFooter) {
					nsIDOMNode parentNode = visualFootersRepresent.getParentNode();
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
}
