package org.jboss.tools.seam.pages.xml.model.helpers;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.jst.web.model.helpers.autolayout.AutoLayout;
import org.jboss.tools.seam.pages.xml.model.SeamPagesConstants;
import org.jboss.tools.seam.pages.xml.model.helpers.autolayout.SeamPagesItems;
import org.jboss.tools.seam.pages.xml.model.impl.SeamPagesProcessImpl;

public class SeamPagesProcessHelper implements SeamPagesConstants {
	private XModelObject process;
	private static XModelObject TEMPLATE;
	private XModelObject config;
	private Map<String,XModelObject> groups = new HashMap<String,XModelObject>();
	private Map<String,XModelObject> exceptions = new HashMap<String,XModelObject>();
	private Map<String,XModelObject> targets = new HashMap<String,XModelObject>();

	public SeamPagesProcessHelper(XModelObject process) {
		this.process = process;
	}

	public static SeamPagesProcessHelper getHelper(XModelObject process) {
		return ((SeamPagesProcessImpl)process).getHelper();
	}

	private synchronized void reset() {
		groups.clear();
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
		//TODO
	}


	public void autolayout() {
		AutoLayout auto = new AutoLayout();
		auto.setItems(new SeamPagesItems());
		auto.setProcess(process);
	}
	
	public XModelObject getPage(String path) {
		path = toNavigationRulePathPart(path);
		XModelObject g = (XModelObject)groups.get(path);
		if(g == null) g = (XModelObject)targets.get(path);
		return g;
	}

	public void updatePages() {
		//TODO
	}

	public static String toNavigationRulePathPart(String path) {
		return "" + path.replace('/', '#');
	}
	
}
