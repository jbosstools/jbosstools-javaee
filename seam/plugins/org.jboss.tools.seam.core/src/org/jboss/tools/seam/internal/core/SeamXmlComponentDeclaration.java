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

import java.util.List;
import java.util.Properties;

import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.project.ext.IValueInfo;
import org.jboss.tools.common.model.project.ext.event.Change;
import org.jboss.tools.common.model.util.NamespaceMapping;
import org.jboss.tools.seam.core.ISeamElement;
import org.jboss.tools.seam.core.ISeamXmlComponentDeclaration;
import org.jboss.tools.seam.core.ScopeType;
import org.jboss.tools.seam.internal.core.scanner.xml.XMLScanner;
import org.w3c.dom.Element;

public class SeamXmlComponentDeclaration extends SeamPropertiesDeclaration
		implements ISeamXmlComponentDeclaration {
	
	String autoCreate = null;
	String className = null;
	String installed = null;
	String jndiName = null;
	String precedence = "20";
	String scope = null;

	boolean isClassNameGuessed = false;

	public String getAutoCreateAsString() {
		return autoCreate;
	}

	public String getClassName() {
		return className;
	}

	public boolean isClassNameGuessed() {
		return isClassNameGuessed;
	}

	public boolean getInstalledAsString() {
		return !"false".equals(installed); //$NON-NLS-1$
	}

	public String getJndiName() {
		return jndiName;
	}

	public String getPrecedence() {
		return precedence;
	}

	public ScopeType getScope() {
		if(scope == null || scope.length() == 0) return ScopeType.UNSPECIFIED;
		try {
			return ScopeType.valueOf(scope.toUpperCase());
		} catch (IllegalArgumentException e) {
			return ScopeType.UNSPECIFIED;
		}
	}

	public String getScopeAsString() {
		return scope;
	}

	public boolean isAutoCreate() {
		return "true".equals(autoCreate); //$NON-NLS-1$
	}

	public boolean isInstalled() {
		return !"false".equals(installed); //$NON-NLS-1$
	}
	
	public void setClassName(String className) {
		this.className = className;
	}
	
	public void setClassName(IValueInfo value) {
		attributes.put(CLASS, value);
		setClassName(value == null ? null : value.getValue());
	}

	public void setClassNameGuessed(boolean b) {
		isClassNameGuessed = b;
	}

	public void setAutoCreate(String autoCreate) {
		this.autoCreate = autoCreate;
	}

	public void setAutoCreate(IValueInfo value) {
		attributes.put(AUTO_CREATE, value);
		setAutoCreate(value == null ? null : value.getValue());
	}

	public void setInstalled(String installed) {
		this.installed = installed;
	}

	public void setInstalled(IValueInfo value) {
		attributes.put(INSTALLED, value);
		setInstalled(value == null ? null : value.getValue());
	}

	public void setJndiName(String jndiName) {
		this.jndiName = jndiName;
	}

	public void setJndiName(IValueInfo value) {
		attributes.put(JNDI_NAME, value);
		setJndiName(value == null ? null : value.getValue());
	}

	public void setPrecedence(String precedence) {
		this.precedence = precedence;
		if(precedence == null || precedence.length() == 0) {
			precedence = "20";
		}
	}

	public void setPrecedence(IValueInfo value) {
		attributes.put(PRECEDENCE, value);
		setPrecedence(value == null ? null : value.getValue());
	}

	public void setScope(String scope) {
		this.scope = scope;
	}

	public void setScope(IValueInfo value) {
		attributes.put(SCOPE, value);
		setScope(value == null ? null : value.getValue());
	}

	/**
	 * Merges loaded data into currently used declaration.
	 * If changes were done returns a list of changes. 
	 * @param d
	 * @return list of changes
	 */
	@Override
	public List<Change> merge(ISeamElement s) {
		List<Change> changes = super.merge(s);
		SeamXmlComponentDeclaration xd = (SeamXmlComponentDeclaration)s;
		
		if(!stringsEqual(className, xd.className)) {
			changes = Change.addChange(changes, new Change(this, CLASS, className, xd.className));
			className = xd.className;
		}
		this.isClassNameGuessed = xd.isClassNameGuessed;
		if(!stringsEqual(autoCreate, xd.autoCreate)) {
			changes = Change.addChange(changes, new Change(this, AUTO_CREATE, autoCreate, xd.autoCreate));
			autoCreate = xd.autoCreate;
		}
		if(!stringsEqual(installed, xd.installed)) {
			changes = Change.addChange(changes, new Change(this, INSTALLED, installed, xd.installed));
			installed = xd.installed;
		}
		if(!stringsEqual(jndiName, xd.jndiName)) {
			changes = Change.addChange(changes, new Change(this, JNDI_NAME, jndiName, xd.jndiName));
			jndiName = xd.jndiName;
		}
		if(!stringsEqual(precedence, xd.precedence)) {
			changes = Change.addChange(changes, new Change(this, PRECEDENCE, precedence, xd.precedence));
			precedence = xd.precedence;
		}
		if(!stringsEqual(scope, xd.scope)) {
			changes = Change.addChange(changes, new Change(this, SCOPE, scope, xd.scope));
			scope = xd.scope;
		}

		return changes;
	}

	public SeamXmlComponentDeclaration clone() throws CloneNotSupportedException {
		SeamXmlComponentDeclaration c = (SeamXmlComponentDeclaration)super.clone();
		return c;
	}

	public String getXMLClass() {
		return SeamXMLConstants.CLS_XML;
	}

	public Element toXML(Element parent, Properties context) {
		Element element = super.toXML(parent, context);
		
		if(isClassNameGuessed) {
			element.setAttribute("isClassNameGuessed", "true");
		}

		return element;
	}
	
	public void loadXML(Element element, Properties context) {
		super.loadXML(element, context);
		
		setAutoCreate(attributes.get(AUTO_CREATE));
		setInstalled(attributes.get(INSTALLED));
		setJndiName(attributes.get(JNDI_NAME));
		setPrecedence(attributes.get(PRECEDENCE));
		setScope(attributes.get(SCOPE));
		setClassName(attributes.get(CLASS));
		
		isClassNameGuessed = "true".equals(element.getAttribute("isClassNameGuessed"));
		
		if(className == null && id instanceof XModelObject) {
			XModelObject c = (XModelObject)id;
			if(c.getModelEntity().getName().equals("FileSeamComponent12")) {
				className = XMLScanner.getImpliedClassName(c, source);
				isClassNameGuessed = true;
			} else {
				XModelObject f = c;
				while(f != null && f.getFileType() != XModelObject.FILE) f = f.getParent();
				NamespaceMapping nm = NamespaceMapping.load(f);
				SeamProject sp = (SeamProject)context.get("seamProject");
				className = XMLScanner.getDefaultClassName(c, nm, sp);
				isClassNameGuessed = true;
			}
		}

	}

}
