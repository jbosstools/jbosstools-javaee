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
import org.eclipse.wst.common.project.facet.core.IFacetedProject;

public class SeamFacetInstallDelegateTest extends AbstractSeamFacetTest {

	public SeamFacetInstallDelegateTest(String name) {
		super(name);
	}

	public void testCreateWarFromScratch() throws CoreException, IOException {
		
		final IFacetedProject fproj = createSeamWarProject();
		
	}


	public void testCreateEarFromScratch() throws CoreException, IOException {
		
		final IFacetedProject fproj = createSeamEarProject();
		
				
	}

}
