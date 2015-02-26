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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IType;
import org.jboss.tools.batch.core.BatchArtifactType;
import org.jboss.tools.batch.core.BatchConstants;
import org.jboss.tools.batch.core.IBatchArtifact;
import org.jboss.tools.batch.core.IBatchProperty;
import org.jboss.tools.batch.internal.core.impl.definition.FieldDefinition;
import org.jboss.tools.batch.internal.core.impl.definition.TypeDefinition;
import org.jboss.tools.common.java.IAnnotationDeclaration;
import org.jboss.tools.common.text.ITextSourceReference;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class BatchArtifact implements IBatchArtifact {
	BatchProject project;
	TypeDefinition definition;
	String name;
	
	List<IBatchProperty> properties = new ArrayList<IBatchProperty>();
	Map<String, IBatchProperty> propertiesByName = new HashMap<String, IBatchProperty>();
	Map<String, IBatchProperty> propertiesByFieldName = new HashMap<String, IBatchProperty>();

	public BatchArtifact() {
	}

	public void setProject(BatchProject project) {
		this.project = project;
	}

	public void setDefinition(TypeDefinition def) {
		definition = def;
		initName();
		List<FieldDefinition> fs = def.getFields();
		for (FieldDefinition f: fs) {
			BatchProperty p = new BatchProperty();
			p.setArtifact(this);
			p.setDefinition(f);
			properties.add(p);
			propertiesByName.put(p.getPropertyName(), p);
			propertiesByFieldName.put(p.getField().getElementName(), p);
		}
	}

	void initName() {
		if(definition.getNamedAnnotation() != null) {
			Object v = definition.getNamedAnnotation().getMemberValue(null);
			if(v != null) {
				name = v.toString();
			} else {
				String n = definition.getType().getElementName();
				if(n.length() > 0) {
					n = n.substring(0, 1).toLowerCase() + n.substring(1);
				}
				name = n;
			}
		} else {
			name = definition.getQualifiedName();
		}
	}

	@Override
	public BatchProject getProject() {
		return project;
	}

	@Override
	public IPath getSourcePath() {
		return definition.getType().getPath();
	}

	@Override
	public BatchArtifactType getArtifactType() {
		return definition.getArtifactType();
	}

	@Override
	public IAnnotationDeclaration getNamedDeclaration() {
		return definition.getNamedAnnotation();
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public IType getType() {
		return definition.getType();
	}

	@Override
	public Collection<IBatchProperty> getProperties() {
		return properties;
	}

	@Override
	public IBatchProperty getProperty(String name) {
		return propertiesByName.get(name);
	}

	@Override
	public IBatchProperty getProperty(IField field) {
		return propertiesByFieldName.get(field.getElementName());
	}
	
	@Override
	public Collection<ITextSourceReference> getReferences() {
		Collection<ITextSourceReference> result = new HashSet<ITextSourceReference>();
		for (IFile file: project.getDeclaredBatchJobs()) {
			result.addAll(BatchUtil.getAttributeReferences(file, BatchConstants.ATTR_REF, getName()));
		}
		return result;
	}
}
