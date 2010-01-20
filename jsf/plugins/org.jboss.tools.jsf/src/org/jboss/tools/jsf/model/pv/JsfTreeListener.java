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

import org.jboss.tools.common.model.XModel;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.event.XModelTreeEvent;
import org.jboss.tools.common.model.event.XModelTreeListener;
import org.jboss.tools.common.model.impl.XModelImpl;
import org.jboss.tools.jsf.model.JSFConstants;
import org.jboss.tools.jsf.model.pv.JSFProjectTagLibs;
import org.jboss.tools.jsf.model.pv.JSFProjectsRoot;
import org.jboss.tools.jst.web.model.pv.WebProjectNode;

public class JsfTreeListener implements XModelTreeListener {

	public void nodeChanged(XModelTreeEvent event) {
		XModelObject source = event.getModelObject();
		JSFProjectsRoot root = (JSFProjectsRoot)getProjectRoot(source.getModel());
		if(root == null) return;
		if(!root.isLoaded()) {
			return;
		}
		if(source.getModelEntity().getName().startsWith("FileWebApp")) {
			WebProjectNode n = getProjectRoot(source.getModel());
			if(n != null) n.invalidate();
		} else if(JSFProjectTagLibs.isTLDFile(source)) {
			invalidateTagLibs(source.getModel());
		}
		if(!(source instanceof WebProjectNode)) {
			WebProjectNode n = getProjectRoot(source.getModel());
			if(n == null) return;
			XModelObject p = n.getTreeParent(source);
			if(p instanceof WebProjectNode) {
				((XModelImpl)p.getModel()).fireNodeChanged(p, p.getPath());
			}
		}
	}
	
	protected WebProjectNode getProjectRoot(XModel model) {
		return (WebProjectNode)model.getByPath("root:JSFProjects");
	}

	public void structureChanged(XModelTreeEvent event) {
		XModelObject source = event.getModelObject();
		JSFProjectsRoot root = (JSFProjectsRoot)getProjectRoot(source.getModel());
		if(root == null || !root.isLoaded()) return;
		if(event.kind() == XModelTreeEvent.CHILD_ADDED) {
			XModelObject c = (XModelObject)event.getInfo();
			String entity = c.getModelEntity().getName();
			if("FilePROPERTIES".equals(c.getModelEntity().getName())) {
				invalidateBundles(source.getModel());
			} else if(JSFProjectTagLibs.isTLDFile(c) || 
                      "FileSystemJar".equals(entity)) {
				invalidateTagLibs(source.getModel());
				invalidateConfig(source.getModel());
			} else if(entity.startsWith(JSFConstants.ENT_FACESCONFIG)) {
				invalidateConfig(source.getModel());
			} else if("JSFManagedBean".equals(entity) || "JSFManagedBean20".equals(entity)) {
				invalidateFolder(source.getModel(), JSFProjectTreeConstants.BEANS);
			} else if("JSFReferencedBean".equals(entity)) {
				invalidateFolder(source.getModel(), JSFProjectTreeConstants.BEANS);
			} else if("FileTiles".equals(entity)) {
				invalidateFolder(source.getModel(), "Tiles");
			} else if("FileFolder".equals(entity)) {
				WebProjectNode n = getProjectRoot(source.getModel());
				if(n != null) n.invalidate();
			}
		} else if(event.kind() == XModelTreeEvent.CHILD_REMOVED) {
			String entity = source.getModelEntity().getName();
			if(source.getFileType() > XModelObject.FILE) {
				WebProjectNode n = getProjectRoot(source.getModel());
				if(n != null) n.invalidate();
				return;
			} else if("FileSystems".equals(entity)) {
				invalidateTagLibs(source.getModel());
			} else if("JSFManagedBeans".equals(entity) || "JSFManagedBean20".equals(entity)) {
				invalidateFolder(source.getModel(), JSFProjectTreeConstants.BEANS);
			} else if("JSFReferencedBeans".equals(entity)) {
				invalidateFolder(source.getModel(), JSFProjectTreeConstants.BEANS);
			}
		} else if(event.kind() == XModelTreeEvent.STRUCTURE_CHANGED) {
			String entity = event.getModelObject().getModelEntity().getName();
			if("FileSystemJar".equals(entity)) {
				invalidateTagLibs(source.getModel());
			}
		}
	}
	
	private void invalidateBundles(XModel model) {
		invalidateFolder(model, "Resource Bundles");
	}

	private void invalidateTagLibs(XModel model) {
		invalidateFolder(model, "Tag Libraries");
	}

	private void invalidateConfig(XModel model) {
		invalidateFolder(model, JSFProjectTreeConstants.CONFIGURATION);
		invalidateFolder(model, JSFProjectTreeConstants.BEANS);
	}
	
	private void invalidateFolder(XModel model, String name) {
		WebProjectNode n = getProjectRoot(model);
		if(n == null) return;
		WebProjectNode b = (WebProjectNode)n.getChildByPath(name);
		if(b == null) return;
		b.invalidate();
	}
}