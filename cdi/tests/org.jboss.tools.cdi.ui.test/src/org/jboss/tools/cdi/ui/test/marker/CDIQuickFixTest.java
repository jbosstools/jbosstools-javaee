package org.jboss.tools.cdi.ui.test.marker;

import org.eclipse.core.runtime.CoreException;
import org.jboss.tools.cdi.core.test.tck.TCKTest;
import org.jboss.tools.cdi.internal.core.validation.CDIValidationErrorManager;
import org.jboss.tools.cdi.ui.marker.AddAnnotationMarkerResolution;
import org.jboss.tools.cdi.ui.marker.AddLocalBeanMarkerResolution;
import org.jboss.tools.cdi.ui.marker.AddRetentionAnnotationMarkerResolution;
import org.jboss.tools.cdi.ui.marker.AddSerializableInterfaceMarkerResolution;
import org.jboss.tools.cdi.ui.marker.AddTargetAnnotationMarkerResolution;
import org.jboss.tools.cdi.ui.marker.ChangeAnnotationMarkerResolution;
import org.jboss.tools.cdi.ui.marker.CreateCDIElementMarkerResolution;
import org.jboss.tools.cdi.ui.marker.DeleteAnnotationMarkerResolution;
import org.jboss.tools.cdi.ui.marker.MakeFieldStaticMarkerResolution;
import org.jboss.tools.cdi.ui.marker.MakeMethodPublicMarkerResolution;
import org.jboss.tools.common.base.test.QuickFixTestUtil;

public class CDIQuickFixTest extends TCKTest {
	private QuickFixTestUtil util = new QuickFixTestUtil();
	
//	private static boolean isSuspendedValidationDefaultValue;
//	public void setUp() throws Exception {
//		super.setUp();
//		isSuspendedValidationDefaultValue = ValidationFramework.getDefault().isSuspended();
//		ValidationFramework.getDefault().suspendAllValidation(false);
//		tckProject.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
//		tckProject.build(IncrementalProjectBuilder.FULL_BUILD, new NullProgressMonitor());
//	}
//	
//	public void tearDown() throws Exception {
//		ValidationFramework.getDefault().suspendAllValidation(isSuspendedValidationDefaultValue);
//		super.tearDown();
//	}
	
	public void testMakeFieldStatic() throws CoreException {
		util.checkProposal(tckProject,
				"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/MakeFieldStatic.java",
				"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/MakeFieldStatic.qfxresult",
				"@Produces",
				CDIValidationErrorManager.ILLEGAL_PRODUCER_FIELD_IN_SESSION_BEAN_ID,
				MakeFieldStaticMarkerResolution.class, true);
	}

	public void testAddLocalBeanResolution() throws CoreException {
		util.checkProposal(tckProject,
				"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/AddLocalBean.java",
				"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/AddLocalBean.qfxresult",
				"@Produces",
				CDIValidationErrorManager.ILLEGAL_PRODUCER_METHOD_IN_SESSION_BEAN_ID,
				AddLocalBeanMarkerResolution.class, true);
	}
	
	public void testMakeProducerMethodPublicResolution() throws CoreException {
		util.checkProposal(tckProject,
				"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/MakeMethodPublic.java",
				"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/MakeMethodPublic.qfxresult",
				"@Produces",
				CDIValidationErrorManager.ILLEGAL_PRODUCER_METHOD_IN_SESSION_BEAN_ID,
				MakeMethodPublicMarkerResolution.class, true);
	}
	
	
	public void testAddSerializableInterfaceResolution() throws CoreException{
		util.checkProposal(tckProject,
				"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/AddSerializable.java",
				"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/AddSerializable.qfxresult",
				"AddSerializable",
				CDIValidationErrorManager.NOT_PASSIVATION_CAPABLE_BEAN_ID,
				AddSerializableInterfaceMarkerResolution.class, true);
	}
	
	public void testAddRetentionToQualifierResolution() throws CoreException{
		util.checkProposal(tckProject,
				"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/AddRetention.java",
				"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/AddRetention.qfxresult",
				"AddRetention",
				CDIValidationErrorManager.MISSING_RETENTION_ANNOTATION_IN_QUALIFIER_TYPE_ID,
				AddRetentionAnnotationMarkerResolution.class, true);
	}
	
	public void testChangeRetentionToQualifierResolution() throws CoreException{
		util.checkProposal(tckProject,
				"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/ChangeAnnotation.java",
				"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/ChangeAnnotation.qfxresult",
				"@Retention(value = null)",
				CDIValidationErrorManager.MISSING_RETENTION_ANNOTATION_IN_QUALIFIER_TYPE_ID,
				ChangeAnnotationMarkerResolution.class, true);
	}
	
	public void testAddTargetToScopeResolution() throws CoreException{
		util.checkProposal(tckProject,
				"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/AddTarget.java",
				"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/AddTarget.qfxresult",
				"AddTarget",
				CDIValidationErrorManager.MISSING_TARGET_ANNOTATION_IN_SCOPE_TYPE_ID,
				AddTargetAnnotationMarkerResolution.class, true);
	}
	
	public void testAddNonbindingToAnnotationMemberOfQualifierResolution() throws CoreException{
		util.checkProposal(tckProject,
				"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/AddAnnotation.java",
				"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/AddAnnotation.qfxresult",
				"abc",
				CDIValidationErrorManager.MISSING_NONBINDING_FOR_ANNOTATION_VALUE_IN_QUALIFIER_TYPE_MEMBER_ID,
				AddAnnotationMarkerResolution.class, true);
	}
	
	public void testDeleteDisposesAnnotationFromParameterResolution() throws CoreException{
		util.checkProposal(tckProject,
				"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/DeleteAnnotation.java",
				"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/DeleteAnnotation.qfxresult",
				"@Inject",
				CDIValidationErrorManager.CONSTRUCTOR_PARAMETER_ANNOTATED_DISPOSES_ID,
				DeleteAnnotationMarkerResolution.class, true);
	}
	
	public void testCreateBeanClassResolution() throws CoreException{
		util.checkProposal(tckProject,
				"WebContent/WEB-INF/beans.xml",
				"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/NonExistingAlternative.qfxresult",
				"com.acme.NonExistingClass",
				CDIValidationErrorManager.UNKNOWN_ALTERNATIVE_BEAN_CLASS_NAME_ID,
				CreateCDIElementMarkerResolution.class, false);
	}

	public void testCreateStereotypeResolution() throws CoreException{
		util.checkProposal(tckProject,
				"WebContent/WEB-INF/beans.xml",
				"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/NonExistingStereotype.qfxresult",
				"com.acme.NotExistingStereotype",
				CDIValidationErrorManager.UNKNOWN_ALTERNATIVE_ANNOTATION_NAME_ID,
				CreateCDIElementMarkerResolution.class, false);
	}
	
	public void testCreateDecoratorResolution() throws CoreException{
		util.checkProposal(tckProject,
				"WebContent/WEB-INF/beans.xml",
				"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/NonExistingDecorator.qfxresult",
				"com.acme.NonExistingDecoratorClass",
				CDIValidationErrorManager.UNKNOWN_DECORATOR_BEAN_CLASS_NAME_ID,
				CreateCDIElementMarkerResolution.class, false);
	}

	public void testCreateInterceptorResolution() throws CoreException{
		util.checkProposal(tckProject,
				"WebContent/WEB-INF/beans.xml",
				"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/NonExistingInterceptor.qfxresult",
				"com.acme.NonExistingInterceptorClass",
				CDIValidationErrorManager.UNKNOWN_INTERCEPTOR_CLASS_NAME_ID,
				CreateCDIElementMarkerResolution.class, false);
	}
}
