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
package org.jboss.tools.struts.ui.navigator;

import org.jboss.tools.common.model.ui.navigator.TreeViewerModelListenerImpl;
import org.jboss.tools.common.model.*;
import org.jboss.tools.common.model.event.XModelTreeEvent;
import org.jboss.tools.common.model.impl.XModelImpl;
import org.jboss.tools.struts.StrutsConstants;
import org.jboss.tools.struts.model.pv.*;
import org.jboss.tools.jst.web.model.pv.WebProjectNode;

public class StrutsProjectsTreeListener extends TreeViewerModelListenerImpl {

	public void nodeChanged(XModelTreeEvent event) {
		XModelObject source = event.getModelObject();
		StrutsProjectsRoot root = (StrutsProjectsRoot)getProjectRoot(source.getModel());
		if(root == null) return;
		if(!root.isLoaded()) {
			if(source.getFileType() != XModelObject.NONE) {
				super.updateNode(root);
			}			
			return;
		}
		String entity = source.getModelEntity().getName();
		if(entity.startsWith("FileWebApp") || entity.startsWith("JstWeb")) {
			WebProjectNode n = getProjectRoot(source.getModel());
			if(n != null) n.invalidate();
		} else if(entity.equals("StrutsConfig11") || entity.equals("StrutsConfig12")) {
			invalidateValidators(source.getModel());
			super.nodeChanged(event);
		} else if(entity.equals("StrutsPluginSetProperty11")) {
			invalidateValidators(source.getModel());
			invalidateTiles(source.getModel());
			super.nodeChanged(event);
		} else if(entity.equals("FileTiles")) {
			invalidateTiles(source.getModel());
			super.nodeChanged(event);
		} else {
			super.nodeChanged(event);
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
		return (WebProjectNode)model.getByPath("root:StrutsProjects");
	}

	public void structureChanged(XModelTreeEvent event) {
		if (viewer.getControl() == null || viewer.getControl().isDisposed()) return;
		XModelObject source = event.getModelObject();
		StrutsProjectsRoot root = (StrutsProjectsRoot)getProjectRoot(source.getModel());
		if(root == null || !root.isLoaded()) return;
		if(event.kind() == XModelTreeEvent.CHILD_ADDED) {
			XModelObject c = (XModelObject)event.getInfo();
			String entity = c.getModelEntity().getName();
			if("FilePROPERTIES".equals(entity)) {
				invalidateBundles(source.getModel());
			} else if(StrutsProjectTagLibs.isTLDFile(c)) {
				invalidateTagLibs(source.getModel());
			} else if(entity.startsWith(StrutsConstants.ENT_STRUTSCONFIG)) {
				invalidateConfig(source.getModel());
			} else if("FileTiles".equals(entity)) {
				invalidateTiles(source.getModel());
			} else if(entity.startsWith("FileValidationRules")) {
				invalidateValidators(source.getModel());
			} else if(entity.startsWith("StrutsPlugin")) {
				invalidateValidators(source.getModel());
				invalidateTiles(source.getModel());
			} else if("FileFolder".equals(entity)) {
				WebProjectNode n = getProjectRoot(source.getModel());
				if(n != null) n.invalidate();
			} else {
				String s_entity = source.getModelEntity().getName();
				if(s_entity.startsWith("StrutsPlugin")) {
					invalidateTiles(source.getModel());
				} else if(s_entity.startsWith("JstWeb")) {
					invalidateConfig(source.getModel());
				}
			}
		} else if(event.kind() == XModelTreeEvent.CHILD_REMOVED) {
			if(source.getFileType() > XModelObject.FILE) {
				WebProjectNode n = getProjectRoot(source.getModel());
				if(n != null) n.invalidate();
				return;
			} else if(source.getFileType() == XModelObject.FILE) {
				String entity = source.getModelEntity().getName();
				if(entity.startsWith("FileWebApp")) {
					invalidateConfig(source.getModel());
				}
			} else {
				String entity = source.getModelEntity().getName();
				if(entity.startsWith("StrutsPlugin")) {
					invalidateTiles(source.getModel());
				} else if(entity.startsWith("JstWeb")) {
					invalidateConfig(source.getModel());
				}
			}
		}
		super.structureChanged(event);
	}
	
	private void invalidateBundles(XModel model) {
		invalidateFolder(model, "Resource Bundles");
	}

	private void invalidateTagLibs(XModel model) {
		invalidateFolder(model, "Tag Libraries");
	}

	private void invalidateConfig(XModel model) {
		invalidateFolder(model, "Configuration");
	}

	private void invalidateValidators(XModel model) {
		invalidateFolder(model, "Validation");
	}

	private void invalidateTiles(XModel model) {
		invalidateFolder(model, "Tiles");
	}
	
	private void invalidateFolder(XModel model, String name) {
		WebProjectNode n = getProjectRoot(model);
		if(n == null) return;
		WebProjectNode b = (WebProjectNode)n.getChildByPath(name);
		b.invalidate();
	}

}
