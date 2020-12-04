/******************************************************************************* 
 * Copyright (c) 2009-2018 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.cdi.core;

import java.util.Optional;

public interface CDIConstants {
	public static final int CDI_VERSION_NONE = 0;
	public static final int CDI_VERSION_1_0 = 10;
	public static final int CDI_VERSION_1_1 = 11;
	
	public static final String ANNOTATED_TYPE_CONFIGURATOR="javax.enterprise.inject.spi.configurator.AnnotatedTypeConfigurator";

	public static final String INHERITED_ANNOTATION_TYPE_NAME = "java.lang.annotation.Inherited";
	public static final String TARGET_ANNOTATION_TYPE_NAME = "java.lang.annotation.Target";
	public static final String RETENTION_ANNOTATION_TYPE_NAME = "java.lang.annotation.Retention";
	
	public static final String RETENTION_POLICY_RUNTIME_TYPE_NAME = "java.lang.annotation.RetentionPolicy.RUNTIME";
	
	public static final String QUALIFIER_ANNOTATION_TYPE_NAME = "javax.inject.Qualifier";

	public static final String NAMED_QUALIFIER_TYPE_NAME = "javax.inject.Named";
	public static final String ANY_QUALIFIER_TYPE_NAME = "javax.enterprise.inject.Any";
	public static final String DEFAULT_QUALIFIER_TYPE_NAME = "javax.enterprise.inject.Default";
	public static final String NEW_QUALIFIER_TYPE_NAME = "javax.enterprise.inject.New";

	public static final String VETOED_ANNOTATION_TYPE_NAME = "javax.enterprise.inject.Vetoed";
	public static final String PRIORITY_ANNOTATION_TYPE_NAME = "javax.annotation.Priority";

	public static final String STEREOTYPE_ANNOTATION_TYPE_NAME = "javax.enterprise.inject.Stereotype";
	public static final String MODEL_STEREOTYPE_TYPE_NAME = "javax.enterprise.inject.Model";
	public static final String DECORATOR_STEREOTYPE_TYPE_NAME = "javax.decorator.Decorator";
	public static final String DELEGATE_STEREOTYPE_TYPE_NAME = "javax.decorator.Delegate";

	public static final String TYPED_ANNOTATION_TYPE_NAME = "javax.enterprise.inject.Typed";

	public static final String PRODUCES_ANNOTATION_TYPE_NAME = "javax.enterprise.inject.Produces";

	public static final String SCOPE_ANNOTATION_TYPE_NAME = "javax.inject.Scope";
	public static final String NORMAL_SCOPE_ANNOTATION_TYPE_NAME = "javax.enterprise.context.NormalScope";

	public static final String INJECT_ANNOTATION_TYPE_NAME = "javax.inject.Inject";

	public static final String ALTERNATIVE_ANNOTATION_TYPE_NAME = "javax.enterprise.inject.Alternative";

	public static final String INTERCEPTOR_BINDING_ANNOTATION_TYPE_NAME = "javax.interceptor.InterceptorBinding";
	public static final String INTERCEPTOR_ANNOTATION_TYPE_NAME = "javax.interceptor.Interceptor";

	public static final String SINGLETON_SCOPED_ANNOTATION_TYPE_NAME = "javax.inject.Singleton";
	public static final String APPLICATION_SCOPED_ANNOTATION_TYPE_NAME = "javax.enterprise.context.ApplicationScoped";
	public static final String CONVERSATION_SCOPED_ANNOTATION_TYPE_NAME = "javax.enterprise.context.ConversationScoped";
	public static final String REQUEST_SCOPED_ANNOTATION_TYPE_NAME = "javax.enterprise.context.RequestScoped";
	public static final String SESSION_SCOPED_ANNOTATION_TYPE_NAME = "javax.enterprise.context.SessionScoped";
	public static final String DEPENDENT_ANNOTATION_TYPE_NAME = "javax.enterprise.context.Dependent";

	public static final String CONVERSATION_TYPE_NAME = "javax.enterprise.context.Conversation";
	public static final String CONVERSATION_BEAN_NAME = CONVERSATION_TYPE_NAME.toLowerCase();

	public static final String SPECIALIZES_ANNOTATION_TYPE_NAME = "javax.enterprise.inject.Specializes";

	public static final String NON_BINDING_ANNOTATION_TYPE_NAME ="javax.enterprise.util.Nonbinding";

	public static final String STATEFUL_ANNOTATION_TYPE_NAME = "javax.ejb.Stateful";
	public static final String STATELESS_ANNOTATION_TYPE_NAME = "javax.ejb.Stateless";
	public static final String SINGLETON_ANNOTATION_TYPE_NAME = "javax.ejb.Singleton";
	public static final String LOCAL_ANNOTATION_TYPE_NAME = "javax.ejb.Local";
	public static final String LOCAL_BEAN_SIMPLE_NAME = "LocalBean";
	public static final String LOCAL_BEAN_ANNOTATION_TYPE_NAME = "javax.ejb.LocalBean";

	public static final String RESOURCE_ANNOTATION_TYPE_NAME = "javax.annotation.Resource";
	public static final String WEB_SERVICE_REF_ANNOTATION_TYPE_NAME = "javax.xml.ws.WebServiceRef";
	public static final String EJB_ANNOTATION_TYPE_NAME = "javax.ejb.EJB";
	public static final String PERSISTENCE_CONTEXT_ANNOTATION_TYPE_NAME = "javax.persistence.PersistenceContext";
	public static final String PERSISTENCE_UNIT_ANNOTATION_TYPE_NAME = "javax.persistence.PersistenceUnit";

	public static final String DISPOSES_ANNOTATION_TYPE_NAME = "javax.enterprise.inject.Disposes";
	public static final String OBSERVERS_ANNOTATION_TYPE_NAME = "javax.enterprise.event.Observes";
	public static final String OBSERVERS_ASYNC_ANNOTATION_TYPE_NAME = "javax.enterprise.event.ObservesAsync";

	public static final String INJECTIONPOINT_TYPE_NAME = "javax.enterprise.inject.spi.InjectionPoint";

	public static final String DECORATOR_SIMPLE_NAME = "Decorator";
	public static final String DECORATOR_TYPE_NAME = "javax.enterprise.inject.spi.Decorator";
	public static final String INTERCEPTOR_SIMPLE_NAME = "Interceptor";
	public static final String INTERCEPTOR_TYPE_NAME = "javax.enterprise.inject.spi.Interceptor";

	public static final String PROVIDER_TYPE_NAME = "javax.inject.Provider";
	public static final String INSTANCE_TYPE_NAME = "javax.enterprise.inject.Instance";

	public static final String PRE_DESTROY_TYPE_NAME = "javax.annotation.PreDestroy";
	public static final String POST_CONSTRUCTOR_TYPE_NAME = "javax.annotation.PostConstruct";

	public static final String EVENT_TYPE_NAME = "javax.enterprise.event.Event";

	public static final String USER_TRANSACTION_TYPE_NAME = "javax.transaction.UserTransaction";
	public static final String PRINCIPAL_TYPE_NAME = "java.security.Principal";
	public static final String VALIDATION_FACTORY_TYPE_NAME = "javax.validation.ValidatorFactory";
	public static final String VALIDATOR_TYPE_NAME = "javax.validation.Validator";
	public static final String BEAN_MANAGER_TYPE_NAME = "javax.enterprise.inject.spi.BeanManager";

	public static final String HTTP_SESSION_TYPE_NAME = "javax.servlet.http.HttpSession";
	public static final String HTTP_SERVLET_REQUEST_TYPE_NAME = "javax.servlet.http.HttpServletRequest";
	public static final String HTTP_SERVLET_CONTEXT_TYPE_NAME = "javax.servlet.ServletContext";

	public static final String ANNOTATION_LITERAL_TYPE_NAME = "javax.enterprise.util.AnnotationLiteral";

	public static final String WELD_BEAN_MANAGER_TYPE_NAME = "org.jboss.weld.manager.BeanManagerImpl";

	public static final String ELEMENT_TYPE_TYPE_NAME = "java.lang.annotation.ElementType.TYPE";
	public static final String ELEMENT_TYPE_METHOD_NAME = "java.lang.annotation.ElementType.METHOD";
	public static final String ELEMENT_TYPE_FIELD_NAME = "java.lang.annotation.ElementType.FIELD";
	public static final String ELEMENT_TYPE_PARAMETER_NAME = "java.lang.annotation.ElementType.PARAMETER";
	public static final String JAVA_UTIL_MAP = "java.util.map";
  public static final String OPTIONAL_TYPE_NAME = Optional.class.getName();

	public static final String JMS_CONTEXT_TYPE_NAME = "javax.jms.JMSContext";
	
	public static final String JSF_RESOURCE_HANDLER = "javax.faces.application.ResourceHandler";
	public static final String JSF_EXTERNAL_CONTEXT = "javax.faces.context.ExternalContext";
	public static final String JSF_FACES_CONTEXT = "javax.faces.context.FacesContext";
	public static final String JSF_FLASH ="javax.faces.context.Flash";
	public static final String JSF_APPLICATION_MAP = "javax.faces.annotation.ApplicationMap";
	public static final String JSF_COOKIE_MAP = "javax.faces.annotation.RequestCookieMap";
	public static final String JSF_FLOW_MAP = "javax.faces.annotation.FlowMap";
	public static final String JSF_HEADER_MAP = "javax.faces.annotation.HeaderMap";
	public static final String JSF_HEADER_VALUES_MAP = "javax.faces.annotation.HeaderValuesMap";
	public static final String JSF_INIT_PARAMETER_MAP = "javax.faces.annotation.InitParameterMap";
	public static final String JSF_REQUEST_PARAMETER_MAP = "javax.faces.annotation.RequestParameterMap";
	public static final String JSF_REQUEST_PARAMETER_VALUES_MAP = "javax.faces.annotation.RequestParameterValuesMap";
	public static final String JSF_REQUEST_MAP = "javax.faces.annotation.RequestMap";
	public static final String JSF_SESSION_MAP = "javax.faces.annotation.SessionMap";
	public static final String JSF_VIEW_MAP = "javax.faces.annotation.ViewMap";
	
	/*
	 * Microprofile Config
	 */
  public static final String MICROPROFILE_CONFIG_CONFIG_TYPE = "org.eclipse.microprofile.config.Config";
  public static final String MICROPROFILE_CONFIG_CONFIG_PROPERTY_TYPE = "org.eclipse.microprofile.config.inject.ConfigProperty";
  public static final String MICROPROFILE_CONFIG_CONFIG_VALUE_TYPE = "org.eclipse.microprofile.config.ConfigValue";
  
  /*
   * Microprofile JWT
   */
  public static final String MICROPROFILE_JWT_JSONWEBTOKEN_TYPE = "org.eclipse.microprofile.jwt.JsonWebToken";
  public static final String MICROPROFILE_JWT_CLAIM_TYPE = "org.eclipse.microprofile.jwt.Claim";
  public static final String JAVAX_JSON_JSON_VALUE_TYPE = "javax.json.JsonValue";
  public static final String JAVAX_JSON_JSON_STRING_TYPE = "javax.json.JsonString";
  public static final String JAVAX_JSON_JSON_NUMBER_TYPE = "javax.json.JsonNumber";
  public static final String JAVAX_JSON_JSON_ARRAY_TYPE = "javax.json.JsonArray";
  public static final String JAVAX_JSON_JSON_OBJECT_TYPE = "javax.json.JsonObject";
  
  /*
   * Microprofile Metrics
   */
  public static final String MICROPROFILE_METRICS_METRIC_REGISTRY_TYPE = "org.eclipse.microprofile.metrics.MetricRegistry";
  public static final String MICROPROFILE_METRICS_METRIC_REGISTRY_TYPE_TYPE = "org.eclipse.microprofile.metrics.annotation.RegistryType";
  public static final String MICROPROFILE_METRICS_METRIC_TYPE = "org.eclipse.microprofile.metrics.annotation.Metric";
  public static final String MICROPROFILE_METRICS_METER_TYPE = "org.eclipse.microprofile.metrics.Meter";
  public static final String MICROPROFILE_METRICS_TIMER_TYPE = "org.eclipse.microprofile.metrics.Timer";
  public static final String MICROPROFILE_METRICS_SIMPLE_TIMER_TYPE = "org.eclipse.microprofile.metrics.SimpleTimer";
  public static final String MICROPROFILE_METRICS_COUNTER_TYPE = "org.eclipse.microprofile.metrics.Counter";
  public static final String MICROPROFILE_METRICS_HISTOGRAM_TYPE = "org.eclipse.microprofile.metrics.Histogram";
  
  /*
   * Microprofile OpenTracing
   */
  public static final String MICROPROFILE_OPEN_TRACING_TRACER_TYPE = "io.opentracing.Tracer";
}