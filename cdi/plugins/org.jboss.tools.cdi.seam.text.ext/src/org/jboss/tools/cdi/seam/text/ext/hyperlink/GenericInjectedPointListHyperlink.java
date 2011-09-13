/******************************************************************************* 
 * Copyright (c) 2011 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.cdi.seam.text.ext.hyperlink;

import java.util.List;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.seam.text.ext.CDISeamExtMessages;
import org.jboss.tools.cdi.text.ext.hyperlink.AlternativeInjectedPointListHyperlink;
import org.jboss.tools.cdi.text.ext.hyperlink.InformationControlManager;

public class GenericInjectedPointListHyperlink extends
		AlternativeInjectedPointListHyperlink {

	public GenericInjectedPointListHyperlink(IRegion region, List<IBean> beans,
			ITextViewer viewer, IDocument document) {
		super(region, beans, viewer, document);
	}

	@Override
	public String getHyperlinkText() {
		return CDISeamExtMessages.CDI_INJECTED_POINT_HYPERLINK_SHOW_GENERIC_BEANS;
	}

	@Override
	protected IHyperlink createHyperlink(IRegion region, IBean bean) {
		return new GenericInjectedPointHyperlink(region, bean, getDocument());
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
			hyperlinks[0].open();
		}else{
			showHyperlinks(hyperlinks);
		}
	}
	
	private void showHyperlinks(IHyperlink[] hyperlinks){
		InformationControlManager.showHyperlinks(CDISeamExtMessages.CDI_SHOW_GENERIC_CONFIGURATION_POINTS_TITLE, viewer, hyperlinks);
	}

}
