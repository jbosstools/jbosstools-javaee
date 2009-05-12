 /*******************************************************************************
  * Copyright (c) 2007 Red Hat, Inc.
  * Distributed under license by Red Hat, Inc. All rights reserved.
  * This program is made available under the terms of the
  * Eclipse Public License v1.0 which accompanies this distribution,
  * and is available at http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributor:
  *     Red Hat, Inc. - initial API and implementation
  ******************************************************************************/
package org.jboss.tools.seam.internal.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.seam.core.ISeamComponent;
import org.jboss.tools.seam.core.ISeamElement;
import org.jboss.tools.seam.core.ISeamProject;
import org.jboss.tools.seam.core.ISeamProperty;
import org.jboss.tools.seam.core.ISeamXmlComponentDeclaration;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.core.event.ISeamValue;
import org.jboss.tools.seam.core.event.ISeamValueList;
import org.jboss.tools.seam.core.event.ISeamValueString;

/**
 * @author Viacheslav Kabanovich
 */
public class SeamMessagesLoader {
	static Map<String, IResource> EMPTY = new HashMap<String, IResource>();
	Map<String, IResource> resources = EMPTY; // Maps the bundleName to the IResource
	Map<String, Long> timestamps = null;
	Set<String> keys = null;
	
	ISeamElement object;
	
	/**
	 *  In Seam 1.2 "org.jboss.seam.core.resourceBundle"
	 *  In Seam 2.0 "org.jboss.seam.core.resourceLoader"
	 */
	String resourceComponent;
	
	public SeamMessagesLoader(ISeamElement object, String resourceComponent) {
		this.object = object;
		this.resourceComponent = resourceComponent;
	}

	public void revalidate() {
		Map<String, IResource> resources2 = getResources();
		if(changed(resources2)) {
			timestamps = new HashMap<String, Long>();
			for (String n : resources2.keySet()) {
				IResource r = resources2.get(n);
				timestamps.put(n, r.getLocalTimeStamp());
			}
			resources = resources2;
			keys = null;
		}
	}
	
	private boolean changed(Map<String, IResource> resources2) {
		if(resources == resources2) return false;
		if(resources2.size() != resources.size()) return true;
		if(timestamps == null) return true;
		for (String s: resources2.keySet()) {
			IResource r2 = resources2.get(s);
			IResource r1 = resources.get(s);
			if(r1 == null || !r2.equals(r1)) return true;
			long l1 = r1.getLocalTimeStamp();
			Long l2 = timestamps.get(s);
			if(l2 == null || l1 != l2.longValue()) return true;
		}
		
		return false;		
	}	
	
	public Map<String, IResource> getResources() {
		ISeamProject p = object.getSeamProject();
		if(p == null) return EMPTY;
		IResource[] srcs = EclipseResourceUtil.getJavaSourceRoots(p.getProject());
		ISeamComponent c = p.getComponent(resourceComponent);
		if(c == null) return EMPTY;
		List<String> names = new ArrayList<String>();
		Set<ISeamXmlComponentDeclaration> ds = c.getXmlDeclarations();
		for (ISeamXmlComponentDeclaration d: ds) {
			ISeamProperty property = d.getProperty("bundleNames");
			if(property == null) continue;
			ISeamValue v = property.getValue();
			if(v == null) continue;
			if(v instanceof ISeamValueList) {
				List<ISeamValueString> vs = ((ISeamValueList)v).getValues();
				for (ISeamValueString s: vs) {
					if(s.getValue() == null) continue;
					String b = s.getValue().getValue();
					names.add(b);
				}
			} else if(v instanceof ISeamValueString) {
				ISeamValueString s = (ISeamValueString)v;
				if(s.getValue() == null) continue;
				String b = s.getValue().getValue();
				if(b == null || b.length() == 0) continue;
				String[] bi = b.split(",");
				for (int i = 0; i < bi.length; i++) {
					names.add(bi[i].trim());
				}
			}
		}
		if(ds.isEmpty()) {
			names.add("messages");
		}
		return getResources(names, srcs);
	}

	public Collection<ISeamProperty> getProperties() {
		throw new IllegalStateException("Not implemented");
	}

	public Collection<String> getPropertyNames() {
		if(keys == null) {
			keys = new HashSet<String>();
			for (IResource r: resources.values()) {
				IPath p = r.getLocation();
				if(p == null) continue;
				File f = p.toFile();
				Properties properties = new Properties();
				FileInputStream is = null;
				try {
					is = new FileInputStream(f); 
					properties.load(is);
				} catch (IOException e) {
					//ignore 
					//TODO keep error for validation
				} finally {
					if(is!=null) {
						try {
							is.close();
						} catch (IOException e) {
							// ignore
						}
					}
				}
				Set<?> s = properties.keySet();
				for (Object o : s) keys.add((String)o);
			}
		}
		return keys;
	}
	
	private Map<String, IResource> getResources(List<String> names, IResource[] srcs) {
		Map<String, IResource> rs = new HashMap<String, IResource>();
		for (String name: names) {
			String n = name.replace('.', '/');
			int k = n.lastIndexOf('/');
			String p = k < 0 ? null : n.substring(0, k);
			if(k >= 0) n = n.substring(k + 1);
			IResource result = null;
			for (int j = 0; j < srcs.length && result == null; j++) {
				result = find(p, n, srcs[j]);
			}
			if(result != null) {
				rs.put(name, result);
			}
		}
		return rs;
	}
	
	private IResource find(String pack, String name, IResource src) {
		if(!(src instanceof IContainer)) return null;
		IContainer c = (IContainer)src;
		if(pack != null) {
			c = c.getFolder(new Path(pack));
		}
		if(c == null || !c.exists()) return null;
		String suffix = ".properties";
		String name_ = name + "_";
		String name_p = name + suffix;
		IResource result = null;
		try {
			IResource[] rs = c.members();
			for (int i = 0; i < rs.length; i++) {
				String n = rs[i].getName();
				if(name_p.equals(n)) return rs[i];
				if(result == null && n.startsWith(name_) && n.endsWith(suffix)) {
					result = rs[i];
				}
			}
		} catch (CoreException e) {
			SeamCorePlugin.getPluginLog().logError(e);
		}
		
		return result;
	}

}
