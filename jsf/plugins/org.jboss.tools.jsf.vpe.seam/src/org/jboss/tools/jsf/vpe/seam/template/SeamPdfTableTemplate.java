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

public class SeamPdfTableTemplate extends SeamPdfAbstractTemplate {

	private nsIDOMElement visualElement;
	private Element sourceElement;


	public VpeCreationData create(VpePageContext pageContext, Node sourceNode,
			nsIDOMDocument visualDocument) {
		sourceElement = (Element) sourceNode;
		nsIDOMNode visualNode = visualDocument.createElement(HTML.TAG_DIV);
		nsIDOMNode tableNode = visualDocument.createElement(HTML.TAG_TABLE);
		nsIDOMElement visualTable = (nsIDOMElement) tableNode
				.queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID);
		visualNode.appendChild(tableNode);
		visualElement = (nsIDOMElement) visualNode
				.queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID);
		visualTable.setAttribute(HTML.ATTR_WIDTH, getWidthPerc(sourceElement));
		visualTable.setAttribute(HTML.ATTR_ALIGN, getAlignment(sourceElement));
		visualTable.setAttribute(HTML.ATTR_CELLSPACING, "0px"); //$NON-NLS-1$
		return new VpeCreationData(visualElement);
	}

	@Override
	public void validate(VpePageContext pageContext, Node sourceNode,
			nsIDOMDocument visualDocument, VpeCreationData data) {
		setColumns(pageContext, sourceNode, visualDocument, data);
	}

	private int getNumberOfColumns(Node sourceTableNode) {
		int columnsNumber = 1;
		String columnsNumberString = ((Element) sourceTableNode)
				.getAttribute("columns"); //$NON-NLS-1$
		if (columnsNumberString != null) {
			try {
				columnsNumber = Integer.parseInt(columnsNumberString);
				if (columnsNumber < 1) {
					columnsNumber = 1;
				}
			} catch (NumberFormatException e) {
				columnsNumber = 1;
			}
		} else {
			columnsNumber = 1;
		}
		return columnsNumber;
	}

	private void setColumns(VpePageContext pageContext, Node sourceNode,
			nsIDOMDocument visualDocument, VpeCreationData data) {
		int numberOfColumns = getNumberOfColumns(sourceNode);
		Node[] cells = SeamUtil.getChildsByName(pageContext, sourceNode,
				"p:cell"); //$NON-NLS-1$
		int cellsLength = cells.length;
		if (numberOfColumns > cellsLength) {
			for (int i = 0; i < cells.length; i++) {
				nsIDOMNode visualCell = pageContext.getDomMapping()
						.getVisualNode(cells[i]);
				nsIDOMNode parentNode = visualCell.getParentNode();
				parentNode.removeChild(visualCell);
			}
			return;
		}
		nsIDOMNode visualTable = ((nsIDOMElement) data.getNode()
				.queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID))
				.getElementsByTagName(HTML.TAG_TABLE).item(0);
		if (cellsLength != 0) {
			nsIDOMNode trVisualNode = visualDocument.createElement(HTML.TAG_TR);
			visualTable.appendChild(trVisualNode);
			int posCounter = 0;
			for (int i = 0; i < cellsLength; i++) {
				nsIDOMNode visualCell = pageContext.getDomMapping()
						.getVisualNode(cells[i]);
				nsIDOMNode parentNode = visualCell.getParentNode();
				if (parentNode != null) {
					parentNode.removeChild(visualCell);
					trVisualNode.appendChild(visualCell);
					int colspanValue = getColspanValue(visualCell);
					posCounter++;
					if (colspanValue > 1 && posCounter % numberOfColumns != 0) {
						int posInRow = posCounter - 1;
						int numEndCells = numberOfColumns - posInRow;
						if (numEndCells <= colspanValue) {
							colspanValue = numEndCells;
							trVisualNode = visualDocument
									.createElement(HTML.TAG_TR);
							visualTable.appendChild(trVisualNode);
							posCounter = 0;
							continue;
						} else {
							for (int j = 0; j < numberOfColumns
									- (colspanValue); j++) {
								i++;
								visualCell = pageContext.getDomMapping()
										.getVisualNode(cells[i]);
								trVisualNode.appendChild(visualCell);
							}
							i -= numberOfColumns - (colspanValue);
							posCounter += colspanValue - 1;
						}
					}
				}
				if ((posCounter % numberOfColumns == 0)
						&& ((i + 1) != cellsLength)) {
					trVisualNode = visualDocument.createElement(HTML.TAG_TR);
					visualTable.appendChild(trVisualNode);
					posCounter = 0;
				}
			}
			if (trVisualNode.getChildNodes().getLength() != numberOfColumns) {
				trVisualNode.getParentNode().removeChild(trVisualNode);
			}
		}
	}

	private String getWidthPerc(Element sourceElement) {
		String width = sourceElement
				.getAttribute(SeamUtil.SEAM_ATTR_WIDTH_PERCENTAGE);
		if (width != null) {
			try {
				int intWidth = Integer.parseInt(width);
				if (intWidth < 1 || intWidth > 100) {
					width = "100%"; //$NON-NLS-1$
				} else {
					width = Integer.toString(intWidth) + "%"; //$NON-NLS-1$
				}
			} catch (NumberFormatException e) {
				width = "100%"; //$NON-NLS-1$
			}
		} else {
			width = "100%"; //$NON-NLS-1$
		}
		return width;
	}

	private String getAlignment(Element sourceElement) {
		String align = sourceElement
				.getAttribute(SeamUtil.SEAM_ATTR_HORIZONAL_ALIGNMENT);
		if (align != null) {
			for (int i = 0; i < SeamUtil.POSSIBLE_ALIGNS.length; i++) {
				if (SeamUtil.POSSIBLE_ALIGNS[i].equalsIgnoreCase(align)) {
					if (SeamUtil.POSSIBLE_ALIGNS[i]
							.equalsIgnoreCase("justifyall")) { //$NON-NLS-1$
						return HTML.VALUE_ALIGN_JUSTIFY;
					}
					return align;
				}
			}
		}
		return HTML.VALUE_ALIGN_CENTER;
	}

	private int getColspanValue(nsIDOMNode visualNode) {
		int colspan = 1;
		nsIDOMElement visualElement = (nsIDOMElement) visualNode
				.queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID);
		String colspanString = visualElement.getAttribute(HTML.ATTR_COLSPAN);
		if (colspanString != null) {
			try {
				colspan = Integer.parseInt(colspanString);
				if (colspan < 1) {
					colspan = 1;
				}
			} catch (NumberFormatException e) {
				colspan = 1;
			}
		}
		return colspan;
	}

}
