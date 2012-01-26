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
			IProject project = fPage.getCreatedType().getResource().getProject();
			registerInBeansXML(project, fPage.getCreatedType().getFullyQualifiedName(), "Alternatives", CDIBeansConstants.ENT_CDI_CLASS, CDIBeansConstants.ATTR_CLASS); //$NON-NLS-1$
		}
		return res;
	}

	public static void registerInBeansXML(IProject project, String typeName, String folderName, String entity, String attribute) {
		IPath path = NewBeansXMLCreationWizard.getContainerForBeansXML(project);
		if(path != null) {
			path = path.append("beans.xml").removeFirstSegments(1); //$NON-NLS-1$
			IFile beansxml = project.getFile(path);
			if(!beansxml.exists()) {
				try {
					createBeansXML(beansxml);
				} catch (CoreException e) {
					CDIUIPlugin.getDefault().logError(e);
				}
			}
			if(beansxml.exists()) {
				XModelObject o = EclipseResourceUtil.createObjectForResource(beansxml);
				if(o != null) {
					XModelObject as = o.getChildByPath(folderName);
					XModelObject c = as.getModel().createModelObject(entity, new Properties());
					c.setAttributeValue(attribute, typeName);
					try {
						DefaultCreateHandler.addCreatedObject(as, c, 0);
						XActionInvoker.invoke("SaveActions.Save", o, new Properties()); //$NON-NLS-1$
					} catch (CoreException e) {
						CDIUIPlugin.getDefault().logError(e);
					}
				}
			}
		}
	}

	public static void createBeansXML(IFile f) throws CoreException {
		if(f.exists()) return;
		IFolder folder = (IFolder)f.getParent();
		if(!folder.exists()) {
			folder.create(true, true, new NullProgressMonitor());
		}
		f.create(getBeansXMLInitialContents(), true, new NullProgressMonitor());
	}

	public static InputStream getBeansXMLInitialContents() {
		FileAnyImpl file = (FileAnyImpl)XModelFactory.getDefaultInstance().createModelObject(CDIBeansConstants.ENT_CDI_BEANS, new Properties());
		return new ByteArrayInputStream(file.getAsText().getBytes());
	}


}
