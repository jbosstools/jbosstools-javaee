package org.jboss.tools.cdi.bot.test.quickfix;

import java.util.logging.Logger;

import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEclipseEditor;
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

	private static final Logger LOGGER = Logger.getLogger(CDIQuickFixTest.class.getName());
	private static final String PROJECT_NAME = "CDIProject";
	private static final String PACKAGE_NAME = "org.cdi.test";
	private static SWTBotTreeItem[] problemsTrees;
	private static final String LINE_SEPARATOR = System.getProperty("line.separator");
	private enum ANNOTATIONS {TARGET, RETENTION, NAMED, TYPED,DISPOSES, OBSERVES}
	private enum CDICOMPONENT {STEREOSCOPE, QUALIFIER, SCOPE, BEAN}
	private SWTBotEclipseEditor ed; 

	@BeforeClass
	public static void setup() {
		eclipse.showView(ViewType.PROJECT_EXPLORER);
		CDIUtil.disableFolding(bot, util);
		CDIUtil.createAndCheckCDIProject(bot, util, projectExplorer,PROJECT_NAME);
	}

	@After
	public void waitForJobs() {
		util.waitForNonIgnoredJobs();
	}
	
	@Test
	public void testSerializableQF() {
		createComponent(CDICOMPONENT.BEAN, "B1");
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
		createComponent(CDICOMPONENT.BEAN, "Animal");
		assertTrue(("Animal.java").equals(ed.getTitle()));
		String code = ed.toTextEditor().getText();
		assertTrue(code.contains("package " + PACKAGE_NAME + ";"));
		assertTrue(code.contains("public class Animal {"));

		createComponent(CDICOMPONENT.BEAN, "Dog");
		CDIUtil.copyResourceToClass(ed, CDIQuickFixTest.class
				.getResourceAsStream("/resources/cdi/Dog.java.cdi"), false);
		assertTrue(("Dog.java").equals(ed.getTitle()));
		code = ed.toTextEditor().getText();
		LOGGER.fine(code);
		assertTrue(code.contains("package " + PACKAGE_NAME + ";"));
		assertTrue(code.contains("public class Dog extends Animal {"));

		createComponent(CDICOMPONENT.QUALIFIER, "Q1");
		assertTrue(("Q1.java").equals(ed.getTitle()));
		code = ed.toTextEditor().getText();
		LOGGER.fine(code);

		createComponent(CDICOMPONENT.BEAN, "BrokenFarm");
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
		bot.sleep(Timing.time1S());
		util.waitForNonIgnoredJobs();
		assertFalse(bot.button("Add >").isEnabled());
		assertFalse(bot.button("Finish").isEnabled());
		bot.table(0).click(bot.table(0).indexOf("Q1 - " + PACKAGE_NAME), 0);
		assertTrue(bot.button("Add >").isEnabled());
		assertFalse(bot.button("Finish").isEnabled());
		bot.clickButton("Add >");
		assertTrue(bot.button("Finish").isEnabled());
		bot.clickButton("Finish");

		bot.sleep(Timing.time1S());
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
		prepareCdiComponent(CDICOMPONENT.STEREOSCOPE, "S1");
		
		// 1.QF - https://issues.jboss.org/browse/JBIDE-7630 
		checkTargetAnnotation(CDICOMPONENT.STEREOSCOPE, ed);
		
		// 2.QF - https://issues.jboss.org/browse/JBIDE-7631
		checkRetentionAnnotation(CDICOMPONENT.STEREOSCOPE, ed);

		// 3.QF - https://issues.jboss.org/browse/JBIDE-7634
		checkNamedAnnotation(CDICOMPONENT.STEREOSCOPE, ed);
		
		// 4.QF - https://issues.jboss.org/browse/JBIDE-7640
		checkTypedAnnotation(CDICOMPONENT.STEREOSCOPE, ed);
	}
	
	@Test
	public void testQualifiersQF() {
		prepareCdiComponent(CDICOMPONENT.QUALIFIER, "Q2");
		
		// 1.QF - https://issues.jboss.org/browse/JBIDE-7631
		checkRetentionAnnotation(CDICOMPONENT.QUALIFIER, ed);
		
		// 2.QF - https://issues.jboss.org/browse/JBIDE-7632
		checkTargetAnnotation(CDICOMPONENT.QUALIFIER, ed);
	}
	
	@Test
	public void testScopeQF() {
		prepareCdiComponent(CDICOMPONENT.SCOPE, "Scope1");
		
		// 1.QF - https://issues.jboss.org/browse/JBIDE-7631
		checkRetentionAnnotation(CDICOMPONENT.SCOPE, ed);
		
		// 2.QF - https://issues.jboss.org/browse/JBIDE-7633
		checkTargetAnnotation(CDICOMPONENT.SCOPE, ed);
	}
	
	@Test
	public void testBeanQF() {
		prepareCdiComponent(CDICOMPONENT.BEAN, "MyBean");
		
		// 1.QF - https://issues.jboss.org/browse/JBIDE-7664
		checkConstructor(CDICOMPONENT.BEAN, ed);
		
		// 2.QF - https://issues.jboss.org/browse/JBIDE-7665
		checkProducerMethod(CDICOMPONENT.BEAN, ed);
		
		// 3.QF - https://issues.jboss.org/browse/JBIDE-7667
		checkObserverDisposerMethod(CDICOMPONENT.BEAN, ed);
		
		// 4.QF - https://issues.jboss.org/browse/JBIDE-7668
		
		// 5.QF - https://issues.jboss.org/browse/JBIDE-7680
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
	
	private void prepareCdiComponent(CDICOMPONENT component, String name) {
		createComponent(component, name);
		checkStartupWarnings(bot);
		switch (component) {
		case QUALIFIER:
			CDIUtil.replaceInEditor(ed, bot, "@Target({ TYPE, METHOD, PARAMETER, FIELD })",
					"@Target({TYPE, METHOD, PARAMETER, FIELD})");
			break;
		case STEREOSCOPE:
		case SCOPE:
			CDIUtil.replaceInEditor(ed, bot, "@Target({ TYPE, METHOD, FIELD })",
					"@Target({TYPE, METHOD, FIELD})");
			break;
		default:
			break;
		}
	}
	
	private void createComponent(CDICOMPONENT component, String name) {
		switch (component) {
		case STEREOSCOPE:
			CDIUtil.stereotype(PACKAGE_NAME, name, null, null, false, false, false,
					false).finish();
			break;
		case QUALIFIER:
			CDIUtil.qualifier(PACKAGE_NAME, name, false, false).finish();
			break;
		case SCOPE:
			CDIUtil.scope(PACKAGE_NAME, name, false, false, true, false).finish();	
			break;
		case BEAN:
			CDIUtil.bean(PACKAGE_NAME, name, true, false, false, false, null,
					null, null, null).finish();
			break;
		default:
			break;
		}
		util.waitForNonIgnoredJobs();
		ed = bot.activeEditor().toTextEditor();
	}
	
	private void prepareNamedAnnotation(SWTBotEclipseEditor ed, boolean add) {
		if (add) {
			ed.toTextEditor().insertText(ed.getLineCount()-4 , 0, 
					"@Named(\"Name\")" + LINE_SEPARATOR);
			ed.toTextEditor().insertText(6 , 0, "import javax.inject.Named;" + LINE_SEPARATOR);
		} else {
			CDIUtil.replaceInEditor(ed, bot, "@Named", "");
			CDIUtil.replaceInEditor(ed, bot,
					"import javax.inject.Named;", "");
		}
		bot.sleep(Timing.time1S());
		ed.save();
	}
	
	private void prepareTypedAnnotation(SWTBotEclipseEditor ed) {
		ed.toTextEditor().insertText(ed.getLineCount()-4 , 0, 
					"@Typed" + LINE_SEPARATOR);
		ed.toTextEditor().insertText(6 , 0, "import javax.enterprise.inject.Typed;" + LINE_SEPARATOR);
		bot.sleep(Timing.time1S());
		ed.save();
	}
	
	private void prepareInjectAnnot(SWTBotEclipseEditor ed) {
		ed.toTextEditor().insertText(3 , 1, "@Inject" + LINE_SEPARATOR);
		ed.toTextEditor().insertText(1 , 0, "import javax.inject.Inject;" + LINE_SEPARATOR);
		ed.toTextEditor().insertText(6 , 15, "String aaa");
	}
	
	private void prepareProducer(SWTBotEclipseEditor ed) {
		CDIUtil.copyResourceToClass(ed, CDIQuickFixTest.class
				.getResourceAsStream("/resources/cdi/MyBean.java.cdi"), false);
	}
	
	private void prepareObserverDisposer(SWTBotEclipseEditor ed) {
		prepareProducer(ed);
		CDIUtil.replaceInEditor(ed, bot, "@Produces", "@Inject");
		CDIUtil.replaceInEditor(ed, bot, "import javax.enterprise.inject.Produces;", "");
		CDIUtil.replaceInEditor(ed, bot, "String produceString", "void method");
		CDIUtil.replaceInEditor(ed, bot, "return \"test\";", "");
	}

	private void checkStartupWarnings(SWTBotExt bot) {
		SWTBotTreeItem[] warningTrees = ProblemsView
				.getFilteredWarningsTreeItems(bot, null, "/" + PROJECT_NAME,
						"S1.java", "CDI Problem");
		assertTrue(warningTrees.length == 0);
	}
	
	private void checkTargetAnnotation(CDICOMPONENT comp, SWTBotEclipseEditor ed) {
		checkTargetAnnotWithReplac(comp, ed, "@Target({TYPE, FIELD})");
		checkTargetAnnotWithReplac(comp, ed, "");
	}
	
	private void checkTargetAnnotWithReplac(CDICOMPONENT comp, SWTBotEclipseEditor ed, String replacement) {
		switch (comp) {
		case STEREOSCOPE:
		case SCOPE:
			CDIUtil.replaceInEditor(ed, bot, 
					"@Target({TYPE, METHOD, FIELD})", replacement);
			break;
		case QUALIFIER:
			if (replacement.equals("")) {
				CDIUtil.replaceInEditor(ed, bot, 
						"@Target({TYPE, METHOD, FIELD, PARAMETER})", replacement);
			} else {
				CDIUtil.replaceInEditor(ed, bot, 
						"@Target({TYPE, METHOD, PARAMETER, FIELD})", replacement);
			}
			CDIUtil.replaceInEditor(ed, bot, 
					"import static java.lang.annotation.ElementType.PARAMETER;", "");
			break;
		default:
			break;
		}
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
		checkSimpleQuickFix(ANNOTATIONS.TARGET, comp, replacement, ed);
	}
	
	private void checkRetentionAnnotation(CDICOMPONENT comp, SWTBotEclipseEditor ed) {
		checkRetenAnnotWithReplac(comp, ed, "@Retention(CLASS)");
		checkRetenAnnotWithReplac(comp, ed, "");
	}

	private void checkRetenAnnotWithReplac(CDICOMPONENT comp, SWTBotEclipseEditor ed, String replacement) {
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
		
		checkSimpleQuickFix(ANNOTATIONS.RETENTION, comp, replacement, ed);
	}
		
	private void checkNamedAnnotation(CDICOMPONENT comp, SWTBotEclipseEditor ed) {
		prepareNamedAnnotation(ed, true);
		checkNamedAnnotWithReplac(comp, ed, "@Named");
		prepareNamedAnnotation(ed, false);
		prepareNamedAnnotation(ed, true);
		checkNamedAnnotWithReplac(comp, ed, "");
	}
	
	private void checkNamedAnnotWithReplac(CDICOMPONENT comp, SWTBotEclipseEditor ed, String replacement) {
		checkSimpleQuickFix(ANNOTATIONS.NAMED, comp, replacement, ed);
	}
	
	private void checkTypedAnnotation(CDICOMPONENT comp, SWTBotEclipseEditor ed) {
		prepareTypedAnnotation(ed);
		checkTypedAnnotWithReplac(comp, ed, "");
	}
	
	private void checkTypedAnnotWithReplac(CDICOMPONENT comp, SWTBotEclipseEditor ed, String replacement) {
		checkSimpleQuickFix(ANNOTATIONS.TYPED, comp, replacement, ed);
	}
	
	private void checkConstructor(CDICOMPONENT comp, SWTBotEclipseEditor ed) {
		prepareInjectAnnot(ed);
		checkConstructorWithReplac(comp, ed, "@Disposes");
		checkConstructorWithReplac(comp, ed, "@Observes");
	}
	
	private void checkConstructorWithReplac(CDICOMPONENT comp, SWTBotEclipseEditor ed, 
			String replacement) {
		ed.toTextEditor().insertText(6 , 15, replacement + " ");
		dispObserCompletion(comp, ed, replacement);
	}
	
	private void checkProducerMethod(CDICOMPONENT comp, SWTBotEclipseEditor ed) {
		prepareProducer(ed);
		checkProducerWithReplac(comp, ed, "@Disposes");
		checkProducerWithReplac(comp, ed, "@Observes");
	}
	
	private void checkProducerWithReplac(CDICOMPONENT comp, SWTBotEclipseEditor ed, String replacement) {
		ed.toTextEditor().insertText(11, 29, replacement + " ");
		dispObserCompletion(comp, ed, replacement);
	}
	
	private void checkObserverDisposerMethod(CDICOMPONENT comp, SWTBotEclipseEditor ed) {
		prepareObserverDisposer(ed);
		checkObserverDisposerWithReplac(comp, ed, "@Disposes");
		checkObserverDisposerWithReplac(comp, ed, "@Observes");
	}
	
	private void checkObserverDisposerWithReplac(CDICOMPONENT comp, 
			SWTBotEclipseEditor ed, String replacement) {
		ed.toTextEditor().insertText(10, 20, replacement + " ");
		dispObserCompletion(comp, ed, replacement);
	}
	
	private void dispObserCompletion(CDICOMPONENT comp, SWTBotEclipseEditor ed, String replacement) {
		ed.toTextEditor().insertText(2 , 0, "import javax.enterprise." + 
				(replacement.contains("Disposes")?"inject.":"event.") + 
				(replacement.substring(1) + ";" + LINE_SEPARATOR));
		bot.sleep(Timing.time1S());
		ed.save();
		ANNOTATIONS annonType = (replacement.equals("@Disposes")?ANNOTATIONS.DISPOSES:ANNOTATIONS.OBSERVES);
		checkSimpleQuickFix(annonType, comp, replacement, ed);
	}
	
	
	private void checkSimpleQuickFix(ANNOTATIONS annonType, CDICOMPONENT comp, String replacement,
			SWTBotEclipseEditor ed) {
		String className = null;
		setClassName(comp, className);
		problemsTrees = getProblems(annonType, className);
		assertTrue(problemsTrees.length != 0);
		resolve(annonType, comp, replacement, ed);
		problemsTrees = getProblems(annonType, className);
		assertTrue(problemsTrees.length == 0);
	}
	
	private void setClassName(CDICOMPONENT comp, String className) {
		switch (comp) {
		case STEREOSCOPE:
			className = "S1.java";
			break;
		case QUALIFIER:
			className = "Q2.java";
			break;
		case SCOPE:
			className = "Scope1.java";
			break;
		case BEAN:
			className = "MyBean.java";
			break;
		default:
			break;
		}
	}
	
	private SWTBotTreeItem[] getProblems(ANNOTATIONS annonType, String className) {
		SWTBotTreeItem[] problemsTree;
		switch (annonType) {
		case NAMED:
		case TYPED:
		case DISPOSES:
		case OBSERVES:
			problemsTree = ProblemsView.getFilteredErrorsTreeItems(bot, null, "/"
					+ PROJECT_NAME, className, "CDI Problem");
			break;
		default:
			problemsTree = ProblemsView.getFilteredWarningsTreeItems(bot, null, "/"
					+ PROJECT_NAME, className, "CDI Problem");
			break;
		}
		return problemsTree;
	}
	
	private void resolve(ANNOTATIONS annonType, CDICOMPONENT comp, String replacement,
			SWTBotEclipseEditor ed) {
		boolean chooseFirstOption = true;
		if (annonType == ANNOTATIONS.NAMED && replacement.equals("")) {
			chooseFirstOption = false;
		} else {
			if ((comp == CDICOMPONENT.BEAN && annonType == ANNOTATIONS.DISPOSES) ||
				(comp == CDICOMPONENT.BEAN && annonType == ANNOTATIONS.OBSERVES)) {
				if (problemsTrees[0].getText().contains("Producer method has a parameter annotated "
						+ replacement) || 
					problemsTrees[0].getText().contains("method cannot be annotated @Inject")) {
					chooseFirstOption = false;
				}
			}
		}
		resolveWithAnnonType(annonType, replacement, chooseFirstOption?0:1);
	}
	
	private void resolveWithAnnonType(ANNOTATIONS annonType, String replacement, int index) {
		CDIUtil.openQuickFix(problemsTrees[0], bot);
		bot.table(0).click(index, 0);
		assertFalse(bot.button("Finish").isEnabled());
		bot.table(1).getTableItem(0).check();
		assertTrue(bot.button("Finish").isEnabled());
		bot.clickButton("Finish");
		bot.sleep(Timing.time1S());
		util.waitForNonIgnoredJobs();
		afterResolveclearImports(annonType, replacement);
	}
	
	private void afterResolveclearImports(ANNOTATIONS annonType, String replacement) {
		if (annonType == ANNOTATIONS.RETENTION) {
			if (replacement.equals("@Retention(CLASS)")) {
				CDIUtil.replaceInEditor(ed, bot, 
						"import static java.lang.annotation.RetentionPolicy.CLASS;","");
			}
		}
	}

}
