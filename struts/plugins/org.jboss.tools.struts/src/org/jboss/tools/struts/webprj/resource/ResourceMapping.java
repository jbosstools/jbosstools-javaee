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
package org.jboss.tools.struts.webprj.resource;

import java.util.*;
import org.jboss.tools.common.model.*;
import org.jboss.tools.common.model.filesystems.*;
import org.jboss.tools.common.model.impl.XModelImpl;
import org.jboss.tools.common.model.util.XModelObjectUtil;
import org.jboss.tools.struts.webprj.model.helpers.*;

public class ResourceMapping {
	WebModulesHelper wh = null;
	Map<String,ConfigInfo> infos = new HashMap<String,ConfigInfo>();

	public ResourceMapping(WebModulesHelper wh) {
		this.wh = wh;
	}
	
	public void revalidate() {
		Set<String> modules = wh.getModules();
		if(isUpToDate(modules)) return;
//		boolean created = 
			createInfos(modules);
	}
	
	private boolean isUpToDate(Set modules) {
		if(modules.size() != infos.size()) return false;
		Iterator it = modules.iterator();
		boolean result = true;
		while(it.hasNext()) {
			Object key = it.next();
			ConfigInfo i = (ConfigInfo)infos.get(key);
			if(i == null) {
				result = false; 
			} else if(!i.isUpToDate()) {
				result = false;
				infos.remove(key);
			} 
		}
		return result;
	}
	
	private boolean createInfos(Set<String> modules) {
		boolean result = false;
		String[] ms = modules.toArray(new String[0]);
		for (int i = 0; i < ms.length; i++) {
			ConfigInfo ci = (ConfigInfo)infos.get(ms[i]);
			if(ci != null && ci.isUpToDate()) continue;
			XModelObject cg = wh.getConfigForModule(wh.getModel(), ms[i]);
			if(cg == null) continue;
			ci = new ConfigInfo(cg);
			infos.put(ms[i], ci);
			result = true;
		}
		return result;		
	}
	
	public Set<String> getResources(XModelObject o) {
		while(o != null && o.getFileType() != XFileObject.FILE) o = o.getParent();
		Set<String> result = new HashSet<String>();
		if(o == null) return result;
		String path = o.getPath();
		ConfigInfo[] cs = (ConfigInfo[])infos.values().toArray(new ConfigInfo[0]);
		for (int i = 0; i < cs.length; i++) {
			if(cs[i].validators.data.contains(path)) result.addAll(cs[i].resources.data);
		}
		return result;
		
	}
	
}

class ConfigInfo {
	XModelObject cg = null;
	long timeStamp = -1;
	ValidatorsInfo validators = new ValidatorsInfo();
	ResourcesInfo resources = new ResourcesInfo();

	public ConfigInfo(XModelObject cg) {
		this.cg = cg;
		timeStamp = cg.getTimeStamp(); 		
		validators.setFolder(cg.getChildByPath("plug-ins"));
		resources.setFolder(cg.getChildByPath("resources"));
	}
	
	public boolean isUpToDate() {
		if(resources.folder == null) return cg.isActive() && cg.getTimeStamp() == timeStamp;
		return resources.isUpToDate() && validators.isUpToDate();
	}
	
	public void revalidate() {
		if(resources.folder == null) return;
		validators.revalidate();
		resources.revalidate();
	}
	
}

class ChildInfo {
	Set<String> data = new HashSet<String>();
	XModelObject folder = null;
	long timeStamp = -1;
	
	public void setFolder(XModelObject folder) {
		this.folder = folder;
		if(folder != null) revalidate();
	}
	
	public boolean isUpToDate() {
		return (folder == null) || (folder.isActive() && folder.getTimeStamp() == timeStamp);
	}
	
	public void revalidate() {}
	
}

class ResourcesInfo extends ChildInfo {
	public void revalidate() {
		if(isUpToDate()) return;
		data.clear();
		XModelObject[] cs = folder.getChildren();
		for (int i = 0; i < cs.length; i++) { 
			data.add(cs[i].getAttributeValue("parameter"));
		}
	}
	
}

class ValidatorsInfo extends ChildInfo {
	public void revalidate() {
		if(isUpToDate()) return;
		data.clear();
		populateData(findPathNames(findValidatorPlugin()));
	}
	
	private XModelObject findValidatorPlugin() {
		if(folder == null) return null;
		XModelObject[] cs = folder.getChildren();
		for (int i = 0; i < cs.length; i++) 
			if("org.apache.struts.validator.ValidatorPlugIn".equals(cs[i].getAttributeValue("className"))) return cs[i];
		return null;		
	}
	
	private XModelObject findPathNames(XModelObject vp) {
		if(vp == null) return null;
		XModelObject[] cs = vp.getChildren();
		for (int i = 0; i < cs.length; i++) 
			if("pathnames".equals(cs[i].getAttributeValue("property"))) return cs[i]; 
		return null;
	}
	
	private void populateData(XModelObject pathnames) {
		if(pathnames == null) return;
		String[] l = XModelObjectUtil.asStringArray(pathnames.getAttributeValue("value"));
		for (int i = 0; i < l.length; i++) {
			XModelObject o = XModelImpl.getByRelativePath(pathnames.getModel(), l[i].trim());
			if(o != null) {
				data.add(o.getPath());
			} 
		}		
	}
	
}
