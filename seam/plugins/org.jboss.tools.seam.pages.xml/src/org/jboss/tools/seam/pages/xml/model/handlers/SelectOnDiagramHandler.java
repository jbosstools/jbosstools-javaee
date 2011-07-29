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
import org.jboss.tools.common.model.*;
import org.jboss.tools.common.model.filesystems.FileSystemsHelper;
import org.jboss.tools.common.model.util.FindObjectHelper;
import org.jboss.tools.common.meta.action.impl.*;
import org.jboss.tools.jst.web.model.ReferenceObject;
import org.jboss.tools.seam.pages.xml.model.SeamPagesConstants;
import org.jboss.tools.seam.pages.xml.model.helpers.SeamPagesDiagramStructureHelper;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class SelectOnDiagramHandler extends AbstractHandler implements SeamPagesConstants {

	public SelectOnDiagramHandler() {}

	public boolean isEnabled(XModelObject object) {
		if(object == null || !object.isActive()) return false;
		XModelObject f = FileSystemsHelper.getFile(object);
		return (f != null && f.getModelEntity().getName().startsWith(SeamPagesConstants.ENT_FILE_SEAM_PAGES));
	}

	public void executeHandler(XModelObject object, Properties p) throws XModelException {
		if(object == null) return;
		XModelObject item = getItemOnDiagram(object);
		if(item == null) return;
		FindObjectHelper.findModelObject(item, FindObjectHelper.IN_EDITOR_ONLY, "Graphical");
		FindObjectHelper.findModelObject(object, FindObjectHelper.IN_EDITOR_ONLY);
	}

	/**
	 *
	 * @param object
	 * @return the best match in diagram XML for argument file XML object. 
	 */
	public static XModelObject getItemOnDiagram(XModelObject object) {
		if(object == null) return null;
		String entity = object.getModelEntity().getName();
		if(entity.startsWith(SeamPagesConstants.ENT_NAVIGATION_RULE)
				|| entity.startsWith(SeamPagesConstants.ENT_RULE)) {
			object = object.getChildByPath("target");
		} else if(entity.startsWith(SeamPagesConstants.ENT_NAVIGATION)) {
			XModelObject[] cs = object.getChildren();
			if(cs.length != 1) {
				object = object.getParent();
			} else {
				object = cs[0].getChildByPath("target");
			}
			if(object == null) return null;
		} else if(entity.startsWith(SeamPagesConstants.ENT_PARAM)) {
			//or should we return object itself, if params are displayed by diagram?
			return getItemOnDiagram(object.getParent());
		}
		XModelObject diagram = SeamPagesDiagramStructureHelper.instance.getDiagram(object);
		return (diagram == null) ? null : getItemOnDiagram(diagram, object);
	}

	private static XModelObject getItemOnDiagram(XModelObject diagramObject, XModelObject object) {
		if(diagramObject instanceof ReferenceObject) {
			if(((ReferenceObject)diagramObject).getReference() == object) return diagramObject;
		}
		return getItemOnDiagram(diagramObject.getChildren(), object);
	}

	private static XModelObject getItemOnDiagram(XModelObject[] diagramObjects, XModelObject object) {
		for (int i = 0; i < diagramObjects.length; i++) {
			XModelObject cr = getItemOnDiagram(diagramObjects[i], object);
			if(cr != null) return cr;
		}
		return null;
	}

}
