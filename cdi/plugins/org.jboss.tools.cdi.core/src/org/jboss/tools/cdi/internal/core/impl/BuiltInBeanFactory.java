/*******************************************************************************
 * Copyright (c) 2011 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.cdi.internal.core.impl;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jdt.core.IType;
import org.jboss.tools.cdi.core.CDIConstants;
import org.jboss.tools.cdi.core.IBuiltInBean;
import org.jboss.tools.cdi.core.IScope;
import org.jboss.tools.cdi.internal.core.impl.definition.TypeDefinition;

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

	static {
		BUILT_IN.add(CDIConstants.USER_TRANSACTION_TYPE_NAME);
		BUILT_IN.add(CDIConstants.PRINCIPAL_TYPE_NAME);
		BUILT_IN.add(CDIConstants.VALIDATION_FACTORY_TYPE_NAME);
		BUILT_IN.add(CDIConstants.VALIDATOR_TYPE_NAME);
		BUILT_IN.add(CDIConstants.BEAN_MANAGER_TYPE_NAME);
		BUILT_IN.add(CDIConstants.CONVERSATION_TYPE_NAME);
		BUILT_IN.add(CDIConstants.INJECTIONPOINT_TYPE_NAME);
	}

	public static boolean isBuiltIn(IType type) {
		return type != null && BUILT_IN.contains(type.getFullyQualifiedName());
	}

	public static ClassBean newClassBean(CDIProject project, TypeDefinition def) {
		ClassBean result = null;
		if(def.getType().getFullyQualifiedName().equals(CDIConstants.CONVERSATION_TYPE_NAME)) {
			result = new ConversationBuiltInBean();
		} else {
			result = new BuiltInBean();
		}
		result.setParent(project);
		result.setDefinition(def);
		return result;
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
	
	public BuiltInBean() {
	}

	public IScope getScope() {
		return getCDIProject().getScope(CDIConstants.DEPENDENT_ANNOTATION_TYPE_NAME);
	}

	public String getName() {
		return null;
	}
}
