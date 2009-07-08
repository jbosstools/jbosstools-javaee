/*******************************************************************************
 * Copyright (c) 2007-2009 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.jsf.vpe.richfaces.template;

import org.jboss.tools.jsf.vpe.richfaces.ComponentUtil;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeAbstractTemplate;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.util.HTML;
import org.jboss.tools.vpe.editor.util.VisualDomUtil;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
/**
 * Class for RichFaces' color picker component.
 * 
 * @author dmaliarevich
 */
public class RichFacesColorPickerTemplate extends VpeAbstractTemplate {

	private static final String COLOR_PICKER_CSS_STYLE_PATH = "colorPicker/colorPicker.css"; //$NON-NLS-1$
	private static final String COLOR_PICKER_COMPONENT_NAME = "colorPicker"; //$NON-NLS-1$
	private static final String COLOR_PICKER_ANGLE_ARROW_ICON_PATH = "/colorPicker/colorPickerAngleArrow.gif";  //$NON-NLS-1$
	private static final String CSS_SPAN = "rich-color-picker-span";  //$NON-NLS-1$
	private static final String CSS_ICON = "rich-color-picker-icon";  //$NON-NLS-1$
	private static final String ATTR_COLOR_MODE = "colorMode";  //$NON-NLS-1$
	private static final String VALUE_COLOR_MODE_RGB = "rgb";  //$NON-NLS-1$
	private static final String DEFAULT_COLOR_PICKER_VALUE_HEX = "#ffffff";  //$NON-NLS-1$
	private static final String DEFAULT_COLOR_PICKER_VALUE_RGB = "rgb(255, 255, 255)";  //$NON-NLS-1$
	private static final String DEFAULT_COLOR_PICKER_ICON_STYLE = "background-color: rgb(255, 255, 255);";  //$NON-NLS-1$
	
	/**
	 * Constructor
	 */
	public RichFacesColorPickerTemplate() {
		super();
	}

	public VpeCreationData create(VpePageContext pageContext, Node sourceNode,
			nsIDOMDocument visualDocument) {
		/*
		 * Adding colorPicker.css file to the page
		 */
		ComponentUtil.setCSSLink(pageContext, COLOR_PICKER_CSS_STYLE_PATH,
				COLOR_PICKER_COMPONENT_NAME);
		
		/*
		 * Casting to Element
		 */
		Element sourceElement = (Element) sourceNode;
		
		/*
		 * Creating visual elements
		 */
		nsIDOMElement span = visualDocument.createElement(HTML.TAG_SPAN);
		nsIDOMElement input = visualDocument.createElement(HTML.TAG_INPUT);
		nsIDOMElement img = visualDocument.createElement(HTML.TAG_IMG);
		
		/*
		 * Nesting elements
		 */
		span.appendChild(input);
		span.appendChild(img);
		
		/*
		 * Setting elements attributes for span, input and image
		 */
		span.setAttribute(HTML.ATTR_CLASS, CSS_SPAN);

		String defaultColorMode = DEFAULT_COLOR_PICKER_VALUE_HEX;
		if ((sourceElement.hasAttribute(ATTR_COLOR_MODE))
				&& (VALUE_COLOR_MODE_RGB.equalsIgnoreCase(sourceElement
						.getAttribute(ATTR_COLOR_MODE)))) {
			defaultColorMode = DEFAULT_COLOR_PICKER_VALUE_RGB;
		}
		input.setAttribute(HTML.ATTR_VALUE, defaultColorMode);
		input.setAttribute(HTML.ATTR_TYPE, HTML.VALUE_TYPE_TEXT);
		input.setAttribute(HTML.ATTR_READONLY, HTML.ATTR_READONLY);
		
		img.setAttribute(HTML.ATTR_CLASS, CSS_ICON);
		img.setAttribute(HTML.ATTR_STYLE, DEFAULT_COLOR_PICKER_ICON_STYLE);
		ComponentUtil.setImg(img, COLOR_PICKER_ANGLE_ARROW_ICON_PATH);
		
		/*
		 * Create VpeCreationData with additional container for rich:colorPicker children
		 */
		VpeCreationData creationData = VisualDomUtil.createTemplateWithTextContainer(
				sourceElement, span, HTML.TAG_SPAN, visualDocument);
		
		return creationData;
	}

}
