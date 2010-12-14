package org.jboss.tools.seam.ui.bot.test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import org.eclipse.datatools.connectivity.ConnectionProfileException;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.jboss.tools.test.TestProperties;
import org.jboss.tools.ui.bot.ext.SWTJBTExt;
import org.jboss.tools.ui.bot.ext.SWTUtilExt;
import org.jboss.tools.ui.bot.ext.helper.DatabaseHelper;
import org.jboss.tools.ui.bot.ext.types.DriverEntity;
import org.jboss.tools.ui.bot.ext.types.IDELabel;
import org.jboss.tools.ui.bot.test.JBTSWTBotTestCase;
import org.jboss.tools.ui.bot.test.WidgetVariables;

public abstract class TestControl extends JBTSWTBotTestCase{

/*Properties here:*/

	protected static Properties projectProperties;
	protected static Properties jbossEAPRuntime;
	protected static Properties seam12Settings;
	protected static Properties seam2fpSettings;
	protected static Properties seam22Settings;
	
	protected static List<Properties[]> parameters = new Vector<Properties[]>();

	protected SWTUtilExt util = new SWTUtilExt(bot);
	
	private static final String PROJECT_PROPERTIES = "projectProperties.properties";
	private static final String EAP_RUNTIME = "jbossEAPRuntime.properties";
	private static final String SEAM_SET_12 = "seam12Settings.properties";
	private static final String SEAM_SET_2FP = "seam2fpSettings.properties";
	private static final String SEAM_SET_22 = "seam22Settings.properties";

	
	public static String JBOSS_EAP_HOME;
	public static String SEAM_12_SETTINGS_HOME;
	public static String SEAM_22_SETTINGS_HOME;
	public static String SEAM_2FP_SETTINGS_HOME;	

	static {
	  
	  Properties vmArgsProps = SWTUtilExt.parseEclipseVMArgs();
	  
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
			SWTUtilExt.overrideValueFromSystemProperty(jbossEAPRuntime,"runtimePath","-Djboss.tools.test.jboss.home",vmArgsProps);
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
			SWTUtilExt.overrideValueFromSystemProperty(seam12Settings,"seamRuntimePath","-Djboss.tools.test.seam.1.2.1.eap.home",vmArgsProps);
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
			SWTUtilExt.overrideValueFromSystemProperty(seam2fpSettings,"seamRuntimePath","-Djboss.tools.test.seam.2fp.eap.home",vmArgsProps);
		} catch (IOException e) {
			fail("Can't load properties from " + SEAM_SET_2FP + " file");		
		}
		catch (IllegalStateException e) {
			fail("Property file " + SEAM_SET_2FP + " was not found");
		}
		try {
			InputStream is = TestControl.class.getResourceAsStream("/" + SEAM_SET_22);
			seam22Settings = new TestProperties();
			seam22Settings.load(is);
			SWTUtilExt.overrideValueFromSystemProperty(seam22Settings,"seamRuntimePath","-Djboss.tools.test.seam.2.2.0.eap.home",vmArgsProps);
		} catch (IOException e) {
			fail("Can't load properties from " + SEAM_SET_22 + " file");		
		}
		catch (IllegalStateException e) {
			fail("Property file " + SEAM_SET_22 + " was not found");
		}
		JBOSS_EAP_HOME = jbossEAPRuntime.getProperty("runtimePath");
		SEAM_12_SETTINGS_HOME = seam12Settings.getProperty("seamRuntimePath");
		SEAM_22_SETTINGS_HOME = seam22Settings.getProperty("seamRuntimePath");
		SEAM_2FP_SETTINGS_HOME = seam2fpSettings.getProperty("seamRuntimePath");
		createConnectionProfile(seam22Settings.getProperty("seamRuntimePath"),"DefaultDS");
		
		//parameters.add(new Properties[] {seam12Settings});
		parameters.add(new Properties[] {seam22Settings});
		//parameters.add(new Properties[] {seam2fpSettings});
				
	}
/*Pre-launch operations here:*/

	@Override
	protected void activePerspective() {
		if (!bot.perspectiveByLabel("Seam").isActive()) {
			bot.perspectiveByLabel("Seam").activate();
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

	public static List<Properties[]> getParameters() {
		return parameters;
	}

	public static Properties getJbossEAPRuntime() {
		return jbossEAPRuntime;
	}

	public static Properties getProjectProperties() {
		return projectProperties;
	}
	
	/**
	 * Creates connection profile in case it's not defined yet
	 * @param pathToSeamRuntime
	 * @param profileName
	 */
	private static void createConnectionProfile(String pathToSeamRuntime, String profileName){
	  
	  StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(pathToSeamRuntime);
    stringBuilder.append(File.separator);
    stringBuilder.append("lib");
    stringBuilder.append(File.separator);
    stringBuilder.append("hsqldb.jar");
    
    try {
      DriverEntity entity = new DriverEntity();
      entity.setDrvPath(stringBuilder.toString());
      entity.setJdbcString("jdbc:hsqldb:hsql://localhost/xdb");
      DatabaseHelper.createDriver(entity);
    } catch (ConnectionProfileException e) {
      fail("Unable to create HSQL Driver" + e);     
    }
    
	}
}
