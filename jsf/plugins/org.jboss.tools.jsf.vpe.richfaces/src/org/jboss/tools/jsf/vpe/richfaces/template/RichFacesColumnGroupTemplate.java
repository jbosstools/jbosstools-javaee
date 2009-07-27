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

import org.jboss.tools.jsf.vpe.richfaces.ComponentUtil;
import org.jboss.tools.jsf.vpe.richfaces.template.util.RichFaces;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class RichFacesColumnGroupTemplate extends RichFacesSubTableTemplate {

	/** @deprecated no one another template should know about this template */
	public static final RichFacesColumnGroupTemplate DEFAULT_INSTANCE = new RichFacesColumnGroupTemplate();
	private static String styleClass;

	public RichFacesColumnGroupTemplate() {
		super();
	}
	
	/** @see RichFacesSubTableTemplate#encode(VpePageContext, VpeCreationData, Element, nsIDOMDocument, nsIDOMElement) */
	@Override
	public VpeCreationData encode(VpePageContext pageContext,
			VpeCreationData creationData, Element sourceElement,
			nsIDOMDocument visualDocument, nsIDOMElement parentVisualNode) {		
		styleClass = sourceElement.getAttribute(RichFaces.ATTR_STYLE_CLASS);
		
		return super.encode(pageContext, creationData, sourceElement, visualDocument,
				parentVisualNode);
	}



	@Override
	protected String getHeaderClass() {
		return "dr-table-header rich-table-header"; //$NON-NLS-1$
	}

	@Override
	protected String getHeaderContinueClass() {
		return "dr-table-header-continue rich-table-header-continue"; //$NON-NLS-1$
	}

	@Override
	protected String getFooterClass() {
		return "dr-table-footer rich-table-footer"; //$NON-NLS-1$
	}

	@Override
	protected String getFooterContinueClass() {
		return "dr-table-footer-continue rich-table-footer-continue"; //$NON-NLS-1$
	}

	@Override
	protected String getHeaderBackgoundImgStyle() {
		return ComponentUtil.getHeaderBackgoundImgStyle();
	}

	/** @see RichFacesSubTableTemplate#encodeFooter(VpePageContext, VpeCreationData, Element, nsIDOMDocument, nsIDOMElement)*/
	@Override
	protected void encodeFooter(final VpePageContext pageContext,
			final VpeCreationData creationData, final Element sourceElement,
			final nsIDOMDocument visualDocument, final nsIDOMElement parentVisualNode) {
		// do nothing, because the tag do not support footers
	}

	/** @see RichFacesSubTableTemplate#encodeHeader(VpePageContext, VpeCreationData, Element, nsIDOMDocument, nsIDOMElement) */
	@Override
	protected void encodeHeader(final VpePageContext pageContext,
			final VpeCreationData creationData, final Element sourceElement,
			final nsIDOMDocument visualDocument, final nsIDOMElement parentVisualNode) {
		// do nothing, because the tag do not support headers
	}

	/** @see RichFacesSubTableTemplate#encodeHeaderOrFooter(VpePageContext, VpeCreationData, Element, nsIDOMDocument, nsIDOMElement, String, String, String) */
	@Override
	protected void encodeHeaderOrFooter(final VpePageContext pageContext,
			final VpeCreationData creationData, final Element sourceElement,
			final nsIDOMDocument visualDocument, final nsIDOMElement parentVisualNode,
			final String facetName, final String trClass, final String tdClass) {
		// do nothing, because the tag do not support headers and footers
	}

	/** @see RichFacesSubTableTemplate#getRowClass(int) */
	@Override
	protected String getRowClass(int row) {
		String rowClass = super.getRowClass(row);
		if (styleClass != null) {
			rowClass += ' ' + styleClass;
		}
		return rowClass;
	}
	
}
