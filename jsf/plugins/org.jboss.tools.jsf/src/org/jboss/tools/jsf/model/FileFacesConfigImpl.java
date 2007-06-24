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
package org.jboss.tools.jsf.model;

import java.util.*;
import org.jboss.tools.common.model.*;
import org.jboss.tools.common.model.impl.*;
import org.jboss.tools.common.model.loaders.XObjectLoader;
import org.jboss.tools.common.model.util.XModelObjectLoaderUtil;
import org.jboss.tools.jsf.model.impl.NavigationRuleObjectImpl;
import org.jboss.tools.jst.web.model.AbstractWebFileImpl;
import org.jboss.tools.jst.web.model.WebProcessLoader;

public class FileFacesConfigImpl extends AbstractWebFileImpl implements JSFNavigationModel, JSFConstants {
	private static final long serialVersionUID = 1146851303785562308L;

	protected RegularChildren createChildren() {
		return new OrderedByEntityChildren();
	}

	protected String getProcessEntity() {
		return "JSFProcess";
	}

	protected boolean hasDTD() {
		return !"FacesConfig12".equals(getModelEntity().getName());
	}

	public void updateRuleIndices() {
		Map<String,Integer> paths = new HashMap<String,Integer>();
		XModelObject[] rs = getChildByPath(FOLDER_NAVIGATION_RULES).getChildren(ENT_NAVIGATION_RULE);
		Map<String,XModelObject> pps = new HashMap<String,XModelObject>();
		int[] is = new int[rs.length];
		for (int i = 0; i < rs.length; i++) {
			String path = rs[i].getAttributeValue(ATT_FROM_VIEW_ID);
			Integer v = paths.get(path);
			is[i] = (v == null) ? 0 : v.intValue() + 1;
			paths.put(path, new Integer(is[i]));
			pps.put(rs[i].getPathPart(), rs[i]);
		}
		boolean success = true;
		int att = 0;
		do {
			success = true;
			for (int i = 0; i < rs.length; i++) {
				String d = NavigationRuleObjectImpl.toNavigationRulePathPart(rs[i].getAttributeValue("from-view-id")) + ":" + is[i];
				XModelObject c = pps.remove(d);
				String v = "" + is[i];
				if(c != null) {
					if(c != rs[i]) {
						success = false;
						v += "." + is[i];
					} else {
						continue;
					}
				}
				rs[i].setAttributeValue("index", v);
			}
		} while(!success && ++att < 5);
	}
	
	public int getRuleCount(String fromViewId) {
		XModelObject[] rs = getChildByPath(FOLDER_NAVIGATION_RULES).getChildren("JSFNavigationRule");
		int s = 0;
		for (int i = 0; i < rs.length; i++) {
			String f = rs[i].getAttributeValue(ATT_FROM_VIEW_ID);
			if(fromViewId.equals(f)) ++s;
		}
		return s;
	}
	
	public boolean move(int from, int to, boolean firechange) {
		XModelObject[] os = children.getObjects();
		boolean updateRules = false;
		if(from >= 0 && from < os.length && to >= 0 && to < os.length && from != to) {
			updateRules = ENT_NAVIGATION_RULE.equals(os[from].getModelEntity().getName());
		}
		boolean b = super.move(from, to, firechange);
		if(b && updateRules) updateRuleIndices();
		return b;
	}

	public XModelObject addRule(String fromViewId) {
		XModelObject rule = getModel().createModelObject(ENT_NAVIGATION_RULE, null);
		rule.setAttributeValue(ATT_FROM_VIEW_ID, fromViewId);
		int i = getRuleCount(fromViewId);
		rule.setAttributeValue("index", "" + i);
		getChildByPath(FOLDER_NAVIGATION_RULES).addChild(rule);
		return rule;		
	}

	protected void mergeAll(XModelObject f, boolean update) {
		FacesProcessImpl process = (FacesProcessImpl)provideWebProcess();
		boolean b = (process != null && process.isPrepared);
		if(b) process.getHelper().addUpdateLock(this);
		merge(f, !update);
		if(b) {
			process.getHelper().removeUpdateLock(this);
			process.getHelper().updateProcess();
		}

		if(process != null) {
			if(!process.isPrepared()/* || update*/ || isForceLoadOn()) {
				XObjectLoader loader = XModelObjectLoaderUtil.getObjectLoader(this);
				((WebProcessLoader)loader).reloadProcess(this);
			}
			if(process.isPrepared())
				process.autolayout();
		}
	}

}
