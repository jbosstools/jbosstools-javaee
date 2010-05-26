package org.jboss.tools.cdi.text.ext.test;

import java.util.ArrayList;

import org.eclipse.jface.text.Region;
import org.jboss.tools.cdi.text.ext.hyperlink.InjectedPointHyperlinkDetector;
import org.jboss.tools.cdi.text.ext.hyperlink.ProducerDisposerHyperlinkDetector;

import junit.framework.Test;
import junit.framework.TestSuite;


public class InjectedPointHyperlinkDetectorTest extends HyperlinkDetectorTest {

	public static Test suite() {
		return new TestSuite(InjectedPointHyperlinkDetectorTest.class);
	}

	public void testInjectedPointHyperlinkDetector() throws Exception {
		ArrayList<Region> regionList = new ArrayList<Region>();
		regionList.add(new Region(115, 6)); // Inject
		regionList.add(new Region(140, 6)); // logger
		regionList.add(new Region(196, 6)); // logger
		regionList.add(new Region(250, 6)); // logger
		
		checkRegions("JavaSource/org/jboss/jsr299/tck/tests/lookup/injectionpoint/LoggerConsumer.java", regionList, new InjectedPointHyperlinkDetector());
	}

	public void testInjectedProducerMethodParametersHyperlinkDetector() throws Exception {
		ArrayList<Region> regionList = new ArrayList<Region>();
		regionList.add(new Region(571, 5)); // order
		regionList.add(new Region(659, 3)); 
		regionList.add(new Region(695, 3));
		
		checkRegions("JavaSource/org/jboss/jsr299/tck/tests/jbt/openon/CustomProducerImpl.java", regionList, new InjectedPointHyperlinkDetector());
	}
	
	public void testProducerDisposerHyperlinkDetector() throws Exception {
		ArrayList<Region> regionList = new ArrayList<Region>();
		regionList.add(new Region(1199, 8)); // @Produces
		regionList.add(new Region(1222, 7)); // producer
		regionList.add(new Region(1291, 7)); // disposer

		checkRegions("JavaSource/org/jboss/jsr299/tck/tests/decorators/invocation/producer/method/ProducerImpl.java", regionList, new ProducerDisposerHyperlinkDetector());
	}

}