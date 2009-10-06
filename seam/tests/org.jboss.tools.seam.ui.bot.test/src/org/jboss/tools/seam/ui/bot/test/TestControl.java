package org.jboss.tools.seam.ui.bot.test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.eclipse.core.runtime.Platform;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.jboss.tools.test.TestProperties;
import org.jboss.tools.ui.bot.test.JBTSWTBotTestCase;
import org.jboss.tools.ui.bot.test.WidgetVariables;

public abstract class TestControl extends JBTSWTBotTestCase{

/*Properties here:*/

	protected static Properties projectProperties;
	protected static Properties jbossEAPRuntime;
	protected static Properties seam12Settings;
	protected static Properties seam2fpSettings;
	protected static Properties seam21Settings;
	
	private static String PROJECT_PROPERTIES = "projectProperties.properties";
	private static String EAP_RUNTIME = "jbossEAPRuntime.properties";
	private static String SEAM_SET_12 = "seam12Settings.properties";
	private static String SEAM_SET_2FP = "seam2fpSettings.properties";
	private static String SEAM_SET_21 = "seam21Settings.properties";
	
	public static String JBOSS_EAP_HOME;
	public static String SEAM_12_SETTINGS_HOME;
	public static String SEAM_21_SETTINGS_HOME;
	public static String SEAM_2FP_SETTINGS_HOME;	

	public static final String[] SUBSTITUTE_PROPERTIES = {
		"jbosstools.test.jboss.home",
		"jbosstools.test.seam.1.2.1.eap.home",
		"jbosstools.test.seam.2.1.0.GA.home",
		"jbosstools.test.seam.2fp.eap.home"
	};
	
	static {
		try {
			InputStream is = TestControl.class.getResourceAsStream("/" + PROJECT_PROPERTIES);
			projectProperties = new TestProperties();
			projectProperties.load(is);
		} catch (IOException e) {
			fail("Can't load properties from " + PROJECT_PROPERTIES + " file");		
		}
		catch (IllegalStateException e) {
			fail("Property file " + PROJECT_PROPERTIES + " was not found");
		}
		try {
			InputStream is = TestControl.class.getResourceAsStream("/" + EAP_RUNTIME);
			jbossEAPRuntime = new TestProperties();
			jbossEAPRuntime.load(is);
		} catch (IOException e) {
			fail("Can't load properties from " + EAP_RUNTIME + " file");		
		}
		catch (IllegalStateException e) {
			fail("Property file " + EAP_RUNTIME + " was not found");
		}
		try {
			InputStream is = TestControl.class.getResourceAsStream("/" + SEAM_SET_12);
			seam12Settings = new TestProperties();
			seam12Settings.load(is);
		} catch (IOException e) {
			fail("Can't load properties from " + SEAM_SET_12 + " file");		
		}
		catch (IllegalStateException e) {
			fail("Property file " + SEAM_SET_12 + " was not found");
		}
		try {
			InputStream is = TestControl.class.getResourceAsStream("/" + SEAM_SET_2FP);
			seam2fpSettings = new TestProperties();
			seam2fpSettings.load(is);
		} catch (IOException e) {
			fail("Can't load properties from " + SEAM_SET_2FP + " file");		
		}
		catch (IllegalStateException e) {
			fail("Property file " + SEAM_SET_2FP + " was not found");
		}
		try {
			InputStream is = TestControl.class.getResourceAsStream("/" + SEAM_SET_21);
			seam21Settings = new TestProperties();
			seam21Settings.load(is);
		} catch (IOException e) {
			fail("Can't load properties from " + SEAM_SET_21 + " file");		
		}
		catch (IllegalStateException e) {
			fail("Property file " + SEAM_SET_21 + " was not found");
		}
		JBOSS_EAP_HOME = System.getProperty("jbosstools.test.jboss.home",jbossEAPRuntime.getProperty("runtimePath"));
		SEAM_12_SETTINGS_HOME = System.getProperty("jbosstools.test.seam.1.2.1.eap.home",seam12Settings.getProperty("seamRuntimePath"));
	
		//Property SEAM_21_SETTINGS_HOME should be deleted or commented. There is no such property on hudson
		SEAM_21_SETTINGS_HOME = System.getProperty("jbosstools.test.seam.home.2.1",seam21Settings.getProperty("seamRuntimePath"));

		
		SEAM_2FP_SETTINGS_HOME = System.getProperty("jbosstools.test.seam.2.0.1.GA.home",seam2fpSettings.getProperty("seamRuntimePath"));	
	}
	
	
	
	
/*Pre-launch operations here:*/

	@Override
	protected void activePerspective() {
		if (!bot.perspectiveByLabel("Seam").isActive()) {
			bot.perspectiveByLabel("Seam").activate();
		}
		
	}
	
	private static void substituteSystemProperties(Properties projectProperties2) {
		for (Object opject : projectProperties2.keySet()) {
			String propertyValue = projectProperties2.get(opject).toString();
			if(propertyValue.matches("\\$\\{.*")) {
				for (String substitute : SUBSTITUTE_PROPERTIES) {
					String regexp = "\\$\\{" + substitute + "}";
					if(propertyValue.matches(regexp)) {
						projectProperties2.put(opject, propertyValue.replaceAll(regexp, System.getProperty(substitute)));
					}
				}
			}
		}
	}

	/*protected void setUp() throws Exception {
		super.setUp();
		bot.viewByTitle(projectProperties.getProperty("jbossServerView")).setFocus();
		SWTBot innerBot = bot.viewByTitle(projectProperties.getProperty("jbossServerView")).bot();
		SWTBotTree tree = innerBot.tree();
		try {
			tree.getTreeItem(jbossEAPRuntime.getProperty("serverName")+"  [Stopped]");
		} catch (WidgetNotFoundException e) {
			createServerRuntime(jbossEAPRuntime);
		}
	}*/

/*Predefined methods here:*/

public static String TYPE_WAR = "WAR";
public static String TYPE_EAR = "EAR";	

/**Creates any Server Runtime + Server. */
	protected void createServerRuntime(Properties serverType){
		bot.menu("File").menu("New").menu("Other...").click();
		SWTBotTree tree = bot.tree();
		tree.expandNode("Server").select("Server");
		bot.button("Next >").click();
		SWTBotTree tree2 = bot.tree();
		tree2.expandNode(serverType.getProperty("runtimeGroup")).select(serverType.getProperty("runtimeItem"));
		bot.textWithLabel("Server name:").setText(serverType.getProperty("serverName"));
		bot.button("Next >").click();
		bot.textWithLabel("Name").setText(serverType.getProperty("runtimeName"));
		bot.textWithLabel("Home Directory").setText(serverType.getProperty("runtimePath"));
		bot.button("Finish").click();
	}

/** Creates any Seam runtime.	*/
	protected void createSeamRuntime(Properties runtimeSet, String homeFolder){
		bot.menu("Window").menu("Preferences").click();
		SWTBotTree tree = bot.tree();
		tree.expandNode("JBoss Tools").expandNode("Web").select("Seam");
		bot.button("Add").click();
		bot.textWithLabel("Home Folder:").setText(homeFolder);
		bot.textWithLabel("Name:").setText(runtimeSet.getProperty("seamRuntimeName"));
		bot.button("Finish").click();
		bot.button("OK").click();
	}

/**Creates any Seam project.	*/
	protected void createSeamProject(Properties runtimeSet, Properties serverType, 
			String type	){
		bot.menu("File").menu("New").menu("Seam Web Project").click();
		bot.textWithLabel("Project name:").setText(runtimeSet.getProperty("testProjectName")+ type);
		bot.comboBoxInGroup("Target runtime").setSelection(serverType.getProperty("runtimeName"));
		bot.comboBoxInGroup("Target Server").setSelection(serverType.getProperty("serverName"));
		bot.comboBoxInGroup("Configuration").setSelection(runtimeSet.getProperty("configName"));
		bot.button("Next >").click();
		bot.button("Next >").click();
		bot.button("Next >").click();
		bot.button("Next >").click();
		bot.comboBoxWithLabel("Seam Runtime:").setSelection(runtimeSet.getProperty("seamRuntimeName"));
		bot.radio(type).click();
		bot.comboBoxWithLabel("Connection profile:").setSelection(projectProperties.getProperty("connName"));
		bot.button("Finish").click();
		bot.waitUntil(Conditions.shellCloses(bot.activeShell()),15000);
	}
	
/**Creates any Seam Action, Form etc.	*/
	protected void createSeamUnit(String unitType, 
			Properties runtimeSet, String type){
		bot.menu("File").menu("New").menu("Seam " +unitType).click();
		SWTBotShell shell = bot.activeShell();
		bot.textWithLabel("Seam Project:").setText(runtimeSet.getProperty("testProjectName")+ type);
		if ("Entity".equals(unitType)) {
			bot.textWithLabel("Seam entity class name:").setText("seam"+unitType);	
		} else {
			bot.textWithLabel("Seam component name:").setText("seam"+unitType);
		}
		bot.button("Finish").click();
		bot.waitUntil(Conditions.shellCloses(shell),15000);
	}

/**Deletes any Seam project.	*/
	protected void deleteSeamProject(Properties runtimeSet, String type){
		SWTBot innerBot = bot.viewByTitle(WidgetVariables.PACKAGE_EXPLORER).bot();
		SWTBotTree tree = innerBot.tree();
		if ("WAR".equals(type)) {
			tree.getTreeItem(runtimeSet.getProperty("testProjectName")+ type).contextMenu("Delete").click();
			bot.button("OK").click();
			bot.sleep(1000);
			tree.getTreeItem(runtimeSet.getProperty("testProjectName")+ type+"-test").contextMenu("Delete").click();
			bot.button("OK").click();
			bot.sleep(1000);
		} else {
			tree.getTreeItem(runtimeSet.getProperty("testProjectName")+ type).contextMenu("Delete").click();
			bot.button("OK").click();
			bot.sleep(1000);
			tree.getTreeItem(runtimeSet.getProperty("testProjectName")+ type+"-ear").contextMenu("Delete").click();
			bot.button("OK").click();
			bot.sleep(1000);
			tree.getTreeItem(runtimeSet.getProperty("testProjectName")+ type+"-ejb").contextMenu("Delete").click();
			bot.button("OK").click();
			bot.sleep(1000);
			tree.getTreeItem(runtimeSet.getProperty("testProjectName")+ type+"-test").contextMenu("Delete").click();
			bot.button("OK").click();
			bot.sleep(1000);
		}
	}
	
}
