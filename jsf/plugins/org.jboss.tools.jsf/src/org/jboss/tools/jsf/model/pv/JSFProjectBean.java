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
package org.jboss.tools.jsf.model.pv;

import java.util.*;

import org.eclipse.jdt.core.*;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.impl.RegularObjectImpl;
import org.jboss.tools.common.model.util.EclipseJavaUtil;
import org.jboss.tools.common.util.BeanUtil;
import org.jboss.tools.jsf.JSFModelPlugin;

public class JSFProjectBean extends RegularObjectImpl {
	private static final long serialVersionUID = 3044316361003259426L;
	protected JSFProjectBeans beans = null;
	protected XModelObject[] beanList = new XModelObject[0];
	protected IType type = null;
	protected boolean isLoading = false;
	protected boolean loaded = false;
	
	public String getPresentationString() {
		String s = "" + getAttributeValue("class name");
		int d = s.lastIndexOf('.');
		if(d >= 0) s = s.substring(d + 1);
		return s;
	}
	
    public boolean isAttributeEditable(String name) {
        return false;
    }

    public void setBeans(JSFProjectBeans beans) {
		this.beans = beans;
	}
    
    void cleanBeans() {
    	beanList = new XModelObject[0];
    }
	
	void addBean(XModelObject bean) {
		XModelObject[] l = new XModelObject[beanList.length + 1];
		System.arraycopy(beanList, 0, l, 0, beanList.length);
		l[beanList.length] = bean;
		beanList = l;
	}
	
	public XModelObject[] getBeanList() {
		return beanList;
	}
	
	public void setType(IType type) {
		this.type = type;
		if(loaded) {
			try {
				update();
			} catch (JavaModelException e) {
				JSFModelPlugin.getPluginLog().logError(e);
			}
		}
	}

	public IType getType() {
		return type;
	}
	
	protected void loadChildren() {
		if(loaded) return;
		loaded = true;
		isLoading = true;
		try {
			update();
		} catch (JavaModelException e) {
			JSFModelPlugin.getPluginLog().logError(e);
		}
		isLoading = false;
	}
	
	public boolean hasChildren() {
		if(type == null) return false;
		if(!loaded) return true;
		return super.hasChildren(); 
	}

	public void update() throws JavaModelException {
		if(!loaded) return;
		Map<String,XModelObject> map = new HashMap<String,XModelObject>();
		Set<String> properties = new HashSet<String>();
		XModelObject[] cs = getChildren();
		for (int i = 0; i < cs.length; i++) map.put(cs[i].getPathPart(), cs[i]);
		if(type != null) {
			IType _type = type;
			
			Set<IType> interfaces = new HashSet<IType>();
			
			while(_type != null) {
			IField[] fs = _type.getFields();
			if(fs != null) for (int i = 0; i < fs.length; i++) {
				String n = fs[i].getElementName();
				if(properties.contains(n)) continue;
				JSFProjectBeanMember c = (JSFProjectBeanMember)map.get(n);
				if(c != null && !c.getModelEntity().getName().equals("JSFProjectBeanProperty")) {
					c.removeFromParent();
					map.remove(n);
					c = null;
				}
				if(c != null) {
					map.remove(n);
					String typeName = EclipseJavaUtil.getMemberTypeAsString(fs[i]);
					c.setType(beans.getType(typeName));
					if(typeName == null) typeName = "";
					c.setAttributeValue("class name", typeName);
					c.setAttributeValue("declaring class", fs[i].getDeclaringType().getFullyQualifiedName());
				} else {
					c = createMember(n, fs[i], "JSFProjectBeanProperty"); 
				}
				properties.add(n);
			}
			IMethod[] ms = _type.getMethods();
			if(ms != null) for (int i = 0; i < ms.length; i++) {
				if(ms[i].isConstructor()) continue;
				if(!Flags.isPublic(ms[i].getFlags()) && !_type.isInterface()) continue;				
				String n = ms[i].getElementName();
				boolean isProperty = false;
				if(BeanUtil.isGetter(ms[i]) || BeanUtil.isSetter(ms[i])) {
					String propertyName = BeanUtil.getPropertyName(n);
					if(propertyName != null) {
						n = propertyName;
						isProperty = true;
					}
				}
				String entity = (isProperty) ? "JSFProjectBeanProperty" : "JSFProjectBeanMethod";
				JSFProjectBeanMember c = (JSFProjectBeanMember)map.get(n);
				if(c != null && !c.getModelEntity().getName().equals(entity)) {
					c.removeFromParent();
					map.remove(n);
					c = null;
				}
				if(c != null) {
					map.remove(n);
					String typeName = EclipseJavaUtil.getMemberTypeAsString(ms[i]);
					c.setType(beans.getType(typeName));
					if(typeName == null) typeName = "";
					c.setAttributeValue("class name", typeName);
					c.setMember(ms[i]);
					c.setAttributeValue("declaring class", ms[i].getDeclaringType().getFullyQualifiedName());
				} else if(!properties.contains(n)) {
					c = createMember(n, ms[i], entity);
				}
				if(isProperty) {
					properties.add(n);
				} else {
					//add it anyway
					properties.add(n);
				}
			}

				String[] is = _type.getSuperInterfaceNames();
				for (int i = 0; i < is.length; i++) {
					String in = EclipseJavaUtil.resolveType(_type, is[i]);
					if(in != null && in.length() > 0) {
						IType it = beans.getType(in);
						if(it != null) interfaces.add(it);
					}
				}

				String sc = _type.getSuperclassName();
				if(sc == null || sc.length() == 0 || "java.lang.Object".equals(sc)) break;
				sc = EclipseJavaUtil.resolveType(_type, sc);
				if(sc == null || sc.length() == 0 || "java.lang.Object".equals(sc)) break;
				_type = beans.getType(sc);

			}

			Set<IType> allInterfaces = new HashSet<IType>();
			
			while(!interfaces.isEmpty()) {
				allInterfaces.addAll(interfaces);
				Set<IType> interfaces2 = new HashSet<IType>();
			
				for (IType t : interfaces) {
					IField[] fs = t.getFields();
					if (fs != null)	for (int i = 0; i < fs.length; i++) {
						String n = fs[i].getElementName();
						if (properties.contains(n)) continue;
						JSFProjectBeanMember c = (JSFProjectBeanMember) map.get(n);
						if (c != null && !c.getModelEntity().getName().equals("JSFProjectBeanProperty")) {
							c.removeFromParent();
							map.remove(n);
							c = null;
						}
						if (c != null) {
							map.remove(n);
							String typeName = EclipseJavaUtil.getMemberTypeAsString(fs[i]);
							c.setType(beans.getType(typeName));
							if (typeName == null) typeName = "";
							c.setAttributeValue("class name", typeName);
							c.setAttributeValue("declaring class", fs[i].getDeclaringType().getFullyQualifiedName());
						} else {
							c = createMember(n, fs[i], "JSFProjectBeanProperty");
						}
						properties.add(n);
					}

					String[] is = t.getSuperInterfaceNames();
					for (int i = 0; i < is.length; i++) {
						String in = EclipseJavaUtil.resolveType(_type, is[i]);
						if(in != null && in.length() > 0) {
							IType it = beans.getType(in);
							if(it != null && !allInterfaces.contains(it)) interfaces2.add(it);
						}
					}
				}
				interfaces = interfaces2;			
			}
		} else if(beanList.length > 0) {
			XModelObject[] ps = beanList[0].getChildren();
			for (int i = 0; i < ps.length; i++) {
				String n = ps[i].getPathPart();
				JSFProjectBeanMember c = (JSFProjectBeanMember)map.get(n);
				if(c != null) {
					map.remove(n);
				} else {
					c = (JSFProjectBeanMember)getModel().createModelObject("JSFProjectBeanProperty", null);
					c.setAttributeValue("name", n);
					c.setBeans(beans);
					c.setType(null);
					addChild(c);
				}
			}
		}
		for(XModelObject o: map.values()) {
			o.removeFromParent();
		}
	}
	
	JSFProjectBeanMember createMember(String n, IMember member, String entity) {
		JSFProjectBeanMember c = (JSFProjectBeanMember)getModel().createModelObject(entity, null);
		c.setAttributeValue("name", n);
		c.setBeans(beans);
		String className = EclipseJavaUtil.getMemberTypeAsString(member);
		c.setAttributeValue("class name", (className == null) ? "" : className);
		c.setAttributeValue("declaring class", member.getDeclaringType().getFullyQualifiedName());
		c.setType(beans.getType(className));
		c.setMember(member);
		if(isLoading) addChild_0(c); else addChild(c);
		return c;
	}
	
}
