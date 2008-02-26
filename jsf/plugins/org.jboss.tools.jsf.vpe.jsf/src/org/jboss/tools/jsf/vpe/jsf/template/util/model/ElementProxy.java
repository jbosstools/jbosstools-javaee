/******************************************************************************* 
 * Copyright (c) 2007 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.jsf.vpe.jsf.template.util.model;

import org.eclipse.wst.xml.core.internal.provisional.document.IDOMAttr;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.NodeList;
import org.w3c.dom.TypeInfo;

public class ElementProxy extends NodeProxy implements IDOMElement {

	public ElementProxy(IDOMElement basicElement, int basicOffset) {
		super(basicElement, basicOffset);
	}

	public int getEndStartOffset() {
		return ((IDOMElement) basicNode).getEndStartOffset() + basicOffset;
	}

	public int getStartEndOffset() {
		return ((IDOMElement) basicNode).getStartEndOffset() + basicOffset;
	}

	public boolean hasEndTag() {
		return ((IDOMElement) basicNode).hasEndTag();
	}

	public boolean hasStartTag() {
		return ((IDOMElement) basicNode).hasStartTag();
	}

	public boolean isCommentTag() {
		return ((IDOMElement) basicNode).isCommentTag();
	}

	public boolean isEmptyTag() {
		return ((IDOMElement) basicNode).isEmptyTag();
	}

	public boolean isEndTag() {
		return ((IDOMElement) basicNode).isEndTag();
	}

	public boolean isGlobalTag() {
		return ((IDOMElement) basicNode).isGlobalTag();
	}

	public boolean isImplicitTag() {
		return ((IDOMElement) basicNode).isImplicitTag();
	}

	public boolean isJSPTag() {
		return ((IDOMElement) basicNode).isJSPTag();
	}

	public boolean isStartTagClosed() {
		return ((IDOMElement) basicNode).isStartTagClosed();
	}

	public boolean isXMLTag() {
		return ((IDOMElement) basicNode).isXMLTag();
	}

	public void notifyEndTagChanged() {
		((IDOMElement) basicNode).notifyEndTagChanged();

	}

	public void notifyStartTagChanged() {
		((IDOMElement) basicNode).notifyStartTagChanged();

	}

	public void setCommentTag(boolean isCommentTag) {
		((IDOMElement) basicNode).setCommentTag(isCommentTag);

	}

	public void setEmptyTag(boolean isEmptyTag) {
		((IDOMElement) basicNode).setEmptyTag(isEmptyTag);

	}

	public void setIdAttribute(String name, boolean isId) {
		((IDOMElement) basicNode).setIdAttribute(name, isId);

	}

	public void setIdAttributeNS(String namespaceURI, String localName,
			boolean isId) {
		((IDOMElement) basicNode).setIdAttributeNS(namespaceURI, localName,
				isId);

	}

	public void setIdAttributeNode(Attr idAttr, boolean isId)
			throws DOMException {
		((IDOMElement) basicNode).setIdAttributeNode(idAttr, isId);

	}

	public void setJSPTag(boolean isJSPTag) {
		((IDOMElement) basicNode).setJSPTag(isJSPTag);

	}

	public String getAttribute(String name) {

		return ((IDOMElement) basicNode).getAttribute(name);
	}

	public String getAttributeNS(String namespaceURI, String localName)
			throws DOMException {
		return ((IDOMElement) basicNode)
				.getAttributeNS(namespaceURI, localName);
	}

	public Attr getAttributeNode(String name) {
		return new AttributeProxy((IDOMAttr) ((IDOMElement) basicNode)
				.getAttributeNode(name), basicOffset);
	}

	public Attr getAttributeNodeNS(String namespaceURI, String localName)
			throws DOMException {
		return new AttributeProxy((IDOMAttr) ((IDOMElement) basicNode)
				.getAttributeNodeNS(namespaceURI, localName), basicOffset);
	}

	public NodeList getElementsByTagName(String name) {
		return createNodeAdapterList(((IDOMElement) basicNode)
				.getElementsByTagName(name));
	}

	public NodeList getElementsByTagNameNS(String namespaceURI, String localName)
			throws DOMException {
		return createNodeAdapterList(((IDOMElement) basicNode)
				.getElementsByTagNameNS(namespaceURI, localName));
	}

	public TypeInfo getSchemaTypeInfo() {
		return ((IDOMElement) basicNode).getSchemaTypeInfo();
	}

	public String getTagName() {
		return ((IDOMElement) basicNode).getTagName();
	}

	public boolean hasAttribute(String name) {
		return ((IDOMElement) basicNode).hasAttribute(name);
	}

	public boolean hasAttributeNS(String namespaceURI, String localName)
			throws DOMException {
		return ((IDOMElement) basicNode)
				.hasAttributeNS(namespaceURI, localName);
	}

	public void removeAttribute(String name) throws DOMException {
		((IDOMElement) basicNode).removeAttribute(name);
	}

	public void removeAttributeNS(String namespaceURI, String localName)
			throws DOMException {

		((IDOMElement) basicNode).removeAttributeNS(namespaceURI, localName);
	}

	public Attr removeAttributeNode(Attr oldAttr) throws DOMException {
		return new AttributeProxy((IDOMAttr) ((IDOMElement) basicNode)
				.removeAttributeNode(oldAttr), basicOffset);
	}

	public void setAttribute(String name, String value) throws DOMException {
		//((IDOMElement) basicNode).setAttribute(name, value);

	}

	public void setAttributeNS(String namespaceURI, String qualifiedName,
			String value) throws DOMException {
		// ((IDOMElement) basicNode).setAttributeNS(namespaceURI, qualifiedName,
		// value);

	}

	public Attr setAttributeNode(Attr newAttr) throws DOMException {
		return new AttributeProxy((IDOMAttr) ((IDOMElement) basicNode)
				.setAttributeNode(newAttr), basicOffset);
	}

	public Attr setAttributeNodeNS(Attr newAttr) throws DOMException {
		return new AttributeProxy((IDOMAttr) ((IDOMElement) basicNode)
				.setAttributeNodeNS(newAttr), basicOffset);
	}

}
