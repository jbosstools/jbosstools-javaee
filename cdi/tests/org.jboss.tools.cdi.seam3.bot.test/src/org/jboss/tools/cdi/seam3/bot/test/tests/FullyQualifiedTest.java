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

import java.util.Arrays;
import java.util.List;

import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.jboss.tools.cdi.bot.test.CDIConstants;
import org.jboss.tools.cdi.bot.test.annotations.ProblemsType;
import org.jboss.tools.cdi.bot.test.uiutils.CollectionsUtil;
import org.jboss.tools.cdi.seam3.bot.test.base.SolderTestBase;
import org.jboss.tools.cdi.seam3.bot.test.util.SeamLibraries;
import org.junit.Test;

public class FullyQualifiedTest extends SolderTestBase {

	private String APPLICATION_CLASS = "Application.java";
	
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
	
	@Test
	public void testNonNamedBean() {

		String projectName = "fullyQualified1";
		
		importProjectWithLibrary(projectName, SeamLibraries.SOLDER);
		
		setEd(packageExplorer.openFile(projectName, CDIConstants.SRC, 
				getPackageName(), APPLICATION_CLASS).toTextEditor());
		
		List<String> beansProposal = editResourceUtil.getProposalList(APPLICATION_CLASS, 
				"\"#{}\"", 3, 0);
		List<String> nonexpectedList = Arrays.asList("cdi.seam.manager : Manager");
		assertTrue(CollectionsUtil.checkNoMatch(beansProposal, nonexpectedList));
		
	}

	@Test
	public void testQualifiedPackage() {
		
		String projectName = "fullyQualified2";
		
		importProjectWithLibrary(projectName, SeamLibraries.SOLDER);
		
		setEd(packageExplorer.openFile(projectName, CDIConstants.SRC, 
				getPackageName(), APPLICATION_CLASS).toTextEditor());
		
		List<String> beansProposal = editResourceUtil.getProposalList(APPLICATION_CLASS, 
				"\"#{}\"", 3, 0);
		List<String> nonexpectedList = Arrays.asList("cdi.test.myBean3 : MyBean3", 
				"cdi.test.myBean4 : MyBean4");
		assertTrue(CollectionsUtil.checkNoMatch(beansProposal, nonexpectedList));
		
		List<String> expectedList = Arrays.asList("cdi.seam.myBean1 : MyBean1", 
				"cdi.seam.myBean2 : MyBean2");
		assertTrue(CollectionsUtil.checkMatch(beansProposal, expectedList));
		
		expectedList = Arrays.asList("myBean3 : MyBean3", 
				"myBean4 : MyBean4");
		assertTrue(CollectionsUtil.checkMatch(beansProposal, expectedList));
		
	}

	@Test
	public void testDifferentExistedPackage() {
		
		String projectName = "fullyQualified3";
		
		importProjectWithLibrary(projectName, SeamLibraries.SOLDER);
		
		setEd(packageExplorer.openFile(projectName, CDIConstants.SRC, 
				getPackageName(), APPLICATION_CLASS).toTextEditor());
		
		List<String> beansProposal = editResourceUtil.getProposalList(APPLICATION_CLASS, 
				"\"#{}\"", 3, 0);
		List<String> nonexpectedList = Arrays.asList("cdi.seam.myBean1 : MyBean1");
		assertTrue(CollectionsUtil.checkNoMatch(beansProposal, nonexpectedList));
		
		List<String> expectedList = Arrays.asList("cdi.test.myBean1 : MyBean1");
		assertTrue(CollectionsUtil.checkMatch(beansProposal, expectedList));
		
	}

	@Test
	public void testDifferentNonExistedPackage() {
		
		String projectName = "fullyQualified4";
		String myBean1 = "MyBean1.java";
		
		importProjectWithLibrary(projectName, SeamLibraries.SOLDER);
		
		setEd(packageExplorer.openFile(projectName, CDIConstants.SRC, 
				getPackageName(), myBean1).toTextEditor());
		
		SWTBotTreeItem[] validationProblems = quickFixHelper.getProblems(
				ProblemsType.ERRORS, projectName);
		assertTrue(validationProblems.length > 0);
		assertTrue(validationProblems.length == 1);
		assertTrue(validationProblems[0].getText().contains("cannot be resolved to a type"));
		
		editResourceUtil.replaceInEditor("cdi.test.MyBean1", "cdi.seam.MyBean2");
		validationProblems = quickFixHelper.getProblems(
				ProblemsType.ERRORS, projectName);
		assertTrue(validationProblems.length > 0);
		assertTrue(validationProblems.length == 1);
		assertTrue(validationProblems[0].getText().contains("cannot be resolved to a type"));
		
	}

	@Test
	public void testFullyNamedBean() {
		
		String projectName = "fullyQualified5";
		
		importProjectWithLibrary(projectName, SeamLibraries.SOLDER);
		
		setEd(packageExplorer.openFile(projectName, CDIConstants.SRC, 
				getPackageName(), APPLICATION_CLASS).toTextEditor());
		
		List<String> beansProposal = editResourceUtil.getProposalList(APPLICATION_CLASS, 
				"\"#{}\"", 3, 0);
		List<String> nonexpectedList = Arrays.asList("cdi.seam.myBean1 : MyBean1");
		assertTrue(CollectionsUtil.checkNoMatch(beansProposal, nonexpectedList));
		
		List<String> expectedList = Arrays.asList("cdi.seam.bean : MyBean1");
		assertTrue(CollectionsUtil.checkMatch(beansProposal, expectedList));
		
		setEd(packageExplorer.openFile(projectName, CDIConstants.SRC, 
				getPackageName(), "MyBean1.java").toTextEditor());
		
		editResourceUtil.replaceInEditor("@FullyQualified", 
				"@FullyQualified(cdi.test.MyBean2.class)");
		
		setEd(packageExplorer.openFile(projectName, CDIConstants.SRC, 
				getPackageName(), APPLICATION_CLASS).toTextEditor());
		beansProposal = editResourceUtil.getProposalList(APPLICATION_CLASS, 
				"\"#{}\"", 3, 0);
		
		nonexpectedList = Arrays.asList("cdi.seam.bean : MyBean1");
		assertTrue(CollectionsUtil.checkNoMatch(beansProposal, nonexpectedList));
		
		expectedList = Arrays.asList("cdi.test.bean : MyBean1");
		assertTrue(CollectionsUtil.checkMatch(beansProposal, expectedList));
		
	}

	@Test
	public void testProducerMethod() {
		
		String projectName = "fullyQualified6";
		
		importProjectWithLibrary(projectName, SeamLibraries.SOLDER);
		
		setEd(packageExplorer.openFile(projectName, CDIConstants.SRC, 
				getPackageName(), APPLICATION_CLASS).toTextEditor());
		
		List<String> beansProposal = editResourceUtil.getProposalList(APPLICATION_CLASS, 
				"\"#{}\"", 3, 0);
		List<String> nonexpectedList = Arrays.asList("cdi.seam.myBean1 : MyBean1", 
				"cdi.seam.myBean1 : MyBean1 - MyBean1", "cdi.seam.myBean1 : bean - MyBean1");
		assertTrue(CollectionsUtil.checkNoMatch(beansProposal, nonexpectedList));
		
		List<String> expectedList = Arrays.asList("cdi.seam.uniqueBean : MyBean1 - MyBean1");
		assertTrue(CollectionsUtil.checkMatch(beansProposal, expectedList));
		
		setEd(packageExplorer.openFile(projectName, CDIConstants.SRC, 
				getPackageName(), "MyBean1.java").toTextEditor());
		
		editResourceUtil.replaceInEditor("@Named", 
				"@Named(\"bean\")");
		
		setEd(packageExplorer.openFile(projectName, CDIConstants.SRC, 
				getPackageName(), APPLICATION_CLASS).toTextEditor());
		beansProposal = editResourceUtil.getProposalList(APPLICATION_CLASS, 
				"\"#{}\"", 3, 0);
		
		nonexpectedList = Arrays.asList("cdi.seam.uniqueBean : MyBean1 - MyBean1");
		assertTrue(CollectionsUtil.checkNoMatch(beansProposal, nonexpectedList));
		
		expectedList = Arrays.asList("cdi.seam.bean : MyBean1 - MyBean1");
		assertTrue(CollectionsUtil.checkMatch(beansProposal, expectedList));
		
		setEd(packageExplorer.openFile(projectName, CDIConstants.SRC, 
				getPackageName(), "MyBean1.java").toTextEditor());
		
		editResourceUtil.replaceInEditor("@FullyQualified", 
				"@FullyQualified(cdi.test.MyBean2.class)");
		
		setEd(packageExplorer.openFile(projectName, CDIConstants.SRC, 
				getPackageName(), APPLICATION_CLASS).toTextEditor());
		beansProposal = editResourceUtil.getProposalList(APPLICATION_CLASS, 
				"\"#{}\"", 3, 0);
		
		nonexpectedList = Arrays.asList("cdi.seam.uniqueBean : MyBean1 - MyBean1",
				"cdi.seam.bean : MyBean1 - MyBean1");
		assertTrue(CollectionsUtil.checkNoMatch(beansProposal, nonexpectedList));
		
		expectedList = Arrays.asList("cdi.test.bean : MyBean1 - MyBean1");
		assertTrue(CollectionsUtil.checkMatch(beansProposal, expectedList));
		
	}
		
	@Test
	public void testProducerField() {
		
		String projectName = "fullyQualified7";
		
		importProjectWithLibrary(projectName, SeamLibraries.SOLDER);
		
		setEd(packageExplorer.openFile(projectName, CDIConstants.SRC, 
				getPackageName(), APPLICATION_CLASS).toTextEditor());
		
		List<String> beansProposal = editResourceUtil.getProposalList(APPLICATION_CLASS, 
				"\"#{}\"", 3, 0);
		List<String> nonexpectedList = Arrays.asList("cdi.seam.myBean1 : MyBean1", 
				"cdi.seam.myBean1 : MyBean1 - MyBean1", "cdi.seam.myBean1 : bean - MyBean1");
		assertTrue(CollectionsUtil.checkNoMatch(beansProposal, nonexpectedList));
		
		List<String> expectedList = Arrays.asList("cdi.seam.uniqueBean : MyBean1 - MyBean1");
		assertTrue(CollectionsUtil.checkMatch(beansProposal, expectedList));
		
		
		setEd(packageExplorer.openFile(projectName, CDIConstants.SRC, 
				getPackageName(), "MyBean1.java").toTextEditor());
		
		editResourceUtil.replaceInEditor("@FullyQualified", 
				"@FullyQualified(cdi.test.MyBean2.class)");
		
		setEd(packageExplorer.openFile(projectName, CDIConstants.SRC, 
				getPackageName(), APPLICATION_CLASS).toTextEditor());
		beansProposal = editResourceUtil.getProposalList(APPLICATION_CLASS, 
				"\"#{}\"", 3, 0);
		
		nonexpectedList = Arrays.asList("cdi.seam.uniqueBean : MyBean1 - MyBean1",
				"cdi.seam.bean : MyBean1 - MyBean1", "cdi.test.bean : MyBean1 - MyBean1");
		assertTrue(CollectionsUtil.checkNoMatch(beansProposal, nonexpectedList));
		
		expectedList = Arrays.asList("cdi.test.uniqueBean : MyBean1 - MyBean1");
		assertTrue(CollectionsUtil.checkMatch(beansProposal, expectedList));
		
		setEd(packageExplorer.openFile(projectName, CDIConstants.SRC, 
				getPackageName(), "MyBean1.java").toTextEditor());
		
		editResourceUtil.replaceInEditor("@Named", 
				"@Named(\"bean\")");
		
		setEd(packageExplorer.openFile(projectName, CDIConstants.SRC, 
				getPackageName(), APPLICATION_CLASS).toTextEditor());
		beansProposal = editResourceUtil.getProposalList(APPLICATION_CLASS, 
				"\"#{}\"", 3, 0);
		
		nonexpectedList = Arrays.asList("cdi.seam.uniqueBean : MyBean1 - MyBean1",
				"cdi.seam.bean : MyBean1 - MyBean1", "cdi.test.uniqueBean : MyBean1 - MyBean1");
		assertTrue(CollectionsUtil.checkNoMatch(beansProposal, nonexpectedList));
		
		expectedList = Arrays.asList("cdi.test.bean : MyBean1 - MyBean1");
		assertTrue(CollectionsUtil.checkMatch(beansProposal, expectedList));
		
	}
	
}
