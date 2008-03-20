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

import java.util.List;

import org.jboss.tools.jsf.vpe.richfaces.ComponentUtil;
import org.jboss.tools.jsf.vpe.richfaces.HtmlComponentUtil;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeAbstractTemplate;
import org.jboss.tools.vpe.editor.template.VpeChildrenInfo;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class RichFacesTabTemplate extends VpeAbstractTemplate {

	private final static String SPACER_FILE_PATH = "common/spacer.gif"; //$NON-NLS-1$
	private final static String ACTIVE_BKG_FILE_PATH = "tabPanel/activeBackground.gif"; //$NON-NLS-1$
	private final static String INACTIVE_BKG_FILE_PATH = "tabPanel/inactiveBackground.gif"; //$NON-NLS-1$
	private final static String BORDER_FILE_PATH = "tabPanel/border.gif"; //$NON-NLS-1$
	
	private final static String VPE_USER_TOGGLE_ID = "vpe-user-toggle-id"; //$NON-NLS-1$

	private static final String DISABLED = "disabled"; //$NON-NLS-1$
	private static final String LABEL = "label"; //$NON-NLS-1$
	
	private static final String CSS_HEADER = "rich-tab-header"; //$NON-NLS-1$
	private static final String CSS_LABEL = "rich-tab-label"; //$NON-NLS-1$
	private static final String CSS_ACTIVE = "rich-tab-active"; //$NON-NLS-1$
	private static final String CSS_INACTIVE = "rich-tab-inactive"; //$NON-NLS-1$
	private static final String CSS_DISABLED = "rich-tab-disabled"; //$NON-NLS-1$
	
	private static final String ZERO = "0"; //$NON-NLS-1$
	private static final String ONE = "1"; //$NON-NLS-1$
	private static final String SPACE = " "; //$NON-NLS-1$
	private static final String EMPTY = ""; //$NON-NLS-1$
	
	/**
	 * Encode body of tab
	 * @param creationData
	 * @param sourceElement
	 * @param visualDocument
	 * @param parentTr
	 * @param active
	 * @param tabClass
	 * @param activeTabClass
	 * @param inactiveTabClass
	 * @param disabledTabClass
	 * @param contentClass
	 * @param contentStyle
	 * @return
	 */
	public static VpeCreationData encodeBody(VpeCreationData creationData, 
			Element sourceElement, 
			nsIDOMDocument visualDocument, 
			nsIDOMElement parentTr, 
			boolean active,
			String tabClass,
			String activeTabClass,
			String inactiveTabClass,
			String disabledTabClass,
			String contentClass,
			String contentStyle) {

	    	nsIDOMElement td = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_TD);

		if(creationData==null) {
			
			creationData = new VpeCreationData(td);
		} else {
			parentTr.appendChild(td);
		}
		if(!active) {
			return creationData;
		}
//		td.setAttribute("style", "position: relative;");
		td.setAttribute(HtmlComponentUtil.HTML_HEIGHT_ATTR, "100%");
		

		nsIDOMElement table = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_TABLE);
		td.appendChild(table);
		table.setAttribute(HtmlComponentUtil.HTML_BORDER_ATTR, ZERO);
		table.setAttribute(HtmlComponentUtil.HTML_CELLPADDING_ATTR, "10");
		table.setAttribute(HtmlComponentUtil.HTML_CELLSPACING_ATTR, ZERO);
		table.setAttribute(HtmlComponentUtil.HTML_WIDTH_ATTR, "100%");
		table.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR, "dr-tbpnl-cntnt-pstn" + SPACE + RichFacesTabPanelTemplate.CSS_CONTENT_POSITION);
		table.setAttribute(HtmlComponentUtil.HTML_STYLE_ATTR, "position: relative; z-index: 1;");

		nsIDOMElement tr = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_TR);
		table.appendChild(tr);
		td = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_TD);
		tr.appendChild(td);
		td.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR, 
				ComponentUtil.getAttribute(sourceElement, HtmlComponentUtil.HTML_STYLECLASS_ATTR)
				+ SPACE + "dr-tbpnl-cntnt"
				+ SPACE + contentClass);
		td.setAttribute(HtmlComponentUtil.HTML_STYLE_ATTR, 
				ComponentUtil.getAttribute(sourceElement, HtmlComponentUtil.HTML_STYLE_ATTR)
				+ "; " +  contentStyle);

		List<Node> children = ComponentUtil.getChildren(sourceElement, true);
		VpeChildrenInfo bodyInfo = new VpeChildrenInfo(td);
		for (Node child : children) {
			bodyInfo.addSourceChild(child);
		}
		creationData.addChildrenInfo(bodyInfo);

		return creationData;
	}

	public VpeCreationData create(VpePageContext pageContext, Node sourceNode, nsIDOMDocument visualDocument) {
		return encodeBody(null, (Element)sourceNode, visualDocument, null, true, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY);
	}

	/**
	 * Encode Header of tab
	 * @param sourceElement
	 * @param visualDocument
	 * @param parentDiv
	 * @param active
	 * @param activeTabClass
	 * @param inactiveTabClass
	 * @param disabledTabClass
	 */
	public static void encodeHeader(Element sourceElement, 
			nsIDOMDocument visualDocument, 
			nsIDOMElement parentDiv,
			boolean active,
			String activeTabClass,
			String inactiveTabClass,
			String disabledTabClass, 
			String toggleId) {
	    
		nsIDOMElement td = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_TD);
		parentDiv.appendChild(td);
		td.setAttribute(HtmlComponentUtil.HTML_STYLE_ATTR, "height: 100%; vertical-align: bottom;");
		String styleClass = "dr-tbpnl-tbcell-dsbl rich-tabhdr-cell-dsbl";
		if(!"true".equalsIgnoreCase(sourceElement.getAttribute(DISABLED))) {
			if(active) {
				styleClass = "dr-tbpnl-tbcell-act" 
					+ SPACE + RichFacesTabPanelTemplate.CSS_CELL_ACTIVE;
			} else {
				styleClass = "dr-tbpnl-tbcell-inact"
					+ SPACE + RichFacesTabPanelTemplate.CSS_CELL_INACTIVE;
			}
		}
		td.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR, styleClass);
		td.setAttribute(VPE_USER_TOGGLE_ID, toggleId);

		nsIDOMElement table = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_TABLE);
		td.appendChild(table);
		table.setAttribute(HtmlComponentUtil.HTML_BORDER_ATTR, ZERO);
		table.setAttribute(HtmlComponentUtil.HTML_CELLPADDING_ATTR, ZERO);
		table.setAttribute(HtmlComponentUtil.HTML_CELLSPACING_ATTR, ZERO);
		table.setAttribute(HtmlComponentUtil.HTML_STYLE_ATTR, "height : 100%; position : relative; z-index : 2;");
		table.setAttribute(VPE_USER_TOGGLE_ID, toggleId);

		nsIDOMElement mainTr = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_TR);
		table.appendChild(mainTr);
		encodeSpacer(mainTr, visualDocument);

		td = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_TD);
		mainTr.appendChild(td);
		td.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR, "dr-tbpnl-tbtopbrdr"
				+ SPACE + RichFacesTabPanelTemplate.CSS_SIDE_CELL);
		td.setAttribute(HtmlComponentUtil.HTML_STYLE_ATTR, "width: "
				+ ComponentUtil.getAttribute(sourceElement,"labelWidth") + ";");
		td.setAttribute(VPE_USER_TOGGLE_ID, toggleId);

		table = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_TABLE);
		td.appendChild(table);
		table.setAttribute(HtmlComponentUtil.HTML_STYLE_ATTR, "height: 100%; width: 100%;");
		table.setAttribute(HtmlComponentUtil.HTML_BORDER_ATTR, ZERO);
		table.setAttribute(HtmlComponentUtil.HTML_CELLPADDING_ATTR, ZERO);
		table.setAttribute(HtmlComponentUtil.HTML_CELLSPACING_ATTR, ZERO);
		table.setAttribute(VPE_USER_TOGGLE_ID, toggleId);

		nsIDOMElement tr = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_TR);
		table.appendChild(tr);
		td = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_TD);
		tr.appendChild(td);

		styleClass = "dr-tbpnl-tb dr-tbpnl-tb-dsbl"
			+ SPACE + CSS_HEADER
			+ SPACE + CSS_LABEL
			+ SPACE + CSS_DISABLED
			+ SPACE + disabledTabClass;
		String bgImgPath = ComponentUtil.getAbsoluteResourcePath(INACTIVE_BKG_FILE_PATH);

		if(!"true".equalsIgnoreCase(sourceElement.getAttribute(DISABLED))) {
			if(active) {
				styleClass = "dr-tbpnl-tb dr-tbpnl-tb-act"
					+ SPACE + CSS_HEADER
					+ SPACE + CSS_LABEL
					+ SPACE + CSS_ACTIVE
					+ SPACE + activeTabClass;
				bgImgPath = ComponentUtil.getAbsoluteResourcePath(ACTIVE_BKG_FILE_PATH);
			} else {
				styleClass = "dr-tbpnl-tb dr-tbpnl-tb-inact"
					+ SPACE + CSS_HEADER
					+ SPACE + CSS_LABEL
					+ SPACE + CSS_INACTIVE
					+ SPACE + inactiveTabClass;
			}
		}

		td.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR, styleClass);
		String style = "background-image: url(file:///" + bgImgPath.replace('\\', '/') + ");";
		td.setAttribute(HtmlComponentUtil.HTML_STYLE_ATTR, style);
		td.setAttribute(VPE_USER_TOGGLE_ID, toggleId);
		String label = sourceElement.getAttribute(LABEL);
		if(label==null) {
			char space = 160;
			label = EMPTY + space;
		}
		td.appendChild(visualDocument.createTextNode(label));
		encodeSpacer(mainTr, visualDocument);
	}

	/*
	 * Add <td class="dr-tbpnl-tbbrdr rich-tabhdr-side-border"><img src="#{spacer}" width="1" height="1" alt="" border="0" /></td>
	 */
	private static void encodeSpacer(nsIDOMElement parentTr, nsIDOMDocument visualDocument) {
	    	nsIDOMElement td = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_TD);
		parentTr.appendChild(td);
		td.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR, "dr-tbpnl-tbbrdr"
				+ SPACE + RichFacesTabPanelTemplate.CSS_SIDE_BORDER);
		String borderImgPath = ComponentUtil.getAbsoluteResourcePath(BORDER_FILE_PATH);
		String style = "background-image: url(file:///" + borderImgPath.replace('\\', '/') + ");";
		td.setAttribute(HtmlComponentUtil.HTML_STYLE_ATTR, style);
		nsIDOMElement img = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_IMG);
		td.appendChild(img);
		ComponentUtil.setImg(img, SPACER_FILE_PATH);
		img.setAttribute(HtmlComponentUtil.HTML_WIDTH_ATTR, ONE);
		img.setAttribute(HtmlComponentUtil.HTML_HEIGHT_ATTR, ONE);
		img.setAttribute(HtmlComponentUtil.HTML_BORDER_ATTR, ZERO);

	}

}