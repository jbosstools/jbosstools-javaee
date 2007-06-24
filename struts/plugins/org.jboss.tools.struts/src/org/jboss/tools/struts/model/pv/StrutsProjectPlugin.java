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
import org.jboss.tools.common.model.filesystems.FileSystemsHelper;
import org.jboss.tools.common.model.impl.XModelImpl;
import org.jboss.tools.struts.webprj.model.helpers.WebModulesHelper;

public abstract class StrutsProjectPlugin extends StrutsProjectFolder {

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
			Map initParams = new HashMap();
			initParams.clear();
			WebModulesHelper h = WebModulesHelper.getInstance(getModel());
			XModelObject[] cs = h.getAllConfigs();
			Set<String> uris = new TreeSet<String>();
			collect(cs, uris);
			List<XModelObject> list = new ArrayList<XModelObject>();
			String[] us = uris.toArray(new String[0]);
			for (int i = 0; i < us.length; i++) {
				XModelObject o = XModelImpl.getByRelativePath(getModel(), us[i]);
				if(o != null) list.add(o);
			}
			treeChildren = list.toArray(new XModelObject[0]);
		} finally {
			isLoading = false;		
		}
		return treeChildren;
	}
	
	protected void collect(XModelObject[] cgs, Set<String> uris) {
		for (int i = 0; i < cgs.length; i++) collect(cgs[i], uris);
	}

	protected void collect(XModelObject c, Set<String> uris) {
		String pathnames = getPathnames(c);
		if(pathnames == null || pathnames.length() == 0) return;
		StringTokenizer st = new StringTokenizer(pathnames, ",");
		while(st.hasMoreTokens()) uris.add(st.nextToken().trim());
	}
	
	protected abstract String getPathnames(XModelObject c);
	
	public Object getAdapter(Class adapter) {
		if(adapter == IResource.class) {
			XModelObject o = FileSystemsHelper.getWebInf(getModel());
			return (o != null) ? o.getAdapter(adapter) : null;
		}
		return super.getAdapter(adapter);
	}
	
	private XModelObject findPlugin(XModelObject c, String className) {
		XModelObject plugins = c.getChildByPath("plug-ins");
		if(plugins == null) return null;
		XModelObject[] cs = plugins.getChildren();
		for (int i = 0; i < cs.length; i++) {
			if(className.equals(cs[i].getAttributeValue("className"))) return cs[i];
		}
		return null;
	}

	protected String getPathnames(XModelObject c, String pluginClassName, String propertyName) {
		XModelObject plugin = findPlugin(c, pluginClassName);
		if(plugin == null) return null;
		XModelObject[] cs = plugin.getChildren();
		for (int i = 0; i < cs.length; i++) {
			if(propertyName.equals(cs[i].getAttributeValue("property"))) return cs[i].getAttributeValue("value");
		}
		return null;
	}	

}
