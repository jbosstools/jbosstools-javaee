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
import org.eclipse.core.resources.*;
import org.eclipse.core.runtime.CoreException;

import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.XModelObjectConstants;
import org.jboss.tools.common.model.event.XModelTreeEvent;
import org.jboss.tools.common.model.filesystems.FileSystemsHelper;
import org.jboss.tools.common.model.impl.ExtraRootImpl;
import org.jboss.tools.common.model.util.*;
import org.jboss.tools.jsf.JSFModelPlugin;
import org.jboss.tools.jst.web.model.helpers.WebAppHelper;
import org.jboss.tools.jst.web.model.pv.WebProjectNode;

public class JSFProjectsRoot extends ExtraRootImpl implements WebProjectNode {
	private static final long serialVersionUID = 2682624545624629547L;
	XModelObject fs;
	XModelObject webroot = null;
	XModelObject webxml = null;
	XModelObject[] treeChildren = new XModelObject[0];
	
    protected void onSetEntity(String entity) {
    	super.onSetEntity(entity);
    	getFileSystems();
		getModel().addModelTreeListener(new JsfTreeListener());
	}
	
	public boolean isLoaded() {
		return children != null && !children.isEmpty();
	}
	
	public XModelObject getFileSystems() {
		if(fs == null) {
			fs = FileSystemsHelper.getFileSystems(getModel());
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
		if(getFileSystems() == null) return "";		
		return getFileSystems().getPresentationString();
	}

	public boolean isActive() {
		return getFileSystems() != null && getFileSystems().isActive();
	}
	
	public XModelObject[] getTreeChildren() {
		fs = FileSystemsHelper.getFileSystems(getModel());
		if(fs == null) return new XModelObject[0];
		webroot = fs.getChildByPath("WEB-ROOT");
		webxml = WebAppHelper.getWebApp(getModel());
		List<XModelObject> list = new ArrayList<XModelObject>();
		if(webroot != null) list.add(webroot);
		XModelObject[] ss = fs.getChildren("FileSystemFolder");
		for (XModelObject s: ss) {
			if(s.getAttributeValue(XModelObjectConstants.ATTR_NAME).startsWith("WEB-ROOT-")) {
				list.add(s);
			}
		}
		XModelObject[] cs = getChildren();
		for (int i = 0; i < cs.length; i++) list.add(cs[i]);
		if(webxml != null) list.add(webxml);
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
				if(n.isChild(object)) return n.getTreeParent(object);
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
			if(p != null && p.exists() && p.isAccessible()) try {
				IMarker[] ms = p.findMarkers(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE);
				if(ms != null && ms.length > 0) {
					for (int i = 0; i < ms.length; i++) {
						if(ms[i].getAttribute(IMarker.SEVERITY, 0) == IMarker.SEVERITY_ERROR) return 2;
					}
				}
				return 0;
			} catch (CoreException e) {
				JSFModelPlugin.getPluginLog().logError(e);
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
