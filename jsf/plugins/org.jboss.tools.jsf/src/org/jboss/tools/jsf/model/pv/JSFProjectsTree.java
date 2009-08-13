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
package org.jboss.tools.jsf.model.pv;

import java.util.*;
import org.jboss.tools.common.model.XModel;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.event.XModelTreeEvent;
import org.jboss.tools.common.model.event.XModelTreeListener;
import org.jboss.tools.common.model.impl.XModelImpl;
import org.jboss.tools.common.model.impl.trees.FileSystemsTree;
import org.jboss.tools.common.model.util.XModelObjectLoaderUtil;
import org.jboss.tools.jst.web.model.pv.WebProjectNode;

public class JSFProjectsTree extends FileSystemsTree implements XModelTreeListener {
	JSFProjectsRoot root = null;
	XModelObject webinf = null;

	protected boolean isConstraintRelevant(String key) {
		return super.isConstraintRelevant(key) || key.startsWith("JSFProjects$");
	}

	public void setModel(XModel model) {
		super.setModel(model);
		root = (JSFProjectsRoot)model.getByPath("root:JSFProjects");
		if(root == null) {
//			root = (JSFProjectsRoot)XModelObjectLoaderUtil.createValidObject(model, "JSFProjectsRoot");
			root = (JSFProjectsRoot)model.createModelObject("JSFProjectsRoot", null);
			((XModelImpl)model).setExtraRoot(root);
			model.addModelTreeListener(this);
		} else {
			root.invalidate();
		}		
	}
	
	public static JSFProjectsRoot getProjectsRoot(XModel model) {
		JSFProjectsRoot root = (JSFProjectsRoot)model.getByPath("root:JSFProjects");
		if(root == null) {
			root = (JSFProjectsRoot)XModelObjectLoaderUtil.createValidObject(model, "JSFProjectsRoot");
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
			webinf = object.getModel().getByPath("FileSystems/WEB-INF");
			if(webinf == null) return cs;
			List<XModelObject> l = new ArrayList<XModelObject>();
			for (int i = 0; i < cs.length; i++) l.add(cs[i]);
			if(webinf != null) l.add(webinf);
			cs = l.toArray(new XModelObject[0]);
			return cs;
		} else {
			return super.getChildren(object);
		}		
	}

	public XModelObject getParent(XModelObject object) {
		return (object == root) ? null
		     : (object == webinf) ? root.webroot 
		     : root.getTreeParent(object);
	}

	public boolean hasChildren(XModelObject object) {
		return (object instanceof WebProjectNode) || super.hasChildren(object);
	}

    public XModelObject getRepresentation(XModelObject object) {
    	if(object != null && "FileJAVA".equals(object.getModelEntity().getName())) {
    		String p = XModelObjectLoaderUtil.getResourcePath(object);
    		if(p == null || !p.endsWith(".java")) return object;
    		p = p.substring(1, p.length() - 5).replace('/', '.');
    		JSFProjectBeans beans = (JSFProjectBeans)root.getChildByPath(JSFProjectTreeConstants.BEANS);
    		XModelObject[] bs = beans == null ? new XModelObject[0] : beans.getTreeChildren();
    		for (int i = 0; i < bs.length; i++) {
    			if(p.equals(bs[i].getAttributeValue("class name"))) {
    				return bs[i];
    			}
    		}
    	}
    	return object;
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
