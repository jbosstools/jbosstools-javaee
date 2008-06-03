/******************************************************************************* 
 * Copyright (c) 2007 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.seam.internal.core;

import java.lang.annotation.Inherited;
import java.util.List;
import java.util.Properties;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.xml.XMLUtilities;
import org.jboss.tools.seam.core.ISeamElement;
import org.jboss.tools.seam.core.ISeamProject;
import org.jboss.tools.seam.core.event.Change;
import org.w3c.dom.Element;

/**
 * @author Viacheslav Kabanovich
 */
public class SeamObject implements ISeamElement {
	/**
	 * Object that allows to identify this object.
	 */
	protected Object id;

	/**
	 * Path of resource where this object is declared.
	 */
	protected IPath source;

	/**
	 * Resource where this object is declared.
	 */
	protected IResource resource = null;

	/**
	 * Parent seam object in the seam model.
	 */
	protected ISeamElement parent;
	
	public SeamObject() {}

	public ISeamProject getSeamProject() {
		return parent == null ? null : parent.getSeamProject();
	}

	public Object getId() {
		return id;
	}
	
	/**
	 * Sets id for this object.
	 * For most objects it is object of Java or XML model 
	 * from which this object is loaded.
	 */
	public void setId(Object id) {
		this.id = id;
	}

	/**
	 * Sets path of resource that declares this object.
	 */
	public void setSourcePath(IPath path) {
		source = path;
	}
	
	/**
	 * Returns path of resource that declares this object.
	 * @return
	 */
	public IPath getSourcePath() {
		if(source == null && parent != null) return parent.getSourcePath();
		return source;
	}

	public IResource getResource() {
		if(resource != null) return resource;
		if(source != null) {
			resource = ResourcesPlugin.getWorkspace().getRoot().getFile(source);
		}
		if(resource == null && parent != null) {
			return parent.getResource();
		}
		return resource;
	}

	/**
	 * Returns parent object of seam model.
	 * @return
	 */
	public ISeamElement getParent() {
		return parent;
	}
	
	public void setParent(ISeamElement parent) {
		this.parent = parent;
	}
	
	protected void adopt(ISeamElement child) {
		if(child.getSeamProject() != null && child.getSeamProject() != getSeamProject()) return;
		((SeamObject)child).setParent(this);
	}

	
	/**
	 * Merges loaded object into current object.
	 * If changes were done returns a list of changes.
	 * If there are no changes, null is returned, 
	 * which prevents creating a lot of unnecessary objects. 
	 * @param f
	 * @return list of changes
	 */
	public List<Change> merge(ISeamElement s) {
		SeamObject o = (SeamObject)s;
		source = o.source;
		id = o.id;
		resource = o.resource;
		//If there are no changes, null is returned, 
		//which prevents creating a lot of unnecessary objects.
		//Subclasses and clients must check returned 
		//value for null, before using it.		
		return null;
	}
	
	public SeamObject clone() throws CloneNotSupportedException {
		SeamObject c = (SeamObject)super.clone();
		c.parent = null;
		//do not copy parent
		return c;
	}
	
	//Serializing to XML
	
	public String getXMLName() {
		return "object";
	}
	
	public String getXMLClass() {
		return null;
	}
	
	public Element toXML(Element parent, Properties context) {
		Element element = XMLUtilities.createElement(parent, getXMLName());
		if(getXMLClass() != null) {
			element.setAttribute(SeamXMLConstants.ATTR_CLASS, getXMLClass());
		}
		if(source != null && !source.equals(context.get(SeamXMLConstants.ATTR_PATH))) {
			element.setAttribute(SeamXMLConstants.ATTR_PATH, source.toString());
		}
		if(id != null) {
			if(id instanceof String) {
				Element eid = XMLUtilities.createElement(element, SeamXMLConstants.TAG_ID);
				eid.setAttribute(SeamXMLConstants.ATTR_CLASS, SeamXMLConstants.CLS_STRING);
				eid.setAttribute(SeamXMLConstants.ATTR_VALUE, id.toString());
			} else if(id instanceof IType) {
				SeamXMLHelper.saveType(element, ((IType)id), SeamXMLConstants.TAG_ID, context);
			} else if(id instanceof IField) {
				SeamXMLHelper.saveField(element, ((IField)id), SeamXMLConstants.TAG_ID, context);
			} else if(id instanceof IMethod) {
				SeamXMLHelper.saveMethod(element, ((IMethod)id), SeamXMLConstants.TAG_ID, context);
			} else if(id instanceof XModelObject) {
				XModelObject o = (XModelObject)id;
				SeamXMLHelper.saveModelObject(element, o, SeamXMLConstants.TAG_ID, context);
			}
		}
		return element;
	}

	public void loadXML(Element element, Properties context) {
		String s = element.getAttribute(SeamXMLConstants.ATTR_PATH);
		if(s != null && s.length() > 0) {
			source = new Path(s);
		} else {
			source = (IPath)context.get(SeamXMLConstants.ATTR_PATH);
		}
		Element e_id = XMLUtilities.getUniqueChild(element, SeamXMLConstants.TAG_ID);
		if(e_id != null) {
			String cls = e_id.getAttribute(SeamXMLConstants.ATTR_CLASS);
			if(SeamXMLConstants.CLS_STRING.equals(cls)) {
				id = e_id.getAttribute("string");
			} else if(SeamXMLConstants.CLS_TYPE.equals(cls)) {
				id = SeamXMLHelper.loadType(e_id, context);
			} else if(SeamXMLConstants.CLS_FIELD.equals(cls)) {
				id = SeamXMLHelper.loadField(e_id, context);
			} else if(SeamXMLConstants.CLS_METHOD.equals(cls)) {
				id = SeamXMLHelper.loadMethod(e_id, context);
			} else if(SeamXMLConstants.CLS_MODEL_OBJECT.equals(cls)) {
				id = SeamXMLHelper.loadModelObject(e_id, context);
			}
		}
	}

	public Object getAdapter(Class cls) {
		return null;
	}
}
