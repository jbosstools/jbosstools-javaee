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
package org.jboss.tools.struts.plugins.model.handlers;

import java.util.Properties;

import org.jboss.tools.common.meta.action.SpecialWizard;
import org.jboss.tools.common.meta.action.XActionInvoker;
import org.jboss.tools.common.meta.action.impl.handlers.DefaultCreateHandler;
import org.jboss.tools.common.model.XModel;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.util.XModelObjectUtil;
import org.jboss.tools.struts.webprj.model.helpers.WebModulesHelper;

public abstract class PluginRegistration implements SpecialWizard {
	protected Properties p;
	protected XModel model;
	protected XModelObject cfg;
	protected String oldPath;
	protected String path;
	protected boolean test;

	public void setObject(Object object) {
		p = (Properties)object;
		model = (XModel)p.get("model");
		XModelObject[] cgs = WebModulesHelper.getInstance(model).getAllConfigs();
		if(cgs == null || cgs.length == 0) return;
		cfg = cgs[0];
		path = p.getProperty("path");
		oldPath = p.getProperty("oldPath");
		test = "true".equals(p.getProperty("test"));
	}

	public int execute() {
		if(cfg == null) return 1;
		if(test) {
			return test();
		} else if(path == null && oldPath != null) {
			remove();
		} else if(path == null) {
			return 1;
		} else if(oldPath != null) {
			replace(); 
		} else {
			append();
		}
		XActionInvoker.invoke("SaveActions.Save", cfg, null);
		return 0;
	}
	
	void append() {
		XModelObject plugin = getPlugin(true);
		XModelObject property = getSetProperty(plugin, true);
		addPathIfNeeded(property, path);
	}
	
	protected void modifyProperties(XModelObject plugin) {
	}
	
	void replace() {
		boolean isDefault = isOldNameDefault();
		XModelObject plugin = getPlugin(isDefault);
		if(plugin == null) return;
		XModelObject property = getSetProperty(plugin, isDefault);
		replacePath(property);
	}
	
	void remove() {
		XModelObject plugin = getPlugin(false);
		if(plugin == null) return;
		XModelObject property = getSetProperty(plugin, false);
		replacePath(property);
	}

	private XModelObject getPlugin(boolean create) {
		XModelObject plugins = cfg.getChildByPath("plug-ins");
		XModelObject[] os = plugins.getChildren();
		for (int i = 0; i < os.length; i++) {
			if(getPluginClassName().equals(os[i].getAttributeValue("className"))) return os[i];
		}
		if(!create) return null;
		XModelObject plugin = model.createModelObject("StrutsPlugin11", null);
		plugin.setAttributeValue("className", getPluginClassName());
		DefaultCreateHandler.addCreatedObject(plugins, plugin, -1);
		modifyProperties(plugin);
		return plugin;
	}
	
	private XModelObject getSetProperty(XModelObject plugin, boolean create) {
		return getSetProperty(plugin, getSetPropertyName(), create, getDefaultSetPropertyValue());
	}
	
	protected XModelObject getSetProperty(XModelObject plugin, String name, boolean create, String value) {
		XModelObject[] ps = plugin.getChildren();
		for (int i = 0; i < ps.length; i++) {
			if(name.equals(ps[i].getAttributeValue("property"))) {
				return ps[i];
			}
		}
		if(!create) return null;
		XModelObject property = model.createModelObject("StrutsPluginSetProperty11", null);
		property.setAttributeValue("property", name);
		if(value != null) property.setAttributeValue("value", value);
		DefaultCreateHandler.addCreatedObject(plugin, property, -1);
		return property;
	}
	
	private void addPathIfNeeded(XModelObject property, String path) {
		String value = property.getAttributeValue("value");
		String[] array = XModelObjectUtil.asStringArray(value);
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < array.length; i++) {
			if(array[i].equals(path)) return;
			if(sb.length() > 0) sb.append(",");
			sb.append(array[i]);
		}
		if(sb.length() > 0) sb.append(",");
		sb.append(path);
		value = sb.toString();
		property.getModel().changeObjectAttribute(property, "value", value);
	}
	
	private void replacePath(XModelObject property) {
		String value = property.getAttributeValue("value");
		String[] s = XModelObjectUtil.asStringArray(value);
		StringBuffer sb = new StringBuffer();
		boolean replaced = false;
		for (int i = 0; i < s.length; i++) {
			String add = null;
			if(s[i].equals(oldPath)) {
				add = path;
				replaced = true;
			} else {
				add = s[i];
			}
			if(add != null) {
				if(sb.length() > 0) sb.append(",");
				sb.append(add);
			}
		}
		if(!replaced && path != null) {
			if(sb.length() > 0) sb.append(",");
			sb.append(path);
		}
		value = sb.toString();
		property.getModel().changeObjectAttribute(property, "value", value);
	}
	
	int test() {
		if(path == null) return 1;
		XModelObject plugin = getPlugin(false);
		if(plugin == null) return 1;
		XModelObject property = getSetProperty(plugin, getSetPropertyName(), false, null);
		if(property == null) return 1;
		String value = property.getAttributeValue("value");
		String[] s = XModelObjectUtil.asStringArray(value);
		for (int i = 0; i < s.length; i++) {
			if(s[i].equals(path)) {
				return 0;
			}
		}
		return 1;
	}

	protected abstract String getPluginClassName();

	protected abstract String getSetPropertyName();

	protected String getDefaultSetPropertyValue() {
		return null;
	}

	protected boolean isOldNameDefault() {
		return false;
	}	

}
