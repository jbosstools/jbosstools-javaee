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
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.IProducerField;
import org.jboss.tools.cdi.core.IProducerMethod;
import org.jboss.tools.cdi.text.ext.CDIExtensionsMessages;
import org.jboss.tools.cdi.text.ext.CDIExtensionsPlugin;
import org.jboss.tools.common.text.ext.hyperlink.AbstractHyperlink;

public class InjectedPointHyperlink extends AbstractHyperlink{
	IBean bean;
	IRegion region;
	
	public InjectedPointHyperlink(IRegion region, IBean bean, IDocument document){
		this.bean = bean;
		this.region = region;
		setDocument(document);
	}
	

	@Override
	protected IRegion doGetHyperlinkRegion(int offset) {
		return region;
	}

	protected void doHyperlink(IRegion region) {
		IEditorPart part = null;
		
		if(bean != null && bean.getBeanClass() != null){
			try{
				part = JavaUI.openInEditor(bean.getBeanClass());
			}catch(JavaModelException ex){
				CDIExtensionsPlugin.log(ex);
			}catch(PartInitException ex){
				CDIExtensionsPlugin.log(ex);
			}
			
			IJavaElement element = getJavaElement();
			if (part != null) {
				JavaUI.revealInEditor(part, element);
			} 
		}
		if (part == null)
			openFileFailed();
	}

	@Override
	public String getHyperlinkText() {
		String text = CDIExtensionsMessages.CDI_INJECTED_POINT_HYPERLINK_OPEN_BEAN+" ";
		if(bean != null)
			text += bean.getBeanClass().getElementName();
		return text;
	}
	
	private IJavaElement getJavaElement(){
		if(bean instanceof IProducerField){
			return ((IProducerField)bean).getField();
		}else if(bean instanceof IProducerMethod){
			return ((IProducerMethod)bean).getMethod();
		}else{
			return bean.getBeanClass();
		}
	}

}
