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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringBufferInputStream;
import java.util.HashMap;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.core.resources.IFile;
import org.jboss.tools.jsf.vpe.richfaces.HtmlComponentUtil;
import org.jboss.tools.jsf.vpe.richfaces.RichFacesTemplatesActivator;
import org.jboss.tools.vpe.editor.VpeSourceDomBuilder;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeAbstractTemplate;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.template.VpeCreatorUtil;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMText;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

/**
 * 
 * @author ezheleznyakov@exadel.com
 * 
 */
public class RichFacesInsertTemplate extends VpeAbstractTemplate {

	private static String RESOURCE_NOT_FOUND_MESSAGE = "Resource was not found."; //$NON-NLS-1$
	private static String RESOURCE_READING_ERROR_MESSAGE = "Resource reading error."; //$NON-NLS-1$
	private static String HIGHLIGHT_ERROR_MESSAGE = "Error occured during highlight."; //$NON-NLS-1$
	private static String ERROR_MESSAGE_STYLE = "color: red; font-weight: bold;"; //$NON-NLS-1$
	
    private static String SRC_ATTR_NAME = "src"; //$NON-NLS-1$
    private static String HIGHTLIGHT_ATTR_NAME = "highlight"; //$NON-NLS-1$

    private static String CODE_TAG = "code>"; //$NON-NLS-1$

    private static String CLASS = "class="; //$NON-NLS-1$

    private static String STYLE = "style="; //$NON-NLS-1$

    private static String OPEN_BRACKET = "{"; //$NON-NLS-1$
    private static String CLOSE_BRACKET = "}"; //$NON-NLS-1$

    private static String SPACE = "&nbsp;"; //$NON-NLS-1$

    private static String SPAN_TAG = "<span style=\"color: rgb(255,255,255)\">_</span>"; //$NON-NLS-1$

    private static String EMPTY_STRING = ""; //$NON-NLS-1$
    private static String UTF8 = "utf-8"; //$NON-NLS-1$

    private static String HTML = "html"; //$NON-NLS-1$
    private static String XHTML = "xhtml"; //$NON-NLS-1$
    private static String XML = "xml"; //$NON-NLS-1$
    private static String JAVA = "java"; //$NON-NLS-1$
    private static String CPP = "cpp"; //$NON-NLS-1$
    private static String CPLUSPLUS = "c++"; //$NON-NLS-1$
    private static String GROOVY = "groovy"; //$NON-NLS-1$
    private static String LZX = "lzx"; //$NON-NLS-1$

    private nsIDOMDocument visualDocument;

    public VpeCreationData create(VpePageContext pageContext, Node sourceNode,
	    nsIDOMDocument visualDocument) {

	this.visualDocument = visualDocument;

	nsIDOMElement div = visualDocument
		.createElement(HtmlComponentUtil.HTML_TAG_DIV);
	VpeCreationData vpeCreationData = new VpeCreationData(div);

	String srcValue = ((Element) sourceNode).getAttribute(SRC_ATTR_NAME);
	String highlightValue = ((Element) sourceNode)
		.getAttribute(HIGHTLIGHT_ATTR_NAME);
	String finalStr = ""; //$NON-NLS-1$
	String buf = ""; //$NON-NLS-1$

	// if there is no source show error message 
	if ((null == srcValue) || ("".equalsIgnoreCase(srcValue))) { //$NON-NLS-1$
	    div.setAttribute(HtmlComponentUtil.HTML_STYLE_ATTR, ERROR_MESSAGE_STYLE);
	    nsIDOMText text = visualDocument.createTextNode(RESOURCE_NOT_FOUND_MESSAGE);
	    div.appendChild(text);
	    return vpeCreationData;
	}
	BufferedReader br = null;
	try {
	    IFile iFile = VpeCreatorUtil.getFile(srcValue, pageContext);
	    if (iFile==null || !iFile.isAccessible()) {
		div.setAttribute(HtmlComponentUtil.HTML_STYLE_ATTR,
			    ERROR_MESSAGE_STYLE);
		    nsIDOMText text = visualDocument
			    .createTextNode(RESOURCE_READING_ERROR_MESSAGE);
		    div.appendChild(text);
		    return vpeCreationData;
	    }
	    File file = new File(iFile.getLocation().toOSString());
	    br = new BufferedReader(new InputStreamReader(
		    new FileInputStream(file)));
	    while ((buf = br.readLine()) != null)
		finalStr += buf + "\n"; //$NON-NLS-1$

	} catch (IOException e) {
	    div.setAttribute(HtmlComponentUtil.HTML_STYLE_ATTR,
		    ERROR_MESSAGE_STYLE);
	    nsIDOMText text = visualDocument
		    .createTextNode(RESOURCE_READING_ERROR_MESSAGE);
	    div.appendChild(text);
	    return vpeCreationData;
	} finally {
		try {
			if(br!=null) {
				br.close();
			}
		} catch (IOException e) {
			RichFacesTemplatesActivator.getPluginLog().logError(e);
		}
	}

	if (highlightValue == null) {
	   // finalStr = finalStr.replace('\n', ' ');
	    nsIDOMText text = visualDocument.createTextNode(finalStr);
	    div.appendChild(text);
	    return vpeCreationData;
	}

	div.setAttribute(HtmlComponentUtil.HTML_STYLE_ATTR, ERROR_MESSAGE_STYLE);
	nsIDOMText text = visualDocument.createTextNode(HIGHLIGHT_ERROR_MESSAGE);
	div.appendChild(text);
	return vpeCreationData;
    }

    /**
     * 
     * @param str
     * @param highlightValue
     *                highlight attribute value
     */
    private String convertString(String str, String highlightValue) {

	HashMap<String, String> map = new HashMap<String, String>();

	if (highlightValue.equalsIgnoreCase(HTML)
		|| highlightValue.equalsIgnoreCase(XHTML)
		|| highlightValue.equalsIgnoreCase(LZX))
	    highlightValue = XML;
	if (highlightValue.equalsIgnoreCase(GROOVY))
	    highlightValue = JAVA;
	if (highlightValue.equalsIgnoreCase(CPLUSPLUS))
	    highlightValue = CPP;

	String sym = "." + highlightValue + "_"; //$NON-NLS-1$ //$NON-NLS-2$

	for (int i = 0; i < str.length();) {
	    int start = str.indexOf(sym, i);
	    if (start == -1)
		break;
	    int startBracket = str.indexOf(OPEN_BRACKET, start);
	    String key = str.substring(start + 1, startBracket - 1);
	    int endBracket = str.indexOf(CLOSE_BRACKET, startBracket);
	    String value = str.substring(startBracket + 2, endBracket - 2);
	    i = endBracket;
	    map.put(key, value);
	}

	int start = str.indexOf(CODE_TAG);
	int end = str.indexOf(CODE_TAG, start + 1);
	str = str.substring(start - 1, end + 5);

	str = str.replaceAll(CLASS, STYLE);

	Set<String> set = map.keySet();

	for (String key : set) {
	    String value = map.get(key);
	    str = str.replaceAll(key, value);
	}
	str = str.replace(SPACE, SPAN_TAG);
	return str;
    }

    /**
     * 
     * @param fileTransform
     */
    @SuppressWarnings("deprecation")
    public Node parseTransformString(String transformString) {

	DocumentBuilderFactory fact = DocumentBuilderFactory.newInstance();

	DocumentBuilder builder = null;
	Document doc = null;
	Node node = null;
	try {
	    builder = fact.newDocumentBuilder();
	    doc = builder.parse(new StringBufferInputStream(transformString));
	    node = doc.getElementsByTagName("code").item(0); //$NON-NLS-1$
	} catch (IOException e) {
	    return node;
	} catch (SAXException e) {
	    return node;
	} catch (ParserConfigurationException e) {
		return node;
	}
	return node;
    }

    /**
     * 
     * @param node
     * @param el
     * @return
     */
    private void buildVisualNode(Node node, nsIDOMElement el) {

	if (node instanceof Text) {
	    nsIDOMText text = visualDocument.createTextNode(node
		    .getTextContent());
	    el.appendChild(text);

	} else {
	    nsIDOMElement elem = visualDocument.createElement(node
		    .getNodeName());
	    el.appendChild(elem);

	    for (int i = 0; i < node.getAttributes().getLength(); i++)
		elem.setAttribute(node.getAttributes().item(i).getNodeName(),
			node.getAttributes().item(i).getNodeValue());

	    for (int i = 0; i < node.getChildNodes().getLength(); i++)
		buildVisualNode(node.getChildNodes().item(i), elem);
	}
    }

    /**
     * Checks, whether it is necessary to re-create an element at change of
     * attribute
     * 
     * @param pageContext
     *                Contains the information on edited page.
     * @param sourceElement
     *                The current element of the source tree.
     * @param visualDocument
     *                The document of the visual tree.
     * @param visualNode
     *                The current node of the visual tree.
     * @param data
     *                The arbitrary data, built by a method <code>create</code>
     * @param name
     *                Attribute name
     * @param value
     *                Attribute value
     * @return <code>true</code> if it is required to re-create an element at
     *         a modification of attribute, <code>false</code> otherwise.
     */
    public boolean recreateAtAttrChange(VpePageContext pageContext,
	    Element sourceElement, nsIDOMDocument visualDocument,
	    nsIDOMElement visualNode, Object data, String name, String value) {
	return true;
    }
    
    @Override
    public void setSourceAttributeSelection(VpePageContext pageContext,
	    Element sourceElement, int offset, int length, Object data) {
	VpeSourceDomBuilder sourceBuilder = pageContext.getSourceBuilder();
	sourceBuilder.setSelection(sourceElement, 0, 0);
    }
}