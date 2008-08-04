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


import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.tools.jsf.vpe.richfaces.ComponentUtil;
import org.jboss.tools.jsf.vpe.richfaces.HtmlComponentUtil;
import org.jboss.tools.jsf.vpe.richfaces.template.util.RichFaces;
import org.jboss.tools.vpe.editor.VpeVisualDomBuilder;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.mapping.AttributeData;
import org.jboss.tools.vpe.editor.mapping.VpeElementData;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.template.VpeToggableTemplate;
import org.jboss.tools.vpe.editor.util.HTML;
import org.jboss.tools.vpe.editor.util.VpeStyleUtil;
import org.jboss.tools.vpe.xulrunner.browser.util.DOMTreeDumper;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMNode;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.sun.org.apache.xerces.internal.impl.xpath.regex.ParseException;


/**
 * The Class RichFacesComboBox2Template.
 * 
 * @author Eugene Stherbin
 */
public class RichFacesComboBoxTemplate extends AbstractEditableRichFacesTemplate implements VpeToggableTemplate {

    /** CSS_FILE_NAME. */
    private static final String CSS_FILE_NAME = "comboBox/comboBox.css"; //$NON-NLS-1$

    /** The Constant DEFAULT_ALIGN. */
    private static final String DEFAULT_ALIGN = "left"; //$NON-NLS-1$

    /** DEFAULT_INPUT_SIZE. */
    private static final String DEFAULT_INPUT_SIZE = "10"; //$NON-NLS-1$

    /** DEFAULT_INPUT_STYLE. */
    private static final String DEFAULT_INPUT_STYLE = "rich-combobox-default-input"; //$NON-NLS-1$

    /** The Constant DEFAULT_LIST_WIDTH. */
    private static final String DEFAULT_LIST_WIDTH = "150px"; //$NON-NLS-1$

    /** The Constant DEFAULT_WIDTH. */
    private static final String DEFAULT_WIDTH = "width : 150px"; //$NON-NLS-1$

    /** IMAGE_NAME_DOWN. */
    private static final String IMAGE_NAME_DOWN = "/comboBox/down.gif"; //$NON-NLS-1$

    /** The Constant RICH_COMBOBOX_BUTTON_STYLE_CLASS. */
    private static final String RICH_COMBOBOX_BUTTON_STYLE_CLASS = "rich-combobox-button"; //$NON-NLS-1$

    /** The Constant RICH_COMBOBOX_IMAGE_STYLE_CLASS. */
    private static final String RICH_COMBOBOX_IMAGE_STYLE_CLASS = "rich-combobox-image"; //$NON-NLS-1$

    /** The Constant RICH_COMBOBOX_INPUT_CELL_STYLE. */
    private static final String RICH_COMBOBOX_INPUT_CELL_STYLE = "rich-combobox-inputCell"; //$NON-NLS-1$

    /** The Constant SECOND_INPUT. */
    private static final String SECOND_INPUT = "secondInput"; //$NON-NLS-1$

    /** The Constant STYLE_EXT. */
    private static final String STYLE_EXT = "richFacesComboBox"; //$NON-NLS-1$
    
    private static final int LIST_ITEM_HEIGHT_DEFAULT_VALUE=18;

    /** The style clasess. */
    private Map<String, String> styleClasess = new HashMap<String, String>();

    /** The Constant ZERO_STRING. */
    private static final String ZERO_STRING = "0"; //$NON-NLS-1$

    /** The source align. */
    private String sourceAlign;

    /** The source button style. */
    private String sourceButtonStyle;

    /** The source default label. */
    private String sourceDefaultLabel = null;

    /** The source list height. */
    private String sourceListHeight;

    /** The source list width. */
    private String sourceListWidth;

    /** The source value. */
    private String sourceValue;

    /** The source width. */
    private String sourceWidth;

    /** The source style. */
    private String sourceStyle;

    /** The source input style. */
    private String sourceInputStyle;

    /** The source input class. */
    private String sourceInputClass;

    /** The is toggle. */
    private boolean isToggle = false;

    /** The source list style. */
    private String sourceListStyle;

    /** The source list class. */
    private String sourceListClass;

    /** The source item class. */
    private String sourceItemClass;
    
    
    /** Source button icon **/
    private String sourceButtonIcon;

    /**
     * The Constructor.
     */
    public RichFacesComboBoxTemplate() {
        super();
        initDefaultClasses();
    }

    /**
     * Calculate with for div.
     * 
     * @param with the with
     * @param minus the minus
     * 
     * @return the string
     */
    private String calculateWithForDiv(String with, int minus) {
        try {
            Integer intValue = 0;
            if (with.endsWith("px")) { //$NON-NLS-1$
                intValue = Integer.parseInt(with.substring(0, with.length() - 2));
            } else {
                intValue = Integer.parseInt(with);
            }
            return String.valueOf((intValue - minus)) + "px"; //$NON-NLS-1$
        } catch (NumberFormatException e) {
            return with;
        }

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
    public VpeCreationData create(VpePageContext pageContext, Node sourceNode, nsIDOMDocument visualDocument) {
        ComponentUtil.setCSSLink(pageContext, CSS_FILE_NAME, STYLE_EXT);

        final Element source = (Element) sourceNode;

        prepareData(source);
        final nsIDOMElement rootDiv = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_DIV);
        
        //Fix  https://jira.jboss.org/jira/browse/JBIDE-2430 issue with resizement. 
        rootDiv.setAttribute("style", "width : "+sourceWidth); //$NON-NLS-1$ //$NON-NLS-2$
        final nsIDOMElement secondDiv = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_DIV);
        secondDiv.setAttribute("align", this.sourceAlign); //$NON-NLS-1$
        secondDiv.setAttribute(HTML.ATTR_CLASS, styleClasess.get("secondDiv")); //$NON-NLS-1$
        String secondDivSubStyle = "; position: {0}; z-index: {1} ;"; //$NON-NLS-1$
        if (isToggle) {
            secondDivSubStyle = MessageFormat.format(secondDivSubStyle, "relative", "2"); //$NON-NLS-1$ //$NON-NLS-2$
        } else {
            secondDivSubStyle = MessageFormat.format(secondDivSubStyle, "static", "0"); //$NON-NLS-1$ //$NON-NLS-2$
        }
        // TODO add ATTR_STYLE.
        secondDiv.setAttribute(HTML.ATTR_STYLE, VpeStyleUtil.PARAMETER_WIDTH + VpeStyleUtil.COLON_STRING + this.sourceListWidth
                + VpeStyleUtil.SEMICOLON_STRING + secondDivSubStyle + sourceStyle);
        final nsIDOMElement thirdDiv = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_DIV);
        thirdDiv.setAttribute(HTML.ATTR_CLASS, styleClasess.get("thirdDiv")); //$NON-NLS-1$
        thirdDiv.setAttribute(HTML.ATTR_STYLE, VpeStyleUtil.PARAMETER_WIDTH + VpeStyleUtil.COLON_STRING + this.sourceWidth
                + "; z-index: 1;"); //$NON-NLS-1$
        final nsIDOMElement firstInput = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_INPUT);
        firstInput.setAttribute(HTML.ATTR_TYPE, "text"); //$NON-NLS-1$
        ;
        firstInput.setAttribute(HTML.ATTR_CLASS, styleClasess.get("firstInput") + " " + sourceInputClass); //$NON-NLS-1$ //$NON-NLS-2$
        firstInput.setAttribute("autocomplete", "off"); //$NON-NLS-1$ //$NON-NLS-2$
        firstInput.setAttribute(HTML.ATTR_STYLE, "width: " + calculateWithForDiv(this.sourceWidth, 17) + VpeStyleUtil.SEMICOLON_STRING //$NON-NLS-1$
                + sourceInputStyle);
        String value = null;
        if (ComponentUtil.isNotBlank(this.sourceDefaultLabel)) {
            value = this.sourceDefaultLabel;
        } else if (ComponentUtil.isNotBlank(this.sourceValue) && ComponentUtil.isBlank(this.sourceDefaultLabel)) {
            value = this.sourceValue;
        }

        if (value != null) {
            firstInput.setAttribute(RichFaces.ATTR_VALUE, value);
        }
        final nsIDOMElement secondInput = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_INPUT);
        secondInput.setAttribute(HTML.ATTR_TYPE, "text"); //$NON-NLS-1$
        ;
        secondInput.setAttribute(HTML.ATTR_CLASS, styleClasess.get(SECOND_INPUT));
        secondInput.setAttribute("readonly", String.valueOf(Boolean.TRUE)); //$NON-NLS-1$
        secondInput.setAttribute(RichFacesAbstractInplaceTemplate.VPE_USER_TOGGLE_ID_ATTR, String.valueOf(0));
        if (this.sourceButtonStyle != null) {
            secondInput.setAttribute(HTML.ATTR_STYLE, sourceButtonStyle);
        }
        //
        final nsIDOMElement thirdInput = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_INPUT);
        thirdInput.setAttribute(HTML.ATTR_TYPE, "text"); //$NON-NLS-1$
        ;
        thirdInput.setAttribute(HTML.ATTR_CLASS, styleClasess.get("thirdInput")); //$NON-NLS-1$
        thirdInput.setAttribute("readonly", String.valueOf(Boolean.TRUE)); //$NON-NLS-1$
        thirdInput.setAttribute(RichFacesAbstractInplaceTemplate.VPE_USER_TOGGLE_ID_ATTR, String.valueOf(0));
        if (this.sourceButtonStyle != null) {
            thirdInput.setAttribute(HTML.ATTR_STYLE, sourceButtonStyle);
        }
        

//        if (ComponentUtil.isNotBlank(this.sourceButtonIcon) && (this.sourceButtonIcon != IMAGE_NAME_DOWN)) {
//            thirdInput.setAttribute(HTML.ATTR_STYLE, thirdInput.getAttribute(HTML.ATTR_STYLE) + " ; background-image: url("
//                    + this.sourceButtonIcon + ")");
//        }
        final nsIDOMElement forthEmptyDiv = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_DIV);
        forthEmptyDiv.setAttribute(HTML.ATTR_CLASS, styleClasess.get("forthEmptyDiv")); //$NON-NLS-1$
        forthEmptyDiv.setAttribute(HTML.ATTR_STYLE, VpeStyleUtil.PARAMETER_WIDTH + VpeStyleUtil.COLON_STRING
                + calculateWithForDiv(this.sourceWidth, 10));
        forthEmptyDiv.appendChild(visualDocument.createTextNode("Struts")); //$NON-NLS-1$

        rootDiv.appendChild(secondDiv);

        secondDiv.appendChild(thirdDiv);
        if (isToggle) {
            secondDiv.appendChild(createToogleDiv(pageContext, source, visualDocument));
        }
        thirdDiv.appendChild(firstInput);
        thirdDiv.appendChild(secondInput);
        thirdDiv.appendChild(thirdInput);
        thirdDiv.appendChild(forthEmptyDiv);

        final VpeCreationData creationData = new VpeCreationData(rootDiv);
//        final DOMTreeDumper dumper = new DOMTreeDumper();
//        dumper.dumpToStream(System.err, secondDiv);

        return creationData;
    }

    /**
     * Creates the toogle div.
     * 
     * @param visualDocument the visual document
     * @param pageContext the page context
     * @param source the source
     * 
     * @return the ns IDOM node
     */
    private nsIDOMNode createToogleDiv(VpePageContext pageContext, Element source, nsIDOMDocument visualDocument) {

        final nsIDOMElement thirdEmptyDiv = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_DIV);

        thirdEmptyDiv.setAttribute(HTML.ATTR_STYLE, this.sourceListStyle + VpeStyleUtil.SEMICOLON_STRING
                + " z-index: 3; position: absolute; visibility: visible; top: 16px; left: 0px;"); //$NON-NLS-1$
        thirdEmptyDiv.setAttribute(HTML.ATTR_CLASS, styleClasess.get("thirdEmptyDiv") + " " + this.sourceListClass); //$NON-NLS-1$ //$NON-NLS-2$
        thirdEmptyDiv.setAttribute(HTML.ATTR_STYLE, "z-index: 3; position: absolute; visibility: visible; top: 16px; left: 0px;"); //$NON-NLS-1$

        final nsIDOMElement shadovDiv = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_DIV);

        final nsIDOMElement positionDiv = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_DIV);

        positionDiv.setAttribute(HTML.ATTR_CLASS, "rich-combobox-list-position"); //$NON-NLS-1$

        final nsIDOMElement decorationDiv = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_DIV);

        decorationDiv.setAttribute(HTML.ATTR_CLASS, "rich-combobox-list-decoration"); //$NON-NLS-1$
        // decorationDiv.setAttribute(HTML.ATTR_STYLE,
        // "height: 54px; width: 208px;");

        final nsIDOMElement scrollDiv = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_DIV);
        scrollDiv.setAttribute(HTML.ATTR_CLASS, "rich-combobox-list-scroll"); //$NON-NLS-1$
        final List<Element> items = ComponentUtil.getSelectItems(source.getChildNodes());
        int defaultHeight = LIST_ITEM_HEIGHT_DEFAULT_VALUE;
        
        if((items!=null) && (items.size() > 1)){
            defaultHeight = ((items.size() - 1)* LIST_ITEM_HEIGHT_DEFAULT_VALUE);
        }
        
        final String listHeight = ComponentUtil.isNotBlank(this.sourceListHeight) ? this.sourceListHeight : String.valueOf(defaultHeight)
                + "px";  //$NON-NLS-1$
        scrollDiv.setAttribute(HTML.ATTR_STYLE, "height: "+listHeight+"; width: " + calculateWithForDiv(sourceListWidth, 2)); //$NON-NLS-1$ //$NON-NLS-2$

        final List<Element> selectItems = ComponentUtil.getSelectItems(source.getChildNodes());

        if (selectItems.size() > 0) {
            for (Element e : selectItems) {
                scrollDiv.appendChild(createSelectItem(e, visualDocument));
            }
        }

        shadovDiv.setAttribute(HTML.ATTR_CLASS, "rich-combobox-shadow"); //$NON-NLS-1$

        final nsIDOMElement table = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_TABLE);
        table.setAttribute(HTML.ATTR_CELLPADDING, "0"); //$NON-NLS-1$
        table.setAttribute(HTML.ATTR_CELLSPACING, "0"); //$NON-NLS-1$
        table.setAttribute(HTML.ATTR_BORDER, "0"); //$NON-NLS-1$
        String width = ""; //$NON-NLS-1$
        try {

            int w = ComponentUtil.parseWidthHeightValue(sourceListWidth);
            w += 7;
            width = String.valueOf(w);
        } catch (ParseException e) {
            width = "217"; //$NON-NLS-1$
        }
        table.setAttribute(HTML.ATTR_STYLE, "width: " + width + "px ; height: 63px;"); //$NON-NLS-1$ //$NON-NLS-2$

        final nsIDOMElement tr1 = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_TR);
        final nsIDOMElement tr2 = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_TR);

        final nsIDOMElement tr1_td1 = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_TD);
        final nsIDOMElement tr1_td2 = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_TD);

        final nsIDOMElement tr2_td1 = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_TD);
        final nsIDOMElement tr2_td2 = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_TD);

        final nsIDOMElement tr1_td1_img = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_IMG);
        final nsIDOMElement tr1_td2_img = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_IMG);

        final nsIDOMElement tr2_td1_img = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_IMG);
        final nsIDOMElement tr2_td2_img = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_IMG);

        tr1_td1.setAttribute(HTML.ATTR_CLASS, "rich-combobox-shadow-tl"); //$NON-NLS-1$
        tr1_td2.setAttribute(HTML.ATTR_CLASS, "rich-combobox-shadow-tr"); //$NON-NLS-1$

        tr2_td1.setAttribute(HTML.ATTR_CLASS, "rich-combobox-shadow-bl"); //$NON-NLS-1$
        tr2_td2.setAttribute(HTML.ATTR_CLASS, "rich-combobox-shadow-br"); //$NON-NLS-1$

        setUpImg(tr1_td1_img, 10, 1, 0, "comboBox/spacer.gif"); //$NON-NLS-1$
        setUpImg(tr1_td2_img, 1, 10, 0, "comboBox/spacer.gif"); //$NON-NLS-1$
        setUpImg(tr2_td1_img, 1, 10, 0, "comboBox/spacer.gif"); //$NON-NLS-1$
        setUpImg(tr2_td2_img, 10, 1, 0, "comboBox/spacer.gif"); //$NON-NLS-1$

        thirdEmptyDiv.appendChild(shadovDiv);
        shadovDiv.appendChild(table);
        thirdEmptyDiv.appendChild(positionDiv);
        positionDiv.appendChild(decorationDiv);
        decorationDiv.appendChild(scrollDiv);

        table.appendChild(tr1);
        table.appendChild(tr2);
        tr1.appendChild(tr1_td1);
        tr1.appendChild(tr1_td2);

        tr2.appendChild(tr2_td1);
        tr2.appendChild(tr2_td2);

        tr1_td1.appendChild(tr1_td1_img);
        tr1_td1.appendChild(visualDocument.createElement(HtmlComponentUtil.HTML_TAG_BR));

        tr1_td2.appendChild(tr1_td2_img);
        tr1_td2.appendChild(visualDocument.createElement(HtmlComponentUtil.HTML_TAG_BR));

        tr2_td1.appendChild(tr2_td1_img);
        tr2_td1.appendChild(visualDocument.createElement(HtmlComponentUtil.HTML_TAG_BR));

        tr2_td2.appendChild(tr2_td2_img);
        tr2_td2.appendChild(visualDocument.createElement(HtmlComponentUtil.HTML_TAG_BR));

        return thirdEmptyDiv;
    }

    /**
     * Creates the select item.
     * 
     * @param visualDocument the visual document
     * @param e the e
     * 
     * @return the ns IDOM node
     */
    private nsIDOMNode createSelectItem(Element e, nsIDOMDocument visualDocument) {
        final nsIDOMElement item = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_SPAN);

        item.setAttribute(HTML.ATTR_CLASS, "rich-combobox-item " + sourceItemClass); //$NON-NLS-1$
        item.appendChild(visualDocument.createTextNode(ComponentUtil.getSelectItemValue(e)));
        return item;
    }

    /**
     * Creates the button table.
     * 
     * @param visualDocument the visual document
     * @param sourceNode the source node
     * 
     * @return the ns IDOM element
     */
    private nsIDOMElement createButtonTable(nsIDOMDocument visualDocument, Node sourceNode) {
        nsIDOMElement table = visualDocument.createElement(HTML.TAG_TABLE);
        setUpTable(table);

        nsIDOMElement rowUp = visualDocument.createElement(HTML.TAG_TR);

        table.appendChild(rowUp);

        nsIDOMElement rowDown = visualDocument.createElement(HTML.TAG_TR);
        nsIDOMElement cellDown = visualDocument.createElement(HTML.TAG_TD);

        nsIDOMElement imageDownElement = visualDocument.createElement(HTML.TAG_INPUT);

        imageDownElement.setAttribute(HTML.ATTR_BORDER, ZERO_STRING);
        imageDownElement.setAttribute(HTML.ATTR_TYPE, HTML.VALUE_IMAGE_TYPE);
        imageDownElement.setAttribute(HTML.ATTR_CLASS, RICH_COMBOBOX_IMAGE_STYLE_CLASS);
        cellDown.appendChild(imageDownElement);
        rowDown.appendChild(cellDown);
        table.appendChild(rowDown);

        return table;
    }

    /**
     * Create a HTML-part containg input element.
     * 
     * @param sourceElement the source element
     * @param visualDocument The current node of the source tree.
     * @param sourceNode The document of the visual tree.
     * @param elementData the element data
     * 
     * @return a HTML-part containg input element
     */
    private nsIDOMElement createInputElement(nsIDOMDocument visualDocument, Element sourceElement, VpeElementData elementData) {
        nsIDOMElement inputElement = visualDocument.createElement(HTML.TAG_INPUT);

        inputElement.setAttribute(HTML.ATTR_CLASS, getInputClass(sourceElement));

        inputElement.setAttribute(HTML.ATTR_STYLE, getInputStyle(sourceElement));

        inputElement.setAttribute(HTML.ATTR_TYPE, HTML.VALUE_TEXT_TYPE);

        inputElement.setAttribute(HTML.ATTR_SIZE, getInputSize(sourceElement));
        inputElement.setAttribute(HTML.ATTR_VALUE, getInputValue(sourceElement));

        if ((sourceElement).hasAttribute(RichFaces.ATTR_VALUE)) {
            elementData.addNodeData(new AttributeData(sourceElement.getAttributeNode(RichFaces.ATTR_VALUE), inputElement, true));
        } else {
            elementData.addNodeData(new AttributeData(RichFaces.ATTR_VALUE, inputElement, true));
        }

        return inputElement;
    }

    /**
     * Gets the default input class.
     * 
     * @return the default input class
     */
    public String getDefaultInputClass() {
        return DEFAULT_INPUT_STYLE;
    }

    /**
     * Gets the default input size.
     * 
     * @return the default input size
     */
    public String getDefaultInputSize() {
        return DEFAULT_INPUT_SIZE;
    }

    /**
     * Return a input class.
     * 
     * @param sourceElement the source element
     * @param sourceNode a sourceNode
     * 
     * @return a input class
     */
    public String getInputClass(Element sourceElement) {
        String returnValue = getDefaultInputClass();
        String tmp = getAttribute(sourceElement, RichFaces.ATTR_INPUT_CLASS);
        if (tmp.length() != 0) {
            returnValue = new StringBuffer().append(returnValue).append(" ") //$NON-NLS-1$
                    .append(tmp).toString();
        }
        return returnValue;
    }

    /**
     * Return a input size.
     * 
     * @param sourceElement the source element
     * @param sourceNode a sourceNode
     * 
     * @return a input size
     */
    protected String getInputSize(Element sourceElement) {
        String returnValue = getDefaultInputSize();
        String tmp = getAttribute(sourceElement, RichFaces.ATTR_INPUT_SIZE);
        if (tmp.length() != 0) {
            returnValue = tmp;
        }
        return returnValue;
    }

    /**
     * Return a input style.
     * 
     * @param sourceElement the source element
     * @param sourceNode a sourceNode
     * 
     * @return a input style
     */
    private String getInputStyle(Element sourceElement) {
        String returnValue = getAttribute(sourceElement, RichFaces.ATTR_INPUT_STYLE);
        return returnValue;
    }

    /**
     * Return a input value.
     * 
     * @param sourceElement the source element
     * @param sourceNode a sourceNode
     * 
     * @return a input value
     */
    private String getInputValue(Element sourceElement) {
        String returnValue = getAttribute(sourceElement, RichFaces.ATTR_VALUE);
        final String defaultLabel = getAttribute(sourceElement, RichFaces.ATTR_DEFAULT_LABEL);

        if (defaultLabel != null && defaultLabel.length() > 0) {
            returnValue = defaultLabel;
        }
        return returnValue;
    }

    /**
     * Inits the default classes.
     */
    private void initDefaultClasses() {
        styleClasess.put("secondDiv", "rich-combobox-font rich-combobox"); //$NON-NLS-1$ //$NON-NLS-2$
        styleClasess.put("thirdDiv", "rich-combobox-font rich-combobox-shell"); //$NON-NLS-1$ //$NON-NLS-2$
        styleClasess.put("thirdEmptyDiv", "rich-combobox-list-cord"); //$NON-NLS-1$ //$NON-NLS-2$
        styleClasess.put("firstInput", "rich-combobox-font-disabled rich-combobox-input-inactive"); //$NON-NLS-1$ //$NON-NLS-2$
        styleClasess.put(SECOND_INPUT, "rich-combobox-font-inactive rich-combobox-button-background rich-combobox-button-inactive"); //$NON-NLS-1$
        styleClasess.put("thirdInput", "rich-combobox-font-inactive rich-combobox-button-icon-inactive rich-combobox-button-inactive"); //$NON-NLS-1$ //$NON-NLS-2$
        styleClasess.put("forthEmptyDiv", "rich-combobox-strut rich-combobox-font"); //$NON-NLS-1$ //$NON-NLS-2$
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
    public boolean isRecreateAtAttrChange(VpePageContext pageContext, Element sourceElement, nsIDOMDocument visualDocument,
            nsIDOMElement visualNode, Object data, String name, String value) {
        // TODO Auto-generated method stub
        return true;
    }

    /**
     * Prepare data.
     * 
     * @param source the source
     */
    private void prepareData(Element source) {
        this.sourceAlign = source.getAttribute("align"); //$NON-NLS-1$
        if (ComponentUtil.isBlank(this.sourceAlign)) {
            this.sourceAlign = DEFAULT_ALIGN;
        }
        this.sourceListWidth = source.getAttribute("listWidth"); //$NON-NLS-1$

        if (ComponentUtil.isBlank(this.sourceListWidth)) {
            this.sourceListWidth = DEFAULT_LIST_WIDTH;
        }
        this.sourceListHeight = source.getAttribute("listHeight"); //$NON-NLS-1$
        

        this.sourceWidth = source.getAttribute("width"); //$NON-NLS-1$
        
        if (ComponentUtil.isBlank(this.sourceWidth)) {
            this.sourceWidth = DEFAULT_LIST_WIDTH;
        }else if(ComponentUtil.isNotBlank(this.sourceWidth) && (this.sourceListWidth == DEFAULT_LIST_WIDTH)){
            this.sourceListWidth = this.sourceWidth;
        }
        
        if (ComponentUtil.isNotBlank(this.sourceWidth) && (this.sourceWidth != DEFAULT_LIST_WIDTH)) {
            if(!this.sourceWidth.endsWith("px")){ //$NON-NLS-1$
                try {
                    int intValue = Integer.parseInt(this.sourceWidth);
                    this.sourceWidth = String.valueOf(intValue)+"px"; //$NON-NLS-1$
                } catch (NumberFormatException e) {
                    this.sourceListWidth = DEFAULT_LIST_WIDTH;
                }
            }
        }

        this.sourceDefaultLabel = ComponentUtil.getAttribute(source, "defaultLabel"); //$NON-NLS-1$
        this.sourceValue = ComponentUtil.getAttribute(source, "value"); //$NON-NLS-1$

        this.sourceButtonStyle = ComponentUtil.getAttribute(source, "buttonStyle"); //$NON-NLS-1$

        final String sourceStyleClasess = ComponentUtil.getAttribute(source, RichFaces.ATTR_STYLE_CLASS);

        if (ComponentUtil.isNotBlank(sourceStyleClasess)) {
            styleClasess.put("secondDiv", styleClasess.get("secondDiv") + " " + sourceStyleClasess); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        }

        this.sourceStyle = ComponentUtil.getAttribute(source, HTML.ATTR_STYLE);
        this.sourceInputStyle = ComponentUtil.getAttribute(source, "inputStyle"); //$NON-NLS-1$
        this.sourceInputClass = ComponentUtil.getAttribute(source, "inputClass"); //$NON-NLS-1$
        this.sourceListClass = ComponentUtil.getAttribute(source, "listClass"); //$NON-NLS-1$
        this.sourceListStyle = ComponentUtil.getAttribute(source, "listStyle"); //$NON-NLS-1$
        this.sourceItemClass = ComponentUtil.getAttribute(source, "itemClass"); //$NON-NLS-1$
        
        this.sourceButtonIcon = ComponentUtil.getAttribute(source, "buttonIcon"); //$NON-NLS-1$
        
        if(ComponentUtil.isBlank(this.sourceButtonIcon)){
            this.sourceButtonIcon = IMAGE_NAME_DOWN;
        }

    }

    /**
     * Sets the attribute.
     * 
     * @param sourceElement the source element
     * @param value the value
     * @param visualDocument the visual document
     * @param visualNode the visual node
     * @param data the data
     * @param pageContext the page context
     * @param name the name
     * 
     * @see com.exadel.vpe.editor.template.VpeAbstractTemplate#setAttribute(com.
     * exadel.vpe.editor.context.VpePageContext, org.w3c.dom.Element,
     * org.w3c.dom.Document, org.w3c.dom.Node, java.lang.Object,
     * java.lang.String, java.lang.String)
     */
    @Override
    public void setAttribute(VpePageContext pageContext, Element sourceElement, nsIDOMDocument visualDocument, nsIDOMNode visualNode,
            Object data, String name, String value) {
        // 1. Call super method
        super.setAttribute(pageContext, sourceElement, visualDocument, visualNode, data, name, value);

        // nsIDOMElement table = (nsIDOMElement) visualNode.queryInterface(
        // nsIDOMElement.NS_IDOMELEMENT_IID);
        // nsIDOMNodeList listTable = table.getChildNodes();
        // nsIDOMNode nodeTr = listTable.item(0);
        // nsIDOMNodeList listTr = nodeTr.getChildNodes();
        // nsIDOMNode nodeTd = listTr.item(0);
        //
        // nsIDOMNodeList listTd = nodeTd.getChildNodes();
        // nsIDOMNode entry0 = listTd.item(0);
        //
        // nsIDOMElement inputElement = (nsIDOMElement) entry0.queryInterface(
        // nsIDOMElement.NS_IDOMELEMENT_IID);
        //
        // inputElement.setAttribute(HTML.ATTR_CLASS, getInputClass(
        // sourceElement));
        //
        // inputElement.setAttribute(HTML.ATTR_STYLE, getInputStyle(
        // sourceElement));
        // inputElement.setAttribute(HTML.ATTR_SIZE, getInputSize(sourceElement)
        // );
        // inputElement.setAttribute(HTML.ATTR_VALUE, getInputValue(
        // sourceElement));
        //
        // // 3. Set a style for main container
        // String strStyle = getAttribute(sourceElement, RichFaces.ATTR_STYLE);
        // strStyle = ((strStyle.length() == 0) ? DEFAULT_WIDTH : strStyle);
        //
        // table.setAttribute(HTML.ATTR_STYLE, strStyle);

    }

    /**
     * Sets the up img.
     * 
     * @param i      *
     * @param width the width
     * @param height the height
     * @param img the img
     * @param j      *
     * @param image the image
     * @param border the border
     * @param td1Img      */
    protected void setUpImg(nsIDOMElement img, int width, int height, int border, String image) {
        ComponentUtil.setImg(img, image);
        img.setAttribute(HTML.ATTR_WIDTH, String.valueOf(width));
        img.setAttribute(HTML.ATTR_HEIGHT, String.valueOf(height));
        img.setAttribute(HTML.ATTR_BORDER, String.valueOf(border));

    }

    /**
     * Sets the up table.
     * 
     * @param table the table
     */
    private void setUpTable(final nsIDOMElement table) {
        table.setAttribute(HTML.ATTR_BORDER, ZERO_STRING);
        table.setAttribute(HTML.ATTR_CELLPADDING, ZERO_STRING);
        table.setAttribute(HTML.ATTR_CELLSPACING, ZERO_STRING);
    }

    /**
     * Sets the up td.
     * 
     * @param visualDocument the visual document
     * @param elementData the element data
     * @param cellInput the cell input
     * @param source the source
     */
    private void setUpTd(nsIDOMDocument visualDocument, final Element source, final VpeElementData elementData,
            final nsIDOMElement cellInput) {
        cellInput.setAttribute(HTML.ATTR_CLASS, RICH_COMBOBOX_INPUT_CELL_STYLE);
        cellInput.setAttribute(HTML.ATTR_VALIGN, HTML.VALUE_TOP_ALIGN);
        cellInput.appendChild(createInputElement(visualDocument, source, elementData));
    }

    /**
     * Stop toggling.
     * 
     * @param sourceNode the source node
     */
    public void stopToggling(Node sourceNode) {
        isToggle = false;

    }

    /**
     * Toggle.
     * 
     * @param builder the builder
     * @param sourceNode the source node
     * @param toggleId the toggle id
     */
    public void toggle(VpeVisualDomBuilder builder, Node sourceNode, String toggleId) {
        isToggle = !isToggle;

    }

}
