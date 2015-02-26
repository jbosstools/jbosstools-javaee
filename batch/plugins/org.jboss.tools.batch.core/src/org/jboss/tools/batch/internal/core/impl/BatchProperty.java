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

import java.util.Collection;
import java.util.HashSet;

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.IField;
import org.jboss.tools.batch.core.BatchConstants;
import org.jboss.tools.batch.core.IBatchArtifact;
import org.jboss.tools.batch.core.IBatchProperty;
import org.jboss.tools.batch.internal.core.impl.BatchUtil.AttrReferencesRequestor;
import org.jboss.tools.batch.internal.core.impl.definition.FieldDefinition;
import org.jboss.tools.common.java.IAnnotationDeclaration;
import org.jboss.tools.common.text.ITextSourceReference;

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

	@Override
	public Collection<ITextSourceReference> getReferences() {
		Collection<ITextSourceReference> result = new HashSet<ITextSourceReference>();
		for (IFile file: parent.project.getDeclaredBatchJobs()) {
			String expression = "//*[@" + BatchConstants.ATTR_REF + "=\"" + parent.getName() + "\"]" 
					+ "/*[name()=\"" + BatchConstants.TAG_PROPERTIES + "\"]" 
					+ "/*[name()=\"" + BatchConstants.TAG_PROPERTY + "\" and @" + BatchConstants.ATTR_NAME + "=\"" + getPropertyName() +"\"]" 
					+ "/@" + BatchConstants.ATTR_NAME;
			AttrReferencesRequestor requestor = new AttrReferencesRequestor(file, expression);
			BatchUtil.scanXMLFile(file, requestor);
			result.addAll(requestor.getResults());
		}
		return result;
	}
}
