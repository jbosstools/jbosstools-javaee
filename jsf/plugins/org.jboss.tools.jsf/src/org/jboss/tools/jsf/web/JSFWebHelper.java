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
package org.jboss.tools.jsf.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.jboss.tools.common.meta.action.XActionInvoker;
import org.jboss.tools.common.model.*;
import org.jboss.tools.common.model.filesystems.impl.FileSystemsImpl;
import org.jboss.tools.common.model.impl.XModelImpl;
import org.jboss.tools.common.model.util.XModelObjectUtil;
import org.jboss.tools.jst.web.model.helpers.WebAppHelper;

public class JSFWebHelper {
	public static String PARAM_NAME = "param-name";
	public static String PARAM_VALUE = "param-value";
	public static ConfigFilesData FACES_CONFIG_DATA = 
		new ConfigFilesData(
			"javax.faces.CONFIG_FILES", 
			new String[]{"/WEB-INF/faces-config.xml"}
		);
	
	public static ConfigFilesData FACELET_TAGLIB_DATA =
		new ConfigFilesData(
			"facelets.LIBRARIES", 
			new String[]{},
			";"
		);
	
	public static ConfigFilesData FACELETS_LIBRARIES =
		new ConfigFilesData(
			"javax.faces.FACELETS_LIBRARIES", 
			new String[]{},
			";"
		);
	
	static {
		FACES_CONFIG_DATA.usesDefaultWithoutRegistration = true;
	}
	
	public static XModelObject getWebConfig(XModel model) {
		return model.getByPath("/web.xml");
	}
	
	public static String[] getFacesConfigList(XModelObject webxml) {
		return getConfigFilesList(webxml, FACES_CONFIG_DATA);
	}

	public static String getFacesConfigListAsString(XModelObject webxml) {
		return getConfigFilesListAsString(webxml, FACES_CONFIG_DATA);
	}
	
	public static void registerFacesConfig(XModel model, String path) throws XModelException {
		registerConfigFile(model, path, FACES_CONFIG_DATA);
	}

	public static boolean isRegisterFacesConfig(XModel model, String path) {
		return isConfigFileRegistered(model, path, FACES_CONFIG_DATA);
	}
	
	public static void unregisterFacesConfig(XModel model, String path) throws XModelException {
		unregisterConfigFile(model, path, FACES_CONFIG_DATA);
	}
	
	public static void registerFaceletsTaglib(XModel model, String path) throws XModelException {
		registerConfigFile(model, path, FACELET_TAGLIB_DATA);
		registerConfigFile(model, path, FACELETS_LIBRARIES);
	}
	
	public static XModelObject findInitParam(XModelObject webxml) {
		return findInitParam(webxml, FACES_CONFIG_DATA.param);
	}
	
	public static XModelObject findInitParam(XModelObject webxml, String name) {
		return WebAppHelper.findWebAppContextParam(webxml, name);
	}
	
	public static void registerFacesConfigRename(XModel model, String oldConfigName, String newConfigName, String path) throws XModelException {
		registerConfigFileRename(model, oldConfigName, newConfigName, path, FACES_CONFIG_DATA);
	}
	
	public static String[] getConfigFilesList(XModelObject webxml, ConfigFilesData data) {
		XModelObject p = findInitParam(webxml, data.param);
		return (p == null) ? data.defaultList : XModelObjectUtil.asStringArray(p.getAttributeValue("param-value"), data.separator);
	}

	public static String[] getCompleteConfigFilesList(XModelObject webxml, ConfigFilesData data) {
		XModelObject p = findInitParam(webxml, data.param);
		if(p == null) return data.defaultList;
		String[] ls = XModelObjectUtil.asStringArray(p.getAttributeValue("param-value"), data.separator);
		if(ls.length == 0 || arrayEquals(data.defaultList, ls)) return data.defaultList;
		List<String> list = new ArrayList<String>();
		for (int i = 0; i < ls.length; i++) {
			if(!list.contains(ls[i])) list.add(ls[i]);
		}
		for (int i = 0; i < data.defaultList.length; i++) {
			if(!list.contains(data.defaultList[i])) list.add(data.defaultList[i]);
		}
		return (String[])list.toArray(new String[0]);
	}
	private static boolean arrayEquals(String[] a, String[] b) {
		if(a.length != b.length) return false;
		for (int i = 0; i < a.length; i++) if(!a[i].equals(b[i])) return false;
		return true;
	}

	public static String getConfigFilesListAsString(XModelObject webxml, ConfigFilesData data) {
		XModelObject p = findInitParam(webxml, data.param);
		if(p != null) {
			String r = p.getAttributeValue("param-value");
			if(data.usesDefaultWithoutRegistration && data.defaultList != null) {
				for (int i = 0; i < data.defaultList.length; i++) {
					String path = data.defaultList[i];
					if(("," + r + ",").indexOf("," + path + ",") >= 0) continue;
					XModelObject o = XModelImpl.getByRelativePath(webxml.getModel(), path);
					if(o == null) continue;
					if(!r.startsWith(",")) r = path + "," + r; else r = path + r; 
				}				
			}
			return r;
		}
		if(data.defaultList == null || data.defaultList.length == 0) return "";
		if(data.defaultList.length == 1) return data.defaultList[0];
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < data.defaultList.length; i++) {
			if(sb.length() > 0) sb.append(data.separator.charAt(0));
			sb.append(data.defaultList[i]);
		}
		return sb.toString();
	}
	
	public static void getConfigFiles(List<XModelObject> list, Set<XModelObject> set, XModel model, ConfigFilesData data) {
		FileSystemsImpl fs = (FileSystemsImpl)model.getByPath("FileSystems");
		if(fs != null) {
			fs.updateOverlapped();
		}
		XModelObject webxml = getWebConfig(model);
		String[] paths = getCompleteConfigFilesList(webxml, data);
		if(paths != null) for (int i = 0; i < paths.length; i++) { 
			String path = paths[i];
			XModelObject o = XModelImpl.getByRelativePath(model, path);
			while(o == null) {
				int d = path.indexOf("/", 1);
				if(d < 0) break;
				path = path.substring(d);
				o = XModelImpl.getByRelativePath(model, path);
			}
			if(o != null && !set.contains(o)) {
				list.add(o);
				set.add(o);
			}
		}
		XModelObject[] js = fs.getChildren("FileSystemJar");
		for (XModelObject o: js) {
			XModelObject c = o.getChildByPath("META-INF/faces-config.xml");
			if(c != null && !set.contains(c)) {
				list.add(c);
				set.add(c);
			}
		}
	}

	public static void registerConfigFile(XModel model, String path, ConfigFilesData data) throws XModelException {
		XModelObject webxml = getWebConfig(model);
		if(webxml == null) return;
		XModelObject p = findInitParam(webxml, data.param);
		if(p == null) {
			if(!data.usesDefaultWithoutRegistration && data.defaultList != null && data.defaultList.length > 0 && webxml.getModel().getByPath(data.defaultList[0]) != null) {
				String path0 = data.defaultList[0];
				path = path0 + data.separator + path;
			}
			p = WebAppHelper.setWebAppContextParam(webxml, data.param, path);
		} else {
			String s = "" + data.separator.charAt(0);
			String v = p.getAttributeValue(PARAM_VALUE);
			if((s + v + s).indexOf(s + path + s) < 0) {
				if(v.length() > 0 && !v.endsWith(s)) v += s;
				v += path;
				model.changeObjectAttribute(p, PARAM_VALUE, v);
			}
		}
		if(webxml.isModified()) {
			XActionInvoker.invoke("SaveActions.Save", webxml, new Properties());
		}
	}
	
	public static boolean isConfigFileDefault(String path, ConfigFilesData data) {
		if(data == null || data.defaultList == null) return false;
		for (int i = 0; i < data.defaultList.length; i++) {
			if(data.defaultList[i].equals(path)) return true;
		}		
		return false;
	}

	public static boolean isConfigFileRegistered(XModel model, String path, ConfigFilesData data) {
		XModelObject webxml = getWebConfig(model);
		if(webxml == null) return false;
		XModelObject p = findInitParam(webxml, data.param);
		if(p == null) return false;
		String[] cs = getConfigFilesList(webxml, data);
		for (int i = 0; i < cs.length; i++) {
			if(cs[i].toLowerCase().endsWith(path.toLowerCase())) return true;
		}
		return false;
	}
	
	public static void unregisterConfigFile(XModel model, String path, ConfigFilesData data) throws XModelException {
		XModelObject webxml = getWebConfig(model);
		if(webxml == null) return;
		XModelObject p = findInitParam(webxml, data.param);
		if(p == null) return;
		StringBuffer sb = new StringBuffer();
		String[] cs = getConfigFilesList(webxml, data);
		for (int i = 0; i < cs.length; i++) {
			if(cs[i].toLowerCase().equals(path.toLowerCase())) continue;
			if(sb.length() > 0) sb.append(data.separator.charAt(0));
			sb.append(cs[i]);
		}
		model.changeObjectAttribute(p, PARAM_VALUE, sb.toString());
	}

	public static void registerConfigFileRename(XModel model, String oldConfigName, String newConfigName, String path, ConfigFilesData data) throws XModelException {
		XModelObject webxml = getWebConfig(model);
		if(webxml == null || "yes".equals(webxml.get("isIncorrect"))) return;
		XModelObject p = findInitParam(webxml, data.param);
		if(p == null) {
			if(data.defaultList != null &&  data.defaultList[0].endsWith("/" + oldConfigName) && path != null) {
				registerConfigFile(model, path, data);
			}
			return;
		}
		StringBuffer sb = new StringBuffer();
		String[] cs = getConfigFilesList(webxml, data);
		boolean done = false;
		for (int i = 0; i < cs.length; i++) {
			if(cs[i].endsWith("/" + oldConfigName)) {
				String n = cs[i].substring(0, cs[i].length() - oldConfigName.length()) + newConfigName;
				if(n.equals(path)) {
					cs[i] = n;
					done = true;
				}
			}
			if(sb.length() > 0) sb.append(data.separator.charAt(0));
			sb.append(cs[i]);
		}
		if(!done && data.defaultList != null &&  data.defaultList[0].endsWith("/" + oldConfigName)) {
			if(sb.length() > 0) sb.append(data.separator.charAt(0));
			sb.append(path);
		}
		model.changeObjectAttribute(p, PARAM_VALUE, sb.toString());
		if(webxml != null) XActionInvoker.invoke("SaveActions.Save", webxml, null);
	}

}
