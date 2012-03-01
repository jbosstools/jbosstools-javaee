/*******************************************************************************
 * Copyright (c) 2011 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.seam.core.test;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.validation.internal.core.ValidationException;
import org.jboss.tools.seam.core.test.validation.SeamCoreValidatorWrapper;
import org.jboss.tools.seam.internal.core.validation.SeamValidationMessages;
import org.jboss.tools.test.util.ProjectImportTestSetup;
import org.jboss.tools.tests.AbstractResourceMarkerTest;

/**
 * @author Alexey Kazakov
 */
public class Seam2ValidatorTest extends AbstractResourceMarkerTest {
	IProject projectEAR = null;
	IProject projectWAR = null;
	IProject projectEJB = null;

	ProjectImportTestSetup setup;

	@Override
	protected void setUp() throws Exception {
		setup = new ProjectImportTestSetup(this, "org.jboss.tools.seam.core.test", new String[]{"projects/Test1-ejb", "projects/Test1-ear", "projects/Test1"}, new String[]{"Test1-ejb", "Test1-ear", "Test1"});
		IProject[] projects = setup.importProjects();
		projectEJB = projects[0];
		projectEAR = projects[1];
		projectWAR = projects[2];
		projectEAR.build(IncrementalProjectBuilder.FULL_BUILD, null);
		projectEJB.build(IncrementalProjectBuilder.FULL_BUILD, null);
		projectWAR.build(IncrementalProjectBuilder.FULL_BUILD, null);
		setProject(projectEJB);
	}

	public void testDuplicateName() throws CoreException, ValidationException {
		IFile componentFile = projectEJB.getFile("ejbModule/org/domain/Test1/session/TestDuplicateNameOk.java");
		IFile componentsXml = projectWAR.getFile("WebContent/WEB-INF/components.xml");

		SeamCoreValidatorWrapper seamValidator = new SeamCoreValidatorWrapper(projectWAR);
		seamValidator.validate(componentFile);
		seamValidator.validate(componentsXml);

		assertFalse("Validation problem was found", seamValidator.isMessageCreated(
				SeamValidationMessages.NONUNIQUE_COMPONENT_NAME_MESSAGE, new Object[]{"org.jboss.seam.security.identity"}));
	}

	@Override
	protected void tearDown() throws Exception {
		setup.deleteProjects();
	}
}