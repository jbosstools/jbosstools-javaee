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

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.tools.jsf.vpe.richfaces.AttributeMap;
import org.jboss.tools.jsf.vpe.richfaces.ComponentUtil;
import org.jboss.tools.jsf.vpe.richfaces.template.util.RichFaces;
import org.jboss.tools.vpe.editor.VpeVisualDomBuilder;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.mapping.AttributeData;
import org.jboss.tools.vpe.editor.mapping.VpeElementData;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.template.VpeToggableTemplate;
import org.jboss.tools.vpe.editor.util.Constants;
import org.jboss.tools.vpe.editor.util.HTML;
import org.jboss.tools.vpe.editor.util.VpeStyleUtil;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMNode;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.sun.org.apache.xerces.internal.impl.xpath.regex.ParseException;


/**
 * The Class RichFacesComboBoxTemplate.
 * 
 * @author Eugene Stherbin
 */
public class RichFacesComboBoxTemplate extends AbstractEditableRichFacesTemplate implements VpeToggableTemplate {

	private static final String BUTTON_ICON_CLASSES_DISABLED = "rich-combobox-font-inactive rich-combobox-button-icon-disabled rich-combobox-button-inactive"; //$NON-NLS-1$
	private static final String BUTTON_ICON_CLASSES = "rich-combobox-font-inactive rich-combobox-button-icon-inactive rich-combobox-button-inactive"; //$NON-NLS-1$
	private static final String SECOND_DIV = "secondDiv"; //$NON-NLS-1$
	private static final String THIRD_DIV = "thirdDiv"; //$NON-NLS-1$
	private static final String THIRD_EMPTY_DIV = "thirdEmptyDiv"; //$NON-NLS-1$
	private static final String TEXT_FIELD = "textField"; //$NON-NLS-1$
	private static final String BUTTON_ICON = "buttonIcon"; //$NON-NLS-1$

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

    /** The Constant BUTTON_BACKGROUND. */
    private static final String BUTTON_BACKGROUND = "buttonBackground"; //$NON-NLS-1$

    /** The Constant STYLE_EXT. */
    private static final String STYLE_EXT = "richFacesComboBox"; //$NON-NLS-1$
    
    private static final int LIST_ITEM_HEIGHT_DEFAULT_VALUE=18;

    /** The style clasess. */
    private Map<String, String> styleClasess = new HashMap<String, String>();

    /** The Constant ZERO_STRING. */
    private static final String ZERO_STRING = "0"; //$NON-NLS-1$

    /** The source align. */
    // Commented because of not working alignment in RichFaces implementation
    // private String sourceAlign;

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
    
    private String sourceButtonIconInactive;
    
    private String sourceButtonIconDisabled;

	private boolean disabled;

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
        final nsIDOMElement rootDiv = visualDocument.createElement(HTML.TAG_DIV);
        
        //Fix  https://jira.jboss.org/jira/browse/JBIDE-2430 issue with resizement. 
        rootDiv.setAttribute(HTML.ATTR_STYLE, HTML.STYLE_PARAMETER_WIDTH+Constants.COLON+sourceWidth);
        final nsIDOMElement comboBoxDiv = visualDocument.createElement(HTML.TAG_DIV); 
        final nsIDOMElement secondDiv = visualDocument.createElement(HTML.TAG_DIV);

        // Commented because of not working alignment in RichFaces implementation  
        // comboBoxDiv.setAttribute(HTML.ATTR_ALIGN, this.sourceAlign); 
        // secondDiv.setAttribute(HTML.ATTR_ALIGN, this.sourceAlign);

        
        //comboBoxDiv.setAttribute(HTML.ATTR_CLASS, styleClasess.get("secondDiv")); //$NON-NLS-1$ 
        secondDiv.setAttribute(HTML.ATTR_CLASS, styleClasess.get(SECOND_DIV)); //$NON-NLS-1$
        String secondDivSubStyle = "; position: {0}; z-index: {1} ;"; //$NON-NLS-1$
        
        if (isToggle) {
            secondDivSubStyle = MessageFormat.format(secondDivSubStyle, "relative", "2"); //$NON-NLS-1$ //$NON-NLS-2$
        } else {
            secondDivSubStyle = MessageFormat.format(secondDivSubStyle, "static", "0"); //$NON-NLS-1$ //$NON-NLS-2$
        }
        // TODO add ATTR_STYLE.
        comboBoxDiv.setAttribute(HTML.ATTR_STYLE, HTML.STYLE_PARAMETER_WIDTH + Constants.COLON + this.sourceListWidth
                + Constants.SEMICOLON + secondDivSubStyle);
        secondDiv.setAttribute(HTML.ATTR_STYLE, HTML.STYLE_PARAMETER_WIDTH + Constants.COLON + this.sourceListWidth
                + Constants.SEMICOLON + secondDivSubStyle + sourceStyle);
        final nsIDOMElement thirdDiv = visualDocument.createElement(HTML.TAG_DIV);
        thirdDiv.setAttribute(HTML.ATTR_CLASS, styleClasess.get(THIRD_DIV));
        thirdDiv.setAttribute(HTML.ATTR_STYLE, HTML.STYLE_PARAMETER_WIDTH + Constants.COLON + this.sourceWidth
                + "; z-index: 1;"); //$NON-NLS-1$
        final nsIDOMElement textField = visualDocument.createElement(HTML.TAG_INPUT);
        textField.setAttribute(HTML.ATTR_TYPE, HTML.VALUE_TEXT_TYPE); 
        
        textField.setAttribute(HTML.ATTR_CLASS, styleClasess.get(TEXT_FIELD) + Constants.WHITE_SPACE + sourceInputClass); //$NON-NLS-1$ 
        textField.setAttribute("autocomplete", "off"); //$NON-NLS-1$ //$NON-NLS-2$
        textField.setAttribute(HTML.ATTR_STYLE, HTML.STYLE_PARAMETER_WIDTH+ Constants.COLON + calculateWithForDiv(this.sourceWidth, 17) + Constants.SEMICOLON 
                + sourceInputStyle);
        String value = null;
        if (ComponentUtil.isNotBlank(this.sourceDefaultLabel)) {
            value = this.sourceDefaultLabel;
        } else if (ComponentUtil.isNotBlank(this.sourceValue) && ComponentUtil.isBlank(this.sourceDefaultLabel)) {
            value = this.sourceValue;
        }

        if (value != null) {
            textField.setAttribute(RichFaces.ATTR_VALUE, value);
        }
        final nsIDOMElement buttonBackground = visualDocument.createElement(HTML.TAG_INPUT);
        buttonBackground.setAttribute(HTML.ATTR_TYPE, HTML.VALUE_TEXT_TYPE); 
        
        if (disabled) {
        	styleClasess.put(BUTTON_ICON, BUTTON_ICON_CLASSES_DISABLED); //$NON-NLS-1$
        } else {
        	styleClasess.put(BUTTON_ICON, BUTTON_ICON_CLASSES); //$NON-NLS-1$
        }
        
        buttonBackground.setAttribute(HTML.ATTR_CLASS, styleClasess.get(BUTTON_BACKGROUND));
        buttonBackground.setAttribute(HTML.ATTR_READONLY, Constants.TRUE); 
        buttonBackground.setAttribute(RichFacesAbstractInplaceTemplate.VPE_USER_TOGGLE_ID_ATTR, String.valueOf(0));
        if (this.sourceButtonStyle != null) {
            buttonBackground.setAttribute(HTML.ATTR_STYLE, sourceButtonStyle);
        }
        //
        final nsIDOMElement buttonIcon = visualDocument.createElement(HTML.TAG_INPUT);
        buttonIcon.setAttribute(HTML.ATTR_TYPE, HTML.VALUE_TEXT_TYPE); 
        ;
        buttonIcon.setAttribute(HTML.ATTR_CLASS, styleClasess.get(BUTTON_ICON)); //$NON-NLS-1$
        buttonIcon.setAttribute(HTML.ATTR_READONLY, Constants.TRUE); 
        buttonIcon.setAttribute(RichFacesAbstractInplaceTemplate.VPE_USER_TOGGLE_ID_ATTR, String.valueOf(0));
        if (this.sourceButtonStyle != null) {
            buttonIcon.setAttribute(HTML.ATTR_STYLE, sourceButtonStyle);
        }
        
        String actualSourceButton;
        if (disabled) {
        	actualSourceButton = sourceButtonIconDisabled;
        } else if (isToggle) {
        	actualSourceButton = sourceButtonIcon;
        } else {
        	actualSourceButton = sourceButtonIconInactive;
        }
        
        if (ComponentUtil.isNotBlank(actualSourceButton) && (actualSourceButton != IMAGE_NAME_DOWN)) {
        	String buttonIconPath = VpeStyleUtil.addFullPathToImgSrc(actualSourceButton, pageContext, true);
        	buttonIconPath = buttonIconPath.replace('\\', '/');
    		String style = "background-image: url(" + buttonIconPath + ")"; //$NON-NLS-1$ //$NON-NLS-2$
            buttonIcon.setAttribute(HTML.ATTR_STYLE, buttonIcon.getAttribute(HTML.ATTR_STYLE) + style);
        }
        final nsIDOMElement forthEmptyDiv = visualDocument.createElement(HTML.TAG_DIV);
        forthEmptyDiv.setAttribute(HTML.ATTR_CLASS, styleClasess.get("forthEmptyDiv")); //$NON-NLS-1$
        forthEmptyDiv.setAttribute(HTML.ATTR_STYLE, HTML.STYLE_PARAMETER_WIDTH + Constants.COLON
                + calculateWithForDiv(this.sourceWidth, 10));
        forthEmptyDiv.appendChild(visualDocument.createTextNode("Struts")); //$NON-NLS-1$

        rootDiv.appendChild(comboBoxDiv);
        comboBoxDiv.appendChild(secondDiv); 

        secondDiv.appendChild(thirdDiv);
        if (isToggle) {
        	comboBoxDiv.appendChild(createToogleDiv(pageContext, source, visualDocument));
        }
        thirdDiv.appendChild(textField);
        thirdDiv.appendChild(buttonBackground);
        thirdDiv.appendChild(buttonIcon);
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

        final nsIDOMElement thirdEmptyDiv = visualDocument.createElement(HTML.TAG_DIV);

        thirdEmptyDiv.setAttribute(HTML.ATTR_STYLE, this.sourceListStyle + Constants.SEMICOLON
                + " z-index: 3; position: absolute; visibility: visible; top: 16px; left: 0px;"); //$NON-NLS-1$
        thirdEmptyDiv.setAttribute(HTML.ATTR_CLASS, styleClasess.get(THIRD_EMPTY_DIV) + " " + this.sourceListClass); //$NON-NLS-1$ //$NON-NLS-2$
        thirdEmptyDiv.setAttribute(HTML.ATTR_STYLE, "z-index: 3; position: absolute; visibility: visible; top: 16px; left: 0px;"); //$NON-NLS-1$

        final nsIDOMElement shadovDiv = visualDocument.createElement(HTML.TAG_DIV);

        final nsIDOMElement positionDiv = visualDocument.createElement(HTML.TAG_DIV);

        positionDiv.setAttribute(HTML.ATTR_CLASS, "rich-combobox-list-position"); //$NON-NLS-1$

        final nsIDOMElement decorationDiv = visualDocument.createElement(HTML.TAG_DIV);

        decorationDiv.setAttribute(HTML.ATTR_CLASS, "rich-combobox-list-decoration"); //$NON-NLS-1$
        // decorationDiv.setAttribute(HTML.ATTR_STYLE,
        // "height: 54px; width: 208px;");

        final nsIDOMElement scrollDiv = visualDocument.createElement(HTML.TAG_DIV);
        scrollDiv.setAttribute(HTML.ATTR_CLASS, "rich-combobox-list-scroll"); //$NON-NLS-1$
        final List<Element> items = ComponentUtil.getSelectItems(source.getChildNodes());
        int defaultHeight = LIST_ITEM_HEIGHT_DEFAULT_VALUE;
        
        if((items!=null) && (items.size() > 1)){
            defaultHeight = ((items.size() - 1)* LIST_ITEM_HEIGHT_DEFAULT_VALUE);
        }
        
        final String listHeight = ComponentUtil.isNotBlank(this.sourceListHeight) ? this.sourceListHeight : String.valueOf(defaultHeight)
                + "px";  //$NON-NLS-1$
        scrollDiv.setAttribute(HTML.ATTR_STYLE, HTML.STYLE_PARAMETER_HEIGHT
				+ Constants.COLON + listHeight + Constants.SEMICOLON
				+ HTML.STYLE_PARAMETER_WIDTH + Constants.COLON
				+ calculateWithForDiv(sourceListWidth, 2)); 

        final List<Element> selectItems = ComponentUtil.getSelectItems(source.getChildNodes());

        if (selectItems.size() > 0) {
            for (Element e : selectItems) {
                scrollDiv.appendChild(createSelectItem(e, visualDocument));
            }
        }

        shadovDiv.setAttribute(HTML.ATTR_CLASS, "rich-combobox-shadow"); //$NON-NLS-1$

        final nsIDOMElement table = visualDocument.createElement(HTML.TAG_TABLE);
        table.setAttribute(HTML.ATTR_CELLPADDING, "0"); //$NON-NLS-1$
        table.setAttribute(HTML.ATTR_CELLSPACING, "0"); //$NON-NLS-1$
        table.setAttribute(HTML.ATTR_BORDER, "0"); //$NON-NLS-1$
        String width = Constants.EMPTY; 
        try {

            int w = ComponentUtil.parseWidthHeightValue(sourceListWidth);
            w += 7;
            width = String.valueOf(w);
        } catch (ParseException e) {
            width = "217"; //$NON-NLS-1$
        }
        table.setAttribute(HTML.ATTR_STYLE, HTML.STYLE_PARAMETER_WIDTH
				+ Constants.COLON + width + Constants.PIXEL
				+ Constants.SEMICOLON + HTML.STYLE_PARAMETER_HEIGHT
				+ Constants.COLON + "63px;"); //$NON-NLS-1$ 

        final nsIDOMElement tr1 = visualDocument.createElement(HTML.TAG_TR);
        final nsIDOMElement tr2 = visualDocument.createElement(HTML.TAG_TR);

        final nsIDOMElement tr1_td1 = visualDocument.createElement(HTML.TAG_TD);
        final nsIDOMElement tr1_td2 = visualDocument.createElement(HTML.TAG_TD);

        final nsIDOMElement tr2_td1 = visualDocument.createElement(HTML.TAG_TD);
        final nsIDOMElement tr2_td2 = visualDocument.createElement(HTML.TAG_TD);

        final nsIDOMElement tr1_td1_img = visualDocument.createElement(HTML.TAG_IMG);
        final nsIDOMElement tr1_td2_img = visualDocument.createElement(HTML.TAG_IMG);

        final nsIDOMElement tr2_td1_img = visualDocument.createElement(HTML.TAG_IMG);
        final nsIDOMElement tr2_td2_img = visualDocument.createElement(HTML.TAG_IMG);

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
        tr1_td1.appendChild(visualDocument.createElement(HTML.TAG_BR));

        tr1_td2.appendChild(tr1_td2_img);
        tr1_td2.appendChild(visualDocument.createElement(HTML.TAG_BR));

        tr2_td1.appendChild(tr2_td1_img);
        tr2_td1.appendChild(visualDocument.createElement(HTML.TAG_BR));

        tr2_td2.appendChild(tr2_td2_img);
        tr2_td2.appendChild(visualDocument.createElement(HTML.TAG_BR));

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
        final nsIDOMElement item = visualDocument.createElement(HTML.TAG_SPAN);

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
            returnValue = new StringBuffer().append(returnValue).append(Constants.WHITE_SPACE) 
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
        styleClasess.put(SECOND_DIV, "rich-combobox-font rich-combobox"); //$NON-NLS-1$ //$NON-NLS-2$
        styleClasess.put(THIRD_DIV, "rich-combobox-font rich-combobox-shell"); //$NON-NLS-1$ //$NON-NLS-2$
        styleClasess.put(THIRD_EMPTY_DIV, "rich-combobox-list-cord"); //$NON-NLS-1$ //$NON-NLS-2$
        styleClasess.put(TEXT_FIELD, "rich-combobox-font-disabled rich-combobox-input-inactive"); //$NON-NLS-1$ //$NON-NLS-2$
       	styleClasess.put(BUTTON_BACKGROUND, "rich-combobox-font-inactive rich-combobox-button-background rich-combobox-button-inactive"); //$NON-NLS-1$
       	styleClasess.put(BUTTON_ICON, BUTTON_ICON_CLASSES); //$NON-NLS-1$ //$NON-NLS-2$
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
    	AttributeMap attributeMap = new AttributeMap(source);

        // Commented because of not working alignment in RichFaces implementation
    	// if (attributeMap.isBlank(RichFaces.ATTR_ALIGN)) {
    	// 	this.sourceAlign = DEFAULT_ALIGN;
    	// } else {
    	// 	this.sourceAlign = attributeMap.getString(RichFaces.ATTR_ALIGN);
    	// }
        
        if (attributeMap.isBlank(RichFaces.ATTR_LIST_WIDTH)) {
        	 this.sourceListWidth = DEFAULT_LIST_WIDTH;
        } else {
        	this.sourceListWidth = attributeMap.getString(RichFaces.ATTR_LIST_WIDTH);
        }
        
        this.sourceListHeight = attributeMap.getString(RichFaces.ATTR_LIST_HEIGHT);
        
        if (attributeMap.isBlank(RichFaces.ATTR_WIDTH)) {
        	this.sourceWidth = DEFAULT_LIST_WIDTH;
        } else {
        	this.sourceWidth = attributeMap.getString(RichFaces.ATTR_WIDTH);
        	
        	if(this.sourceListWidth == DEFAULT_LIST_WIDTH) {
                this.sourceListWidth = this.sourceWidth;
            }
        }
        
        if (ComponentUtil.isNotBlank(this.sourceWidth) && (this.sourceWidth != DEFAULT_LIST_WIDTH)) {
            if(!this.sourceWidth.endsWith(Constants.PIXEL)){
                try {
                    int intValue = Integer.parseInt(this.sourceWidth);
                    this.sourceWidth = String.valueOf(intValue)+Constants.PIXEL; 
                } catch (NumberFormatException e) {
                    this.sourceListWidth = DEFAULT_LIST_WIDTH;
                }
            }
        }

        this.sourceDefaultLabel = attributeMap.getString("defaultLabel"); //$NON-NLS-1$
        this.sourceValue = attributeMap.getString( RichFaces.ATTR_VALUE); 

        this.sourceButtonStyle = attributeMap.getString("buttonStyle"); //$NON-NLS-1$

        final String sourceStyleClasess = attributeMap.getString(RichFaces.ATTR_STYLE_CLASS);

        if (ComponentUtil.isNotBlank(sourceStyleClasess)) {
            styleClasess.put(SECOND_DIV, styleClasess.get(SECOND_DIV) + " " + sourceStyleClasess); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        }

        this.sourceStyle = attributeMap.getString(HTML.ATTR_STYLE);
        this.sourceInputStyle = attributeMap.getString("inputStyle"); //$NON-NLS-1$
        this.sourceInputClass = attributeMap.getString("inputClass"); //$NON-NLS-1$
        this.sourceListClass = attributeMap.getString("listClass"); //$NON-NLS-1$
        this.sourceListStyle = attributeMap.getString("listStyle"); //$NON-NLS-1$
        this.sourceItemClass = attributeMap.getString("itemClass"); //$NON-NLS-1$
        
        this.sourceButtonIcon = attributeMap.getString(BUTTON_ICON); //$NON-NLS-1$
        this.sourceButtonIconInactive = attributeMap.getString("buttonIconInactive"); //$NON-NLS-1$
        this.sourceButtonIconDisabled = attributeMap.getString("buttonIconDisabled"); //$NON-NLS-1$
        this.disabled = (attributeMap.getBoolean("disabled") == Boolean.TRUE); //$NON-NLS-1$

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
    	if (disabled) {
    		isToggle = false;
    	} else {
    		isToggle = !isToggle;
    	}

    }
}
