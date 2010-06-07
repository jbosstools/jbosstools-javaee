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

import java.util.ArrayList;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.ICodeAssist;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.core.ResolvedBinaryType;
import org.eclipse.jdt.internal.core.ResolvedSourceType;
import org.eclipse.jdt.internal.ui.javaeditor.EditorUtility;
import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;
import org.eclipse.jdt.internal.ui.text.JavaWordFinder;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.hyperlink.AbstractHyperlinkDetector;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.ui.texteditor.ITextEditor;
import org.jboss.tools.cdi.core.CDICoreNature;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.core.CDIUtil;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.ICDIProject;
import org.jboss.tools.cdi.core.IInjectionPoint;
import org.jboss.tools.cdi.text.ext.CDIExtensionsPlugin;

public class InjectedPointHyperlinkDetector extends AbstractHyperlinkDetector{
	

	public IHyperlink[] detectHyperlinks(ITextViewer textViewer,
			IRegion region, boolean canShowMultipleHyperlinks) {
		ITextEditor textEditor= (ITextEditor)getAdapter(ITextEditor.class);
		if (region == null || !canShowMultipleHyperlinks || !(textEditor instanceof JavaEditor))
			return null;
		
		int offset= region.getOffset();
		
		IJavaElement input= EditorUtility.getEditorInputJavaElement(textEditor, false);
		if (input == null)
			return null;

		if (input.getResource() == null || input.getResource().getProject() == null)
			return null;

		IDocument document= textEditor.getDocumentProvider().getDocument(textEditor.getEditorInput());
		IRegion wordRegion= JavaWordFinder.findWord(document, offset);
		if (wordRegion == null)
			return null;
		
		IFile file = null;
		
		try {
			IResource resource = input.getCorrespondingResource();
			if (resource instanceof IFile)
				file = (IFile) resource;
		} catch (JavaModelException e) {
			CDIExtensionsPlugin.log(e);
		}
		
		if(file == null)
			return null;
		CDICoreNature cdiNature = CDICorePlugin.getCDI(file.getProject(), true);
		if(cdiNature == null)
			return null;
		
		IJavaElement[] elements = null;
		
		try {
			elements = ((ICodeAssist)input).codeSelect(wordRegion.getOffset(), wordRegion.getLength());
			if (elements == null) 
				return null;
			
			ArrayList<IHyperlink> hyperlinks = new ArrayList<IHyperlink>();
			for (IJavaElement element : elements) {
				int position = 0;
				if(element instanceof ResolvedSourceType || element instanceof ResolvedBinaryType){
					ICompilationUnit cUnit = (ICompilationUnit)input;
					element = cUnit.getElementAt(wordRegion.getOffset());
					if(element == null)
						continue;
					
					if(element instanceof IMethod){
						position = offset;
					}
				}

				if(findInjectedBeans(cdiNature, element, position, file)){
					hyperlinks.add(new InjectedPointListHyperlink(file, textViewer, wordRegion, element, position, document));
				}
			}
			
			if (hyperlinks != null && !hyperlinks.isEmpty()) {
				return (IHyperlink[])hyperlinks.toArray(new IHyperlink[hyperlinks.size()]);
			}
		} catch (JavaModelException jme) {
			CDIExtensionsPlugin.log(jme);
		}
		return null;
	}
	
	private boolean findInjectedBeans(CDICoreNature nature, IJavaElement element, int offset, IFile file){
		ICDIProject cdiProject = nature.getDelegate();
		
		if(cdiProject == null){
			return false;
		}
		
		Set<IBean> beans = cdiProject.getBeans(file.getFullPath());
		
		IInjectionPoint injectionPoint = CDIUtil.findInjectionPoint(beans, element, offset);
		if(injectionPoint == null){
			return false;
		}
		
		Set<IBean> resultBeanSet = cdiProject.getBeans(false, injectionPoint);
		if(resultBeanSet.size() > 0)
			return true;
		
		return false;
	}
}
