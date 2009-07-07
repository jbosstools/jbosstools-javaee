/*******************************************************************************
 * Copyright (c) 2007-2009 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.jsf.vpe.richfaces.template;

import org.jboss.tools.jsf.vpe.richfaces.ComponentUtil;
import org.jboss.tools.jsf.vpe.richfaces.template.util.RichFaces;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeAbstractTemplate;
import org.jboss.tools.vpe.editor.template.VpeChildrenInfo;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.util.Constants;
import org.jboss.tools.vpe.editor.util.HTML;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class RichFacesPageTemplate extends VpeAbstractTemplate {

    private static final String CSS_BASIC_STYLE_PATH = "page/page-basic.css"; //$NON-NLS-1$
    private static final String CSS_SIMPLE_THEME_STYLE_PATH = "page/page-theme_simple.css"; //$NON-NLS-1$
    private static final String COMPONENT_NAME = "richFacesPage"; //$NON-NLS-1$
	
	private static final String FACET_NAME_SUBHEADER = "subheader"; //$NON-NLS-1$
	private static final String FACET_NAME_SIDEBAR = "sidebar"; //$NON-NLS-1$

	private static final String CSS_PAGE = "rich-page"; //$NON-NLS-1$
	private static final String CSS_PAGE_HEADER = "rich-page-header"; //$NON-NLS-1$
	private static final String CSS_PAGE_HEADER_CONTENT = "rich-page-header-content"; //$NON-NLS-1$
	private static final String CSS_PAGE_SUBHEADER = "rich-page-subheader"; //$NON-NLS-1$
	private static final String CSS_PAGE_CONTENT = "rich-page-content"; //$NON-NLS-1$
	private static final String CSS_PAGE_SIDEBAR = "rich-page-sidebar"; //$NON-NLS-1$
	private static final String CSS_PAGE_MAIN = "rich-page-main"; //$NON-NLS-1$
	private static final String CSS_PAGE_BODY = "rich-page-body"; //$NON-NLS-1$
	private static final String CSS_PAGE_FOOTER = "rich-page-footer"; //$NON-NLS-1$
	private static final String CSS_PAGE_FOOTER_CONTENT = "rich-page-footer-content"; //$NON-NLS-1$

	private static final String STYLE_SIDEBAR_LEFT = "float: left;"; //$NON-NLS-1$
	private static final String STYLE_PAGE_MAIN_RIGHT = "float: right;margin-left: -30em;"; //$NON-NLS-1$
	
	private static final String STYLE_SIDEBAR_RIGHT = "float: right;"; //$NON-NLS-1$
	private static final String STYLE_PAGE_MAIN_LEFT = "float: left;margin-right: -30em;"; //$NON-NLS-1$

	/**
	 * Constructor
	 */
	public RichFacesPageTemplate() {
		super();
	}

	public VpeCreationData create(VpePageContext pageContext, Node sourceNode,
			nsIDOMDocument visualDocument) {
		VpeCreationData creationData = null;
		Element sourceElement = (Element)sourceNode;
		
		/*
		 * Adding default css file for rich:page component
		 */
		ComponentUtil.setCSSLink(pageContext, CSS_BASIC_STYLE_PATH, COMPONENT_NAME);
		
		/*
		 * Read rich:page's attributes
		 */
		Attributes attrs = new Attributes(sourceElement);
		
		/*
		 * Create divs' structure.
		 */
		nsIDOMElement pageDiv = visualDocument.createElement(HTML.TAG_DIV);
		nsIDOMElement pageContentDiv = visualDocument.createElement(HTML.TAG_DIV);
		nsIDOMElement pageMainDiv = visualDocument.createElement(HTML.TAG_DIV);
		nsIDOMElement pageBodyDiv = visualDocument.createElement(HTML.TAG_DIV);
		nsIDOMElement pageHeaderDiv;
		nsIDOMElement pageHeaderContentDiv;
		nsIDOMElement pageSubHeaderDiv;
		nsIDOMElement pageSidebarDiv;
		nsIDOMElement pageFooterDiv;
		nsIDOMElement pageFooterContentDiv;
		creationData = new VpeCreationData(pageDiv);
		
		/*
		 * Add basic style classes and attributes. 
		 */
		String styleClass = CSS_PAGE;
		if (ComponentUtil.isNotBlank(attrs.getStyleClass())) {
			styleClass += Constants.WHITE_SPACE + attrs.getStyleClass();
		}
		pageDiv.setAttribute(HTML.ATTR_CLASS, styleClass);
		String style = Constants.EMPTY;
		/*
		 * Page's width in 'em' does not affect its size in VPE, 
		 * so width should be set in 'px' to come into effect. 
		 */
		if (ComponentUtil.isNotBlank(attrs.getWidth())) {
			style += Constants.WHITE_SPACE + "; width: " + attrs.getWidth() + "px;"; //$NON-NLS-1$ //$NON-NLS-2$
		}
		
		if (ComponentUtil.isNotBlank(attrs.getStyle())) {
			style += Constants.WHITE_SPACE + attrs.getStyle();
		}
		pageDiv.setAttribute(HTML.ATTR_STYLE, style);
		if (ComponentUtil.isNotBlank(attrs.getDir())) {
			pageDiv.setAttribute(HTML.ATTR_DIR, attrs.getDir());
		}
		
		pageContentDiv.setAttribute(HTML.ATTR_CLASS, CSS_PAGE_CONTENT);
		pageMainDiv.setAttribute(HTML.ATTR_CLASS, CSS_PAGE_MAIN);
		String bodyClass = CSS_PAGE_BODY;
		if (ComponentUtil.isNotBlank(attrs.getBodyClass())) {
			bodyClass += Constants.WHITE_SPACE + attrs.getBodyClass();
		}
		pageBodyDiv.setAttribute(HTML.ATTR_CLASS, bodyClass);
		
		/*
		 * 1)
		 * Encode page header facet,
		 * add header divs and classes
		 */
		Element headerFacet = ComponentUtil.getFacet(sourceElement, RichFaces.NAME_FACET_HEADER);
		if(headerFacet != null) {
			pageHeaderDiv = visualDocument.createElement(HTML.TAG_DIV);
			pageHeaderContentDiv = visualDocument.createElement(HTML.TAG_DIV);

			String headerClass = CSS_PAGE_HEADER;
			if (ComponentUtil.isNotBlank(attrs.getHeaderClass())) {
				headerClass += Constants.WHITE_SPACE + attrs.getHeaderClass();
			}
			pageHeaderDiv.setAttribute(HTML.ATTR_CLASS, headerClass);
			pageHeaderContentDiv.setAttribute(HTML.ATTR_CLASS, CSS_PAGE_HEADER_CONTENT);

			pageHeaderDiv.appendChild(pageHeaderContentDiv);
			pageDiv.appendChild(pageHeaderDiv);
			
			VpeChildrenInfo headerInfo = new VpeChildrenInfo(pageHeaderContentDiv);
		    headerInfo.addSourceChild(headerFacet);
		    creationData.addChildrenInfo(headerInfo);
		}
		
		/*
		 * 2)
		 * Encode page subheader facet,
		 * add subheader divs and classes
		 */
		Element subHeaderFacet = ComponentUtil.getFacet(sourceElement, FACET_NAME_SUBHEADER);
		if(subHeaderFacet != null) {
			pageSubHeaderDiv = visualDocument.createElement(HTML.TAG_DIV);
			String subheaderClass = CSS_PAGE_SUBHEADER;
			if (ComponentUtil.isNotBlank(attrs.getSubHeaderClass())) {
				subheaderClass += Constants.WHITE_SPACE + attrs.getSubHeaderClass();
			}
			pageSubHeaderDiv.setAttribute(HTML.ATTR_CLASS, subheaderClass);
			pageDiv.appendChild(pageSubHeaderDiv);
			
			VpeChildrenInfo subHeaderInfo = new VpeChildrenInfo(pageSubHeaderDiv);
			subHeaderInfo.addSourceChild(subHeaderFacet);
			creationData.addChildrenInfo(subHeaderInfo);
		}
		
		/*
		 * 3)
		 * Nesting page content div.
		 */
		pageDiv.appendChild(pageContentDiv);
		
		/*
		 * 4)
		 * Encode page sidebar facet,
		 * add sidebar divs and classes
		 */
		Element sidebarFacet = ComponentUtil.getFacet(sourceElement, FACET_NAME_SIDEBAR);
		if(sidebarFacet != null) {
			pageSidebarDiv = visualDocument.createElement(HTML.TAG_DIV);
			String sidebarClass = CSS_PAGE_SIDEBAR;
			if (ComponentUtil.isNotBlank(attrs.getSidebarClass())) {
				sidebarClass += Constants.WHITE_SPACE + attrs.getSidebarClass();
			}
			pageSidebarDiv.setAttribute(HTML.ATTR_CLASS, sidebarClass);
			pageContentDiv.appendChild(pageSidebarDiv);

			/*
			 * Processing attributes for sidebar style, width and position.
			 */
			String sidebarStyle = Constants.EMPTY;
			String pageMainDivStyle = Constants.EMPTY;
			String pageBodyStyle = Constants.EMPTY;
			double widthDouble = ComponentUtil.parseWidth(attrs.getSidebarWidth());
			if (widthDouble != -1) {
				sidebarStyle += Constants.WHITE_SPACE + "; width: " + widthDouble + "em;"; //$NON-NLS-1$ //$NON-NLS-2$
			}			
			
			/*
			 * If sidebar position 'right' is specified explicitly
			 * add 'float: right' style to the sidebar, 
			 * otherwise default 'left' position will be used.
			 */
			if (ComponentUtil.isNotBlank(attrs.getSidebarPosition())
					&& RichFaces.VALUE_RIGHT.equalsIgnoreCase(attrs
							.getSidebarPosition())) {
				sidebarStyle += Constants.WHITE_SPACE + STYLE_SIDEBAR_RIGHT;
				pageMainDivStyle += Constants.WHITE_SPACE + STYLE_PAGE_MAIN_LEFT;
				if (widthDouble != -1) {
					pageBodyStyle += Constants.WHITE_SPACE + ";margin-right: " + widthDouble + "em;"; //$NON-NLS-1$ //$NON-NLS-2$
				}
			} else {
				sidebarStyle += Constants.WHITE_SPACE + STYLE_SIDEBAR_LEFT;
				pageMainDivStyle += Constants.WHITE_SPACE + STYLE_PAGE_MAIN_RIGHT;
				if (widthDouble != -1) {
					pageBodyStyle += Constants.WHITE_SPACE + ";margin-left: " + widthDouble + "em;"; //$NON-NLS-1$ //$NON-NLS-2$
				}
			}
			
			pageSidebarDiv.setAttribute(HTML.ATTR_STYLE, sidebarStyle);
			pageMainDiv.setAttribute(HTML.ATTR_STYLE, pageMainDivStyle);
			pageBodyDiv.setAttribute(HTML.ATTR_STYLE, pageBodyStyle);
			
			VpeChildrenInfo sidebarInfo = new VpeChildrenInfo(pageSidebarDiv);
		    sidebarInfo.addSourceChild(sidebarFacet);
		    creationData.addChildrenInfo(sidebarInfo);
		}
		
		/*
		 * 5)
		 * Nesting page's main and body divs.
		 * Encode all children into page body div.
		 */
		pageContentDiv.appendChild(pageMainDiv);
		pageMainDiv.appendChild(pageBodyDiv);
		VpeChildrenInfo pageBodyDivInfo = new VpeChildrenInfo(pageBodyDiv);
		for (Node child : ComponentUtil.getChildren(sourceElement, true)) {
			pageBodyDivInfo.addSourceChild(child);
		}
		creationData.addChildrenInfo(pageBodyDivInfo);
		
		/*
		 * 6)
		 * Encode page footer facet,
		 * add footer divs and classes
		 */
		Element footerFacet = ComponentUtil.getFacet(sourceElement, RichFaces.NAME_FACET_FOOTER);
		if(footerFacet != null) {
			pageFooterDiv = visualDocument.createElement(HTML.TAG_DIV);
			pageFooterContentDiv = visualDocument.createElement(HTML.TAG_DIV);
			String footerClass = CSS_PAGE_FOOTER;
			if (ComponentUtil.isNotBlank(attrs.getFooterClass())) {
				footerClass += Constants.WHITE_SPACE + attrs.getFooterClass();
			}
			pageFooterDiv.setAttribute(HTML.ATTR_CLASS, footerClass);
			pageFooterContentDiv.setAttribute(HTML.ATTR_CLASS, CSS_PAGE_FOOTER_CONTENT);
			
			pageFooterDiv.appendChild(pageFooterContentDiv);
			pageDiv.appendChild(pageFooterDiv);
			
			VpeChildrenInfo footerInfo = new VpeChildrenInfo(pageFooterContentDiv);
		    footerInfo.addSourceChild(footerFacet);
		    creationData.addChildrenInfo(footerInfo);
		}
		
		return creationData;
	}
	
	class Attributes {
		
		/*
		 *	rich:page attributes for groups
		 */
		private final String BODY_CLASS = "bodyClass"; //$NON-NLS-1$
		private final String CONTENT_CLASS = "contentClass"; //$NON-NLS-1$
		private final String FOOTER_CLASS = "footerClass"; //$NON-NLS-1$
		private final String HEADER_CLASS = "headerClass"; //$NON-NLS-1$
		private final String SUBHEADER_CLASS = "subheaderClass"; //$NON-NLS-1$
		private final String LANG = "lang"; //$NON-NLS-1$
		private final String MARKUP_TYPE = "markupType"; //$NON-NLS-1$
		private final String NAMESPACE = "namespace"; //$NON-NLS-1$
		private final String PAGE_TITLE = "pageTitle"; //$NON-NLS-1$
		private final String SIDEBAR_CLASS = "sidebarClass"; //$NON-NLS-1$
		private final String SIDEBAR_POSITION = "sidebarPosition"; //$NON-NLS-1$
		private final String SIDEBAR_WIDTH = "sidebarWidth"; //$NON-NLS-1$
		private final String THEME = "theme"; //$NON-NLS-1$

		private String bodyClass;
		private String contentClass;
		private String footerClass;
		private String headerClass;
		private String subheaderClass;
		private String dir;
		private String lang;
		private String markupType;
		private String namespace;
		private String pageTitle;
		private String sidebarClass;
		private String sidebarPosition;
		private String sidebarWidth;
		private String style;
		private String styleClass;
		private String theme;
		private String width;
		
		public Attributes(final Element sourceElement) {
			bodyClass = sourceElement.getAttribute(BODY_CLASS);
			contentClass = sourceElement.getAttribute(CONTENT_CLASS);
			footerClass = sourceElement.getAttribute(FOOTER_CLASS);
			headerClass = sourceElement.getAttribute(HEADER_CLASS);
			subheaderClass = sourceElement.getAttribute(SUBHEADER_CLASS);
			dir = sourceElement.getAttribute(HTML.ATTR_DIR);
			lang = sourceElement.getAttribute(LANG);
			markupType = sourceElement.getAttribute(MARKUP_TYPE);
			namespace = sourceElement.getAttribute(NAMESPACE);
			pageTitle = sourceElement.getAttribute(PAGE_TITLE);
			sidebarClass = sourceElement.getAttribute(SIDEBAR_CLASS);
			sidebarPosition = sourceElement.getAttribute(SIDEBAR_POSITION);
			sidebarWidth = sourceElement.getAttribute(SIDEBAR_WIDTH);
			style = sourceElement.getAttribute(HTML.ATTR_STYLE);
			styleClass = sourceElement.getAttribute(RichFaces.ATTR_STYLE_CLASS);
			theme = sourceElement.getAttribute(THEME);
			width = sourceElement.getAttribute(HTML.ATTR_WIDTH);
		}
		
		
		public String getLang() {
			return lang;
		}

		public void setLang(String lang) {
			this.lang = lang;
		}

		public String getBodyClass() {
			return bodyClass;
		}

		public String getContentClass() {
			return contentClass;
		}

		public String getFooterClass() {
			return footerClass;
		}

		public String getHeaderClass() {
			return headerClass;
		}
		
		public String getSubHeaderClass() {
			return subheaderClass;
		}

		public String getDir() {
			return dir;
		}
		
		public String getMarkupType() {
			return markupType;
		}

		public String getNamespace() {
			return namespace;
		}

		public String getPageTitle() {
			return pageTitle;
		}

		public String getSidebarClass() {
			return sidebarClass;
		}

		public String getSidebarPosition() {
			return sidebarPosition;
		}

		public String getSidebarWidth() {
			return sidebarWidth;
		}
		
		public String getStyle() {
			return style;
		}
		
		public String getStyleClass() {
			return styleClass;
		}

		public String getTheme() {
			return theme;
		}

		public String getWidth() {
			return width;
		}
	}

}
