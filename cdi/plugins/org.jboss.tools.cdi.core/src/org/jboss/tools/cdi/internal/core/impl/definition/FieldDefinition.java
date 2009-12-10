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
package org.jboss.tools.cdi.internal.core.impl.definition;

import org.eclipse.jdt.core.IField;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class FieldDefinition extends AbstractMemberDefinition {
	IField field;

	public FieldDefinition() {}

	public void setField(IField field, DefinitionContext context) {
		this.field = field;
		setAnnotatable(field, field.getDeclaringType(), context);
	}

	public IField getField() {
		return field;
	}

}
