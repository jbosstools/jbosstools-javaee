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

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IRegion;
import org.jboss.tools.common.model.XModel;
import org.jboss.tools.common.text.ext.hyperlink.XModelBasedHyperlink;
import org.jboss.tools.common.text.ext.hyperlink.xpl.Messages;
import org.jboss.tools.common.text.ext.util.StructuredModelWrapper;
import org.jboss.tools.common.text.ext.util.Utils;
import org.jboss.tools.jst.web.project.WebProject;
import org.jboss.tools.jst.web.project.list.WebPromptingProvider;
import org.jboss.tools.jst.web.tld.TaglibMapping;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

public class JsfJSPXmlNsHyperlink extends XModelBasedHyperlink {
	
	protected String getRequestMethod() {
		return WebPromptingProvider.JSF_OPEN_TAG_LIBRARY;
	}

	private String getUri(IRegion region) {
		StructuredModelWrapper smw = new StructuredModelWrapper();
		smw.init(getDocument());
		try {
			Document xmlDocument = smw.getDocument();
			if (xmlDocument == null) return null;

			IFile file = smw.getFile();

			XModel xModel = getXModel(file);

			WebProject wp = WebProject.getInstance(xModel);
			TaglibMapping tm = wp.getTaglibMapping();

			Node n = Utils.findNodeForOffset(xmlDocument, region.getOffset());

			if (n instanceof Attr) {
				Attr attr = (Attr)n;
				
				String name = attr.getName();
				if (name.indexOf("xmlns:") == 0) { //$NON-NLS-1$
					
					String prefix = name.substring(name.indexOf(':') + 1);
					String uri = Utils.trimQuotes(attr.getValue());
					if (prefix != null && prefix.trim().length() > 0) {
						return tm.resolveURI(uri);
					}
				}
			}
		} finally {
			smw.dispose();
		}

		return null;
	}
	
	protected Properties getRequestProperties(IRegion region) {
		Properties p = new Properties();

		String uri = getUri(region);
		if (uri != null && uri.trim().length() > 0) {
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
		String uri = getUri(getHyperlinkRegion());
		if (uri == null)
			return  MessageFormat.format(Messages.NotFound, "URI"); //$NON-NLS-1$
		
		return MessageFormat.format(Messages.Open, uri);
	}
}
