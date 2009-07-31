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
package org.jboss.tools.jsf.text.ext.hyperlink;

import java.text.MessageFormat;
import java.util.Properties;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.jboss.tools.common.model.XModel;
import org.jboss.tools.common.text.ext.hyperlink.AbstractHyperlink;
import org.jboss.tools.common.text.ext.hyperlink.xpl.Messages;
import org.jboss.tools.common.text.ext.util.StructuredModelWrapper;
import org.jboss.tools.common.text.ext.util.Utils;
import org.jboss.tools.jsf.text.ext.JSFExtensionsPlugin;
import org.jboss.tools.jsf.text.ext.JSFTextExtMessages;
import org.jboss.tools.jst.web.project.list.WebPromptingProvider;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

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

		String beanName = getBeanName(getRegion(region.getOffset()));

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

	IRegion fLastRegion = null;
	/** 
	 * @see com.ibm.sse.editor.AbstractHyperlink#doGetHyperlinkRegion(int)
	 */
	protected IRegion doGetHyperlinkRegion(int offset) {
		fLastRegion = getRegion(offset);
		return fLastRegion;
	}

	private IRegion getRegion (int offset) {
		StructuredModelWrapper smw = new StructuredModelWrapper();
		smw.init(getDocument());
		try {
			Document xmlDocument = smw.getDocument();
			if (xmlDocument == null) return null;
			
			Node n = Utils.findNodeForOffset(xmlDocument, offset);

			if (n == null || !(n instanceof Attr || n instanceof Text)) return null;
			
			int start = Utils.getValueStart(n);
			int end = Utils.getValueEnd(n);

			if (start > offset || end < offset) return null;

			String text = getDocument().get(start, end - start);
			StringBuffer sb = new StringBuffer(text);

			//find start and end of class property
			int bStart = 0;
			int bEnd = text.length() - 1;

			while (bStart < bEnd && (Character.isWhitespace(sb.charAt(bStart)) 
					|| sb.charAt(bStart) == '\"' || sb.charAt(bStart) == '\"')) { 
				bStart++;
			}
			while (bEnd > bStart && (Character.isWhitespace(sb.charAt(bEnd)) 
					|| sb.charAt(bEnd) == '\"' || sb.charAt(bEnd) == '\"')) { 
				bEnd--;
			}
			bEnd++;

			final int propStart = bStart + start;
			final int propLength = bEnd - bStart;
			
			if (propStart > offset || propStart + propLength < offset) return null;
			
			return new Region(propStart,propLength);
		} catch (BadLocationException x) {
			JSFExtensionsPlugin.log("", x); //$NON-NLS-1$
			return null;
		} finally {
			smw.dispose();
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
		String renderKitName = getBeanName(fLastRegion);
		if (renderKitName == null)
			return  MessageFormat.format(Messages.OpenA, JSFTextExtMessages.RenderKit);
		
		return MessageFormat.format(JSFTextExtMessages.OpenRenderKit, renderKitName);
	}

}
