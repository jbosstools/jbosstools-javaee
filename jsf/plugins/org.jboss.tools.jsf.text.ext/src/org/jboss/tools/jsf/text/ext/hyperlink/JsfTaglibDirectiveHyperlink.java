/*******************************************************************************
 * Copyright (c) 2007-2012 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.jsf.text.ext.hyperlink;

import java.text.MessageFormat;
import java.util.Properties;

import org.eclipse.jface.text.IRegion;
import org.jboss.tools.common.text.ext.hyperlink.XModelBasedHyperlink;
import org.jboss.tools.common.text.ext.hyperlink.xpl.Messages;
import org.jboss.tools.common.text.ext.util.StructuredModelWrapper;
import org.jboss.tools.common.text.ext.util.Utils;
import org.jboss.tools.jst.web.project.list.WebPromptingProvider;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * @author Jeremy
 *
 */
public class JsfTaglibDirectiveHyperlink extends XModelBasedHyperlink {
	
	protected String getRequestMethod() {
		return WebPromptingProvider.JSF_OPEN_TAG_LIBRARY;
	}

	private String getTaglibUri(IRegion region) {
		StructuredModelWrapper smw = new StructuredModelWrapper();
		smw.init(getDocument());
		try {
			Document xmlDocument = smw.getDocument();
			if (xmlDocument == null) return null;
			
			Node n = Utils.findNodeForOffset(xmlDocument, region.getOffset());

			if (n instanceof Attr) n = ((Attr)n).getOwnerElement();
			if ((n == null) || !(n instanceof Node)) return null;
			
			Node node = n;
			
			String uri = Utils.getAttributeValue(getDocument(), node, "uri"); //$NON-NLS-1$
			if (uri != null) {
				return uri;
			}
		} finally {
			smw.dispose();
		}

		return null;
	}
	
	protected Properties getRequestProperties(IRegion region) {
		Properties p = new Properties();

		String uri = getTaglibUri(region);
		if (uri != null) {
			p.setProperty("prefix", uri); //$NON-NLS-1$
		}
		return p;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see IHyperlink#getHyperlinkText()
	 */
	public String getHyperlinkText() {
		String uri = getTaglibUri(getHyperlinkRegion());
		if (uri == null)
			return  MessageFormat.format(Messages.OpenA, Messages.TagLibrary);
		
		return MessageFormat.format(Messages.OpenTagLibraryForUri, uri);
	}
}
