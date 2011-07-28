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

import org.jboss.tools.common.meta.XChild;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.event.XModelTreeEvent;
import org.jboss.tools.common.model.filesystems.FileSystemsHelper;
import org.jboss.tools.common.model.filesystems.impl.FileSystemsImpl;
import org.jboss.tools.common.model.filesystems.impl.FileSystemsLoader;
import org.jboss.tools.common.model.impl.*;

public class JSFProjectResourceBundles extends JSFProjectFolder {
	private static final long serialVersionUID = 6050376048898424153L;
	Iterator<XModelObject> roots = null;
	
	public void invalidate() {
		if(!valid || isLoading) return;
		valid = false;
		fireStructureChanged(XModelTreeEvent.STRUCTURE_CHANGED, this);
	}
	
	public void set(String name, String value) {
		if("invalidate".equals(name)) {
			if("true".equals(value)) invalidate();
		} else {
			super.set(name, value);
		}
	}

	public XModelObject[] getTreeChildren() {
		if(isLoading || valid) return treeChildren;
		isLoading = true;
		valid = true;
		try {
			roots = getRoots();
			List<XModelObject> list = collect(roots);
			treeChildren = list.toArray(new XModelObject[0]);
		} finally {
			isLoading = false;
		}
		EntityComparator c = new EntityComparator(new XChild[0]);
		Arrays.sort(treeChildren, c);
		return treeChildren;
	}
	
	protected Iterator<XModelObject> getRoots() {
		List<XModelObject> list = new ArrayList<XModelObject>();
		FileSystemsImpl fs = (FileSystemsImpl)FileSystemsHelper.getFileSystems(getModel());
		new FileSystemsLoader().updateSrcs(fs);
		XModelObject[] cs = FileSystemsHelper.getFileSystems(getModel()).getChildren("FileSystemFolder");
		for (int i = 0; i < cs.length; i++) {
			String n = cs[i].getAttributeValue("name");
			if(n.startsWith("src") || n.startsWith("lib-")) list.add(cs[i]);
		}
		XModelObject web = getModel().getByPath("Web");
		XModelObject[] ms = (web == null) ? new XModelObject[0] : web.getChildren("WebJSFModule");
		if(ms.length > 0) {
			String s = ms[0].getAttributeValue("src file system");
			if(s == null || s.length() == 0 || "src".equals(s)) return list.iterator();
			StringTokenizer st = new StringTokenizer(s, ",");
			while(st.hasMoreTokens()) {
				String t = st.nextToken().trim();
				if(t.length() == 0 || "src".equals(t)) continue;
				XModelObject r = FileSystemsHelper.getFileSystem(getModel(), t);
				if(r != null) list.add(r);
			}
		}
		return list.iterator();
	}

	protected List<XModelObject> collect(Iterator<XModelObject> rs) {
		List<XModelObject> list = new ArrayList<XModelObject>();
		while(rs.hasNext()) {
			collect(list, rs.next());
		}
		return list;
	}
	
	protected void collect(List<XModelObject> l, XModelObject o) {
		if(o.getFileType() == XModelObject.FILE) {
			if(acceptFile(o)) l.add(o);
		} else if(o.getFileType() > XModelObject.FILE) {
			XModelObject[] cs = o.getChildren();
			for (int i = 0; i < cs.length; i++) collect(l, cs[i]);
		}
	}
	
	protected boolean acceptFile(XModelObject o) {
		return "FilePROPERTIES".equals(o.getModelEntity().getName());
	}

	public Object getAdapter(Class adapter) {
		if(adapter == IResource.class) {
			if(roots == null) roots = getRoots();
			Iterator rs = roots;
			while(rs.hasNext()) {
				XModelObject r = (XModelObject)rs.next();
				Object a = r.getAdapter(adapter);
				if(a != null) return a;
			}
			return null;
		}
		return super.getAdapter(adapter);
	}

	public XModelObject getTreeParent(XModelObject object) {
		if(!acceptFile(object)) return null;
		return (isChild(object)) ? this : null;
	}

}
