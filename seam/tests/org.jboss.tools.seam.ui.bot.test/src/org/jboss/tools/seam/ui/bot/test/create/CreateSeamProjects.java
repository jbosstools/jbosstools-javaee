package org.jboss.tools.seam.ui.bot.test.create;

import static org.eclipse.swtbot.swt.finder.waits.Conditions.shellCloses;

import java.util.Properties;

import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotBrowser;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.jboss.tools.seam.ui.bot.test.AbstractSeamTestBase;
import org.jboss.tools.seam.ui.bot.test.EARTests;
import org.jboss.tools.seam.ui.bot.test.TestControl;
import org.jboss.tools.seam.ui.bot.test.WARTests;
import org.jboss.tools.ui.bot.ext.SWTBotExt;
import org.jboss.tools.ui.bot.ext.SWTJBTExt;
import org.jboss.tools.ui.bot.ext.SWTOpenExt;
import org.jboss.tools.ui.bot.ext.SWTTestExt;
import org.jboss.tools.ui.bot.ext.SWTUtilExt;
import org.jboss.tools.ui.bot.ext.config.Annotations.DB;
import org.jboss.tools.ui.bot.ext.config.TestConfigurator;
import org.jboss.tools.ui.bot.ext.config.Annotations.SWTBotTestRequires;
import org.jboss.tools.ui.bot.ext.config.Annotations.Seam;
import org.jboss.tools.ui.bot.ext.config.Annotations.Server;
import org.jboss.tools.ui.bot.ext.config.Annotations.ServerState;
import org.jboss.tools.ui.bot.ext.gen.ActionItem;
import org.jboss.tools.ui.bot.ext.parts.SWTBotBrowserExt;
import org.jboss.tools.ui.bot.ext.parts.SWTBotRadioExt;
import org.jboss.tools.ui.bot.ext.view.ProblemsView;
import org.jboss.tools.ui.bot.test.SWTJBTBot;
import org.junit.Test;
import org.junit.experimental.categories.Category;


//@SWTBotTestRequires(server=@Server(state=ServerState.NotRunning),perspective="Seam",seam=@Seam())
@SWTBotTestRequires(server=@Server,perspective="Seam",seam=@Seam(),db=@DB)
public class CreateSeamProjects extends AbstractSeamTestBase {
  
	protected static final String VALIDATION = "Validation";
	protected static final String DEPLOY_SOURCE = "Deploying datasource to server";
	protected static final String REG_IN_SERVER = "Register in server";
	protected static final String CONN_PROFILE = "hsqldb18_internal";
	
		
  public CreateSeamProjects() {
	}

    private SWTJBTExt swtJbtExt = new SWTJBTExt(bot);
	
    @Test
	@Category(WARTests.class)
	public void testCreateSeamProjectWar(){
		createSeamProject(TestControl.TYPE_WAR);
		util.waitForNonIgnoredJobs();
	}
    
    @Test
	@Category(WARTests.class)
	public void testCheckSeamProjectWar(){
    	checkSeamProject(TestControl.TYPE_WAR);
    }
	
    @Test
	@Category(EARTests.class)
	public void testCreateSeamProjectEar(){
		createSeamProject(TestControl.TYPE_EAR);
		util.waitForNonIgnoredJobs();
	}

    @Test
	@Category(EARTests.class)
    public void testCheckSeamProjectEar(){
    	checkSeamProject(TestControl.TYPE_EAR);
    }
    
	
	protected void createSeamProject(String type) {
		SWTJBTBot bot = new SWTJBTBot();
		bot.menu("File").menu("New").menu("Seam Web Project").click();
		bot.textWithLabel("Project name:").setText(AbstractSeamTestBase.testProjectName + type);
		bot.comboBoxInGroup("Target runtime").setSelection(SWTTestExt.configuredState.getServer().name);
		bot.comboBoxInGroup("Target Server").setSelection(SWTTestExt.configuredState.getServer().name);
		bot.comboBoxInGroup("Configuration").setSelection(
				AbstractSeamTestBase.seamConfigPrefix +
				SWTTestExt.configuredState.getSeam().version);				
		bot.button("Next >").click();
		bot.button("Next >").click();
		bot.button("Next >").click();
		bot.button("Next >").click();
		bot.comboBoxWithLabel("Seam Runtime:").setSelection(SWTTestExt.configuredState.getSeam().name);
		new SWTBotRadioExt(bot.radio(type).widget).clickWithoutDeselectionEvent();
		bot.comboBoxWithLabel("Connection profile:").setSelection(CONN_PROFILE);
		
		SWTBotShell seamPrjShell = bot.activeShell();		
		bot.button("Finish").click();
		bot.waitUntil(shellCloses(seamPrjShell), 480000);
		log.info("Seam shell closed.");
		bot.shells()[0].activate();		
		
	}
	
	protected void checkSeamProject(String type) {
		
		problems.show();
		SWTBotTreeItem[] errors = ProblemsView.getFilteredErrorsTreeItems(bot, null, null, null, null);
		assertNull("Errors in problem view.", errors);
		
		open.viewOpen(ActionItem.View.GeneralInternalWebBrowser.LABEL);
		
		bot.sleep(20000);
		SWTBotBrowserExt bBrowser = bot.browserExt();
		bBrowser.goURL("http://localhost:8080/" + AbstractSeamTestBase.testProjectName + type + "/home.seam");
		util.waitForBrowserLoadsPage(bBrowser);
		assertContains("Welcome to Seam", bBrowser.getText());
	}

}