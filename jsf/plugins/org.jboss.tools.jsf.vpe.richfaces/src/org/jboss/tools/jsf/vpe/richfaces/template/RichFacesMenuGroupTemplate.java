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
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeAbstractTemplate;
import org.jboss.tools.vpe.editor.template.VpeChildrenInfo;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.util.Constants;
import org.jboss.tools.vpe.editor.util.HTML;
import org.jboss.tools.vpe.editor.util.VpeStyleUtil;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMText;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class RichFacesMenuGroupTemplate extends VpeAbstractTemplate {

	/*
	 * rich:menuGroup constants
	 */
	private static final String COMPONENT_NAME = "menuGroup"; //$NON-NLS-1$
	private static final String STYLE_PATH = "menuGroup/menuGroup.css"; //$NON-NLS-1$
	private static final String SPACER_IMG_PATH = "menuGroup/spacer.gif"; //$NON-NLS-1$
	private static final String FOLDER_IMG_PATH = "menuGroup/arrow.gif"; //$NON-NLS-1$
	private static final String FOLDER_IMG_WIDTH = "16px;"; //$NON-NLS-1$
	private static final String FOLDER_IMG_HEIGHT = "16px;"; //$NON-NLS-1$
	private static final String CHILD_GROUP_NAME = ":menuGroup"; //$NON-NLS-1$
	private static final String CHILD_ITEM_NAME = ":menuItem"; //$NON-NLS-1$
	private static final String ICON_FACET_NAME = "icon"; //$NON-NLS-1$
	private static final String ICON_DISABLED_FACET_NAME = "iconDisabled"; //$NON-NLS-1$

	/*
	 * Constants for drop down mechanism.
	 */
	private static final String MENU_PARENT_ID = "vpe-ddm-menu-ul"; //$NON-NLS-1$
	private static final String MENU_CHILD_ID = "vpe-ddm-menu-li"; //$NON-NLS-1$

	/*
	 * rich:menuGroup css styles names
	 */
	private static final String CSS_RICH_MENU_GROUP = "rich-menu-group"; //$NON-NLS-1$
	private static final String CSS_RICH_MENU_GROUP_LABEL = "rich-menu-group-label"; //$NON-NLS-1$
	private static final String CSS_RICH_MENU_GROUP_FOLDER = "rich-menu-group-folder"; //$NON-NLS-1$
	private static final String CSS_RICH_MENU_GROUP_HOVER = "rich-menu-group-over"; //$NON-NLS-1$
	private static final String CSS_RICH_MENU_ITEM_LABEL = "rich-menu-item-label"; //$NON-NLS-1$
	private static final String CSS_RICH_MENU_ITEM_ICON = "rich-menu-item-icon"; //$NON-NLS-1$
	private static final String CSS_RICH_MENU_ITEM_FOLDER = "rich-menu-item-folder"; //$NON-NLS-1$
	private static final String CSS_RICH_MENU_ITEM_LABEL_DISABLED = "rich-menu-item-label-disabled"; //$NON-NLS-1$
	private static final String CSS_RICH_MENU_ITEM_ICON_DISABLED = "rich-menu-item-icon-disabled"; //$NON-NLS-1$
	private static final String CSS_RICH_MENU_ITEM_FOLDER_DISABLED = "rich-menu-item-folder-disabled"; //$NON-NLS-1$
	private static final String CSS_RICH_MENU_ITEM_ICON_ENABLED = "rich-menu-item-icon-enabled"; //$NON-NLS-1$
	private static final String CSS_RICH_MENU_ITEM_ICON_SELECTED = "rich-menu-item-icon-selected"; //$NON-NLS-1$
	private static final String CSS_RICH_MENU_LIST_BORDER = "rich-menu-list-border"; //$NON-NLS-1$
	private static final String CSS_RICH_MENU_LIST_BG = "rich-menu-list-bg"; //$NON-NLS-1$
	private static final String CSS_RICH_LIST_FOLDER_DIV_STYLE = ""; //$NON-NLS-1$
	private static final String CSS_RICH_LIST_BORDER_DIV_STYLE = ""; //$NON-NLS-1$
	private static final String CSS_MENU_GROUP_TOP_DIV = "dr-menu-group-top-div"; //$NON-NLS-1$
	
	public VpeCreationData create(VpePageContext pageContext, Node sourceNode,
			nsIDOMDocument visualDocument) {
		VpeCreationData creationData = null;
		Element sourceElement = (Element)sourceNode;
		ComponentUtil.setCSSLink(pageContext, STYLE_PATH, COMPONENT_NAME);
		final Attributes attrs = new Attributes(sourceElement);
		
		/*
		 * MenuGroup component structure.
		 * In order of  nesting.
		 */
		nsIDOMElement ddmMainUL;
		nsIDOMElement grMainLI;
		nsIDOMElement grChildrenUL;
		nsIDOMElement ddmChildrenLI;
		
		nsIDOMElement grTopDiv;
		nsIDOMElement grImgSpan;
		nsIDOMElement grImg;
		nsIDOMElement grFolderImg;
		nsIDOMElement grLabelSpan;
		nsIDOMText grLabelText;
		nsIDOMElement grFolderImgSpan;
		nsIDOMElement grListBorderDiv;
		nsIDOMElement grListBgDiv;
		
		/*
		 * Creating visual elements
		 */
		grMainLI = visualDocument.createElement(HTML.TAG_LI);
		grChildrenUL = visualDocument.createElement(HTML.TAG_UL);
		grTopDiv = visualDocument.createElement(HTML.TAG_DIV);
		grImgSpan = visualDocument.createElement(HTML.TAG_SPAN);
		grImg = visualDocument.createElement(HTML.TAG_IMG);
		grFolderImg = visualDocument.createElement(HTML.TAG_IMG);
		grLabelSpan = visualDocument.createElement(HTML.TAG_SPAN);
		grLabelText = visualDocument.createTextNode(Constants.EMPTY);
		grFolderImgSpan = visualDocument.createElement(HTML.TAG_SPAN);
		grListBorderDiv = visualDocument.createElement(HTML.TAG_DIV);
		grListBgDiv = visualDocument.createElement(HTML.TAG_DIV);
		creationData = new VpeCreationData(grMainLI);
		
		/*
		 * Nesting elements
		 */
		grTopDiv.appendChild(grImgSpan);
		grTopDiv.appendChild(grLabelSpan);
		grLabelSpan.appendChild(grLabelText);
		grTopDiv.appendChild(grFolderImgSpan);
//		grFolderDiv.appendChild(grListBorderDiv);
//		grListBorderDiv.appendChild(grListBgDiv);
		grMainLI.appendChild(grTopDiv);

		/*
		 * Setting attributes for the drop-down mechanism
		 */
		grMainLI.setAttribute(MENU_CHILD_ID, Constants.EMPTY);
		grChildrenUL.setAttribute(MENU_PARENT_ID, Constants.EMPTY);
	    
		/*
		 * Setting css classes
		 */
		String topDivClass = Constants.EMPTY;
		String imgSpanClass = Constants.EMPTY;
		String labelSpanClass = Constants.EMPTY;
		String folderDivClass = Constants.EMPTY;
		
		topDivClass += Constants.WHITE_SPACE + CSS_RICH_MENU_GROUP;
		imgSpanClass += Constants.WHITE_SPACE + CSS_RICH_MENU_ITEM_ICON_ENABLED;
		labelSpanClass += Constants.WHITE_SPACE + CSS_RICH_MENU_ITEM_LABEL + Constants.WHITE_SPACE + CSS_RICH_MENU_GROUP_LABEL;
		folderDivClass += Constants.WHITE_SPACE + CSS_RICH_MENU_ITEM_FOLDER + Constants.WHITE_SPACE + CSS_RICH_MENU_GROUP_FOLDER;
		
		if (ComponentUtil.isNotBlank(attrs.getStyleClass())) {
			topDivClass += Constants.WHITE_SPACE + attrs.getStyleClass();
		}
		if (ComponentUtil.isNotBlank(attrs.getIconClass())) {
			imgSpanClass += Constants.WHITE_SPACE + attrs.getIconClass();
			folderDivClass += Constants.WHITE_SPACE + attrs.getIconClass();
		}
		if (ComponentUtil.isNotBlank(attrs.getLabelClass())) {
			labelSpanClass += Constants.WHITE_SPACE + attrs.getLabelClass();
		}
		
//		grTopDiv.setAttribute(HTML.ATTR_CLASS, topDivClass);
		grTopDiv.setAttribute(HTML.ATTR_CLASS, CSS_MENU_GROUP_TOP_DIV);
		grMainLI.setAttribute(HTML.ATTR_CLASS, topDivClass);
		grImgSpan.setAttribute(HTML.ATTR_CLASS, imgSpanClass);
		grLabelSpan.setAttribute(HTML.ATTR_CLASS, labelSpanClass);
		grFolderImgSpan.setAttribute(HTML.ATTR_CLASS, folderDivClass);
//		grListBorderDiv.setAttribute(HTML.ATTR_CLASS, CSS_RICH_MENU_LIST_BORDER);
//		grListBgDiv.setAttribute(HTML.ATTR_CLASS, CSS_RICH_MENU_LIST_BG);
		grChildrenUL.setAttribute(HTML.ATTR_CLASS, CSS_RICH_MENU_LIST_BORDER
				+ Constants.WHITE_SPACE + CSS_RICH_MENU_LIST_BG);
		/*
		 * Setting css styles
		 */
		String topDivStyle = Constants.EMPTY;
		
		if (ComponentUtil.isNotBlank(attrs.getStyle())) {
			topDivStyle += Constants.WHITE_SPACE + attrs.getStyle();
		}
		
		grMainLI.setAttribute(HTML.ATTR_STYLE, topDivStyle);
		grFolderImgSpan.setAttribute(HTML.ATTR_STYLE, CSS_RICH_LIST_FOLDER_DIV_STYLE);
//		grListBorderDiv.setAttribute(HTML.ATTR_STYLE, CSS_RICH_LIST_BORDER_DIV_STYLE);
//		grChildrenUL.setAttribute(HTML.ATTR_STYLE,
//				CSS_RICH_LIST_FOLDER_DIV_STYLE + Constants.WHITE_SPACE
//						+ CSS_RICH_LIST_BORDER_DIV_STYLE);
		
		/*
		 * Encode label value
		 */
		String labelValue = Constants.EMPTY;
		if (ComponentUtil.isNotBlank(attrs.getValue())) {
		    labelValue = attrs.getValue();
		}
		grLabelText.setNodeValue(labelValue);
		
		/*
		 * Encode icon facets
		 */
		Element iconFacet = ComponentUtil.getFacet(sourceElement, ICON_FACET_NAME);
//		Element iconDisabledFacet = ComponentUtil.getFacet(sourceElement, ICON_DISABLED_FACET_NAME);
		if (null != iconFacet) {
			VpeChildrenInfo childInfo = new VpeChildrenInfo(grImgSpan);
			childInfo.addSourceChild(iconFacet);
			creationData.addChildrenInfo(childInfo);
		} else {
			if (ComponentUtil.isNotBlank(attrs.getIcon())) {
				/*
				 * Add path to specified image
				 */
				String imgFullPath = VpeStyleUtil.addFullPathToImgSrc(attrs.getIcon(), pageContext, true);
				grImg.setAttribute(HTML.ATTR_SRC, imgFullPath);
			} else {
				/*
				 * Create spacer image
				 */
				ComponentUtil.setImg(grImg, SPACER_IMG_PATH);
			}
			/*
			 * Add image to span
			 */
			grImgSpan.appendChild(grImg);
			
		}
		
		/*
		 * Add group folder icon 
		 */
		if (ComponentUtil.isNotBlank(attrs.getIconFolder())) {
			/*
			 * Add path to specified image
			 */
			String imgFullPath = VpeStyleUtil.addFullPathToImgSrc(attrs.getIconFolder(), pageContext, true);
			grFolderImg.setAttribute(HTML.ATTR_SRC, imgFullPath);
		} else {
			/*
			 * Create default arrow image
			 */
			ComponentUtil.setImg(grFolderImg, FOLDER_IMG_PATH);
		}
		/*
		 * Add image to group folder div
		 */
		grFolderImgSpan.appendChild(grFolderImg);
		
		/*
		 * Adding child nodes:
		 * <rich:menuGroup> and <rich:menuItem> only.
		 */
		List<Node> children = ComponentUtil.getChildren(sourceElement);
		boolean missingChildContainer = true;
		for (Node child : children) {
			if (child.getNodeType() == Node.ELEMENT_NODE
					&& (child.getNodeName().endsWith(CHILD_GROUP_NAME) 
							|| child.getNodeName().endsWith(CHILD_ITEM_NAME))) {
				if (missingChildContainer) {
					/*
					 * Add children <ul> tag.
					 */
					grMainLI.appendChild(grChildrenUL);
					missingChildContainer = false;
				}
				VpeChildrenInfo childDivInfo = new VpeChildrenInfo(grChildrenUL);
				childDivInfo.addSourceChild(child);
				creationData.addChildrenInfo(childDivInfo);
			}
		}

		return creationData;
	}

	@Override
	public boolean recreateAtAttrChange(VpePageContext pageContext,
		Element sourceElement, nsIDOMDocument visualDocument,
		nsIDOMElement visualNode, Object data, String name, String value) {
	    return true;
	}

	class Attributes {

	    /*
	     * rich:menuGroup attributes names
	     */
	    private String DIRECTION = "direction"; //$NON-NLS-1$
	    private String ICON = "icon"; //$NON-NLS-1$
	    private String ICON_DISABLED = "iconDisabled"; //$NON-NLS-1$
	    private String ICON_FOLDER = "iconFolder"; //$NON-NLS-1$
	    private String ICON_FOLDER_DISABLED = "iconFolderDisabled"; //$NON-NLS-1$

	    /*
	     * rich:menuGroup css styles and classes attributes names
	     */
	    private String ICON_CLASS = "iconClass"; //$NON-NLS-1$
	    private String ICON_STYLE = "iconStyle"; //$NON-NLS-1$
	    private String LABEL_CLASS = "labelClass"; //$NON-NLS-1$
	    private String SELECT_CLASS = "selectClass"; //$NON-NLS-1$
	    private String SELECT_STYLE = "selectStyle"; //$NON-NLS-1$

	    /*
	     * rich:menuGroup attributes
	     */
	    private String mg_icon;
	    private String mg_iconFolder;
	    private String mg_direction;
	    private String mg_disabled;
	    private String mg_iconDisabled;
	    private String mg_iconFolderDisabled;
	    private String mg_value;

	    /*
	     * rich:menuGroup css styles and classes attributes
	     */
	    private String mg_iconClass;
	    private String mg_iconStyle;
	    private String mg_labelClass;
	    private String mg_selectClass;
	    private String mg_selectStyle;
	    private String mg_style;
	    private String mg_styleClass;

	    public Attributes(final Element sourceElement) {
		if (null == sourceElement) {
		    return;
		}
		mg_direction = sourceElement.getAttribute(DIRECTION);
		mg_disabled = sourceElement.getAttribute(HTML.ATTR_DISABLED);
		mg_icon = sourceElement.getAttribute(ICON);
		mg_iconDisabled = sourceElement.getAttribute(ICON_DISABLED);
		mg_iconFolder = sourceElement.getAttribute(ICON_FOLDER);
		mg_iconFolderDisabled = sourceElement.getAttribute(ICON_FOLDER_DISABLED);
		mg_value = sourceElement.getAttribute(HTML.ATTR_VALUE);

		mg_iconClass = sourceElement.getAttribute(ICON_CLASS);
		mg_iconStyle = sourceElement.getAttribute(ICON_STYLE);
		mg_labelClass = sourceElement.getAttribute(LABEL_CLASS);
		mg_selectClass = sourceElement.getAttribute(SELECT_CLASS);
		mg_selectStyle = sourceElement.getAttribute(SELECT_STYLE);
		mg_style = sourceElement.getAttribute(HTML.ATTR_STYLE);
		mg_styleClass = sourceElement.getAttribute(RichFaces.ATTR_STYLE_CLASS);
	    }

	    public String getIconClass() {
		return mg_iconClass;
	    }

	    public String getLabelClass() {
		return mg_labelClass;
	    }

	    public String getStyle() {
		return mg_style;
	    }

	    public String getStyleClass() {
		return mg_styleClass;
	    }

	    public String getIcon() {
		return mg_icon;
	    }

	    public String getIconFolder() {
		return mg_iconFolder;
	    }

	    public String getIconStyle() {
		return mg_iconStyle;
	    }

	    public String getValue() {
		return mg_value;
	    }

	}
		
}
