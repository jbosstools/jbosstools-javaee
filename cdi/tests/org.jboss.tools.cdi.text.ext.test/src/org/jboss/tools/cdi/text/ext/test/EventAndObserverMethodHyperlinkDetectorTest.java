package org.jboss.tools.cdi.text.ext.test;

import java.util.ArrayList;

import org.jboss.tools.cdi.text.ext.CDIExtensionsMessages;
import org.jboss.tools.cdi.text.ext.hyperlink.EventAndObserverMethodHyperlinkDetector;
import org.jboss.tools.cdi.text.ext.hyperlink.EventListHyperlink;
import org.jboss.tools.cdi.text.ext.hyperlink.ObserverMethodListHyperlink;

public class EventAndObserverMethodHyperlinkDetectorTest extends HyperlinkDetectorTest {

	public void testEventHyperlinkDetector() throws Exception {
		ArrayList<TestRegion> regionList = new ArrayList<TestRegion>();
		
		regionList.add(new TestRegion(959, 6,   new TestHyperlink[]{new TestHyperlink(ObserverMethodListHyperlink.class, CDIExtensionsMessages.CDI_EVENT_LIST_HYPERLINK_OPEN_OBSERVER_METHODS)}));
		regionList.add(new TestRegion(967, 16,  new TestHyperlink[]{new TestHyperlink(ObserverMethodListHyperlink.class, CDIExtensionsMessages.CDI_EVENT_LIST_HYPERLINK_OPEN_OBSERVER_METHODS)}));
		regionList.add(new TestRegion(985, 11,  new TestHyperlink[]{new TestHyperlink(ObserverMethodListHyperlink.class, CDIExtensionsMessages.CDI_EVENT_LIST_HYPERLINK_OPEN_OBSERVER_METHODS)}));
		regionList.add(new TestRegion(1006, 6,  new TestHyperlink[]{new TestHyperlink(ObserverMethodListHyperlink.class, CDIExtensionsMessages.CDI_EVENT_LIST_HYPERLINK_OPEN_OBSERVER_METHODS)}));
		regionList.add(new TestRegion(1014, 3,  new TestHyperlink[]{new TestHyperlink(ObserverMethodListHyperlink.class, CDIExtensionsMessages.CDI_EVENT_LIST_HYPERLINK_OPEN_OBSERVER_METHODS)}));
		regionList.add(new TestRegion(1019, 34, new TestHyperlink[]{new TestHyperlink(ObserverMethodListHyperlink.class, CDIExtensionsMessages.CDI_EVENT_LIST_HYPERLINK_OPEN_OBSERVER_METHODS)}));
		regionList.add(new TestRegion(1055, 42, new TestHyperlink[]{new TestHyperlink(ObserverMethodListHyperlink.class, CDIExtensionsMessages.CDI_EVENT_LIST_HYPERLINK_OPEN_OBSERVER_METHODS)}));
		regionList.add(new TestRegion(1107, 6,  new TestHyperlink[]{new TestHyperlink(ObserverMethodListHyperlink.class, CDIExtensionsMessages.CDI_EVENT_LIST_HYPERLINK_OPEN_OBSERVER_METHODS)}));
		regionList.add(new TestRegion(1115, 34, new TestHyperlink[]{new TestHyperlink(ObserverMethodListHyperlink.class, CDIExtensionsMessages.CDI_EVENT_LIST_HYPERLINK_OPEN_OBSERVER_METHODS)}));
		regionList.add(new TestRegion(1151, 36, new TestHyperlink[]{new TestHyperlink(ObserverMethodListHyperlink.class, CDIExtensionsMessages.CDI_EVENT_LIST_HYPERLINK_OPEN_OBSERVER_METHODS)}));
		regionList.add(new TestRegion(1188, 3,  new TestHyperlink[]{new TestHyperlink(ObserverMethodListHyperlink.class, CDIExtensionsMessages.CDI_EVENT_LIST_HYPERLINK_OPEN_OBSERVER_METHODS)}));
		regionList.add(new TestRegion(1235, 11, new TestHyperlink[]{new TestHyperlink(ObserverMethodListHyperlink.class, CDIExtensionsMessages.CDI_EVENT_LIST_HYPERLINK_OPEN_OBSERVER_METHODS)}));
		regionList.add(new TestRegion(1334, 42, new TestHyperlink[]{new TestHyperlink(ObserverMethodListHyperlink.class, CDIExtensionsMessages.CDI_EVENT_LIST_HYPERLINK_OPEN_OBSERVER_METHODS)}));

		checkRegions("JavaSource/org/jboss/jsr299/tck/tests/event/bindingTypes/EventEmitter.java", regionList, new EventAndObserverMethodHyperlinkDetector());
	}

	public void testObserverMethodHyperlinkDetector() throws Exception {
		ArrayList<TestRegion> regionList = new ArrayList<TestRegion>();
		regionList.add(new TestRegion(1196, 4,  new TestHyperlink[]{new TestHyperlink(EventListHyperlink.class, CDIExtensionsMessages.CDI_EVENT_LIST_HYPERLINK_OPEN_EVENTS)}));
		regionList.add(new TestRegion(1201, 13, new TestHyperlink[]{new TestHyperlink(EventListHyperlink.class, CDIExtensionsMessages.CDI_EVENT_LIST_HYPERLINK_OPEN_EVENTS)}));
		regionList.add(new TestRegion(1216, 15, new TestHyperlink[]{new TestHyperlink(EventListHyperlink.class, CDIExtensionsMessages.CDI_EVENT_LIST_HYPERLINK_OPEN_EVENTS)}));
		regionList.add(new TestRegion(1232, 9,  new TestHyperlink[]{new TestHyperlink(EventListHyperlink.class, CDIExtensionsMessages.CDI_EVENT_LIST_HYPERLINK_OPEN_EVENTS)}));
		regionList.add(new TestRegion(1264, 18, new TestHyperlink[]{new TestHyperlink(EventListHyperlink.class, CDIExtensionsMessages.CDI_EVENT_LIST_HYPERLINK_OPEN_EVENTS)}));

		checkRegions("JavaSource/org/jboss/jsr299/tck/tests/event/observer/checkedException/TeaCupPomeranian.java", regionList, new EventAndObserverMethodHyperlinkDetector());
	}

}