/******************************************************************************* 
 * Copyright (c) 2007-2009 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.jsf.vpe.seam.template;

/**
 * @author yzhishko
 */

import java.util.StringTokenizer;

import org.jboss.tools.jsf.vpe.seam.template.util.SeamUtil;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.util.HTML;
import org.jboss.tools.vpe.editor.util.VisualDomUtil;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMNode;
import org.mozilla.interfaces.nsIDOMNodeList;
import org.mozilla.interfaces.nsIDOMText;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class SeamPdfSectionTemplate extends SeamPdfAbstractTemplate {

	private nsIDOMElement visualElement;
	private Element sourceElement;
	private String sectionNumberString;
	private String headNameString;


	public VpeCreationData create(VpePageContext pageContext, Node sourceNode,
			nsIDOMDocument visualDocument) {
		visualElement = VisualDomUtil.createBorderlessContainer(visualDocument);
		sourceElement = (Element) sourceNode;
		sectionNumberString = calculateSectionNumber(pageContext, sourceElement);
		headNameString = calculateHeadName(sectionNumberString);
		nsIDOMNode headNode = visualDocument.createElement(headNameString);
		nsIDOMText textNode = visualDocument
				.createTextNode(sectionNumberString);
		headNode.appendChild(textNode);
		visualElement.appendChild(headNode);
		return new VpeCreationData(visualElement);
	}

	private String calculateSectionNumber(VpePageContext pageContext,
			Element sourceElement) {
		StringBuffer chapterNumberBuffer = new StringBuffer();
		calculateNumberFromTree(pageContext, sourceElement, chapterNumberBuffer);
		Node parentSection = SeamUtil.getParentByName(pageContext,
				sourceElement, "p:chapter");
		if (parentSection != null) {
			int chapterNumber = getChapterNumber(parentSection);
			chapterNumberBuffer
					.insert(0, Integer.toString(chapterNumber) + ".");
		}
		return chapterNumberBuffer.toString();
	}

	private String calculateHeadName(String sectionNumberString) {
		StringTokenizer tokenizer = new StringTokenizer(sectionNumberString,
				".", false);
		int headNumber = 0;
		while (tokenizer.hasMoreElements()) {
			tokenizer.nextToken();
			headNumber++;
		}
		if (headNumber > 6) {
			headNumber = 6;
		}
		return "H" + Integer.toString(headNumber);
	}

	private void calculateNumberFromTree(VpePageContext pageContext,
			Element sourceElement, StringBuffer sectionNumberString) {
		Node parentSection = SeamUtil.getParentByName(pageContext,
				sourceElement, "p:section");
		if (parentSection != null) {
			int sectionNum = 0;
			NodeList children = parentSection.getChildNodes();
			for (int i = 0; i < children.getLength(); i++) {
				if (children.item(i).getNodeName().endsWith(":section")) {
					sectionNum++;
					if (children.item(i) == sourceElement) {
						sectionNumberString.insert(0, Integer
								.toString(sectionNum)
								+ ".");
						break;
					}
				}
			}
			calculateNumberFromTree(pageContext, (Element) parentSection,
					sectionNumberString);
		} else {
			Node parentChapter = SeamUtil.getParentByName(pageContext,
					sourceElement, "p:chapter");
			int sectionNum = 0;
			NodeList children = parentChapter.getChildNodes();
			for (int i = 0; i < children.getLength(); i++) {
				if (children.item(i).getNodeName().endsWith(":section")) {
					sectionNum++;
					if (children.item(i) == sourceElement) {
						sectionNumberString.insert(0, Integer
								.toString(sectionNum)
								+ ".");
						break;
					}
				}
			}
		}
	}

	private int getChapterNumber(Node chapterNode) {
		int chapterNumber = 1;
		String chapterNumberString = ((Element) chapterNode)
				.getAttribute("number");
		if (chapterNumberString != null) {
			try {
				chapterNumber = Integer.parseInt(chapterNumberString);
			} catch (NumberFormatException e) {
				chapterNumber = 1;
			}
		} else {
			chapterNumber = 1;
		}
		return chapterNumber;
	}

	private void setTitle(VpePageContext pageContext, Element sourceElement,
			VpeCreationData data) {
		Node sourceTitleNode = null;
		NodeList children = sourceElement.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			if (children.item(i) instanceof Element) {
				if (children.item(i).getNodeName().endsWith(":title")) {
					sourceTitleNode = children.item(i);
				}
			}
		}
		nsIDOMNode visualTitleNode = null;
		if (sourceTitleNode != null) {
			visualTitleNode = pageContext.getDomMapping().getVisualNode(
					sourceTitleNode);
		}
		if (visualTitleNode != null) {
			nsIDOMElement headElement = getHeadElement(data);
			nsIDOMNode parentNode = visualTitleNode.getParentNode();
			if (parentNode != null) {
				parentNode.removeChild(visualTitleNode);
				headElement.appendChild(visualTitleNode);
			}
		}
	}

	private nsIDOMElement getHeadElement(VpeCreationData data) {
		nsIDOMNode visualNode = data.getNode();
		nsIDOMNodeList children = visualNode.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			if (children.item(i).getNodeType() == nsIDOMNode.ELEMENT_NODE) {
				String nodeName = children.item(i).getNodeName();
				if (HTML.TAG_H1.equalsIgnoreCase(nodeName)
						|| HTML.TAG_H2.equalsIgnoreCase(nodeName)
						|| HTML.TAG_H3.equalsIgnoreCase(nodeName)
						|| HTML.TAG_H4.equalsIgnoreCase(nodeName)
						|| HTML.TAG_H5.equalsIgnoreCase(nodeName)
						|| HTML.TAG_H6.equalsIgnoreCase(nodeName)) {
					return (nsIDOMElement) children.item(i).queryInterface(
							nsIDOMElement.NS_IDOMELEMENT_IID);
				}
			}
		}
		return null;
	}

	@Override
	public void validate(VpePageContext pageContext, Node sourceNode,
			nsIDOMDocument visualDocument, VpeCreationData data) {
		setTitle(pageContext, (Element) sourceNode, data);
	}
	
}
