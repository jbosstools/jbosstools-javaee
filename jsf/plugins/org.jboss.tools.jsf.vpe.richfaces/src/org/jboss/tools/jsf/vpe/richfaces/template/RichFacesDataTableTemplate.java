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

import static org.jboss.tools.vpe.xulrunner.util.XPCOM.queryInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jboss.tools.jsf.vpe.richfaces.ComponentUtil;
import org.jboss.tools.jsf.vpe.richfaces.RichFacesTemplatesActivator;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeAbstractTemplate;
import org.jboss.tools.vpe.editor.template.VpeChildrenInfo;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.util.Constants;
import org.jboss.tools.vpe.editor.util.HTML;
import org.jboss.tools.vpe.editor.util.SourceDomUtil;
import org.jboss.tools.vpe.editor.util.VisualDomUtil;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMNode;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class RichFacesDataTableTemplate extends VpeAbstractTemplate {
	
	public VpeCreationData create(VpePageContext pageContext, Node sourceNode, nsIDOMDocument visualDocument) {

		Element sourceElement = (Element)sourceNode;
		nsIDOMElement table = visualDocument.createElement(HTML.TAG_TABLE);
		VisualDomUtil.copyAttributes(sourceNode, table);

		VpeCreationData creationData = new VpeCreationData(table);

		ComponentUtil.setCSSLink(pageContext, "dataTable/dataTable.css", "richFacesDataTable"); //$NON-NLS-1$ //$NON-NLS-2$
		String tableClass = "dr-table rich-table"; //$NON-NLS-1$
		if (sourceElement.hasAttribute(RichFaces.ATTR_STYLE_CLASS)) {
			tableClass += " " + sourceElement.getAttribute(RichFaces.ATTR_STYLE_CLASS); //$NON-NLS-1$
		}
		table.setAttribute(HTML.ATTR_CLASS, tableClass);

		// Encode colgroup definition.
		ArrayList<Element> columns = RichFaces.getColumns(sourceElement);
		int columnsLength = RichFaces.getColumnsCount(sourceElement, columns);
		nsIDOMElement colgroup = visualDocument.createElement(HTML.TAG_COLGROUP);
		colgroup.setAttribute(HTML.ATTR_SPAN, String.valueOf(columnsLength));
		table.appendChild(colgroup);
		
		if (sourceElement.hasAttribute(RichFaces.ATTR_COLUMNS_WIDTH)) {
			String columnsWidth = sourceElement.getAttribute(RichFaces.ATTR_COLUMNS_WIDTH);
			String[] widths = columnsWidth.split(Constants.COMMA);
			for (int i = 0; i < widths.length; i++) {
				nsIDOMElement col = visualDocument.createElement(HTML.TAG_COL);
				col.setAttribute(HTML.ATTR_WIDTH, widths[i]);
				colgroup.appendChild(col);
			}
		}
		
		/*
		 * Encode Caption
		 */
		Element caption = SourceDomUtil.getFacetByName(pageContext,
				sourceElement, RichFaces.NAME_FACET_CAPTION);
		Map<String, List<Node>> captionFacetChildren = VisualDomUtil.findFacetElements(caption, pageContext);
		Node captionNode= null;
		if (captionFacetChildren.get(VisualDomUtil.FACET_JSF_TAG).size() > 0) {
			captionNode = captionFacetChildren.get(VisualDomUtil.FACET_JSF_TAG).get(0);
		}
		encodeCaption(pageContext, creationData, sourceElement, visualDocument,table,captionNode);

		/*
		 * Encode Header
		 */
		Element header = SourceDomUtil.getFacetByName(pageContext,
				sourceElement, RichFaces.NAME_FACET_HEADER);
		boolean tableFacetHasChildren = false;
		if (header != null) {
			tableFacetHasChildren = header.getChildNodes().getLength() > 0;
		}
		boolean hasColumnWithHeader = RichFaces.hasColumnWithFacet(columns, RichFaces.NAME_FACET_HEADER);
		if(tableFacetHasChildren || hasColumnWithHeader) {
			nsIDOMElement thead = visualDocument.createElement(HTML.TAG_THEAD);
			table.appendChild(thead);
			String headerClass = sourceElement.hasAttribute(RichFaces.ATTR_HEADER_CLASS) 
					? sourceElement.getAttribute(RichFaces.ATTR_HEADER_CLASS) : null;
			if(tableFacetHasChildren) {
				/*
				 * Encode Header for the whole table first 
				 */
				encodeTableHeaderOrFooterFacet(pageContext, creationData,
						thead, columnsLength, visualDocument, header,
						"dr-table-header rich-table-header", //$NON-NLS-1$
						"dr-table-header-continue rich-table-header-continue", //$NON-NLS-1$
						"dr-table-headercell rich-table-headercell", //$NON-NLS-1$
						headerClass, HTML.TAG_TD, true);
			}
			if(hasColumnWithHeader) {
				nsIDOMElement tr = visualDocument.createElement(HTML.TAG_TR);
				thead.appendChild(tr);
				String styleClass = ComponentUtil.encodeStyleClass(null, "dr-table-subheader rich-table-subheader", null, headerClass); //$NON-NLS-1$
				if(styleClass!=null) {
					tr.setAttribute(HTML.ATTR_CLASS, styleClass);
				}
				/*
				 * Encode Header for columns
				 */
				encodeHeaderOrFooterFacets(pageContext, creationData, tr,
						visualDocument, columns,
						"dr-table-subheadercell rich-table-subheadercell", //$NON-NLS-1$
						headerClass, RichFaces.NAME_FACET_HEADER, HTML.TAG_TD);
			}
		}

		/*
		 * Encode Footer
		 * Facet cannot be found inside UI:INCLUDE tag
		 */
		Element footer = SourceDomUtil.getFacetByName(pageContext,
				sourceElement, RichFaces.NAME_FACET_FOOTER);
		tableFacetHasChildren = false;
		if (footer != null) {
			tableFacetHasChildren = footer.getChildNodes().getLength() > 0;
		}
		boolean hasColumnWithFooter = RichFaces.hasColumnWithFacet(columns, RichFaces.NAME_FACET_FOOTER);
		if (tableFacetHasChildren || hasColumnWithFooter) {
			nsIDOMElement tfoot = visualDocument.createElement(HTML.TAG_TFOOT);
			table.appendChild(tfoot);
			String footerClass = sourceElement.hasAttribute(RichFaces.ATTR_FOOTER_CLASS) 
					? sourceElement.getAttribute(RichFaces.ATTR_FOOTER_CLASS) : null;
			if(hasColumnWithFooter) {
				nsIDOMElement tr = visualDocument.createElement(HTML.TAG_TR);
				tfoot.appendChild(tr);
				String styleClass = ComponentUtil.encodeStyleClass(null, "dr-table-subfooter rich-table-subfooter", null, footerClass); //$NON-NLS-1$
				if(styleClass!=null) {
					tr.setAttribute(HTML.ATTR_CLASS, styleClass);
				}
				/*
				 * Encode Footer for columns first
				 */
				encodeHeaderOrFooterFacets(pageContext, creationData, tr,
						visualDocument, columns,
						"dr-table-subfootercell rich-table-subfootercell", //$NON-NLS-1$
						footerClass, RichFaces.NAME_FACET_FOOTER, HTML.TAG_TD);
			}
			if (tableFacetHasChildren) {
				/*
				 * Encode Footer for the whole table
				 */
				encodeTableHeaderOrFooterFacet(pageContext, creationData,
						tfoot, columnsLength, visualDocument, footer,
						"dr-table-footer rich-table-footer", //$NON-NLS-1$
						"dr-table-footer-continue rich-table-footer-continue", //$NON-NLS-1$
						"dr-table-footercell rich-table-footercell", //$NON-NLS-1$
						footerClass,HTML.TAG_TD, true);
			}
		}

		new RichFacesDataTableChildrenEncoder(creationData, visualDocument,
				sourceElement, table).encodeChildren();

		return creationData;
	}

	protected void encodeCaption(VpePageContext pageContext,
			VpeCreationData creationData, Element sourceElement,
			nsIDOMDocument visualDocument, nsIDOMElement table, 
			Node captionBody) {
		
		/*
		 * Encode caption
		 */
		if (null != captionBody) {
			
			nsIDOMElement caption = visualDocument.createElement(HTML.TAG_CAPTION);
			table.appendChild(caption);
			
			String defaultCaptionClass = "dr-table-caption rich-table-caption"; //$NON-NLS-1$
			String captionClass = table.hasAttribute(RichFaces.ATTR_CAPTION_CLASS) ? defaultCaptionClass + " " + table.getAttribute(RichFaces.ATTR_CAPTION_CLASS) : defaultCaptionClass; //$NON-NLS-1$
			caption.setAttribute(HTML.ATTR_CLASS, captionClass);
			
			if (table.hasAttribute(RichFaces.ATTR_CAPTION_STYLE)) {
				String captionStyle = table.getAttribute(RichFaces.ATTR_CAPTION_STYLE);
				caption.setAttribute(HTML.ATTR_STYLE, captionStyle);
			}
			
			VpeChildrenInfo cap = new VpeChildrenInfo(caption);
			/*
			 * Display existing JSF component
			 */
			cap.addSourceChild(captionBody);
			creationData.addChildrenInfo(cap);
		}

	}

	public static void encodeHeaderOrFooterFacets(VpePageContext pageContext,
			VpeCreationData creationData, nsIDOMElement parentTr,
			nsIDOMDocument visualDocument, ArrayList<Element> headersOrFooters,
			String skinCellClass, String headerClass, String facetName,
			String element) {
		
		for (Element column : headersOrFooters) {
		    Element facet = SourceDomUtil.getFacetByName(pageContext,
		    		column, facetName);
		    /*
		     * If facet is null unwanted cells might be added.
		     * Thus do not add TD for such facets.
		     */
		    if (null != facet) {
		    	String classAttribute = facetName + "Class"; //$NON-NLS-1$
		    	
		    	String columnHeaderClass = column.hasAttribute(classAttribute) ? column.getAttribute(classAttribute) : null;
		    	nsIDOMElement td = visualDocument.createElement(element);
		    	parentTr.appendChild(td);
		    	String styleClass = ComponentUtil.encodeStyleClass(null, skinCellClass, headerClass, columnHeaderClass);
		    	if (!RichFacesColumnTemplate.isVisible(column)) {
		    		VisualDomUtil.setSubAttribute(td, HTML.ATTR_STYLE,
		    				HTML.STYLE_PARAMETER_DISPLAY, HTML.STYLE_VALUE_NONE);
		    	}
		    	td.setAttribute(HTML.ATTR_CLASS, styleClass);
		    	td.setAttribute(HTML.ATTR_SCOPE, "col"); //$NON-NLS-1$		    	
		    	if(column.hasAttribute("colspan")) { //$NON-NLS-1$
		    		String colspan = column.getAttribute("colspan"); //$NON-NLS-1$
		    		td.setAttribute(HTML.ATTR_COLSPAN, colspan);
		    	}
		    	if (RichFaces.NAME_FACET_HEADER.equals(facetName)) {
		    		nsIDOMElement icon = RichFacesColumnTemplate.getHeaderIcon(pageContext, column, visualDocument);
		    		if (icon != null) {
		    			td.appendChild(icon);
		    		}
		    	}
		    	
		    	/*
		    	 * Add facet source here
		    	 */
		    	VpeChildrenInfo childrenInfo = new VpeChildrenInfo(td);
		    	childrenInfo.addSourceChild(facet);
		    	creationData.addChildrenInfo(childrenInfo);
		    	
			}
		}
	}

	protected void encodeTableHeaderOrFooterFacet(
			final VpePageContext pageContext, VpeCreationData creationData,
			nsIDOMElement parentTheadOrTfood, int columns,
			nsIDOMDocument visualDocument, Node facetBody,
			String skinFirstRowClass, String skinRowClass,
			String skinCellClass, String facetBodyClass, 
			String facetVisualNode, boolean encodeAllFacetChildrenManually) {
		
		if (null == facetBody) {
			RichFacesTemplatesActivator.getDefault().logError("Facet Body is 'null' in <rich:dataTable> !!"); //$NON-NLS-1$
		}
		NodeList allFacetElements = facetBody.getChildNodes();
		int length = allFacetElements.getLength();
		Node child = null;
		boolean isColumnGroup = false;
		boolean isSubTable = false;
		if (length > 0) {
			for (int i = 0; i < length; i++) {
				child = allFacetElements.item(i);
				if (child.getNodeName().endsWith(RichFaces.TAG_COLUMN_GROUP)) {
					isColumnGroup = true;
					facetBody = child;
					break;
				} else if (child.getNodeName().endsWith(RichFaces.TAG_SUB_TABLE)) {
					isSubTable = true;
					facetBody = child;
					break;
				}
			}			
		} else {
			isColumnGroup = facetBody.getNodeName().endsWith(RichFaces.TAG_COLUMN_GROUP);
			isSubTable = facetBody.getNodeName().endsWith(RichFaces.TAG_SUB_TABLE);		
		}
		if(isColumnGroup) {
			RichFacesColumnGroupTemplate.DEFAULT_INSTANCE.encodeSubTable(pageContext, creationData, (Element)facetBody, visualDocument, parentTheadOrTfood);
		} else if(isSubTable) {
			RichFacesSubTableTemplate.DEFAULT_INSTANCE.encodeSubTable(pageContext, creationData, (Element)facetBody, visualDocument, parentTheadOrTfood);
		} else {
			nsIDOMElement tr = visualDocument.createElement(HTML.TAG_TR);
			parentTheadOrTfood.appendChild(tr);

			String styleClass = ComponentUtil.encodeStyleClass(null, skinFirstRowClass, facetBodyClass, null);
			if(styleClass!=null) {
				tr.setAttribute(HTML.ATTR_CLASS, styleClass);
			}
			String style = ComponentUtil.getHeaderBackgoundImgStyle();
			tr.setAttribute(HTML.ATTR_STYLE, style);

			nsIDOMElement td = visualDocument.createElement(facetVisualNode);
			tr.appendChild(td);

			styleClass = ComponentUtil.encodeStyleClass(null, skinCellClass, facetBodyClass, null);
			if(styleClass!=null) {
				td.setAttribute(HTML.ATTR_CLASS, styleClass);
			}

			// the cell spans the entire row
			td.setAttribute(HTML.ATTR_COLSPAN, HTML.VALUE_COLSPAN_ALL);
			
			td.setAttribute(HTML.ATTR_SCOPE, "colgroup"); //$NON-NLS-1$

			VpeChildrenInfo childrenInfo = new VpeChildrenInfo(td);
			if (encodeAllFacetChildrenManually) {
				for (int i = 0; i < length; i++) {
					childrenInfo.addSourceChild(allFacetElements.item(i));
				}
				creationData.addChildrenInfo(childrenInfo);
			} else {
				childrenInfo.addSourceChild(facetBody);
				creationData.addChildrenInfo(childrenInfo);
			}
		}
	}

	/**
	 * @see org.jboss.tools.vpe.editor.template.VpeAbstractTemplate#validate(org.jboss.tools.vpe.editor.context.VpePageContext, org.w3c.dom.Node, org.mozilla.interfaces.nsIDOMDocument, org.jboss.tools.vpe.editor.template.VpeCreationData)
	 */
	@Override
	public void validate(VpePageContext pageContext, Node sourceNode,
			nsIDOMDocument visualDocument, VpeCreationData data) {
		RichFacesDataTableChildrenEncoder.validateChildren(pageContext, sourceNode, visualDocument, data);
		
		final RichFacesDataTableStyleClassesApplier styleClassesApplier = 
			new RichFacesDataTableStyleClassesApplier(visualDocument, 
					pageContext, sourceNode);
		styleClassesApplier.applyClasses((nsIDOMElement) data.getNode());
	}

	@Override
	public void removeAttribute(VpePageContext pageContext, Element sourceElement, nsIDOMDocument visualDocument, nsIDOMNode visualNode, Object data, String name) {
		nsIDOMElement visualElement = queryInterface(visualNode, nsIDOMElement.class); 
		visualElement.removeAttribute(name);
	}

	@Override
	public void setAttribute(VpePageContext pageContext, Element sourceElement, nsIDOMDocument visualDocument, nsIDOMNode visualNode, Object data, String name, String value) {
		nsIDOMElement visualElement = queryInterface(visualNode, nsIDOMElement.class); 
		visualElement.setAttribute(name, value);
	}
}
