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
 *
 */
public class StrutsConfigFormBeanHyperlink extends StrutsXModelBasedHyperlink {
	
	protected String getRequestMethod() {
		return WebPromptingProvider.STRUTS_OPEN_OBJECT_BY_PATH;
	}

	protected Properties getRequestProperties(IRegion region) {
		Properties p = new Properties();
		
		String value = getAttributeValue(region);
		if (value != null) {
			String path = "form-beans/" + value;
			p.setProperty(WebPromptingProvider.MODEL_OBJECT_PATH, path);
			p.setProperty("prefix", path);
		}
		return p;
	}
	
	private String getAttributeValue(IRegion region) {
		if(region == null || getDocument() == null) return null;
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
		String formBeanName = getAttributeValue(getHyperlinkRegion());
		if (formBeanName == null)
			return  MessageFormat.format(Messages.OpenA, StrutsTextExtMessages.FormBean);
		
		return MessageFormat.format(StrutsTextExtMessages.OpenFormBean, formBeanName);
	}
}
