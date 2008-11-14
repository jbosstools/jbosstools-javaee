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
import org.jboss.tools.jsf.vpe.richfaces.HtmlComponentUtil;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.util.HTML;
import org.jboss.tools.vpe.xulrunner.browser.util.DOMTreeDumper;
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

    private static final String _24PX = "24px";

    /** The Constant INPLACE_SELECT_CSS. */
    private static final String INPLACE_SELECT_CSS = "inplaceSelect/inplaceSelect.css";

    /** The Constant INPLACE_SELECT_EXT. */
    private static final String INPLACE_SELECT_EXT = "inplaceSelect";

    /** The select width. */
    private String selectWidth;
    
    protected String sourceListHeight;
    
    protected String sourceListWidth;

    /**
     * Create.
     * 
     * @param visualDocument
     *            the visual document
     * @param sourceNode
     *            the source node
     * @param pageContext
     *            the page context
     * 
     * @return the vpe creation data
     */
    public VpeCreationData create(VpePageContext pageContext, Node sourceNode, nsIDOMDocument visualDocument) {
        VpeCreationData data = null;
        // <span id="j_id5" class="rich-inplace rich-inplace-view" style="">
        ComponentUtil.setCSSLink(pageContext, getCssStyle(), getCssExtension());
        final Element source = (Element) sourceNode;
        prepareData(pageContext,source);
        final nsIDOMElement rootSpan = createRootSpanTemplateMethod(source, visualDocument);
        data = new VpeCreationData(rootSpan);

        if (isToggle) {
            final nsIDOMElement innerInput1 = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_INPUT);
            final nsIDOMElement innerInput2 = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_INPUT);

            preapareInputBase(innerInput1);
            preapareInputBase(innerInput2);
            innerInput1.setAttribute(VPE_USER_TOGGLE_ID_ATTR, String.valueOf(0));
            innerInput1.setAttribute("autocomplete", "off");
            innerInput1.setAttribute(HTML.ATTR_CLASS, "rich-inplace-select-field");
            innerInput1.setAttribute(HTML.ATTR_VALUE, ((this.defaultLabel == null) ? "" : this.defaultLabel));
            // TODO
            innerInput1.setAttribute(HTML.ATTR_STYLE, "top: 1px ; width:100px");
            innerInput1.setAttribute(HTML.ATTR_VALUE, getValue());
            innerInput2.setAttribute(HTML.ATTR_CLASS, "rich-inplace-select-arrow");
            // TODO
            innerInput2.setAttribute(HTML.ATTR_STYLE, "top: 2px; left: 89px;");

            rootSpan.appendChild(innerInput1);
            rootSpan.appendChild(innerInput2);
            if (ComponentUtil.getSelectItems(source.getChildNodes()).size() > 0) {
                final nsIDOMElement selectList = createSelectedList(source, visualDocument);
                rootSpan.appendChild(selectList);
            }
            if (this.showControls) {
                rootSpan.appendChild(createControlsDiv(pageContext, sourceNode, visualDocument, data));
            }

        } else {
            rootSpan.appendChild(visualDocument.createTextNode(getValue()));
        }
//         DOMTreeDumper d = new DOMTreeDumper();
//         d.dumpToStream(System.err, rootSpan);
        return data;
    }

    /**
     * Creates the selected list.
     * 
     * @param visualDocument
     *            the visual document
     * @param source
     *            the source
     * 
     * @return the ns IDOM element
     */
    private nsIDOMElement createSelectedList(Element source, nsIDOMDocument visualDocument) {
        // rich-inplace-select-width-list
        final nsIDOMElement div = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_DIV);

        div.setAttribute(HTML.ATTR_CLASS, "rich-inplace-select-width-list");
        div.setAttribute(VPE_USER_TOGGLE_ID_ATTR, String.valueOf(0));
        div.setAttribute(HTML.ATTR_STYLE, "position: absolute; height: 100px; left: 0px; top: 22px; visibility: visible;");

        final nsIDOMElement shadowDiv = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_DIV);

        shadowDiv.setAttribute(HTML.ATTR_CLASS, "rich-inplace-select-list-shadow");
        shadowDiv.setAttribute(HTML.ATTR_STYLE, "");

        final nsIDOMElement table = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_TABLE);
        final nsIDOMElement tr1 = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_TR);
        final nsIDOMElement tr2 = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_TR);

        final nsIDOMElement td1 = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_TD);
        final nsIDOMElement td2 = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_TD);
        final nsIDOMElement td3 = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_TD);
        final nsIDOMElement td4 = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_TD);

        td1.setAttribute(HTML.ATTR_CLASS, "rich-inplace-select-shadow-tl");
        td2.setAttribute(HTML.ATTR_CLASS, "rich-inplace-select-shadow-tr");
        td3.setAttribute(HTML.ATTR_CLASS, "rich-inplace-select-shadow-bl");
        td4.setAttribute(HTML.ATTR_CLASS, "rich-inplace-select-shadow-br");

        final nsIDOMElement img1 = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_IMG);
        final nsIDOMElement img2 = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_IMG);
        final nsIDOMElement img3 = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_IMG);
        final nsIDOMElement img4 = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_IMG);

        setUpImg(img1, 10, 1, 0, SPACER_GIF);
        setUpImg(img2, 1, 10, 0, SPACER_GIF);
        setUpImg(img3, 1, 10, 0, SPACER_GIF);
        setUpImg(img4, 10, 10, 0, SPACER_GIF);

        final nsIDOMElement listPositionDiv = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_DIV);
        listPositionDiv.setAttribute(HTML.ATTR_CLASS, "rich-inplace-select-list-position");
        listPositionDiv.setAttribute(VPE_USER_TOGGLE_ID_ATTR, String.valueOf(0));

        final nsIDOMElement listDecarationDiv = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_DIV);
        listDecarationDiv.setAttribute(HTML.ATTR_CLASS, "rich-inplace-select-list-decoration");

        final nsIDOMElement listScrollDiv = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_DIV);
        final List<Element> elements = ComponentUtil.getSelectItems(source.getChildNodes());
        // added by estherbin
        // fix http://jira.jboss.com/jira/browse/JBIDE-2196
        // tramanovich comment.
        
        if (this.sourceListHeight == _24PX) {
            int height = 24;

            if ((elements != null) && (elements.size() > 1)) {
                height += ((elements.size() - 2) * 24)+1;
            }
            this.sourceListHeight = String.valueOf(height) + String.valueOf("px");
        }

        listScrollDiv.setAttribute(HTML.ATTR_CLASS, "rich-inplace-select-list-scroll");
        listScrollDiv.setAttribute(HTML.ATTR_STYLE, "height:" + this.sourceListHeight + "; width: " + this.sourceListWidth);

        if (elements.size() > 0) {
            for (Element e : elements) {
                final nsIDOMElement span = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_SPAN);

                span.setAttribute(HTML.ATTR_CLASS, "rich-inplace-select-item rich-inplace-select-font");
                span.appendChild(visualDocument.createTextNode(ComponentUtil.getSelectItemValue(e)));
                span.setAttribute(HTML.ATTR_STYLE, "text-align: left;");
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
        td1.appendChild(visualDocument.createElement(HtmlComponentUtil.HTML_TAG_BR));
        td2.appendChild(img2);
        td2.appendChild(visualDocument.createElement(HtmlComponentUtil.HTML_TAG_BR));

        table.appendChild(tr2);
        tr2.appendChild(td3);
        tr2.appendChild(td4);
        td3.appendChild(img3);
        td3.appendChild(visualDocument.createElement(HtmlComponentUtil.HTML_TAG_BR));
        td4.appendChild(img4);
        td4.appendChild(visualDocument.createElement(HtmlComponentUtil.HTML_TAG_BR));

        return div;
    }

    /**
     * Gets the css extension.
     * 
     * @return the css extension
     * 
     * @see org.jboss.tools.jsf.vpe.richfaces.template.
     *      RichFacesAbstractInplaceTemplate#getCssExtension()
     */
    @Override
    protected String getCssExtension() {
        return INPLACE_SELECT_EXT;
    }

    /**
     * Gets the css style.
     * 
     * @return the css style
     * 
     * @see org.jboss.tools.jsf.vpe.richfaces.template.
     *      RichFacesAbstractInplaceTemplate#getCssStyle()
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
        return "-select";
    }

    /**
     * Gets the root span classes.
     * 
     * @return the root span classes
     * 
     * @see org.jboss.tools.jsf.vpe.richfaces.template.
     *      RichFacesAbstractInplaceTemplate#getRootSpanClasses()
     */
    @Override
    protected String[] getRootSpanClasses() {
        String[] result = new String[2];
        String clazz = "";

        if (this.isToggle) {
            result[0] = "rich-inplace-select-edit";
            if (ComponentUtil.isNotBlank(this.editClass)) {
                clazz = this.editClass;
            }
        } else {
            result[0] = "rich-inplace-select-view";
            if (ComponentUtil.isNotBlank(this.viewClass)) {
                clazz = this.viewClass;
            }
        }
        result[1] = clazz;
        return result;
    }

    /**
     * Preapare input base.
     * 
     * @param innerInput
     *            the inner input
     * @param innerInput1
     */
    private void preapareInputBase(nsIDOMElement innerInput) {
        innerInput.setAttribute(HTML.ATTR_TYPE, "text");
        innerInput.setAttribute(HtmlComponentUtil.HTML_READONLY_ATTR, "text");

    }

    /**
     * Prepare data.
     * 
     * @param source
     *            the source
     */
    @Override
    protected void prepareData(VpePageContext pageContext,Element source) {
        
        this.sourceListHeight = ComponentUtil.getAttribute(source,"listHeight");
        this.sourceListWidth = ComponentUtil.getAttribute(source, "listWidth");
        
        if (ComponentUtil.isBlank(this.sourceListHeight)) {
            this.sourceListHeight = _24PX;
        }

        if (ComponentUtil.isBlank(this.sourceListWidth)) {
            this.sourceListWidth = String.valueOf("198px");
        }
        
        super.prepareData(pageContext,source);

    }

    @Override
    protected void initPositions() {
        this.controlsVerticalPositions.put("center", "100px");
    }

    /**
     * @see org.jboss.tools.jsf.vpe.richfaces.template.RichFacesAbstractInplaceTemplate#getCssStylesControlSuffix()
     */
    @Override
    protected String getCssStylesControlSuffix() {
        return this.getCssStylesSuffix();
    }

    @Override
    protected String getControlPositionsSubStyles() {
    
        return  "top:0px ; left: " + controlsVerticalPositions.get(this.controlsVerticalPosition)
            + ";left:" + " " + controlsHorizontalPositions.get(this.controlsHorizontalPosition) + ";";
    }

    @Override
    protected String getMainControlsDivCssClass() {
        // TODO Auto-generated method stub
        return "rich-inplace"+getCssStylesControlSuffix()+"-control-set";
    }

}
