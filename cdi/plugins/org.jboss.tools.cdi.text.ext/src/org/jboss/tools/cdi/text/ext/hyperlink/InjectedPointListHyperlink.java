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

import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jface.text.hyperlink.MultipleHyperlinkPresenter;
import org.eclipse.swt.graphics.RGB;
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
	private IRegion region;
	private ITextViewer viewer;
	
	private static MultipleHyperlinkPresenter mhp = new MultipleHyperlinkPresenter(new RGB(0, 0, 255));
	private static boolean installed = false;
	
	public InjectedPointListHyperlink(IFile file, ITextViewer viewer, IRegion region, IJavaElement element, IDocument document){
		this.file = file;
		this.element = element;
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
		
		IInjectionPoint injectionPoint = CDIUtil.findInjectionPoint(beans, element);
		if(injectionPoint == null){
			openFileFailed();
			return;
		}
		
		Set<IBean> resultBeanSet = cdiProject.getBeans(injectionPoint);
		List<IBean> resultBeanList = CDIUtil.sortBeans(resultBeanSet);
			
		IHyperlink[] hyperlinks = new IHyperlink[resultBeanList.size()];
		
		int index=0;
		for(IBean bean : resultBeanList){
			hyperlinks[index++] = new InjectedPointHyperlink(region, bean, getDocument());
		}
		
		if(hyperlinks.length == 0){
			openFileFailed();
			return;
		}
		
		if(hyperlinks.length == 1){
			((InjectedPointHyperlink)hyperlinks[0]).doHyperlink(region);
		}else{
			if(installed)
				mhp.uninstall();
			
			installed = true;
			
			mhp.install(viewer);
			mhp.showHyperlinks(hyperlinks);
		}
	}

	@Override
	public String getHyperlinkText() {
		return CDIExtensionsMessages.CDI_INJECTED_POINT_HYPERLINK_OPEN_INJECTED_CLASS;
	}

}
