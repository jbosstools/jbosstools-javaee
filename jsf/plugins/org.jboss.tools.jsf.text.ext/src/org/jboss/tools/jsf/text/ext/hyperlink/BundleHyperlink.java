/******************************************************************************* 
 * Copyright (c) 2011 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.jsf.text.ext.hyperlink;

import java.text.MessageFormat;
import java.util.Properties;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.jboss.tools.common.el.core.model.ELInvocationExpression;
import org.jboss.tools.common.el.core.resolver.ELContext;
import org.jboss.tools.common.el.core.resolver.ELResolution;
import org.jboss.tools.common.el.core.resolver.ELResolver;
import org.jboss.tools.common.el.core.resolver.ELSegment;
import org.jboss.tools.common.el.core.resolver.MessagePropertyELSegment;
import org.jboss.tools.common.text.ext.hyperlink.XModelBasedHyperlink;
import org.jboss.tools.common.text.ext.hyperlink.xpl.Messages;
import org.jboss.tools.common.text.ext.util.StructuredModelWrapper;
import org.jboss.tools.common.text.ext.util.Utils;
import org.jboss.tools.jsf.text.ext.hyperlink.JSPExprHyperlinkPartitioner.ExpressionStructure;
import org.jboss.tools.jst.web.project.list.WebPromptingProvider;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * @author Jeremy
 */
public class BundleHyperlink extends XModelBasedHyperlink {
	
	private static final String VIEW_TAGNAME = "view"; //$NON-NLS-1$
	private static final String LOCALE_ATTRNAME = "locale"; //$NON-NLS-1$
	private static final String PREFIX_SEPARATOR = ":"; //$NON-NLS-1$

	private String getPageLocale(IRegion region) {
		if(getDocument() == null || region == null) return null;

		StructuredModelWrapper smw = new StructuredModelWrapper();
		try {
			smw.init(getDocument());
			Document xmlDocument = smw.getDocument();
			if (xmlDocument == null) return null;
			
			Node n = Utils.findNodeForOffset(xmlDocument, region.getOffset());
			if (!(n instanceof Attr) ) return null; 

			Element el = ((Attr)n).getOwnerElement();
			
			Element jsfCoreViewTag = null;
			String nodeToFind = PREFIX_SEPARATOR + VIEW_TAGNAME; 
	
			while (el != null) {
				if (el.getNodeName() != null && el.getNodeName().endsWith(nodeToFind)) {
					jsfCoreViewTag = el;
					break;
				}
				Node parent = el.getParentNode();
				el = (parent instanceof Element ? (Element)parent : null); 
			}
			
			if (jsfCoreViewTag == null || !jsfCoreViewTag.hasAttribute(LOCALE_ATTRNAME)) return null;
			
			String locale = Utils.trimQuotes((jsfCoreViewTag.getAttributeNode(LOCALE_ATTRNAME)).getValue());
			if (locale == null || locale.length() == 0) return null;
			return locale;
		} finally {
			smw.dispose();
		}
	}

	IRegion fLastRegion = null;
	
	/** 
	 * @see com.ibm.sse.editor.AbstractHyperlink#doGetHyperlinkRegion(int)
	 */
	protected IRegion doGetHyperlinkRegion(int offset) {
		fLastRegion = JSPBundleHyperlinkPartitioner.getRegion(getDocument(), offset);
		return fLastRegion;
	}

	protected String getRequestMethod() {
		return requestProperties != null && requestProperties.getProperty(WebPromptingProvider.KEY) == null ? 
				WebPromptingProvider.JSF_OPEN_BUNDLE : WebPromptingProvider.JSF_OPEN_KEY;
	}

	protected Properties getRequestProperties(IRegion region) {
		ELContext context = JSPExprHyperlinkPartitioner.getELContext(getDocument());
		if(context != null){
			ExpressionStructure eStructure = JSPExprHyperlinkPartitioner.getExpression(context, getOffset());
			if(eStructure != null){
				ELInvocationExpression invocationExpression = JSPExprHyperlinkPartitioner.getInvocationExpression(eStructure.reference, eStructure.expression, getOffset());
				if(invocationExpression != null){
					for(ELResolver resolver : context.getElResolvers()){
						ELResolution resolution = resolver.resolve(context, invocationExpression, getOffset());
						if(resolution==null) {
							continue;
						}
						ELSegment segment = resolution.findSegmentByOffset(getOffset()-eStructure.reference.getStartPosition());
						
						if (segment != null && segment.isResolved() && segment instanceof MessagePropertyELSegment) {
							MessagePropertyELSegment mpSegment = (MessagePropertyELSegment)segment;
							String bundleBasename = mpSegment.getBaseName();
							String property = mpSegment.isBundle() ? null : trimQuotes(mpSegment.getToken().getText());
							String locale = getPageLocale(region);
							
							Properties p = new Properties();
							if (bundleBasename != null) {
								p.put(WebPromptingProvider.BUNDLE, bundleBasename);
							}
							
							if (property != null) {
								p.put(WebPromptingProvider.KEY, property);
							}
							
							if (locale != null) {
								p.setProperty(WebPromptingProvider.LOCALE, locale);
							}

							return p;
						}
					}
				}
			}
		}
		return null;
	}
	
	private String trimQuotes(String value) {
		if(value == null)
			return null;

		if(value.startsWith("'") || value.startsWith("\"")) {  //$NON-NLS-1$ //$NON-NLS-2$
			value = value.substring(1);
		} 
		
		if(value.endsWith("'") || value.endsWith("\"")) { //$NON-NLS-1$ //$NON-NLS-2$
			value = value.substring(0, value.length() - 1);
		}
		return value;
	}	


	/**
	 * Returns the text to be shown for Open action
	 * 
	 * @see IHyperlink#getHyperlinkText()
	 */
	public String getHyperlinkText() {
		Properties p = getRequestProperties(fLastRegion);
		String baseName = p == null ? null : p.getProperty(WebPromptingProvider.BUNDLE); 
		String propertyName = p == null ? null : p.getProperty(WebPromptingProvider.KEY);
		if (baseName == null || propertyName == null)
			return  MessageFormat.format(Messages.OpenA, Messages.BundleProperty);
		
		return MessageFormat.format(Messages.OpenBundleProperty, propertyName, baseName);
	}

}
