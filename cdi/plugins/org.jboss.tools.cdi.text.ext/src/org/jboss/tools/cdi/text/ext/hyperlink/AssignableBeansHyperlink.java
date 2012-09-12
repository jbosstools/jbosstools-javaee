/******************************************************************************* 
 * Copyright (c) 2011-2012 Red Hat, Inc. 
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
	
	public AssignableBeansHyperlink(IRegion region, IInjectionPoint injectionPoint, IDocument document){
		this.injectionPoint = injectionPoint;
		setRegion(region);
		setDocument(document);
	}

	protected void doHyperlink(IRegion region) {
		Display display = Display.getCurrent();
		if(display == null) {
			display = Display.getDefault();
		}
		AssignableBeansDialog dialog = new AssignableBeansDialog(display.getActiveShell());
		dialog.setInjectionPoint(injectionPoint);
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
		Display display = Display.getCurrent();
		if(display == null) {
			display = Display.getDefault();
		}
		AssignableBeansDialog dialog = new AssignableBeansDialog(display.getActiveShell());
		dialog.setInjectionPoint(injectionPoint);
		return dialog.beans;
	}
}
