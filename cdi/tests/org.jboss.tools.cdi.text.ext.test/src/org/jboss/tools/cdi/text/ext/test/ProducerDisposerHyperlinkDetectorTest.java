package org.jboss.tools.cdi.text.ext.test;

import java.util.ArrayList;

import org.jboss.tools.cdi.text.ext.CDIExtensionsMessages;
import org.jboss.tools.cdi.text.ext.hyperlink.DisposerHyperlink;
import org.jboss.tools.cdi.text.ext.hyperlink.ProducerDisposerHyperlinkDetector;
import org.jboss.tools.cdi.text.ext.hyperlink.ProducerHyperlink;

public class ProducerDisposerHyperlinkDetectorTest extends HyperlinkDetectorTest {

	public void testProducerDisposerHyperlinkDetector() throws Exception {
		ArrayList<TestRegion> regionList = new ArrayList<TestRegion>();
		regionList.add(new TestRegion(1199, 8, new TestHyperlink[]{new TestHyperlink(DisposerHyperlink.class, CDIExtensionsMessages.CDI_PRODUCER_DISPOSER_HYPERLINK_OPEN_BOUND_DISPOSER+ " dispose")})); // @Produces
		regionList.add(new TestRegion(1222, 7, new TestHyperlink[]{new TestHyperlink(DisposerHyperlink.class, CDIExtensionsMessages.CDI_PRODUCER_DISPOSER_HYPERLINK_OPEN_BOUND_DISPOSER+ " dispose")})); // producer
		regionList.add(new TestRegion(1291, 7, new TestHyperlink[]{new TestHyperlink(ProducerHyperlink.class, CDIExtensionsMessages.CDI_PRODUCER_DISPOSER_HYPERLINK_OPEN_BOUND_PRODUCER+ " produce")})); // disposer

		checkRegions("JavaSource/org/jboss/jsr299/tck/tests/decorators/invocation/producer/method/ProducerImpl.java", regionList, new ProducerDisposerHyperlinkDetector());
	}
}