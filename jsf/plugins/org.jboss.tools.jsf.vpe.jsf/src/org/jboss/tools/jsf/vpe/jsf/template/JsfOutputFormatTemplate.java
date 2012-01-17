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
package org.jboss.tools.jsf.vpe.jsf.template;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.wst.xml.core.internal.provisional.document.IDOMAttr;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.util.HTML;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author Sergey Dzmitrovich
 * 
 * template for <h:outputFormat .../> jsf tag
 * 
 */
public class JsfOutputFormatTemplate extends AbstractOutputJsfTemplate {

	/**
	 * name of "choice"
	 */
	private static final String CHOICE_NAME = "choice"; //$NON-NLS-1$

	/**
	 * message format elements separator
	 */
	private static final String MESSAGE_FORMAT_ELEMENTS_SEPARATOR = ","; //$NON-NLS-1$

	/**
	 * choices separator
	 */
	private static final String CHOICES_SEPARATOR = "\\|"; //$NON-NLS-1$

	/**
	 * choices separator
	 */
	private static final String CHOICE_PAIR_SEPARATOR = "#"; //$NON-NLS-1$

	/**
	 * message format elements pattern
	 */
	private static final String MESSAGE_FORMAT_ELEMENTS_PATTERN = "\\{(\\d+)(,.*?){0,2}?\\}"; //$NON-NLS-1$

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.jboss.tools.vpe.editor.template.VpeTemplate#create(org.jboss.tools.vpe.editor.context.VpePageContext,
	 *      org.w3c.dom.Node, org.mozilla.interfaces.nsIDOMDocument)
	 */
	public VpeCreationData create(VpePageContext pageContext, Node sourceNode,
			nsIDOMDocument visualDocument) {

		Element element = (Element) sourceNode;

		// create span element
		nsIDOMElement span = visualDocument.createElement(HTML.TAG_SPAN);

		// creation data
		VpeCreationData creationData = new VpeCreationData(span);

		// copy attributes
		copyOutputJsfAttributes(span, element);

		processOutputAttribute(pageContext, visualDocument, element, span,
				creationData);

		return creationData;

	}

	@Override
	public boolean recreateAtAttrChange(VpePageContext pageContext,
			Element sourceElement, nsIDOMDocument visualDocument,
			nsIDOMElement visualNode, Object data, String name, String value) {

		return true;
	}

	@Override
	protected String prepareAttrValue(VpePageContext pageContext, Element parent, Attr attr) {
		return prepareAttrValueByParams(attr.getNodeValue(), getParams(parent));
	}

	/**
	 * find message format elements and update value
	 * 
	 * @param nodeValue
	 * @param paramList
	 * @return
	 */
	private String prepareAttrValueByParams(String nodeValue, List<Element> paramList) {
		// matcher
		Matcher matcher = Pattern.compile(MESSAGE_FORMAT_ELEMENTS_PATTERN).matcher(nodeValue);
		int lastPos = 0;
		StringBuilder sb = new StringBuilder();
		while (matcher.find()) {
			// get next message format elements
			String messageFormatElement = matcher.group();
			// set end and start
			int start = matcher.start();
			int end = matcher.end();
			// get value of message format element
			String value = parseMessageFormatElement(messageFormatElement, paramList);
			// update value
			sb.append(nodeValue.substring(lastPos, start));
			sb.append(value);
			lastPos = end;
		}
		sb.append(nodeValue.substring(lastPos));
		return sb.toString();
	}

	/**
	 * get value of message format element
	 * 
	 * @param messageFormatElement
	 * @param paramList
	 * @return
	 */
	private String parseMessageFormatElement(String messageFormatElement,
			List<Element> paramList) {
		String value = null;

		// separate message format element
		String[] parametres = messageFormatElement
				.split(MESSAGE_FORMAT_ELEMENTS_SEPARATOR);

		// if this is "choice" format
		if (parametres.length > 2) {
			String format = parametres[1].trim();
			if (CHOICE_NAME.equalsIgnoreCase(format)) {

				// get "choice" as value
				String choice = getChoice(parametres[2]);
				if (choice != null)
					value = choice.replaceAll("[\\{\\}]", ""); //$NON-NLS-1$//$NON-NLS-2$

			}
		}

		// if this is not "choice" format
		if (value == null) {

			// get number of param
			String paramNumber = parametres[0].trim();
			paramNumber = paramNumber.replaceAll("[\\{\\}]", ""); //$NON-NLS-1$//$NON-NLS-2$

			try {
				// decode
				int num = Integer.decode(paramNumber);

				if (num < paramList.size() && paramList.get(num).hasAttribute(JSF.ATTR_VALUE)) {
					// get param's value
					Element paramElement = paramList.get(num);
					value = paramElement.hasAttribute(JSF.ATTR_VALUE) ? 
								paramElement.getAttribute(JSF.ATTR_VALUE) : null;
				}
			} catch (NumberFormatException e) {
				// illegal param value
			}
		}
		// return or value or starting value
		return value != null ? value : messageFormatElement;
	}

	/**
	 * get choice
	 * 
	 * @param choiceString
	 * @return
	 */
	private String getChoice(String choiceString) {
		// separate all choices
		String[] choices = choiceString.split(CHOICES_SEPARATOR);
		// separate first choice pair(choice value / choice string)
		String[] choice = choices[0].split(CHOICE_PAIR_SEPARATOR);
		// return choice string
		if (choice.length > 1) {
			return choice[1];
		}
		return null;
	}

	/**
	 * get all <f:param .../> tags
	 * 
	 * @param sourcElement
	 * @return
	 */
	private List<Element> getParams(Element sourcElement) {
		NodeList nodeList = sourcElement.getChildNodes();
		List<Element> params = new ArrayList<Element>();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node child = nodeList.item(i);
			if (JSF.TAG_PARAM.equals(child.getLocalName())) {
				params.add((Element) child);
			}
		}
		return params;
	}
}
