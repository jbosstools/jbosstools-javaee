package org.jboss.tools.seam.pages.xml.model.helpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.util.XModelObjectUtil;
import org.jboss.tools.jst.web.model.ReferenceObject;
import org.jboss.tools.jst.web.model.helpers.autolayout.AutoLayout;
import org.jboss.tools.seam.pages.xml.model.SeamPagesConstants;
import org.jboss.tools.seam.pages.xml.model.helpers.autolayout.SeamPagesItems;
import org.jboss.tools.seam.pages.xml.model.impl.ReferenceObjectImpl;
import org.jboss.tools.seam.pages.xml.model.impl.SeamPagesProcessImpl;

public class SeamPagesProcessHelper implements SeamPagesConstants {
	private XModelObject process;
	private static XModelObject TEMPLATE;
	private XModelObject config;
	private Map<String,XModelObject> pageItems = new HashMap<String,XModelObject>();
	private Map<String,XModelObject> exceptionItems = new HashMap<String,XModelObject>();
	private Map<String,XModelObject> targets = new HashMap<String,XModelObject>();

	public SeamPagesProcessHelper(XModelObject process) {
		this.process = process;
	}

	public static SeamPagesProcessHelper getHelper(XModelObject process) {
		return ((SeamPagesProcessImpl)process).getHelper();
	}

	private synchronized void reset() {
		pageItems.clear();
		exceptionItems.clear();
		targets.clear();
		this.config = process.getParent();
	}

	public void restoreRefs() {
		((SeamPagesProcessImpl)process).setReference(process.getParent());
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
		XModelObject[] sourcePages = config.getChildByPath(FOLDER_PAGES).getChildren();

		for (int i = 0; i < sourcePages.length; i++) {
			String view = sourcePages[i].getAttributeValue(ATTR_VIEW_ID);
			if(view == null) continue;
			String pp = toNavigationRulePathPart(view);
			XModelObject g = findOrCreateItem(view, pp, TYPE_PAGE);
			((ReferenceObjectImpl)g).setReference(sourcePages[i]);
			pageItems.put(pp, g);
			XModelObject[] ns = sourcePages[i].getChildren();
			for (int j = 0; j < ns.length; j++) {
				String entity = ns[j].getModelEntity().getName();
				if(!entity.startsWith(ENT_NAVIGATION)) continue;
				if(entity.startsWith(ENT_NAVIGATION_RULE)) {
					addTarget(ns[j]);
				} else {
					XModelObject[] rs = ns[j].getChildren();
					for (int k = 0; k < rs.length; k++) {
						addTarget(rs[k]);
					}
				}
			}
		}

		XModelObject[] sourceExceptions = config.getChildByPath(FOLDER_EXCEPTIONS).getChildren();
		for (int i = 0; i < sourceExceptions.length; i++) {
			String code = sourceExceptions[i].getAttributeValue("class");
			XModelObject g = findOrCreateItem(code, code, TYPE_EXCEPTION);
			((ReferenceObjectImpl)g).setReference(sourceExceptions[i]);
			exceptionItems.put(code, g);
			addTarget(sourceExceptions[i]);
		}

		Iterator<String> it = pageItems.keySet().iterator();
		while(it.hasNext()) targets.remove(it.next());

		removeObsoletePageItems();
		removeObsoleteExceptionItems();
		createPageItems();
		updatePageItems();
		updateExceptionItems();
		
		updatePages();
	}

	private void addTarget(XModelObject rule) {
		XModelObject target = rule.getChildByPath("target");
		if(target == null) return;
		String tvi = target.getAttributeValue(ATTR_VIEW_ID);
		if(tvi == null) return;
		String ppt = toNavigationRulePathPart(tvi);
		targets.put(ppt, getTemplate());							
	}

	private XModelObject getTemplate() {
		if(TEMPLATE == null && process != null) {
			TEMPLATE = process.getModel().createModelObject(ENT_PROCESS_ITEM, null);
		}
		return TEMPLATE;
	}
	
	public XModelObject findOrCreateItem(String path, String pp, String type) {
		if(pp == null) pp = toNavigationRulePathPart(path);
		XModelObject g = process.getChildByPath(pp);
		if(g == null) {
			g = process.getModel().createModelObject(ENT_PROCESS_ITEM, null);
			g.setAttributeValue(ATTR_NAME, pp);
			g.setAttributeValue(ATTR_PATH, path);
			g.setAttributeValue(ATTR_TYPE, type);
			process.addChild(g);
		}
		return g;
	}
	
	private void removeObsoletePageItems() {
		XModelObject[] ps = process.getChildren(ENT_PROCESS_ITEM);
		for (int i = 0; i < ps.length; i++) {
			String path = ps[i].getPathPart();
			String type = ps[i].getAttributeValue(ATTR_TYPE);
			if(!TYPE_PAGE.equals(type)) continue;
			if(!pageItems.containsKey(path) && !targets.containsKey(path)) {
				ps[i].removeFromParent();
			}
		}
	}

	private void removeObsoleteExceptionItems() {
		XModelObject[] ps = process.getChildren(ENT_PROCESS_ITEM);
		for (int i = 0; i < ps.length; i++) {
			String path = ps[i].getPathPart();
			String type = ps[i].getAttributeValue(ATTR_TYPE);
			if(!TYPE_EXCEPTION.equals(type)) continue;
			if(!exceptionItems.containsKey(path)) {
				ps[i].removeFromParent();
			}
		}
	}

	private void createPageItems() {
		String[] paths = (String[])targets.keySet().toArray(new String[0]);
		for (int i = 0; i < paths.length; i++) {
			String fvi = toFromViewId(paths[i]);
			XModelObject g = findOrCreateItem(fvi, paths[i], TYPE_PAGE);
			targets.put(paths[i], g);			
		}
	}

	private void updatePageItems() {
		ReferenceObjectImpl[] gs = (ReferenceObjectImpl[])pageItems.values().toArray(new ReferenceObjectImpl[0]);
		for (int i = 0; i < gs.length; i++) {
			updatePageItem(gs[i]);
		}
		gs = (ReferenceObjectImpl[])targets.values().toArray(new ReferenceObjectImpl[0]);
		for (int i = 0; i < gs.length; i++) {
			gs[i].setReference(null);
			updatePageItem(gs[i]);
		}
	}
	
	private void updateExceptionItems() {
		ReferenceObjectImpl[] gs = (ReferenceObjectImpl[])exceptionItems.values().toArray(new ReferenceObjectImpl[0]);
		for (int i = 0; i < gs.length; i++) {
			updateExceptionItem(gs[i]);
		}
	}

	private void updatePageItem(ReferenceObjectImpl item) {
		if(item.isUpToDate()) return;
		item.notifyUpdate();
		XModelObject sourcePage = item.getReference();		
		item.setAttributeValue(ATTR_ID, sourcePage.getPathPart());
		item.setAttributeValue(ATTR_PATH, sourcePage.getAttributeValue(ATTR_VIEW_ID));
		XModelObject[] cs = getPageTargets(sourcePage);		
		updateOutputs(item, cs);
	}

	private XModelObject[] getPageTargets(XModelObject o) {
		XModelObject[] ns = o.getChildren();
		List<XModelObject> result = null;
		for (int i = 0; i < ns.length; i++) {
			String entity = ns[i].getModelEntity().getName();
			if(!entity.startsWith(ENT_NAVIGATION)) continue;
			if(entity.startsWith(ENT_NAVIGATION_RULE)) {
				XModelObject t = getTargetChild(ns[i]);
				if(t != null) {
					if(result == null) result = new ArrayList<XModelObject>();
					result.add(t);
				}
			} else {
				XModelObject[] rs = ns[i].getChildren();
				for (int k = 0; k < rs.length; k++) {
					XModelObject t = getTargetChild(rs[k]);
					if(t != null) {
						if(result == null) result = new ArrayList<XModelObject>();
						result.add(t);
					}
				}
			}
		}
		return result == null ? new XModelObject[0]
		             : result.toArray(new XModelObject[0]);
	}
	
	private void updateExceptionItem(ReferenceObjectImpl item) {
		if(item.isUpToDate()) return;
		item.notifyUpdate();
		XModelObject exc = item.getReference();		
		item.setAttributeValue(ATTR_ID, exc.getPathPart());
		item.setAttributeValue(ATTR_PATH, exc.getAttributeValue(ATTR_VIEW_ID));
		XModelObject t = getTargetChild(exc);
		XModelObject[] cs = t == null ? new XModelObject[0] : new XModelObject[]{t};		
		updateOutputs(item, cs);
	}

	private XModelObject getTargetChild(XModelObject o) {
		XModelObject t = o.getChildByPath("target");
		if(t == null || t.getModelEntity().getAttribute(ATTR_VIEW_ID) == null) return null;
		return t;		
	}
	
	private void updateOutputs(ReferenceObjectImpl item, XModelObject[] cases) {
		XModelObject[] os = item.getChildren();
		if(isOutputOrderUpToDate(cases, os)) {
			updateOutputs_1(item, cases, os);
		} else {
			updateOutputs_2(item, cases, os);
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
			XModelObject output = outputs[i];
			output.removeFromParent();
			map.put(output.getAttributeValue(ATTR_ID), output);			
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
			String pp = cases[i].getAttributeValue(ATTR_VIEW_ID);
			String id = outputs[i].getAttributeValue(ATTR_ID);
			if(!pp.equals(id)) return false;			
		}
		return true;
	}
	
	private XModelObject createOutput(XModelObject item, XModelObject rulecase) {
		XModelObject output = item.getModel().createModelObject(ENT_PROCESS_ITEM_OUTPUT, null);
		output.setAttributeValue(ATTR_ID, rulecase.getAttributeValue(ATTR_VIEW_ID));
		output.setAttributeValue(ATTR_PATH, rulecase.getAttributeValue(ATTR_VIEW_ID));
		String name = XModelObjectUtil.createNewChildName("output", item);
		output.setAttributeValue(ATTR_NAME, name);

		ReferenceObjectImpl r = (ReferenceObjectImpl)output;
		r.setReference(rulecase);
		updateOutput(r);

		item.addChild(output);
		return output;
	}
	
	private void updateOutput(ReferenceObjectImpl output) {
		if(output.isUpToDate()) return;
		output.notifyUpdate();
		XModelObject rulecase = output.getReference();		
		output.setAttributeValue(ATTR_ID, rulecase.getAttributeValue(ATTR_VIEW_ID));
		String path = rulecase.getAttributeValue(ATTR_VIEW_ID);
		output.setAttributeValue(ATTR_PATH, path);
//		String title = SeamPagesProcessStructureHelper.createItemOutputPresentation(rulecase);
//		output.setAttributeValue("title", title);
		XModelObject g = findGroupByPath(path);
		String target = (g == null) ? "" : g.getPathPart();
		output.setAttributeValue(ATTR_TARGET, target);
	}
	
	private XModelObject findGroupByPath(String path) {
		return getPage(path);
	}
	
	public void autolayout() {
		AutoLayout auto = new AutoLayout();
		auto.setItems(new SeamPagesItems());
		auto.setProcess(process);
	}
	
	public XModelObject getPage(String path) {
		path = toNavigationRulePathPart(path);
		XModelObject g = (XModelObject)pageItems.get(path);
		if(g == null) g = (XModelObject)targets.get(path);
		return g;
	}

	public void updatePages() {
		//TODO
	}

	public static String toNavigationRulePathPart(String path) {
		return "" + path.replace('/', '#');
	}
	
	public static String toFromViewId(String pathpart) {
		if(!pathpart.startsWith("rules:")) return pathpart.replace('#', '/');
		pathpart = pathpart.substring(6).replace('#', '/');
		int i = pathpart.lastIndexOf(':');
		return (i < 0) ? pathpart : pathpart.substring(0, i);
	}

	public static boolean isPattern(String path) {
		return path != null && (path.length() == 0 || path.indexOf('*') >= 0);
	}

}
