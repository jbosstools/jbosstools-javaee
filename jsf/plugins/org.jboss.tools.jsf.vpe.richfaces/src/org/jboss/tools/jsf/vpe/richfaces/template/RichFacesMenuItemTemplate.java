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

public class RichFacesMenuItemTemplate extends VpeAbstractTemplate {
	
	/*
	 * rich:menuItem constants
	 */
	private static final String COMPONENT_NAME = "menuItem"; //$NON-NLS-1$
	private static final String STYLE_PATH = "menuItem/menuItem.css"; //$NON-NLS-1$
	private static final String SPACER_IMG_PATH = "menuItem/spacer.gif"; //$NON-NLS-1$
	private static final String ICON_FACET_NAME = "icon"; //$NON-NLS-1$
	private static final String ICON_DISABLED_FACET_NAME = "iconDisabled"; //$NON-NLS-1$
	
	/*
	 * Constants for drop down mechanism.
	 */
	private static  final String MENU_CHILD_ID = "vpe-ddm-menu-li"; //$NON-NLS-1$
	
	/*
	 * rich:menuItem css styles names
	 */
	private static final String CSS_RICH_MENU_ITEM = "rich-menu-item"; //$NON-NLS-1$
	private static final String CSS_RICH_MENU_ITEM_LABEL = "rich-menu-item-label"; //$NON-NLS-1$
	private static final String CSS_RICH_MENU_ITEM_ICON = "rich-menu-item-icon"; //$NON-NLS-1$
	private static final String CSS_RICH_MENU_ITEM_DISABLED = "rich-menu-item-disabled"; //$NON-NLS-1$
	private static final String CSS_RICH_MENU_ITEM_ENABLED = "rich-menu-item-enabled"; //$NON-NLS-1$
	private static final String CSS_RICH_MENU_ITEM_HOVER = "rich-menu-item-hover"; //$NON-NLS-1$
	private static final String CSS_RICH_MENU_ITEM_LABEL_DISBLED = "rich-menu-item-label-disabled"; //$NON-NLS-1$
	private static final String CSS_RICH_MENU_ITEM_ICON_DISABLED = "rich-menu-item-icon-disabled"; //$NON-NLS-1$
	private static final String CSS_RICH_MENU_ITEM_LABEL_ENABLED = "rich-menu-item-label-enabled"; //$NON-NLS-1$
	private static final String CSS_RICH_MENU_ITEM_ICON_ENABLED = "rich-menu-item-icon-enabled"; //$NON-NLS-1$
	private static final String CSS_RICH_MENU_ITEM_LABEL_SELECTED = "rich-menu-item-label-selected"; //$NON-NLS-1$
	private static final String CSS_RICH_MENU_ITEM_ICON_SELECTED = "rich-menu-item-icon-selected"; //$NON-NLS-1$
	private static final String CSS_MENU_ITEM_TOP_DIV = "dr-menu-item-top-div"; //$NON-NLS-1$
	
	public VpeCreationData create(VpePageContext pageContext, Node sourceNode,
			nsIDOMDocument visualDocument) {
		VpeCreationData creationData = null;
		Element sourceElement = (Element)sourceNode;
		ComponentUtil.setCSSLink(pageContext, STYLE_PATH, COMPONENT_NAME);
		final Attributes attrs = new Attributes(sourceElement);
		
		/*
		 * MenuItem component structure.
		 * In order of  nesting.
		 */
		nsIDOMElement itemMainLI;
		
		nsIDOMElement itemTopDiv;
		nsIDOMElement itemIconImgSpan;
		nsIDOMElement itemIconImg;
		nsIDOMElement itemLabelSpan;
		nsIDOMText itemLabelText;
		
		/*
		 * Creating visual elements
		 */
		itemMainLI = visualDocument.createElement(HTML.TAG_LI);
		itemTopDiv = visualDocument.createElement(HTML.TAG_DIV);
		itemIconImgSpan = visualDocument.createElement(HTML.TAG_SPAN);
		itemIconImg = visualDocument.createElement(HTML.TAG_IMG);
		itemLabelSpan = visualDocument.createElement(HTML.TAG_SPAN);
		itemLabelText = visualDocument.createTextNode(Constants.EMPTY);
		creationData = new VpeCreationData(itemMainLI);
		
		/*
		 * Nesting elements
		 */
		itemTopDiv.appendChild(itemIconImgSpan);
		itemTopDiv.appendChild(itemLabelSpan);
		itemLabelSpan.appendChild(itemLabelText);
		itemMainLI.appendChild(itemTopDiv);
		
		/*
		 * Setting attributes for the drop-down mechanism
		 */
		itemMainLI.setAttribute(MENU_CHILD_ID, Constants.EMPTY);
		
		/*
		 * Setting css classes
		 */
		String topDivClass = Constants.EMPTY;
		String iconImgSpanClass = Constants.EMPTY;
		String labelSpanClass = Constants.EMPTY;
		
		topDivClass += Constants.WHITE_SPACE+ CSS_RICH_MENU_ITEM;
		iconImgSpanClass += Constants.WHITE_SPACE + CSS_RICH_MENU_ITEM_ICON;
		labelSpanClass += Constants.WHITE_SPACE + CSS_RICH_MENU_ITEM_LABEL;
		
		if (ComponentUtil.isNotBlank(attrs.getStyleClass())) {
			topDivClass += Constants.WHITE_SPACE + attrs.getStyleClass();
		}
		if (ComponentUtil.isNotBlank(attrs.getIconClass())) {
			iconImgSpanClass += Constants.WHITE_SPACE + attrs.getIconClass();
		}
		if (ComponentUtil.isNotBlank(attrs.getLabelClass())) {
			labelSpanClass += Constants.WHITE_SPACE + attrs.getLabelClass();
		}
		
//		itemTopDiv.setAttribute(HTML.ATTR_CLASS, topDivClass);
		itemTopDiv.setAttribute(HTML.ATTR_CLASS, CSS_MENU_ITEM_TOP_DIV);
		itemMainLI.setAttribute(HTML.ATTR_CLASS, topDivClass);
		itemIconImgSpan.setAttribute(HTML.ATTR_CLASS, iconImgSpanClass);
		itemLabelSpan.setAttribute(HTML.ATTR_CLASS, labelSpanClass);
		

		/*
		 * Setting css styles
		 */
		String topDivStyle = Constants.EMPTY;
		
		if (ComponentUtil.isNotBlank(attrs.getStyle())) {
			topDivStyle += Constants.WHITE_SPACE + attrs.getStyle();
		}
		
//		itemTopDiv.setAttribute(HTML.ATTR_STYLE, topDivStyle);
		itemMainLI.setAttribute(HTML.ATTR_STYLE, topDivStyle);
		
		/*
		 * Encode icon facets
		 */
		Element iconFacet = ComponentUtil.getFacet(sourceElement, ICON_FACET_NAME);
//		Element iconDisabledFacet = ComponentUtil.getFacet(sourceElement, ICON_DISABLED_FACET_NAME);
		if (null != iconFacet) {
			VpeChildrenInfo childInfo = new VpeChildrenInfo(itemIconImgSpan);
			childInfo.addSourceChild(iconFacet);
			creationData.addChildrenInfo(childInfo);
		} else {
			if (ComponentUtil.isNotBlank(attrs.getIcon())) {
				/*
				 * Add path to specified image
				 */
				String imgFullPath = VpeStyleUtil.addFullPathToImgSrc(attrs.getIcon(), pageContext, true);
				itemIconImg.setAttribute(HTML.ATTR_SRC, imgFullPath);
			} else {
				/*
				 * Create spacer image
				 */
				ComponentUtil.setImg(itemIconImg, SPACER_IMG_PATH);
			}
			/*
			 * Add image to span
			 */
			itemIconImgSpan.appendChild(itemIconImg);
		}

		/*
		 * Encode label and icon value
		 */
		String labelValue = Constants.EMPTY;
		if (ComponentUtil.isNotBlank(attrs.getValue())) {
		    labelValue = attrs.getValue();
		}
		itemLabelText.setNodeValue(labelValue);
		
		/*
		 * Adding child nodes, including text nodes.
		 */
		List<Node> children = ComponentUtil.getChildren(sourceElement, true);
		for (Node child : children) {
			VpeChildrenInfo childInfo = new VpeChildrenInfo(itemLabelSpan);
			childInfo.addSourceChild(child);
			creationData.addChildrenInfo(childInfo);
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
	     * rich:menuItem attributes names
	     */
	    private String ICON = "icon"; //$NON-NLS-1$

	    /*
	     * rich:menuItem css styles and classes attributes names
	     */
	    private String ICON_CLASS = "iconClass"; //$NON-NLS-1$
	    private String ICON_DISABLED = "iconDisabled"; //$NON-NLS-1$
	    private String ICON_STYLE = "iconStyle"; //$NON-NLS-1$
	    private String LABEL_CLASS = "labelClass"; //$NON-NLS-1$
	    private String SELECT_STYLE = "selectStyle"; //$NON-NLS-1$
	    private String SELECT_CLASS = "selectClass"; //$NON-NLS-1$

	    /*
	     * rich:menuItem attributes 
	     */
	    private String mi_disabled;
	    private String mi_icon;
	    private String mi_value;

	    /*
	     * rich:menuItem css styles and classes attributes
	     */
	    private String mi_iconClass;
	    private String mi_iconDisabled;
	    private String mi_iconStyle;
	    private String mi_labelClass;
	    private String mi_selectClass;
	    private String mi_selectStyle;
	    private String mi_style;
	    private String mi_styleClass;

	    public Attributes(final Element sourceElement) {
		if (null == sourceElement) {
		    return;
		}
		mi_disabled = sourceElement.getAttribute(HTML.ATTR_DISABLED);
		mi_icon = sourceElement.getAttribute(ICON);
		mi_value = sourceElement.getAttribute(HTML.ATTR_VALUE);

		mi_iconClass = sourceElement.getAttribute(ICON_CLASS);
		mi_iconDisabled = sourceElement.getAttribute(ICON_DISABLED);
		mi_iconStyle = sourceElement.getAttribute(ICON_STYLE);
		mi_labelClass = sourceElement.getAttribute(LABEL_CLASS);
		mi_selectClass = sourceElement.getAttribute(SELECT_CLASS);
		mi_selectStyle = sourceElement.getAttribute(SELECT_STYLE);
		mi_style = sourceElement.getAttribute(HTML.ATTR_STYLE);
		mi_styleClass = sourceElement.getAttribute(RichFaces.ATTR_STYLE_CLASS);
	    }

	    public String getIconClass() {
		return mi_iconClass;
	    }

	    public String getLabelClass() {
		return mi_labelClass;
	    }

	    public String getStyle() {
		return mi_style;
	    }

	    public String getStyleClass() {
		return mi_styleClass;
	    }

	    public String getIcon() {
		return mi_icon;
	    }

	    public String getValue() {
		return mi_value;
	    }

	}

}
