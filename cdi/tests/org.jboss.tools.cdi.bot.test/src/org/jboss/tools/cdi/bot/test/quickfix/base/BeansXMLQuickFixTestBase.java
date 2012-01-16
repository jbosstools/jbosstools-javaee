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

	/**
	 * Method firstly gets beans.xml validation problem. Then
	 * it opens quick fix wizard, selects default value and
	 * press finish button
	 */
	private void openBeanXMLValidationProblem() {
		
		SWTBotTreeItem validationProblem = getProblem(CDIAnnotationsType.INJECT,
				CDIWizardType.BEANS_XML);		

		quickFixHelper.openQuickFix(validationProblem);	
		QuickFixDialogWizard qfWizard = new QuickFixDialogWizard();
		qfWizard.setFix(qfWizard.getAvailableFixes().get(0));
		qfWizard.setResource(qfWizard.getResources().get(0));
		qfWizard.finish();
	}
	
	/**
	 * Method checks if there is no beans.xml validation error
	 * @return
	 */
	public boolean isBeanXMLValidationProblemsEmpty() {
		return getProblem(CDIAnnotationsType.INJECT, CDIWizardType.BEANS_XML) == null;
	}
	
	/**
	 * Method resolves validation error where there is no such Alternative as 
	 * configured in beans.xml. It opens quick fix and through finish button
	 * the Bean Wizard dialog is opened where both parameters are used to create
	 * the new alternative bean
	 * @param name
	 * @param pkg
	 */
	public void resolveAddNewAlternative(String name, String pkg) {
		
		openBeanXMLValidationProblem();
		CDIWizardBase cdiWizardBase = new CDIWizardBase(CDIWizardType.BEAN);
		if (cdiWizardBase.isAlternative() && cdiWizardBase.canFinish()) {
			cdiWizardBase.setName(name).setPackage(pkg).finish();
		}else {
			fail("Dialog can't be finished");
		}
		
	}
	
	/**
	 * Method resolves validation error where there is no such Stereotype as 
	 * configured in beans.xml. It opens quick fix and through finish button
	 * the Stereotype Wizard dialog is opened where both parameters are used to create
	 * the new stereotype annotation
	 * @param name
	 * @param pkg
	 */
	public void resolveAddNewStereotype(String name, String pkg) {
		
		openBeanXMLValidationProblem();
		CDIWizardBase cdiWizardBase = new CDIWizardBase(CDIWizardType.STEREOTYPE);
		if (cdiWizardBase.isAlternative() && cdiWizardBase.canFinish()) {
			cdiWizardBase.setName(name).setPackage(pkg).finish();
		}else {
			fail("Dialog can't be finished");
		}
		
	}
	
	/**
	 * Method resolves validation error where there is no such decorator as 
	 * configured in beans.xml. It opens quick fix and through finish button
	 * the Decorator Wizard dialog is opened where both parameters are used to create
	 * the new decorator. Interface "java.util.List" is automatically used. 
	 * @param name
	 * @param pkg
	 */
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
	
	/**
	 * Method resolves validation error where there is no such Interceptor as 
	 * configured in beans.xml. It opens quick fix and through finish button
	 * the Interceptor Wizard dialog is opened where both parameters are used to create
	 * the new Interceptor
	 * @param name
	 * @param pkg
	 */
	public void resolveAddNewInterceptor(String name, String pkg) {
		
		openBeanXMLValidationProblem();
		CDIWizardBase cdiWizardBase = new CDIWizardBase(CDIWizardType.INTERCEPTOR);
		if (cdiWizardBase.canFinish()) {
			cdiWizardBase.setName(name).setPackage(pkg).finish();
		}else {
			fail("Dialog can't be finished");
		}
		
	}
	
	/**
	 * Method corrects Bean or Stereotype which has no @Alternative annotation in it 
	 * by adding these parameter.
	 * @param name
	 * @param pkg
	 */
	public void resolveAddAlternativeToExistingComponent(String name) {
		
		openBeanXMLValidationProblem();
		String content = bot.editorByTitle(name + ".java").toTextEditor().getText();
		assertTrue(content.contains("@Alternative"));
		
	}
	
}
