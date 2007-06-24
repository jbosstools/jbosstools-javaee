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

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;


public class MethodDescriptor
{
	private Constructor constructor;
	
	public MethodDescriptor(Constructor constructor)
	{
		this.constructor = constructor;
	}
	
	public String getName()
	{
		return constructor.getName();	
	}
	
	public String getParameters()
	{
		StringBuffer result = new StringBuffer();
		
		Class parameters[] = constructor.getParameterTypes();
		for (int i = 0; i < parameters.length; i++)
			result.append('p').append(i).append((i == parameters.length - 1) ? "" : ", ");
			 		
		return result.toString();
	}

	public String getParametersWithType()
	{
		StringBuffer result = new StringBuffer();
		
		Class parameters[] = constructor.getParameterTypes();
		for (int i = 0; i < parameters.length; i++)
		{
			if (parameters[i].isArray())
				result.append(parameters[i].getComponentType().getName()).append("[]");
			else
				result.append(parameters[i].getName());
			result.append(' ').append('p').append(i);
			if (i < parameters.length - 1) result.append(", ");
		}
			 		
		return result.toString();
	}
	
	public String getModifiers()
	{
		StringBuffer result = new StringBuffer();
		int modifiers = constructor.getModifiers();
		
		if ((Modifier.PUBLIC & modifiers) != 0)
			result.append("public");
		else if ((Modifier.PRIVATE & modifiers) != 0)
			result.append("private");
		else if ((Modifier.PROTECTED & modifiers) != 0)
			result.append("protected");
		
		if ((Modifier.STATIC & modifiers) != 0)
			result.append(result.length() > 0 ? " " : "").append("static");
		
		if ((Modifier.FINAL & modifiers) != 0)
			result.append(result.length() > 0 ? " " : "").append("final");

		return result.toString();
	}
}
