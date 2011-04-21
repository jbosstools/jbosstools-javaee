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
package org.jboss.tools.cdi.core;

public interface CDIConstants {
	public String INHERITED_ANNOTATION_TYPE_NAME = "java.lang.annotation.Inherited";
	public String TARGET_ANNOTATION_TYPE_NAME = "java.lang.annotation.Target";
	public String RETENTION_ANNOTATION_TYPE_NAME = "java.lang.annotation.Retention";
	
	public String QUALIFIER_ANNOTATION_TYPE_NAME = "javax.inject.Qualifier";

	public String NAMED_QUALIFIER_TYPE_NAME = "javax.inject.Named";
	public String ANY_QUALIFIER_TYPE_NAME = "javax.enterprise.inject.Any";
	public String DEFAULT_QUALIFIER_TYPE_NAME = "javax.enterprise.inject.Default";
	public String NEW_QUALIFIER_TYPE_NAME = "javax.enterprise.inject.New";

	public String STEREOTYPE_ANNOTATION_TYPE_NAME = "javax.enterprise.inject.Stereotype";
	public String MODEL_STEREOTYPE_TYPE_NAME = "javax.enterprise.inject.Model";
	public String DECORATOR_STEREOTYPE_TYPE_NAME = "javax.decorator.Decorator";
	public String DELEGATE_STEREOTYPE_TYPE_NAME = "javax.decorator.Delegate";

	public String TYPED_ANNOTATION_TYPE_NAME = "javax.enterprise.inject.Typed";

	public String PRODUCES_ANNOTATION_TYPE_NAME = "javax.enterprise.inject.Produces";

	public String SCOPE_ANNOTATION_TYPE_NAME = "javax.inject.Scope";
	public String NORMAL_SCOPE_ANNOTATION_TYPE_NAME = "javax.enterprise.context.NormalScope";

	public String PROVIDER_ANNOTATION_TYPE_NAME = "javax.inject.Provider";

	public String INJECT_ANNOTATION_TYPE_NAME = "javax.inject.Inject";

	public String ALTERNATIVE_ANNOTATION_TYPE_NAME = "javax.enterprise.inject.Alternative";

	public String INTERCEPTOR_BINDING_ANNOTATION_TYPE_NAME = "javax.interceptor.InterceptorBinding";
	public String INTERCEPTOR_ANNOTATION_TYPE_NAME = "javax.interceptor.Interceptor";

	public String APPLICATION_SCOPED_ANNOTATION_TYPE_NAME = "javax.enterprise.context.ApplicationScoped";
	public String CONVERSATION_SCOPED_ANNOTATION_TYPE_NAME = "javax.enterprise.context.ConversationScoped";
	public String REQUEST_SCOPED_ANNOTATION_TYPE_NAME = "javax.enterprise.context.RequestScoped";
	public String SESSION_SCOPED_ANNOTATION_TYPE_NAME = "javax.enterprise.context.SessionScoped";
	public String DEPENDENT_ANNOTATION_TYPE_NAME = "javax.enterprise.context.Dependent";

	public String SPECIALIZES_ANNOTATION_TYPE_NAME = "javax.enterprise.inject.Specializes";

	public String NON_BINDING_ANNOTATION_TYPE_NAME ="javax.enterprise.util.Nonbinding";

	public String STATEFUL_ANNOTATION_TYPE_NAME = "javax.ejb.Stateful";
	public String STATELESS_ANNOTATION_TYPE_NAME = "javax.ejb.Stateless";
	public String SINGLETON_ANNOTATION_TYPE_NAME = "javax.ejb.Singleton";
	public String LOCAL_ANNOTATION_TYPE_NAME = "javax.ejb.Local";
	public String LOCAL_BEAN_SIMPLE_NAME = "LocalBean";
	public String LOCAL_BEAN_ANNOTATION_TYPE_NAME = "javax.ejb.LocalBean";

	public String RESOURCE_ANNOTATION_TYPE_NAME = "javax.annotation.Resource";
	public String WEB_SERVICE_REF_ANNOTATION_TYPE_NAME = "javax.xml.ws.WebServiceRef";
	public String EJB_ANNOTATION_TYPE_NAME = "javax.ejb.EJB";
	public String PERSISTENCE_CONTEXT_ANNOTATION_TYPE_NAME = "javax.persistence.PersistenceContext";
	public String PERSISTENCE_UNIT_ANNOTATION_TYPE_NAME = "javax.persistence.PersistenceUnit";

	public String DISPOSES_ANNOTATION_TYPE_NAME = "javax.enterprise.inject.Disposes";
	public String OBSERVERS_ANNOTATION_TYPE_NAME = "javax.enterprise.event.Observes";

	public String INJECTIONPOINT_TYPE_NAME = "javax.enterprise.inject.spi.InjectionPoint";

	public String DECORATOR_SIMPLE_NAME = "Decorator";
	public String DECORATOR_TYPE_NAME = "javax.enterprise.inject.spi.Decorator";
	public String INTERCEPTOR_SIMPLE_NAME = "Interceptor";
	public String INTERCEPTOR_TYPE_NAME = "javax.enterprise.inject.spi.Interceptor";

	public String INSTANCE_TYPE_NAME = "javax.enterprise.inject.Instance";

	public String PRE_DESTROY_TYPE_NAME = "javax.annotation.PreDestroy";
	public String POST_CONSTRUCTOR_TYPE_NAME = "javax.annotation.PostConstruct";

	public String EVENT_TYPE_NAME = "javax.enterprise.event.Event";

	public String USER_TRANSACTION_TYPE_NAME = "javax.transaction.UserTransaction";
	public String PRINCIPAL_TYPE_NAME = "java.security.Principal";
	public String VALIDATION_FACTORY_TYPE_NAME = "javax.validation.ValidatorFactory";
	public String VALIDATOR_TYPE_NAME = "javax.validation.Validator";
	public String BEAN_MANAGER_TYPE_NAME = "javax.enterprise.inject.spi.BeanManager";

	public String ANNOTATION_LITERAL_TYPE_NAME = "javax.enterprise.util.AnnotationLiteral";

	public String WELD_BEAN_MANAGER_TYPE_NAME = "org.jboss.weld.manager.BeanManagerImpl";

}