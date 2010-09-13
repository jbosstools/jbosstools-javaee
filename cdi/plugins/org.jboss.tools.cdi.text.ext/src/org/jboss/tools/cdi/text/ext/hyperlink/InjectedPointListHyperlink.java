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
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.jboss.tools.cdi.core.CDICoreNature;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.core.CDIUtil;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.ICDIProject;
import org.jboss.tools.cdi.core.IInjectionPoint;
import org.jboss.tools.cdi.text.ext.CDIExtensionsMessages;
import org.jboss.tools.common.text.ext.hyperlink.AbstractHyperlink;

public class InjectedPointListHyperlink extends AbstractHyperlink{
	private IFile file;
	private IJavaElement element;
	private int position;
	private IRegion region;
	private ITextViewer viewer;
	
	public InjectedPointListHyperlink(IFile file, ITextViewer viewer, IRegion region, IJavaElement element, int position, IDocument document){
		this.file = file;
		this.element = element;
		this.position = position;
		this.region = region;
		this.viewer = viewer;
		setDocument(document);
	}
	

	@Override
	protected IRegion doGetHyperlinkRegion(int offset) {
		return region;
	}

	protected void doHyperlink(IRegion region) {
		CDICoreNature cdiNature = CDICorePlugin.getCDI(file.getProject(), true);
		
		if(cdiNature == null){
			openFileFailed();
			return;
		}
		
		ICDIProject cdiProject = cdiNature.getDelegate();
		
		if(cdiProject == null){
			openFileFailed();
			return;
		}
		
		Set<IBean> beans = cdiProject.getBeans(file.getFullPath());
		
		IInjectionPoint injectionPoint = CDIUtil.findInjectionPoint(beans, element, position);
		if(injectionPoint == null){
			openFileFailed();
			return;
		}
		
		Set<IBean> resultBeanSet = cdiProject.getBeans(true, injectionPoint);
		List<IBean> resultBeanList = CDIUtil.sortBeans(resultBeanSet);
		
		Set<IBean> alternativeBeanSet = cdiProject.getBeans(false, injectionPoint);
		List<IBean> alternativeBeanList = CDIUtil.sortBeans(alternativeBeanSet);
			
		ArrayList<IHyperlink> hyperlinks = new ArrayList<IHyperlink>();
		
		if(resultBeanList.size() > 0){
			hyperlinks.add(new InjectedPointHyperlink(region, resultBeanList.get(0), getDocument(), true));
			//alternativeBeanList.remove(resultBeanList.get(0));
			if(alternativeBeanList.size() > 0)
				hyperlinks.add(new AlternativeInjectedPointListHyperlink(region, alternativeBeanList, viewer, getDocument()));
		}else if(alternativeBeanList.size() > 0){
			hyperlinks.add(new InjectedPointHyperlink(region, alternativeBeanList.get(0), getDocument(), true));
			//alternativeBeanList.remove(0);
			if(alternativeBeanList.size() > 0)
				hyperlinks.add(new AlternativeInjectedPointListHyperlink(region, alternativeBeanList, viewer, getDocument()));
		}
		
		IHyperlink[] result = hyperlinks.toArray(new IHyperlink[hyperlinks.size()]);
		
		if(result.length == 0){
			openFileFailed();
			return;
		}
		
		if(result.length == 1){
			((InjectedPointHyperlink)result[0]).doHyperlink(region);
		}else{
			MultipleHyperlinkPresenterManager.installAndShow(viewer, result);
		}
	}

	@Override
	public String getHyperlinkText() {
		return CDIExtensionsMessages.CDI_INJECTED_POINT_HYPERLINK_OPEN_INJECTED_BEAN;
	}

}
