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
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeChildrenInfo;
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
        final nsIDOMElement innerInput1 = visualDocument.createElement(HTML.TAG_INPUT);
        data = new VpeCreationData(rootSpan);

        String clazz = ""; //$NON-NLS-1$
        if (this.isToggle) {
            rootSpan.appendChild(innerInput1);
            innerInput1.setAttribute(VPE_USER_TOGGLE_ID_ATTR, String.valueOf(0));
            innerInput1.setAttribute(HTML.ATTR_CLASS, "rich-inplace-field"); //$NON-NLS-1$
            innerInput1.setAttribute(HTML.ATTR_STYLE, "top: 0px; width: " + this.inputWidth + ";"); //$NON-NLS-1$ //$NON-NLS-2$
            innerInput1.setAttribute(HTML.ATTR_TYPE, "text"); //$NON-NLS-1$
            innerInput1.setAttribute("autocomplete", "off"); //$NON-NLS-1$ //$NON-NLS-2$
           
            if (showControls) {
                rootSpan.appendChild(createControlsDiv(pageContext, sourceNode, visualDocument, data));
            }
        } else {
            innerInput1.setAttribute(HTML.ATTR_STYLE, "width: " + this.inputWidth + "; position: absolute; left: -32767px;"); //$NON-NLS-1$ //$NON-NLS-2$
            innerInput1.setAttribute(HTML.ATTR_TYPE, "button"); //$NON-NLS-1$
            
            /*
             * Add empty children info to avoid children processing.
             * Only available child is "controls" facet
             */
            data.addChildrenInfo(new VpeChildrenInfo(rootSpan));
        }


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
     * 
     * @see org.jboss.tools.jsf.vpe.richfaces.template.
     * RichFacesAbstractInplaceTemplate#getCssStylesSuffix()
     */
    @Override
    protected String getCssStylesSuffix() {
        return ""; //$NON-NLS-1$
    }

    /**
     * Gets the root span classes.
     * 
     * @return the root span classes
     */
    protected String[] getRootSpanClasses() {
        String[] rst = new String[3];
        String clazz = ""; //$NON-NLS-1$

        if (this.isToggle) {
            rst[0] = "rich-inplace-edit"; //$NON-NLS-1$
            if (ComponentUtil.isNotBlank(this.editClass)) {
                clazz = this.editClass;
                rst[1] = clazz;
            }
        } else {
            rst[0] = "rich-inplace-view"; //$NON-NLS-1$
            if (ComponentUtil.isNotBlank(this.viewClass)) {
        	clazz = this.viewClass;
        	rst[1] = clazz;
            }
        }
        if (ComponentUtil.isNotBlank(this.styleClass)) {
            rst[2] = this.styleClass;
        }
        return rst;
    }

    /**
     * Prepare data.
     * 
     * @param source the source
     */
    protected void prepareData(VpePageContext pageContext,Element source) {
        try {
            this.inputWidth = String.valueOf(ComponentUtil.parseWidthHeightValue(source.getAttribute("inputWidth"))); //$NON-NLS-1$
        } catch (NumberFormatException e) {
            this.inputWidth = DEFAULT_INPUT_WIDTH_VALUE;
        }
        this.controlsHorizontalPositions.put("right", this.inputWidth); //$NON-NLS-1$
        this.controlsHorizontalPositions.put(CONTROLS_VERTICAL_POSITION_DEFAULT_VALUE, "18px"); //$NON-NLS-1$
        super.prepareData(pageContext,source);

    }

    /**
     * @see org.jboss.tools.jsf.vpe.richfaces.template.RichFacesAbstractInplaceTemplate#getCssStylesControlSuffix()
     */
    @Override
    public String getCssStylesControlSuffix() {
        return "-input"; //$NON-NLS-1$
    }

    @Override
    protected String getControlPositionsSubStyles() {
        return "top: " + controlsVerticalPositions.get(this.controlsVerticalPosition) //$NON-NLS-1$
            + ";left:" + " " + controlsHorizontalPositions.get(this.controlsHorizontalPosition) + ";"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }

    @Override
    protected String getMainControlsDivCssClass() {
        return "rich-inplace"+getCssStylesControlSuffix()+"-controls-set"; //$NON-NLS-1$ //$NON-NLS-2$
    }

}
