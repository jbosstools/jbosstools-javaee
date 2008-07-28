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


import org.jboss.tools.jsf.vpe.richfaces.ComponentUtil;
import org.jboss.tools.jsf.vpe.richfaces.HtmlComponentUtil;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.util.HTML;
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
    protected String inputWidth;

    /**
     * The Constructor.
     */
    public RichFacesInplaceInputTemplate() {
        super();

    }

    /**
     * Create0.
     * 
     * @param visualDocument the visual document
     * @param sourceNode the source node
     * @param pageContext the page context
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
        final nsIDOMElement innerInput1 = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_INPUT);

        String clazz = "";
        if (this.isToggle) {
            rootSpan.appendChild(innerInput1);
            innerInput1.setAttribute(VPE_USER_TOGGLE_ID_ATTR, String.valueOf(0));
            innerInput1.setAttribute(HTML.ATTR_CLASS, "rich-inplace-field");
            innerInput1.setAttribute(HTML.ATTR_STYLE, "top: 0px; width: " + this.inputWidth + ";");
            innerInput1.setAttribute(HTML.ATTR_TYPE, "text");
            innerInput1.setAttribute("autocomplete", "off");
           
            if (showControls) {
                rootSpan.appendChild(createControlsDiv(pageContext, sourceNode, visualDocument));
            }
        } else {
            innerInput1.setAttribute(HTML.ATTR_STYLE, "width: " + this.inputWidth + "; position: absolute; left: -32767px;");
            innerInput1.setAttribute(HTML.ATTR_TYPE, "button");
        }
        data = new VpeCreationData(rootSpan);


        if (!isToggle) {
            final String value = getValue();
//            
//            if (value.equals(DEFAULT_NULL_VALUE)) {
//                final nsIDOMElement innerSpan = visualDocument.createElement(HtmlComponentUtil.HTML_TAG_SPAN);
//                rootSpan.appendChild(innerSpan);
//                innerSpan.appendChild(visualDocument.createTextNode(value));
//                innerSpan.setAttribute(HTML.ATTR_STYLE, "display: none");
//                inner
//                innerSpan.setAttribute(VPE_USER_TOGGLE_ID_ATTR, String.valueOf(this.isToggle));
////                rootSpan.appendChild(visualDocument.createTextNode(value));
////                rootSpan.setAttribute(HTML.ATTR_STYLE, rootSpan.getAttribute(HTML.ATTR_STYLE) +" ; display:none");
//            } else {
                rootSpan.appendChild(visualDocument.createTextNode(value));
 //           }
        } else {
            innerInput1.setAttribute(HTML.ATTR_VALUE, this.sourceValue);
        }
//        final DOMTreeDumper dumper = new DOMTreeDumper();
//        dumper.dumpToStream(System.err, rootSpan);

        return data;
    }


    /**
     * Gets the css extension.
     * 
     * @return the css extension
     */
    protected String getCssExtension() {
        return "inplaceInput";
    }

    /**
     * Gets the css style.
     * 
     * @return the css style
     */
    protected String getCssStyle() {
        return "inplaceInput/inplaceInput.css";
    }

    /**
     * Gets the css styles suffix.
     * 
     * @return the css styles suffix
     * 
     * @see org.jboss.tools.jsf.vpe.richfaces.template.
     * RichFacesAbstractInplaceTemplate#getCssStylesSuffix()
     */
    @Override
    protected String getCssStylesSuffix() {
        return "";
    }

    /**
     * Gets the root span classes.
     * 
     * @return the root span classes
     */
    protected Object[] getRootSpanClasses() {
        String[] rst = new String[2];
        String clazz = "";

        if (this.isToggle) {
            rst[0] = "rich-inplace-edit";
            if (ComponentUtil.isNotBlank(this.editClass)) {
                clazz = this.editClass;
            }
        } else {
            rst[0] = "rich-inplace-view";
            if (ComponentUtil.isNotBlank(this.styleClass)) {
                clazz = this.styleClass;
            }

        }
        rst[1] = clazz;
        return rst;
    }

    /**
     * Prepare data.
     * 
     * @param source the source
     */
    protected void prepareData(VpePageContext pageContext,Element source) {
        try {
            this.inputWidth = String.valueOf(ComponentUtil.parseWidthHeightValue(source.getAttribute("inputWidth")));
        } catch (NumberFormatException e) {
            this.inputWidth = DEFAULT_INPUT_WIDTH_VALUE;
        }
        this.controlsHorizontalPositions.put("right", this.inputWidth);
        this.controlsHorizontalPositions.put(CONTROLS_VERTICAL_POSITION_DEFAULT_VALUE, "18px");
        super.prepareData(pageContext,source);

    }

    /**
     * @see org.jboss.tools.jsf.vpe.richfaces.template.RichFacesAbstractInplaceTemplate#getCssStylesControlSuffix()
     */
    @Override
    public String getCssStylesControlSuffix() {
        return "-input";
    }

    @Override
    protected String getControlPositionsSubStyles() {
        return "top: " + controlsVerticalPositions.get(this.controlsVerticalPosition)
            + ";left:" + " " + controlsHorizontalPositions.get(this.controlsHorizontalPosition) + ";";
    }

    @Override
    protected String getMainControlsDivCssClass() {
        return "rich-inplace"+getCssStylesControlSuffix()+"-controls-set";
    }

}
