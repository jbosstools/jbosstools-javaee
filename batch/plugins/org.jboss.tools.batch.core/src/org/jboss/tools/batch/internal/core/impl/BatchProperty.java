/*************************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     JBoss by Red Hat - Initial implementation.
 ************************************************************************************/
package org.jboss.tools.batch.internal.core.impl;

import org.eclipse.jdt.core.IField;
import org.jboss.tools.batch.core.IBatchArtifact;
import org.jboss.tools.batch.core.IBatchProperty;
import org.jboss.tools.batch.internal.core.impl.definition.FieldDefinition;
import org.jboss.tools.common.java.IAnnotationDeclaration;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class BatchProperty implements IBatchProperty {
	BatchArtifact parent;
	FieldDefinition definition;

	public BatchProperty() {}

	public void setArtifact(BatchArtifact parent) {
		this.parent = parent;
	}

	public void setDefinition(FieldDefinition definition) {
		this.definition = definition;
		
	}
	@Override
	public IAnnotationDeclaration getInjectDeclaration() {
		return definition.getInjectAnnotation();
	}

	@Override
	public IAnnotationDeclaration getBatchPropertyDeclaration() {
		return definition.getBatchPropertyAnnotation();
	}

	@Override
	public String getPropertyName() {
		IAnnotationDeclaration d = getBatchPropertyDeclaration();
		if(d != null) {
			Object o = d.getMemberValue("name");
			if(o != null) {
				return o.toString();
			}
		}
		return getField().getElementName();
	}

	@Override
	public IField getField() {
		return definition.getField();
	}

	@Override
	public IBatchArtifact getArtifact() {
		return parent;
	}

}
