/*************************************************************************************
 * Copyright (c) 2008-2011 Red Hat, Inc. and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     JBoss by Red Hat - Initial implementation.
 ************************************************************************************/
package org.jboss.tools.cdi.ui.test.marker;

import org.eclipse.core.runtime.CoreException;
import org.jboss.tools.cdi.core.test.tck.TCKTest;
import org.jboss.tools.cdi.internal.core.validation.CDICoreValidator;
import org.jboss.tools.cdi.internal.core.validation.CDIValidationErrorManager;
import org.jboss.tools.cdi.ui.marker.AddAnnotationMarkerResolution;
import org.jboss.tools.cdi.ui.marker.AddLocalBeanMarkerResolution;
import org.jboss.tools.cdi.ui.marker.AddNameMarkerResolution;
import org.jboss.tools.cdi.ui.marker.AddRetentionAnnotationMarkerResolution;
import org.jboss.tools.cdi.ui.marker.AddSerializableInterfaceMarkerResolution;
import org.jboss.tools.cdi.ui.marker.AddTargetAnnotationMarkerResolution;
import org.jboss.tools.cdi.ui.marker.ChangeAnnotationMarkerResolution;
import org.jboss.tools.cdi.ui.marker.CreateCDIElementMarkerResolution;
import org.jboss.tools.cdi.ui.marker.DeleteAllDisposerDuplicantMarkerResolution;
import org.jboss.tools.cdi.ui.marker.DeleteAllInjectedConstructorsMarkerResolution;
import org.jboss.tools.cdi.ui.marker.DeleteAllOtherAnnotationsFromParametersMarkerResolution;
import org.jboss.tools.cdi.ui.marker.DeleteAnnotationMarkerResolution;
import org.jboss.tools.cdi.ui.marker.MakeBeanScopedDependentMarkerResolution;
import org.jboss.tools.cdi.ui.marker.MakeFieldProtectedMarkerResolution;
import org.jboss.tools.cdi.ui.marker.MakeFieldStaticMarkerResolution;
import org.jboss.tools.cdi.ui.marker.MakeMethodBusinessMarkerResolution;
import org.jboss.tools.cdi.ui.marker.MakeMethodPublicMarkerResolution;
import org.jboss.tools.common.base.test.MarkerResolutionTestUtil;

/**
 * @author Daniel Azarov
 * 
 */
public class CDIMarkerResolutionTest  extends TCKTest {

	public void testMakeProducerFieldStaticResolution() throws CoreException {
		MarkerResolutionTestUtil.checkResolution(tckProject, 
				new String[]{
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/NonStaticProducerOfSessionBeanBroken.java"
				},
				new String[]{
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/NonStaticProducerOfSessionBeanBroken.qfxresult"
				},
				CDICoreValidator.PROBLEM_TYPE,
				CDIValidationErrorManager.MESSAGE_ID_ATTRIBUTE_NAME,
				CDIValidationErrorManager.ILLEGAL_PRODUCER_FIELD_IN_SESSION_BEAN_ID,
				MakeFieldStaticMarkerResolution.class);
	}

	public void testMakeProducerMethodBusinessResolution() throws CoreException {
		MarkerResolutionTestUtil.checkResolution(
				tckProject,
				new String[]{
						"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/FooProducer.java",
						"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/FooProducerLocal.java"
				},
				new String[]{
						"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/FooProducer1.qfxresult",
						"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/FooProducerLocal.qfxresult"
				},
				CDICoreValidator.PROBLEM_TYPE,
				CDIValidationErrorManager.MESSAGE_ID_ATTRIBUTE_NAME,
				CDIValidationErrorManager.ILLEGAL_PRODUCER_METHOD_IN_SESSION_BEAN_ID,
				MakeMethodBusinessMarkerResolution.class);
	}

	public void testAddLocalBeanResolution() throws CoreException {
		MarkerResolutionTestUtil.checkResolution(
				tckProject,
				new String[]{
						"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/FooProducer.java"
				},
				new String[]{
						"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/FooProducer2.qfxresult",
				},
				CDICoreValidator.PROBLEM_TYPE,
				CDIValidationErrorManager.MESSAGE_ID_ATTRIBUTE_NAME,
				CDIValidationErrorManager.ILLEGAL_PRODUCER_METHOD_IN_SESSION_BEAN_ID,
				AddLocalBeanMarkerResolution.class);
	}

	public void testMakeProducerMethodPublicResolution() throws CoreException {
		MarkerResolutionTestUtil.checkResolution(tckProject,
				new String[]{
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/FooProducerNoInterface.java"
				},
				new String[]{
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/FooProducerNoInterface.qfxresult"
				},
				CDICoreValidator.PROBLEM_TYPE,
				CDIValidationErrorManager.MESSAGE_ID_ATTRIBUTE_NAME,
				CDIValidationErrorManager.ILLEGAL_PRODUCER_METHOD_IN_SESSION_BEAN_ID,
				MakeMethodPublicMarkerResolution.class);
	}
	
	public void testMakeObserverParamMethodBusinessResolution() throws CoreException {
		MarkerResolutionTestUtil.checkResolution(tckProject,
				new String[]{
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/TibetanTerrier_Broken.java",
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/Terrier.java"
				},
				new String[]{
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/TibetanTerrier_Broken1.qfxresult",
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/Terrier.qfxresult"
				},
				CDICoreValidator.PROBLEM_TYPE,
				CDIValidationErrorManager.MESSAGE_ID_ATTRIBUTE_NAME,
				CDIValidationErrorManager.ILLEGAL_OBSERVER_IN_SESSION_BEAN_ID,
				MakeMethodBusinessMarkerResolution.class);
	}

	public void testAddLocalBeanResolution2() throws CoreException {
		MarkerResolutionTestUtil.checkResolution(tckProject,
				new String[]{
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/TibetanTerrier_Broken.java"
				},
				new String[]{
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/TibetanTerrier_Broken2.qfxresult"
				},
				CDICoreValidator.PROBLEM_TYPE,
				CDIValidationErrorManager.MESSAGE_ID_ATTRIBUTE_NAME,
				CDIValidationErrorManager.ILLEGAL_OBSERVER_IN_SESSION_BEAN_ID,
				AddLocalBeanMarkerResolution.class);
	}

	public void testMakeObserverParamMethodPublicResolution() throws CoreException {
		MarkerResolutionTestUtil.checkResolution(tckProject,
				new String[]{
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/TibetanTerrier_BrokenNoInterface.java"
				},
				new String[]{
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/TibetanTerrier_BrokenNoInterface.qfxresult"
				},
				CDICoreValidator.PROBLEM_TYPE,
				CDIValidationErrorManager.MESSAGE_ID_ATTRIBUTE_NAME,
				CDIValidationErrorManager.ILLEGAL_OBSERVER_IN_SESSION_BEAN_ID,
				MakeMethodPublicMarkerResolution.class);
	}

	public void testMakeDisposerParamMethodBusinessResolution() throws CoreException {
		MarkerResolutionTestUtil.checkResolution(tckProject,
				new String[]{
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/NotBusinessMethod_Broken.java",
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/LocalInt.java"
				},
				new String[]{
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/NotBusinessMethod_Broken1.qfxresult",
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/LocalInt.qfxresult"
				},
				CDICoreValidator.PROBLEM_TYPE,
				CDIValidationErrorManager.MESSAGE_ID_ATTRIBUTE_NAME,
				CDIValidationErrorManager.ILLEGAL_DISPOSER_IN_SESSION_BEAN_ID,
				MakeMethodBusinessMarkerResolution.class);
	}

	public void testAddLocalBeanResolution3() throws CoreException {
		MarkerResolutionTestUtil.checkResolution(tckProject,
				new String[]{
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/NotBusinessMethod_Broken.java"
				},
				new String[]{
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/NotBusinessMethod_Broken2.qfxresult"
				},
				CDICoreValidator.PROBLEM_TYPE,
				CDIValidationErrorManager.MESSAGE_ID_ATTRIBUTE_NAME,
				CDIValidationErrorManager.ILLEGAL_DISPOSER_IN_SESSION_BEAN_ID,
				AddLocalBeanMarkerResolution.class);
	}

	public void testMakeDisposerParamMethodPublicResolution() throws CoreException {
		MarkerResolutionTestUtil.checkResolution(tckProject,
				new String[]{
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/NotBusinessMethod_BrokenNoInterface.java"
				},
				new String[]{
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/NotBusinessMethod_BrokenNoInterface.qfxresult"
				},
				CDICoreValidator.PROBLEM_TYPE,
				CDIValidationErrorManager.MESSAGE_ID_ATTRIBUTE_NAME,
				CDIValidationErrorManager.ILLEGAL_DISPOSER_IN_SESSION_BEAN_ID,
				MakeMethodPublicMarkerResolution.class);
	}

	public void testDeleteAllDisposerDuplicantsResolution() throws CoreException {
		MarkerResolutionTestUtil.checkResolution(tckProject,
				new String[]{
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/TimestampLogger_Broken.java"
				},
//				new String[]{
//					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/TimestampLogger_Broken.qfxresult"
//				},
				CDICoreValidator.PROBLEM_TYPE,
				CDIValidationErrorManager.MESSAGE_ID_ATTRIBUTE_NAME,
				CDIValidationErrorManager.MULTIPLE_DISPOSERS_FOR_PRODUCER_ID,
				DeleteAllDisposerDuplicantMarkerResolution.class);
	}

	public void testDeleteAllInjectedConstructorsResolution() throws CoreException {
		MarkerResolutionTestUtil.checkResolution(tckProject,
				new String[]{
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/Goose_Broken.java"
				},
//				new String[]{
//					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/Goose_Broken.qfxresult"
//				},
				CDICoreValidator.PROBLEM_TYPE,
				CDIValidationErrorManager.MESSAGE_ID_ATTRIBUTE_NAME,
				CDIValidationErrorManager.MULTIPLE_INJECTION_CONSTRUCTORS_ID,
				DeleteAllInjectedConstructorsMarkerResolution.class);
	}
	
	public void testAddSerializableInterfaceResolution() throws CoreException{
		MarkerResolutionTestUtil.checkResolution(tckProject,
				new String[]{
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/Hamina_Broken.java"
				},
				new String[]{
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/Hamina_Broken.qfxresult"
				},
				CDICoreValidator.PROBLEM_TYPE,
				CDIValidationErrorManager.MESSAGE_ID_ATTRIBUTE_NAME,
				CDIValidationErrorManager.NOT_PASSIVATION_CAPABLE_BEAN_ID,
				AddSerializableInterfaceMarkerResolution.class);
	}

	public void testAddSerializableInterfaceResolution2() throws CoreException{
		MarkerResolutionTestUtil.checkResolution(tckProject,
				new String[]{
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/SecondBean.java"
				},
				new String[]{
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/SecondBean.qfxresult"
				},
				CDICoreValidator.PROBLEM_TYPE,
				CDIValidationErrorManager.MESSAGE_ID_ATTRIBUTE_NAME,
				CDIValidationErrorManager.NOT_PASSIVATION_CAPABLE_BEAN_ID,
				AddSerializableInterfaceMarkerResolution.class);
	}

	public void testAddRetentionToQualifierResolution() throws CoreException{
		MarkerResolutionTestUtil.checkResolution(tckProject,
				new String[]{
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/TestQualifier1.java"
				},
				new String[]{
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/TestQualifier1.qfxresult"
				},
				CDICoreValidator.PROBLEM_TYPE,
				CDIValidationErrorManager.MESSAGE_ID_ATTRIBUTE_NAME,
				CDIValidationErrorManager.MISSING_RETENTION_ANNOTATION_IN_QUALIFIER_TYPE_ID,
				AddRetentionAnnotationMarkerResolution.class);
	}

	public void testChangeRetentionToQualifierResolution() throws CoreException{
		MarkerResolutionTestUtil.checkResolution(tckProject,
				new String[]{
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/TestQualifier2.java"
				},
				new String[]{
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/TestQualifier2.qfxresult"
				},
				CDICoreValidator.PROBLEM_TYPE,
				CDIValidationErrorManager.MESSAGE_ID_ATTRIBUTE_NAME,
				CDIValidationErrorManager.MISSING_RETENTION_ANNOTATION_IN_QUALIFIER_TYPE_ID,
				ChangeAnnotationMarkerResolution.class);
	}
	
	public void testAddRetentionToScopeResolution() throws CoreException{
		MarkerResolutionTestUtil.checkResolution(tckProject,
				new String[]{
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/TestScope1.java"
				},
				new String[]{
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/TestScope1.qfxresult"
				},
				CDICoreValidator.PROBLEM_TYPE,
				CDIValidationErrorManager.MESSAGE_ID_ATTRIBUTE_NAME,
				CDIValidationErrorManager.MISSING_RETENTION_ANNOTATION_IN_SCOPE_TYPE_ID,
				AddRetentionAnnotationMarkerResolution.class);
	}

	public void testChangeRetentionToScopeResolution() throws CoreException{
		MarkerResolutionTestUtil.checkResolution(tckProject,
				new String[]{
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/TestScope2.java"
				},
				new String[]{
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/TestScope2.qfxresult"
				},
				CDICoreValidator.PROBLEM_TYPE,
				CDIValidationErrorManager.MESSAGE_ID_ATTRIBUTE_NAME,
				CDIValidationErrorManager.MISSING_RETENTION_ANNOTATION_IN_SCOPE_TYPE_ID,
				ChangeAnnotationMarkerResolution.class);
	}
	
	public void testAddRetentionToStereotypeResolution() throws CoreException{
		MarkerResolutionTestUtil.checkResolution(tckProject,
				new String[]{
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/TestStereotype1.java"
				},
				new String[]{
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/TestStereotype1.qfxresult"
				},
				CDICoreValidator.PROBLEM_TYPE,
				CDIValidationErrorManager.MESSAGE_ID_ATTRIBUTE_NAME,
				CDIValidationErrorManager.MISSING_RETENTION_ANNOTATION_IN_STEREOTYPE_TYPE_ID,
				AddRetentionAnnotationMarkerResolution.class);
	}

	public void testChangeRetentionToStereotypeResolution() throws CoreException{
		MarkerResolutionTestUtil.checkResolution(tckProject,
				new String[]{
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/TestStereotype2.java"
				},
				new String[]{
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/TestStereotype2.qfxresult"
				},
				CDICoreValidator.PROBLEM_TYPE,
				CDIValidationErrorManager.MESSAGE_ID_ATTRIBUTE_NAME,
				CDIValidationErrorManager.MISSING_RETENTION_ANNOTATION_IN_STEREOTYPE_TYPE_ID,
				ChangeAnnotationMarkerResolution.class);
	}
	
	public void testAddTargetToStereotypeResolution() throws CoreException{
		MarkerResolutionTestUtil.checkResolution(tckProject,
				new String[]{
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/TestStereotype3.java"
				},
				CDICoreValidator.PROBLEM_TYPE,
				CDIValidationErrorManager.MESSAGE_ID_ATTRIBUTE_NAME,
				CDIValidationErrorManager.MISSING_TARGET_ANNOTATION_IN_STEREOTYPE_TYPE_ID,
				AddTargetAnnotationMarkerResolution.class);
	}

	public void testTargetRetentionToStereotypeResolution() throws CoreException{
		MarkerResolutionTestUtil.checkResolution(tckProject,
				new String[]{
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/TestStereotype4.java"
				},
				CDICoreValidator.PROBLEM_TYPE,
				CDIValidationErrorManager.MESSAGE_ID_ATTRIBUTE_NAME,
				CDIValidationErrorManager.MISSING_TARGET_ANNOTATION_IN_STEREOTYPE_TYPE_ID,
				ChangeAnnotationMarkerResolution.class);
	}
	
	public void testAddTargetToQualifierResolution() throws CoreException{
		MarkerResolutionTestUtil.checkResolution(tckProject,
				new String[]{
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/TestQualifier3.java"
				},
				CDICoreValidator.PROBLEM_TYPE,
				CDIValidationErrorManager.MESSAGE_ID_ATTRIBUTE_NAME,
				CDIValidationErrorManager.MISSING_TARGET_ANNOTATION_IN_QUALIFIER_TYPE_ID,
				AddTargetAnnotationMarkerResolution.class);
	}

	public void testChangeTargetToQualifierResolution() throws CoreException{
		MarkerResolutionTestUtil.checkResolution(tckProject,
				new String[]{
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/TestQualifier4.java"
				},
				CDICoreValidator.PROBLEM_TYPE,
				CDIValidationErrorManager.MESSAGE_ID_ATTRIBUTE_NAME,
				CDIValidationErrorManager.MISSING_TARGET_ANNOTATION_IN_QUALIFIER_TYPE_ID,
				ChangeAnnotationMarkerResolution.class);
	}
	
	public void testAddTargetToScopeResolution() throws CoreException{
		MarkerResolutionTestUtil.checkResolution(tckProject,
				new String[]{
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/TestScope3.java"
				},
				new String[]{
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/TestScope3.qfxresult"
				},
				CDICoreValidator.PROBLEM_TYPE,
				CDIValidationErrorManager.MESSAGE_ID_ATTRIBUTE_NAME,
				CDIValidationErrorManager.MISSING_TARGET_ANNOTATION_IN_SCOPE_TYPE_ID,
				AddTargetAnnotationMarkerResolution.class);
	}

	public void testChangeTargetToScopeResolution() throws CoreException{
		MarkerResolutionTestUtil.checkResolution(tckProject,
				new String[]{
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/TestScope4.java"
				},
				new String[]{
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/TestScope4.qfxresult"
				},
				CDICoreValidator.PROBLEM_TYPE,
				CDIValidationErrorManager.MESSAGE_ID_ATTRIBUTE_NAME,
				CDIValidationErrorManager.MISSING_TARGET_ANNOTATION_IN_SCOPE_TYPE_ID,
				ChangeAnnotationMarkerResolution.class);
	}
	
	public void testAddNonbindingToAnnotationMemberOfQualifierResolution() throws CoreException{
		MarkerResolutionTestUtil.checkResolution(tckProject,
				new String[]{
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/TestQualifier5.java"
				},
				new String[]{
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/TestQualifier5.qfxresult"
				},
				CDICoreValidator.PROBLEM_TYPE,
				CDIValidationErrorManager.MESSAGE_ID_ATTRIBUTE_NAME,
				CDIValidationErrorManager.MISSING_NONBINDING_FOR_ANNOTATION_VALUE_IN_QUALIFIER_TYPE_MEMBER_ID,
				AddAnnotationMarkerResolution.class);
	}

	public void testAddNonbindingToArrayMemberOfQualifierResolution() throws CoreException{
		MarkerResolutionTestUtil.checkResolution(tckProject,
				new String[]{
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/TestQualifier6.java"
				},
				new String[]{
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/TestQualifier6.qfxresult"
				},
				CDICoreValidator.PROBLEM_TYPE,
				CDIValidationErrorManager.MESSAGE_ID_ATTRIBUTE_NAME,
				CDIValidationErrorManager.MISSING_NONBINDING_FOR_ARRAY_VALUE_IN_QUALIFIER_TYPE_MEMBER_ID,
				AddAnnotationMarkerResolution.class);
	}

	public void testAddNonbindingToAnnotationMemberOfInterceptorResolution() throws CoreException{
		MarkerResolutionTestUtil.checkResolution(tckProject,
				new String[]{
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/TestInterceptor1.java"
				},
				new String[]{
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/TestInterceptor1.qfxresult"
				},
				CDICoreValidator.PROBLEM_TYPE,
				CDIValidationErrorManager.MESSAGE_ID_ATTRIBUTE_NAME,
				CDIValidationErrorManager.MISSING_NONBINDING_FOR_ANNOTATION_VALUE_IN_INTERCEPTOR_BINDING_TYPE_MEMBER_ID,
				AddAnnotationMarkerResolution.class);
	}

	public void testAddNonbindingToArrayMemberOfInterceptorResolution() throws CoreException{
		MarkerResolutionTestUtil.checkResolution(tckProject,
				new String[]{
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/TestInterceptor2.java"
				},
				new String[]{
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/TestInterceptor2.qfxresult"
				},
				CDICoreValidator.PROBLEM_TYPE,
				CDIValidationErrorManager.MESSAGE_ID_ATTRIBUTE_NAME,
				CDIValidationErrorManager.MISSING_NONBINDING_FOR_ARRAY_VALUE_IN_INTERCEPTOR_BINDING_TYPE_MEMBER_ID,
				AddAnnotationMarkerResolution.class);
	}

	public void testDeleteInjectFromProducerFieldResolution() throws CoreException{
		MarkerResolutionTestUtil.checkResolution(tckProject,
				new String[]{
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/TestInjectProducerField.java"
				},
				CDICoreValidator.PROBLEM_TYPE,
				CDIValidationErrorManager.MESSAGE_ID_ATTRIBUTE_NAME,
				CDIValidationErrorManager.PRODUCER_ANNOTATED_INJECT_ID,
				DeleteAnnotationMarkerResolution.class);
	}

	public void testDeleteInjectFromProducerMethodResolution() throws CoreException{
		MarkerResolutionTestUtil.checkResolution(tckProject,
				new String[]{
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/TestInjectProducerMethod.java"
				},
				CDICoreValidator.PROBLEM_TYPE,
				CDIValidationErrorManager.MESSAGE_ID_ATTRIBUTE_NAME,
				CDIValidationErrorManager.PRODUCER_ANNOTATED_INJECT_ID,
				DeleteAnnotationMarkerResolution.class);
	}

	public void testDeleteInjectFromObserverMethodResolution() throws CoreException{
		MarkerResolutionTestUtil.checkResolution(tckProject,
				new String[]{
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/TestInjectObserverMethod.java"
				},
				CDICoreValidator.PROBLEM_TYPE,
				CDIValidationErrorManager.MESSAGE_ID_ATTRIBUTE_NAME,
				CDIValidationErrorManager.OBSERVER_ANNOTATED_INJECT_ID,
				DeleteAnnotationMarkerResolution.class);
	}

	public void testDeleteInjectFromDisposerMethodResolution() throws CoreException{
		MarkerResolutionTestUtil.checkResolution(tckProject,
				new String[]{
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/TestInjectDisposerMethod.java"
				},
				CDICoreValidator.PROBLEM_TYPE,
				CDIValidationErrorManager.MESSAGE_ID_ATTRIBUTE_NAME,
				CDIValidationErrorManager.DISPOSER_ANNOTATED_INJECT_ID,
				DeleteAnnotationMarkerResolution.class);
	}

	public void testDeleteDisposesAnnotationFromParameterResolution() throws CoreException{
		MarkerResolutionTestUtil.checkResolution(tckProject,
				new String[]{
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/TestDisposerConstructor.java"
				},
				new String[]{
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/TestDisposerConstructor.qfxresult"
				},
				CDICoreValidator.PROBLEM_TYPE,
				CDIValidationErrorManager.MESSAGE_ID_ATTRIBUTE_NAME,
				CDIValidationErrorManager.CONSTRUCTOR_PARAMETER_ANNOTATED_DISPOSES_ID,
				DeleteAnnotationMarkerResolution.class);
	}

	public void testDeleteObservesAnnotationFromParameterResolution() throws CoreException{
		MarkerResolutionTestUtil.checkResolution(tckProject,
				new String[]{
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/TestObserverConstructor.java"
				},
				new String[]{
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/TestObserverConstructor.qfxresult"
				},
				CDICoreValidator.PROBLEM_TYPE,
				CDIValidationErrorManager.MESSAGE_ID_ATTRIBUTE_NAME,
				CDIValidationErrorManager.CONSTRUCTOR_PARAMETER_ANNOTATED_OBSERVES_ID,
				DeleteAnnotationMarkerResolution.class);
	}

	public void testDeleteDisposerFromInterceptorResolution() throws CoreException{
		MarkerResolutionTestUtil.checkResolution(tckProject,
				new String[]{
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/TestInterceptor3.java"
				},
				new String[]{
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/TestInterceptor3.qfxresult"
				},
				CDICoreValidator.PROBLEM_TYPE,
				CDIValidationErrorManager.MESSAGE_ID_ATTRIBUTE_NAME,
				CDIValidationErrorManager.DISPOSER_IN_INTERCEPTOR_ID,
				DeleteAnnotationMarkerResolution.class);
	}

	public void testDeleteDisposerFromDecoratorResolution() throws CoreException{
		MarkerResolutionTestUtil.checkResolution(tckProject,
				new String[]{
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/TestDecorator.java"
				},
				new String[]{
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/TestDecorator.qfxresult"
				},
				CDICoreValidator.PROBLEM_TYPE,
				CDIValidationErrorManager.MESSAGE_ID_ATTRIBUTE_NAME,
				CDIValidationErrorManager.DISPOSER_IN_DECORATOR_ID,
				DeleteAnnotationMarkerResolution.class);
	}

	public void testDeleteProducerFromInterceptorResolution() throws CoreException{
		MarkerResolutionTestUtil.checkResolution(tckProject,
				new String[]{
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/TestInterceptor4.java"
				},
				new String[]{
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/TestInterceptor4.qfxresult"
				},
				CDICoreValidator.PROBLEM_TYPE,
				CDIValidationErrorManager.MESSAGE_ID_ATTRIBUTE_NAME,
				CDIValidationErrorManager.PRODUCER_IN_INTERCEPTOR_ID,
				DeleteAnnotationMarkerResolution.class);
	}

	public void testDeleteProducerFromDecoratorResolution() throws CoreException{
		MarkerResolutionTestUtil.checkResolution(tckProject,
				new String[]{
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/TestDecorator2.java"
				},
				new String[]{
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/TestDecorator2.qfxresult"
				},
				CDICoreValidator.PROBLEM_TYPE,
				CDIValidationErrorManager.MESSAGE_ID_ATTRIBUTE_NAME,
				CDIValidationErrorManager.PRODUCER_IN_DECORATOR_ID,
				DeleteAnnotationMarkerResolution.class);
	}

	public void testNonEmptyNamedInStereotypeResolution() throws CoreException{
		MarkerResolutionTestUtil.checkResolution(tckProject,
				new String[]{
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/TestStereotype5.java"
				},
				CDICoreValidator.PROBLEM_TYPE,
				CDIValidationErrorManager.MESSAGE_ID_ATTRIBUTE_NAME,
				CDIValidationErrorManager.STEREOTYPE_DECLARES_NON_EMPTY_NAME_ID,
				DeleteAnnotationMarkerResolution.class);
	}

	public void testNonEmptyNamedInStereotypeResolution2() throws CoreException{
		MarkerResolutionTestUtil.checkResolution(tckProject,
				new String[]{
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/TestStereotype5.java"
				},
				CDICoreValidator.PROBLEM_TYPE,
				CDIValidationErrorManager.MESSAGE_ID_ATTRIBUTE_NAME,
				CDIValidationErrorManager.STEREOTYPE_DECLARES_NON_EMPTY_NAME_ID,
				ChangeAnnotationMarkerResolution.class);
	}

	public void testNamedInInterceptorResolution() throws CoreException{
		MarkerResolutionTestUtil.checkResolution(tckProject,
				new String[]{
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/TestInterceptor5.java"
				},
				new String[]{
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/TestInterceptor5.qfxresult"
				},
				CDICoreValidator.PROBLEM_TYPE,
				CDIValidationErrorManager.MESSAGE_ID_ATTRIBUTE_NAME,
				CDIValidationErrorManager.INTERCEPTOR_HAS_NAME_ID,
				DeleteAnnotationMarkerResolution.class);
	}

	public void testNamedStereotypedInterceptorResolution() throws CoreException{
		MarkerResolutionTestUtil.checkResolution(tckProject,
				new String[]{
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/NamedStereotypedInterceptorBroken.java",
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/NamedStereotype.java"
				},
				CDICoreValidator.PROBLEM_TYPE,
				CDIValidationErrorManager.MESSAGE_ID_ATTRIBUTE_NAME,
				CDIValidationErrorManager.INTERCEPTOR_HAS_NAME_ID,
				DeleteAnnotationMarkerResolution.class);
	}

	public void testNamedInDecoratorResolution() throws CoreException{
		MarkerResolutionTestUtil.checkResolution(tckProject,
				new String[]{
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/TestDecorator3.java"
				},
				new String[]{
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/TestDecorator3.qfxresult"
				},
				CDICoreValidator.PROBLEM_TYPE,
				CDIValidationErrorManager.MESSAGE_ID_ATTRIBUTE_NAME,
				CDIValidationErrorManager.DECORATOR_HAS_NAME_ID,
				DeleteAnnotationMarkerResolution.class);
	}

	public void testNamedStereotypedDecoratorResolution() throws CoreException{
		MarkerResolutionTestUtil.checkResolution(tckProject,
				new String[]{
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/NamedStereotypedDecoratorBroken.java",
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/NamedStereotype.java"
				},
				CDICoreValidator.PROBLEM_TYPE,
				CDIValidationErrorManager.MESSAGE_ID_ATTRIBUTE_NAME,
				CDIValidationErrorManager.DECORATOR_HAS_NAME_ID,
				DeleteAnnotationMarkerResolution.class);
	}
	
	public void testFullyQualifedNamedDecoratorResolution() throws CoreException{
		MarkerResolutionTestUtil.checkResolution(tckProject,
				new String[]{
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/TD.java",
				},
				CDICoreValidator.PROBLEM_TYPE,
				CDIValidationErrorManager.MESSAGE_ID_ATTRIBUTE_NAME,
				CDIValidationErrorManager.DECORATOR_HAS_NAME_ID,
				DeleteAnnotationMarkerResolution.class);
	}

	public void testTypedInStereotypeResolution() throws CoreException{
		MarkerResolutionTestUtil.checkResolution(tckProject,
				new String[]{
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/TestStereotype6.java"
				},
				new String[]{
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/TestStereotype6.qfxresult"
				},
				CDICoreValidator.PROBLEM_TYPE,
				CDIValidationErrorManager.MESSAGE_ID_ATTRIBUTE_NAME,
				CDIValidationErrorManager.STEREOTYPE_IS_ANNOTATED_TYPED_ID,
				DeleteAnnotationMarkerResolution.class);
	}
	
	public void testSpecializesInDecoratorResolution() throws CoreException{
		MarkerResolutionTestUtil.checkResolution(tckProject,
				new String[]{
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/TestDecorator4.java"
				},
				new String[]{
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/TestDecorator4.qfxresult"
				},
				CDICoreValidator.PROBLEM_TYPE,
				CDIValidationErrorManager.MESSAGE_ID_ATTRIBUTE_NAME,
				CDIValidationErrorManager.DECORATOR_ANNOTATED_SPECIALIZES_ID,
				DeleteAnnotationMarkerResolution.class);
	}
	
	public void testSpecializesInInterceptorResolution() throws CoreException{
		MarkerResolutionTestUtil.checkResolution(tckProject,
				new String[]{
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/TestInterceptor6.java"
				},
				new String[]{
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/TestInterceptor6.qfxresult"
				},
				CDICoreValidator.PROBLEM_TYPE,
				CDIValidationErrorManager.MESSAGE_ID_ATTRIBUTE_NAME,
				CDIValidationErrorManager.INTERCEPTOR_ANNOTATED_SPECIALIZES_ID,
				DeleteAnnotationMarkerResolution.class);
	}
	
	public void testDisposerInProducerResolution() throws CoreException{
		MarkerResolutionTestUtil.checkResolution(tckProject,
				new String[]{
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/TestDisposerProducerMethod.java"
				},
				CDICoreValidator.PROBLEM_TYPE,
				CDIValidationErrorManager.MESSAGE_ID_ATTRIBUTE_NAME,
				CDIValidationErrorManager.PRODUCER_PARAMETER_ILLEGALLY_ANNOTATED_DISPOSES_ID,
				DeleteAnnotationMarkerResolution.class);
	}

	public void testObserverInProducerResolution() throws CoreException{
		MarkerResolutionTestUtil.checkResolution(tckProject,
				new String[]{
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/TestObserverProducerMethod.java"
				},
				CDICoreValidator.PROBLEM_TYPE,
				CDIValidationErrorManager.MESSAGE_ID_ATTRIBUTE_NAME,
				CDIValidationErrorManager.PRODUCER_PARAMETER_ILLEGALLY_ANNOTATED_OBSERVES_ID,
				DeleteAnnotationMarkerResolution.class);
	}
	
	public void testDisposerInObserverResolution() throws CoreException{
		MarkerResolutionTestUtil.checkResolution(tckProject,
				new String[]{
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/DisposerInObserver.java"
				},
				CDICoreValidator.PROBLEM_TYPE,
				CDIValidationErrorManager.MESSAGE_ID_ATTRIBUTE_NAME,
				CDIValidationErrorManager.OBSERVER_PARAMETER_ILLEGALLY_ANNOTATED_ID,
				DeleteAnnotationMarkerResolution.class);
	}
	
	public void testObserverInDecoratorResolution() throws CoreException{
		MarkerResolutionTestUtil.checkResolution(tckProject,
				new String[]{
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/ObserverInDecorator.java"
				},
				new String[]{
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/ObserverInDecorator.qfxresult"
				},
				CDICoreValidator.PROBLEM_TYPE,
				CDIValidationErrorManager.MESSAGE_ID_ATTRIBUTE_NAME,
				CDIValidationErrorManager.OBSERVER_IN_DECORATOR_ID,
				DeleteAnnotationMarkerResolution.class);
	}

	public void testObserverInInterceptorResolution() throws CoreException{
		MarkerResolutionTestUtil.checkResolution(tckProject,
				new String[]{
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/ObserverInInterceptor.java"
				},
				new String[]{
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/ObserverInInterceptor.qfxresult"
				},
				CDICoreValidator.PROBLEM_TYPE,
				CDIValidationErrorManager.MESSAGE_ID_ATTRIBUTE_NAME,
				CDIValidationErrorManager.OBSERVER_IN_INTERCEPTOR_ID,
				DeleteAnnotationMarkerResolution.class);
	}

	public void testSessionBeanAnnotatedDecoratorResolution() throws CoreException{
		MarkerResolutionTestUtil.checkResolution(tckProject,
				new String[]{
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/SessionBeanAnnotatedDecoratorBroken.java"
				},
				new String[]{
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/SessionBeanAnnotatedDecoratorBroken.qfxresult"
				},
				CDICoreValidator.PROBLEM_TYPE,
				CDIValidationErrorManager.MESSAGE_ID_ATTRIBUTE_NAME,
				CDIValidationErrorManager.SESSION_BEAN_ANNOTATED_DECORATOR_ID,
				DeleteAnnotationMarkerResolution.class);
	}

	public void testSessionBeanAnnotatedInterceptorBrokenResolution() throws CoreException{
		MarkerResolutionTestUtil.checkResolution(tckProject,
				new String[]{
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/SessionBeanAnnotatedInterceptorBroken.java"
				},
				new String[]{
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/SessionBeanAnnotatedInterceptorBroken.qfxresult"
				},
				CDICoreValidator.PROBLEM_TYPE,
				CDIValidationErrorManager.MESSAGE_ID_ATTRIBUTE_NAME,
				CDIValidationErrorManager.SESSION_BEAN_ANNOTATED_INTERCEPTOR_ID,
				DeleteAnnotationMarkerResolution.class);
	}

	public void testCreateBeanClassResolution() throws CoreException{
		MarkerResolutionTestUtil.checkResolution(tckProject,
				new String[]{
					//"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/beans.xml"
					"WebContent/WEB-INF/beans.xml"
				},
				CDICoreValidator.PROBLEM_TYPE,
				CDIValidationErrorManager.MESSAGE_ID_ATTRIBUTE_NAME,
				CDIValidationErrorManager.UNKNOWN_ALTERNATIVE_BEAN_CLASS_NAME_ID,
				CreateCDIElementMarkerResolution.class);
	}

	public void testCreateStereotypeResolution() throws CoreException{
		MarkerResolutionTestUtil.checkResolution(tckProject,
				new String[]{
					//"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/beans.xml"
					"WebContent/WEB-INF/beans.xml"
				},
				CDICoreValidator.PROBLEM_TYPE,
				CDIValidationErrorManager.MESSAGE_ID_ATTRIBUTE_NAME,
				CDIValidationErrorManager.UNKNOWN_ALTERNATIVE_ANNOTATION_NAME_ID,
				CreateCDIElementMarkerResolution.class);
	}

	public void testCreateInterceptorResolution() throws CoreException{
		MarkerResolutionTestUtil.checkResolution(tckProject,
				new String[]{
					//"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/beans.xml"
					"WebContent/WEB-INF/beans.xml"
				},
				CDICoreValidator.PROBLEM_TYPE,
				CDIValidationErrorManager.MESSAGE_ID_ATTRIBUTE_NAME,
				CDIValidationErrorManager.UNKNOWN_INTERCEPTOR_CLASS_NAME_ID,
				CreateCDIElementMarkerResolution.class);
	}
	
	public void testAddNameResolution() throws CoreException{
		MarkerResolutionTestUtil.checkResolution(tckProject,
				new String[]{
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/NamedInjectionBroken.java"
				},
				CDICoreValidator.PROBLEM_TYPE,
				CDIValidationErrorManager.MESSAGE_ID_ATTRIBUTE_NAME,
				CDIValidationErrorManager.PARAM_INJECTION_DECLARES_EMPTY_NAME_ID,
				AddNameMarkerResolution.class);
	}

	public void testDeleteAllOtherDisposerParametersResolution() throws CoreException{
		MarkerResolutionTestUtil.checkResolution(tckProject,
				new String[]{
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/MultipleDisposers.java"
				},
				CDICoreValidator.PROBLEM_TYPE,
				CDIValidationErrorManager.MESSAGE_ID_ATTRIBUTE_NAME,
				CDIValidationErrorManager.MULTIPLE_DISPOSING_PARAMETERS_ID,
				DeleteAllOtherAnnotationsFromParametersMarkerResolution.class);
	}

	public void testDeleteAllOtherObserverParametersResolution() throws CoreException{
		MarkerResolutionTestUtil.checkResolution(tckProject,
				new String[]{
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/MultipleObservers.java"
				},
				CDICoreValidator.PROBLEM_TYPE,
				CDIValidationErrorManager.MESSAGE_ID_ATTRIBUTE_NAME,
				CDIValidationErrorManager.MULTIPLE_OBSERVING_PARAMETERS_ID,
				DeleteAllOtherAnnotationsFromParametersMarkerResolution.class);
	}
	
	public void testMakeFieldProtectedResolution() throws CoreException{
		MarkerResolutionTestUtil.checkResolution(tckProject,
				new String[]{
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/Leopard_Broken.java"
				},
				CDICoreValidator.PROBLEM_TYPE,
				CDIValidationErrorManager.MESSAGE_ID_ATTRIBUTE_NAME,
				CDIValidationErrorManager.ILLEGAL_SCOPE_FOR_MANAGED_BEAN_WITH_PUBLIC_FIELD_ID,
				MakeFieldProtectedMarkerResolution.class);
	}
	
	public void testMakeBeanScopedDependentResolution() throws CoreException{
		MarkerResolutionTestUtil.checkResolution(tckProject,
				new String[]{
					"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/Leopard_Broken.java"
				},
				CDICoreValidator.PROBLEM_TYPE,
				CDIValidationErrorManager.MESSAGE_ID_ATTRIBUTE_NAME,
				CDIValidationErrorManager.ILLEGAL_SCOPE_FOR_MANAGED_BEAN_WITH_PUBLIC_FIELD_ID,
				MakeBeanScopedDependentMarkerResolution.class);
	}

}