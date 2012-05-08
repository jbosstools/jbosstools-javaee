package org.jboss.tools.seam.ui.bot.test.create;

import static org.eclipse.swtbot.swt.finder.waits.Conditions.shellCloses;

import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.jboss.tools.seam.ui.bot.test.AbstractSeamTestBase;
import org.jboss.tools.seam.ui.bot.test.EARTests;
import org.jboss.tools.seam.ui.bot.test.TestControl;
import org.jboss.tools.seam.ui.bot.test.WARTests;
import org.jboss.tools.ui.bot.ext.SWTTestExt;
import org.jboss.tools.ui.bot.ext.Timing;
import org.jboss.tools.ui.bot.ext.gen.ActionItem;
import org.jboss.tools.ui.bot.ext.parts.SWTBotBrowserExt;
import org.jboss.tools.ui.bot.ext.parts.SWTBotRadioExt;
import org.jboss.tools.ui.bot.ext.view.ProblemsView;
import org.jboss.tools.ui.bot.test.SWTJBTBot;
import org.junit.Test;
import org.junit.experimental.categories.Category;


public class CreateSeamProjects extends AbstractSeamTestBase {
  
	protected static final String VALIDATION = "Validation";
	protected static final String DEPLOY_SOURCE = "Deploying datasource to server";
	protected static final String REG_IN_SERVER = "Register in server";
	
		
  public CreateSeamProjects() {
	}
  @Test
	@Category(WARTests.class)
	public void testCreateSeamProjectWar(){
		createSeamProject(TestControl.TYPE_WAR);
		util.waitForNonIgnoredJobs(Timing.time100S());
    bot.sleep(Timing.time3S());
    // checkSeamProject(TestControl.TYPE_WAR);
	}
  @Test
	@Category(EARTests.class)
	public void testCreateSeamProjectEar(){
		createSeamProject(TestControl.TYPE_EAR);
		util.waitForNonIgnoredJobs(Timing.time100S());
    bot.sleep(Timing.time3S());
    // checkSeamProject(TestControl.TYPE_EAR);
	}
	
	protected void createSeamProject(String type) {
		SWTJBTBot bot = new SWTJBTBot();
		bot.menu("File").menu("New").menu("Seam Web Project").click();
		bot.textWithLabel("Project name:").setText(AbstractSeamTestBase.testProjectName + type);
		bot.comboBoxInGroup("Target runtime").setSelection(SWTTestExt.configuredState.getServer().name);
		bot.comboBoxInGroup("Target Server").setSelection(SWTTestExt.configuredState.getServer().name);
		bot.comboBoxInGroup("Configuration").setSelection(
				AbstractSeamTestBase.seamConfigPrefix +
				SWTTestExt.configuredState.getSeam().version + 
				(SWTTestExt.configuredState.getSeam().version.equals("2.3") ? " (Technical Preview)" : ""));				
		bot.button("Next >").click();
		bot.button("Next >").click();
		bot.button("Next >").click();
		bot.button("Next >").click();
		bot.comboBoxWithLabel("Seam Runtime:").setSelection(SWTTestExt.configuredState.getSeam().name);
		new SWTBotRadioExt(bot.radio(type).widget).clickWithoutDeselectionEvent();
		bot.comboBoxWithLabel("Connection profile:").setSelection(SWTTestExt.configuredState.getDB().name);
		
		SWTBotShell seamPrjShell = bot.activeShell();		
		bot.button("Finish").click();
		bot.waitUntil(shellCloses(seamPrjShell), 480000);
		log.info("Seam shell closed.");
		bot.shells()[0].activate();		
		
	}
	
	protected void checkSeamProject(String type) {
		
		problems.show();
		SWTBotTreeItem[] errors = ProblemsView.getFilteredErrorsTreeItems(bot, null, null, null, null);
		assertTrue("Errors in problem view.", errors == null || errors.length == 0);
		
		open.viewOpen(ActionItem.View.GeneralInternalWebBrowser.LABEL);
		
		bot.sleep(20000);
		SWTBotBrowserExt bBrowser = bot.browserExt();
		bBrowser.goURL("http://localhost:8080/" + AbstractSeamTestBase.testProjectName + type + "/home.seam");
		util.waitForBrowserLoadsPage(bBrowser);
		assertContains("Welcome to Seam", bBrowser.getText());
	}

}