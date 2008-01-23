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
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMNode;
import org.mozilla.interfaces.nsIDOMText;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Sergey Dzmitrovich
 * 
 */
public class RichFacesListShuttleTemplate extends VpeAbstractTemplate {

	private static final String ATTR_CONTROLS_TYPE = "controlsType";
	/**
	 * 
	 */
	private static final String ATTR_SHOW_BUTTON_LABELS = "showButtonLabels";

	/**
	 * source caption key
	 */
	private static final String SOURCE_CAPTION = "sourceCaption";

	/**
	 * target caption key
	 */
	private static final String TARGET_CAPTION = "targetCaption";

	/**
	 * path to css
	 */
	private static final String STYLE_PATH = "shuttle/shuttle.css";

	/**
	 * path to img
	 */
	private static final String BUTTON_IMG_PATH = "shuttle/button.gif";

	/**
	 * path to img
	 */
	private static final String HEADER_IMG_PATH = "shuttle/button.gif";

	/**
	 * default value of width of box(list)
	 */
	private static final String DEFAULT_LIST_WIDTH = "140px";

	/**
	 * default value of height of box(list)
	 */
	private static final String DEFAULT_LIST_HEIGHT = "140px";

	/**
	 * rowClasses attribute name
	 */
	private static final String ATTR_ROW_CLASSES = "rowClasses";

	/**
	 * columnClasses attribute name
	 */
	private static final String ATTR_COLUMN_CLASSES = "columnClasses";

	/**
	 * attribute name of width of source list
	 */
	private static final String ATTR_SOURCE_LIST_WIDTH = "sourceListWidth";

	/**
	 * attribute name of width of target list
	 */
	private static final String ATTR_TARGET_LIST_WIDTH = "targetListWidth";

	/**
	 * attribute name of height of source list
	 */
	private static final String ATTR_LISTS_HEIGHT = "listsHeight";

	/**
	 * If this attribute in source node is "false", 'Copy All' and 'Remove All'
	 * controls aren't displayed
	 */
	private static final String ATTR_FAST_MOVE_CONTROLS_VIZIBLE = "fastMoveControlsVisible";

	/**
	 * If this attribute in source node is "false", 'Top' and 'Bottom' controls
	 * aren't displayed.
	 */
	private static final String ATTR_FAST_ORDER_CONTROLS_VIZIBLE = "fastOrderControlsVisible";

	/**
	 * If this attribute in source node is "false", 'Copy' and 'Remove' controls
	 * aren't displayed
	 */
	private static final String ATTR_MOVE_CONTROLS_VIZIBLE = "moveControlsVisible";

	/**
	 * If this attribute in source node is "false", 'Up' and 'Down' controls
	 * aren't displayed.
	 */
	private static final String ATTR_ORDER_CONTROLS_VIZIBLE = "orderControlsVisible";

	/**
	 * default button align
	 */
	private static final String DEFAULT_BUTTON_ALIGN = "middle";

	/**
	 * Customizes vertically a position of move/copy controls relatively to
	 * lists
	 */
	private static final String ATTR_MOVE_CONTROLS_VERTICAL_ALIGN = "moveControlsVerticalAlign";

	/**
	 * 
	 * Customizes vertically a position of order controls relatively to lists
	 */
	private static final String ATTR_ORDER_CONTROLS_VERTICAL_ALIGN = "orderControlsVerticalAlign";

	/**
	 * button images
	 */
	private static final Map<String, String> buttonImages;

	static {
		buttonImages = new HashMap<String, String>();

		// images of the first set of buttons
		buttonImages.put("copyAllControl", "shuttle/arrow_copy_all.gif");
		buttonImages.put("copyControl", "shuttle/arrow_copy.gif");
		buttonImages.put("removeControl", "shuttle/arrow_remove.gif");
		buttonImages.put("removeAllControl", "shuttle/arrow_remove_all.gif");

		// images of the second set of buttons
		buttonImages.put("topControl", "shuttle/arrow_first.gif");
		buttonImages.put("upControl", "shuttle/arrow_up.gif");
		buttonImages.put("downControl", "shuttle/arrow_down.gif");
		buttonImages.put("bottomControl", "shuttle/arrow_last.gif");

	}

	/**
	 * style classes
	 */
	private static final Map<String, String> defaultStyleClasses;

	static {
		defaultStyleClasses = new HashMap<String, String>();

		// general style
		defaultStyleClasses.put("style", "rich-list-shuttle");

		// styles of the lists
		defaultStyleClasses.put("list-header", "rich-shuttle-list-header");
		defaultStyleClasses.put("list", "rich-shuttle-list-content");

		// styles of button's block
		defaultStyleClasses.put("controls", "rich-shuttle-controls");

		// styles of the first set of buttons
		defaultStyleClasses.put("copyAllControl",
				"rich-shuttle-button rich-shuttle-copyAll");
		defaultStyleClasses.put("copyControl",
				"rich-shuttle-button rich-shuttle-copy");
		defaultStyleClasses.put("removeControl",
				"rich-shuttle-button rich-shuttle-remove");
		defaultStyleClasses.put("removeAllControl",
				"rich-shuttle-button rich-shuttle-removeAll");

		// styles of the second set of buttons
		defaultStyleClasses.put("topControl",
				"rich-shuttle-button rich-shuttle-top");
		defaultStyleClasses.put("upControl",
				"rich-shuttle-button rich-shuttle-up");
		defaultStyleClasses.put("downControl",
				"rich-shuttle-button rich-shuttle-down");
		defaultStyleClasses.put("bottomControl",
				"rich-shuttle-button rich-shuttle-bottom");

		// styles of captions
		defaultStyleClasses.put("sourceCaption", "rich-shuttle-source-caption");
		defaultStyleClasses.put("targetCaption", "rich-shuttle-target-caption");

		// styles of rows
		defaultStyleClasses.put("sourceRow", "rich-shuttle-source-row");
		defaultStyleClasses.put("targetRow", "rich-shuttle-target-row");

		// styles of rows
		defaultStyleClasses.put("columns", "");

	}

	/**
	 * default labels
	 */
	private static final Map<String, String> defaultLabels;

	static {
		defaultLabels = new HashMap<String, String>();

		// values of the first set of buttons
		defaultLabels.put("copyAllControl", "Copy all");
		defaultLabels.put("copyControl", "Copy");
		defaultLabels.put("removeControl", "Remove");
		defaultLabels.put("removeAllControl", "Remove All");

		// images of the second set of buttons
		defaultLabels.put("topControl", "First");
		defaultLabels.put("upControl", "Up");
		defaultLabels.put("downControl", "Down");
		defaultLabels.put("bottomControl", "Last");

		// caption labels
		defaultLabels.put("sourceCaption", "");
		defaultLabels.put("targetCaption", "");

	}

	/**
	 * "fast move" buttons block
	 */
	private static final List<String> fastMoveButtons;

	static {
		fastMoveButtons = new ArrayList<String>();
		fastMoveButtons.add("copyAllControl");
		fastMoveButtons.add("removeAllControl");
	}

	/**
	 * "move" buttons block
	 */
	private static final List<String> moveButtons;

	static {
		moveButtons = new ArrayList<String>();
		moveButtons.add("copyControl");
		moveButtons.add("removeControl");
	}

	/**
	 * "fast order" buttons block
	 */
	private static final List<String> fastOrderButtons;

	static {
		fastOrderButtons = new ArrayList<String>();
		fastOrderButtons.add("topControl");
		fastOrderButtons.add("bottomControl");

	}

	/**
	 * "order" buttons block
	 */
	private static final List<String> orderButtons;

	static {
		orderButtons = new ArrayList<String>();
		orderButtons.add("upControl");
		orderButtons.add("downControl");

	}

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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jboss.tools.vpe.editor.template.VpeAbstractTemplate#isRecreateAtAttrChange(org.jboss.tools.vpe.editor.context.VpePageContext,
	 *      org.w3c.dom.Element, org.mozilla.interfaces.nsIDOMDocument,
	 *      org.mozilla.interfaces.nsIDOMElement, java.lang.Object,
	 *      java.lang.String, java.lang.String)
	 */
	public boolean isRecreateAtAttrChange(VpePageContext pageContext,
			Element sourceElement, nsIDOMDocument visualDocument,
			nsIDOMElement visualNode, Object data, String name, String value) {
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

		ComponentUtil.setCSSLink(pageContext, STYLE_PATH, "shuttle");

		// cast to Element
		Element sourceElement = (Element) sourceNode;

		// get children
		List<Node> children = ComponentUtil.getChildren(sourceElement);

		// prepare data
		prepareData(sourceElement);

		// create table element
		nsIDOMElement basicTable = visualDocument.createElement(HTML.TAG_TABLE);
		// ComponentUtil.copyAttributes(sourceNode, basicTable);

		basicTable.setAttribute("class", styleClasses.get("style"));

		VpeCreationData creationData = new VpeCreationData(basicTable);

		// create caption
		nsIDOMElement caption = createCaption(visualDocument, creationData);
		if (caption != null)
			basicTable.appendChild(caption);

		// create "tr" tag
		nsIDOMElement basicTr = visualDocument.createElement(HTML.TAG_TR);

		// create source box
		nsIDOMElement sourceBoxTd = visualDocument.createElement(HTML.TAG_TD);
		nsIDOMElement sourceBox = createBox(visualDocument, creationData,
				children, "source");
		sourceBox.setAttribute(HTML.ATTR_STYLE, "width:" + sourceListsWidth
				+ ";height:" + listsHeight + ";");
		sourceBoxTd.appendChild(sourceBox);

		// create source buttons
		nsIDOMElement sourceButtonsTd = visualDocument
				.createElement(HTML.TAG_TD);
		nsIDOMElement sourceButtonsBlock = createButtonsBlock(visualDocument,
				creationData, sourceButtons);
		sourceButtonsTd.appendChild(sourceButtonsBlock);

		// set vertical-align attribute for source buttons
		sourceButtonsTd.setAttribute(HTML.ATTR_STYLE, "vertical-align: "
				+ sourceButtonsAlign);

		// create target box
		nsIDOMElement targetBoxTd = visualDocument.createElement(HTML.TAG_TD);
		nsIDOMElement targetBox = createBox(visualDocument, creationData,
				children, "target");
		targetBox.setAttribute(HTML.ATTR_STYLE, "width:" + targetListsWidth
				+ ";height:" + listsHeight + ";");
		targetBoxTd.appendChild(targetBox);

		// create target buttons
		nsIDOMElement targetButtonsTd = visualDocument
				.createElement(HTML.TAG_TD);
		nsIDOMElement targetButtonsBlock = createButtonsBlock(visualDocument,
				creationData, targetButtons);
		targetButtonsTd.appendChild(targetButtonsBlock);

		// set vertical-align attribute for target buttons
		targetButtonsTd.setAttribute(HTML.ATTR_STYLE, "vertical-align: "
				+ targetButtonsAlign);

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
		captionLabelTd.setAttribute(HTML.ATTR_COLSPAN, "2");
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
		div.setAttribute(HTML.ATTR_CLASS, styleClasses.get("list"));
		// create table element
		nsIDOMElement box = visualDocument.createElement(HTML.TAG_TABLE);
		box.setAttribute("cellspacing", "0");
		box.setAttribute("cellpadding", "0");
		box.setAttribute("width", "100%");

		// create "tr" for box
		nsIDOMElement tr = visualDocument.createElement(HTML.TAG_TR);
		tr.setAttribute(HTML.ATTR_STYLE, "vertical-align:top");
		tr.setAttribute(HTML.ATTR_CLASS, styleClasses.get(boxId + "Row") + " "
				+ rowClass);

		VpeChildrenInfo trInfo = new VpeChildrenInfo(tr);
		creationData.addChildrenInfo(trInfo);

		// add children to "tr" element
		for (Node child : children) {
			if ("column".equals(child.getLocalName())) {
				trInfo.addSourceChild(child);
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
				.setAttribute(HTML.ATTR_CLASS, styleClasses.get("controls"));

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
		buttonSpace.setAttribute(HTML.ATTR_CLASS, "rich-shuttle-control");

		if (facetLabels.containsKey(buttonId)) {

			VpeChildrenInfo buttonInfo = new VpeChildrenInfo(buttonSpace);
			creationData.addChildrenInfo(buttonInfo);

			buttonInfo.addSourceChild(facetLabels.get(buttonId));

		} else {
			// button represent "div" element
			nsIDOMElement button = visualDocument.createElement(HTML.TAG_DIV);
			button.setAttribute(HTML.ATTR_CLASS, styleClasses.get(buttonId));
			button.setAttribute(HTML.ATTR_STYLE, ComponentUtil
					.getBackgoundImgStyle(BUTTON_IMG_PATH));
			// button represent "div" element
			nsIDOMElement buttonContent = visualDocument
					.createElement(HTML.TAG_DIV);
			buttonContent.setAttribute(HTML.ATTR_CLASS,
					"rich-shuttle-button-content");

			nsIDOMElement buttonImage = visualDocument
					.createElement(HTML.TAG_IMG);

			buttonImage.setAttribute(HTML.ATTR_WIDTH, "15");
			buttonImage.setAttribute(HTML.ATTR_HEIGHT, "15");
			buttonImage.setAttribute(HTML.ATTR_CLASS,
					"rich-shuttle-button-content");
			ComponentUtil.setImg(buttonImage, buttonImages.get(buttonId));
			buttonContent.appendChild(buttonImage);

			if (isShowButtonLabels) {
				nsIDOMText buttonText = visualDocument.createTextNode(labels
						.get(buttonId));

				buttonContent.appendChild(buttonText);
			}

			button.appendChild(buttonContent);

			buttonSpace.appendChild(button);
		}
		return buttonSpace;

	}

	/**
	 * prepare data
	 * 
	 * @param sourceElement
	 */
	void prepareData(Element sourceElement) {

		// prepare labels
		Set<String> labelsKeys = defaultLabels.keySet();

		isShowButtonLabels = !"false".equalsIgnoreCase(sourceElement
				.getAttribute(ATTR_SHOW_BUTTON_LABELS));

		for (String key : labelsKeys) {

			String label = sourceElement.getAttribute(key + "Label");

			if (label != null)
				labels.put(key, label);
			else
				labels.put(key, defaultLabels.get(key));
		}

		// prepare style classes
		Set<String> styleClassesKeys = defaultStyleClasses.keySet();
		for (String key : styleClassesKeys) {

			String styleClass = sourceElement.getAttribute(key + "Class");
			if (styleClass != null)
				styleClasses.put(key, defaultStyleClasses.get(key) + " "
						+ styleClass);
			else
				styleClasses.put(key, defaultStyleClasses.get(key));
		}

		// prepare facets
		NodeList children = sourceElement.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {

			Node child = children.item(i);

			if ((child instanceof Element)
					&& ("facet".equals(child.getLocalName()))
					&& (defaultLabels.containsKey(((Element) child)
							.getAttribute("name")))) {

				facetLabels.put(((Element) child).getAttribute("name"), child);

			}

		}

		// get rowClass
		String rowClasses = sourceElement.getAttribute(ATTR_ROW_CLASSES);

		// if this attribue exist then
		if (rowClasses != null) {
			rowClass = rowClasses.split(",")[0];
		}

		// if "controlsType" attribute is not "none" (if buttons are visible)
		if (!"none".equalsIgnoreCase(sourceElement
				.getAttribute(ATTR_CONTROLS_TYPE))) {

			// prepare source buttons
			if (!"false".equalsIgnoreCase(sourceElement
					.getAttribute(ATTR_FAST_MOVE_CONTROLS_VIZIBLE)))
				sourceButtons.addAll(fastMoveButtons);
			if (!"false".equalsIgnoreCase(sourceElement
					.getAttribute(ATTR_MOVE_CONTROLS_VIZIBLE)))
				sourceButtons.addAll(sourceButtons.size() == 0 ? 0 : 1,
						moveButtons);

			// prepare target buttons
			if (!"false".equalsIgnoreCase(sourceElement
					.getAttribute(ATTR_FAST_ORDER_CONTROLS_VIZIBLE)))
				targetButtons.addAll(fastOrderButtons);
			if (!"false".equalsIgnoreCase(sourceElement
					.getAttribute(ATTR_ORDER_CONTROLS_VIZIBLE)))
				targetButtons.addAll(targetButtons.size() == 0 ? 0 : 1,
						orderButtons);

		}

		// prepare buttons attributes
		sourceButtonsAlign = sourceElement
				.getAttribute(ATTR_MOVE_CONTROLS_VERTICAL_ALIGN) != null ? sourceElement
				.getAttribute(ATTR_MOVE_CONTROLS_VERTICAL_ALIGN)
				: DEFAULT_BUTTON_ALIGN;

		targetButtonsAlign = sourceElement
				.getAttribute(ATTR_ORDER_CONTROLS_VERTICAL_ALIGN) != null ? sourceElement
				.getAttribute(ATTR_ORDER_CONTROLS_VERTICAL_ALIGN)
				: DEFAULT_BUTTON_ALIGN;

		// prepare lists attributes
		listsHeight = sourceElement.getAttribute(ATTR_LISTS_HEIGHT) != null ? sourceElement
				.getAttribute(ATTR_LISTS_HEIGHT)
				: DEFAULT_LIST_HEIGHT;

		sourceListsWidth = sourceElement.getAttribute(ATTR_SOURCE_LIST_WIDTH) != null ? sourceElement
				.getAttribute(ATTR_SOURCE_LIST_WIDTH)
				: DEFAULT_LIST_WIDTH;

		targetListsWidth = sourceElement.getAttribute(ATTR_TARGET_LIST_WIDTH) != null ? sourceElement
				.getAttribute(ATTR_TARGET_LIST_WIDTH)
				: DEFAULT_LIST_WIDTH;
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
	 * @param sourceElement
	 * @param name
	 * @return
	 */

}
