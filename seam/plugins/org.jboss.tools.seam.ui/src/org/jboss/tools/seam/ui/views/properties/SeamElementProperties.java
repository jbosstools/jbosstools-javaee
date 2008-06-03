/*******************************************************************************
 * Copyright (c) 2007 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.seam.ui.views.properties;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;

/**
 * @author Viacheslav Kabanovich
 */
public class SeamElementProperties implements IPropertySource {
	static String NAME = "name"; //$NON-NLS-1$
	static String SCOPE = "scope"; //$NON-NLS-1$
	static String CLASS = "class"; //$NON-NLS-1$
	static String PRECEDENCE = "precedence"; //$NON-NLS-1$
	static String INSTALLED = "installed"; //$NON-NLS-1$

	static String ENTITY = "entity"; //$NON-NLS-1$
	static String STATEFUL = "stateful"; //$NON-NLS-1$
	
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
