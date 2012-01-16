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

import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.jboss.tools.cdi.bot.test.annotations.ValidationType;
import org.jboss.tools.cdi.bot.test.quickfix.injection.QualifierOperation;
import org.jboss.tools.cdi.bot.test.quickfix.validators.BeanValidationProvider;
import org.jboss.tools.cdi.bot.test.quickfix.validators.IValidationProvider;
import org.jboss.tools.cdi.bot.test.uiutils.wizards.QuickFixDialogWizard;
import org.jboss.tools.cdi.bot.test.uiutils.wizards.SpecifyBeanDialogWizard;
import org.jboss.tools.ui.bot.ext.Timing;

public class EligibleInjectionQuickFixTestBase extends QuickFixTestBase{
	
	private static IValidationProvider validationProvider = new BeanValidationProvider();
	
	public IValidationProvider validationProvider() {
		return validationProvider;
	}
	
	/**
	 * Method resolves multiple bean injection problem. By setting class which
	 * should be more qualified and qualifier name it resolves this problem.
	 * If qualifier doesn't exist, by using qualifier wizard it creates the new
	 * one and uses it to resolve problem
	 * @param classToQualify
	 * @param qualifier
	 */
	public void resolveMultipleBeans(ValidationType validationType, String classToQualify, 
			String qualifier, QualifierOperation operation) {
		
		SWTBotTreeItem validationProblem = getProblem(validationType);		
		assertNotNull(validationProblem);
		
		quickFixHelper.openQuickFix(validationProblem);
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
			boolean qualifFound = false;
			for (String availQualifer : spBeanDialogWizard.getAvailableQualifiers()) {
				if (availQualifer.equals(qualifier + " - " + getPackageName())) {
					qualifFound = true;
					spBeanDialogWizard.addQualifier(availQualifer);
				}
			}
			// there was no such qualifer, it has to be created, after creation it 
			// has to be added to in Bean qualifiers
			if (!qualifFound) {
				spBeanDialogWizard.createNewQualifier(qualifier, getPackageName()).
				setName(qualifier).finish();
				bot.sleep(Timing.time2S());
				for (String availQualifer : spBeanDialogWizard.getAvailableQualifiers()) {
					if (availQualifer.equals(qualifier + " - " + getPackageName())) {						
						spBeanDialogWizard.addQualifier(availQualifer);
					}
				}
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
