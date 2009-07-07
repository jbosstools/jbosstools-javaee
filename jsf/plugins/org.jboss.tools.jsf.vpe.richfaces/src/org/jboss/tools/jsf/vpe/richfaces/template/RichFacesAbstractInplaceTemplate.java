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

import java.util.HashMap;
import java.util.Map;

import org.jboss.tools.jsf.vpe.richfaces.ComponentUtil;
import org.jboss.tools.jsf.vpe.richfaces.template.util.RichFaces;
import org.jboss.tools.jst.jsp.outline.cssdialog.common.Constants;
import org.jboss.tools.vpe.editor.VpeVisualDomBuilder;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeChildrenInfo;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.template.VpeToggableTemplate;
import org.jboss.tools.vpe.editor.util.HTML;
import org.jboss.tools.vpe.editor.util.VpeStyleUtil;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMText;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Base class for both {@link RichFacesInplaceInputTemplate} and {@link
 * RichFacesInplaceSelectTemplate}.
 * 
 * @author Eugene Stherbin
 * @see RichFacesInplaceInputTemplate
 * @see RichFacesInplaceSelectTemplate
 */
public abstract class RichFacesAbstractInplaceTemplate extends AbstractRichFacesTemplate implements VpeToggableTemplate {

    /** The Constant APPLY_BUTTON_GIF. */
    protected static final String APPLY_BUTTON_GIF = "/applyButton.gif"; //$NON-NLS-1$

    /** The Constant CANCEL_BUTTON_GIF. */
    protected static final String CANCEL_BUTTON_GIF = "/cancelButton.gif"; //$NON-NLS-1$

    /** The Constant controlsVerticalPositions. */
    protected final Map<String, String> controlsVerticalPositions = new HashMap<String, String>();

    /** The Constant DEFAULT_INPUT_WIDTH_VALUE. */
    protected static final String DEFAULT_INPUT_WIDTH_VALUE = "66px"; //$NON-NLS-1$

    /** The Constant DEFAULT_VERTICAL_POSITION. */
    protected static final String DEFAULT_VERTICAL_POSITION = null;

    /** The Constant defaultButtonImages. */
    protected static final Map<String, String> defaultButtonImages = new HashMap<String, String>();

    /** The default style classes. */
    protected static final Map<String, String> defaultStyleClasses = new HashMap<String, String>();

    /** The Constant RICH_INPLACE_VIEW_DEFAULT_STYLE_CLASS. */
    protected static final String RICH_INPLACE_VIEW_DEFAULT_STYLE_CLASS = "rich-inplace-view"; //$NON-NLS-1$

    /** The Constant VPE_USER_TOGGLE_ID_ATTR. */
    public static final String VPE_USER_TOGGLE_ID_ATTR = "vpe-user-toggle-id"; //$NON-NLS-1$

    /** The button images. */
    protected final Map<String, String> buttonImages = new HashMap<String, String>();


    /** The controls horizontal positions. */
    protected final Map<String, String> controlsHorizontalPositions = new HashMap<String, String>();

    /** The is show input. */
    protected boolean isToggle = false;
    

    /** The Constant SPACER_GIF. */
    protected final String SPACER_GIF = getCssExtension() + "/spacer.gif"; //$NON-NLS-1$

    protected final String DEFAULT_LABEL_VALUE = "\u00A0\u00A0\u00A0"; //$NON-NLS-1$
    
    protected String sourceCancelButtonIcon;
    protected String sourceApplyButtonIcon;

    /**
     * The Constructor.
     */
    public RichFacesAbstractInplaceTemplate() {
        super();
        initDefaultStyleClasses();
        initDefaultButtonImages();
        initPositions();
    }

    /**
     * Creates the root span template method.
     *
     * @param visualDocument the visual document
     * @param source the source
     * @return the ns IDOM element
     */
    protected nsIDOMElement createRootSpanTemplateMethod(Element source, nsIDOMDocument visualDocument, Attributes attrs) {
        final nsIDOMElement rootSpan = visualDocument.createElement(HTML.TAG_SPAN);
        rootSpan.setAttribute(VPE_USER_TOGGLE_ID_ATTR, String.valueOf(this.isToggle));
        String rootStyleClass = "rich-inplace" + getCssStylesSuffix(); //$NON-NLS-1$
        for (String sc : getRootSpanClasses(attrs)) {
            if (ComponentUtil.isNotBlank(sc)) {
            	rootStyleClass += Constants.WHITE_SPACE + sc;
            }
        }
        rootSpan.setAttribute(HTML.ATTR_CLASS, rootStyleClass);
        String style = Constants.EMPTY;
        if (this.isToggle) {
            style = "position: relative;"; //$NON-NLS-1$
        }
        rootSpan.setAttribute(HTML.ATTR_STYLE, style + "; display: " + attrs.getLayout() + Constants.SEMICOLON); //$NON-NLS-1$
        return rootSpan;
    }

    /**
     * Gets the css extension.
     *
     * @return the css extension
     */
    protected abstract String getCssExtension();

    /**
     * Gets the css style.
     *
     * @return the css style
     */
    protected abstract String getCssStyle();

    /**
     * Gets the css styles suffix.
     *
     * @return the css styles suffix
     */
    protected abstract String getCssStylesSuffix();

    /**
     * Gets the root span classes.
     *
     * @return the root span classes
     */
    protected abstract String[] getRootSpanClasses(Attributes attrs);

    /**
     * Gets the value.
     *
     * @return the value
     */
    protected String getValue(Attributes attrs) {
    	String rst = Constants.EMPTY;
    	if (ComponentUtil.isNotBlank(attrs.getDefaultLabel())) {
    		rst = attrs.getDefaultLabel();
    	} else if (ComponentUtil.isBlank(attrs.getDefaultLabel())
    			&& ComponentUtil.isNotBlank(attrs.getValue())) {
    		rst = attrs.getValue();
    	} else if (isToggle) {
    		rst = Constants.WHITE_SPACE;
    	} else {
    		rst = DEFAULT_LABEL_VALUE;
    	}
    	return rst;
    }

    /**
     * Initialize the default button images.
     */
    protected void initDefaultButtonImages() {
        if (defaultButtonImages.isEmpty()) {
            defaultButtonImages.put("cancelControlIcon", getCssExtension() + CANCEL_BUTTON_GIF); //$NON-NLS-1$
            defaultButtonImages.put("saveControlIcon", getCssExtension() + APPLY_BUTTON_GIF); //$NON-NLS-1$
        }
    }

    /**
     * Initialize the default style classes.
     */
    protected void initDefaultStyleClasses() {
        if (defaultStyleClasses.isEmpty()) {
            defaultStyleClasses.put("rootSpan", "rich-inplace" + getCssStylesSuffix() + " {0} {1}"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        }
    }

    /**
     * Initialize the positions.
     */
    protected void initPositions() {
        if (controlsVerticalPositions.isEmpty()) {
            controlsVerticalPositions.put(HTML.VALUE_ALIGN_BOTTOM, "18px"); //$NON-NLS-1$
            controlsVerticalPositions.put(HTML.VALUE_ALIGN_TOP, "-12px"); //$NON-NLS-1$
            controlsVerticalPositions.put(HTML.VALUE_ALIGN_CENTER, "0px"); //$NON-NLS-1$
        }
        if (controlsHorizontalPositions.isEmpty()) {
            controlsHorizontalPositions.put(HTML.VALUE_ALIGN_LEFT, "0px"); //$NON-NLS-1$
            controlsHorizontalPositions.put(HTML.VALUE_ALIGN_CENTER, "53px"); //$NON-NLS-1$
        }
    }

    /**
     * Prepare data.
     *
     * @param source the source
     */
    protected void prepareData(VpePageContext pageContext, Element source) {
        prepareImages(source);
    }

    /**
     * Prepare images.
     *
     * @param source the source
     */
    protected void prepareImages(Element source) {
        for (String key : defaultButtonImages.keySet()) {
            String value = ComponentUtil.getAttribute(source, key);
            if(ComponentUtil.isNotBlank(value)) {
                this.buttonImages.put(key, value);
            } else {
                this.buttonImages.put(key, defaultButtonImages.get(key));
            }
        }
    }

    /**
     * Sets the up image.
     *
     * @param img the img
     * @param width the width
     * @param height the height
     * @param border the border
     * @param image the image
     */
    protected void setUpImg(nsIDOMElement img, int width, int height, int border, String image) {
        ComponentUtil.setImg(img, image);
        img.setAttribute(HTML.ATTR_WIDTH, String.valueOf(width));
        img.setAttribute(HTML.ATTR_HEIGHT, String.valueOf(height));
        img.setAttribute(HTML.ATTR_BORDER, String.valueOf(border));
    }

    /**
     * Sets the up span root.
     *
     * @param visualDocument the visual document
     * @param spanRoot the span root
     * @param source the source
     */
    protected void setUpSpanRoot(nsIDOMElement spanRoot, Element source, nsIDOMDocument visualDocument, Attributes attrs) {
        if (attrs.getStyleClass().length() > 0) {
            spanRoot.setAttribute(HTML.ATTR_CLASS, attrs.getStyleClass());
        } else {
            spanRoot.setAttribute(HTML.ATTR_CLASS, RICH_INPLACE_VIEW_DEFAULT_STYLE_CLASS);
        }
        String value = Constants.WHITE_SPACE;
        if (attrs.getValue().length() > 0) {
            value = attrs.getValue();
        }
        final nsIDOMText text = visualDocument.createTextNode(value);
        spanRoot.appendChild(text);
    }

    /**
     * Stop toggling.
     *
     * @param sourceNode the source node
     * @see org.jboss.tools.vpe.editor.template.VpeToggableTemplate#stopToggling(org.w3c.dom.Node)
     */
    public void stopToggling(Node sourceNode) {
        this.isToggle = false;
    }

    /**
     * Toggle.
     *
     * @param builder the builder
     * @param sourceNode the source node
     * @param toggleId the toggle id
     *
     * @see org.jboss.tools.vpe.editor.template.VpeToggableTemplate#toggle(
     * 		org.jboss.tools.vpe.editor.VpeVisualDomBuilder, org.w3c.dom.Node, java.lang.String)
     */
    public void toggle(VpeVisualDomBuilder builder, Node sourceNode, String toggleId) {
        isToggle = !isToggle;
    }

    protected abstract String getCssStylesControlSuffix(); 

    protected abstract String getControlPositionsSubStyles(Attributes attrs);

    protected abstract String getMainControlsDivCssClass();

    /**
     * Creates the controls div.
     *
     * @param visualDocument the visual document
     * @param sourceNode the source node
     * @param pageContext the page context
     * @param creationData the VpeCreationData object
     * @return the ns IDOM element
     */
    protected nsIDOMElement createControlsDiv(VpePageContext pageContext,
	    Node sourceNode, nsIDOMDocument visualDocument,
	    VpeCreationData creationData, Attributes attrs) {
        final nsIDOMElement element = visualDocument.createElement(HTML.TAG_DIV);

        element.setAttribute(HTML.ATTR_CLASS, getMainControlsDivCssClass());
        element.setAttribute(HTML.ATTR_STYLE, "position: absolute; " + getControlPositionsSubStyles(attrs)); //$NON-NLS-1$
        final nsIDOMElement divShadov = visualDocument.createElement(HTML.TAG_DIV);

        divShadov.setAttribute(HTML.ATTR_CLASS, "rich-inplace" + getCssStylesSuffix() + "-shadow"); //$NON-NLS-1$ //$NON-NLS-2$
        final nsIDOMElement divShadovTable = visualDocument.createElement(HTML.TAG_TABLE);
        divShadovTable.setAttribute(HTML.ATTR_CELLPADDING, "0"); //$NON-NLS-1$
        divShadovTable.setAttribute(HTML.ATTR_CELLSPACING, "0"); //$NON-NLS-1$
        divShadovTable.setAttribute(HTML.ATTR_BORDER, "0"); //$NON-NLS-1$
        final nsIDOMElement divShadovTBody = visualDocument.createElement(HTML.TAG_TBODY);
        final nsIDOMElement divShadovTr1 = visualDocument.createElement(HTML.TAG_TR);
        final nsIDOMElement divShadovTr2 = visualDocument.createElement(HTML.TAG_TR);
        final nsIDOMElement divShadovTd1 = visualDocument.createElement(HTML.TAG_TD);
        final nsIDOMElement divShadovTd2 = visualDocument.createElement(HTML.TAG_TD);
        final nsIDOMElement divShadovTd1Tr2 = visualDocument.createElement(HTML.TAG_TD);
        final nsIDOMElement divShadovTd2Tr2 = visualDocument.createElement(HTML.TAG_TD);

        final nsIDOMElement td1Img = visualDocument.createElement(HTML.TAG_IMG);
        final nsIDOMElement td2Img = visualDocument.createElement(HTML.TAG_IMG);
        final nsIDOMElement td3Img = visualDocument.createElement(HTML.TAG_IMG);
        final nsIDOMElement td4Img = visualDocument.createElement(HTML.TAG_IMG);
        setUpImg(td1Img, 10, 1, 0, SPACER_GIF);
        setUpImg(td2Img, 1, 10, 0, SPACER_GIF);
        setUpImg(td3Img, 1, 10, 0, SPACER_GIF);
        setUpImg(td4Img, 10, 1, 0, SPACER_GIF);
        divShadovTd1.setAttribute(HTML.ATTR_CLASS, "rich-inplace" + getCssStylesSuffix() + "-shadow-tl"); //$NON-NLS-1$ //$NON-NLS-2$
        divShadovTd2.setAttribute(HTML.ATTR_CLASS, "rich-inplace" + getCssStylesSuffix() + "-shadow-tr"); //$NON-NLS-1$ //$NON-NLS-2$

        divShadovTd1Tr2.setAttribute(HTML.ATTR_CLASS, "rich-inplace" + getCssStylesSuffix() + "-shadow-bl"); //$NON-NLS-1$ //$NON-NLS-2$
        divShadovTd2Tr2.setAttribute(HTML.ATTR_CLASS, "rich-inplace" + getCssStylesSuffix() + "-shadow-br"); //$NON-NLS-1$ //$NON-NLS-2$

        final nsIDOMElement divButtons = visualDocument.createElement(HTML.TAG_DIV);
        divButtons.setAttribute(HTML.ATTR_STYLE, "position: relative; height: 18px;"); //$NON-NLS-1$

        /*
         * Encoding controls facet
         */
        Element facetElement = ComponentUtil.getFacetElement((Element) sourceNode, "controls", true); //$NON-NLS-1$
        if (facetElement != null) {
        	VpeChildrenInfo childrenInfo = new VpeChildrenInfo(divButtons);
        	childrenInfo.addSourceChild(facetElement);
        	creationData.addChildrenInfo(childrenInfo);
        } else {
		    // Create "Apply" button
		    final nsIDOMElement applyButtonImg = visualDocument.createElement(HTML.TAG_INPUT);
		    applyButtonImg.setAttribute(HTML.ATTR_TYPE, HTML.VALUE_TYPE_IMAGE);
	
		    String applyButtonClass = "rich-inplace" + getCssStylesSuffix() + "-control"; //$NON-NLS-1$ //$NON-NLS-2$
		    if (ComponentUtil.isNotBlank(attrs.getControlClass())) {
		    	applyButtonClass += Constants.EMPTY + attrs.getControlClass();
		    }
		    applyButtonImg.setAttribute(HTML.ATTR_CLASS, applyButtonClass);
	
		    final String saveControlIconImg = buttonImages.get("saveControlIcon"); //$NON-NLS-1$
		    if (defaultButtonImages.containsValue(saveControlIconImg)) {
				// Set default icon from resources
				ComponentUtil.setImg(applyButtonImg, saveControlIconImg);
		    } else {
		    	// Set custom user icon
				String imgFullPath = VpeStyleUtil.addFullPathToImgSrc(saveControlIconImg, pageContext, true);
				applyButtonImg.setAttribute(HTML.ATTR_SRC, imgFullPath);
		    }
		    applyButtonImg.setAttribute(VPE_USER_TOGGLE_ID_ATTR, String.valueOf(0));
	
		    // Create "Cancel" button
		    final nsIDOMElement cancelButtonImg = visualDocument.createElement(HTML.TAG_INPUT);
		    cancelButtonImg.setAttribute(HTML.ATTR_TYPE, HTML.VALUE_TYPE_IMAGE);
	
		    String cancelButtonClass = "rich-inplace" + getCssStylesSuffix() + "-control"; //$NON-NLS-1$ //$NON-NLS-2$
		    if (ComponentUtil.isNotBlank(attrs.getControlClass())) {
		    	cancelButtonClass += Constants.EMPTY + attrs.getControlClass();
		    }
		    cancelButtonImg.setAttribute(HTML.ATTR_CLASS, cancelButtonClass);
	
		    final String cancelControlIconImg = buttonImages.get("cancelControlIcon"); //$NON-NLS-1$
		    if (defaultButtonImages.containsValue(cancelControlIconImg)) {
				// Set default icon from resources
				ComponentUtil.setImg(cancelButtonImg, cancelControlIconImg);
		    } else {
		    	// Set custom user icon
				String imgFullPath = VpeStyleUtil.addFullPathToImgSrc(cancelControlIconImg, pageContext, true);
				cancelButtonImg.setAttribute(HTML.ATTR_SRC, imgFullPath);
		    }
		    cancelButtonImg.setAttribute(VPE_USER_TOGGLE_ID_ATTR, String.valueOf(0));
	
		    divButtons.appendChild(applyButtonImg);
		    divButtons.appendChild(cancelButtonImg);
	
		    /*
		     * Adding shadow to controls
		     */
		    element.appendChild(divShadov);
		    divShadov.appendChild(divShadovTable);
		    divShadovTable.appendChild(divShadovTBody);
		    divShadovTBody.appendChild(divShadovTr1);
		    divShadovTr1.appendChild(divShadovTd1);
		    divShadovTd1.appendChild(td1Img);
		    divShadovTr1.appendChild(divShadovTd2);
		    divShadovTd2.appendChild(td2Img);
		    
		    divShadovTBody.appendChild(divShadovTr2);
		    divShadovTr2.appendChild(divShadovTd1Tr2);
		    divShadovTd1Tr2.appendChild(td3Img);
		    divShadovTr2.appendChild(divShadovTd2Tr2);
		    divShadovTd2Tr2.appendChild(td4Img);
        }

        // Adding controls
        element.appendChild(divButtons);

        return element;
    }
    
    class Attributes {

	/*
	 * Attributes names
	 */
	private final String EDIT_CLASS = "editClass"; //$NON-NLS-1$
	private final String VIEW_CLASS = "viewClass"; //$NON-NLS-1$
	private final String CONTROL_CLASS = "controlClass"; //$NON-NLS-1$
	private final String SHOW_CONTROLS = "showControls"; //$NON-NLS-1$
	private final String LAYOUT = "layout"; //$NON-NLS-1$
	private final String CONTROLS_HORIZONTAL_POSOSITION = "controlsHorizontalPosition"; //$NON-NLS-1$
	private final String CONTROLS_VERTICAL_POSITION = "controlsVerticalPosition"; //$NON-NLS-1$
	private final String DEFAULT_LABEL = "defaultLabel"; //$NON-NLS-1$

	private final String DEFAULT_VALUE = Constants.WHITE_SPACE;

	/*
	 * Attributes
	 */
	private String editClass;
	private String viewClass;
	private String controlClass;
	private boolean showControls;
	private String layout;
	private String value;
	private String controlsHorizontalPosition;
	private String controlsVerticalPosition;
	private String defaultLabel;
	private String styleClass;

	public Attributes(final Element sourceElement) {
	    if (null == sourceElement) {
		return;
	    }
	    editClass = sourceElement.getAttribute(EDIT_CLASS);
	    viewClass = sourceElement.getAttribute(VIEW_CLASS);
	    controlClass = sourceElement.getAttribute(CONTROL_CLASS);
	    showControls = Boolean.parseBoolean(sourceElement.getAttribute(SHOW_CONTROLS));
	    layout = sourceElement.getAttribute(LAYOUT);
	    value = sourceElement.getAttribute(HTML.ATTR_VALUE);
	    controlsHorizontalPosition = sourceElement.getAttribute(CONTROLS_HORIZONTAL_POSOSITION);
	    controlsVerticalPosition = sourceElement.getAttribute(CONTROLS_VERTICAL_POSITION);
	    defaultLabel = sourceElement.getAttribute(DEFAULT_LABEL);
	    styleClass = sourceElement.getAttribute(RichFaces.ATTR_STYLE_CLASS);

	    /*
	     * Setting default value when needed.
	     */
	    if (ComponentUtil.isBlank(value)) {
		value = DEFAULT_VALUE;
	    }
	    if (ComponentUtil.isBlank(layout)
		    || (!(layout.equalsIgnoreCase(HTML.VALUE_INLINE)) 
			    && !(layout.equalsIgnoreCase(HTML.VALUE_BLOCK)))) {
		layout = HTML.VALUE_INLINE;
	    }
	    if (ComponentUtil.isBlank(controlsVerticalPosition)
		    || !isInKeySet(controlsVerticalPositions,
			    controlsVerticalPosition)) {
		this.controlsVerticalPosition = HTML.VALUE_ALIGN_CENTER;
	    }
	    if (ComponentUtil.isBlank(controlsHorizontalPosition)
		    || !isInKeySet(controlsHorizontalPositions,
			    controlsHorizontalPosition)) {
		this.controlsHorizontalPosition = HTML.VALUE_ALIGN_RIGHT;
	    }
	}


	/**
	 * Checks if the value is in the map's key set.
	 *
	 * @param map the map
	 * @param value the value
	 * @return true, if the value is in
	 */
	protected boolean isInKeySet(Map<String, String> map, String value) {
	    boolean rst = false;
	    for (String key : map.keySet()) {
		if (key.equalsIgnoreCase(value)) {
		    rst = true;
		    break;
		}
	    }
	    return rst;
	}

	public String getEditClass() {
	    return editClass;
	}

	public String getViewClass() {
	    return viewClass;
	}

	public String getLayout() {
	    return layout;
	}

	public String getControlClass() {
	    return controlClass;
	}

	public boolean isShowControls() {
	    return showControls;
	}

	public String getValue() {
	    return value;
	}

	public String getControlsHorizontalPosition() {
	    return controlsHorizontalPosition;
	}

	public String getControlsVerticalPosition() {
	    return controlsVerticalPosition;
	}

	public String getDefaultLabel() {
	    return defaultLabel;
	}

	public String getStyleClass() {
	    return styleClass;
	}	
    }
    
}