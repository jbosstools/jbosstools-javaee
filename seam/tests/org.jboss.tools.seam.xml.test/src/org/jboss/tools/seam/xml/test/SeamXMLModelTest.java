/*******************************************************************************
 * Copyright (c) 2007 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.seam.xml.test;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.markers.XMarkerManager;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.seam.xml.components.model.SeamComponentConstants;
import org.jboss.tools.test.util.JobUtils;

public class SeamXMLModelTest extends TestCase {
	IProject project = null;

	@Override
	protected void setUp() throws Exception {
		project = ResourcesPlugin.getWorkspace().getRoot().getProject("Test");
	}

	/**
	 * This test is to check different cases of declaring components in xml.
	 * It does not check interaction of xml declaration with other declarations.
	 */
	public void testXMLModel() {
		IFile f = project.getFile(new Path("components22.xml"));
		assertTrue("File components22.xml is not accessible in Test project.", f.isAccessible());

		XModelObject fileObject = EclipseResourceUtil.createObjectForResource(f);
		assertNotNull("Cannot create XModel object for file components22.xml.", fileObject);

		String entity = fileObject.getModelEntity().getName();
		assertEquals("File components22.xml is incorrectly parsed by XModel.", SeamComponentConstants.ENT_SEAM_COMPONENTS_22, entity);

		//TODO continue test
	}

	public void testComponentFile() {
		XModelObject fileObject = getComponent22Object();
		String entity = fileObject.getModelEntity().getName();
		assertEquals("File XYZ.component.xml is incorrectly parsed by XModel.", SeamComponentConstants.ENT_SEAM_COMPONENT_FILE_22, entity);
	}

	protected XModelObject getComponents22Object() {
		IFile f = project.getFile(new Path("components22.xml"));
		assertTrue(f.exists());
		return EclipseResourceUtil.createObjectForResource(f);
	}

	protected XModelObject getComponent22Object() {
		IFile f = project.getFile(new Path("XYZ.component.xml"));
		assertNotNull(f);
		assertTrue(f.exists());
		return EclipseResourceUtil.createObjectForResource(f);
	}

	protected XModelObject getComponents23Object() {
		IFile f = project.getFile(new Path("components23.xml"));
		assertTrue(f.exists());
		return EclipseResourceUtil.createObjectForResource(f);
	}

	public void testNavigationPagesComponent() {
		XModelObject fileObject = getComponents22Object();
		assertNotNull("Cannot create XModel object for file components22.xml.", fileObject);

		XModelObject navigationPages = fileObject.getChildByPath("org.jboss.seam.navigation.pages");
		assertNotNull("Cannot find org.jboss.seam.navigation.pages", navigationPages);

		XModelObject resources = navigationPages.getChildByPath("resources");
		assertNotNull("Cannot find resources in org.jboss.seam.navigation.pages", resources);

		XModelObject[] resourcesList = resources.getChildren();
		assertEquals(1, resourcesList.length);

		assertAttribute(navigationPages, "no-conversation-view-id", "a.xhtml");
		assertAttribute(navigationPages, "login-view-id", "b.xhtml");
		assertAttribute(navigationPages, "http-port", "1111");
		assertAttribute(navigationPages, "https-port", "1112");
	}

	public void testDebugAttribute() { //JBIDE-7362
		XModelObject fileObject = getComponents22Object();
		JobUtils.waitForIdle();
		assertNotNull("Cannot create XModel object for file components22.xml.", fileObject);
		XMarkerManager.getInstance().getErrorState(fileObject);
		
		XModelObject coreInit0 = fileObject.getChildByPath("org.jboss.seam.core.init");
		assertNotNull("Cannot find component org.jboss.seam.core.init.", coreInit0);
		assertFalse("Validator found wrong errors in component org.jboss.seam.core.init", XMarkerManager.getInstance().hasErrors(coreInit0));

		XModelObject coreInit1 = fileObject.getChildByPath("org.jboss.seam.core.init1");
		assertNotNull("Cannot find component org.jboss.seam.core.init1.", coreInit1);
		assertTrue("Validator failed to report an error in component org.jboss.seam.core.init1", XMarkerManager.getInstance().hasErrors(coreInit1));

		XModelObject coreInit2 = fileObject.getChildByPath("org.jboss.seam.core.init2");
		assertNotNull("Cannot find component org.jboss.seam.core.init2.", coreInit2);
		assertFalse("Validator found wrong errors in component org.jboss.seam.core.init2", XMarkerManager.getInstance().hasErrors(coreInit2));

	}

	@SuppressWarnings("nls")
	public void testXML23Model() {
		IFile f = project.getFile(new Path("components23.xml"));
		assertTrue("File components23.xml is not accessible in Test project.", f.isAccessible());

		XModelObject fileObject = EclipseResourceUtil.createObjectForResource(f);
		assertNotNull("Cannot create XModel object for file components22.xml.", fileObject);

		String entity = fileObject.getModelEntity().getName();
		assertEquals("File components23.xml is incorrectly parsed by XModel.", SeamComponentConstants.ENT_SEAM_COMPONENTS_23, entity);

		//TODO continue test
		
		XModelObject c = fileObject.getChildByPath("myComponent");
		assertNotNull(c);
		
		c = fileObject.getChildByPath("myFactory");
		assertNotNull(c);
		assertEquals("myFactory", c.getAttributeValue("value"));

		c = fileObject.getChildByPath("org.jboss.seam.navigation.pages");
		assertNotNull(c);
		assertEquals("3", c.getAttributeValue("http-port"));

		c = fileObject.getChildByPath("org.jboss.seam.remoting.remoting");
		assertNotNull(c);
		assertEquals("1", c.getAttributeValue("poll-interval"));
		assertEquals("2", c.getAttributeValue("poll-timeout"));

		c = fileObject.getChildByPath("entityQuery");
		assertNotNull(c);
		assertEquals("abc", c.getAttributeValue("ejbql"));

		c = fileObject.getChildByPath("hibernateEntityQuery");
		assertNotNull(c);
		assertEquals("abc", c.getAttributeValue("ejbql"));
		assertEquals("sss", c.getAttributeValue("cache-region"));
		assertEquals("3", c.getAttributeValue("fetch-size"));
		assertEquals("x", c.getAttributeValue("session"));

		c = fileObject.getChildByPath("entityHome");
		assertNotNull(c);
		assertEquals("org.MyEntityHome", c.getAttributeValue("entity-class"));

		c = fileObject.getChildByPath("hibernateEntityHome");
		assertNotNull(c);

		c = fileObject.getChildByPath("org.jboss.seam.security.identity");
		assertNotNull(c);
		assertEquals("#{m1}", c.getAttributeValue("authenticate-method"));
		assertEquals("true", c.getAttributeValue("remember-me"));

		c = fileObject.getChildByPath("org.jboss.seam.security.identityManager");
		assertNotNull(c);
		assertEquals("#{xyz}", c.getAttributeValue("identity-store"));

		c = fileObject.getChildByPath("org.jboss.seam.security.jpaIdentityStore");
		assertNotNull(c);
		assertEquals("myManager", c.getAttributeValue("entity-manager"));
		assertEquals("org.MyStore", c.getAttributeValue("user-class"));

		c = fileObject.getChildByPath("org.jboss.seam.security.jpaTokenStore");
		assertNotNull(c);
		assertEquals("myEntityManager", c.getAttributeValue("entity-manager"));
		assertEquals("String", c.getAttributeValue("token-class"));

		c = fileObject.getChildByPath("org.jboss.seam.security.ldapIdentityStore");
		assertNotNull(c);
		assertEquals("777", c.getAttributeValue("server-address"));
		assertEquals("555", c.getAttributeValue("server-port"));

		c = fileObject.getChildByPath("org.jboss.seam.security.permissionManager");
		assertNotNull(c);
		assertEquals("#{permissionStore}", c.getAttributeValue("permission-store"));

		c = fileObject.getChildByPath("org.jboss.seam.security.jpaPermissionStore");
		assertNotNull(c);
		assertEquals("myEntityManager", c.getAttributeValue("entity-manager"));
		assertEquals("org.MyPermissionClass", c.getAttributeValue("user-permission-class"));

		c = fileObject.getChildByPath("org.jboss.seam.security.ruleBasedPermissionResolver");
		assertNotNull(c);
		assertEquals("#{myRules}", c.getAttributeValue("security-rules"));

		c = fileObject.getChildByPath("org.jboss.seam.security.persistentPermissionResolver");
		assertNotNull(c);
		assertEquals("#{myPermissionStore}", c.getAttributeValue("permission-store"));

		c = fileObject.getChildByPath("org.jboss.seam.security.rememberMe");
		assertNotNull(c);
		assertEquals("101", c.getAttributeValue("cookie-max-age"));
		assertEquals("disabled", c.getAttributeValue("mode"));

		c = fileObject.getChildByPath("org.jboss.seam.transaction.transaction");
		assertNotNull(c);
		assertEquals("abc", c.getAttributeValue("jndi-name"));

		c = fileObject.getChildByPath("org.jboss.seam.ui.jpaEntityLoader");
		assertNotNull(c);
		assertEquals("myEntityManager", c.getAttributeValue("entity-manager"));

		c = fileObject.getChildByPath("org.jboss.seam.ui.hibernateEntityLoader");
		assertNotNull(c);
		assertEquals("mySession", c.getAttributeValue("session"));

		c = fileObject.getChildByPath("org.jboss.seam.ui.entityConverter");
		assertNotNull(c);
		assertEquals("myEntityLoader", c.getAttributeValue("entity-loader"));

		c = fileObject.getChildByPath("org.jboss.seam.web.contextFilter");
		assertNotNull(c);
		assertEquals("*", c.getAttributeValue("url-pattern"));

		c = fileObject.getChildByPath("org.jboss.seam.web.exceptionFilter");
		assertNotNull(c);
		assertEquals("*", c.getAttributeValue("url-pattern"));

		c = fileObject.getChildByPath("org.jboss.seam.web.multipartFilter");
		assertNotNull(c);
		assertEquals("*", c.getAttributeValue("url-pattern"));

		c = fileObject.getChildByPath("org.jboss.seam.web.ajax4jsfFilter");
		assertNotNull(c);

		c = fileObject.getChildByPath("org.jboss.seam.web.authenticationFilter");
		assertNotNull(c);

		c = fileObject.getChildByPath("org.jboss.seam.web.cacheControlFilter");
		assertNotNull(c);

		c = fileObject.getChildByPath("org.jboss.seam.servlet.characterEncodingFilter");
		assertNotNull(c);

		c = fileObject.getChildByPath("org.jboss.seam.web.hotDeployFilter");
		assertNotNull(c);

		c = fileObject.getChildByPath("org.jboss.seam.web.identityFilter");
		assertNotNull(c);

		c = fileObject.getChildByPath("org.jboss.seam.web.loggingFilter");
		assertNotNull(c);

		c = fileObject.getChildByPath("org.jboss.seam.web.redirectFilter");
		assertNotNull(c);

		c = fileObject.getChildByPath("org.jboss.seam.web.rewriteFilter");
		assertNotNull(c);

		c = fileObject.getChildByPath("org.jboss.seam.web.session");
		assertNotNull(c);

		c = fileObject.getChildByPath("org.jboss.seam.web.wicketFilter");
		assertNotNull(c);
		assertEquals("myClass", c.getAttributeValue("application-class"));

		c = fileObject.getChildByPath("myPersistentContext");
		assertNotNull(c);
		assertEquals("myname", c.getAttributeValue("persistence-unit-jndi-name"));

		c = fileObject.getChildByPath("myManagerFactory");
		assertNotNull(c);
		assertEquals("myName", c.getAttributeValue("persistence-unit-name"));

		c = fileObject.getChildByPath("myFilter");
		assertNotNull(c);
		assertEquals("myFilterName", c.getAttributeValue("filter name"));

		c = fileObject.getChildByPath("mySession");
		assertNotNull(c);

		c = fileObject.getChildByPath("mySessionFactory");
		assertNotNull(c);

		c = fileObject.getChildByPath("org.jboss.seam.async.dispatcher");
		assertNotNull(c);

		c = fileObject.getChildByPath("myManagedWorkingMemory");
		assertNotNull(c);

		c = fileObject.getChildByPath("myRuleBase");
		assertNotNull(c);
		assertEquals("fff", c.getAttributeValue("rule-files"));

		c = fileObject.getChildByPath("myRuleAgent");
		assertNotNull(c);
		assertEquals("fff", c.getAttributeValue("configuration-file"));

		c = fileObject.getChildByPath("org.jboss.seam.international.localeSelector");
		assertNotNull(c);
		assertEquals("ss", c.getAttributeValue("locale-string"));

		c = fileObject.getChildByPath("org.jboss.seam.international.timeZoneSelector");
		assertNotNull(c);
		assertEquals("22", c.getAttributeValue("time-zone-id"));

		c = fileObject.getChildByPath("org.jboss.seam.international.localeConfig");
		assertNotNull(c);
		assertEquals("ru", c.getAttributeValue("default-locale"));

		c = fileObject.getChildByPath("org.jboss.seam.wicket.webApplication");
		assertNotNull(c);
		assertEquals("org.MyApplication", c.getAttributeValue("application-class"));

		c = fileObject.getChildByPath("myKeyStore");
		assertNotNull(c);
		assertEquals("a", c.getAttributeValue("key-alias"));
		assertEquals("p", c.getAttributeValue("key-password"));
		assertEquals("keyStore", c.getAttributeValue("key-store"));
		assertEquals("q", c.getAttributeValue("key-store-password"));

		c = fileObject.getChildByPath("org.jboss.seam.core.init");
		assertNotNull(c);
		assertEquals("pp", c.getAttributeValue("jndi-pattern"));
		assertEquals("true", c.getAttributeValue("security-enabled"));
		assertEquals("true", c.getAttributeValue("transaction-management-enabled"));

		c = fileObject.getChildByPath("org.jboss.seam.core.manager");
		assertNotNull(c);
		assertEquals("1", c.getAttributeValue("conversation-timeout"));
		assertEquals("1", c.getAttributeValue("concurrent-request-timeout"));
		assertEquals("p", c.getAttributeValue("conversation-id-parameter"));
		assertEquals("MANUAL", c.getAttributeValue("default-flush-mode"));
		assertEquals("q", c.getAttributeValue("parent-conversation-id-parameter"));

		c = fileObject.getChildByPath("org.jboss.seam.core.pojoCache");
		assertNotNull(c);
		assertEquals("nnn", c.getAttributeValue("cfg-resource-name"));

		c = fileObject.getChildByPath("org.jboss.seam.core.resourceLoader");
		assertNotNull(c);
		assertEquals("n1,n2", c.getAttributeValue("bundle-names"));

		c = fileObject.getChildByPath("mySelector");
		assertNotNull(c);
		assertEquals("x", c.getAttributeValue("theme"));
		assertEquals("x,y", c.getAttributeValue("available-themes"));
		assertEquals("true", c.getAttributeValue("cookie-enabled"));
		assertEquals("100", c.getAttributeValue("cookie-max-age"));

		c = fileObject.getChildByPath("org.jboss.seam.jms.queueConnection");
		assertNotNull(c);
		assertEquals("myName", c.getAttributeValue("factory-jndi-name"));

		c = fileObject.getChildByPath("org.jboss.seam.jms.topicConnection");
		assertNotNull(c);
		assertEquals("factory", c.getAttributeValue("factory-jndi-name"));

		c = fileObject.getChildByPath("myTopicPublisher");
		assertNotNull(c);
		assertEquals("jjj", c.getAttributeValue("topic-jndi-name"));

		c = fileObject.getChildByPath("mySender");
		assertNotNull(c);
		assertEquals("nn", c.getAttributeValue("queue-jndi-name"));

		c = fileObject.getChildByPath("org.jboss.seam.bpm.actor");
		assertNotNull(c);
		assertEquals("1", c.getAttributeValue("group-actor-ids"));

		c = fileObject.getChildByPath("org.jboss.seam.bpm.jbpm");
		assertNotNull(c);
	}

	protected void assertAttribute(XModelObject object, String name, String value) {
		String actual = object.getAttributeValue(name);
		assertEquals("Attribute " + name + " in " + object.getPresentationString() + " is incorrect.", value, actual);
	}
}