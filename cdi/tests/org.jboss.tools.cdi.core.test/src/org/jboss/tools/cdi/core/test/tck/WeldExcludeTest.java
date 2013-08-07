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
import org.eclipse.core.resources.ResourcesPlugin;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.ICDIProject;
import org.jboss.tools.cdi.core.IInjectionPointField;
import org.jboss.tools.cdi.core.test.DependentProjectTest;

import junit.framework.TestCase;

public class WeldExcludeTest extends TestCase {
	protected static String PLUGIN_ID = "org.jboss.tools.cdi.core.test";
	IProject project = null;
	ICDIProject cdi;
	String fileName = "src/test/TestExcluded.java";

	public WeldExcludeTest() {}

	public void setUp() throws Exception {
		project = ResourcesPlugin.getWorkspace().getRoot().getProject("weld1.1");
//		project.build(IncrementalProjectBuilder.INCREMENTAL_BUILD, null);
		cdi = CDICorePlugin.getCDIProject(project, true);
	}

	//<weld:exclude name="exclude.p1.Bean1"/>
	public void testExactMatch() {
		//Bean exclude.p1.Bean1 is excluded
		IInjectionPointField bean1 = getInjectionPointField(cdi, fileName, "bean1");
		Collection<IBean> bs = cdi.getBeans(false, bean1);
		assertTrue(bs.isEmpty());

		//Bean exclude.p1.Bean2 is not excluded
		IInjectionPointField bean2 = getInjectionPointField(cdi, fileName, "bean2");
		bs = cdi.getBeans(false, bean2);
		assertEquals(1, bs.size());
	}

	//<weld:exclude name="exclude.p2.*"/>
	public void testCurrentPackageMatch() {
		//Bean exclude.p2.Bean3 is excluded
		IInjectionPointField bean3 = getInjectionPointField(cdi, fileName, "bean3");
		Collection<IBean> bs = cdi.getBeans(false, bean3);
		assertTrue(bs.isEmpty());

		//Bean exclude.p2.p3.Bean4 is not excluded
		IInjectionPointField bean4 = getInjectionPointField(cdi, fileName, "bean4");
		bs = cdi.getBeans(false, bean4);
		assertEquals(1, bs.size());
	}

	//<weld:exclude name="exclude.p4.**">
	//  <weld:if-class-available name="exclude.p4.Bean5"/>
	//  <weld:if-class-available name="!exclude.p6.DoesNotExist"/>
	//</weld:exclude>
	public void testParentPackageMatch() {
		//Bean exclude.p4.Bean5 is excluded
		IInjectionPointField bean5 = getInjectionPointField(cdi, fileName, "bean5");
		Collection<IBean> bs = cdi.getBeans(false, bean5);
		assertTrue(bs.isEmpty());

		//Bean exclude.p4.p5.Bean6 is excluded
		IInjectionPointField bean6 = getInjectionPointField(cdi, fileName, "bean6");
		bs = cdi.getBeans(false, bean6);
		assertTrue(bs.isEmpty());		
	}

	//<weld:exclude name="exclude.p6.**">
	//  <weld:if-class-available name="exclude.p6.DoesNotExist"/>
	//</weld:exclude>
	public void testDisabledMatch() {
		//Bean exclude.p6.Bean7 is not excluded
		IInjectionPointField bean7 = getInjectionPointField(cdi, fileName, "bean7");
		Collection<IBean> bs = cdi.getBeans(false, bean7);
		assertEquals(1, bs.size());
	}

	protected IInjectionPointField getInjectionPointField(ICDIProject cdi, String beanClassFilePath, String fieldName) {
		return DependentProjectTest.getInjectionPointField(cdi, beanClassFilePath, fieldName);
	}
}
