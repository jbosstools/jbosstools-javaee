/******************************************************************************* 
 * Copyright (c) 2010 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.cdi.core.test.tck.validation;

import java.io.IOException;
import java.text.MessageFormat;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.jboss.tools.cdi.internal.core.validation.CDIValidationMessages;
import org.jboss.tools.common.base.test.validation.TestUtil;
import org.jboss.tools.test.util.JobUtils;
import org.jboss.tools.test.util.ResourcesUtils;
import org.jboss.tools.tests.AbstractResourceMarkerTest;

/**
 * @author Alexey Kazakov
 */
public class DependentProjectValidationTest extends ValidationTest {

	protected static String PLUGIN_ID = "org.jboss.tools.cdi.core.test";

	IProject project1 = null;
	IProject project2 = null;
	IProject project3 = null;
	IProject project4 = null;
	IProject project5 = null;

	public void setUp() throws Exception {
		project1 = ResourcesUtils.importProject(PLUGIN_ID, "/projects/CDITest1");
		TestUtil._waitForValidation(project1);
		project2 = ResourcesUtils.importProject(PLUGIN_ID, "/projects/CDITest2");
		TestUtil._waitForValidation(project2);
		project3 = ResourcesUtils.importProject(PLUGIN_ID, "/projects/CDITest3");
		TestUtil._waitForValidation(project3);
		project4 = ResourcesUtils.importProject(PLUGIN_ID, "/projects/CDITest4");
		TestUtil._waitForValidation(project4);
		project5 = ResourcesUtils.importProject(PLUGIN_ID, "/projects/CDITest5");
		TestUtil._waitForValidation(project5);
	}

	/**
	 * https://issues.jboss.org/browse/JBIDE-7946
	 */
	public void testDependentProjects() throws CoreException, IOException {
		boolean saveAutoBuild = ResourcesUtils.setBuildAutomatically(false);
		IFile testBean3 = project3.getFile("src/cdi/test3/TestBean3.java");
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(testBean3, MessageFormat.format(CDIValidationMessages.UNPROXYABLE_BEAN_PRIMITIVE_TYPE, "boolean", "TestBean3.foo()"), 10);

		IFile testBean4 = project4.getFile("src/cdi/test4/TestBean4.java");
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(testBean4, MessageFormat.format(CDIValidationMessages.UNPROXYABLE_BEAN_PRIMITIVE_TYPE, "int", "TestBean4.foo()"), 10);

		IFile testBean5 = project5.getFile("src/cdi/test5/TestBean5.java");
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(testBean5, MessageFormat.format(CDIValidationMessages.UNPROXYABLE_BEAN_PRIMITIVE_TYPE, "boolean", "TestBean5.foo()"), 10);

		IFile scope = project2.getFile(new Path("src/test/TestScope.java"));
		IFile normalScope = project2.getFile(new Path("src/test/TestNormalScope.validation"));

		scope.setContents(normalScope.getContents(), IFile.FORCE, new NullProgressMonitor());
		TestUtil.validate(scope);

		AbstractResourceMarkerTest.assertMarkerIsCreated(testBean3, MessageFormat.format(CDIValidationMessages.UNPROXYABLE_BEAN_PRIMITIVE_TYPE, "boolean", "TestBean3.foo()"), 10);
		AbstractResourceMarkerTest.assertMarkerIsCreated(testBean4, MessageFormat.format(CDIValidationMessages.UNPROXYABLE_BEAN_PRIMITIVE_TYPE, "int", "TestBean4.foo()"), 10);
		AbstractResourceMarkerTest.assertMarkerIsCreated(testBean5, MessageFormat.format(CDIValidationMessages.UNPROXYABLE_BEAN_PRIMITIVE_TYPE, "boolean", "TestBean5.foo()"), 10);

		normalScope = project2.getFile(new Path("src/test/TestScope.java"));
		scope = project2.getFile(new Path("src/test/TestScope.validation"));

		normalScope.setContents(scope.getContents(), IFile.FORCE, new NullProgressMonitor());
		TestUtil.validate(normalScope);

		AbstractResourceMarkerTest.assertMarkerIsNotCreated(testBean3, MessageFormat.format(CDIValidationMessages.UNPROXYABLE_BEAN_PRIMITIVE_TYPE, "boolean", "TestBean3.foo()"), 10);
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(testBean4, MessageFormat.format(CDIValidationMessages.UNPROXYABLE_BEAN_PRIMITIVE_TYPE, "int", "TestBean4.foo()"), 10);
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(testBean5, MessageFormat.format(CDIValidationMessages.UNPROXYABLE_BEAN_PRIMITIVE_TYPE, "boolean", "TestBean5.foo()"), 10);
		ResourcesUtils.setBuildAutomatically(saveAutoBuild);
	}

	public void tearDown() throws Exception {
		boolean saveAutoBuild = ResourcesUtils.setBuildAutomatically(false);
		project1.delete(true, true, null);
		project2.delete(true, true, null);
		project3.delete(true, true, null);
		project4.delete(true, true, null);
		project5.delete(true, true, null);
		JobUtils.waitForIdle();
		ResourcesUtils.setBuildAutomatically(saveAutoBuild);
	}
}