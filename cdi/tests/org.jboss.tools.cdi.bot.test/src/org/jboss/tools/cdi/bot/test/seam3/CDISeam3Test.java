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
package org.jboss.tools.cdi.bot.test.seam3;


import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.swtbot.swt.finder.keyboard.Keystrokes;
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
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Test operates on seam3 features using CDI tools
 * 
 * @author Jaroslav Jankovic
 */

@Require(clearProjects = false, perspective = "Java EE", server = @Server(state = ServerState.NotRunning, version = "6.0", operator = ">="))
@RunWith(RequirementAwareSuite.class)
@SuiteClasses({ CDIAllBotTests.class })
public class CDISeam3Test extends CDIBase {

	private static final Logger LOGGER = Logger.getLogger(CDISeam3Test.class.getName());
	private static final String PROJECT_NAME = "CDIProject";
	private static final String PACKAGE_NAME = "cdi";
	private static final String GENERIC_PACKAGE_NAME = "org.cdi.generic";
	private final String genericPoint1 = "MyExtendedConfiguration ";	
	private final String genericPoint2 = "MyConfigurationProducer.getOneConfig()";	
	private final String genericPoint3 = "MyConfigurationProducer.getSecondConfig()";
	
	
	@BeforeClass
	public static void checkAndCreateProject() {
		if (!projectExists(PROJECT_NAME)) {
			createAndCheckCDIProject(bot, util, projectExplorer,PROJECT_NAME);
		}
		addLibrary("seam-solder.jar");
		checkLibrary("seam-solder.jar");
	}
	
	@After
	public void waitForJobs() {
		util.waitForNonIgnoredJobs();
	}
	
	/*
	 * https://issues.jboss.org/browse/JBIDE-8202
	 */	
	@Test
	public void testResourceOpenOn() {
			
		createComponent(CDICOMPONENT.BEANSXML, "beans.xml", PROJECT_NAME + "/WebContent/WEB-INF", null);		
		
		createComponent(CDICOMPONENT.BEAN, "B2", PACKAGE_NAME, null);
		CDIUtil.copyResourceToClass(getEd(), CDIQuickFixTest.class
				.getResourceAsStream("/resources/cdi/B2.java.cdi"), false);
		LOGGER.info("Content of \"B2.java.cdi\" copied to B2");
		openOn("beansXml", "B2.java", "Open Resource");
		String destinationFile = getEd().getTitle();		
		assertTrue("ERROR: redirected to " + destinationFile,
					destinationFile.equals("beans.xml"));

		moveFileInProjectExplorer("beans.xml", PROJECT_NAME + "/WebContent/WEB-INF",
								  PROJECT_NAME + "/WebContent/META-INF");
		LOGGER.info("bean.xml was moved to META-INF");
		
		setEd(bot.swtBotEditorExtByTitle("B2.java"));
		CDIUtil.replaceInEditor(getEd(), bot, "WEB", "META");
		openOn("beansXml", "B2.java", "Open Resource");
		
		destinationFile = getEd().getTitle();
		assertTrue("ERROR: redirected to " + destinationFile,
				   destinationFile.equals("beans.xml"));

	}
	
	/*
	 * https://issues.jboss.org/browse/JBIDE-8692
	 */		
	@Test
	public void testGenericOpenOn() {

		prepareGenericOpenOn();
		
		checkMyBeanInjections();

		checkMyGenericBean();

		checkMyGenericBean2();		
	}
	
	private static void addLibrary(String libraryName) {
		try {
			addLibraryIntoProject(PROJECT_NAME, libraryName);
			LOGGER.info("Library: \"" + libraryName + "\" copied");
			addLibraryToProjectsClassPath(PROJECT_NAME, libraryName);
			LOGGER.info("Library: \"" + libraryName + "\" on class path of project\"" + PROJECT_NAME + "\"");
		} catch (IOException exc) {
			LOGGER.log(Level.SEVERE, "Error while adding seam solder library into project");
		}		
	}
	
	private static void checkLibrary(String libraryName) {
		isLibraryInProjectClassPath(PROJECT_NAME, libraryName);		
	}

	/**
	 * create all necessary components for this test
	 */
	private void prepareGenericOpenOn() {
		/*
		 * injectable beans + qualifiers + generic configuration components
		 */
		createComponent(CDICOMPONENT.BEAN, "MyBean", GENERIC_PACKAGE_NAME, null);
		CDIUtil.copyResourceToClass(getEd(), CDISeam3Test.class
				.getResourceAsStream("/resources/generic/MyBean.java.cdi"), false);
		CDIUtil.replaceInEditor(getEd(), bot, "MyBeanX", "MyBean");
				
		createComponent(CDICOMPONENT.BEAN, "MyBean2", GENERIC_PACKAGE_NAME, null);
		CDIUtil.copyResourceToClass(getEd(), CDISeam3Test.class
				.getResourceAsStream("/resources/generic/MyBean.java.cdi"), false);
		CDIUtil.replaceInEditor(getEd(), bot, "MyBeanX", "MyBean2");
				
		createComponent(CDICOMPONENT.BEAN, "MyBean3", GENERIC_PACKAGE_NAME, null);
		CDIUtil.copyResourceToClass(getEd(), CDISeam3Test.class
				.getResourceAsStream("/resources/generic/MyBean.java.cdi"), false);
		CDIUtil.replaceInEditor(getEd(), bot, "MyBeanX", "MyBean3");
		
		createComponent(CDICOMPONENT.BEAN, "MyConfiguration", GENERIC_PACKAGE_NAME, null);
		CDIUtil.copyResourceToClass(getEd(), CDISeam3Test.class
				.getResourceAsStream("/resources/generic/MyBean.java.cdi"), false);
		CDIUtil.replaceInEditor(getEd(), bot, "MyBeanX", "MyConfiguration");					
		
		createComponent(CDICOMPONENT.BEAN, "MyGenericType", GENERIC_PACKAGE_NAME, null);
		CDIUtil.copyResourceToClass(getEd(), CDISeam3Test.class
				.getResourceAsStream("/resources/generic/MyGenericType.java.cdi"), false);
		
		createComponent(CDICOMPONENT.QUALIFIER, "Qualifier1", GENERIC_PACKAGE_NAME, null);
		createComponent(CDICOMPONENT.QUALIFIER, "Qualifier2", GENERIC_PACKAGE_NAME, null);
		
		createComponent(CDICOMPONENT.BEAN, "MyExtendedConfiguration", GENERIC_PACKAGE_NAME, null);
		CDIUtil.copyResourceToClass(getEd(), CDISeam3Test.class
				.getResourceAsStream("/resources/generic/MyExtendConfig.java.cdi"), false);
		
		createComponent(CDICOMPONENT.BEAN, "MyConfigurationProducer", GENERIC_PACKAGE_NAME, null);
		CDIUtil.copyResourceToClass(getEd(), CDISeam3Test.class
				.getResourceAsStream("/resources/generic/MyConfigProd.java.cdi"), false);			
		
		/*
		 * beans which include atributes suggesting opening all the available 
		 * generic configurations 
		 */
		
		createComponent(CDICOMPONENT.BEAN, "MyBeanInjections", GENERIC_PACKAGE_NAME, null);
		CDIUtil.copyResourceToClass(getEd(), CDISeam3Test.class
				.getResourceAsStream("/resources/generic/MyBeanInjections.java.cdi"), false);
		
		createComponent(CDICOMPONENT.BEAN, "MyGenericBean", GENERIC_PACKAGE_NAME, null);
		CDIUtil.copyResourceToClass(getEd(), CDISeam3Test.class
				.getResourceAsStream("/resources/generic/MyGenericBean.java.cdi"), false);
		
		createComponent(CDICOMPONENT.BEAN, "MyGenericBean2", GENERIC_PACKAGE_NAME, null);
		CDIUtil.copyResourceToClass(getEd(), CDISeam3Test.class
				.getResourceAsStream("/resources/generic/MyGenericBean2.java.cdi"), false);
	}
	
	private void checkMyBeanInjections() {
		checkFirstOpenOnAndGeneric();				
		checkSecondOpenOnAndGeneric();
		checkThirdOpenOnAndGeneric();
	}
			
	private void checkMyGenericBean() {	
		String parameter = "MyConfiguration config";
		String classTitle = "MyGenericBean.java";
		checkAllGenericPointsForAtribute(parameter, classTitle);		
	}

	private void checkMyGenericBean2() {		
		String classTitle = "MyGenericBean2.java";
				
		checkAllGenericPointsForAtribute("MyConfiguration config", classTitle);
		
		checkAllGenericPointsForAtribute("MyBean c", classTitle);
		
		checkAllGenericPointsForAtribute("MyBean2 c2", classTitle);
		
		checkAllGenericPointsForAtribute("MyBean3 c3", classTitle);
		
		checkAllGenericPointsForAtribute("MyBean parameter1", classTitle);		
		
	}
	private void checkFirstOpenOnAndGeneric() {
		checkOpenOnAndGeneric("first1", "MyBeanInjections.java", "Generic Configuration Point", 
				"MyConfigurationProducer.java", "getOneConfig");
		checkOpenOnAndGeneric("first1", "MyBeanInjections.java", "@Inject Bean", 
				"MyGenericBean.java", "createMyFirstBean");	
		
		checkOpenOnAndGeneric("first2", "MyBeanInjections.java", "Generic Configuration Point", 
				"MyConfigurationProducer.java", "getSecondConfig");
		checkOpenOnAndGeneric("first2", "MyBeanInjections.java", "@Inject Bean", 
				"MyGenericBean.java", "createMyFirstBean");
		
		checkOpenOnAndGeneric("first3", "MyBeanInjections.java", "Generic Configuration Point", 
				"MyExtendedConfiguration.java", "MyExtendedConfiguration");
		checkOpenOnAndGeneric("first3", "MyBeanInjections.java", "@Inject Bean", 
				"MyGenericBean.java", "createMyFirstBean");
	}
	
	private void checkSecondOpenOnAndGeneric() {
		checkOpenOnAndGeneric("second1", "MyBeanInjections.java", "Generic Configuration Point", 
				"MyConfigurationProducer.java", "getOneConfig");
		checkOpenOnAndGeneric("second1", "MyBeanInjections.java", "@Inject Bean", 
				"MyGenericBean2.java", "createMySecondBean");	
		
		checkOpenOnAndGeneric("second2", "MyBeanInjections.java", "Generic Configuration Point", 
				"MyConfigurationProducer.java", "getSecondConfig");
		checkOpenOnAndGeneric("second2", "MyBeanInjections.java", "@Inject Bean", 
				"MyGenericBean2.java", "createMySecondBean");
		
		checkOpenOnAndGeneric("second3", "MyBeanInjections.java", "Generic Configuration Point", 
				"MyExtendedConfiguration.java", "MyExtendedConfiguration");
		checkOpenOnAndGeneric("second3", "MyBeanInjections.java", "@Inject Bean", 
				"MyGenericBean2.java", "createMySecondBean");
	}
	
	private void checkThirdOpenOnAndGeneric() {
		checkOpenOnAndGeneric("third1", "MyBeanInjections.java", "Generic Configuration Point", 
				"MyConfigurationProducer.java", "getOneConfig");
		checkOpenOnAndGeneric("third1", "MyBeanInjections.java", "@Inject Bean", 
				"MyGenericBean.java", "myThirdBean");	
		
		checkOpenOnAndGeneric("third2", "MyBeanInjections.java", "Generic Configuration Point", 
				"MyConfigurationProducer.java", "getSecondConfig");
		checkOpenOnAndGeneric("third2", "MyBeanInjections.java", "@Inject Bean", 
				"MyGenericBean.java", "myThirdBean");
		
		checkOpenOnAndGeneric("third3", "MyBeanInjections.java", "Generic Configuration Point", 
				"MyExtendedConfiguration.java", "MyExtendedConfiguration");
		checkOpenOnAndGeneric("third3", "MyBeanInjections.java", "@Inject Bean", 
				"MyGenericBean.java", "myThirdBean");
	}
	
	private void checkOpenOnAndGeneric(String openOnString, String titleName, String chosenOption, 
			String afterOpenOnTitleName, String injectSelectionAtribute) {		
		openOn(openOnString, titleName, chosenOption);
		String activeEditor = bot.activeEditor().getTitle();
		String selectedString = bot.activeEditor().toTextEditor().getSelection();
		assertTrue(activeEditor, activeEditor.equals(afterOpenOnTitleName));
		assertTrue(selectedString, selectedString.equals(injectSelectionAtribute));
	}
	
	private void checkAllGenericPointsForAtribute(String parameter, String classTitle) {		
		openOn(parameter, classTitle, "Show All Generic Configuration Points...");	
		bot.sleep(Timing.time1S());
		SWTBotTable genericPointTable = bot.table(0);
		assertTrue(checkAllGenericConfPoints(genericPointTable));	
		getEd().pressShortcut(Keystrokes.ESC);	
		bot.sleep(Timing.time2S());
	}
	
	private boolean checkAllGenericConfPoints(SWTBotTable genericPointTable) {
		boolean isGenericPoint1Present = false;
		boolean isGenericPoint2Present = false;
		boolean isGenericPoint3Present = false;
		for (int rowIterator = 0; rowIterator < genericPointTable.rowCount(); rowIterator++) {
			String itemInTable = genericPointTable.getTableItem(rowIterator).getText(); 
			if (itemInTable.contains(genericPoint1)) {
				isGenericPoint1Present = true;						
				continue;
			} 
			if (itemInTable.contains(genericPoint2)) {
				isGenericPoint2Present = true;				
				continue;
			}
			if (itemInTable.contains(genericPoint3)) {
				isGenericPoint3Present = true;					
				continue;
			}
		}
		return isGenericPoint1Present && isGenericPoint2Present && isGenericPoint3Present;
	}
	
}
	
