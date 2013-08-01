/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package test;

import javax.enterprise.inject.Default;
import javax.inject.Inject;

import org.jboss.weld.context.ApplicationContext;
import org.jboss.weld.context.ConversationContext;
import org.jboss.weld.context.DependentContext;
import org.jboss.weld.context.RequestContext;
import org.jboss.weld.context.SessionContext;
import org.jboss.weld.context.SingletonContext;
import org.jboss.weld.context.bound.Bound;
import org.jboss.weld.context.bound.BoundConversationContext;
import org.jboss.weld.context.bound.BoundRequestContext;
import org.jboss.weld.context.bound.BoundSessionContext;
import org.jboss.weld.context.ejb.Ejb;
import org.jboss.weld.context.ejb.EjbRequestContext;
import org.jboss.weld.context.http.Http;
import org.jboss.weld.context.http.HttpConversationContext;
import org.jboss.weld.context.http.HttpRequestContext;
import org.jboss.weld.context.http.HttpSessionContext;
import org.jboss.weld.context.unbound.Unbound;

public class Test {

	//Bean @Default DependentContext is injected
	@Inject DependentContext dependentContext;

	//Bean @Default ApplicationContext is injected
	@Inject ApplicationContext applicationContext;

	//Bean @Default SingletonContext is injected
	@Inject SingletonContext singletonContext;

	//Bean @Unbound RequestContext is injected
	@Inject @Unbound RequestContext unboundRequestContext;

	//Bean @Bound @Default BoundRequestContext is injected
	@Inject @Bound RequestContext boundRequestContext;
	@Inject @Default BoundRequestContext boundRequestContext2;

	//Bean @Http @Default HttpRequestContext is injected
	@Inject @Http RequestContext httpRequestContext;
	@Inject @Default HttpRequestContext httpRequestContext2;
	
	//Bean @Ejb @Default EjbRequestContext is injected
	@Inject @Ejb RequestContext ejbRequestContext;
	@Inject @Default EjbRequestContext ejbRequestContext2;

	//Bean BoundConversationContext is injected
	@Inject @Bound ConversationContext boundConversationContext;
	@Inject @Default BoundConversationContext boundConversationContext2; 
	
	//Bean HttpConversationContext is injected
	@Inject @Http ConversationContext httpConversationContext;
	@Inject @Default HttpConversationContext httpConversationContext2; 

	//Bean @Bound @Default BoundSessionContext is injected
	@Inject @Bound SessionContext boundSessionContext;
	@Inject @Default BoundSessionContext boundSessionContext2; 

	//Bean @Http @Default HttpSessionContext is injected
	@Inject @Http SessionContext httpSessionContext;
	@Inject @Default HttpSessionContext httpSessionContext2; 

	//Invalid: 3 beans are available for injection.
	@Inject RequestContext invalidRequestContext;

	//Invalid: 2 beans are available for injection.
	@Inject ConversationContext invalidConversationContext;

	//Invalid: 2 beans are available for injection.
	@Inject SessionContext invalidSessionContext;

}
