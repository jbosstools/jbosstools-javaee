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
import org.eclipse.jdt.core.IType;
import org.jboss.tools.seam.core.BijectedAttributeType;
import org.jboss.tools.seam.core.IBijectedAttribute;
import org.jboss.tools.seam.core.ISeamComponent;
import org.jboss.tools.seam.core.ISeamProperty;
import org.jboss.tools.seam.core.ISeamXmlElement;
import org.jboss.tools.seam.core.ScopeType;

public class SeamComponent implements ISeamComponent {
	protected Map<String,ISeamProperty<? extends Object>> properties = new HashMap<String, ISeamProperty<? extends Object>>();
	protected IPath source;

	public SeamComponent () {
	}

	public Set<String> getPropertyNames() {
		return properties.keySet();
	}

	public boolean getBooleanProperty(String propertyName) {
		ISeamProperty<?> o = getProperty(propertyName);
		Object value = o.getValue();
		if(value instanceof Boolean) return ((Boolean)value).booleanValue();
		if(value instanceof String && "true".equals(value));
		return false;
	}

	public String getStringProperty(String propertyName) {
		ISeamProperty<?> o = getProperty(propertyName);
		Object value = o.getValue();
		return value == null ? null : value.toString();
	}

	public ISeamProperty<? extends Object> getProperty(String propertyName) {
		return properties.get(propertyName);
	}

	public void addProperty(ISeamProperty<? extends Object> property) {
		properties.put(property.getName(), property);
	}

	public void setStringProperty(String propertyName, String value) {
		ISeamProperty<? extends Object> p = getProperty(propertyName);
		if(p == null) {
			p = new SeamProperty<String>(propertyName, value);
		} else {
			p.setObject(value);
		}
		properties.put(propertyName, p);
	}

	public void setSource(IPath path) {
		source = path;
	}

	/**
	 * @return ID of Seam Component. It's unique within Project.
	 */
	public String getId() {
		return getClassName() + ":" + getName();
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.seam.core.ISeamComponent#addBijectedAttribute(org.jboss.tools.seam.core.IBijectedAttribute)
	 */
	public void addBijectedAttribute(IBijectedAttribute attribute) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.seam.core.ISeamComponent#addJavaSourceClass(org.eclipse.jdt.core.IType)
	 */
	public void addJavaSourceClass(IType sourceClass) {
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

	public void setClassName(String name) {
		// TODO Auto-generated method stub
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.seam.core.ISeamComponent#getJavaSourceClasses()
	 */
	public Set<IType> getJavaSourceClasses() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.seam.core.ISeamComponent#getName()
	 */
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setName(String name) {
		// TODO Auto-generated method stub
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.seam.core.ISeamComponent#getProperties(java.lang.String)
	 */
	public List<ISeamProperty<? extends Object>> getProperties(
			String propertyName) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.seam.core.ISeamComponent#getProperties()
	 */
	public Set<ISeamProperty<? extends Object>> getProperties() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.seam.core.ISeamComponent#getScope()
	 */
	public ScopeType getScope() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setScope(ScopeType scope) {
		// TODO Auto-generated method stub
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.seam.core.ISeamComponent#getXmlSourceElements()
	 */
	public Set<ISeamXmlElement> getXmlSourceElements() {
		// TODO Auto-generated method stub
		return null;
	}
}