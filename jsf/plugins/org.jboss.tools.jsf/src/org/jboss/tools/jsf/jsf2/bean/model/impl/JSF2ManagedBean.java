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

import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IType;
import org.jboss.tools.common.java.impl.AnnotationDeclaration;
import org.jboss.tools.jsf.jsf2.bean.model.IJSF2ManagedBean;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class JSF2ManagedBean implements IJSF2ManagedBean {
	TypeDefinition typeDefinition;
	
	public JSF2ManagedBean() {}

	public void setDefinition(TypeDefinition d) {
		typeDefinition = d;
	}

	@Override
	public String getName() {
		String result = null;
		AnnotationDeclaration d = getManagedBeanDeclaration();
		if(d != null) {
			Object m = d.getMemberValue("name", true);
			if(m != null) {
				result = m.toString();
			}
			if(result == null || result.length() == 0) {
				result = typeDefinition.getType().getElementName();
				if(result.length() > 0) {
					result = result.substring(0, 1).toLowerCase() + result.substring(1);
				}
			}
		}
		return result;
	}

	@Override
	public AnnotationDeclaration getManagedBeanDeclaration() {
		return typeDefinition.getManagedBeanAnnotation();
	}

	@Override
	public IPath getSourcePath() {
		return typeDefinition.getType().getPath();
	}

	@Override
	public IType getBeanClass() {
		return typeDefinition.getType();
	}

}
