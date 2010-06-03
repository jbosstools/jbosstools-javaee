/******************************************************************************* 
 * Copyright (c) 2007 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.cdi.internal.core.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jdt.core.IMethod;
import org.jboss.tools.cdi.core.IQualifier;
import org.jboss.tools.cdi.internal.core.impl.definition.AnnotationMemberDefinition;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class QualifierElement extends CDIAnnotationElement implements IQualifier {
	Set<IMethod> nonbindingMethods = null;

	public QualifierElement() {}

	public Set<IMethod> getNonBindingMethods() {
		if(nonbindingMethods == null) {
			Set<IMethod> result = new HashSet<IMethod>();
			List<AnnotationMemberDefinition> ms = definition.getMethods();
			for (AnnotationMemberDefinition m: ms) {
				if(m.getNonbindingAnnotation() != null) {
					result.add(m.getMethod());
				}
			}
			nonbindingMethods = result;
		}
		return nonbindingMethods;
		
	}

}
