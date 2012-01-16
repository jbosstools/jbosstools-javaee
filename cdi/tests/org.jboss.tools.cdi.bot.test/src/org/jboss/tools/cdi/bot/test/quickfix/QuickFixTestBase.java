/*******************************************************************************
 * Copyright (c) 2010 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.jboss.tools.cdi.bot.test.quickfix;


import java.util.List;

import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.jboss.tools.cdi.bot.test.CDITestBase;
import org.jboss.tools.cdi.bot.test.annotations.ProblemsType;
import org.jboss.tools.cdi.bot.test.annotations.ValidationType;
import org.jboss.tools.cdi.bot.test.quickfix.validators.IValidationProvider;
import org.jboss.tools.cdi.bot.test.uiutils.wizards.QuickFixDialogWizard;
import org.junit.BeforeClass;

/**
 * Test base for all QuickFix-like tests
 * 
 * @author Jaroslav Jankovic
 */

public abstract class QuickFixTestBase extends CDITestBase {
	
	@BeforeClass
	public static void setup() {
		problems.show();		
	}
	
	protected abstract IValidationProvider validationProvider();
	
	/**
	 * checkQuickFix is the most important method in this class. It
	 * gets validation error prior to component type and annotation type,
	 * then it resolve validation error through quick fix
	 * wizard and finally check if validation errors was fixed through
	 * this wizard
	 * @param validationType
	 * @param compType
	 */
	public void checkQuickFix(ValidationType validationType) {
		SWTBotTreeItem validationProblem = getProblem(validationType);		
		assertNotNull(validationProblem);
		resolveQuickFix(validationProblem);
		validationProblem = getProblem(validationType);		
		assertNull(validationProblem);
	}
	
	/**
	 * Methods gets the particular validation problem located in Problems View by
	 * using specific ValidationErrorsProvider
	 * @param validationType
	 * @param compType
	 * @return
	 */
	protected SWTBotTreeItem getProblem(ValidationType validationType) {		
		IValidationProvider validationErrorsProvider = validationProvider();
		List<String> validationProblems = null;
		SWTBotTreeItem[] problemsInProblemsView = null;
		if (validationErrorsProvider.getAllWarningsAnnotation().contains(validationType)) {
			validationProblems = validationErrorsProvider.getAllWarningForAnnotationType(validationType);
			problemsInProblemsView = quickFixHelper.getProblems(ProblemsType.WARNINGS, getProjectName());
		} else {
			validationProblems = validationErrorsProvider.getAllErrorsForAnnotationType(validationType);
			problemsInProblemsView = quickFixHelper.getProblems(ProblemsType.ERRORS, getProjectName());
		}
		for (SWTBotTreeItem ti: problemsInProblemsView) {
			for (String validationProblem: validationProblems) {					
				if (ti.getText().contains(validationProblem)) {										
					return ti;
				}
			}
		}
		return null;
	}
	
	/**
	 * Method resolves particular validation problem (parameter ti).
	 * It simply open context menu for param "ti", open menu "Quick Fix" and
	 * chooses first option and confirms it (resolve it)
	 * @param ti
	 */
	private void resolveQuickFix(SWTBotTreeItem ti) {
		quickFixHelper.openQuickFix(ti);
		
		QuickFixDialogWizard qfWizard = new QuickFixDialogWizard();
		
		String firstFix = qfWizard.getAvailableFixes().get(0);				
		String firstResource = qfWizard.getResources().get(0);
		
		qfWizard.setFix(firstFix).setResource(firstResource).finish();
		
		util.waitForNonIgnoredJobs();
	}

}
