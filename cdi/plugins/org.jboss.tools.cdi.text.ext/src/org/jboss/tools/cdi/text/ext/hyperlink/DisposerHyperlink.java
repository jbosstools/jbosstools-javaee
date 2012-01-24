/******************************************************************************* 
 * Copyright (c) 2010 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.cdi.text.ext.hyperlink;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.jboss.tools.cdi.text.ext.CDIExtensionsMessages;
import org.jboss.tools.cdi.text.ext.CDIExtensionsPlugin;
import org.jboss.tools.common.text.ext.hyperlink.AbstractHyperlink;

public class DisposerHyperlink extends AbstractHyperlink{
	IMethod method;
	IRegion region;
	
	public DisposerHyperlink(IRegion region, IMethod method, IDocument document){
		this.method = method;
		this.region = region;
		setDocument(document);
	}
	

	@Override
	protected IRegion doGetHyperlinkRegion(int offset) {
		return region;
	}

	protected void doHyperlink(IRegion region) {
		IEditorPart part = null;
		
		if(method != null){
			try{
				part = JavaUI.openInEditor(method);
			}catch(JavaModelException ex){
				CDIExtensionsPlugin.getDefault().logError(ex);
			}catch(PartInitException ex){
				CDIExtensionsPlugin.getDefault().logError(ex);
			}
			
			if (part != null) {
				JavaUI.revealInEditor(part, (IJavaElement)method);
			} 
		}
		if (part == null)
			openFileFailed();
	}

	@Override
	public String getHyperlinkText() {
		String text = CDIExtensionsMessages.CDI_PRODUCER_DISPOSER_HYPERLINK_OPEN_BOUND_DISPOSER+" ";
		if(method != null)
			text += method.getElementName();
		return text;
	}
	
}
