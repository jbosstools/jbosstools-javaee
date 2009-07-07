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


import java.util.List;

import org.jboss.tools.jsf.vpe.richfaces.ComponentUtil;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeChildrenInfo;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.util.Constants;
import org.jboss.tools.vpe.editor.util.HTML;
import org.jboss.tools.vpe.editor.util.VisualDomUtil;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Template for the <rich:inplaceInput> component.
 *
 * @author Eugene Stherbin
 */
public class RichFacesInplaceInputTemplate extends RichFacesAbstractInplaceTemplate {

    /** The input width. */
    protected String inputWidth = null;

    /**
     * The Constructor.
     */
    public RichFacesInplaceInputTemplate() {
        super();
    }

    /**
	 * Creates a node of the visual tree on the node of the source tree. This
	 * visual node should not have the parent node This visual node can have child nodes.<br>
	 * <b>Note</b>: all in-line children will be ignored, except <f:facet> tags with name "controls".
	 * In this case save/cancel controls will be replaced with facet content.
	 *
	 * @param pageContext
	 *            Contains the information on edited page.
	 * @param sourceNode
	 *            The current node of the source tree.
	 * @param visualDocument
	 *            The document of the visual tree.
	 * @return The information on the created node of the visual tree.
     */
    public VpeCreationData create(VpePageContext pageContext, Node sourceNode, nsIDOMDocument visualDocument) {
    	ComponentUtil.setCSSLink(pageContext, getCssStyle(), getCssExtension());
    	Element sourceElement = (Element) sourceNode;
    	final Attributes attrs = new Attributes(sourceElement);
    	/*
    	 * Prepare data
    	 */
    	prepareData(pageContext, sourceElement);

    	final nsIDOMElement rootSpan = createRootSpanTemplateMethod(sourceElement, visualDocument, attrs);
    	final nsIDOMElement innerInput1 = visualDocument.createElement(HTML.TAG_INPUT);
    	VpeCreationData creationData = VisualDomUtil.createTemplateWithTextContainer(
    			sourceElement, rootSpan, HTML.TAG_SPAN, visualDocument);
    	if (isToggle) {
    		rootSpan.appendChild(innerInput1);
    		innerInput1.setAttribute(VPE_USER_TOGGLE_ID_ATTR, String.valueOf(0));
    		innerInput1.setAttribute(HTML.ATTR_CLASS, "rich-inplace-field"); //$NON-NLS-1$
    		innerInput1.setAttribute(HTML.ATTR_STYLE, "top: 0px; width: " + this.inputWidth + Constants.SEMICOLON); //$NON-NLS-1$
    		innerInput1.setAttribute(HTML.ATTR_TYPE, HTML.VALUE_TYPE_TEXT);
    		innerInput1.setAttribute("autocomplete", "off"); //$NON-NLS-1$ //$NON-NLS-2$

    		if (attrs.isShowControls()) {
    			rootSpan.appendChild(createControlsDiv(pageContext, sourceNode,
    					visualDocument, creationData, attrs));
    		}
    	} else {
    		innerInput1.setAttribute(HTML.ATTR_STYLE,
    				"width: " + this.inputWidth + "; position: absolute; left: -32767px;"); //$NON-NLS-1$ //$NON-NLS-2$
    		innerInput1.setAttribute(HTML.ATTR_TYPE, HTML.VALUE_TYPE_BUTTON);

    		/*
    		 * Add empty children info to avoid children processing.
    		 * Only available child is "controls" facet
    		 */
//            creationData.addChildrenInfo(new VpeChildrenInfo(rootSpan));
    	}
    	if (!isToggle) {
    		rootSpan.appendChild(visualDocument.createTextNode(getValue(attrs)));
    	} else {
    		innerInput1.setAttribute(HTML.ATTR_VALUE, getValue(attrs));
    	}
    	return creationData;
    }

    /**
     * Gets the css extension.
     *
     * @return the css extension
     */
    protected String getCssExtension() {
        return "inplaceInput"; //$NON-NLS-1$
    }

    /**
     * Gets the css style.
     *
     * @return the css style
     */
    protected String getCssStyle() {
        return "inplaceInput/inplaceInput.css"; //$NON-NLS-1$
    }

    /**
     * Gets the css styles suffix.
     *
     * @return the css styles suffix
     * @see org.jboss.tools.jsf.vpe.richfaces.template.RichFacesAbstractInplaceTemplate#getCssStylesSuffix()
     */
    @Override
    protected String getCssStylesSuffix() {
        return Constants.EMPTY;
    }

    /**
     * Gets the root span classes.
     *
     * @return the root span classes
     */
    protected String[] getRootSpanClasses(Attributes attrs) {
        String[] rst = new String[3];
        String clazz = Constants.EMPTY;

        if (isToggle) {
            rst[0] = "rich-inplace-edit"; //$NON-NLS-1$
            if (ComponentUtil.isNotBlank(attrs.getEditClass())) {
                clazz = attrs.getEditClass();
                rst[1] = clazz;
            }
        } else {
            rst[0] = "rich-inplace-view"; //$NON-NLS-1$
            if (ComponentUtil.isNotBlank(attrs.getViewClass())) {
            	clazz = attrs.getViewClass();
            	rst[1] = clazz;
            }
        }
        if (ComponentUtil.isNotBlank(attrs.getStyleClass())) {
            rst[2] = attrs.getStyleClass();
        }
        return rst;
    }

    /**
     * Prepare data.
     *
     * @param source the source
     */
    protected void prepareData(VpePageContext pageContext, Element source) {
        try {
            this.inputWidth = String.valueOf(ComponentUtil.parseWidthHeightValue(source.getAttribute("inputWidth"))) + //$NON-NLS-1$
            	Constants.PIXEL;
        } catch (NumberFormatException e) {
            this.inputWidth = DEFAULT_INPUT_WIDTH_VALUE;
        }
        this.controlsHorizontalPositions.put("right", this.inputWidth); //$NON-NLS-1$
        this.controlsHorizontalPositions.put(HTML.VALUE_ALIGN_CENTER, "18px"); //$NON-NLS-1$
        super.prepareImages(source);
    }

    /**
     * @see org.jboss.tools.jsf.vpe.richfaces.template.RichFacesAbstractInplaceTemplate#getCssStylesControlSuffix()
     */
    @Override
    public String getCssStylesControlSuffix() {
        return "-input"; //$NON-NLS-1$
    }

    @Override
    protected String getControlPositionsSubStyles(Attributes attrs) {
        return "top: " + controlsVerticalPositions.get(attrs.getControlsVerticalPosition()) //$NON-NLS-1$
            + ";left: " + controlsHorizontalPositions.get(attrs.getControlsHorizontalPosition()) + Constants.SEMICOLON;  //$NON-NLS-1$
    }

    @Override
    protected String getMainControlsDivCssClass() {
        return "rich-inplace"+getCssStylesControlSuffix()+"-controls-set"; //$NON-NLS-1$ //$NON-NLS-2$
    }
}