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
package org.jboss.tools.cdi.text.ext.hyperlink;

import java.util.Set;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.swt.widgets.Display;
import org.jboss.tools.cdi.core.ICDIElement;
import org.jboss.tools.cdi.core.IInjectionPoint;
import org.jboss.tools.cdi.text.ext.CDIExtensionsMessages;
import org.jboss.tools.common.text.ext.hyperlink.AbstractHyperlink;

public class AssignableBeansHyperlink extends AbstractHyperlink implements ITestableCDIHyperlink{
	protected IInjectionPoint injectionPoint;
	IRegion region;
	
	public AssignableBeansHyperlink(IRegion region, IInjectionPoint injectionPoint, IDocument document){
		this.injectionPoint = injectionPoint;
		this.region = region;
		setDocument(document);
	}

	@Override
	protected IRegion doGetHyperlinkRegion(int offset) {
		return region;
	}

	protected void doHyperlink(IRegion region) {
		Display display = Display.getCurrent();
		if(display == null) {
			display = Display.getDefault();
		}
		AssignableBeansDialog dialog = new AssignableBeansDialog(display.getActiveShell());
		dialog.setInjectionPoint(injectionPoint);
		dialog.create();
		dialog.getShell().setText(CDIExtensionsMessages.ASSIGNABLE_BEANS_DIALOG_TITLE);
		dialog.getShell().setSize(700, 400);
		dialog.open();
	}

	@Override
	public String getHyperlinkText() {
		return CDIExtensionsMessages.CDI_INJECTED_POINT_HYPERLINK_SHOW_ASSIGNABLE;
	}
	
	public ICDIElement getCDIElement() {
		return null;
	}

	public Set<? extends ICDIElement> getCDIElements() {
		return null;
	}

}
