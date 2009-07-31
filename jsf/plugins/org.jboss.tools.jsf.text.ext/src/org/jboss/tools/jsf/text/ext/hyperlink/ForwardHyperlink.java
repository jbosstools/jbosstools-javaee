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
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IRegion;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.ide.IDE;
import org.jboss.tools.common.model.XModel;
import org.jboss.tools.common.text.ext.hyperlink.AbstractHyperlink;
import org.jboss.tools.common.text.ext.hyperlink.xpl.Messages;
import org.jboss.tools.jsf.text.ext.JSFExtensionsPlugin;
import org.jboss.tools.jst.web.project.list.WebPromptingProvider;

/**
 * @author Jeremy
 */
public class ForwardHyperlink extends AbstractHyperlink {
	/** 
	 * @see com.ibm.sse.editor.AbstractHyperlink#doHyperlink(org.eclipse.jface.text.IRegion)
	 */
	protected void doHyperlink(IRegion region) {
		String fileName = getFilePath(region);
		IFile fileToOpen = getFileToOpen(fileName);
		if (fileToOpen != null && fileToOpen.exists()) {
			IWorkbenchPage workbenchPage = JSFExtensionsPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage();
			try {
				IDE.openEditor(workbenchPage,fileToOpen,true);
			} catch (CoreException e) {
				openFileFailed();
			}
		} else {
			openFileFailed();
		}
	}
	
	private String getFilePath(IRegion region) {
		if(getDocument() == null || region == null) return null;
		try {
			return getDocument().get(region.getOffset(), region.getLength());
		} catch (BadLocationException x) {
			JSFExtensionsPlugin.log("", x); //$NON-NLS-1$
		}
		return null;
	}
	
	private IFile getFileToOpen(String fileName) {
		IFile documentFile = getFile();
		XModel xModel = getXModel(documentFile);
		if (xModel != null) {
			List<Object> list = WebPromptingProvider.getInstance().getList(xModel, WebPromptingProvider.JSF_GET_PATH, fileName, null);
			if (list != null && list.size() > 0) {
				for (Object o: list) {
					if (o instanceof String) {
						fileName = (String)o;
						break;
					}
				}
			}
		}
		// End of Slava's magic
		return super.getFileFromProject(fileName);
	}
	
	IRegion fLastRegion = null;
	/** 
	 * @see com.ibm.sse.editor.AbstractHyperlink#doGetHyperlinkRegion(int)
	 */
	protected IRegion doGetHyperlinkRegion(int offset) {
		fLastRegion = JSPForwardHyperlinkPartitioner.getRegion(getDocument(), offset);
		return fLastRegion;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see IHyperlink#getHyperlinkText()
	 */
	public String getHyperlinkText() {
		String filePath = getFilePath(fLastRegion);
		if (filePath == null)
			return  MessageFormat.format(Messages.OpenA, Messages.File);
		
		return MessageFormat.format(Messages.OpenFile, filePath);
	}

}