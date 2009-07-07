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

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

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
import org.w3c.dom.NodeList;

public class RichFacesLayoutTemplate extends VpeAbstractTemplate {

	private static final String FLOAT_LEFT_STYLE = ";float: left;"; //$NON-NLS-1$
	private static final String FLOAT_RIGHT_STYLE = ";float: right;"; //$NON-NLS-1$
	private static final String BOTTOM_SECONDARY_DIV_STYLE = ";display: block; height: 0; clear: both; visibility: hidden;"; //$NON-NLS-1$
	private static final String BOTTOM_SECONDARY_DIV_TEXT = "."; //$NON-NLS-1$
	private static final String LAYOUT_PANEL_NAME = ":layoutPanel"; //$NON-NLS-1$
	
	/**
	 * Constructor
	 */
	public RichFacesLayoutTemplate() {
		super();
	}

	public VpeCreationData create(VpePageContext pageContext, Node sourceNode,
			nsIDOMDocument visualDocument) {
		
		VpeCreationData creationData = null;
		Element sourceElement = (Element)sourceNode;
		nsIDOMElement mainDiv = visualDocument.createElement(HTML.TAG_DIV);
		String style = sourceElement.getAttribute(HTML.ATTR_STYLE);
		if (ComponentUtil.isNotBlank(style)) {
			mainDiv.setAttribute(HTML.ATTR_STYLE, style);
		}
		nsIDOMElement topDiv = visualDocument.createElement(HTML.TAG_DIV);
		nsIDOMElement centerDiv = visualDocument.createElement(HTML.TAG_DIV);
		nsIDOMElement leftDiv = visualDocument.createElement(HTML.TAG_DIV);
		nsIDOMElement rightDiv = visualDocument.createElement(HTML.TAG_DIV);
		nsIDOMElement bottomDiv = visualDocument.createElement(HTML.TAG_DIV);
		nsIDOMElement bottomSecondaryDiv = visualDocument.createElement(HTML.TAG_DIV);
		creationData = new VpeCreationData(mainDiv);
		
		bottomSecondaryDiv.setAttribute(HTML.ATTR_STYLE,
				BOTTOM_SECONDARY_DIV_STYLE);
		bottomSecondaryDiv.appendChild(visualDocument
				.createTextNode(BOTTOM_SECONDARY_DIV_TEXT));
		bottomDiv.appendChild(bottomSecondaryDiv);
		
		NodeList children = sourceNode.getChildNodes();
		Map<String, Element> panels = new HashMap<String, Element>();
		/*
		 * Array of columns weights
		 * 0 - for left panel
		 * 1 - for center panel
		 * 2 - for right panel
		 */
		String[] widthStrings = {Constants.EMPTY, Constants.EMPTY, Constants.EMPTY};
		for (int i = 0; i < children.getLength(); i++) {
			Node node = children.item(i);
			if (node instanceof Element && node.getNodeName() != null
					&& node.getNodeName().indexOf(LAYOUT_PANEL_NAME) > 0) {
				Element element = (Element) node;
                String position = element.getAttribute(RichFaces.ATTR_POSITION);
                /*
                 * Adding several panel with the same position is forbidden.
                 * During adding to the map only the last panel  
                 * with repeating position name will be displayed.
                 * Counting columns quantity and reading 'width attribute'
                 * at the same time.
                 */
                if (ComponentUtil.isNotBlank(position)) {
                	if (RichFaces.VALUE_TOP.equalsIgnoreCase(position)) {
                		panels.put(RichFaces.VALUE_TOP, element);
                	} else if (RichFaces.VALUE_LEFT.equalsIgnoreCase(position)) {
                		panels.put(RichFaces.VALUE_LEFT, element);
                		widthStrings[0] = element.getAttribute(HTML.ATTR_WIDTH);
                	} else if (RichFaces.VALUE_CENTER.equalsIgnoreCase(position)) {
						panels.put(RichFaces.VALUE_CENTER, element);
						widthStrings[1] = element.getAttribute(HTML.ATTR_WIDTH);
					} else if (RichFaces.VALUE_RIGHT.equalsIgnoreCase(position)) {
						panels.put(RichFaces.VALUE_RIGHT, element);
						widthStrings[2] = element.getAttribute(HTML.ATTR_WIDTH);
					} else if (RichFaces.VALUE_BOTTOM.equalsIgnoreCase(position)) {
						panels.put(RichFaces.VALUE_BOTTOM, element);
					}
				}
            }
		}
		
		/*
		 * Columns weights processing:
		 * 1) If column has no weight specified 
		 *  a) it should have a percent weight - when other weights are less than 100% summary,
		 *   if there are some columns without weight - they should share 
		 *   total free weight between each other equally.
		 *  b) if other columns has weights specified in percents
		 *   and in summary it's more than 100%
		 *   than no weight should be set.
		 *  2) If there are some columns has weight in '%' and their weights' 
		 *   sum is more or less 100% then new weight should be set in range of 100%
		 *   in proportion to specified weights.
		 *  3) If column has weight set in '%' and the value is greater than 100%
		 *   the value is added to style without changes.
		 *  4) Weights in 'px' and 'em' are set in style without changes.
		 */
		
		/*
		 * Array of columns weights
		 * 0 - for left panel
		 * 1 - for center panel
		 * 2 - for right panel
		 */
		double[] widths = {-1, -1, -1};
		for (int i = 0; i < widthStrings.length; i++) {
			widths[i] = parseWidthFromPercents(widthStrings[i]);
		}
		/*
		 * A) Find any >100% weight.
		 *  Leave it as is.
		 */
		boolean widthOverflow = false;
		for (double w : widths) {
			if (w > 100) {
				widthOverflow = true;
			}
		}
		if (!widthOverflow){
			/*
			 * B) When weights are less than 100
			 *  Count total weight in '%' (<100 || >100)
			 */
			double totalWidth = 0;
			for (double w : widths) {
				if (w > 0) {
					totalWidth += w; 
				}
			}
			/* 
			 * Count columns with no width specified.
			 */
			int noWeightColumns = 0;
			for (String ws : widthStrings) {
				if ((null == ws) 
						|| Constants.EMPTY.equalsIgnoreCase(ws)) {
					noWeightColumns++;
				}
			}
			/*
			 * Free width to add to total width to 100.
			 */
			double totalFreeWidth = 100 - totalWidth;
			/*
			 * Total available width should always be less or equal 100. 
			 */
			double availableWidth = 100;
			if ((totalWidth < 100) && (noWeightColumns > 0)){
				/*
				 * Set specified width, free space will be filled
				 * with columns without width attribute.
				 */
				availableWidth = totalWidth;
			}
			/* 
			 * C) Adjust existed weights in '%'  
			 */
			double[] coeffs = {-1, -1, -1};
			for (int i = 0; i < widths.length; i++) {
				if (widths[i] > 0) {
					coeffs[i] = widths[i] / totalWidth;
					BigDecimal b = new BigDecimal(availableWidth*coeffs[i]).setScale(2,
							BigDecimal.ROUND_HALF_UP);
					widthStrings[i] = b.doubleValue() + Constants.PERCENT;
				}
			}
			
			/*
			 * D) Adjust empty weight   
			 * When there is some free space to adjust - 
			 * divide it equally between width free columns.
			 */
			if ((totalFreeWidth > 0) && (noWeightColumns > 0)) {
				BigDecimal b = new BigDecimal(totalFreeWidth/noWeightColumns).setScale(2,
						BigDecimal.ROUND_HALF_UP);
				for (int i = 0; i < widthStrings.length; i++) {
					if ((null == widthStrings[i]) 
							|| Constants.EMPTY.equalsIgnoreCase(widthStrings[i])) {
						widthStrings[i] = b.doubleValue() + Constants.PERCENT;
					}
				}
			}
		}
		/*
		 * E) Leave 'px' and 'em' without changes 
		 */

		/*
		 * Adding panels' divs.
		 * Order is important!
		 */
		addPanelFromMap(RichFaces.VALUE_TOP, panels, mainDiv, topDiv,
				Constants.EMPTY, null, creationData);
		addPanelFromMap(RichFaces.VALUE_LEFT, panels, mainDiv, leftDiv,
				FLOAT_LEFT_STYLE, widthStrings[0], creationData);
		addPanelFromMap(RichFaces.VALUE_CENTER, panels, mainDiv, centerDiv,
				FLOAT_LEFT_STYLE, widthStrings[1], creationData);
		addPanelFromMap(RichFaces.VALUE_RIGHT, panels, mainDiv, rightDiv,
				FLOAT_RIGHT_STYLE, widthStrings[2], creationData);
		addPanelFromMap(RichFaces.VALUE_BOTTOM, panels, mainDiv, bottomDiv,
				Constants.EMPTY, null, creationData);
		
		return creationData;
	}
	
	/**
	 * Parse width string from percents form to a number.
	 * @param widthStr panel's width string
	 * @return panel's width number or -1 when parsing failed
	 */
	private double parseWidthFromPercents(String widthStr) {
		double result = -1;
		if ((null != widthStr) &&  widthStr.endsWith(Constants.PERCENT)) {
			try {
				result = Double.parseDouble(widthStr.substring(0, widthStr.length()-1));
			} catch (NumberFormatException e) {
				/*
				 * Cannot parse - skip.
				 */
			}
		}
		return result;
	}
	
	/**
	 * Adds rich:layoutPanel to current rich:layout.
	 * Styles and width will be set.
	 * Panel will be added to children info.
	 * 
	 * @param panelPositionMapName panel name, also used to get the panel from the map
	 * @param panelsMap map with panels elements from source
	 * @param mainDiv rich:layout's div 
	 * @param panelDiv div to render layoutPanel
	 * @param style panel's css style
	 * @param panelWidth panel's width
	 * @param creationData VpeCreationData
	 */
	private void addPanelFromMap(String panelPositionMapName, Map<String, Element> panelsMap,
			nsIDOMElement mainDiv, nsIDOMElement panelDiv, String style, String panelWidth, VpeCreationData creationData) {
		Element panel = panelsMap.get(panelPositionMapName);
		if (null != panel) {
			String widthStr = panel.getAttribute(HTML.ATTR_WIDTH);
			/*
			 * Apply column width for left, center and right panels only.
			 */
			if (!RichFaces.VALUE_TOP.equalsIgnoreCase(panelPositionMapName)
					&& !RichFaces.VALUE_BOTTOM.equalsIgnoreCase(panelPositionMapName)
					&& (null != panelWidth)) {
				style += "; width: " + panelWidth + ";"; //$NON-NLS-1$ //$NON-NLS-2$
			} else {
				
			}
			/*
			 * Set the original width to 'width' attribute
			 * as richfaces do.
			 */
			
			if (ComponentUtil.isNotBlank(widthStr)) {
				panelDiv.setAttribute(HTML.ATTR_WIDTH, widthStr);
			}
			panelDiv.setAttribute(HTML.ATTR_STYLE, style);
			mainDiv.appendChild(panelDiv);
			VpeChildrenInfo panelInfo = new VpeChildrenInfo(panelDiv);
			panelInfo.addSourceChild(panel);
		    creationData.addChildrenInfo(panelInfo);
		}
	}

}
