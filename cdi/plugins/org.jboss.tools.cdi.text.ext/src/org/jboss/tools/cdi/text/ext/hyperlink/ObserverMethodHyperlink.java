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

import java.util.Set;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.jboss.tools.cdi.core.ICDIElement;
import org.jboss.tools.cdi.core.IObserverMethod;
import org.jboss.tools.cdi.text.ext.CDIExtensionsMessages;
import org.jboss.tools.cdi.text.ext.CDIExtensionsPlugin;
import org.jboss.tools.common.text.ext.hyperlink.AbstractHyperlink;

public class ObserverMethodHyperlink extends AbstractHyperlink implements IInformationItem, ITestableCDIHyperlink{
	IObserverMethod observerMethod;
	IRegion region;
	
	public ObserverMethodHyperlink(IRegion region, IObserverMethod observerMethod, IDocument document){
		this.observerMethod = observerMethod;
		this.region = region;
		setDocument(document);
	}

	protected IRegion doGetHyperlinkRegion(int offset) {
		return region;
	}

	protected void doHyperlink(IRegion region) {
		IEditorPart part = null;
		
		if(observerMethod != null && observerMethod.getClassBean() != null){
			try{
				part = JavaUI.openInEditor(observerMethod.getClassBean().getBeanClass());
			}catch(JavaModelException ex){
				CDIExtensionsPlugin.getDefault().logError(ex);
			}catch(PartInitException ex){
				CDIExtensionsPlugin.getDefault().logError(ex);
			}
			
			IJavaElement element = observerMethod.getMethod();
			if (part != null) {
				JavaUI.revealInEditor(part, element);
			} 
		}
		if (part == null)
			openFileFailed();
	}

	@Override
	public String getHyperlinkText() {
		return CDIExtensionsMessages.CDI_EVENT_HYPERLINK_OPEN_OBSERVER_METHOD+" "+observerMethod.getElementName();
	}


	public ICDIElement getCDIElement() {
		return observerMethod;
	}

	public Set<? extends ICDIElement> getCDIElements() {
		return null;
	}
}
