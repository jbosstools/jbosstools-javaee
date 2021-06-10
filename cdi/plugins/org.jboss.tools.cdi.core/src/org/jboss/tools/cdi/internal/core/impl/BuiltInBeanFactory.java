/*******************************************************************************
 * Copyright (c) 2011-2018 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.cdi.internal.core.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jdt.core.IType;
import org.jboss.tools.cdi.core.CDIConstants;
import org.jboss.tools.cdi.core.IBuiltInBean;
import org.jboss.tools.cdi.core.IScope;
import org.jboss.tools.cdi.core.WeldConstants;
import org.jboss.tools.cdi.internal.core.impl.definition.TypeDefinition;
import org.jboss.tools.common.java.impl.AnnotationLiteral;

/**
 * 3.6. Additional built-in beans.
 * A Java EE or embeddable EJB container must provide the following built-in beans, all of which 
 * have qualifier @Default, scope @ Dependent, and have no bean EL name:
 * UserTransaction, Principal, VlidationFactory, Validator, BeanManager.
 * 
 * 6.7.5. The Conversation interface
 * The container provides a built-in bean with bean type Conversation, scope @RequestScoped, and qualifier @Default,
 * named javax.enterprise.context.conversation.
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class BuiltInBeanFactory {

	public static final Set<String> BUILT_IN = new HashSet<String>();
	static final Map<String, BuiltInBeanInfo> BUILT_IN_INFO = new HashMap<String, BuiltInBeanInfo>();

	static {
		BUILT_IN.add(CDIConstants.USER_TRANSACTION_TYPE_NAME);
		BUILT_IN.add(CDIConstants.PRINCIPAL_TYPE_NAME);
		BUILT_IN.add(CDIConstants.VALIDATION_FACTORY_TYPE_NAME);
		BUILT_IN.add(CDIConstants.VALIDATOR_TYPE_NAME);
		BUILT_IN.add(CDIConstants.BEAN_MANAGER_TYPE_NAME);
		BUILT_IN.add(CDIConstants.CONVERSATION_TYPE_NAME);
		BUILT_IN.add(CDIConstants.INJECTIONPOINT_TYPE_NAME);
		BUILT_IN.add(CDIConstants.HTTP_SESSION_TYPE_NAME);
		BUILT_IN.add(CDIConstants.HTTP_SERVLET_REQUEST_TYPE_NAME);
		BUILT_IN.add(CDIConstants.HTTP_SERVLET_CONTEXT_TYPE_NAME);
		
		BUILT_IN.add(CDIConstants.JSF_RESOURCE_HANDLER);
		BUILT_IN.add(CDIConstants.JSF_EXTERNAL_CONTEXT);
		BUILT_IN.add(CDIConstants.JSF_FACES_CONTEXT);
		BUILT_IN.add(CDIConstants.JSF_FLASH);

		BUILT_IN.add(CDIConstants.JMS_CONTEXT_TYPE_NAME);
		
		BUILT_IN.add(WeldConstants.DEPENDENT_CONTEXT_TYPE);
		
		addInfo(CDIConstants.JAVA_UTIL_MAP, null, CDIConstants.JSF_APPLICATION_MAP);
		addInfo(CDIConstants.JAVA_UTIL_MAP, null, CDIConstants.JSF_COOKIE_MAP);
		addInfo(CDIConstants.JAVA_UTIL_MAP, null, CDIConstants.JSF_FLOW_MAP);
		addInfo(CDIConstants.JAVA_UTIL_MAP, null, CDIConstants.JSF_HEADER_MAP);
		addInfo(CDIConstants.JAVA_UTIL_MAP, null, CDIConstants.JSF_HEADER_VALUES_MAP);
		addInfo(CDIConstants.JAVA_UTIL_MAP, null, CDIConstants.JSF_INIT_PARAMETER_MAP);
		addInfo(CDIConstants.JAVA_UTIL_MAP, null, CDIConstants.JSF_REQUEST_PARAMETER_MAP);
		addInfo(CDIConstants.JAVA_UTIL_MAP, null, CDIConstants.JSF_REQUEST_PARAMETER_VALUES_MAP);
		addInfo(CDIConstants.JAVA_UTIL_MAP, null, CDIConstants.JSF_REQUEST_MAP);
		addInfo(CDIConstants.JAVA_UTIL_MAP, null, CDIConstants.JSF_SESSION_MAP);
		addInfo(CDIConstants.JAVA_UTIL_MAP, null, CDIConstants.JSF_VIEW_MAP);

		addInfo(WeldConstants.SINGLETON_CONTEXT_TYPE, 
				CDIConstants.SINGLETON_SCOPED_ANNOTATION_TYPE_NAME,
				null);
		addInfo(WeldConstants.APPLICATION_CONTEXT_TYPE, 
				CDIConstants.APPLICATION_SCOPED_ANNOTATION_TYPE_NAME,
				null);
		addInfo(WeldConstants.REQUEST_CONTEXT_TYPE, 
				CDIConstants.REQUEST_SCOPED_ANNOTATION_TYPE_NAME,
				WeldConstants.UNBOUND_QUALIFIER_TYPE).defaultQualifier = false;
		addInfo(WeldConstants.BOUND_REQUEST_CONTEXT_TYPE, 
				CDIConstants.REQUEST_SCOPED_ANNOTATION_TYPE_NAME,
				WeldConstants.BOUND_QUALIFIER_TYPE);
		addInfo(WeldConstants.HTTP_REQUEST_CONTEXT_TYPE, 
				CDIConstants.REQUEST_SCOPED_ANNOTATION_TYPE_NAME,
				WeldConstants.HTTP_QUALIFIER_TYPE);
		addInfo(WeldConstants.EJB_REQUEST_CONTEXT_TYPE, 
				CDIConstants.REQUEST_SCOPED_ANNOTATION_TYPE_NAME,
				WeldConstants.EJB_QUALIFIER_TYPE);
		addInfo(WeldConstants.BOUND_SESSION_CONTEXT_TYPE, 
				CDIConstants.SESSION_SCOPED_ANNOTATION_TYPE_NAME,
				WeldConstants.BOUND_QUALIFIER_TYPE);
		addInfo(WeldConstants.HTTP_SESSION_CONTEXT_TYPE, 
				CDIConstants.SESSION_SCOPED_ANNOTATION_TYPE_NAME,
				WeldConstants.HTTP_QUALIFIER_TYPE);
		addInfo(WeldConstants.BOUND_CONVERSATION_CONTEXT_TYPE, 
				CDIConstants.CONVERSATION_SCOPED_ANNOTATION_TYPE_NAME,
				WeldConstants.BOUND_QUALIFIER_TYPE);
		addInfo(WeldConstants.HTTP_CONVERSATION_CONTEXT_TYPE, 
				CDIConstants.CONVERSATION_SCOPED_ANNOTATION_TYPE_NAME,
				WeldConstants.HTTP_QUALIFIER_TYPE);
		
		BUILT_IN.add(CDIConstants.MICROPROFILE_CONFIG_CONFIG_TYPE);
		addInfo(Boolean.class.getName(), null, CDIConstants.MICROPROFILE_CONFIG_CONFIG_PROPERTY_TYPE);
		addInfo(Byte.class.getName(), null, CDIConstants.MICROPROFILE_CONFIG_CONFIG_PROPERTY_TYPE);
		addInfo(Short.class.getName(), null, CDIConstants.MICROPROFILE_CONFIG_CONFIG_PROPERTY_TYPE);
		addInfo(Integer.class.getName(), null, CDIConstants.MICROPROFILE_CONFIG_CONFIG_PROPERTY_TYPE);
		addInfo(Long.class.getName(), null, CDIConstants.MICROPROFILE_CONFIG_CONFIG_PROPERTY_TYPE);
		addInfo(Float.class.getName(), null, CDIConstants.MICROPROFILE_CONFIG_CONFIG_PROPERTY_TYPE);
		addInfo(Double.class.getName(), null, CDIConstants.MICROPROFILE_CONFIG_CONFIG_PROPERTY_TYPE);
		addInfo(Character.class.getName(), null, CDIConstants.MICROPROFILE_CONFIG_CONFIG_PROPERTY_TYPE);
		addInfo(Class.class.getName(), null, CDIConstants.MICROPROFILE_CONFIG_CONFIG_PROPERTY_TYPE);
		addInfo(String.class.getName(), null, CDIConstants.MICROPROFILE_CONFIG_CONFIG_PROPERTY_TYPE);
		addInfo(CDIConstants.MICROPROFILE_CONFIG_CONFIG_VALUE_TYPE, null, CDIConstants.MICROPROFILE_CONFIG_CONFIG_PROPERTY_TYPE);

	   BUILT_IN.add(CDIConstants.MICROPROFILE_JWT_JSONWEBTOKEN_TYPE);
     addInfo(String.class.getName(), null, CDIConstants.MICROPROFILE_JWT_CLAIM_TYPE);
     addInfo(Long.class.getName(), null, CDIConstants.MICROPROFILE_JWT_CLAIM_TYPE);
	   addInfo(Boolean.class.getName(), null, CDIConstants.MICROPROFILE_JWT_CLAIM_TYPE);
	   addInfo(CDIConstants.JAVAX_JSON_JSON_VALUE_TYPE, null, CDIConstants.MICROPROFILE_JWT_CLAIM_TYPE);
	   addInfo(CDIConstants.JAVAX_JSON_JSON_STRING_TYPE, null, CDIConstants.MICROPROFILE_JWT_CLAIM_TYPE);
	   addInfo(CDIConstants.JAVAX_JSON_JSON_NUMBER_TYPE, null, CDIConstants.MICROPROFILE_JWT_CLAIM_TYPE);
	   addInfo(CDIConstants.JAVAX_JSON_JSON_ARRAY_TYPE, null, CDIConstants.MICROPROFILE_JWT_CLAIM_TYPE);
	   addInfo(CDIConstants.JAVAX_JSON_JSON_OBJECT_TYPE, null, CDIConstants.MICROPROFILE_JWT_CLAIM_TYPE);

     BUILT_IN.add(CDIConstants.MICROPROFILE_METRICS_METRIC_REGISTRY_TYPE);
     addInfo(CDIConstants.MICROPROFILE_METRICS_METRIC_REGISTRY_TYPE, null, CDIConstants.MICROPROFILE_METRICS_METRIC_REGISTRY_TYPE_TYPE);
     addInfo(CDIConstants.MICROPROFILE_METRICS_METER_TYPE, null, CDIConstants.MICROPROFILE_METRICS_METRIC_TYPE);
     addInfo(CDIConstants.MICROPROFILE_METRICS_TIMER_TYPE, null, CDIConstants.MICROPROFILE_METRICS_METRIC_TYPE);
     addInfo(CDIConstants.MICROPROFILE_METRICS_SIMPLE_TIMER_TYPE, null, CDIConstants.MICROPROFILE_METRICS_METRIC_TYPE);
     addInfo(CDIConstants.MICROPROFILE_METRICS_COUNTER_TYPE, null, CDIConstants.MICROPROFILE_METRICS_METRIC_TYPE);
     addInfo(CDIConstants.MICROPROFILE_METRICS_HISTOGRAM_TYPE, null, CDIConstants.MICROPROFILE_METRICS_METRIC_TYPE);
     
     BUILT_IN.add(CDIConstants.MICROPROFILE_OPEN_TRACING_TRACER_TYPE);
     
}

	static BuiltInBeanInfo addInfo(String type, String scopeName, String qualifierName) {
		BUILT_IN.add(type);
		BuiltInBeanInfo info = BUILT_IN_INFO.get(type);
		if (info == null) {
		  info = new BuiltInBeanInfo(scopeName, qualifierName);
	    BUILT_IN_INFO.put(type, info);
		} else {
		  info.qualifierName.add(qualifierName);
		}
		return info;
	}

	public static boolean isBuiltIn(IType type) {
		return type != null && BUILT_IN.contains(type.getFullyQualifiedName());
	}

	public static ClassBean newClassBean(CDIProject project, TypeDefinition def) {
		ClassBean result = null;
		String typeName = def.getType().getFullyQualifiedName();
		if(typeName.equals(CDIConstants.CONVERSATION_TYPE_NAME)) {
			result = new ConversationBuiltInBean();
		} else {
			BuiltInBean b = new BuiltInBean();
			BuiltInBeanInfo info = BUILT_IN_INFO.get(typeName);
			if(info != null) {
				if(info.scopeName != null) {
					b.scopeName = info.scopeName;
				}
				if(info.qualifierName != null) {
				  info.qualifierName.forEach(qualifierName -> addAnnotation(project, def, qualifierName));
					if(info.defaultQualifier) {
						addAnnotation(project, def, CDIConstants.DEFAULT_QUALIFIER_TYPE_NAME);
					}
				}
			}
			result = b;
		}
		result.setParent(project);
		result.setDefinition(def);
		return result;
	}

	private static void addAnnotation(CDIProject project, TypeDefinition def, String typeName) {
		if(def.getAnnotation(typeName) == null) {
			IType t = project.getNature().getType(typeName);
			if(t != null) {
				AnnotationLiteral l = new AnnotationLiteral(project.getResource(), 0, 0, null, 0, t);
				def.addAnnotation(l, project.getNature().getDefinitions());
			}
		}
	}

}


class ConversationBuiltInBean extends ClassBean implements IBuiltInBean {
	ConversationBuiltInBean() {
	}

	public String getName() {
		return "javax.enterprise.context.conversation";
	}

	public IScope getScope() {
		return getCDIProject().getScope(CDIConstants.REQUEST_SCOPED_ANNOTATION_TYPE_NAME);
	}

}

class BuiltInBean extends ClassBean implements IBuiltInBean {
	String scopeName = CDIConstants.DEPENDENT_ANNOTATION_TYPE_NAME;
	
	public BuiltInBean() {
	}

	public IScope getScope() {
		return getCDIProject().getScope(scopeName);
	}

	public String getName() {
		return null;
	}
}

class BuiltInBeanInfo {
	String scopeName;
	List<String> qualifierName = new ArrayList<>();
	boolean defaultQualifier = true;

	BuiltInBeanInfo(String scopeName, String qualifierName) {
		this.scopeName = scopeName;
		this.qualifierName.add(qualifierName);
	}

	public String getScopeName() {
		return scopeName;
	}

	public List<String> getQualifierName() {
		return qualifierName;
	}
}
