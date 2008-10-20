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
import org.jboss.tools.jsf.vpe.richfaces.template.util.RichFaces;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeAbstractTemplate;
import org.jboss.tools.vpe.editor.template.VpeChildrenInfo;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.template.expression.VpeExpression;
import org.jboss.tools.vpe.editor.template.expression.VpeExpressionBuilder;
import org.jboss.tools.vpe.editor.template.expression.VpeExpressionBuilderException;
import org.jboss.tools.vpe.editor.template.expression.VpeExpressionException;
import org.jboss.tools.vpe.editor.util.HTML;
import org.jboss.tools.vpe.editor.util.VpeClassUtil;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMNode;
import org.mozilla.interfaces.nsIDOMNodeList;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class RichFacesSubTableTemplate extends VpeAbstractTemplate {

	/**
	 * 
	 */
	private static final String VAL_TRUE = "true";
	/**
	 * 
	 */
	private static final String ATTR_BREAK_BEFORE = "breakBefore"; //$NON-NLS-1$
	/**
	 * 
	 */
	private static final String COLUMN_CLASSES_EXPRESSION = "{@columnClasses}"; //$NON-NLS-1$
	/**
	 * 
	 */
	private static final String ROW_CLASSES_EXPRESSION = "{@rowClasses}"; //$NON-NLS-1$
	private static final String DEAFAULT_CELL_CLASS = "dr-subtable-cell rich-subtable-cell"; //$NON-NLS-1$
	private static List<String> rowClasses;
	private static List<String> columnClasses;
	public static RichFacesSubTableTemplate DEFAULT_INSTANCE = new RichFacesSubTableTemplate();


	public RichFacesSubTableTemplate() {
		super();
	}


	/**
	 * Encode columnGroup
	 * @param creationData
	 * @param columnGroupSourceElement
	 * @param visualDocument
	 * @param parentVisualNode
	 * @return
	 */
	public VpeCreationData encode(VpeCreationData creationData, final Element sourceElement, final nsIDOMDocument visualDocument, final nsIDOMElement parentVisualNode) {
		if(creationData!=null) {
			// Encode header
			encodeHeader(creationData, sourceElement, visualDocument, parentVisualNode);
		}

		initClasses(sourceElement, null);

		nsIDOMElement curTr = visualDocument.createElement(HTML.TAG_TR);
		ComponentUtil.copyAttributes(sourceElement, curTr);

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

		// Create mapping to Encode body
		VpeChildrenInfo trChildrenInfo = new VpeChildrenInfo(curTr);
		final List<Node> children = ComponentUtil.getChildren(sourceElement);
		for (final Node child : children) {
			if (child.getNodeName().endsWith(':' + RichFaces.TAG_COLUMN)) {
				final boolean breakBefore = VAL_TRUE.equals( ((Element)child).getAttribute(ATTR_BREAK_BEFORE) );
				if (breakBefore) {
					curRow++;
					curColumn = 0;
					curTr = visualDocument.createElement(HTML.TAG_TR);
					ComponentUtil.copyAttributes(sourceElement, curTr);

					if(header) {
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


		if(parentVisualNode!=null) {
			// Encode footer
			encodeFooter(creationData, sourceElement, visualDocument, parentVisualNode);
		}

		return creationData;
	}

	/** Adds necessary attributes to its children.*/
	@Override
	public void validate(final VpePageContext pageContext, final Node sourceNode,
			final nsIDOMDocument visualDocument, final VpeCreationData creationData) {
		initClasses(sourceNode, pageContext);
		final List<VpeChildrenInfo> childrenInfoList = creationData.getChildrenInfoList();
		if (childrenInfoList != null) {
			for (final VpeChildrenInfo childrenInfo : childrenInfoList) {
				final List<Node> sourceChildren = childrenInfo.getSourceChildren();
				if (sourceChildren != null 
						&& sourceChildren.size() > 0 
						&& sourceChildren.get(0).getParentNode() == sourceNode) {
					final nsIDOMNodeList visualChildren = childrenInfo.getVisualParent().getChildNodes();
					addStylesToCells(visualDocument, visualChildren);
				}
			}
		}
	}
	
	/** Adds HTML style classes names to all TDs from the list <code>visualChildren</code>
	 * according to <code>columnClasses</code> attribute of the tag. */
	private void addStylesToCells(nsIDOMDocument visualDocument, nsIDOMNodeList visualChildren) {
		int column = 0;
		for (int i = 0; i < visualChildren.getLength(); i++) {
			final nsIDOMNode visualChild = visualChildren.item(i);
			if ( visualChild.getNodeType() == nsIDOMNode.ELEMENT_NODE && HTML.TAG_TD.equalsIgnoreCase(visualChild.getNodeName()) ) {
				final nsIDOMNode tableCell = visualChild;
				nsIDOMNode columnStyle = tableCell.getAttributes().getNamedItem(HTML.ATTR_CLASS);
				if (columnStyle == null) {
					columnStyle = visualDocument.createAttribute(HTML.ATTR_CLASS);
				}
				columnStyle.setNodeValue(columnStyle.getNodeValue() + ' ' + getColumnClass(column));
				column++;
			}
		}
	}

	public VpeCreationData create(final VpePageContext pageContext, final Node sourceNode, final nsIDOMDocument visualDocument) {
		final Element sourceElement = (Element)sourceNode;
		final VpeCreationData creationData = encode(null, sourceElement, visualDocument, null);
		return creationData;
	}

	protected void encodeHeader(final VpeCreationData creationData, final Element sourceElement, final nsIDOMDocument visualDocument, final nsIDOMElement parentVisualNode) {
		encodeHeaderOrFooter(creationData, sourceElement, visualDocument, parentVisualNode, "header", "dr-subtable-header rich-subtable-header", "dr-subtable-headercell rich-subtable-headercell");
	}

	protected void encodeFooter(final VpeCreationData creationData, final Element sourceElement, final nsIDOMDocument visualDocument, final nsIDOMElement parentVisualNode) {
		encodeHeaderOrFooter(creationData, sourceElement, visualDocument, parentVisualNode, "footer", "dr-subtable-footer rich-subtable-footer", "dr-subtable-footercell rich-subtable-footercell");
	}

	protected void encodeHeaderOrFooter(final VpeCreationData creationData, final Element sourceElement, final nsIDOMDocument visualDocument, final nsIDOMElement parentVisualNode, final String facetName, final String trClass, final String tdClass) {
		final ArrayList<Element> columns = RichFacesDataTableTemplate.getColumns(sourceElement);
		final ArrayList<Element> columnsHeaders = RichFacesDataTableTemplate.getColumnsWithFacet(columns, facetName);
		if(!columnsHeaders.isEmpty()) {
			final nsIDOMElement tr = visualDocument.createElement(HTML.TAG_TR);
			parentVisualNode.appendChild(tr);
			final String styleClass = trClass;
			if(styleClass!=null) {
				tr.setAttribute(HTML.ATTR_CLASS, styleClass);
			}
			RichFacesDataTableTemplate.encodeHeaderOrFooterFacets(creationData, tr, visualDocument, columnsHeaders,
					tdClass,
					null, facetName, HTML.TAG_TD);
		}

	}

	private boolean isHeader(final Element sourceElement) {
		return icludedInFacet(sourceElement, "header");
	}

	private boolean isFooter(final Element sourceElement) {
		return icludedInFacet(sourceElement, "footer");
	}

	private boolean icludedInFacet(final Element sourceElement, final String facetName) {
		final Node parent = sourceElement.getParentNode();
		return parent!=null && ComponentUtil.isFacet(parent, facetName);
	}

	protected String getHeaderClass() {
		return "dr-subtable-header rich-subtable-header";
	}

	protected String getHeaderContinueClass() {
		return "dr-subtable-header-continue rich-subtable-header-continue";
	}

	protected String getFooterClass() {
		return "dr-subtable-footer rich-subtable-footer";
	}

	protected String getFooterContinueClass() {
		return "dr-subtable-footer-continue rich-subtable-footer-continue";
	}

	private String getRowClass(final int row) {
		StringBuffer rowClass = new StringBuffer(DEAFAULT_CELL_CLASS);

		if (rowClasses != null) {
			final int rowClassesSize = rowClasses.size();
			if(rowClassesSize > 0) {
				rowClass = new StringBuffer(rowClasses.get(row % rowClassesSize));
			}
		}

		return rowClass.toString();
	}

	private String getColumnClass(final int column) {
		StringBuffer columnClass = new StringBuffer(DEAFAULT_CELL_CLASS);
		if (columnClasses != null) {
			final int columnClassesSize = columnClasses.size();
			if (columnClassesSize > 0) {
				columnClass = new  StringBuffer(columnClasses.get(column % columnClassesSize));
			}
		}
		return columnClass.toString();
	}

	private void initClasses(final Node sourceNode, final VpePageContext pageContext) {
		VpeExpression rowClassesExpr;
		VpeExpression columnClassesExpr;
		try {
			rowClassesExpr = VpeExpressionBuilder
				.buildCompletedExpression(ROW_CLASSES_EXPRESSION, caseSensitive).getExpression();
			columnClassesExpr = VpeExpressionBuilder
				.buildCompletedExpression(COLUMN_CLASSES_EXPRESSION, caseSensitive).getExpression();
		} catch (final VpeExpressionBuilderException e) {
			throw new RuntimeException(e);
		}

		try {
			rowClasses = VpeClassUtil.getClasses(rowClassesExpr, sourceNode,
					pageContext);
			columnClasses = VpeClassUtil.getClasses(columnClassesExpr, sourceNode,
					pageContext);
		} catch (final VpeExpressionException e) {
			throw new RuntimeException(e);
		}
	}

	protected String getHeaderBackgoundImgStyle() {
		return null;
	}


	/* (non-Javadoc)
	 * @see org.jboss.tools.vpe.editor.template.VpeAbstractTemplate#isRecreateAtAttrChange(org.jboss.tools.vpe.editor.context.VpePageContext, org.w3c.dom.Element, org.mozilla.interfaces.nsIDOMDocument, org.mozilla.interfaces.nsIDOMElement, java.lang.Object, java.lang.String, java.lang.String)
	 */
	@Override
	public boolean isRecreateAtAttrChange(final VpePageContext pageContext,
			final Element sourceElement, final nsIDOMDocument visualDocument,
			final nsIDOMElement visualNode, final Object data, final String name, final String value) {
		return true;
	}
}