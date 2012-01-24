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
import org.jboss.tools.cdi.core.IInjectionPoint;
import org.jboss.tools.cdi.core.IInjectionPointField;
import org.jboss.tools.cdi.core.IInjectionPointParameter;
import org.jboss.tools.cdi.text.ext.CDIExtensionsMessages;
import org.jboss.tools.cdi.text.ext.CDIExtensionsPlugin;
import org.jboss.tools.common.text.ext.hyperlink.AbstractHyperlink;

public class EventHyperlink extends AbstractHyperlink implements ITestableCDIHyperlink, IInformationItem{
	IInjectionPoint event;
	IRegion region;
	
	public EventHyperlink(IRegion region, IInjectionPoint event, IDocument document){
		this.event = event;
		this.region = region;
		setDocument(document);
	}
	

	@Override
	protected IRegion doGetHyperlinkRegion(int offset) {
		return region;
	}

	protected void doHyperlink(IRegion region) {
		IEditorPart part = null;
		
		if(event != null && event.getClassBean() != null){
			try{
				part = JavaUI.openInEditor(event.getClassBean().getBeanClass());
			}catch(JavaModelException ex){
				CDIExtensionsPlugin.getDefault().logError(ex);
			}catch(PartInitException ex){
				CDIExtensionsPlugin.getDefault().logError(ex);
			}
			
			IJavaElement element = event.getClassBean().getBeanClass();
			if(event instanceof IInjectionPointField)
				element = ((IInjectionPointField)event).getField();
			else if(event instanceof IInjectionPointParameter)
				element = ((IInjectionPointParameter)event).getBeanMethod().getMethod();
			
			if (part != null) {
				JavaUI.revealInEditor(part, element);
			} 
		}
		if (part == null)
			openFileFailed();
	}

	@Override
	public String getHyperlinkText() {
		return CDIExtensionsMessages.CDI_EVENT_HYPERLINK_OPEN_EVENT+" "+event.getElementName();
	}


	public ICDIElement getCDIElement() {
		return event;
	}

	public Set<? extends ICDIElement> getCDIElements() {
		return null;
	}
}
