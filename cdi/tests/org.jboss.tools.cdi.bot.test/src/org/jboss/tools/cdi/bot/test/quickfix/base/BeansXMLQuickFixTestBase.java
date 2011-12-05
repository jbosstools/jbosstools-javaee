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

import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.jboss.tools.cdi.bot.test.annotations.CDIAnnotationsType;
import org.jboss.tools.cdi.bot.test.annotations.CDIWizardType;
import org.jboss.tools.cdi.bot.test.uiutils.wizards.CDIWizardBase;
import org.jboss.tools.cdi.bot.test.uiutils.wizards.QuickFixDialogWizard;

public class BeansXMLQuickFixTestBase extends QuickFixTestBase {

	private void openBeanXMLValidationProblem() {
		
		SWTBotTreeItem validationProblem = getProblem(CDIAnnotationsType.INJECT,
				CDIWizardType.BEANS_XML);		

		openQuickFix(validationProblem);	
		QuickFixDialogWizard qfWizard = new QuickFixDialogWizard();
		qfWizard.setFix(qfWizard.getAvailableFixes().get(0));
		qfWizard.setResource(qfWizard.getResources().get(0));
		qfWizard.finish();
	}
	
	public boolean isBeanXMLValidationProblemsEmpty() {
		return getProblem(CDIAnnotationsType.INJECT, CDIWizardType.BEANS_XML) == null;
	}
	
	public void resolveAddNewAlternative(String name, String pkg) {
		
		openBeanXMLValidationProblem();
		CDIWizardBase cdiWizardBase = new CDIWizardBase(CDIWizardType.BEAN);
		if (cdiWizardBase.isAlternative() && cdiWizardBase.canFinish()) {
			cdiWizardBase.setName(name).setPackage(pkg).finish();
		}else {
			fail("Dialog can't be finished");
		}
		
	}
	
	public void resolveAddNewStereotype(String name, String pkg) {
		
		openBeanXMLValidationProblem();
		CDIWizardBase cdiWizardBase = new CDIWizardBase(CDIWizardType.STEREOTYPE);
		if (cdiWizardBase.isAlternative() && cdiWizardBase.canFinish()) {
			cdiWizardBase.setName(name).setPackage(pkg).finish();
		}else {
			fail("Dialog can't be finished");
		}
		
	}
	
	public void resolveAddNewDecorator(String name, String pkg) {
		
		openBeanXMLValidationProblem();
		CDIWizardBase cdiWizardBase = new CDIWizardBase(CDIWizardType.DECORATOR);		
		cdiWizardBase.addInterface("java.util.List");
		if (cdiWizardBase.canFinish()) {
			cdiWizardBase.setName(name).setPackage(pkg).finish();
		} else {
			fail("Dialog can't be finished");
		}
		
	}
	
	public void resolveAddNewInterceptor(String name, String pkg) {
		
		openBeanXMLValidationProblem();
		CDIWizardBase cdiWizardBase = new CDIWizardBase(CDIWizardType.INTERCEPTOR);
		if (cdiWizardBase.canFinish()) {
			cdiWizardBase.setName(name).setPackage(pkg).finish();
		}else {
			fail("Dialog can't be finished");
		}
		
	}
	
	public void resolveAddAlternativeToExistingComponent(String name, String pkg) {
		
		openBeanXMLValidationProblem();
		String content = bot.editorByTitle(name + ".java").toTextEditor().getText();
		assertTrue(content.contains("@Alternative"));
		
	}
	
}
