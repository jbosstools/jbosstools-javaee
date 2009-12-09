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

	public String TYPED_ANNOTATION_TYPE_NAME = "javax.enterprise.inject.Typed";

	public String PRODUCES_ANNOTATION_TYPE_NAME = "javax.enterprise.inject.Produces";

	public String SCOPE_ANNOTATION_TYPE_NAME = "javax.inject.Scope";
	public String NORMAL_SCOPE_ANNOTATION_TYPE_NAME = "javax.enterprise.context.NormalScope";

	public String PROVIDER_ANNOTATION_TYPE_NAME = "javax.inject.Provider";

	public String INJECT_ANNOTATION_TYPE_NAME = "javax.inject.Inject";

	public String ALTERNATIVE_ANNOTATION_TYPE_NAME = "javax.enterprise.inject.Alternative";

	public String INTERCEPTOR_BINDING_ANNOTATION_TYPE_NAME = "javax.interceptor.InterceptorBinding";

	public String APPLICATION_SCOPED_ANNOTATION_TYPE_NAME = "javax.enterprise.context.ApplicationScoped";
	public String CONVERSATION_SCOPED_ANNOTATION_TYPE_NAME = "javax.enterprise.context.ConversationScoped";
	public String REQUEST_SCOPED_ANNOTATION_TYPE_NAME = "javax.enterprise.context.RequestScoped";
	public String SESSION_SCOPED_ANNOTATION_TYPE_NAME = "javax.enterprise.context.SessionScoped";
	public String DEPENDENT_ANNOTATION_TYPE_NAME = "javax.enterprise.context.SessionScoped";

	public String SPECIALIZES_ANNOTATION_TYPE_NAME = "javax.enterprise.inject.Specializes";

	public String DELEGATE_STEREOTYPE_TYPE_NAME = "javax.decorator.Delegate";

}
