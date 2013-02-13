/******************************************************************************* 
 * Copyright (c) 2013 Red Hat, Inc. 
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
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.ui.wizards.NewTypeWizardPage;
import org.eclipse.swt.widgets.Composite;
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
public class BeansXMLAccess {
	String folderName;
	String entity;
	String attribute;
	protected NewTypeWizardPage page;
	protected CheckBoxEditorWrapper check = null;
	protected boolean isEnabled = true;

	public BeansXMLAccess(NewTypeWizardPage page, String folderName, String entity, String attribute) {
		this.page = page;
		this.folderName = folderName;
		this.entity = entity;
		this.attribute = attribute;
	}

	public void create(Composite composite) {
		create(composite, true);
	}

	public void create(Composite composite, boolean initValue) {
		String label = "Register in beans.xml";
		check = NewBeanWizardPage.createCheckBoxField(composite, "register", label, initValue);
	}

	public boolean isSelected() {
		if(check != null) {
			return check.composite.getValue() == Boolean.TRUE
					&& check.checkBox.isEnabled();
		}
		return false;
	}

	public void setEnabled(boolean b) {
		isEnabled = b;
		validate();
	}

	public void validate() {
		if(check == null) {
			return;
		}
		IPackageFragmentRoot p = page.getPackageFragmentRoot();
		if(p != null && p.getResource().exists()) {
			String typeName = page.getPackageText() + "." + page.getTypeName();
			IProject project = p.getResource().getProject();
			boolean b = isRegisteredInBeansXML(project, typeName);
			check.composite.setEnabled(!b && isEnabled);
		} else {
			check.composite.setEnabled(isEnabled);
		}
	}

	public void registerInBeansXML() {
		IProject project = page.getCreatedType().getResource().getProject();
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
					c.setAttributeValue(attribute, page.getCreatedType().getFullyQualifiedName());
					//Check that 'typeName' is already registered in 'folderName'.
					if(as.getChildByPath(c.getPathPart()) == null) {
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
	}

	public boolean isRegisteredInBeansXML(IProject project, String typeName) {
		IPath path = NewBeansXMLCreationWizard.getContainerForBeansXML(project);
		if(path != null) {
			path = path.append("beans.xml").removeFirstSegments(1); //$NON-NLS-1$
			IFile beansxml = project.getFile(path);
			if(beansxml.exists()) {
				XModelObject o = EclipseResourceUtil.createObjectForResource(beansxml);
				if(o != null) {
					return  o.getChildByPath(folderName + "/" + typeName) != null;
				}
			}
		}			
		return false;
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
