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

import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.jboss.tools.cdi.bot.test.CDIConstants;
import org.jboss.tools.cdi.bot.test.annotations.ProblemsType;
import org.jboss.tools.cdi.seam3.bot.test.base.SolderTestBase;
import org.jboss.tools.cdi.seam3.bot.test.util.SeamLibraries;
import org.jboss.tools.ui.bot.ext.SWTEclipseExt;
import org.jboss.tools.ui.bot.ext.types.ViewType;
import org.junit.Test;

public class ExactAnnotationTest extends SolderTestBase {

	private String className = "Application.java";
	
	private final String EXACT_INTERFACE = "exact-interface";
	private final String EXACT_BEANS = "exact-beans";
	private final String MULTIPLE_BEANS = "Multiple beans are eligible for injection"; 
	
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
		
		projectImportHelper.importTestProject("/resources/projects/" + projectName, 
				projectName);		
		addAndCheckLibraryInProject(projectName, SeamLibraries.SOLDER);
		
		setEd(packageExplorer.openFile(projectName, CDIConstants.SRC, 
				"cdi.seam", className).toTextEditor());
		
		testExactImproperValue(projectName);
				
		editResourceUtil.replaceInEditor(managerClass, peopleManager + ".class");
		testExactProperValue(projectName, peopleManager);

		
		bot.editorByTitle(className).show();
		
		editResourceUtil.replaceInEditor(peopleManager + ".class", otherManager + ".class");
		testExactProperValue(projectName, otherManager);
		
	}
	
	private void testExactImproperValue(String projectName) {
		
		SWTEclipseExt.showView(bot,ViewType.PROBLEMS);
		SWTBotTreeItem[] validationProblems = quickFixHelper.getProblems(ProblemsType.WARNINGS, projectName);
		assertTrue(validationProblems.length > 0);
		assertTrue(validationProblems.length == 1);
		assertContains(MULTIPLE_BEANS, validationProblems[0].getText());
		
	}
	
	private void testExactProperValue(String projectName, String value) {
		
		SWTEclipseExt.showView(bot,ViewType.PROBLEMS);
		SWTBotTreeItem[] validationProblems = quickFixHelper.getProblems(ProblemsType.WARNINGS, projectName);
		assertTrue(validationProblems.length == 0);
		assertTrue(openOnUtil.openOnByOption(value + ".class", className, "Open @Inject Bean"));
		assertTrue(getEd().getTitle().equals(value + ".java"));
		
	}
	
}