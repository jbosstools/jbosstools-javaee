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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IAnnotatable;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.ICodeAssist;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;
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
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.ICDIProject;
import org.jboss.tools.cdi.core.IInjectionPoint;
import org.jboss.tools.cdi.core.IInjectionPointField;
import org.jboss.tools.cdi.core.IInjectionPointMethod;

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
			// Ignore. It is probably because of Java element's resource is not found 
		}
		
		if(file == null)
			return null;
		
		CDICoreNature cdiNature = CDICorePlugin.getCDI(file.getProject(), true);
		
		if(cdiNature == null)
			return null;
		
		ICDIProject cdiProject = cdiNature.getDelegate();
		
		if(cdiProject == null)
			return null;
		
		Set<IBean> beans = cdiProject.getBeans(file.getFullPath());
		
		int[] range = new int[]{wordRegion.getOffset(), wordRegion.getOffset() + wordRegion.getLength()};
		
		IJavaElement[] elements = null;
		
		try {
			elements = ((ICodeAssist)input).codeSelect(wordRegion.getOffset(), wordRegion.getLength());
			if (elements == null) 
				return null;
			
			ArrayList<IHyperlink> hyperlinks = new ArrayList<IHyperlink>();
			for (IJavaElement element : elements) {
				if (element instanceof IAnnotatable) {
					IAnnotatable annotatable = (IAnnotatable)element;
					
					IAnnotation annotation = annotatable.getAnnotation("Injected");
					if (annotation == null)
						continue;
					IInjectionPoint injectionPoint = findInjectionPoint(beans, element);
					if(injectionPoint != null){
						Set<IBean> resultBeanSet = cdiProject.getBeans(injectionPoint);
						List<IBean> resultBeanList = sortBeans(resultBeanSet);
						for(IBean bean : resultBeanList){
							if(bean != null)
								hyperlinks.add(new InjectedPointHyperlink(wordRegion, bean));
						}
					}
				}
			}
			if (hyperlinks != null && !hyperlinks.isEmpty()) {
				return (IHyperlink[])hyperlinks.toArray(new IHyperlink[hyperlinks.size()]);
			}
		} catch (JavaModelException jme) {
			// ignore
		}
		return null;
	}
	
	private IInjectionPoint findInjectionPoint(Set<IBean> beans, IJavaElement element){
		if(!(element instanceof IField) && (element instanceof IMethod) )
			return null;
		
		for(IBean bean : beans){
			Set<IInjectionPoint> injectionPoints = bean.getInjectionPoints();
			for(IInjectionPoint iPoint : injectionPoints){
				if(element instanceof IField && iPoint instanceof IInjectionPointField){
					if(((IInjectionPointField)iPoint).getField() != null && ((IInjectionPointField)iPoint).getField().equals(element))
						return iPoint;
				}else if(element instanceof IMethod && iPoint instanceof IInjectionPointMethod){
					if(((IInjectionPointMethod)iPoint).getMethod() != null && ((IInjectionPointMethod)iPoint).getMethod().equals(element))
						return iPoint;
					
				}
			}
		}
	return null;
	}
	
	private List<IBean> sortBeans(Set<IBean> beans){
		Set<IBean> alternativeBeans = new HashSet<IBean>();
		Set<IBean> nonAlternativeBeans = new HashSet<IBean>();
		
		for(IBean bean : beans){
			if(bean.isAlternative())
				alternativeBeans.add(bean);
			else
				nonAlternativeBeans.add(bean);
		}
		
		ArrayList<IBean> sortedBeans = new ArrayList<IBean>();
		sortedBeans.addAll(alternativeBeans);
		sortedBeans.addAll(nonAlternativeBeans);
		return sortedBeans;
	}

}
