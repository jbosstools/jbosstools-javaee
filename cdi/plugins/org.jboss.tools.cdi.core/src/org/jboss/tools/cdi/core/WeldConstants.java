/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.cdi.core;

/**
 * @author Viacheslav Kabanovich
 */
public interface WeldConstants {
	public String CONTEXT_PACK = "org.jboss.weld.context.";
	
	public String DEPENDENT_CONTEXT_TYPE = CONTEXT_PACK + "DependentContext";

	public String REQUEST_CONTEXT_TYPE = CONTEXT_PACK + "RequestContext";
	public String BOUND_REQUEST_CONTEXT_TYPE = CONTEXT_PACK + "bound.BoundRequestContext";
	public String HTTP_REQUEST_CONTEXT_TYPE = CONTEXT_PACK + "http.HttpRequestContext";
	public String EJB_REQUEST_CONTEXT_TYPE = CONTEXT_PACK + "ejb.EjbRequestContext";

	public String CONVERSATION_CONTEXT_TYPE = CONTEXT_PACK + "ConversationContext";
	public String BOUND_CONVERSATION_CONTEXT_TYPE = CONTEXT_PACK + "bound.BoundConversationContext";
	public String HTTP_CONVERSATION_CONTEXT_TYPE = CONTEXT_PACK + "http.HttpConversationContext";

	public String SESSION_CONTEXT_TYPE = CONTEXT_PACK + "SessionContext";
	public String BOUND_SESSION_CONTEXT_TYPE = CONTEXT_PACK + "bound.BoundSessionContext";
	public String HTTP_SESSION_CONTEXT_TYPE = CONTEXT_PACK + "http.HttpSessionContext";

	public String APPLICATION_CONTEXT_TYPE = CONTEXT_PACK + "ApplicationContext";

	public String SINGLETON_CONTEXT_TYPE = CONTEXT_PACK + "SingletonContext";

	public String UNBOUND_QUALIFIER_TYPE = CONTEXT_PACK + "unbound.Unbound";
	public String BOUND_QUALIFIER_TYPE = CONTEXT_PACK + "bound.Bound";
	public String EJB_QUALIFIER_TYPE = CONTEXT_PACK + "ejb.Ejb";
	public String HTTP_QUALIFIER_TYPE = CONTEXT_PACK + "http.Http";

	public String SINGLETON_SCOPE_TYPE = "javax.inject.Singleton"; //?

}
