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
import org.jboss.tools.cdi.core.IAnnotationDeclaration;
import org.jboss.tools.cdi.core.IJavaAnnotation;
import org.jboss.tools.cdi.core.IRootDefinitionContext;
import org.jboss.tools.cdi.core.extension.ICDIExtension;
import org.jboss.tools.cdi.core.extension.feature.IProcessAnnotatedMemberFeature;
import org.jboss.tools.cdi.internal.core.impl.AnnotationLiteral;
import org.jboss.tools.cdi.internal.core.impl.definition.AbstractMemberDefinition;
import org.jboss.tools.cdi.internal.core.impl.definition.BeanMemberDefinition;
import org.jboss.tools.cdi.internal.core.impl.definition.MethodDefinition;

/**
 * This implementation detects @Unwrap annotation at methods and makes CDI builder aware of 
 * it being a producer method by adding fake annotation literal for @Produces annotation based 
 * on the unwrap annotation.
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class CDISeamSolderUnwrapsExtension implements ICDIExtension, IProcessAnnotatedMemberFeature {

	public void processAnnotatedMember(BeanMemberDefinition memberDefinition, IRootDefinitionContext context) {
		if(memberDefinition instanceof MethodDefinition) {
			if(memberDefinition.isAnnotationPresent(CDISeamSolderConstants.UNWRAPS_ANNOTATION_TYPE_NAME)) {
				IJavaAnnotation ja = createFakeProducesAnnotation(memberDefinition, context);
				if(ja != null) {
					memberDefinition.addAnnotation(ja, context);
				}
			}
		}

	}

	IJavaAnnotation createFakeProducesAnnotation(AbstractMemberDefinition def, IRootDefinitionContext context) {
		IAnnotationDeclaration a = def.getAnnotation(CDISeamSolderConstants.UNWRAPS_ANNOTATION_TYPE_NAME);
		if(a == null) return null;
		IType producesAnnotation = context.getProject().getType(CDIConstants.PRODUCES_ANNOTATION_TYPE_NAME);
		return (producesAnnotation == null) ? null
			: new AnnotationLiteral(def.getResource(), a.getStartPosition(), a.getLength(), null, IMemberValuePair.K_UNKNOWN, producesAnnotation);
	}

}
