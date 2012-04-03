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

import org.jboss.tools.cdi.bot.test.CDIConstants;
import org.jboss.tools.cdi.seam3.bot.test.base.SolderAnnotationTestBase;
import org.jboss.tools.cdi.seam3.bot.test.util.SeamLibraries;
import org.junit.Test;

public class ExactAnnotationTest extends SolderAnnotationTestBase {

	private String className = "Application.java";
	
	private final String EXACT_INTERFACE = "exact-interface";
	private final String EXACT_BEANS = "exact-beans";
	
	@Override
	public String getProjectName() {
		return "exact-interface";
	}
	
	@Override
	public void prepareWorkspace() {
		
	}
	
	@Override
	public void waitForJobs() {
		projectExplorer.deleteAllProjects();
	} 
	
	@Test
	public void testExactAnnotationForInterface() {
		
		testExactAnnotationsForProject(EXACT_INTERFACE);
		
	}
	
	@Test
	public void testExactAnnotationForBeans() {
		
		testExactAnnotationsForProject(EXACT_BEANS);
		
	}
	
	private void testExactAnnotationsForProject(String projectName) {

		String managerClass = "Manager.class";
		String peopleManager = "PeopleManager";
		String otherManager = "OtherManager";
		
		importSeam3ProjectWithLibrary(projectName, SeamLibraries.SOLDER);
		
		setEd(packageExplorer.openFile(projectName, CDIConstants.SRC, 
				getPackageName(), className).toTextEditor());
		
		testAnnotationImproperValue(projectName, false);
				
		editResourceUtil.replaceInEditor(managerClass, peopleManager + ".class");
		testAnnotationProperValue(projectName, peopleManager + ".class", 
				peopleManager, false, null);

		bot.editorByTitle(className).show();
		
		editResourceUtil.replaceInEditor(peopleManager + ".class", otherManager + ".class");
		testAnnotationProperValue(projectName, otherManager + ".class", 
				otherManager, false, null);
		
	}
	
}