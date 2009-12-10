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
package org.jboss.tools.cdi.internal.core.impl.definition;

import java.util.HashSet;
import java.util.Set;

import org.jboss.tools.cdi.core.CDIConstants;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class AnnotationHelper implements CDIConstants {
	public static final Set<String> BASIC_ANNOTATION_TYPES = new HashSet<String>();
	public static final Set<String> CDI_ANNOTATION_TYPES = new HashSet<String>();
	public static final Set<String> SCOPE_ANNOTATION_TYPES = new HashSet<String>();
	public static final Set<String> STEREOTYPE_ANNOTATION_TYPES = new HashSet<String>();
	
	{
		BASIC_ANNOTATION_TYPES.add(INHERITED_ANNOTATION_TYPE_NAME);
		BASIC_ANNOTATION_TYPES.add(TARGET_ANNOTATION_TYPE_NAME);
		BASIC_ANNOTATION_TYPES.add(RETENTION_ANNOTATION_TYPE_NAME);

		SCOPE_ANNOTATION_TYPES.add(APPLICATION_SCOPED_ANNOTATION_TYPE_NAME);
		SCOPE_ANNOTATION_TYPES.add(CONVERSATION_SCOPED_ANNOTATION_TYPE_NAME);
		SCOPE_ANNOTATION_TYPES.add(REQUEST_SCOPED_ANNOTATION_TYPE_NAME);
		SCOPE_ANNOTATION_TYPES.add(SESSION_SCOPED_ANNOTATION_TYPE_NAME);
		SCOPE_ANNOTATION_TYPES.add(DEPENDENT_ANNOTATION_TYPE_NAME);
		
		STEREOTYPE_ANNOTATION_TYPES.add(MODEL_STEREOTYPE_TYPE_NAME);
		STEREOTYPE_ANNOTATION_TYPES.add(DECORATOR_STEREOTYPE_TYPE_NAME);

		CDI_ANNOTATION_TYPES.add(QUALIFIER_ANNOTATION_TYPE_NAME);
		CDI_ANNOTATION_TYPES.add(NAMED_QUALIFIER_TYPE_NAME);
		CDI_ANNOTATION_TYPES.add(ANY_QUALIFIER_TYPE_NAME);
		CDI_ANNOTATION_TYPES.add(DEFAULT_QUALIFIER_TYPE_NAME);
		CDI_ANNOTATION_TYPES.add(NEW_QUALIFIER_TYPE_NAME);
		CDI_ANNOTATION_TYPES.add(STEREOTYPE_ANNOTATION_TYPE_NAME);
		CDI_ANNOTATION_TYPES.add(TYPED_ANNOTATION_TYPE_NAME);
		CDI_ANNOTATION_TYPES.add(PRODUCES_ANNOTATION_TYPE_NAME);
		CDI_ANNOTATION_TYPES.add(SCOPE_ANNOTATION_TYPE_NAME);
		CDI_ANNOTATION_TYPES.add(NORMAL_SCOPE_ANNOTATION_TYPE_NAME);
		CDI_ANNOTATION_TYPES.add(PROVIDER_ANNOTATION_TYPE_NAME);
		CDI_ANNOTATION_TYPES.add(INJECT_ANNOTATION_TYPE_NAME);
		CDI_ANNOTATION_TYPES.add(ALTERNATIVE_ANNOTATION_TYPE_NAME);
		CDI_ANNOTATION_TYPES.add(INTERCEPTOR_BINDING_ANNOTATION_TYPE_NAME);

		CDI_ANNOTATION_TYPES.addAll(SCOPE_ANNOTATION_TYPES);
		CDI_ANNOTATION_TYPES.addAll(STEREOTYPE_ANNOTATION_TYPES);
	}

}
