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
import org.jboss.tools.cdi.seam3.bot.test.util.SeamLibrary;
import org.junit.Test;

/**
 * 
 * @author jjankovi
 *
 */
public class VetoAnnotationTest extends SolderAnnotationTestBase {

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
		
		importSeam3ProjectWithLibrary(projectName, SeamLibrary.SOLDER);
		
		setEd(packageExplorer.openFile(projectName, CDIConstants.SRC, 
				getPackageName(), APPLICATION_CLASS).toTextEditor());
		
		testNoBeanValidationProblemExists(projectName);
		
		setEd(packageExplorer.openFile(projectName, CDIConstants.SRC, 
				getPackageName(), otherBean + ".java").toTextEditor());
		editResourceUtil.replaceInEditor("public class " + otherBean,
				"public class " + otherBean + " extends " + vetoBean);
		
		testProperInjectBean(projectName, "bean", otherBean);
		
	}
	
	@Test
	public void testSessionBean() {
		
		String vetoBean = "Bean";
		String otherBean = "OtherBean";
		String projectName = "veto2";
		
		importSeam3ProjectWithLibrary(projectName, SeamLibrary.SOLDER);
		
		setEd(packageExplorer.openFile(projectName, CDIConstants.SRC, 
				getPackageName(), APPLICATION_CLASS).toTextEditor());
		
		testNoBeanValidationProblemExists(projectName);
		
		setEd(packageExplorer.openFile(projectName, CDIConstants.SRC, 
				getPackageName(), otherBean + ".java").toTextEditor());
		editResourceUtil.replaceInEditor("public class " + otherBean,
				"public class " + otherBean + " extends " + vetoBean);
		
		testProperInjectBean(projectName, "bean", otherBean);
		
	}
	
	@Test
	public void testProducerMethod() {
		
		String vetoBean = "Bean";
		String projectName = "veto3";
		
		importSeam3ProjectWithLibrary(projectName, SeamLibrary.SOLDER);
		
		setEd(packageExplorer.openFile(projectName, CDIConstants.SRC, 
				getPackageName(), APPLICATION_CLASS).toTextEditor());
		
		testNoBeanValidationProblemExists(projectName);
		
		setEd(packageExplorer.openFile(projectName, CDIConstants.SRC, 
				getPackageName(), vetoBean + ".java").toTextEditor());
		editResourceUtil.replaceInEditor("@Veto", "");
		editResourceUtil.replaceInEditor("import org.jboss.seam.solder.core.Veto;", "");
		
		testProperInjectProducer(projectName, "manager", vetoBean, "getManager");
		
	}
	
	@Test
	public void testProducerField() {
		
		String vetoBean = "Bean";
		String projectName = "veto4";
		
		importSeam3ProjectWithLibrary(projectName, SeamLibrary.SOLDER);
		
		setEd(packageExplorer.openFile(projectName, CDIConstants.SRC, 
				getPackageName(), APPLICATION_CLASS).toTextEditor());
		
		testNoBeanValidationProblemExists(projectName);
		
		setEd(packageExplorer.openFile(projectName, CDIConstants.SRC, 
				getPackageName(), vetoBean + ".java").toTextEditor());
		editResourceUtil.replaceInEditor("@Veto", "");
		editResourceUtil.replaceInEditor("import org.jboss.seam.solder.core.Veto;", "");
		
		testProperInjectProducer(projectName, "manager", vetoBean,  "manager");
		
	}
	
	@Test
	public void testObserverMethods() {
		
		String vetoBean = "Bean";
		String projectName = "veto5";
		String eventAttribute = "eventAttribute";
		
		importSeam3ProjectWithLibrary(projectName, SeamLibrary.SOLDER);
		
		setEd(packageExplorer.openFile(projectName, CDIConstants.SRC, 
				getPackageName(), APPLICATION_CLASS).toTextEditor());
		
		assertFalse(openOnUtil.openOnByOption(eventAttribute, APPLICATION_CLASS, 
				CDIConstants.OPEN_CDI_OBSERVER_METHOD));
		
		setEd(packageExplorer.openFile(projectName, CDIConstants.SRC, 
				getPackageName(), vetoBean + ".java").toTextEditor());
		editResourceUtil.replaceInEditor("@Veto", "");
		editResourceUtil.replaceInEditor("import org.jboss.seam.solder.core.Veto;", "");
		
		assertTrue(openOnUtil.openOnByOption(eventAttribute, APPLICATION_CLASS, 
				CDIConstants.OPEN_CDI_OBSERVER_METHOD));
		assertTrue(getEd().getTitle().equals(vetoBean + ".java"));
		assertTrue(getEd().getSelection().equals("method"));
		
	}
	
}
