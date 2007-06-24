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

import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.text.IRegion;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.ide.IDE;

import org.jboss.tools.common.model.XModel;
import org.jboss.tools.common.text.ext.hyperlink.AbstractHyperlink;
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
	
		try {
			String fileName = getFilePath(region);
			IFile fileToOpen = getFileToOpen(fileName);
			if (fileToOpen.exists()) {
				IWorkbenchPage workbenchPage = JSFExtensionsPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage();
				IDE.openEditor(workbenchPage,fileToOpen,true);
			} else {
				throw new FileNotFoundException((fileToOpen == null ? "" : fileToOpen.toString()));
			}
		} catch (Exception x) {
			// could not open editor
			openFileFailed();
		}
	}
	
	private String getFilePath(IRegion region) {
		try {
			return getDocument().get(region.getOffset(), region.getLength());
		} catch (Exception x) {
			JSFExtensionsPlugin.log("", x);
			return null;
		} finally {
		}
	}
	
	private IFile getFileToOpen(String fileName) {
		IFile documentFile = getFile();
		XModel xModel = getXModel(documentFile);
		try {	
			WebPromptingProvider provider = WebPromptingProvider.getInstance();

			if (xModel != null) {
				List list = provider.getList(xModel, WebPromptingProvider.JSF_GET_PATH, fileName, null);
				if (list != null && list.size() > 0) {
					for (Iterator i = list.iterator(); i.hasNext();) {
						Object o = i.next();
						if (o instanceof String) {
							fileName = (String)o;
							break;
						}
					}
				}
			}
			// End of Slava's magic
			return super.getFileFromProject(fileName);

		} catch (Exception x) {
			JSFExtensionsPlugin.log("", x);
			return null;
		}

	}
	

	/** 
	 * @see com.ibm.sse.editor.AbstractHyperlink#doGetHyperlinkRegion(int)
	 */
	protected IRegion doGetHyperlinkRegion(int offset) {
		IRegion region = JSPForwardHyperlinkPartitioner.getRegion(getDocument(), offset);
		return region;
	}

}