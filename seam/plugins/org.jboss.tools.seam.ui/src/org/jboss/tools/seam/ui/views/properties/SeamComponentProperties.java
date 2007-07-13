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
import org.jboss.tools.seam.core.ISeamComponent;

/**
 * @author Viacheslav Kabanovich
 */
public class SeamComponentProperties extends SeamElementProperties {
	enum Precedence {
		BUILT_IN(0),
		FRAMEWORK(10),
		APPLICATION(20),
		DEPLOYMENT(30),
		MOCK(40);
	
		int value;

		Precedence(int value) {
			this.value = value;
		}
			
		static String getStringValue(int v) {
			for (int i = 0; i < Precedence.values().length; i++) {
				if(v == Precedence.values()[i].value) return Precedence.values()[i].toString();
			}
			return "" + v;
		}
	}
	
	ISeamComponent element;
	
	static IPropertyDescriptor[] DESCRIPTORS = {
		NAME_DESCRIPTOR, SCOPE_DESCRIPTOR, CLASS_DESCRIPTOR, PRECEDENCE_DESCRIPTOR, 
		ENTITY_DESCRIPTOR
	};
	
	static IPropertyDescriptor[] ENTITY_DESCRIPTORS = {
		NAME_DESCRIPTOR, SCOPE_DESCRIPTOR, CLASS_DESCRIPTOR, PRECEDENCE_DESCRIPTOR, 
		ENTITY_DESCRIPTOR, STATEFUL_DESCRIPTOR
	};
	
	public SeamComponentProperties(ISeamComponent element) {
		this.element = element;
	}

	public IPropertyDescriptor[] getPropertyDescriptors() {
		if(element != null && element.isEntity()) return ENTITY_DESCRIPTORS;
		return DESCRIPTORS;
	}

	public Object getPropertyValue(Object id) {
		if(NAME.equals(id)) {
			return element.getName();
		} else if(SCOPE.equals(id)) {
			return element.getScope().toString();
		} else if(CLASS.equals(id)) {
			return element.getClassName();
		} else if(PRECEDENCE.equals(id)) {
			return Precedence.getStringValue(element.getPrecedence());
		} if(ENTITY.equals(id)) {
			return "" + element.isEntity();
		} if(STATEFUL.equals(id)) {
			return "" + element.isStateful();
		}
		return null;
	}

}
