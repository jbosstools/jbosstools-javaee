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

import java.util.HashMap;
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
	protected Map<String,ISeamProperty> properties = new HashMap<String, ISeamProperty>();
	protected IPath source;

	public SeamComponent () {
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

	public void setSource(IPath path) {
		source = path;
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.seam.core.ISeamComponent#addBijectedAttribute(org.jboss.tools.seam.core.IBijectedAttribute)
	 */
	public void addBijectedAttribute(IBijectedAttribute attribute) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.seam.core.ISeamComponent#addFactory(org.jboss.tools.seam.core.ISeamAnnotatedFactory)
	 */
	public void addFactory(ISeamAnnotatedFactory factory) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.seam.core.ISeamComponent#addMethod(org.jboss.tools.seam.core.ISeamComponentMethod)
	 */
	public void addMethod(ISeamComponentMethod method) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.seam.core.ISeamComponent#addRole(org.jboss.tools.seam.core.IRole)
	 */
	public void addRole(IRole role) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.seam.core.ISeamComponent#addSourceDeclaration(org.jboss.tools.seam.core.ISeamSource)
	 */
	public void addSourceDeclaration(ISeamSource source) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.seam.core.ISeamComponent#getBijectedAttributes()
	 */
	public Set<IBijectedAttribute> getBijectedAttributes() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.seam.core.ISeamComponent#getBijectedAttributesByName(java.lang.String)
	 */
	public Set<IBijectedAttribute> getBijectedAttributesByName(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.seam.core.ISeamComponent#getBijectedAttributesByType(org.jboss.tools.seam.core.BijectedAttributeType)
	 */
	public Set<IBijectedAttribute> getBijectedAttributesByType(
			BijectedAttributeType type) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.seam.core.ISeamComponent#getClassName()
	 */
	public String getClassName() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.seam.core.ISeamComponent#getFactories()
	 */
	public Set<ISeamAnnotatedFactory> getFactories() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.seam.core.ISeamComponent#getMethods()
	 */
	public Set<ISeamComponentMethod> getMethods() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.seam.core.ISeamComponent#getMethodsByType(org.jboss.tools.seam.core.SeamComponentMethodType)
	 */
	public Set<ISeamComponentMethod> getMethodsByType(
			SeamComponentMethodType type) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.seam.core.ISeamComponent#getRoles()
	 */
	public Set<IRole> getRoles() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.seam.core.ISeamComponent#getSourceDeclarations()
	 */
	public Set<ISeamSource> getSourceDeclarations() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.seam.core.ISeamComponent#isEntity()
	 */
	public boolean isEntity() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.seam.core.ISeamComponent#isStateful()
	 */
	public boolean isStateful() {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.seam.core.ISeamComponent#removeBijectedAttribute(org.jboss.tools.seam.core.IBijectedAttribute)
	 */
	public void removeBijectedAttribute(IBijectedAttribute attribute) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.seam.core.ISeamComponent#removeFactory(org.jboss.tools.seam.core.ISeamAnnotatedFactory)
	 */
	public void removeFactory(ISeamAnnotatedFactory factory) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.seam.core.ISeamComponent#removeMethod(org.jboss.tools.seam.core.ISeamComponentMethod)
	 */
	public void removeMethod(ISeamComponentMethod method) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.seam.core.ISeamComponent#removeRole(org.jboss.tools.seam.core.IRole)
	 */
	public void removeRole(IRole role) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.seam.core.ISeamComponent#removeSourceDeclaration(org.jboss.tools.seam.core.ISeamSource)
	 */
	public void removeSourceDeclaration(ISeamSource source) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.seam.core.ISeamComponent#setClassName(java.lang.String)
	 */
	public void setClassName(String className) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.seam.core.ISeamComponent#setEntity(boolean)
	 */
	public void setEntity(boolean entity) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.seam.core.ISeamComponent#setStateful(boolean)
	 */
	public void setStateful(boolean stateful) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.seam.core.ISeamContextVariable#getName()
	 */
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.seam.core.ISeamContextVariable#getScope()
	 */
	public ScopeType getScope() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.seam.core.ISeamContextVariable#setName(java.lang.String)
	 */
	public void setName(String name) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.seam.core.ISeamContextVariable#setScope(org.jboss.tools.seam.core.ScopeType)
	 */
	public void setScope(ScopeType type) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.seam.core.ISeamComponent#getProperties(java.lang.String)
	 */
	public List<ISeamProperty> getProperties(String propertyName) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.seam.core.ISeamComponent#getProperties()
	 */
	public Set<ISeamProperty> getProperties() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.seam.core.ISeamComponent#removeProperty(org.jboss.tools.seam.core.ISeamProperty)
	 */
	public void removeProperty(ISeamProperty property) {
		// TODO Auto-generated method stub
		
	}
}