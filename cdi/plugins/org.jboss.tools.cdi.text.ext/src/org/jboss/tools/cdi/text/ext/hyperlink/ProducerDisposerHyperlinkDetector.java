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
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.ICodeAssist;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
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
import org.jboss.tools.cdi.core.CDIConstants;
import org.jboss.tools.cdi.core.CDICoreNature;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.core.CDIUtil;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.IBeanMethod;
import org.jboss.tools.cdi.core.ICDIProject;
import org.jboss.tools.cdi.core.IClassBean;
import org.jboss.tools.cdi.core.IProducer;
import org.jboss.tools.cdi.core.IProducerMethod;
import org.jboss.tools.cdi.text.ext.CDIExtensionsPlugin;

public class ProducerDisposerHyperlinkDetector extends AbstractHyperlinkDetector {
	
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
		
		Set<IBean> beans = getBeans(file);
		
		if(beans == null)
			return null;
		
		IJavaElement[] elements = null;
		
		try {
			elements = ((ICodeAssist)input).codeSelect(wordRegion.getOffset(), wordRegion.getLength());
			if (elements == null) 
				return null;
			
			ArrayList<IHyperlink> hyperlinks = new ArrayList<IHyperlink>();
			for (IJavaElement element : elements) {
				
				if(element instanceof IType){
					if(CDIConstants.PRODUCES_ANNOTATION_TYPE_NAME.equals(((IType) element).getFullyQualifiedName())){
						ICompilationUnit cUnit = (ICompilationUnit)input;
						element = cUnit.getElementAt(wordRegion.getOffset());
						if(element == null)
							continue;
					}
				}
				
				if (element instanceof IMethod) {
					for(IBean bean : beans){
						if(bean instanceof IClassBean){
							IProducerMethod producer = getProducer((IClassBean)bean, (IMethod)element);
							if(producer != null){
								List<IMethod> disposers = findDisposerMethods(producer);
								for(IMethod method : disposers){
									hyperlinks.add(new DisposerHyperlink(region, method, document));
								}
							}else{
								IBeanMethod disposer = getDisposer((IClassBean)bean, (IMethod)element);
								if(disposer != null){
									List<IMethod> producers = findProducerMethods((IClassBean)bean, disposer);
									for(IMethod method : producers){
										hyperlinks.add(new ProducerHyperlink(region, method, document));
									}
								}
							}
						}
					}
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
	
	private Set<IBean> getBeans(IFile file){
		CDICoreNature cdiNature = CDIUtil.getCDINatureWithProgress(file.getProject());
		
		if(cdiNature == null)
			return null;
		
		
		ICDIProject cdiProject = cdiNature.getDelegate();
		
		if(cdiProject == null)
			return null;
		
		
		Set<IBean> beans = cdiProject.getBeans(file.getFullPath());
		return beans;
	}
	
	private IProducerMethod getProducer(IClassBean classBean, IMethod method){
		for(IProducer producer : classBean.getProducers()){
			if(producer instanceof IProducerMethod && ((IProducerMethod)producer).getMethod().equals(method))
				return (IProducerMethod)producer;
		}
		return null;
	}

	private IBeanMethod getDisposer(IClassBean classBean, IMethod method){
		for(IBeanMethod disposer : classBean.getDisposers()){
			if(disposer.getMethod().equals(method))
				return disposer;
		}
		return null;
	}
	
	private List<IMethod> findProducerMethods(IClassBean classBean, IBeanMethod disposer){
		ArrayList<IMethod> methods = new ArrayList<IMethod>();
		for(IProducer producer : classBean.getProducers()){
			if(producer instanceof IProducerMethod){
				for(IBeanMethod beanMethod : producer.getCDIProject().resolveDisposers((IProducerMethod)producer)){
					if(beanMethod.equals(disposer))
						methods.add(((IProducerMethod)producer).getMethod());
				}
			}
		}
		return methods;
	}

	private List<IMethod> findDisposerMethods(IProducerMethod producer){
		ArrayList<IMethod> methods = new ArrayList<IMethod>();
		for(IBeanMethod beanMethod : producer.getCDIProject().resolveDisposers(producer)){
			methods.add(beanMethod.getMethod());
		}
		return methods;
	}
	
}
