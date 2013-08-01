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
package org.jboss.tools.cdi.core.test.tck;

import java.util.Collection;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.jdt.core.IType;
import org.jboss.tools.cdi.core.CDIConstants;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.IBuiltInBean;
import org.jboss.tools.cdi.core.ICDIProject;
import org.jboss.tools.cdi.core.IInjectionPointField;
import org.jboss.tools.cdi.core.IScope;
import org.jboss.tools.cdi.core.WeldConstants;
import org.jboss.tools.cdi.core.test.DependentProjectTest;
import org.jboss.tools.test.util.JobUtils;
import org.jboss.tools.test.util.ResourcesUtils;

import junit.framework.TestCase;

public class BuiltInContextBeanInjectionTest extends TestCase {
	protected static String PLUGIN_ID = "org.jboss.tools.cdi.core.test";
	IProject project = null;

	public BuiltInContextBeanInjectionTest() {}

	public void setUp() throws Exception {
		project = ResourcesUtils.importProject(PLUGIN_ID, "/projects/weld1.1");
		project.build(IncrementalProjectBuilder.INCREMENTAL_BUILD, null);
	}

	public void testBuiltInContextBeans() {
		ICDIProject cdi = CDICorePlugin.getCDIProject(project, true);
		String fileName = "src/test/Test.java";

		//@Inject DependentContext dependentContext
		IInjectionPointField dependentContext = getInjectionPointField(cdi, fileName, "dependentContext");
		Collection<IBean> bs = cdi.getBeans(false, dependentContext);
		checkInjected(bs, WeldConstants.DEPENDENT_CONTEXT_TYPE, CDIConstants.DEPENDENT_ANNOTATION_TYPE_NAME);

		//@Inject ApplicationContext applicationContext
		IInjectionPointField applicationContext = getInjectionPointField(cdi, fileName, "applicationContext");
		bs = cdi.getBeans(false, applicationContext);
		checkInjected(bs, WeldConstants.APPLICATION_CONTEXT_TYPE, CDIConstants.APPLICATION_SCOPED_ANNOTATION_TYPE_NAME);

		//@Inject SingletonContext singletonContext
		IInjectionPointField singletonContext = getInjectionPointField(cdi, fileName, "singletonContext");
		bs = cdi.getBeans(false, singletonContext);
		checkInjected(bs, WeldConstants.SINGLETON_CONTEXT_TYPE, CDIConstants.SINGLETON_SCOPED_ANNOTATION_TYPE_NAME);

		//@Inject @Unbound RequestContext unboundRequestContext
		IInjectionPointField unboundRequestContext = getInjectionPointField(cdi, fileName, "unboundRequestContext");
		bs = cdi.getBeans(false, unboundRequestContext);
		checkInjected(bs, WeldConstants.REQUEST_CONTEXT_TYPE, CDIConstants.REQUEST_SCOPED_ANNOTATION_TYPE_NAME);

		//@Inject @Bound RequestContext boundRequestContext
		IInjectionPointField boundRequestContext = getInjectionPointField(cdi, fileName, "boundRequestContext");
		bs = cdi.getBeans(false, boundRequestContext);
		checkInjected(bs, WeldConstants.BOUND_REQUEST_CONTEXT_TYPE, CDIConstants.REQUEST_SCOPED_ANNOTATION_TYPE_NAME);

		//@Inject @Default BoundRequestContext boundRequestContext2
		IInjectionPointField boundRequestContext2 = getInjectionPointField(cdi, fileName, "boundRequestContext2");
		bs = cdi.getBeans(false, boundRequestContext2);
		checkInjected(bs, WeldConstants.BOUND_REQUEST_CONTEXT_TYPE, CDIConstants.REQUEST_SCOPED_ANNOTATION_TYPE_NAME);

		//@Inject @Http RequestContext httpRequestContext
		IInjectionPointField httpRequestContext = getInjectionPointField(cdi, fileName, "httpRequestContext");
		bs = cdi.getBeans(false, httpRequestContext);
		checkInjected(bs, WeldConstants.HTTP_REQUEST_CONTEXT_TYPE, CDIConstants.REQUEST_SCOPED_ANNOTATION_TYPE_NAME);

		//@Inject @Default BoundRequestContext httpRequestContext2
		IInjectionPointField httpRequestContext2 = getInjectionPointField(cdi, fileName, "httpRequestContext2");
		bs = cdi.getBeans(false, httpRequestContext2);
		checkInjected(bs, WeldConstants.HTTP_REQUEST_CONTEXT_TYPE, CDIConstants.REQUEST_SCOPED_ANNOTATION_TYPE_NAME);

		//@Inject @Ejb RequestContext ejbRequestContext
		IInjectionPointField ejbRequestContext = getInjectionPointField(cdi, fileName, "ejbRequestContext");
		bs = cdi.getBeans(false, ejbRequestContext);
		checkInjected(bs, WeldConstants.EJB_REQUEST_CONTEXT_TYPE, CDIConstants.REQUEST_SCOPED_ANNOTATION_TYPE_NAME);

		//@Inject @Default EjbRequestContext ejbRequestContext2
		IInjectionPointField ejbRequestContext2 = getInjectionPointField(cdi, fileName, "ejbRequestContext2");
		bs = cdi.getBeans(false, ejbRequestContext2);
		checkInjected(bs, WeldConstants.EJB_REQUEST_CONTEXT_TYPE, CDIConstants.REQUEST_SCOPED_ANNOTATION_TYPE_NAME);

		//@Inject @Bound ConversationContext boundConversationContext
		IInjectionPointField boundConversationContext = getInjectionPointField(cdi, fileName, "boundConversationContext");
		bs = cdi.getBeans(false, boundConversationContext);
		checkInjected(bs, WeldConstants.BOUND_CONVERSATION_CONTEXT_TYPE, CDIConstants.CONVERSATION_SCOPED_ANNOTATION_TYPE_NAME);

		//@Inject @Default BoundConversationContext boundConversationContext2
		IInjectionPointField boundConversationContext2 = getInjectionPointField(cdi, fileName, "boundConversationContext2");
		bs = cdi.getBeans(false, boundConversationContext2);
		checkInjected(bs, WeldConstants.BOUND_CONVERSATION_CONTEXT_TYPE, CDIConstants.CONVERSATION_SCOPED_ANNOTATION_TYPE_NAME);

		//@Inject @Http ConversationContext httpConversationContext
		IInjectionPointField httpConversationContext = getInjectionPointField(cdi, fileName, "httpConversationContext");
		bs = cdi.getBeans(false, httpConversationContext);
		checkInjected(bs, WeldConstants.HTTP_CONVERSATION_CONTEXT_TYPE, CDIConstants.CONVERSATION_SCOPED_ANNOTATION_TYPE_NAME);

		//@Inject @Default HttpConversationContext httpConversationContext2
		IInjectionPointField httpConversationContext2 = getInjectionPointField(cdi, fileName, "httpConversationContext2");
		bs = cdi.getBeans(false, httpConversationContext2);
		checkInjected(bs, WeldConstants.HTTP_CONVERSATION_CONTEXT_TYPE, CDIConstants.CONVERSATION_SCOPED_ANNOTATION_TYPE_NAME);

		//@Inject @Bound SessionContext boundSessionContext
		IInjectionPointField boundSessionContext = getInjectionPointField(cdi, fileName, "boundSessionContext");
		bs = cdi.getBeans(false, boundSessionContext);
		checkInjected(bs, WeldConstants.BOUND_SESSION_CONTEXT_TYPE, CDIConstants.SESSION_SCOPED_ANNOTATION_TYPE_NAME);

		//@Inject @Default BoundSessionContext boundSessionContext2
		IInjectionPointField boundSessionContext2 = getInjectionPointField(cdi, fileName, "boundSessionContext2");
		bs = cdi.getBeans(false, boundSessionContext2);
		checkInjected(bs, WeldConstants.BOUND_SESSION_CONTEXT_TYPE, CDIConstants.SESSION_SCOPED_ANNOTATION_TYPE_NAME);

		//@Inject @Http SessionContext httpSessionContext
		IInjectionPointField httpSessionContext = getInjectionPointField(cdi, fileName, "httpSessionContext");
		bs = cdi.getBeans(false, httpSessionContext);
		checkInjected(bs, WeldConstants.HTTP_SESSION_CONTEXT_TYPE, CDIConstants.SESSION_SCOPED_ANNOTATION_TYPE_NAME);

		//@Inject @Default HttpSessionContext httpSessionContext2
		IInjectionPointField httpSessionContext2 = getInjectionPointField(cdi, fileName, "httpSessionContext2");
		bs = cdi.getBeans(false, httpSessionContext2);
		checkInjected(bs, WeldConstants.HTTP_SESSION_CONTEXT_TYPE, CDIConstants.SESSION_SCOPED_ANNOTATION_TYPE_NAME);

		//@Inject RequestContext invalidRequestContext
		IInjectionPointField invalidRequestContext = getInjectionPointField(cdi, fileName, "invalidRequestContext");
		bs = cdi.getBeans(false, invalidRequestContext);
		assertEquals(3, bs.size());
		
		//@Inject ConversationContext invalidConversationContext
		IInjectionPointField invalidConversationContext = getInjectionPointField(cdi, fileName, "invalidConversationContext");
		bs = cdi.getBeans(false, invalidConversationContext);
		assertEquals(2, bs.size());
		
		//@Inject SessionContext invalidSessionContext
		IInjectionPointField invalidSessionContext = getInjectionPointField(cdi, fileName, "invalidSessionContext");
		bs = cdi.getBeans(false, invalidSessionContext);
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

	public void tearDown() throws Exception {
		boolean saveAutoBuild = ResourcesUtils.setBuildAutomatically(false);
		project.delete(true, true, null);
		JobUtils.waitForIdle();
		ResourcesUtils.setBuildAutomatically(saveAutoBuild);
	}

	protected IInjectionPointField getInjectionPointField(ICDIProject cdi, String beanClassFilePath, String fieldName) {
		return DependentProjectTest.getInjectionPointField(cdi, beanClassFilePath, fieldName);
	}
}
