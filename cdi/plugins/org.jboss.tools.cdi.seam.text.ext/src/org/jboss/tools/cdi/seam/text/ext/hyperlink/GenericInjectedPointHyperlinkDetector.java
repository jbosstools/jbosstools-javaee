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
package org.jboss.tools.cdi.seam.text.ext.hyperlink;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeRoot;
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
import org.jboss.tools.cdi.core.CDIUtil;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.IBeanMember;
import org.jboss.tools.cdi.core.ICDIProject;
import org.jboss.tools.cdi.core.IClassBean;
import org.jboss.tools.cdi.core.IInjectionPoint;
import org.jboss.tools.cdi.core.IInjectionPointParameter;
import org.jboss.tools.cdi.seam.solder.core.generic.GenericClassBean;
import org.jboss.tools.cdi.seam.text.ext.CDISeamExtPlugin;

public class GenericInjectedPointHyperlinkDetector extends AbstractHyperlinkDetector{
	protected IRegion region;
	protected IDocument document;
	protected ITextViewer viewer;

	public IHyperlink[] detectHyperlinks(ITextViewer textViewer,
			IRegion region, boolean canShowMultipleHyperlinks) {
		this.region = region;
		this.viewer = textViewer;
		
		ITextEditor textEditor= (ITextEditor)getAdapter(ITextEditor.class);
		if (region == null || !canShowMultipleHyperlinks || !(textEditor instanceof JavaEditor))
			return null;
		
		int offset= region.getOffset();
		
		ITypeRoot input = EditorUtility.getEditorInputJavaElement(textEditor, false);
		if (input == null)
			return null;

		document= textEditor.getDocumentProvider().getDocument(textEditor.getEditorInput());
		IRegion wordRegion= JavaWordFinder.findWord(document, offset);
		if (wordRegion == null)
			return null;
		
		IProject project = null;
		
		project = input.getJavaProject().getProject();
		
		if(project == null)
			return null;
		
		CDICoreNature cdiNature = CDIUtil.getCDINatureWithProgress(project);
		if(cdiNature == null)
			return null;
		
		IJavaElement[] elements = null;
		
		try {
			elements = input.codeSelect(wordRegion.getOffset(), wordRegion.getLength());
			if (elements == null) 
				return null;
			if(elements.length != 1)
				return null;
			
			ArrayList<IHyperlink> hyperlinks = new ArrayList<IHyperlink>();
			int position = 0;
			if(elements[0] instanceof IType){
				elements[0] = input.getElementAt(wordRegion.getOffset());
				if(elements[0] == null)
					return null;
				
				if(elements[0] instanceof IMethod){
					position = offset;
				}
			}

			findInjectedBeans(cdiNature, elements[0], position, input.getPath(), hyperlinks);
			
			if (hyperlinks != null && !hyperlinks.isEmpty()) {
				return (IHyperlink[])hyperlinks.toArray(new IHyperlink[hyperlinks.size()]);
			}
		} catch (JavaModelException jme) {
			CDISeamExtPlugin.getDefault().logError(jme);
		}
		return null;
	}
	
	protected void findInjectedBeans(CDICoreNature nature, IJavaElement element, int offset, IPath path, ArrayList<IHyperlink> hyperlinks){
		ICDIProject cdiProject = nature.getDelegate();
		
		if(cdiProject == null) {
			return;
		}
		
		Set<IBean> beans = cdiProject.getBeans(path);
		
		Set<IInjectionPoint> injectionPoints = findInjectionPoints(beans, element, offset);
		if(injectionPoints.isEmpty()) {
			return;
		}
		
		Set<IBean> resultBeanSet2 = new HashSet<IBean>();

		for (IInjectionPoint injectionPoint: injectionPoints) {
			Set<IBean> resultBeanSet = cdiProject.getBeans(true, injectionPoint);
		
			for (IBean b: resultBeanSet) {
				IClassBean cb = null;
				if(b instanceof IClassBean) {
					cb = (IClassBean)b;
				}
				if(b instanceof IBeanMember) {
					cb = ((IBeanMember)b).getClassBean();
				}
				if(cb instanceof GenericClassBean) {
					GenericClassBean gcb = (GenericClassBean)cb;
					IBean gpb = gcb.getGenericProducerBean();
					if(gpb != null) {
						resultBeanSet2.add(gpb);
					}
				} else if(injectionPoint.getClassBean() instanceof GenericClassBean
					&& injectionPoint.isAnnotationPresent(((GenericClassBean)injectionPoint.getClassBean()).getVersion().getInjectGenericAnnotationTypeName())) {
					resultBeanSet2.add(b);
				}
			}
		}

		List<IBean> resultBeanList = CDIUtil.sortBeans(resultBeanSet2);		
		if(resultBeanList.size() == 1) {
			hyperlinks.add(new GenericInjectedPointHyperlink(region, resultBeanList.get(0), document));
		} else if(resultBeanList.size() > 0) {
			hyperlinks.add(new GenericInjectedPointListHyperlink(region, resultBeanList, viewer, document));
		}
	}

	public static Set<IInjectionPoint> findInjectionPoints(Set<IBean> beans, IJavaElement element, int position) {
		Set<IInjectionPoint> results = new HashSet<IInjectionPoint>();

		for (IBean bean : beans) {
			Set<IInjectionPoint> injectionPoints = bean.getInjectionPoints();
			for (IInjectionPoint iPoint : injectionPoints) {
				if(element != null && iPoint.isDeclaredFor(element)) {
					results.add(iPoint);
				} else if(iPoint instanceof IInjectionPointParameter && position != 0) {
					if(iPoint.getStartPosition() <= position && (iPoint.getStartPosition()+iPoint.getLength()) >= position)
						results.add(iPoint);
				}
			}
		}
		return results;
	}

}
