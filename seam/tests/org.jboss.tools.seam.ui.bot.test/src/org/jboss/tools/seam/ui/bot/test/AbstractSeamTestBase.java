package org.jboss.tools.seam.ui.bot.test;

import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotCLabel;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.jboss.tools.ui.bot.ext.SWTTestExt;
import org.jboss.tools.ui.bot.ext.Timing;
import org.jboss.tools.ui.bot.ext.config.Annotations.DB;
import org.jboss.tools.ui.bot.ext.config.Annotations.Require;
import org.jboss.tools.ui.bot.ext.config.Annotations.Seam;
import org.jboss.tools.ui.bot.ext.config.Annotations.Server;
import org.jboss.tools.ui.bot.ext.helper.ContextMenuHelper;

@Require(
		server=@Server(),
		seam=@Seam(),
		db=@DB(),
		perspective="Seam",
		clearProjects=false)
public abstract class AbstractSeamTestBase extends SWTTestExt {
	
	public static final String testProjectName = "SeamPrj";
	public static final String seamConfigPrefix = "Dynamic Web Project with Seam ";
	public static final String testDomainName = "org.domain";
	
	public AbstractSeamTestBase() {
	}
	
	private String getTestPackageName(String type) {
		return type ==  TestControl.TYPE_WAR ?
			testDomainName + ".seamprjwar.test" :
		    testDomainName + ".seamprjear.test" ;						
	}
	

	/**Creates any Seam Action, Form etc.	*/
	public void createSeamUnit(String unitType, String type){
		bot.menu("File").menu("New").menu("Seam " +unitType).click();
		SWTBotShell shell = bot.activeShell();
		bot.textWithLabel("Seam Project:").setText(testProjectName + type);
		if ("Entity".equals(unitType)) {
			bot.textWithLabel("Seam entity class name:").setText("seam"+unitType);	
		} else {
			bot.textWithLabel("Seam component name:").setText("seam"+unitType);
		}
		bot.button("Finish").click();
		bot.waitUntil(Conditions.shellCloses(shell),15000);
	}

	/**Executes test of any Seam Action , Form etc.	*/
	public void checkSeamUnit(String unitType, String type){
		projectExplorer.selectProject(testProjectName + type + "-test");
		
		SWTBot viewBot = bot.activeView().bot();
		
		projectExplorer.selectTreeItem("Seam" + unitType + "Test.java", new String[] {
			testProjectName + type + "-test",
			"test-src",
			getTestPackageName(type)
		});
					
		ContextMenuHelper.clickContextMenu(viewBot.tree(), "Run As", "2 TestNG Test");
		SWTTestExt.util.waitForNonIgnoredJobs(120000);
		SWTBotView ngView = bot.viewByTitle("Results of running class Seam" + unitType + "Test");
		bot.sleep(Timing.time20S());
		SWTBot ngBot = ngView.bot();
		int k = 0;
		SWTBotCLabel l = ngBot.clabel(k);
				
		int passed = -1;
		int failed = -1;
		int skipped = -1;
		
		while ( (passed < 0) || (failed < 0) || (skipped < 0) ) {
			String txt = l.getText();
			if (txt.startsWith("Passed")) passed = Integer.valueOf(txt.split(":")[1].trim()); 
			if (txt.startsWith("Failed")) failed = Integer.valueOf(txt.split(":")[1].trim()); 
			if (txt.startsWith("Skipped")) skipped = Integer.valueOf(txt.split(":")[1].trim()); 
			k++;
			l = ngBot.clabel(k);
		}
		assertTrue("Failed test(s).", failed == 0);
		assertTrue("Skipped test(s).", skipped == 0);
		assertTrue("No passing tests.", passed > 0);
		
	}
	
	
}
