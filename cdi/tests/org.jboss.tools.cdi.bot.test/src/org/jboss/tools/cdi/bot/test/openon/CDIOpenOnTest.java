package org.jboss.tools.cdi.bot.test.openon;

import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.jboss.tools.cdi.bot.test.CDIAllBotTests;
import org.jboss.tools.cdi.bot.test.uiutils.actions.CDIUtil;
import org.jboss.tools.ui.bot.ext.RequirementAwareSuite;
import org.jboss.tools.ui.bot.ext.SWTJBTExt;
import org.jboss.tools.ui.bot.ext.SWTTestExt;
import org.jboss.tools.ui.bot.ext.Timing;
import org.jboss.tools.ui.bot.ext.config.Annotations.Require;
import org.jboss.tools.ui.bot.ext.config.Annotations.Server;
import org.jboss.tools.ui.bot.ext.config.Annotations.ServerState;
import org.jboss.tools.ui.bot.ext.types.ViewType;
import org.jboss.tools.ui.bot.ext.view.ProblemsView;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

/*
 * Test operates on hyperlinks-openons
 * 
 * @author Jaroslav Jankovic
 */

@Require(perspective = "Java EE", server = @Server(state = ServerState.NotRunning, version = "6.0", operator = ">="))
@RunWith(RequirementAwareSuite.class)
@SuiteClasses({ CDIAllBotTests.class })
public class CDIOpenOnTest extends SWTTestExt {
	
	//private static final Logger LOGGER = Logger.getLogger(CDIQuickFixTest.class.getName());
	private static final String PROJECT_NAME = "CDIProject";
	private static final String PACKAGE_NAME = "org.cdi.test";
	
	
	@BeforeClass
	public static void setup() {
		eclipse.showView(ViewType.PROJECT_EXPLORER);		
		CDIUtil.createAndCheckCDIProject(bot, util, projectExplorer, PROJECT_NAME);
	}
	
	@After
	public void waitForJobs() {
		util.waitForNonIgnoredJobs();
	}
		
	@Test
	public void testInjectHyperlink() {		
		CDIUtil.bean(PACKAGE_NAME, "Animal", true, false, false, false, null,
				null, null, null).finish();
		bot.sleep(Timing.time1S());
		util.waitForNonIgnoredJobs();
		
		CDIUtil.bean(PACKAGE_NAME, "BrokenFarm", true, false, false, false, null,
				null, null, null).finish();
		util.waitForNonIgnoredJobs();
		SWTBotEditor ed = bot.activeEditor();
		CDIUtil.copyResourceToClass(ed, CDIOpenOnTest.class
				.getResourceAsStream("/resources/cdi/BrokenFarm.java.cdi"), false);		
		
		SWTBotTreeItem warningNode = ProblemsView.getWarningsNode(bot);
		assertNull("Warnings node should be empty.", warningNode);
		String openOnString = "@Inject";
		ed = SWTJBTExt.selectTextInSourcePane(bot, "BrokenFarm.java", openOnString, 1, openOnString.length()-1);
		ed.setFocus();
		bot.menu("Navigate").menu("Open Hyperlink").click();		
		bot.activeShell().bot().table(0).click(1, 0);
		bot.sleep(Timing.time1S());
		ed = bot.activeEditor();
		assertTrue("ERROR: redirected to " + ed.getTitle(), ed.getTitle().equals("Animal.java"));
	}
	
	/*
	 * TO DO 
	 * 
	 * - https://issues.jboss.org/browse/JBIDE-8202	
	 * - https://issues.jboss.org/browse/JBIDE-8692
	 * - OpenOn for Disposer/Produce and for injection point works
	 * - Classes indication for Open Injected Class works
	 * - OpenOn works in beans.xml => https://jira.jboss.org/browse/JBIDE-7025
	 * 
	 * 
	 */
}