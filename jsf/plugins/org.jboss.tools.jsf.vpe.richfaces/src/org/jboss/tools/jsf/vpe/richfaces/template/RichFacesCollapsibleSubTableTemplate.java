/*******************************************************************************
 * Copyright (c) 2007-2011 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.jsf.vpe.richfaces.template;

import org.jboss.tools.jsf.vpe.richfaces.template.util.RichFaces;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeAbstractTemplate;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.util.HTML;
import org.jboss.tools.vpe.editor.util.VisualDomUtil;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class RichFacesCollapsibleSubTableTemplate extends VpeAbstractTemplate {
	
	private static final String CST_TBODY_CLASS = "rf-cst"; //$NON-NLS-1$
	private static final String CST_ROW_CLASS = "rf-cst-r"; //$NON-NLS-1$
	private static final String CST_FIRST_RAW_CLASS = "rf-cst-fst-r"; //$NON-NLS-1$
	private static final String CST_CELL_CLASS = "rf-cst-c"; //$NON-NLS-1$
	private static final String CST_HEADER_CLASS = "rf-cst-hdr"; //$NON-NLS-1$
	private static final String CST_FIRST_HEADER_CLASS = "rf-cst-hdr-fst"; //$NON-NLS-1$
	private static final String CST_FIRST_HEADER_ROW_CLASS = "rf-cst-hdr-fst-r"; //$NON-NLS-1$
	private static final String CST_HEADER_CELL_CLASS = "rf-cst-hdr-c"; //$NON-NLS-1$
	private static final String CST_SUBHEADER_CLASS = "rf-cst-shdr"; //$NON-NLS-1$
	private static final String CST_SUBHEADER_CELL_CLASS = "rf-cst-shdr-c"; //$NON-NLS-1$
	private static final String CST_FOOTER_CLASS = "rf-cst-ftr"; //$NON-NLS-1$
	private static final String CST_FIRST_FOOTER_CLASS = "rf-cst-ftr-fst"; //$NON-NLS-1$
	private static final String CST_FOOTER_CELL_CLASS = "rf-cst-ftr-c"; //$NON-NLS-1$
	private static final String CST_SUBFOOTER_CLASS = "rf-cst-sftr"; //$NON-NLS-1$
	private static final String CST_FIRST_SUBFOOTER_CLASS = "rf-cst-sftr-fst"; //$NON-NLS-1$
	private static final String CST_SUBFOOTER_CELL_CLASS = "rf-cst-sftr-c"; //$NON-NLS-1$
	
	public VpeCreationData create(final VpePageContext pageContext,
			final Node sourceNode, final nsIDOMDocument visualDocument) {
		final Element sourceElement = (Element) sourceNode;
		final nsIDOMElement tbody = visualDocument.createElement(HTML.TAG_TBODY);
		/*
		 * Copy attributes from the source node to visual node.
		 */
		VisualDomUtil.copyAttributes(sourceNode, tbody);
		/*
		 * Add collapsible style property
		 */
		String displayStyle = "display: table-row-group;"; //$NON-NLS-1$
		if (RichFaces.readCollapsedStateFromSourceNode(sourceNode)) {
			displayStyle = "display: none;"; //$NON-NLS-1$
		}
		/*
		 * Get previous style attribute value
		 */
		String style = null;
		if (sourceElement.hasAttribute(HTML.ATTR_STYLE)) {
			style = sourceElement.getAttribute(HTML.ATTR_STYLE);
		}
		style += displayStyle;
		/*
		 * Re-set style to the visual node
		 */
		tbody.setAttribute(HTML.ATTR_STYLE, style);
		tbody.setAttribute(HTML.ATTR_CLASS, CST_TBODY_CLASS);
		/*
		 * Create VpeCreationData for this visual node.
		 * Initialize ChildrenEncoder.
		 */
		VpeCreationData creationData = new VpeCreationData(tbody);
		RichFacesDataTableChildrenEncoder childrenEncoder = new RichFacesDataTableChildrenEncoder(
				creationData, visualDocument, sourceElement, tbody);
		childrenEncoder.setRowClasses(CST_FIRST_RAW_CLASS, CST_ROW_CLASS);
		/*
		 * Encode header
		 */
		childrenEncoder.encodeTableHeader(
				pageContext, tbody, null, 
				HTML.TAG_TD, RichFaces.NAME_FACET_HEADER, CST_HEADER_CLASS, 
				null, null, null, CST_HEADER_CELL_CLASS);
		/*
		 * Encode children
		 */
		childrenEncoder.encodeChildren();
		/*
		 * Encode footer
		 */
		childrenEncoder.encodeTableFooter(
				pageContext, tbody, null, 
				HTML.TAG_TD, RichFaces.NAME_FACET_FOOTER, CST_FOOTER_CLASS, 
				null, null, null, CST_FOOTER_CELL_CLASS);
		
		return creationData;
	}

	@Override
	public void validate(VpePageContext pageContext, Node sourceNode,
			nsIDOMDocument visualDocument, VpeCreationData creationData) {
		RichFacesDataTableChildrenEncoder.validateChildren(
				pageContext, sourceNode, visualDocument, creationData);
		/*
		 * Apply css styles to the generated visual node.
		 */
		final RichFacesDataTableStyleClassesApplier styleClassesApplier = 
			new RichFacesDataTableStyleClassesApplier(visualDocument, pageContext, sourceNode);
		styleClassesApplier.applyClasses((nsIDOMElement) creationData.getNode());
	}
	
	
	/**
	 * Method that is responsible for collapsing/expanding subtable
	 * 
	 * @param sourceNode source node for the subtable
	 * @param toggleId the id
	 */
	public void toggle(Node sourceNode, String toggleId) {
		/*
		 * Collapsed state will be changed.
		 * Then the whole template should be rebuilt to apply changes.
		 */
		if (RichFaces.readCollapsedStateFromSourceNode(sourceNode)) {
			sourceNode.setUserData(RichFaces.COLLAPSED_STATE, "false", null); //$NON-NLS-1$
		} else {
			sourceNode.setUserData(RichFaces.COLLAPSED_STATE, "true", null); //$NON-NLS-1$
		}
	}

}
