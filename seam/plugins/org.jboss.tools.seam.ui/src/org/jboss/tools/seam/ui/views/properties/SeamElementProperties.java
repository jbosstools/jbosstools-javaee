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
package org.jboss.tools.seam.ui.views.properties;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;

/**
 * @author Viacheslav Kabanovich
 */
public class SeamElementProperties implements IPropertySource {
	static String NAME = "name";
	static String SCOPE = "scope";
	static String CLASS = "class";
	static String PRECEDENCE = "precedence";
	static String INSTALLED = "installed";

	static String ENTITY = "entity";
	static String STATEFUL = "stateful";
	
	static IPropertyDescriptor NAME_DESCRIPTOR = new PropertyDescriptor(NAME, NAME);
	static IPropertyDescriptor SCOPE_DESCRIPTOR = new PropertyDescriptor(SCOPE, SCOPE);
	static IPropertyDescriptor CLASS_DESCRIPTOR = new PropertyDescriptor(CLASS, CLASS);
	static IPropertyDescriptor PRECEDENCE_DESCRIPTOR = new PropertyDescriptor(PRECEDENCE, PRECEDENCE);

	static IPropertyDescriptor ENTITY_DESCRIPTOR = new PropertyDescriptor(ENTITY, ENTITY);
	static IPropertyDescriptor STATEFUL_DESCRIPTOR = new PropertyDescriptor(STATEFUL, STATEFUL);

	public SeamElementProperties() {
	}

	public Object getEditableValue() {
		return this;
	}

	public IPropertyDescriptor[] getPropertyDescriptors() {
		return null;
	}

	public Object getPropertyValue(Object id) {
		return null;
	}

	public boolean isPropertySet(Object id) {
		return false;
	}

	public void resetPropertyValue(Object id) {
	}

	public void setPropertyValue(Object id, Object value) {
	}

}
