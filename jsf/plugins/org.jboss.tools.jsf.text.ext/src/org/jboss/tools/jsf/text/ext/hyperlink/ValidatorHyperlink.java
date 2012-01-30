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
import org.jboss.tools.jsf.text.ext.JSFExtensionsPlugin;
import org.jboss.tools.jsf.text.ext.JSFTextExtMessages;
import org.jboss.tools.jst.web.project.list.WebPromptingProvider;

/**
 * @author Jeremy
 */
public class ValidatorHyperlink extends AbstractHyperlink {

	/** 
	 * @see com.ibm.sse.editor.AbstractHyperlink#doHyperlink(org.eclipse.jface.text.IRegion)
	 */
	protected void doHyperlink(IRegion region) {
		if(region == null) {
			openFileFailed();
			return;
		}
		IFile file = getFile();
		XModel xModel = getXModel(file);			
		if (xModel == null) {
			openFileFailed();
			return;
		}

		WebPromptingProvider provider = WebPromptingProvider.getInstance();

		String validatorID = getValidatorId(getHyperlinkRegion());
		if (validatorID == null) {
			openFileFailed();
			return;
		}
		Properties p = new Properties();
		p.put(WebPromptingProvider.FILE, file);
		provider.getList(xModel, WebPromptingProvider.JSF_OPEN_VALIDATOR, validatorID, p);
		String error = p.getProperty(WebPromptingProvider.ERROR); 
		if ( error != null && error.length() > 0) {
			openFileFailed();
		}
	}
	
	private String getValidatorId(IRegion region) {
		if(getDocument() == null || region == null) return null;
		try {
			return getDocument().get(region.getOffset(), region.getLength());
		} catch (BadLocationException x) {
			JSFExtensionsPlugin.log("", x); //$NON-NLS-1$
			return null;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see IHyperlink#getHyperlinkText()
	 */
	public String getHyperlinkText() {
		String validatorId = getValidatorId(getHyperlinkRegion());
		if (validatorId == null)
			return  MessageFormat.format(Messages.OpenA, JSFTextExtMessages.Validator);
		
		return MessageFormat.format(JSFTextExtMessages.OpenValidatorForId, validatorId);
	}
}