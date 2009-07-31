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
package org.jboss.tools.jsf.model.handlers;

import java.util.*;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import org.jboss.tools.common.meta.action.impl.*;
import org.jboss.tools.common.meta.action.impl.handlers.DefaultRemoveHandler;
import org.jboss.tools.common.model.*;
import org.jboss.tools.common.model.util.XModelObjectLoaderUtil;
import org.jboss.tools.jsf.messages.JSFUIMessages;
import org.jboss.tools.jsf.model.*;
import org.jboss.tools.jsf.model.helpers.*;
import org.jboss.tools.jsf.model.impl.NavigationRuleObjectImpl;
import org.jboss.tools.jst.web.model.ReferenceObject;

public class RenameViewSupport extends SpecialWizardSupport implements JSFConstants {
	String initialPath;
	ReferenceGroupImpl group;
	XModelObject page;
	ReferenceObject item;

	public void reset() {
		initGroup();
		initialPath = group.getAttributeValue("path"); //$NON-NLS-1$
		initialPath = AddViewSupport.revalidatePath(initialPath);
		page = (JSFProcessHelper.isPattern(initialPath)) ? null : getTarget().getModel().getByPath(initialPath); 
		setAttributeValue(0, ATT_FROM_VIEW_ID, initialPath);
	}
	
	void initGroup() {
		String entity = getTarget().getModelEntity().getName();
		item = null;
		if(ENT_PROCESS_GROUP.equals(entity)) {
			group = (ReferenceGroupImpl)getTarget();
		} else if(ENT_PROCESS_ITEM.equals(entity)) {
			group = (ReferenceGroupImpl)getTarget().getParent();
			if(group.getChildren().length > 1) item = (ReferenceObject)getTarget();
		}
	}

	public void action(String name) throws XModelException {
		if(FINISH.equals(name)) {
			execute();
			setFinished(true);
		} else if(CANCEL.equals(name)) {
			setFinished(true);
		} else if(HELP.equals(name)) {
			help();
		}
	}

	public String[] getActionNames(int stepId) {
		return new String[]{FINISH, CANCEL, HELP};
	}
	
	void execute() throws XModelException {
		Properties p = extractStepData(0);
		String path = AddViewSupport.revalidatePath(p.getProperty(ATT_FROM_VIEW_ID));
		if(initialPath.equals(path)) return;
		JSFProcessHelper h = JSFProcessHelper.getHelper(JSFProcessStructureHelper.instance.getProcess(group));
		h.addUpdateLock(this);
		try {
			if(item != null) {
				extract(initialPath, path);
			} else {
				replace(group, initialPath, path);
				if(page != null && "true".equals(p.getProperty("rename file")) && isFieldEditorEnabled(0, "rename file", p)) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					try {
						renameFile(page, path);
					} catch (CoreException e) {
						throw new XModelException(e);
					}
				}
			}
		} finally {
			h.removeUpdateLock(this);
			h.updateProcess();
		}
	}

	public boolean isActionEnabled(String name) {
		if(FINISH.equals(name)) {
			String path = getAttributeValue(0, ATT_FROM_VIEW_ID);
			path = AddViewSupport.revalidatePath(path);
			if(initialPath.equals(path)) return false;
		}
		return true;
	}
    
	public boolean isFieldEditorEnabled(int stepId, String name, Properties values) {
		if(name.equals("rename file")) { //$NON-NLS-1$
			if(page == null) return false;
			if(item != null) return false; // only one rule shall be renamed
			String path = AddViewSupport.revalidatePath(values.getProperty(ATT_FROM_VIEW_ID));
			if(path.equals(initialPath)) return false;
			if(JSFProcessHelper.isPattern(path)) return false;
			XModelObject page2 = getTarget().getModel().getByPath(path);
			if(page2 != null) return false;
		}
		return true;
	}
	
	public static void replace(ReferenceGroupImpl group, String oldPath, String newPath) throws XModelException {
		String pp = NavigationRuleObjectImpl.toNavigationRulePathPart(newPath);
		boolean isPattern = JSFProcessHelper.isPattern(newPath);
		XModel model = group.getModel();
		XModelObject process = group.getParent();
		FileFacesConfigImpl fcg = (FileFacesConfigImpl)process.getParent();
		int count = fcg.getRuleCount(newPath);
		XModelObject[] rs = group.getReferences();
		for (int i = rs.length - 1; i >= 0; i--) {
			model.changeObjectAttribute(rs[i], "index", "1000"); //$NON-NLS-1$ //$NON-NLS-2$
			model.changeObjectAttribute(rs[i], ATT_FROM_VIEW_ID, newPath);
			model.changeObjectAttribute(rs[i], "index", "" + (count + i)); //$NON-NLS-1$ //$NON-NLS-2$
		}
		if(process.getChildByPath(pp) == null) {
			model.changeObjectAttribute(group, ATT_NAME, pp);
			model.changeObjectAttribute(group, ATT_PATH, newPath);
		} else if(isPattern && rs.length > 0) {
			String index = rs[0].getAttributeValue("index"); //$NON-NLS-1$
			String ppi = pp + ":" + index; //$NON-NLS-1$
			group.setAttributeValue(ATT_PATH, newPath);
			if(process.getChildByPath(ppi) == null) {
				model.changeObjectAttribute(group, ATT_NAME, ppi);
			}
		} else if(isPattern && rs.length == 0) {
			int index = -1;
			while(process.getChildByPath(pp + ":" + index) != null) index--; //$NON-NLS-1$
			String ppi = pp + ":" + index; //$NON-NLS-1$
			model.changeObjectAttribute(group, ATT_PATH, newPath);
			model.changeObjectAttribute(group, ATT_NAME, ppi);
		} else if(!isPattern && rs.length == 0) {
			DefaultRemoveHandler.removeFromParent(group);
		}
		XModelObject[] gs = process.getChildren(ENT_PROCESS_GROUP);
		for (int i = 0; i < gs.length; i++) {
			XModelObject[] is = gs[i].getChildren(ENT_PROCESS_ITEM);
			for (int j = 0; j < is.length; j++) {
				XModelObject[] os = is[j].getChildren(ENT_PROCESS_ITEM_OUTPUT);
				for (int k = 0; k < os.length ; k++) {
					if(!oldPath.equals(os[k].getAttributeValue(ATT_PATH))) continue;
					ReferenceObject output = (ReferenceObject)os[k];
					XModelObject c = output.getReference();
					if(c != null) {
						model.changeObjectAttribute(c, ATT_TO_VIEW_ID, newPath);
					}
				}
			}
		}		
	}
	
	public static void renameFile(XModelObject page, String path) throws XModelException, CoreException {
		IResource r = (IResource)page.getAdapter(IResource.class);
		String initialPath = XModelObjectLoaderUtil.getResourcePath(page);
		String p = r.getFullPath().toString();
		if(!p.toLowerCase().endsWith(initialPath.toLowerCase())) return;
		p = p.substring(0, p.length() - initialPath.length()) + path;
		IPath np = new Path(p);
		provideParent(r.getWorkspace().getRoot().getFile(np));
		r.move(np, true, null);
		page.getModel().update();		
	}
	
	public static void provideParent(IResource resource) throws XModelException, CoreException {
		IResource parent = resource.getParent();
		if(parent.exists()) return;
		IFolder folder = resource.getWorkspace().getRoot().getFolder(parent.getFullPath());
		provideParent(folder);
		folder.create(true, true, null);
	}
	
	/*
	 * If action is called on item in a group that has more items, 
	 * the item will be extracted from this group and added to the other,
	 * or new group will be created. 
	 */	
	private void extract(String oldPath, String newPath) throws XModelException {
		String pp = NavigationRuleObjectImpl.toNavigationRulePathPart(newPath);
//		boolean isPattern = JSFProcessHelper.isPattern(newPath);
		XModel model = group.getModel();
		XModelObject process = group.getParent();
		FileFacesConfigImpl fcg = (FileFacesConfigImpl)process.getParent();
		int count = fcg.getRuleCount(newPath);
		XModelObject rule = item.getReference();
		model.changeObjectAttribute(rule, "index", "1000"); //$NON-NLS-1$ //$NON-NLS-2$
		model.changeObjectAttribute(rule, ATT_FROM_VIEW_ID, newPath);
		model.changeObjectAttribute(rule, "index", "" + (count)); //$NON-NLS-1$ //$NON-NLS-2$
		XModelObject g = process.getChildByPath(pp);
		if(g != null) return;
		JSFProcessHelper h = JSFProcessHelper.getHelper(process);
		g = h.findOrCreateGroup(newPath, pp);
		int[] cs = JSFProcessStructureHelper.instance.asIntArray(group, "shape"); //$NON-NLS-1$
		if(cs != null && cs.length > 1) {
			g.setAttributeValue("shape", "" + (cs[0] + 30) + "," + (cs[1] + 30) + ",0,0"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		}		
	}
    
	protected DefaultWizardDataValidator viewValidator = new ViewValidator();
    
	public WizardDataValidator getValidator(int step) {
		viewValidator.setSupport(this, step);
		return viewValidator;    	
	}
	
	class ViewValidator extends DefaultWizardDataValidator {
		public void validate(Properties data) {
			super.validate(data);
			if(message != null) return;
			String path = data.getProperty("from-view-id"); //$NON-NLS-1$
			if(!AddViewSupport.isCorrectPath(path)) {
				message = JSFUIMessages.ATTRIBUTE_FROM_VIEW_ID_IS_NOT_CORRECT;
			} 
			if(message != null) return;

		}		
	}

}
