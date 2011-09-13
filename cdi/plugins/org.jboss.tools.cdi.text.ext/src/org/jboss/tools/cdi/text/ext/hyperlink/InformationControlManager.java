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

import java.util.List;

import org.eclipse.jface.text.AbstractInformationControlManager;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.jboss.tools.cdi.text.ext.hyperlink.xpl.HierarchyInformationControl;
import org.jboss.tools.cdi.text.ext.hyperlink.xpl.InformationPresenter;

public class InformationControlManager {
	public static void showHyperlinks(String title, ITextViewer viwer, List<IHyperlink> hyperlinks){
		InformationPresenter presenter= new InformationPresenter(viwer, getHierarchyPresenterControlCreator(title, hyperlinks));
		presenter.setAnchor(AbstractInformationControlManager.ANCHOR_GLOBAL);
		presenter.setSizeConstraints(60, 10, true, false);
		presenter.install(viwer.getTextWidget());
		presenter.showInformation();
	}
	
	private static IInformationControlCreator getHierarchyPresenterControlCreator(final String title, final List<IHyperlink> hyperlinks) {
		return new IInformationControlCreator() {
			public IInformationControl createInformationControl(Shell parent) {
				int shellStyle= SWT.RESIZE;
				int treeStyle= SWT.V_SCROLL | SWT.H_SCROLL;
				HierarchyInformationControl iControl = new HierarchyInformationControl(parent, title, shellStyle, treeStyle, hyperlinks);
				iControl.setInput(hyperlinks);
				return iControl;
			}
		};
	}
}
