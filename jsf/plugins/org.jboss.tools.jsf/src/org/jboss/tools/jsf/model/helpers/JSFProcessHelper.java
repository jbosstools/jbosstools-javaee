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
package org.jboss.tools.jsf.model.helpers;

import java.util.*;
import org.jboss.tools.common.model.*;
import org.jboss.tools.common.model.util.XModelObjectUtil;
import org.jboss.tools.jsf.JSFPreference;
import org.jboss.tools.jsf.model.*;
import org.jboss.tools.jsf.model.helpers.autolayout.JSFItems;
import org.jboss.tools.jsf.model.impl.NavigationRuleObjectImpl;
import org.jboss.tools.jst.web.model.ReferenceObject;
import org.jboss.tools.jst.web.model.helpers.autolayout.AutoLayout;

public class JSFProcessHelper implements JSFConstants {
	private XModelObject process;
	private static XModelObject TEMPLATE;
	private XModelObject config;
	private Map<String,XModelObject> groups = new HashMap<String,XModelObject>();
	private Map<String,XModelObject> targets = new HashMap<String,XModelObject>();
	private XModelObject[] rules = new XModelObject[0];

	public JSFProcessHelper(XModelObject process) {
		this.process = process;
	}
	
	private XModelObject getTemplate() {
		if(TEMPLATE == null && process != null) {
			TEMPLATE = process.getModel().createModelObject(ENT_PROCESS_GROUP, null);
		}
		return TEMPLATE;
	}
	
	public static JSFProcessHelper getHelper(XModelObject process) {
		return ((FacesProcessImpl)process).getHelper();
	}

	private synchronized void reset() {
		groups.clear();
		targets.clear();
		this.config = process.getParent();
	}

	public void restoreRefs() {
		((FacesProcessImpl)process).setReference(process.getParent());
	}
	
	Set<Object> updateLocks = new HashSet<Object>();
	
	public boolean isUpdateLocked() {
		return updateLocks.size() > 0;
	}
	
	public void addUpdateLock(Object lock) {
		updateLocks.add(lock);
	}

	public void removeUpdateLock(Object lock) {
		updateLocks.remove(lock);
	}

	public void updateProcess() {
		if(isUpdateLocked()) return;
		addUpdateLock(this);
		try {
			updateProcess0();
		} finally {
			removeUpdateLock(this);
		}
	}
	private void updateProcess0() {
		reset();
		rules = config.getChildByPath(FOLDER_NAVIGATION_RULES).getChildren();
        for (int i = 0; i < rules.length; i++) {
        	String fvi = rules[i].getAttributeValue(ATT_FROM_VIEW_ID);
        	if(fvi == null) continue;
        	String pp = getRuleIdentity(rules[i]);
			XModelObject g = findOrCreateGroup(fvi, pp);
			groups.put(pp, g);			
        	XModelObject[] cs = rules[i].getChildren();
        	for (int j = 0; j < cs.length; j++) {
        		String tvi = cs[j].getAttributeValue(ATT_TO_VIEW_ID);
        		if(tvi == null) continue;
        		String ppt = NavigationRuleObjectImpl.toNavigationRulePathPart(tvi);
        		targets.put(ppt, getTemplate());
        	}
        }
        for(String s: groups.keySet()) targets.remove(s);

		removeObsoleteGroups();
		createPageGroups();
		updateGroups();

		updatePages();
	}
	
	private String getRuleIdentity(XModelObject rule) {
		String fvi = rule.getAttributeValue(ATT_FROM_VIEW_ID);
		String pp = NavigationRuleObjectImpl.toNavigationRulePathPart(fvi);
		if(!isPattern(fvi)) return pp;
		String index = rule.getAttributeValue("index"); //$NON-NLS-1$
		if(index.startsWith("-") || index.equals("0")) return pp; //$NON-NLS-1$ //$NON-NLS-2$
		return pp + ":" + index; //$NON-NLS-1$
	}
	
	public XModelObject findOrCreateGroup(String path, String pp) {
		if(pp == null) pp = NavigationRuleObjectImpl.toNavigationRulePathPart(path);
		XModelObject g = process.getChildByPath(pp);
		if(g == null) {
			g = process.getModel().createModelObject(ENT_PROCESS_GROUP, null);
			g.setAttributeValue(ATT_NAME, pp);
			g.setAttributeValue(ATT_PATH, path);
			process.addChild(g);
		}
		return g;
	}
	
	private void removeObsoleteGroups() {
		boolean q = "yes".equals(JSFPreference.DO_NOT_CREATE_EMPTY_RULE.getValue()); //$NON-NLS-1$
		XModelObject[] ps = process.getChildren(ENT_PROCESS_GROUP);
		for (int i = 0; i < ps.length; i++) {
			String path = ps[i].getPathPart();
			if(!groups.containsKey(path) && !targets.containsKey(path)) {
				if(q && "true".equals(ps[i].getAttributeValue("persistent"))) { //$NON-NLS-1$ //$NON-NLS-2$
					groups.put(path, ps[i]);
				} else {
					ps[i].removeFromParent();
				}
			}
		}
	}
	
	private void createPageGroups() {
		String[] paths = (String[])targets.keySet().toArray(new String[0]);
		for (int i = 0; i < paths.length; i++) {
			String fvi = NavigationRuleObjectImpl.toFromViewId(paths[i]);
			XModelObject g = findOrCreateGroup(fvi, paths[i]);
			targets.put(paths[i], g);			
		}
	}
	
	private void updateGroups() {
		ReferenceGroupImpl[] gs = (ReferenceGroupImpl[])groups.values().toArray(new ReferenceGroupImpl[0]);
		for (int i = 0; i < gs.length; i++) {
			setGroupReferences(rules, gs[i]);
			updateGroup(gs[i]);
		}
		gs = (ReferenceGroupImpl[])targets.values().toArray(new ReferenceGroupImpl[0]);
		for (int i = 0; i < gs.length; i++) {
			gs[i].setReference(new XModelObject[0]);
			updateGroup(gs[i]);
		}
	}
	
	private void setGroupReferences(XModelObject[] rules, ReferenceGroupImpl group) {
		String path = group.getPathPart();
		ArrayList<XModelObject> list = null;
		for (int i = 0; i < rules.length; i++) {
			String pp = getRuleIdentity(rules[i]);
			if(!path.equals(pp)) continue;
			if(list == null) list = new ArrayList<XModelObject>();
			list.add(rules[i]);
		}
		XModelObject[] rs = (list == null) ? new XModelObject[0] : list.toArray(new XModelObject[list.size()]);
		group.setReference(rs);
	}
	
	private void updateGroup(ReferenceGroupImpl group) {
		if(group.isUpToDate()) return;
		group.notifyUpdate();
		XModelObject[] rs = group.getReferences();
		XModelObject[] is = group.getChildren(ENT_PROCESS_ITEM);
		for (int i = 0; i < rs.length; i++) {
			XModelObject item = null;
			if(i < is.length) {
				item = is[i]; 
			} else {
				item = createItem(group, rs[i]);
			}
			ReferenceObjectImpl r = (ReferenceObjectImpl)item;
			r.setReference(rs[i]);
			updateItem(r);
		}
		for (int i = rs.length; i < is.length; i++) is[i].removeFromParent();
	}
	
	private XModelObject createItem(XModelObject group, XModelObject rule) {
		XModelObject item = group.getModel().createModelObject(ENT_PROCESS_ITEM, null);
		item.setAttributeValue(ATT_ID, rule.getPathPart());
		item.setAttributeValue(ATT_PATH, rule.getAttributeValue(ATT_FROM_VIEW_ID));
		String name = XModelObjectUtil.createNewChildName("item", group); //$NON-NLS-1$
		item.setAttributeValue(ATT_NAME, name);
		group.addChild(item);
		return item;
	}
	
	private void updateItem(ReferenceObjectImpl item) {
		if(item.isUpToDate()) return;
		item.notifyUpdate();
		XModelObject rule = item.getReference();		
		item.setAttributeValue(ATT_ID, rule.getPathPart());
		item.setAttributeValue(ATT_PATH, rule.getAttributeValue(ATT_FROM_VIEW_ID));
		updateOutputs(item);
	}
	
	private void updateOutputs(ReferenceObjectImpl item) {
		XModelObject rule = item.getReference();
		XModelObject[] cs = rule.getChildren();		
		XModelObject[] os = item.getChildren();
		if(isOutputOrderUpToDate(cs, os)) {
			updateOutputs_1(item, cs, os);
		} else {
			updateOutputs_2(item, cs, os);
		}
	}

	private void updateOutputs_1(ReferenceObjectImpl item, XModelObject[] cases, XModelObject[] outputs) {
		int c = 0;
		for (int i = 0; i < cases.length; i++) {
			XModelObject output = null;
			if(c < outputs.length) {
				output = outputs[c]; 
			} else {
				output = createOutput(item, cases[i]);
			}
			ReferenceObjectImpl r = (ReferenceObjectImpl)output;
			r.setReference(cases[i]);
			updateOutput(r);
			++c;
		}
		for (int i = c; i < outputs.length; i++) outputs[i].removeFromParent();
	}

	private void updateOutputs_2(ReferenceObjectImpl item, XModelObject[] cases, XModelObject[] outputs) {
		Map<String,XModelObject> map = new HashMap<String,XModelObject>();
		for (int i = 0; i < outputs.length; i++) {
			outputs[i].removeFromParent();
			map.put(outputs[i].getAttributeValue(ATT_ID), outputs[i]);			
		}
		for (int i = 0; i < cases.length; i++) {
			XModelObject output = map.get(cases[i].getPathPart());
			if(output == null) {
				output = createOutput(item, cases[i]);
			} else {
				item.addChild(output);
			}
			ReferenceObjectImpl r = (ReferenceObjectImpl)output;
			r.setReference(cases[i]);
			updateOutput(r);
		}
	}
	
	private boolean isOutputOrderUpToDate(XModelObject[] cases, XModelObject[] outputs) {
		for (int i = 0; i < cases.length && i < outputs.length; i++) {
			ReferenceObject r = (ReferenceObject)outputs[i];
			if(r.getReference() == cases[i]) continue;
			String pp = cases[i].getPathPart();
			String id = outputs[i].getAttributeValue(ATT_ID);
			if(!pp.equals(id)) return false;			
		}
		return true;
	}
	
	private XModelObject createOutput(XModelObject item, XModelObject rulecase) {
		XModelObject output = item.getModel().createModelObject(ENT_PROCESS_ITEM_OUTPUT, null);
		output.setAttributeValue(ATT_ID, rulecase.getPathPart());
		output.setAttributeValue(ATT_PATH, rulecase.getAttributeValue(ATT_TO_VIEW_ID));
		String name = XModelObjectUtil.createNewChildName("output", item); //$NON-NLS-1$
		output.setAttributeValue(ATT_NAME, name);
		item.addChild(output);
		return output;
	}
	
	private void updateOutput(ReferenceObjectImpl output) {
		if(output.isUpToDate()) return;
		output.notifyUpdate();
		XModelObject rulecase = output.getReference();		
		output.setAttributeValue(ATT_ID, rulecase.getPathPart());
		String path = rulecase.getAttributeValue(ATT_TO_VIEW_ID);
		output.setAttributeValue(ATT_PATH, path);
		String title = JSFProcessStructureHelper.createItemOutputPresentation(rulecase);
		output.setAttributeValue("title", title); //$NON-NLS-1$
		XModelObject g = findGroupByPath(path);
		String target = (g == null) ? "" : g.getPathPart(); //$NON-NLS-1$
		output.setAttributeValue(ATT_TARGET, target);
	}
	
	private XModelObject findGroupByPath(String path) {
		return getPage(path);
	}
	
	public void autolayout() {
		AutoLayout auto = new AutoLayout();
		auto.setItems(new JSFItems());
		auto.setProcess(process);
	}
	
	public XModelObject getPage(String path) {
		path = NavigationRuleObjectImpl.toNavigationRulePathPart(path);
		XModelObject g = (XModelObject)groups.get(path);
		if(g == null) g = (XModelObject)targets.get(path);
		return g;
	}

	public void updatePages() {
		JSFPageUpdateManager pu = JSFPageUpdateManager.getInstance(process.getModel());
		pu.lock();
		XModelObject[] items = process.getChildren();
		for (int i = 0; i < items.length; i++) pu.updatePage(this, items[i]);
		pu.unlock();
	}
	
	public static boolean isPattern(String path) {
		return path != null && (path.length() == 0 || path.indexOf('*') >= 0);
	}

}
