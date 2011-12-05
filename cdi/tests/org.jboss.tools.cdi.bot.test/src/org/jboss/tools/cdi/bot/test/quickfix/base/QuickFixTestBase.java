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

package org.jboss.tools.cdi.bot.test.quickfix.base;


import java.util.ArrayList;

import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.jboss.tools.cdi.bot.test.annotations.CDIAnnotationsType;
import org.jboss.tools.cdi.bot.test.annotations.CDIWizardType;
import org.jboss.tools.cdi.bot.test.annotations.ProblemsType;
import org.jboss.tools.cdi.bot.test.quickfix.injection.QualifierOperation;
import org.jboss.tools.cdi.bot.test.quickfix.validators.BeanValidationProvider;
import org.jboss.tools.cdi.bot.test.quickfix.validators.DecoratorValidationProvider;
import org.jboss.tools.cdi.bot.test.quickfix.validators.InterceptorBindingValidationProvider;
import org.jboss.tools.cdi.bot.test.quickfix.validators.InterceptorValidationProvider;
import org.jboss.tools.cdi.bot.test.quickfix.validators.QualifierValidationProvider;
import org.jboss.tools.cdi.bot.test.quickfix.validators.ScopeValidationProvider;
import org.jboss.tools.cdi.bot.test.quickfix.validators.StereotypeValidationProvider;
import org.jboss.tools.cdi.bot.test.uiutils.QuickFixHelper;
import org.jboss.tools.cdi.bot.test.uiutils.wizards.QuickFixDialogWizard;
import org.jboss.tools.cdi.bot.test.uiutils.wizards.SpecifyBeanDialogWizard;
import org.jboss.tools.ui.bot.ext.Timing;
import org.junit.BeforeClass;

/**
 * Test base for all QuickFix-like tests
 * 
 * @author Jaroslav Jankovic
 */

public class QuickFixTestBase extends QuickFixHelper {
	
	@Override
	public void checkAndCreateProject() {
		if (!projectHelper.projectExists(getProjectName())) {
			projectHelper.createCDIProject(getProjectName());
		}
	}		
	
	@BeforeClass
	public static void setup() {
		problems.show();
	}
	

	/**
	 * checkQuickFix is the most important method in this class. It
	 * gets validation error prior to component type and annotation type,
	 * then it resolve validation error through quick fix
	 * wizard and finally check if validation errors was fixed through
	 * this wizard
	 * @param annonType
	 * @param compType
	 */
	public void checkQuickFix(CDIAnnotationsType annonType, CDIWizardType compType) {
		SWTBotTreeItem validationProblem = getProblem(annonType, compType);		
		assertNotNull(validationProblem);
		resolveQuickFix(validationProblem);
		validationProblem = getProblem(annonType, compType);		
		assertNull(validationProblem);
	}
	
	/**
	 * Methods gets the particular validation problem located in Problems View by
	 * using specific ValidationErrorsProvider
	 * @param annonType
	 * @param compType
	 * @return
	 */
	private SWTBotTreeItem getProblem(CDIAnnotationsType annonType, CDIWizardType compType) {		
		switch (compType) {
		case STEREOTYPE:
			validationErrorsProvider = new StereotypeValidationProvider();
			break;
		case QUALIFIER:
			validationErrorsProvider = new QualifierValidationProvider();
			break;
		case SCOPE:
			validationErrorsProvider = new ScopeValidationProvider();
			break;
		case BEAN:
			validationErrorsProvider = new BeanValidationProvider();
			break;
		case INTERCEPTOR:
			validationErrorsProvider = new InterceptorValidationProvider();
			break;
		case DECORATOR:
			validationErrorsProvider = new DecoratorValidationProvider();
			break;
		case INTERCEPTOR_BINDING:
			validationErrorsProvider = new InterceptorBindingValidationProvider();
			break;
		}
		ArrayList<String> validationProblems = null;
		SWTBotTreeItem[] problemsInProblemsView = null;
		if (validationErrorsProvider.getAllWarningsAnnotation().contains(annonType)) {
			validationProblems = validationErrorsProvider.getAllWarningForAnnotationType(annonType);
			problemsInProblemsView = getProblems(ProblemsType.WARNINGS);
		} else {
			validationProblems = validationErrorsProvider.getAllErrorsForAnnotationType(annonType);
			problemsInProblemsView = getProblems(ProblemsType.ERRORS);
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
		openQuickFix(ti);
		
		QuickFixDialogWizard qfWizard = new QuickFixDialogWizard();
		
		String firstFix = qfWizard.getAvailableFixes().get(0);				
		String firstResource = qfWizard.getResources().get(0);
		
		qfWizard.setFix(firstFix).setResource(firstResource).finish();
		
		util.waitForNonIgnoredJobs();
	}

	/**
	 * Method resolves multiple bean injection problem. By setting class which
	 * should be more qualified and qualifier name it resolves this problem.
	 * If qualifier doesn't exist, by using qualifier wizard it creates the new
	 * one and uses it to resolve problem
	 * @param classToQualify
	 * @param qualifier
	 */
	public void resolveMultipleBeans(String classToQualify, String qualifier, 
			QualifierOperation operation) {
		
		SWTBotTreeItem validationProblem = getProblem(CDIAnnotationsType.INJECT,
				CDIWizardType.BEAN);		

		openQuickFix(validationProblem);
		QuickFixDialogWizard quickFixWizard = new QuickFixDialogWizard();
		for (String availableFix : quickFixWizard.getAvailableFixes()) {
			if (availableFix.contains(classToQualify)) {
				quickFixWizard.setFix(availableFix).
				setResource(quickFixWizard.getResources().get(0)).
				finish();
			}
		}
	
		SpecifyBeanDialogWizard spBeanDialogWizard = new SpecifyBeanDialogWizard();
		if (operation == QualifierOperation.ADD) {
			for (String availQualifer : spBeanDialogWizard.getAvailableQualifiers()) {
				if (availQualifer.equals(qualifier + " - " + getPackageName())) {
					spBeanDialogWizard.addQualifier(availQualifer);
				}
			}
			// there was no such qualifer, it has to be created
			if (!spBeanDialogWizard.canFinish()) {
				spBeanDialogWizard.createNewQualifier(qualifier, getPackageName()).
				setName(qualifier).finish();
			}
		} else {
			for (String inBeanQualifer : spBeanDialogWizard.getInBeanQualifiers()) {
				if (inBeanQualifer.equals(qualifier + " - " + getPackageName())) {
					spBeanDialogWizard.removeQualifier(inBeanQualifer);
				}
			}
		}
		
		spBeanDialogWizard.finish();
		
		bot.sleep(Timing.time1S());
		util.waitForNonIgnoredJobs();
	}
	
}
