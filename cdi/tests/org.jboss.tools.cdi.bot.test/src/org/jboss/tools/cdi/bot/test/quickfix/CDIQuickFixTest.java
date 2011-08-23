package org.jboss.tools.cdi.bot.test.quickfix;

import java.util.logging.Logger;

import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEclipseEditor;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.jboss.tools.cdi.bot.test.CDIAllBotTests;
import org.jboss.tools.cdi.bot.test.uiutils.actions.CDIUtil;
import org.jboss.tools.ui.bot.ext.RequirementAwareSuite;
import org.jboss.tools.ui.bot.ext.SWTBotExt;
import org.jboss.tools.ui.bot.ext.SWTTestExt;
import org.jboss.tools.ui.bot.ext.Timing;
import org.jboss.tools.ui.bot.ext.config.Annotations.Require;
import org.jboss.tools.ui.bot.ext.config.Annotations.Server;
import org.jboss.tools.ui.bot.ext.config.Annotations.ServerState;
import org.jboss.tools.ui.bot.ext.types.ViewType;
import org.jboss.tools.ui.bot.ext.view.ProblemsView;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

/*
 * Test operates on quick fixes of CDI components
 * 
 * @author Jaroslav Jankovic
 */

@Require(perspective = "Java EE", server = @Server(state = ServerState.NotRunning, version = "6.0", operator = ">="))
@RunWith(RequirementAwareSuite.class)
@SuiteClasses({ CDIAllBotTests.class })
public class CDIQuickFixTest extends SWTTestExt {

	private static final Logger LOGGER = Logger.getLogger(CDIQuickFixTest.class
			.getName());
	private static final String PROJECT_NAME = "CDIProject";
	private static final String PACKAGE_NAME = "org.cdi.test";
	private static SWTBotTreeItem[] problemsTrees;
	private static final String LINE_SEPARATOR = System.getProperty("line.separator");
	private enum ANNOTATIONS {TARGET, RETENTION, NAMED, TYPED}

	@BeforeClass
	public static void setup() {
		eclipse.showView(ViewType.PROJECT_EXPLORER);
		CDIUtil.disableFolding(bot, util);
		CDIUtil.createAndCheckCDIProject(bot, util, projectExplorer,
				PROJECT_NAME);
	}

	@After
	public void waitForJobs() {
		util.waitForNonIgnoredJobs();
	}

	@Test
	public void testSerializableQF() {
		CDIUtil.bean(PACKAGE_NAME, "B1", true, false, false, false, null, null,
				null, null).finish();
		util.waitForNonIgnoredJobs();
		SWTBotEditor ed = bot.activeEditor();
		assertTrue(("B1.java").equals(ed.getTitle()));
		String code = ed.toTextEditor().getText();
		LOGGER.fine(code);
		assertTrue(code.contains("package " + PACKAGE_NAME + ";"));
		assertTrue(code.contains("public class B1 {"));

		CDIUtil.copyResourceToClass(ed, CDIQuickFixTest.class
				.getResourceAsStream("/resources/cdi/B1.java.cdi"), false);
		assertContains("@SessionScoped", ed.toTextEditor().getText());
		problemsTrees = ProblemsView.getFilteredWarningsTreeItems(bot,
				"Managed bean B1 which", "/" + PROJECT_NAME, "B1.java",
				"CDI Problem");
		assertTrue(problemsTrees.length == 1);

		CDIUtil.resolveQuickFix(problemsTrees[0], bot, util);
		SWTBotEclipseEditor eclEditor = ed.toTextEditor();
		assertTrue(eclEditor.getText().contains("import java.io.Serializable;"));
		problemsTrees = ProblemsView.getFilteredWarningsTreeItems(bot,
				"Managed bean B1 which", "/" + PROJECT_NAME, "B1.java",
				"CDI Problem");
		assertTrue(problemsTrees.length == 0);
	}

	@Test
	public void testMultipleBeansQF() {
		CDIUtil.bean(PACKAGE_NAME, "Animal", true, false, false, false, null,
				null, null, null).finish();
		util.waitForNonIgnoredJobs();
		SWTBotEditor ed = bot.activeEditor();
		assertTrue(("Animal.java").equals(ed.getTitle()));
		String code = ed.toTextEditor().getText();
		assertTrue(code.contains("package " + PACKAGE_NAME + ";"));
		assertTrue(code.contains("public class Animal {"));

		CDIUtil.bean(PACKAGE_NAME, "Dog", true, false, false, false, null,
				null, null, null).finish();
		util.waitForNonIgnoredJobs();
		ed = bot.activeEditor();
		CDIUtil.copyResourceToClass(ed, CDIQuickFixTest.class
				.getResourceAsStream("/resources/cdi/Dog.java.cdi"), false);
		assertTrue(("Dog.java").equals(ed.getTitle()));
		code = ed.toTextEditor().getText();
		LOGGER.fine(code);
		assertTrue(code.contains("package " + PACKAGE_NAME + ";"));
		assertTrue(code.contains("public class Dog extends Animal {"));

		CDIUtil.qualifier(PACKAGE_NAME, "Q1", false, false).finish();
		util.waitForNonIgnoredJobs();
		ed = bot.activeEditor();
		assertTrue(("Q1.java").equals(ed.getTitle()));
		code = ed.toTextEditor().getText();
		LOGGER.fine(code);

		CDIUtil.bean(PACKAGE_NAME, "BrokenFarm", true, false, false, false,
				null, null, null, null).finish();
		util.waitForNonIgnoredJobs();
		ed = bot.activeEditor();
		CDIUtil.copyResourceToClass(ed, CDIQuickFixTest.class
				.getResourceAsStream("/resources/cdi/BrokenFarm.java.cdi"),
				false);
		assertTrue(("BrokenFarm.java").equals(ed.getTitle()));
		code = ed.toTextEditor().getText();
		LOGGER.fine(code);
		assertTrue(code.contains("package " + PACKAGE_NAME + ";"));
		assertTrue(code.contains("public class BrokenFarm {"));
		assertTrue(code.contains("@Inject private Animal animal;"));

		problemsTrees = ProblemsView.getFilteredWarningsTreeItems(bot,
				"Multiple beans are eligible", "/" + PROJECT_NAME,
				"BrokenFarm.java", "CDI Problem");
		assertTrue(problemsTrees.length == 1);

		CDIUtil.openQuickFix(problemsTrees[0], bot);
		String qualifBean = null;
		if (bot.table(0).cell(0, 0).contains("Animal")) {
			qualifBean = "Animal";
		} else {
			qualifBean = "Dog";
		}
		bot.activeShell().bot().button("Finish").click();
		bot.sleep(Timing.time2S());
		util.waitForNonIgnoredJobs();
		assertFalse(bot.button("Add >").isEnabled());
		assertFalse(bot.button("Finish").isEnabled());
		bot.table(0).click(bot.table(0).indexOf("Q1 - " + PACKAGE_NAME), 0);
		assertTrue(bot.button("Add >").isEnabled());
		assertFalse(bot.button("Finish").isEnabled());
		bot.clickButton("Add >");
		assertTrue(bot.button("Finish").isEnabled());
		bot.clickButton("Finish");

		bot.sleep(Timing.time2S());
		util.waitForNonIgnoredJobs();
		code = ed.toTextEditor().getText();
		assertTrue(code.contains("@Inject @Q1 private Animal animal;"));
		code = bot.editorByTitle(qualifBean + ".java").toTextEditor().getText();
		assertTrue(code.contains("@Q1"));
		problemsTrees = ProblemsView.getFilteredWarningsTreeItems(bot,
				"Multiple beans are eligible", "/" + PROJECT_NAME,
				"BrokenFarm.java", "CDI Problem");
		assertTrue(problemsTrees.length == 0);
	}

	@Test
	public void testStereoscopeQF() {
		CDIUtil.stereotype(PACKAGE_NAME, "S1", null, null, false, false, false,
				false).finish();
		util.waitForNonIgnoredJobs();
		SWTBotEclipseEditor ed = bot.activeEditor().toTextEditor();
		checkStartupWarnings(bot);
		prepareStereoscope(ed);
		
		// 1.QF - https://issues.jboss.org/browse/JBIDE-7630 
		checkTargetAnnotation(ed);
		
		// 2.QF - https://issues.jboss.org/browse/JBIDE-7631
		checkRetentionAnnotation(ed);

		// 3.QF - https://issues.jboss.org/browse/JBIDE-7634
		checkNamedAnnotation(ed);
		
		// 4.QF - https://issues.jboss.org/browse/JBIDE-7640
		checkTypedAnnotation(ed);
	}

	@Ignore("not ready yet")
	@Test
	public void testQualifiersQF() {
		/*
		 * 2 QF
		 */

		CDIUtil.qualifier(PACKAGE_NAME, "Q1", false, false).finish();
		util.waitForNonIgnoredJobs();
		SWTBotEditor ed = bot.activeEditor();
		ed.setFocus();
	}

	@Ignore("not ready yet")
	@Test
	public void testScopeQF() {
		/*
		 * 2 QF
		 */

		CDIUtil.scope(PACKAGE_NAME, "Scope1", false, false, true, false)
				.finish();
		util.waitForNonIgnoredJobs();
		SWTBotEditor ed = bot.activeEditor();
		ed.setFocus();
	}

	@Ignore("not ready yet")
	@Test
	public void testBeanQF() {
		/*
		 * 5 QF
		 */

		CDIUtil.bean(PACKAGE_NAME, "MyBean", true, false, false, false, null,
				null, null, null).finish();
		util.waitForNonIgnoredJobs();
		SWTBotEditor ed = bot.activeEditor();
		ed.setFocus();
	}

	@Ignore("not ready yet")
	@Test
	public void testInterDecorQF() {
		/*
		 * 5 QF
		 * 
		 * tu vytvorim jednotnu metodu, s jednym parametrom - ci iner alebo
		 * decor jediny zmysel pri vytvarani komponenty, potom by to malo byt
		 * rovnake
		 */
	}
	
	private void prepareStereoscope(SWTBotEclipseEditor ed) {
		CDIUtil.replaceInEditor(ed, bot, "@Target({ TYPE, METHOD, FIELD })",
				"@Target({TYPE, METHOD, FIELD})");
	}
	
	private void prepareNamedAnnotation(SWTBotEclipseEditor ed, boolean add) {
		if (add) {
			ed.toTextEditor().insertText(ed.getLineCount()-4 , 0, 
					"@Named(\"Stereoscope\")" + LINE_SEPARATOR);
			ed.toTextEditor().insertText(6 , 0, "import javax.inject.Named;" + LINE_SEPARATOR);
		} else {
			CDIUtil.replaceInEditor(ed, bot, "@Named", "");
			CDIUtil.replaceInEditor(ed, bot,
					"import javax.inject.Named;", "");
		}
		bot.sleep(Timing.time2S());
		ed.save();
	}
	
	private void prepareTypedAnnotation(SWTBotEclipseEditor ed) {
		ed.toTextEditor().insertText(ed.getLineCount()-4 , 0, 
					"@Typed" + LINE_SEPARATOR);
		ed.toTextEditor().insertText(6 , 0, "import javax.enterprise.inject.Typed;" + LINE_SEPARATOR);
		bot.sleep(Timing.time2S());
		ed.save();
	}

	private void checkStartupWarnings(SWTBotExt bot) {
		SWTBotTreeItem[] warningTrees = ProblemsView
				.getFilteredWarningsTreeItems(bot, null, "/" + PROJECT_NAME,
						"S1.java", "CDI Problem");
		assertTrue(warningTrees.length == 0);
	}
	
	private void checkTargetAnnotation(SWTBotEclipseEditor ed) {
		checkTargetAnnotWithReplac(ed, "@Target({TYPE, FIELD})");
		checkTargetAnnotWithReplac(ed, "");
	}
	
	private void checkTargetAnnotWithReplac(SWTBotEclipseEditor ed, String replacement) {
		CDIUtil.replaceInEditor(ed, bot, "@Target({TYPE, METHOD, FIELD})",
				replacement);
		CDIUtil.replaceInEditor(ed, bot,
				"import static java.lang.annotation.ElementType.METHOD;", "");
		if (replacement.equals("")) {
			CDIUtil.replaceInEditor(ed, bot,
					"import java.lang.annotation.Target;", "");
			CDIUtil.replaceInEditor(ed, bot,
					"import static java.lang.annotation.ElementType.TYPE;", "");
			CDIUtil.replaceInEditor(ed, bot,
					"import static java.lang.annotation.ElementType.FIELD;", "");
		}
		checkSimpleQuickFix(ANNOTATIONS.TARGET, replacement, ed);
	}
	
	private void checkRetentionAnnotation(SWTBotEclipseEditor ed) {
		checkRetenAnnotWithReplac(ed, "@Retention(CLASS)");
		checkRetenAnnotWithReplac(ed, "");
	}

	private void checkRetenAnnotWithReplac(SWTBotEclipseEditor ed, String replacement) {
		CDIUtil.replaceInEditor(ed, bot, "@Retention(RUNTIME)", replacement);
		if (replacement.equals("@Retention(CLASS)")) {
			CDIUtil.replaceInEditor(ed, bot,
					"import static java.lang.annotation.RetentionPolicy.RUNTIME;",
					"import static java.lang.annotation.RetentionPolicy.CLASS;");
		} else {
			CDIUtil.replaceInEditor(ed, bot,
					"import static java.lang.annotation.RetentionPolicy.RUNTIME;",
					"");
			CDIUtil.replaceInEditor(ed, bot,
					"import java.lang.annotation.Retention;", "");
		}
		
		checkSimpleQuickFix(ANNOTATIONS.RETENTION, replacement, ed);
	}
		
	private void checkNamedAnnotation(SWTBotEclipseEditor ed) {
		prepareNamedAnnotation(ed, true);
		checkNamedAnnotWithReplac(ed, "@Named");
		prepareNamedAnnotation(ed, false);
		prepareNamedAnnotation(ed, true);
		checkNamedAnnotWithReplac(ed, "");
	}
	
	private void checkNamedAnnotWithReplac(SWTBotEclipseEditor ed, String replacement) {
		checkSimpleQuickFix(ANNOTATIONS.NAMED, replacement, ed);
	}
	
	private void checkTypedAnnotation(SWTBotEclipseEditor ed) {
		prepareTypedAnnotation(ed);
		checkTypedAnnotWithReplac(ed, "");
	}
	
	private void checkTypedAnnotWithReplac(SWTBotEclipseEditor ed, String replacement) {
		checkSimpleQuickFix(ANNOTATIONS.TYPED, replacement, ed);
	}

	private void checkSimpleQuickFix(ANNOTATIONS annonType, String replacement,
			SWTBotEclipseEditor ed) {
		if (annonType == ANNOTATIONS.NAMED || annonType == ANNOTATIONS.TYPED) {
			problemsTrees = ProblemsView.getFilteredErrorsTreeItems(bot, null, "/"
				+ PROJECT_NAME, "S1.java", "CDI Problem");
		} else {
			problemsTrees = ProblemsView.getFilteredWarningsTreeItems(bot, null, "/"
				+ PROJECT_NAME, "S1.java", "CDI Problem");
		}
		assertTrue(problemsTrees.length == 1);
		if (annonType != ANNOTATIONS.NAMED) {
			CDIUtil.resolveQuickFix(problemsTrees[0], bot, util);
			if (annonType == ANNOTATIONS.RETENTION) {
				if (replacement.equals("@Retention(CLASS)")) {
					CDIUtil.replaceInEditor(ed, bot, 
							"import static java.lang.annotation.RetentionPolicy.CLASS;","");
				}
			}
		} else {
			if (replacement.equals("@Named")) {
				CDIUtil.resolveQuickFix(problemsTrees[0], bot, util);
			} else {
				CDIUtil.openQuickFix(problemsTrees[0], bot);
				bot.table(0).click(1, 0);
				assertFalse(bot.button("Finish").isEnabled());
				bot.table(1).getTableItem(0).check();
				assertTrue(bot.button("Finish").isEnabled());
				bot.clickButton("Finish");
				bot.sleep(Timing.time2S());
				util.waitForNonIgnoredJobs();
			}
		}
		problemsTrees = ProblemsView.getFilteredWarningsTreeItems(bot, null, "/"
				+ PROJECT_NAME, "S1.java", "CDI Problem");
		assertTrue(problemsTrees.length == 0);
		problemsTrees = ProblemsView.getFilteredErrorsTreeItems(bot, null, "/"
				+ PROJECT_NAME, "S1.java", "CDI Problem");
		assertTrue(problemsTrees.length == 0);
	}

}
