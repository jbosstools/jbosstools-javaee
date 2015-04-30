/******************************************************************************* 
 * Copyright (c) 2015 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.cdi.extension.internal.core.batch;

import org.eclipse.jdt.core.IType;
import org.jboss.tools.cdi.core.CDIUtil;
import org.jboss.tools.cdi.core.IInjectionPoint;
import org.jboss.tools.cdi.core.extension.ICDIExtension;
import org.jboss.tools.cdi.core.extension.feature.IInjectionPointValidatorFeature;
import org.jboss.tools.common.java.IAnnotationDeclaration;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class BatchExtension implements ICDIExtension, IInjectionPointValidatorFeature {

	private static final String BATCH_PROPERTY_QUALIFIER = "javax.batch.api.BatchProperty";
	private static final String JOB_CONTEXT_TYPE = "javax.batch.runtime.context.JobContext";
	private static final String STEP_CONTEXT_TYPE = "javax.batch.runtime.context.StepContext";

	@Override
	public boolean shouldIgnoreInjection(IType typeOfInjectionPoint, IInjectionPoint injection) {
		if(typeOfInjectionPoint != null) {
			String typeName = typeOfInjectionPoint.getFullyQualifiedName();
			if(JOB_CONTEXT_TYPE.equals(typeName) || STEP_CONTEXT_TYPE.equals(typeName)) {
				return true;
			}
			IAnnotationDeclaration declaration = CDIUtil.getAnnotationDeclaration(injection, BATCH_PROPERTY_QUALIFIER);
			return declaration!=null;
		}
		return false;
	}

}
