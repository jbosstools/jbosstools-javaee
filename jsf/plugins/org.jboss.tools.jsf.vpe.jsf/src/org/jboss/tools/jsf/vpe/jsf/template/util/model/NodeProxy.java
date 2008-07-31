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

import java.util.Collection;

import org.eclipse.wst.sse.core.internal.provisional.INodeAdapter;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.xml.core.internal.document.InvalidCharacterException;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMAttr;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMNode;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMText;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.UserDataHandler;

public class NodeProxy implements IDOMNode {

	protected Node basicNode;

	protected Node parentNode;

	protected int basicOffset;

	public NodeProxy(Node basicNode, int basicOffset) {

		this.basicNode = basicNode;
		this.basicOffset = basicOffset;

	}

	// implementation of Node interface methods
	public Node appendChild(Node newChild) throws DOMException {
		return basicNode.appendChild(newChild);
	}

	public Node cloneNode(boolean deep) {
		return basicNode.cloneNode(deep);
	}

	public short compareDocumentPosition(Node other) throws DOMException {
		return basicNode.compareDocumentPosition(other);
	}

	public NamedNodeMap getAttributes() {

		NamedNodeMap basicAttributes = basicNode.getAttributes();
		NamedNodeMap newAttributes = new NamedNodeMapImpl();

		if (basicAttributes != null)
			for (int i = 0; i < basicAttributes.getLength(); i++) {

				IDOMAttr attr = (IDOMAttr) basicAttributes.item(i);

				newAttributes
						.setNamedItem(new AttributeProxy(attr, basicOffset));

			}

		return newAttributes;
	}

	public String getBaseURI() {
		return basicNode.getBaseURI();
	}

	public NodeList getChildNodes() {

		return createNodeAdapterList(basicNode.getChildNodes());

	}

	public Object getFeature(String feature, String version) {
		return basicNode.getFeature(feature, version);
	}

	public Node getFirstChild() {

		return getNodeAdapter(basicNode.getFirstChild());

	}

	public Node getLastChild() {

		return getNodeAdapter(basicNode.getLastChild());

	}

	public String getLocalName() {
		return basicNode.getLocalName();
	}

	public String getNamespaceURI() {
		return basicNode.getNamespaceURI();
	}

	public Node getNextSibling() {

		return getNodeAdapter(basicNode.getNextSibling());

	}

	public String getNodeName() {
		return basicNode.getNodeName();
	}

	public short getNodeType() {
		return basicNode.getNodeType();
	}

	public String getNodeValue() throws DOMException {
		return basicNode.getNodeValue();
	}

	public Document getOwnerDocument() {
		return basicNode.getOwnerDocument();
	}

	public Node getParentNode() {
		return basicNode.getParentNode();
	}

	public String getPrefix() {
		return basicNode.getPrefix();
	}

	public Node getPreviousSibling() {
		return getNodeAdapter(basicNode.getPreviousSibling());
	}

	public String getTextContent() throws DOMException {
		return basicNode.getTextContent();
	}

	public Object getUserData(String key) {
		return basicNode.getUserData(key);
	}

	public boolean hasAttributes() {
		return basicNode.hasAttributes();
	}

	public boolean hasChildNodes() {
		return basicNode.hasChildNodes();
	}

	public Node insertBefore(Node newChild, Node refChild) throws DOMException {
		return insertBefore(newChild, refChild);
	}

	public boolean isDefaultNamespace(String namespaceURI) {
		return false;
	}

	public boolean isEqualNode(Node arg) {
		return basicNode.isEqualNode(arg);
	}

	public boolean isSameNode(Node other) {
		return basicNode.isSameNode(other);
	}

	public boolean isSupported(String feature, String version) {
		return basicNode.isSupported(feature, version);
	}

	public String lookupNamespaceURI(String prefix) {
		return basicNode.lookupNamespaceURI(prefix);
	}

	public String lookupPrefix(String namespaceURI) {
		return basicNode.lookupPrefix(namespaceURI);
	}

	public void normalize() {

		basicNode.normalize();
	}

	public Node removeChild(Node oldChild) throws DOMException {
		return basicNode.removeChild(oldChild);
	}

	public Node replaceChild(Node newChild, Node oldChild) throws DOMException {
		return basicNode.replaceChild(newChild, oldChild);
	}

	public void setNodeValue(String nodeValue) throws DOMException {
		basicNode.setNodeValue(nodeValue);
	}

	public void setPrefix(String prefix) throws DOMException {
		basicNode.setPrefix(prefix);
	}

	public void setTextContent(String textContent) throws DOMException {
		basicNode.setTextContent(textContent);
	}

	public Object setUserData(String key, Object data, UserDataHandler handler) {
		return basicNode.setUserData(key, data, handler);
	}

	// implementation of IDOMNode interface methods

	public IStructuredDocumentRegion getEndStructuredDocumentRegion() {
		return ((IDOMNode) basicNode).getEndStructuredDocumentRegion();
	}

	public IStructuredDocumentRegion getFirstStructuredDocumentRegion() {
		return ((IDOMNode) basicNode).getFirstStructuredDocumentRegion();
	}

	public IStructuredDocumentRegion getLastStructuredDocumentRegion() {
		return ((IDOMNode) basicNode).getLastStructuredDocumentRegion();
	}

	public IDOMModel getModel() {
		return ((IDOMNode) basicNode).getModel();
	}

	public ITextRegion getNameRegion() {
		return ((IDOMNode) basicNode).getNameRegion();
	}

	public String getSource() {
		return ((IDOMNode) basicNode).getSource();
	}

	public IStructuredDocumentRegion getStartStructuredDocumentRegion() {
		return ((IDOMNode) basicNode).getStartStructuredDocumentRegion();
	}

	public IStructuredDocument getStructuredDocument() {
		return getStructuredDocument();
	}

	public ITextRegion getValueRegion() {
		return ((IDOMNode) basicNode).getValueRegion();
	}

	public String getValueSource() {
		return ((IDOMNode) basicNode).getValueSource();
	}

	public boolean isChildEditable() {
		return ((IDOMNode) basicNode).isChildEditable();
	}

	public boolean isClosed() {
		return ((IDOMNode) basicNode).isClosed();
	}

	public boolean isContainer() {
		return ((IDOMNode) basicNode).isContainer();
	}

	public boolean isDataEditable() {
		return ((IDOMNode) basicNode).isDataEditable();
	}

	public boolean isId() {
		return ((IDOMNode) basicNode).isId();
	}

	public void setChildEditable(boolean editable) {
		((IDOMNode) basicNode).setChildEditable(editable);
	}

	public void setDataEditable(boolean editable) {
		((IDOMNode) basicNode).setDataEditable(editable);
	}

	public void setEditable(boolean editable, boolean deep) {
		((IDOMNode) basicNode).setEditable(editable, deep);

	}

	public void setSource(String source) throws InvalidCharacterException {

		((IDOMNode) basicNode).setSource(source);
	}

	public void setValueSource(String source) {
		((IDOMNode) basicNode).setValueSource(source);

	}

	public boolean contains(int testPosition) {
		return ((IDOMNode) basicNode).contains(testPosition);
	}

	public int getEndOffset() {
		return ((IDOMNode) basicNode).getEndOffset() + basicOffset;
	}

	public int getLength() {
		return ((IDOMNode) basicNode).getLength();
	}

	public int getStartOffset() {
		return ((IDOMNode) basicNode).getStartOffset() + basicOffset;
	}

	public void addAdapter(INodeAdapter adapter) {
		((IDOMNode) basicNode).addAdapter(adapter);
	}

	public INodeAdapter getAdapterFor(Object type) {
		return ((IDOMNode) basicNode).getAdapterFor(type);
	}

	public Collection getAdapters() {
		return ((IDOMNode) basicNode).getAdapters();
	}

	public INodeAdapter getExistingAdapter(Object type) {
		return ((IDOMNode) basicNode).getExistingAdapter(type);
	}

	public void notify(int eventType, Object changedFeature, Object oldValue,
			Object newValue, int pos) {
		((IDOMNode) basicNode).notify();

	}

	public void removeAdapter(INodeAdapter adapter) {
		((IDOMNode) basicNode).removeAdapter(adapter);
	}

	protected NodeProxy getNodeAdapter(Node node) {
		if (node == null)
			return null;

		if (node instanceof IDOMText)
			return new TextProxy((IDOMText) node, basicOffset);
		else if (node instanceof IDOMElement)
			return new ElementProxy((IDOMElement) node, basicOffset);
		else
			return new NodeProxy(node, basicOffset);
	}

	protected NodeList createNodeAdapterList(NodeList nodeList) {

		NodeListImpl newNodeList = new NodeListImpl();

		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);

			newNodeList.appendNode(getNodeAdapter(node));

		}

		return newNodeList;

	}

}
