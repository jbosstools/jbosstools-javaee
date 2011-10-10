package org.jboss.tools.cdi.bot.test.openon;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.jboss.tools.cdi.bot.test.CDIAllBotTests;
import org.jboss.tools.cdi.bot.test.quickfix.CDIQuickFixTest;
import org.jboss.tools.cdi.bot.test.uiutils.actions.CDIBase;
import org.jboss.tools.cdi.bot.test.uiutils.actions.CDIUtil;
import org.jboss.tools.ui.bot.ext.RequirementAwareSuite;
import org.jboss.tools.ui.bot.ext.config.Annotations.Require;
import org.jboss.tools.ui.bot.ext.config.Annotations.Server;
import org.jboss.tools.ui.bot.ext.config.Annotations.ServerState;
import org.jboss.tools.ui.bot.ext.helper.TreeHelper;
import org.jboss.tools.ui.bot.ext.types.ViewType;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

/*
 * Test operates on hyperlinks-openons
 * 
 * @author Jaroslav Jankovic
 * 
 * 
 * TO DO 
 * 
 * - OpenOn for Disposer/Produce and for injection point works
 * - Classes indication for Open Injected Class works
 * 
 * 
 */

@Require(perspective = "Java EE", server = @Server(state = ServerState.NotRunning, version = "6.0", operator = ">="))
@RunWith(RequirementAwareSuite.class)
@SuiteClasses({ CDIAllBotTests.class })
public class CDIOpenOnTest extends CDIBase {

	private static final Logger LOGGER = Logger.getLogger(CDIQuickFixTest.class.getName());
	private static final String PROJECT_NAME = "CDIProject3";
	private static final String PACKAGE_NAME = "org.cdi.test";

	@BeforeClass
	public static void setup() {
		eclipse.showView(ViewType.PROJECT_EXPLORER);
	}

	@After
	public void waitForJobs() {
		util.waitForNonIgnoredJobs();
	}

	@Test
	public void testCreateProject() {
		createAndCheckCDIProject(bot, util, projectExplorer, PROJECT_NAME);
	}
	
	@Test
	public void testInjectOpenOn() {

		createComponent(CDICOMPONENT.BEAN, "Animal", PACKAGE_NAME, null);

		createComponent(CDICOMPONENT.BEAN, "BrokenFarm", PACKAGE_NAME, null);

		CDIUtil.copyResourceToClass(getEd(), CDIOpenOnTest.class
				.getResourceAsStream("/resources/cdi/BrokenFarm.java.cdi"),
				false);
		openOn("@Inject", "BrokenFarm.java", "@Inject");
		assertTrue("ERROR: redirected to " + getEd().getTitle(), getEd()
				.getTitle().equals("Animal.java"));
	}
	
	@Test
	public void testBeansXMLClassesOpenOn() {

		// https://issues.jboss.org/browse/JBIDE-7025
		createComponent(CDICOMPONENT.BEANSXML, null, PROJECT_NAME
				+ "/WebContent/WEB-INF", null);
		
		createComponent(CDICOMPONENT.DECORATOR, "D1", PACKAGE_NAME,
				"java.util.Set");
		bot.editorByTitle("beans.xml").show();
		bot.cTabItem("Source").activate();
		openOn(PACKAGE_NAME + ".D1", "beans.xml", null);
		assertTrue("ERROR: redirected to " + getEd(),
				getEd().getTitle().equals("D1.java"));
		
		createComponent(CDICOMPONENT.INTERCEPTOR, "Interceptor1", PACKAGE_NAME,
				null);
		bot.editorByTitle("beans.xml").show();
		openOn(PACKAGE_NAME + ".Interceptor1", "beans.xml", null);
		assertTrue("ERROR: redirected to " + getEd(),
					getEd().getTitle().equals("Interceptor1.java"));
		
		createComponent(CDICOMPONENT.BEAN, "B1", PACKAGE_NAME,
				"alternative+beansxml");
		bot.editorByTitle("beans.xml").show();
		openOn(PACKAGE_NAME + ".B1", "beans.xml", null);
		assertTrue("ERROR: redirected to " + getEd(),
					getEd().getTitle().equals("B1.java"));
		
		createComponent(CDICOMPONENT.STEREOSCOPE, "S1", PACKAGE_NAME,
				"alternative+beansxml");
		bot.editorByTitle("beans.xml").show();
		openOn(PACKAGE_NAME + ".S1", "beans.xml", null);
		assertTrue("ERROR: redirected to " + getEd(),
					getEd().getTitle().equals("S1.java"));		
		
	}

	@Test
	public void testResourceOpenOn() {

		// https://issues.jboss.org/browse/JBIDE-8202
		addLibrary("seam-solder.jar");
				
		createComponent(CDICOMPONENT.BEAN, "B2", PACKAGE_NAME, null);
		CDIUtil.copyResourceToClass(getEd(), CDIQuickFixTest.class
				.getResourceAsStream("/resources/cdi/B2.java.cdi"), false);
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
	
	@Test
	public void testGenericOpenOn() {

		// https://issues.jboss.org/browse/JBIDE-8692

		/*
		 * copy files from project which is mentioned in JIRA, then it will be easy 
		 */
	}
	
	private void moveFileInProjectExplorer(String file, String sourceFolder, String destFolder) {
		SWTBotTree tree = projectExplorer.bot().tree();
		SWTBotTreeItem item = projectExplorer.selectTreeItem(file, sourceFolder.split("/"));
		
		CDIUtil.nodeContextMenu(tree, item, "Move...").click();
		
		assertFalse(bot.button("OK").isEnabled());
		
		tree = bot.tree();	
		tree.collapseNode(destFolder.split("/")[0]);	
		
		TreeHelper.expandNode(bot, destFolder.split("/")).select();		

		assertTrue(bot.button("OK").isEnabled());
		bot.button("OK").click();		
	}
	
	private void addLibrary(String libraryName) {
		try {
			addLibraryIntoProject(PROJECT_NAME, libraryName);
			LOGGER.info("Library: \"" + libraryName + "\" copied");
			addLibraryToProjectsClassPath(PROJECT_NAME, libraryName);
		} catch (IOException exc) {
			LOGGER.log(Level.SEVERE, "Error while adding seam solder library into project");
		}		
	}
	
}