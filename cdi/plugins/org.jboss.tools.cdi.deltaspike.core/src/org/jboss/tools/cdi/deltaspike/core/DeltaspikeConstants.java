/******************************************************************************* 
 * Copyright (c) 2012 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.cdi.deltaspike.core;

public interface DeltaspikeConstants {

	public String CONFIG_PROPERTY_ANNOTATION_TYPE_NAME = "org.apache.deltaspike.core.api.config.annotation.ConfigProperty"; //$NON-NLS-1$

	public String BEFORE_HANDLES_ANNOTATION_TYPE_NAME = "org.apache.deltaspike.core.api.exception.control.annotation.BeforeHandles"; //$NON-NLS-1$
	public String EXCEPTION_HANDLER_ANNOTATION_TYPE_NAME = "org.apache.deltaspike.core.api.exception.control.annotation.ExceptionHandler"; //$NON-NLS-1$
	public String HANDLES_ANNOTATION_TYPE_NAME = "org.apache.deltaspike.core.api.exception.control.annotation.Handles"; //$NON-NLS-1$
	public String EXCEPTION_EVENT_TYPE_NAME = "org.apache.deltaspike.core.api.exception.control.event.ExceptionEvent"; //$NON-NLS-1$

	public String EXCLUDE_ANNOTATION_TYPE_NAME = "org.apache.deltaspike.core.api.exclude.annotation.Exclude"; //$NON-NLS-1$

	public String MESSAGE_BUNDLE_ANNOTATION_TYPE_NAME = "org.apache.deltaspike.core.api.message.annotation.MessageBundle"; //$NON-NLS-1$
	public String MESSAGE_CONTEXT_CONFIG_ANNOTATION_TYPE_NAME = "org.apache.deltaspike.core.api.message.annotation.MessageContextConfig"; //$NON-NLS-1$
	public String MESSAGE_TEMPLATE_ANNOTATION_TYPE_NAME = "org.apache.deltaspike.core.api.message.annotation.MessageTemplate"; //$NON-NLS-1$

	public String SECURED_ANNOTATION_TYPE_NAME = "org.apache.deltaspike.security.api.authorization.annotation.Secured"; //$NON-NLS-1$
	public String SECURES_ANNOTATION_TYPE_NAME = "org.apache.deltaspike.security.api.authorization.annotation.Secures"; //$NON-NLS-1$
	public String SECURITY_BINDING_ANNOTATION_TYPE_NAME = "org.apache.deltaspike.security.api.authorization.annotation.SecurityBindingType"; //$NON-NLS-1$
	public String SECURITY_PARAM_BINDING_ANNOTATION_TYPE_NAME = "org.apache.deltaspike.security.api.authorization.annotation.SecurityParameterBinding"; //$NON-NLS-1$

	public String MESSAGE_BUNDLE_ANNOTATION_KIND = "messageBundleAnnotation";

}
