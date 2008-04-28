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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IResource;

import org.jboss.tools.common.meta.XChild;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.event.XModelTreeEvent;
import org.jboss.tools.common.model.impl.EntityComparator;
import org.jboss.tools.common.model.impl.XModelImpl;
import org.jboss.tools.jst.web.model.helpers.WebAppHelper;

public class JSFProjectTiles extends JSFProjectFolder {
	private static final long serialVersionUID = 2682624545623794049L;
	
	public static String TILES_SERVLET_CLASS = "org.apache.struts.tiles.TilesServlet";
	public static String TILES_SERVLET_DEFAULT_NAME = "Tiles Servlet";
	public static String TILES_DEFINITIONS = "definitions-config";
	
	public static String TILES_DEFINITIONS_2 = "tiles-definitions";

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
			XModelObject webxml = WebAppHelper.getWebApp(getModel());
			XModelObject servlet = WebAppHelper.findServlet(webxml, TILES_SERVLET_CLASS, TILES_SERVLET_DEFAULT_NAME);
			String[] paths = WebAppHelper.getWebAppInitParamValueList(servlet, TILES_DEFINITIONS);
			if((paths == null || paths.length == 0) && webxml != null) {
				paths = WebAppHelper.getWebAppContextParamValueList(webxml, TILES_DEFINITIONS_2);
			}
			List<XModelObject> list = new ArrayList<XModelObject>();
			for (int i = 0; i < paths.length; i++) {
				XModelObject o = XModelImpl.getByRelativePath(getModel(), paths[i]);
				if(o != null) list.add(o);
			}
			treeChildren = list.toArray(new XModelObject[0]);
		} finally {
			isLoading = false;
		}
		EntityComparator c = new EntityComparator(new XChild[0]);
		Arrays.sort(treeChildren, c);
		return treeChildren;
	}

	public XModelObject getTreeParent(XModelObject object) {
		if(!acceptFile(object)) return null;
		return (isChild(object)) ? this : null;
	}

	protected boolean acceptFile(XModelObject o) {
		return o.getModelEntity().getName().startsWith("FileTiles");
	}
	
	public Object getAdapter(Class adapter) {
		if(adapter == IResource.class) {
			XModelObject o = getModel().getByPath("FileSystems/WEB-INF");
			return (o != null) ? o.getAdapter(adapter) : null;
		}
		return super.getAdapter(adapter);
	}
}
