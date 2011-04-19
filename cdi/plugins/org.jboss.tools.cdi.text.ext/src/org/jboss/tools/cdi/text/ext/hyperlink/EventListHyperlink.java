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

import java.util.Set;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.jboss.tools.cdi.core.IInjectionPoint;
import org.jboss.tools.cdi.text.ext.CDIExtensionsMessages;
import org.jboss.tools.common.text.ext.hyperlink.AbstractHyperlink;

public class EventListHyperlink extends AbstractHyperlink{
	private ITextViewer viewer;
	private Set<IInjectionPoint> events;
	private IRegion region;
	private int previousIndex;
	
	public EventListHyperlink(ITextViewer viewer, IRegion region, Set<IInjectionPoint> events, IDocument document, int previousIndex){
		this.viewer = viewer;
		this.events = events;
		this.region = region;
		this.previousIndex = previousIndex;
		setDocument(document);
	}

	@Override
	protected IRegion doGetHyperlinkRegion(int offset) {
		return region;
	}

	protected void doHyperlink(IRegion region) {
		IHyperlink[] hyperlinks = new IHyperlink[events.size()];
		
		int index=0;
		for(IInjectionPoint event : events){
			hyperlinks[index++] = new EventHyperlink(region, event, getDocument());
		}
		
		if(hyperlinks.length == 0){
			openFileFailed();
			return;
		}
		
		if(hyperlinks.length == 1){
			((EventHyperlink)hyperlinks[0]).doHyperlink(region);
		}else{
			MultipleHyperlinkPresenterManager.installAndShow(viewer, hyperlinks, previousIndex);
		}
	}

	@Override
	public String getHyperlinkText() {
		return CDIExtensionsMessages.CDI_EVENT_LIST_HYPERLINK_OPEN_EVENTS;
	}

}
