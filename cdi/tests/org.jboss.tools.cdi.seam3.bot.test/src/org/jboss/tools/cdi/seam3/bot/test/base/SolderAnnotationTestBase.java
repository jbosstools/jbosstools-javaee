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

package org.jboss.tools.cdi.seam3.bot.test.base;

import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.jboss.tools.cdi.bot.test.CDIConstants;
import org.jboss.tools.cdi.bot.test.annotations.ProblemsType;

/**
 * 
 * @author jjankovi
 *
 */
public class SolderAnnotationTestBase extends SolderTestBase {
	
	protected String APPLICATION_CLASS = "Application.java";
	
	/**
	 * 
	 * @param projectName
	 */
	protected void testNoBeanValidationProblemExists(String projectName) {
		
		testBeanValidationProblemExists(projectName, true);
		
	}
	
	/**
	 * 
	 * @param projectName
	 */
	protected void testMultipleBeansValidationProblemExists(String projectName) {
		
		testBeanValidationProblemExists(projectName, false);
		
	}
	
	/**
	 * 
	 * @param projectName
	 * @param noBeanEligible
	 */
	private void testBeanValidationProblemExists(String projectName, boolean noBeanEligible) {
		
		SWTBotTreeItem[] validationProblems = quickFixHelper.getProblems(
				ProblemsType.WARNINGS, projectName);
		assertTrue(validationProblems.length > 0);
		assertTrue(validationProblems.length == 1);
		assertContains(noBeanEligible?CDIConstants.NO_BEAN_IS_ELIGIBLE:
			CDIConstants.MULTIPLE_BEANS, validationProblems[0].getText());
	
	}
	
	/**
	 * 
	 * @param projectName
	 * @param openOnString
	 * @param openedClass
	 * @param producer
	 * @param producerMethod
	 */
	protected void testProperInjectBean(String projectName, 
			String openOnString, String openedClass) {
		
		testProperInject(projectName, openOnString, openedClass, false, null);
		
	}
	
	/**
	 * 
	 * @param projectName
	 * @param openOnString
	 * @param openedClass
	 */
	protected void testProperInjectProducer(String projectName, 
			String openOnString, String openedClass, 
			String producerMethod) {
		
		testProperInject(projectName, openOnString, openedClass, true, producerMethod);
		
	}
	
	/**
	 * 
	 * @param projectName
	 * @param openOnString
	 * @param openedClass
	 * @param producer
	 * @param producerMethod
	 */
	private void testProperInject(String projectName, String openOnString, String openedClass, 
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
