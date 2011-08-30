package org.jboss.tools.cdi.bot.test.quickfix;

import java.util.logging.Logger;

import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEclipseEditor;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.jboss.tools.cdi.bot.test.CDIAllBotTests;
import org.jboss.tools.cdi.bot.test.uiutils.actions.CDIUtil;
import org.jboss.tools.ui.bot.ext.RequirementAwareSuite;
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
	private enum ANNOTATIONS {TARGET, RETENTION, NAMED, TYPED, DISPOSES, OBSERVES, INTERCEPTOR, DECORATOR}
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
		String className = "S1";
		prepareCdiComponent(CDICOMPONENT.STEREOSCOPE, className);
		
		// 1.QF - https://issues.jboss.org/browse/JBIDE-7630 
		checkTargetAnnotation(CDICOMPONENT.STEREOSCOPE, ed, className);
		
		// 2.QF - https://issues.jboss.org/browse/JBIDE-7631
		checkRetentionAnnotation(CDICOMPONENT.STEREOSCOPE, ed, className);

		// 3.QF - https://issues.jboss.org/browse/JBIDE-7634
		checkNamedAnnotation(CDICOMPONENT.STEREOSCOPE, ed, className);
		
		// 4.QF - https://issues.jboss.org/browse/JBIDE-7640
		checkTypedAnnotation(CDICOMPONENT.STEREOSCOPE, ed, className);
	}
	
	@Test
	public void testQualifiersQF() {
		String className = "Q2";
		prepareCdiComponent(CDICOMPONENT.QUALIFIER, className);
		
		// 1.QF - https://issues.jboss.org/browse/JBIDE-7631
		checkRetentionAnnotation(CDICOMPONENT.QUALIFIER, ed, className);
		
		// 2.QF - https://issues.jboss.org/browse/JBIDE-7632
		checkTargetAnnotation(CDICOMPONENT.QUALIFIER, ed, className);
	}
	
	@Test
	public void testScopeQF() {
		String className = "Scope1";
		prepareCdiComponent(CDICOMPONENT.SCOPE, className);
		
		// 1.QF - https://issues.jboss.org/browse/JBIDE-7631
		checkRetentionAnnotation(CDICOMPONENT.SCOPE, ed, className);
		
		// 2.QF - https://issues.jboss.org/browse/JBIDE-7633
		checkTargetAnnotation(CDICOMPONENT.SCOPE, ed, className);
	}
	
	@Test
	public void testBeanQF() {
		String className = "MyBean";
		prepareCdiComponent(CDICOMPONENT.BEAN, className);
		
		// 1.QF - https://issues.jboss.org/browse/JBIDE-7664
		checkConstructor(CDICOMPONENT.BEAN, ed, className);
		
		// 2.QF - https://issues.jboss.org/browse/JBIDE-7665
		checkProducerMethod(CDICOMPONENT.BEAN, ANNOTATIONS.DISPOSES, ed, className);
		
		// 3.QF - https://issues.jboss.org/browse/JBIDE-7667
		checkObserverDisposerMethod(CDICOMPONENT.BEAN, ed, className);
		
		// 4.QF - https://issues.jboss.org/browse/JBIDE-7668
		checkObserWithDisposParamMethod(CDICOMPONENT.BEAN, ed, className);
		
		// 5.QF - https://issues.jboss.org/browse/JBIDE-7680
		checkSessionBean(CDICOMPONENT.BEAN, ed, className);
	}
	
	@Test
	public void testInterDecorQF() {
		String className = "InterDecor";
		prepareCdiComponent(CDICOMPONENT.BEAN, className);
		
		// 1.QF - https://issues.jboss.org/browse/JBIDE-7636
		checkNamedAnnotation(CDICOMPONENT.BEAN, ed, className);
		
		// 2.QF - https://issues.jboss.org/browse/JBIDE-7683
		checkProducerMethod(CDICOMPONENT.BEAN, ANNOTATIONS.INTERCEPTOR, ed, className);
		
		// 3.QF - https://issues.jboss.org/browse/JBIDE-7684
		checkDisposesAnnotation(CDICOMPONENT.BEAN, ed, className);
		
		// 4.QF - https://issues.jboss.org/browse/JBIDE-7685
		checkObserveAnnotation(CDICOMPONENT.BEAN, ed, className);
		
		// 5.QF - https://issues.jboss.org/browse/JBIDE-7686
	}
	
	private void prepareCdiComponent(CDICOMPONENT component, String name) {
		createComponent(component, name);
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
		}
		util.waitForNonIgnoredJobs();
		ed = bot.activeEditor().toTextEditor();
	}
	
	private void prepareNamedAnnotation(SWTBotEclipseEditor ed, CDICOMPONENT comp, 
			String className, boolean add) {
		if (comp == CDICOMPONENT.BEAN) {
			CDIUtil.insertInEditor(ed, bot, 1, 0, "import javax.inject.Named;" + LINE_SEPARATOR);
			CDIUtil.insertInEditor(ed, bot, 3, 0, "@Named" + LINE_SEPARATOR);
			if (add) {
				CDIUtil.insertInEditor(ed, bot, 2, 0, "import javax.decorator.Decorator;" + LINE_SEPARATOR);
				CDIUtil.insertInEditor(ed, bot, 4, 0, "@Decorator" + LINE_SEPARATOR);
			} else {
				CDIUtil.replaceInEditor(ed, bot, "import javax.decorator.Decorator;", 
						"import javax.interceptor.Interceptor;");
				CDIUtil.replaceInEditor(ed, bot, "@Decorator", "@Interceptor");
			}
		} else {
			if (add) {
				CDIUtil.insertInEditor(ed, bot, ed.getLineCount()-4, 0, "@Named(\"Name\")" + LINE_SEPARATOR);
				CDIUtil.insertInEditor(ed, bot, 6 , 0, "import javax.inject.Named;" + LINE_SEPARATOR);
			} else {
				CDIUtil.replaceInEditor(ed, bot, "@Named", "");
				CDIUtil.replaceInEditor(ed, bot,
						"import javax.inject.Named;", "");
			}
		}
	}
	
	private void prepareTypedAnnotation(SWTBotEclipseEditor ed) {
		CDIUtil.insertInEditor(ed, bot, ed.getLineCount()-4 , 0, "@Typed" + LINE_SEPARATOR);
		CDIUtil.insertInEditor(ed, bot, 6 , 0, "import javax.enterprise.inject.Typed;" + LINE_SEPARATOR);
	}
	
	private void prepareInjectAnnot(SWTBotEclipseEditor ed) {
		CDIUtil.insertInEditor(ed, bot, 3 , 1, "@Inject" + LINE_SEPARATOR);
		CDIUtil.insertInEditor(ed, bot, 1 , 0, "import javax.inject.Inject;" + LINE_SEPARATOR);
		CDIUtil.insertInEditor(ed, bot, 6 , 15, "String aaa");
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
	
	private void prepareObserWithDisposParam(SWTBotEclipseEditor ed) {
		prepareProducer(ed);
		CDIUtil.replaceInEditor(ed, bot, "import javax.inject.Inject;", "import javax.enterprise.event.Observes;" + 
				LINE_SEPARATOR + "import javax.enterprise.inject.Disposes;" + LINE_SEPARATOR);
		CDIUtil.replaceInEditor(ed, bot, "@Inject", "");
		CDIUtil.replaceInEditor(ed, bot, "MyBean(String aaa)", 
				"void method(@Observes String param1, @Disposes String param2)");
	}
	
	private void prepareCheckSessionBean(String replacement) {
		if (replacement.equals("@Decorator")) {
			CDIUtil.insertInEditor(ed, bot, 3, 0, "import javax.decorator.Decorator;" + LINE_SEPARATOR);
			CDIUtil.insertInEditor(ed, bot, 4, 0, "import javax.ejb.Stateless;" + LINE_SEPARATOR);
			CDIUtil.insertInEditor(ed, bot, 5, 0, "@Decorator" + LINE_SEPARATOR);
			CDIUtil.insertInEditor(ed, bot, 6, 0, "@Stateless" + LINE_SEPARATOR);
		} else {
			CDIUtil.copyResourceToClass(ed, CDIQuickFixTest.class
					.getResourceAsStream("/resources/cdi/MyBean2.java.cdi"), false);
		}
	}
	
	private void prepareDisposesAnnot(SWTBotEclipseEditor ed) {
		CDIUtil.copyResourceToClass(ed, CDIQuickFixTest.class
				.getResourceAsStream("/resources/cdi/InterDecor.java.cdi"), false);
	}

	private void checkTargetAnnotation(CDICOMPONENT comp, SWTBotEclipseEditor ed, String className) {
		checkTargetAnnotWithReplac(comp, ed, className, "@Target({TYPE, FIELD})");
		checkTargetAnnotWithReplac(comp, ed, className, "");
	}
	
	private void checkTargetAnnotWithReplac(CDICOMPONENT comp, SWTBotEclipseEditor ed, 
			String className, String replacement) {
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
		checkQuickFix(ANNOTATIONS.TARGET, comp, className, replacement, ed);
	}
	
	private void checkRetentionAnnotation(CDICOMPONENT comp, SWTBotEclipseEditor ed, String className) {
		checkRetenAnnotWithReplac(comp, ed, className, "@Retention(CLASS)");
		checkRetenAnnotWithReplac(comp, ed, className, "");
	}

	private void checkRetenAnnotWithReplac(CDICOMPONENT comp, SWTBotEclipseEditor ed, 
			String className, String replacement) {
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
		checkQuickFix(ANNOTATIONS.RETENTION, comp, className, replacement, ed);
	}
		
	private void checkNamedAnnotation(CDICOMPONENT comp, SWTBotEclipseEditor ed,String className) {
		if (className.equals("InterDecor")) {
			prepareNamedAnnotation(ed, comp, className, true);
			checkNamedAnnotWithReplac(comp, ed, className, "@Named");
			prepareNamedAnnotation(ed, comp, className, false);
			checkNamedAnnotWithReplac(comp, ed, className, "@Named");
		} else {
			prepareNamedAnnotation(ed, comp, className, true);
			checkNamedAnnotWithReplac(comp, ed, className, "@Named");
			prepareNamedAnnotation(ed, comp, className, false);
			prepareNamedAnnotation(ed, comp, className, true);
			checkNamedAnnotWithReplac(comp, ed, className, "");
		}
	}
	
	private void checkNamedAnnotWithReplac(CDICOMPONENT comp, SWTBotEclipseEditor ed, 
			String className, String replacement) {
		checkQuickFix(ANNOTATIONS.NAMED, comp, className, replacement, ed);
	}
	
	private void checkTypedAnnotation(CDICOMPONENT comp, SWTBotEclipseEditor ed, String className) {
		prepareTypedAnnotation(ed);
		checkTypedAnnotWithReplac(comp, ed, className, "");
	}
	
	private void checkTypedAnnotWithReplac(CDICOMPONENT comp, SWTBotEclipseEditor ed,
			String className, String replacement) {
		checkQuickFix(ANNOTATIONS.TYPED, comp, className, replacement, ed);
	}
	
	private void checkConstructor(CDICOMPONENT comp, SWTBotEclipseEditor ed, String className) {
		prepareInjectAnnot(ed);
		checkConstructorWithReplac(comp, ed, className, "@Disposes");
		checkConstructorWithReplac(comp, ed, className, "@Observes");
	}
	
	private void checkConstructorWithReplac(CDICOMPONENT comp, SWTBotEclipseEditor ed, 
			String className, String replacement) {
		CDIUtil.insertInEditor(ed, bot, 6, 15, replacement + " ");
		dispObserCompletion(comp, ed, className, replacement);
	}
	
	private void checkProducerMethod(CDICOMPONENT comp, ANNOTATIONS annonType, SWTBotEclipseEditor ed,
			String className) {
		if (className.equals("InterDecor")) {
			prepareProducer(ed);
			CDIUtil.replaceInEditor(ed, bot, "MyBean", "InterDecor");
			checkProducerWithReplac(comp, annonType, ed, className, "@Interceptor");
			prepareProducer(ed);
			CDIUtil.replaceInEditor(ed, bot, "MyBean", "InterDecor");
			checkProducerWithReplac(comp, annonType, ed, className, "@Decorator");
		} else {
			prepareProducer(ed);
			checkProducerWithReplac(comp, annonType, ed, className, "@Disposes");
			checkProducerWithReplac(comp, annonType, ed, className, "@Observes");
		}
	}
	
	private void checkProducerWithReplac(CDICOMPONENT comp, ANNOTATIONS annonType, 
			SWTBotEclipseEditor ed, String className, String replacement) {
		if (className.equals("InterDecor")) {
			String annot = replacement.equals("@Interceptor")?"@Interceptor":"@Decorator";
			String importAnnot = replacement.equals("@Interceptor")?
					"import javax.interceptor.Interceptor;":
					"import javax.decorator.Decorator;";
			CDIUtil.insertInEditor(ed, bot, 3, 0, annot + LINE_SEPARATOR);
			CDIUtil.insertInEditor(ed, bot, 1, 0,  importAnnot + LINE_SEPARATOR);
			checkQuickFix(replacement.equals("@Interceptor")?ANNOTATIONS.INTERCEPTOR:
				ANNOTATIONS.DECORATOR, comp, className, replacement, ed);
		} else {
			CDIUtil.insertInEditor(ed, bot, 11, 29, replacement + " ");
			dispObserCompletion(comp, ed, className, replacement);
		}
	}
	
	private void checkObserverDisposerMethod(CDICOMPONENT comp, SWTBotEclipseEditor ed, String className) {
		prepareObserverDisposer(ed);
		checkObserverDisposerWithReplac(comp, ed, className, "@Disposes");
		checkObserverDisposerWithReplac(comp, ed, className, "@Observes");
	}
	
	private void checkObserverDisposerWithReplac(CDICOMPONENT comp, 
			SWTBotEclipseEditor ed, String className, String replacement) {
		CDIUtil.insertInEditor(ed, bot, 10, 20, replacement + " ");
		dispObserCompletion(comp, ed, className, replacement);
	}
	
	private void checkObserWithDisposParamMethod(CDICOMPONENT comp, SWTBotEclipseEditor ed, String className) {
		prepareObserWithDisposParam(ed);
		checkObserWithDisposParamWithReplac(comp, ed, className, "@Disposes");
		checkObserWithDisposParamWithReplac(comp, ed, className, "@Observes");
	}
	
	private void checkObserWithDisposParamWithReplac(CDICOMPONENT comp, 
			SWTBotEclipseEditor ed, String className, String replacement) {
		if (replacement.equals("@Observes")) {
			CDIUtil.insertInEditor(ed, bot, 3, 0, "import javax.enterprise.inject.Disposes;");
			CDIUtil.insertInEditor(ed, bot, 6, 46, "@Disposes "); 
		}
		checkQuickFix(replacement.equals("@Disposes")?ANNOTATIONS.DISPOSES:ANNOTATIONS.OBSERVES, 
				CDICOMPONENT.BEAN, className, "", ed);
	}
	
	private void checkSessionBean(CDICOMPONENT comp, SWTBotEclipseEditor ed, String className) {
		checkSessionBeanWithReplac(comp, ed, className, "@Decorator");
		checkSessionBeanWithReplac(comp, ed, className, "@Interceptor");
	}
	
	private void checkSessionBeanWithReplac(CDICOMPONENT comp, SWTBotEclipseEditor ed, 
			String className, String replacement) {
		prepareCheckSessionBean(replacement);
		checkQuickFix(replacement.equals("@Decorator")?ANNOTATIONS.DECORATOR:ANNOTATIONS.INTERCEPTOR, 
				comp, className, replacement, ed);
	}
	
	private void dispObserCompletion(CDICOMPONENT comp, SWTBotEclipseEditor ed, String className, String replacement) {
		CDIUtil.insertInEditor(ed, bot, 2 , 0, "import javax.enterprise." + 
				(replacement.contains("Disposes")?"inject.":"event.") + 
				(replacement.substring(1) + ";" + LINE_SEPARATOR));
		ANNOTATIONS annonType = (replacement.equals("@Disposes")?ANNOTATIONS.DISPOSES:ANNOTATIONS.OBSERVES);
		checkQuickFix(annonType, comp, className, replacement, ed);
	}
	
	private void checkDisposesAnnotation(CDICOMPONENT comp, SWTBotEclipseEditor ed, String className) {
		checkDisposesAnnotWithReplac(comp, ed, className, "@Decorator");
		checkDisposesAnnotWithReplac(comp, ed, className, "@Interceptor");
	}
	
	private void checkDisposesAnnotWithReplac(CDICOMPONENT comp, SWTBotEclipseEditor ed, 
			String className, String replacement) {
		prepareDisposesAnnot(ed);
		String annot = replacement;
		String importAnnot = "import javax." + replacement.substring(1).toLowerCase() 
				+ "." + replacement.substring(1) + ";";
		CDIUtil.insertInEditor(ed, bot, 2, 0, annot + LINE_SEPARATOR);
		CDIUtil.insertInEditor(ed, bot, 1, 0, importAnnot + LINE_SEPARATOR);
		checkQuickFix(ANNOTATIONS.DECORATOR, comp, className, replacement, ed);
	}
	
	private void checkObserveAnnotation(CDICOMPONENT comp, SWTBotEclipseEditor ed, String className) {
		checkObserveAnnotWithReplac(comp, ed, className, "@Decorator");
		checkObserveAnnotWithReplac(comp, ed, className, "@Interceptor");
	}
	
	private void checkObserveAnnotWithReplac(CDICOMPONENT comp, SWTBotEclipseEditor ed, 
			String className, String replacement) {
		prepareDisposesAnnot(ed);
		CDIUtil.replaceInEditor(ed, bot, "@Disposes", "@Observes");
		CDIUtil.replaceInEditor(ed, bot, "import javax.enterprise.inject.Disposes;", 
				"import javax.enterprise.event.Observes;");
		CDIUtil.replaceInEditor(ed, bot, "dispose", "observe");
		String annot = replacement;
		String importAnnot = "import javax." + replacement.substring(1).toLowerCase() 
				+ "." + replacement.substring(1) + ";";
		CDIUtil.insertInEditor(ed, bot, 2, 0, annot + LINE_SEPARATOR);
		CDIUtil.insertInEditor(ed, bot, 1, 0, importAnnot + LINE_SEPARATOR);
		checkQuickFix(ANNOTATIONS.DECORATOR, comp, className, replacement, ed);
	}
	
	private void checkQuickFix(ANNOTATIONS annonType, CDICOMPONENT comp, String className, String replacement,
			SWTBotEclipseEditor ed) {
		String componentClass = className + ".java";
		problemsTrees = getProblems(annonType, comp, componentClass);
		assertTrue(problemsTrees.length != 0);
		resolveQuickFix(annonType, comp, replacement, ed);
		problemsTrees = getProblems(annonType, comp, componentClass);
		assertTrue(problemsTrees.length == 0);
	}
	
	private SWTBotTreeItem[] getProblems(ANNOTATIONS annonType, CDICOMPONENT comp, String className) {
		SWTBotTreeItem[] problemsTree;
		boolean warningType = true;
		switch (annonType) {
		case NAMED:
			warningType = ((comp == CDICOMPONENT.BEAN)?true:false);
			break;
		case TYPED:
		case DISPOSES:
		case OBSERVES:
		case DECORATOR:
		case INTERCEPTOR:
			warningType = false;
			break;
		}
		String problemsContains = null;
		if (warningType) {
			problemsTree = ProblemsView.getFilteredWarningsTreeItems(bot, problemsContains, "/"
					+ PROJECT_NAME, className, "CDI Problem");
		} else {
			if (className.equals("InterDecor.java")) {
				if (ed.toTextEditor().getText().contains("produceString")) {
					problemsContains = "Producer cannot be declared in";
				}
				if (ed.toTextEditor().getText().contains("disposeMethod")) {
					problemsContains = "has a method annotated @Disposes";
				}
				if (ed.toTextEditor().getText().contains("observeMethod")) {
					problemsContains = "have a method with a parameter annotated @Observes";
				}
			}
			problemsTree = ProblemsView.getFilteredErrorsTreeItems(bot, problemsContains, "/"
					+ PROJECT_NAME, className, "CDI Problem");
		}
		return problemsTree;
	}
	
	private void resolveQuickFix(ANNOTATIONS annonType, CDICOMPONENT comp, String replacement,
			SWTBotEclipseEditor ed) {
		int index = indexDetermine(annonType, comp, replacement);
		resolve(annonType, replacement, index);
	}
	
	private int indexDetermine(ANNOTATIONS annonType, CDICOMPONENT comp, String replacement) {
		boolean chooseFirstOption = true;
		if (annonType == ANNOTATIONS.NAMED && replacement.equals("")) {
			chooseFirstOption = false;
		} 
		if ((comp == CDICOMPONENT.BEAN && annonType == ANNOTATIONS.DISPOSES) ||
			(comp == CDICOMPONENT.BEAN && annonType == ANNOTATIONS.OBSERVES)) {
			if ((problemsTrees[0].getText().contains("Producer method has a parameter annotated "
					+ replacement)) || 
				(problemsTrees[0].getText().contains("method cannot be annotated @Inject")) ||
				(annonType == ANNOTATIONS.OBSERVES && 
					problemsTrees[0].getText().contains("Observer method has a parameter annotated @Disposes"))) {
				chooseFirstOption = false;
			}
		}
		return chooseFirstOption?0:1;
	}
	
	private void resolve(ANNOTATIONS annonType, String replacement, int index) {
		CDIUtil.openQuickFix(problemsTrees[0], bot);
		bot.table(0).click(index, 0);
		assertFalse(bot.button("Finish").isEnabled());
		bot.table(1).getTableItem(0).check();
		assertTrue(bot.button("Finish").isEnabled());
		bot.clickButton("Finish");
		bot.sleep(Timing.time1S());
		util.waitForNonIgnoredJobs();
		clearImports(annonType, replacement);
	}
	
	private void clearImports(ANNOTATIONS annonType, String replacement) {
		if (annonType == ANNOTATIONS.RETENTION && replacement.equals("@Retention(CLASS)")) {
			CDIUtil.replaceInEditor(ed, bot, 
					"import static java.lang.annotation.RetentionPolicy.CLASS;","");
		}
	}

}
