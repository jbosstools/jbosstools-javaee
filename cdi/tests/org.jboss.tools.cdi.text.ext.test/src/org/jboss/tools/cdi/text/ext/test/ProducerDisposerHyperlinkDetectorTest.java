package org.jboss.tools.cdi.text.ext.test;

import java.util.ArrayList;

import org.jboss.tools.cdi.core.test.tck.TCKTest;
import org.jboss.tools.cdi.text.ext.CDIExtensionsMessages;
import org.jboss.tools.cdi.text.ext.hyperlink.DisposerHyperlink;
import org.jboss.tools.cdi.text.ext.hyperlink.ProducerDisposerHyperlinkDetector;
import org.jboss.tools.cdi.text.ext.hyperlink.ProducerHyperlink;
import org.jboss.tools.cdi.text.ext.test.CDIHyperlinkTestUtil.TestHyperlink;
import org.jboss.tools.cdi.text.ext.test.CDIHyperlinkTestUtil.TestRegion;

public class ProducerDisposerHyperlinkDetectorTest extends TCKTest {

	public void testProducerDisposerHyperlinkDetector() throws Exception {
		ArrayList<TestRegion> regionList = new ArrayList<TestRegion>();
		regionList.add(new TestRegion(/*1199, 8*/"Produces", new TestHyperlink[]{new TestHyperlink(DisposerHyperlink.class, CDIExtensionsMessages.CDI_PRODUCER_DISPOSER_HYPERLINK_OPEN_BOUND_DISPOSER+ " dispose")})); // @Produces
		regionList.add(new TestRegion(/*1222, 7*/"produce", new TestHyperlink[]{new TestHyperlink(DisposerHyperlink.class, CDIExtensionsMessages.CDI_PRODUCER_DISPOSER_HYPERLINK_OPEN_BOUND_DISPOSER+ " dispose")})); // producer
		regionList.add(new TestRegion(/*1291, 7*/"dispose", new TestHyperlink[]{new TestHyperlink(ProducerHyperlink.class, CDIExtensionsMessages.CDI_PRODUCER_DISPOSER_HYPERLINK_OPEN_BOUND_PRODUCER+ " produce")})); // disposer

		CDIHyperlinkTestUtil.checkRegions(tckProject, "JavaSource/org/jboss/jsr299/tck/tests/decorators/invocation/producer/method/ProducerImpl.java", regionList, new ProducerDisposerHyperlinkDetector());
	}
}