package org.jboss.tools.seam.ui.bot.test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTable;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.jboss.tools.test.TestProperties;
import org.jboss.tools.ui.bot.ext.types.IDELabel;
import org.jboss.tools.ui.bot.ext.types.IDELabel.PreferencesDialog;
import org.jboss.tools.ui.bot.test.JBTSWTBotTestCase;
import org.jboss.tools.ui.bot.test.WidgetVariables;

public abstract class TestControl extends JBTSWTBotTestCase{

/*Properties here:*/

	protected static Properties projectProperties;
	protected static Properties jbossEAPRuntime;
	protected static Properties seam12Settings;
	protected static Properties seam2fpSettings;
	protected static Properties seam22Settings;
	
	private static final String PROJECT_PROPERTIES = "projectProperties.properties";
	private static final String EAP_RUNTIME = "jbossEAPRuntime.properties";
	private static final String SEAM_SET_12 = "seam12Settings.properties";
	private static final String SEAM_SET_2FP = "seam2fpSettings.properties";
	private static final String SEAM_SET_22 = "seam22Settings.properties";
	protected static final String VALIDATION = "Validation";
	protected static final String DEPLOY_SOURCE = "Deploying datasource to server";
	protected static final String REG_IN_SERVER = "Register in server";
	
	public static String JBOSS_EAP_HOME;
	public static String SEAM_12_SETTINGS_HOME;
	public static String SEAM_22_SETTINGS_HOME;
	public static String SEAM_2FP_SETTINGS_HOME;	

	static {
	  
	  Properties vmArgsProps = TestControl.parseEclipseVMArgs();
	  
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
			TestControl.overrideValueFromSystemProperty(jbossEAPRuntime,"runtimePath","-Djboss.tools.test.jboss.home",vmArgsProps);
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
			TestControl.overrideValueFromSystemProperty(seam12Settings,"seamRuntimePath","-Djboss.tools.test.seam.1.2.1.eap.home",vmArgsProps);
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
			TestControl.overrideValueFromSystemProperty(seam2fpSettings,"seamRuntimePath","-Djboss.tools.test.seam.2fp.eap.home",vmArgsProps);
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
			TestControl.overrideValueFromSystemProperty(seam22Settings,"seamRuntimePath","-Djboss.tools.test.seam.2.2.0.eap.home",vmArgsProps);
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

/**Creates any Server Runtime + Server. */
	protected void createServerRuntime(Properties serverType){
	  // Check if server is not already defined
	  bot.menu(IDELabel.Menu.WINDOW).menu(IDELabel.Menu.PREFERENCES).click();
    bot.shell(IDELabel.Shell.PREFERENCES).activate();
    bot.tree().expandNode(IDELabel.PreferencesDialog.SERVER_GROUP).select(
      PreferencesDialog.RUNTIME_ENVIRONMENTS);
    SWTBotTable tbRuntimeEnvironments = bot.table();
    boolean createRuntime = true;
    int numRows = tbRuntimeEnvironments.rowCount();
    if (numRows > 0) {
      int currentRow = 0;
      while (createRuntime && currentRow < numRows) {
        if (tbRuntimeEnvironments.cell(currentRow, 0).equalsIgnoreCase(
            serverType.getProperty("runtimeName"))) {
          createRuntime = false;
        } else {
          currentRow++;
        }
      }
    }
    bot.button(IDELabel.Button.OK).click();
    
	  if (createRuntime){
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
	}

/** Creates any Seam runtime.	*/
	protected void createSeamRuntime(Properties runtimeSet, String homeFolder){
		bot.menu("Window").menu("Preferences").click();
		SWTBotTree tree = bot.tree();
		delay();
		tree.expandNode("JBoss Tools")
			.expandNode("Web")
			.expandNode("Seam")
			.select();
		
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
	/**
	 * Overrides propertyName property value within properties with value stored within vmargProperties with name vmargPropertyName
	 * @param properties
	 * @param propertyName
	 * @param vmargPropertyName
	 * @param vmargProperties
	 */
	private static void overrideValueFromSystemProperty (Properties properties, String propertyName , 
    String vmargPropertyName, Properties vmargProperties){
	  
	  String vmargProperty = vmargProperties.getProperty(vmargPropertyName);
	  if (vmargProperty != null){
	    properties.setProperty(propertyName, vmargProperty);
	  }
	  
	}
	/**
	 * Returns Properties which contains Virtual Machine arguments
	 * with name starting with "-D"
	 * @return
	 */
	private static Properties parseEclipseVMArgs (){
    
	  Properties vmArgsProps = new Properties();
    
	  String vmArgs = System.getProperty("eclipse.vmargs");
	  
	  if (vmArgs != null){
	    for (String line : vmArgs.split("\n")){
	      if (line.startsWith("-D")){
	        String[] splitLine = line.split("=");
	        vmArgsProps.setProperty(splitLine[0], splitLine[1]);
	      }
	    }
	  }
    
    return vmArgsProps;
    
	}
}
