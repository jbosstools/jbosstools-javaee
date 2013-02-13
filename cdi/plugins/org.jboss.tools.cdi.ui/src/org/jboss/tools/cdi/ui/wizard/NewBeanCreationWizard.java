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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Properties;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.ui.wizards.NewClassWizardPage;
import org.jboss.tools.cdi.ui.CDIUIMessages;
import org.jboss.tools.cdi.ui.CDIUIPlugin;
import org.jboss.tools.cdi.xml.beans.model.CDIBeansConstants;
import org.jboss.tools.common.meta.action.XActionInvoker;
import org.jboss.tools.common.meta.action.impl.handlers.DefaultCreateHandler;
import org.jboss.tools.common.model.XModelFactory;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.filesystems.impl.FileAnyImpl;
import org.jboss.tools.common.model.util.EclipseResourceUtil;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class NewBeanCreationWizard extends NewCDIElementWizard {

	public NewBeanCreationWizard() {
		setWindowTitle(CDIUIMessages.NEW_BEAN_WIZARD_TITLE);
	}

	protected void initPageFromAdapter() {
		super.initPageFromAdapter();
		if(adapter != null) {
			((NewBeanWizardPage)fPage).setAlternative(true);
			((NewBeanWizardPage)fPage).setMayBeRegisteredInBeansXML(false);
		}
	}
	/*
	 * @see Wizard#createPages
	 */
	public void addPages() {
		super.addPages();
		if (fPage == null) {
			fPage = new NewBeanWizardPage();
			((NewClassWizardPage)fPage).init(getSelection());
			initPageFromAdapter();
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
		if(res && ((NewBeanWizardPage)fPage).isToBeRegisteredInBeansXML()) {
			((NewBeanWizardPage)fPage).registerInBeansXML.registerInBeansXML();
		}
		return res;
	}

}
