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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.tools.jsf.vpe.richfaces.ComponentUtil;
import org.jboss.tools.jsf.vpe.richfaces.HtmlComponentUtil;
import org.jboss.tools.jsf.vpe.richfaces.template.RichFacesToolBarTemplate.SourceToolBarColumnElements;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeAbstractTemplate;
import org.jboss.tools.vpe.editor.template.VpeChildrenInfo;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.util.MozillaSupports;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class RichFacesToolBarGroupTemplate extends VpeAbstractTemplate {
  
	@Override
	public boolean isRecreateAtAttrChange(VpePageContext pageContext, Element sourceElement, Document visualDocument, Node visualNode, Object data, String name, String value) {
		return true;
	}

	public VpeCreationData create(VpePageContext pageContext, Node sourceNode, Document visualDocument) {
		Map visualNodeMap = pageContext.getDomMapping().getVisualMap();
		
		RichFacesToolBarTemplate.SourceToolBarElements sourceElements = new RichFacesToolBarTemplate.SourceToolBarElements(sourceNode);
		RichFacesToolBarTemplate.VisualToolBarElements visualElements = new RichFacesToolBarTemplate.VisualToolBarElements();

		Element sourceElement = (Element)sourceNode;
		
		Element visualRow = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_SPAN);
		visualRow.setAttribute("style", "border: 0px; margin: 0px 0px 0px 0px; padding: 0px 0px 0px 0px;");

 		VpeCreationData creatorInfo = new VpeCreationData(visualRow);

		Element cell = null;

		if (true || sourceElements.hasBodySection()) {

			// Columns at left
			for (int i = 0; i < sourceElements.getColumnAtLeftCount(); i++) {
				SourceToolBarColumnElements column = sourceElements.getColumnAtLeft(i);

				if (column.hasBody()) {
					Node columnBody = column.getColumn();
					cell = visualDocument.createElement("td");
					ComponentUtil.correctAttribute(sourceElement, cell,
							RichFacesToolBarTemplate.CONTENTCLASS_ATTR_NAME,
							HtmlComponentUtil.HTML_CLASS_ATTR, "dr-toolbar-int rich-toolbar-item", "dr-toolbar-int rich-toolbar-item");
					ComponentUtil.correctAttribute(sourceElement, cell,
							RichFacesToolBarTemplate.CONTENTSTYLE_ATTR_NAME,
							HtmlComponentUtil.HTML_STYLE_ATTR, null, null);
						
					visualRow.appendChild(cell);
	
					VpeChildrenInfo info = new VpeChildrenInfo(cell);
					creatorInfo.addChildrenInfo(info);
					
					info.addSourceChild(column.getColumn());
					MozillaSupports.release(cell);
				} else if (column.isSeparator()){
					String itemSeparator = column.getSeparatorType();
					String separatorImageUrl = RichFacesToolBarTemplate.getSeparatorImageUrlString (itemSeparator);
					
					if (separatorImageUrl != null) {
						// Insert separator here
						cell = visualDocument.createElement("td");
						cell.setAttribute("align", "center");
						ComponentUtil.correctAttribute(sourceElement, cell,
								RichFacesToolBarTemplate.SEPARATORCLASS_ATTR_NAME,
								HtmlComponentUtil.HTML_CLASS_ATTR, null, null);
						Element separatorImage = visualDocument.createElement("img");
						ComponentUtil.setImg(separatorImage, separatorImageUrl);
						cell.appendChild(separatorImage);
						visualRow.appendChild(cell);
						MozillaSupports.release(separatorImage);
						MozillaSupports.release(cell);
					}
				}
			}
			
			// Columns at right
			for (int i = 0; i < sourceElements.getColumnAtRightCount(); i++) {
				SourceToolBarColumnElements column = sourceElements.getColumnAtRight(i);
				if (column.hasBody()) {
					Node columnBody = column.getColumn();
					cell = visualDocument.createElement("td");
					ComponentUtil.correctAttribute(sourceElement, cell,
							RichFacesToolBarTemplate.CONTENTCLASS_ATTR_NAME,
							HtmlComponentUtil.HTML_CLASS_ATTR, "dr-toolbar-int rich-toolbar-item", "dr-toolbar-int rich-toolbar-item");
					ComponentUtil.correctAttribute(sourceElement, cell,
							RichFacesToolBarTemplate.CONTENTSTYLE_ATTR_NAME,
							HtmlComponentUtil.HTML_STYLE_ATTR, null, null);
					visualRow.appendChild(cell);
	
					VpeChildrenInfo info = new VpeChildrenInfo(cell);
					creatorInfo.addChildrenInfo(info);
					
					info.addSourceChild(column.getColumn());
					MozillaSupports.release(cell);
				} else if (column.isSeparator()){
					String itemSeparator = column.getSeparatorType();
					String separatorImageUrl = RichFacesToolBarTemplate.getSeparatorImageUrlString (itemSeparator);

					if (separatorImageUrl != null) {
						// Insert separator here
						cell = visualDocument.createElement("td");
						cell.setAttribute("align", "center");
						ComponentUtil.correctAttribute(sourceElement, cell,
								RichFacesToolBarTemplate.SEPARATORCLASS_ATTR_NAME,
								HtmlComponentUtil.HTML_CLASS_ATTR, null, null);
						Element separatorImage = visualDocument.createElement("img");
						ComponentUtil.setImg(separatorImage, separatorImageUrl);
						cell.appendChild(separatorImage);
						visualRow.appendChild(cell);
						MozillaSupports.release(separatorImage);
						MozillaSupports.release(cell);
					}
				}
			}
			
			// Empty column
			cell = visualDocument.createElement("td");
			cell.setAttribute("width", "100%");
			visualRow.appendChild(cell);
			MozillaSupports.release(cell);
		}

		Object[] elements = new Object[2];
		elements[0] = visualElements;
		elements[1] = sourceElements;
		visualNodeMap.put(this, elements);

		return creatorInfo;
	}

	private VisualToolBarGroupElements getVisualToolBarElements(Map visualNodeMap) {
		if (visualNodeMap != null) {
			Object o = visualNodeMap.get(this);
			try {
				if (o != null && o instanceof Object[] && ((Object[])o)[0] instanceof VisualToolBarGroupElements) {
					return (VisualToolBarGroupElements)((Object[])o)[0];
				}
			} catch (Exception e) {
			}
		}
		return null;
	}

	private void setRowDisplayStyle(Element row, boolean visible) {
		if (row != null) {
			row.setAttribute("style", "display:" + (visible ? "" : "none"));
		}
	}

	class SourceToolBarGroupElements {
		private List columns;
		Map attributes;
		
		public SourceToolBarGroupElements(Node sourceNode) {
			init(sourceNode);
			initAttributes(sourceNode);
		}
		
		void initAttributes(Node sourceNode) {
			NamedNodeMap attrs = sourceNode.getAttributes();
			attributes = new HashMap<String, String>();
			for (int i = 0; attrs != null && i < attrs.getLength(); i++) {
				Node attribute = attrs.item(i);
				attributes.put(attribute.getNodeName(), attribute.getNodeValue());
			}
		}
		
		void init (Node sourceNode) {
			NodeList children = sourceNode.getChildNodes();

			int cnt = children != null ? children.getLength() : 0;
			if (cnt > 0) {
				for (int i = 0; i < cnt; i++) {
					Node child = children.item(i);
					if (child.getNodeType() == Node.ELEMENT_NODE) {
						Element childElement = (Element)child;
						
						if (childElement.getNodeName().endsWith(":toolBarGroup")) {
							initToolBarGroup(childElement);
						} else {
							if (columns == null) columns = new ArrayList();
							columns.add(new SourceToolBarGroupColumnElements(child));
							columns.add(new SourceToolBarGroupColumnElements(null));
						}
					} else if (child.getNodeType() == Node.TEXT_NODE) {
						String text = child.getNodeValue();
						text = (text == null ? null : text.trim());
						if (text != null && text.length() > 0) {
							if (columns == null) columns = new ArrayList();
							columns.add(new SourceToolBarGroupColumnElements(child));
							columns.add(new SourceToolBarGroupColumnElements(null));
						}
					}
				}
			}
		}

		void initToolBarGroup(Element sourceElement) {
			if (sourceElement == null) return;
			NodeList children = sourceElement.getChildNodes();

			int cnt = children != null ? children.getLength() : 0;
			if (cnt > 0) {
				for (int i = 0; i < cnt; i++) {
					Node child = children.item(i);
					if (child.getNodeType() == Node.ELEMENT_NODE) {
						Element childElement = (Element)child;
						
						if (childElement.getNodeName().endsWith(":toolBarGroup")) {
							initToolBarGroup(childElement);
						} else {
							if (columns == null) columns = new ArrayList();
							columns.add(new SourceToolBarGroupColumnElements(child));
						}
					} else if (child.getNodeType() == Node.TEXT_NODE) {
						String text = child.getNodeValue();
						text = (text == null ? null : text.trim());
						if (text != null && text.length() > 0) {
							if (columns == null) columns = new ArrayList();
							columns.add(new SourceToolBarGroupColumnElements(child));
						}
					}
				}
			}
		}
		
		public SourceToolBarGroupColumnElements getColumn(int index) {
			if (columns != null && index < getColumnCount()) return (SourceToolBarGroupColumnElements)columns.get(index);
			return null;
		}

		public int getColumnCount() {
			if (columns != null) return columns.size();
			return 0;
		}

		public boolean hasColspan() {
			return (getColumnCount()) >= 2;
		}

		public boolean hasBodySection() {
			for (int i = 0; i < getColumnCount(); i++) {
				SourceToolBarGroupColumnElements column = getColumn(i);
				if (column.hasBody()) return true;
			}
			return false;
		}

		Map getAttributes() {
			return attributes;
		}
		
		String getAttributeValue(String name) {
			return (String)attributes.get(name);
		}
	}
	
	
	
	public static class SourceToolBarGroupColumnElements {
		private Node column;
		private List body;

		public SourceToolBarGroupColumnElements(Node columnNode) {
			init(columnNode);
		}

		private void init(Node columnNode) {
			column = columnNode;
			if (columnNode != null) {
				body = new ArrayList();
				body.add(columnNode);
			}
		}

		public boolean hasBody() {
			return body != null && body.size() > 0;
		}

		public List getBody() {
			return body;
		}

		public void setBody(List body) {
			this.body = body;
		}

		public void setColumn(Node column) {
			this.column = column;
		}

		public int getBodyElementsCount() {
			if (body != null) return body.size();
			return 0;
		}

		public Node getBodyElement(int index) {
			if (body != null) return (Node)body.get(index);
			return null;
		}

		public Node getColumn() {
			return column;
		}
	}

	public static class VisualToolBarGroupElements {
		private Element body;
		private Element bodyRow;
		private List columns;

		public VisualToolBarGroupElements() {
		}
		public VisualToolBarGroupElements(Element body) {
			this.body = body;
		}
		
		private VisualToolBarGroupColumnElements getColumn(int index) {
			if (columns != null && index < getColumnCount()) return (VisualToolBarGroupColumnElements)columns.get(index);
			return null;
		}

		private int getColumnCount() {
			if (columns != null) return columns.size();
			return 0;
		}

		private List getColumns() {
			if (columns == null) columns = new ArrayList();
			return columns;
		}

		public Element getBody() {
			return body;
		}

		public void setBody(Element body) {
			this.body = body;
		}

		public Element getBodyRow() {
			return bodyRow;
		}

		public void setBodyRow(Element bodyRow) {
			this.bodyRow = bodyRow;
		}
	}

	public static class VisualToolBarGroupColumnElements {
		private Element bodyCell;

		private boolean isEmpty() {
			return bodyCell == null;
		}

		public Element getBodyCell() {
			return bodyCell;
		}

		public void setBodyCell(Element bodyCell) {
			this.bodyCell = bodyCell;
		}
	}
}

