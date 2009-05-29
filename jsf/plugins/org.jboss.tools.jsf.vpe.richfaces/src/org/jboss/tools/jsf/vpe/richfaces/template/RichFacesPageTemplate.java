package org.jboss.tools.jsf.vpe.richfaces.template;

import org.jboss.tools.jsf.vpe.richfaces.ComponentUtil;
import org.jboss.tools.jsf.vpe.richfaces.template.util.RichFaces;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeAbstractTemplate;
import org.jboss.tools.vpe.editor.template.VpeChildrenInfo;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.util.HTML;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class RichFacesPageTemplate extends VpeAbstractTemplate {

    private static final String CSS_BASIC_STYLE_PATH = "page/page-basic.css"; //$NON-NLS-1$
    private static final String CSS_SIMPLE_THEME_STYLE_PATH = "page/page-theme_simple.css"; //$NON-NLS-1$
    private static final String COMPONENT_NAME = "richFacesPage"; //$NON-NLS-1$
	
//	private static final String FACET_NAME_PAGE_HEADER = "pageHeader"; //$NON-NLS-1$
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
		 * Add basic style classes. 
		 */
		String styleClass = CSS_PAGE;
		if (ComponentUtil.isNotBlank(attrs.getStyleClass())) {
			styleClass += attrs.getStyleClass();
		}
		if (ComponentUtil.isNotBlank(attrs.getStyle())) {
			pageDiv.setAttribute(HTML.ATTR_STYLE, attrs.getStyle());
		}
		pageDiv.setAttribute(HTML.ATTR_CLASS, styleClass);
		pageContentDiv.setAttribute(HTML.ATTR_CLASS, CSS_PAGE_CONTENT);
		pageMainDiv.setAttribute(HTML.ATTR_CLASS, CSS_PAGE_MAIN);
		pageBodyDiv.setAttribute(HTML.ATTR_CLASS, CSS_PAGE_BODY);
		
		/*
		 * 1)
		 * Encode page header facet,
		 * add header divs and classes
		 */
		Element headerFacet = ComponentUtil.getFacet(sourceElement, RichFaces.NAME_FACET_HEADER);
		if(headerFacet != null) {
			pageHeaderDiv = visualDocument.createElement(HTML.TAG_DIV);
			pageHeaderContentDiv = visualDocument.createElement(HTML.TAG_DIV);
			
			pageHeaderDiv.setAttribute(HTML.ATTR_CLASS, CSS_PAGE_HEADER);
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
			pageSubHeaderDiv.setAttribute(HTML.ATTR_CLASS, CSS_PAGE_SUBHEADER);
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
			pageSidebarDiv.setAttribute(HTML.ATTR_CLASS, CSS_PAGE_SIDEBAR);
			pageContentDiv.appendChild(pageSidebarDiv);
			
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
			
			pageFooterDiv.setAttribute(HTML.ATTR_CLASS, CSS_PAGE_FOOTER);
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
		private final String LANG = "lang"; //$NON-NLS-1$
		private final String MARKUP_TYPE = "markupType"; //$NON-NLS-1$
		private final String NAMESPACE = "namespace"; //$NON-NLS-1$
		private final String PAGE_TITLE = "pageTitle"; //$NON-NLS-1$
		private final String SIDERBAR_CLASS = "siderbarClass"; //$NON-NLS-1$
		private final String SIDERBAR_POSITION = "siderbarPosition"; //$NON-NLS-1$
		private final String SIDERBAR_WIDTH = "siderbarWidth"; //$NON-NLS-1$
		private final String THEME = "theme"; //$NON-NLS-1$

		private String bodyClass;
		private String contentClass;
		private String footerClass;
		private String headerClass;
		private String lang;
		private String markupType;
		private String namespace;
		private String pageTitle;
		private String siderbarClass;
		private String siderbarPosition;
		private String siderbarWidth;
		private String style;
		private String styleClass;
		private String theme;
		private String width;
		
		public Attributes(final Element sourceElement) {
			bodyClass = sourceElement.getAttribute(BODY_CLASS);
			contentClass = sourceElement.getAttribute(CONTENT_CLASS);
			footerClass = sourceElement.getAttribute(FOOTER_CLASS);
			headerClass = sourceElement.getAttribute(HEADER_CLASS);
			lang = sourceElement.getAttribute(LANG);
			markupType = sourceElement.getAttribute(MARKUP_TYPE);
			namespace = sourceElement.getAttribute(NAMESPACE);
			pageTitle = sourceElement.getAttribute(PAGE_TITLE);
			siderbarClass = sourceElement.getAttribute(SIDERBAR_CLASS);
			siderbarPosition = sourceElement.getAttribute(SIDERBAR_POSITION);
			siderbarWidth = sourceElement.getAttribute(SIDERBAR_WIDTH);
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

		public String getMarkupType() {
			return markupType;
		}

		public String getNamespace() {
			return namespace;
		}

		public String getPageTitle() {
			return pageTitle;
		}

		public String getSiderbarClass() {
			return siderbarClass;
		}

		public String getSiderbarPosition() {
			return siderbarPosition;
		}

		public String getSiderbarWidth() {
			return siderbarWidth;
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
