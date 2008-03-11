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
package org.jboss.tools.struts.model.pv;

import java.util.*;
import org.jboss.tools.common.model.XModel;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.event.XModelTreeEvent;
import org.jboss.tools.common.model.event.XModelTreeListener;
import org.jboss.tools.common.model.filesystems.FileSystemsHelper;
import org.jboss.tools.common.model.impl.XModelImpl;
import org.jboss.tools.common.model.impl.trees.FileSystemsTree;
import org.jboss.tools.common.model.util.XModelObjectLoaderUtil;
import org.jboss.tools.jst.web.model.pv.WebProjectNode;
import org.jboss.tools.jst.web.project.WebModuleConstants;

public class StrutsProjectsTree extends FileSystemsTree implements XModelTreeListener {
	StrutsProjectsRoot root = null;
	XModelObject webinf = null;

	protected boolean isConstraintRelevant(String key) {
		return super.isConstraintRelevant(key) || key.startsWith("StrutsProjects$");
	}

	public void setModel(XModel model) {
		super.setModel(model);
		root = (StrutsProjectsRoot)model.getByPath("root:StrutsProjects");
		if(root == null) {
///			root = (StrutsProjectsRoot)XModelObjectLoaderUtil.createValidObject(model, "StrutsProjectsRoot");
			root = (StrutsProjectsRoot)model.createModelObject("StrutsProjectsRoot", null);
			((XModelImpl)model).setExtraRoot(root);
			model.addModelTreeListener(this);
		} else {
			root.invalidate();
		}		
	}

	public static StrutsProjectsRoot getProjectsRoot(XModel model) {
		StrutsProjectsRoot root = (StrutsProjectsRoot)model.getByPath("root:StrutsProjects");
		if(root == null) {
			root = (StrutsProjectsRoot)XModelObjectLoaderUtil.createValidObject(model, "StrutsProjectsRoot");
			((XModelImpl)model).setExtraRoot(root);
		}
		return root;
	}

	public XModelObject getRoot() {
		return root;
	}
	
	public XModelObject[] getChildren(XModelObject object) {
		if(object instanceof WebProjectNode) {
			return ((WebProjectNode)object).getTreeChildren();
		} else if(object == root.webroot) {
			XModelObject[] cs = super.getChildren(object);
			XModelObject web = object.getModel().getByPath("Web");
			XModelObject[] ws = (web == null) ? new XModelObject[0] : web.getChildren();
			webinf = FileSystemsHelper.getWebInf(object.getModel());
			List<XModelObject> l = new ArrayList<XModelObject>();
			if(webinf != null) l.add(webinf);
			Set<String> roots = new HashSet<String>();
			roots.add(root.webroot.getAttributeValue("name"));
			for (int i = 0; i < ws.length; i++) {
				String module = ws[i].getAttributeValue("name");
				if(module.length() == 0) continue;
				String fsn = ws[i].getAttributeValue(WebModuleConstants.ATTR_ROOT_FS);
				if(roots.contains(fsn)) continue;
				roots.add(fsn);
				XModelObject fs = FileSystemsHelper.getFileSystem(object.getModel(), fsn);
				if(fs != null) l.add(fs);
			}
			for (int i = 0; i < cs.length; i++) l.add(cs[i]);
			cs = l.toArray(new XModelObject[0]);
			return cs;
		} else {
			return super.getChildren(object);
		}		
	}

	public XModelObject getParent(XModelObject object) {
		return (object == root) ? null
			 : (object == webinf) ? root.webroot
			 : ("FileSystemFolder".equals(object.getModelEntity().getName()) && object != root.webroot) ? root.webroot 
			 : root.getTreeParent(object);
	}

	public boolean hasChildren(XModelObject object) {
		return (object instanceof WebProjectNode) || super.hasChildren(object);
	}

	public void nodeChanged(XModelTreeEvent event) {
		
	}

	public void structureChanged(XModelTreeEvent event) {
		XModelObject source = event.getModelObject();
		String entity = source.getModelEntity().getName();
		if(event.kind() == XModelTreeEvent.STRUCTURE_CHANGED) {
			if("FileSystemJar".equals(entity) && root != null) {
				root.invalidate();
			}
		}		
	}

}
