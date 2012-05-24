/******************************************************************************* 
 * Copyright (c) 2010-2012 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.cdi.deltaspike.text.ext;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.jboss.tools.cdi.deltaspike.core.DeltaspikeCorePlugin;
import org.jboss.tools.common.text.ext.hyperlink.AbstractHyperlink;

public class JavaElementHyperlink extends AbstractHyperlink {
	protected  IJavaElement element;
	
	public JavaElementHyperlink(IRegion region, IJavaElement element, IDocument document){
		this.element = element;
		setRegion(region);
		setDocument(document);
	}

	public void doHyperlink(IRegion region) {
		IEditorPart part = null;
		
		if(element != null && element.exists() && element.getAncestor(IJavaElement.TYPE) != null) {
			try{
				part = JavaUI.openInEditor(element.getAncestor(IJavaElement.TYPE));
			}catch(JavaModelException ex){
				DeltaspikeCorePlugin.getDefault().logError(ex);
			}catch(PartInitException ex){
				DeltaspikeCorePlugin.getDefault().logError(ex);
			}
			
			if (part != null) {
				JavaUI.revealInEditor(part, element);
			} 
		}
		if (part == null)
			openFileFailed();
	}

	@Override
	public String getHyperlinkText() {
		String s = "";
		if(element instanceof IMethod) {
			IMethod m = (IMethod)element;
			s = m.getDeclaringType().getElementName() + "." + m.getElementName() + "()";
		}
		String text = "Open authorizer method " + s;
		return text;
	}
	
}
