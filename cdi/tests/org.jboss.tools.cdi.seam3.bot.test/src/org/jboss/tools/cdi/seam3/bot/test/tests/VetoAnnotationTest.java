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
import org.junit.Ignore;
import org.junit.Test;

public class VetoAnnotationTest extends SolderTestBase {

	private String APPLICATION_CLASS = "Application.java";
	
	@Override
	public String getProjectName() {
		return "veto1";
	}
	
	@Override
	public void waitForJobs() {
		projectExplorer.deleteAllProjects();		
	} 
	
	@Override
	public void prepareWorkspace() {
		
	}
	
	
	@Test
	public void testManagedBeans() {
		
		String vetoBean = "Bean";
		String otherBean = "OtherBean";
		String projectName = "veto1";
		
		importProjectWithLibrary(projectName, SeamLibraries.SOLDER);
		
		setEd(packageExplorer.openFile(projectName, CDIConstants.SRC, 
				"cdi.seam", APPLICATION_CLASS).toTextEditor());
		
		testVetoAnnotationImproperValue(projectName);
		
		setEd(packageExplorer.openFile(projectName, CDIConstants.SRC, 
				"cdi.seam", otherBean + ".java").toTextEditor());
		editResourceUtil.replaceInEditor("public class " + otherBean,
				"public class " + otherBean + " extends " + vetoBean);
		
		testVetoAnnotationProperValue(projectName, "bean", otherBean, false, null);
		
	}
	
	@Test
	public void testSessionBean() {
		
		String vetoBean = "Bean";
		String otherBean = "OtherBean";
		String projectName = "veto2";
		
		importProjectWithLibrary(projectName, SeamLibraries.SOLDER);
		
		setEd(packageExplorer.openFile(projectName, CDIConstants.SRC, 
				"cdi.seam", APPLICATION_CLASS).toTextEditor());
		
		testVetoAnnotationImproperValue(projectName);
		
		setEd(packageExplorer.openFile(projectName, CDIConstants.SRC, 
				"cdi.seam", otherBean + ".java").toTextEditor());
		editResourceUtil.replaceInEditor("public class " + otherBean,
				"public class " + otherBean + " extends " + vetoBean);
		
		testVetoAnnotationProperValue(projectName, "bean", otherBean, false, null);
		
	}
	
	@Test
	public void testProducerMethod() {
		
		String vetoBean = "Bean";
		String projectName = "veto3";
		
		importProjectWithLibrary(projectName, SeamLibraries.SOLDER);
		
		setEd(packageExplorer.openFile(projectName, CDIConstants.SRC, 
				"cdi.seam", APPLICATION_CLASS).toTextEditor());
		
		testVetoAnnotationImproperValue(projectName);
		
		setEd(packageExplorer.openFile(projectName, CDIConstants.SRC, 
				"cdi.seam", vetoBean + ".java").toTextEditor());
		editResourceUtil.replaceInEditor("@Veto", "");
		editResourceUtil.replaceInEditor("import org.jboss.seam.solder.core.Veto;", "");
		
		testVetoAnnotationProperValue(projectName, "manager", vetoBean,
				true, "getManager");
		
	}
	
	@Test
	public void testProducerField() {
		
		String vetoBean = "Bean";
		String projectName = "veto4";
		
		importProjectWithLibrary(projectName, SeamLibraries.SOLDER);
		
		setEd(packageExplorer.openFile(projectName, CDIConstants.SRC, 
				"cdi.seam", APPLICATION_CLASS).toTextEditor());
		
		testVetoAnnotationImproperValue(projectName);
		
		setEd(packageExplorer.openFile(projectName, CDIConstants.SRC, 
				"cdi.seam", vetoBean + ".java").toTextEditor());
		editResourceUtil.replaceInEditor("@Veto", "");
		editResourceUtil.replaceInEditor("import org.jboss.seam.solder.core.Veto;", "");
		
		testVetoAnnotationProperValue(projectName, "manager", vetoBean,
				true, "manager");
		
	}
	@Ignore
	@Test
	public void testObserverMethods() {
		
		
		
	}
	/*@Ignore
	@Test
	public void testDecorator() {
		
	}
	@Ignore
	@Test
	public void testInterceptor() {
		
	}*/
	
	private void testVetoAnnotationImproperValue(String projectName) {
		
		SWTBotTreeItem[] validationProblems = quickFixHelper.getProblems(
				ProblemsType.WARNINGS, projectName);
		assertTrue(validationProblems.length > 0);
		assertTrue(validationProblems.length == 1);
		assertContains(CDIConstants.NO_BEAN_IS_ELIGIBLE, validationProblems[0].getText());
		
	}
	
	private void testVetoAnnotationProperValue(String projectName, String openOnString, String openedClass, 
			boolean producer, String producerMethod) {
		SWTBotTreeItem[] validationProblems = quickFixHelper.getProblems(
				ProblemsType.WARNINGS, projectName);
		assertTrue(validationProblems.length == 0);
		assertTrue(openOnUtil.openOnByOption(openOnString, APPLICATION_CLASS, CDIConstants.OPEN_INJECT_BEAN));
		assertTrue(getEd().getTitle().equals(openedClass + ".java"));
		if (producer) {
			assertTrue(getEd().getSelection().equals(producerMethod));
		}
	}
	
}
