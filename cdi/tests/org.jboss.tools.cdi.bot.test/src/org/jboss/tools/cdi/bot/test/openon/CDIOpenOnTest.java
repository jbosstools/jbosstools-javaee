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
import org.jboss.tools.cdi.bot.test.quickfix.CDIQuickFixTest;
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
 * Test operates on hyperlinks-openons using CDI tools
 * 
 * @author Jaroslav Jankovic
 * 
 */

@Require(clearProjects = true, perspective = "Java EE", server = @Server(state = ServerState.NotRunning, version = "6.0", operator = ">="))
@RunWith(RequirementAwareSuite.class)
@SuiteClasses({ CDIAllBotTests.class })
public class CDIOpenOnTest extends CDIBase {

	private static final Logger LOGGER = Logger.getLogger(CDIQuickFixTest.class.getName());	
		
	@Override
	public String getProjectName() {
		return "CDIOpenOnTest";
	}
				
	@Test
	public void testBeanInjectOpenOn() {

		prepareInjectedPointsComponents();
		
		testInjectedPoints();
		
	}
		
	/*
	 * https://issues.jboss.org/browse/JBIDE-7025
	 */	
	@Test
	public void testBeansXMLClassesOpenOn() {
		
		/*
		 * check if beans.xml was not created in previous tests. If so, I cannot create 
		 * beans.xml into getProjectName()/WebContent/WEB-INF/beans.xml.
		 */
		if (!projectExplorer.isFilePresent(getProjectName(), "WebContent/META-INF/beans.xml") && 
			!projectExplorer.isFilePresent(getProjectName(), "WebContent/WEB-INF/beans.xml")) {
			createComponent(CDICOMPONENT.BEANSXML, null, getProjectName() + "/WebContent/WEB-INF", null);			
		}
		
		createComponent(CDICOMPONENT.DECORATOR, "D1", getPackageName(),
				"java.util.Set");
		bot.editorByTitle("beans.xml").show();
		bot.cTabItem("Source").activate();
		openOn(getPackageName() + ".D1", "beans.xml", null);
		assertTrue("ERROR: redirected to " + getEd().getTitle(),
				getEd().getTitle().equals("D1.java"));
		
		createComponent(CDICOMPONENT.INTERCEPTOR, "Interceptor1", getPackageName(),
				null);
		bot.editorByTitle("beans.xml").show();
		openOn(getPackageName() + ".Interceptor1", "beans.xml", null);
		assertTrue("ERROR: redirected to " + getEd(),
					getEd().getTitle().equals("Interceptor1.java"));
		
		createComponent(CDICOMPONENT.BEAN, "B1", getPackageName(),
				"alternative+beansxml");
		bot.editorByTitle("beans.xml").show();
		openOn(getPackageName() + ".B1", "beans.xml", null);
		assertTrue("ERROR: redirected to " + getEd(),
					getEd().getTitle().equals("B1.java"));
		
		createComponent(CDICOMPONENT.STEREOSCOPE, "S1", getPackageName(),
				"alternative+beansxml");
		bot.editorByTitle("beans.xml").show();
		openOn(getPackageName() + ".S1", "beans.xml", null);
		assertTrue("ERROR: redirected to " + getEd(),
					getEd().getTitle().equals("S1.java"));		
		
	}
	
	/*
	 * https://issues.jboss.org/browse/JBIDE-6251
	 */	
	@Test
	public void testDisposerProducerOpenOn() {
		
		String testedBean = "DisposerProducerBean";
		createComponent(CDICOMPONENT.BEAN, "MyBean", getPackageName(), null);
		createComponent(CDICOMPONENT.BEAN, testedBean, getPackageName(), null);
		CDIUtil.copyResourceToClass(getEd(), CDIOpenOnTest.class
				.getResourceAsStream("/resources/cdi/" + testedBean + ".java.cdi"),
				false);
		openOn("disposeMethod", testedBean + ".java", "Open Bound Producer");
		assertTrue(getEd().toTextEditor().getSelection().equals("produceMethod"));
		
		openOn("produceMethod", testedBean + ".java", "Open Bound Disposer");
		assertTrue(getEd().toTextEditor().getSelection().equals("disposeMethod"));		
	}
	
	@Test
	public void testObserverOpenOn() {		
		createComponent(CDICOMPONENT.BEAN, "EventBean", getPackageName(), null);
		CDIUtil.copyResourceToClass(getEd(), CDIOpenOnTest.class
				.getResourceAsStream("/resources/cdi/EventBean.java.cdi"),
				false);
		createComponent(CDICOMPONENT.BEAN, "ObserverBean", getPackageName(), null);
		CDIUtil.copyResourceToClass(getEd(), CDIOpenOnTest.class
				.getResourceAsStream("/resources/cdi/ObserverBean.java.cdi"),
				false);	
		
		bot.editorByTitle("EventBean.java").show();
		setEd(bot.activeEditor().toTextEditor());
		CDIUtil.replaceInEditor(getEd(), bot, " event", " event");
		
		openOn("observerMethod", "ObserverBean.java", "Open CDI Event");
		assertTrue(getEd().toTextEditor().getSelection().equals("event"));
		
		openOn("Event<ObserverBean> event", "EventBean.java", "Open CDI Observer Method");
		assertTrue(getEd().toTextEditor().getSelection().equals("observerMethod"));				
	}
	
	private void prepareInjectedPointsComponents() {
		createComponent(CDICOMPONENT.QUALIFIER, "Q1", getPackageName(), null);
		
		createComponent(CDICOMPONENT.QUALIFIER, "Q2", getPackageName(), null);
		
		createComponent(CDICOMPONENT.BEAN, "MyBean1", getPackageName(), null);
		
		createComponent(CDICOMPONENT.BEAN, "MyBean2", getPackageName(), null);
		CDIUtil.copyResourceToClass(getEd(), CDIOpenOnTest.class
				.getResourceAsStream("/resources/injectedPoints/MyBean2.java.cdi"),
				false);
		
		createComponent(CDICOMPONENT.BEAN, "MyBean3", getPackageName(), null);
		CDIUtil.copyResourceToClass(getEd(), CDIOpenOnTest.class
				.getResourceAsStream("/resources/injectedPoints/MyBean3.java.cdi"),
				false);
		
		createComponent(CDICOMPONENT.BEAN, "MyBean4", getPackageName(), null);
		CDIUtil.copyResourceToClass(getEd(), CDIOpenOnTest.class
				.getResourceAsStream("/resources/injectedPoints/MyBean4.java.cdi"),
				false);
		
		createComponent(CDICOMPONENT.BEAN, "MyBean5", getPackageName(), null);
		CDIUtil.copyResourceToClass(getEd(), CDIOpenOnTest.class
				.getResourceAsStream("/resources/injectedPoints/MyBean5.java.cdi"),
				false);
		
		createComponent(CDICOMPONENT.BEAN, "MainBean", getPackageName(), null);
		CDIUtil.copyResourceToClass(getEd(), CDIOpenOnTest.class
				.getResourceAsStream("/resources/injectedPoints/MainBean.java.cdi"),
				false);
	}
	
	private void testInjectedPoints() {
		String injectOption = null;
		for (int i = 1; i < 12; i++) {
			injectOption = "Show All Assignable Beans...";
			String injectPoint = "myBean" + i;
			if (i > 8) injectOption = "Open @Inject Bean";			
			checkInjectedPoint(injectPoint, injectOption);
		}
		
	}
	
	private void checkInjectedPoint(String injectedPoint, String option) {
		openOn(injectedPoint, "MainBean.java", option);
		bot.sleep(Timing.time1S());
		if (option.equals("Open @Inject Bean")) {
			LOGGER.info("Testing injected point: \"" + injectedPoint + "\" started");
			assertTrue(getEd().getTitle().equals("MyBean4.java"));
			assertTrue(getEd().toTextEditor().getSelection().equals("MyBean4"));
			LOGGER.info("Testing injected point: \"" + injectedPoint + "\" ended");
		} else {
			SWTBotTable assignBeans = bot.table(0);			
			assertTrue(checkAllAssignBeans(injectedPoint, assignBeans)); 
		}
	}

	private boolean checkAllAssignBeans(String injectedPoint,
			SWTBotTable assignBeans) {
		String packageProjectPath = getPackageName() + " - /" + getProjectName() + "/src";
		String paramAssignBean = "XXX - " + packageProjectPath;
		String prodInjPoint = "@Produces MyBean3.getMyBeanXXX()";
		boolean allassignBeans = false;	
		String indexOfInjPoint = injectedPoint.split("myBean")[1];
		int intIndexOfInjPoint = Integer.parseInt(indexOfInjPoint);
		LOGGER.info("Testing injected point: \"" + injectedPoint + "\" started");
		switch (intIndexOfInjPoint) {
		case 1:				
			if (assignBeans.containsItem(paramAssignBean.replace("XXX", "MyBean1")) &&
				assignBeans.containsItem(paramAssignBean.replace("XXX", "MyBean2")) &&
				assignBeans.containsItem(paramAssignBean.replace(
						"XXX", prodInjPoint.replace("XXX", "1")))&& 
				assignBeans.containsItem(paramAssignBean.replace(
						"XXX", prodInjPoint.replace("XXX", "1WithIMB2")))&&
				assignBeans.containsItem(paramAssignBean.replace(
						"XXX", prodInjPoint.replace("XXX", "2")))) {
				allassignBeans = true;
			}
			break;
		case 2:
			if (assignBeans.containsItem(paramAssignBean.replace("XXX", "MyBean2")) &&
				assignBeans.containsItem(paramAssignBean.replace(
						"XXX", prodInjPoint.replace("XXX", "2")))) {
				allassignBeans = true;
			}
			break;
		case 3:
			if (assignBeans.containsItem(paramAssignBean.replace("XXX", "MyBean4")) &&
				assignBeans.containsItem(paramAssignBean.replace(
						"XXX", prodInjPoint.replace("XXX", "1WithQ1")))&& 
				assignBeans.containsItem(paramAssignBean.replace(
						"XXX", prodInjPoint.replace("XXX", "1WithIMB2Q1")))&&
				assignBeans.containsItem(paramAssignBean.replace(
						"XXX", prodInjPoint.replace("XXX", "2WithQ1")))) {
				allassignBeans = true;
			}	
			break;
		case 4:
			if (assignBeans.containsItem(paramAssignBean.replace("XXX", "MyBean4")) &&					
				assignBeans.containsItem(paramAssignBean.replace(
						"XXX", prodInjPoint.replace("XXX", "2WithQ1")))) {
				allassignBeans = true;
			}					
			break;
		case 5:
			if (assignBeans.containsItem(paramAssignBean.replace("XXX", "MyBean4")) &&					
				assignBeans.containsItem(paramAssignBean.replace("XXX", "MyBean5")) &&
				assignBeans.containsItem(paramAssignBean.replace(
						"XXX", prodInjPoint.replace("XXX", "1WithQ2"))) &&
				assignBeans.containsItem(paramAssignBean.replace(
						"XXX", prodInjPoint.replace("XXX", "2WithQ2"))) && 
				assignBeans.containsItem(paramAssignBean.replace(
						"XXX", prodInjPoint.replace("XXX", "1WithIMB2Q2")))) {
				allassignBeans = true;
			}	
			break;
		case 6:
			if (assignBeans.containsItem(paramAssignBean.replace("XXX", "MyBean4")) &&					
				assignBeans.containsItem(paramAssignBean.replace("XXX", "MyBean5")) &&
				assignBeans.containsItem(paramAssignBean.replace(
						"XXX", prodInjPoint.replace("XXX", "2WithQ2")))) {
				allassignBeans = true;
			}	
			break;
		case 7:
			if (assignBeans.containsItem(paramAssignBean.replace("XXX", "MyBean1")) &&
				assignBeans.containsItem(paramAssignBean.replace("XXX", "MyBean2")) &&
				assignBeans.containsItem(paramAssignBean.replace("XXX", "MyBean4")) &&					
				assignBeans.containsItem(paramAssignBean.replace("XXX", "MyBean5")) &&
				assignBeans.containsItem(paramAssignBean.replace(
						"XXX", prodInjPoint.replace("XXX", "1"))) &&
				assignBeans.containsItem(paramAssignBean.replace(
						"XXX", prodInjPoint.replace("XXX", "1WithIMB2"))) &&
				assignBeans.containsItem(paramAssignBean.replace(
						"XXX", prodInjPoint.replace("XXX", "1WithIMB2Q1"))) &&
				assignBeans.containsItem(paramAssignBean.replace(
						"XXX", prodInjPoint.replace("XXX", "1WithIMB2Q2"))) &&
				assignBeans.containsItem(paramAssignBean.replace(
						"XXX", prodInjPoint.replace("XXX", "1WithQ1"))) &&
				assignBeans.containsItem(paramAssignBean.replace(
						"XXX", prodInjPoint.replace("XXX", "1WithQ2"))) &&
				assignBeans.containsItem(paramAssignBean.replace(
						"XXX", prodInjPoint.replace("XXX", "2"))) && 
				assignBeans.containsItem(paramAssignBean.replace(
						"XXX", prodInjPoint.replace("XXX", "2WithQ1"))) &&
				assignBeans.containsItem(paramAssignBean.replace(
						"XXX", prodInjPoint.replace("XXX", "2WithQ2")))) {
				allassignBeans = true;
			}
			break;
		case 8:
			if (assignBeans.containsItem(paramAssignBean.replace("XXX", "MyBean2")) &&
				assignBeans.containsItem(paramAssignBean.replace("XXX", "MyBean4")) &&					
				assignBeans.containsItem(paramAssignBean.replace("XXX", "MyBean5")) &&
				assignBeans.containsItem(paramAssignBean.replace(
						"XXX", prodInjPoint.replace("XXX", "2"))) &&
				assignBeans.containsItem(paramAssignBean.replace(
						"XXX", prodInjPoint.replace("XXX", "2WithQ1"))) &&
				assignBeans.containsItem(paramAssignBean.replace(
						"XXX", prodInjPoint.replace("XXX", "2WithQ2")))) {
				allassignBeans = true;
			}	
			break;
		case 9:
		case 10:
		case 11:			
			throw new IllegalStateException("Injection Point \"" + injectedPoint + "\" should " +
					"have been tested earlier!!");							
		}	
		LOGGER.info("Testing injected point: \"" + injectedPoint + "\" ended");
		return allassignBeans;		
	}
	
}