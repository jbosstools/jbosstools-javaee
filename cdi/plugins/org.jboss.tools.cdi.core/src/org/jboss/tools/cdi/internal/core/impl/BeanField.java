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
package org.jboss.tools.cdi.internal.core.impl;

import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IMember;
import org.jboss.tools.cdi.core.IBeanField;
import org.jboss.tools.cdi.internal.core.impl.definition.FieldDefinition;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class BeanField extends BeanMember implements IBeanField {
	protected IField field;

	public BeanField() {}

	public void setDefinition(FieldDefinition definition) {
		super.setDefinition(definition);
		setField(definition.getField());
	}

	public FieldDefinition getDefinition() {
		return (FieldDefinition)definition;
	}

	public IField getField() {
		return field;
	}

	public void setField(IField field) {
		this.field = field;
		setMember(field);
	}

	public IMember getSourceMember() {
		return getField();
	}

}
