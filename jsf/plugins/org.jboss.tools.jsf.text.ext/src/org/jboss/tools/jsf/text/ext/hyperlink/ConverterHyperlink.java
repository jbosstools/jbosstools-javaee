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
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IRegion;
import org.jboss.tools.common.model.XModel;
import org.jboss.tools.common.text.ext.hyperlink.AbstractHyperlink;
import org.jboss.tools.common.text.ext.hyperlink.xpl.Messages;
import org.jboss.tools.jsf.JSFModelPlugin;
import org.jboss.tools.jsf.text.ext.JSFTextExtMessages;
import org.jboss.tools.jst.web.project.list.WebPromptingProvider;

/**
 * @author Jeremy
 */
public class ConverterHyperlink extends AbstractHyperlink {

	/** 
	 * @see com.ibm.sse.editor.AbstractHyperlink#doHyperlink(org.eclipse.jface.text.IRegion)
	 */
	protected void doHyperlink(IRegion region) {
		XModel xModel = getXModel();
		if (xModel == null) {
			openFileFailed();
			return;
		}
		WebPromptingProvider provider = WebPromptingProvider.getInstance();
		Properties p = new Properties();
		String converterID = getConverterID(region);
		IFile file = getFile();
		if(file != null) p.put(WebPromptingProvider.FILE, file);
		provider.getList(xModel, WebPromptingProvider.JSF_OPEN_CONVERTOR, converterID, p);
		String error = p.getProperty(WebPromptingProvider.ERROR); 
		if ( error != null && error.length() > 0) {
			openFileFailed();
		}
	}
	
	private String getConverterID (IRegion region) {
		String converterID = null;
		if(getDocument() != null && region != null) { 
			try {
				converterID = getDocument().get(region.getOffset(), region.getLength());
			} catch (BadLocationException x) {
				JSFModelPlugin.getPluginLog().logError("Cannot get convertor id", x); //$NON-NLS-1$
			}
		}
		return converterID;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see IHyperlink#getHyperlinkText()
	 */
	public String getHyperlinkText() {
		String converterId = getConverterID(getHyperlinkRegion());
		if (converterId == null)
			return  MessageFormat.format(Messages.OpenA, JSFTextExtMessages.Converter);
		
		return MessageFormat.format(JSFTextExtMessages.OpenConverterForId, converterId);
	}
}