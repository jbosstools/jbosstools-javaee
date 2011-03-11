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

import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.jboss.tools.cdi.text.ext.hyperlink.xpl.MultipleHyperlinkPresenter;

public class MultipleHyperlinkPresenterManager {
	private static MultipleHyperlinkPresenter mhp = new MultipleHyperlinkPresenter(new RGB(0, 0, 255));
	private static boolean installed = false;
	private static MyPartListener listener = new MyPartListener();
	
	public static void installAndShow(ITextViewer viewer, IHyperlink[] hyperlinks, int previousIndex){
		if(installed)
			uninstall();
		
		mhp.install(viewer);
		mhp.showHyperlinks(hyperlinks, previousIndex);
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPartService().addPartListener(listener);
		installed = true;
	}
	
	public static void uninstall(){
		installed = false;
		mhp.uninstall();
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getPartService().removePartListener(listener);
	}
	
	static class MyPartListener implements IPartListener{
		public MyPartListener(){
			
		}
		
		public void partActivated(IWorkbenchPart arg0) {
		}

		public void partBroughtToTop(IWorkbenchPart arg0) {
		}

		public void partClosed(IWorkbenchPart arg0) {
			if(installed){
				uninstall();
			}
		}

		public void partDeactivated(IWorkbenchPart arg0) {
			
		}

		public void partOpened(IWorkbenchPart arg0) {
		}
	}
}
