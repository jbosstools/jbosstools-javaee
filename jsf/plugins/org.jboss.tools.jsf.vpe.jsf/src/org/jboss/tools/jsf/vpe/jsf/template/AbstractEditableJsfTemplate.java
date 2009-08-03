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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.wst.xml.core.internal.document.AttrImpl;
import org.jboss.tools.jsf.vpe.jsf.template.util.JSF;
import org.jboss.tools.vpe.editor.context.VpePageContext;
import org.jboss.tools.vpe.editor.template.VpeAbstractTemplate;
import org.jboss.tools.vpe.editor.util.HTML;
import org.jboss.tools.vpe.editor.util.TextUtil;
import org.mozilla.interfaces.nsIDOMElement;
import org.mozilla.interfaces.nsIDOMNode;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

/**
 * general class for jsf templates.
 * 
 * @author Sergey Dzmitrovich
 */
public abstract class AbstractEditableJsfTemplate extends VpeAbstractTemplate {
	
	/**
	 * Gets the output attribute node.
	 * 
	 * @param element the element
	 * 
	 * @return the output attribute node
	 */
	public Attr getOutputAttributeNode(Element element) {
       return null;
    }

    // general jsf attributes
	/**
	 * Contains JSF attributes and appropriate HTML attributes 
	 * content of that does not have to be modified in templates.
	 */
    static final private Map<String, String> attributes 
    		= new HashMap<String, String>();
	static {
		attributes.put(JSF.ATTR_STYLE, HTML.ATTR_STYLE);
		attributes.put(JSF.ATTR_STYLE_CLASS, HTML.ATTR_CLASS);
	}

	/**
	 * Renames and copies most general JSF attributes from the
	 * {@code sourceElement} to the {@code visualElement}.
	 * 
	 * @param sourceElement the source element
	 * @param visualElement the visual element
	 * @see AbstractEditableJsfTemplate#attributes attributes
	 */
	protected void copyGeneralJsfAttributes(Element sourceElement,
			nsIDOMElement visualElement) {
		
		Set<Map.Entry<String, String>> jsfAttrEntries = attributes.entrySet();
		
		for (Map.Entry<String, String> attrEntry : jsfAttrEntries) {
			copyAttribute(visualElement, sourceElement, attrEntry.getKey(),
					attrEntry.getValue());
		}

	}

	/**
	 * copy attribute.
	 * 
	 * @param sourceElement the source element
	 * @param targetAtttributeName the target atttribute name
	 * @param sourceAttributeName the source attribute name
	 * @param visualElement the visual element
	 */
	protected void copyAttribute(nsIDOMElement visualElement,
			Element sourceElement, String sourceAttributeName,
			String targetAtttributeName) {

		if (sourceElement.hasAttribute(sourceAttributeName))
			visualElement.setAttribute(targetAtttributeName, sourceElement
					.getAttribute(sourceAttributeName));

	}
	@Override
	public IRegion getSourceRegionForOpenOn(VpePageContext pageContext, Node sourceNode ,nsIDOMNode domNode) {

		final Attr attr= getOutputAttributeNode((Element) sourceNode);
		int offset = TextUtil.getStartELDocumentPosition(attr);
		if(offset!=-1){
			return new Region(offset, 0);
		} else {
			return super.getSourceRegionForOpenOn(pageContext, sourceNode, domNode);
		}
 
	}
}
