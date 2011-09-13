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
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.text.ext.CDIExtensionsMessages;
import org.jboss.tools.common.text.ext.hyperlink.AbstractHyperlink;

public class AlternativeInjectedPointListHyperlink extends AbstractHyperlink{
	private IRegion region;
	protected List<IBean> beans;
	protected ITextViewer viewer;
	
	public AlternativeInjectedPointListHyperlink(IRegion region, List<IBean> beans, ITextViewer viewer, IDocument document){
		this.beans = beans;
		this.region = region;
		this.viewer = viewer;
		setDocument(document);
	}
	

	@Override
	protected IRegion doGetHyperlinkRegion(int offset) {
		return region;
	}

	protected void doHyperlink(IRegion region) {
		IHyperlink[] hyperlinks = new IHyperlink[beans.size()];
		
		int index=0;
		for(IBean bean : beans){
			hyperlinks[index++] = createHyperlink(region, bean);
		}
		
		if(hyperlinks.length == 0){
			openFileFailed();
			return;
		}
		
		if(hyperlinks.length == 1){
			((InjectedPointHyperlink)hyperlinks[0]).doHyperlink(region);
		}else{
			MultipleHyperlinkPresenterManager.installAndShow(viewer, hyperlinks);
		}
	}

	protected IHyperlink createHyperlink(IRegion region, IBean bean) {
		return new InjectedPointHyperlink(region, bean, getDocument());
	}

	@Override
	public String getHyperlinkText() {
		return CDIExtensionsMessages.CDI_INJECTED_POINT_HYPERLINK_SHOW_ALTERNATIVES;
	}

}
