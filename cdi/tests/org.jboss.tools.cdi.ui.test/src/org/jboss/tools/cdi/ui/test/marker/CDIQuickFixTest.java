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
import org.jboss.tools.cdi.ui.marker.DeleteAnnotationMarkerResolution;
import org.jboss.tools.cdi.ui.marker.MakeFieldStaticMarkerResolution;
import org.jboss.tools.cdi.ui.marker.MakeMethodPublicMarkerResolution;
import org.jboss.tools.common.base.test.QuickFixTestUtil;

public class CDIQuickFixTest extends TCKTest {
	private QuickFixTestUtil util = new QuickFixTestUtil();
	
	public void testMakeFieldStatic() throws CoreException {
		util.checkPrpposal(tckProject,
				"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/MakeFieldStatic.java",
				"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/MakeFieldStatic.qfxresult",
				CDIValidationErrorManager.ILLEGAL_PRODUCER_FIELD_IN_SESSION_BEAN_ID,
				MakeFieldStaticMarkerResolution.class);
	}

	public void testAddLocalBeanResolution() throws CoreException {
		util.checkPrpposal(tckProject,
				"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/AddLocalBean.java",
				"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/AddLocalBean.qfxresult",
				CDIValidationErrorManager.ILLEGAL_PRODUCER_METHOD_IN_SESSION_BEAN_ID,
				AddLocalBeanMarkerResolution.class);
	}
	
	public void testMakeProducerMethodPublicResolution() throws CoreException {
		util.checkPrpposal(tckProject,
				"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/MakeMethodPublic.java",
				"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/MakeMethodPublic.qfxresult",
				CDIValidationErrorManager.ILLEGAL_PRODUCER_METHOD_IN_SESSION_BEAN_ID,
				MakeMethodPublicMarkerResolution.class);
	}
	
	
	public void testAddSerializableInterfaceResolution() throws CoreException{
		util.checkPrpposal(tckProject,
				"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/AddSerializable.java",
				"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/AddSerializable.qfxresult",
				CDIValidationErrorManager.NOT_PASSIVATION_CAPABLE_BEAN_ID,
				AddSerializableInterfaceMarkerResolution.class);
	}
	
	public void testAddRetentionToQualifierResolution() throws CoreException{
		util.checkPrpposal(tckProject,
				"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/AddRetention.java",
				"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/AddRetention.qfxresult",
				CDIValidationErrorManager.MISSING_RETENTION_ANNOTATION_IN_QUALIFIER_TYPE_ID,
				AddRetentionAnnotationMarkerResolution.class);
	}
	
	public void testChangeRetentionToQualifierResolution() throws CoreException{
		util.checkPrpposal(tckProject,
				"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/ChangeAnnotation.java",
				"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/ChangeAnnotation.qfxresult",
				CDIValidationErrorManager.MISSING_RETENTION_ANNOTATION_IN_QUALIFIER_TYPE_ID,
				ChangeAnnotationMarkerResolution.class);
	}
	
	public void testAddTargetToScopeResolution() throws CoreException{
		util.checkPrpposal(tckProject,
				"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/AddTarget.java",
				"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/AddTarget.qfxresult",
				CDIValidationErrorManager.MISSING_TARGET_ANNOTATION_IN_SCOPE_TYPE_ID,
				AddTargetAnnotationMarkerResolution.class);
	}
	
	public void testAddNonbindingToAnnotationMemberOfQualifierResolution() throws CoreException{
		util.checkPrpposal(tckProject,
				"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/AddAnnotation.java",
				"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/AddAnnotation.qfxresult",
				CDIValidationErrorManager.MISSING_NONBINDING_FOR_ANNOTATION_VALUE_IN_QUALIFIER_TYPE_MEMBER_ID,
				AddAnnotationMarkerResolution.class);
	}
	
	public void testDeleteDisposesAnnotationFromParameterResolution() throws CoreException{
		util.checkPrpposal(tckProject,
				"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/DeleteAnnotation.java",
				"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/DeleteAnnotation.qfxresult",
				CDIValidationErrorManager.CONSTRUCTOR_PARAMETER_ANNOTATED_DISPOSES_ID,
				DeleteAnnotationMarkerResolution.class);
	}
	
}
