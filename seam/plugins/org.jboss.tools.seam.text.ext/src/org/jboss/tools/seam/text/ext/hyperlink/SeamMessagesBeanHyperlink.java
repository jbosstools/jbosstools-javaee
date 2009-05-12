/*******************************************************************************
 * Copyright (c) 2008 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.seam.text.ext.hyperlink;

import java.text.MessageFormat;
import java.util.Map;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IRegion;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.common.model.util.FindObjectHelper;
import org.jboss.tools.common.text.ext.hyperlink.AbstractHyperlink;
import org.jboss.tools.common.text.ext.hyperlink.xpl.Messages;
import org.jboss.tools.seam.core.ISeamMessages;

/**
 * @author Jeremy
 */

public class SeamMessagesBeanHyperlink extends AbstractHyperlink {

	/**
	 * @see com.ibm.sse.editor.AbstractHyperlink#doHyperlink(org.eclipse.jface.text.IRegion)
	 */
	protected void doHyperlink(IRegion region) {
		Map <String, ISeamMessages> messages = SeamBeanHyperlinkPartitioner.findMessagesComponents(getDocument(), region);
		if (messages == null || messages.isEmpty()) {
			// Nothing to open
			openFileFailed();
			return;
		}
			
		for (String property : messages.keySet()) {
			ISeamMessages messagesComponent = messages.get(property);
			Map <String, IResource> resources = messagesComponent.getResourcesMap();
			if (resources == null || resources.isEmpty())
				continue;
			
			for (String bundle : resources.keySet()) {
				IResource resource = resources.get(bundle);
				XModelObject xmo = EclipseResourceUtil.getObjectByResource(resource);
				if (xmo == null) 
					continue;
				
				XModelObject xmoChild = xmo.getChildByPath(property);
				if (xmoChild == null) 
					continue;
				
				int result = FindObjectHelper.findModelObject(xmoChild, FindObjectHelper.IN_EDITOR_ONLY);
				if (result == 0) {
					// Success
					return;
				}
			}
		}
		// could not open editor
		openFileFailed();
	}

	IRegion fLastRegion = null;
	/**
	 * @see com.ibm.sse.editor.AbstractHyperlink#doGetHyperlinkRegion(int)
	 */
	protected IRegion doGetHyperlinkRegion(int offset) {
		fLastRegion = SeamBeanHyperlinkPartitioner.getMessagesPropertyRegion(getDocument(), offset);
		return fLastRegion;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see IHyperlink#getHyperlinkText()
	 */
	public String getHyperlinkText() {
		
		String beanName;
		try {
			beanName = getDocument().get(fLastRegion.getOffset(), fLastRegion.getLength());
		} catch (BadLocationException e) {
			beanName = null;
		}
		
		if (beanName == null)
			return  MessageFormat.format(Messages.NotFound, Messages.Bean);
		
		return MessageFormat.format(Messages.OpenBean, beanName);
	}
}