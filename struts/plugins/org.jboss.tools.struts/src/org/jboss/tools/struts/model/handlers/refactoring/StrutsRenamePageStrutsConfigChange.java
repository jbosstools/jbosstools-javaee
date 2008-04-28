/*******************************************************************************
 * Copyright (c) 2007 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.struts.model.handlers.refactoring;

import java.util.Properties;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;

import org.jboss.tools.common.meta.action.XActionInvoker;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.filesystems.impl.FolderImpl;
import org.jboss.tools.common.model.util.XModelObjectLoaderUtil;
import org.jboss.tools.common.model.refactoring.RefactoringHelper;
import org.jboss.tools.struts.StrutsModelPlugin;
import org.jboss.tools.struts.messages.StrutsUIMessages;
import org.jboss.tools.struts.model.handlers.page.RenamePageHandler;
import org.jboss.tools.struts.model.helpers.StrutsProcessHelper;
import org.jboss.tools.struts.model.helpers.StrutsProcessStructureHelper;
import org.jboss.tools.struts.webprj.model.helpers.WebModulesHelper;

public class StrutsRenamePageStrutsConfigChange extends CompositeChange {
	XModelObject object;
	String newName;
	
	XModelObject[] fs;
	String oldPath;
	String newPath;
	
	Properties replacements = new Properties();
	
	public StrutsRenamePageStrutsConfigChange(XModelObject object, String newName) {
		super(StrutsUIMessages.STRUTS_CONFIG_CHANGES);
		this.object = object;
		this.newName = newName;
		replacements.clear();
		oldPath = XModelObjectLoaderUtil.getResourcePath(object);
		int i = oldPath.lastIndexOf("/"); //$NON-NLS-1$
		newPath = oldPath.substring(0, i + 1) + newName;

		String oldText = "\"" + oldPath + "\""; //$NON-NLS-1$ //$NON-NLS-2$
		String newText = "\"" + newPath + "\""; //$NON-NLS-1$ //$NON-NLS-2$
		replacements.setProperty(oldText, newText);
		try {
			addChanges();
		} catch (Exception e) {
			StrutsModelPlugin.getPluginLog().logError(e);
		}
	}

	private void addChanges() throws Exception {
		if(object == null) return;
		fs = WebModulesHelper.getInstance(object.getModel()).getAllConfigs();
		addChanges(fs);
	}
	
	private void addChanges(XModelObject[] objects) {
		for (int i = 0; i < objects.length; i++) {
			RefactoringHelper.addChanges(objects[i], replacements, this);
		}
	}

	public Change perform(IProgressMonitor pm) throws CoreException {
//		Change undo = super.perform(pm);
		if(fs == null) return null;
		XModelObject parent = object.getParent();
		if(parent instanceof FolderImpl) {
			((FolderImpl)parent).update();
		}
		for (int i = 0; i < fs.length; i++) {
			try {
				onPageRename(fs[i], oldPath, newPath);
			} catch (Exception e) {
				throw new CoreException(new Status(IStatus.ERROR, "org.jboss.tools.struts", 0, e.getMessage(), e)); //$NON-NLS-1$
			}
		}
		for (int i = 0; i < fs.length; i++) {
			if(fs[i].isModified()) {
				XActionInvoker.invoke("SaveActions.Save", fs[i], null); //$NON-NLS-1$
			}
		}
		return null;
	}
	
    static void onPageRename(XModelObject config, String oldpath, String path) throws Exception {
		XModelObject process = StrutsProcessStructureHelper.instance.getProcess(config);
		if(process == null) return;
		XModelObject page = findPage(process, oldpath);
    	if(page != null) {
    		StrutsProcessHelper.getHelper(page.getParent()).resetPage(page, oldpath, path);
    		page.getModel().changeObjectAttribute(page, "path", path); //$NON-NLS-1$
    	}
    	RenamePageHandler.changeMatchingAttributesInHierarhy(config, oldpath, path);
    }
    
    static XModelObject findPage(XModelObject process, String oldpath) {
		XModelObject[] items = process.getChildren();
		for (int i = 0; i < items.length; i++) {
			if("forward".equals(items[i].getAttributeValue("type"))) continue; //$NON-NLS-1$ //$NON-NLS-2$
			if(!oldpath.equals(items[i].getAttributeValue("path"))) continue; //$NON-NLS-1$
			return items[i];
		}
		return null;
    }

}
