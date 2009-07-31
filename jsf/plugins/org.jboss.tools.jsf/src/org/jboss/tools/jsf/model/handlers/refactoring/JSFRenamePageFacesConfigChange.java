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
package org.jboss.tools.jsf.model.handlers.refactoring;

import java.util.ArrayList;
import java.util.Properties;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;

import org.jboss.tools.common.meta.action.XActionInvoker;
import org.jboss.tools.common.model.XModelException;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.filesystems.impl.FolderImpl;
import org.jboss.tools.common.model.util.XModelObjectLoaderUtil;
import org.jboss.tools.jsf.messages.JSFUIMessages;
import org.jboss.tools.jsf.model.ReferenceGroupImpl;
import org.jboss.tools.jsf.model.handlers.RenameViewSupport;
import org.jboss.tools.jsf.model.helpers.JSFProcessHelper;
import org.jboss.tools.jsf.model.helpers.JSFProcessStructureHelper;
import org.jboss.tools.jsf.model.pv.JSFProjectsTree;
import org.jboss.tools.common.model.refactoring.RefactoringHelper;
import org.jboss.tools.jst.web.model.pv.WebProjectNode;

public class JSFRenamePageFacesConfigChange extends CompositeChange {
	XModelObject object;
	String newName;
	
	String oldText;
	String newText;
	XModelObject[] fs = new XModelObject[0];	
	
	Properties replacements = new Properties();
	
	public JSFRenamePageFacesConfigChange(XModelObject object, String newName) {
		super(JSFUIMessages.FACES_CONFIG_CHANGES);
		this.object = object;
		this.newName = newName;
		replacements.clear();
		oldText = XModelObjectLoaderUtil.getResourcePath(object);
		int i = oldText.lastIndexOf("/"); //$NON-NLS-1$
		newText = oldText.substring(0, i + 1) + newName;
		replacements.setProperty(oldText, newText);
		addChanges();
	}

	public JSFRenamePageFacesConfigChange(XModelObject object, String name, XModelObject newParent) {
		super(JSFUIMessages.FACES_CONFIG_CHANGES);
		this.object = object;
		this.newName = name;
		replacements.clear();
		String oldText = XModelObjectLoaderUtil.getResourcePath(object);
		String newText = XModelObjectLoaderUtil.getResourcePath(newParent) + "/" + name; //$NON-NLS-1$
		replacements.setProperty(oldText, newText);
		addChanges();
	}

	private void addChanges() {
		if(object == null) return;
		XModelObject root = JSFProjectsTree.getProjectsRoot(object.getModel());
		XModelObject conf = (root == null) ? null : root.getChildByPath("Configuration"); //$NON-NLS-1$
		fs = (conf == null) ? new XModelObject[0] : ((WebProjectNode)conf).getTreeChildren();
		addChanges(fs);
	}
	
	private void addChanges(XModelObject[] objects) {
		for (int i = 0; i < objects.length; i++) {
			int c = getChildren().length; 
			RefactoringHelper.addChanges(objects[i], replacements, this);
			if(c == getChildren().length) {
				XModelObject[] gs = findGroups(objects[i]);
				if(gs.length > 0) {
					add(new JSFRenamePathDiagramChange(objects[i], gs));
				}				
			}
		}
	}
	
	public Change perform(IProgressMonitor pm) throws CoreException {
		XModelObject parent = object.getParent();
		if(parent instanceof FolderImpl) {
			((FolderImpl)parent).update();
		}
		for (int i = 0; i < fs.length; i++) {
			performChangeInFile(fs[i]);
		}
		for (int i = 0; i < fs.length; i++) {
			if(fs[i].isModified()) {
				XActionInvoker.invoke("SaveActions.Save", fs[i], null); //$NON-NLS-1$
			}
		}
		return null;
	}

	private void performChangeInFile(XModelObject f) throws XModelException {
		replaceIcons(f, oldText, newText);
		XModelObject[] gs = findGroups(f);
		if(gs.length == 0) return;
		JSFProcessHelper h = JSFProcessHelper.getHelper(JSFProcessStructureHelper.instance.getProcess(f));
		h.addUpdateLock(this);
		try {
			for (int j = 0; j < gs.length; j++) {
				RenameViewSupport.replace((ReferenceGroupImpl)gs[j], oldText, newText);
			}
		} finally {
			h.removeUpdateLock(this);
			h.updateProcess();
		}
	}

	XModelObject[] findGroups(XModelObject f) {
		XModelObject process = JSFProcessStructureHelper.instance.getProcess(f);
		if(process == null) return new XModelObject[0];
		XModelObject[] gs = JSFProcessStructureHelper.instance.getGroups(process);
		ArrayList<XModelObject> list = new ArrayList<XModelObject>();
		for (int i = 0; i < gs.length; i++) {
			String path = gs[i].getAttributeValue("path"); //$NON-NLS-1$
			if(path != null && path.equals(oldText)) list.add(gs[i]);
		}
		return list.toArray(new XModelObject[0]);
	}

	static String[] ICON_ATTRIBUTES = {"small-icon", "large-icon"}; //$NON-NLS-1$ //$NON-NLS-2$

	static void replaceIcons(XModelObject f, String oldText, String newText) throws XModelException {
		if(oldText == null || oldText.length() == 0) return;
		if(f.getModelEntity().getAttribute(ICON_ATTRIBUTES[0]) != null) {
			for (int i = 0; i < ICON_ATTRIBUTES.length; i++) {
				String v = f.getAttributeValue(ICON_ATTRIBUTES[i]);
				if(oldText.equals(v)) f.getModel().editObjectAttribute(f, "small-icon", newText); //$NON-NLS-1$
			}
		}
		XModelObject[] cs = f.getChildrenForSave();
		for (int i = 0; i < cs.length; i++) replaceIcons(cs[i], oldText, newText);
	}

}
