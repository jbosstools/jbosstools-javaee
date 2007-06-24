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

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;

import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.event.XModelTreeEvent;
import org.jboss.tools.common.model.impl.ExtraRootImpl;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.common.model.util.XModelObjectLoaderUtil;
import org.jboss.tools.jst.web.model.helpers.WebAppHelper;
import org.jboss.tools.jst.web.model.pv.WebProjectNode;
import org.jboss.tools.struts.StrutsModelPlugin;

public class StrutsProjectsRoot extends ExtraRootImpl implements WebProjectNode {
	private static final long serialVersionUID = 542432599464041729L;
	XModelObject fs;
	XModelObject webroot = null;
	XModelObject webxml = null;
	XModelObject[] treeChildren = new XModelObject[0];
	
    protected void onSetEntity(String entity) {
    	super.onSetEntity(entity);
    	getFileSystems();
	}
	
	public boolean isLoaded() {
		return children != null && !children.isEmpty();
	}
	
	public XModelObject getFileSystems() {
		if(fs == null) {
			fs = getModel().getByPath("FileSystems");
			webxml = WebAppHelper.getWebApp(getModel());
			if(fs != null) webroot = fs.getChildByPath("WEB-ROOT");
		}
		return fs;
	}
	
	public String get(String name) {
		if("_hasErrors_".equals(name)) {
			return (fs == null) ? "yes" : fs.get(name);
		}
		return super.get(name);
	}
	
	public XModelObject getWebXML() {
		return webxml;
	}
	
	public boolean hasChildren() {
		return true;
	}
	
	protected void loadChildren() {
		if(!isLoaded()) {
			XModelObjectLoaderUtil.addRequiredChildren(this);
		}
	}
	
	public String getPresentationString() {
		return getFileSystems().getPresentationString();
	}

	public boolean isActive() {
		return getFileSystems() != null && getFileSystems().isActive();
	}
	
	public XModelObject[] getTreeChildren() {
		fs = getModel().getByPath("FileSystems");
		if(fs == null) return new XModelObject[0];
		webroot = fs.getChildByPath("WEB-ROOT");
		webxml = WebAppHelper.getWebApp(getModel());
		XModelObject[] cs = getChildren();
		List<XModelObject> list = new ArrayList<XModelObject>();
		if(webroot != null) list.add(webroot);
		for (int i = 0; i < cs.length; i++) list.add(cs[i]);
		treeChildren = list.toArray(new XModelObject[0]);
		return treeChildren;
	}
	
	public boolean isChild(XModelObject object) {
		for (int i = 0; i < treeChildren.length; i++)
		  if(treeChildren[i] == object) return true;
		return false;
	}
	
	public XModelObject getTreeParent(XModelObject object) {
		if(treeChildren.length == 0) getTreeChildren();
		if(isChild(object)) return this;
		XModelObject[] cs = getChildren();
		for (int i = 0; i < cs.length; i++) {
			if(cs[i] instanceof WebProjectNode) {
				WebProjectNode n = (WebProjectNode)cs[i];
				XModelObject p = n.getTreeParent(object);
				if(p != null) return p;
			}
		}
		return object.getParent();
	}

	public void invalidate() {
		if(!isLoaded()) return;
		XModelObject[] cs = getChildren();
		for (int i = 0; i < cs.length; i++) {
			WebProjectNode n = (WebProjectNode)cs[i];
			n.invalidate();
		}
		fireStructureChanged(XModelTreeEvent.STRUCTURE_CHANGED, this);
	}

	public Object getAdapter(Class adapter) {
		return (fs != null) ? fs.getAdapter(adapter) : super.getAdapter(adapter);
	}

	public int getErrorState() {
		if(org.jboss.tools.common.model.project.WatcherLoader.isLocked(getModel())) return 0;
		if(!isLoaded()) {
			IProject p = EclipseResourceUtil.getProject(fs);
			try {
				IMarker[] ms = p.findMarkers(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE);
				if(ms != null && ms.length > 0) {
					for (int i = 0; i < ms.length; i++) {
						if(ms[i].getAttribute(IMarker.SEVERITY, 0) == IMarker.SEVERITY_ERROR) return 2;
					}
				}
				return 0;
			} catch (Exception e) {
                StrutsModelPlugin.getPluginLog().logError(e);
				return 0;
			}
		}
		getTreeChildren();
		if(webxml == null || webxml.getErrorState() != 0 || webxml.getErrorChildCount() > 0) {
			setErrorState(webxml == null ? 2 : webxml.getErrorState());
		} else {
			setErrorState(0);
		}
		return super.getErrorState();
	}

}
