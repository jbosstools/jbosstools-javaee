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
package org.jboss.tools.jsf.model.handlers.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.osgi.util.NLS;
import org.jboss.tools.common.java.generation.JavaPropertyGenerator;
import org.jboss.tools.common.model.ServiceDialog;
import org.jboss.tools.common.model.XModelException;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.util.EclipseJavaUtil;
import org.jboss.tools.jsf.messages.JSFUIMessages;
import org.jboss.tools.jsf.model.helpers.bean.BeanHelper;

public class AddManagedBeanPropertiesContext {
	IType type = null;
	Map<String,IJavaElement> properties = null;
	boolean[] enablement = new boolean[0];
	String[] names = new String[0];
	String[] values = new String[0];

	public void setType(IType type) {
		if(this.type == type) return;
		this.type = type;
		properties = new HashMap<String,IJavaElement>();
		try {
			properties = BeanHelper.getJavaProperties(type);
		} catch (JavaModelException e) {
			//ignore
		}
		if(properties.size() != enablement.length) {
			enablement = new boolean[properties.size()];
			names = new String[properties.size()];
			values = new String[properties.size()];
		}
		names = (String[])properties.keySet().toArray(new String[0]);
		for (int i = 0; i < names.length; i++) {
			enablement[i] = false;
			values[i] = ""; //$NON-NLS-1$
		}
	}
	
	public int size() {
		return enablement.length;
	}
	
	public boolean isEnabled(int i) {
		return i >= 0 && i < size() && enablement[i];
	}

	public void setEnabled(int i, boolean v) {
		if(i >= 0 && i < size()) enablement[i] = v;
	}
	
	public String getName(int i) {
		return names[i];
	}

	public String getValue(int i) {
		return values[i];
	}

	public void setValue(int i, String v) {
		values[i] = v;
	}
	
	public void addProperties(XModelObject bean) throws XModelException, CoreException {
		List<PropertyData> toGenerate = new ArrayList<PropertyData>();
		int applyForAll = 0;
		for (int i = 0; i < names.length; i++) {
			if(!enablement[i]) continue;
			PropertyData data = new PropertyData();
			data.name = names[i];
			XModelObject p = bean.getModel().createModelObject("JSFManagedProperty", null); //$NON-NLS-1$
			IMember m = (IMember)properties.get(names[i]);
			data.type = EclipseJavaUtil.getMemberTypeAsString(m);
			p.setAttributeValue("property-name", data.name); //$NON-NLS-1$
			p.setAttributeValue("property-class", data.type); //$NON-NLS-1$
			p.setAttributeValue("value", values[i]); //$NON-NLS-1$
			bean.addChild(p);
			if(type.isBinary() || type.isInterface()) continue;
			if(m instanceof IMethod) continue;
			boolean getter = BeanHelper.findGetter(type, names[i]) != null;
			boolean setter = BeanHelper.findSetter(type, names[i]) != null;
			if(getter && setter) continue;
			if(applyForAll == 0) {
				
				String message = getMessage(getter, setter, names[i]);
				
				ServiceDialog d = bean.getModel().getService();
				Properties cp = new Properties();
				cp.setProperty(ServiceDialog.DIALOG_MESSAGE, message);
				cp.put(ServiceDialog.CHECKED, Boolean.FALSE);
				cp.setProperty(ServiceDialog.CHECKBOX_MESSAGE, JSFUIMessages.APPLY_FOR_ALL_PROPERTIES);
				cp.put(ServiceDialog.BUTTONS, new String[]{JSFUIMessages.YES, JSFUIMessages.NO});
				boolean q = d.openConfirm(cp);
				boolean b = ((Boolean)cp.get(ServiceDialog.CHECKED)).booleanValue();
				if(b) applyForAll = (q) ? 1 : -1;
				if(q) {
					data.getter = !getter;
					data.setter = !setter;
					toGenerate.add(data);
				}
			} else if(applyForAll > 0) {
				data.getter = !getter;
				data.setter = !setter;
				toGenerate.add(data);
			}
		}
		PropertyData[] datas = toGenerate.toArray(new PropertyData[0]);
		JavaPropertyGenerator g = new JavaPropertyGenerator();
		g.setOwner(type);
		for (int i = 0; i < datas.length; i++) {
			g.generate(datas[i].name, datas[i].type, "public", false, datas[i].getter, datas[i].setter); //$NON-NLS-1$
		}
	}
	
	private String getMessage(boolean getter, boolean setter, String name){
				
		if(!getter && !setter) {
			return NLS.bind(JSFUIMessages.ADD_GETTER_SETTER_FOR_PROPERTY, names);
		}
		if (!getter && setter){
			return NLS.bind(JSFUIMessages.ADD_GETTER_FOR_PROPERTY, names);
		}
		return NLS.bind(JSFUIMessages.ADD_SETTER_FOR_PROPERTY, names);
	}
	
	class PropertyData {
		String name;
		String type;
		boolean getter;
		boolean setter;
	}

}
