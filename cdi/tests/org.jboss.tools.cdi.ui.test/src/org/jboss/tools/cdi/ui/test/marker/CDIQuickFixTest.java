package org.jboss.tools.cdi.ui.test.marker;

import org.eclipse.core.runtime.CoreException;
import org.jboss.tools.cdi.core.test.tck.TCKTest;
import org.jboss.tools.cdi.internal.core.validation.CDIValidationErrorManager;
import org.jboss.tools.common.base.test.QuickFixTestUtil;
import org.jboss.tools.common.ui.marker.ConfigureProblemSeverityMarkerResolution;

public class CDIQuickFixTest extends TCKTest {
	QuickFixTestUtil util = new QuickFixTestUtil();
	
	public void testConfigureProblemSeverity() throws CoreException {
		util.checkPrpposal(tckProject,
				"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/NonStaticProducerBroken.java",
				"JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/NonStaticProducerBroken.new",
				CDIValidationErrorManager.MESSAGE_ID_ATTRIBUTE_NAME,
				CDIValidationErrorManager.ILLEGAL_PRODUCER_FIELD_IN_SESSION_BEAN_ID,
				ConfigureProblemSeverityMarkerResolution.class);
	} 
}
