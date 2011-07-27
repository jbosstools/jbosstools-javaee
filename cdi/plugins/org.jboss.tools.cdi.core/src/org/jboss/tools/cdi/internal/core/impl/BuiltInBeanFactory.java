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

import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IType;
import org.jboss.tools.cdi.core.CDIConstants;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.IScope;
import org.jboss.tools.common.java.IParametedType;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class BuiltInBeanFactory {

	static Set<String> BUILT_IN = new HashSet<String>();
	static {
		BUILT_IN.add(CDIConstants.USER_TRANSACTION_TYPE_NAME);
		BUILT_IN.add(CDIConstants.PRINCIPAL_TYPE_NAME);
		BUILT_IN.add(CDIConstants.VALIDATION_FACTORY_TYPE_NAME);
		BUILT_IN.add(CDIConstants.VALIDATOR_TYPE_NAME);
		BUILT_IN.add(CDIConstants.BEAN_MANAGER_TYPE_NAME);
		BUILT_IN.add(CDIConstants.CONVERSATION_TYPE_NAME);
	}

	public static boolean isBuiltIn(IType type) {
		return type != null && BUILT_IN.contains(type.getFullyQualifiedName());
	}

	public static IBean newBean(CDIProject project, IParametedType type, IPath contextPath) {
		BuiltInBean result = null;
		if(type.getType().getFullyQualifiedName().equals(CDIConstants.CONVERSATION_TYPE_NAME)) {
			result = new ConversationBuiltInBean(type);
		} else {
			result = new BuiltInBean(type);
		}
		result.setParent(project);
		result.setSourcePath(contextPath);
		return result;
	}

	static class ConversationBuiltInBean extends BuiltInBean {
		ConversationBuiltInBean(IParametedType type) {
			super(type);
		}

		public String getName() {
			return "javax.enterprise.context.conversation";
		}

		public IScope getScope() {
			return getCDIProject().getScope(CDIConstants.REQUEST_SCOPED_ANNOTATION_TYPE_NAME);
		}

	}

}
