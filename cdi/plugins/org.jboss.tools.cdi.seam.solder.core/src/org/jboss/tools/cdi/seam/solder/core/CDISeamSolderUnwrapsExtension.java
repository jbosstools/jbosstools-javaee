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
package org.jboss.tools.cdi.seam.solder.core;

import org.eclipse.jdt.core.IMemberValuePair;
import org.eclipse.jdt.core.IType;
import org.jboss.tools.cdi.core.CDIConstants;
import org.jboss.tools.cdi.core.IRootDefinitionContext;
import org.jboss.tools.cdi.core.extension.ICDIExtension;
import org.jboss.tools.cdi.core.extension.feature.IProcessAnnotatedMemberFeature;
import org.jboss.tools.cdi.internal.core.impl.definition.AbstractMemberDefinition;
import org.jboss.tools.cdi.internal.core.impl.definition.BeanMemberDefinition;
import org.jboss.tools.cdi.internal.core.impl.definition.MethodDefinition;
import org.jboss.tools.common.java.IAnnotationDeclaration;
import org.jboss.tools.common.java.IJavaAnnotation;
import org.jboss.tools.common.java.impl.AnnotationLiteral;

/**
 * This implementation detects @Unwrap annotation at methods and makes CDI builder aware of 
 * it being a producer method by adding fake annotation literal for @Produces annotation based 
 * on the unwrap annotation.
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class CDISeamSolderUnwrapsExtension implements ICDIExtension, IProcessAnnotatedMemberFeature {

	protected Version getVersion() {
		return Version.instance;
	}

	public void processAnnotatedMember(BeanMemberDefinition memberDefinition, IRootDefinitionContext context) {
		if(memberDefinition instanceof MethodDefinition) {
			if(memberDefinition.isAnnotationPresent(getVersion().getUnwrapsAnnotationTypeName())) {
				IJavaAnnotation ja = createFakeProducesAnnotation(memberDefinition, context);
				if(ja != null) {
					memberDefinition.addAnnotation(ja, context);
				}
			}
		}

	}

	/**
	 * Returns new annotation object with Produces type if definition is annotated with Unwraps
	 * and Produces type is available in class path. Returns null otherwise.
	 * 
	 * @param def
	 * @param context
	 * @return new annotation object with Produces type or null
	 */
	IJavaAnnotation createFakeProducesAnnotation(AbstractMemberDefinition def, IRootDefinitionContext context) {
		IJavaAnnotation result = null;
		IAnnotationDeclaration a = def.getAnnotation(getVersion().getUnwrapsAnnotationTypeName());
		if(a != null) {
			IType producesAnnotation = context.getProject().getType(CDIConstants.PRODUCES_ANNOTATION_TYPE_NAME);
			if (producesAnnotation != null) {
				result = new AnnotationLiteral(def.getResource(), a.getStartPosition(), a.getLength(), null, IMemberValuePair.K_UNKNOWN, producesAnnotation);
			}
		}
		return result;
	}

}
