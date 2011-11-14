/*******************************************************************************
 * Copyright (c) 2010 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.cdi.bot.test.openon;

import java.util.logging.Logger;

import org.eclipse.swtbot.swt.finder.widgets.SWTBotTable;
import org.jboss.tools.cdi.bot.test.CDIAllBotTests;
import org.jboss.tools.cdi.bot.test.uiutils.actions.CDIBase;
import org.jboss.tools.cdi.bot.test.uiutils.actions.CDIUtil;
import org.jboss.tools.ui.bot.ext.RequirementAwareSuite;
import org.jboss.tools.ui.bot.ext.Timing;
import org.jboss.tools.ui.bot.ext.config.Annotations.Require;
import org.jboss.tools.ui.bot.ext.config.Annotations.Server;
import org.jboss.tools.ui.bot.ext.config.Annotations.ServerState;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Test operates on CDI perspective
 * 
 * @author Jaroslav Jankovic
 */

@Require(clearProjects = true, perspective = "Java EE", server = @Server(state = ServerState.NotRunning, version = "6.0", operator = ">="))
@RunWith(RequirementAwareSuite.class)
@SuiteClasses({ CDIAllBotTests.class })
public class CDIFindObserverForEventTest extends CDIBase {

	private static final Logger LOGGER = Logger.getLogger(CDIFindObserverForEventTest.class.getName());
	private final String[] events = { "myBean1Q1Event", "myBean1AnyEvent",
			"myBean2Q1Event", "myBean2AnyEvent", "myBean1Q2Event",
			"myBean2Q2Event", "myBean1Q1Event.fire(new MyBean1());",
			"myBean1AnyEvent.fire(new MyBean1())",
			"myBean2Q1Event.fire(new MyBean2())",
			"myBean2AnyEvent.fire(new MyBean2())",
			"myBean1Q2Event.fire(new MyBean1())",
			"myBean2Q2Event.fire(new MyBean2())",
			"myBean1AnyEvent.fire(new MyBean2())" };
	private final String[] observers = { "observeNoQualifierMyBean1",
			"observeAnyMyBean1", "observeQ1MyBean1",
			"observeNoQualifierMyBean2", "observeAnyMyBean2",
			"observeQ1MyBean2", "observeQ2MyBean1", "observeQ2MyBean2" };

	@Override
	public String getProjectName() {
		return "CDIObserverTest";
	}
	
	@Test
	public void testSimpleCaseObserverFinding() {

		prepareSimpleObserverFinding();

		testSimpleObserverFinding(); 

	}

	// not implemented yet
	@Test
	public void testComplexCaseObserverFinding() {

		prepareComplexObserverFinding();

		testComplexObserverFinding();
	}

	private void prepareSimpleObserverFinding() {

		createComponent(CDICOMPONENT.QUALIFIER, "Q1", getPackageName(), null);

		createComponent(CDICOMPONENT.QUALIFIER, "Q2", getPackageName(), null);

		createComponent(CDICOMPONENT.BEAN, "MyBean1", getPackageName(), null);
		CDIUtil.copyResourceToClass(getEd(), CDIFindObserverForEventTest.class
				.getResourceAsStream("/resources/events/MyBean1.java.cdi"),
				false);

		createComponent(CDICOMPONENT.BEAN, "MyBean2", getPackageName(), null);
		CDIUtil.copyResourceToClass(getEd(), CDIFindObserverForEventTest.class
				.getResourceAsStream("/resources/events/MyBean2.java.cdi"),
				false);

		createComponent(CDICOMPONENT.BEAN, "EventsProducer", getPackageName(), null);
		CDIUtil.copyResourceToClass(
				getEd(),
				CDIFindObserverForEventTest.class
						.getResourceAsStream("/resources/events/EventsProducer.java.cdi"),
				false);

		createComponent(CDICOMPONENT.BEAN, "ObserverBean", getPackageName(), null);
		CDIUtil.copyResourceToClass(
				getEd(),
				CDIFindObserverForEventTest.class
						.getResourceAsStream("/resources/events/ObserverBean.java.cdi"),
				false);
		util.waitForNonIgnoredJobs();
	}

	private void testSimpleObserverFinding() {

		
		for (int i = 0; i < events.length; i++) {			
			checkObserverMethodsForEvent(events[i]);
		}
		

		for (int i = 0; i < observers.length; i++) {
			checkEventsForObserverMethods(observers[i]);
		}
	}

	private void checkObserverMethodsForEvent(String eventName) {
		
		String eventsClass = "EventsProducer.java";
		
		String showObserverOption = "Show CDI Observer Methods...";
		
		checkEventsAndObserver(eventName, eventsClass, showObserverOption);
		
	}

	private void checkEventsForObserverMethods(String observerName) {
		
		String observerClass = "ObserverBean.java";
		
		/**
		 * there are two observer methods for which there is only one
		 * event, so there will be no "Show CDI Events" option, instead
		 * of that, there will be "Open CDI Event" option
		 */
		String showObserverOption = ((observerName.equals("observeQ1MyBean2")) || 
						(observerName.equals("observeQ2MyBean2"))) ? "Open CDI Event" : "Show CDI Events...";
		
		checkEventsAndObserver(observerName, observerClass, showObserverOption);
	}

	private void checkEventsAndObserver(String name, String className,
			String option) {
		openOn(name, className, option);
		bot.sleep(Timing.time1S());
		if (option.equals("Open CDI Event")) {
			if (name.equals("observeQ1MyBean2")) {
				LOGGER.info("Testing observer: observeQ1MyBean2 started");
				assertTrue(getEd().toTextEditor().getSelection().equals("myBean2Q1Event"));
				LOGGER.info("Testing observer: observeQ1MyBean2 ended");
			}else {  
				//observeQ1MyBean2
				LOGGER.info("Testing observer: observeQ1MyBean2 started");
				assertTrue(getEd().toTextEditor().getSelection().equals("myBean2Q2Event"));
				LOGGER.info("Testing observer: observeQ1MyBean2 ended");
			}			
		} else {
			SWTBotTable observerTable = bot.table(0);
			if (className.equals("EventsProducer.java")) {
				assertTrue(checkAllObserverMethodsForEvent(name, observerTable)); 
			}
			if (className.equals("ObserverBean.java")) {
				assertTrue(checkAllEventsForObserverMethod(name, observerTable)); 
			}
		}
	}

	private boolean checkAllObserverMethodsForEvent(String eventName, 
			SWTBotTable observerTable) {
		String observerClass = "ObserverBean";
		String packageProjectPath = getPackageName() + " - /" + getProjectName() + "/src";
		String parametrizedEventItem = observerClass + ".observeXXX() - " + packageProjectPath;
		boolean allObserversFound = false;		
		for (int i = 0; i < events.length; i++) {
			if (eventName.equals(events[i])) {
				LOGGER.info("Testing event: " + events[i] + " started");
				switch (i) {
				//myBean1Q1Event
				//myBean1Q1Event.fire(new MyBean1())
				case 0: 
				case 6:
					if (observerTable.containsItem(parametrizedEventItem.replace("XXX", "Q1MyBean1")) &&
						observerTable.containsItem(parametrizedEventItem.replace("XXX", "AnyMyBean1")) &&
						observerTable.containsItem(parametrizedEventItem.replace("XXX", "NoQualifierMyBean1"))) {
						allObserversFound = true;
					}
					break;
				//myBean1AnyEvent
				//myBean1AnyEvent.fire(new MyBean1())	
				//myBean1AnyEvent.fire(new MyBean2())				
				case 1:
				case 7:
				case 12:
					if (observerTable.containsItem(parametrizedEventItem.replace("XXX", "AnyMyBean1")) &&
						observerTable.containsItem(parametrizedEventItem.replace("XXX", "NoQualifierMyBean1"))) {
						allObserversFound = true;
					}
					break;
				//myBean2Q1Event
				//myBean2Q1Event.fire(new MyBean2())
				case 2:
				case 8:
					if (observerTable.containsItem(parametrizedEventItem.replace("XXX", "Q1MyBean1")) &&
						observerTable.containsItem(parametrizedEventItem.replace("XXX", "Q1MyBean2")) &&
						observerTable.containsItem(parametrizedEventItem.replace("XXX", "AnyMyBean1")) &&
						observerTable.containsItem(parametrizedEventItem.replace("XXX", "AnyMyBean2")) &&
						observerTable.containsItem(parametrizedEventItem.replace("XXX", "NoQualifierMyBean1")) &&						
						observerTable.containsItem(parametrizedEventItem.replace("XXX", "NoQualifierMyBean2"))) {
						allObserversFound = true;
					}
					break;
				//myBean2AnyEvent	
				//myBean2AnyEvent.fire(new MyBean2())				
				case 3:
				case 9:
					if (observerTable.containsItem(parametrizedEventItem.replace("XXX", "AnyMyBean1")) &&
						observerTable.containsItem(parametrizedEventItem.replace("XXX", "AnyMyBean2")) &&
						observerTable.containsItem(parametrizedEventItem.replace("XXX", "NoQualifierMyBean1")) &&
						observerTable.containsItem(parametrizedEventItem.replace("XXX", "NoQualifierMyBean2"))) {
						allObserversFound = true;
					}
					break;	
				//myBean1Q2Event	
				//myBean1Q2Event.fire(new MyBean1())				
				case 4:
				case 10:
					if (observerTable.containsItem(parametrizedEventItem.replace("XXX", "Q2MyBean1")) &&
						observerTable.containsItem(parametrizedEventItem.replace("XXX", "AnyMyBean1")) &&
						observerTable.containsItem(parametrizedEventItem.replace("XXX", "NoQualifierMyBean1"))) {
						allObserversFound = true;
					}
					break;
				//myBean2Q2Event	
				//myBean2Q2Event.fire(new MyBean2())
				case 5:
				case 11:
					if (observerTable.containsItem(parametrizedEventItem.replace("XXX", "Q2MyBean1")) &&
						observerTable.containsItem(parametrizedEventItem.replace("XXX", "Q2MyBean2")) &&
						observerTable.containsItem(parametrizedEventItem.replace("XXX", "AnyMyBean1")) &&
						observerTable.containsItem(parametrizedEventItem.replace("XXX", "AnyMyBean2")) &&
						observerTable.containsItem(parametrizedEventItem.replace("XXX", "NoQualifierMyBean1")) &&						
						observerTable.containsItem(parametrizedEventItem.replace("XXX", "NoQualifierMyBean2"))) {
						allObserversFound = true;
					}
					break;
				}
				LOGGER.info("Testing event: " + events[i] + " ended");
				break;
			}
		}
		return allObserversFound;
	}

	private boolean checkAllEventsForObserverMethod(String observerName,
			SWTBotTable eventsTable) {
		String eventsClass = "EventsProducer";
		String packageProjectPath = getPackageName() + " - /" + getProjectName() + "/src";
		String parametrizedEventItem = eventsClass + ".myBeanXXX - " + packageProjectPath;
		boolean allEventsFound = false;		
		for (int i = 0; i < observers.length; i++) {
			if (observerName.equals(observers[i])) {
				LOGGER.info("Testing observer: " + observers[i] + " started");
				switch (i) {
				//observeNoQualifierMyBean1		
				//observeAnyMyBean1
				case 0:
				case 1:
					if (eventsTable.containsItem(parametrizedEventItem.replace("XXX", "1Q1Event")) &&
						eventsTable.containsItem(parametrizedEventItem.replace("XXX", "2Q1Event")) &&						
						eventsTable.containsItem(parametrizedEventItem.replace("XXX", "1AnyEvent")) &&
						eventsTable.containsItem(parametrizedEventItem.replace("XXX", "2AnyEvent")) &&						
						eventsTable.containsItem(parametrizedEventItem.replace("XXX", "1Q2Event")) &&
						eventsTable.containsItem(parametrizedEventItem.replace("XXX", "2Q2Event"))) {
						allEventsFound = true;
					}
				//observeQ1MyBean1
				case 2:
					if (eventsTable.containsItem(parametrizedEventItem.replace("XXX", "1Q1Event")) &&
						eventsTable.containsItem(parametrizedEventItem.replace("XXX", "2Q1Event"))) {
						allEventsFound = true;
					}
					break;
				//observeNoQualifierMyBean2	
				//observeAnyMyBean2	
				case 3:
				case 4:
					if (eventsTable.containsItem(parametrizedEventItem.replace("XXX", "2Q1Event")) &&
						eventsTable.containsItem(parametrizedEventItem.replace("XXX", "2AnyEvent")) &&
						eventsTable.containsItem(parametrizedEventItem.replace("XXX", "2Q2Event"))) {
						allEventsFound = true;
						}
					break;	
				//observeQ1MyBean2	
				case 5:
					throw new IllegalStateException("Observer method \"observeQ1MyBean2\" should " +
							"have been tested earlier!!");							
				//observeQ2MyBean1
				case 6:
					if (eventsTable.containsItem(parametrizedEventItem.replace("XXX", "1Q2Event")) &&
						eventsTable.containsItem(parametrizedEventItem.replace("XXX", "2Q2Event"))) {
						allEventsFound = true;
						}
					break;
				//observeQ2MyBean2	
				case 7:	
					throw new IllegalStateException("Observer method \"observeQ2MyBean2\" should " +
							"have been tested earlier!!");					
				}
				LOGGER.info("Testing observer: " + observers[i] + " ended");
				break;
			}
		}
		return allEventsFound;
	}

	// not implemented yet
	private void prepareComplexObserverFinding() {

	}

	// not implemented yet
	private void testComplexObserverFinding() {
		/**
		 * main idea - check events which have multiple qualifiers defined
		 * (http://docs.jboss.org/weld/reference/1.0.0/en-US/html/events.html -
		 * 11.6) - check events with qualifiers which has members
		 * (http://docs.jboss.org/weld/reference/1.0.0/en-US/html/events.html -
		 * 11.5)
		 */
	}
}
