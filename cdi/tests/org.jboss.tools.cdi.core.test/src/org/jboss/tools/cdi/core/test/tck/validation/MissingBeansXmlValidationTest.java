/******************************************************************************* 
 * Copyright (c) 2013-2014 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.cdi.core.test.tck.validation;

import junit.framework.TestCase;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.osgi.util.NLS;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.core.CDIVersion;
import org.jboss.tools.cdi.core.ICDIProject;
import org.jboss.tools.cdi.internal.core.scanner.lib.BeanArchiveDetector;
import org.jboss.tools.cdi.internal.core.validation.CDIValidationMessages;
import org.jboss.tools.common.base.test.validation.TestUtil;
import org.jboss.tools.test.util.ResourcesUtils;
import org.jboss.tools.tests.AbstractResourceMarkerTest;

/**
 * @author Alexey Kazakov
 */
public class MissingBeansXmlValidationTest extends TestCase {

	IProject missingBeansXmlParentProject;
	IProject missingBeansXmlChildProject;
	IProject missingBeansXmlProjectCDI11;
	IProject missingBeansXmlProjectCDI12;
	boolean saveAutoBuild;

	@Override
	public void setUp() throws Exception {
		missingBeansXmlParentProject = ResourcesPlugin.getWorkspace().getRoot().getProject("missingBeansXmlParentProject");
		assertTrue("Can't load missingBeansXmlParentProject", missingBeansXmlParentProject.exists());
		missingBeansXmlChildProject = ResourcesPlugin.getWorkspace().getRoot().getProject("missingBeansXmlChildProject");
		assertTrue("Can't load missingBeansXmlChildProject", missingBeansXmlChildProject.exists());
		missingBeansXmlProjectCDI11 = ResourcesPlugin.getWorkspace().getRoot().getProject("missingBeansXmlProjectCDI11");
		assertTrue("Can't load missingBeansXmlProjectCDI11", missingBeansXmlProjectCDI11.exists());
		missingBeansXmlProjectCDI12 = ResourcesPlugin.getWorkspace().getRoot().getProject("missingBeansXmlProjectCDI12");
		assertTrue("Can't load missingBeansXmlProjectCDI12", missingBeansXmlProjectCDI12.exists());
		saveAutoBuild = ResourcesUtils.setBuildAutomatically(false);
		TestUtil._waitForValidation(missingBeansXmlParentProject);
		TestUtil._waitForValidation(missingBeansXmlChildProject);
		TestUtil._waitForValidation(missingBeansXmlProjectCDI11);
		TestUtil._waitForValidation(missingBeansXmlProjectCDI12);
	}

	@Override
	public void tearDown() throws CoreException {
		ResourcesUtils.setBuildAutomatically(saveAutoBuild);
	}

	public void testMissingBeansXml() throws CoreException {
		AbstractResourceMarkerTest.assertMarkerIsCreated(missingBeansXmlChildProject, NLS.bind(CDIValidationMessages.MISSING_BEANS_XML, "missingBeansXmlChildProject"), 0);
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(missingBeansXmlParentProject, NLS.bind(CDIValidationMessages.MISSING_BEANS_XML, "missingBeansXmlParentProject"));

		IFile beansXml = missingBeansXmlParentProject.getFile("src/META-INF/beans.xml");
		AbstractResourceMarkerTest.assertMarkerIsCreated(beansXml, NLS.bind(CDIValidationMessages.UNKNOWN_INTERCEPTOR_CLASS_NAME, "demo.TestInt"), 5);
	}

	public void testCreatingBeansXml() throws CoreException {
		boolean saveAutoBuild = ResourcesUtils.setBuildAutomatically(false);
		try {
			IFile beansXml = missingBeansXmlChildProject.getFile("src/META-INF/beans_.xml");
			IFile newBeansXml = missingBeansXmlChildProject.getFile("src/META-INF/beans.xml");
			beansXml.move(newBeansXml.getFullPath(), true, new NullProgressMonitor());
			TestUtil.validate(missingBeansXmlChildProject, new IResource[]{beansXml, newBeansXml});

			AbstractResourceMarkerTest.assertMarkerIsNotCreated(missingBeansXmlChildProject, NLS.bind(CDIValidationMessages.MISSING_BEANS_XML, "missingBeansXmlChildProject"));
			AbstractResourceMarkerTest.assertMarkerIsNotCreated(missingBeansXmlParentProject, NLS.bind(CDIValidationMessages.MISSING_BEANS_XML, "missingBeansXmlParentProject"));
		} finally {
			IFile beansXml = missingBeansXmlChildProject.getFile("src/META-INF/beans.xml");
			IFile newBeansXml = missingBeansXmlChildProject.getFile("src/META-INF/beans_.xml");
			beansXml.move(newBeansXml.getFullPath(), true, new NullProgressMonitor());
			TestUtil.validate(newBeansXml);

			ResourcesUtils.setBuildAutomatically(saveAutoBuild);
		}
	}

	public void testMissingBeansXmlCDI11() throws CoreException {
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(missingBeansXmlProjectCDI11, NLS.bind(CDIValidationMessages.MISSING_BEANS_XML, "missingBeansXmlProjectCDI11"));
	}

	public void testMissingBeansXmlCDI12() throws CoreException {
		AbstractResourceMarkerTest.assertMarkerIsNotCreated(missingBeansXmlProjectCDI12, NLS.bind(CDIValidationMessages.MISSING_BEANS_XML, "missingBeansXmlProjectCDI12"));
		ICDIProject cdi = CDICorePlugin.getCDIProject(missingBeansXmlProjectCDI12, true);
		assertEquals(CDIVersion.CDI_1_2, cdi.getVersion());
		assertEquals(BeanArchiveDetector.ANNOTATED, cdi.getNature().getBeanDiscoveryMode());
		
		//check beans loaded in annotated mode
		assertTrue(cdi.getBeans("beanNotAnnotated", false).isEmpty());
		assertFalse(cdi.getBeans(new Path("/missingBeansXmlProjectCDI12/src/beans/DecoratorAnnotatedBean.java")).isEmpty());
		assertFalse(cdi.getBeans("scopeAnnotatedBean", false).isEmpty());
		assertFalse(cdi.getBeans("stereotypeAnnotatedBean", false).isEmpty());
	}
}