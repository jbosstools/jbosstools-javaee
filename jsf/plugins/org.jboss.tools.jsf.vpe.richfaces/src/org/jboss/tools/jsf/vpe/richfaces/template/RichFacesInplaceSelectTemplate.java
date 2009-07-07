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
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.util.Constants;
import org.jboss.tools.vpe.editor.util.HTML;
import org.jboss.tools.vpe.editor.util.VisualDomUtil;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Template for <rich:inplaceSelect/> tag.
 *
 * @author Eugene Stherbin
 */
public class RichFacesInplaceSelectTemplate extends RichFacesAbstractInplaceTemplate {
		
    /*
     * Default width and height of the drop down select list.
     */         
    private static final String SOURCE_LIST_DEFAULT_HEIGHT = "24px"; //$NON-NLS-1$
    private static final String SOURCE_LIST_DEFAULT_WIDTH = "198px"; //$NON-NLS-1$

    /** The Constant INPLACE_SELECT_CSS. */
    private static final String INPLACE_SELECT_CSS = "inplaceSelect/inplaceSelect.css"; //$NON-NLS-1$

    /** The Constant INPLACE_SELECT_EXT. */
    private static final String INPLACE_SELECT_EXT = "inplaceSelect"; //$NON-NLS-1$

    /*
     * Width and height of the drop down select list.
     */
    protected String sourceListHeight;
    protected String sourceListWidth;

    /**
	 * Creates a node of the visual tree on the node of the source tree. This
	 * visual node should not have the parent node This visual node can have child nodes.
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
        final Element sourceElement = (Element) sourceNode;
        final Attributes attrs = new Attributes(sourceElement);
	    /*
	     * Prepare data
	     */
        prepareData(pageContext, sourceElement);
        final nsIDOMElement rootSpan = createRootSpanTemplateMethod(sourceElement, visualDocument, attrs);
        VpeCreationData creationData = VisualDomUtil.createTemplateWithTextContainer(
				sourceElement, rootSpan, HTML.TAG_SPAN, visualDocument);
        if (isToggle) {
            final nsIDOMElement innerInput1 = visualDocument.createElement(HTML.TAG_INPUT);
            final nsIDOMElement innerInput2 = visualDocument.createElement(HTML.TAG_INPUT);
            preapareInputBase(innerInput1);
            preapareInputBase(innerInput2);
            innerInput1.setAttribute(VPE_USER_TOGGLE_ID_ATTR, String.valueOf(0));
            innerInput1.setAttribute("autocomplete", "off"); //$NON-NLS-1$ //$NON-NLS-2$
            innerInput1.setAttribute(HTML.ATTR_CLASS, "rich-inplace-select-field"); //$NON-NLS-1$
            innerInput1.setAttribute(HTML.ATTR_VALUE,
            			((attrs.getDefaultLabel() == null) ? Constants.EMPTY : attrs.getDefaultLabel()));
            // TODO
            innerInput1.setAttribute(HTML.ATTR_STYLE, "top: 1px ; width:100px"); //$NON-NLS-1$
            innerInput1.setAttribute(HTML.ATTR_VALUE, getValue(attrs));
            innerInput2.setAttribute(HTML.ATTR_CLASS, "rich-inplace-select-arrow"); //$NON-NLS-1$
            // TODO
            innerInput2.setAttribute(HTML.ATTR_STYLE, "top: 2px; left: 89px;"); //$NON-NLS-1$

            rootSpan.appendChild(innerInput1);
            rootSpan.appendChild(innerInput2);
            List<Element> elements = ComponentUtil.getSelectItems(sourceElement.getChildNodes());
            if ((elements != null) && (elements.size() > 0)) {
                final nsIDOMElement selectList = createSelectedList(elements, visualDocument);
                rootSpan.appendChild(selectList);
            }
            if (attrs.isShowControls()) {
				rootSpan.appendChild(createControlsDiv(pageContext, sourceNode,
						visualDocument, creationData, attrs));
            }
        } else {
            rootSpan.appendChild(visualDocument.createTextNode(getValue(attrs)));
        }
        return creationData;
    }

    /**
     * Creates the selected list.
     *
     * @param visualDocument the visual document
     * @param source the source
     * @return the ns IDOM element
     */
    private nsIDOMElement createSelectedList(List<Element> elements, nsIDOMDocument visualDocument) {
        // rich-inplace-select-width-list
        final nsIDOMElement div = visualDocument.createElement(HTML.TAG_DIV);

        div.setAttribute(HTML.ATTR_CLASS, "rich-inplace-select-width-list"); //$NON-NLS-1$
        div.setAttribute(VPE_USER_TOGGLE_ID_ATTR, String.valueOf(0));
        div.setAttribute(HTML.ATTR_STYLE, "position: absolute; height: 100px; left: 0px; top: 22px; visibility: visible;"); //$NON-NLS-1$

        final nsIDOMElement shadowDiv = visualDocument.createElement(HTML.TAG_DIV);

        shadowDiv.setAttribute(HTML.ATTR_CLASS, "rich-inplace-select-list-shadow"); //$NON-NLS-1$
        shadowDiv.setAttribute(HTML.ATTR_STYLE, Constants.EMPTY);

        final nsIDOMElement table = visualDocument.createElement(HTML.TAG_TABLE);
        final nsIDOMElement tr1 = visualDocument.createElement(HTML.TAG_TR);
        final nsIDOMElement tr2 = visualDocument.createElement(HTML.TAG_TR);

        final nsIDOMElement td1 = visualDocument.createElement(HTML.TAG_TD);
        final nsIDOMElement td2 = visualDocument.createElement(HTML.TAG_TD);
        final nsIDOMElement td3 = visualDocument.createElement(HTML.TAG_TD);
        final nsIDOMElement td4 = visualDocument.createElement(HTML.TAG_TD);

        td1.setAttribute(HTML.ATTR_CLASS, "rich-inplace-select-shadow-tl"); //$NON-NLS-1$
        td2.setAttribute(HTML.ATTR_CLASS, "rich-inplace-select-shadow-tr"); //$NON-NLS-1$
        td3.setAttribute(HTML.ATTR_CLASS, "rich-inplace-select-shadow-bl"); //$NON-NLS-1$
        td4.setAttribute(HTML.ATTR_CLASS, "rich-inplace-select-shadow-br"); //$NON-NLS-1$

        final nsIDOMElement img1 = visualDocument.createElement(HTML.TAG_IMG);
        final nsIDOMElement img2 = visualDocument.createElement(HTML.TAG_IMG);
        final nsIDOMElement img3 = visualDocument.createElement(HTML.TAG_IMG);
        final nsIDOMElement img4 = visualDocument.createElement(HTML.TAG_IMG);

        setUpImg(img1, 10, 1, 0, SPACER_GIF);
        setUpImg(img2, 1, 10, 0, SPACER_GIF);
        setUpImg(img3, 1, 10, 0, SPACER_GIF);
        setUpImg(img4, 10, 10, 0, SPACER_GIF);

        final nsIDOMElement listPositionDiv = visualDocument.createElement(HTML.TAG_DIV);
        listPositionDiv.setAttribute(HTML.ATTR_CLASS, "rich-inplace-select-list-position"); //$NON-NLS-1$
        listPositionDiv.setAttribute(VPE_USER_TOGGLE_ID_ATTR, String.valueOf(0));

        final nsIDOMElement listDecarationDiv = visualDocument.createElement(HTML.TAG_DIV);
        listDecarationDiv.setAttribute(HTML.ATTR_CLASS, "rich-inplace-select-list-decoration"); //$NON-NLS-1$

        final nsIDOMElement listScrollDiv = visualDocument.createElement(HTML.TAG_DIV);
        // added by estherbin
        // fix http://jira.jboss.com/jira/browse/JBIDE-2196
        // tramanovich comment.
        if (this.sourceListHeight == SOURCE_LIST_DEFAULT_HEIGHT) {
            int height = 24;
            if (elements.size() > 1) {
                height += ((elements.size() - 2) * 24)+1;
            }
            this.sourceListHeight = String.valueOf(height) + Constants.PIXEL;
        }
		String dropDownListSizesStyle = HTML.ATTR_HEIGHT + Constants.COLON
				+ this.sourceListHeight + Constants.SEMICOLON
				+ Constants.WHITE_SPACE + HTML.ATTR_WIDTH + Constants.COLON
				+this.sourceListWidth + Constants.SEMICOLON;
		
		System.out.println(" dropDownListSizesStyle = "
				+ dropDownListSizesStyle);
		
		table.setAttribute(HTML.ATTR_STYLE, dropDownListSizesStyle);
        listScrollDiv.setAttribute(HTML.ATTR_CLASS, "rich-inplace-select-list-scroll"); //$NON-NLS-1$
        listScrollDiv.setAttribute(HTML.ATTR_STYLE, dropDownListSizesStyle);
//        listScrollDiv.setAttribute(HTML.ATTR_STYLE, "height:" + this.sourceListHeight + "; width: " + this.sourceListWidth); //$NON-NLS-1$ //$NON-NLS-2$

        if (elements.size() > 0) {
            for (Element e : elements) {
                final nsIDOMElement span = visualDocument.createElement(HTML.TAG_SPAN);

                span.setAttribute(HTML.ATTR_CLASS, "rich-inplace-select-item rich-inplace-select-font"); //$NON-NLS-1$
                span.appendChild(visualDocument.createTextNode(ComponentUtil.getSelectItemValue(e)));
                span.setAttribute(HTML.ATTR_STYLE, "text-align: left;"); //$NON-NLS-1$
                listScrollDiv.appendChild(span);
            }
        }

        div.appendChild(shadowDiv);
        div.appendChild(listPositionDiv);
        listPositionDiv.appendChild(listDecarationDiv);
        listDecarationDiv.appendChild(listScrollDiv);

        shadowDiv.appendChild(table);

        table.appendChild(tr1);
        tr1.appendChild(td1);

        tr1.appendChild(td2);
        td1.appendChild(img1);
        td1.appendChild(visualDocument.createElement(HTML.TAG_BR));
        td2.appendChild(img2);
        td2.appendChild(visualDocument.createElement(HTML.TAG_BR));

        table.appendChild(tr2);
        tr2.appendChild(td3);
        tr2.appendChild(td4);
        td3.appendChild(img3);
        td3.appendChild(visualDocument.createElement(HTML.TAG_BR));
        td4.appendChild(img4);
        td4.appendChild(visualDocument.createElement(HTML.TAG_BR));

        return div;
    }

    /**
     * Gets the css extension.
     *
     * @return the css extension
     * @see org.jboss.tools.jsf.vpe.richfaces.template.RichFacesAbstractInplaceTemplate#getCssExtension()
     */
    @Override
    protected String getCssExtension() {
        return INPLACE_SELECT_EXT;
    }

    /**
     * Gets the css style.
     *
     * @return the css style
     * @see org.jboss.tools.jsf.vpe.richfaces.template.RichFacesAbstractInplaceTemplate#getCssStyle()
     */
    @Override
    protected String getCssStyle() {
        return INPLACE_SELECT_CSS;
    }

    /**
     * Gets the css styles suffix.
     *
     * @return the css styles suffix
     */
    @Override
    protected String getCssStylesSuffix() {
        return "-select"; //$NON-NLS-1$
    }

    /**
     * Gets the root span classes.
     *
     * @return the root span classes
     * @see org.jboss.tools.jsf.vpe.richfaces.template.RichFacesAbstractInplaceTemplate#getRootSpanClasses()
     */
    @Override
    protected String[] getRootSpanClasses(Attributes attrs) {
        String[] result = new String[3];
        String clazz = Constants.EMPTY;
        if (this.isToggle) {
            result[0] = "rich-inplace-select-edit"; //$NON-NLS-1$
            if (ComponentUtil.isNotBlank(attrs.getEditClass())) {
                clazz = attrs.getEditClass();
            }
        } else {
            result[0] = "rich-inplace-select-view"; //$NON-NLS-1$
            if (ComponentUtil.isNotBlank(attrs.getViewClass())) {
                clazz = attrs.getViewClass();
            }
        }
        result[1] = clazz;
        if (ComponentUtil.isNotBlank(attrs.getStyleClass())) {
            result[2] = attrs.getStyleClass();
        }
        return result;
    }

    /**
     * Prepare input base.
     *
     * @param innerInput the inner input
     */
    private void preapareInputBase(nsIDOMElement innerInput) {
        innerInput.setAttribute(HTML.ATTR_TYPE, HTML.VALUE_TYPE_TEXT);
        innerInput.setAttribute(HTML.ATTR_READONLY, HTML.VALUE_TYPE_TEXT);
    }

    /**
     * Prepare data.
     *
     * @param pageContext VpePageContext object
     * @param source the source
     */
    @Override
    protected void prepareData(VpePageContext pageContext, Element source) {
        this.sourceListHeight = ComponentUtil.getAttribute(source, "listHeight"); //$NON-NLS-1$
        this.sourceListWidth = ComponentUtil.getAttribute(source, "listWidth"); //$NON-NLS-1$
        if (ComponentUtil.isBlank(this.sourceListHeight)) {
            this.sourceListHeight = SOURCE_LIST_DEFAULT_HEIGHT;
        }

        if (ComponentUtil.isBlank(this.sourceListWidth)) {
            this.sourceListWidth = SOURCE_LIST_DEFAULT_WIDTH;
        }

        super.prepareImages(source);
    }

    @Override
    protected void initPositions() {
        this.controlsVerticalPositions.put(HTML.VALUE_ALIGN_CENTER, "100px"); //$NON-NLS-1$
    }

    /**
     * @see org.jboss.tools.jsf.vpe.richfaces.template.RichFacesAbstractInplaceTemplate#getCssStylesControlSuffix()
     */
    @Override
    protected String getCssStylesControlSuffix() {
        return this.getCssStylesSuffix();
    }

    /**
     * @see org.jboss.tools.jsf.vpe.richfaces.template.RichFacesAbstractInplaceTemplate#getControlPositionsSubStyles()
     */
    @Override
    protected String getControlPositionsSubStyles(Attributes attrs) {
        return  "top:0px ; left: " + controlsVerticalPositions.get(attrs.getControlsVerticalPosition()) //$NON-NLS-1$
            + ";left: " + controlsHorizontalPositions.get(attrs.getControlsHorizontalPosition()) + Constants.SEMICOLON; //$NON-NLS-1$
    }

    /**
     * @see org.jboss.tools.jsf.vpe.richfaces.template.RichFacesAbstractInplaceTemplate#getMainControlsDivCssClass()
     */
    @Override
    protected String getMainControlsDivCssClass() {
        return "rich-inplace" + getCssStylesControlSuffix() + "-control-set"; //$NON-NLS-1$ //$NON-NLS-2$
    }
}