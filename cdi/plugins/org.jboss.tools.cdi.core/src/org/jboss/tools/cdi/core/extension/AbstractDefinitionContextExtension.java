/******************************************************************************* 
 * Copyright (c) 2011 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.cdi.core.extension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.IPath;
import org.jboss.tools.cdi.core.IRootDefinitionContext;
import org.jboss.tools.cdi.internal.core.impl.definition.AnnotationDefinition;
import org.jboss.tools.cdi.internal.core.impl.definition.TypeDefinition;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public abstract class AbstractDefinitionContextExtension implements IDefinitionContextExtension {
	protected IRootDefinitionContext root;
	
	protected AbstractDefinitionContextExtension original;
	protected AbstractDefinitionContextExtension workingCopy;
	
	protected abstract AbstractDefinitionContextExtension copy(boolean clean);

	public void newWorkingCopy(boolean forFullBuild) {
		if(original != null) return;
		workingCopy = copy(forFullBuild);
		workingCopy.original = this;
	}

	public void applyWorkingCopy() {
		if(original != null) {
			original.applyWorkingCopy();
			return;
		}
		if(workingCopy == null) {
			return;
		}
		
		doApplyWorkingCopy();
		
		workingCopy = null;
	}

	protected void doApplyWorkingCopy() {}

	public void clean() {
	}

	public void clean(IPath path) {
	}

	public void clean(String typeName) {
	}

	public void setRootContext(IRootDefinitionContext context) {
		this.root = context;
	}

	public IRootDefinitionContext getRootContext() {
		return root;
	}

	public IDefinitionContextExtension getWorkingCopy() {
		if(original != null) {
			return this;
		}
		if(workingCopy != null) {
			return workingCopy;
		}
		workingCopy = copy(false);
		workingCopy.original = this;
		return workingCopy;
	}

	public void computeAnnotationKind(AnnotationDefinition annotation) {
		
	}

	private static List<TypeDefinition> EMPTY_LIST = Collections.unmodifiableList(new ArrayList<TypeDefinition>());

	public List<TypeDefinition> getTypeDefinitions() {
		return EMPTY_LIST;
	}

}
