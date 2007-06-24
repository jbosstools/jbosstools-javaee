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
import org.eclipse.core.resources.IResource;

import org.jboss.tools.common.model.XModel;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.event.XModelTreeEvent;
import org.jboss.tools.common.model.filesystems.FileSystemsHelper;
import org.jboss.tools.jsf.web.JSFWebHelper;
import org.jboss.tools.jst.web.model.pv.WebProjectNode;

public class JSFProjectConfiguration extends JSFProjectFolder {
	private static final long serialVersionUID = 5602237618569192068L;
	
	public JSFProjectConfiguration() {}

	public void invalidate() {
		if(!valid || isLoading) return;
		valid = false;
		fireStructureChanged(XModelTreeEvent.STRUCTURE_CHANGED, this);
		XModelObject[] os = getChildren();
		for (int i = 0; i < os.length; i++) {
			if(os[i] instanceof JSFProjectFolder) {
				((JSFProjectFolder)os[i]).invalidate();
			}
		}
	}

	public XModelObject[] getTreeChildren() {
		if(isLoading || valid) return treeChildren;
		isLoading = true;
		valid = true;
		try {
			List<XModelObject> list = getConfiguration(getModel());
			XModelObject[] os = getChildren();
			for (int i = 0; i < os.length; i++) {
				if(acceptChild(os[i])) {
					list.add(os[i]);
				}
			}
			treeChildren = list.toArray(new XModelObject[0]);
		} finally {
			isLoading = false;		
		}
		return treeChildren;
	}
	
	private boolean acceptChild(XModelObject object) {
		String entity = object.getModelEntity().getName();
		if(entity.equals("JSFProjectShaleConfiguration")) {
			XModelObject jar = object.getModel().getByPath("FileSystems/lib-shale-clay.jar");
			return jar != null;
		}
		return true;
	}
	
	static List<XModelObject> getConfiguration(XModel model) {
		List<XModelObject> list = new ArrayList<XModelObject>();
		JSFWebHelper.getConfigFiles(list, new HashSet<XModelObject>(), model, JSFWebHelper.FACES_CONFIG_DATA);
		return list;
	}
	
	public Object getAdapter(Class adapter) {
		if(adapter == IResource.class) {
			XModelObject o = FileSystemsHelper.getWebInf(getModel());
			return (o != null) ? o.getAdapter(adapter) : null;
		}
		return super.getAdapter(adapter);
	}

	public XModelObject getTreeParent(XModelObject object) {
		XModelObject[] cs = getChildren();
		for (int i = 0; i < cs.length; i++) {
			WebProjectNode n = (WebProjectNode)cs[i];
			XModelObject p = n.getTreeParent(object);
			if(p != null) return p;
		}
		if(isChild(object)) return this;
		return null;
	}

}
