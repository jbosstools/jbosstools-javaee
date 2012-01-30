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
package org.jboss.tools.struts.text.ext.hyperlink;

import java.text.MessageFormat;
import java.util.Properties;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IRegion;
import org.jboss.tools.common.text.ext.hyperlink.xpl.Messages;
import org.jboss.tools.common.text.ext.util.Utils;
import org.jboss.tools.jst.web.project.list.WebPromptingProvider;
import org.jboss.tools.struts.text.ext.StrutsExtensionsPlugin;

/**
 * @author Jeremy
 *
 */
public class StrutsBundleHyperlink extends StrutsXModelBasedHyperlink {
	
	protected String getRequestMethod() {
		return WebPromptingProvider.STRUTS_OPEN_BUNDLE;
	}

	protected Properties getRequestProperties(IRegion region) {
		Properties p = new Properties();

		p.setProperty(WebPromptingProvider.BUNDLE, getBundle(region));
		p.setProperty("prefix", p.getProperty(WebPromptingProvider.BUNDLE));
		return p;
	}
	
	private String getBundle(IRegion region) {
		if(region == null || getDocument() == null) return "";
		try {
			return Utils.trimQuotes(getDocument().get(region.getOffset(), region.getLength()));
		} catch (BadLocationException x) {
			StrutsExtensionsPlugin.getPluginLog().logError(x);
			return null;
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see IHyperlink#getHyperlinkText()
	 */
	public String getHyperlinkText() {
		String baseName = getBundle(getHyperlinkRegion());
		if (baseName == null)
			return  MessageFormat.format(Messages.OpenA, Messages.Bundle);
		
		return MessageFormat.format(Messages.OpenBundle, baseName);
	}
}
