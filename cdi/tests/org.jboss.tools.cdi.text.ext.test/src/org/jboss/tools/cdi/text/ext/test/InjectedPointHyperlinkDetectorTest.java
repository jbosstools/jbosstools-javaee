package org.jboss.tools.cdi.text.ext.test;

import java.util.ArrayList;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.text.ext.CDIExtensionsMessages;
import org.jboss.tools.cdi.text.ext.hyperlink.AlternativeInjectedPointListHyperlink;
import org.jboss.tools.cdi.text.ext.hyperlink.InjectedPointHyperlink;
import org.jboss.tools.cdi.text.ext.hyperlink.InjectedPointHyperlinkDetector;
import org.jboss.tools.cdi.text.ext.test.HyperlinkDetectorTest.TestRegion;
import org.jboss.tools.common.util.FileUtil;


public class InjectedPointHyperlinkDetectorTest extends HyperlinkDetectorTest {

	public void testInjectedPointHyperlinkDetector() throws Exception {
		Set<IBean> beans = cdiProject.getBeans("/tck/JavaSource/org/jboss/jsr299/tck/tests/lookup/injectionpoint/BasicLogger.java",	true);
		IBean bean=null;
		for(IBean b : beans){
			bean = b;
		}
		ArrayList<TestRegion> regionList = new ArrayList<TestRegion>();
		regionList.add(new TestRegion(115, 6,   new TestHyperlink[]{
			new TestHyperlink(InjectedPointHyperlink.class, CDIExtensionsMessages.CDI_INJECTED_POINT_HYPERLINK_OPEN_INJECT_BEAN+ " BasicLogger", bean),
			new TestHyperlink(AlternativeInjectedPointListHyperlink.class, CDIExtensionsMessages.CDI_INJECTED_POINT_HYPERLINK_SHOW_ALTERNATIVES)
		})); // Inject
		regionList.add(new TestRegion(133, 6,   new TestHyperlink[]{
			new TestHyperlink(InjectedPointHyperlink.class, CDIExtensionsMessages.CDI_INJECTED_POINT_HYPERLINK_OPEN_INJECT_BEAN+ " BasicLogger", bean),
			new TestHyperlink(AlternativeInjectedPointListHyperlink.class, CDIExtensionsMessages.CDI_INJECTED_POINT_HYPERLINK_SHOW_ALTERNATIVES)
		})); // Logger
		regionList.add(new TestRegion(140, 6,   new TestHyperlink[]{
			new TestHyperlink(InjectedPointHyperlink.class, CDIExtensionsMessages.CDI_INJECTED_POINT_HYPERLINK_OPEN_INJECT_BEAN+ " BasicLogger", bean),
			new TestHyperlink(AlternativeInjectedPointListHyperlink.class, CDIExtensionsMessages.CDI_INJECTED_POINT_HYPERLINK_SHOW_ALTERNATIVES)
		})); // logger
		regionList.add(new TestRegion(196, 6,   new TestHyperlink[]{
			new TestHyperlink(InjectedPointHyperlink.class, CDIExtensionsMessages.CDI_INJECTED_POINT_HYPERLINK_OPEN_INJECT_BEAN+ " BasicLogger", bean),
			new TestHyperlink(AlternativeInjectedPointListHyperlink.class, CDIExtensionsMessages.CDI_INJECTED_POINT_HYPERLINK_SHOW_ALTERNATIVES)
		})); // logger
		regionList.add(new TestRegion(250, 6,   new TestHyperlink[]{
			new TestHyperlink(InjectedPointHyperlink.class, CDIExtensionsMessages.CDI_INJECTED_POINT_HYPERLINK_OPEN_INJECT_BEAN+ " BasicLogger", bean),
			new TestHyperlink(AlternativeInjectedPointListHyperlink.class, CDIExtensionsMessages.CDI_INJECTED_POINT_HYPERLINK_SHOW_ALTERNATIVES)
		})); // logger
		
		checkRegions("JavaSource/org/jboss/jsr299/tck/tests/lookup/injectionpoint/LoggerConsumer.java", regionList, new InjectedPointHyperlinkDetector());
	}

	public void testInjectedProducerMethodParametersHyperlinkDetector() throws Exception {
		Set<IBean> beans = cdiProject.getBeans("/tck/JavaSource/org/jboss/jsr299/tck/tests/jbt/openon/CustomProducerImpl.java",	true);
		IBean bean=null;
		for(IBean b : beans){
			bean = b;
		}
		IFile file = tckProject.getFile("JavaSource/org/jboss/jsr299/tck/tests/jbt/openon/CustomProducerImpl.java");
		String text = FileUtil.readStream(file);
		
		int orderPosition = text.indexOf("Something order");
		
		ArrayList<TestRegion> regionList = new ArrayList<TestRegion>();
		regionList.add(new TestRegion(orderPosition, 15,   new TestHyperlink[]{
			new TestHyperlink(InjectedPointHyperlink.class, CDIExtensionsMessages.CDI_INJECTED_POINT_HYPERLINK_OPEN_INJECT_BEAN+ " CustomProducerImpl.produce()", bean)
		})); // order
		
		checkRegions("JavaSource/org/jboss/jsr299/tck/tests/jbt/openon/CustomProducerImpl.java", regionList, new InjectedPointHyperlinkDetector());
	}
	
	public void testInjectedConstructorParametersHyperlinkDetector() throws Exception {
		Set<IBean> beans = cdiProject.getBeans("/tck/JavaSource/org/jboss/jsr299/tck/tests/context/dependent/Fox.java",	true);
		IBean bean=null;
		for(IBean b : beans){
			bean = b;
		}
		ArrayList<TestRegion> regionList = new ArrayList<TestRegion>();
		regionList.add(new TestRegion(880, 6,  new TestHyperlink[]{new TestHyperlink(InjectedPointHyperlink.class, CDIExtensionsMessages.CDI_INJECTED_POINT_HYPERLINK_OPEN_INJECT_BEAN+ " Fox", bean)}));
		regionList.add(new TestRegion(894, 3,  new TestHyperlink[]{new TestHyperlink(InjectedPointHyperlink.class, CDIExtensionsMessages.CDI_INJECTED_POINT_HYPERLINK_OPEN_INJECT_BEAN+ " Fox", bean)})); // Fox
		regionList.add(new TestRegion(898, 3,  new TestHyperlink[]{new TestHyperlink(InjectedPointHyperlink.class, CDIExtensionsMessages.CDI_INJECTED_POINT_HYPERLINK_OPEN_INJECT_BEAN+ " Fox", bean)}));
		regionList.add(new TestRegion(975, 3,  new TestHyperlink[]{new TestHyperlink(InjectedPointHyperlink.class, CDIExtensionsMessages.CDI_INJECTED_POINT_HYPERLINK_OPEN_INJECT_BEAN+ " Fox", bean)}));
		regionList.add(new TestRegion(979, 3,  new TestHyperlink[]{new TestHyperlink(InjectedPointHyperlink.class, CDIExtensionsMessages.CDI_INJECTED_POINT_HYPERLINK_OPEN_INJECT_BEAN+ " Fox", bean)}));
		regionList.add(new TestRegion(1017, 3, new TestHyperlink[]{new TestHyperlink(InjectedPointHyperlink.class, CDIExtensionsMessages.CDI_INJECTED_POINT_HYPERLINK_OPEN_INJECT_BEAN+ " Fox", bean)}));

		checkRegions("JavaSource/org/jboss/jsr299/tck/tests/context/dependent/FoxFarm.java", regionList, new InjectedPointHyperlinkDetector());
	}
	
	public void testInjectedInitializerParametersHyperlinkDetector() throws Exception {
		Set<IBean> beans = cdiProject.getBeans("/tck/JavaSource/org/jboss/jsr299/tck/tests/context/dependent/Fox.java",	true);
		IBean bean=null;
		for(IBean b : beans){
			bean = b;
		}
		ArrayList<TestRegion> regionList = new ArrayList<TestRegion>();
		regionList.add(new TestRegion(880, 6,  new TestHyperlink[]{new TestHyperlink(InjectedPointHyperlink.class, CDIExtensionsMessages.CDI_INJECTED_POINT_HYPERLINK_OPEN_INJECT_BEAN+ " Fox", bean)}));
		regionList.add(new TestRegion(894, 3,  new TestHyperlink[]{new TestHyperlink(InjectedPointHyperlink.class, CDIExtensionsMessages.CDI_INJECTED_POINT_HYPERLINK_OPEN_INJECT_BEAN+ " Fox", bean)})); // Fox
		regionList.add(new TestRegion(898, 3,  new TestHyperlink[]{new TestHyperlink(InjectedPointHyperlink.class, CDIExtensionsMessages.CDI_INJECTED_POINT_HYPERLINK_OPEN_INJECT_BEAN+ " Fox", bean)}));
		regionList.add(new TestRegion(972, 3,  new TestHyperlink[]{new TestHyperlink(InjectedPointHyperlink.class, CDIExtensionsMessages.CDI_INJECTED_POINT_HYPERLINK_OPEN_INJECT_BEAN+ " Fox", bean)}));
		regionList.add(new TestRegion(976, 3,  new TestHyperlink[]{new TestHyperlink(InjectedPointHyperlink.class, CDIExtensionsMessages.CDI_INJECTED_POINT_HYPERLINK_OPEN_INJECT_BEAN+ " Fox", bean)}));
		regionList.add(new TestRegion(1014, 3, new TestHyperlink[]{new TestHyperlink(InjectedPointHyperlink.class, CDIExtensionsMessages.CDI_INJECTED_POINT_HYPERLINK_OPEN_INJECT_BEAN+ " Fox", bean)}));

		checkRegions("JavaSource/org/jboss/jsr299/tck/tests/context/dependent/FoxHole.java", regionList, new InjectedPointHyperlinkDetector());
	}

}