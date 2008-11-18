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
import org.jboss.tools.vpe.editor.template.VpeCreatorUtil;
import org.jboss.tools.vpe.editor.template.VpeTemplate;
import org.jboss.tools.vpe.editor.util.HTML;
import org.jboss.tools.vpe.editor.util.VisualDomUtil;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMNode;
import org.mozilla.interfaces.nsIDOMNodeList;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * The class {@code RichFacesDataTableChildrenEncoder} encodes children of a {@code rich:*Table}.
 *
 * <BR/>Use this class as follows:
 * <blockquote><pre>
 * RichFacesDataTableChildrenEncoder encoder
 *    = new RichFacesDataTableChildrenEncoder(
 *       creationData, visualDocument, sourceElement, table);
 * encoder.encodeChildren();</pre></blockquote>
 *
 * Method {@link #validateChildren(VpePageContext, Node, nsIDOMDocument, VpeCreationData) validateChildren}
 * MUST be invoked from {@link VpeTemplate#validate(VpePageContext, Node, nsIDOMDocument, VpeCreationData) validate}
 * method of the caller of this class:
 * <blockquote><pre>
 * public void validate(VpePageContext pageContext, Node sourceNode,
 *    nsIDOMDocument visualDocument, VpeCreationData data) {
 *       RichFacesDataTableChildrenEncoder.validateChildren(
 *          pageContext, sourceNode, visualDocument, data);
 *       ...
 * }</pre></blockquote>
 *
 * @author yradtsevich 
 * */
class RichFacesDataTableChildrenEncoder {
	private String firstRowClass = "dr-table-firstrow rich-table-firstrow"; //$NON-NLS-1$
	private String nonFirstRowClass = "dr-table-row rich-table-row"; //$NON-NLS-1$

	/**@param firstRowClass the class of the first row in the table
	 * @param nonFirstRowClass the class of all rows in the table except the first one*/
	public void setRowClasses(final String firstRowClass, final String nonFirstRowClass) {
		this.firstRowClass = firstRowClass;
		this.nonFirstRowClass = nonFirstRowClass;
	}

	/**Non-HTML tag that is used to create temporary containers for {@code rich:subTable} and {@code rich:columnGroup}.*/
	private static final String TAG_SUB_TABLE_OR_COLUMN_GROUP_CONTAINER = "subTableOrColumnGroup-container"; //$NON-NLS-1$
	private final VpeCreationData creationData;
	private final nsIDOMDocument visualDocument;
	private final Element sourceElement;
	private final nsIDOMElement table;

	public RichFacesDataTableChildrenEncoder(final VpeCreationData creationData,
			final nsIDOMDocument visualDocument, final Element sourceElement,
			final nsIDOMElement table) {
		this.creationData = creationData;
		this.visualDocument = visualDocument;
		this.sourceElement = sourceElement;
		this.table = table;
	}

	/**
	 * Creates containers in {@code table} for {@code sourceElement}'s children
	 * and adds appropriate objects of {@link VpeChildrenInfo} to {@code creationData}.
	 *
	 * <BR/>It knows about following tags:
	 * {@code rich:column, rich:columns, rich:subTable} and {@code rich:columnGroup}.
	 * <BR/>For any another tag it uses {@link #addElementToTable(Node)} method.
	 * */
	public void encodeChildren() {
		// create an empty childrenInfo. It tells to VpeVisualDomBuilder
		// that it is not necessary to add any child of the sourceElement
		// except ones specified in another vpeChildrenInfo's
		final VpeChildrenInfo childInfo = new VpeChildrenInfo(null);
		creationData.addChildrenInfo(childInfo);
		
		final List<Node> children = ComponentUtil.getChildren(sourceElement);		
		boolean createNewRow = true;
		for (final Node child : children) {
			final String nodeName = child.getNodeName();
			if (nodeName.endsWith(RichFaces.TAG_COLUMN) ||
					nodeName.endsWith(RichFaces.TAG_COLUMNS)) {
				createNewRow |= RichFacesColumnTemplate.isBreakBefore(child);
				addColumnToRow(child, createNewRow);
				createNewRow = false;
			} else if(nodeName.endsWith(RichFaces.TAG_SUB_TABLE)
					|| nodeName.endsWith(RichFaces.TAG_COLUMN_GROUP)) {
				addSubTableOrColumnGroupToTable(child);
				createNewRow = true;
			} else if (!VpeCreatorUtil.isFacet(child)) {
				addElementToTable(child);
				createNewRow = true;
			}
		}
	}

	/**
	 * Makes necessary changes in the table's body after all children of the table have been encoded.
	 */
	public static void validateChildren(final VpePageContext pageContext, final Node sourceNode,
			final nsIDOMDocument visualDocument, final VpeCreationData creationData) {
		final nsIDOMNode visualNode = creationData.getNode();
		fixSubTables(visualNode);
	}
	
	/**
	 * Creates a container for {@code subTableOrColumnGroupNode} in {@code table}
	 * and adds an appropriate object of {@link VpeChildrenInfo} to {@code creationData}.
	 * <BR/>The container is the tag {@link #TAG_SUB_TABLE_OR_COLUMN_GROUP_CONTAINER}.
	 */
	private nsIDOMElement addSubTableOrColumnGroupToTable(final Node subTableOrColumnGroupNode) {
		final nsIDOMElement subTableOrColumnGroupContainer = visualDocument.createElement(TAG_SUB_TABLE_OR_COLUMN_GROUP_CONTAINER);
		table.appendChild(subTableOrColumnGroupContainer);
		final VpeChildrenInfo childInfo = new VpeChildrenInfo(subTableOrColumnGroupContainer);
		childInfo.addSourceChild(subTableOrColumnGroupNode);
		creationData.addChildrenInfo(childInfo);

		return subTableOrColumnGroupContainer;
	}

	private nsIDOMElement currentRow = null;
	private VpeChildrenInfo currentRowChildrenInfo = null;
	private int rowNumber = 0;
	/**
	 * Creates a container for {@code columnNode} in {@code table}
	 * and adds an appropriate object of {@link VpeChildrenInfo} to {@code creationData}.
	 * <BR/>If the parameter {@code createNewRow} is {@code true} then it  creates the
	 * container in a new row.
	 * */
	private nsIDOMElement addColumnToRow(final Node columnNode, final boolean createNewRow) {
		if ( createNewRow || (currentRow == null) ) {
			currentRow = visualDocument.createElement(HTML.TAG_TR);
			table.appendChild(currentRow);
			currentRowChildrenInfo = new VpeChildrenInfo(currentRow);
			creationData.addChildrenInfo(currentRowChildrenInfo);
			rowNumber++;
			if (rowNumber == 1) {
				currentRow.setAttribute(HTML.ATTR_CLASS, firstRowClass);
			} else {
				currentRow.setAttribute(HTML.ATTR_CLASS, nonFirstRowClass);
			}
		}

		currentRowChildrenInfo.addSourceChild(columnNode);
		return currentRow;
	}

	/**
	 * Creates a row container for {@code node} in {@code table}
	 * and adds an appropriate object of {@link VpeChildrenInfo} to {@code creationData}.
	 * <BR/>The container spans the entire row.
	 * */
	private void addElementToTable(final Node node) {
		final nsIDOMElement tr = this.visualDocument.createElement(HTML.TAG_TR);
		table.appendChild(tr);
		final nsIDOMElement td = this.visualDocument.createElement(HTML.TAG_TD);

		td.setAttribute(HTML.ATTR_COLSPAN, HTML.VALUE_COLSPAN_ALL);
		tr.appendChild(td);
		final VpeChildrenInfo childInfo = new VpeChildrenInfo(td);
		childInfo.addSourceChild(node);
		creationData.addChildrenInfo(childInfo);
	}

	/**
	 * Replaces all occurencies of {@link #TAG_SUB_TABLE_OR_COLUMN_GROUP_CONTAINER} tag in
	 * the {@code visualNode} by the tag's child.
	 * @see #addSubTableOrColumnGroupToTable(Node)
	 */
	private static void fixSubTables(final nsIDOMNode visualNode) {
		final nsIDOMElement element = (nsIDOMElement) visualNode;
		final nsIDOMNodeList subTableContainers = element.getElementsByTagName(TAG_SUB_TABLE_OR_COLUMN_GROUP_CONTAINER);
		final long length = subTableContainers.getLength();
		for (int i = 0; i < length; i++) {
			final nsIDOMNode subTableContainer = subTableContainers.item(0);
			final nsIDOMNodeList subTableContainerChildren = subTableContainer.getChildNodes();

			if (subTableContainerChildren == null
					|| subTableContainerChildren.getLength() != 1) {
				final RuntimeException e = new RuntimeException("This is probably a bug. subTable-container should have one inner tag.");//$NON-NLS-1$
				RichFacesTemplatesActivator.getPluginLog().logError(e);
			}
			
			VisualDomUtil.replaceNodeByItsChildren(subTableContainer);
		}
	}	
}
