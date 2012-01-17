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

import java.util.ArrayList;
import java.util.List;

import org.jboss.tools.jsf.vpe.richfaces.ComponentUtil;
import org.jboss.tools.jsf.vpe.richfaces.RichFacesTemplatesActivator;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeAbstractTemplate;
import org.jboss.tools.vpe.editor.template.VpeChildrenInfo;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.template.expression.VpeExpression;
import org.jboss.tools.vpe.editor.template.expression.VpeExpressionException;
import org.jboss.tools.vpe.editor.util.HTML;
import org.jboss.tools.vpe.editor.util.VisualDomUtil;
import org.jboss.tools.vpe.editor.util.VpeClassUtil;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMNode;
import org.mozilla.interfaces.nsIDOMNodeList;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class RichFacesSubTableTemplate extends VpeAbstractTemplate {

	private static final String DEAFAULT_CELL_CLASS = "dr-subtable-cell"; //$NON-NLS-1$
	private static List<String> rowClasses;
	private static List<String> columnClasses;
	
	private static final String ST_HEADER_CLASS = "dr-subtable-header rich-subtable-header";
	private static final String ST_HEADER_CONTINUE_CLASS = "dr-subtable-header-continue rich-subtable-header-continue";
	private static final String ST_FOOTER_CLASS = "dr-subtable-footer rich-subtable-footer";
	private static final String ST_FOOTER_CONTINUE_CLASS = "dr-subtable-footer-continue rich-subtable-footer-continue";
	
	/** @deprecated no one another template should know about this template */
	public static final RichFacesSubTableTemplate DEFAULT_INSTANCE = new RichFacesSubTableTemplate();

	public RichFacesSubTableTemplate() {
		super();
	}
	
	public VpeCreationData encodeSubTable(final VpePageContext pageContext,
			VpeCreationData creationData, final Element sourceElement,
			final nsIDOMDocument visualDocument, nsIDOMElement parentVisualNode) {
		if(creationData!=null) {
			/*
			 * Encode header
			 */
			encodeHeader(pageContext, creationData, sourceElement, visualDocument, parentVisualNode);
		}

		initClasses(sourceElement, null);

		nsIDOMElement curTr = visualDocument.createElement(HTML.TAG_TR);
		if (parentVisualNode == null) {
			parentVisualNode = curTr;
		}
		VisualDomUtil.copyAttributes(sourceElement, curTr);

		boolean header = false;
		boolean footer = false;
		int curRow = 0;
		int curColumn = 0;

		if(isHeader(sourceElement)) {
			curTr.setAttribute(HTML.ATTR_CLASS, getHeaderClass());
			final String style = getHeaderBackgoundImgStyle();
			if(style!=null) {
				curTr.setAttribute(HTML.ATTR_STYLE, style);
			}
			header = true;
		} else if(isFooter(sourceElement)) {
			curTr.setAttribute(HTML.ATTR_CLASS, getFooterClass());
			footer = true;
		} else {
			curTr.setAttribute(HTML.ATTR_CLASS, getRowClass(curRow));
		}

		if(creationData==null) {
			// Method was called from create()
			creationData = new VpeCreationData(curTr);
		} else {
			// Method was called from dataTable
			parentVisualNode.appendChild(curTr);
		}
		
		creationData.addChildrenInfo(new VpeChildrenInfo(null));

		// Create mapping to Encode body
		VpeChildrenInfo trChildrenInfo = new VpeChildrenInfo(curTr);
		
		final List<Node> children = ComponentUtil.getChildren(sourceElement);
		for (final Node child : children) {
			if (child.getNodeType() == Node.ELEMENT_NODE) {
				String nodeName = child.getNodeName();
				if (nodeName.endsWith(':' + RichFaces.TAG_COLUMN) ||
						nodeName.endsWith(':' + RichFaces.TAG_COLUMNS)) {
					if (RichFacesColumnTemplate.isBreakBefore(child)) {
						curRow++;
						curColumn = 0;
						curTr = visualDocument.createElement(HTML.TAG_TR);
						VisualDomUtil.copyAttributes(sourceElement, curTr);
	
						if (header) {
							curTr.setAttribute(HTML.ATTR_CLASS, getHeaderContinueClass());
						} else if(footer) {
							curTr.setAttribute(HTML.ATTR_CLASS, getFooterContinueClass());
						} else {
							curTr.setAttribute(HTML.ATTR_CLASS, getRowClass(curRow));
						}
	
						parentVisualNode.appendChild(curTr);
						trChildrenInfo = new VpeChildrenInfo(curTr);
						creationData.addChildrenInfo(trChildrenInfo);
					}
	
					final VpeChildrenInfo innerTdChildrenInfo = new VpeChildrenInfo(curTr);
					creationData.addChildrenInfo(innerTdChildrenInfo);
					innerTdChildrenInfo.addSourceChild(child);
					curColumn++;
				} else {
					trChildrenInfo.addSourceChild(child);
				}
			}
		}


		if(parentVisualNode!=null) {
			// Encode footer
			encodeFooter(pageContext, creationData, sourceElement,
					visualDocument, parentVisualNode);
		}
		return creationData;
	}
	

	/** Adds necessary attributes to its children.*/
	@Override
	public void validate(final VpePageContext pageContext, final Node sourceNode,
			final nsIDOMDocument visualDocument, final VpeCreationData creationData) {
		initClasses(sourceNode, pageContext);
		
		nsIDOMNode visualNode = creationData.getNode();
		/*
		 * https://jira.jboss.org/jira/browse/JBIDE-4311
		 * Tag name can be in any case.
		 */
		if (visualNode != null && visualNode.getNodeName().equalsIgnoreCase(HTML.TAG_TBODY)) {
			// we are called by VpeVisualDomBuilder			
			addStylesToCells(visualDocument, visualNode);
		} else {
			RuntimeException e = new RuntimeException("This is probably a bug. The main tag of subTable shuld be 'TBODY'.");//$NON-NLS-1$
			RichFacesTemplatesActivator.getPluginLog().logError(e);
		}
	}
	
	/**
	 * Adds HTML style classes names to all TDs from the <code>rowsContainer</code>
	 * according to <code>columnClasses</code> attribute of the tag.
	 */
	private void addStylesToCells(nsIDOMDocument visualDocument, nsIDOMNode rowsContainer) {
		nsIDOMNodeList rowsContainerChildren = rowsContainer.getChildNodes();
		for (int j = 0; j < rowsContainerChildren.getLength(); j++) {
			nsIDOMNode tBodyChild = rowsContainerChildren.item(j);
			if (tBodyChild.getNodeType() == nsIDOMNode.ELEMENT_NODE && HTML.TAG_TR.equalsIgnoreCase(tBodyChild.getNodeName())) {
				nsIDOMNodeList rowChildren = tBodyChild.getChildNodes();
				int column = 0;
				for (int i = 0; i < rowChildren.getLength(); i++) {
					final nsIDOMNode visualChild = rowChildren.item(i);
					if ( visualChild.getNodeType() == nsIDOMNode.ELEMENT_NODE && HTML.TAG_TD.equalsIgnoreCase(visualChild.getNodeName()) ) {
						final nsIDOMNode tableCell = visualChild;
						nsIDOMNode columnStyle = tableCell.getAttributes().getNamedItem(HTML.ATTR_CLASS);
						if (columnStyle == null) {
							columnStyle = visualDocument.createAttribute(HTML.ATTR_CLASS);
						}
						columnStyle.setNodeValue(columnStyle.getNodeValue() + HTML.VALUE_CLASS_DELIMITER
								+ getColumnClass(column));
						column++;
					}
				}
			}
		}
	}

	public VpeCreationData create(final VpePageContext pageContext, final Node sourceNode, final nsIDOMDocument visualDocument) {
		final Element sourceElement = (Element)sourceNode;
		final nsIDOMElement tbody = visualDocument.createElement(HTML.TAG_TBODY);
		VpeCreationData creationData = new VpeCreationData(tbody);
		creationData = encodeSubTable(pageContext, creationData, sourceElement,
				visualDocument, tbody);
		return creationData;
	}

	protected void encodeHeader(final VpePageContext pageContext, final VpeCreationData creationData, final Element sourceElement, final nsIDOMDocument visualDocument, final nsIDOMElement parentVisualNode) {
		encodeHeaderOrFooter(
				pageContext,
				creationData,
				sourceElement,
				visualDocument,
				parentVisualNode,
				RichFaces.NAME_FACET_HEADER,
				"dr-subtable-header rich-subtable-header", //$NON-NLS-1$ 
				"dr-subtable-headercell rich-subtable-headercell"); //$NON-NLS-1$
	}

	protected void encodeFooter(final VpePageContext pageContext, final VpeCreationData creationData, final Element sourceElement, final nsIDOMDocument visualDocument, final nsIDOMElement parentVisualNode) {
		encodeHeaderOrFooter(
				pageContext,
				creationData,
				sourceElement,
				visualDocument,
				parentVisualNode,
				RichFaces.NAME_FACET_FOOTER,
				"dr-subtable-footer rich-subtable-footer", //$NON-NLS-1$  
				"dr-subtable-footercell rich-subtable-footercell"); //$NON-NLS-1$
	}

	protected void encodeHeaderOrFooter(final VpePageContext pageContext,
			final VpeCreationData creationData, final Element sourceElement,
			final nsIDOMDocument visualDocument,
			final nsIDOMElement parentVisualNode, final String facetName,
			final String trClass, final String tdClass) {

		final ArrayList<Element> columns = RichFaces.getColumns(sourceElement);
		// final ArrayList<Element> columnsHeaders =
		// ComponentUtil.getColumnsWithFacet(columns, facetName);
		final boolean hasColumnWithFacet = RichFaces.hasColumnWithFacet(columns, facetName);
		if (hasColumnWithFacet) {
			final nsIDOMElement tr = visualDocument.createElement(HTML.TAG_TR);
			parentVisualNode.appendChild(tr);
			final String styleClass = trClass;
			if (styleClass != null) {
				tr.setAttribute(HTML.ATTR_CLASS, styleClass);
			}
			RichFacesDataTableTemplate.encodeHeaderOrFooterFacets(pageContext,
					creationData, tr, visualDocument, columns, tdClass, null,
					facetName, HTML.TAG_TD);
		}
	}

	private boolean isHeader(final Element sourceElement) {
		return icludedInFacet(sourceElement, RichFaces.NAME_FACET_HEADER);
	}

	private boolean isFooter(final Element sourceElement) {
		return icludedInFacet(sourceElement, RichFaces.NAME_FACET_FOOTER);
	}

	private boolean icludedInFacet(final Element sourceElement, final String facetName) {
		final Node parent = sourceElement.getParentNode();
		return parent!=null && ComponentUtil.isFacet(parent, facetName);
	}

	protected String getHeaderClass() {
		return ST_HEADER_CLASS;
	}

	protected String getHeaderContinueClass() {
		return ST_HEADER_CONTINUE_CLASS;
	}

	protected String getFooterClass() {
		return ST_FOOTER_CLASS;
	}

	protected String getFooterContinueClass() {
		return ST_FOOTER_CONTINUE_CLASS;
	}
	
	protected String getRowClass(final int row) {
		String rowClass = DEAFAULT_CELL_CLASS;

		if (rowClasses != null) {
			final int rowClassesSize = rowClasses.size();
			if(rowClassesSize > 0) {
				rowClass = rowClasses.get(row % rowClassesSize);
			}
		}

		return rowClass;
	}

	private String getColumnClass(final int column) {
		String columnClass = DEAFAULT_CELL_CLASS;
		if (columnClasses != null) {
			final int columnClassesSize = columnClasses.size();
			if (columnClassesSize > 0) {
				columnClass = columnClasses.get(column % columnClassesSize);
			}
		}
		return columnClass;
	}

	private void initClasses(final Node sourceNode, final VpePageContext pageContext) {
		final VpeExpression exprRowClasses = RichFaces.getExprRowClasses();
		final VpeExpression exprColumnClasses = RichFaces.getExprColumnClasses();

		try {
			rowClasses = VpeClassUtil.getClasses(exprRowClasses, sourceNode,
					pageContext);
			columnClasses = VpeClassUtil.getClasses(exprColumnClasses, sourceNode,
					pageContext);
		} catch (final VpeExpressionException e) {
			throw new RuntimeException(e);
		}
	}

	protected String getHeaderBackgoundImgStyle() {
		return null;
	}


	/* (non-Javadoc)
	 * @see org.jboss.tools.vpe.editor.template.VpeAbstractTemplate#isRecreateAtAttrChange(org.jboss.tools.vpe.editor.context.VpePageContext, org.w3c.dom.Element, org.mozilla.interfaces.nsIDOMDocument, org.mozilla.interfaces.nsIDOMElement, java.lang.Object, java.lang.String, java.lang.String) */
	@Override
	public boolean recreateAtAttrChange(final VpePageContext pageContext,
			final Element sourceElement, final nsIDOMDocument visualDocument,
			final nsIDOMElement visualNode, final Object data, final String name, final String value) {
		return true;
	}


	/* (non-Javadoc)
	 * @see org.jboss.tools.vpe.editor.template.VpeAbstractTemplate#getNodeForUpdate(org.jboss.tools.vpe.editor.context.VpePageContext, org.w3c.dom.Node, org.mozilla.interfaces.nsIDOMNode, java.lang.Object) */
	@Override
	public Node getNodeForUpdate(VpePageContext pageContext, Node sourceNode,
			nsIDOMNode visualNode, Object data) {
		Node parent = sourceNode.getParentNode();
		return parent;
	}
}
