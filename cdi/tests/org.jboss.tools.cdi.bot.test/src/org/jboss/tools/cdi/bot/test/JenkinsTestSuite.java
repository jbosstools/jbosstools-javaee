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
package org.jboss.tools.cdi.bot.test;

import org.jboss.tools.cdi.bot.test.editor.BeansEditorTest;
import org.jboss.tools.cdi.bot.test.wizard.CDIWebProjectWizardTest;
import org.jboss.tools.cdi.bot.test.wizard.ConfigurationPresetTest;
import org.jboss.tools.cdi.bot.test.wizard.FacetTest;
import org.jboss.tools.cdi.bot.test.wizard.WizardTest;
import org.jboss.tools.ui.bot.ext.RequirementAwareSuite;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Suite of tests executed on jenkins slave
 * @author Jaroslav Jankovic
 */
@RunWith(RequirementAwareSuite.class)
@SuiteClasses({
	ConfigurationPresetTest.class,
	FacetTest.class, 
	CDIWebProjectWizardTest.class,
	WizardTest.class,
	BeansEditorTest.class,
	
	/** Not stable yet
	
	NamedRefactoringTest.class,
	NamedComponentsSearchingTest.class,
	BeansXMLValidationTest.class,			
	BeansXMLCompletionTest.class,	
	BeansXMLValidationQuickFixTest.class,
//	AsYouTypeValidationTest.class, // not implemented yet
	DecoratorFromWebBeanTest.class,
	ProblemEligibleInjectionTest.class,
	AllAssignableDialogTest.class,
	AssignableDialogFilterTest.class,
	QuickFixProposalsDescriptionTest.class,
	StereotypeValidationQuickFixTest.class,
	QualifierValidationQuickFixTest.class,
	ScopeValidationQuickFixTest.class,
	BeanValidationQuickFixTest.class,
	InterceptorValidationQuickFixTest.class,
	DecoratorValidationQuickFixTest.class,
	IBindingValidationQuickFixTest.class,
	OpenOnTest.class,
	FindObserverForEventTest.class
	
	**/
})
public class JenkinsTestSuite {
		
}
