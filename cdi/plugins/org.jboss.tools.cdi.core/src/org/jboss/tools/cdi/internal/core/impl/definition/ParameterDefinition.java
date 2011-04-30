/******************************************************************************* 
 * Copyright (c) 2009 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.cdi.internal.core.impl.definition;

import java.util.Set;

import org.eclipse.jdt.core.IMemberValuePair;
import org.eclipse.jdt.internal.core.MemberValuePair;
import org.jboss.tools.cdi.internal.core.impl.ParametedType;
import org.jboss.tools.cdi.internal.core.impl.TypeDeclaration;
import org.jboss.tools.common.text.ITextSourceReference;

public class ParameterDefinition extends AbstractMemberDefinition {
	protected MethodDefinition methodDefinition;
	
	protected String name;
	protected ParametedType type;
	protected TypeDeclaration overridenType;
	protected int index;

	protected ITextSourceReference position = null;

	public ParameterDefinition() {}

	public String getName() {
		return name;
	}

	public ParametedType getType() {
		return type;
	}

	public TypeDeclaration getOverridenType() {
		return overridenType;
	}

	public void setOverridenType(TypeDeclaration overridenType) {
		this.overridenType = overridenType;
	}

	public MethodDefinition getMethodDefinition() {
		return methodDefinition;
	}

	private static IMemberValuePair[] EMPTY_PAIRS = new IMemberValuePair[0];

	static IMemberValuePair[] getMemberValues(String source) {
		int p1 = source.indexOf('(');
		int p2 = source.indexOf(')');
		if(p1 >= 0 && p2 > p1) {
			String params = source.substring(p1 + 1, p2).trim();
			if(params.length() > 0) {
				if(params.startsWith("{") && params.endsWith("}")) {
					//TODO
				} else if(params.endsWith(".class")) {
					params = params.substring(0, params.length() - 6);
					IMemberValuePair pair = new MemberValuePair("value", params, IMemberValuePair.K_CLASS);
					return new IMemberValuePair[]{pair};
				} else if(params.startsWith("\"") && params.endsWith("\"")) {
					params = params.substring(1, params.length() - 1);
					IMemberValuePair pair = new MemberValuePair("value", params, IMemberValuePair.K_STRING);
					return new IMemberValuePair[]{pair};
				} else {
					//TODO
				}
			}
		}
		
		return EMPTY_PAIRS;
	}

	public Set<String> getAnnotationTypes() {
		return annotationsByType.keySet();
	}

	public void setPosition(ITextSourceReference position) {
		this.position = position;
	}

	public ITextSourceReference getPosition() {
		return position;
	}

	public String getAnnotationText(String annotationTypeName) {
		ITextSourceReference pos = getAnnotationPosition(annotationTypeName);
		if(pos == null) return null;
		String text = methodDefinition.getTypeDefinition().getContent().substring(pos.getStartPosition(), pos.getStartPosition() + pos.getLength());
		return text;
	}

}