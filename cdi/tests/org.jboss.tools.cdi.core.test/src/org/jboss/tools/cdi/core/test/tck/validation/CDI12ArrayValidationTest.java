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
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.jboss.tools.cdi.core.CDICorePlugin;
import org.jboss.tools.cdi.core.CDIVersion;
import org.jboss.tools.cdi.core.ICDIProject;
import org.jboss.tools.cdi.internal.core.scanner.lib.BeanArchiveDetector;
import org.jboss.tools.cdi.internal.core.validation.CDIValidationMessages;
import org.jboss.tools.common.base.test.validation.TestUtil;
import org.jboss.tools.test.util.ResourcesUtils;
import org.jboss.tools.tests.AbstractResourceMarkerTest;

/**
 * @author Viacheslav Kabanovich
 */
public class CDI12ArrayValidationTest extends TestCase {

	IProject missingBeansXmlProjectCDI11;
	IProject missingBeansXmlProjectCDI12;
	boolean saveAutoBuild;

	@Override
	public void setUp() throws Exception {
		missingBeansXmlProjectCDI11 = ResourcesPlugin.getWorkspace().getRoot().getProject("missingBeansXmlProjectCDI11");
		assertTrue("Can't load missingBeansXmlProjectCDI11", missingBeansXmlProjectCDI11.exists());
		missingBeansXmlProjectCDI12 = ResourcesPlugin.getWorkspace().getRoot().getProject("missingBeansXmlProjectCDI12");
		assertTrue("Can't load missingBeansXmlProjectCDI12", missingBeansXmlProjectCDI12.exists());
		saveAutoBuild = ResourcesUtils.setBuildAutomatically(false);
		TestUtil._waitForValidation(missingBeansXmlProjectCDI11);
		TestUtil._waitForValidation(missingBeansXmlProjectCDI12);
	}

	@Override
	public void tearDown() throws CoreException {
		ResourcesUtils.setBuildAutomatically(saveAutoBuild);
	}

	public void testMissingBeansXmlCDI12() throws CoreException {
		ICDIProject cdi = CDICorePlugin.getCDIProject(missingBeansXmlProjectCDI12, true);
		assertEquals(CDIVersion.CDI_1_2, cdi.getVersion());
		assertEquals(BeanArchiveDetector.ANNOTATED, cdi.getNature().getBeanDiscoveryMode());
		
	
		IFile file = missingBeansXmlProjectCDI12.getFile("/src/beans/DependentBean.java");
		AbstractResourceMarkerTest.assertMarkerIsCreated(file, CDIValidationMessages.INJECTION_TYPE_IS_VARIABLE[CDIVersion.CDI_1_2.getIndex()], 18, 22);
		AbstractResourceMarkerTest.assertMarkerIsCreated(file, CDIValidationMessages.PRODUCER_FIELD_TYPE_IS_VARIABLE[CDIVersion.CDI_1_2.getIndex()], 10);
		AbstractResourceMarkerTest.assertMarkerIsCreated(file, CDIValidationMessages.PRODUCER_METHOD_RETURN_TYPE_IS_VARIABLE[CDIVersion.CDI_1_2.getIndex()], 13);
		
	}
}