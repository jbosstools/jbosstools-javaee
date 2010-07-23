package org.jboss.tools.cdi.text.ext.test;

import java.util.ArrayList;

import org.eclipse.jface.text.Region;
import org.jboss.tools.cdi.text.ext.hyperlink.ProducerDisposerHyperlinkDetector;

public class ProducerDisposerHyperlinkDetectorTest extends HyperlinkDetectorTest {

	public void testProducerDisposerHyperlinkDetector() throws Exception {
		ArrayList<Region> regionList = new ArrayList<Region>();
		regionList.add(new Region(1199, 8)); // @Produces
		regionList.add(new Region(1222, 7)); // producer
		regionList.add(new Region(1291, 7)); // disposer

		checkRegions("JavaSource/org/jboss/jsr299/tck/tests/decorators/invocation/producer/method/ProducerImpl.java", regionList, new ProducerDisposerHyperlinkDetector());
	}
}