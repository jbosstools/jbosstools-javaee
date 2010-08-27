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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.jboss.tools.jsf.vpe.richfaces.ComponentUtil;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeAbstractTemplate;
import org.jboss.tools.vpe.editor.template.VpeChildrenInfo;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.util.HTML;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMText;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class RichFacesToolBarTemplate extends VpeAbstractTemplate {
	public static final String TAG_NAME = "toolBar"; //$NON-NLS-1$
	
	public static final String ITEM_SEPARATOR_NONE = "none"; //$NON-NLS-1$
	public static final String ITEM_SEPARATOR_LINE = "line"; //$NON-NLS-1$
	public static final String ITEM_SEPARATOR_GRID = "grid"; //$NON-NLS-1$
	public static final String ITEM_SEPARATOR_DISC = "disc"; //$NON-NLS-1$
	public static final String ITEM_SEPARATOR_SQUARE = "square"; //$NON-NLS-1$

	public static final String ITEM_SEPARATOR_LINE_URL = "toolBar/separatorLine.gif"; //$NON-NLS-1$
	public static final String ITEM_SEPARATOR_GRID_URL = "toolBar/separatorGrid.gif"; //$NON-NLS-1$
	public static final String ITEM_SEPARATOR_DISC_URL = "toolBar/separatorDisc.gif"; //$NON-NLS-1$
	public static final String ITEM_SEPARATOR_SQUARE_URL = "toolBar/separatorSquare.gif"; //$NON-NLS-1$

	public static final String EXCEPTION_ATTR_STYLE_VALUE = "color: red; font-weight:bold;"; //$NON-NLS-1$

	static final String CONTENTCLASS_ATTR_NAME = "contentClass"; //$NON-NLS-1$
	static final String CONTENTSTYLE_ATTR_NAME = "contentStyle"; //$NON-NLS-1$
	static final String STYLEATTR_CLASS_NAME = "styleClass"; //$NON-NLS-1$
	static final String ATTR_STYLE_NAME = "style"; //$NON-NLS-1$
	static final String ITEMSEPARATOR_ATTR_NAME = "itemSeparator"; //$NON-NLS-1$
	static final String SEPARATORCLASS_ATTR_NAME = "separatorClass"; //$NON-NLS-1$
	static final String WIDTH_ATTR_NAME = "width"; //$NON-NLS-1$
	static final String HEIGHT_ATTR_NAME = "height"; //$NON-NLS-1$
	
	private static final String CSS_DR_TOOLBAR_INT = "dr-toolbar-int"; //$NON-NLS-1$
	private static final String CSS_DR_TOOLBAR_EXT = "dr-toolbar-ext"; //$NON-NLS-1$
	private static final String CSS_RICH_TOOLBAR = "rich-toolbar"; //$NON-NLS-1$
	private static final String CSS_RICH_TOOLBAR_ITEM = "rich-toolbar-item"; //$NON-NLS-1$
	
	private static final String SPACE = " "; //$NON-NLS-1$

	public VpeCreationData create(VpePageContext pageContext, Node sourceNode,  nsIDOMDocument visualDocument) {
		nsIDOMElement visualNode = visualDocument.createElement(HTML.TAG_TABLE);
		VpeCreationData creationData = new VpeCreationData(visualNode);
		Element sourceElement = (Element) sourceNode;
		String itemSeparator = sourceElement.getAttribute(ITEMSEPARATOR_ATTR_NAME); 

			itemSeparator = checkAndUpdateItemSeparatorName(itemSeparator);
			SourceToolBarItems sourceToolBarItems = new SourceToolBarItems(sourceNode, itemSeparator);
			String itemSeparatorImageUrl = getSeparatorImageUrlString(sourceToolBarItems.getItemSeparator());
	
			ComponentUtil.setCSSLink(pageContext, "toolBar/toolBar.css", "richFacesToolBar"); //$NON-NLS-1$ //$NON-NLS-2$
	
			ComponentUtil.correctAttribute(sourceElement, visualNode,
					WIDTH_ATTR_NAME,
					HTML.ATTR_WIDTH, null, "100%"); //$NON-NLS-1$
			ComponentUtil.correctAttribute(sourceElement, visualNode,
					HEIGHT_ATTR_NAME,
					HTML.ATTR_HEIGHT, null, null);
			ComponentUtil.correctAttribute(sourceElement, visualNode,
				STYLEATTR_CLASS_NAME, HTML.ATTR_CLASS,
				CSS_DR_TOOLBAR_EXT + SPACE + CSS_RICH_TOOLBAR,
				CSS_DR_TOOLBAR_EXT + SPACE + CSS_RICH_TOOLBAR);
			
			String style = ComponentUtil.getHeaderBackgoundImgStyle() + ";"; //$NON-NLS-1$
			ComponentUtil.correctAttribute(sourceElement, visualNode,
					ATTR_STYLE_NAME,
					HTML.ATTR_STYLE, style, style);
	
			nsIDOMElement body = null;
			nsIDOMElement row = null;
			nsIDOMElement cell = null;
	
			body = visualDocument.createElement(HTML.TAG_TBODY);
			row = visualDocument.createElement(HTML.TAG_TR);
			row.setAttribute(HTML.ATTR_VALIGN, HTML.VALUE_ALIGN_MIDDLE);
	
			SourceToolBarItem toolBarItem;
			Iterator<SourceToolBarItem> iterator = sourceToolBarItems.getLeftItemsIterator();
			while (iterator.hasNext()) {
				toolBarItem = iterator.next();
				cell = visualDocument.createElement(HTML.TAG_TD);
				if (toolBarItem.isItem()) {
					ComponentUtil.correctAttribute(sourceElement, cell,
						CONTENTCLASS_ATTR_NAME,
						HTML.ATTR_CLASS, 
						CSS_DR_TOOLBAR_INT + SPACE + CSS_RICH_TOOLBAR_ITEM,
						CSS_DR_TOOLBAR_INT + SPACE + CSS_RICH_TOOLBAR_ITEM);
					ComponentUtil.correctAttribute(sourceElement, cell,
							CONTENTSTYLE_ATTR_NAME,
							HTML.ATTR_STYLE,
							toolBarItem.isToolBarGroupItem() ? "padding: 0px 0px 0px 0px;" : null, //$NON-NLS-1$
							toolBarItem.isToolBarGroupItem() ? "padding: 0px 0px 0px 0px;" : null); //$NON-NLS-1$
					
					VpeChildrenInfo childrenInfo = new VpeChildrenInfo(cell);
					creationData.addChildrenInfo(childrenInfo);
					childrenInfo.addSourceChild(toolBarItem.getToolBarItem());
				} else {
					if (itemSeparatorImageUrl != null) {
						cell = visualDocument.createElement(HTML.TAG_TD);
						cell.setAttribute(HTML.ATTR_ALIGN,
							HTML.VALUE_ALIGN_CENTER);
						ComponentUtil.correctAttribute(sourceElement, cell,
								SEPARATORCLASS_ATTR_NAME,
								HTML.ATTR_CLASS, null, null);
						nsIDOMElement separatorImage = visualDocument
							.createElement(HTML.TAG_IMG);
						ComponentUtil.setImg(separatorImage, itemSeparatorImageUrl);
						cell.appendChild(separatorImage);
					}
				}
				
				row.appendChild(cell);
			}
	
			// Empty column
			cell = visualDocument.createElement(HTML.TAG_TD);
			cell.setAttribute(HTML.ATTR_WIDTH, "100%"); //$NON-NLS-1$
			row.appendChild(cell);
	
			iterator = sourceToolBarItems.getRightItemsIterator();
			while (iterator.hasNext()) {
				toolBarItem = iterator.next();
				cell = visualDocument.createElement(HTML.TAG_TD);
				if (toolBarItem.isItem()) {
					ComponentUtil.correctAttribute(sourceElement, cell,
							CONTENTCLASS_ATTR_NAME,
							HTML.ATTR_CLASS, CSS_DR_TOOLBAR_INT + SPACE + CSS_RICH_TOOLBAR_ITEM, CSS_DR_TOOLBAR_INT + SPACE + CSS_RICH_TOOLBAR_ITEM);
					ComponentUtil.correctAttribute(sourceElement, cell,
							CONTENTSTYLE_ATTR_NAME,
							HTML.ATTR_STYLE,
							toolBarItem.isToolBarGroupItem() ? "padding: 0px;" : null, //$NON-NLS-1$
							toolBarItem.isToolBarGroupItem() ? "padding: 0px;" : null); //$NON-NLS-1$
					
					VpeChildrenInfo childrenInfo = new VpeChildrenInfo(cell);
					creationData.addChildrenInfo(childrenInfo);
					childrenInfo.addSourceChild(toolBarItem.getToolBarItem());
				} else {
					if (itemSeparatorImageUrl != null) {
						cell = visualDocument.createElement(HTML.TAG_TD);
						cell.setAttribute(HTML.ATTR_ALIGN, HTML.VALUE_ALIGN_CENTER);
						ComponentUtil.correctAttribute(sourceElement, cell,
								SEPARATORCLASS_ATTR_NAME,
								HTML.ATTR_CLASS, null, null);
						nsIDOMElement separatorImage = visualDocument.createElement(HTML.TAG_IMG);
						ComponentUtil.setImg(separatorImage, itemSeparatorImageUrl);
						cell.appendChild(separatorImage);
					}
				}
				
				row.appendChild(cell);
			}
			
			body.appendChild(row);
			visualNode.appendChild(body);
		
		return creationData;
	}

	/**
	 * 
	 * @param visualDocument
	 * @param message
	 * @return
	 */
	static nsIDOMElement createExceptionNode(nsIDOMDocument visualDocument, String message) {
		nsIDOMElement visualNode;
		
		visualNode = visualDocument.createElement(HTML.TAG_SPAN);
		visualNode.setAttribute(HTML.ATTR_STYLE, EXCEPTION_ATTR_STYLE_VALUE);
		nsIDOMText text = visualDocument.createTextNode(message);
		visualNode.appendChild(text);

		return visualNode;
	}

	
	
//	public VpeCreationData create(VpePageContext pageContext, Node sourceNode, Document visualDocument) {
//		SourceToolBarElements sourceElements = new SourceToolBarElements(sourceNode);
//		VisualToolBarElements visualElements = new VisualToolBarElements();
//
//		Element sourceElement = (Element)sourceNode;
//		
//		ComponentUtil.setCSSLink(pageContext, "toolBar/toolBar.css", "richFacesToolBar");
//
//		Element visualTable = visualDocument.createElement("table");
//		
//		ComponentUtil.correctAttribute(sourceElement, visualTable,
//				ATTR_WIDTH_NAME,
//				HTML.ATTR_WIDTH, null, "100%");
//		ComponentUtil.correctAttribute(sourceElement, visualTable,
//				ATTR_HEIGHT_NAME,
//				HTML.ATTR_HEIGHT, null, null);
//		ComponentUtil.correctAttribute(sourceElement, visualTable,
//				STYLEATTR_CLASS_NAME,
//				HTML.ATTR_CLASS, "dr-toolbar-ext rich-toolbar", "dr-toolbar-ext rich-toolbar");
//
//		String style = ComponentUtil.getHeaderBackgoundImgStyle() + ";";
//		ComponentUtil.correctAttribute(sourceElement, visualTable,
//				ATTR_STYLE_NAME,
//				HTML.ATTR_STYLE, style, style);
//
//
//		VpeCreationData creatorInfo = new VpeCreationData(visualTable);
//
//		String separatorClass = sourceElement.getAttribute("separatorClass");
//		
//		Element section = null, row = null, cell = null;
//
//		if (true || sourceElements.hasBodySection()) {
//			section = visualDocument.createElement("tbody");
//			row = visualDocument.createElement("tr");
//			row.setAttribute("valign", "middle");
//			
//			// Columns at left
//			for (int i = 0; i < sourceElements.getColumnAtLeftCount(); i++) {
//				SourceToolBarColumnElements column = sourceElements.getColumnAtLeft(i);
//
//				if (column.hasBody()) {
//					Node columnBody = column.getColumn();
//					cell = visualDocument.createElement("td");
//					ComponentUtil.correctAttribute(sourceElement, cell,
//							CONTENTATTR_CLASS_NAME,
//							HTML.ATTR_CLASS, "dr-toolbar-int rich-toolbar-item", "dr-toolbar-int rich-toolbar-item");
//					ComponentUtil.correctAttribute(sourceElement, cell,
//							CONTENTATTR_STYLE_NAME,
//							HTML.ATTR_STYLE, null, null);
//						
//					row.appendChild(cell);
//	
//					VpeChildrenInfo info = new VpeChildrenInfo(cell);
//					creatorInfo.addChildrenInfo(info);
//					
//					info.addSourceChild(column.getColumn());
//					MozillaSupports.release(cell);
//				} else if (column.isSeparator()){
//					String itemSeparator = column.getSeparatorType();
//					String separatorImageUrl = getSeparatorImageUrlString (itemSeparator);
//					
//					if (separatorImageUrl != null) {
//						// Insert separator here
//						cell = visualDocument.createElement("td");
//						cell.setAttribute("align", "center");
//						ComponentUtil.correctAttribute(sourceElement, cell,
//								SEPARATORATTR_CLASS_NAME,
//								HTML.ATTR_CLASS, null, null);
//						Element separatorImage = visualDocument.createElement("img");
//						ComponentUtil.setImg(separatorImage, separatorImageUrl);
//						cell.appendChild(separatorImage);
//						row.appendChild(cell);
//						MozillaSupports.release(separatorImage);
//						MozillaSupports.release(cell);
//					}
//				}
//			}
//			
//			// Empty column
//			cell = visualDocument.createElement("td");
//			cell.setAttribute("width", "100%");
//			row.appendChild(cell);
//			MozillaSupports.release(cell);
//
//			// Columns at right
//			for (int i = 0; i < sourceElements.getColumnAtRightCount(); i++) {
//				SourceToolBarColumnElements column = sourceElements.getColumnAtRight(i);
//				if (column.hasBody()) {
//					Node columnBody = column.getColumn();
//					cell = visualDocument.createElement("td");
//					ComponentUtil.correctAttribute(sourceElement, cell,
//							CONTENTATTR_CLASS_NAME,
//							HTML.ATTR_CLASS, "dr-toolbar-int rich-toolbar-item", "dr-toolbar-int rich-toolbar-item");
//					ComponentUtil.correctAttribute(sourceElement, cell,
//							CONTENTATTR_STYLE_NAME,
//							HTML.ATTR_STYLE, null, null);
//					row.appendChild(cell);
//	
//					VpeChildrenInfo info = new VpeChildrenInfo(cell);
//					creatorInfo.addChildrenInfo(info);
//					
//					info.addSourceChild(column.getColumn());
//					MozillaSupports.release(cell);
//				} else if (column.isSeparator()){
//					String itemSeparator = column.getSeparatorType();
//					String separatorImageUrl = getSeparatorImageUrlString (itemSeparator);
//
//					if (separatorImageUrl != null) {
//						// Insert separator here
//						cell = visualDocument.createElement("td");
//						cell.setAttribute("align", "center");
//						ComponentUtil.correctAttribute(sourceElement, cell,
//								SEPARATORATTR_CLASS_NAME,
//								HTML.ATTR_CLASS, null, null);
//						Element separatorImage = visualDocument.createElement("img");
//						ComponentUtil.setImg(separatorImage, separatorImageUrl);
//						cell.appendChild(separatorImage);
//						row.appendChild(cell);
//						MozillaSupports.release(separatorImage);
//						MozillaSupports.release(cell);
//					}
//				}
//			}
//
//			
//			section.appendChild(row);
//			MozillaSupports.release(row);
//			visualTable.appendChild(section);
//			MozillaSupports.release(section);
//			visualElements.setBodyRow(row);
//			visualElements.setBody(section);
//		}
//
//		Map visualNodeMap = pageContext.getDomMapping().getVisualMap();
//		
//		Object[] elements = new Object[2];
//		elements[0] = visualElements;
//		elements[1] = sourceElements;
//		visualNodeMap.put(this, elements);
//
//		return creatorInfo;
//	}

	public static String getSeparatorImageUrlString (String itemSeparator) {
		String separatorImageUrl = null;
		if (ITEM_SEPARATOR_DISC.equals(itemSeparator)) {
			separatorImageUrl = ITEM_SEPARATOR_DISC_URL;
		} else if (ITEM_SEPARATOR_GRID.equals(itemSeparator)) {
			separatorImageUrl = ITEM_SEPARATOR_GRID_URL;
		} else if (ITEM_SEPARATOR_LINE.equals(itemSeparator)) {
			separatorImageUrl = ITEM_SEPARATOR_LINE_URL;
		} else if (ITEM_SEPARATOR_SQUARE.equals(itemSeparator)) {
			separatorImageUrl = ITEM_SEPARATOR_SQUARE_URL;
		}
		return separatorImageUrl;
	}
	
	static String checkAndUpdateItemSeparatorName(String itemSeparator) {
		if (itemSeparator == null
				|| (itemSeparator != null && itemSeparator.length() == 0)) {
			return ITEM_SEPARATOR_NONE;
		} else if (ITEM_SEPARATOR_DISC.equals(itemSeparator)
		|| ITEM_SEPARATOR_LINE.equals(itemSeparator)
		|| ITEM_SEPARATOR_GRID.equals(itemSeparator)
		|| ITEM_SEPARATOR_SQUARE.equals(itemSeparator)
		|| ITEM_SEPARATOR_NONE.equals(itemSeparator)) {
			return itemSeparator;
		} 
			return ITEM_SEPARATOR_NONE;
	}
	
	private class SourceToolBarItem {
		private Node toolBarItem;
		private String itemSeparator;
		private boolean isToolBarItemLocationRight;
		private boolean isToolBarGroupItem;
		
		public SourceToolBarItem(Node toolBarItem) {
			this.toolBarItem = toolBarItem;
			this.itemSeparator = null;
			this.isToolBarItemLocationRight = false;
			 
			if (toolBarItem.getNodeType() == Node.ELEMENT_NODE) {
				Element element = (Element) toolBarItem;
				this.isToolBarGroupItem = element.getNodeName().endsWith(":" + RichFacesToolBarGroupTemplate.TAG_NAME); //$NON-NLS-1$
				if (isToolBarGroupItem()) {
					isToolBarItemLocationRight = RichFacesToolBarGroupTemplate
							.ATTR_LOCATION_RIGHT_VALUE
							.equals(element.getAttribute(RichFacesToolBarGroupTemplate.ATTR_LOCATION_NAME));
				}
			}
		}

		public SourceToolBarItem(String itemSeparator) {
			this.toolBarItem = null;
			this.itemSeparator = itemSeparator;
		}
		
		public Node getToolBarItem() {
			return toolBarItem;
		}

		public String getItemSeparator() {
			return itemSeparator;
		}

		public boolean isItem() {
			return toolBarItem != null;
		}
		
		public boolean isToolBarItemLocationRight() {
			return isToolBarItemLocationRight;
		}

		public boolean isToolBarGroupItem() {
			return isToolBarGroupItem;
		}
	}
	
	private class SourceToolBarItems {
		private List<SourceToolBarItem> leftToolBarItems;
		private List<SourceToolBarItem> rightToolBarItems;
		private String itemSeparator;
		private boolean itemSeparatorExists; 

		public SourceToolBarItems(Node sourceNode, String itemSeparator) {
			this.leftToolBarItems = new LinkedList<SourceToolBarItem>();
			this.rightToolBarItems = new LinkedList<SourceToolBarItem>();
			this.itemSeparator = itemSeparator;
			this.itemSeparatorExists = !(itemSeparator == null
					|| itemSeparator.length() == 0
					|| ITEMSEPARATOR_ATTR_NAME.equals(itemSeparator));
			
			init(sourceNode);
		}
		
		private void init(Node sourceNode) {
			NodeList childrenList = sourceNode.getChildNodes();
			int childrenCount = childrenList.getLength();
			for (int i=0; i < childrenCount; i++) {
				Node child = childrenList.item(i);
				if (isVisibleNode(child)) {
					SourceToolBarItem toolBarItem = new SourceToolBarItem(child);
					if (toolBarItem.isToolBarItemLocationRight()) {
						if (isItemSeparatorExists()) {
							rightToolBarItems.add(new SourceToolBarItem(itemSeparator));
						}
						rightToolBarItems.add(toolBarItem);
					} else {
						leftToolBarItems.add(toolBarItem);
						if (isItemSeparatorExists()) {
							leftToolBarItems.add(new SourceToolBarItem(itemSeparator));
						}
					}
				}
			}
			
			// yradtsevich: JBIDE-4058:
			// There should be no trailing separator after the left toolbar group
			// and no leading separator at the beginning of the right toolbar group 
			if (isItemSeparatorExists() && !leftToolBarItems.isEmpty()) {
		       leftToolBarItems.remove(leftToolBarItems.size()-1);
			}
			if (isItemSeparatorExists() && !rightToolBarItems.isEmpty()) {
				rightToolBarItems.remove(0);
			}
		}

		public Iterator<SourceToolBarItem> getLeftItemsIterator() {
			return leftToolBarItems.iterator();
		}

		public Iterator<SourceToolBarItem> getRightItemsIterator() {
			return rightToolBarItems.iterator();
		}
		
		private boolean isVisibleNode(Node node) {
			return node.getNodeType() == Node.ELEMENT_NODE
					|| (node.getNodeType() == Node.TEXT_NODE
							&& node.getNodeValue() != null
							&& node.getNodeValue().trim().length() > 0);
		}

		public boolean isItemSeparatorExists() {
			return itemSeparatorExists;
		}

		public String getItemSeparator() {
			return itemSeparator;
		}
	}
}