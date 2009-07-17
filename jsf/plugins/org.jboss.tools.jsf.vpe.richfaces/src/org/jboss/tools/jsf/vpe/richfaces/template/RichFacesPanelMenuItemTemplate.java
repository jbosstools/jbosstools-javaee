/******************************************************************************* 
 * Copyright (c) 2007 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/

package org.jboss.tools.jsf.vpe.richfaces.template;

import java.util.List;

import org.jboss.tools.jsf.vpe.richfaces.ComponentUtil;
import org.jboss.tools.vpe.editor.VpeSourceDomBuilder;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeAbstractTemplate;
import org.jboss.tools.vpe.editor.template.VpeChildrenInfo;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.util.HTML;
import org.jboss.tools.vpe.editor.util.ResourceUtil;
import org.jboss.tools.vpe.editor.util.VpeStyleUtil;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMText;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


public class RichFacesPanelMenuItemTemplate extends VpeAbstractTemplate {

	public static final String VPE_PANEL_MENU_ITEM_ID = "vpe-panel-menu-item-id"; //$NON-NLS-1$
	
	/*
	 * rich:panelMenuItem attributes
	 */
	private static final String DISABLED = "disabled"; //$NON-NLS-1$
	private static final String LABEL = "label"; //$NON-NLS-1$
	private static final String ICON = "icon"; //$NON-NLS-1$
	private static final String ICON_CLASS = "iconClass"; //$NON-NLS-1$
	private static final String ICON_STYLE = "iconStyle"; //$NON-NLS-1$
	private static final String ICON_DISABLED = "iconDisabled"; //$NON-NLS-1$
	private static final String DISABLED_CLASS = "disabledClass"; //$NON-NLS-1$
	private static final String DISABLED_STYLE = "disabledStyle"; //$NON-NLS-1$
	private static final String STYLE = "style"; //$NON-NLS-1$
	private static final String STYLE_CLASS = "styleClass"; //$NON-NLS-1$

	/*
	 *	rich:panelMenuItem css styles
	 */ 
	public static final String CSS_TOP_ITEM = "rich-pmenu-top-item"; //$NON-NLS-1$
	public static final String CSS_TOP_ITEM_ICON = "rich-pmenu-top-item-icon"; //$NON-NLS-1$
	public static final String CSS_TOP_ITEM_LABEL = "rich-pmenu-top-item-label"; //$NON-NLS-1$
	public static final String CSS_ITEM = "rich-pmenu-item"; //$NON-NLS-1$
	public static final String CSS_ITEM_ICON = "rich-pmenu-item-icon"; //$NON-NLS-1$
	public static final String CSS_ITEM_LABEL = "rich-pmenu-item-label"; //$NON-NLS-1$
	public static final String CSS_ITEM_SELECTED = "rich-pmenu-item-selected"; //$NON-NLS-1$
	public static final String CSS_HOVERED_ELEMENT = "rich-pmenu-hovered-element"; //$NON-NLS-1$
	public static final String CSS_DISABLED_ELEMENT = "rich-pmenu-disabled-element"; //$NON-NLS-1$
	
	/*
	 *	rich:panelMenuItem default css styles
	 */ 
	private static final String DR_NOWARP_CLASS = "dr-pmenu-nowrap"; //$NON-NLS-1$
	private static final String DR_TOP_DIV = "dr-pmenu-top-self-div"; //$NON-NLS-1$
	
	private static final String IMG_POINTS_SRC = "/panelMenuItem/points.gif"; //$NON-NLS-1$
	private static final String IMG_SPACER_SRC = "/panelMenuItem/spacer.gif"; //$NON-NLS-1$
	private static final String STYLE_PATH = "/panelMenuItem/style.css"; //$NON-NLS-1$
	
	private static final String SPACE = " "; //$NON-NLS-1$
	private static final String EMPTY = ""; //$NON-NLS-1$
	private static final String TRUE = "true"; //$NON-NLS-1$
	private static final String RIGHT = "right"; //$NON-NLS-1$
	private static final String LEFT = "left"; //$NON-NLS-1$
	private static final String VSPACE = "vspace"; //$NON-NLS-1$
	private static final String HSPACE = "hspace"; //$NON-NLS-1$
	private static final String NO_SIZE_VALUE = "0"; //$NON-NLS-1$
	private static final String DEFAULT_SIZE_VALUE = "16"; //$NON-NLS-1$
	
	private static final String COMPONENT_NAME = "panelMenuItem"; //$NON-NLS-1$
	private static final String PANEL_MENU_END_TAG = ":panelMenu"; //$NON-NLS-1$
	private static final String PANEL_MENU_GROUP_END_TAG = ":panelMenuGroup"; //$NON-NLS-1$
	private static final String MARGIN_TOP = "margin-top: 3px; "; //$NON-NLS-1$
	private static final String WIDTH_100_PERCENTS = "width: 100%; "; //$NON-NLS-1$
	
	/*
	 *	rich:panelMenu attributes for items
	 */ 
	private static String pm_disabled;
	private static String pm_iconItem;
	private static String pm_iconDisabledItem;
	private static String pm_iconItemPosition;
	private static String pm_iconTopItem;
	private static String pm_iconTopDisabledItem;
	private static String pm_iconItemTopPosition;
	
	/*
	 *	rich:panelMenu style classes for items
	 */ 
	private static String pm_disabledItemClass;
	private static String pm_disabledItemStyle;
	private static String pm_topItemClass;
	private static String pm_topItemStyle;
	private static String pm_itemClass;
	private static String pm_itemStyle;
	
	/*
	 * rich:panelMenuItem attributes
	 */
	private static String pmi_disabled;
	private static String pmi_icon;
	private static String pmi_iconClass;
	private static String pmi_iconStyle;
	private static String pmi_iconDisabled;
	private static String pmi_disabledClass;
	private static String pmi_disabledStyle;
	private static String pmi_style;
	private static String pmi_styleClass;

	public VpeCreationData create(VpePageContext pageContext, Node sourceNode,
			nsIDOMDocument visualDocument) {
		nsIDOMElement creationDataDiv = visualDocument
				.createElement(HTML.TAG_DIV);
		VpeCreationData creationData = new VpeCreationData(creationDataDiv); 
		Element itemSourceElement = (Element) sourceNode;
		Element srcElement  = null;
		//added by estherbin fixed https://jira.jboss.org/jira/browse/JBIDE-1605
//        if ((itemSourceElement.getUserData(VpeVisualDomBuilder.SRC_NODE) != null)
//                && (itemSourceElement.getUserData(VpeVisualDomBuilder.SRC_NODE) instanceof Element)) {
//            srcElement = (Element) itemSourceElement.getUserData(VpeVisualDomBuilder.SRC_NODE);
//        }
		
			
		ComponentUtil.setCSSLink(pageContext, STYLE_PATH, COMPONENT_NAME);
		
	    //added by estherbin fixed https://jira.jboss.org/jira/browse/JBIDE-1605
        final Element elementToPass = ((srcElement != null) ? srcElement : itemSourceElement);
		
		Element anySuitableParent = getItemParent(elementToPass, false);
		Element panelMenuParent = getItemParent(elementToPass, true);
		
		String childId = (String) elementToPass.getUserData(VPE_PANEL_MENU_ITEM_ID);
		//fix for JBIDE-3737
		if(childId==null) {
			childId = "";//$NON-NLS-1$
		}
		readPanelMenuAttributes(panelMenuParent);
		readPanelMenuItemAttributes(itemSourceElement);
		
		creationDataDiv.setAttribute(HTML.ATTR_CLASS, DR_TOP_DIV);
		creationDataDiv.setAttribute(HTML.ATTR_BORDER, "0"); //$NON-NLS-1$
		creationDataDiv.setAttribute(HTML.ATTR_STYLE, MARGIN_TOP);
		
		boolean childOfPanelMenu = anySuitableParent != null ? anySuitableParent
				.getNodeName().endsWith(PANEL_MENU_END_TAG)
				: false;
		boolean childOfPanelMenuGroup = anySuitableParent != null ? anySuitableParent
				.getNodeName().endsWith(PANEL_MENU_GROUP_END_TAG)
				: false;
		
		if (childOfPanelMenu || childOfPanelMenuGroup) {
			creationDataDiv.setAttribute("vpeSupport", COMPONENT_NAME); //$NON-NLS-1$
			
			nsIDOMElement table = visualDocument
					.createElement(HTML.TAG_TABLE);
			creationDataDiv.appendChild(table);

			table.setAttribute(HTML.ATTR_CELLPADDING,
					NO_SIZE_VALUE);
			table.setAttribute(HTML.ATTR_CELLSPACING,
					NO_SIZE_VALUE);
			table.setAttribute(HTML.ATTR_BORDER,
					NO_SIZE_VALUE);

			nsIDOMElement tr = visualDocument
					.createElement(HTML.TAG_TR);
			table.appendChild(tr);
			
			/*
			 * Add indentation for nested items
			 */
			String[] ids = childId.split(RichFacesPanelMenuGroupTemplate.GROUP_COUNT_SEPARATOR);
			if (ids.length > 0) {
				for (int i = 1; i <= ids.length; i++) {
					/*
					 * Skip indentation in top menu items
					 */
					if (EMPTY.equalsIgnoreCase(ids[0])) {
						continue;
					}
					nsIDOMElement spacerTd = visualDocument
							.createElement(HTML.TAG_TD);
					nsIDOMElement spacerImg = visualDocument
							.createElement(HTML.TAG_IMG);
					spacerTd.appendChild(spacerImg);
					ComponentUtil.setImg(spacerImg, IMG_SPACER_SRC);
					setDefaultImgAttributes(spacerImg);
					spacerTd.setAttribute(HTML.ATTR_HEIGHT,
							DEFAULT_SIZE_VALUE);
					spacerTd.setAttribute(HTML.ATTR_WIDTH,
							DEFAULT_SIZE_VALUE);
					tr.appendChild(spacerTd);
				}
			}
			

			nsIDOMElement tdNowrapLeft = visualDocument
					.createElement(HTML.TAG_TD);
			tr.appendChild(tdNowrapLeft);
			

			nsIDOMElement tdLabel = visualDocument
					.createElement(HTML.TAG_TD);
			tr.appendChild(tdLabel);
			
			/*
			 * Item label routine.
			 */
			Attr labelAttr = null;
			String labelValue = EMPTY;
			String bundleValue = EMPTY;
			String resultValue = EMPTY;
			if (itemSourceElement.hasAttribute(LABEL)) {
				labelAttr = itemSourceElement.getAttributeNode(LABEL);
			}
			if (null != labelAttr) {
				labelValue = labelAttr.getNodeValue();
				bundleValue = ResourceUtil.getBundleValue(pageContext,
						labelAttr.getValue());
			}
			
			if (ComponentUtil.isNotBlank(labelValue)) {
				if (ComponentUtil.isNotBlank(bundleValue)) {
					if (!labelValue.equals(bundleValue)) {
						resultValue = bundleValue;
					} else {
						resultValue = labelValue;
					}
				} else {
					resultValue = labelValue;
				}
			} else {
				if (ComponentUtil.isNotBlank(bundleValue)) {
					resultValue = bundleValue;
				} else {
					resultValue = EMPTY;
				}
			}
			nsIDOMText text = visualDocument.createTextNode(resultValue);
			
			tdLabel.appendChild(text);

			nsIDOMElement tdRight = visualDocument
					.createElement(HTML.TAG_TD);
			tr.appendChild(tdRight);

			nsIDOMElement imgIcon = visualDocument
					.createElement(HTML.TAG_IMG);
			setDefaultImgAttributes(imgIcon);
			setIcon(childOfPanelMenu, pageContext, imgIcon);

			nsIDOMElement imgSpacer1 = visualDocument
					.createElement(HTML.TAG_IMG);
			setDefaultImgAttributes(imgSpacer1);
			ComponentUtil.setImg(imgSpacer1, IMG_SPACER_SRC);

			tdNowrapLeft.appendChild(imgSpacer1);

			nsIDOMElement imgSpacer = visualDocument
					.createElement(HTML.TAG_IMG);
			setDefaultImgAttributes(imgSpacer);
			ComponentUtil.setImg(imgSpacer, IMG_SPACER_SRC);

			nsIDOMElement iconCell = tdNowrapLeft;
			nsIDOMElement emptyCell = tdRight;
			if (!childOfPanelMenu && ComponentUtil.isNotBlank(pm_iconItemPosition)) {
				if (RIGHT.equalsIgnoreCase(pm_iconItemPosition)) {
					/*
					 * Set icon image on the right
					 */
					iconCell = tdRight;
					emptyCell = tdNowrapLeft;
				}
			} 
			if (childOfPanelMenu && ComponentUtil.isNotBlank(pm_iconItemTopPosition)) {
				if (RIGHT.equalsIgnoreCase(pm_iconItemTopPosition)) {
					/*
					 * Set icon image on the right
					 */
					iconCell = tdRight;
					emptyCell = tdNowrapLeft;
				} else if (LEFT.equalsIgnoreCase(pm_iconItemTopPosition)) {
					iconCell = tdNowrapLeft;
					emptyCell = tdRight;
				}
			}
			iconCell.appendChild(imgIcon);
			emptyCell.appendChild(imgSpacer);
			
			setItemClassAndStyle(childOfPanelMenu, table, tr, iconCell, imgIcon, tdLabel, emptyCell);
			
			List<Node> children = ComponentUtil.getChildren(itemSourceElement);
			
			nsIDOMElement childSpan = visualDocument
				.createElement(HTML.TAG_SPAN);
			VpeChildrenInfo childrenInfo = new VpeChildrenInfo(childSpan);
			
			if (!children.isEmpty()) {
				childrenInfo = new VpeChildrenInfo(tdLabel);
				creationData.addChildrenInfo(childrenInfo);
				for (Node child : children) {
					if (!(child.getNodeName().endsWith(PANEL_MENU_GROUP_END_TAG) || child 
							.getNodeName().endsWith(PANEL_MENU_END_TAG))) {
						childrenInfo.addSourceChild(child);
					}
				}
			}
			
			if (childrenInfo.getSourceChildren() == null) {
				creationData.addChildrenInfo(childrenInfo);
			}
			
		}
		return creationData;
	}

	public boolean recreateAtAttrChange(VpePageContext pageContext,
			Element sourceElement, nsIDOMDocument visualDocument,
			nsIDOMElement visualNode, Object data, String name, String value) {
		return true;
	}

	private static void setDefaultImgAttributes(nsIDOMElement element) {
		element.setAttribute(VSPACE, NO_SIZE_VALUE);
		element.setAttribute(HSPACE, NO_SIZE_VALUE);
		element.setAttribute(HTML.ATTR_HEIGHT,
				DEFAULT_SIZE_VALUE);
		element.setAttribute(HTML.ATTR_WIDTH,
				DEFAULT_SIZE_VALUE);
	}

	/**
	 * Gets the specified icon from panelMenu or panelMenuGroupItem
	 * 
	 * @param pm_icon the panelMenu icon attribute
	 * @param pmi_icon the panelMenuGroupItem icon attribute
	 * 
	 * @return the specified icon
	 */
	private static String[] getSpecifiedIcon(String pm_icon, String pmi_icon) {
		String[] imgPath = {EMPTY, EMPTY};
		if (ComponentUtil.isNotBlank(pmi_icon)) {
			/*
			 * Icon was set in the panelMenuItem attribute.
			 */
			imgPath[0] = pmi_icon;
			if (RichFacesPanelMenuGroupTemplate.DEFAULT_ICON_MAP.containsKey(pmi_icon)) {
				imgPath[1] = RichFacesPanelMenuGroupTemplate.DEFAULT_ICON_MAP.get(pmi_icon);
			}
		} else if (ComponentUtil.isNotBlank(pm_icon)) {
			/*
			 * Icon was set in the panelMenu attribute.
			 */
			imgPath[0] = pm_icon;
			if (RichFacesPanelMenuGroupTemplate.DEFAULT_ICON_MAP.containsKey(pm_icon)) {
				imgPath[1] = RichFacesPanelMenuGroupTemplate.DEFAULT_ICON_MAP.get(pm_icon);
			}
		}
		return imgPath;
	}
	
	/**
	 * Sets the src attribute value for the image
	 * 
	 * @param childOfPanelMenu if the item is the child of panelMenu
	 * @param pageContext the page context
	 * @param img the image
	 */
	private static void setIcon(boolean childOfPanelMenu, VpePageContext pageContext, nsIDOMElement img) {
		
		/*
		 * The first array element contains specified icon path or default icon name.
		 * The second array element contains default icon path.
		 */
		String[] imgPath = {EMPTY, EMPTY};
		
		if (childOfPanelMenu) {
			if (TRUE.equalsIgnoreCase(pmi_disabled)) {
				imgPath = getSpecifiedIcon(pm_iconTopDisabledItem, pmi_iconDisabled);
			} else {
				imgPath = getSpecifiedIcon(pm_iconTopItem, pmi_icon);
			}
		} else {
			if (TRUE.equalsIgnoreCase(pmi_disabled)) {
				imgPath = getSpecifiedIcon(pm_iconDisabledItem, pmi_iconDisabled);
			} else {
				imgPath = getSpecifiedIcon(pm_iconItem, pmi_icon);
			}
		}
		
		if (EMPTY.equalsIgnoreCase(imgPath[0])) {
			/*
			 * Icon wasn't set. Use default image.
			 */
			ComponentUtil.setImg(img, IMG_POINTS_SRC);
		} else {
			/*
			 * Set specified icon.
			 */
			if (RichFacesPanelMenuGroupTemplate.DEFAULT_ICON_MAP.containsKey(imgPath[0])) {
				ComponentUtil.setImg(img, imgPath[1]);
			} else {
				String imgFullPath = VpeStyleUtil.addFullPathToImgSrc(imgPath[0], pageContext, true);
				img.setAttribute(HTML.ATTR_SRC, imgFullPath);
			}
		}
	}
	
	/**
	 * Sets the item class and style for the item.
	 * 
	 * @param childOfPanelMenu if the item is the child of panelMenu
	 * @param table the table
	 * @param tr the table row
	 * @param iconCell the first table row column image
	 * @param labelCell the second table row column
	 * @param emptyCell the third table row column image
	 */
	private static void setItemClassAndStyle(boolean childOfPanelMenu,
			nsIDOMElement table,
			nsIDOMElement tr,
			nsIDOMElement iconCell,
			nsIDOMElement imgIcon,
			nsIDOMElement labelCell,
			nsIDOMElement emptyCell) {
		
		String tableStyle = MARGIN_TOP;
		String iconCellStyle = EMPTY;
		String labelCellStyle = WIDTH_100_PERCENTS;
		String emptyCellStyle = EMPTY;
		
		String tableClass = EMPTY;
		String trClass = EMPTY;
		String iconCellClass = EMPTY;
		String imgIconClass = EMPTY;
		String labelCellClass = EMPTY;
		String emptyCellClass = EMPTY;
		
		if (ComponentUtil.isNotBlank(pmi_styleClass)) {
			tableClass += SPACE + pmi_styleClass;
		}
		if (ComponentUtil.isNotBlank(pmi_style)) {
			tableStyle += SPACE + pmi_style;
		}
		
		tableClass += SPACE + CSS_ITEM;
		
		if (TRUE.equalsIgnoreCase(pm_disabled)) {
			if (childOfPanelMenu) {
				tableClass += SPACE + CSS_TOP_ITEM;
				iconCellClass += SPACE + CSS_TOP_ITEM_ICON; 
				labelCellClass += SPACE + CSS_TOP_ITEM_LABEL; 
				if (ComponentUtil.isNotBlank(pm_disabledItemClass)) {
					tableClass += SPACE + pm_disabledItemClass;
				}
				if (ComponentUtil.isNotBlank(pm_topItemStyle)) {
					tableStyle += SPACE + pm_topItemStyle;
				}
			}
		}
		
		if (TRUE.equalsIgnoreCase(pmi_disabled)) {
//			tableClass += SPACE + CSS_DISABLED_ELEMENT;
			if ((ComponentUtil.isNotBlank(pm_disabledItemClass)) 
				&& !(TRUE.equalsIgnoreCase(pm_disabled))) {
				tableClass += SPACE + pm_disabledItemClass;
			}
			if (ComponentUtil.isNotBlank(pm_disabledItemStyle)) {
				tableStyle += SPACE + pm_disabledItemStyle;
			}
			if (ComponentUtil.isNotBlank(pmi_disabledClass)) {
				tableClass += SPACE + pmi_disabledClass;
			}
			if (ComponentUtil.isNotBlank(pmi_disabledStyle)) {
				tableStyle += SPACE + pmi_disabledStyle;
			}
		} 
		
		if (!(TRUE.equalsIgnoreCase(pm_disabled))
				&& (!(TRUE.equalsIgnoreCase(pmi_disabled)))) {
//			iconCellClass = DR_NOWARP_CLASS + SPACE + CSS_ITEM_ICON;
			iconCellClass = DR_NOWARP_CLASS;
			imgIconClass = CSS_ITEM_ICON;
			labelCellClass = CSS_ITEM_LABEL;
			emptyCellClass = DR_NOWARP_CLASS;
			
			if (ComponentUtil.isNotBlank(pmi_iconClass)) {
				imgIconClass += SPACE + pmi_iconClass;
			}
			if (ComponentUtil.isNotBlank(pmi_iconStyle)) {
				iconCellStyle += SPACE + pmi_iconStyle;
			}
			if (childOfPanelMenu) {
				tableClass += SPACE + CSS_TOP_ITEM;
				imgIconClass += SPACE + CSS_TOP_ITEM_ICON; 
				labelCellClass += SPACE + CSS_TOP_ITEM_LABEL; 
				if (ComponentUtil.isNotBlank(pm_topItemClass)) {
					tableClass += SPACE + pm_topItemClass;
				}
				if (ComponentUtil.isNotBlank(pm_topItemStyle)) {
					tableStyle += SPACE + pm_topItemStyle;
				}
			} else {
				if (ComponentUtil.isNotBlank(pm_itemClass)) {
					tableClass += SPACE + pm_itemClass;
				}
				if (ComponentUtil.isNotBlank(pm_itemStyle)) {
					tableStyle += SPACE + pm_itemStyle;
				}
			}
		}
		
		table.setAttribute(HTML.ATTR_STYLE, tableStyle);
		iconCell.setAttribute(HTML.ATTR_STYLE, iconCellStyle);
		labelCell.setAttribute(HTML.ATTR_STYLE, labelCellStyle);
		emptyCell.setAttribute(HTML.ATTR_STYLE, emptyCellStyle);
		
		table.setAttribute(HTML.ATTR_CLASS, tableClass);
		tr.setAttribute(HTML.ATTR_CLASS, trClass);
		iconCell.setAttribute(HTML.ATTR_CLASS, iconCellClass);
		imgIcon.setAttribute(HTML.ATTR_CLASS, imgIconClass);
		labelCell.setAttribute(HTML.ATTR_CLASS, labelCellClass);
		emptyCell.setAttribute(HTML.ATTR_CLASS, emptyCellClass);
	}
	
	/**
	 * Gets the panel menu item parent.
	 * 
	 * @param sourceItemElement the source item element
	 * @param findOnlyPanelMenuParent flag to find only panel menu parent
	 * 
	 * @return the item parent, can return null
	 */
	private static final Element getItemParent(Element sourceItemElement,
			boolean findOnlyPanelMenuParent) {
		Element parent = null;
		Element currentElement = sourceItemElement;
		
		while ((currentElement.getParentNode() != null)
				&& (currentElement.getParentNode().getNodeType() == Node.ELEMENT_NODE)) {

			currentElement = parent = (Element) currentElement.getParentNode();

			if (findOnlyPanelMenuParent) {
				if (parent.getNodeName().endsWith(PANEL_MENU_END_TAG)) {
					break;
				}
			} else {
				if (parent.getNodeName().endsWith(PANEL_MENU_END_TAG)
						|| parent.getNodeName().endsWith(
								PANEL_MENU_GROUP_END_TAG)) {
					break;
				}
			}

		}
		return parent;
	}
	
	/**
	 * Read attributes from the source element.
	 * 
	 * @param sourceNode the source node
	 */
	private static void readPanelMenuAttributes(Element sourceParentElement) {
		
		if (null == sourceParentElement) {
			return;
		}
		
		/*
		 *	rich:panelMenu attributes for items
		 */ 
		pm_disabled = sourceParentElement.getAttribute(HTML.ATTR_DISABLED);
		pm_iconItem = sourceParentElement.getAttribute(RichFacesPanelMenuTemplate.ICON_ITEM);
		pm_iconDisabledItem = sourceParentElement.getAttribute(RichFacesPanelMenuTemplate.ICON_DISABLED_ITEM);
		pm_iconItemPosition = sourceParentElement.getAttribute(RichFacesPanelMenuTemplate.ICON_ITEM_POSITION);
		pm_iconTopItem = sourceParentElement.getAttribute(RichFacesPanelMenuTemplate.ICON_TOP_ITEM);
		pm_iconTopDisabledItem = sourceParentElement.getAttribute(RichFacesPanelMenuTemplate.ICON_TOP_DISABLED_ITEM);
		pm_iconItemTopPosition = sourceParentElement.getAttribute(RichFacesPanelMenuTemplate.ICON_ITEM_TOP_POSITION);
		
		/*
		 *	rich:panelMenu style classes for items
		 */ 
		pm_disabledItemClass = sourceParentElement.getAttribute(RichFacesPanelMenuTemplate.DISABLED_ITEM_CLASS);
		pm_disabledItemStyle = sourceParentElement.getAttribute(RichFacesPanelMenuTemplate.DISABLED_ITEM_STYLE);
		pm_topItemClass = sourceParentElement.getAttribute(RichFacesPanelMenuTemplate.TOP_ITEM_CLASS);
		pm_topItemStyle = sourceParentElement.getAttribute(RichFacesPanelMenuTemplate.TOP_ITEM_STYLE);
		pm_itemClass = sourceParentElement.getAttribute(RichFacesPanelMenuTemplate.ITEM_CLASS);
		pm_itemStyle = sourceParentElement.getAttribute(RichFacesPanelMenuTemplate.ITEM_STYLE);
	
	}
	
	/**
	 * Read attributes from the source element.
	 * 
	 * @param sourceNode the source node
	 */
	private static void readPanelMenuItemAttributes(Element sourceElement) {
		
		if (null == sourceElement) {
			return;
		}
		
		/*
		 * rich:panelMenuItem attributes
		 */
		pmi_disabled = sourceElement.getAttribute(DISABLED);
		pmi_icon = sourceElement.getAttribute(ICON);
		pmi_iconClass = sourceElement.getAttribute(ICON_CLASS);
		pmi_iconStyle = sourceElement.getAttribute(ICON_STYLE);
		pmi_iconDisabled = sourceElement.getAttribute(ICON_DISABLED);
		pmi_disabledClass = sourceElement.getAttribute(DISABLED_CLASS);
		pmi_disabledStyle = sourceElement.getAttribute(DISABLED_STYLE);
		pmi_style = sourceElement.getAttribute(STYLE);
		pmi_styleClass = sourceElement.getAttribute(STYLE_CLASS);
		
	}
    
    /* (non-Javadoc)
     * @see org.jboss.tools.vpe.editor.template.VpeAbstractTemplate#setSourceAttributeSelection(org.jboss.tools.vpe.editor.context.VpePageContext, org.w3c.dom.Element, int, int, java.lang.Object)
     */
    @Override
    public void setSourceAttributeSelection(VpePageContext pageContext,
	    Element sourceElement, int offset, int length, Object data) {
	VpeSourceDomBuilder sourceBuilder = pageContext.getSourceBuilder();
	sourceBuilder.setSelection(sourceElement, 0, 0);
    }
}