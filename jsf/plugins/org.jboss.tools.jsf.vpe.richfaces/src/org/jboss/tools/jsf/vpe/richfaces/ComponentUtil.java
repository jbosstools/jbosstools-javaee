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
import java.math.BigDecimal;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.editors.text.ILocationProvider;
import org.jboss.tools.jsf.vpe.richfaces.template.util.RichFaces;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.util.Constants;
import org.jboss.tools.vpe.editor.util.ElService;
import org.jboss.tools.vpe.editor.util.FileUtil;
import org.jboss.tools.vpe.editor.util.HTML;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMNode;
import org.mozilla.interfaces.nsIDOMNodeList;
import org.mozilla.xpcom.XPCOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;


/**
 * The Class ComponentUtil.
 */
public class ComponentUtil {

    /** The Constant EMPTY_SELECT_ITEM_VALUE. */
    private static final String EMPTY_SELECT_ITEM_VALUE = "<f:selectItem/>"; //$NON-NLS-1$

    /** The Constant EMPTY_SELECT_ITEMS_VALUE. */
    private static final String EMPTY_SELECT_ITEMS_VALUE = "<f:selectItems/>"; //$NON-NLS-1$

    /** The Constant SELECT_ITEMS. */
    private static final String SELECT_ITEMS = "selectItems"; //$NON-NLS-1$

    /** The Constant SELECT_ITEM. */
    private static final String SELECT_ITEM = "selectItem"; //$NON-NLS-1$

    /**
     * Gets child of Facet element by name. If facet has a few children the
     * method will return first one.
     * 
     * @param sourceElement the source element
     * @param facetName the facet name
     * @param returnTextNode return child text node if facet doesn't have any child elements;
     * 
     * @return the facet
     */
    public static Node getFacet(Element sourceElement, String facetName, boolean returnTextNode) {
        NodeList children = sourceElement.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node node = children.item(i);
            if (node instanceof Element && node.getNodeName() != null && node.getNodeName().indexOf(":facet") > 0) { //$NON-NLS-1$
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
     * @param sourceElement the source element
     * @param facetName      *
     * param returnTextNode return child text node if facet doesn't have
     * any child elements;
     * @param returnTextNode the return text node
     * 
     * @return the facets
     */
    public static ArrayList<Node> getFacets(Element sourceElement, boolean returnTextNode) {
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
     * @param sourceElement the source element
     * @param facetName the facet name
     * 
     * @return the facet
     */
    public static Element getFacet(Element sourceElement, String facetName) {
        return (Element) getFacet(sourceElement, facetName, false);
    }
    
    /**
     * Gets the facet element.
     * 
     * @param sourceElement the source element
     * @param facetName the facet name
     * @param returnTextNode the return text node
     * 
     * @return the facet element
     */
    public static Element getFacetElement(Element sourceElement,
	    String facetName) {
	NodeList children = sourceElement.getChildNodes();
	for (int i = 0; i < children.getLength(); i++) {
	    Node node = children.item(i);
	    if (node instanceof Element && node.getNodeName() != null
		    && node.getNodeName().indexOf(":facet") > 0) { //$NON-NLS-1$
		Element facetElement = (Element) node;
		String name = facetElement.getAttribute("name"); //$NON-NLS-1$
		if (facetName.equals(name)) {
		    return facetElement;
		}
	    }
	}
	return null;
    }
    
    
    /**
	 * 
	 * @param parentElement
	 * @param facetName
	 * @param order
	 *            - indicate what facet should take first or last.
	 * @return
	 */
    public static Element getFacetElement(Element parentElement,
			String facetName, boolean last) {

		NodeList children = parentElement.getChildNodes();

		if (children != null) {
			
			int index = last ? children.getLength()-1 : 0;
			int step = last ? -1 : 1;
			int stopIndex = last ? -1 : children.getLength();

			while (index != stopIndex) {

				Node child = children.item(index);

				if ((child.getNodeType() == Node.ELEMENT_NODE)
						&& RichFaces.TAG_FACET.equals(child.getLocalName())
						&& facetName.equals(((Element) child)
								.getAttribute(RichFaces.ATTR_NAME))) {
					return (Element) child;
				}

				index += step;

			}

		}
    	
    	return null;
    }

    /**
     * Returns true if "rendered" attribute of source element does not contain
     * "false" value.
     * 
     * @param sourceElement the source element
     * 
     * @return true, if is rendered
     */
    public static boolean isRendered(Element sourceElement) {
        return !Constants.FALSE.equalsIgnoreCase(sourceElement.getAttribute("rendered")); //$NON-NLS-1$
    }

    /**
     * Sets CSS link in visual html document.
     * 
     * @param ext the ext
     * @param pageContext the page context
     * @param cssHref the css href
     */
    public static void setCSSLink(VpePageContext pageContext, String cssHref, String ext) {
        String pluginPath = RichFacesTemplatesActivator.getPluginResourcePath();
        IPath pluginFile = new Path(pluginPath);
        File cssFile = pluginFile.append(cssHref).toFile();
        if (cssFile.exists()) {
            String cssPath = "file:///" + cssFile.getPath().replace('\\', '/'); //$NON-NLS-1$
            pageContext.getVisualBuilder().replaceLinkNodeToHead(cssPath, ext, true);
        }
    }

    /**
     * Gets the absolute resource path.
     * 
     * @param resourcePathInPlugin the resource path in plugin
     * 
     * @return the absolute resource path
     */
    public static String getAbsoluteResourcePath(String resourcePathInPlugin) {
        String pluginPath = RichFacesTemplatesActivator.getPluginResourcePath();
        IPath pluginFile = new Path(pluginPath);
        File file = pluginFile.append(resourcePathInPlugin).toFile();
        if (file.exists()) {
            return file.getAbsolutePath();
        } else {
            throw new IllegalArgumentException("Can't get path for " //$NON-NLS-1$
                    + resourcePathInPlugin);
        }
    }
    
    /**
     * Gets the absolute workspace path.
     * 
     * @param resourcePathInWorkspace the relative path in workspace
     * 
     * @return the absolute workspace path
     */
    public static String getAbsoluteWorkspacePath(
			String resourcePathInWorkspace, VpePageContext pageContext) {

		String resolvedValue = resourcePathInWorkspace
				.replaceFirst(
						"^\\s*(\\#|\\$)\\{facesContext.externalContext.requestContextPath\\}", Constants.EMPTY); //$NON-NLS-1$

		IFile file = null;
		if (pageContext.getVisualBuilder().getCurrentIncludeInfo() != null
				&&(pageContext.getVisualBuilder().getCurrentIncludeInfo().getStorage() instanceof IFile)) {
			file = (IFile) pageContext.getVisualBuilder().getCurrentIncludeInfo()
					.getStorage();
		}
		if (file == null)
			return resolvedValue;

		resolvedValue = ElService.getInstance().replaceEl(file, resolvedValue);

		URI uri = null;
		try {
			uri = new URI(resolvedValue);
		} catch (URISyntaxException e) {
		}

		if ((uri != null)
				&& (uri.isAbsolute() || (new File(resolvedValue)).exists()))
			return resolvedValue;

		return Constants.FILE_PREFIX
				+ FileUtil.getFile(resolvedValue, file).getLocation()
						.toPortableString();
	}

    /**
     * Adds image as attribute to IMG tag.
     * 
     * @param img the img
     * @param fileImageName the file image name
     */
    public static void setImg(nsIDOMElement img, String fileImageName) {
        img.setAttribute(HTML.ATTR_SRC, "file://" //$NON-NLS-1$
                + getAbsoluteResourcePath(fileImageName).replace('\\', '/'));
    }
    
    /**
     * Sets the attribute to element children
     * 
     * @param attrName
     *            attribute name
     * @param attrValue
     *            attribute value
     * @param children
     *            children
     */
    public static void applyAttributeValueOnChildren(String attrName,
	    String attrValue, List<nsIDOMElement> children) {
	if (children == null || attrName == null || attrValue == null) {
	    return;
	}
	for (nsIDOMElement child : children) {
	    child.setAttribute(attrName, attrValue);
	    applyAttributeValueOnChildren(attrName, attrValue,
		    getElementChildren(child));
	}
    }

    /**
     * Gets element's children of type nsIDOMElement. No text nodes will be
     * returned.
     * 
     * @param element
     *            the element
     * @return children the list of the elements
     */
    public static List<nsIDOMElement> getElementChildren(nsIDOMElement element) {
	List<nsIDOMElement> result = new ArrayList<nsIDOMElement>();
	if (element.hasChildNodes()) {
	    nsIDOMNodeList children = element.getChildNodes();
	    if (null != children) {
		long len = children.getLength();
		for (int i = 0; i < len; i++) {
		    nsIDOMNode item = children.item(i);
		    try {
			nsIDOMElement elem = (nsIDOMElement) item
				.queryInterface(nsIDOMElement.NS_IDOMELEMENT_IID);
			result.add(elem);
		    } catch (XPCOMException ex) {
			// just ignore this exception
		    }
		}
	    }
	}
	return result;
    }

    /**
     * Returns all child source elements of component but facets.
     * 
     * @param returnTextNodes return child text nodes and elements or elements only;
     * @param sourceElement the source element
     * 
     * @return the children
     */
    public static List<Node> getChildren(Element sourceElement, boolean returnTextNodes) {
        ArrayList<Node> children = new ArrayList<Node>();
        NodeList nodeList = sourceElement.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node child = nodeList.item(i);
            if ((child instanceof Element || returnTextNodes) && (!child.getNodeName().equals("f:facet"))) { //$NON-NLS-1$
                children.add(child);
            }
        }
        return children;
    }

    /**
     * Returns all child source elements of component but facets.
     * 
     * @param sourceElement the source element
     * 
     * @return the children
     */
    public static List<Node> getChildren(Element sourceElement) {
        return getChildren(sourceElement, false);
    }

    /**
     * Returns all child visual elements of component but facets.
     * 
     * @param returnTextNodes the return text nodes
     * @param visualElement the visual element
     * 
     * @return the children
     * 
     * return returnTextNodes return child text nodes and elements or elements
     * only;
     */
    public static List<nsIDOMNode> getChildren(nsIDOMElement visualElement, boolean returnTextNodes) {
        ArrayList<nsIDOMNode> children = new ArrayList<nsIDOMNode>();
        nsIDOMNodeList nodeList = visualElement.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            nsIDOMNode child = nodeList.item(i);
            if ((child instanceof nsIDOMElement || returnTextNodes) && (!child.getNodeName().equals("f:facet"))) { //$NON-NLS-1$
                children.add(child);
            }
        }
        return children;
    }

    /**
     * Returns all child visual elements of component but facets.
     * 
     * @param visualElement the visual element
     * 
     * @return the children
     */
    public static List<nsIDOMNode> getChildren(nsIDOMElement visualElement) {
        return getChildren(visualElement, false);
    }

    /**
     * Returns true if sourceNode is Facet.
     * 
     * @param sourceNode the source node
     * @param facetName the facet name
     * 
     * @return true, if is facet
     */
    public static boolean isFacet(Node sourceNode, String facetName) {
        if (sourceNode != null && sourceNode instanceof Element && sourceNode.getNodeName().equals("f:facet")) { //$NON-NLS-1$
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
     * @return the header backgound img style
     */
    public static String getHeaderBackgoundImgStyle() {
        return getBackgoundImgStyle("common/background.gif"); //$NON-NLS-1$
    }

    /**
     * Returns Style with background image for default RichFaces skin.
     * 
     * @param imagePath the image path
     * 
     * @return the backgound img style
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
     * @param attributeName the attribute name
     * @param sourceElement the source element
     * 
     * @return the attribute
     */
    public static String getAttribute(Element sourceElement, String attributeName) {
        String attribute = sourceElement.getAttribute(attributeName);
        if (attribute == null) {
            attribute = Constants.EMPTY;
        }
        return attribute;
    }

    /**
     * Returns value of attribute.
     * 
     * @param attributeName the attribute name
     * @param sourceElement the source element
     * @param defaultValue the default value
     *
     * @return the attribute
     */
    public static String getAttribute(Element sourceElement, String attributeName, String defaultValue) {
        String attribute = sourceElement.getAttribute(attributeName);
        if (attribute == null) {
            attribute = defaultValue;
        }
        return attribute;
    }

    /**
     * Returns value of attribute.
     * 
     * @param attributeName the attribute name
     * @param sourceElement the source element
     * 
     * @return the attribute
     */
    public static String getAttribute(nsIDOMElement sourceElement, String attributeName) {
        String attribute = sourceElement.getAttribute(attributeName);
        if (attribute == null) {
            attribute = Constants.EMPTY;
        }
        return attribute;
    }

    /**
     * Parameter present.
     * 
     * @param style the style
     * @param name the name
     * 
     * @return true, if parameter present
     */
    public static boolean parameterPresent(String style, String name) {
        if (style != null && style.length() > 0) {
            String[] styles = style.split(Constants.SEMICOLON);
            for (int i = 0; i < styles.length; i++) {
                String[] pair = styles[i].split(Constants.COLON);
                if (pair[0].trim().equals(name)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Gets the style parametr.
     * 
     * @param style the style
     * @param name the name
     * 
     * @return the style parametr
     */
    public static String getStyleParametr(String style, String name) {
        if (style != null && style.length() > 0) {
            String[] styles = style.split(Constants.SEMICOLON);
            for (int i = 0; i < styles.length; i++) {
                String[] pair = styles[i].split(Constants.COLON);
                if (pair[0].trim().equals(name)) {
                    return pair[1].trim();
                }
            }
        }
        return null;
    }

    /**
     * Adds the parameter.
     * 
     * @param element the element
     * @param style the style
     * 
     * @return the string
     */
    public static String addParameter(String style, String element) {
        String s = style.trim();
        return style + (s.length() == 0 || s.endsWith(Constants.SEMICOLON) ? Constants.EMPTY : Constants.SEMICOLON)
                + element;
    }

    /**
     * Adds image as attribute to IMG tag from users workspace.
     * 
     * @param img image element to set picture to
     * @param pageContext Page Context
     * @param undefinedImgName default image when image is undefined
     * @param fileImageName image name
     */
    public static void setImgFromResources(VpePageContext pageContext, nsIDOMElement img, String fileImageName, String undefinedImgName) {
        IEditorInput input = pageContext.getEditPart().getEditorInput();
        IPath inputPath = getInputParentPath(input);
        //Fix For JBIDE-3030
        if(pageContext.getVisualBuilder().getCurrentIncludeInfo()==null
        		||!(pageContext.getVisualBuilder().getCurrentIncludeInfo().getStorage() instanceof IFile)) {
        	return;
        }
        String path = ElService.getInstance().replaceEl((IFile)pageContext.getVisualBuilder().getCurrentIncludeInfo().getStorage(), fileImageName);
        File file = new File(inputPath.toOSString() + File.separator + path);
        if (file.exists()) {
            img.setAttribute(HTML.ATTR_SRC, HtmlComponentUtil.FILE_PROTOCOL + inputPath.toString() + "/" //$NON-NLS-1$
                    + path.replace('\\', '/'));
        } else {
            img.setAttribute(HTML.ATTR_SRC, undefinedImgName.replace('\\', '/'));
        }
    }

    /**
     * Open file.
     * 
     * @param fileName file name
     * @param pageContext Page Context
     * 
     * @return file
     */
    public static File openFile(VpePageContext pageContext, String fileName) {
        IEditorInput input = pageContext.getEditPart().getEditorInput();
        IPath inputPath = getInputParentPath(input);
        File file = new File(inputPath.toOSString() + File.separator + fileName);
        return file;
    }

    /**
     * Returns locale of user input.
     * 
     * @param input the input
     * 
     * @return the input parent path
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
     * Move attributes from sourceNode to targetNode.
     *
     * @param sourceNode source node to copy the attribute from
     * @param targetNode target visual node
     * @param sourceAttrName source attribute name to copy the attribute value from
     * @param targetAttrName target attribute name
     * @param prefValue the value that will be concatenated with source attribute and set to
     * target attribute if the source attribute exists
     * @param defValue the value that will be set to target attribute is the source node
     * does not have specified attribute
     */
    public static void correctAttribute(Element sourceNode, nsIDOMElement targetNode,
    		String sourceAttrName, String targetAttrName,
            String prefValue, String defValue) {
        String attrValue = ((Element) sourceNode).getAttribute(sourceAttrName);
        if (prefValue != null && prefValue.trim().length() > 0 && attrValue != null) {
            attrValue = prefValue.trim() + Constants.WHITE_SPACE + attrValue;
        }
        if (attrValue != null) {
            targetNode.setAttribute(targetAttrName, attrValue);
        } else if (defValue != null) {
            targetNode.setAttribute(targetAttrName, defValue);
        } else {
        	// FIXME: probably a bugged line - 
        	// removing source attribute from target node [commented by yradtsevich]
            targetNode.removeAttribute(sourceAttrName);
        }
    }

    /**
     * Parses string value retrieved from sourceElement.getAttribure(..) method
     * to its boolean value.
     * <p>
     * <code>false</code> is returned only if it specified explicitly, otherwise
     * <code>true</code> is returned.
     * 
     * @param str the string to parse
     * 
     * @return boolean value from string
     */
    public static boolean string2boolean(String str) {
    	if (Constants.FALSE.equals(str)) {
    		return false;
    	} else {
    		return true;
    	}
    }

    /**
     * find elements by name.
     * 
     * @param node - current node
     * @param name - name element
     * @param elements - list of found elements
     */
    static public void findElementsByName(Node node, List<Node> elements, String name) {
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
     * find all elements by name.
     * 
     * @param node - current node
     * @param name - name element
     * @param elements - list of found elements
     */
    static public void findAllElementsByName(nsIDOMNode node, List<nsIDOMNode> elements, String name) {

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

  
    /**
     * Parses the width height value.
     * 
     * @param value the value
     * 
     * @return the integer
     */
    public static Integer parseWidthHeightValue(String value) {
        Integer rst = null;

        if ((value == null) || (value.trim().length() == 0)) {
            throw new NumberFormatException("Passed value is empty "); //$NON-NLS-1$
        }

        if (value.endsWith(Constants.PIXEL)) {
            rst = Integer.parseInt(value.substring(0, value.length() - 2));
        } else {
            rst = Integer.parseInt(value);
        }

        return rst;
    }

    /**
     * Parses the size attribute.
     * 
     * @param sourceElement the source element
     * @param attributeName the attribute name
     * @param defaultValue the default value
     * 
     * @return the int size
     */
    public static int parseSizeAttribute(Element sourceElement, String attributeName,
	    int defaultValue) {

	if (sourceElement.hasAttribute(attributeName)) {
	    String attrValue = sourceElement.getAttribute(attributeName);

	    if (attrValue.endsWith(Constants.PIXEL))
		attrValue = attrValue.substring(0, attrValue.length()
			- Constants.PIXEL.length());

	    try {
		/*
		 * Decoding attribute's value
		 */
		int intValue = Integer.decode(attrValue);
		return intValue;
	    } catch (NumberFormatException e) {
		/*
		 * if attribute's value is not a number do nothing 
		 * return default value
		 */
	    }
	}
	return defaultValue;
    }

    /**
     * Parses the number attribute.
     * 
     * @param sourceElement the source element
     * @param attributeName the attribute name
     * @param defaultValue the default value
     * 
     * @return the int number
     */
    public static int parseNumberAttribute(Element sourceElement,
	    String attributeName, int defaultValue) {

	if (sourceElement.hasAttribute(attributeName)) {
	    String attrValue = sourceElement.getAttribute(attributeName);

	    try {
		/*
		 * Decoding attribute's value
		 */
		int intValue = Integer.decode(attrValue);
		return intValue;
	    } catch (NumberFormatException e) {
		/*
		 * if attribute's value is not a number do nothing 
		 * return default value
		 */
	    }
	}
	return defaultValue;
    }
    
    /**
     * Checks if is blank.
     * 
     * @param value the value
     * 
     * @return true, if is blank
     */
    public static boolean isBlank(String value) {
        return ((value == null) || (value.trim().length() == 0));
    }

    /**
     * Checks if is not blank.
     * 
     * @param value the value
     * 
     * @return true, if is not blank
     */
    public static boolean isNotBlank(String value) {
        return !isBlank(value);
    }

    /**
     * Gets the select items.
     * 
     * @param children the children
     * 
     * @return the select items
     */
    public static List<Element> getSelectItems(NodeList children) {
        final List<Element> selectItems = new ArrayList<Element>();

        for (int i = 0; i < children.getLength(); i++) {
            final Node child = children.item(i);

            if ((child instanceof Element) && (child.getNodeName().indexOf(SELECT_ITEM)) > 1) {
                selectItems.add((Element) child);
            }
        }
        return selectItems;
    }
    
    /**
     * Gets the select item value.
     * 
     * @param e the e
     * 
     * @return the select item value
     */
    public static String getSelectItemValue(Element e) {
        String value = e.getAttribute(RichFaces.ATTR_SELECT_ITEM_LABEL);

        if (isBlank(value)) {
            value = e.getAttribute(RichFaces.ATTR_SELECT_ITEM_VALUE);
            if (ComponentUtil.isBlank(value)) {
                if (e.getNodeName().endsWith(SELECT_ITEMS)) {
                    value = e.getAttribute(RichFaces.ATTR_VALUE);

                    if (ComponentUtil.isBlank(value)) {
                        value = EMPTY_SELECT_ITEMS_VALUE;
                    }
                } else if (e.getNodeName().endsWith(SELECT_ITEM)) {
                    value = EMPTY_SELECT_ITEM_VALUE;
                }
            }
        }
        return value;
    }

    /**
     * Encodes style class
     * @param parentPredefined
     * @param predefined
     * @param parent
     * @param custom
     * @return
     */
    public static String encodeStyleClass(Object parentPredefined,
	    Object predefined, Object parent, Object custom) {
	StringBuffer styleClass = new StringBuffer();
	// Construct predefined classes
	if (null != parentPredefined) {
	    styleClass.append(parentPredefined).append(Constants.WHITE_SPACE);
	} else if (null != predefined) {
	    styleClass.append(predefined).append(Constants.WHITE_SPACE);
	}
	// Append class from parent component.
	if (null != parent) {
	    styleClass.append(parent).append(Constants.WHITE_SPACE);
	}
	if (null != custom) {
	    styleClass.append(custom);
	}
	if (styleClass.length() > 0) {
	    return styleClass.toString();
	}
	return null;
    }


	/**
	 * Returns columns with facet.
	 * @param columns
	 * @param facetName
	 * @return list of columns with facet
	 */
	public static ArrayList<Element> getColumnsWithFacet(
			ArrayList<Element> columns, String facetName) {
		ArrayList<Element> columnsWithFacet = new ArrayList<Element>();
		for (Element column : columns) {
			Element body = getFacet(column, facetName);
			if (body != null) {
				columnsWithFacet.add(column);
			}
		}
		return columnsWithFacet;
	}
	
	/**
	 * Parses string width value from 'px' into 'em'.
	 * 
	 * @param widthAttribute width attribute value.
	 * @return width value in 'em' or -1 if the value wasn't parsed.
	 */
	public static double parseWidth(String widthAttribute) {
		double widthDouble = -1;
		if (ComponentUtil.isNotBlank(widthAttribute)) {
			try {
				int widthInt = Integer.parseInt(widthAttribute);
				/*
				 * Parse 'px' to 'em'.
				 */
				widthDouble = widthInt / 13.33333;
				/*
				 * Set Double fraction precision to 5 numbers.
				 */
				BigDecimal b = new BigDecimal(widthDouble).setScale(5,
						BigDecimal.ROUND_HALF_UP);
				widthDouble = b.doubleValue();
			} catch (NumberFormatException e) {
				/*
				 * Do nothing, default width will be applied.
				 */
			}
		}
		return widthDouble;
	}
}