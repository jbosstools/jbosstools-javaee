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

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public interface DeltaspikeConstants {

	public String CONFIG_PROPERTY_ANNOTATION_TYPE_NAME = "org.apache.deltaspike.core.api.config.ConfigProperty"; //$NON-NLS-1$

	public String BEFORE_HANDLES_ANNOTATION_TYPE_NAME = "org.apache.deltaspike.core.api.exception.control.BeforeHandles"; //$NON-NLS-1$
	public String EXCEPTION_HANDLER_ANNOTATION_TYPE_NAME = "org.apache.deltaspike.core.api.exception.control.ExceptionHandler"; //$NON-NLS-1$
	public String HANDLES_ANNOTATION_TYPE_NAME = "org.apache.deltaspike.core.api.exception.control.Handles"; //$NON-NLS-1$
	public String EXCEPTION_EVENT_TYPE_NAME = "org.apache.deltaspike.core.api.exception.control.event.ExceptionEvent"; //$NON-NLS-1$

	public String EXCLUDE_ANNOTATION_TYPE_NAME = "org.apache.deltaspike.core.api.exclude.Exclude"; //$NON-NLS-1$

	public String MESSAGE_BUNDLE_ANNOTATION_TYPE_NAME = "org.apache.deltaspike.core.api.message.MessageBundle"; //$NON-NLS-1$
	public String MESSAGE_CONTEXT_CONFIG_ANNOTATION_TYPE_NAME = "org.apache.deltaspike.core.api.message.MessageContextConfig"; //$NON-NLS-1$
	public String MESSAGE_TEMPLATE_ANNOTATION_TYPE_NAME = "org.apache.deltaspike.core.api.message.MessageTemplate"; //$NON-NLS-1$

	public String SECURED_ANNOTATION_TYPE_NAME = "org.apache.deltaspike.security.api.authorization.Secured"; //$NON-NLS-1$
	public String SECURES_ANNOTATION_TYPE_NAME = "org.apache.deltaspike.security.api.authorization.Secures"; //$NON-NLS-1$
	public String SECURITY_BINDING_ANNOTATION_TYPE_NAME = "org.apache.deltaspike.security.api.authorization.SecurityBindingType"; //$NON-NLS-1$
	public String SECURITY_PARAM_BINDING_ANNOTATION_TYPE_NAME = "org.apache.deltaspike.security.api.authorization.SecurityParameterBinding"; //$NON-NLS-1$

	public String MESSAGE_BUNDLE_ANNOTATION_KIND = "messageBundleAnnotation"; //$NON-NLS-1$
	public String SECURITY_BINDING_ANNOTATION_KIND = "securityBindingAnnotation"; //$NON-NLS-1$
	public String SECURES_ANNOTATION_KIND = "securesAnnotation"; //$NON-NLS-1$

	public String INVOCATION_HANDLER_TYPE = "java.lang.reflect.InvocationHandler";
	public String PARTIALBEAN_BINDING_ANNOTATION_TYPE_NAME = "org.apache.deltaspike.partialbean.api.PartialBeanBinding"; //$NON-NLS-1$
	public String PARTIALBEAN_BINDING_ANNOTATION_KIND = "partialbeanBindingAnnotation"; //$NON-NLS-1$
}
