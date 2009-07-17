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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jboss.tools.jsf.vpe.richfaces.ComponentUtil;
import org.jboss.tools.jsf.vpe.richfaces.template.RichFacesFileUploadTemplate.StyleClasses;
import org.jboss.tools.jsf.vpe.richfaces.template.util.RichFaces;
import org.jboss.tools.vpe.editor.VpeSourceDomBuilder;
import org.jboss.tools.vpe.editor.VpeVisualDomBuilder;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeAbstractTemplate;
import org.jboss.tools.vpe.editor.template.VpeChildrenInfo;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.template.VpeToggableTemplate;
import org.jboss.tools.vpe.editor.util.Constants;
import org.jboss.tools.vpe.editor.util.HTML;
import org.jboss.tools.vpe.editor.util.ResourceUtil;
import org.jboss.tools.vpe.editor.util.VpeStyleUtil;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMText;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class RichFacesPanelMenuGroupTemplate extends VpeAbstractTemplate implements
VpeToggableTemplate {

	public static final String VPE_EXPANDED_TOGGLE_IDS = "vpe-expanded-toggle-ids"; //$NON-NLS-1$
	
	public static final String GROUP_COUNT_SEPARATOR = "-"; //$NON-NLS-1$
	public static final Map<String, String> DEFAULT_ICON_MAP = new HashMap<String, String>();

	/*
	 * pich:panelMenuGroup attributes
	 */
	private static final String DISABLED_CLASS = "disabledClass"; //$NON-NLS-1$
	private static final String DISABLED_STYLE = "disabledStyle"; //$NON-NLS-1$
	private static final String ICON_CLASS = "iconClass"; //$NON-NLS-1$
	private static final String ICON_STYLE = "iconStyle"; //$NON-NLS-1$
	private static final String ICON_EXPANDED = "iconExpanded"; //$NON-NLS-1$
	private static final String ICON_COLLAPSED = "iconCollapsed"; //$NON-NLS-1$
	private static final String ICON_DISABLED = "iconDisabled"; //$NON-NLS-1$
	private static final String LABEL = "label"; //$NON-NLS-1$
	private static final String STYLE = "style"; //$NON-NLS-1$
	private static final String STYLE_CLASS = "styleClass"; //$NON-NLS-1$
	
	/*
	 *	rich:panelMenuGroup css styles
	 */ 
	private static final String CSS_DR_TOP_GROUP_DIV = "dr-pmenu-top-group-div"; //$NON-NLS-1$
	private static final String CSS_DR_TOP_GROUP = "dr-pmenu-top-group"; //$NON-NLS-1$
	private static final String CSS_DR_GROUP = "dr-pmenu-group"; //$NON-NLS-1$
	private static final String CSS_TOP_GROUP_ICON = "rich-pmenu-top-group-self-icon"; //$NON-NLS-1$
	private static final String CSS_TOP_GROUP_LABEL = "rich-pmenu-top-group-self-label"; //$NON-NLS-1$
	private static final String CSS_DR_GROUP_DIV = "dr-pmenu-group-div"; //$NON-NLS-1$
	private static final String CSS_GROUP = "rich-pmenu-group"; //$NON-NLS-1$
	private static final String CSS_TOP_GROUP = "rich-pmenu-top-group"; //$NON-NLS-1$
	private static final String CSS_TOP_GROUP_DIV = "rich-pmenu-top-group-div"; //$NON-NLS-1$
	private static final String CSS_GROUP_ICON = "rich-pmenu-group-self-icon"; //$NON-NLS-1$
	private static final String CSS_GROUP_LABEL = "rich-pmenu-group-self-label"; //$NON-NLS-1$
	private static final String CSS_HOVERED_ELEMENT = "rich-pmenu-hovered-element"; //$NON-NLS-1$
	private static final String CSS_DISABLED_ELEMENT = "rich-pmenu-disabled-element"; //$NON-NLS-1$
	
	private static final String NAME_COMPONENT = "panelMenuGroup"; //$NON-NLS-1$
	private static final String PANEL_MENU_END_TAG = ":panelMenu"; //$NON-NLS-1$
	private static final String PANEL_MENU_GROUP_END_TAG = ":panelMenuGroup"; //$NON-NLS-1$
	private static final String PANEL_MENU_ITEM_END_TAG = ":panelMenuItem"; //$NON-NLS-1$

	private static final String COMPONENT_ATTR_VPE_SUPPORT = "vpeSupport"; //$NON-NLS-1$
	private static final String PANEL_MENU_GROUP_ICON_SPACER_PATH = "/panelMenuGroup/spacer.gif"; //$NON-NLS-1$
	private static final String STYLE_PATH = "/panelMenuGroup/style.css"; //$NON-NLS-1$

	private static final String VSPACE = "vspace"; //$NON-NLS-1$
	private static final String HSPACE = "hspace"; //$NON-NLS-1$
	private static final String TRUE = "true"; //$NON-NLS-1$
	private static final String RIGHT = "right"; //$NON-NLS-1$
	private static final String LEFT = "right"; //$NON-NLS-1$

	private static final String WIDTH_100_PERSENTS = "width: 100%; "; //$NON-NLS-1$
	private static final String DEFAULT_SIZE_VALUE = "16"; //$NON-NLS-1$
	
	private List<String> expandedIds = new ArrayList<String>();
	
	static {
		DEFAULT_ICON_MAP.put("chevron", "/panelMenuGroup/chevron.gif"); //$NON-NLS-1$ //$NON-NLS-2$
		DEFAULT_ICON_MAP.put("chevronUp", "/panelMenuGroup/chevronUp.gif"); //$NON-NLS-1$ //$NON-NLS-2$
		DEFAULT_ICON_MAP.put("chevronDown", "/panelMenuGroup/chevronDown.gif"); //$NON-NLS-1$ //$NON-NLS-2$
		DEFAULT_ICON_MAP.put("triangle", "/panelMenuGroup/triangle.gif"); //$NON-NLS-1$ //$NON-NLS-2$
		DEFAULT_ICON_MAP.put("triangleUp", "/panelMenuGroup/triangleUp.gif"); //$NON-NLS-1$ //$NON-NLS-2$
		DEFAULT_ICON_MAP
				.put("triangleDown", "/panelMenuGroup/triangleDown.gif"); //$NON-NLS-1$ //$NON-NLS-2$
		DEFAULT_ICON_MAP.put("disc", "/panelMenuGroup/disc.gif"); //$NON-NLS-1$ //$NON-NLS-2$
		DEFAULT_ICON_MAP.put("grid", "/panelMenuGroup/grid.gif"); //$NON-NLS-1$ //$NON-NLS-2$
	}

	@SuppressWarnings("unchecked")
	public VpeCreationData create(VpePageContext pageContext, Node sourceNode,
			nsIDOMDocument visualDocument) {
		Element groupSourceElement =  (Element) sourceNode;
		expandedIds = (List<String>) groupSourceElement.getUserData(VPE_EXPANDED_TOGGLE_IDS);
		String childId = (String) groupSourceElement.getUserData(VpeVisualDomBuilder.VPE_USER_TOGGLE_ID);
		
		/*
		 * Counts child groups in a parent group 
		 */
		int childGroupCount = 1;

		Element anySuitableParent = getGroupParent(groupSourceElement, false);
		Element panelMenuParent = getGroupParent(groupSourceElement, true);
		final Attributes attrs = new Attributes(groupSourceElement, panelMenuParent);
	
		ComponentUtil.setCSSLink(pageContext, STYLE_PATH, NAME_COMPONENT);
		boolean expanded = false;
		if (null != expandedIds) {
 			 expanded = expandedIds.contains(childId);
		}
		nsIDOMElement div = visualDocument
				.createElement(HTML.TAG_DIV);
		VpeCreationData creationData = new VpeCreationData(div);
		div.setAttribute(COMPONENT_ATTR_VPE_SUPPORT, NAME_COMPONENT);
		div.setAttribute(VpeVisualDomBuilder.VPE_USER_TOGGLE_ID, childId);

		buildTable(pageContext, anySuitableParent, panelMenuParent, groupSourceElement,
				visualDocument, div, expanded, childId, attrs);
		
		nsIDOMElement childSpan = visualDocument
		.createElement(HTML.TAG_SPAN);
		VpeChildrenInfo childrenInfo = new VpeChildrenInfo(childSpan);
		
	
		List<Node> children = null;
		
//		if (srcNode != null) {
//            children = ComponentUtil.getChildren((Element) groupSourceElement.getUserData(VpeVisualDomBuilder.SRC_NODE));
//        } else {
            children = ComponentUtil.getChildren(groupSourceElement);
//        }
		if (expanded) {
			for (Node child : children) {
				boolean isGroup = child.getNodeName().endsWith(
						PANEL_MENU_GROUP_END_TAG);
				boolean isItem = child.getNodeName().endsWith(
						PANEL_MENU_ITEM_END_TAG);
				if (isGroup) {
					String newLevelToggleId = childId + GROUP_COUNT_SEPARATOR
					+ childGroupCount;
					childGroupCount++;
					child.setUserData(VpeVisualDomBuilder.VPE_USER_TOGGLE_ID, newLevelToggleId, null);
					child.setUserData(VPE_EXPANDED_TOGGLE_IDS, expandedIds, null);
				}
				if (isItem) {
					child.setUserData(RichFacesPanelMenuItemTemplate.VPE_PANEL_MENU_ITEM_ID, childId, null);
				}
				childrenInfo = new VpeChildrenInfo(div);
				childrenInfo.addSourceChild(child);
				creationData.addChildrenInfo(childrenInfo);
			}
		}
		
		if (childrenInfo.getSourceChildren() == null) {
			creationData.addChildrenInfo(childrenInfo);
		}
		
		return creationData;
	}

	private void buildTable(VpePageContext pageContext,
			Element anySuitableParent, Element panelMenuParent, Element groupSourceElement,
			nsIDOMDocument visualDocument, nsIDOMElement div, 
			boolean expanded, String activeChildId, Attributes attrs) {
		String tableStyle = Constants.EMPTY;
		String tableClass = Constants.EMPTY;
		String iconCellClass = Constants.EMPTY;
		String iconCellStyle = Constants.EMPTY;
		String labelCellClass = Constants.EMPTY;
		String emptyCellClass = Constants.EMPTY;
		String divClass = Constants.EMPTY;

		nsIDOMElement table = visualDocument
				.createElement(HTML.TAG_TABLE);
		div.appendChild(table);

		table.setAttribute(HTML.ATTR_CELLSPACING, Constants.ZERO_STRING);
		table.setAttribute(HTML.ATTR_CELLPADDING, Constants.ZERO_STRING);
		table.setAttribute(HTML.ATTR_BORDER, Constants.ZERO_STRING);

		nsIDOMElement tableBodyRow = visualDocument
				.createElement(HTML.TAG_TR);
		table.appendChild(tableBodyRow);
		
		/*
		 * Add indentation for nested groups
		 */
		
		List<nsIDOMElement> indentTds = new ArrayList<nsIDOMElement>();
		
		if (activeChildId != null) {
			String[] ids = activeChildId.split(GROUP_COUNT_SEPARATOR);
			
			if (ids.length > 1) {
				for (int i = 1; i <= ids.length - 1; i++) {
					nsIDOMElement spacerTd = visualDocument
							.createElement(HTML.TAG_TD);
					nsIDOMElement spacerImg = visualDocument
							.createElement(HTML.TAG_IMG);
					spacerTd.appendChild(spacerImg);
					ComponentUtil.setImg(spacerImg,
							PANEL_MENU_GROUP_ICON_SPACER_PATH);
					setDefaultImgAttributes(spacerImg);
					tableBodyRow.appendChild(spacerTd);
					indentTds.add(spacerTd);
				}
			}
		}

		nsIDOMElement column1 = visualDocument
				.createElement(HTML.TAG_TD);
		column1.setAttribute(VpeVisualDomBuilder.VPE_USER_TOGGLE_ID, activeChildId);
		tableBodyRow.appendChild(column1);

		nsIDOMElement column2 = visualDocument
				.createElement(HTML.TAG_TD);
		tableBodyRow.appendChild(column2);
		column2.setAttribute(HTML.ATTR_STYLE, WIDTH_100_PERSENTS);

		nsIDOMElement column3 = visualDocument
		.createElement(HTML.TAG_TD);
		column3.setAttribute(VpeVisualDomBuilder.VPE_USER_TOGGLE_ID, activeChildId);
		tableBodyRow.appendChild(column3);
		
		/*
		 * Group Label Routine.
		 */
		Attr labelAttr = null;
		String labelValue = Constants.EMPTY;
		String bundleValue = Constants.EMPTY;
		String resultValue = Constants.EMPTY;
		if (groupSourceElement.hasAttribute(LABEL)) {
			labelAttr = groupSourceElement.getAttributeNode(LABEL);
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
				resultValue = Constants.EMPTY;
			}
		}
		nsIDOMText text = visualDocument.createTextNode(resultValue);
		
		column2.appendChild(text);
		column2.setAttribute(VpeVisualDomBuilder.VPE_USER_TOGGLE_ID, activeChildId);

		boolean childOfPanelMenu = anySuitableParent != null ? anySuitableParent
				.getNodeName().endsWith(PANEL_MENU_END_TAG)
				: false;
		
		/*
		 * Group Icon Routine
		 */
		nsIDOMElement iconCell = column1;
		nsIDOMElement emptyCell = column3;
		if (!childOfPanelMenu && ComponentUtil.isNotBlank(attrs.getPm_iconGroupPosition())) {
			if (RIGHT.equalsIgnoreCase(attrs.getPm_iconGroupPosition())) {
				/*
				 * Set icon image on the right
				 */
				iconCell = column3;
				emptyCell = column1;
			}
		} 
		if (childOfPanelMenu && ComponentUtil.isNotBlank(attrs.getPm_iconGroupTopPosition())) {
			if (RIGHT.equalsIgnoreCase(attrs.getPm_iconGroupTopPosition())) {
				/*
				 * Set icon image on the right
				 */
				iconCell = column3;
				emptyCell = column1;
			} else if (LEFT.equalsIgnoreCase(attrs.getPm_iconGroupTopPosition())) {
				iconCell = column1;
				emptyCell = column3;
			}
		}

		nsIDOMElement imgIcon = visualDocument
				.createElement(HTML.TAG_IMG);
		
		nsIDOMElement imgSpacer = visualDocument
		.createElement(HTML.TAG_IMG);
		ComponentUtil.setImg(imgSpacer, PANEL_MENU_GROUP_ICON_SPACER_PATH);
		setDefaultImgAttributes(imgSpacer);
		
		iconCell.appendChild(imgIcon);
		emptyCell.appendChild(imgSpacer);

		setIcon(pageContext, panelMenuParent, groupSourceElement,
				imgIcon, expanded, childOfPanelMenu, attrs);

		/*
		 * Group Style Classes Routine
		 */
		if (childOfPanelMenu) {
		    tableClass += Constants.WHITE_SPACE + CSS_DR_TOP_GROUP
		    	+ Constants.WHITE_SPACE + CSS_TOP_GROUP
		    	+ Constants.WHITE_SPACE + CSS_GROUP;
		    divClass += Constants.WHITE_SPACE + CSS_DR_TOP_GROUP_DIV
		    	+ Constants.WHITE_SPACE + CSS_TOP_GROUP_DIV;
		} else {
		    tableClass += Constants.WHITE_SPACE + CSS_GROUP
		    	+ Constants.WHITE_SPACE + CSS_DR_GROUP;
		    divClass += Constants.WHITE_SPACE + CSS_DR_GROUP_DIV;
		}
		 
		if (TRUE.equalsIgnoreCase(attrs.getPm_disabled())) {
			if (childOfPanelMenu) {
				tableClass += Constants.WHITE_SPACE + CSS_DISABLED_ELEMENT;
				if (ComponentUtil.isNotBlank(attrs.getPm_disabledGroupClass())) {
					tableClass += Constants.WHITE_SPACE + attrs.getPm_disabledGroupClass();
				}
				if (ComponentUtil.isNotBlank(attrs.getPm_topGroupStyle())) {
					tableStyle += Constants.WHITE_SPACE + attrs.getPm_topGroupStyle();
				}
			}
		}
		
		if ((TRUE.equalsIgnoreCase(attrs.getPmg_disabled()))) {
			if (!(TRUE.equalsIgnoreCase(attrs.getPm_disabled()))) {
				tableClass += Constants.WHITE_SPACE + CSS_DISABLED_ELEMENT;
				if (ComponentUtil.isNotBlank(attrs.getPm_disabledGroupClass())){
					tableClass += Constants.WHITE_SPACE + attrs.getPm_disabledGroupClass();
				}
			}
			
			if (ComponentUtil.isNotBlank(attrs.getPmg_disabledClass())) {
				tableClass += Constants.WHITE_SPACE + attrs.getPmg_disabledClass();
			} 
			if (ComponentUtil.isNotBlank(attrs.getPm_disabledGroupStyle())) {
				tableStyle += Constants.WHITE_SPACE + attrs.getPm_disabledGroupStyle();
			}
			if (ComponentUtil.isNotBlank(attrs.getPmg_disabledStyle())) {
				tableStyle += Constants.WHITE_SPACE + attrs.getPmg_disabledStyle();
			}
		} 
		
		if (!(TRUE.equalsIgnoreCase(attrs.getPm_disabled()))
				&& (!(TRUE.equalsIgnoreCase(attrs.getPmg_disabled())))) {
			tableClass = Constants.WHITE_SPACE + CSS_DR_GROUP + Constants.WHITE_SPACE + CSS_GROUP;
			iconCellClass = Constants.WHITE_SPACE + CSS_GROUP_ICON;
			labelCellClass += Constants.WHITE_SPACE + CSS_GROUP_LABEL;
			emptyCellClass += Constants.WHITE_SPACE + CSS_GROUP_ICON;
			
			if (ComponentUtil.isNotBlank(attrs.getPmg_iconClass())) {
				iconCellClass += Constants.WHITE_SPACE + attrs.getPmg_iconClass();
			}
			if (ComponentUtil.isNotBlank(attrs.getPmg_iconStyle())) {
				iconCellStyle += Constants.WHITE_SPACE + attrs.getPmg_iconStyle();
			}
			
			if (childOfPanelMenu) {
				tableClass = Constants.WHITE_SPACE + CSS_DR_TOP_GROUP + Constants.WHITE_SPACE + CSS_GROUP;
				iconCellClass = Constants.WHITE_SPACE + CSS_GROUP_ICON + Constants.WHITE_SPACE + CSS_TOP_GROUP_ICON; 
				labelCellClass = Constants.WHITE_SPACE + CSS_TOP_GROUP_LABEL;
				emptyCellClass = Constants.WHITE_SPACE + CSS_TOP_GROUP_ICON;
				if (ComponentUtil.isNotBlank(attrs.getPm_topGroupClass())){
					tableClass += Constants.WHITE_SPACE + attrs.getPm_topGroupClass();
				} 
				if (ComponentUtil.isNotBlank(attrs.getPm_topGroupStyle())) {
					tableStyle += attrs.getPm_topGroupStyle();
				}
				if (ComponentUtil.isNotBlank(attrs.getPmg_iconClass())) {
					iconCellClass += Constants.WHITE_SPACE + attrs.getPmg_iconClass();
				}
				if (ComponentUtil.isNotBlank(attrs.getPmg_iconStyle())) {
					iconCellStyle += Constants.WHITE_SPACE + attrs.getPmg_iconStyle();
				}
			} else {
			    	if (ComponentUtil.isNotBlank(attrs.getPm_groupClass())) {
					tableClass += Constants.WHITE_SPACE + attrs.getPm_groupClass();
			    	} 
			    	if (ComponentUtil.isNotBlank(attrs.getPm_groupStyle())) {
					tableStyle += Constants.WHITE_SPACE + attrs.getPm_groupStyle();
				}
			}
		}

		if (ComponentUtil.isNotBlank(attrs.getPmg_styleClass())) {
			tableClass += Constants.WHITE_SPACE + attrs.getPmg_styleClass();
		}
		if (ComponentUtil.isNotBlank(attrs.getPmg_style())) {
			tableStyle += Constants.WHITE_SPACE + attrs.getPmg_style();
		}
		
		iconCell.setAttribute(HTML.ATTR_CLASS, iconCellClass);
//		iconCell.setAttribute(HtmlComponentUtil.HTML_STYLE_ATTR, iconCellStyle);
		
		for (nsIDOMElement indentTdCell : indentTds) {
			indentTdCell.setAttribute(HTML.ATTR_CLASS, iconCellClass);
		}
		
		column2.setAttribute(HTML.ATTR_CLASS, labelCellClass);
		emptyCell.setAttribute(HTML.ATTR_CLASS, emptyCellClass);
		div.setAttribute(HTML.ATTR_CLASS, divClass);
		table.setAttribute(HTML.ATTR_CLASS, tableClass);
		table.setAttribute(HTML.ATTR_STYLE, tableStyle);
	}

	private static void setDefaultImgAttributes(nsIDOMElement element) {
		element.setAttribute(HTML.ATTR_WIDTH,
				DEFAULT_SIZE_VALUE);
		element.setAttribute(VSPACE, Constants.ZERO_STRING);
		element.setAttribute(HSPACE, Constants.ZERO_STRING);
		element.setAttribute(HTML.ATTR_HEIGHT,
				DEFAULT_SIZE_VALUE);
	}
	
	/**
	 * Gets the panel menu group parent.
	 * 
	 * @param sourceElement the source element
	 * @param findOnlyPanelMenuParent flag to find only panel menu parent
	 * 
	 * @return the group parent
	 */
	private static final Element getGroupParent(Element sourceElement,
			boolean findOnlyPanelMenuParent) {
	
		Element parent = null;
		Element currentElement = sourceElement;
		
		while ((currentElement.getParentNode() != null)
				&& (currentElement.getParentNode().getNodeType() == Node.ELEMENT_NODE)) {
			currentElement = parent = (Element) currentElement.getParentNode();
			if (findOnlyPanelMenuParent) {
				if ((parent != null)
						&& parent.getNodeName().endsWith(PANEL_MENU_END_TAG)) {
					break;
				}
			} else {
				if ((parent != null)
						&& parent.getNodeName().endsWith(PANEL_MENU_END_TAG)
						|| parent.getNodeName().endsWith(
								PANEL_MENU_GROUP_END_TAG)) {
					break;
				}
			}
		}
		return parent;
	}

	private void setIcon(VpePageContext pageContext, Node panelMenuParent,
			Element groupSourceElement, nsIDOMElement imgIcon,
			boolean expanded, boolean childOfPanelMenu, Attributes attrs) {
		String pathIconExpanded = attrs.getPmg_iconExpanded();
		String pathIconCollapsed = attrs.getPmg_iconCollapsed();
		String pathIconDisabled = attrs.getPmg_iconDisabled();
			
			if (pathIconExpanded == null) {
			    if (childOfPanelMenu) {
				pathIconExpanded = attrs.getPm_iconExpandedTopGroup();
			    } 
			    pathIconExpanded = attrs.getPm_iconExpandedGroup();
			}
			if (pathIconCollapsed == null) {
			    if (childOfPanelMenu) {
				pathIconCollapsed = attrs.getPm_iconCollapsedTopGroup();
			    } 
			    pathIconCollapsed = attrs.getPm_iconCollapsedGroup();
			}
			if (pathIconDisabled == null) {
			    if (childOfPanelMenu) {
				pathIconDisabled = attrs.getPm_iconTopDisabledGroup();
			    } 
			    pathIconDisabled = attrs.getPm_iconDisabledGroup();
			}

		if (TRUE.equalsIgnoreCase(attrs.getPmg_disabled())) {
			if (ComponentUtil.isNotBlank(pathIconDisabled)) {
				if (DEFAULT_ICON_MAP.containsKey(pathIconDisabled)) {
					pathIconDisabled = DEFAULT_ICON_MAP.get(pathIconDisabled);
					ComponentUtil.setImg(imgIcon, pathIconDisabled);
				} else {
					String imgFullPath = VpeStyleUtil.addFullPathToImgSrc(
							pathIconDisabled, pageContext, true);
					imgIcon.setAttribute(HTML.ATTR_SRC,
							imgFullPath);
				}
			} else {
				ComponentUtil.setImg(imgIcon, PANEL_MENU_GROUP_ICON_SPACER_PATH);
			}
		} else {
			if (expanded) {
				if (ComponentUtil.isNotBlank(pathIconExpanded)) {
					if (DEFAULT_ICON_MAP.containsKey(pathIconExpanded)) {
						pathIconExpanded = DEFAULT_ICON_MAP
						.get(pathIconExpanded);
						ComponentUtil.setImg(imgIcon, pathIconExpanded);
					} else {
						String imgFullPath = VpeStyleUtil.addFullPathToImgSrc(
								pathIconExpanded, pageContext, true);
						imgIcon.setAttribute(HTML.ATTR_SRC,
								imgFullPath);
					}
				} else {
					ComponentUtil.setImg(imgIcon, PANEL_MENU_GROUP_ICON_SPACER_PATH);
				}
			} else {
				if (ComponentUtil.isNotBlank(pathIconCollapsed)) {
					if (DEFAULT_ICON_MAP.containsKey(pathIconCollapsed)) {
						pathIconCollapsed = DEFAULT_ICON_MAP
						.get(pathIconCollapsed);
						ComponentUtil.setImg(imgIcon, pathIconCollapsed);
					} else {
						String imgFullPath = VpeStyleUtil.addFullPathToImgSrc(
								pathIconCollapsed, pageContext, true);
						imgIcon.setAttribute(HTML.ATTR_SRC,
								imgFullPath);
					}
				} else {
					ComponentUtil.setImg(imgIcon, PANEL_MENU_GROUP_ICON_SPACER_PATH);
				}
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.jboss.tools.vpe.editor.template.VpeToggableTemplate#toggle(org.jboss.tools.vpe.editor.VpeVisualDomBuilder, org.w3c.dom.Node, java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public void toggle(VpeVisualDomBuilder builder, Node sourceNode,
			String toggleId) {
	    
	    Element groupSourceElement =  (Element) sourceNode;
	    Element panelMenuParent = getGroupParent(groupSourceElement, true);
	    
	    String pmg_disabled = groupSourceElement.getAttribute(HTML.ATTR_DISABLED);
	    String pm_disabled = panelMenuParent.getAttribute(HTML.ATTR_DISABLED);
	    String pm_expandSingle = panelMenuParent.getAttribute(RichFacesPanelMenuTemplate.EXPAND_SINGLE);
		/*
		 * Do nothing when panel menu or panel group are disabled.
		 */
		if ((TRUE.equalsIgnoreCase(pm_disabled))
				|| (TRUE.equalsIgnoreCase(pmg_disabled))) {
			return;
		}
		
		expandedIds = (List<String>) sourceNode.getUserData(VPE_EXPANDED_TOGGLE_IDS);
		if (null == expandedIds) {
			expandedIds = new ArrayList<String>();
		}
		/*
		 * Expand only one group.
		 */
		if ((null != pm_expandSingle) && (TRUE.equalsIgnoreCase(pm_expandSingle))) {
			if (expandedIds.contains(toggleId)) {
				/*
				 * Close group and its children
				 */
				expandedIds.remove(toggleId);
				for (Iterator<String> iterator = expandedIds.iterator(); iterator.hasNext();) {
					String id = iterator.next();
					if (id.startsWith(toggleId)) {
						iterator.remove();
					}
				}
			} else {
				/*
				 * Expand new group, close others
				 */
				String[] toggleIds = toggleId.split(RichFacesPanelMenuGroupTemplate.GROUP_COUNT_SEPARATOR);
				if ((null != toggleIds) && (toggleIds.length > 0)) {
					for (Iterator<String> iterator = expandedIds.iterator(); iterator.hasNext();) {
						String id = iterator.next();
						String[] ids = id.split(RichFacesPanelMenuGroupTemplate.GROUP_COUNT_SEPARATOR);
						if ((null != ids) && (ids.length > 0)) {
							if (ids.length >= toggleIds.length) {
								/*
								 * Remove all ids that are deeper than selected 
								 * and that are on the same level.
								 */
								iterator.remove();
							} else {
								/*
								 * Remove all ids that are not in the selected branch. 
								 */
								for (int i = 0; i < ids.length; i++) {
									if (!ids[i].equalsIgnoreCase(toggleIds[i])) {
										iterator.remove();
									}
								}
							}
						}
					}
				}
				expandedIds.add(toggleId);
			}
		} else {
			/*
			 * Expand any number of groups.
			 */
			if (expandedIds.contains(toggleId)) {
				expandedIds.remove(toggleId);
				for (Iterator<String> iterator = expandedIds.iterator(); iterator.hasNext();) {
					String id = iterator.next();
					if (id.startsWith(toggleId)) {
						iterator.remove();
					}
				}
			} else {
				expandedIds.add(toggleId);
			}
		}
		sourceNode.setUserData(VPE_EXPANDED_TOGGLE_IDS, expandedIds, null);
		panelMenuParent.setUserData(VPE_EXPANDED_TOGGLE_IDS, expandedIds, null);
		builder.updateNode(panelMenuParent);
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.vpe.editor.template.VpeToggableTemplate#stopToggling(org.w3c.dom.Node)
	 */
	public void stopToggling(Node sourceNode) {
		if (null != expandedIds) {
			expandedIds.clear();
		}
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.vpe.editor.template.VpeAbstractTemplate#isRecreateAtAttrChange(org.jboss.tools.vpe.editor.context.VpePageContext, org.w3c.dom.Element, org.mozilla.interfaces.nsIDOMDocument, org.mozilla.interfaces.nsIDOMElement, java.lang.Object, java.lang.String, java.lang.String)
	 */
	public boolean recreateAtAttrChange(VpePageContext pageContext,
			Element sourceElement, nsIDOMDocument visualDocument,
			nsIDOMElement visualNode, Object data, String name, String value) {
		return true;
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
    
    class Attributes {
	
	/*
	 *	rich:panelMenu attributes for groups
	 */ 
	private String pm_iconGroupPosition;
	private String pm_iconGroupTopPosition;
	private String pm_iconCollapsedGroup;
	private String pm_iconCollapsedTopGroup;
	private String pm_iconExpandedGroup;
	private String pm_iconExpandedTopGroup;
	private String pm_iconDisabledGroup;
	private String pm_iconTopDisabledGroup;
	private String pm_expandSingle;
	
	/*
	 *	rich:panelMenu style classes for groups
	 */ 
	private String pm_disabled;
	private String pm_disabledGroupClass;
	private String pm_disabledGroupStyle;
	private String pm_topGroupClass;
	private String pm_topGroupStyle;
	private String pm_groupClass;
	private String pm_groupStyle;
	private String pm_style;
	private String pm_styleClass;
	
	/*
	 * pich:panelMenuGroup attributes
	 */
	private String pmg_disabledStyle;
	private String pmg_disabledClass;
	private String pmg_disabled;
	private String pmg_iconClass;
	private String pmg_iconStyle;
	private String pmg_iconExpanded;
	private String pmg_iconCollapsed;
	private String pmg_iconDisabled;
	private String pmg_style;
	private String pmg_styleClass;
	
	public Attributes(final Element panelGroupElement, final Element panelMenuElement) {
	    readPanelMenuGroupAttributes(panelGroupElement);
	    readPanelMenuAttributes(panelMenuElement);
	    
	}
	
	/**
	 * Read attributes from the source element.
	 * 
	 * @param sourceNode the source node
	 */
	private void readPanelMenuAttributes(Element sourceParentElement) {
		
		if (null == sourceParentElement) {
			return;
		}

		/*
		 *	rich:panelMenu attributes for groups
		 */ 
		pm_iconGroupPosition = sourceParentElement.getAttribute(RichFacesPanelMenuTemplate.ICON_GROUP_POSITION);
		pm_iconGroupTopPosition = sourceParentElement.getAttribute(RichFacesPanelMenuTemplate.ICON_GROUP_TOP_POSITION);
		pm_iconCollapsedGroup = sourceParentElement.getAttribute(RichFacesPanelMenuTemplate.ICON_COLLAPSED_GROUP);
		pm_iconCollapsedTopGroup = sourceParentElement.getAttribute(RichFacesPanelMenuTemplate.ICON_COLLAPSED_TOP_GROUP);
		pm_iconExpandedGroup = sourceParentElement.getAttribute(RichFacesPanelMenuTemplate.ICON_EXPANDED_GROUP);
		pm_iconExpandedTopGroup = sourceParentElement.getAttribute(RichFacesPanelMenuTemplate.ICON_EXPANDED_TOP_GROUP);
		pm_iconDisabledGroup = sourceParentElement.getAttribute(RichFacesPanelMenuTemplate.ICON_DISABLED_GROUP);
		pm_iconTopDisabledGroup = sourceParentElement.getAttribute(RichFacesPanelMenuTemplate.ICON_TOP_DISABLED_GROUP);
		pm_expandSingle = sourceParentElement.getAttribute(RichFacesPanelMenuTemplate.EXPAND_SINGLE);
		
		/*
		 *	rich:panelMenu style classes for groups
		 */ 
		pm_disabled = sourceParentElement.getAttribute(HTML.ATTR_DISABLED);
		pm_disabledGroupClass = sourceParentElement.getAttribute(RichFacesPanelMenuTemplate.DISABLED_GROUP_CLASS);
		pm_disabledGroupStyle = sourceParentElement.getAttribute(RichFacesPanelMenuTemplate.DISABLED_GROUP_STYLE);
		pm_topGroupClass = sourceParentElement.getAttribute(RichFacesPanelMenuTemplate.TOP_GROUP_CLASS);
		pm_topGroupStyle = sourceParentElement.getAttribute(RichFacesPanelMenuTemplate.TOP_GROUP_STYLE);
		pm_groupClass = sourceParentElement.getAttribute(RichFacesPanelMenuTemplate.GROUP_CLASS);
		pm_groupStyle = sourceParentElement.getAttribute(RichFacesPanelMenuTemplate.GROUP_STYLE);
		pm_style = sourceParentElement.getAttribute(RichFaces.ATTR_STYLE);
		pm_styleClass = sourceParentElement.getAttribute(RichFaces.ATTR_STYLE_CLASS);
	}
	
	/**
	 * Read attributes from the source element.
	 * 
	 * @param sourceNode the source node
	 */
	private void readPanelMenuGroupAttributes(Element sourceElement) {
		
		if (null == sourceElement) {
			return;
		}
		
		/*
		 * pich:panelMenuGroup attributes
		 */
		pmg_disabledStyle = sourceElement.getAttribute(DISABLED_STYLE);
		pmg_disabledClass = sourceElement.getAttribute(DISABLED_CLASS);
		pmg_disabled = sourceElement.getAttribute(HTML.ATTR_DISABLED);
		pmg_iconClass = sourceElement.getAttribute(ICON_CLASS);
		pmg_iconStyle = sourceElement.getAttribute(ICON_STYLE);
		pmg_iconExpanded = sourceElement.getAttribute(ICON_EXPANDED);
		pmg_iconCollapsed = sourceElement.getAttribute(ICON_COLLAPSED);
		pmg_iconDisabled = sourceElement.getAttribute(ICON_DISABLED);
		pmg_style = sourceElement.getAttribute(STYLE);
		pmg_styleClass = sourceElement.getAttribute(STYLE_CLASS);
	}

	public String getPm_iconGroupPosition() {
	    return pm_iconGroupPosition;
	}

	public String getPm_iconGroupTopPosition() {
	    return pm_iconGroupTopPosition;
	}

	public String getPm_iconCollapsedGroup() {
	    return pm_iconCollapsedGroup;
	}

	public String getPm_iconCollapsedTopGroup() {
	    return pm_iconCollapsedTopGroup;
	}

	public String getPm_iconExpandedGroup() {
	    return pm_iconExpandedGroup;
	}

	public String getPm_iconExpandedTopGroup() {
	    return pm_iconExpandedTopGroup;
	}

	public String getPm_iconDisabledGroup() {
	    return pm_iconDisabledGroup;
	}

	public String getPm_iconTopDisabledGroup() {
	    return pm_iconTopDisabledGroup;
	}

	public String getPm_expandSingle() {
	    return pm_expandSingle;
	}

	public String getPm_disabled() {
	    return pm_disabled;
	}

	public String getPm_disabledGroupClass() {
	    return pm_disabledGroupClass;
	}

	public String getPm_disabledGroupStyle() {
	    return pm_disabledGroupStyle;
	}

	public String getPm_topGroupClass() {
	    return pm_topGroupClass;
	}

	public String getPm_topGroupStyle() {
	    return pm_topGroupStyle;
	}

	public String getPm_groupClass() {
	    return pm_groupClass;
	}

	public String getPm_groupStyle() {
	    return pm_groupStyle;
	}

	public String getPm_style() {
	    return pm_style;
	}

	public String getPm_styleClass() {
	    return pm_styleClass;
	}

	public String getPmg_disabledStyle() {
	    return pmg_disabledStyle;
	}

	public String getPmg_disabledClass() {
	    return pmg_disabledClass;
	}

	public String getPmg_disabled() {
	    return pmg_disabled;
	}

	public String getPmg_iconClass() {
	    return pmg_iconClass;
	}

	public String getPmg_iconStyle() {
	    return pmg_iconStyle;
	}

	public String getPmg_iconExpanded() {
	    return pmg_iconExpanded;
	}

	public String getPmg_iconCollapsed() {
	    return pmg_iconCollapsed;
	}

	public String getPmg_iconDisabled() {
	    return pmg_iconDisabled;
	}

	public String getPmg_style() {
	    return pmg_style;
	}

	public String getPmg_styleClass() {
	    return pmg_styleClass;
	}
	
    }
}