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
package org.jboss.tools.jsf.model.helpers.pages;

import java.util.*;

import org.jboss.tools.common.model.*;
import org.jboss.tools.jsf.model.pv.JSFProjectConfiguration;
import org.jboss.tools.jsf.model.pv.JSFProjectTreeConstants;
import org.jboss.tools.jsf.model.pv.JSFProjectsTree;
import org.jboss.tools.jst.web.project.list.IWebPromptingProvider;

public class ResourceBundleHelper {
	
	/**
	 * Creates two maps and puts them to a list.
	 * Maps are based on data in resource-bundle elements 
	 * in faces config files of version 1.2.
	 * First map is 'var' attribute values (map keys) 
	 * to 'base-name' attribute values (map values).
	 * Second map is the reverse of the first map.
	 * Returns either list with 2 maps or empty list. 
	 *
	 * @param model
	 * @param list
	 * @return
	 */	
	public static List<Object> getRegisteredResourceBundles(XModel model) {
		XModelObject root = JSFProjectsTree.getProjectsRoot(model);
		if(root == null) return IWebPromptingProvider.EMPTY_LIST;
		JSFProjectConfiguration cg = (JSFProjectConfiguration)root.getChildByPath(JSFProjectTreeConstants.CONFIGURATION);
		if(cg == null) return IWebPromptingProvider.EMPTY_LIST;
		Map<String,String> map1 = new TreeMap<String,String>();
		Map<String,String> map2 = new TreeMap<String,String>();
		List<Object> list = new ArrayList<Object>();
		list.add(map1);
		list.add(map2);
		XModelObject[] cgs = cg.getTreeChildren();
		for (int i = 0; i < cgs.length; i++) {
			if(!cgs[i].getModelEntity().getName().startsWith("FacesConfig")) continue;
			XModelObject a = cgs[i].getChildByPath("application");
			if(a == null) continue; //Tree has not only faces configs
			XModelObject[] bs = a.getChildren("JSFResourceBundle");
			for (int j = 0; j < bs.length; j++) {
				String baseName = bs[j].getAttributeValue("base-name");
				String _var = bs[j].getAttributeValue("var");
				if(baseName != null && _var != null && baseName.length() > 0 && _var.length() > 0) {
					map1.put(_var, baseName);
					map2.put(baseName, _var);
				}
			}
		}
		return list;
	}

}
