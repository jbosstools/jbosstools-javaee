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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.XModelObjectConstants;
import org.jboss.tools.common.model.filesystems.impl.FileAnyImpl;
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
	static class MessageBundle {
		String bundle;
		List<XModelObject> files = new ArrayList<XModelObject>();
		 MessageBundle(String bundle, List<XModelObject> files) {
			 this.bundle = bundle;
			 this.files = files;
		 }
	}
	static List<MessageBundle> EMPTY = new ArrayList<MessageBundle>();
	List<MessageBundle> resources = EMPTY; // Maps the bundleName to the IResource
	Map<String, Long> timestamps = null;
	Set<String> keys = null;
	Map<String, List<XModelObject>> properties = null;
	
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
		List<MessageBundle> resources2 = getResources();
		if(changed(resources2)) {
			timestamps = new HashMap<String, Long>();
			for (int i = 0; i < resources2.size(); i++) {
				List<XModelObject> r = resources2.get(i).files;
				long q = 0;
				for (XModelObject o: r) q += o.getTimeStamp();
				timestamps.put(resources2.get(i).bundle, q);
			}
			resources = resources2;
			keys = null;
			properties = null;
		}
	}
	
	private boolean changed(List<MessageBundle> resources2) {
		if(resources == resources2) return false;
		if(resources2.size() != resources.size()) return true;
		if(timestamps == null) return true;
		for (int i = 0; i < resources2.size(); i++) {
			List<XModelObject> r2 = resources2.get(i).files;
			List<XModelObject> r1 = resources.get(i).files;
			if(r1 == null || r2.size() == r1.size()) return true;
			long l1 = 0;
			for (XModelObject o: r1) l1 += o.getTimeStamp();
			Long l2 = timestamps.get(resources2.get(i).bundle);
			if(l2 == null || l1 != l2.longValue()) return true;
		}
		
		return false;		
	}	
	
	public List<MessageBundle> getResources() {
		ISeamProject p = object.getSeamProject();
		if(p == null) return EMPTY;
		IResource[] srcs = EclipseResourceUtil.getJavaSourceRoots(p.getProject());
		List<String> names = getNames();
		return names.isEmpty() ? EMPTY : getResources(names, srcs);
	}

	private List<String> getNames() {
		ISeamProject p = object.getSeamProject();
		if(p == null) {
			return new ArrayList<String>();
		}
		ISeamComponent c = p.getComponent(resourceComponent);
		if(c == null) {
			return new ArrayList<String>();
		}
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
		return names;
	}

	public Collection<ISeamProperty> getProperties() {
		throw new IllegalStateException("Not implemented");
	}

	public Map<String, List<XModelObject>> getPropertiesMap() {
		if(properties == null) {
			properties = new HashMap<String, List<XModelObject>>();
			for (int i = 0; i < resources.size(); i++) {
				List<XModelObject> list = resources.get(i).files;
				for (XModelObject o: list) {
					XModelObject[] ps = o.getChildren();
					for (XModelObject p: ps) {
						String propertyName = p.getAttributeValue(XModelObjectConstants.ATTR_NAME);
						List<XModelObject> vs = properties.get(propertyName);
						if(vs == null) {
							vs = new ArrayList<XModelObject>();
							properties.put(propertyName, vs);
						}
						vs.add(p);
					}
				}
			}
		}
		return properties;
	}
	
	private List<MessageBundle> getResources(List<String> names, IResource[] srcs) {
		List<MessageBundle> rs = new ArrayList<MessageBundle>();
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
				List<XModelObject> l = new ArrayList<XModelObject>();
				XModelObject o = EclipseResourceUtil.createObjectForResource(result);
				if(o != null) {
					XModelObject[] os = o.getParent().getChildren();
					String dn = o.getAttributeValue(XModelObjectConstants.ATTR_NAME);
					for (XModelObject c: os) {
						String fileName = FileAnyImpl.toFileName(c);
						if(fileName.endsWith(".properties") && (c == o || fileName.startsWith(dn + "_"))) {
							l.add(c);
						}
					}
					rs.add(new MessageBundle(name, l));
				}
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
