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
package org.jboss.tools.jsf.jsf2.bean.model.impl;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IType;
import org.jboss.tools.common.util.FileUtil;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class AbstractTypeDefinition extends AbstractMemberDefinition {
	protected String qualifiedName;
	protected IType type;

	protected String content = null;

	public AbstractTypeDefinition() {}

	@Override
	public AbstractTypeDefinition getTypeDefinition() {
		return this;
	}

	public String getQualifiedName() {
		return qualifiedName;
	}

	public IType getType() {
		return type;
	}

	public void setType(IType type, DefinitionContext context, int flags) {
		super.setAnnotatable(type, type, context, flags);
	}

	@Override
	protected void init(IType contextType, DefinitionContext context, int flags) throws CoreException {
		this.type = contextType;
		super.init(contextType, context, flags);
		qualifiedName = getType().getFullyQualifiedName();
	}

	public String getContent() {
		if(type == null || type.isBinary()) return null;
		if(content == null && resource instanceof IFile && resource.getName().endsWith(".java")) {
			content = FileUtil.getContentFromEditorOrFile((IFile)resource);
		}
		return content;
	}
	
}
