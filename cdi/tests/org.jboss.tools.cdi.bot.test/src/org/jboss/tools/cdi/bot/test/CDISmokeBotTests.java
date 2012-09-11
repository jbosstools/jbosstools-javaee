/*******************************************************************************
 * Copyright (c) 2010-2011 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.cdi.bot.test;

import org.jboss.tools.cdi.bot.test.beansxml.BeansXMLValidationTest;
import org.jboss.tools.cdi.bot.test.editor.BeansEditorTest;
import org.jboss.tools.cdi.bot.test.quickfix.injection.ProblemEligibleInjectionTest;
import org.jboss.tools.cdi.bot.test.wizard.CDIWebProjectWizardTest;
import org.jboss.tools.cdi.bot.test.wizard.ConfigurationPresetTest;
import org.jboss.tools.cdi.bot.test.wizard.DynamicWebProjectWithCDITest;
import org.jboss.tools.cdi.bot.test.wizard.FacetTest;
import org.jboss.tools.cdi.bot.test.wizard.WizardTest;
import org.jboss.tools.ui.bot.ext.RequirementAwareSuite;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

/**
*
* @author Lukas Jungmann
* @author Jaroslav Jankovic
*/
@RunWith(RequirementAwareSuite.class)
@SuiteClasses({	
//	PerspectiveTest.class,	 
	ConfigurationPresetTest.class,
	FacetTest.class, 
	CDIWebProjectWizardTest.class,
	DynamicWebProjectWithCDITest.class,
	WizardTest.class,	
	BeansEditorTest.class,
	ProblemEligibleInjectionTest.class,
	BeansXMLValidationTest.class,
	})
public class CDISmokeBotTests extends AbstractTestSuite {
	
}
