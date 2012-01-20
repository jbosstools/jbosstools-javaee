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
package org.jboss.tools.cdi.seam.solder.core;

public class Version30 extends Version implements CDISeamSolderConstants30 {
	public static Version instance = new Version30();
	
	public String getExactAnnotationTypeName() {
		return EXACT_ANNOTATION_TYPE_NAME_30;
	}

	public String getFullyQualifiedAnnotationTypeName() {
		return FULLY_QUALIFIED_ANNOTATION_TYPE_NAME_30;
	}

	public String getRequiresAnnotationTypeName() {
		return REQUIRES_ANNOTATION_TYPE_NAME_30;
	}

	public String getVetoAnnotationTypeName() {
		return VETO_ANNOTATION_TYPE_NAME_30;
	}

	public String getMessageLoggerAnnotationTypeName() {
		return MESSAGE_LOGGER_ANNOTATION_TYPE_NAME_30;
	}

	public String getMessageBundleAnnotationTypeName() {
		return MESSAGE_BUNDLE_ANNOTATION_TYPE_NAME_30;
	}

	public String getHandlerTypeAnnotationTypeName() {
		return SERVICE_HANDLER_TYPE_ANNOTATION_TYPE_NAME_30;
	}

	public String getDefaultBeanAnnotationTypeName() {
		return DEFAULT_BEAN_ANNOTATION_TYPE_NAME_30;
	}

	public String getUnwrapsAnnotationTypeName() {
		return UNWRAPS_ANNOTATION_TYPE_NAME_30;
	}

	public String getGenericTypeAnnotationTypeName() {
		return GENERIC_TYPE_ANNOTATION_TYPE_NAME_30;
	}

	public String getGenericQualifierAnnotationTypeName() {
		return GENERIC_QUALIFIER_TYPE_NAME_30;
	}

	public String getGenericConfigurationAnnotationTypeName() {
		return GENERIC_CONFIGURATION_ANNOTATION_TYPE_NAME_30;
	}

	public String getApplyScopeAnnotationTypeName() {
		return APPLY_SCOPE_ANNOTATION_TYPE_NAME_30;
	}

	public String getInjectGenericAnnotationTypeName() {
		return INJECT_GENERIC_ANNOTATION_TYPE_NAME_30;
	}
}
