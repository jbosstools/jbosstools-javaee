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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IType;
import org.jboss.tools.batch.internal.core.BatchArtifactType;
import org.jboss.tools.batch.internal.core.IRootDefinitionContext;
import org.jboss.tools.common.java.IParametedType;
import org.jboss.tools.common.java.ParametedType;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class TypeDefinition extends AbstractTypeDefinition {
	boolean isAbstract;
	List<FieldDefinition> fields = new ArrayList<FieldDefinition>();

	BatchArtifactType artifactType = null;

	public TypeDefinition() {
	}

	@Override
	protected void init(IType contextType, IRootDefinitionContext context, int flags) throws CoreException {
		super.init(contextType, context, flags);
		boolean allMembers = (flags & FLAG_ALL_MEMBERS) > 0;
		isAbstract = Flags.isAbstract(type.getFlags()) || type.isInterface();
		if(isAbstract) {
			//Abstract type cannot be a batch artifact
			return;
		}
		IField[] fs = getType().getFields();
		for (int i = 0; i < fs.length; i++) {
			FieldDefinition f = newFieldDefinition();
			f.setTypeDefinition(this);
			f.setField(fs[i], context, flags);
			if(allMembers || f.isBatchProperty()) {
				fields.add(f);
			}
		}
		Collection<IParametedType> ts = getParametedType().getAllTypes();
		Set<String> typeNames = new HashSet<String>();
		for (IParametedType t: ts) {
			IType tp = t.getType();
			if(tp != null && tp.exists()) {
				typeNames.add(tp.getFullyQualifiedName());
			}
		}
		for (BatchArtifactType bat: BatchArtifactType.values()) {
			if(typeNames.contains(bat.getInterfaceName())) {
				artifactType = bat;
				break;
			}
		}
	}

	protected FieldDefinition newFieldDefinition() {
		return new FieldDefinition();
	}

	public ParametedType getSuperType() {
		return parametedType == null ? null : parametedType.getSuperType();
	}

	public List<FieldDefinition> getFields() {
		return fields;
	}

	public boolean isAbstract() {
		return isAbstract;
	}

	public BatchArtifactType getArtifactType() {
		return artifactType;
	}
}