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
import org.jboss.tools.jsf.vpe.richfaces.HtmlComponentUtil;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeAbstractTemplate;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMNode;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Displays template for gmap
 * 
 * @author Max Areshkau
 * 
 */
public class RichFacesGMapTemplate extends VpeAbstractTemplate {

	private String IMAGE_NAME = "/gmap/gmap.gif";
	
	private String STYLE_CLASS_ATTR_NAME="styleClass";

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.exadel.vpe.editor.template.VpeAbstractTemplate#removeAttribute(com.exadel.vpe.editor.context.VpePageContext,
	 *      org.w3c.dom.Element, org.w3c.dom.Document, org.w3c.dom.Node,
	 *      java.lang.Object, java.lang.String)
	 */
	@Override
	public void removeAttribute(VpePageContext pageContext, Element sourceElement, nsIDOMDocument visualDocument, nsIDOMNode visualNode, Object data, String name) {
		super.removeAttribute(pageContext, sourceElement, visualDocument, visualNode, data, name);
		nsIDOMElement img = (nsIDOMElement) visualNode.queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID);
		if(STYLE_CLASS_ATTR_NAME.equals(name)){
			img.removeAttribute(HtmlComponentUtil.HTML_CLASS_ATTR);
		} else{
			img.removeAttribute(name);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.exadel.vpe.editor.template.VpeAbstractTemplate#setAttribute(com.exadel.vpe.editor.context.VpePageContext,
	 *      org.w3c.dom.Element, org.w3c.dom.Document, org.w3c.dom.Node,
	 *      java.lang.Object, java.lang.String, java.lang.String)
	 */
	@Override
	public void setAttribute(VpePageContext pageContext, Element sourceElement, nsIDOMDocument visualDocument, nsIDOMNode visualNode, Object data, String name, String value) {
		super.setAttribute(pageContext, sourceElement, visualDocument, visualNode, data, name, value);
		nsIDOMElement img = (nsIDOMElement) visualNode.queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID);
		img.setAttribute(name, value);
		if(STYLE_CLASS_ATTR_NAME.equals(name)){
			img.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR,value);
			} else{
				img.setAttribute(name,value);
			}
	}

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
		nsIDOMElement img = visualDocument.createElement("img");
		ComponentUtil.setImg(img, IMAGE_NAME);
		ComponentUtil.copyAttributes(sourceNode, img);
		if(((Element)sourceNode).getAttribute(STYLE_CLASS_ATTR_NAME)!=null){
			img.setAttribute(HtmlComponentUtil.HTML_CLASS_ATTR,((Element)sourceNode).getAttribute("styleClass"));
			} 
		VpeCreationData creationData = new VpeCreationData(img);
		return creationData;
	}

	@Override
	public void resize(VpePageContext pageContext, Element sourceElement, nsIDOMDocument visualDocument, nsIDOMElement visualElement, Object data, int resizerConstrains, int top, int left, int width, int height) {
		super.resize(pageContext, sourceElement, visualDocument, visualElement, data, resizerConstrains, top, left, width, height);
	}

}