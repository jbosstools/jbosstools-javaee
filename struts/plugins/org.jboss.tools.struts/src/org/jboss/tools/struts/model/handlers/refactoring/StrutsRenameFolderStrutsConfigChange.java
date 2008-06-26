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

import java.util.*;
import org.eclipse.core.runtime.*;
import org.eclipse.ltk.core.refactoring.*;

import org.jboss.tools.common.meta.action.XActionInvoker;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.refactoring.RefactoringHelper;
import org.jboss.tools.struts.StrutsModelPlugin;
import org.jboss.tools.struts.messages.StrutsUIMessages;
import org.jboss.tools.struts.model.handlers.page.RenamePageHandler;
import org.jboss.tools.struts.model.helpers.*;
import org.jboss.tools.struts.webprj.model.helpers.WebModulesHelper;

public class StrutsRenameFolderStrutsConfigChange extends CompositeChange {
	XModelObject object;
	String newName;
	
	XModelObject[] fs;
	String oldPath;
	String newPath;
	
	Properties replacements = new Properties();
	
	public StrutsRenameFolderStrutsConfigChange(XModelObject object, String newName) {
		super(StrutsUIMessages.STRUTS_CONFIG_CHANGES);
		this.object = object;
		this.newName = newName;
		replacements.clear();
		oldPath = object.getAttributeValue("name"); //$NON-NLS-1$
		newPath = newName;

		oldPath = "/" + oldPath + "/"; //$NON-NLS-1$ //$NON-NLS-2$
		newPath = "/" + newPath + "/"; //$NON-NLS-1$ //$NON-NLS-2$
		XModelObject parent = object.getParent();
		while(parent != null && parent.getFileType() == XModelObject.FOLDER) {
			oldPath = "/" + parent.getAttributeValue("name") + oldPath; //$NON-NLS-1$ //$NON-NLS-2$
			newPath = "/" + parent.getAttributeValue("name") + newPath; //$NON-NLS-1$ //$NON-NLS-2$
			parent = parent.getParent();
		}
		replacements.setProperty("\"" + oldPath, "\"" + newPath); //$NON-NLS-1$ //$NON-NLS-2$
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
			int c = getChildren().length;
			RefactoringHelper.addChanges(objects[i], replacements, this);
			if(c == getChildren().length) {
				XModelObject process = StrutsProcessStructureHelper.instance.getProcess(objects[i]);
				if(process != null && findPages(process, oldPath).length > 0) {
					add(new StrutsRenamePathDiagramChange(objects[i], findPages(process, oldPath)));
				}
			}
		}
	}

	public Change perform(IProgressMonitor pm) throws CoreException {
//		Change undo = super.perform(pm);
		if(fs == null) return null;
		for (int i = 0; i < fs.length; i++) {
			try {
				onFolderRename(fs[i], oldPath, newPath);
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
	
    static void onFolderRename(XModelObject config, String oldpath, String path) throws Exception {
		XModelObject process = StrutsProcessStructureHelper.instance.getProcess(config);
		if(process == null) return;
		XModelObject[] pages = findPages(process, oldpath);
    	for (int i = 0; i < pages.length; i++) {
    		String op = pages[i].getAttributeValue("path"); //$NON-NLS-1$
    		String np = path + op.substring(oldpath.length());
    		StrutsProcessHelper.getHelper(pages[i].getParent()).resetPage(pages[i], op, np);
    		pages[i].getModel().changeObjectAttribute(pages[i], "path", np); //$NON-NLS-1$
        	RenamePageHandler.changeMatchingAttributesInHierarhy(config, op, np);
    	}
    }
    
    static XModelObject[] findPages(XModelObject process, String oldpath) {
    	ArrayList<XModelObject> list = new ArrayList<XModelObject>();
		XModelObject[] items = process.getChildren();
		for (int i = 0; i < items.length; i++) {
			if("forward".equals(items[i].getAttributeValue("type"))) continue; //$NON-NLS-1$ //$NON-NLS-2$
			String p = items[i].getAttributeValue("path"); //$NON-NLS-1$
			if(p != null && p.startsWith(oldpath)) {
				list.add(items[i]);
			}
		}
		return list.toArray(new XModelObject[0]);
    }

}
