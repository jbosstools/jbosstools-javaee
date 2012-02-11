/******************************************************************************* 
 * Copyright (c) 2012 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.jsf.test.validation;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.jboss.tools.common.base.test.validation.TestUtil;
import org.jboss.tools.test.util.ResourcesUtils;
import org.jboss.tools.tests.AbstractResourceMarkerTest;

/**
 * @author Alexey Kazakov
 */
public class BuilderValidationTest extends AbstractResourceMarkerTest {

	/*
	 * (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		project = ResourcesPlugin.getWorkspace().getRoot().getProject("jsf2pr");
	}

	/**
	 * See https://issues.jboss.org/browse/JBIDE-10872
	 * EL incremental validation doesn't work for complex ELs.
	 * @throws Exception
	 */
	public void testFullBuildValidation() throws Exception {
		assertDefaultErrors();
	}

	private void assertDefaultErrors() throws Exception {
		/*
		 * 	#{authenticator.test.string.newString}  #6
		 *  #{authenticator.authenticate()}         #7
		 *  #{authenticator.test.foo()}             #8
		 *  #{authenticator.test.string.foo()}      #9
		 *  
		 *  #{authenticator.broken}                 #11
		 */
		IFile file = project.getFile("WebContent/inputname.xhtml");
		AbstractResourceMarkerTest.assertMarkerIsCreated(file, "\"broken\" cannot be resolved'", 11);
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(file, "\"authenticator\" cannot be resolved'", 6);
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(file, "\"authenticator\" cannot be resolved'", 7);
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(file, "\"authenticator\" cannot be resolved'", 8);
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(file, "\"authenticator\" cannot be resolved'", 9);
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(file, "\"authenticate()\" cannot be resolved'", 7);
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(file, "\"test\" cannot be resolved'", 6);
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(file, "\"test\" cannot be resolved'", 8);
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(file, "\"test\" cannot be resolved'", 9);
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(file, "\"string\" cannot be resolved'", 6);
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(file, "\"string\" cannot be resolved'", 9);
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(file, "\"newString\" cannot be resolved'", 6);
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(file, "\"foo()\" cannot be resolved'", 8);
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(file, "\"foo()\" cannot be resolved'", 9);
	}

	/**
	 * See https://issues.jboss.org/browse/JBIDE-10872
	 * EL incremental validation doesn't work for complex ELs.
	 * @throws Exception
	 */
	public void testIncrementalBuildValidation() throws Exception {
		boolean saveAutoBuild = ResourcesUtils.setBuildAutomatically(false);

		// Authenticator.java
		IFile javaFile = project.getFile("JavaSource/demo/Authenticator.java");
		IFile broken = project.getFile("JavaSource/demo/Authenticator.broken");
		IFile original = project.getFile("JavaSource/demo/Authenticator.original");
		javaFile.setContents(broken.getContents(), IFile.FORCE, new NullProgressMonitor());

		TestUtil.validate(javaFile);

		IFile file = project.getFile("WebContent/inputname.xhtml");
		AbstractResourceMarkerTest.assertMarkerIsCreated(file, "\"broken\" cannot be resolved'", 11);
		AbstractResourceMarkerTest.assertMarkerIsCreated(file, "\"test\" cannot be resolved'", 6, 8, 9);

		AbstractResourceMarkerTest.assertMarkerIsNotCreated(file, "\"authenticator\" cannot be resolved'", 6);
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(file, "\"authenticator\" cannot be resolved'", 7);
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(file, "\"authenticator\" cannot be resolved'", 8);
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(file, "\"authenticator\" cannot be resolved'", 9);
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(file, "\"authenticate()\" cannot be resolved'", 7);
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(file, "\"string\" cannot be resolved'", 6);
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(file, "\"string\" cannot be resolved'", 9);
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(file, "\"newString\" cannot be resolved'", 6);
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(file, "\"foo()\" cannot be resolved'", 8);
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(file, "\"foo()\" cannot be resolved'", 9);

		javaFile.setContents(original.getContents(), IFile.FORCE, new NullProgressMonitor());

		TestUtil.validate(javaFile);

		assertDefaultErrors();

		// Test.java
		javaFile = project.getFile("JavaSource/demo/Test.java");
		broken = project.getFile("JavaSource/demo/Test.broken");
		original = project.getFile("JavaSource/demo/Test.original");
		javaFile.setContents(broken.getContents(), IFile.FORCE, new NullProgressMonitor());

		TestUtil.validate(javaFile);

		AbstractResourceMarkerTest.assertMarkerIsCreated(file, "\"broken\" cannot be resolved'", 11);
		AbstractResourceMarkerTest.assertMarkerIsCreated(file, "\"string\" cannot be resolved'", 6, 9);

		AbstractResourceMarkerTest.assertMarkerIsNotCreated(file, "\"authenticator\" cannot be resolved'", 6);
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(file, "\"authenticator\" cannot be resolved'", 7);
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(file, "\"authenticator\" cannot be resolved'", 8);
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(file, "\"authenticator\" cannot be resolved'", 9);
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(file, "\"authenticate()\" cannot be resolved'", 7);
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(file, "\"test\" cannot be resolved'", 6);
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(file, "\"test\" cannot be resolved'", 8);
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(file, "\"test\" cannot be resolved'", 9);
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(file, "\"newString\" cannot be resolved'", 6);
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(file, "\"foo()\" cannot be resolved'", 8);
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(file, "\"foo()\" cannot be resolved'", 9);

		javaFile.setContents(original.getContents(), IFile.FORCE, new NullProgressMonitor());

		TestUtil.validate(javaFile);

		assertDefaultErrors();

		// Test2.java
		javaFile = project.getFile("JavaSource/demo/Test2.java");
		broken = project.getFile("JavaSource/demo/Test2.broken");
		original = project.getFile("JavaSource/demo/Test2.original");
		javaFile.setContents(broken.getContents(), IFile.FORCE, new NullProgressMonitor());

		TestUtil.validate(javaFile);

		AbstractResourceMarkerTest.assertMarkerIsCreated(file, "\"broken\" cannot be resolved'", 11);
		AbstractResourceMarkerTest.assertMarkerIsCreated(file, "\"newString\" cannot be resolved'", 6);

		AbstractResourceMarkerTest.assertMarkerIsNotCreated(file, "\"authenticator\" cannot be resolved'", 6);
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(file, "\"authenticator\" cannot be resolved'", 7);
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(file, "\"authenticator\" cannot be resolved'", 8);
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(file, "\"authenticator\" cannot be resolved'", 9);
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(file, "\"authenticate()\" cannot be resolved'", 7);
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(file, "\"test\" cannot be resolved'", 6);
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(file, "\"test\" cannot be resolved'", 8);
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(file, "\"test\" cannot be resolved'", 9);
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(file, "\"string\" cannot be resolved'", 6);
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(file, "\"string\" cannot be resolved'", 9);
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(file, "\"foo()\" cannot be resolved'", 8);
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(file, "\"foo()\" cannot be resolved'", 9);

		javaFile.setContents(original.getContents(), IFile.FORCE, new NullProgressMonitor());

		TestUtil.validate(javaFile);

		assertDefaultErrors();

		ResourcesUtils.setBuildAutomatically(saveAutoBuild);
	}
}