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

package org.jboss.tools.cdi.ui.wizard;

import org.eclipse.core.resources.IProject;
import org.jboss.tools.cdi.ui.CDIUIMessages;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class NewInterceptorCreationWizard extends NewCDIElementWizard {
    
	public NewInterceptorCreationWizard() {
		setWindowTitle(CDIUIMessages.NEW_INTERCEPTOR_WIZARD_TITLE);
	}

	/*
	 * @see Wizard#createPages
	 */
	public void addPages() {
		super.addPages();
		if (fPage == null) {
			fPage = new  NewInterceptorWizardPage();
			((NewInterceptorWizardPage)fPage).init(getSelection());
			initPageFromAdapter();
			if(adapter != null) {
				((NewInterceptorWizardPage)fPage).setMayBeRegisteredInBeansXML(false);
			}
		}
		addPage(fPage);
	}

	/*(non-Javadoc)
	 * @see org.eclipse.jdt.internal.ui.wizards.NewElementWizard#canRunForked()
	 */
	protected boolean canRunForked() {
		return !fPage.isEnclosingTypeSelected();
	}

	public boolean performFinish() {
		boolean res = super.performFinish();
		if(res && ((NewInterceptorWizardPage)fPage).isToBeRegisteredInBeansXML()) {
			IProject project = fPage.getCreatedType().getResource().getProject();
			NewBeanCreationWizard.registerInBeansXML(project, fPage.getCreatedType().getFullyQualifiedName(), "Interceptors", "CDIClass", "class");
		}
		return res;
	}

}
