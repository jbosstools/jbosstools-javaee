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

public class SolderAnnotationTestBase extends SolderTestBase {
	
	protected String APPLICATION_CLASS = "Application.java";
	
	protected void testAnnotationImproperValue(String projectName, boolean noBeanEligible) {
		
		SWTBotTreeItem[] validationProblems = quickFixHelper.getProblems(
				ProblemsType.WARNINGS, projectName);
		assertTrue(validationProblems.length > 0);
		assertTrue(validationProblems.length == 1);
		assertContains(noBeanEligible?CDIConstants.NO_BEAN_IS_ELIGIBLE:
			CDIConstants.MULTIPLE_BEANS, validationProblems[0].getText());
		
	}
	
	protected void testAnnotationProperValue(String projectName, String openOnString, String openedClass, 
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
