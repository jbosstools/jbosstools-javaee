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

import org.jboss.tools.jsf.vpe.richfaces.ComponentUtil;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeAbstractTemplate;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.util.HTML;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Displays template for gmap
 * 
 * @author Max Areshkau
 * 
 */
public class RichFacesGMapTemplate extends VpeAbstractTemplate {

	private static final String ALL_CONTROLS_IMG = "gmap/allControls.png"; //$NON-NLS-1$
	private static final String NO_CONTROLS_IMG = "gmap/noControls.png"; //$NON-NLS-1$
	private static final String LARGE_IMG = "gmap/large.png"; //$NON-NLS-1$
	private static final String SCALE_IMG = "gmap/scale.png"; //$NON-NLS-1$
	private static final String TYPE_IMG = "gmap/type.png"; //$NON-NLS-1$
	private static final String LARGE_SCALE_IMG = "gmap/large-scale.png"; //$NON-NLS-1$
	private static final String LARGE_TYPE_IMG = "gmap/large-type.png"; //$NON-NLS-1$
	private static final String TYPE_SCALE_IMG = "gmap/type-scale.png"; //$NON-NLS-1$
	
	private static final String SHOW_LARGE_MAP = "showGLargeMapControl"; //$NON-NLS-1$
	private static final String SHOW_MAP_TYPE = "showGMapTypeControl"; //$NON-NLS-1$
	private static final String SHOW_SCALE = "showGScaleControl"; //$NON-NLS-1$
	private static final String TRUE = "true"; //$NON-NLS-1$
	
	/**
	 * Create html instead of rich:faces component.
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
		
		Element sourceElement = (Element) sourceNode; 
		
		/*
		 * Value indicating enabled controls combinations:
		 * 
		 * 0 - no controls are available.
		 * 1 - map type control is enabled.
		 * 2 - large map control is enabled.
		 * 4 - scale control is enabled.
		 * 3 - map type and large map controls are enabled.
		 * 5 - map type and scale controls are enabled.
		 * 6 - large map and scale controls are enabled.
		 * 7 - all controls are enabled.
		 */
		int controls = 0;
		
		String showGLargeMapControl = sourceElement.getAttribute(SHOW_LARGE_MAP);
		String showGMapTypeControl = sourceElement.getAttribute(SHOW_MAP_TYPE);
		String showGScaleControl = sourceElement.getAttribute(SHOW_SCALE);
		String style = sourceElement.getAttribute(HTML.ATTR_STYLE);
		String styleClass = sourceElement.getAttribute(RichFaces.ATTR_STYLE_CLASS);
		
		nsIDOMElement mapImg = visualDocument.createElement(HTML.TAG_IMG);
		
		if (TRUE.equalsIgnoreCase(showGMapTypeControl)) {
			controls += 1;
		}
		if (TRUE.equalsIgnoreCase(showGLargeMapControl)) {
			controls += 2;
		}
		if (TRUE.equalsIgnoreCase(showGScaleControl)) {
			controls += 4;
		}
		
		switch (controls) {
		case 0:
			ComponentUtil.setImg(mapImg, NO_CONTROLS_IMG);
			break;
		case 1:
			ComponentUtil.setImg(mapImg, TYPE_IMG);
			break;
		case 2:
			ComponentUtil.setImg(mapImg, LARGE_IMG);
			break;
		case 4:
			ComponentUtil.setImg(mapImg, SCALE_IMG);
			break;
		case 3:
			ComponentUtil.setImg(mapImg, LARGE_TYPE_IMG);
			break;
		case 5:
			ComponentUtil.setImg(mapImg, TYPE_SCALE_IMG);
			break;
		case 6:
			ComponentUtil.setImg(mapImg, LARGE_SCALE_IMG);
			break;
		case 7:
			ComponentUtil.setImg(mapImg, ALL_CONTROLS_IMG);
			break;
		default:
			ComponentUtil.setImg(mapImg, NO_CONTROLS_IMG);
		}
		
		mapImg.setAttribute(HTML.ATTR_CLASS, styleClass);
		mapImg.setAttribute(HTML.ATTR_STYLE, style);
		
		VpeCreationData creationData = new VpeCreationData(mapImg);
		return creationData;
	}
}