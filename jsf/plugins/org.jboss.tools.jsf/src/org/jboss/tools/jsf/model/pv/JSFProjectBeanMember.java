/*******************************************************************************
 * Copyright (c) 2007 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.jsf.model.pv;

import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.jboss.tools.common.model.util.EclipseJavaUtil;

public class JSFProjectBeanMember extends JSFProjectBean {
	private static final long serialVersionUID = 7769158153322658422L;
	protected IMember member = null;

	public String getPresentationString() {
		return getAttributeValue("name");
	}

    public boolean isAttributeEditable(String name) {
        return false;
    }

	public void setMember(IMember member) {
		this.member = member;
	}

	public IMember getMember() {
		return member;
	}

	public boolean hasMethodSignature(String returnType, String[] parameters) {
		if(!(member instanceof IMethod)) return false;
		IMethod method = (IMethod)member;
		String[] ps = method.getParameterTypes();
		if(ps.length != parameters.length) return false;
		for (int i = 0; i < ps.length; i++) {
			if(parameters[i].equals(ps[i])) continue;
			String t = EclipseJavaUtil.resolveTypeAsString(method.getDeclaringType(), ps[i]);
			if(t == null || !t.equals(parameters[i])) return false;
		}
		if(!returnType.equals(getAttributeValue("class name"))) return false;
		return true;
	}

}
