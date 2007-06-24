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
import org.jboss.tools.common.model.filesystems.FileSystemsHelper;

public class StrutsProjectTagLibs extends StrutsProjectResourceBundles {
	private static final long serialVersionUID = 562611058980338342L;

	protected Iterator<XModelObject> getRoots() {
		List<XModelObject> list = new ArrayList<XModelObject>();
		XModelObject r = FileSystemsHelper.getWebInf(getModel());
		if(r != null) list.add(r);
		r = getModel().getByPath("FileSystems/lib-struts.jar/META-INF");
		if(r != null) list.add(r);
		return list.iterator();
	}

	protected List<XModelObject> collect(Iterator<XModelObject> rs) {
		List<XModelObject> list = super.collect(rs);
		Iterator it = list.iterator();
		Set<String> set = new HashSet<String>();
		while(it.hasNext()) {
			XModelObject o = (XModelObject)it.next();
			String uri = o.getAttributeValue("uri");
			if(set.contains(uri)) {
				it.remove();
			} else {
				set.add(uri);
			}
		}
		return list;
	}

	static String TLD_ENTITIES = ".FileTLD_PRO.FileTLD_1_2.FileTLD_2_0.FileTLD_2_1.";

	protected boolean acceptFile(XModelObject o) {
		return isTLDFile(o);
	}
	
	public static boolean isTLDFile(XModelObject o) {
		String entity = "." + o.getModelEntity().getName();
		return TLD_ENTITIES.indexOf(entity) >= 0;
	}

	public Object getAdapter(Class adapter) {
		if(adapter == IResource.class) {
			XModelObject o = FileSystemsHelper.getWebInf(getModel());
			return (o != null) ? o.getAdapter(adapter) : null;
		}
		return super.getAdapter(adapter);
	}

}
