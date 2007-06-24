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
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IPath;
import org.jboss.tools.seam.core.ISeamComponent;
import org.jboss.tools.seam.core.ISeamProperty;

public class SeamComponent implements ISeamComponent {
	protected Map<String,ISeamProperty<? extends Object>> properties = new HashMap<String, ISeamProperty<? extends Object>>();
	protected IPath source;
	
	public SeamComponent () {
		properties.put(NAME, new SeamProperty<String>(NAME));
		properties.put(CLASS, new SeamProperty<String>(CLASS));
		properties.put(SCOPE, new SeamProperty<String>(SCOPE, DEFAULT));
	}

	public String getName() {
		return getStringProperty(NAME);
	}
	
	public void setName(String name) {
		setStringProperty(NAME, name);
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

	public String getClassName() {
		return getStringProperty(CLASS);
	}
	
	public void setClassName(String className) {
		setStringProperty(CLASS, className);
	}
	
	public String getScope() {
		return getStringProperty(SCOPE);
	}

	public void setScope(String scope) {
		setStringProperty(SCOPE, scope);
	}
	
	public String getJndiName() {
		return getStringProperty(JNDI_NAME);
	}

	public void setJndiName(String jndiName) {
		setStringProperty(JNDI_NAME, jndiName);
	}
	
	public String getPrecedence() {
		return getStringProperty(PRECEDENCE);
	}

	public void setPrecedence(String precedence) {
		setStringProperty(PRECEDENCE, precedence);
	}
	
	public boolean isAutoCreate() {
		return getBooleanProperty(AUTO_CREATE);
	}

	public void setAutoCreate(String autoCreate) {
		if(DEFAULT.equals(autoCreate) || autoCreate == null || autoCreate.length() == 0) {
			setStringProperty(AUTO_CREATE, DEFAULT);
		}
		addProperty(new SeamProperty<Boolean>(AUTO_CREATE, Boolean.parseBoolean(autoCreate)));
	}
	
	public boolean isInstalled() {
		return getBooleanProperty(INSTALLED);
	}	

	public void setInstalled(String installed) {
		if(DEFAULT.equals(installed) || installed == null || installed.length() == 0) {
			setStringProperty(INSTALLED, DEFAULT);
		}
		addProperty(new SeamProperty<Boolean>(INSTALLED, Boolean.parseBoolean(installed)));
	}
	
}
