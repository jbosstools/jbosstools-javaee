/*******************************************************************************
  * Copyright (c) 2007-2008 Red Hat, Inc.
  * Distributed under license by Red Hat, Inc. All rights reserved.
  * This program is made available under the terms of the
  * Eclipse Public License v1.0 which accompanies this distribution,
  * and is available at http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributor:
  *     Red Hat, Inc. - initial API and implementation
  ******************************************************************************/
package org.jboss.tools.jsf.vpe.jsf.template;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.jboss.tools.jsf.vpe.jsf.template.util.ComponentUtil;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.i18n.MainLocaleProvider;
import org.jboss.tools.vpe.editor.template.VpeAbstractTemplate;
import org.jboss.tools.vpe.editor.template.VpeChildrenInfo;
import org.jboss.tools.vpe.editor.template.VpeCreationData;
import org.jboss.tools.vpe.editor.util.Constants;
import org.jboss.tools.vpe.editor.util.HTML;
import org.jboss.tools.vpe.editor.util.VpeStyleUtil;
import org.mozilla.interfaces.nsIDOMDocument;
import org.mozilla.interfaces.nsIDOMElement;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Creates template for JSF f:view tag.
 * <p>Parses the locale attribute.
 * <p>Adds nested children.
 * 
 * @author dmaliarevich
 */
public class JsfView extends VpeAbstractTemplate {

	private static String TABLE_WIDTH_STYLE = "width: 100%;"; //$NON-NLS-1$
	private static String ATTR_LOCALE = "locale"; //$NON-NLS-1$
	
	/**
	 * Instantiates a new jsf view.
	 */
	public JsfView() {
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.vpe.editor.template.VpeTemplate#create(org.jboss.tools.vpe.editor.context.VpePageContext, org.w3c.dom.Node, org.mozilla.interfaces.nsIDOMDocument)
	 */
	public VpeCreationData create(VpePageContext pageContext, Node sourceNode,
			nsIDOMDocument visualDocument) {
		
		Element sourceElement = (Element)sourceNode;
		nsIDOMElement table = visualDocument.createElement(HTML.TAG_TABLE);
		nsIDOMElement tr = visualDocument.createElement(HTML.TAG_TR);
		nsIDOMElement td = visualDocument.createElement(HTML.TAG_TD);
		nsIDOMElement div = visualDocument.createElement(HTML.TAG_DIV);
		
		table.setAttribute(VpeStyleUtil.ATTRIBUTE_STYLE, TABLE_WIDTH_STYLE);
		td.appendChild(div);
		tr.appendChild(td);
		table.appendChild(tr);

		/*
		 * Variables that are used in locale processing.
		 * By default Locale object will be created for the empty string.
		 */
		String localeString = Constants.EMPTY;
		Locale locale = null;
		
		/*
		 * 1. Get locale value. 
		 * 
		 * 1.1 Parse EL expression from locale atrribute:
		 * VPE cannot resolve runtime values for EL expressions, 
		 * only Substituted and Global EL expression will be resolved.
		 * These expressions are parsed before template creating 
		 * in VpeVisualDomBuilder, f:view template has already got parsed el.
		 * 
		 * 1.2 If there is a default locale specified - use it in any case, 
		 * otherwise get the locale from the attribute. 
		 */
		String defaultLocaleString = MainLocaleProvider.getInstance().getLocaleString();
		if (ComponentUtil.isNotBlank(defaultLocaleString)) {
			localeString = defaultLocaleString; 
		} else {
			String localeAttribute = sourceElement.getAttribute(ATTR_LOCALE); 
			if (ComponentUtil.isNotBlank(localeAttribute)) {
				localeString = localeAttribute;
			}
		}
		
		/*
		 * 2. Create Locale object from locale string.
		 */
		locale = createLocale(localeString);
		
		/*
		 * 3. Get bundles for this Locale and Refresh the page.
		 * If there is no locale attribute in f:view - use default locale, 
		 * that is got from MainLocaleProvider.  
		 * When Default Locate is found - use it in any case.
		 */
		pageContext.getBundle().setLocale(locale);
		pageContext.getBundle().refreshRegisteredBundles();

		VpeCreationData creationData = new VpeCreationData(table);
		VpeChildrenInfo divInfo = new VpeChildrenInfo(div);
		creationData.addChildrenInfo(divInfo);
		
		for (Node child : getChildren(sourceElement)) {
			divInfo.addSourceChild(child);
		}

		return creationData;
	}
	
	/**
	 * Creates the locale.
	 * <p>If the locale string could be parsed into language and country -
	 * creates Locale for this arguments.
	 * <p> By default - locale for empty string is created.
	 * 
	 * @param localeString the locale string
	 * @return Locale object
	 */
	Locale createLocale(String localeString) {
		Locale newLocale = null;
		if (localeString.length() == 2) {
			newLocale = new Locale(localeString);
		} else if ((localeString.length() == 5) && (localeString.indexOf("_") == 2)) { //$NON-NLS-1$
			newLocale = new Locale(localeString.substring(0, 2), localeString.substring(3));
		} else {
			newLocale = new Locale(Constants.EMPTY);
		}
		return newLocale;	
	}
	
	/**
	 * Gets the children.
	 * 
	 * @param sourceElement the source element
	 * 
	 * @return the children
	 */
	public static List<Node> getChildren(Element sourceElement) {
		ArrayList<Node> children = new ArrayList<Node>();
		NodeList nodeList = sourceElement.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node child = nodeList.item(i);
				children.add(child);
		}
		return children;
	}
	
	/* (non-Javadoc)
	 * @see org.jboss.tools.vpe.editor.template.VpeAbstractTemplate#isRecreateAtAttrChange(org.jboss.tools.vpe.editor.context.VpePageContext, org.w3c.dom.Element, org.mozilla.interfaces.nsIDOMDocument, org.mozilla.interfaces.nsIDOMElement, java.lang.Object, java.lang.String, java.lang.String)
	 */
	public boolean recreateAtAttrChange(VpePageContext pageContext,
			Element sourceElement, nsIDOMDocument visualDocument,
			nsIDOMElement visualNode, Object data, String name, String value) {
		return true;
	}

}
