/*******************************************************************************
 * Copyright (c) 2012 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.cdi.ui.test.testmodel;

import org.eclipse.jdt.core.IMemberValuePair;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;

/*
Signature	 						Java Type
Z	 								boolean
B	 								byte
C	 								char
S	 								short
I	 								int
J	 								long
F	 								float
D	 								double
L fully-qualified-class ;	 		fully-qualified-class
[ type	 							type[]
( arg-types ) ret-type	 			method type
*/

public class AnnotationType extends Type {
	private static IMethod[] methods = new IMethod[]{
		new Method("type", "LString;", null),
		new Method("name", "LString;", new MemberValuePair("name", "User", IMemberValuePair.K_STRING)),
		new Method("realChanky", "Z", null),
		new Method("unrealChanky", "Z", new MemberValuePair("unrealChanky", true, IMemberValuePair.K_BOOLEAN)),
		new Method("number", "I", null),
		new Method("size", "I", new MemberValuePair("size", 125, IMemberValuePair.K_INT)),
		new Method("bTs", "B", null),
		new Method("bTs2", "B", new MemberValuePair("bTs2", (byte)4, IMemberValuePair.K_BYTE)),
		new Method("posibility", "D", null),
		new Method("posibility2", "D", new MemberValuePair("posibility2", 0.9999, IMemberValuePair.K_DOUBLE)),
		new Method("ch", "C", null),
		new Method("ch2", "C", new MemberValuePair("ch2", 'T', IMemberValuePair.K_CHAR))
	};

	public AnnotationType(String qualifiedName) {
		super(qualifiedName);
	}
	
	@Override
	public boolean isAnnotation() throws JavaModelException {
		return true;
	}
	
	@Override
	public IMethod[] getMethods() throws JavaModelException {
		return methods;
	}
}
