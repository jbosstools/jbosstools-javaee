package org.jboss.tools.cdi.seam.text.ext.test;

import java.util.ArrayList;

import org.eclipse.jface.text.Region;
import org.jboss.tools.cdi.seam.text.ext.hyperlink.CDISeamResourceLoadingHyperlinkDetector;
import org.jboss.tools.cdi.text.ext.test.HyperlinkDetectorTest;

public class CDISeamResourceLoadingHyperlinkDetectorTest extends HyperlinkDetectorTest{
	
	public void testCDISeamResourceLoadingHyperlinkDetector() throws Exception {
		ArrayList<Region> regionList = new ArrayList<Region>();
		regionList.add(new Region(115, 6)); 
		regionList.add(new Region(133, 6)); 
		regionList.add(new Region(140, 6)); 
		regionList.add(new Region(196, 6)); 
		regionList.add(new Region(250, 6)); 
		
		checkRegions("JavaSource/org/jboss/jsr299/tck/tests/jbt/openon/ResourceLoader.java", regionList, new CDISeamResourceLoadingHyperlinkDetector());
	}

}
