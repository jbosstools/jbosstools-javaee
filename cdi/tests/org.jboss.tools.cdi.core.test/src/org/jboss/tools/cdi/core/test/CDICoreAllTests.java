/******************************************************************************* 
 * Copyright (c) 2009-2014 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.cdi.core.test;

import org.eclipse.jdt.internal.core.JavaModelManager;
import org.eclipse.wst.validation.ValidationFramework;
import org.jboss.tools.cdi.core.test.ca.BeansXmlCACDI11Test;
import org.jboss.tools.cdi.core.test.ca.BeansXmlCACDI12Test;
import org.jboss.tools.cdi.core.test.ca.BeansXmlCACDI20Test;
import org.jboss.tools.cdi.core.test.ca.BeansXmlCATest;
import org.jboss.tools.cdi.core.test.cdi20.validation.ObservesAsyncTest;
import org.jboss.tools.cdi.core.test.extension.ExtensionFactoryTest;
import org.jboss.tools.cdi.core.test.extension.ExtensionManagerTest;
import org.jboss.tools.cdi.core.test.extension.ExtensionsInSrsAndUsedProjectTest;
import org.jboss.tools.cdi.core.test.extension.SystemExtensionTest;
import org.jboss.tools.cdi.core.test.project.CDIFacetTest;
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
import org.jboss.tools.cdi.core.test.tck.validation.CDI12ArrayValidationTest;
import org.jboss.tools.cdi.core.test.tck.validation.CoreValidationTest;
import org.jboss.tools.cdi.core.test.tck.validation.DefenitionErrorsValidationTest;
import org.jboss.tools.cdi.core.test.tck.validation.DependentProjectValidationTest;
import org.jboss.tools.cdi.core.test.tck.validation.DeploymentProblemsValidationTests;
import org.jboss.tools.cdi.core.test.tck.validation.DisableCDISupportTest;
import org.jboss.tools.cdi.core.test.tck.validation.DiscoveryModeChangeTest;
import org.jboss.tools.cdi.core.test.tck.validation.DiscoveryModeChangeTestSetup;
import org.jboss.tools.cdi.core.test.tck.validation.ELReferenceTest;
import org.jboss.tools.cdi.core.test.tck.validation.ELValidationTest;
import org.jboss.tools.cdi.core.test.tck.validation.IncrementalValidationTest;
import org.jboss.tools.cdi.core.test.tck.validation.JBIDE28076ProjectValidationTest;
import org.jboss.tools.cdi.core.test.tck.validation.MissingBeansXmlValidationTest;
import org.jboss.tools.cdi.core.test.tck.validation.SuppressWarningsTests;
import org.jboss.tools.cdi.core.test.tck.validation.WeldExcludeIncrementalValidationTest;
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
import org.jboss.tools.cdi.core.test.tck11.validation.AnnotationsValidationCDI11ATest;
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
import org.jboss.tools.cdi.core.test.tck12.AssignabilityOfRawAndParameterizedTypesCDI12Test;
import org.jboss.tools.cdi.core.test.tck12.BeanDefinitionCDI12Test;
import org.jboss.tools.cdi.core.test.tck12.BeanSpecializationCDI12Test;
import org.jboss.tools.cdi.core.test.tck12.BuiltInBeanInjectionCDI12Test;
import org.jboss.tools.cdi.core.test.tck12.CDIProjectAsYouTypeCDI12Test;
import org.jboss.tools.cdi.core.test.tck12.CDIUtilCDI12Test;
import org.jboss.tools.cdi.core.test.tck12.CoreCDI12Test;
import org.jboss.tools.cdi.core.test.tck12.DecoratorDefinitionCDI12Test;
import org.jboss.tools.cdi.core.test.tck12.DefaultNamedCDI12Test;
import org.jboss.tools.cdi.core.test.tck12.EnterpriseQualifierDefinitionCDI12Test;
import org.jboss.tools.cdi.core.test.tck12.EnterpriseResolutionByTypeCDI12Test;
import org.jboss.tools.cdi.core.test.tck12.EnterpriseScopeDefinitionCDI12Test;
import org.jboss.tools.cdi.core.test.tck12.EnterpriseStereotypeDefinitionCDI12Test;
import org.jboss.tools.cdi.core.test.tck12.InjectionPointCDI12Test;
import org.jboss.tools.cdi.core.test.tck12.InjectionPointWithNewQualifierCDI12Test;
import org.jboss.tools.cdi.core.test.tck12.InterceptorDefinitionCDI12Test;
import org.jboss.tools.cdi.core.test.tck12.NameDefinitionCDI12Test;
import org.jboss.tools.cdi.core.test.tck12.NamedBeanRefactoringCDI12Test;
import org.jboss.tools.cdi.core.test.tck12.ObserverMethodResolutionCDI12Test;
import org.jboss.tools.cdi.core.test.tck12.PriorityCDI12Test;
import org.jboss.tools.cdi.core.test.tck12.ProducerMethodDefinitionCDI12Test;
import org.jboss.tools.cdi.core.test.tck12.QualifierDefinitionCDI12Test;
import org.jboss.tools.cdi.core.test.tck12.QualifierWithMembersCDI12Test;
import org.jboss.tools.cdi.core.test.tck12.ResolvedTypesCacheCDI12Test;
import org.jboss.tools.cdi.core.test.tck12.ResourceExclusionCDI12Test;
import org.jboss.tools.cdi.core.test.tck12.ScopeDefinitionCDI12Test;
import org.jboss.tools.cdi.core.test.tck12.SelectedAlternativeCDI12Test;
import org.jboss.tools.cdi.core.test.tck12.StereotypeDefinitionCDI12Test;
import org.jboss.tools.cdi.core.test.tck12.StereotypeInheritenceCDI12Test;
import org.jboss.tools.cdi.core.test.tck12.VetoedCDI12Test;
import org.jboss.tools.cdi.core.test.tck12.lookup.AmbiguousDependencyCDI12Test;
import org.jboss.tools.cdi.core.test.tck12.lookup.CircularDependencyCDI12Test;
import org.jboss.tools.cdi.core.test.tck12.lookup.DynamicLookupCDI12Test;
import org.jboss.tools.cdi.core.test.tck12.lookup.PackageInfoCDI12Test;
import org.jboss.tools.cdi.core.test.tck12.lookup.ResolutionByNameCDI12Test;
import org.jboss.tools.cdi.core.test.tck12.lookup.ResolutionByTypeCDI12Test;
import org.jboss.tools.cdi.core.test.tck12.lookup.UnsatisfiedDependencyCDI12Test;
import org.jboss.tools.cdi.core.test.tck12.validation.AYTAnnotationValidationCDI12Test;
import org.jboss.tools.cdi.core.test.tck12.validation.AYTBeansXmlValidationCDI12Test;
import org.jboss.tools.cdi.core.test.tck12.validation.AYTDefenitionErrorsValidationCDI12Test;
import org.jboss.tools.cdi.core.test.tck12.validation.AYTDeploymentProblemsValidationCDI12Tests;
import org.jboss.tools.cdi.core.test.tck12.validation.AYTSuppressWarningsCDI12Tests;
import org.jboss.tools.cdi.core.test.tck12.validation.AYTWeldValidationCDI12Test;
import org.jboss.tools.cdi.core.test.tck12.validation.AnnotationsValidationCDI12Test;
import org.jboss.tools.cdi.core.test.tck12.validation.BeansXmlValidationCDI12Test;
import org.jboss.tools.cdi.core.test.tck12.validation.CoreValidationCDI12Test;
import org.jboss.tools.cdi.core.test.tck12.validation.DefenitionErrorsValidationCDI12Test;
import org.jboss.tools.cdi.core.test.tck12.validation.DeploymentProblemsValidationCDI12Tests;
import org.jboss.tools.cdi.core.test.tck12.validation.DisableCDISupportCDI12Test;
import org.jboss.tools.cdi.core.test.tck12.validation.ELReferenceCDI12Test;
import org.jboss.tools.cdi.core.test.tck12.validation.IncrementalValidationCDI12Test;
import org.jboss.tools.cdi.core.test.tck12.validation.SuppressWarningsCDI12Tests;
import org.jboss.tools.cdi.core.test.tck12.validation.WeldValidationCDI12Test;
import org.jboss.tools.cdi.core.test.tck20.AssignabilityOfRawAndParameterizedTypesCDI20Test;
import org.jboss.tools.cdi.core.test.tck20.BeanDefinitionCDI20Test;
import org.jboss.tools.cdi.core.test.tck20.BeanSpecializationCDI20Test;
import org.jboss.tools.cdi.core.test.tck20.BuiltInBeanInjectionCDI20Test;
import org.jboss.tools.cdi.core.test.tck20.CDIProjectAsYouTypeCDI20Test;
import org.jboss.tools.cdi.core.test.tck20.CDIUtilCDI20Test;
import org.jboss.tools.cdi.core.test.tck20.CoreCDI20Test;
import org.jboss.tools.cdi.core.test.tck20.DecoratorDefinitionCDI20Test;
import org.jboss.tools.cdi.core.test.tck20.DefaultNamedCDI20Test;
import org.jboss.tools.cdi.core.test.tck20.EnterpriseQualifierDefinitionCDI20Test;
import org.jboss.tools.cdi.core.test.tck20.EnterpriseResolutionByTypeCDI20Test;
import org.jboss.tools.cdi.core.test.tck20.EnterpriseScopeDefinitionCDI20Test;
import org.jboss.tools.cdi.core.test.tck20.EnterpriseStereotypeDefinitionCDI20Test;
import org.jboss.tools.cdi.core.test.tck20.InjectionPointCDI20Test;
import org.jboss.tools.cdi.core.test.tck20.InjectionPointWithNewQualifierCDI20Test;
import org.jboss.tools.cdi.core.test.tck20.InterceptorDefinitionCDI20Test;
import org.jboss.tools.cdi.core.test.tck20.NameDefinitionCDI20Test;
import org.jboss.tools.cdi.core.test.tck20.NamedBeanRefactoringCDI20Test;
import org.jboss.tools.cdi.core.test.tck20.ObserverMethodResolutionCDI20Test;
import org.jboss.tools.cdi.core.test.tck20.PriorityCDI20Test;
import org.jboss.tools.cdi.core.test.tck20.ProducerMethodDefinitionCDI20Test;
import org.jboss.tools.cdi.core.test.tck20.QualifierDefinitionCDI20Test;
import org.jboss.tools.cdi.core.test.tck20.QualifierWithMembersCDI20Test;
import org.jboss.tools.cdi.core.test.tck20.ResolvedTypesCacheCDI20Test;
import org.jboss.tools.cdi.core.test.tck20.ResourceExclusionCDI20Test;
import org.jboss.tools.cdi.core.test.tck20.ScopeDefinitionCDI20Test;
import org.jboss.tools.cdi.core.test.tck20.SelectedAlternativeCDI20Test;
import org.jboss.tools.cdi.core.test.tck20.StereotypeDefinitionCDI20Test;
import org.jboss.tools.cdi.core.test.tck20.StereotypeInheritenceCDI20Test;
import org.jboss.tools.cdi.core.test.tck20.VetoedCDI20Test;
import org.jboss.tools.cdi.core.test.tck20.lookup.AmbiguousDependencyCDI20Test;
import org.jboss.tools.cdi.core.test.tck20.lookup.CircularDependencyCDI20Test;
import org.jboss.tools.cdi.core.test.tck20.lookup.DynamicLookupCDI20Test;
import org.jboss.tools.cdi.core.test.tck20.lookup.PackageInfoCDI20Test;
import org.jboss.tools.cdi.core.test.tck20.lookup.ResolutionByNameCDI20Test;
import org.jboss.tools.cdi.core.test.tck20.lookup.ResolutionByTypeCDI20Test;
import org.jboss.tools.cdi.core.test.tck20.lookup.UnsatisfiedDependencyCDI20Test;
import org.jboss.tools.cdi.core.test.tck20.validation.AYTAnnotationValidationCDI20Test;
import org.jboss.tools.cdi.core.test.tck20.validation.AYTBeansXmlValidationCDI20Test;
import org.jboss.tools.cdi.core.test.tck20.validation.AYTDefenitionErrorsValidationCDI20Test;
import org.jboss.tools.cdi.core.test.tck20.validation.AYTDeploymentProblemsValidationCDI20Tests;
import org.jboss.tools.cdi.core.test.tck20.validation.AYTSuppressWarningsCDI20Tests;
import org.jboss.tools.cdi.core.test.tck20.validation.AYTWeldValidationCDI20Test;
import org.jboss.tools.cdi.core.test.tck20.validation.AnnotationsValidationCDI20Test;
import org.jboss.tools.cdi.core.test.tck20.validation.BeansXmlValidationCDI20Test;
import org.jboss.tools.cdi.core.test.tck20.validation.CoreValidationCDI20Test;
import org.jboss.tools.cdi.core.test.tck20.validation.DefenitionErrorsValidationCDI20Test;
import org.jboss.tools.cdi.core.test.tck20.validation.DeploymentProblemsValidationCDI20Tests;
import org.jboss.tools.cdi.core.test.tck20.validation.DisableCDISupportCDI20Test;
import org.jboss.tools.cdi.core.test.tck20.validation.ELReferenceCDI20Test;
import org.jboss.tools.cdi.core.test.tck20.validation.ELValidationCDI20Test;
import org.jboss.tools.cdi.core.test.tck20.validation.IncrementalValidationCDI20Test;
import org.jboss.tools.cdi.core.test.tck20.validation.SuppressWarningsCDI20Tests;
import org.jboss.tools.cdi.core.test.tck20.validation.WeldValidationCDI20Test;
import org.jboss.tools.common.base.test.validation.ValidationExceptionTest;
import org.jboss.tools.test.util.ProjectImportTestSetup;

import junit.framework.Test;
import junit.framework.TestSuite;

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

		TestSuite suiteD = new TestSuite("Discovery Mode Change Test");
		suiteD.addTestSuite(DiscoveryModeChangeTest.class);
		suiteAll.addTest(new DiscoveryModeChangeTestSetup(suiteD));

		suiteAll.addTestSuite(CDIValidationMessagesTest.class);
		suiteAll.addTestSuite(CDIFacetedProjectListenerTest.class);
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

		suiteAll.addTestSuite(DisableCDISupportTest.class); // This test removes tck test projects. It's better to run
															// it after all main TCK tests.

		/***** Not TCK tests *****/

		suiteAll.addTestSuite(TwoWebContentFoldersTest.class);
		suiteAll.addTestSuite(RemoveJarFromClasspathTest.class);
		suiteAll.addTestSuite(ExtensionFactoryTest.class);
		suiteAll.addTestSuite(ExtensionManagerTest.class);
		suiteAll.addTestSuite(BeansXMLTest.class);
		TestSuite dependentSuite = new TestSuite("Dependent Projects Tests");
		dependentSuite.addTestSuite(DependentProjectTest.class);
		dependentSuite.addTestSuite(ExtensionsInSrsAndUsedProjectTest.class);
		dependentSuite.addTestSuite(SystemExtensionTest.class);
		DependentProjectsTestSetup dependent = new DependentProjectsTestSetup(dependentSuite);
		suiteAll.addTest(dependent);
		suiteAll.addTestSuite(EnableCDISupportForWarTest.class);
		suiteAll.addTestSuite(EnableCDISupportForJarTest.class);
		suiteAll.addTestSuite(CDIFacetTest.class);
		suiteAll.addTestSuite(DependentProjectValidationTest.class);
		suiteAll.addTestSuite(JBIDE28076ProjectValidationTest.class);
		TestSuite weldSuite = new TestSuite("Weld Tests");
		weldSuite.addTestSuite(BuiltInContextBeanInjectionWeldTest.class);
		weldSuite.addTestSuite(WeldExcludeTest.class);
		weldSuite.addTestSuite(WeldExcludeIncrementalValidationTest.class);
		ProjectImportTestSetup weldTestSetup = new ProjectImportTestSetup(weldSuite, "org.jboss.tools.cdi.core.test",
				new String[] { "projects/weld1.1" }, new String[] { "weld1.1" });
		suiteAll.addTest(weldTestSetup);
		TestSuite archiveSuite = new TestSuite("Archive Tests");
		archiveSuite.addTestSuite(BeanArchivesTest.class);
		ProjectImportTestSetup archiveTestSetup = new ProjectImportTestSetup(archiveSuite,
				"org.jboss.tools.cdi.core.test", new String[] { "projects/CDIArchivesTest" },
				new String[] { "CDIArchivesTest" });
		suiteAll.addTest(archiveTestSetup);
		suiteAll.addTest(new CDICoreTestSetup(suite));

		TestSuite cdi11Suite = new TestSuite("CDI 1.1 Annotated Tests");
		cdi11Suite.addTestSuite(CDI11AnnotatedTest.class);
		ProjectImportTestSetup cdi11TestSetup = new ProjectImportTestSetup(cdi11Suite, "org.jboss.tools.cdi.core.test",
				new String[] { "projects/CDITest11" }, new String[] { "CDITest11" });
		suiteAll.addTest(cdi11TestSetup);

		suite = new TestSuite(MissingBeansXmlValidationTest.class.getName());
		suite.addTestSuite(MissingBeansXmlValidationTest.class);
		suite.addTestSuite(CDI12ArrayValidationTest.class);
		ProjectImportTestSetup testSetup = new ProjectImportTestSetup(suite, "org.jboss.tools.cdi.core.test",
				new String[] { "projects/missingBeansXmlParentProject", "projects/missingBeansXmlChildProject",
						"projects/missingBeansXmlProjectCDI11", "projects/missingBeansXmlProjectCDI12" },
				new String[] { "missingBeansXmlParentProject", "missingBeansXmlChildProject",
						"missingBeansXmlProjectCDI11", "missingBeansXmlProjectCDI12" });
		suiteAll.addTest(testSetup);

		suite = new TestSuite(BuilderOrderValidationTest.class.getName());
		suite.addTestSuite(BuilderOrderValidationTest.class);
		testSetup = new ProjectImportTestSetup(suite, "org.jboss.tools.cdi.core.test",
				new String[] { "projects/CDITestBrokenBuilderOrder" }, new String[] { "CDITestBrokenBuilderOrder" });
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

		suiteAll.addTestSuite(DisableCDISupportCDI11Test.class); // This test removes tck test projects. It's better to
																	// run it after all main TCK tests.

		suiteAll.addTest(new CDI11CoreTestSetup(suite));

		/***** TCK 1.1 in annotated discovery mode project tests *****/

		suite = new TestSuite("TCK 1.1 Annotated Tests");

		// TODO add other 1.1 tests.

		// Marker validation tests
		suite.addTestSuite(AnnotationsValidationCDI11ATest.class);

		suiteAll.addTest(new CDI11AnnotatedCoreTestSetup(suite));

		/***** TCK 1.2 project tests *****/

		// Core tests
		suite = new TestSuite("TCK 1.2 Tests");
		suite.addTestSuite(VetoedCDI12Test.class);
		suite.addTestSuite(ELReferenceCDI12Test.class);
		suite.addTestSuite(ResolutionByNameCDI12Test.class);
		suite.addTestSuite(PackageInfoCDI12Test.class);
		suite.addTestSuite(BeanDefinitionCDI12Test.class);
		suite.addTestSuite(NameDefinitionCDI12Test.class);
		suite.addTestSuite(QualifierDefinitionCDI12Test.class);
		suite.addTestSuite(EnterpriseQualifierDefinitionCDI12Test.class);
		suite.addTestSuite(ScopeDefinitionCDI12Test.class);
		suite.addTestSuite(EnterpriseScopeDefinitionCDI12Test.class);
		suite.addTestSuite(StereotypeDefinitionCDI12Test.class);
		suite.addTestSuite(DefaultNamedCDI12Test.class);
		suite.addTestSuite(EnterpriseStereotypeDefinitionCDI12Test.class);
		suite.addTestSuite(StereotypeInheritenceCDI12Test.class);
		suite.addTestSuite(ProducerMethodDefinitionCDI12Test.class);
		suite.addTestSuite(InjectionPointCDI12Test.class);
		suite.addTestSuite(BeanSpecializationCDI12Test.class);
		suite.addTestSuite(ResolutionByTypeCDI12Test.class);
		suite.addTestSuite(EnterpriseResolutionByTypeCDI12Test.class);
		suite.addTestSuite(AssignabilityOfRawAndParameterizedTypesCDI12Test.class);
		suite.addTestSuite(InjectionPointWithNewQualifierCDI12Test.class);
		suite.addTestSuite(QualifierWithMembersCDI12Test.class);
		suite.addTestSuite(InterceptorDefinitionCDI12Test.class);
		suite.addTestSuite(DecoratorDefinitionCDI12Test.class);
		suite.addTestSuite(ObserverMethodResolutionCDI12Test.class);
		suite.addTestSuite(BuiltInBeanInjectionCDI12Test.class);
		suite.addTestSuite(BeansXmlCACDI12Test.class);
		suite.addTestSuite(SelectedAlternativeCDI12Test.class);
		suite.addTestSuite(CircularDependencyCDI12Test.class);
		suite.addTestSuite(DynamicLookupCDI12Test.class);
		suite.addTestSuite(AmbiguousDependencyCDI12Test.class);
		suite.addTestSuite(UnsatisfiedDependencyCDI12Test.class);
		suite.addTestSuite(ResolvedTypesCacheCDI12Test.class);
		suite.addTestSuite(CDIUtilCDI12Test.class);
		suite.addTestSuite(CoreCDI12Test.class);
		suite.addTestSuite(ResourceExclusionCDI12Test.class);
		suite.addTestSuite(PriorityCDI12Test.class);

		// Marker validation tests
		suite.addTestSuite(DefenitionErrorsValidationCDI12Test.class);
		suite.addTestSuite(DeploymentProblemsValidationCDI12Tests.class);
		suite.addTestSuite(BeansXmlValidationCDI12Test.class);
		suite.addTestSuite(AnnotationsValidationCDI12Test.class);
		suite.addTestSuite(CoreValidationCDI12Test.class);
		suite.addTestSuite(ELValidationCDI11Test.class);
		suite.addTestSuite(SuppressWarningsCDI12Tests.class);
		suite.addTestSuite(IncrementalValidationCDI12Test.class);
		suite.addTestSuite(WeldValidationCDI12Test.class);

		// As-you-type model tests
		suite.addTestSuite(CDIProjectAsYouTypeCDI12Test.class);

		// As-you-type validation tests
		suite.addTestSuite(CDIProjectAsYouTypeCDI12Test.class);
		suite.addTestSuite(AYTDefenitionErrorsValidationCDI12Test.class);
		suite.addTestSuite(AYTDeploymentProblemsValidationCDI12Tests.class);
		suite.addTestSuite(AYTBeansXmlValidationCDI12Test.class);
		suite.addTestSuite(AYTAnnotationValidationCDI12Test.class);
		suite.addTestSuite(AYTSuppressWarningsCDI12Tests.class);
		suite.addTestSuite(AYTWeldValidationCDI12Test.class);

		// Refactoring test
		suite.addTestSuite(NamedBeanRefactoringCDI12Test.class);

		suiteAll.addTestSuite(DisableCDISupportCDI12Test.class); // This test removes tck test projects. It's better to
																	// run it after all main TCK tests.

		suiteAll.addTest(new CDI12CoreTestSetup(suite));

		/***** TCK 2.0 project tests *****/

		// Core tests
		suite = new TestSuite("TCK 2.0 Tests");
		suite.addTestSuite(VetoedCDI20Test.class);
		suite.addTestSuite(ELReferenceCDI20Test.class);
		suite.addTestSuite(ResolutionByNameCDI20Test.class);
		suite.addTestSuite(PackageInfoCDI20Test.class);
		suite.addTestSuite(BeanDefinitionCDI20Test.class);
		suite.addTestSuite(NameDefinitionCDI20Test.class);
		suite.addTestSuite(QualifierDefinitionCDI20Test.class);
		suite.addTestSuite(EnterpriseQualifierDefinitionCDI20Test.class);
		suite.addTestSuite(ScopeDefinitionCDI20Test.class);
		suite.addTestSuite(EnterpriseScopeDefinitionCDI20Test.class);
		suite.addTestSuite(StereotypeDefinitionCDI20Test.class);
		suite.addTestSuite(DefaultNamedCDI20Test.class);
		suite.addTestSuite(EnterpriseStereotypeDefinitionCDI20Test.class);
		suite.addTestSuite(StereotypeInheritenceCDI20Test.class);
		suite.addTestSuite(ProducerMethodDefinitionCDI20Test.class);
		suite.addTestSuite(InjectionPointCDI20Test.class);
		suite.addTestSuite(BeanSpecializationCDI20Test.class);
		suite.addTestSuite(ResolutionByTypeCDI20Test.class);
		suite.addTestSuite(EnterpriseResolutionByTypeCDI20Test.class);
		suite.addTestSuite(AssignabilityOfRawAndParameterizedTypesCDI20Test.class);
		suite.addTestSuite(InjectionPointWithNewQualifierCDI20Test.class);
		suite.addTestSuite(QualifierWithMembersCDI20Test.class);
		suite.addTestSuite(InterceptorDefinitionCDI20Test.class);
		suite.addTestSuite(DecoratorDefinitionCDI20Test.class);
		suite.addTestSuite(ObserverMethodResolutionCDI20Test.class);
		suite.addTestSuite(BuiltInBeanInjectionCDI20Test.class);
		suite.addTestSuite(BeansXmlCACDI20Test.class);
		suite.addTestSuite(SelectedAlternativeCDI20Test.class);
		suite.addTestSuite(CircularDependencyCDI20Test.class);
		suite.addTestSuite(DynamicLookupCDI20Test.class);
		suite.addTestSuite(AmbiguousDependencyCDI20Test.class);
		suite.addTestSuite(UnsatisfiedDependencyCDI20Test.class);
		suite.addTestSuite(ResolvedTypesCacheCDI20Test.class);
		suite.addTestSuite(CDIUtilCDI20Test.class);
		suite.addTestSuite(CoreCDI20Test.class);
		suite.addTestSuite(ResourceExclusionCDI20Test.class);
		suite.addTestSuite(PriorityCDI20Test.class);

		// Marker validation tests
		suite.addTestSuite(DefenitionErrorsValidationCDI20Test.class);
		suite.addTestSuite(DeploymentProblemsValidationCDI20Tests.class);
		suite.addTestSuite(BeansXmlValidationCDI20Test.class);
		suite.addTestSuite(AnnotationsValidationCDI20Test.class);
		suite.addTestSuite(CoreValidationCDI20Test.class);
		suite.addTestSuite(ELValidationCDI20Test.class);
		suite.addTestSuite(SuppressWarningsCDI20Tests.class);
		suite.addTestSuite(IncrementalValidationCDI20Test.class);
		suite.addTestSuite(WeldValidationCDI20Test.class);

		// As-you-type model tests
		suite.addTestSuite(CDIProjectAsYouTypeCDI20Test.class);

		// As-you-type validation tests
		suite.addTestSuite(CDIProjectAsYouTypeCDI20Test.class);
		suite.addTestSuite(AYTDefenitionErrorsValidationCDI20Test.class);
		suite.addTestSuite(AYTDeploymentProblemsValidationCDI20Tests.class);
		suite.addTestSuite(AYTBeansXmlValidationCDI20Test.class);
		suite.addTestSuite(AYTAnnotationValidationCDI20Test.class);
		suite.addTestSuite(AYTSuppressWarningsCDI20Tests.class);
		suite.addTestSuite(AYTWeldValidationCDI20Test.class);

		// Refactoring test
		suite.addTestSuite(NamedBeanRefactoringCDI20Test.class);

		suiteAll.addTestSuite(DisableCDISupportCDI20Test.class); // This test removes tck test projects. It's better to
																	// run it after all main TCK tests.

		suiteAll.addTest(new CDI20CoreTestSetup(suite));

		suiteAll.addTestSuite(ObservesAsyncTest.class);

		/***** Common validation exception tests *****/

		suiteAll.addTestSuite(ValidationExceptionTest.class); // This test should be added last!
		return suiteAll;
	}
}