/******************************************************************************* 
 * Copyright (c) 2007 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.seam.core.test.project.facet;

import java.io.IOException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.jboss.tools.seam.internal.core.project.facet.ISeamFacetDataModelProperties;

public class SeamFacetInstallDelegateTest extends AbstractSeamFacetTest {

	public SeamFacetInstallDelegateTest(String name) {
		super(name);
	}

	public void testCreateWar() throws CoreException, IOException {
		
		final IFacetedProject fproj = createSeamWarProject("seamwar");
		
	}


	public void testCreateEar() throws CoreException, IOException {
		
		final IFacetedProject fproj = createSeamEarProject("seamear");
	}
	
	public void testCreateCustomProject() throws CoreException, IOException {

		IDataModel createSeamDataModel = createSeamDataModel("war");
		createSeamDataModel.setProperty(ISeamFacetDataModelProperties.SESION_BEAN_PACKAGE_NAME, "x.y.z");
		
		final IFacetedProject fproj = createSeamProject("customProject",createSeamDataModel);
		
		assertTrue(fproj.getProject().findMember("src/action/x/y/z").exists());

	}
	
}
