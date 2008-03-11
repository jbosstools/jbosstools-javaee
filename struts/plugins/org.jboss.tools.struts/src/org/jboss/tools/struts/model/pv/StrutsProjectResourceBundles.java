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
import org.eclipse.core.resources.IResource;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.event.XModelTreeEvent;

public class StrutsProjectResourceBundles extends StrutsProjectFolder {
	private static final long serialVersionUID = 3575704465532661555L;
	
	public void invalidate() {
		if(!valid || isLoading) return;
		valid = false;
		fireStructureChanged(XModelTreeEvent.STRUCTURE_CHANGED, this);
	}

	public XModelObject[] getTreeChildren() {
		if(isLoading || valid) return treeChildren;
		isLoading = true;
		valid = true;
		try {
			Iterator<XModelObject> roots = getRoots();
			List<XModelObject> list = collect(roots);
			treeChildren = list.toArray(new XModelObject[0]);
		} finally {
			isLoading = false;
		}
		Arrays.sort(treeChildren, DEFAULT_COMPARATOR);
		return treeChildren;
	}
	
	protected Iterator<XModelObject> getRoots() {
		List<XModelObject> list = new ArrayList<XModelObject>();
		XModelObject r = getModel().getByPath("FileSystems/src");
		if(r != null) list.add(r);
//		XModelObject[] os = getModel().getByPath("FileSystems").getChildren("FileSystemJar");
//		for (int i = 0; i < os.length; i++) {
//			list.add(os[i]);
//		}
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
			XModelObject o = getModel().getByPath("FileSystems/src");
			return (o != null) ? o.getAdapter(adapter) : null;
		}
		return super.getAdapter(adapter);
	}

	public XModelObject getTreeParent(XModelObject object) {
		if(!acceptFile(object)) return null;
		return (isChild(object)) ? this : null;
	}

}
