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
package org.jboss.tools.struts.model.helpers;

import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;
import org.jboss.tools.common.model.util.EclipseJavaUtil;

public class MethodDescriptor {
	private IMethod constructor;
	
	public MethodDescriptor(IMethod constructor) {
		this.constructor = constructor;
	}
	
	public String getName() {
		return constructor.getElementName();
	}
	
	public String getParameters() {
		StringBuffer result = new StringBuffer();
		
		String[] ps = constructor.getParameterTypes();
		
		for (int i = 0; i < ps.length; i++) {
			String type = EclipseJavaUtil.resolveType(constructor.getDeclaringType(), ps[i]);
			if(type == null) type = ps[i];
			result.append('p').append(i).append((i == ps.length - 1) ? "" : ", ");
		}
			 		
		return result.toString();
	}

	public String getParametersWithType() {
		StringBuffer result = new StringBuffer();
		
		String[] ps = constructor.getParameterTypes();
		
		for (int i = 0; i < ps.length; i++) {
			String type = EclipseJavaUtil.resolveType(constructor.getDeclaringType(), ps[i]);
			if(type == null) type = ps[i];
			type = convertType(type);
			result.append(type);
			result.append(' ').append('p').append(i);
			if (i < ps.length - 1) result.append(", ");
		}
			 		
		return result.toString();
	}
	
	private String convertType(String type) {
		String postfix = "";
		while(type.startsWith("[")) {
			postfix += "[]";
			type = type.substring(1);
		}
		if(type.startsWith("L") && type.endsWith(";")) {
			return type.substring(1, type.length() - 1) + postfix;
		}
		if(type.equals("I")) {
			type = "int";
		}
		return type + postfix;
	}
	
	public String getModifiers()
	{
		StringBuffer result = new StringBuffer();
		int modifiers = 0;
		try {
			modifiers = constructor.getFlags();
		} catch (JavaModelException e) {
			//ignore
		}
		
		if (Flags.isPublic(modifiers))
			result.append("public");
		else if (Flags.isPrivate(modifiers))
			result.append("private");
		else if (Flags.isProtected(modifiers))
			result.append("protected");
		
		if (Flags.isStatic(modifiers))
			result.append(result.length() > 0 ? " " : "").append("static");
		
		if (Flags.isFinal(modifiers))
			result.append(result.length() > 0 ? " " : "").append("final");

		return result.toString();
	}

}
