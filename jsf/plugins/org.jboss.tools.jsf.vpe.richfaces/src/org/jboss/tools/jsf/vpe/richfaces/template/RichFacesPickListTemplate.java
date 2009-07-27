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


import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jboss.tools.jsf.vpe.richfaces.ComponentUtil;
import org.jboss.tools.jsf.vpe.richfaces.template.util.RichFaces;
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
 * Template for <rich:pickList/>.
 * 
 * @author Eugene Stherbin
 */
public class RichFacesPickListTemplate extends VpeAbstractTemplate {

    /** attribute name of height of source list. */
    private static final String ATTR_LISTS_HEIGHT = "listsHeight"; //$NON-NLS-1$

    /** Customizes vertically a position of move/copy controls relatively to lists. */
    private static final String ATTR_MOVE_CONTROLS_VERTICAL_ALIGN = "moveControlsVerticalAlign"; //$NON-NLS-1$

    /** The Constant ATTR_SHOW_BUTTON_LABELS. */
    private static final String ATTR_SHOW_BUTTON_LABELS = "showButtonsLabel"; //$NON-NLS-1$

    /** attribute name of width of source list. */
    private static final String ATTR_SOURCE_LIST_WIDTH = "sourceListWidth"; //$NON-NLS-1$

    /** attribute name of width of target list. */
    private static final String ATTR_TARGET_LIST_WIDTH = "targetListWidth"; //$NON-NLS-1$

    /** path to img. */
    private static final String BUTTON_IMG_PATH = "pickList/button.gif"; //$NON-NLS-1$

    /** The Constant CLASS_SUFFIX. */
    private static final String CLASS_SUFFIX = "Class"; //$NON-NLS-1$

    /** The Constant CONTROL_MAP_KEY. */
    private static final String CONTROL_MAP_KEY = "control"; //$NON-NLS-1$

    /** The Constant CSS_EXTENSION. */
    private static final String CSS_EXTENSION = "pickList"; //$NON-NLS-1$

    /** default button align. */
    private static final String DEFAULT_BUTTON_ALIGN = HTML.VALUE_ALIGN_MIDDLE;

    /** default value of height of box(list). */
    private static final String DEFAULT_LIST_HEIGHT = "140px"; //$NON-NLS-1$

    /** default value of width of box(list). */
    private static final String DEFAULT_LIST_WIDTH = "140px"; //$NON-NLS-1$

    /** The Constant LABEL_SUFFIX. */
    private static final String LABEL_SUFFIX = "Label"; //$NON-NLS-1$

    /** The Constant LIST_MAP_KEY. */
    private static final String LIST_MAP_KEY = "list"; //$NON-NLS-1$

    /** The Constant RICH_LIST_PICKLIST_BUTTON_CONTENT_CSS_CLASS. */
    private static final String RICH_LIST_PICKLIST_BUTTON_CONTENT_CSS_CLASS = "rich-list-picklist-button-content"; //$NON-NLS-1$

    /** The Constant RICH_LIST_PICKLIST_BUTTON_CSS_CLASS. */
    private static final String RICH_LIST_PICKLIST_BUTTON_CSS_CLASS = "rich-list-picklist-button"; //$NON-NLS-1$

    /** The Constant RICH_PICKLIST_CONTROL_BUTTON_CSS_CLASS. */
    private static final String RICH_PICKLIST_CONTROL_BUTTON_CSS_CLASS = "rich-picklist-control-button-class"; //$NON-NLS-1$

    /** The Constant RICH_PICKLIST_INTERNAL_TAB_CSS_CLASS. */
    private static final String RICH_PICKLIST_INTERNAL_TAB_CSS_CLASS = "rich-picklist-internal-tab"; //$NON-NLS-1$

    /** The Constant RICH_PICKLIST_LIST_CONTENT_CSS_CLASS. */
    private static final String RICH_PICKLIST_LIST_CONTENT_CSS_CLASS = "rich-picklist-list-content"; //$NON-NLS-1$

    /** The Constant RICH_PICKLIST_LIST_CSS_CLASS. */
    private static final String RICH_PICKLIST_LIST_CSS_CLASS = "rich-picklist-list"; //$NON-NLS-1$

    /** The Constant RICH_PICKLIST_SOURCE_CELL_CSS_CLASS. */
    private static final String RICH_PICKLIST_SOURCE_CELL_CSS_CLASS = "rich-picklist-source-cell"; //$NON-NLS-1$

    /** The Constant SELECT_ITEM. */
    private static final String SELECT_ITEM = "selectItem"; //$NON-NLS-1$

    /** The Constant SOURCE_LIST. */
    private static final String SOURCE_LIST = "source"; //$NON-NLS-1$

    /** The Constant SPACER_GIF. */
    private static final String SPACER_GIF = "/spacer.gif"; //$NON-NLS-1$

    /** path to css. */
    private static final String STYLE_PATH = CSS_EXTENSION + "/pickList.css"; //$NON-NLS-1$

    /** The Constant styleClasses. */
    private static final Map<String, String> styleClasses = new HashMap<String, String>();

    /** The Constant TARGET_LIST. */
    private static final String TARGET_LIST = "target"; //$NON-NLS-1$

    /** The Constant TD_STYLE_1. */
    private static final String TD_STYLE_1 = "border: 0px none ; padding: 0px;"; //$NON-NLS-1$

    /** The Constant WIDTH_15. */
    private static final String WIDTH_15 = "15"; //$NON-NLS-1$

    /**
     * Gets the children.
     * 
     * @param sourceNode the source node
     * 
     * @return the children
     */
    public static List<Node> getChildren(Node sourceNode) {
        final ArrayList<Node> children = new ArrayList<Node>();
        final NodeList nodeList = sourceNode.getChildNodes();

        for (int i = 0; i < nodeList.getLength(); i++) {
            final Node child = nodeList.item(i);

            children.add(child);
        }
        return children;
    }

    /** button images. */
    private final Map<LabelKey, String> buttonImages = new HashMap<LabelKey, String>();

    /** default labels. */
    private final Map<LabelKey, String> defaultLabels = new HashMap<LabelKey, String>();

    /** style classes. */
    private final Map<String, String> defaultStyleClasses = new HashMap<String, String>();

    /** facetLabels. */
    private final Map<String, Node> facetLabels = new HashMap<String, Node>();

    /** The is show button labels. */
    private boolean isShowButtonLabels;

    /** labels for controls. */
    private final Map<String, String> labels = new HashMap<String, String>();

    /** value of height attribute of lists (source/target). */
    private String listsHeight;

    /** value of vertical-align attribute for source (copy/remove) buttons. */
    private String moveControlsAlign;

    /** source buttons. */
    private final List<String> sourceButtons = new ArrayList<String>();

    /** value of width attribute of source list. */
    private String sourceListsWidth;

    /** target buttons. */
    private final List<String> targetButtons = new ArrayList<String>();

    /** value of width attribute of target list. */
    private String targetListsWidth;

    private enum LabelKey {
        COPY_ALL_CONTROL("copyAllControl"), //$NON-NLS-1$
        COPY_CONTROL("copyControl"), //$NON-NLS-1$
        REMOVE_CONTROL("removeControl"), //$NON-NLS-1$
        REMOVE_ALL_CONTROL("removeAllControl"); //$NON-NLS-1$

        private String value;
        LabelKey(String val) {
         this.value = val;
        }
        public String getValue() {
        	return this.value;
        }
    } 
    /**
     * The Constructor.
     */
    public RichFacesPickListTemplate() {
        super();
        init();
    }

    /**
     * Adds the childrens.
     * 
     * @param td the td
     * @param visualDocument the visual document
     * @param creationData the creation data
     * @param children the children
     */
    private void addChildrens(nsIDOMDocument visualDocument, List<Node> children, nsIDOMElement td, VpeCreationData creationData) {
        List<Element> selectItems = new ArrayList<Element>();
        for (Node child : children) {
            if ((child instanceof Element) && (child.getNodeName().indexOf(SELECT_ITEM)) > 1) {
                // createItemDiv(visualDocument,(Element)child,td);
                selectItems.add((Element) child);
            }
        }
        if (selectItems.size() > 0) {
            final nsIDOMElement topItemDiv = visualDocument.createElement(HTML.TAG_DIV);

            topItemDiv.setAttribute(HTML.ATTR_CLASS, RICH_PICKLIST_LIST_CSS_CLASS);

            final nsIDOMElement pickListContentItemDiv = visualDocument.createElement(HTML.TAG_DIV);

            pickListContentItemDiv.setAttribute(HTML.ATTR_CLASS, RICH_PICKLIST_LIST_CONTENT_CSS_CLASS);
            pickListContentItemDiv.setAttribute(HTML.ATTR_STYLE, VpeStyleUtil.PARAMETER_WIDTH + VpeStyleUtil.COLON_STRING
                    + sourceListsWidth + VpeStyleUtil.SEMICOLON_STRING + VpeStyleUtil.PARAMETER_HEIGHT + VpeStyleUtil.COLON_STRING
                    + listsHeight);

            final nsIDOMElement itemsTable = visualDocument.createElement(HTML.TAG_TABLE);
            final nsIDOMElement itemsTableTBody = visualDocument.createElement(HTML.TAG_TBODY);

            itemsTable.setAttribute(HTML.ATTR_CLASS, RICH_PICKLIST_INTERNAL_TAB_CSS_CLASS);
            itemsTable.setAttribute(HTML.ATTR_CELLPADDING, "0"); //$NON-NLS-1$
            itemsTable.setAttribute(HTML.ATTR_CELLPADDING, "0"); //$NON-NLS-1$
            final VpeChildrenInfo childrensInfo = new VpeChildrenInfo(itemsTableTBody);
            creationData.addChildrenInfo(childrensInfo);
            for (Element selectItem : selectItems) {
                // itemsTableTBody.appendChild(createItemTr(visualDocument,
                // selectItem));
                childrensInfo.addSourceChild(selectItem);
            }
            td.appendChild(topItemDiv);

            topItemDiv.appendChild(pickListContentItemDiv);

            pickListContentItemDiv.appendChild(itemsTable);

            itemsTable.appendChild(itemsTableTBody);
        }

    }


    /**
     * Clear data.
     */
    private void clearData() {

        labels.clear();
        styleClasses.clear();
        sourceButtons.clear();
        targetButtons.clear();
        facetLabels.clear();

    }

    /**
     * Create.
     * 
     * @param visualDocument the visual document
     * @param sourceNode the source node
     * @param pageContext the page context
     * 
     * @return the vpe creation data
     */
    @SuppressWarnings("unchecked")
    public VpeCreationData create(VpePageContext pageContext, Node sourceNode, nsIDOMDocument visualDocument) {

        // cast to Element
        Element sourceElement = (Element) sourceNode;

        // prepare data
        prepareData(sourceElement);
        ComponentUtil.setCSSLink(pageContext, STYLE_PATH, CSS_EXTENSION); 
        // create table element
        final nsIDOMElement rootTable = visualDocument.createElement(HTML.TAG_TABLE);
        final nsIDOMElement rootTBody = visualDocument.createElement(HTML.TAG_TBODY);
        final nsIDOMElement rootTr = visualDocument.createElement(HTML.TAG_TR);
        final VpeCreationData creationData = new VpeCreationData(rootTable);
        creationData.addChildrenInfo(new VpeChildrenInfo(null));
        
        rootTable.setAttribute(HTML.ATTR_CLASS, styleClasses.get(RichFaces.ATTR_STYLE));
        rootTable.setAttribute(HTML.ATTR_STYLE, sourceElement.getAttribute(RichFaces.ATTR_STYLE));

        // create source box
        final nsIDOMElement sourceBoxTd = visualDocument.createElement(HTML.TAG_TD);
        final nsIDOMElement sourceBox = createBox(visualDocument, creationData, getChildren(sourceNode), SOURCE_LIST); 
        sourceBox.setAttribute(HTML.ATTR_STYLE, VpeStyleUtil.PARAMETER_WIDTH + VpeStyleUtil.COLON_STRING + sourceListsWidth
                + VpeStyleUtil.SEMICOLON_STRING + VpeStyleUtil.PARAMETER_HEIGHT + VpeStyleUtil.COLON_STRING + listsHeight
                + VpeStyleUtil.SEMICOLON_STRING);

        sourceBoxTd.appendChild(sourceBox);
        // create source buttons
        nsIDOMElement controlsButtonsTd = visualDocument.createElement(HTML.TAG_TD);
        nsIDOMElement sourceButtonsBlock = createButtonsBlock(visualDocument, creationData);
        controlsButtonsTd.appendChild(sourceButtonsBlock);

        // set vertical-align attribute for source buttons
        controlsButtonsTd.setAttribute(HTML.ATTR_STYLE, VpeStyleUtil.PARAMETR_VERTICAL_ALIGN + VpeStyleUtil.COLON_STRING
                + moveControlsAlign);

        // create target box
        final nsIDOMElement targetBoxTd = visualDocument.createElement(HTML.TAG_TD);
        final nsIDOMElement targetBox = createBox(visualDocument, creationData, getChildren(sourceNode), TARGET_LIST);
        targetBox.setAttribute(HTML.ATTR_STYLE, VpeStyleUtil.PARAMETER_WIDTH + VpeStyleUtil.COLON_STRING + targetListsWidth
                + VpeStyleUtil.SEMICOLON_STRING + VpeStyleUtil.PARAMETER_HEIGHT + VpeStyleUtil.COLON_STRING + listsHeight
                + VpeStyleUtil.SEMICOLON_STRING);
        targetBoxTd.appendChild(targetBox);

        // add all blocks to "tr"
        rootTr.appendChild(sourceBoxTd);
        rootTr.appendChild(controlsButtonsTd);
        rootTr.appendChild(targetBoxTd);

        rootTBody.appendChild(rootTr);
        // add "tr" to table
        rootTable.appendChild(rootTBody);
//         DOMTreeDumper dumpber = new DOMTreeDumper();
//         dumpber.dumpToStream(System.err, rootTable);
        clearData();

        return creationData;
    }

    /**
     * create box (list).
     * 
     * @param visualDocument the visual document
     * @param creationData the creation data
     * @param boxId the box id
     * @param children the children
     * 
     * @return the ns IDOM element
     */
    private nsIDOMElement createBox(nsIDOMDocument visualDocument, VpeCreationData creationData, List<Node> children, String boxId) {

        nsIDOMElement div = visualDocument.createElement(HTML.TAG_DIV);
        div.setAttribute(HTML.ATTR_CLASS, MessageFormat.format(styleClasses.get(LIST_MAP_KEY), boxId));
        // create table element
        nsIDOMElement box = visualDocument.createElement(HTML.TAG_TABLE);
        box.setAttribute(HTML.ATTR_CELLSPACING, "0"); //$NON-NLS-1$ 
        box.setAttribute(HTML.ATTR_CELLPADDING, "0"); //$NON-NLS-1$ 
        box.setAttribute(HTML.ATTR_WIDTH, "100%"); //$NON-NLS-1$ 
        box.setAttribute(HTML.ATTR_CLASS, "rich-picklist-body"); //$NON-NLS-1$
        final nsIDOMElement td = visualDocument.createElement(HTML.TAG_TD);
        final nsIDOMElement tr = visualDocument.createElement(HTML.TAG_TR);

        td.setAttribute(HTML.ATTR_STYLE, TD_STYLE_1);
        if (SOURCE_LIST.equalsIgnoreCase(boxId)) {
            addChildrens(visualDocument, children, td, creationData);
        }
        box.appendChild(tr);
        tr.appendChild(td);
        div.appendChild(box);
        return div;
    }

    /**
     * create button.
     * 
     * @param buttonId the button id
     * @param visualDocument the visual document
     * @param creationData the creation data
     * @param buttonImage      *
     * param buttonValue *
     * 
     * @return the ns IDOM element
     */
    private nsIDOMElement createButton(nsIDOMDocument visualDocument, VpeCreationData creationData, LabelKey buttonId) {

        nsIDOMElement buttonSpace = visualDocument.createElement(HTML.TAG_DIV);
        buttonSpace.setAttribute(HTML.ATTR_CLASS, RICH_PICKLIST_CONTROL_BUTTON_CSS_CLASS);

        // button represent "div" element
        nsIDOMElement metaButton = visualDocument.createElement(HTML.TAG_DIV);

        metaButton.setAttribute(HTML.ATTR_STYLE, ComponentUtil.getBackgoundImgStyle(BUTTON_IMG_PATH));
        metaButton.setAttribute(HTML.ATTR_CLASS, RICH_LIST_PICKLIST_BUTTON_CSS_CLASS); 

        nsIDOMElement buttonContent = visualDocument.createElement(HTML.TAG_DIV);
        buttonContent.setAttribute(HTML.ATTR_CLASS, RICH_LIST_PICKLIST_BUTTON_CONTENT_CSS_CLASS); 

        nsIDOMElement buttonImage = visualDocument.createElement(HTML.TAG_IMG);

        buttonImage.setAttribute(HTML.ATTR_WIDTH, WIDTH_15);
        buttonImage.setAttribute(HTML.ATTR_HEIGHT, WIDTH_15); 
        ComponentUtil.setImg(buttonImage, buttonImages.get(buttonId));
        buttonContent.appendChild(buttonImage);

        if (isShowButtonLabels) {
            nsIDOMText buttonText = visualDocument.createTextNode(labels.get(buttonId.getValue()));

            buttonContent.appendChild(buttonText);
        }

        buttonSpace.appendChild(metaButton);
        metaButton.appendChild(buttonContent);
        return buttonSpace;

    }

    /**
     * create buttons block.
     * 
     * @param visualDocument the visual document
     * @param creationData the creation data
     * @param buttonNames the button names
     * 
     * @return the ns IDOM element
     */
    private nsIDOMElement createButtonsBlock(nsIDOMDocument visualDocument, VpeCreationData creationData) {

        // create "div"
        nsIDOMElement buttonsBlock = visualDocument.createElement(HTML.TAG_DIV);
        buttonsBlock.setAttribute(HTML.ATTR_CLASS, styleClasses.get(CONTROL_MAP_KEY));

        for (LabelKey buttonId : LabelKey.values()) {

            buttonsBlock.appendChild(createButton(visualDocument, creationData, buttonId));

        }

        return buttonsBlock;

    }

    /**
     * Creates the item tr.
     * 
     * @param td      *
     * param child the child
     * @param child the child
     * @param visualDocument the visual document
     * 
     * @return the ns IDOM element
     */
    private nsIDOMElement createItemTr(nsIDOMDocument visualDocument, Element child) {
        final nsIDOMElement tr = visualDocument.createElement(HTML.TAG_TR);
        final nsIDOMElement td = visualDocument.createElement(HTML.TAG_TD);
        // tr.setAttribute(HTML.ATTR_CLASS, "rich-picklist-source-row");
        td.setAttribute(HTML.ATTR_CLASS, RICH_PICKLIST_SOURCE_CELL_CSS_CLASS);
        nsIDOMElement itemImage = visualDocument.createElement(HTML.TAG_IMG);

        itemImage.setAttribute(HTML.ATTR_WIDTH, "1px"); //$NON-NLS-1$
        itemImage.setAttribute(HTML.ATTR_HEIGHT, "1px"); //$NON-NLS-1$
        ComponentUtil.setImg(itemImage, getCssExtension() + SPACER_GIF);
        tr.appendChild(td);
        td.appendChild(itemImage);
        td.appendChild(visualDocument.createTextNode(getTextForSelectItem(child)));
        return tr;

    }

    /**
     * Gets the css extension.
     * 
     * @return the css extension
     */
    protected String getCssExtension() {
        return CSS_EXTENSION;
    }

    /**
     * Gets the text for select item.
     * 
     * @param child the child
     * 
     * @return the text for select item
     */
    private String getTextForSelectItem(Element child) {
        String result = ""; //$NON-NLS-1$
        String attrName = RichFaces.ATTR_SELECT_ITEM_LABEL;
        String defaultValue = "<h:selectItem/>"; //$NON-NLS-1$
        if (child.getNodeName().endsWith("selectItems")) { //$NON-NLS-1$
            attrName = RichFaces.ATTR_VALUE;
            defaultValue = "<h:selectItems/>"; //$NON-NLS-1$
        }
        result = ComponentUtil.getAttribute(child, attrName);

        if (result.trim().length() == 0) {
            result = defaultValue;
        }
        return result;
    }

    /**
     * Init.
     */
    private void init() {
        initButtonImagesMap();
        initDefaultLabelsMap();
        initDefaultStyleClasses();
    }

    /**
     * Inits the button images map.
     */
    private void initButtonImagesMap() {
        // images of the first set of buttons
        buttonImages.put(LabelKey.COPY_ALL_CONTROL, getCssExtension() + "/arrow_copy_all.gif"); //$NON-NLS-1$ 
        buttonImages.put(LabelKey.COPY_CONTROL, getCssExtension() + "/arrow_copy.gif"); //$NON-NLS-1$ 
        buttonImages.put(LabelKey.REMOVE_CONTROL, getCssExtension() + "/arrow_remove.gif"); //$NON-NLS-1$ 
        buttonImages.put(LabelKey.REMOVE_ALL_CONTROL, getCssExtension() + "/arrow_remove_all.gif"); //$NON-NLS-1$

    }

    /**
     * Inits the default labels map.
     */
    private void initDefaultLabelsMap() {
        // values of the first set of buttons
        defaultLabels.put(LabelKey.COPY_ALL_CONTROL, "Copy all"); //$NON-NLS-1$ 
        defaultLabels.put(LabelKey.COPY_CONTROL, "Copy"); //$NON-NLS-1$
        defaultLabels.put(LabelKey.REMOVE_CONTROL, "Remove"); //$NON-NLS-1$ 
        defaultLabels.put(LabelKey.REMOVE_ALL_CONTROL, "Remove All"); //$NON-NLS-1$
    }

    /**
     * Inits the default style classes.
     */
    private void initDefaultStyleClasses() {
        // general style
        defaultStyleClasses.put("style", "rich-list-picklist"); //$NON-NLS-1$ //$NON-NLS-2$

        defaultStyleClasses.put(LIST_MAP_KEY, "rich-picklist-{0}-items"); //$NON-NLS-1$ 

        // styles of button's block
        defaultStyleClasses.put(CONTROL_MAP_KEY, "rich-picklist-controls"); //$NON-NLS-1$ 

    }

    /**
     * Checks if is recreate at attr change.
     * 
     * @param sourceElement the source element
     * @param value the value
     * @param visualDocument the visual document
     * @param visualNode the visual node
     * @param data the data
     * @param pageContext the page context
     * @param name the name
     * 
     * @return true, if is recreate at attr change
     */
    @Override
    public boolean recreateAtAttrChange(VpePageContext pageContext, Element sourceElement, nsIDOMDocument visualDocument,
            nsIDOMElement visualNode, Object data, String name, String value) {
        // TODO Auto-generated method stub
        return true;
    }

    /**
     * prepare data.
     * 
     * @param sourceElement the source element
     */
    private void prepareData(Element sourceElement) {
        prepareLabels(sourceElement);
        prepareStyleClasses(sourceElement);
        prepareOtherParameters(sourceElement);
    }

    /**
     * Prepare labels.
     * 
     * @param sourceElement the source element
     */
    private void prepareLabels(Element sourceElement) {
        // prepare labels

        for (LabelKey key : LabelKey.values()) {

            String label = sourceElement.getAttribute(key.getValue() + LABEL_SUFFIX); 

            if (label != null) {
                labels.put(key.getValue(), label);
            } else {
                labels.put(key.getValue(), defaultLabels.get(key));
            }
        }
    }

    /**
     * Prepare other parameters.
     * 
     * @param sourceElement the source element
     */
    private void prepareOtherParameters(Element sourceElement) {

        isShowButtonLabels = !Boolean.FALSE.toString().equalsIgnoreCase(sourceElement 
                .getAttribute(ATTR_SHOW_BUTTON_LABELS));
        // prepare buttons attributes
        moveControlsAlign = sourceElement.getAttribute(ATTR_MOVE_CONTROLS_VERTICAL_ALIGN) != null ? sourceElement
                .getAttribute(ATTR_MOVE_CONTROLS_VERTICAL_ALIGN) : DEFAULT_BUTTON_ALIGN;

        // prepare lists attributes

        String listsHeightString = sourceElement.getAttribute(ATTR_LISTS_HEIGHT);
        try {
            listsHeight = String.valueOf(ComponentUtil.parseWidthHeightValue(listsHeightString));
        } catch (NumberFormatException e) {
            listsHeight = DEFAULT_LIST_HEIGHT;
        }

        String sourceListWithString = sourceElement.getAttribute(ATTR_SOURCE_LIST_WIDTH);
        try {
            sourceListsWidth = String.valueOf(ComponentUtil.parseWidthHeightValue(sourceListWithString));
        } catch (NumberFormatException e) {
            sourceListsWidth = DEFAULT_LIST_WIDTH;
        }

        String targetListWithString = sourceElement.getAttribute(ATTR_TARGET_LIST_WIDTH);
        try {
            targetListsWidth = String.valueOf(ComponentUtil.parseWidthHeightValue(targetListWithString));
        } catch (NumberFormatException e) {
            targetListsWidth = DEFAULT_LIST_WIDTH;
        }
    }

    /**
     * Prepare style classes.
     * 
     * @param sourceElement the source element
     */
    private void prepareStyleClasses(Element sourceElement) {
        // prepare style classes
        Set<String> styleClassesKeys = defaultStyleClasses.keySet();
        for (String key : styleClassesKeys) {

            String styleClass = sourceElement.getAttribute(key + CLASS_SUFFIX); 
            if (styleClass != null) {
                styleClasses.put(key, defaultStyleClasses.get(key) + " " //$NON-NLS-1$
                        + styleClass);
            } else {
                styleClasses.put(key, defaultStyleClasses.get(key));
            }
        }
    }
}
