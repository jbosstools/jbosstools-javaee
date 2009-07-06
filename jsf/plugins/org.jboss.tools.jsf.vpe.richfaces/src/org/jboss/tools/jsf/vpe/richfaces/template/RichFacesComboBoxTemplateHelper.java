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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import org.jboss.tools.jsf.vpe.richfaces.ComponentUtil;
import org.jboss.tools.jsf.vpe.richfaces.template.util.AttributeMap;
import org.jboss.tools.jsf.vpe.richfaces.template.util.RichFaces;
import org.jboss.tools.vpe.editor.VpeVisualDomBuilder;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.util.Constants;
import org.jboss.tools.vpe.editor.util.HTML;
import org.jboss.tools.vpe.editor.util.VpeStyleUtil;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMNode;
import org.w3c.dom.Element;
import org.w3c.dom.Node;



/**
 * @author Eugene Stherbin
 * @author yradtsevich
 */
public class RichFacesComboBoxTemplateHelper {
	private static final WeakHashMap<Node, Object> expandedComboBoxes = new WeakHashMap<Node, Object>();
	private static final String DISABLED_ATTR_NAME = "disabled"; //$NON-NLS-1$
	private static final String BUTTON_ICON_CLASSES_DISABLED = 
		"rich-combobox-font-inactive rich-combobox-button-icon-disabled rich-combobox-button-inactive"; //$NON-NLS-1$
	private static final String BUTTON_ICON_CLASSES = 
		"rich-combobox-font-inactive rich-combobox-button-icon-inactive rich-combobox-button-inactive"; //$NON-NLS-1$
	private static final String SECOND_DIV = "secondDiv"; //$NON-NLS-1$
	private static final String THIRD_DIV = "thirdDiv"; //$NON-NLS-1$
	private static final String THIRD_EMPTY_DIV = "thirdEmptyDiv"; //$NON-NLS-1$
	private static final String TEXT_FIELD = "textField"; //$NON-NLS-1$
	private static final String BUTTON_ICON = "buttonIcon"; //$NON-NLS-1$

	/** CSS_FILE_NAME. */
    private static final String CSS_FILE_NAME = "comboBox/comboBox.css"; //$NON-NLS-1$

    /** The Constant DEFAULT_LIST_WIDTH. */
    private static final String DEFAULT_LIST_WIDTH = "150px"; //$NON-NLS-1$

    /** IMAGE_NAME_DOWN. */
    private static final String IMAGE_NAME_DOWN = "/comboBox/down.gif"; //$NON-NLS-1$

    /** The Constant BUTTON_BACKGROUND. */
    private static final String BUTTON_BACKGROUND = "buttonBackground"; //$NON-NLS-1$

    /** The Constant STYLE_EXT. */
    private static final String STYLE_EXT = "richFacesComboBox"; //$NON-NLS-1$

    private static final int LIST_ITEM_HEIGHT_DEFAULT_VALUE = 18;

    /** The style clasess. */
    private final Map<String, String> styleClasess = new HashMap<String, String>();

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
    private boolean expanded;

	private final VpePageContext pageContext;
	private final Node sourceNode;
	private final nsIDOMDocument visualDocument;
	private final VpeCreationData vpeCreationData;

    /**
     * The Constructor.
     */
    public RichFacesComboBoxTemplateHelper(final VpePageContext pageContext, final Node sourceNode,
    		final nsIDOMDocument visualDocument) {
    	this.pageContext = pageContext;
    	this.sourceNode = sourceNode;
    	this.visualDocument = visualDocument;
        initDefaultClasses();
        vpeCreationData = create();
    }

    /**
     * Calculate with for div.
     *
     * @param with the with
     * @param minus the minus
     *
     * @return the string
     */
    private static String calculateWithForDiv(final String with, final int minus) {
        try {
            Integer intValue = 0;
            if (with.endsWith("px")) { //$NON-NLS-1$
                intValue = Integer.parseInt(with.substring(0, with.length() - 2));
            } else {
                intValue = Integer.parseInt(with);
            }
            return String.valueOf((intValue - minus)) + "px"; //$NON-NLS-1$
        } catch (final NumberFormatException e) {
            return with;
        }

    }
    public  VpeCreationData getVpeCreationData() {
    	return vpeCreationData;
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
    private VpeCreationData create() {
        ComponentUtil.setCSSLink(pageContext, CSS_FILE_NAME, STYLE_EXT);

        final Element source = (Element) sourceNode;

        prepareData(source);
        final nsIDOMElement rootDiv = visualDocument.createElement(HTML.TAG_DIV);

        //Fix  https://jira.jboss.org/jira/browse/JBIDE-2430 issue with resizement.
        rootDiv.setAttribute(HTML.ATTR_STYLE, 
        		HTML.STYLE_PARAMETER_WIDTH + Constants.COLON+sourceWidth);
        final nsIDOMElement comboBoxDiv = visualDocument.createElement(HTML.TAG_DIV);
        final nsIDOMElement secondDiv = visualDocument.createElement(HTML.TAG_DIV);

        // Commented because of not working alignment in RichFaces implementation
        // comboBoxDiv.setAttribute(HTML.ATTR_ALIGN, this.sourceAlign);
        // secondDiv.setAttribute(HTML.ATTR_ALIGN, this.sourceAlign);


        //comboBoxDiv.setAttribute(HTML.ATTR_CLASS, styleClasess.get("secondDiv")); //$NON-NLS-1$
        secondDiv.setAttribute(HTML.ATTR_CLASS, styleClasess.get(SECOND_DIV));
        String secondDivSubStyle = "; position: {0}; z-index: {1} ;"; //$NON-NLS-1$

        if (expanded) {
            secondDivSubStyle = MessageFormat.format(secondDivSubStyle, "relative", "2"); //$NON-NLS-1$ //$NON-NLS-2$
        } else {
            secondDivSubStyle = MessageFormat.format(secondDivSubStyle, "static", "0"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        comboBoxDiv.setAttribute(HTML.ATTR_STYLE, HTML.STYLE_PARAMETER_WIDTH + Constants.COLON + sourceListWidth
                + Constants.SEMICOLON + secondDivSubStyle);
        secondDiv.setAttribute(HTML.ATTR_STYLE, HTML.STYLE_PARAMETER_WIDTH + Constants.COLON + sourceListWidth
                + Constants.SEMICOLON + secondDivSubStyle + sourceStyle);
        final nsIDOMElement thirdDiv = visualDocument.createElement(HTML.TAG_DIV);
        thirdDiv.setAttribute(HTML.ATTR_CLASS, styleClasess.get(THIRD_DIV));
        thirdDiv.setAttribute(HTML.ATTR_STYLE, HTML.STYLE_PARAMETER_WIDTH + Constants.COLON + sourceWidth
                + "; z-index: 1;"); //$NON-NLS-1$
        final nsIDOMElement textField = visualDocument.createElement(HTML.TAG_INPUT);
        textField.setAttribute(HTML.ATTR_TYPE, HTML.VALUE_TYPE_TEXT);

        textField.setAttribute(HTML.ATTR_CLASS, 
        		styleClasess.get(TEXT_FIELD) + Constants.WHITE_SPACE + sourceInputClass);
        textField.setAttribute("autocomplete", "off"); //$NON-NLS-1$ //$NON-NLS-2$
        textField.setAttribute(HTML.ATTR_STYLE, 
        		HTML.STYLE_PARAMETER_WIDTH + Constants.COLON + calculateWithForDiv(sourceWidth, 17)
        		+ Constants.SEMICOLON + sourceInputStyle);
        String value = null;
        if (ComponentUtil.isNotBlank(sourceDefaultLabel)) {
            value = sourceDefaultLabel;
        } else if (ComponentUtil.isNotBlank(sourceValue) && ComponentUtil.isBlank(sourceDefaultLabel)) {
            value = sourceValue;
        }

        if (value != null) {
            textField.setAttribute(RichFaces.ATTR_VALUE, value);
        }
        final nsIDOMElement buttonBackground = visualDocument.createElement(HTML.TAG_INPUT);
        buttonBackground.setAttribute(HTML.ATTR_TYPE, HTML.VALUE_TYPE_TEXT);

        if (disabled) {
        	styleClasess.put(BUTTON_ICON, BUTTON_ICON_CLASSES_DISABLED);
        } else {
        	styleClasess.put(BUTTON_ICON, BUTTON_ICON_CLASSES);
        }

        buttonBackground.setAttribute(HTML.ATTR_CLASS, styleClasess.get(BUTTON_BACKGROUND));
        buttonBackground.setAttribute(HTML.ATTR_READONLY, Constants.TRUE);
        buttonBackground.setAttribute(RichFacesAbstractInplaceTemplate.VPE_USER_TOGGLE_ID_ATTR, String.valueOf(0));
        if (sourceButtonStyle != null) {
            buttonBackground.setAttribute(HTML.ATTR_STYLE, sourceButtonStyle);
        }

        final nsIDOMElement buttonIcon = visualDocument.createElement(HTML.TAG_INPUT);
        buttonIcon.setAttribute(HTML.ATTR_TYPE, HTML.VALUE_TYPE_TEXT);
        ;
        buttonIcon.setAttribute(HTML.ATTR_CLASS, styleClasess.get(BUTTON_ICON));
        buttonIcon.setAttribute(HTML.ATTR_READONLY, Constants.TRUE);
        buttonIcon.setAttribute(RichFacesAbstractInplaceTemplate.VPE_USER_TOGGLE_ID_ATTR, String.valueOf(0));
        if (sourceButtonStyle != null) {
            buttonIcon.setAttribute(HTML.ATTR_STYLE, sourceButtonStyle);
        }

        String actualSourceButton;
        if (disabled) {
        	actualSourceButton = sourceButtonIconDisabled;
        } else if (expanded) {
        	actualSourceButton = sourceButtonIcon;
        } else {
        	actualSourceButton = sourceButtonIconInactive;
        }

        if (ComponentUtil.isNotBlank(actualSourceButton) && (actualSourceButton != IMAGE_NAME_DOWN)) {
        	String buttonIconPath = VpeStyleUtil.addFullPathToImgSrc(actualSourceButton, pageContext, true);
        	buttonIconPath = buttonIconPath.replace('\\', '/');
    		final String style = "background-image: url(" + buttonIconPath + ")"; //$NON-NLS-1$ //$NON-NLS-2$
            buttonIcon.setAttribute(HTML.ATTR_STYLE, buttonIcon.getAttribute(HTML.ATTR_STYLE) + style);
        }
        final nsIDOMElement forthEmptyDiv = visualDocument.createElement(HTML.TAG_DIV);
        forthEmptyDiv.setAttribute(HTML.ATTR_CLASS, styleClasess.get("forthEmptyDiv")); //$NON-NLS-1$
        forthEmptyDiv.setAttribute(HTML.ATTR_STYLE, HTML.STYLE_PARAMETER_WIDTH + Constants.COLON
                + calculateWithForDiv(sourceWidth, 10));
        forthEmptyDiv.appendChild(visualDocument.createTextNode("Struts")); //$NON-NLS-1$

        rootDiv.appendChild(comboBoxDiv);
        comboBoxDiv.appendChild(secondDiv);

        secondDiv.appendChild(thirdDiv);
        if (expanded) {
        	comboBoxDiv.appendChild(createToogleDiv());
        }
        thirdDiv.appendChild(textField);
        thirdDiv.appendChild(buttonBackground);
        thirdDiv.appendChild(buttonIcon);
        thirdDiv.appendChild(forthEmptyDiv);

        final VpeCreationData creationData = new VpeCreationData(rootDiv);

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
    private nsIDOMNode createToogleDiv() {

        final nsIDOMElement thirdEmptyDiv = visualDocument.createElement(HTML.TAG_DIV);

        thirdEmptyDiv.setAttribute(HTML.ATTR_STYLE, sourceListStyle + Constants.SEMICOLON
                + " z-index: 3; position: absolute; visibility: visible; top: 16px; left: 0px;"); //$NON-NLS-1$
        thirdEmptyDiv.setAttribute(HTML.ATTR_CLASS, 
        		styleClasess.get(THIRD_EMPTY_DIV) + " " + sourceListClass); //$NON-NLS-1$
        thirdEmptyDiv.setAttribute(HTML.ATTR_STYLE, 
        		"z-index: 3; position: absolute; visibility: visible; top: 16px; left: 0px;"); //$NON-NLS-1$

        final nsIDOMElement shadovDiv = visualDocument.createElement(HTML.TAG_DIV);

        final nsIDOMElement positionDiv = visualDocument.createElement(HTML.TAG_DIV);

        positionDiv.setAttribute(HTML.ATTR_CLASS, "rich-combobox-list-position"); //$NON-NLS-1$

        final nsIDOMElement decorationDiv = visualDocument.createElement(HTML.TAG_DIV);

        decorationDiv.setAttribute(HTML.ATTR_CLASS, "rich-combobox-list-decoration"); //$NON-NLS-1$
        // decorationDiv.setAttribute(HTML.ATTR_STYLE,
        // "height: 54px; width: 208px;");

        final nsIDOMElement scrollDiv = visualDocument.createElement(HTML.TAG_DIV);
        scrollDiv.setAttribute(HTML.ATTR_CLASS, "rich-combobox-list-scroll"); //$NON-NLS-1$
        final List<Element> items = ComponentUtil.getSelectItems(sourceNode.getChildNodes());
        int defaultHeight = LIST_ITEM_HEIGHT_DEFAULT_VALUE;

        if((items!=null) && (items.size() > 1)){
            defaultHeight = ((items.size() - 1)* LIST_ITEM_HEIGHT_DEFAULT_VALUE);
        }

        final String listHeight = ComponentUtil.isNotBlank(sourceListHeight) 
        	? sourceListHeight 
        	: String.valueOf(defaultHeight) + "px";  //$NON-NLS-1$
        scrollDiv.setAttribute(HTML.ATTR_STYLE, HTML.STYLE_PARAMETER_MAX_HEIGHT
				+ Constants.COLON + listHeight + Constants.SEMICOLON
				+ HTML.STYLE_PARAMETER_WIDTH + Constants.COLON
				+ calculateWithForDiv(sourceListWidth, 2));

        final List<Element> selectItems = ComponentUtil.getSelectItems(sourceNode.getChildNodes());

        if (selectItems.size() > 0) {
            for (final Element e : selectItems) {
                scrollDiv.appendChild(createSelectItem(e));
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
        } catch (final NumberFormatException e) {
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
    private nsIDOMNode createSelectItem(final Element e) {
        final nsIDOMElement item = visualDocument.createElement(HTML.TAG_SPAN);

        item.setAttribute(HTML.ATTR_CLASS, "rich-combobox-item " + sourceItemClass); //$NON-NLS-1$
        item.appendChild(visualDocument.createTextNode(ComponentUtil.getSelectItemValue(e)));
        return item;
    }

    /**
     * Inits the default classes.
     */
    private void initDefaultClasses() {
        styleClasess.put(SECOND_DIV, "rich-combobox-font rich-combobox"); //$NON-NLS-1$
        styleClasess.put(THIRD_DIV, "rich-combobox-font rich-combobox-shell"); //$NON-NLS-1$
        styleClasess.put(THIRD_EMPTY_DIV, "rich-combobox-list-cord"); //$NON-NLS-1$
        styleClasess.put(TEXT_FIELD, "rich-combobox-font-disabled rich-combobox-input-inactive"); //$NON-NLS-1$
       	styleClasess.put(BUTTON_BACKGROUND, 
       			"rich-combobox-font-inactive rich-combobox-button-background rich-combobox-button-inactive"); //$NON-NLS-1$
       	styleClasess.put(BUTTON_ICON, BUTTON_ICON_CLASSES);
        styleClasess.put("forthEmptyDiv", "rich-combobox-strut rich-combobox-font"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * Prepare data.
     *
     * @param source the source
     */
    private void prepareData(final Element source) {
    	final AttributeMap attributeMap = new AttributeMap(source);

        // Commented because of not working alignment in RichFaces implementation
    	// if (attributeMap.isBlank(RichFaces.ATTR_ALIGN)) {
    	// 	this.sourceAlign = DEFAULT_ALIGN;
    	// } else {
    	// 	this.sourceAlign = attributeMap.getString(RichFaces.ATTR_ALIGN);
    	// }

        if (attributeMap.isBlank(RichFaces.ATTR_LIST_WIDTH)) {
        	 sourceListWidth = DEFAULT_LIST_WIDTH;
        } else {
        	sourceListWidth = attributeMap.getString(RichFaces.ATTR_LIST_WIDTH);
        }

        sourceListHeight = attributeMap.getString(RichFaces.ATTR_LIST_HEIGHT);

        if (attributeMap.isBlank(RichFaces.ATTR_WIDTH)) {
        	sourceWidth = DEFAULT_LIST_WIDTH;
        } else {
        	sourceWidth = attributeMap.getString(RichFaces.ATTR_WIDTH);

        	if(sourceListWidth == DEFAULT_LIST_WIDTH) {
                sourceListWidth = sourceWidth;
            }
        }

        if (ComponentUtil.isNotBlank(sourceWidth) && (sourceWidth != DEFAULT_LIST_WIDTH)) {
            if(!sourceWidth.endsWith(Constants.PIXEL)){
                try {
                    final int intValue = Integer.parseInt(sourceWidth);
                    sourceWidth = String.valueOf(intValue)+Constants.PIXEL;
                } catch (final NumberFormatException e) {
                	sourceWidth = DEFAULT_LIST_WIDTH;
                    sourceListWidth = DEFAULT_LIST_WIDTH;
                }
            }
        }

        sourceDefaultLabel = attributeMap.getString("defaultLabel"); //$NON-NLS-1$
        sourceValue = attributeMap.getString( RichFaces.ATTR_VALUE);

        sourceButtonStyle = attributeMap.getString("buttonStyle"); //$NON-NLS-1$

        final String sourceStyleClasess = attributeMap.getString(RichFaces.ATTR_STYLE_CLASS);

        if (ComponentUtil.isNotBlank(sourceStyleClasess)) {
            styleClasess.put(SECOND_DIV, styleClasess.get(SECOND_DIV) + " " + sourceStyleClasess); //$NON-NLS-1$
        }

        sourceStyle = attributeMap.getString(HTML.ATTR_STYLE);
        sourceInputStyle = attributeMap.getString("inputStyle"); //$NON-NLS-1$
        sourceInputClass = attributeMap.getString("inputClass"); //$NON-NLS-1$
        sourceListClass = attributeMap.getString("listClass"); //$NON-NLS-1$
        sourceListStyle = attributeMap.getString("listStyle"); //$NON-NLS-1$
        sourceItemClass = attributeMap.getString("itemClass"); //$NON-NLS-1$

        sourceButtonIcon = attributeMap.getString(BUTTON_ICON);
        sourceButtonIconInactive = attributeMap.getString("buttonIconInactive"); //$NON-NLS-1$
        sourceButtonIconDisabled = attributeMap.getString("buttonIconDisabled"); //$NON-NLS-1$
        disabled = isDisabled(attributeMap);
       	expanded = isExpanded(source);

        if(ComponentUtil.isBlank(sourceButtonIcon)){
            sourceButtonIcon = IMAGE_NAME_DOWN;
        }

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
    protected static void setUpImg(final nsIDOMElement img, final int width, final int height,
    		final int border, final String image) {
        ComponentUtil.setImg(img, image);
        img.setAttribute(HTML.ATTR_WIDTH, String.valueOf(width));
        img.setAttribute(HTML.ATTR_HEIGHT, String.valueOf(height));
        img.setAttribute(HTML.ATTR_BORDER, String.valueOf(border));

    }

    /**
     * Inverts <code>expanded</code> state of the ComboBox
     *
     * @param builder the builder
     * @param sourceNode the source node
     * @param toggleId the toggle id
     */
    public static void toggle(final VpeVisualDomBuilder builder, final Node sourceNode, final String toggleId) {
    	AttributeMap attributes = new AttributeMap((Element)sourceNode);
    	if (isDisabled(attributes) || isExpanded(sourceNode)) {
    		expandedComboBoxes.remove(sourceNode);
    	} else {
    		expandedComboBoxes.put(sourceNode, null);
    	}
    }

	public static void stopToggling(final Node sourceNode) {
		expandedComboBoxes.remove(sourceNode);
	}

    public static boolean isDisabled(final AttributeMap attributes) {
   		return attributes.getBoolean(DISABLED_ATTR_NAME) == Boolean.TRUE;
    }

	private static boolean isExpanded(Node node) {
		return expandedComboBoxes.containsKey(node);
	}
}
