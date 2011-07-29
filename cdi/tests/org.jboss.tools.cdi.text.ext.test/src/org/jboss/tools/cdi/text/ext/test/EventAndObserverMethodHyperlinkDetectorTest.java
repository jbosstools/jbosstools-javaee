package org.jboss.tools.cdi.text.ext.test;

import java.util.ArrayList;

import org.jboss.tools.cdi.core.test.tck.TCKTest;
import org.jboss.tools.cdi.text.ext.CDIExtensionsMessages;
import org.jboss.tools.cdi.text.ext.hyperlink.EventAndObserverMethodHyperlinkDetector;
import org.jboss.tools.cdi.text.ext.hyperlink.EventListHyperlink;
import org.jboss.tools.cdi.text.ext.hyperlink.ObserverMethodListHyperlink;
import org.jboss.tools.cdi.text.ext.test.CDIHyperlinkTestUtil.TestRegion;
import org.jboss.tools.cdi.text.ext.test.CDIHyperlinkTestUtil.TestHyperlink;

public class EventAndObserverMethodHyperlinkDetectorTest extends TCKTest {

	public void testEventHyperlinkDetector() throws Exception {
		String[] elementPaths = new String[]{
				"/tck/JavaSource/org/jboss/jsr299/tck/tests/event/bindingTypes/DiscerningObserver.java",
				"/tck/JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/TibetanTerrier_Broken.java",
				"/tck/JavaSource/org/jboss/jsr299/tck/tests/event/observer/checkedException/TeaCupPomeranian.java",
				"/tck/JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/interceptors/ObserverMethodInInterceptorBroken.java",
				"/tck/JavaSource/org/jboss/jsr299/tck/tests/implementation/enterprise/newBean/GoldenRetriever.java",
				"/tck/JavaSource/org/jboss/jsr299/tck/tests/event/observer/runtimeException/TeaCupPomeranian.java",
				"/tck/JavaSource/org/jboss/jsr299/tck/tests/event/bindingTypes/DiscerningObserver.java",
				"/tck/JavaSource/org/jboss/jsr299/tck/tests/event/broken/observer/notBusinessMethod/TibetanTerrier_Broken.java",
				"/tck/JavaSource/org/jboss/jsr299/tck/tests/event/broken/observer8/Terrier.java",
				"/tck/JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/decorators/ObserverMethodInDecoratorBroken.java",
				"/tck/JavaSource/org/jboss/jsr299/tck/tests/jbt/validation/observers/ClassFragmentLogger.java",
				"/tck/JavaSource/org/jboss/jsr299/tck/tests/implementation/simple/newSimpleBean/Fox.java",
				"/tck/JavaSource/org/jboss/jsr299/tck/tests/event/broken/observer/beanNotManaged/AbstractBean.java",
				"/tck/JavaSource/org/jboss/jsr299/tck/tests/jbt/quickfixes/TibetanTerrier_BrokenNoInterface.java",
				"/tck/JavaSource/org/jboss/jsr299/tck/tests/event/StringObserver.java",
				"/tck/JavaSource/org/jboss/jsr299/tck/tests/event/observer/transactional/Pomeranian.java",
				"/tck/JavaSource/org/jboss/jsr299/tck/tests/event/eventTypes/EventTypeFamilyObserver.java"
		};
		
		ArrayList<TestRegion> regionList = new ArrayList<TestRegion>();
		
		regionList.add(new TestRegion(/*959, 6*/"Inject",   new TestHyperlink[]{new TestHyperlink(ObserverMethodListHyperlink.class, CDIExtensionsMessages.CDI_EVENT_LIST_HYPERLINK_OPEN_OBSERVER_METHODS, elementPaths)}));
		regionList.add(new TestRegion(/*967, 16*/"Any Event<String",  new TestHyperlink[]{new TestHyperlink(ObserverMethodListHyperlink.class, CDIExtensionsMessages.CDI_EVENT_LIST_HYPERLINK_OPEN_OBSERVER_METHODS, elementPaths)}));
		regionList.add(new TestRegion(/*985, 11*/"stringEvent",  new TestHyperlink[]{new TestHyperlink(ObserverMethodListHyperlink.class, CDIExtensionsMessages.CDI_EVENT_LIST_HYPERLINK_OPEN_OBSERVER_METHODS, elementPaths)}));
		regionList.add(new TestRegion(/*1006, 6*/"Inject",  new TestHyperlink[]{new TestHyperlink(ObserverMethodListHyperlink.class, CDIExtensionsMessages.CDI_EVENT_LIST_HYPERLINK_OPEN_OBSERVER_METHODS, elementPaths)}));
		regionList.add(new TestRegion(/*1014, 3*/"Any",  new TestHyperlink[]{new TestHyperlink(ObserverMethodListHyperlink.class, CDIExtensionsMessages.CDI_EVENT_LIST_HYPERLINK_OPEN_OBSERVER_METHODS, elementPaths)}));
		regionList.add(new TestRegion(/*1019, 34*/"NonRuntimeBindingType Event<String",  new TestHyperlink[]{new TestHyperlink(ObserverMethodListHyperlink.class, CDIExtensionsMessages.CDI_EVENT_LIST_HYPERLINK_OPEN_OBSERVER_METHODS, elementPaths)}));
		regionList.add(new TestRegion(/*1055, 42*/"stringEventWithAnyAndNonRuntimeBindingType",  new TestHyperlink[]{new TestHyperlink(ObserverMethodListHyperlink.class, CDIExtensionsMessages.CDI_EVENT_LIST_HYPERLINK_OPEN_OBSERVER_METHODS, elementPaths)}));
		regionList.add(new TestRegion(/*1107, 6*/"Inject",  new TestHyperlink[]{new TestHyperlink(ObserverMethodListHyperlink.class, CDIExtensionsMessages.CDI_EVENT_LIST_HYPERLINK_OPEN_OBSERVER_METHODS, elementPaths)}));
		regionList.add(new TestRegion(/*1115, 34*/"NonRuntimeBindingType Event<String",  new TestHyperlink[]{new TestHyperlink(ObserverMethodListHyperlink.class, CDIExtensionsMessages.CDI_EVENT_LIST_HYPERLINK_OPEN_OBSERVER_METHODS, elementPaths)}));
		regionList.add(new TestRegion(/*1151, 36*/"stringEventWithOnlyNonRuntimeBindingType",  new TestHyperlink[]{new TestHyperlink(ObserverMethodListHyperlink.class, CDIExtensionsMessages.CDI_EVENT_LIST_HYPERLINK_OPEN_OBSERVER_METHODS, elementPaths)}));
		regionList.add(new TestRegion(/*1235, 11*/"stringEvent",  new TestHyperlink[]{new TestHyperlink(ObserverMethodListHyperlink.class, CDIExtensionsMessages.CDI_EVENT_LIST_HYPERLINK_OPEN_OBSERVER_METHODS, elementPaths)}));
		regionList.add(new TestRegion(/*1334, 42*/"stringEventWithAnyAndNonRuntimeBindingType",  new TestHyperlink[]{new TestHyperlink(ObserverMethodListHyperlink.class, CDIExtensionsMessages.CDI_EVENT_LIST_HYPERLINK_OPEN_OBSERVER_METHODS, elementPaths)}));

		CDIHyperlinkTestUtil.checkRegions(tckProject, "JavaSource/org/jboss/jsr299/tck/tests/event/bindingTypes/EventEmitter.java", regionList, new EventAndObserverMethodHyperlinkDetector());
	}

	public void testObserverMethodHyperlinkDetector() throws Exception {
		String[] elementPaths = new String[]{
			"/tck/JavaSource/org/jboss/jsr299/tck/tests/event/bindingTypes/EventEmitter.java",
			"/tck/JavaSource/org/jboss/jsr299/tck/tests/event/bindingTypes/EventEmitter.java",
			"/tck/JavaSource/org/jboss/jsr299/tck/tests/event/bindingTypes/EventEmitter.java",
			"/tck/JavaSource/org/jboss/jsr299/tck/tests/event/fires/nonbinding/OwlFinch_Broken.java"
		};
		
		ArrayList<TestRegion> regionList = new ArrayList<TestRegion>();
		
		regionList.add(new TestRegion(/*1201, 13*/"observeSimpleEvent",  new TestHyperlink[]{new TestHyperlink(EventListHyperlink.class, CDIExtensionsMessages.CDI_EVENT_LIST_HYPERLINK_OPEN_EVENTS, elementPaths)}));
		regionList.add(new TestRegion(/*1216, 15*/"Observes String",  new TestHyperlink[]{new TestHyperlink(EventListHyperlink.class, CDIExtensionsMessages.CDI_EVENT_LIST_HYPERLINK_OPEN_EVENTS, elementPaths)}));
		regionList.add(new TestRegion(/*1232, 9*/"someEvent",  new TestHyperlink[]{new TestHyperlink(EventListHyperlink.class, CDIExtensionsMessages.CDI_EVENT_LIST_HYPERLINK_OPEN_EVENTS, elementPaths)}));
		regionList.add(new TestRegion(/*1264, 18*/"OversizedException",  new TestHyperlink[]{new TestHyperlink(EventListHyperlink.class, CDIExtensionsMessages.CDI_EVENT_LIST_HYPERLINK_OPEN_EVENTS, elementPaths)}));

		CDIHyperlinkTestUtil.checkRegions(tckProject, "JavaSource/org/jboss/jsr299/tck/tests/event/observer/checkedException/TeaCupPomeranian.java", regionList, new EventAndObserverMethodHyperlinkDetector());
	}

}