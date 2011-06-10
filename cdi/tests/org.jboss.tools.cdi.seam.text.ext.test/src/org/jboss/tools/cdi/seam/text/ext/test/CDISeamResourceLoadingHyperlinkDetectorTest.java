package org.jboss.tools.cdi.seam.text.ext.test;

import java.util.ArrayList;

import org.eclipse.core.resources.IFile;
import org.eclipse.osgi.util.NLS;
import org.jboss.tools.cdi.core.test.tck.TCKTest;
import org.jboss.tools.cdi.seam.text.ext.CDISeamExtMessages;
import org.jboss.tools.cdi.seam.text.ext.hyperlink.CDISeamResourceLoadingHyperlink;
import org.jboss.tools.cdi.seam.text.ext.hyperlink.CDISeamResourceLoadingHyperlinkDetector;
import org.jboss.tools.cdi.text.ext.test.CDIHyperlinkTestUtil;
import org.jboss.tools.common.util.FileUtil;
import org.jboss.tools.cdi.text.ext.test.CDIHyperlinkTestUtil.TestHyperlink;
import org.jboss.tools.cdi.text.ext.test.CDIHyperlinkTestUtil.TestRegion;

public class CDISeamResourceLoadingHyperlinkDetectorTest extends TCKTest{
	private static final String FILENAME = "JavaSource/org/jboss/jsr299/tck/tests/jbt/openon/ResourceLoader.java";
	
	public void testCDISeamResourceLoadingHyperlinkDetector() throws Exception {
		IFile file = tckProject.getFile(FILENAME);
		String text = FileUtil.readStream(file);
		
		
		ArrayList<TestRegion> regionList = new ArrayList<TestRegion>();
		
		int injectPosition = text.indexOf("@Inject");
		//System.out.println("injectPosition - "+injectPosition);
		if(injectPosition > 0){
			regionList.add(new TestRegion(injectPosition, 58, new TestHyperlink[]{new TestHyperlink(CDISeamResourceLoadingHyperlink.class, NLS.bind(CDISeamExtMessages.CDI_SEAM_RESOURCE_LOADING_HYPERLINK, "WEB-INF/beans.xml"))}));
		}
		 
		CDIHyperlinkTestUtil.checkRegions(tckProject, FILENAME, regionList, new CDISeamResourceLoadingHyperlinkDetector());
	}

}
