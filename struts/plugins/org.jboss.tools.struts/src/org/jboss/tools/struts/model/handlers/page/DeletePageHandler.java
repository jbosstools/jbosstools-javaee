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
package org.jboss.tools.struts.model.handlers.page;

import java.util.Properties;

import org.eclipse.core.resources.IFile;
import org.eclipse.osgi.util.NLS;

import org.jboss.tools.common.model.ServiceDialog;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.filesystems.impl.DeleteFileHandler;
import org.jboss.tools.common.model.filesystems.impl.FileAnyImpl;
import org.jboss.tools.common.model.undo.XRemoveUndo;
import org.jboss.tools.common.model.undo.XUndoManager;
import org.jboss.tools.struts.StrutsConstants;
import org.jboss.tools.struts.StrutsModelPlugin;
import org.jboss.tools.struts.StrutsPreference;
import org.jboss.tools.struts.messages.StrutsUIMessages;
import org.jboss.tools.struts.model.StrutsProcessImpl;
import org.jboss.tools.struts.model.helpers.StrutsProcessHelper;
import org.jboss.tools.struts.model.helpers.StrutsProcessStructureHelper;

public class DeletePageHandler implements StrutsConstants {

    public DeletePageHandler() {}

    public void executeHandler(XModelObject object, Properties p) throws Exception {
        String target = object.getAttributeValue(ATT_NAME);
        if(!object.isActive()) return;
        if(StrutsProcessHelper.getReferers(object.getParent(), target).size() > 0) {
            ServiceDialog d = object.getModel().getService();
            String msg = NLS.bind(StrutsUIMessages.PAGE_IS_REFERENCED,object.getPresentationString());
            d.showDialog(StrutsUIMessages.WARNING, msg, new String[]{StrutsUIMessages.OK}, null, ServiceDialog.WARNING);
        } else {
			XModelObject page = StrutsProcessStructureHelper.instance.getPhysicalPage(object);
			IFile f = getFile(object);
			boolean deleteFile = true;
        	if(f != null) {
				try {
					deleteFile = "yes".equals(StrutsPreference.REMOVE_PAGE_AND_FILE.getValue()); //$NON-NLS-1$
				} catch (Exception e) {
					StrutsModelPlugin.getPluginLog().logError(e);
				}
				ServiceDialog d = object.getModel().getService();
				Properties xp = new Properties();
				xp.setProperty(ServiceDialog.DIALOG_MESSAGE, StrutsUIMessages.DELETE_PAGE + object.getPresentationString() + "?"); //$NON-NLS-2$
				xp.setProperty(ServiceDialog.CHECKBOX_MESSAGE, StrutsUIMessages.REMOVE_FILE_FROM_DISK);
				xp.put(ServiceDialog.CHECKED, new Boolean(deleteFile));
				if(!d.openConfirm(xp)) return;
				deleteFile = ((Boolean)xp.get(ServiceDialog.CHECKED)).booleanValue();
				try {
					StrutsPreference.REMOVE_PAGE_AND_FILE.setValue(deleteFile ? "yes" : "no"); //$NON-NLS-1$ //$NON-NLS-2$
				} catch (Exception e) {
					StrutsModelPlugin.getPluginLog().logError(e);
				}
        	}
            XModelObject parent = object.getParent();
            object.removeFromParent();
            XUndoManager undo = parent.getModel().getUndoManager();
            if(undo != null) {
                parent.getModel().getUndoManager().addUndoable(new DeletePageUndo(parent, object));
            }
            parent.setModified(true);
			if(f != null && deleteFile) {
				new DeleteFileHandler().executeHandler(page, null);
			}
        }
    }
    
    public IFile getFile(XModelObject object) {
		XModelObject page = StrutsProcessStructureHelper.instance.getPhysicalPage(object);
		return (page instanceof FileAnyImpl) ? ((FileAnyImpl)page).getFile() : null;
    }

}

class DeletePageUndo extends XRemoveUndo {
    DeletePageUndo(XModelObject parent, XModelObject child) {
        super(parent, child);
    }
    public void doUndo() {
        super.doUndo();
        XModelObject parent = model.getByPath(parentpath);
        if(parent != null && child != null) {
            String p = child.getAttributeValue("path"); //$NON-NLS-1$
            ((StrutsProcessImpl)parent).getHelper().resetPage(child, p, p);
        }
    }

    public void doRedo() {
        super.doRedo();
        XModelObject parent = model.getByPath(parentpath);
        if(parent != null && child != null) {
            String p = child.getAttributeValue("path"); //$NON-NLS-1$
            ((StrutsProcessImpl)parent).getHelper().getPage(p);
        }
    }

}

