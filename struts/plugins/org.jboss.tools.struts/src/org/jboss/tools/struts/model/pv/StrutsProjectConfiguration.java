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
import org.jboss.tools.common.model.impl.*;
import org.jboss.tools.jst.web.model.helpers.WebAppHelper;
import org.jboss.tools.jst.web.model.pv.WebProjectNode;
import org.jboss.tools.struts.webprj.model.helpers.WebModulesHelper;

public class StrutsProjectConfiguration extends RegularObjectImpl implements WebProjectNode {
	private static final long serialVersionUID = 2859450986105757992L;
	protected XModelObject[] treeChildren = EMPTY_CHILDREN;
	protected boolean isLoading = false;
	protected boolean valid = false;
	XModelObject web = null;
	XModelObject webxml = null;
	long webTimeStamp = -1;
	long webxmlTimeStamp = -1;

	public void invalidate() {
		if(!valid || isLoading) return;
		long ts = (web == null || !web.isActive()) ? -1 : web.getTimeStamp();
		if(webTimeStamp != ts || webxml != WebAppHelper.getWebApp(getModel())) {
			webTimeStamp = ts;
			valid = false;
		}
		XModelObject[] cs = getChildren();
		for (int i = 0; i < cs.length; i++) {
			if(!(cs[i] instanceof StrutsProjectModule)) continue;
			StrutsProjectModule m = (StrutsProjectModule)cs[i];
			m.valid = false;
		}
		fireStructureChanged(XModelTreeEvent.STRUCTURE_CHANGED, this);
	}

	public XModelObject[] getTreeChildren() {
		if(isLoading || valid) return treeChildren;
		isLoading = true;
		valid = true;
		try {
			if(web == null || !web.isActive()) {
				web = getModel().getByPath("Web");
			}
			if(webxml == null || !webxml.isActive()) {
				webxml = WebAppHelper.getWebApp(getModel());
			}
			if(web == null) return treeChildren = EMPTY_CHILDREN;
			webTimeStamp = (web == null) ? -1 : web.getTimeStamp();
			Map<String,XModelObject> initParams = new HashMap<String,XModelObject>();
			initParams.clear();
			XModelObject[] ps = web.getChildren(WebModulesHelper.ENT_STRUTS_WEB_MODULE);
			for (int i = 0; i < ps.length; i++) {
				String n = ps[i].getAttributeValue("name");
				if(n.length() == 0) n = "default";
				initParams.put(n, ps[i]);
			}
			List<XModelObject> list = new ArrayList<XModelObject>();
			XModelObject[] cs = getChildren();
			for (int i = 0; i < cs.length; i++) {
				StrutsProjectModule module = (StrutsProjectModule)cs[i];
				XModelObject webModule = module.getModule();
				if(webModule == null || !webModule.isActive()) {
					module.removeFromParent();  
				} else {
					module.invalidate();
					list.add(module);
				}
			}
			Iterator it = initParams.keySet().iterator();
			while(it.hasNext()) {
				String n = it.next().toString();
				XModelObject webModule = (XModelObject)initParams.get(n);
				StrutsProjectModule module = (StrutsProjectModule)getModel().createModelObject("StrutsProjectModule", null);
				module.setAttributeValue("name", n);
				module.setModule(webModule);
				if(addChild(module)) {
					list.add(module);
				}
			}
			if(webxml != null) list.add(webxml);
			treeChildren = (XModelObject[])list.toArray(new XModelObject[0]);
		} finally {
			isLoading = false;		
		}
		return treeChildren;
	}
	
	public boolean isChild(XModelObject object) {
		if(treeChildren.length == 0) getTreeChildren();
		if(object == webxml) return true;
		for (int i = 0; i < treeChildren.length; i++) {
			if(treeChildren[i] == object) return true;
		}
		return false;
	}

	public Object getAdapter(Class adapter) {
		if(adapter == IResource.class) {
			XModelObject o = getModel().getByPath("FileSystems/WEB-INF");
			return (o != null) ? o.getAdapter(adapter) : null;
		}
		return super.getAdapter(adapter);
	}

	public XModelObject getTreeParent(XModelObject object) {
		if(isChild(object)) return this;
		String entity = object.getModelEntity().getName();
		if(!entity.startsWith("StrutsConfig")) return null;
		XModelObject[] cs = getChildren();
		for (int i = 0; i < cs.length; i++) {
			WebProjectNode n = (WebProjectNode)cs[i];
			XModelObject p = n.getTreeParent(object);
			if(p != null) return p;
		}
		return null;
	}

	public int getErrorState() {
		getTreeChildren();
		if(webxml == null || webxml.getErrorState() != 0 || webxml.getErrorChildCount() > 0) {
			setErrorState(webxml == null ? 2 : webxml.getErrorState());
		} else {
			setErrorState(0);
		}
		return super.getErrorState();
	}
}
