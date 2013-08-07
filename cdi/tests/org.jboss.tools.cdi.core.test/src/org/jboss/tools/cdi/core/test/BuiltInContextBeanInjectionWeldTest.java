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
package org.jboss.tools.cdi.core.test;

import java.util.Collection;

import junit.framework.TestCase;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.IType;
import org.jboss.tools.cdi.core.CDIConstants;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.IBuiltInBean;
import org.jboss.tools.cdi.core.ICDIProject;
import org.jboss.tools.cdi.core.IInjectionPointField;
import org.jboss.tools.cdi.core.IScope;
import org.jboss.tools.cdi.core.WeldConstants;

public class BuiltInContextBeanInjectionWeldTest extends TestCase {
	protected static String PLUGIN_ID = "org.jboss.tools.cdi.core.test";
	IProject project = null;
	ICDIProject cdi;
	String fileName = "src/test/Test.java";

	@Override
	public void setUp() throws Exception {
		project = ResourcesPlugin.getWorkspace().getRoot().getProject("weld1.1");
//		project.build(IncrementalProjectBuilder.INCREMENTAL_BUILD, null);
		cdi = CDICorePlugin.getCDIProject(project, true);
	}

	//@Inject DependentContext dependentContext
	public void testBuiltInDependentContextBean() {
		IInjectionPointField dependentContext = getInjectionPointField(cdi, fileName, "dependentContext");
		Collection<IBean> bs = cdi.getBeans(false, dependentContext);
		checkInjected(bs, WeldConstants.DEPENDENT_CONTEXT_TYPE, CDIConstants.DEPENDENT_ANNOTATION_TYPE_NAME);
	}

	//@Inject ApplicationContext applicationContext
	public void testBuiltInApplicationContextBean() {
		IInjectionPointField applicationContext = getInjectionPointField(cdi, fileName, "applicationContext");
		Collection<IBean> bs = cdi.getBeans(false, applicationContext);
		checkInjected(bs, WeldConstants.APPLICATION_CONTEXT_TYPE, CDIConstants.APPLICATION_SCOPED_ANNOTATION_TYPE_NAME);
	}

	//@Inject SingletonContext singletonContext
	public void testBuiltInSingletoContextBean() {
		IInjectionPointField singletonContext = getInjectionPointField(cdi, fileName, "singletonContext");
		Collection<IBean> bs = cdi.getBeans(false, singletonContext);
		checkInjected(bs, WeldConstants.SINGLETON_CONTEXT_TYPE, CDIConstants.SINGLETON_SCOPED_ANNOTATION_TYPE_NAME);
	}

	//@Inject @Unbound RequestContext unboundRequestContext
	public void testBuiltInUnboundContextBean() {
		IInjectionPointField unboundRequestContext = getInjectionPointField(cdi, fileName, "unboundRequestContext");
		Collection<IBean> bs = cdi.getBeans(false, unboundRequestContext);
		checkInjected(bs, WeldConstants.REQUEST_CONTEXT_TYPE, CDIConstants.REQUEST_SCOPED_ANNOTATION_TYPE_NAME);
	}

	//@Inject @Bound RequestContext boundRequestContext
	//@Inject @Default BoundRequestContext boundRequestContext2
	public void testBuiltInBoundRequestContextBean() {
		IInjectionPointField boundRequestContext = getInjectionPointField(cdi, fileName, "boundRequestContext");
		Collection<IBean> bs = cdi.getBeans(false, boundRequestContext);
		checkInjected(bs, WeldConstants.BOUND_REQUEST_CONTEXT_TYPE, CDIConstants.REQUEST_SCOPED_ANNOTATION_TYPE_NAME);

		IInjectionPointField boundRequestContext2 = getInjectionPointField(cdi, fileName, "boundRequestContext2");
		bs = cdi.getBeans(false, boundRequestContext2);
		checkInjected(bs, WeldConstants.BOUND_REQUEST_CONTEXT_TYPE, CDIConstants.REQUEST_SCOPED_ANNOTATION_TYPE_NAME);
	}

	//@Inject @Http RequestContext httpRequestContext
	//@Inject @Default HttpRequestContext httpRequestContext2
	public void testBuiltInHttpRequestContextBean() {
		IInjectionPointField httpRequestContext = getInjectionPointField(cdi, fileName, "httpRequestContext");
		Collection<IBean> bs = cdi.getBeans(false, httpRequestContext);
		checkInjected(bs, WeldConstants.HTTP_REQUEST_CONTEXT_TYPE, CDIConstants.REQUEST_SCOPED_ANNOTATION_TYPE_NAME);

		IInjectionPointField httpRequestContext2 = getInjectionPointField(cdi, fileName, "httpRequestContext2");
		bs = cdi.getBeans(false, httpRequestContext2);
		checkInjected(bs, WeldConstants.HTTP_REQUEST_CONTEXT_TYPE, CDIConstants.REQUEST_SCOPED_ANNOTATION_TYPE_NAME);
	}

	//@Inject @Ejb RequestContext ejbRequestContext
	//@Inject @Default EjbRequestContext ejbRequestContext2
	public void testBuiltInEjbRequestContextBean() {
		IInjectionPointField ejbRequestContext = getInjectionPointField(cdi, fileName, "ejbRequestContext");
		Collection<IBean> bs = cdi.getBeans(false, ejbRequestContext);
		checkInjected(bs, WeldConstants.EJB_REQUEST_CONTEXT_TYPE, CDIConstants.REQUEST_SCOPED_ANNOTATION_TYPE_NAME);

		IInjectionPointField ejbRequestContext2 = getInjectionPointField(cdi, fileName, "ejbRequestContext2");
		bs = cdi.getBeans(false, ejbRequestContext2);
		checkInjected(bs, WeldConstants.EJB_REQUEST_CONTEXT_TYPE, CDIConstants.REQUEST_SCOPED_ANNOTATION_TYPE_NAME);
	}
	
	//@Inject @Bound ConversationContext boundConversationContext
	public void testBuiltInBoundConversationContextBean() {
		IInjectionPointField boundConversationContext = getInjectionPointField(cdi, fileName, "boundConversationContext");
		Collection<IBean> bs = cdi.getBeans(false, boundConversationContext);
		checkInjected(bs, WeldConstants.BOUND_CONVERSATION_CONTEXT_TYPE, CDIConstants.CONVERSATION_SCOPED_ANNOTATION_TYPE_NAME);

		//@Inject @Default BoundConversationContext boundConversationContext2
		IInjectionPointField boundConversationContext2 = getInjectionPointField(cdi, fileName, "boundConversationContext2");
		bs = cdi.getBeans(false, boundConversationContext2);
		checkInjected(bs, WeldConstants.BOUND_CONVERSATION_CONTEXT_TYPE, CDIConstants.CONVERSATION_SCOPED_ANNOTATION_TYPE_NAME);
	}

	//@Inject @Http ConversationContext httpConversationContext
	//@Inject @Default HttpConversationContext httpConversationContext2
	public void testBuiltInHttpConversationContextBean() {
		IInjectionPointField httpConversationContext = getInjectionPointField(cdi, fileName, "httpConversationContext");
		Collection<IBean> bs = cdi.getBeans(false, httpConversationContext);
		checkInjected(bs, WeldConstants.HTTP_CONVERSATION_CONTEXT_TYPE, CDIConstants.CONVERSATION_SCOPED_ANNOTATION_TYPE_NAME);

		IInjectionPointField httpConversationContext2 = getInjectionPointField(cdi, fileName, "httpConversationContext2");
		bs = cdi.getBeans(false, httpConversationContext2);
		checkInjected(bs, WeldConstants.HTTP_CONVERSATION_CONTEXT_TYPE, CDIConstants.CONVERSATION_SCOPED_ANNOTATION_TYPE_NAME);
	}

	//@Inject @Bound SessionContext boundSessionContext
	//@Inject @Default BoundSessionContext boundSessionContext2
	public void testBuiltInBoundSessionContextBean() {
		IInjectionPointField boundSessionContext = getInjectionPointField(cdi, fileName, "boundSessionContext");
		Collection<IBean> bs = cdi.getBeans(false, boundSessionContext);
		checkInjected(bs, WeldConstants.BOUND_SESSION_CONTEXT_TYPE, CDIConstants.SESSION_SCOPED_ANNOTATION_TYPE_NAME);

		IInjectionPointField boundSessionContext2 = getInjectionPointField(cdi, fileName, "boundSessionContext2");
		bs = cdi.getBeans(false, boundSessionContext2);
		checkInjected(bs, WeldConstants.BOUND_SESSION_CONTEXT_TYPE, CDIConstants.SESSION_SCOPED_ANNOTATION_TYPE_NAME);
	}

	//@Inject @Http SessionContext httpSessionContext
	//@Inject @Default HttpSessionContext httpSessionContext2
	public void testBuiltInHttpSessionContextBeans() {
		IInjectionPointField httpSessionContext = getInjectionPointField(cdi, fileName, "httpSessionContext");
		Collection<IBean> bs = cdi.getBeans(false, httpSessionContext);
		checkInjected(bs, WeldConstants.HTTP_SESSION_CONTEXT_TYPE, CDIConstants.SESSION_SCOPED_ANNOTATION_TYPE_NAME);

		IInjectionPointField httpSessionContext2 = getInjectionPointField(cdi, fileName, "httpSessionContext2");
		bs = cdi.getBeans(false, httpSessionContext2);
		checkInjected(bs, WeldConstants.HTTP_SESSION_CONTEXT_TYPE, CDIConstants.SESSION_SCOPED_ANNOTATION_TYPE_NAME);
	}

	//@Inject RequestContext invalidRequestContext
	public void testBuiltInRequestContextBeansNumber() {
		IInjectionPointField invalidRequestContext = getInjectionPointField(cdi, fileName, "invalidRequestContext");
		Collection<IBean> bs = cdi.getBeans(false, invalidRequestContext);
		assertEquals(3, bs.size());
	}

	//@Inject ConversationContext invalidConversationContext
	public void testBuiltInConversationContextBeansNumber() {
		IInjectionPointField invalidConversationContext = getInjectionPointField(cdi, fileName, "invalidConversationContext");
		Collection<IBean> bs = cdi.getBeans(false, invalidConversationContext);
		assertEquals(2, bs.size());
	}

	//@Inject SessionContext invalidSessionContext
	public void testBuiltInSessionContextBeansNumber() {
		IInjectionPointField invalidSessionContext = getInjectionPointField(cdi, fileName, "invalidSessionContext");
		Collection<IBean> bs = cdi.getBeans(false, invalidSessionContext);
		assertEquals(2, bs.size());
	}

	void checkInjected(Collection<IBean> bs, String typeName, String scopeName) {
		assertEquals(1, bs.size());
		IBean b = bs.iterator().next();
		assertTrue(b instanceof IBuiltInBean);
		IType t = b.getBeanClass();
		assertEquals(typeName, t.getFullyQualifiedName());
		IScope s = b.getScope();
		assertNotNull(s);
		assertEquals(scopeName, s.getSourceType().getFullyQualifiedName());
	}

	protected IInjectionPointField getInjectionPointField(ICDIProject cdi, String beanClassFilePath, String fieldName) {
		return DependentProjectTest.getInjectionPointField(cdi, beanClassFilePath, fieldName);
	}
}