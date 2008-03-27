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
package org.jboss.tools.jsf.vpe.richfaces;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.editors.text.ILocationProvider;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMNode;
import org.mozilla.interfaces.nsIDOMNodeList;
import org.mozilla.xpcom.XPCOMException;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

public class ComponentUtil {

	/**
	 * Gets child of Facet element by name. If facet has a few children the
	 * method will return first one.
	 * 
	 * @param sourceElement
	 * @param facetName
	 * @param returnTextNode
	 *            return child text node if facet doesn't have any child
	 *            elements;
	 * @return
	 */
	public static Node getFacet(Element sourceElement, String facetName,
			boolean returnTextNode) {
		NodeList children = sourceElement.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node node = children.item(i);
			if (node instanceof Element && node.getNodeName() != null
					&& node.getNodeName().indexOf(":facet") > 0) { //$NON-NLS-1$
				Element element = (Element) node;
				String name = element.getAttribute("name"); //$NON-NLS-1$
				if (facetName.equals(name)) {
					NodeList childNodes = element.getChildNodes();
					Text textNode = null;
					for (int j = 0; j < childNodes.getLength(); j++) {
						Node child = childNodes.item(j);
						if (child instanceof Element) {
							return child;
						} else if (child instanceof Text) {
							textNode = (Text) child;
						}
					}
					if (returnTextNode) {
						return textNode;
					}
				}
			}
		}
		return null;
	}

	/**
	 * Gets all facets of sourceElement. If facet has a few children the method
	 * will return first one.
	 * 
	 * @param sourceElement
	 * @param facetName
	 * @param returnTextNode
	 *            return child text node if facet doesn't have any child
	 *            elements;
	 * @return
	 */
	public static ArrayList<Node> getFacets(Element sourceElement,
			boolean returnTextNode) {
		ArrayList<Node> facets = new ArrayList<Node>();
		NodeList children = sourceElement.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node node = children.item(i);
			if (node instanceof Element && "f:facet".equals(node.getNodeName())) { //$NON-NLS-1$
				Element element = (Element) node;
				NodeList childNodes = element.getChildNodes();
				Text textNode = null;
				for (int j = 0; j < childNodes.getLength(); j++) {
					Node child = childNodes.item(j);
					if (child instanceof Element) {
						facets.add(child);
						break;
					} else if (child instanceof Text) {
						textNode = (Text) child;
					}
				}
				if (returnTextNode && facets.isEmpty()) {
					facets.add(textNode);
				}
			}
		}
		return facets;
	}

	/**
	 * Gets child of Facet element by name. If facet has a few children the
	 * method will return first one.
	 * 
	 * @param sourceElement
	 * @param facetName
	 * @return
	 */
	public static Element getFacet(Element sourceElement, String facetName) {
		return (Element) getFacet(sourceElement, facetName, false);
	}

	/**
	 * Returns true if "rendered" attribute of source element does not contain
	 * "false" value.
	 * 
	 * @param sourceElement
	 * @return
	 */
	public static boolean isRendered(Element sourceElement) {
		return !"false" //$NON-NLS-1$
		.equalsIgnoreCase(sourceElement.getAttribute("rendered")); //$NON-NLS-1$
	}

	/**
	 * Sets CSS link in visual html document.
	 * 
	 * @param pageContext
	 * @param cssHref
	 */
	public static void setCSSLink(VpePageContext pageContext, String cssHref,
			String ext) {
		String pluginPath = RichFacesTemplatesActivator.getPluginResourcePath();
		IPath pluginFile = new Path(pluginPath);
		File cssFile = pluginFile.append(cssHref).toFile();
		if (cssFile.exists()) {
			String cssPath = "file:///" + cssFile.getPath(); //$NON-NLS-1$
			pageContext.getVisualBuilder().replaceLinkNodeToHead(cssPath, ext);
		}
	}

	public static String getAbsoluteResourcePath(String resourcePathInPlugin) {
		String pluginPath = RichFacesTemplatesActivator.getPluginResourcePath();
		IPath pluginFile = new Path(pluginPath);
		File file = pluginFile.append(resourcePathInPlugin).toFile();
		if (file.exists()) {
			return file.getAbsolutePath();
		} else {
			throw new RuntimeException("Can't get path for " //$NON-NLS-1$
					+ resourcePathInPlugin);
		}
	}

	/**
	 * Adds image as attribute to IMG tag
	 * 
	 * @param img
	 * @param fileImageName
	 */
	public static void setImg(nsIDOMElement img, String fileImageName) {
		img.setAttribute("src", "file://" //$NON-NLS-1$//$NON-NLS-2$
				+ getAbsoluteResourcePath(fileImageName));
	}

	/**
	 * Returns all child source elements of component but facets.
	 * 
	 * @param sourceElement
	 * @param returnTextNodes
	 *            return child text nodes and elements or elements only;
	 * @return
	 */
	public static List<Node> getChildren(Element sourceElement,
			boolean returnTextNodes) {
		ArrayList<Node> children = new ArrayList<Node>();
		NodeList nodeList = sourceElement.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node child = nodeList.item(i);
			if ((child instanceof Element || returnTextNodes)
					&& (!child.getNodeName().equals("f:facet"))) { //$NON-NLS-1$
				children.add(child);
			}
		}
		return children;
	}

	/**
	 * Returns all child source elements of component but facets.
	 * 
	 * @param sourceElement
	 * @return
	 */
	public static List<Node> getChildren(Element sourceElement) {
		return getChildren(sourceElement, false);
	}

	/**
	 * Returns all child visual elements of component but facets.
	 * 
	 * @param visualElement
	 * @param
	 * @return returnTextNodes return child text nodes and elements or elements
	 *         only;
	 */
	public static List<nsIDOMNode> getChildren(nsIDOMElement visualElement,
			boolean returnTextNodes) {
		ArrayList<nsIDOMNode> children = new ArrayList<nsIDOMNode>();
		nsIDOMNodeList nodeList = visualElement.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			nsIDOMNode child = nodeList.item(i);
			if ((child instanceof nsIDOMElement || returnTextNodes)
					&& (!child.getNodeName().equals("f:facet"))) { //$NON-NLS-1$
				children.add(child);
			}
		}
		return children;
	}

	/**
	 * Returns all child visual elements of component but facets.
	 * 
	 * @param visualElement
	 * @return
	 */
	public static List<nsIDOMNode> getChildren(nsIDOMElement visualElement) {
		return getChildren(visualElement, false);
	}

	/**
	 * Copies all attributes from source node to visual node.
	 * 
	 * @param sourceNode
	 * @param visualNode
	 */
	public static void copyAttributes(Node sourceNode,
			nsIDOMElement visualElement) {
		NamedNodeMap namedNodeMap = sourceNode.getAttributes();
		for (int i = 0; i < namedNodeMap.getLength(); i++) {
			Node attribute = namedNodeMap.item(i);
			// added by Max Areshkau fix for JBIDE-1568
			try {

				visualElement.setAttribute(attribute.getNodeName(), attribute
						.getNodeValue());
			} catch (XPCOMException ex) {
				// if error-code not equals error for incorrect name throws
				// exception
				// error code is NS_ERROR_DOM_INVALID_CHARACTER_ERR=0x80530005
				if (ex.errorcode != 2152923141L) {
					throw ex;
				}
				// else we ignore this exception
			}
		}
	}

	/**
	 * Copies attributes from source node to visual node.
	 * 
	 * @param attributes -
	 *            list names of attributes which will copy
	 * @param sourceNode
	 * @param visualNode
	 */
	public static void copyAttributes(Element sourceElement,
			List<String> attributes, nsIDOMElement visualElement) {

		if (attributes == null)
			return;

		for (String attributeName : attributes) {

			String attributeValue = sourceElement.getAttribute(attributeName);
			if (attributeValue != null)
				visualElement.setAttribute(attributeName, attributeValue);
		}

	}

	/**
	 * Returns true if sourceNode is Facet
	 * 
	 * @param sourceNode
	 * @param facetName
	 * @return
	 */
	public static boolean isFacet(Node sourceNode, String facetName) {
		if (sourceNode != null && sourceNode instanceof Element
				&& sourceNode.getNodeName().equals("f:facet")) { //$NON-NLS-1$
			String name = ((Element) sourceNode).getAttribute("name"); //$NON-NLS-1$
			if (facetName.equals(name)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns Style with default background image for default RichFaces skin.
	 * 
	 * @return
	 */
	public static String getHeaderBackgoundImgStyle() {
		return getBackgoundImgStyle("common/background.gif"); //$NON-NLS-1$
	}

	/**
	 * Returns Style with background image for default RichFaces skin.
	 * 
	 * @return
	 */
	public static String getBackgoundImgStyle(String imagePath) {
		String imgPath = ComponentUtil.getAbsoluteResourcePath(imagePath);
		String style = "background-image: url(file:///" //$NON-NLS-1$
				+ imgPath.replace('\\', '/') + ");"; //$NON-NLS-1$
		return style;
	}

	// public static createStyleClass(String content){
	//		
	// }

	/**
	 * Returns value of attribute.
	 * 
	 * @param sourceElement
	 * @param attributeName
	 * @return
	 */
	public static String getAttribute(Element sourceElement,
			String attributeName) {
		String attribute = sourceElement.getAttribute(attributeName);
		if (attribute == null) {
			attribute = ""; //$NON-NLS-1$
		}
		return attribute;
	}

	/**
	 * Returns value of attribute.
	 * 
	 * @param sourceElement
	 * @param attributeName
	 * @return
	 */
	public static String getAttribute(nsIDOMElement sourceElement,
			String attributeName) {
		String attribute = sourceElement.getAttribute(attributeName);
		if (attribute == null) {
			attribute = ""; //$NON-NLS-1$
		}
		return attribute;
	}

	/**
	 * @param style
	 * @param name
	 * @return
	 */
	public static boolean parameterPresent(String style, String name) {
		if (style != null && style.length() > 0) {
			String[] styles = style.split(";"); //$NON-NLS-1$
			for (int i = 0; i < styles.length; i++) {
				String[] pair = styles[i].split(":"); //$NON-NLS-1$
				if (pair[0].trim().equals(name)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * @param style
	 * @param name
	 * @return
	 */
	public static String getStyleParametr(String style, String name) {
		if (style != null && style.length() > 0) {
			String[] styles = style.split(";"); //$NON-NLS-1$
			for (int i = 0; i < styles.length; i++) {
				String[] pair = styles[i].split(":"); //$NON-NLS-1$
				if (pair[0].trim().equals(name)) {
					return pair[1].trim();
				}
			}
		}
		return null;
	}

	/**
	 * 
	 * @param style
	 * @param element
	 * @return
	 */
	public static String addParameter(String style, String element) {
		String s = style.trim();
		return style + (s.length() == 0 || s.endsWith(";") ? "" : ";") //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
				+ element;
	}

	/**
	 * Adds image as attribute to IMG tag from users worcpace
	 * 
	 * @param pageContext
	 *            Page Context
	 * @param img
	 *            img element to which set picture
	 * @param fileImageName
	 *            image name
	 * @param undefinedImgName
	 *            default image when image is undefined
	 */
	public static void setImgFromResources(VpePageContext pageContext,
			nsIDOMElement img, String fileImageName, String undefinedImgName) {
		IEditorInput input = pageContext.getEditPart().getEditorInput();
		IPath inputPath = getInputParentPath(input);
		File file = new File(inputPath.toOSString() + File.separator
				+ fileImageName);
		if (file.exists()) {
			img.setAttribute(HtmlComponentUtil.HTML_ATR_SRC,
					HtmlComponentUtil.FILE_PROTOCOL + inputPath.toOSString()
							+ File.separator + fileImageName);
		} else {
			img.setAttribute(HtmlComponentUtil.HTML_ATR_SRC, undefinedImgName);
		}
	}

	/**
	 * Open file
	 * 
	 * @param pageContext
	 *            Page Context
	 * @param fileName
	 *            file name
	 * @return file
	 */
	public static File openFile(VpePageContext pageContext, String fileName) {
		IEditorInput input = pageContext.getEditPart().getEditorInput();
		IPath inputPath = getInputParentPath(input);
		File file = new File(inputPath.toOSString() + File.separator + fileName);
		return file;
	}

	/**
	 * Returns locale of user input
	 * 
	 * @param input
	 * @return
	 */
	public static IPath getInputParentPath(IEditorInput input) {
		IPath inputPath = null;
		if (input instanceof ILocationProvider) {
			inputPath = ((ILocationProvider) input).getPath(input);
		} else if (input instanceof IFileEditorInput) {
			IFile inputFile = ((IFileEditorInput) input).getFile();
			if (inputFile != null) {
				inputPath = inputFile.getLocation();
			}
		}
		if (inputPath != null && !inputPath.isEmpty()) {
			inputPath = inputPath.removeLastSegments(1);
		}
		return inputPath;
	}

	/**
	 * Move attributes from sourceNode to html
	 * 
	 * @param sourceNode
	 * @param visualNode
	 * @param attrName
	 * @param htmlAttrName
	 * @param prefValue
	 * @param defValue
	 */
	public static void correctAttribute(Element sourceNode,
			nsIDOMElement visualNode, String attrName, String htmlAttrName,
			String prefValue, String defValue) {
		String attrValue = ((Element) sourceNode).getAttribute(attrName);
		if (prefValue != null && prefValue.trim().length() > 0
				&& attrValue != null) {
			attrValue = prefValue.trim() + " " + attrValue; //$NON-NLS-1$
		}
		if (attrValue != null) {
			visualNode.setAttribute(htmlAttrName, attrValue);
		} else if (defValue != null) {
			visualNode.setAttribute(htmlAttrName, defValue);
		} else
			visualNode.removeAttribute(attrName);
	}

	/**
	 * Parses string value retrieved from sourceElement.getAttribure(..) method
	 * to its boolean value.
	 * <p>
	 * <code>false</code> is returned only if it specified explicitly,
	 * otherwise <code>true</code> is returned.
	 * 
	 * @param str
	 *            the string to parse
	 * @return boolean value from string
	 */
	public static boolean string2boolean(String str) {
		if ((str == null) || ("".equals(str))) { //$NON-NLS-1$
			return true;
		} else if (("true".equalsIgnoreCase(str)) //$NON-NLS-1$
				|| ("false".equalsIgnoreCase(str))) { //$NON-NLS-1$
			return new Boolean(str).booleanValue();
		}
		return true;
	}

	/**
	 * find elements by name
	 * 
	 * @param node -
	 *            current node
	 * @param elements -
	 *            list of found elements
	 * @param name -
	 *            name element
	 */
	static public void findElementsByName(Node node, List<Node> elements,
			String name) {
		// get children
		NodeList children = node.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			Node child = children.item(i);
			// if current child is required then add his to list
			if (name.equalsIgnoreCase((child.getNodeName()))) {
				elements.add(child);
			} else {
				findElementsByName(child, elements, name);
			}
		}
	}

	/**
	 * find all elements by name
	 * 
	 * @param node -
	 *            current node
	 * @param elements -
	 *            list of found elements
	 * @param name -
	 *            name element
	 */
	static public void findAllElementsByName(nsIDOMNode node,
			List<nsIDOMNode> elements, String name) {

		try {
			nsIDOMNodeList list = node.getChildNodes();
			if (node.getNodeName().equalsIgnoreCase(name)) {
				elements.add(node);
			}
			for (int i = 0; i < list.getLength(); i++) {
				findAllElementsByName(list.item(i), elements, name);
			}
		} catch (XPCOMException e) {
			// Ignore
			return;
		}
	}

}