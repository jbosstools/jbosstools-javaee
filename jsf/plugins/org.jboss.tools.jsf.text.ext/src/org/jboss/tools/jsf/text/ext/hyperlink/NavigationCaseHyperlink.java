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
public class NavigationCaseHyperlink extends AbstractHyperlink {

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
		
		try {	
			WebPromptingProvider provider = WebPromptingProvider.getInstance();
			region = JSPNavigationCaseHyperlinkPartitioner.getRegion(getDocument(), region.getOffset());
			if(region == null) {
				openFileFailed();
				return;
			}
			String beanName = getDocument().get(region.getOffset(), region.getLength());
			Properties p = new Properties();
			p.put(WebPromptingProvider.FILE, file);
			provider.getList(xModel, WebPromptingProvider.JSF_OPEN_ACTION, beanName, p);
		} catch (BadLocationException x) {
			JSFExtensionsPlugin.log("", x); //$NON-NLS-1$
		}
	}
	
	IRegion fLastRegion = null;
	/** 
	 * @see com.ibm.sse.editor.AbstractHyperlink#doGetHyperlinkRegion(int)
	 */
	protected IRegion doGetHyperlinkRegion(int offset) {
		fLastRegion = JSPNavigationCaseHyperlinkPartitioner.getRegion(getDocument(), offset);
		return fLastRegion;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see IHyperlink#getHyperlinkText()
	 */
	public String getHyperlinkText() {
		return MessageFormat.format(Messages.BrowseFor, JSFTextExtMessages.NavigationRule);
	}

}