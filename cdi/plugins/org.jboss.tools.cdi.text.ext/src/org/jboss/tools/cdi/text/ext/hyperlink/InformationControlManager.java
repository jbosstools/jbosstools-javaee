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

import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.jboss.tools.cdi.text.ext.hyperlink.xpl.HierarchyInformationControl;

public class InformationControlManager extends org.jboss.tools.common.text.ext.hyperlink.InformationControlManager {
	public static final InformationControlManager instance = new InformationControlManager();
	
	protected IInformationControlCreator getHierarchyPresenterControlCreator(final String title, final IHyperlink[] hyperlinks) {
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
