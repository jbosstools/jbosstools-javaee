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
import org.jboss.tools.cdi.bot.test.CDITestBase;
import org.jboss.tools.cdi.bot.test.annotations.CDIWizardType;
import org.jboss.tools.cdi.bot.test.annotations.ValidationType;
import org.jboss.tools.cdi.bot.test.quickfix.validators.BeansXmlValidationProvider;
import org.jboss.tools.cdi.bot.test.quickfix.validators.IValidationProvider;
import org.jboss.tools.cdi.bot.test.uiutils.wizards.CDIWizardBase;
import org.jboss.tools.cdi.bot.test.uiutils.wizards.QuickFixDialogWizard;
import org.jboss.tools.ui.bot.ext.view.ProblemsView;

public class BeansXMLQuickFixTestBase extends CDITestBase {

	private IValidationProvider validationProvider = new BeansXmlValidationProvider();
	
	public IValidationProvider getValidationProvider() {
		return validationProvider;
	}
	
	/**
	 * Method checks if there is no beans.xml validation error
	 * @return
	 */
	public boolean isBeanXMLValidationErrorEmpty() {
		return ProblemsView.getFilteredErrorsTreeItems(bot, null, "/" + getProjectName(), 
				"beans.xml", "CDI Problem").length == 0;
	}
	
	/**
	 * Method resolves validation error where there is no such Alternative as 
	 * configured in beans.xml. It opens quick fix and through finishWithWait button
	 * the Bean Wizard dialog is opened where both parameters are used to create
	 * the new alternative bean
	 * @param name
	 * @param pkg
	 */
	public void resolveAddNewAlternative(String name, String pkg) {
		
		openBeanXMLValidationProblem(ValidationType.NO_CLASS, getProjectName());
		CDIWizardBase cdiWizardBase = new CDIWizardBase(CDIWizardType.BEAN);
		if (cdiWizardBase.isAlternative() && cdiWizardBase.canFinish()) {
			cdiWizardBase.setName(name).setPackage(pkg).finishWithWait();
		}else {
			fail("Dialog can't be finishWithWaited");
		}
		
	}
	
	/**
	 * Method resolves validation error where there is no such Stereotype as 
	 * configured in beans.xml. It opens quick fix and through finishWithWait button
	 * the Stereotype Wizard dialog is opened where both parameters are used to create
	 * the new stereotype annotation
	 * @param name
	 * @param pkg
	 */
	public void resolveAddNewStereotype(String name, String pkg) {
		
		openBeanXMLValidationProblem(ValidationType.NO_ANNOTATION, getProjectName());
		CDIWizardBase cdiWizardBase = new CDIWizardBase(CDIWizardType.STEREOTYPE);
		if (cdiWizardBase.isAlternative() && cdiWizardBase.canFinish()) {
			cdiWizardBase.setName(name).setPackage(pkg).finishWithWait();
		}else {
			fail("Dialog can't be finishWithWaited");
		}
		
	}
	
	/**
	 * Method resolves validation error where there is no such decorator as 
	 * configured in beans.xml. It opens quick fix and through finishWithWait button
	 * the Decorator Wizard dialog is opened where both parameters are used to create
	 * the new decorator. Interface "java.util.List" is automatically used. 
	 * @param name
	 * @param pkg
	 */
	public void resolveAddNewDecorator(String name, String pkg) {
		
		openBeanXMLValidationProblem(ValidationType.NO_CLASS, getProjectName());
		CDIWizardBase cdiWizardBase = new CDIWizardBase(CDIWizardType.DECORATOR);		
		cdiWizardBase.addInterface("java.util.List");
		if (cdiWizardBase.canFinish()) {
			cdiWizardBase.setName(name).setPackage(pkg).finishWithWait();
		} else {
			fail("Dialog can't be finishWithWaited");
		}
		
	}
	
	/**
	 * Method resolves validation error where there is no such Interceptor as 
	 * configured in beans.xml. It opens quick fix and through finishWithWait button
	 * the Interceptor Wizard dialog is opened where both parameters are used to create
	 * the new Interceptor
	 * @param name
	 * @param pkg
	 */
	public void resolveAddNewInterceptor(String name, String pkg) {
		
		openBeanXMLValidationProblem(ValidationType.NO_CLASS, getProjectName());
		CDIWizardBase cdiWizardBase = new CDIWizardBase(CDIWizardType.INTERCEPTOR);
		if (cdiWizardBase.canFinish()) {
			cdiWizardBase.setName(name).setPackage(pkg).finishWithWait();
		}else {
			fail("Dialog can't be finishWithWaited");
		}
		
	}
	
	/**
	 * Method corrects Bean which has no @Alternative annotation in it 
	 * by adding these parameter.
	 * @param name
	 * @param pkg
	 */
	public void resolveAddAlternativeToBean(String name) {
		
		openBeanXMLValidationProblem(ValidationType.NO_ALTERNATIVE, getProjectName());
		String content = bot.editorByTitle(name + ".java").toTextEditor().getText();
		assertTrue(content.contains("@Alternative"));
		
	}
	
	/**
	 * Method corrects Stereotype which has no @Alternative annotation in it 
	 * by adding these parameter.
	 * @param name
	 * @param pkg
	 */
	public void resolveAddAlternativeToStereotype(String name) {
		
		openBeanXMLValidationProblem(ValidationType.NO_ALTERNATIVE_STEREOTYPE, getProjectName());
		String content = bot.editorByTitle(name + ".java").toTextEditor().getText();
		assertTrue(content.contains("@Alternative"));
		
	}
	
	/**
	 * Method firstly gets beans.xml validation problem. Then
	 * it opens quick fix wizard, selects default value and
	 * press finishWithWait button
	 */
	private void openBeanXMLValidationProblem(ValidationType validationProblemType, String projectName) {
		
		SWTBotTreeItem validationProblem = quickFixHelper.getProblem(validationProblemType, 
				projectName, validationProvider);		
		assertNotNull(validationProblem);
		
		quickFixHelper.openQuickFix(validationProblem);	
		QuickFixDialogWizard qfWizard = new QuickFixDialogWizard();
		qfWizard.setFix(qfWizard.getDefaultCDIQuickFix());
		qfWizard.setResource(qfWizard.getResources().get(0));
		qfWizard.finishWithWait();
	}
	
}
