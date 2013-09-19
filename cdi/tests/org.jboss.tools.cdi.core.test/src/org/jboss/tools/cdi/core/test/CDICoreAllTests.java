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
import org.eclipse.wst.validation.ValidationFramework;
import org.jboss.tools.cdi.core.test.ca.BeansXmlCACDI11Test;
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
import org.jboss.tools.cdi.core.test.tck.CDIProjectAsYouTypeTest;
import org.jboss.tools.cdi.core.test.tck.CDIUtilTest;
import org.jboss.tools.cdi.core.test.tck.CoreTest;
import org.jboss.tools.cdi.core.test.tck.DecoratorDefinitionTest;
import org.jboss.tools.cdi.core.test.tck.DefaultNamedTest;
import org.jboss.tools.cdi.core.test.tck.EnterpriseQualifierDefinitionTest;
import org.jboss.tools.cdi.core.test.tck.EnterpriseResolutionByTypeTest;
import org.jboss.tools.cdi.core.test.tck.EnterpriseScopeDefinitionTest;
import org.jboss.tools.cdi.core.test.tck.EnterpriseStereotypeDefinitionTest;
import org.jboss.tools.cdi.core.test.tck.InjectionPointTest;
import org.jboss.tools.cdi.core.test.tck.InjectionPointWithNewQualifierTest;
import org.jboss.tools.cdi.core.test.tck.InterceptorDefinitionTest;
import org.jboss.tools.cdi.core.test.tck.NameDefinitionTest;
import org.jboss.tools.cdi.core.test.tck.NamedBeanRefactoringTest;
import org.jboss.tools.cdi.core.test.tck.ObserverMethodResolutionTest;
import org.jboss.tools.cdi.core.test.tck.ProducerMethodDefinitionTest;
import org.jboss.tools.cdi.core.test.tck.QualifierDefinitionTest;
import org.jboss.tools.cdi.core.test.tck.QualifierWithMembersTest;
import org.jboss.tools.cdi.core.test.tck.ResolvedTypesCacheTest;
import org.jboss.tools.cdi.core.test.tck.ResourceExclusionTest;
import org.jboss.tools.cdi.core.test.tck.ScopeDefinitionTest;
import org.jboss.tools.cdi.core.test.tck.SelectedAlternativeTest;
import org.jboss.tools.cdi.core.test.tck.StereotypeDefinitionTest;
import org.jboss.tools.cdi.core.test.tck.StereotypeInheritenceTest;
import org.jboss.tools.cdi.core.test.tck.WeldExcludeTest;
import org.jboss.tools.cdi.core.test.tck.lookup.AmbiguousDependencyTest;
import org.jboss.tools.cdi.core.test.tck.lookup.CircularDependencyTest;
import org.jboss.tools.cdi.core.test.tck.lookup.DynamicLookupTest;
import org.jboss.tools.cdi.core.test.tck.lookup.PackageInfoTest;
import org.jboss.tools.cdi.core.test.tck.lookup.ResolutionByNameTest;
import org.jboss.tools.cdi.core.test.tck.lookup.ResolutionByTypeTest;
import org.jboss.tools.cdi.core.test.tck.lookup.UnsatisfiedDependencyTest;
import org.jboss.tools.cdi.core.test.tck.validation.AYTAnnotationValidationTest;
import org.jboss.tools.cdi.core.test.tck.validation.AYTBeansXmlValidationTest;
import org.jboss.tools.cdi.core.test.tck.validation.AYTDefenitionErrorsValidationTest;
import org.jboss.tools.cdi.core.test.tck.validation.AYTDeploymentProblemsValidationTests;
import org.jboss.tools.cdi.core.test.tck.validation.AYTSuppressWarningsTests;
import org.jboss.tools.cdi.core.test.tck.validation.AYTWeldValidationTest;
import org.jboss.tools.cdi.core.test.tck.validation.AnnotationsValidationTest;
import org.jboss.tools.cdi.core.test.tck.validation.BeansXmlValidationTest;
import org.jboss.tools.cdi.core.test.tck.validation.BuilderOrderValidationTest;
import org.jboss.tools.cdi.core.test.tck.validation.CoreValidationTest;
import org.jboss.tools.cdi.core.test.tck.validation.DefenitionErrorsValidationTest;
import org.jboss.tools.cdi.core.test.tck.validation.DependentProjectValidationTest;
import org.jboss.tools.cdi.core.test.tck.validation.DeploymentProblemsValidationTests;
import org.jboss.tools.cdi.core.test.tck.validation.DisableCDISupportTest;
import org.jboss.tools.cdi.core.test.tck.validation.ELReferenceTest;
import org.jboss.tools.cdi.core.test.tck.validation.ELValidationTest;
import org.jboss.tools.cdi.core.test.tck.validation.IncrementalValidationTest;
import org.jboss.tools.cdi.core.test.tck.validation.MissingBeansXmlValidationTest;
import org.jboss.tools.cdi.core.test.tck.validation.SuppressWarningsTests;
import org.jboss.tools.cdi.core.test.tck.validation.WeldValidationTest;
import org.jboss.tools.cdi.core.test.tck11.AssignabilityOfRawAndParameterizedTypesCDI11Test;
import org.jboss.tools.cdi.core.test.tck11.BeanDefinitionCDI11Test;
import org.jboss.tools.cdi.core.test.tck11.BeanSpecializationCDI11Test;
import org.jboss.tools.cdi.core.test.tck11.BuiltInBeanInjectionCDI11Test;
import org.jboss.tools.cdi.core.test.tck11.CDIProjectAsYouTypeCDI11Test;
import org.jboss.tools.cdi.core.test.tck11.CDIUtilCDI11Test;
import org.jboss.tools.cdi.core.test.tck11.CoreCDI11Test;
import org.jboss.tools.cdi.core.test.tck11.DecoratorDefinitionCDI11Test;
import org.jboss.tools.cdi.core.test.tck11.DefaultNamedCDI11Test;
import org.jboss.tools.cdi.core.test.tck11.EnterpriseQualifierDefinitionCDI11Test;
import org.jboss.tools.cdi.core.test.tck11.EnterpriseResolutionByTypeCDI11Test;
import org.jboss.tools.cdi.core.test.tck11.EnterpriseScopeDefinitionCDI11Test;
import org.jboss.tools.cdi.core.test.tck11.EnterpriseStereotypeDefinitionCDI11Test;
import org.jboss.tools.cdi.core.test.tck11.InjectionPointCDI11Test;
import org.jboss.tools.cdi.core.test.tck11.InjectionPointWithNewQualifierCDI11Test;
import org.jboss.tools.cdi.core.test.tck11.InterceptorDefinitionCDI11Test;
import org.jboss.tools.cdi.core.test.tck11.NameDefinitionCDI11Test;
import org.jboss.tools.cdi.core.test.tck11.NamedBeanRefactoringCDI11Test;
import org.jboss.tools.cdi.core.test.tck11.ObserverMethodResolutionCDI11Test;
import org.jboss.tools.cdi.core.test.tck11.PriorityCDI11Test;
import org.jboss.tools.cdi.core.test.tck11.ProducerMethodDefinitionCDI11Test;
import org.jboss.tools.cdi.core.test.tck11.QualifierDefinitionCDI11Test;
import org.jboss.tools.cdi.core.test.tck11.QualifierWithMembersCDI11Test;
import org.jboss.tools.cdi.core.test.tck11.ResolvedTypesCacheCDI11Test;
import org.jboss.tools.cdi.core.test.tck11.ResourceExclusionCDI11Test;
import org.jboss.tools.cdi.core.test.tck11.ScopeDefinitionCDI11Test;
import org.jboss.tools.cdi.core.test.tck11.SelectedAlternativeCDI11Test;
import org.jboss.tools.cdi.core.test.tck11.StereotypeDefinitionCDI11Test;
import org.jboss.tools.cdi.core.test.tck11.StereotypeInheritenceCDI11Test;
import org.jboss.tools.cdi.core.test.tck11.VetoedCDI11Test;
import org.jboss.tools.cdi.core.test.tck11.lookup.AmbiguousDependencyCDI11Test;
import org.jboss.tools.cdi.core.test.tck11.lookup.CircularDependencyCDI11Test;
import org.jboss.tools.cdi.core.test.tck11.lookup.DynamicLookupCDI11Test;
import org.jboss.tools.cdi.core.test.tck11.lookup.PackageInfoCDI11Test;
import org.jboss.tools.cdi.core.test.tck11.lookup.ResolutionByNameCDI11Test;
import org.jboss.tools.cdi.core.test.tck11.lookup.ResolutionByTypeCDI11Test;
import org.jboss.tools.cdi.core.test.tck11.lookup.UnsatisfiedDependencyCDI11Test;
import org.jboss.tools.cdi.core.test.tck11.validation.AYTAnnotationValidationCDI11Test;
import org.jboss.tools.cdi.core.test.tck11.validation.AYTBeansXmlValidationCDI11Test;
import org.jboss.tools.cdi.core.test.tck11.validation.AYTDefenitionErrorsValidationCDI11Test;
import org.jboss.tools.cdi.core.test.tck11.validation.AYTDeploymentProblemsValidationCDI11Tests;
import org.jboss.tools.cdi.core.test.tck11.validation.AYTSuppressWarningsCDI11Tests;
import org.jboss.tools.cdi.core.test.tck11.validation.AYTWeldValidationCDI11Test;
import org.jboss.tools.cdi.core.test.tck11.validation.AnnotationsValidationCDI11Test;
import org.jboss.tools.cdi.core.test.tck11.validation.BeansXmlValidationCDI11Test;
import org.jboss.tools.cdi.core.test.tck11.validation.CoreValidationCDI11Test;
import org.jboss.tools.cdi.core.test.tck11.validation.DefenitionErrorsValidationCDI11Test;
import org.jboss.tools.cdi.core.test.tck11.validation.DeploymentProblemsValidationCDI11Tests;
import org.jboss.tools.cdi.core.test.tck11.validation.DisableCDISupportCDI11Test;
import org.jboss.tools.cdi.core.test.tck11.validation.ELReferenceCDI11Test;
import org.jboss.tools.cdi.core.test.tck11.validation.ELValidationCDI11Test;
import org.jboss.tools.cdi.core.test.tck11.validation.IncrementalValidationCDI11Test;
import org.jboss.tools.cdi.core.test.tck11.validation.SuppressWarningsCDI11Tests;
import org.jboss.tools.cdi.core.test.tck11.validation.WeldValidationCDI11Test;
import org.jboss.tools.common.base.test.validation.ValidationExceptionTest;
import org.jboss.tools.test.util.ProjectImportTestSetup;

/**
 * @author Alexey Kazakov
 */
public class CDICoreAllTests {

	public static Test suite() {
		// it could be done here because it is not needed to be enabled back
		JavaModelManager.getIndexManager().disable();
		ValidationFramework.getDefault().suspendAllValidation(true);

		ValidationExceptionTest.initLogger();

		TestSuite suiteAll = new TestSuite("CDI Core Tests");
		suiteAll.addTestSuite(CDIImagesTest.class);
		suiteAll.addTestSuite(TypeTest.class);
		TestSuite suite = new TestSuite("TCK Tests");

		/***** TCK 1.0 project tests *****/

		// Core tests
		suite.addTestSuite(ELReferenceTest.class);
		suite.addTestSuite(ResolutionByNameTest.class);
		suite.addTestSuite(PackageInfoTest.class);
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
		suite.addTestSuite(InjectionPointWithNewQualifierTest.class);
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
		suite.addTestSuite(ResolvedTypesCacheTest.class);
		suite.addTestSuite(CDIUtilTest.class);
		suite.addTestSuite(CoreTest.class);
		suite.addTestSuite(ResourceExclusionTest.class);

		// Marker validation tests
		suite.addTestSuite(DefenitionErrorsValidationTest.class);
		suite.addTestSuite(DeploymentProblemsValidationTests.class);
		suite.addTestSuite(BeansXmlValidationTest.class);
		suite.addTestSuite(AnnotationsValidationTest.class);
		suite.addTestSuite(CoreValidationTest.class);
		suite.addTestSuite(ELValidationTest.class);
		suite.addTestSuite(SuppressWarningsTests.class);
		suite.addTestSuite(IncrementalValidationTest.class);
		suite.addTestSuite(WeldValidationTest.class);

		// As-you-type model tests
		suite.addTestSuite(CDIProjectAsYouTypeTest.class);

		// As-you-type validation tests
		suite.addTestSuite(CDIProjectAsYouTypeTest.class);
		suite.addTestSuite(AYTDefenitionErrorsValidationTest.class);
		suite.addTestSuite(AYTDeploymentProblemsValidationTests.class);
		suite.addTestSuite(AYTBeansXmlValidationTest.class);
		suite.addTestSuite(AYTAnnotationValidationTest.class);
		suite.addTestSuite(AYTSuppressWarningsTests.class);
		suite.addTestSuite(AYTWeldValidationTest.class);

		// Refactoring test
		suite.addTestSuite(NamedBeanRefactoringTest.class);

		suiteAll.addTestSuite(DisableCDISupportTest.class); // This test removes tck test projects. It's better to run it after all main TCK tests.

		/***** Not TCK tests *****/

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
		TestSuite weldSuite = new TestSuite("Weld Tests");
		weldSuite.addTestSuite(BuiltInContextBeanInjectionWeldTest.class);
		weldSuite.addTestSuite(WeldExcludeTest.class);
		ProjectImportTestSetup weldTestSetup = new ProjectImportTestSetup(weldSuite,
				"org.jboss.tools.cdi.core.test",
				new String[]{"projects/weld1.1"},
				new String[]{"weld1.1"});
		suiteAll.addTest(weldTestSetup);
		TestSuite archiveSuite = new TestSuite("Archive Tests");
		archiveSuite.addTestSuite(BeanArchivesTest.class);
		ProjectImportTestSetup archiveTestSetup = new ProjectImportTestSetup(archiveSuite,
				"org.jboss.tools.cdi.core.test",
				new String[]{"projects/CDIArchivesTest"},
				new String[]{"CDIArchivesTest"});
		suiteAll.addTest(archiveTestSetup);
		suiteAll.addTest(new CDICoreTestSetup(suite));

		suite = new TestSuite(MissingBeansXmlValidationTest.class.getName());
		suite.addTestSuite(MissingBeansXmlValidationTest.class);
		ProjectImportTestSetup testSetup = new ProjectImportTestSetup(suite,
				"org.jboss.tools.cdi.core.test",
				new String[]{"projects/missingBeansXmlParentProject", "projects/missingBeansXmlChildProject"},
				new String[]{"missingBeansXmlParentProject", "missingBeansXmlChildProject"});
		suiteAll.addTest(testSetup);

		suite = new TestSuite(BuilderOrderValidationTest.class.getName());
		suite.addTestSuite(BuilderOrderValidationTest.class);
		testSetup = new ProjectImportTestSetup(suite,
				"org.jboss.tools.cdi.core.test",
				new String[]{"projects/CDITestBrokenBuilderOrder"},
				new String[]{"CDITestBrokenBuilderOrder"});
		suiteAll.addTest(testSetup);


		/***** TCK 1.1 project tests *****/

		// Core tests
		suite = new TestSuite("TCK 1.1 Tests");
		suite.addTestSuite(VetoedCDI11Test.class);
		suite.addTestSuite(ELReferenceCDI11Test.class);
		suite.addTestSuite(ResolutionByNameCDI11Test.class);
		suite.addTestSuite(PackageInfoCDI11Test.class);
		suite.addTestSuite(BeanDefinitionCDI11Test.class);
		suite.addTestSuite(NameDefinitionCDI11Test.class);
		suite.addTestSuite(QualifierDefinitionCDI11Test.class);
		suite.addTestSuite(EnterpriseQualifierDefinitionCDI11Test.class);
		suite.addTestSuite(ScopeDefinitionCDI11Test.class);
		suite.addTestSuite(EnterpriseScopeDefinitionCDI11Test.class);
		suite.addTestSuite(StereotypeDefinitionCDI11Test.class);
		suite.addTestSuite(DefaultNamedCDI11Test.class);
		suite.addTestSuite(EnterpriseStereotypeDefinitionCDI11Test.class);
		suite.addTestSuite(StereotypeInheritenceCDI11Test.class);
		suite.addTestSuite(ProducerMethodDefinitionCDI11Test.class);
		suite.addTestSuite(InjectionPointCDI11Test.class);
		suite.addTestSuite(BeanSpecializationCDI11Test.class);
		suite.addTestSuite(ResolutionByTypeCDI11Test.class);
		suite.addTestSuite(EnterpriseResolutionByTypeCDI11Test.class);
		suite.addTestSuite(AssignabilityOfRawAndParameterizedTypesCDI11Test.class);
		suite.addTestSuite(InjectionPointWithNewQualifierCDI11Test.class);
		suite.addTestSuite(QualifierWithMembersCDI11Test.class);
		suite.addTestSuite(InterceptorDefinitionCDI11Test.class);
		suite.addTestSuite(DecoratorDefinitionCDI11Test.class);
		suite.addTestSuite(ObserverMethodResolutionCDI11Test.class);
		suite.addTestSuite(BuiltInBeanInjectionCDI11Test.class);
		suite.addTestSuite(BeansXmlCACDI11Test.class);
		suite.addTestSuite(SelectedAlternativeCDI11Test.class);
		suite.addTestSuite(CircularDependencyCDI11Test.class);
		suite.addTestSuite(DynamicLookupCDI11Test.class);
		suite.addTestSuite(AmbiguousDependencyCDI11Test.class);
		suite.addTestSuite(UnsatisfiedDependencyCDI11Test.class);
		suite.addTestSuite(ResolvedTypesCacheCDI11Test.class);
		suite.addTestSuite(CDIUtilCDI11Test.class);
		suite.addTestSuite(CoreCDI11Test.class);
		suite.addTestSuite(ResourceExclusionCDI11Test.class);
		suite.addTestSuite(PriorityCDI11Test.class);

		// Marker validation tests
		suite.addTestSuite(DefenitionErrorsValidationCDI11Test.class);
		suite.addTestSuite(DeploymentProblemsValidationCDI11Tests.class);
		suite.addTestSuite(BeansXmlValidationCDI11Test.class);
		suite.addTestSuite(AnnotationsValidationCDI11Test.class);
		suite.addTestSuite(CoreValidationCDI11Test.class);
		suite.addTestSuite(ELValidationCDI11Test.class);
		suite.addTestSuite(SuppressWarningsCDI11Tests.class);
		suite.addTestSuite(IncrementalValidationCDI11Test.class);
		suite.addTestSuite(WeldValidationCDI11Test.class);

		// As-you-type model tests
		suite.addTestSuite(CDIProjectAsYouTypeCDI11Test.class);

		// As-you-type validation tests
		suite.addTestSuite(CDIProjectAsYouTypeCDI11Test.class);
		suite.addTestSuite(AYTDefenitionErrorsValidationCDI11Test.class);
		suite.addTestSuite(AYTDeploymentProblemsValidationCDI11Tests.class);
		suite.addTestSuite(AYTBeansXmlValidationCDI11Test.class);
		suite.addTestSuite(AYTAnnotationValidationCDI11Test.class);
		suite.addTestSuite(AYTSuppressWarningsCDI11Tests.class);
		suite.addTestSuite(AYTWeldValidationCDI11Test.class);

		// Refactoring test
		suite.addTestSuite(NamedBeanRefactoringCDI11Test.class);

		suiteAll.addTestSuite(DisableCDISupportCDI11Test.class); // This test removes tck test projects. It's better to run it after all main TCK tests.

		suiteAll.addTest(new CDI11CoreTestSetup(suite));

		/***** Common validation exception tests *****/

		suiteAll.addTestSuite(ValidationExceptionTest.class); // This test should be added last!
		return suiteAll;
	}
}