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

import org.eclipse.swtbot.swt.finder.widgets.SWTBotTable;
import org.jboss.tools.cdi.bot.test.CDIAllBotTests;
import org.jboss.tools.cdi.bot.test.CDITestBase;
import org.jboss.tools.cdi.bot.test.annotations.CDIWizardType;
import org.jboss.tools.ui.bot.ext.RequirementAwareSuite;
import org.jboss.tools.ui.bot.ext.Timing;
import org.jboss.tools.ui.bot.ext.config.Annotations.Require;
import org.jboss.tools.ui.bot.ext.config.Annotations.Server;
import org.jboss.tools.ui.bot.ext.config.Annotations.ServerState;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Test operates on seam3 features using CDI tools
 * 
 * @author Jaroslav Jankovic
 */

@Require(clearProjects = true, perspective = "Java EE", server = @Server(state = ServerState.NotRunning, version = "6.0", operator = ">="))
@RunWith(RequirementAwareSuite.class)
@SuiteClasses({ CDIAllBotTests.class })
public class Seam3Test extends CDITestBase {

	private static final Logger LOGGER = Logger.getLogger(Seam3Test.class.getName());
	private final String genericPoint1 = "MyExtendedConfiguration ";	
	private final String genericPoint2 = "MyConfigurationProducer.getOneConfig()";	
	private final String genericPoint3 = "MyConfigurationProducer.getSecondConfig()";
	
	
	@Override
	public void checkAndCreateProject() {
		if (!projectHelper.projectExists(getProjectName())) {
			projectHelper.createCDIProject(getProjectName());
			addLibrary("seam-solder.jar");
			checkLibrary("seam-solder.jar");
		}
	}
	
	@Override
	public String getProjectName() {
		return "CDISeam3Test";
	}
		
	private String getGenericPackageName() {
		return "org.cdi.generic";
	}
	

	/**
	 * https://issues.jboss.org/browse/JBIDE-8202
	 */	
	@Test
	public void testResourceOpenOn() {
			
		String className = "Bean1";
		
		wizard.createCDIComponent(CDIWizardType.BEANS_XML, "beans.xml", getProjectName() + "/WebContent/WEB-INF", null);		
		
		wizard.createCDIComponent(CDIWizardType.BEAN, className, getPackageName(), null);
		editResourceUtil.replaceClassContentByResource(Seam3Test.class
				.getResourceAsStream("/resources/seam3/Bean.java.cdi"), false);
		editResourceUtil.replaceInEditor("BeanComponent", className);		
		
		openOnUtil.openOnByOption("beansXml", className + ".java", "Open Resource");
		
		String destinationFile = getEd().getTitle();		
		assertTrue("ERROR: redirected to " + destinationFile,
					destinationFile.equals("beans.xml"));

		editResourceUtil.moveFileInProjectExplorer("beans.xml", getProjectName() + "/WebContent/WEB-INF",
								  getProjectName() + "/WebContent/META-INF");
		LOGGER.info("bean.xml was moved to META-INF");
		
		setEd(bot.swtBotEditorExtByTitle(className + ".java"));
		editResourceUtil.replaceInEditor("WEB", "META");
		openOnUtil.openOnByOption("beansXml", className + ".java", "Open Resource");
		
		destinationFile = getEd().getTitle();
		assertTrue("ERROR: redirected to " + destinationFile,
				   destinationFile.equals("beans.xml"));

	}
	
	/**
	 * https://issues.jboss.org/browse/JBIDE-8692
	 */		
	@Test
	public void testGenericOpenOn() {

		prepareGenericOpenOn();
		
		checkMyBeanInjections();

		checkMyGenericBean();

		checkMyGenericBean2();		
	}
	
	private void addLibrary(String libraryName) {
		try {
			libraryUtil.addLibraryIntoProject(getProjectName(), libraryName);			
			LOGGER.info("Library: \"" + libraryName + "\" copied");
			util.waitForNonIgnoredJobs();
			libraryUtil.addLibraryToProjectsClassPath(getProjectName(), libraryName);
			LOGGER.info("Library: \"" + libraryName + "\" on class path of project\"" + getProjectName() + "\"");
		} catch (IOException exc) {
			LOGGER.log(Level.SEVERE, "Error while adding seam solder library into project");
		}		
	}
	
	private void checkLibrary(String libraryName) {
		assertTrue(libraryUtil.isLibraryInProjectClassPath(getProjectName(), libraryName));		
	}

	/**
	 * wizard.create all necessary components for this test
	 */
	private void prepareGenericOpenOn() {
		/**
		 * injectable beans + qualifiers + generic configuration components
		 */
		wizard.createCDIComponent(CDIWizardType.BEAN, "MyBean", getGenericPackageName(), null);
		editResourceUtil.replaceClassContentByResource(Seam3Test.class
				.getResourceAsStream("/resources/generic/MyBean.java.cdi"), false);
		editResourceUtil.replaceInEditor("MyBeanX", "MyBean");
				
		wizard.createCDIComponent(CDIWizardType.BEAN, "MyBean2", getGenericPackageName(), null);
		editResourceUtil.replaceClassContentByResource(Seam3Test.class
				.getResourceAsStream("/resources/generic/MyBean.java.cdi"), false);
		editResourceUtil.replaceInEditor("MyBeanX", "MyBean2");
				
		wizard.createCDIComponent(CDIWizardType.BEAN, "MyBean3", getGenericPackageName(), null);
		editResourceUtil.replaceClassContentByResource(Seam3Test.class
				.getResourceAsStream("/resources/generic/MyBean.java.cdi"), false);
		editResourceUtil.replaceInEditor("MyBeanX", "MyBean3");
		
		wizard.createCDIComponent(CDIWizardType.BEAN, "MyConfiguration", getGenericPackageName(), null);
		editResourceUtil.replaceClassContentByResource(Seam3Test.class
				.getResourceAsStream("/resources/generic/MyBean.java.cdi"), false);
		editResourceUtil.replaceInEditor("MyBeanX", "MyConfiguration");					
		
		wizard.createCDIComponent(CDIWizardType.BEAN, "MyGenericType", getGenericPackageName(), null);
		editResourceUtil.replaceClassContentByResource(Seam3Test.class
				.getResourceAsStream("/resources/generic/MyGenericType.java.cdi"), false);
		
		wizard.createCDIComponent(CDIWizardType.QUALIFIER, "Qualifier1", getGenericPackageName(), null);
		wizard.createCDIComponent(CDIWizardType.QUALIFIER, "Qualifier2", getGenericPackageName(), null);
		
		wizard.createCDIComponent(CDIWizardType.BEAN, "MyExtendedConfiguration", getGenericPackageName(), null);
		editResourceUtil.replaceClassContentByResource(Seam3Test.class
				.getResourceAsStream("/resources/generic/MyExtendConfig.java.cdi"), false);
		
		wizard.createCDIComponent(CDIWizardType.BEAN, "MyConfigurationProducer", getGenericPackageName(), null);
		editResourceUtil.replaceClassContentByResource(Seam3Test.class
				.getResourceAsStream("/resources/generic/MyConfigProd.java.cdi"), false);			
		
		/**
		 * beans which include atributes suggesting opening all the available 
		 * generic configurations 
		 */
		
		wizard.createCDIComponent(CDIWizardType.BEAN, "MyBeanInjections", getGenericPackageName(), null);
		editResourceUtil.replaceClassContentByResource(Seam3Test.class
				.getResourceAsStream("/resources/generic/MyBeanInjections.java.cdi"), false);
		
		wizard.createCDIComponent(CDIWizardType.BEAN, "MyGenericBean", getGenericPackageName(), null);
		editResourceUtil.replaceClassContentByResource(Seam3Test.class
				.getResourceAsStream("/resources/generic/MyGenericBean.java.cdi"), false);
		
		wizard.createCDIComponent(CDIWizardType.BEAN, "MyGenericBean2", getGenericPackageName(), null);
		editResourceUtil.replaceClassContentByResource(Seam3Test.class
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
		openOnUtil.openOnByOption(openOnString, titleName, chosenOption);
		String activeEditor = bot.activeEditor().getTitle();
		String selectedString = bot.activeEditor().toTextEditor().getSelection();
		assertTrue(activeEditor, activeEditor.equals(afterOpenOnTitleName));
		assertTrue(selectedString, selectedString.equals(injectSelectionAtribute));
	}
	
	private void checkAllGenericPointsForAtribute(String parameter, String classTitle) {		
		openOnUtil.openOnByOption(parameter, classTitle, "Show All Generic Configuration Points...");	
		bot.sleep(Timing.time1S());
		SWTBotTable genericPointTable = bot.table(0);
		assertTrue(checkAllGenericConfPoints(genericPointTable));	
		//getEd().pressShortcut(Keystrokes.ESC);	
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
	
