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

import java.util.Properties;
import org.jboss.tools.common.meta.action.impl.*;
import org.jboss.tools.common.meta.action.impl.handlers.*;
import org.jboss.tools.common.model.*;
import org.jboss.tools.common.model.filesystems.impl.DeleteFileHandler;
import org.jboss.tools.jst.web.model.ReferenceObject;
import org.jboss.tools.seam.pages.xml.model.SeamPagesConstants;
import org.jboss.tools.seam.pages.xml.model.helpers.SeamPagesDiagramHelper;

public class DeletePageItemHandler extends AbstractHandler implements SeamPagesConstants {

	public boolean isEnabled(XModelObject object) {
		if(object == null || !object.isObjectEditable()) return false;
		if(!ENT_DIAGRAM_ITEM.equals(object.getModelEntity().getName())) return false;
		ReferenceObject g = (ReferenceObject)object;
		return g.getReference() != null || "true".equals(g.getAttributeValue("persistent"));
	}

	public boolean getSignificantFlag(XModelObject object) {
		return getPage(object) == null;
	}
	
	private XModelObject getPage(XModelObject object) {
		if(object == null) return null;
		String path = object.getAttributeValue(ATTR_PATH);
		return (path == null || path.length() == 0) ? null : object.getModel().getByPath(path);
	}

	public void executeHandler(XModelObject object, Properties p) throws XModelException {
		if(!isEnabled(object)) return;
		ReferenceObject g = (ReferenceObject)object;
		XModelObject diagram = object.getParent();

		XModelObject page = getPage(object);
		boolean deletePage = (page != null);
		Boolean pb = p == null ? null : (Boolean)p.get("deletePage");
		if(pb != null) {
			deletePage = deletePage && pb.booleanValue();
		} else if(deletePage) {
			String message = "Delete " + DefaultCreateHandler.title(object, false) + "?";
			int q = confirmPageDelete(object.getModel(), message);
			if(q < 0) return;
			deletePage = (q == 0);
			if(p != null) p.put("deletePage", Boolean.valueOf(q == 0));
		}
		g.getModel().changeObjectAttribute(g, "persistent", "false");
		XModelObject c = g.getReference();
		if(c != null) {
			DefaultRemoveHandler.removeFromParent(c);
		}
		SeamPagesDiagramHelper.getHelper(diagram).updateDiagram();
		if(deletePage) {
			new DeleteFileHandler().executeHandler(page, null);
		} 
	}
	
	private int confirmPageDelete(XModel model, String message) {
		ServiceDialog d = model.getService();
		Properties dp = new Properties();
		dp.setProperty(ServiceDialog.DIALOG_MESSAGE, message);
		dp.put(ServiceDialog.CHECKED, Boolean.FALSE);
		dp.setProperty(ServiceDialog.CHECKBOX_MESSAGE, "Delete file from disk");
		if(!d.openConfirm(dp)) return -1;
		Boolean b = (Boolean)dp.get(ServiceDialog.CHECKED);
		return (b != null && b.booleanValue()) ? 0 : 1;
	}

}
