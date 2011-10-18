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

import org.jboss.tools.cdi.bot.test.CDIAllBotTests;
import org.jboss.tools.cdi.bot.test.quickfix.CDIQuickFixTest;
import org.jboss.tools.cdi.bot.test.uiutils.actions.CDIBase;
import org.jboss.tools.cdi.bot.test.uiutils.actions.CDIUtil;
import org.jboss.tools.ui.bot.ext.RequirementAwareSuite;
import org.jboss.tools.ui.bot.ext.config.Annotations.Require;
import org.jboss.tools.ui.bot.ext.config.Annotations.Server;
import org.jboss.tools.ui.bot.ext.config.Annotations.ServerState;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

/*
 * Test operates on hyperlinks-openons using CDI tools
 * 
 * @author Jaroslav Jankovic
 * 
 * 
 * TO DO 
 * 
 * - Classes indication for Open Injected Class works
 * - https://issues.jboss.org/browse/JBIDE-6179
 * 
 * 
 */

@Require(perspective = "Java EE", server = @Server(state = ServerState.NotRunning, version = "6.0", operator = ">="))
@RunWith(RequirementAwareSuite.class)
@SuiteClasses({ CDIAllBotTests.class })
public class CDIOpenOnTest extends CDIBase {

	private static final Logger LOGGER = Logger.getLogger(CDIQuickFixTest.class.getName());
	private static final String PROJECT_NAME = "CDIProject3";
	private static final String PACKAGE_NAME = "org.cdi.test";
	
	@After
	public void waitForJobs() {
		util.waitForNonIgnoredJobs();
	}

	@Test
	public void testCreateProject() {
		createAndCheckCDIProject(bot, util, projectExplorer, PROJECT_NAME);
	}
	
	@Test
	public void testBeanInjectOpenOn() {

		createComponent(CDICOMPONENT.BEAN, "Animal", PACKAGE_NAME, null);

		createComponent(CDICOMPONENT.BEAN, "BrokenFarm", PACKAGE_NAME, null);

		CDIUtil.copyResourceToClass(getEd(), CDIOpenOnTest.class
				.getResourceAsStream("/resources/cdi/BrokenFarm.java.cdi"),
				false);
		LOGGER.info("Content of \"BrokenFarm.java.cdi\" copied to BrokenFarm");
		openOn("@Inject", "BrokenFarm.java", "@Inject");
		assertTrue("ERROR: redirected to " + getEd().getTitle(), getEd()
				.getTitle().equals("Animal.java"));
	}
	
	/*
	 * https://issues.jboss.org/browse/JBIDE-7025
	 */
	@Test
	public void testBeansXMLClassesOpenOn() {
		
		/*
		 * check if beans.xml was not created in previous tests. If so, I cannot create 
		 * beans.xml into PROJECT_NAME/WebContent/WEB-INF/beans.xml.
		 */
		if (!projectExplorer.isFilePresent(PROJECT_NAME, "WebContent/META-INF/beans.xml") && 
			!projectExplorer.isFilePresent(PROJECT_NAME, "WebContent/WEB-INF/beans.xml")) {
			createComponent(CDICOMPONENT.BEANSXML, null, PROJECT_NAME + "/WebContent/WEB-INF", null);			
		}
		
		createComponent(CDICOMPONENT.DECORATOR, "D1", PACKAGE_NAME,
				"java.util.Set");
		bot.editorByTitle("beans.xml").show();
		bot.cTabItem("Source").activate();
		openOn(PACKAGE_NAME + ".D1", "beans.xml", null);
		assertTrue("ERROR: redirected to " + getEd(),
				getEd().getTitle().equals("D1.java"));
		
		createComponent(CDICOMPONENT.INTERCEPTOR, "Interceptor1", PACKAGE_NAME,
				null);
		bot.editorByTitle("beans.xml").show();
		openOn(PACKAGE_NAME + ".Interceptor1", "beans.xml", null);
		assertTrue("ERROR: redirected to " + getEd(),
					getEd().getTitle().equals("Interceptor1.java"));
		
		createComponent(CDICOMPONENT.BEAN, "B1", PACKAGE_NAME,
				"alternative+beansxml");
		bot.editorByTitle("beans.xml").show();
		openOn(PACKAGE_NAME + ".B1", "beans.xml", null);
		assertTrue("ERROR: redirected to " + getEd(),
					getEd().getTitle().equals("B1.java"));
		
		createComponent(CDICOMPONENT.STEREOSCOPE, "S1", PACKAGE_NAME,
				"alternative+beansxml");
		bot.editorByTitle("beans.xml").show();
		openOn(PACKAGE_NAME + ".S1", "beans.xml", null);
		assertTrue("ERROR: redirected to " + getEd(),
					getEd().getTitle().equals("S1.java"));		
		
	}
	
	/*
	 * https://issues.jboss.org/browse/JBIDE-6251
	 */
	@Test
	public void testDisposerOpenOn() {
		/*
		 * not implemented yet
		 */
		
	}
	
	/*
	 * https://issues.jboss.org/browse/JBIDE-6311
	 * https://issues.jboss.org/browse/JBIDE-6251
	 * https://issues.jboss.org/browse/JBIDE-5928
	 */
	@Test
	public void testProducerOpenOn() {
		/*
		 * not implemented yet
		 */
		
	}
	
	@Test
	public void testObserverOpenOn() {
		/*
		 * not implemented yet
		 * 
		 * Bean 1
		 * 	@Inject
		 *	@Qualifier1
		 *	Event<MyBean> event2;
		 *
		 * Bean 2
		 *  void myObserver(@Observes MyBean bean) {
		 *
		 *	}
		 * 
		 */
		
	}
	
	
}