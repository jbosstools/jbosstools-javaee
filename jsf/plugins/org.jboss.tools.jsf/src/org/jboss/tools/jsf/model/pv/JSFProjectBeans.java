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
import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.ElementChangedEvent;
import org.eclipse.jdt.core.IElementChangedListener;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.swt.widgets.Display;

import org.jboss.tools.common.meta.XModelEntity;
import org.jboss.tools.common.model.*;
import org.jboss.tools.common.model.event.XModelTreeEvent;
import org.jboss.tools.common.model.impl.RegularObjectImpl;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.jsf.model.JSFConstants;
import org.jboss.tools.jst.web.model.pv.WebProjectNode;

public class JSFProjectBeans extends RegularObjectImpl implements WebProjectNode {
	private static final long serialVersionUID = 2682624545623269421L;
	private Map<String,IType> types = new HashMap<String,IType>();
	protected boolean isLoading = false;
	protected boolean valid = false;

	public void invalidate() {
		if(!valid || isLoading) return;
		valid = false;
		fireStructureChanged(XModelTreeEvent.STRUCTURE_CHANGED, this);
	}

	public XModelObject[] getTreeChildren() {
		if(isLoading || valid) return getChildren();
		isLoading = true;
		valid = true;
		try {
			updateListener();
			XModelObject[] cs = getChildren();
			Map<String,XModelObject> map = new HashMap<String,XModelObject>();
			Map<String,JSFProjectBean> classes = new HashMap<String,JSFProjectBean>(); //include each class once
			for (int i = 0; i < cs.length; i++) map.put(cs[i].getPathPart(), cs[i]);
			List<XModelObject> list = JSFProjectConfiguration.getConfiguration(getModel());
			XModelObject[] fcs = (XModelObject[])list.toArray(new XModelObject[0]);
			for (int i = 0; i < fcs.length; i++) {
				process(fcs[i], BeanConstants.MANAGED_BEAN_CONSTANTS, map, classes);
				process(fcs[i], BeanConstants.REFERENCED_BEAN_CONSTANTS, map, classes);
			}
			
			for(XModelObject o: map.values()) {
				o.removeFromParent();
			}
		} finally {
			isLoading = false;
		}
		return getChildren();
	}
	
	private void process(XModelObject fcg, BeanConstants constants,
			Map map, Map<String,JSFProjectBean> classes) {
		XModelObject mb = fcg.getChildByPath(constants.folder);
		if(mb != null) process(mb.getChildren(), constants, map, classes);
	}
	
	private void process(XModelObject[] bs, BeanConstants constants,
			Map map, Map<String,JSFProjectBean> classes) {
		for (int j = 0; j < bs.length; j++) {
			String bn = bs[j].getAttributeValue(constants.nameAttribute);
			String cn = bs[j].getAttributeValue(constants.classAttribute);
			if(!acceptClass(cn)) continue;
			if(classes.containsKey(cn)) {
				JSFProjectBean b = classes.get(cn);
				if(b != null) b.addBean(bs[j]);
				continue;
			}
			JSFProjectBean b = (JSFProjectBean)map.get(bn);
			IType type = getType(cn);
			if(b == null) {
				b = (JSFProjectBean)getModel().createModelObject("JSFProjectBean", null);
				b.setAttributeValue("name", bn);
				b.setBeans(this);
				addChild(b);
			} else {
				map.remove(bn);
				b.cleanBeans();
			}
			b.setAttributeValue("class name", cn);
			b.addBean(bs[j]);
			b.setType(type);
			classes.put(cn, b);
		}
	}
	
	static String primitive = "!int!char!boolean!short!double!long!void!byte!float!";
	
	private boolean acceptClass(String classname) {
		if(classname == null) return false;
		if(classname.indexOf('.') < 0 && primitive.indexOf("!" + classname + "!") >= 0) return false;
		if(classname.indexOf('[') >= 0) {
			// We refuse to process arrays. Is this correct?
			return false;
		}
		return !classname.startsWith("java.") && !classname.startsWith("javax.");
	}
	
	public IType getType(String typename) {
		if(typename == null || typename.length() == 0 || !acceptClass(typename)) return null;
		IType type = (IType)types.get(typename);
		if(type != null && type.exists()) return type;
		if(type != null) types.remove(typename);
		IProject project = (IProject)getModel().getProperties().get("project");
		if(project == null) return null;
		type = EclipseResourceUtil.getValidType(project, typename);
		if(type != null) types.put(typename, type);
		return type;
	}
	
	public XModelObject getTreeParent(XModelObject object) {
		if(isChild(object)) return this;
		return null;
	}

	public boolean isChild(XModelObject object) {
		return object != null && object.getParent() == this;
	}
	
	private void updateListener() {
		if(listener != null) return;
		IProject p = (IProject)getModel().getProperties().get("project");
		if(p == null) return;
		listener = new JavaElementChangedListener();
		JavaCore.addElementChangedListener(listener);
	}
	
	JavaElementChangedListener listener = null;

	private class JavaElementChangedListener implements IElementChangedListener {

		public void elementChanged(ElementChangedEvent event) {
			if(!isActive()) {
				JavaCore.removeElementChangedListener(this);
			} else {
				lazyInvalidate();
			}
		}
	}

	private void lazyInvalidate() {
		if(!valid || isLoading) return;
		valid = false;
		Display.getDefault().asyncExec(new XJob.XRunnable() {
			public void run() {
				fireStructureChanged(XModelTreeEvent.STRUCTURE_CHANGED, this);
			}
			public String getId() {
				return "JSF Project Beans Update - " + XModelConstants.getWorkspace(getModel());
			}
		});
	}

}
class BeanConstants {
	static final BeanConstants MANAGED_BEAN_CONSTANTS = new BeanConstants(JSFConstants.FOLDER_MANAGED_BEANS, "managed-bean-name", "managed-bean-class");
	static final BeanConstants REFERENCED_BEAN_CONSTANTS = new BeanConstants(JSFConstants.FOLDER_REFENCED_BEANS, "referenced-bean-name", "referenced-bean-class");

	String folder;
	String nameAttribute;
	String classAttribute;
	BeanConstants(String folder, String nameAttribute, String classAttribute) {
		this.folder = folder;
		this.nameAttribute = nameAttribute;
		this.classAttribute = classAttribute;
	}
	public static BeanConstants getConstants(XModelObject bean) {
		if(bean == null) return null;
		XModelEntity entity = bean.getModelEntity();
		if(entity.getAttribute(MANAGED_BEAN_CONSTANTS.nameAttribute) != null) return MANAGED_BEAN_CONSTANTS;
		if(entity.getAttribute(REFERENCED_BEAN_CONSTANTS.nameAttribute) != null) return REFERENCED_BEAN_CONSTANTS;
		return null;
	}
}
