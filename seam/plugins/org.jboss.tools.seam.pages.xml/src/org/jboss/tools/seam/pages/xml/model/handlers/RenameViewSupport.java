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
package org.jboss.tools.seam.pages.xml.model.handlers;

import java.util.*;

import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.*;
import org.jboss.tools.common.meta.action.impl.*;
import org.jboss.tools.common.meta.action.impl.handlers.DefaultRemoveHandler;
import org.jboss.tools.common.model.*;
import org.jboss.tools.common.model.util.XModelObjectLoaderUtil;
import org.jboss.tools.jst.web.model.AbstractWebFileImpl;
import org.jboss.tools.jst.web.model.ReferenceObject;
import org.jboss.tools.seam.pages.xml.SeamPagesXMLMessages;
import org.jboss.tools.seam.pages.xml.model.SeamPagesConstants;
import org.jboss.tools.seam.pages.xml.model.helpers.SeamPagesDiagramHelper;
import org.jboss.tools.seam.pages.xml.model.helpers.SeamPagesDiagramStructureHelper;

public class RenameViewSupport extends SpecialWizardSupport implements SeamPagesConstants {
	String initialPath;
	XModelObject page;
	ReferenceObject item;

	public boolean isEnabled(XModelObject target) {
		if(!super.isEnabled(target)) return false;
		if(!ENT_DIAGRAM_ITEM.equals(target.getModelEntity().getName())) {
			return false;
		}
		if(TYPE_EXCEPTION.equals(target.getAttributeValue(ATTR_TYPE))) {
			return false;
		}
		return true;
	}

	public void reset() {
		initItem();
		initialPath = item.getAttributeValue("path"); //$NON-NLS-1$
		initialPath = AddViewSupport.revalidatePath(initialPath);
		page = (SeamPagesDiagramHelper.isPattern(initialPath)) ? null : getTarget().getModel().getByPath(initialPath); 
		setAttributeValue(0, ATTR_VIEW_ID, initialPath);
	}
	
	void initItem() {
		String entity = getTarget().getModelEntity().getName();
		item = null;
		if(ENT_DIAGRAM_ITEM.equals(entity)) {
			item = (ReferenceObject)getTarget();
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
		String path = AddViewSupport.revalidatePath(p.getProperty(ATTR_VIEW_ID));
		if(initialPath.equals(path)) return;
		SeamPagesDiagramHelper h = SeamPagesDiagramHelper.getHelper(SeamPagesDiagramStructureHelper.instance.getDiagram(item));
		h.addUpdateLock(this);
		try {
			replace(item, initialPath, path);
			if(page != null && "true".equals(p.getProperty("rename file")) && isFieldEditorEnabled(0, "rename file", p)) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				try {
					renameFile(page, path);
				} catch (CoreException e) {
					throw new XModelException(e);
				}
			}
		} finally {
			h.removeUpdateLock(this);
			h.updateDiagram();
		}
	}

	public boolean isActionEnabled(String name) {
		if(FINISH.equals(name)) {
			String path = getAttributeValue(0, ATTR_VIEW_ID);
			path = AddViewSupport.revalidatePath(path);
			if(initialPath.equals(path)) return false;
		}
		return true;
	}
    
	public boolean isFieldEditorEnabled(int stepId, String name, Properties values) {
		if(name.equals("rename file")) { //$NON-NLS-1$
			if(page == null) return false;
			String path = AddViewSupport.revalidatePath(values.getProperty(ATTR_VIEW_ID));
			if(path.equals(initialPath)) return false;
			if(SeamPagesDiagramHelper.isPattern(path)) return false;
			XModelObject page2 = getTarget().getModel().getByPath(path);
			if(page2 != null) return false;
		}
		return true;
	}
	
	public static void replace(ReferenceObject group, String oldPath, String newPath) throws XModelException {
		String pp = SeamPagesDiagramHelper.toNavigationRulePathPart(newPath);
		boolean isPattern = SeamPagesDiagramHelper.isPattern(newPath);
		XModel model = group.getModel();
		XModelObject process = group.getParent();
		AbstractWebFileImpl fcg = (AbstractWebFileImpl)process.getParent();

		XModelObject rs = group.getReference();
		if(rs != null) model.changeObjectAttribute(rs, ATTR_VIEW_ID, newPath);

		if(process.getChildByPath(pp) == null) {
			model.changeObjectAttribute(group, ATTR_NAME, pp);
			model.changeObjectAttribute(group, ATTR_PATH, newPath);
		} else if(isPattern && rs != null) {
			String index = rs.getAttributeValue("index"); //$NON-NLS-1$
			String ppi = pp;
			group.setAttributeValue(ATTR_PATH, newPath);
			if(process.getChildByPath(ppi) == null) {
				model.changeObjectAttribute(group, ATTR_NAME, ppi);
			}
		} else if(isPattern && rs == null) {
			String ppi = pp;
			model.changeObjectAttribute(group, ATTR_PATH, newPath);
			model.changeObjectAttribute(group, ATTR_NAME, ppi);
		} else if(!isPattern && rs == null) {
			DefaultRemoveHandler.removeFromParent(group);
		}
		XModelObject[] gs = process.getChildren(ENT_DIAGRAM_ITEM);
		for (int i = 0; i < gs.length; i++) {
			XModelObject[] os = gs[i].getChildren(ENT_DIAGRAM_ITEM_OUTPUT);
			for (int k = 0; k < os.length ; k++) {
				if(!oldPath.equals(os[k].getAttributeValue(ATTR_PATH))) continue;
				ReferenceObject output = (ReferenceObject)os[k];
				XModelObject c = output.getReference();
				if(c != null) {
					model.changeObjectAttribute(c, ATTR_VIEW_ID, newPath);
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
	
	protected DefaultWizardDataValidator viewValidator = new ViewValidator();
    
	public WizardDataValidator getValidator(int step) {
		viewValidator.setSupport(this, step);
		return viewValidator;    	
	}
	
	class ViewValidator extends DefaultWizardDataValidator {
		public void validate(Properties data) {
			super.validate(data);
			if(message != null) return;
			String path = data.getProperty(ATTR_VIEW_ID);
			if(!AddViewSupport.isCorrectPath(path)) {
				message = SeamPagesXMLMessages.ATTRIBUTE_VIEW_ID_IS_NOT_CORRECT;
			} 
			if(message != null) return;

		}		
	}

}
