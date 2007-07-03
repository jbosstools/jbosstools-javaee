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
package org.jboss.tools.seam.internal.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IPath;
import org.jboss.tools.seam.core.BijectedAttributeType;
import org.jboss.tools.seam.core.IBijectedAttribute;
import org.jboss.tools.seam.core.IRole;
import org.jboss.tools.seam.core.ISeamAnnotatedFactory;
import org.jboss.tools.seam.core.ISeamComponent;
import org.jboss.tools.seam.core.ISeamComponentMethod;
import org.jboss.tools.seam.core.ISeamProperty;
import org.jboss.tools.seam.core.ISeamSource;
import org.jboss.tools.seam.core.ScopeType;
import org.jboss.tools.seam.core.SeamComponentMethodType;

public class SeamComponent implements ISeamComponent {
	protected ISeamComponent base = null;
	
	protected String name = null;
	protected String className = null;
	protected ScopeType scopeType = ScopeType.UNSPECIFIED;
	protected boolean stateful = false;
	protected boolean entity = false;
	
	protected Map<String,ISeamProperty> properties = new HashMap<String, ISeamProperty>();
	protected IPath source;
	protected ISeamSource sourceDeclaration = null;
	
	protected Set<IBijectedAttribute> bijectedAttributes = new HashSet<IBijectedAttribute>();
	protected Set<ISeamAnnotatedFactory> annotatedFactories = new HashSet<ISeamAnnotatedFactory>();
	protected Set<ISeamComponentMethod> componentMethods = new HashSet<ISeamComponentMethod>();
	protected Set<IRole> roles = new HashSet<IRole>();

	public SeamComponent () {
	}
	
	public void setBaseComponent(ISeamComponent base) {
		this.base = base;
	}
	
	public ISeamComponent getBaseComponent() {
		return base;
	}

	public Set<String> getPropertyNames() {
		return properties.keySet();
	}

	public boolean getBooleanProperty(String propertyName) {
		ISeamProperty o = getProperty(propertyName);
		Object value = o.getValue();
		if(value instanceof Boolean) return ((Boolean)value).booleanValue();
		if(value instanceof String && "true".equals(value));
		return false;
	}

	public String getStringProperty(String propertyName) {
		ISeamProperty o = getProperty(propertyName);
		Object value = o.getValue();
		return value == null ? null : value.toString();
	}

	public ISeamProperty getProperty(String propertyName) {
		return properties.get(propertyName);
	}

	public void addProperty(ISeamProperty property) {
		properties.put(property.getName(), property);
	}

	public void setStringProperty(String propertyName, String value) {
		ISeamProperty p = getProperty(propertyName);
		if(p == null) {
			p = new SeamProperty(propertyName, value);
		} else {
			p.setValue(value);
		}
		properties.put(propertyName, p);
	}

	public void setSourcePath(IPath path) {
		source = path;
	}

	/**
	 * @see org.jboss.tools.seam.core.ISeamComponent#addBijectedAttribute(org.jboss.tools.seam.core.IBijectedAttribute)
	 */
	public void addBijectedAttribute(IBijectedAttribute attribute) {
		bijectedAttributes.add(attribute);		
	}

	/**
	 * @see org.jboss.tools.seam.core.ISeamComponent#addFactory(org.jboss.tools.seam.core.ISeamAnnotatedFactory)
	 */
	public void addFactory(ISeamAnnotatedFactory factory) {
		annotatedFactories.add(factory);		
	}

	/**
	 * @see org.jboss.tools.seam.core.ISeamComponent#addMethod(org.jboss.tools.seam.core.ISeamComponentMethod)
	 */
	public void addMethod(ISeamComponentMethod method) {
		componentMethods.add(method);
	}

	/**
	 * @see org.jboss.tools.seam.core.ISeamComponent#addRole(org.jboss.tools.seam.core.IRole)
	 */
	public void addRole(IRole role) {
		roles.add(role);		
	}

	/**
	 * @see org.jboss.tools.seam.core.ISeamComponent#addSourceDeclaration(org.jboss.tools.seam.core.ISeamSource)
	 */
	public void addSourceDeclaration(ISeamSource source) {
		sourceDeclaration = source;
	}

	/**
	 * @see org.jboss.tools.seam.core.ISeamComponent#getBijectedAttributes()
	 */
	public Set<IBijectedAttribute> getBijectedAttributes() {
		return base != null ? base.getBijectedAttributes() : bijectedAttributes;
	}

	/**
	 * @see org.jboss.tools.seam.core.ISeamComponent#getBijectedAttributesByName(java.lang.String)
	 */
	public Set<IBijectedAttribute> getBijectedAttributesByName(String name) {
		Set<IBijectedAttribute> result = null;
		for(IBijectedAttribute a: getBijectedAttributes()) {
			if(name.equals(a.getName())) {
				if(result == null) result = new HashSet<IBijectedAttribute>();
				result.add(a);
			}
		}
		return result;
	}

	/**
	 * @see org.jboss.tools.seam.core.ISeamComponent#getBijectedAttributesByType(org.jboss.tools.seam.core.BijectedAttributeType)
	 */
	public Set<IBijectedAttribute> getBijectedAttributesByType(
			BijectedAttributeType type) {
		Set<IBijectedAttribute> result = null;
		for(IBijectedAttribute a: getBijectedAttributes()) {
			if(type.equals(a.getType())) {
				if(result == null) result = new HashSet<IBijectedAttribute>();
				result.add(a);
			}
		}
		return result;
	}

	/**
	 * @see org.jboss.tools.seam.core.ISeamComponent#getClassName()
	 */
	public String getClassName() {
		return className;
	}

	/**
	 * @see org.jboss.tools.seam.core.ISeamComponent#getFactories()
	 */
	public Set<ISeamAnnotatedFactory> getFactories() {
		return base != null ? base.getFactories() : annotatedFactories;
	}

	/**
	 * @see org.jboss.tools.seam.core.ISeamComponent#getMethods()
	 */
	public Set<ISeamComponentMethod> getMethods() {
		return base != null ? base.getMethods() : componentMethods;
	}

	/**
	 * @see org.jboss.tools.seam.core.ISeamComponent#getMethodsByType(org.jboss.tools.seam.core.SeamComponentMethodType)
	 */
	public Set<ISeamComponentMethod> getMethodsByType(
			SeamComponentMethodType type) {
		Set<ISeamComponentMethod> result = null;
		for(ISeamComponentMethod a: getMethods()) {
			if(type.equals(a.getType())) {
				if(result == null) result = new HashSet<ISeamComponentMethod>();
				result.add(a);
			}
		}
		return result;
	}

	/**
	 * @see org.jboss.tools.seam.core.ISeamComponent#getRoles()
	 */
	public Set<IRole> getRoles() {
		return base != null ? base.getRoles() : roles;
	}

	/**
	 * @see org.jboss.tools.seam.core.ISeamComponent#getSourceDeclarations()
	 */
	public Set<ISeamSource> getSourceDeclarations() {
		Set<ISeamSource> sources = base == null ? null : base.getSourceDeclarations();
		if(sources == null) sources = new HashSet<ISeamSource>();
		sources.add(sourceDeclaration);
		return sources;
	}

	/**
	 * @see org.jboss.tools.seam.core.ISeamComponent#isEntity()
	 */
	public boolean isEntity() {
		return entity;
	}

	/**
	 * @see org.jboss.tools.seam.core.ISeamComponent#isStateful()
	 */
	public boolean isStateful() {
		return stateful;
	}

	/**
	 * @see org.jboss.tools.seam.core.ISeamComponent#removeBijectedAttribute(org.jboss.tools.seam.core.IBijectedAttribute)
	 */
	public void removeBijectedAttribute(IBijectedAttribute attribute) {
		bijectedAttributes.remove(attribute);
	}

	/**
	 * @see org.jboss.tools.seam.core.ISeamComponent#removeFactory(org.jboss.tools.seam.core.ISeamAnnotatedFactory)
	 */
	public void removeFactory(ISeamAnnotatedFactory factory) {
		annotatedFactories.remove(factory);
	}

	/**
	 * @see org.jboss.tools.seam.core.ISeamComponent#removeMethod(org.jboss.tools.seam.core.ISeamComponentMethod)
	 */
	public void removeMethod(ISeamComponentMethod method) {
		componentMethods.remove(method);
	}

	/**
	 * @see org.jboss.tools.seam.core.ISeamComponent#removeRole(org.jboss.tools.seam.core.IRole)
	 */
	public void removeRole(IRole role) {
		roles.remove(role);
	}

	/**
	 * @see org.jboss.tools.seam.core.ISeamComponent#removeSourceDeclaration(org.jboss.tools.seam.core.ISeamSource)
	 */
	public void removeSourceDeclaration(ISeamSource source) {
		if(sourceDeclaration == source) {
			sourceDeclaration = null;
		}
	}

	/**
	 * @see org.jboss.tools.seam.core.ISeamComponent#setClassName(java.lang.String)
	 */
	public void setClassName(String className) {
		this.className = className;
	}

	/**
	 * @see org.jboss.tools.seam.core.ISeamComponent#setEntity(boolean)
	 */
	public void setEntity(boolean entity) {
		this.entity = entity;
	}

	/**
	 * @see org.jboss.tools.seam.core.ISeamComponent#setStateful(boolean)
	 */
	public void setStateful(boolean stateful) {
		this.stateful = stateful;
	}

	/**
	 * @see org.jboss.tools.seam.core.ISeamContextVariable#getName()
	 */
	public String getName() {
		if(name == null && base != null) {
			return base.getName();
		}
		return name;
	}

	/**
	 * @see org.jboss.tools.seam.core.ISeamContextVariable#getScope()
	 */
	public ScopeType getScope() {
		if(scopeType == ScopeType.UNSPECIFIED && base != null) {
			return base.getScope();
		}
		return scopeType;
	}

	/**
	 * @see org.jboss.tools.seam.core.ISeamContextVariable#setName(java.lang.String)
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @see org.jboss.tools.seam.core.ISeamContextVariable#setScope(org.jboss.tools.seam.core.ScopeType)
	 */
	public void setScope(ScopeType type) {
		this.scopeType = type;
	}

	/**
	 * @see org.jboss.tools.seam.core.ISeamComponent#getProperties(java.lang.String)
	 */
	public List<ISeamProperty> getProperties(String propertyName) {
		List<ISeamProperty> list = new ArrayList<ISeamProperty>();
		ISeamProperty p = properties.get(propertyName);
		if(p != null) list.add(p);
		return list;
	}

	/**
	 * @see org.jboss.tools.seam.core.ISeamComponent#getProperties()
	 */
	public Collection<ISeamProperty> getProperties() {
		return properties.values();
	}

	/**
	 * @see org.jboss.tools.seam.core.ISeamComponent#removeProperty(org.jboss.tools.seam.core.ISeamProperty)
	 */
	public void removeProperty(ISeamProperty property) {
		properties.remove(property.getName());		
	}

}
