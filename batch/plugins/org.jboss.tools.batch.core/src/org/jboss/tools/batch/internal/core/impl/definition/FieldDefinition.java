/******************************************************************************* 
 * Copyright (c) 2015 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.batch.internal.core.impl.definition;

import org.eclipse.jdt.core.IField;
import org.jboss.tools.batch.internal.core.IRootDefinitionContext;
import org.jboss.tools.common.java.ITypeDeclaration;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class FieldDefinition extends AbstractMemberDefinition {
	AbstractTypeDefinition typeDefinition;
	IField field;
	ITypeDeclaration overridenType = null;

	public FieldDefinition() {}

	public void setTypeDefinition(AbstractTypeDefinition typeDefinition) {
		this.typeDefinition = typeDefinition;
	}

	@Override
	public AbstractTypeDefinition getTypeDefinition() {
		return typeDefinition;
	}

	public void setField(IField field, IRootDefinitionContext context, int flags) {
		this.field = field;
		setAnnotatable(field, field.getDeclaringType(), context, flags);
	}

	public IField getField() {
		return field;
	}

	public void setOverridenType(ITypeDeclaration overridenType) {
		this.overridenType = overridenType;
	}

	public ITypeDeclaration getOverridenType() {
		return overridenType;
	}

	public boolean isBatchProperty() {
		return getBatchPropertyAnnotation() != null;
	}

}
