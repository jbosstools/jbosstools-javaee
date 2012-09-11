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

import org.eclipse.swtbot.swt.finder.widgets.SWTBotTable;
import org.jboss.tools.cdi.bot.test.CDIConstants;
import org.jboss.tools.cdi.bot.test.annotations.CDIWizardType;
import org.jboss.tools.ui.bot.ext.Timing;
import org.junit.Test;

/**
 * Test operates on hyperlinks-openons using CDI support
 * 
 * @author Jaroslav Jankovic
 * 
 */

public class OpenOnTest extends OpenOnBase {
	
	@Test
	public void testBeanInjectOpenOn() {

		prepareInjectedPointsComponents();
		
		String injectOption = null;
		for (int i = 1; i < 12; i++) {
			String injectPoint = "myBean" + i;
			injectOption = CDIConstants.SHOW_ALL_ASSIGNABLE;			
			if (i > 8) injectOption = "Open @Inject Bean";			
			checkInjectedPoint(injectPoint, injectOption);
		}
		
	}
	
	// https://issues.jboss.org/browse/JBIDE-7025	 
	@Test
	public void testBeansXMLClassesOpenOn() {
		
		beansHelper.createEmptyBeansXML(getProjectName());
				
		assertTrue(checkBeanXMLDecoratorOpenOn(getPackageName(), "D1"));
		
		assertTrue(checkBeanXMLInterceptorOpenOn(getPackageName(), "I1"));
		
		assertTrue(checkBeanXMLAlternativeOpenOn(getPackageName(), "A1"));
		
	}
	
	// https://issues.jboss.org/browse/JBIDE-6251	
	@Test
	public void testDisposerProducerOpenOn() {
		
		String className = "Bean1";
		
		wizard.createCDIComponentWithContent(CDIWizardType.BEAN, className, 
				getPackageName(), null, "/resources/openon/BeanWithDisposerAndProducer.java.cdi");
		
		editResourceUtil.replaceInEditor("BeanComponent", className);
		
		bot.sleep(Timing.time2S());
		
		openOnUtil.openOnByOption("disposeMethod", className + ".java", "Open Bound Producer");
		assertTrue(getEd().toTextEditor().getSelection().equals("produceMethod"));
		
		openOnUtil.openOnByOption("produceMethod", className + ".java", "Open Bound Disposer");
		assertTrue(getEd().toTextEditor().getSelection().equals("disposeMethod"));		
	}
	
	@Test
	public void testObserverOpenOn() {		
		wizard.createCDIComponentWithContent(CDIWizardType.BEAN, "EventBean", 
				getPackageName(), null, "/resources/openon/EventBean.java.cdi");

		wizard.createCDIComponentWithContent(CDIWizardType.BEAN, "ObserverBean", 
				getPackageName(), null, "/resources/openon/ObserverBean.java.cdi");
		
		bot.editorByTitle("EventBean.java").show();
		setEd(bot.activeEditor().toTextEditor());
		editResourceUtil.replaceInEditor(" event", " event");
		
		openOnUtil.openOnByOption("observerMethod", "ObserverBean.java", "Open CDI Event");
		assertTrue(getEd().toTextEditor().getSelection().equals("event"));
		
		openOnUtil.openOnByOption("Event<ObserverBean> event", "EventBean.java", "Open CDI Observer Method");
		assertTrue(getEd().toTextEditor().getSelection().equals("observerMethod"));				
	}
	
	private void prepareInjectedPointsComponents() {
		wizard.createCDIComponent(CDIWizardType.QUALIFIER, "Q1", getPackageName(), null);
		
		wizard.createCDIComponent(CDIWizardType.QUALIFIER, "Q2", getPackageName(), null);
		
		wizard.createCDIComponent(CDIWizardType.BEAN, "MyBean1", getPackageName(), null);
		
		wizard.createCDIComponentWithContent(CDIWizardType.BEAN, "MyBean2", 
				getPackageName(), null, "/resources/openon/InjectedPoints/MyBean2.java.cdi");
		
		wizard.createCDIComponentWithContent(CDIWizardType.BEAN, "MyBean3", 
				getPackageName(), null, "/resources/openon/InjectedPoints/MyBean3.java.cdi");
		
		wizard.createCDIComponentWithContent(CDIWizardType.BEAN, "MyBean4", 
				getPackageName(), null, "/resources/openon/InjectedPoints/MyBean4.java.cdi");
		
		wizard.createCDIComponentWithContent(CDIWizardType.BEAN, "MyBean5",
				getPackageName(), null, "/resources/openon/InjectedPoints/MyBean5.java.cdi");
		
		wizard.createCDIComponentWithContent(CDIWizardType.BEAN, "MainBean", 
				getPackageName(), null, "/resources/openon/InjectedPoints/MainBean.java.cdi");
		
		util.waitForNonIgnoredJobs();
		eclipse.cleanAllProjects();
		
	}
	
	private void checkInjectedPoint(String injectedPoint, String option) {
		openOnUtil.openOnByOption(injectedPoint, "MainBean.java", option);
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