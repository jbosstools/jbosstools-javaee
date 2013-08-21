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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.ICDIProject;
import org.jboss.tools.cdi.core.IInjectionPointField;

import junit.framework.TestCase;

public class BeanArchivesTest extends TestCase {
	protected static String PLUGIN_ID = "org.jboss.tools.cdi.core.test";
	IProject project = null;
	ICDIProject cdi;
	String fileName = "src/test/a/Test.java";

	public BeanArchivesTest() {
	}

	@Override
	public void setUp() throws Exception {
		project = ResourcesPlugin.getWorkspace().getRoot().getProject("CDIArchivesTest");
		cdi = CDICorePlugin.getCDIProject(project, true);
	}

	/**
	  * //Archive cdianimals.jar has beans.xml with bean-discovery-mode="annotated"
	  * @Inject Cat cat; //not annotated - not a bean
	  * @Inject Dog dog; //annotated - bean
	 */
	public void testJarWithBeansXMLinAnnotatedMode() {
		IInjectionPointField cat = getInjectionPointField(cdi, fileName, "cat");
		Collection<IBean> bs = cdi.getBeans(false, cat);
		assertTrue(bs.isEmpty());
		IInjectionPointField dog = getInjectionPointField(cdi, fileName, "dog");
		bs = cdi.getBeans(false, dog);
		assertEquals(1, bs.size());
	}

	/**
	  * //Archive cdibirds.jar does not include beans.xml
	  * @Inject Crow crow; //not annotated - not a bean
	  * @Inject Heron heron; //annotated - bean
	 */
	public void testJarWithoutBeansXML() {
		IInjectionPointField crow = getInjectionPointField(cdi, fileName, "crow");
		Collection<IBean> bs = cdi.getBeans(false, crow);
		assertTrue(bs.isEmpty());
		IInjectionPointField heron = getInjectionPointField(cdi, fileName, "heron");
		bs = cdi.getBeans(false, heron);
		assertEquals(1, bs.size());
	}

	/**
	  * //Archive folder 'plants' has beans.xml with bean-discovery-mode="annotated"
	  * @Inject Tree tree; //not annotated - not a bean
	  * @Inject Flower flower; //annotated - bean
	 */
	public void testFolderWithBeansXMLinAnnotatedMode() {
		IInjectionPointField tree = getInjectionPointField(cdi, fileName, "tree");
		Collection<IBean> bs = cdi.getBeans(false, tree);
		assertTrue(bs.isEmpty());
		IInjectionPointField flower = getInjectionPointField(cdi, fileName, "flower");
		bs = cdi.getBeans(false, flower);
		assertEquals(1, bs.size());
	}

	/**
	  * //Archive folder 'cdiinsects' does not include beans.xml
	  * @Inject Bee bee; //not annotated - not a bean
	  * @Inject Fly fly; //annotated - bean
	 */
	public void testFolderWithoutBeansXML() {
		IInjectionPointField bee = getInjectionPointField(cdi, fileName, "bee");
		Collection<IBean> bs = cdi.getBeans(false, bee);
		assertTrue(bs.isEmpty());
		IInjectionPointField fly = getInjectionPointField(cdi, fileName, "fly");
		bs = cdi.getBeans(false, fly);
		assertEquals(1, bs.size());
	}

	/**
	  * //Archive cdifish.jar has beans.xml with bean-discovery-mode="all"
	  * @Inject Salmon salmon; //not annotated - bean
	  * @Inject Eel eel; //annotated - bean
	 */
	public void testJarWithBeansXMLinAllMode() {
		IInjectionPointField salmon = getInjectionPointField(cdi, fileName, "salmon");
		Collection<IBean> bs = cdi.getBeans(false, salmon);
		assertEquals(1, bs.size());
		IInjectionPointField eel = getInjectionPointField(cdi, fileName, "eel");
		bs = cdi.getBeans(false, eel);
		assertEquals(1, bs.size());
	}


	protected IInjectionPointField getInjectionPointField(ICDIProject cdi, String beanClassFilePath, String fieldName) {
		return DependentProjectTest.getInjectionPointField(cdi, beanClassFilePath, fieldName);
	}
}
