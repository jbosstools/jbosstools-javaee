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
package org.jboss.tools.struts.text.ext.hyperlink;

import java.util.Properties;

import org.eclipse.jface.text.IRegion;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import org.jboss.tools.common.text.ext.util.StructuredModelWrapper;
import org.jboss.tools.common.text.ext.util.Utils;
import org.jboss.tools.jst.web.project.list.WebPromptingProvider;
import org.jboss.tools.struts.text.ext.StrutsExtensionsPlugin;

/**
 * @author Jeremy
 */
public class StrutsValidationMethodHyperlink0 extends StrutsXModelBasedHyperlink {
	private static final String CLASSNAME_ATTRNAME = "classname";
//	private static final String METHODPARAMS_ATTRNAME = "methodParams";

	protected String getRequestMethod() {
		return WebPromptingProvider.STRUTS_OPEN_METHOD;
	}

	protected Properties getRequestProperties(IRegion region) {
		Properties p = new Properties();

		String value = getMethodName(region);
		if (value != null) {
			p.setProperty(WebPromptingProvider.NAME, value);
			p.setProperty("prefix", value);
		}


		value = getClassName(region);
		if (value != null) {
			p.setProperty(WebPromptingProvider.TYPE, value);
		}

		/* Isn't used yet!!!
		 * 
		value = getMethodParams(region);
		if (value != null) {
			p.setProperty(WebPromptingProvider.TYPE, value);
		}
		*/
		
		return p;
	}
	
	
	
	private String getMethodName(IRegion region) {
		try {
			return Utils.trimQuotes(getDocument().get(region.getOffset(), region.getLength()));
		} catch (Exception x) {
			StrutsExtensionsPlugin.getPluginLog().logError(x);
			return null;
		}
	}
	
	private String getClassName(IRegion region) {
		return getAttributeValue(region, CLASSNAME_ATTRNAME);
	}
	
	private String getAttributeValue(IRegion region, String attrName) {
		StructuredModelWrapper smw = new StructuredModelWrapper();
		try {
			smw.init(getDocument());
			Document xmlDocument = smw.getDocument();
			if (xmlDocument == null) return null;
			Node n = Utils.findNodeForOffset(xmlDocument, region.getOffset());
			if (n == null || !(n instanceof Attr)) return null;
			Node node = ((Attr)n).getOwnerElement();
			Attr attr = (Attr)node.getAttributes().getNamedItem(attrName);
			return Utils.getTrimmedValue(getDocument(), attr);
		} catch (Exception x) {
			StrutsExtensionsPlugin.getPluginLog().logError(x);
			return null;
		} finally {
			smw.dispose();
		}
	}

}
