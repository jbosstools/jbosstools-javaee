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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Test operates on hyperlinks-openons using CDI tools
 * 
 * @author Jaroslav Jankovic
 * 
 * 
 * TO DO 
 * 
 * - Classes indication for Open Injected Class works
 * 
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

		createComponent(CDICOMPONENT.BEAN, "Animal", getPackageName(), null);

		createComponent(CDICOMPONENT.BEAN, "BrokenFarm", getPackageName(), null);

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
		createComponent(CDICOMPONENT.QUALIFIER, "Q1", getPackageName(), null);
		createComponent(CDICOMPONENT.BEAN, "MyBean3", getPackageName(), null);
		CDIUtil.copyResourceToClass(getEd(), CDIOpenOnTest.class
				.getResourceAsStream("/resources/cdi/MyBean3.java.cdi"),
				false);
		createComponent(CDICOMPONENT.BEAN, "MyBean4", getPackageName(), null);
		CDIUtil.copyResourceToClass(getEd(), CDIOpenOnTest.class
				.getResourceAsStream("/resources/cdi/MyBean4.java.cdi"),
				false);	
		
		bot.editorByTitle("MyBean3.java").show();
		setEd(bot.activeEditor().toTextEditor());
		CDIUtil.replaceInEditor(getEd(), bot, " event", " event");
		
		openOn("observerMethod", "MyBean4.java", "Open CDI Event");
		assertTrue(getEd().toTextEditor().getSelection().equals("event"));
		
		openOn("Event<MyBean4> event", "MyBean3.java", "Open CDI Observer Method");
		assertTrue(getEd().toTextEditor().getSelection().equals("observerMethod"));				
	}
	
	
}