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
package org.jboss.tools.cdi.core.test.tck.validation;

import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.ICDIProject;
import org.jboss.tools.cdi.core.IInjectionPointField;
import org.jboss.tools.cdi.core.test.DependentProjectTest;
import org.jboss.tools.cdi.internal.core.validation.CDIValidationMessages;
import org.jboss.tools.common.base.test.validation.TestUtil;
import org.jboss.tools.tests.AbstractResourceMarkerTest;

import junit.framework.TestCase;

public class WeldExcludeIncrementalValidationTest extends TestCase {
	protected static String PLUGIN_ID = "org.jboss.tools.cdi.core.test";
	IProject project = null;
	ICDIProject cdi;
	String fileName = "src/test/TestExcluded.java";

	public WeldExcludeIncrementalValidationTest() {}

	public void setUp() throws Exception {
		project = ResourcesPlugin.getWorkspace().getRoot().getProject("weld1.1");
//		project.build(IncrementalProjectBuilder.INCREMENTAL_BUILD, null);
		cdi = CDICorePlugin.getCDIProject(project, true);
	}

	//<weld:exclude name="exclude.p1.Bean1"/>
	public void testExactMatch() throws Exception {
		IFile bean_xml = project.getFile("src/META-INF/beans.xml");
		//Bean exclude.p1.Bean1 is excluded
		IInjectionPointField bean1 = getInjectionPointField(cdi, fileName, "bean1");
		TestUtil.validate(bean_xml);
		AbstractResourceMarkerTest.assertMarkerIsCreated(bean1.getResource(), CDIValidationMessages.UNSATISFIED_INJECTION_POINTS, 14, 16, 18, 19);

		IFile beans_modified = project.getFile("src/META-INF/beans.modified");
		bean_xml.setContents(beans_modified.getContents(), IFile.FORCE, new NullProgressMonitor());
		TestUtil.validate(bean_xml);
		AbstractResourceMarkerTest.assertMarkerIsCreated(bean1.getResource(), CDIValidationMessages.UNSATISFIED_INJECTION_POINTS, 16, 18, 19);

		IFile beans_original = project.getFile("src/META-INF/beans.original");
		bean_xml.setContents(beans_original.getContents(), IFile.FORCE, new NullProgressMonitor());
		TestUtil.validate(bean_xml);
		AbstractResourceMarkerTest.assertMarkerIsCreated(bean1.getResource(), CDIValidationMessages.UNSATISFIED_INJECTION_POINTS, 14, 16, 18, 19);
	}

	protected IInjectionPointField getInjectionPointField(ICDIProject cdi, String beanClassFilePath, String fieldName) {
		return DependentProjectTest.getInjectionPointField(cdi, beanClassFilePath, fieldName);
	}
}
