/*******************************************************************************
 * Copyright (c) 2010-2012 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.jboss.tools.cdi.seam3.bot.test.tests;

import org.jboss.tools.cdi.seam3.bot.test.base.SolderTestBase;
import org.junit.Test;


public class FullyQualifiedTest extends SolderTestBase {

	@Override
	public String getProjectName() {
		return "fullyQualified";
	}
	
	@Override
	public void waitForJobs() {
		projectExplorer.deleteAllProjects();		
	} 
	
	@Override
	public void prepareWorkspace() {
		
	}
	
	// should be error
	@Test
	public void testNonNamedBean() {
		
	}
	// all beans in that package are fully qualified
	@Test
	public void testQualifiedPackage() {
		
	}
	// should be ok
	@Test
	public void testDifferentExistedPackage() {
		
	}
	// should be error
	@Test
	public void testDifferentNonExistedPackage() {
		
	}
	// should be ok
	@Test
	public void testDifferentExistedClass() {
		
	}
	// should be error
	@Test
	public void testDifferentNonExistedClass() {
		
	}
	// should be ok
	@Test
	public void testNamedBean() {
		
	}
	// should be ok	
	@Test
	public void testProducerMethod() {
		
	}
	// should be ok	
	@Test
	public void testProducerField() {
		
	}
	
}
