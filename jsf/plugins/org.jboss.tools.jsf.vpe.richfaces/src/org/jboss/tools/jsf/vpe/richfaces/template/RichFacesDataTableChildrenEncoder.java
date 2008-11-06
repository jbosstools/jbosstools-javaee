/*******************************************************************************
 * Copyright (c) 2007-2008 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.jsf.vpe.richfaces.template;

import java.util.List;

import org.jboss.tools.jsf.vpe.richfaces.ComponentUtil;
import org.jboss.tools.jsf.vpe.richfaces.RichFacesTemplatesActivator;
import org.jboss.tools.jsf.vpe.richfaces.template.util.RichFaces;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeChildrenInfo;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.util.HTML;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMNode;
import org.mozilla.interfaces.nsIDOMNodeList;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**@author yradtsevich */
class RichFacesDataTableChildrenEncoder {
	private static final String TAG_SUB_TABLE_OR_COLUMN_GROUP_CONTAINER = "subTableOrColumnGroup-container"; //$NON-NLS-1$
	private VpeCreationData creationData;
	private nsIDOMDocument visualDocument; 
	private Element sourceElement;
	private nsIDOMElement table;

	public RichFacesDataTableChildrenEncoder(VpeCreationData creationData,
			nsIDOMDocument visualDocument, Element sourceElement,
			nsIDOMElement table) {
		this.creationData = creationData;
		this.visualDocument = visualDocument;
		this.sourceElement = sourceElement;
		this.table = table;
	}

	public void encodeChildren() {
		// Create mapping to Encode body
		List<Node> children = ComponentUtil.getChildren(sourceElement);
		boolean createNewRow = true;
		for (Node child : children) {
			String nodeName = child.getNodeName();
			if (nodeName.endsWith(RichFaces.TAG_COLUMN) || 
					nodeName.endsWith(RichFaces.TAG_COLUMNS)) {
				createNewRow |= RichFacesColumnTemplate.isBreakBefore(child);				
				addColumnToRow(child, createNewRow);
				createNewRow = false;
			} else if(nodeName.endsWith(RichFaces.TAG_SUB_TABLE) 
					|| nodeName.endsWith(RichFaces.TAG_COLUMN_GROUP)) {
				addSubTableOrColumnGroupToTable(child);
				createNewRow = true;
			} else {
				VpeChildrenInfo childInfo = new VpeChildrenInfo(table);
				childInfo.addSourceChild(child);
				creationData.addChildrenInfo(childInfo);
				createNewRow = true;
			}
		}
	}

	private nsIDOMElement addSubTableOrColumnGroupToTable(Node subTableOrColumnGroupNode) {
		nsIDOMElement subTableOrColumnGroupContainer = visualDocument.createElement(TAG_SUB_TABLE_OR_COLUMN_GROUP_CONTAINER);
		table.appendChild(subTableOrColumnGroupContainer);
		VpeChildrenInfo childInfo = new VpeChildrenInfo(subTableOrColumnGroupContainer);
		childInfo.addSourceChild(subTableOrColumnGroupNode);
		creationData.addChildrenInfo(childInfo);
		
		return subTableOrColumnGroupContainer;
	}

	private nsIDOMElement currentRow = null;
	private VpeChildrenInfo currentRowChildrenInfo = null;
	private int rowNumber = 0;
	private nsIDOMElement addColumnToRow(Node columnNode, boolean createNewRow) {
		if ( createNewRow || (currentRow == null) ) {
			currentRow = visualDocument.createElement(HTML.TAG_TR);
			table.appendChild(currentRow);
			currentRowChildrenInfo = new VpeChildrenInfo(currentRow);
			creationData.addChildrenInfo(currentRowChildrenInfo);
			rowNumber++;
			if (rowNumber == 1) {
				currentRow.setAttribute(HTML.ATTR_CLASS, "dr-table-firstrow rich-table-firstrow"); //$NON-NLS-1$
			} else {
				currentRow.setAttribute(HTML.ATTR_CLASS, "dr-table-row rich-table-row"); //$NON-NLS-1$
			}
		}

		currentRowChildrenInfo.addSourceChild(columnNode);
		return currentRow;
	}

	public static void validateChildren(VpePageContext pageContext, Node sourceNode,
			nsIDOMDocument visualDocument, VpeCreationData creationData) {
		nsIDOMNode visualNode = creationData.getNode();
		fixSubTables(visualNode);		
	}

	private static void fixSubTables(nsIDOMNode node) {
		nsIDOMElement element = (nsIDOMElement) node;
		nsIDOMNodeList subTableContainers = element.getElementsByTagName(TAG_SUB_TABLE_OR_COLUMN_GROUP_CONTAINER);
		long length = subTableContainers.getLength();
		for (int i = 0; i < length; i++) {
			nsIDOMNode subTableContainer = subTableContainers.item(0);
			nsIDOMNodeList subTableContainerChildren = subTableContainer.getChildNodes();				
			nsIDOMNode containerParent = subTableContainer.getParentNode();
			if (subTableContainerChildren != null 
					&& subTableContainerChildren.getLength() == 1) {
				nsIDOMNode subTableMainTag = subTableContainerChildren.item(0);
				subTableContainer.removeChild(subTableMainTag);
				containerParent.insertBefore(subTableMainTag, subTableContainer);
			} else {
				RuntimeException e = new RuntimeException("This is probably a bug. subTable-container should have one inner tag.");//$NON-NLS-1$
				RichFacesTemplatesActivator.getPluginLog().logError(e);
			}
			containerParent.removeChild(subTableContainer);
		}	
	}
}
