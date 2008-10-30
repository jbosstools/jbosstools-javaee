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
import org.jboss.tools.jsf.vpe.richfaces.template.util.RichFaces;
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

	private static final String CLEAN_EARTH_IMG = "gmap/cleanEarth.png"; //$NON-NLS-1$
	private static final String GMAP_TYPE_CONTROL_IMG = "gmap/mapType.png"; //$NON-NLS-1$
	private static final String GLARGE_MAP_CONTROL_IMG = "gmap/largeMap.gif"; //$NON-NLS-1$
	private static final String GSCALE_CONTROL_IMG = "gmap/scale.png"; //$NON-NLS-1$
	
	private static final String SHOW_LARGE_MAP = "showGLargeMapControl"; //$NON-NLS-1$
	private static final String SHOW_MAP_TYPE = "showGMapTypeControl"; //$NON-NLS-1$
	private static final String SHOW_SCALE = "showGScaleControl"; //$NON-NLS-1$
	private static final String FALSE = "false"; //$NON-NLS-1$
	private static final String VISIBILITY_HIDDEN = "visibility: hidden;"; //$NON-NLS-1$
	
//	private static final String gmapWrapperStyle ="width: 400px; height: 398px; "; //$NON-NLS-1$
	private String gmapWrapperStyle ="display: block; overflow: hidden; /*width: 400px; height: 398px;*/ float: left; position: relative;  "; //$NON-NLS-1$
	private String cleanEarthImgStyle ="float: left; position: relative; width: 400px; height: 398px; "; //$NON-NLS-1$
	private String mapTypeImgStyle ="float: left; position: relative; width: 202px; height: 19px; top: 3px; left: -206px; "; //$NON-NLS-1$
	private String largeMapImgStyle ="float: left; position: relative; width: 57px; height: 270px; top: 5px; left: -596px; "; //$NON-NLS-1$
	private String scaleImgStyle ="float: left; position: relative; width: 85px; height: 27px; top: 366px; left: -592px;"; //$NON-NLS-1$

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
		
		String showGLargeMapControl = sourceElement.getAttribute(SHOW_LARGE_MAP);
		String showGMapTypeControl = sourceElement.getAttribute(SHOW_MAP_TYPE);
		String showGScaleControl = sourceElement.getAttribute(SHOW_SCALE);
		
		nsIDOMElement gmapWrapperElement = visualDocument.createElement(HTML.TAG_DIV);
		nsIDOMElement gmapCleanEarhtImg = visualDocument.createElement(HTML.TAG_IMG);
		nsIDOMElement gmapGMapTypeControlImg = visualDocument.createElement(HTML.TAG_IMG);
		nsIDOMElement gmapGLargeMapControlImg = visualDocument.createElement(HTML.TAG_IMG);
		nsIDOMElement gmapGScaleControlImg = visualDocument.createElement(HTML.TAG_IMG);
		
		ComponentUtil.setImg(gmapCleanEarhtImg, CLEAN_EARTH_IMG);
		ComponentUtil.setImg(gmapGMapTypeControlImg, GMAP_TYPE_CONTROL_IMG);
		ComponentUtil.setImg(gmapGLargeMapControlImg, GLARGE_MAP_CONTROL_IMG);
		ComponentUtil.setImg(gmapGScaleControlImg, GSCALE_CONTROL_IMG);
		
		
		gmapWrapperElement.appendChild(gmapCleanEarhtImg);
		gmapWrapperElement.appendChild(gmapGMapTypeControlImg);
		gmapWrapperElement.appendChild(gmapGLargeMapControlImg);
		gmapWrapperElement.appendChild(gmapGScaleControlImg);
		
		if (FALSE.equalsIgnoreCase(showGMapTypeControl)) {
			mapTypeImgStyle += VISIBILITY_HIDDEN;
		}
		if (FALSE.equalsIgnoreCase(showGLargeMapControl)) {
			largeMapImgStyle += VISIBILITY_HIDDEN;
		}
		if (FALSE.equalsIgnoreCase(showGScaleControl)) {
			scaleImgStyle += VISIBILITY_HIDDEN;
		}
		
		gmapWrapperElement.setAttribute(HTML.ATTR_STYLE, gmapWrapperStyle);
		gmapCleanEarhtImg.setAttribute(HTML.ATTR_STYLE, cleanEarthImgStyle);
		gmapGMapTypeControlImg.setAttribute(HTML.ATTR_STYLE, mapTypeImgStyle);
		gmapGLargeMapControlImg.setAttribute(HTML.ATTR_STYLE, largeMapImgStyle);
		gmapGScaleControlImg.setAttribute(HTML.ATTR_STYLE, scaleImgStyle);
		
		VpeCreationData creationData = new VpeCreationData(gmapWrapperElement);
		return creationData;
	}

	public boolean isRecreateAtAttrChange(VpePageContext pageContext,
			Element sourceElement, nsIDOMDocument visualDocument,
			nsIDOMElement visualNode, Object data, String name, String value) {
		return true;
	}

	

}