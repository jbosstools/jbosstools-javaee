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

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IRegion;
import org.jboss.tools.common.model.XModel;
import org.jboss.tools.common.text.ext.hyperlink.AbstractHyperlink;
import org.jboss.tools.common.text.ext.hyperlink.xpl.Messages;
import org.jboss.tools.jsf.text.ext.JSFExtensionsPlugin;
import org.jboss.tools.jst.web.project.list.WebPromptingProvider;

/**
 * @author Jeremy
 */
public class BeanHyperlink extends AbstractHyperlink {

	/**
	 * @see com.ibm.sse.editor.AbstractHyperlink#doHyperlink(org.eclipse.jface.text.IRegion)
	 */
	protected void doHyperlink(IRegion region) {
		XModel xModel = getXModel();
		if (xModel == null || region == null) {
			openFileFailed();
			return;
		}
		WebPromptingProvider provider = WebPromptingProvider.getInstance();
		region = JSPBeanHyperlinkPartitioner.getRegionPart(getDocument(), region.getOffset());
		if(region == null) {
			openFileFailed();
			return;
		}
		try {	
			String beanName = getDocument().get(region.getOffset(), region.getLength());
			if(beanName == null) {
				openFileFailed();
				return;
			}
			provider.getList(xModel, WebPromptingProvider.JSF_BEAN_OPEN, beanName, null);
		} catch (BadLocationException x) {
			JSFExtensionsPlugin.log("", x); //$NON-NLS-1$
			openFileFailed();
		}
	}

	private String getBeanName(IRegion region) {
		if (region == null)
			return null;
		IRegion regionPart = JSPBeanHyperlinkPartitioner.getRegionPart(getDocument(), region.getOffset());
		if(regionPart == null) 
			return null;
		try {	
			String beanName = getDocument().get(region.getOffset(), region.getLength());
			return beanName;
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
		fLastRegion = JSPBeanHyperlinkPartitioner.getWordRegion(getDocument(), offset);
		return fLastRegion;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see IHyperlink#getHyperlinkText()
	 */
	public String getHyperlinkText() {
		String beanName = getBeanName(fLastRegion);
		if (beanName == null)
			return  MessageFormat.format(Messages.OpenA, Messages.Bean);
		
		return MessageFormat.format(Messages.OpenBean, beanName);
	}

}
