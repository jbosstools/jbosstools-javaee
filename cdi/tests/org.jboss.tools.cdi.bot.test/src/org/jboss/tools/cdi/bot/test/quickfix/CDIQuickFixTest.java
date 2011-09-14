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
	private enum ANNOTATIONS {TARGET, RETENTION, NAMED, TYPED, DISPOSES, OBSERVES, INTERCEPTOR, 
							  SPECIALIZES, DECORATOR}
	private enum CDICOMPONENT {STEREOSCOPE, QUALIFIER, SCOPE, BEAN, ANNOTATION, INTERBINDING}
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
		checkTargetAnnotation(CDICOMPONENT.STEREOSCOPE, className);
		
		// 2.QF - https://issues.jboss.org/browse/JBIDE-7631
		checkRetentionAnnotation(CDICOMPONENT.STEREOSCOPE, className);

		// 3.QF - https://issues.jboss.org/browse/JBIDE-7634
		checkNamedAnnotation(CDICOMPONENT.STEREOSCOPE, className);
		
		// 4.QF - https://issues.jboss.org/browse/JBIDE-7640
		checkTypedAnnotation(CDICOMPONENT.STEREOSCOPE, className);
	}
	
	@Test
	public void testQualifiersQF() {
		String className = "Q2";
		prepareCdiComponent(CDICOMPONENT.QUALIFIER, className);
		
		// 1.QF - https://issues.jboss.org/browse/JBIDE-7631
		checkRetentionAnnotation(CDICOMPONENT.QUALIFIER, className);
		
		// 2.QF - https://issues.jboss.org/browse/JBIDE-7632
		checkTargetAnnotation(CDICOMPONENT.QUALIFIER, className);
	}
	
	@Test
	public void testScopeQF() {
		String className = "Scope1";
		prepareCdiComponent(CDICOMPONENT.SCOPE, className);
		
		// 1.QF - https://issues.jboss.org/browse/JBIDE-7631
		checkRetentionAnnotation(CDICOMPONENT.SCOPE, className);
		
		// 2.QF - https://issues.jboss.org/browse/JBIDE-7633
		checkTargetAnnotation(CDICOMPONENT.SCOPE, className);
	}
	
	@Test
	public void testBeanQF() {
		String className = "MyBean";
		prepareCdiComponent(CDICOMPONENT.BEAN, className);
		
		// 1.QF - https://issues.jboss.org/browse/JBIDE-7664
		checkConstructor(CDICOMPONENT.BEAN, className);
		
		// 2.QF - https://issues.jboss.org/browse/JBIDE-7665
		checkProducerMethod(CDICOMPONENT.BEAN, ANNOTATIONS.DISPOSES, className);
		
		// 3.QF - https://issues.jboss.org/browse/JBIDE-7667
		checkObserverDisposerMethod(CDICOMPONENT.BEAN, className);
		
		// 4.QF - https://issues.jboss.org/browse/JBIDE-7668
		checkObserWithDisposParamMethod(CDICOMPONENT.BEAN, className);
		
		// 5.QF - https://issues.jboss.org/browse/JBIDE-7680
		checkSessionBean(CDICOMPONENT.BEAN, className);
	}
	
	@Test
	public void testInterDecorQF() {
		String className = "InterDecor";
		prepareCdiComponent(CDICOMPONENT.BEAN, className);
		
		// 1.QF - https://issues.jboss.org/browse/JBIDE-7636
		checkNamedAnnotation(CDICOMPONENT.BEAN, className);
		
		// 2.QF - https://issues.jboss.org/browse/JBIDE-7683
		checkProducerMethod(CDICOMPONENT.BEAN, ANNOTATIONS.INTERCEPTOR, className);
		
		// 3.QF - https://issues.jboss.org/browse/JBIDE-7684
		checkDisposesAnnotation(CDICOMPONENT.BEAN, className);
		
		// 4.QF - https://issues.jboss.org/browse/JBIDE-7685
		checkObserveAnnotation(CDICOMPONENT.BEAN, className);
		
		// 5.QF - https://issues.jboss.org/browse/JBIDE-7686
		checkSpecializeAnnotation(CDICOMPONENT.BEAN, "TestBean");
		
		// https://issues.jboss.org/browse/JBIDE-7641 - NIE INTER DECOR
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
		case INTERBINDING:
			CDIUtil.binding(PACKAGE_NAME, name, null, true, false).finish();
			break;
		case ANNOTATION:
			
		}
		util.waitForNonIgnoredJobs();
		ed = bot.activeEditor().toTextEditor();
	}
	
	private void prepareNamedAnnotation(CDICOMPONENT comp, 
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
	
	private void prepareTypedAnnotation() {
		CDIUtil.insertInEditor(ed, bot, ed.getLineCount()-4 , 0, "@Typed" + LINE_SEPARATOR);
		CDIUtil.insertInEditor(ed, bot, 6 , 0, "import javax.enterprise.inject.Typed;" + LINE_SEPARATOR);
	}
	
	private void prepareInjectAnnot() {
		CDIUtil.insertInEditor(ed, bot, 3 , 1, "@Inject" + LINE_SEPARATOR);
		CDIUtil.insertInEditor(ed, bot, 1 , 0, "import javax.inject.Inject;" + LINE_SEPARATOR);
		CDIUtil.insertInEditor(ed, bot, 6 , 15, "String aaa");
	}
	
	private void prepareProducer() {
		CDIUtil.copyResourceToClass(ed, CDIQuickFixTest.class
				.getResourceAsStream("/resources/cdi/MyBean.java.cdi"), false);
	}
	
	private void prepareObserverDisposer() {
		prepareProducer();
		CDIUtil.replaceInEditor(ed, bot, "@Produces", "@Inject");
		CDIUtil.replaceInEditor(ed, bot, "import javax.enterprise.inject.Produces;", "");
		CDIUtil.replaceInEditor(ed, bot, "String produceString", "void method");
		CDIUtil.replaceInEditor(ed, bot, "return \"test\";", "");
	}
	
	private void prepareObserWithDisposParam() {
		prepareProducer();
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
	
	private void prepareDisposesAnnot() {
		CDIUtil.copyResourceToClass(ed, CDIQuickFixTest.class
				.getResourceAsStream("/resources/cdi/InterDecor.java.cdi"), false);
	}
	
	private void prepareComponentsForSpecializeAnnotation(String testBeanName) {
		createComponent(CDICOMPONENT.BEAN, "AnyBean");
		createComponent(CDICOMPONENT.INTERBINDING, "AnyBinding");		
		createComponent(CDICOMPONENT.BEAN, testBeanName);
		CDIUtil.copyResourceToClass(ed, CDIQuickFixTest.class
				.getResourceAsStream("/resources/cdi/TestBean.java.cdi"), false);
	}

	private void checkTargetAnnotation(CDICOMPONENT comp, String className) {
		checkTargetAnnotWithAddon(comp, className, "@Target({TYPE, FIELD})");
		checkTargetAnnotWithAddon(comp, className, "");
	}
	
	private void checkTargetAnnotWithAddon(CDICOMPONENT comp, String className, 
			String replacement) {
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
		checkQuickFix(ANNOTATIONS.TARGET, comp, className, replacement);
	}
	
	private void checkRetentionAnnotation(CDICOMPONENT comp, String className) {
		checkRetentionAnnotWithAddon(comp, className, "@Retention(CLASS)");
		checkRetentionAnnotWithAddon(comp, className, "");
	}

	private void checkRetentionAnnotWithAddon(CDICOMPONENT comp, String className, 
			String replacement) {
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
		checkQuickFix(ANNOTATIONS.RETENTION, comp, className, replacement);
	}
		
	private void checkNamedAnnotation(CDICOMPONENT comp, String className) {
		if (className.equals("InterDecor")) {
			prepareNamedAnnotation(comp, className, true);
			checkNamedAnnotWithAddon(comp, className, "@Named");
			prepareNamedAnnotation(comp, className, false);
			checkNamedAnnotWithAddon(comp, className, "@Named");
		} else {
			prepareNamedAnnotation(comp, className, true);
			checkNamedAnnotWithAddon(comp, className, "@Named");
			prepareNamedAnnotation(comp, className, false);
			prepareNamedAnnotation(comp, className, true);
			checkNamedAnnotWithAddon(comp, className, "");
		}
	}
	
	private void checkNamedAnnotWithAddon(CDICOMPONENT comp, String className, 
			String replacement) {
		checkQuickFix(ANNOTATIONS.NAMED, comp, className, replacement);
	}
	
	private void checkTypedAnnotation(CDICOMPONENT comp, String className) {
		prepareTypedAnnotation();
		checkTypedAnnotWithAddon(comp, className, "");
	}
	
	private void checkTypedAnnotWithAddon(CDICOMPONENT comp, String className, 
			String replacement) {
		checkQuickFix(ANNOTATIONS.TYPED, comp, className, replacement);
	}
	
	private void checkConstructor(CDICOMPONENT comp, String className) {
		prepareInjectAnnot();
		checkConstructorWithAddon(comp, className, "@Disposes");
		checkConstructorWithAddon(comp, className, "@Observes");
	}
	
	private void checkConstructorWithAddon(CDICOMPONENT comp, 
			String className, String replacement) {
		CDIUtil.insertInEditor(ed, bot, 6, 15, replacement + " ");
		dispObserCompletion(comp, className, replacement);
	}
	
	private void checkProducerMethod(CDICOMPONENT comp, ANNOTATIONS annonType, String className) {
		if (className.equals("InterDecor")) {
			prepareProducer();
			CDIUtil.replaceInEditor(ed, bot, "MyBean", "InterDecor");
			checkProducerWithAddon(comp, annonType, className, "@Interceptor");
			prepareProducer();
			CDIUtil.replaceInEditor(ed, bot, "MyBean", "InterDecor");
			checkProducerWithAddon(comp, annonType, className, "@Decorator");
		} else {
			prepareProducer();
			checkProducerWithAddon(comp, annonType, className, "@Disposes");
			checkProducerWithAddon(comp, annonType, className, "@Observes");
		}
	}
	
	private void checkProducerWithAddon(CDICOMPONENT comp, ANNOTATIONS annonType, 
			String className, String replacement) {
		if (className.equals("InterDecor")) {
			String annot = replacement.equals("@Interceptor")?"@Interceptor":"@Decorator";
			String importAnnot = replacement.equals("@Interceptor")?
					"import javax.interceptor.Interceptor;":
					"import javax.decorator.Decorator;";
			CDIUtil.insertInEditor(ed, bot, 3, 0, annot + LINE_SEPARATOR);
			CDIUtil.insertInEditor(ed, bot, 1, 0,  importAnnot + LINE_SEPARATOR);
			checkQuickFix(replacement.equals("@Interceptor")?ANNOTATIONS.INTERCEPTOR:
				ANNOTATIONS.DECORATOR, comp, className, replacement);
		} else {
			CDIUtil.insertInEditor(ed, bot, 11, 29, replacement + " ");
			dispObserCompletion(comp, className, replacement);
		}
	}
	
	private void checkObserverDisposerMethod(CDICOMPONENT comp, String className) {
		prepareObserverDisposer();
		checkObserverDisposerWithAddon(comp, className, "@Disposes");
		checkObserverDisposerWithAddon(comp, className, "@Observes");
	}
	
	private void checkObserverDisposerWithAddon(CDICOMPONENT comp, 
			String className, String replacement) {
		CDIUtil.insertInEditor(ed, bot, 10, 20, replacement + " ");
		dispObserCompletion(comp, className, replacement);
	}
	
	private void checkObserWithDisposParamMethod(CDICOMPONENT comp, String className) {
		prepareObserWithDisposParam();
		checkObserWithDisposParamWithAddon(comp, className, "@Disposes");
		checkObserWithDisposParamWithAddon(comp, className, "@Observes");
	}
	
	private void checkObserWithDisposParamWithAddon(CDICOMPONENT comp, 
			String className, String replacement) {
		if (replacement.equals("@Observes")) {
			CDIUtil.insertInEditor(ed, bot, 3, 0, "import javax.enterprise.inject.Disposes;");
			CDIUtil.insertInEditor(ed, bot, 6, 46, "@Disposes "); 
		}
		checkQuickFix(replacement.equals("@Disposes")?ANNOTATIONS.DISPOSES:ANNOTATIONS.OBSERVES, 
				CDICOMPONENT.BEAN, className, "");
	}
	
	private void checkSessionBean(CDICOMPONENT comp, String className) {
		checkSessionBeanWithAddon(comp, className, "@Decorator");
		checkSessionBeanWithAddon(comp, className, "@Interceptor");
	}
	
	private void checkSessionBeanWithAddon(CDICOMPONENT comp, 
			String className, String replacement) {
		prepareCheckSessionBean(replacement);
		checkQuickFix(replacement.equals("@Decorator")?ANNOTATIONS.DECORATOR:ANNOTATIONS.INTERCEPTOR, 
				comp, className, replacement);
	}
	
	private void dispObserCompletion(CDICOMPONENT comp, String className, String replacement) {
		CDIUtil.insertInEditor(ed, bot, 2 , 0, "import javax.enterprise." + 
				(replacement.contains("Disposes")?"inject.":"event.") + 
				(replacement.substring(1) + ";" + LINE_SEPARATOR));
		ANNOTATIONS annonType = (replacement.equals("@Disposes")?ANNOTATIONS.DISPOSES:ANNOTATIONS.OBSERVES);
		checkQuickFix(annonType, comp, className, replacement);
	}
	
	private void checkDisposesAnnotation(CDICOMPONENT comp, String className) {
		checkDisposesAnnotWithAddon(comp, className, "@Decorator");
		checkDisposesAnnotWithAddon(comp, className, "@Interceptor");
	}
	
	private void checkDisposesAnnotWithAddon(CDICOMPONENT comp, String className, 
			String replacement) {
		prepareDisposesAnnot();
		String annot = replacement;
		String importAnnot = "import javax." + replacement.substring(1).toLowerCase() 
				+ "." + replacement.substring(1) + ";";
		CDIUtil.insertInEditor(ed, bot, 2, 0, annot + LINE_SEPARATOR);
		CDIUtil.insertInEditor(ed, bot, 1, 0, importAnnot + LINE_SEPARATOR);
		checkQuickFix(ANNOTATIONS.DECORATOR, comp, className, replacement);
	}
	
	private void checkObserveAnnotation(CDICOMPONENT comp, String className) {
		checkObserveAnnotWithAddon(comp, className, "@Decorator");
		checkObserveAnnotWithAddon(comp, className, "@Interceptor");
	}
	
	private void checkObserveAnnotWithAddon(CDICOMPONENT comp, String className, 
			String replacement) {
		prepareDisposesAnnot();
		CDIUtil.replaceInEditor(ed, bot, "@Disposes", "@Observes");
		CDIUtil.replaceInEditor(ed, bot, "import javax.enterprise.inject.Disposes;", 
				"import javax.enterprise.event.Observes;");
		CDIUtil.replaceInEditor(ed, bot, "dispose", "observe");
		String annot = replacement;
		String importAnnot = "import javax." + replacement.substring(1).toLowerCase() 
				+ "." + replacement.substring(1) + ";";
		CDIUtil.insertInEditor(ed, bot, 2, 0, annot + LINE_SEPARATOR);
		CDIUtil.insertInEditor(ed, bot, 1, 0, importAnnot + LINE_SEPARATOR);
		checkQuickFix(ANNOTATIONS.DECORATOR, comp, className, replacement);
	}
	
	private void checkSpecializeAnnotation(CDICOMPONENT comp, String className) {
		prepareComponentsForSpecializeAnnotation(className);
		checkSpecializeAnnotWithAddon(comp, className, "@Interceptor");
		checkSpecializeAnnotWithAddon(comp, className, "@Decorator");
	}
	
	private void checkSpecializeAnnotWithAddon(CDICOMPONENT comp, String className, 
			String replacement) {		
		if (replacement.equals("@Decorator")) {
			CDIUtil.copyResourceToClass(ed, 
					CDIQuickFixTest.class.getResourceAsStream("/resources/cdi/TestBean2.java.cdi"), false);
		}
		checkQuickFix(ANNOTATIONS.SPECIALIZES, comp, className, replacement);
	}
	
	private void checkQuickFix(ANNOTATIONS annonType, CDICOMPONENT comp, 
			String className, String replacement) {
		String componentClass = className + ".java";
		problemsTrees = getProblems(annonType, comp, componentClass);
		assertTrue(problemsTrees.length != 0);
		resolveQuickFix(annonType, comp, replacement);
		problemsTrees = getProblems(annonType, comp, componentClass);
		assertTrue(problemsTrees.length == 0);
	}
	
	private SWTBotTreeItem[] getProblems(ANNOTATIONS annonType, CDICOMPONENT comp, String className) {
		SWTBotTreeItem[] problemsTree;
		boolean warningType = true;
		switch (annonType) {
		case NAMED:
		case SPECIALIZES:
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
			if (annonType == ANNOTATIONS.SPECIALIZES) {
				problemsContains = "@Specializes";
			}
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
	
	private void resolveQuickFix(ANNOTATIONS annonType, CDICOMPONENT comp, String replacement) {
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
