/******************************************************************************* 
 * Copyright (c) 2009 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.cdi.core.test;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.jdt.internal.core.JavaModelManager;
import org.jboss.tools.cdi.core.test.ca.BeansXmlCATest;
import org.jboss.tools.cdi.core.test.extension.ExtensionFactoryTest;
import org.jboss.tools.cdi.core.test.extension.ExtensionManagerTest;
import org.jboss.tools.cdi.core.test.extension.ExtensionsInSrsAndUsedProjectTest;
import org.jboss.tools.cdi.core.test.project.EnableCDISupportForJarTest;
import org.jboss.tools.cdi.core.test.project.EnableCDISupportForWarTest;
import org.jboss.tools.cdi.core.test.tck.AssignabilityOfRawAndParameterizedTypesTest;
import org.jboss.tools.cdi.core.test.tck.BeanDefinitionTest;
import org.jboss.tools.cdi.core.test.tck.BeanSpecializationTest;
import org.jboss.tools.cdi.core.test.tck.BuiltInBeanInjectionTest;
import org.jboss.tools.cdi.core.test.tck.DecoratorDefinitionTest;
import org.jboss.tools.cdi.core.test.tck.DefaultNamedTest;
import org.jboss.tools.cdi.core.test.tck.EnterpriseQualifierDefinitionTest;
import org.jboss.tools.cdi.core.test.tck.EnterpriseResolutionByTypeTest;
import org.jboss.tools.cdi.core.test.tck.EnterpriseScopeDefinitionTest;
import org.jboss.tools.cdi.core.test.tck.EnterpriseStereotypeDefinitionTest;
import org.jboss.tools.cdi.core.test.tck.InjectionPointTest;
import org.jboss.tools.cdi.core.test.tck.InterceptorDefinitionTest;
import org.jboss.tools.cdi.core.test.tck.NameDefinitionTest;
import org.jboss.tools.cdi.core.test.tck.ObserverMethodResolutionTest;
import org.jboss.tools.cdi.core.test.tck.ProducerMethodDefinitionTest;
import org.jboss.tools.cdi.core.test.tck.QualifierDefinitionTest;
import org.jboss.tools.cdi.core.test.tck.QualifierWithMembersTest;
import org.jboss.tools.cdi.core.test.tck.ScopeDefinitionTest;
import org.jboss.tools.cdi.core.test.tck.SelectedAlternativeTest;
import org.jboss.tools.cdi.core.test.tck.StereotypeDefinitionTest;
import org.jboss.tools.cdi.core.test.tck.StereotypeInheritenceTest;
import org.jboss.tools.cdi.core.test.tck.lookup.AmbiguousDependencyTest;
import org.jboss.tools.cdi.core.test.tck.lookup.CircularDependencyTest;
import org.jboss.tools.cdi.core.test.tck.lookup.DynamicLookupTest;
import org.jboss.tools.cdi.core.test.tck.lookup.ResolutionByNameTest;
import org.jboss.tools.cdi.core.test.tck.lookup.ResolutionByTypeTest;
import org.jboss.tools.cdi.core.test.tck.lookup.UnsatisfiedDependencyTest;
import org.jboss.tools.cdi.core.test.tck.validation.AnnotationsValidationTest;
import org.jboss.tools.cdi.core.test.tck.validation.BeansXmlValidationTest;
import org.jboss.tools.cdi.core.test.tck.validation.BuilderOrderValidationTest;
import org.jboss.tools.cdi.core.test.tck.validation.CoreValidationTest;
import org.jboss.tools.cdi.core.test.tck.validation.DefenitionErrorsValidationTest;
import org.jboss.tools.cdi.core.test.tck.validation.DependentProjectValidationTest;
import org.jboss.tools.cdi.core.test.tck.validation.DeploymentProblemsValidationTests;
import org.jboss.tools.cdi.core.test.tck.validation.DisableCDISupportTest;
import org.jboss.tools.cdi.core.test.tck.validation.ELValidationTest;
import org.jboss.tools.cdi.core.test.tck.validation.IncrementalValidationTest;
import org.jboss.tools.cdi.core.test.tck.validation.ValidationExceptionTest;
import org.jboss.tools.test.util.ProjectImportTestSetup;

/**
 * @author Alexey Kazakov
 */
public class CDICoreAllTests {

	public static Test suite() {
		// it could be done here because it is not needed to be enabled back
		JavaModelManager.getIndexManager().disable();

		ValidationExceptionTest.initLogger();

		TestSuite suiteAll = new TestSuite("CDI Core Tests");
		suiteAll.addTestSuite(TypeTest.class);
		TestSuite suite = new TestSuite("TCK Tests");
		suite.addTestSuite(ResolutionByNameTest.class);
		suite.addTestSuite(BeanDefinitionTest.class);
		suite.addTestSuite(NameDefinitionTest.class);
		suite.addTestSuite(QualifierDefinitionTest.class);
		suite.addTestSuite(EnterpriseQualifierDefinitionTest.class);
		suite.addTestSuite(ScopeDefinitionTest.class);
		suite.addTestSuite(EnterpriseScopeDefinitionTest.class);
		suite.addTestSuite(StereotypeDefinitionTest.class);
		suite.addTestSuite(DefaultNamedTest.class);
		suite.addTestSuite(EnterpriseStereotypeDefinitionTest.class);
		suite.addTestSuite(StereotypeInheritenceTest.class);
		suite.addTestSuite(ProducerMethodDefinitionTest.class);
		suite.addTestSuite(InjectionPointTest.class);
		suite.addTestSuite(BeanSpecializationTest.class);
		suite.addTestSuite(ResolutionByTypeTest.class);
		suite.addTestSuite(EnterpriseResolutionByTypeTest.class);
		suite.addTestSuite(AssignabilityOfRawAndParameterizedTypesTest.class);
		suite.addTestSuite(QualifierWithMembersTest.class);
		suite.addTestSuite(InterceptorDefinitionTest.class);
		suite.addTestSuite(DecoratorDefinitionTest.class);
		suite.addTestSuite(ObserverMethodResolutionTest.class);
		suite.addTestSuite(BuiltInBeanInjectionTest.class);
		suite.addTestSuite(BeansXmlCATest.class);
		suite.addTestSuite(SelectedAlternativeTest.class);
		suite.addTestSuite(CircularDependencyTest.class);
		suite.addTestSuite(DynamicLookupTest.class);
		suite.addTestSuite(AmbiguousDependencyTest.class);
		suite.addTestSuite(UnsatisfiedDependencyTest.class);

		// Validation tests
		suite.addTestSuite(DefenitionErrorsValidationTest.class);
		suite.addTestSuite(DeploymentProblemsValidationTests.class);
		suite.addTestSuite(BeansXmlValidationTest.class);
		suite.addTestSuite(AnnotationsValidationTest.class);
		suite.addTestSuite(CoreValidationTest.class);
		suite.addTestSuite(ELValidationTest.class);
		suite.addTestSuite(IncrementalValidationTest.class);

		suiteAll.addTestSuite(TwoWebContentFoldersTest.class);
		suiteAll.addTestSuite(RemoveJarFromClasspathTest.class);
		suiteAll.addTestSuite(ExtensionFactoryTest.class);
		suiteAll.addTestSuite(ExtensionManagerTest.class);
		suiteAll.addTestSuite(BeansXMLTest.class);
		TestSuite dependentSuite = new TestSuite("Dependent Projects Tests");
		dependentSuite.addTestSuite(DependentProjectTest.class);
		dependentSuite.addTestSuite(ExtensionsInSrsAndUsedProjectTest.class);
		DependentProjectsTestSetup dependent = new DependentProjectsTestSetup(dependentSuite);
		suiteAll.addTest(dependent);
		suiteAll.addTestSuite(EnableCDISupportForWarTest.class);
		suiteAll.addTestSuite(EnableCDISupportForJarTest.class);
		suiteAll.addTestSuite(DependentProjectValidationTest.class);
		suiteAll.addTest(new CDICoreTestSetup(suite));
		
		suite = new TestSuite(BuilderOrderValidationTest.class.getName());
		suite.addTestSuite(BuilderOrderValidationTest.class);
		ProjectImportTestSetup testSetup = new ProjectImportTestSetup(suite,
				"org.jboss.tools.cdi.core.test",
				new String[]{"projects/CDITestBrokenBuilderOrder"},
				new String[]{"CDITestBrokenBuilderOrder"});
		suiteAll.addTest(testSetup);

		suiteAll.addTestSuite(DisableCDISupportTest.class);

		suiteAll.addTestSuite(ValidationExceptionTest.class); // This test should be added last!
		return suiteAll;
	}
}