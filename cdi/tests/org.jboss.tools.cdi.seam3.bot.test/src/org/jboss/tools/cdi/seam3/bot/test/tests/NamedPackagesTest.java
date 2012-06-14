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

import org.jboss.tools.cdi.bot.test.CDIConstants;
import org.jboss.tools.cdi.bot.test.uiutils.CollectionsUtil;
import org.jboss.tools.cdi.seam3.bot.test.base.Seam3TestBase;
import org.jboss.tools.cdi.seam3.bot.test.util.SeamLibrary;
import org.junit.After;
import org.junit.Test;

/**
 * 
 * @author jjankovi
 *
 */
public class NamedPackagesTest extends Seam3TestBase {

	private final String CDI_SEAM_PACKAGE = "cdi.seam";
	private final String CDI_TEST_PACKAGE = "cdi.test";
	private final String ORG_JBOSS_PACKAGE = "org.jboss";
	private static String projectName = "named";
	
	private final String PACKAGE_INFO_JAVA_CDI = "package-info.java.cdi";
	private final String PACKAGE_INFO_JAVA = "package-info.java";
	
	private final String MANAGER_JAVA = "Manager.java";
	
	@After
	public void waitForJobs() {
		projectExplorer.deleteAllProjects();		
	} 
	
	@Test
	public void testNoNamedPackaged() {
		
		importSeam3ProjectWithLibrary(projectName, SeamLibrary.SOLDER_3_1);
		
		setEd(packageExplorer.openFile(projectName, CDIConstants.SRC, 
				ORG_JBOSS_PACKAGE, MANAGER_JAVA).toTextEditor());
		List<String> beansProposal = editResourceUtil.getProposalList(
				MANAGER_JAVA, "\"#{}\"", 3, 0);
		List<String> nonexpectedList = Arrays.asList("bean1 : Bean1", "bean2 : Bean2", 
				"bean3 : Bean3", "bean4 : Bean4");
		assertTrue(CollectionsUtil.checkNoMatch(beansProposal, nonexpectedList));
		
	}
	
	@Test
	public void testOneNamedPackage() {
		
		importSeam3ProjectWithLibrary(projectName, SeamLibrary.SOLDER_3_1);
		
		editResourceUtil.renameFileInExplorerBase(packageExplorer, PACKAGE_INFO_JAVA_CDI, 
				projectName + "/" + CDIConstants.SRC + "/" + CDI_SEAM_PACKAGE, PACKAGE_INFO_JAVA);
		eclipse.cleanAllProjects();
		
		setEd(packageExplorer.openFile(projectName, CDIConstants.SRC, 
				ORG_JBOSS_PACKAGE, MANAGER_JAVA).toTextEditor());
		List<String> beansProposal = editResourceUtil.getProposalList(
				MANAGER_JAVA, "\"#{}\"", 3, 0);
		
		List<String> nonExpectedList = Arrays.asList("bean3 : Bean3", "bean4 : Bean4");
		assertTrue(CollectionsUtil.checkNoMatch(beansProposal, nonExpectedList));
		List<String> expectedList = Arrays.asList("bean1 : Bean1", "bean2 : Bean2");
		assertTrue(CollectionsUtil.checkMatch(beansProposal, expectedList));
		
	}
	
	@Test
	public void testBothNamedPackages() {
		
		importSeam3ProjectWithLibrary(projectName, SeamLibrary.SOLDER_3_1);
		
		editResourceUtil.renameFileInExplorerBase(packageExplorer, PACKAGE_INFO_JAVA_CDI, 
				projectName + "/" + CDIConstants.SRC + "/" + CDI_SEAM_PACKAGE, PACKAGE_INFO_JAVA);
		editResourceUtil.renameFileInExplorerBase(packageExplorer, PACKAGE_INFO_JAVA_CDI, 
				projectName + "/" + CDIConstants.SRC + "/" + CDI_TEST_PACKAGE, PACKAGE_INFO_JAVA);
		eclipse.cleanAllProjects();
		
		setEd(packageExplorer.openFile(projectName, CDIConstants.SRC, 
				ORG_JBOSS_PACKAGE, MANAGER_JAVA).toTextEditor());
		List<String> beansProposal = editResourceUtil.getProposalList(
				MANAGER_JAVA, "\"#{}\"", 3, 0);
		
		List<String> expectedList = Arrays.asList("bean1 : Bean1", "bean2 : Bean2", 
				"bean3 : Bean3", "bean4 : Bean4");
		assertTrue(CollectionsUtil.checkMatch(beansProposal, expectedList));
		
	}
	
}
