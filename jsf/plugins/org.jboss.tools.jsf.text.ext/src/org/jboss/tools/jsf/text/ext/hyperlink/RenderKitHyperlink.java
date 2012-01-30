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

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IRegion;
import org.jboss.tools.common.model.XModel;
import org.jboss.tools.common.text.ext.hyperlink.AbstractHyperlink;
import org.jboss.tools.common.text.ext.hyperlink.xpl.Messages;
import org.jboss.tools.jsf.text.ext.JSFExtensionsPlugin;
import org.jboss.tools.jsf.text.ext.JSFTextExtMessages;
import org.jboss.tools.jst.web.project.list.WebPromptingProvider;

/**
 * @author Jeremy
 */
public class RenderKitHyperlink extends AbstractHyperlink {

	/** 
	 * @see com.ibm.sse.editor.AbstractHyperlink#doHyperlink(org.eclipse.jface.text.IRegion)
	 */
	protected void doHyperlink(IRegion region) {
		if(getDocument() == null || region == null) {
			openFileFailed();
			return;
		}
		XModel xModel = getXModel();
		if (xModel == null) {
			openFileFailed();
			return;
		}
		WebPromptingProvider provider = WebPromptingProvider.getInstance();

		String beanName = getBeanName(getHyperlinkRegion());

		Properties p = new Properties();
		provider.getList(xModel, WebPromptingProvider.JSF_OPEN_RENDER_KIT, beanName, p);
		String error = p.getProperty(WebPromptingProvider.ERROR); 
		if ( error != null && error.length() > 0) {
			openFileFailed();
		}
	}
	
	private String getBeanName(IRegion region) {
		if(getDocument() == null || region == null) return null;
		try {
			return trimQuotes(getDocument().get(region.getOffset(), region.getLength()));
		} catch (BadLocationException x) {
			JSFExtensionsPlugin.log("", x); //$NON-NLS-1$
			return null;
		}
	}

	private String trimQuotes(String word) {
		String attrText = word;
		int bStart = 0;
		int bEnd = word.length() - 1;
		StringBuffer sb = new StringBuffer(attrText);
		//find start and end of path property
		while (bStart < bEnd && 
				(sb.charAt(bStart) == '\'' || sb.charAt(bStart) == '\"' ||
						Character.isWhitespace(sb.charAt(bStart)))) { 
			bStart++;
		}
		while (bEnd > bStart && 
				(sb.charAt(bEnd) == '\'' || sb.charAt(bEnd) == '\"' ||
						Character.isWhitespace(sb.charAt(bEnd)))) { 
			bEnd--;
		}
		bEnd++;
		return sb.substring(bStart, bEnd);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see IHyperlink#getHyperlinkText()
	 */
	public String getHyperlinkText() {
		String renderKitName = getBeanName(getHyperlinkRegion());
		if (renderKitName == null)
			return  MessageFormat.format(Messages.OpenA, JSFTextExtMessages.RenderKit);
		
		return MessageFormat.format(JSFTextExtMessages.OpenRenderKit, renderKitName);
	}
}
