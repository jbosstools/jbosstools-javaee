package org.jboss.tools.seam.pages.xml.model.helpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jboss.tools.common.model.XModelException;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.util.XModelObjectUtil;
import org.jboss.tools.jst.web.model.ReferenceObject;
import org.jboss.tools.jst.web.model.helpers.autolayout.AutoLayout;
import org.jboss.tools.seam.pages.xml.model.SeamPagesConstants;
import org.jboss.tools.seam.pages.xml.model.helpers.autolayout.SeamPagesItems;
import org.jboss.tools.seam.pages.xml.model.impl.ReferenceObjectImpl;
import org.jboss.tools.seam.pages.xml.model.impl.SeamPagesDiagramImpl;

public class SeamPagesDiagramHelper implements SeamPagesConstants {
	private XModelObject diagram;
	private static XModelObject TEMPLATE;
	private XModelObject config;
	private Map<String,XModelObject> pageItems = new HashMap<String,XModelObject>();
	private Set<String> pageViewIds = new HashSet<String>();
	private Map<String,XModelObject> exceptionItems = new HashMap<String,XModelObject>();
	private Map<String,XModelObject> targets = new HashMap<String,XModelObject>();

	public SeamPagesDiagramHelper(XModelObject diagram) {
		this.diagram = diagram;
	}

	public static SeamPagesDiagramHelper getHelper(XModelObject diagram) {
		return ((SeamPagesDiagramImpl)diagram).getHelper();
	}

	private synchronized void reset() {
		pageItems.clear();
		pageViewIds.clear();
		exceptionItems.clear();
		targets.clear();
		this.config = diagram.getParent();
	}

	public void restoreRefs() {
		((SeamPagesDiagramImpl)diagram).setReference(diagram.getParent());
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

	public void updateDiagram() {
		if(isUpdateLocked()) return;
		addUpdateLock(this);
		try {
			updateDiagram0();
		} finally {
			removeUpdateLock(this);
		}
	}

	private void updateDiagram0() {
		reset();
		XModelObject[] sourcePages = config.getChildByPath(FOLDER_PAGES).getChildren();

		XModelObject[] cs = diagram.getChildren();
		Map<XModelObject,XModelObject> old = new HashMap<XModelObject, XModelObject>();
		for (int i = 0; i < cs.length; i++) {
			if(cs[i] instanceof ReferenceObject) {
				XModelObject k = ((ReferenceObject)cs[i]).getReference();
				if(k != null) old.put(k, cs[i]);
			}
		}

		for (int i = 0; i < sourcePages.length; i++) {
			String view = sourcePages[i].getAttributeValue(ATTR_VIEW_ID);
			if(view == null) continue;
			String pp = toNavigationRulePathPart(view);
			XModelObject og = old.get(sourcePages[i]);
			if(og != null) {
				String opp = og.getPathPart();
				if(!pp.equals(opp)) {
					pageItems.remove(opp);
					og.setAttributeValue(ATTR_NAME, pp);
				}
			}
			XModelObject g = og != null ? og : findOrCreateItem(view, pp, TYPE_PAGE);
			((ReferenceObjectImpl)g).setReference(sourcePages[i]);
			pageItems.put(pp, g);
			pageViewIds.add(view);
			XModelObject[] ns = sourcePages[i].getChildren();
			for (int j = 0; j < ns.length; j++) {
				String entity = ns[j].getModelEntity().getName();
				if(!entity.startsWith(ENT_NAVIGATION)) continue;
				if(entity.startsWith(ENT_NAVIGATION_RULE)) {
					addTarget(ns[j], true);
					if(!entity.endsWith(SUFF_21) && !entity.endsWith(SUFF_22)) {
						continue;
					}
				}
				XModelObject[] rs = ns[j].getChildren();
				for (int k = 0; k < rs.length; k++) {
					addTarget(rs[k], true);
				}
			}
		}

		XModelObject[] sourceExceptions = config.getChildByPath(FOLDER_EXCEPTIONS).getChildren();
		for (int i = 0; i < sourceExceptions.length; i++) {
			String code = sourceExceptions[i].getAttributeValue("class");
			String pp = "exception:" + code;
			XModelObject og = old.get(sourceExceptions[i]);
			if(og != null) {
				String opp = og.getPathPart();
				if(!pp.equals(opp)) {
					exceptionItems.remove(opp);
					og.setAttributeValue(ATTR_NAME, pp);
					((ReferenceObjectImpl)og).setReference(null);
				}
			}
			XModelObject g = og != null ? og : findOrCreateItem(code, pp, TYPE_EXCEPTION);
			((ReferenceObjectImpl)g).setReference(sourceExceptions[i]);
			exceptionItems.put(pp, g);
			addTarget(sourceExceptions[i], false);
		}

		Iterator<String> it = pageItems.keySet().iterator();
		while(it.hasNext()) targets.remove(it.next());

		removeObsoleteExceptionItems();
		createPageItems();
		removeObsoletePageItems();
		updatePageItems();
		updateExceptionItems();
		
		updatePages();
	}

	private void addTarget(XModelObject rule, boolean addEmpty) {
		XModelObject target = rule.getChildByPath("target");
		if(target == null) return;
		String tvi = target.getAttributeValue(ATTR_VIEW_ID);
		if(tvi == null) return;
		if(!addEmpty && tvi.length() == 0) return;
		targets.put(toNavigationRulePathPart(tvi), getTemplate());							
	}

	private XModelObject getTemplate() {
		if(TEMPLATE == null && diagram != null) {
			TEMPLATE = diagram.getModel().createModelObject(ENT_DIAGRAM_ITEM, null);
		}
		return TEMPLATE;
	}
	
	public XModelObject findOrCreateItem(String path, String pp, String type) {
		if(pp == null) pp = toNavigationRulePathPart(path);
		XModelObject g = diagram.getChildByPath(pp);
		if(g == null) {
			g = diagram.getModel().createModelObject(ENT_DIAGRAM_ITEM, null);
			g.setAttributeValue(ATTR_NAME, pp);
			g.setAttributeValue(ATTR_PATH, path);
			g.setAttributeValue(ATTR_TYPE, type);
			diagram.addChild(g);
		}
		return g;
	}
	
	private void removeObsoletePageItems() {
		XModelObject[] ps = diagram.getChildren(ENT_DIAGRAM_ITEM);
		for (int i = 0; i < ps.length; i++) {
			String path = ps[i].getPathPart();
			String type = ps[i].getAttributeValue(ATTR_TYPE);
			if(!TYPE_PAGE.equals(type)) continue;
			if(!pageItems.containsKey(path) && targets.get(path) != ps[i]) {
				ps[i].removeFromParent();
			}
		}
	}

	private void removeObsoleteExceptionItems() {
		XModelObject[] ps = diagram.getChildren(ENT_DIAGRAM_ITEM);
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
//			fvi = findBestMatch(fvi);
			XModelObject g = findOrCreateItem(fvi, toNavigationRulePathPart(fvi), TYPE_PAGE);
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
			long ts = gs[i].getTimeStamp();
			boolean hadReference = gs[i].getReference() != null;
			gs[i].setReference(null);
			gs[i].setAttributeValue("params", "");
			
			XModelObject[] os = gs[i].getChildren(ENT_DIAGRAM_ITEM_OUTPUT);
			for (int j = 0; j < os.length; j++) {
				if(SUBTYPE_CUSTOM.equals(os[j].getAttributeValue(ATTR_SUBTYPE))) {
					continue;
				}
				gs[i].removeChild(os[j]);
			}
			updatePageItem(gs[i]);
			if(hadReference && ts == gs[i].getTimeStamp()) {
				gs[i].fireReferenceChanged();
			}
		}
	}
	
	private void updateExceptionItems() {
		ReferenceObjectImpl[] gs = (ReferenceObjectImpl[])exceptionItems.values().toArray(new ReferenceObjectImpl[0]);
		for (int i = 0; i < gs.length; i++) {
			updateExceptionItem(gs[i]);
		}
	}

	private void updatePageItem(ReferenceObjectImpl item) {
		if(item.getReference() == null) {
			updateUndeclaredPageItem(item);
			return;
		}
//		if(item.isUpToDate()) return;
		boolean iud = item.isUpToDate();
		long ts = item.getTimeStamp();
		item.notifyUpdate();
		XModelObject sourcePage = item.getReference();		
		item.setAttributeValue(ATTR_ID, sourcePage.getPathPart());
		item.setAttributeValue(ATTR_PATH, sourcePage.getAttributeValue(ATTR_VIEW_ID));
		String[][] params = SeamPagesDiagramStructureHelper.getInstance().getParams(item);
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < params.length; i++) {
			sb.append(params[i][0]).append('=').append(params[i][1]).append(';');
		}
		item.setAttributeValue("params", sb.toString());
		XModelObject[] cs = getPageTargets(sourcePage);		
		updateOutputs(item, cs);
		if(!iud && ts == item.getTimeStamp()) {
			item.fireReferenceChanged();
		}
	}

	private void updateUndeclaredPageItem(ReferenceObjectImpl item) {
		//update virtual link to findBestMatch(path) element
		XModelObject[] os = item.getChildren();
		String path = item.getAttributeValue(ATTR_PATH);
		String fvi = findBestMatch(path);
		XModelObject g = getPage(fvi);
		if(g != null && item != g) {
			XModelObject output = item.getModel().createModelObject(ENT_DIAGRAM_ITEM_OUTPUT, null);
			output.setAttributeValue(ATTR_ID, fvi);
			output.setAttributeValue(ATTR_PATH, fvi);
			String name = XModelObjectUtil.createNewChildName("output", item);
			output.setAttributeValue(ATTR_NAME, name);

			ReferenceObjectImpl r = (ReferenceObjectImpl)output;
			r.setReference(null);

			String target = (g == null) ? "" : g.getPathPart();
			output.setAttributeValue(ATTR_TARGET, target);

			item.addChild(output);
		}
		
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
				if(!entity.endsWith(SUFF_21) && !entity.endsWith(SUFF_22)) {
					continue;
				}
			}
			XModelObject[] rs = ns[i].getChildren();
			for (int k = 0; k < rs.length; k++) {
				XModelObject t = getTargetChild(rs[k]);
				if (t != null) {
					if (result == null) result = new ArrayList<XModelObject>();
					result.add(t);
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
		item.setAttributeValue(ATTR_PATH, exc.getAttributeValue("class"));
		XModelObject t = getTargetChild(exc);
		XModelObject[] cs = t == null ? new XModelObject[0] : new XModelObject[]{t};
		if(cs.length == 1) {
			String path = t.getAttributeValue(ATTR_VIEW_ID);
			if(path == null || path.length() == 0) {
				cs = new XModelObject[0];
			}
		}
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
		XModelObject output = item.getModel().createModelObject(ENT_DIAGRAM_ITEM_OUTPUT, null);
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
		if(output.getReference() == null) return;
//		if(output.isUpToDate()) return;
		output.notifyUpdate();
		XModelObject rulecase = output.getReference();		
		output.setAttributeValue(ATTR_ID, rulecase.getAttributeValue(ATTR_VIEW_ID));
		String path = rulecase.getAttributeValue(ATTR_VIEW_ID);
		output.setAttributeValue(ATTR_PATH, path);
//		String title = SeamPagesDiagramStructureHelper.createItemOutputPresentation(rulecase);
//		output.setAttributeValue("title", title);

//		String fvi = findBestMatch(path);
		XModelObject g = getPage(path);
		String target = (g == null) ? "" : g.getPathPart();
		output.setAttributeValue(ATTR_TARGET, target);
	}
	
	public void autolayout() {
		AutoLayout auto = new AutoLayout();
		auto.setItems(new SeamPagesItems());
		auto.setProcess(diagram);
	}
	
	public XModelObject getPage(String path) {
		path = toNavigationRulePathPart(path);
		XModelObject g = (XModelObject)pageItems.get(path);
		if(g == null) g = (XModelObject)targets.get(path);
		return g;
	}

	public void updatePages() {
		SeamPagesPageRefUpdateManager pu = SeamPagesPageRefUpdateManager.getInstance(diagram.getModel());
		pu.lock();
		XModelObject[] items = diagram.getChildren();
		for (int i = 0; i < items.length; i++) pu.updatePage(this, items[i]);
		pu.unlock();
	}
	
	public String findBestMatch(String viewId) {
		if(viewId == null || pageViewIds.contains(viewId)) {
			return viewId;
		}
		String best = viewId;
		int match = 0;
		for (String v: pageViewIds) {
			int i = v.indexOf('*');
			if(i < 0) continue;
			String head = v.substring(0, i);
			String tail = v.substring(i + 1);
			int m = head.length() + tail.length();
			if(m > viewId.length() || m <= match) continue;
			if(head.length() > 0 && !viewId.startsWith(head)) continue;
			if(tail.length() > 0 && !viewId.endsWith(tail)) continue;
			best = v;
			match = m;			
		}
		return best;
	}

	public static String toNavigationRulePathPart(String path) {
		return "" + encode(path);
	}
	
	public static String toFromViewId(String pathpart) {
		if(!pathpart.startsWith("rules:")) return decode(pathpart);
		pathpart = decode(pathpart.substring(6));
		int i = pathpart.lastIndexOf(':');
		return (i < 0) ? pathpart : pathpart.substring(0, i);
	}

	public static boolean isPattern(String path) {
		return path != null && (path.length() == 0 || path.indexOf('*') >= 0);
	}

	static String encode(String s) {
		StringBuffer result = new StringBuffer();
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if(c == '/') {
				result.append("#x");
			} else if(c == '#') {
				result.append("##");
			} else {
				result.append(c);
			}
		}
		return result.toString();
	}

	static String decode(String s) {
		StringBuffer result = new StringBuffer();
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			if(c == '#') {
				char c1 = i + 1 < s.length() ? s.charAt(i + 1) : '\0';
				if(c1 == 'x') {
					result.append('/');
					i++;
				} else if(c1 == '#') {
					result.append("#");
					i++;
				} else {
					result.append("#");
				}
			} else {
				result.append(c);
			}
		}
		return result.toString();
	}

}
