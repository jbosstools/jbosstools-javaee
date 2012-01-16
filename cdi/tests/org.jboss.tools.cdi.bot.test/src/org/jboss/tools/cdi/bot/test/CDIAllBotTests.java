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

import org.jboss.tools.cdi.bot.test.beansxml.BeansXMLCompletionTest;
import org.jboss.tools.cdi.bot.test.beansxml.BeansXMLValidationTest;
import org.jboss.tools.cdi.bot.test.editor.BeansEditorTest;
import org.jboss.tools.cdi.bot.test.named.NamedComponentsSearchingTest;
import org.jboss.tools.cdi.bot.test.named.NamedRefactoringTest;
import org.jboss.tools.cdi.bot.test.openon.FindObserverForEventTest;
import org.jboss.tools.cdi.bot.test.openon.OpenOnTest;
import org.jboss.tools.cdi.bot.test.quickfix.injection.ProblemEligibleInjectionTest;
import org.jboss.tools.cdi.bot.test.quickfix.test.BeanValidationQuickFixTest;
import org.jboss.tools.cdi.bot.test.quickfix.test.BeansXMLValidationQuickFixTest;
import org.jboss.tools.cdi.bot.test.quickfix.test.DecoratorValidationQuickFixTest;
import org.jboss.tools.cdi.bot.test.quickfix.test.IBindingValidationQuickFixTest;
import org.jboss.tools.cdi.bot.test.quickfix.test.InterceptorValidationQuickFixTest;
import org.jboss.tools.cdi.bot.test.quickfix.test.QualifierValidationQuickFixTest;
import org.jboss.tools.cdi.bot.test.quickfix.test.ScopeValidationQuickFixTest;
import org.jboss.tools.cdi.bot.test.quickfix.test.StereotypeValidationQuickFixTest;
import org.jboss.tools.cdi.bot.test.seam3.Seam3ConfigValidationTest;
import org.jboss.tools.cdi.bot.test.seam3.Seam3GenericOpenOnTest;
import org.jboss.tools.cdi.bot.test.seam3.Seam3ResourceOpenOnTest;
import org.jboss.tools.cdi.bot.test.wizard.CDISupportTest;
import org.jboss.tools.cdi.bot.test.wizard.ConfigurationPresetTest;
import org.jboss.tools.cdi.bot.test.wizard.FacetTest;
import org.jboss.tools.cdi.bot.test.wizard.WizardTest;
import org.jboss.tools.ui.bot.ext.RequirementAwareSuite;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

/**
 * This test suite requires JBoss AS 6 or newer
 * 
 * System properties:
 *  -Dswtbot.test.properties.file=$PATH
 *  -Dusage_reporting_enabled=$BOOLEAN
 *  
 *  Format of swtbot.properties file:
 *  SERVER=EAP|JBOSS_AS,<server version>,<jre version to run with>|default,<server home>
 *  
 *  Sample swtbot.properties file:
 *
 *  SERVER=JBOSS_AS,6.0,default,/home/jjankovi/Dokumenty/Red_Hat_Stuff/Runtimes/jboss-6.0.0.Final
 *  JAVA=1.6,/space/java/sdk/jdk1.6.0_22
 *  
 *  
 *  Suite duration: aprox. 30min
 * 
 * @author Lukas Jungmann
 * @author Jaroslav Jankovic
 */
@RunWith(RequirementAwareSuite.class)
@SuiteClasses({	
//	PerspectiveTest.class,
	ConfigurationPresetTest.class,
	FacetTest.class, 
	CDISupportTest.class,
	WizardTest.class,
	BeansEditorTest.class,
	NamedRefactoringTest.class,
	NamedComponentsSearchingTest.class,
	BeansXMLValidationTest.class,
	BeansXMLCompletionTest.class,
	ProblemEligibleInjectionTest.class, 
	BeansXMLValidationQuickFixTest.class, 
	StereotypeValidationQuickFixTest.class,
	QualifierValidationQuickFixTest.class,
	ScopeValidationQuickFixTest.class,
	BeanValidationQuickFixTest.class,
	InterceptorValidationQuickFixTest.class,
	DecoratorValidationQuickFixTest.class,
	IBindingValidationQuickFixTest.class,
	OpenOnTest.class,
	FindObserverForEventTest.class, 
	Seam3ResourceOpenOnTest.class,
	Seam3GenericOpenOnTest.class,
	Seam3ConfigValidationTest.class,
})
public class CDIAllBotTests extends AbstractTestSuite {
		
}
