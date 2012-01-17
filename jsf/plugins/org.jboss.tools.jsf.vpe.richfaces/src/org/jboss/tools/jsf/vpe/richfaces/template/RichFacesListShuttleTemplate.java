/******************************************************************************* 
 * Copyright (c) 2007 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.jsf.vpe.richfaces.template;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jboss.tools.jsf.vpe.richfaces.ComponentUtil;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeAbstractTemplate;
import org.jboss.tools.vpe.editor.template.VpeChildrenInfo;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.util.HTML;
import org.jboss.tools.vpe.editor.util.VpeStyleUtil;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMText;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Sergey Dzmitrovich
 * 
 */
public class RichFacesListShuttleTemplate extends VpeAbstractTemplate {

	/**
	 * source caption key
	 */
	private static final String SOURCE_CAPTION = "sourceCaption"; //$NON-NLS-1$

	/**
	 * target caption key
	 */
	private static final String TARGET_CAPTION = "targetCaption"; //$NON-NLS-1$

	/**
	 * path to css
	 */
	private static final String STYLE_PATH = "shuttle/shuttle.css"; //$NON-NLS-1$

	/**
	 * path to img
	 */
	private static final String BUTTON_IMG_PATH = "shuttle/button.gif"; //$NON-NLS-1$

	/**
	 * path to img
	 */
	private static final String HEADER_IMG_PATH = "shuttle/header.gif"; //$NON-NLS-1$

	/**
	 * default value of width of box(list)
	 */
	private static final String DEFAULT_LIST_WIDTH = "140px"; //$NON-NLS-1$

	/**
	 * default value of height of box(list)
	 */
	private static final String DEFAULT_LIST_HEIGHT = "140px"; //$NON-NLS-1$

	/**
	 * attribute name of width of source list
	 */
	private static final String ATTR_SOURCE_LIST_WIDTH = "sourceListWidth"; //$NON-NLS-1$

	/**
	 * attribute name of width of target list
	 */
	private static final String ATTR_TARGET_LIST_WIDTH = "targetListWidth"; //$NON-NLS-1$

	/**
	 * attribute name of height of source list
	 */
	private static final String ATTR_LISTS_HEIGHT = "listsHeight"; //$NON-NLS-1$

	/**
	 * If this attribute in source node is "false", 'Copy All' and 'Remove All'
	 * controls aren't displayed
	 */
	private static final String ATTR_FAST_MOVE_CONTROLS_VIZIBLE = "fastMoveControlsVisible"; //$NON-NLS-1$

	/**
	 * If this attribute in source node is "false", 'Top' and 'Bottom' controls
	 * aren't displayed.
	 */
	private static final String ATTR_FAST_ORDER_CONTROLS_VIZIBLE = "fastOrderControlsVisible"; //$NON-NLS-1$

	/**
	 * If this attribute in source node is "false", 'Copy' and 'Remove' controls
	 * aren't displayed
	 */
	private static final String ATTR_MOVE_CONTROLS_VIZIBLE = "moveControlsVisible"; //$NON-NLS-1$

	/**
	 * If this attribute in source node is "false", 'Up' and 'Down' controls
	 * aren't displayed.
	 */
	private static final String ATTR_ORDER_CONTROLS_VIZIBLE = "orderControlsVisible"; //$NON-NLS-1$

	/**
	 * default button align
	 */
	private static final String DEFAULT_BUTTON_ALIGN = HTML.VALUE_ALIGN_MIDDLE;

	/**
	 * Customizes vertically a position of move/copy controls relatively to
	 * lists
	 */
	private static final String ATTR_MOVE_CONTROLS_VERTICAL_ALIGN = "moveControlsVerticalAlign"; //$NON-NLS-1$

	/**
	 * 
	 * Customizes vertically a position of order controls relatively to lists
	 */
	private static final String ATTR_ORDER_CONTROLS_VERTICAL_ALIGN = "orderControlsVerticalAlign"; //$NON-NLS-1$

	/**
	 * button images
	 */
	private static final Map<String, String> buttonImages;

	static {
		buttonImages = new HashMap<String, String>();

		// images of the first set of buttons
		buttonImages.put("copyAllControl", "shuttle/arrow_copy_all.gif"); //$NON-NLS-1$ //$NON-NLS-2$
		buttonImages.put("copyControl", "shuttle/arrow_copy.gif"); //$NON-NLS-1$ //$NON-NLS-2$
		buttonImages.put("removeControl", "shuttle/arrow_remove.gif"); //$NON-NLS-1$ //$NON-NLS-2$
		buttonImages.put("removeAllControl", "shuttle/arrow_remove_all.gif"); //$NON-NLS-1$ //$NON-NLS-2$

		// images of the second set of buttons
		buttonImages.put("topControl", "shuttle/arrow_first.gif"); //$NON-NLS-1$ //$NON-NLS-2$
		buttonImages.put("upControl", "shuttle/arrow_up.gif"); //$NON-NLS-1$ //$NON-NLS-2$
		buttonImages.put("downControl", "shuttle/arrow_down.gif"); //$NON-NLS-1$ //$NON-NLS-2$
		buttonImages.put("bottomControl", "shuttle/arrow_last.gif"); //$NON-NLS-1$ //$NON-NLS-2$

	}

	/**
	 * style classes
	 */
	private static final Map<String, String> defaultStyleClasses;

	static {
		defaultStyleClasses = new HashMap<String, String>();

		// general style
		defaultStyleClasses.put("style", "rich-list-shuttle"); //$NON-NLS-1$ //$NON-NLS-2$

		// styles of the lists
		defaultStyleClasses.put("header", "rich-shuttle-list-header"); //$NON-NLS-1$ //$NON-NLS-2$
		defaultStyleClasses.put("headerCell", "rich-shuttle-header-tab-cell"); //$NON-NLS-1$ //$NON-NLS-2$
		defaultStyleClasses.put("list", "rich-shuttle-list-content"); //$NON-NLS-1$ //$NON-NLS-2$

		// styles of button's block
		defaultStyleClasses.put("controls", "rich-shuttle-controls"); //$NON-NLS-1$ //$NON-NLS-2$

		// styles of the first set of buttons
		defaultStyleClasses.put("copyAllControl", //$NON-NLS-1$
				"rich-shuttle-button rich-shuttle-copyAll"); //$NON-NLS-1$
		defaultStyleClasses.put("copyControl", //$NON-NLS-1$
				"rich-shuttle-button rich-shuttle-copy"); //$NON-NLS-1$
		defaultStyleClasses.put("removeControl", //$NON-NLS-1$
				"rich-shuttle-button rich-shuttle-remove"); //$NON-NLS-1$
		defaultStyleClasses.put("removeAllControl", //$NON-NLS-1$
				"rich-shuttle-button rich-shuttle-removeAll"); //$NON-NLS-1$

		// styles of the second set of buttons
		defaultStyleClasses.put("topControl", //$NON-NLS-1$
				"rich-shuttle-button rich-shuttle-top"); //$NON-NLS-1$
		defaultStyleClasses.put("upControl", //$NON-NLS-1$
				"rich-shuttle-button rich-shuttle-up"); //$NON-NLS-1$
		defaultStyleClasses.put("downControl", //$NON-NLS-1$
				"rich-shuttle-button rich-shuttle-down"); //$NON-NLS-1$
		defaultStyleClasses.put("bottomControl", //$NON-NLS-1$
				"rich-shuttle-button rich-shuttle-bottom"); //$NON-NLS-1$

		// styles of captions
		defaultStyleClasses.put(SOURCE_CAPTION, "rich-shuttle-source-caption"); //$NON-NLS-1$ 
		defaultStyleClasses.put(TARGET_CAPTION, "rich-shuttle-target-caption"); //$NON-NLS-1$

		// styles of rows
		defaultStyleClasses.put("sourceRow", "rich-shuttle-source-row"); //$NON-NLS-1$ //$NON-NLS-2$
		defaultStyleClasses.put("targetRow", "rich-shuttle-target-row"); //$NON-NLS-1$ //$NON-NLS-2$

		// styles of rows
		defaultStyleClasses.put("columns", ""); //$NON-NLS-1$ //$NON-NLS-2$

	}

	/**
	 * default labels
	 */
	private static final Map<String, String> defaultLabels;

	static {
		defaultLabels = new HashMap<String, String>();

		// values of the first set of buttons
		defaultLabels.put("copyAllControl", "Copy all"); //$NON-NLS-1$ //$NON-NLS-2$
		defaultLabels.put("copyControl", "Copy"); //$NON-NLS-1$ //$NON-NLS-2$
		defaultLabels.put("removeControl", "Remove"); //$NON-NLS-1$ //$NON-NLS-2$
		defaultLabels.put("removeAllControl", "Remove All"); //$NON-NLS-1$ //$NON-NLS-2$

		// images of the second set of buttons
		defaultLabels.put("topControl", "First"); //$NON-NLS-1$ //$NON-NLS-2$
		defaultLabels.put("upControl", "Up"); //$NON-NLS-1$ //$NON-NLS-2$
		defaultLabels.put("downControl", "Down"); //$NON-NLS-1$ //$NON-NLS-2$
		defaultLabels.put("bottomControl", "Last"); //$NON-NLS-1$ //$NON-NLS-2$

		// caption labels
		defaultLabels.put(SOURCE_CAPTION, ""); //$NON-NLS-1$
		defaultLabels.put(TARGET_CAPTION, ""); //$NON-NLS-1$
	}

	/**
	 * "fast move" buttons block
	 */
	private static final List<String> fastMoveButtons = Arrays.asList(
				"copyAllControl", //$NON-NLS-1$
				"removeAllControl" //$NON-NLS-1$
		);

	/**
	 * "move" buttons block
	 */
	private static final List<String> moveButtons = Arrays.asList(
				"copyControl", //$NON-NLS-1$
				"removeControl" //$NON-NLS-1$
		);

	/**
	 * "fast order" buttons block
	 */
	private static final List<String> fastOrderButtons = Arrays.asList(
			"topControl", //$NON-NLS-1$
			"bottomControl" //$NON-NLS-1$
		);

	/**
	 * "order" buttons block
	 */
	private static final List<String> orderButtons = Arrays.asList(
				"upControl", //$NON-NLS-1$
				"downControl" //$NON-NLS-1$
		);

	/**
	 * labels for controls
	 */
	private final Map<String, String> labels = new HashMap<String, String>();

	/**
	 * source buttons
	 */
	private final List<String> sourceButtons = new ArrayList<String>();

	/**
	 * target buttons
	 */
	private final List<String> targetButtons = new ArrayList<String>();

	/**
	 * 
	 */
	private static final Map<String, String> styleClasses = new HashMap<String, String>();

	/**
	 * value of vertical-align attribute for source (copy/remove) buttons
	 */
	private String sourceButtonsAlign;

	/**
	 * value of vertical-align attribute for source (up/down/top/bottom) buttons
	 */
	private String targetButtonsAlign;

	/**
	 * value of height attribute of lists (source/target)
	 */
	private String listsHeight;

	/**
	 * value of width attribute of source list
	 */
	private String sourceListsWidth;

	/**
	 * value of width attribute of target list
	 */
	private String targetListsWidth;

	/**
	 * row style class
	 */
	private String rowClass;

	/**
	 * column style class
	 */
	private List<String> columnClasses;

	/**
	 * facetLabels
	 */
	private final Map<String, Node> facetLabels = new HashMap<String, Node>();

	/**
	 * 
	 */
	private boolean isShowButtonLabels;

	/**
	 * 
	 */
	public RichFacesListShuttleTemplate() {

	}

	@Override
	public boolean recreateAtAttrChange(VpePageContext pageContext,
			Element sourceElement, nsIDOMDocument visualDocument,
			nsIDOMElement visualNode, Object data, String name, String value) {
		// TODO Auto-generated method stub
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jboss.tools.vpe.editor.template.VpeTemplate#create(org.jboss.tools.vpe.editor.context.VpePageContext,
	 *      org.w3c.dom.Node, org.mozilla.interfaces.nsIDOMDocument)
	 */
	public VpeCreationData create(VpePageContext pageContext, Node sourceNode,
			nsIDOMDocument visualDocument) {

		// cast to Element
		Element sourceElement = (Element) sourceNode;

		// prepare data
		prepareData(sourceElement);

		ComponentUtil.setCSSLink(pageContext, STYLE_PATH, "shuttle"); //$NON-NLS-1$
		// create table element
		nsIDOMElement basicTable = visualDocument.createElement(HTML.TAG_TABLE);
		// ComponentUtil.copyAttributes(sourceNode, basicTable);

		basicTable.setAttribute(HTML.ATTR_CLASS, styleClasses.get("style")); //$NON-NLS-1$
		String styleAttr = sourceElement.hasAttribute(RichFaces.ATTR_STYLE) ? sourceElement.getAttribute(RichFaces.ATTR_STYLE) : null;
		basicTable.setAttribute(HTML.ATTR_STYLE, styleAttr);

		VpeCreationData creationData = new VpeCreationData(basicTable);
		creationData.addChildrenInfo(new VpeChildrenInfo(null));

		// create caption
		nsIDOMElement caption = createCaption(visualDocument, creationData);
		if (caption != null)
			basicTable.appendChild(caption);

		// create "tr" tag
		nsIDOMElement basicTr = visualDocument.createElement(HTML.TAG_TR);

		// create source box
		nsIDOMElement sourceBoxTd = visualDocument.createElement(HTML.TAG_TD);
		nsIDOMElement sourceBox = createBox(visualDocument, creationData,
				getChildren(sourceNode), "source"); //$NON-NLS-1$
		sourceBox.setAttribute(HTML.ATTR_STYLE, VpeStyleUtil.PARAMETER_WIDTH
				+ VpeStyleUtil.COLON_STRING + sourceListsWidth
				+ VpeStyleUtil.SEMICOLON_STRING + VpeStyleUtil.PARAMETER_HEIGHT
				+ VpeStyleUtil.COLON_STRING + listsHeight
				+ VpeStyleUtil.SEMICOLON_STRING);
		sourceBoxTd.appendChild(sourceBox);

		// create source buttons
		nsIDOMElement sourceButtonsTd = visualDocument
				.createElement(HTML.TAG_TD);
		nsIDOMElement sourceButtonsBlock = createButtonsBlock(visualDocument,
				creationData, sourceButtons);
		sourceButtonsTd.appendChild(sourceButtonsBlock);

		// set vertical-align attribute for source buttons
		sourceButtonsTd.setAttribute(HTML.ATTR_STYLE,
				VpeStyleUtil.PARAMETR_VERTICAL_ALIGN
						+ VpeStyleUtil.COLON_STRING + sourceButtonsAlign);

		// create target box
		nsIDOMElement targetBoxTd = visualDocument.createElement(HTML.TAG_TD);
		nsIDOMElement targetBox = createBox(visualDocument, creationData,
				getChildren(sourceNode), "target"); //$NON-NLS-1$
		targetBox.setAttribute(HTML.ATTR_STYLE, VpeStyleUtil.PARAMETER_WIDTH
				+ VpeStyleUtil.COLON_STRING + targetListsWidth
				+ VpeStyleUtil.SEMICOLON_STRING + VpeStyleUtil.PARAMETER_HEIGHT
				+ VpeStyleUtil.COLON_STRING + listsHeight
				+ VpeStyleUtil.SEMICOLON_STRING);
		targetBoxTd.appendChild(targetBox);

		// create target buttons
		nsIDOMElement targetButtonsTd = visualDocument
				.createElement(HTML.TAG_TD);
		nsIDOMElement targetButtonsBlock = createButtonsBlock(visualDocument,
				creationData, targetButtons);
		targetButtonsTd.appendChild(targetButtonsBlock);

		// set vertical-align attribute for target buttons
		targetButtonsTd.setAttribute(HTML.ATTR_STYLE,
				VpeStyleUtil.PARAMETR_VERTICAL_ALIGN
						+ VpeStyleUtil.COLON_STRING + targetButtonsAlign);

		// add all blocks to "tr"
		basicTr.appendChild(sourceBoxTd);
		basicTr.appendChild(sourceButtonsTd);
		basicTr.appendChild(targetBoxTd);
		basicTr.appendChild(targetButtonsTd);

		// add "tr" to table
		basicTable.appendChild(basicTr);

		clearData();

		return creationData;
	}

	/**
	 * create caption of listShuttle component
	 * 
	 * caption is "tr" tag which contain two "td" tags
	 * 
	 * @param sourceCaptionLabel
	 * @param targetCaptionLabel
	 * @param visualDocument
	 * @param creationData
	 * @param sourceElement
	 * @return
	 */
	private nsIDOMElement createCaption(nsIDOMDocument visualDocument,
			VpeCreationData creationData) {

		// check sourceCaptionLabel
		if ((labels.get(SOURCE_CAPTION).length() == 0)
				&& (!facetLabels.containsKey(SOURCE_CAPTION))
				&& (labels.get(TARGET_CAPTION).length() == 0)
				&& (!facetLabels.containsKey(TARGET_CAPTION)))
			return null;

		// basic element for caption is "tr" tag
		nsIDOMElement caption = visualDocument.createElement(HTML.TAG_TR);

		// create source caption label
		caption.appendChild(createCaptionLabel(visualDocument, creationData,
				SOURCE_CAPTION));
		// create target caption label
		caption.appendChild(createCaptionLabel(visualDocument, creationData,
				TARGET_CAPTION));

		return caption;

	}

	/**
	 * create caption label
	 * 
	 * @param visualDocument
	 * @param creationData
	 * @param sourceElement
	 * @param label
	 * @return
	 */
	private nsIDOMElement createCaptionLabel(nsIDOMDocument visualDocument,
			VpeCreationData creationData, String labelId) {

		// create "td" for target caption label
		nsIDOMElement captionLabelTd = visualDocument
				.createElement(HTML.TAG_TD);

		// set attributes
		captionLabelTd.setAttribute(HTML.ATTR_COLSPAN, "2"); //$NON-NLS-1$
		captionLabelTd.setAttribute(HTML.ATTR_CLASS, styleClasses.get(labelId));

		// if facet is defined for this label add facet to "td"
		if (facetLabels.containsKey(labelId)) {

			VpeChildrenInfo captionLabelTdInfo = new VpeChildrenInfo(
					captionLabelTd);
			creationData.addChildrenInfo(captionLabelTdInfo);

			captionLabelTdInfo.addSourceChild(facetLabels.get(labelId));
		}
		// add to "td" value of captionLabel
		else {
			nsIDOMText captionLabelText = visualDocument.createTextNode(labels
					.get(labelId));

			captionLabelTd.appendChild(captionLabelText);

		}
		return captionLabelTd;

	}

	/**
	 * create box (list)
	 * 
	 * @param visualDocument
	 * @param creationData
	 * @return
	 */
	private nsIDOMElement createBox(nsIDOMDocument visualDocument,
			VpeCreationData creationData, List<Node> children, String boxId) {

		nsIDOMElement div = visualDocument.createElement(HTML.TAG_DIV);
		div.setAttribute(HTML.ATTR_CLASS, styleClasses.get("list")); //$NON-NLS-1$
		// create table element
		nsIDOMElement box = visualDocument.createElement(HTML.TAG_TABLE);
		box.setAttribute(HTML.ATTR_CELLSPACING, "0"); //$NON-NLS-1$ 
		box.setAttribute(HTML.ATTR_CELLPADDING, "0"); //$NON-NLS-1$ 
		box.setAttribute(HTML.ATTR_WIDTH, "100%"); //$NON-NLS-1$ 
		// box.setAttribute("height", listsHeight);

		nsIDOMElement header = createHeader(visualDocument, creationData,
				children);
		if (header != null)
			box.appendChild(header);

		// create body for box
		nsIDOMElement tr = visualDocument.createElement(HTML.TAG_TR);
		tr.setAttribute(HTML.ATTR_STYLE, VpeStyleUtil.PARAMETR_VERTICAL_ALIGN
				+ VpeStyleUtil.COLON_STRING + HTML.VALUE_ALIGN_TOP);
		tr.setAttribute(HTML.ATTR_CLASS, styleClasses.get(boxId + "Row") + " " //$NON-NLS-1$ //$NON-NLS-2$
				+ rowClass);

		// VpeChildrenInfo trInfo = new VpeChildrenInfo(tr);
		// creationData.addChildrenInfo(trInfo);

		// add children to "tr" element
		int columnCount = 0;
		for (Node child : children) {
			if (child.getNodeType() == Node.ELEMENT_NODE) {
				String localName = child.getLocalName();
				if (RichFaces.TAG_COLUMN.equals(localName) || 
						RichFaces.TAG_COLUMNS.equals(localName)) {
	
					nsIDOMElement column = visualDocument
							.createElement(HTML.TAG_TD);
	
					tr.appendChild(column);
	
					if (columnClasses.size() > 0) {
	
						String columnClass = columnClasses.get(columnCount
								% columnClasses.size());
						column.setAttribute(HTML.ATTR_CLASS, columnClass);
	
					}
					nsIDOMElement columnTable = visualDocument
							.createElement(HTML.TAG_TABLE);
					column.appendChild(columnTable);
	
					nsIDOMElement columnTableTr = visualDocument
							.createElement(HTML.TAG_TR);
					columnTable.appendChild(columnTableTr);
	
					VpeChildrenInfo columnTableTrInfo = new VpeChildrenInfo(
							columnTableTr);
					creationData.addChildrenInfo(columnTableTrInfo);
					columnTableTrInfo.addSourceChild(child);
	
					columnCount++;
	
					// trInfo.addSourceChild(child);
	
				}
			}
		}

		// add "tr" to table
		box.appendChild(tr);
		div.appendChild(box);
		return div;
	}

	/**
	 * create buttons block
	 * 
	 * @param visualDocument
	 * @param creationData
	 * @param buttonNames
	 * @return
	 */
	private nsIDOMElement createButtonsBlock(nsIDOMDocument visualDocument,
			VpeCreationData creationData, List<String> buttonNames) {

		// create "div"
		nsIDOMElement buttonsBlock = visualDocument.createElement(HTML.TAG_DIV);
		buttonsBlock
				.setAttribute(HTML.ATTR_CLASS, styleClasses.get("controls")); //$NON-NLS-1$

		for (String buttonId : buttonNames) {

			buttonsBlock.appendChild(createButton(visualDocument, creationData,
					buttonId));

		}

		return buttonsBlock;

	}

	/**
	 * create button
	 * 
	 * @param visualDocument
	 * @param creationData
	 * @param buttonValue
	 * @param buttonImage
	 * @return
	 */
	private nsIDOMElement createButton(nsIDOMDocument visualDocument,
			VpeCreationData creationData, String buttonId) {

		nsIDOMElement buttonSpace = visualDocument.createElement(HTML.TAG_DIV);
		buttonSpace.setAttribute(HTML.ATTR_CLASS, "rich-shuttle-control"); //$NON-NLS-1$

		if (facetLabels.containsKey(buttonId)) {

			VpeChildrenInfo buttonInfo = new VpeChildrenInfo(buttonSpace);
			creationData.addChildrenInfo(buttonInfo);

			buttonInfo.addSourceChild(facetLabels.get(buttonId));

		} else {
			// button represent "div" element
			nsIDOMElement metaButton = visualDocument
					.createElement(HTML.TAG_DIV);

			metaButton.setAttribute(HTML.ATTR_STYLE, ComponentUtil
					.getBackgoundImgStyle(BUTTON_IMG_PATH));
			metaButton
					.setAttribute(HTML.ATTR_CLASS, "rich-shuttle-meta-button"); //$NON-NLS-1$

			// button represent "div" element

			nsIDOMElement button = visualDocument.createElement(HTML.TAG_DIV);
			button.setAttribute(HTML.ATTR_CLASS, styleClasses.get(buttonId));

			nsIDOMElement buttonContent = visualDocument
					.createElement(HTML.TAG_DIV);
			buttonContent.setAttribute(HTML.ATTR_CLASS,
					"rich-shuttle-button-content"); //$NON-NLS-1$

			nsIDOMElement buttonImage = visualDocument
					.createElement(HTML.TAG_IMG);

			buttonImage.setAttribute(HTML.ATTR_WIDTH, "15"); //$NON-NLS-1$
			buttonImage.setAttribute(HTML.ATTR_HEIGHT, "15"); //$NON-NLS-1$
			ComponentUtil.setImg(buttonImage, buttonImages.get(buttonId));
			buttonContent.appendChild(buttonImage);

			if (isShowButtonLabels) {
				nsIDOMText buttonText = visualDocument.createTextNode(labels
						.get(buttonId));

				buttonContent.appendChild(buttonText);
			}

			button.appendChild(buttonContent);
			metaButton.appendChild(button);
			buttonSpace.appendChild(metaButton);
		}
		return buttonSpace;

	}

	/**
	 * prepare data
	 * 
	 * @param sourceElement
	 */
	private void prepareData(Element sourceElement) {

		// prepare labels
		Set<String> labelsKeys = defaultLabels.keySet();

		isShowButtonLabels = !"false".equalsIgnoreCase(sourceElement //$NON-NLS-1$
				.getAttribute(RichFaces.ATTR_SHOW_BUTTON_LABELS));

		for (String key : labelsKeys) {
			if (sourceElement.hasAttribute(key + "Label")) { //$NON-NLS-1$
				String label = sourceElement.getAttribute(key + "Label"); //$NON-NLS-1$
				labels.put(key, label);
			}
			else
				labels.put(key, defaultLabels.get(key));
		}

		// prepare style classes
		Set<String> styleClassesKeys = defaultStyleClasses.keySet();
		for (String key : styleClassesKeys) {
			if (sourceElement.hasAttribute(key + "Class")) { //$NON-NLS-1$
				String styleClass = sourceElement.getAttribute(key + "Class"); //$NON-NLS-1$
				styleClasses.put(key, defaultStyleClasses.get(key) + " " //$NON-NLS-1$
						+ styleClass);
			}
			else
				styleClasses.put(key, defaultStyleClasses.get(key));
		}

		// prepare facets for caption and buttons
		NodeList children = sourceElement.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {

			Node child = children.item(i);

			if ((child instanceof Element)
					&& (RichFaces.TAG_FACET.equals(child.getLocalName()))
					&& (defaultLabels.containsKey(((Element) child)
							.getAttribute(RichFaces.ATTR_NAME)))) {

				facetLabels.put(((Element) child)
						.getAttribute(RichFaces.ATTR_NAME), child);

			}

		}
		
		// if attribue exist then
		if (sourceElement.hasAttribute(RichFaces.ATTR_ROW_CLASSES)) {
			// get rowClass
			String rowClasses = sourceElement.getAttribute(RichFaces.ATTR_ROW_CLASSES);
			rowClass = rowClasses.split("[,;]")[0]; //$NON-NLS-1$
		}
		
		if (sourceElement.hasAttribute(RichFaces.ATTR_COLUMN_CLASSES)) {
			String columnClassesAtribute = sourceElement.getAttribute(RichFaces.ATTR_COLUMN_CLASSES);
			columnClasses = Arrays.asList(columnClassesAtribute.split("[,;]")); //$NON-NLS-1$
		}
		else
			columnClasses = new ArrayList<String>();

		// if "controlsType" attribute is not "none" (if buttons are visible)
		if (!"none".equalsIgnoreCase(sourceElement //$NON-NLS-1$
				.getAttribute(RichFaces.ATTR_CONTROLS_TYPE))) {

			// prepare source buttons
			if (!"false".equalsIgnoreCase(sourceElement //$NON-NLS-1$
					.getAttribute(ATTR_FAST_MOVE_CONTROLS_VIZIBLE)))
				sourceButtons.addAll(fastMoveButtons);
			if (!"false".equalsIgnoreCase(sourceElement //$NON-NLS-1$
					.getAttribute(ATTR_MOVE_CONTROLS_VIZIBLE)))
				sourceButtons.addAll(sourceButtons.size() == 0 ? 0 : 1,
						moveButtons);

			// prepare target buttons
			if (!"false".equalsIgnoreCase(sourceElement //$NON-NLS-1$
					.getAttribute(ATTR_FAST_ORDER_CONTROLS_VIZIBLE)))
				targetButtons.addAll(fastOrderButtons);
			if (!"false".equalsIgnoreCase(sourceElement //$NON-NLS-1$
					.getAttribute(ATTR_ORDER_CONTROLS_VIZIBLE)))
				targetButtons.addAll(targetButtons.size() == 0 ? 0 : 1,
						orderButtons);

		}

		// prepare buttons attributes
		sourceButtonsAlign = sourceElement
				.hasAttribute(ATTR_MOVE_CONTROLS_VERTICAL_ALIGN) ? sourceElement
				.getAttribute(ATTR_MOVE_CONTROLS_VERTICAL_ALIGN)
				: DEFAULT_BUTTON_ALIGN;

		targetButtonsAlign = sourceElement
				.hasAttribute(ATTR_ORDER_CONTROLS_VERTICAL_ALIGN) ? sourceElement
				.getAttribute(ATTR_ORDER_CONTROLS_VERTICAL_ALIGN)
				: DEFAULT_BUTTON_ALIGN;

		// prepare lists attributes

		if (sourceElement.hasAttribute(ATTR_LISTS_HEIGHT)) {
			String listsHeightVal = sourceElement.getAttribute(ATTR_LISTS_HEIGHT);
			listsHeight = VpeStyleUtil.addPxIfNecessary(listsHeightVal);			
		} else {
			listsHeight = DEFAULT_LIST_HEIGHT;
		}

		if (sourceElement.hasAttribute(ATTR_SOURCE_LIST_WIDTH)) {
			String listWidthVal = sourceElement.getAttribute(ATTR_SOURCE_LIST_WIDTH);
			sourceListsWidth = VpeStyleUtil.addPxIfNecessary(listWidthVal);			
		} else {
			sourceListsWidth = DEFAULT_LIST_WIDTH;			
		}
						
		if (sourceElement.hasAttribute(ATTR_TARGET_LIST_WIDTH)) {
			String listWidthVal = sourceElement.getAttribute(ATTR_TARGET_LIST_WIDTH);
			targetListsWidth = VpeStyleUtil.addPxIfNecessary(listWidthVal);
		} else {
			targetListsWidth = DEFAULT_LIST_WIDTH;			
		}
	}

	private void clearData() {

		labels.clear();
		styleClasses.clear();
		sourceButtons.clear();
		targetButtons.clear();
		facetLabels.clear();

	}

	/**
	 * 
	 * @param node
	 * @param name
	 * @return
	 */
	private Element getNodeFacet(Node node, String name) {

		NodeList children = node.getChildNodes();

		Element facet = null;

		for (int i = 0; i < children.getLength(); i++) {

			Node child = children.item(i);

			if ((child instanceof Element)
					&& (RichFaces.TAG_FACET.equals(child.getLocalName()))
					&& (name.equals(((Element) child)
							.getAttribute(RichFaces.ATTR_NAME)))) {

				facet = (Element) child;

			}

		}
		return facet;
	}

	/**
	 * 
	 * @param children
	 * @return
	 */
	private boolean haveFacet(List<Node> children, String name) {

		for (Node node : children) {

			if (getNodeFacet(node, name) != null)
				return true;
		}

		return false;

	}

	/**
	 * 
	 * @param visualDocument
	 * @param creationData
	 * @param children
	 * @param id -
	 *            "header" or "footer"
	 * @return
	 */
	private nsIDOMElement createHeader(nsIDOMDocument visualDocument,
			VpeCreationData creationData, List<Node> children) {

		if (!haveFacet(children, "header")) //$NON-NLS-1$
			return null;

		nsIDOMElement header = visualDocument.createElement(HTML.TAG_TR);

		header.setAttribute(HTML.ATTR_CLASS, styleClasses.get(header));

		for (Node child : children) {
			String localName = child.getLocalName();
			if (RichFaces.TAG_COLUMN.equals(localName) ||
					RichFaces.TAG_COLUMNS.equals(localName)) {

				nsIDOMElement headerCell = visualDocument
						.createElement(HTML.TAG_TH);

				headerCell.setAttribute("background", "file:///" //$NON-NLS-1$ //$NON-NLS-2$
						+ ComponentUtil
								.getAbsoluteResourcePath(HEADER_IMG_PATH).replace('\\', '/'));

				// get header classes
				String headerClass = styleClasses.get("headerCell"); //$NON-NLS-1$

				if ((child instanceof Element)
						&& ((Element) child).hasAttribute("headerClass")) { //$NON-NLS-1$
					headerClass += " " //$NON-NLS-1$
							+ ((Element) child).getAttribute("headerClass"); //$NON-NLS-1$
				}
				headerCell.setAttribute(HTML.ATTR_CLASS, headerClass);

				Element facet = getNodeFacet(child, "header"); //$NON-NLS-1$
				if (facet != null) {
					VpeChildrenInfo headerCellInfo = new VpeChildrenInfo(
							headerCell);
					creationData.addChildrenInfo(headerCellInfo);

					headerCellInfo.addSourceChild(facet);

				} else {

					nsIDOMElement pre = visualDocument
							.createElement(HTML.TAG_PRE);

					pre.appendChild(visualDocument.createTextNode("")); //$NON-NLS-1$
					headerCell.appendChild(pre);

				}
				header.appendChild(headerCell);

			}
		}

		return header;

	}

	/**
	 * 
	 * @param sourceNode
	 * @return
	 */
	public static List<Node> getChildren(Node sourceNode) {
		ArrayList<Node> children = new ArrayList<Node>();
		NodeList nodeList = sourceNode.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node child = nodeList.item(i);
			children.add(child);
		}
		return children;
	}
}
