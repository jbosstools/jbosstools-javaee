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
import java.util.Iterator;
import java.util.List;

import org.jboss.tools.jsf.vpe.richfaces.ComponentUtil;
import org.jboss.tools.jsf.vpe.richfaces.HtmlComponentUtil;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeAbstractTemplate;
import org.jboss.tools.vpe.editor.template.VpeChildrenInfo;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMNode;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class RichFacesToolBarGroupTemplate extends VpeAbstractTemplate {
	
	public static final String TAG_NAME = "toolBarGroup"; //$NON-NLS-1$
	public static final String ATTR_ITEMSEPARATOR_NAME = "itemSeparator"; //$NON-NLS-1$
	public static final String ATTR_LOCATION_NAME = "location"; //$NON-NLS-1$
	public static final String ATTR_LOCATION_RIGHT_VALUE = "right"; //$NON-NLS-1$

	private static final String TOOLBAR_PARENT_WARNING = "Parent should be toolBar"; //$NON-NLS-1$
	
	private static final String CSS_DR_TOOLBAR_INT = "dr-toolbar-int"; //$NON-NLS-1$
	private static final String CSS_RICH_TOOLBAR_ITEM = "rich-toolbar-item"; //$NON-NLS-1$
	
	private static final String SPACE = " "; //$NON-NLS-1$
	private static final String EMPTY = ""; //$NON-NLS-1$
	
	private static final String GROUP_TABLE_STYLE = "border: 0px none; margin: 0px 0px 0px 0px; padding: 0px 0px 0px 0px; width: 100%; height: 100%"; //$NON-NLS-1$
	
	private class SourceToolBarGroupItem {
		private Node toolBarGroupItem;
		private String itemSeparator;
		
		public SourceToolBarGroupItem(Node toolBarGroupItem) {
			this.toolBarGroupItem = toolBarGroupItem;
			this.itemSeparator = null;
		}

		public SourceToolBarGroupItem(String itemSeparator) {
			this.toolBarGroupItem = null;
			this.itemSeparator = itemSeparator;
		}

		public Node getToolBarGroupItem() {
			return toolBarGroupItem;
		}

		public String getItemSeparator() {
			return itemSeparator;
		}

		public boolean isItem() {
			return toolBarGroupItem != null;
		}
	}
	
	private class SourceToolBarGroupItems {
		private boolean isToolBarGroupLocationRight;
		private String itemSeparator;
		boolean isItemSeparatorExists;
		private List<SourceToolBarGroupItem> toolBarGroupItems = new ArrayList<SourceToolBarGroupItem>();
		
		public SourceToolBarGroupItems(Node sourceNode, boolean isToolBarGroupLocationRight,
				String itemSeparator) {
			this.isToolBarGroupLocationRight = isToolBarGroupLocationRight;
			this.itemSeparator = itemSeparator;
			this.isItemSeparatorExists = !(itemSeparator == null
				|| (itemSeparator != null && itemSeparator.length() == 0)
				|| RichFacesToolBarTemplate.ITEM_SEPARATOR_NONE.equals(itemSeparator));
			
			init(sourceNode);
		}
		
		private void init(Node sourceNode) {
			NodeList childrenList = sourceNode.getChildNodes();
			int childrenCount = childrenList.getLength();
			boolean isFirstItem = true;
			for (int i=0; i<childrenCount; i++) {
				Node child = childrenList.item(i);
				if (child.getNodeType() == Node.ELEMENT_NODE
						|| isVisibleText(child)) {
					if (isItemSeparatorExists && isToolBarGroupLocationRight
							&& !isFirstItem) {
						toolBarGroupItems.add(new SourceToolBarGroupItem(itemSeparator));
					}
					
					toolBarGroupItems.add(new SourceToolBarGroupItem(child));
					
					if (isItemSeparatorExists && !isToolBarGroupLocationRight
							&& !isLastItem(childrenList, i)) {
						toolBarGroupItems.add(new SourceToolBarGroupItem(itemSeparator));
					}
					
					isFirstItem = false;
				}
			}
		}
		
		private boolean isVisibleText(Node textNode) {
			return textNode.getNodeType() == Node.TEXT_NODE
					&& textNode.getNodeValue() != null
					&& textNode.getNodeValue().trim().length() > 0;
		}
		
		private boolean isLastItem(NodeList list, int index) {
			int listLength = list.getLength();
			
			for (int i=index+1; i < listLength; i++ ) {
				Node item = list.item(i);
				if (item.getNodeType() == Node.ELEMENT_NODE
						|| isVisibleText(item)) {
					return false;
				}
			}
			
			return true;
		}
		
		public Iterator<SourceToolBarGroupItem> iterator() {
			return toolBarGroupItems.iterator();
		}
	}
	
	
	public VpeCreationData create(VpePageContext pageContext, Node sourceNode, nsIDOMDocument visualDocument) {
		nsIDOMElement visualNode = null;
		VpeCreationData creationData = null;
		
		Element sourceElement = (Element)sourceNode;
		
		if (!sourceNode.getParentNode().getNodeName().endsWith(":" + RichFacesToolBarTemplate.TAG_NAME)) { //$NON-NLS-1$
			visualNode = RichFacesToolBarTemplate.createExceptionNode(visualDocument, TOOLBAR_PARENT_WARNING);
			creationData = new VpeCreationData(visualNode);
		}  else {
			String itemSeparator = sourceElement.getAttribute(ATTR_ITEMSEPARATOR_NAME);
			itemSeparator = RichFacesToolBarTemplate.checkAndUpdateItemSeparatorName(itemSeparator);
			SourceToolBarGroupItems sourceToolBarGroupItems = new SourceToolBarGroupItems(sourceNode,
					ATTR_LOCATION_RIGHT_VALUE.equals(sourceElement.getAttribute(ATTR_LOCATION_NAME)),
					itemSeparator);
			
			
			visualNode = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_TABLE);
			visualNode.setAttribute(HtmlComponentUtil.HTML_STYLE_ATTR, GROUP_TABLE_STYLE);
			nsIDOMElement body = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_TBODY);
			nsIDOMElement row = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_TR);
			row.setAttribute(HtmlComponentUtil.HTML_ATTR_VALIGN, HtmlComponentUtil.HTML_ATTR_VALIGN_MIDDLE_VALUE);
			
			creationData = new VpeCreationData(visualNode);
			
			Iterator<SourceToolBarGroupItem> iterator = sourceToolBarGroupItems.iterator();
			while(iterator.hasNext()) {
				SourceToolBarGroupItem toolBarGroupItem = iterator.next();
				
				nsIDOMElement cell = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_TD);
				if (toolBarGroupItem.isItem()) {
					
					String styleClass = sourceElement.hasAttribute(HtmlComponentUtil.HTML_STYLECLASS_ATTR) ?
							sourceElement.getAttribute(HtmlComponentUtil.HTML_STYLECLASS_ATTR) : EMPTY;
					String style = sourceElement.hasAttribute(HtmlComponentUtil.HTML_STYLE_ATTR) ?
							sourceElement.getAttribute(HtmlComponentUtil.HTML_STYLE_ATTR) : EMPTY;
							
					Node parentNode = sourceElement.getParentNode();							
					if ((null != parentNode) && (parentNode instanceof Element)) {
						Element parentElement = (Element)parentNode;
						if (parentElement.hasAttribute(RichFacesToolBarTemplate.CONTENTCLASS_ATTR_NAME)) {
							String contentClass = parentElement.getAttribute(RichFacesToolBarTemplate.CONTENTCLASS_ATTR_NAME);
							styleClass += SPACE + contentClass;
						}
						if (parentElement.hasAttribute(RichFacesToolBarTemplate.CONTENTSTYLE_ATTR_NAME)) {
							String contentStyle = parentElement.getAttribute(RichFacesToolBarTemplate.CONTENTSTYLE_ATTR_NAME);
							style += SPACE + contentStyle;
						}
					}
					styleClass += SPACE + CSS_DR_TOOLBAR_INT + SPACE + CSS_RICH_TOOLBAR_ITEM;
					cell.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR, styleClass);
					cell.setAttribute(HtmlComponentUtil.HTML_STYLE_ATTR, style);
					VpeChildrenInfo childrenInfo = new VpeChildrenInfo(cell);
					creationData.addChildrenInfo(childrenInfo);
					childrenInfo.addSourceChild(toolBarGroupItem.getToolBarGroupItem());
				} else {
					cell.setAttribute(HtmlComponentUtil.HTML_ALIGN_ATTR, HtmlComponentUtil.HTML_ALIGN_CENTER_VALUE);
					ComponentUtil.correctAttribute(sourceElement, cell,
							RichFacesToolBarTemplate.SEPARATORCLASS_ATTR_NAME,
							HtmlComponentUtil.HTML_CLASS_ATTR, null, null);
					String separatorImageUrl = RichFacesToolBarTemplate
							.getSeparatorImageUrlString(toolBarGroupItem.getItemSeparator());
					nsIDOMElement separatorImage = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_IMG);
					ComponentUtil.setImg(separatorImage, separatorImageUrl);
					visualNode.appendChild(separatorImage);
					cell.appendChild(separatorImage);
				}
				
				row.appendChild(cell);
			}
			
			body.appendChild(row);
			visualNode.appendChild(body);
		}
		
		return creationData;
	}
	

	@Override
	public Node getNodeForUpdate(VpePageContext pageContext, Node sourceNode, nsIDOMNode visualNode, Object data) {
		String prefix = sourceNode.getPrefix();
		if (prefix == null) {
			return null;
		}

		String parentNodeName = prefix + ":" + RichFacesToolBarTemplate.TAG_NAME; //$NON-NLS-1$
		
		Node parent = sourceNode.getParentNode();
		while (parent != null) {
			if (parentNodeName.equals(parent.getNodeName())) {
				break;
			}
			
			parent = parent.getParentNode();
		}
		
		return parent;
	}
}

