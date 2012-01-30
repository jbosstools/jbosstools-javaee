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
import org.jboss.tools.struts.text.ext.StrutsTextExtMessages;

/**
 * @author Jeremy
 */
public class StrutsActionMappingHyperlink extends StrutsXModelBasedHyperlink {
	
	protected String getRequestMethod() {
		return WebPromptingProvider.STRUTS_OPEN_ACTION_MAPPING;
	}

	protected Properties getRequestProperties(IRegion region) {
		Properties p = new Properties();
		String action = getAction(region);
		p.setProperty(WebPromptingProvider.ACTION, action);
		p.setProperty("prefix", action);
		return p;
	}
	
	private String getAction(IRegion region) {
		if(region == null || getDocument() == null) return "";
		try {
			return Utils.trimQuotes(getDocument().get(region.getOffset(), region.getLength()));
		} catch (BadLocationException x) {
			StrutsExtensionsPlugin.getPluginLog().logError(x);
			return "";
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see IHyperlink#getHyperlinkText()
	 */
	public String getHyperlinkText() {
		String actionName = getAction(getHyperlinkRegion());
		if (actionName == null)
			return  MessageFormat.format(Messages.OpenAn, StrutsTextExtMessages.ActionMapping);
		
		return MessageFormat.format(StrutsTextExtMessages.OpenActionMapping, actionName);
	}
}
