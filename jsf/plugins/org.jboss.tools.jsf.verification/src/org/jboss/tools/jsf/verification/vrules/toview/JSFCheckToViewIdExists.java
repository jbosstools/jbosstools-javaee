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
package org.jboss.tools.jsf.verification.vrules.toview;

import org.eclipse.core.resources.IFile;
import org.jboss.tools.common.model.*;
import org.jboss.tools.jsf.model.*;
import org.jboss.tools.jsf.model.pv.JSFProjectsTree;
import org.jboss.tools.jsf.verification.vrules.JSFDefaultCheck;
import org.jboss.tools.jsf.web.JSFWebProject;
import org.jboss.tools.jsf.web.pattern.JSFUrlPattern;
import org.jboss.tools.common.verification.vrules.*;
import org.jboss.tools.jst.web.model.pv.WebProjectNode;

public class JSFCheckToViewIdExists extends JSFDefaultCheck implements JSFConstants {

	public VResult[] check(VObject object) {
		String attr = (String)object.getAttribute(ATT_TO_VIEW_ID);
		if(attr == null || attr.length() == 0 || attr.indexOf('*') >= 0 || !attr.startsWith("/")) return null; //$NON-NLS-1$
		if(attr.indexOf('?') >= 0) {
			attr = attr.substring(0, attr.indexOf('?'));
		}
		XModel model = getXModel(object);
		XModelObject o = model.getByPath(attr);
		if(o == null) {
			JSFUrlPattern pattern = JSFWebProject.getInstance(model).getUrlPattern();
			if(pattern != null && pattern.isJSFUrl(attr)) {
				attr = pattern.getJSFPath(attr);
				o = model.getByPath(attr);
			}
		}
		if(o != null) {
			IFile f = (IFile)o.getAdapter(IFile.class);
			if(f != null) {
				String path = f.getLocation().toOSString().replace('\\', '/');
				if(path.endsWith(attr)) return null;
			}
		} else if(checkTiles(model, attr)) {
			return null;
		}
		return fire(object, ATT_TO_VIEW_ID, ATT_TO_VIEW_ID, attr);
	}
	
	private boolean checkTiles(XModel model, String path) {
		XModelObject root = JSFProjectsTree.getProjectsRoot(model);
		if(root == null) return false;
		XModelObject tiles = root.getChildByPath("Tiles"); //$NON-NLS-1$
		if(tiles == null) return false;
		XModelObject[] ts = ((WebProjectNode)tiles).getTreeChildren();
		if(ts.length == 0) return false;
		int d = path.lastIndexOf('.');
		if(d < 0) return false;
		String tileName = path.substring(0, d + 1) + "tiles"; //$NON-NLS-1$
		tileName = tileName.replace('/', '#');
		for (int i = 0; i < ts.length; i++) {
			if(ts[i].getChildByPath(tileName) != null) return true;
		}
		return false;
	}

}
