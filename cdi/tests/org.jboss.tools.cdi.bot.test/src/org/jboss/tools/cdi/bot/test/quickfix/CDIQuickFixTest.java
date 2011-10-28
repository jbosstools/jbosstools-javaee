/*******************************************************************************
 * Copyright (c) 2010 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.cdi.bot.test.quickfix;


import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.jboss.tools.cdi.bot.test.CDIAllBotTests;
import org.jboss.tools.cdi.bot.test.uiutils.actions.CDIBase;
import org.jboss.tools.cdi.bot.test.uiutils.actions.CDIUtil;
import org.jboss.tools.ui.bot.ext.RequirementAwareSuite;
import org.jboss.tools.ui.bot.ext.Timing;
import org.jboss.tools.ui.bot.ext.config.Annotations.Require;
import org.jboss.tools.ui.bot.ext.config.Annotations.Server;
import org.jboss.tools.ui.bot.ext.config.Annotations.ServerState;
import org.jboss.tools.ui.bot.ext.view.ProblemsView;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Test operates on quick fixes of CDI components validation
 * 
 * @author Jaroslav Jankovic
 */

@Require(clearProjects = false, perspective = "Java EE", server = @Server(state = ServerState.NotRunning, version = "6.0", operator = ">="))
@RunWith(RequirementAwareSuite.class)
@SuiteClasses({ CDIAllBotTests.class })
public class CDIQuickFixTest extends CDIBase {

	private static final String PROJECT_NAME = "CDIProject";
	private static final String PACKAGE_NAME = "cdi";
	private static SWTBotTreeItem[] problemsTrees;
	private static final String LINE_SEPARATOR = System.getProperty("line.separator");	
	
	
	@BeforeClass
	public static void checkAndCreateProject() {
		if (!projectExists(PROJECT_NAME)) {
			createAndCheckCDIProject(bot, util, projectExplorer,PROJECT_NAME);
		}
	}
	
	@AfterClass
	public static void clean() {		
		removeObjectInProjectExplorer(PACKAGE_NAME, PROJECT_NAME + "/Java Resources/src");		
	}
	
	/*
	 * check problems (warnings and errors in Problems View)
	 */
	@After
	public void waitForJobs() {
		checkProjectAllProblems();
		util.waitForNonIgnoredJobs();
	}
	
	
	@Test
	public void testSerializableQF() {
		
		
		String className = "B1";
		createComponent(CDICOMPONENT.BEAN, className, PACKAGE_NAME, null);
		
		// https://issues.jboss.org/browse/JBIDE-8550
		checkSerializableAnnotation(CDICOMPONENT.BEAN, className);
	}
	
	@Test
	public void testMultipleBeansQF() {
		String className = "BrokenFarm";
		createComponent(CDICOMPONENT.BEAN, className, PACKAGE_NAME, null);
		
		// https://issues.jboss.org/browse/JBIDE-7635
		checkMultipleBeans(CDICOMPONENT.BEAN, className);
	}
	
	/*
	 * CDI Quick Fix test operates over validation 
	 * concerning about Stereoscope component
	 */
	
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
	
	/*
	 * CDI Quick Fix test operates over validation 
	 * concerning about Qualifier component
	 */
	
	@Test
	public void testQualifiersQF() {
		String className = "Q2";
		prepareCdiComponent(CDICOMPONENT.QUALIFIER, className);
		
		// 1.QF - https://issues.jboss.org/browse/JBIDE-7631
		checkRetentionAnnotation(CDICOMPONENT.QUALIFIER, className);
		
		// 2.QF - https://issues.jboss.org/browse/JBIDE-7632
		checkTargetAnnotation(CDICOMPONENT.QUALIFIER, className);
		
		// 3.QF - https://issues.jboss.org/browse/JBIDE-7641
		checkNonBindingAnnotation(CDICOMPONENT.QUALIFIER, className);
		
	}
	
	/*
	 * CDI Quick Fix test operates over validation 
	 * concerning about Scope component
	 */
	
	@Test
	public void testScopeQF() {
		String className = "Scope1";
		prepareCdiComponent(CDICOMPONENT.SCOPE, className);
		
		// 1.QF - https://issues.jboss.org/browse/JBIDE-7631
		checkRetentionAnnotation(CDICOMPONENT.SCOPE, className);
		
		// 2.QF - https://issues.jboss.org/browse/JBIDE-7633
		checkTargetAnnotation(CDICOMPONENT.SCOPE, className);
	}
	
	/*
	 * CDI Quick Fix test operates over validation 
	 * concerning about general Bean component
	 */
	
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
	
	/*
	 * CDI Quick Fix test operates over validation 
	 * concerning about Interceptors/Interceptor Binding/Decorator component
	 */
	
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
		
		// 6.QF - https://issues.jboss.org/browse/JBIDE-7641
		checkNonBindingAnnotation(CDICOMPONENT.INTERBINDING, "Interceptor");
	}
	
	
	/*
	 * method edits default Target form because of "one space inconsistency"
	 */
	private void prepareCdiComponent(CDICOMPONENT component, String name) {
		createComponent(component, name, PACKAGE_NAME, null);
		switch (component) {
		case QUALIFIER:
			CDIUtil.replaceInEditor(getEd(), bot, "@Target({ TYPE, METHOD, PARAMETER, FIELD })",
					"@Target({TYPE, METHOD, PARAMETER, FIELD})");
			break;
		case STEREOSCOPE:
		case SCOPE:
			CDIUtil.replaceInEditor(getEd(), bot, "@Target({ TYPE, METHOD, FIELD })",
					"@Target({TYPE, METHOD, FIELD})");
			break;
		}
	}
	
	/*
	 ****************************************************************
	 * 
	 * "prepare" like methods which prepare structure of project
	 * before testing itself
	 * 
	 ****************************************************************
	 */
	
	private void prepareSerializableAnnotation() {
		CDIUtil.copyResourceToClass(getEd(), CDIQuickFixTest.class
				.getResourceAsStream("/resources/cdi/B1.java.cdi"), false);
	}
	
	private void prepareMultipleBeans(String className) {
		createComponent(CDICOMPONENT.BEAN, "Animal", PACKAGE_NAME, null);
		createComponent(CDICOMPONENT.BEAN, "Dog", PACKAGE_NAME, null);
		CDIUtil.copyResourceToClass(getEd(), CDIQuickFixTest.class
				.getResourceAsStream("/resources/cdi/Dog.java.cdi"), false);
		createComponent(CDICOMPONENT.QUALIFIER, "Q1", PACKAGE_NAME, null);
		bot.editorByTitle(className + ".java").show();
		setEd(bot.activeEditor().toTextEditor());
		CDIUtil.copyResourceToClass(getEd(), CDIQuickFixTest.class
				.getResourceAsStream("/resources/cdi/BrokenFarm.java.cdi"),
				false);
		
	}
	
	private void prepareNamedAnnotation(CDICOMPONENT comp, 
			String className, boolean add) {
		if (comp == CDICOMPONENT.BEAN) {
			CDIUtil.insertInEditor(getEd(), bot, 1, 0, "import javax.inject.Named;" + LINE_SEPARATOR);
			CDIUtil.insertInEditor(getEd(), bot, 3, 0, "@Named" + LINE_SEPARATOR);
			if (add) {
				CDIUtil.insertInEditor(getEd(), bot, 2, 0, "import javax.decorator.Decorator;" + LINE_SEPARATOR);
				CDIUtil.insertInEditor(getEd(), bot, 4, 0, "@Decorator" + LINE_SEPARATOR);
			} else {
				CDIUtil.replaceInEditor(getEd(), bot, "import javax.decorator.Decorator;", 
						"import javax.interceptor.Interceptor;");
				CDIUtil.replaceInEditor(getEd(), bot, "@Decorator", "@Interceptor");
			}
		} else {
			if (add) {
				CDIUtil.insertInEditor(getEd(), bot, getEd().getLineCount()-4, 0, "@Named(\"Name\")" + LINE_SEPARATOR);
				CDIUtil.insertInEditor(getEd(), bot, 6 , 0, "import javax.inject.Named;" + LINE_SEPARATOR);
			} else {
				CDIUtil.replaceInEditor(getEd(), bot, "@Named", "");
				CDIUtil.replaceInEditor(getEd(), bot,
						"import javax.inject.Named;", "");
			}
		}
	}
	
	private void prepareTypedAnnotation() {
		CDIUtil.insertInEditor(getEd(), bot, getEd().getLineCount()-4 , 0, "@Typed" + LINE_SEPARATOR);
		CDIUtil.insertInEditor(getEd(), bot, 6 , 0, "import javax.enterprise.inject.Typed;" + LINE_SEPARATOR);
	}
	
	private void prepareInjectAnnot() {
		CDIUtil.insertInEditor(getEd(), bot, 3 , 1, "@Inject" + LINE_SEPARATOR);
		CDIUtil.insertInEditor(getEd(), bot, 1 , 0, "import javax.inject.Inject;" + LINE_SEPARATOR);
		CDIUtil.insertInEditor(getEd(), bot, 6 , 15, "String aaa");
	}
	
	private void prepareProducer() {
		CDIUtil.copyResourceToClass(getEd(), CDIQuickFixTest.class
				.getResourceAsStream("/resources/cdi/MyBean.java.cdi"), false);
	}
	
	private void prepareObserverDisposer() {
		prepareProducer();
		CDIUtil.replaceInEditor(getEd(), bot, "@Produces", "@Inject");
		CDIUtil.replaceInEditor(getEd(), bot, "import javax.enterprise.inject.Produces;", "");
		CDIUtil.replaceInEditor(getEd(), bot, "String produceString", "void method");
		CDIUtil.replaceInEditor(getEd(), bot, "return \"test\";", "");
	}
	
	private void prepareObserWithDisposParam() {
		prepareProducer();
		CDIUtil.replaceInEditor(getEd(), bot, "import javax.inject.Inject;", "import javax.enterprise.event.Observes;" + 
				LINE_SEPARATOR + "import javax.enterprise.inject.Disposes;" + LINE_SEPARATOR);
		CDIUtil.replaceInEditor(getEd(), bot, "@Inject", "");
		CDIUtil.replaceInEditor(getEd(), bot, "MyBean(String aaa)", 
				"void method(@Observes String param1, @Disposes String param2)");
	}
	
	private void prepareCheckSessionBean(String replacement) {
		if (replacement.equals("@Decorator")) {
			CDIUtil.insertInEditor(getEd(), bot, 3, 0, "import javax.decorator.Decorator;" + LINE_SEPARATOR);
			CDIUtil.insertInEditor(getEd(), bot, 4, 0, "import javax.ejb.Stateless;" + LINE_SEPARATOR);
			CDIUtil.insertInEditor(getEd(), bot, 5, 0, "@Decorator" + LINE_SEPARATOR);
			CDIUtil.insertInEditor(getEd(), bot, 6, 0, "@Stateless" + LINE_SEPARATOR);
		} else {
			CDIUtil.copyResourceToClass(getEd(), CDIQuickFixTest.class
					.getResourceAsStream("/resources/cdi/MyBean2.java.cdi"), false);
		}
	}
	
	private void prepareDisposesAnnot() {
		CDIUtil.copyResourceToClass(getEd(), CDIQuickFixTest.class
				.getResourceAsStream("/resources/cdi/InterDecor.java.cdi"), false);
	}
	
	private void prepareComponentsForSpecializeAnnotation(String testBeanName) {
		createComponent(CDICOMPONENT.BEAN, "AnyBean", PACKAGE_NAME, null);
		createComponent(CDICOMPONENT.INTERBINDING, "AnyBinding", PACKAGE_NAME, null);		
		createComponent(CDICOMPONENT.BEAN, testBeanName, PACKAGE_NAME, null);
		CDIUtil.copyResourceToClass(getEd(), CDIQuickFixTest.class
				.getResourceAsStream("/resources/cdi/TestBean.java.cdi"), false);
	}
	
	
	/*
	 ****************************************************************
	 * 
	 * "check" like methods which are the most general method to test
	 * CDI components with various annotations, replacements and so on 
	 * 
	 ****************************************************************
	 */
	
	
	private void checkSerializableAnnotation(CDICOMPONENT comp, String className) {
		prepareSerializableAnnotation();
		checkQuickFix(ANNOTATIONS.SERIALIZABLE, comp, className, "Serializable");
		cleanWarnings(className);
	}
	
	private void checkMultipleBeans(CDICOMPONENT comp, String className) {
		prepareMultipleBeans(className);
		checkQuickFix(ANNOTATIONS.NAMED, comp, className, "MultipleBeans");
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
			CDIUtil.replaceInEditor(getEd(), bot, 
					"@Target({TYPE, METHOD, FIELD})", replacement);
			break;
		case QUALIFIER:
			if (replacement.equals("")) {
				CDIUtil.replaceInEditor(getEd(), bot, 
						"@Target({TYPE, METHOD, FIELD, PARAMETER})", replacement);
			} else {
				CDIUtil.replaceInEditor(getEd(), bot, 
						"@Target({TYPE, METHOD, PARAMETER, FIELD})", replacement);
			}
			CDIUtil.replaceInEditor(getEd(), bot, 
					"import static java.lang.annotation.ElementType.PARAMETER;", "");
			break;
		}
		CDIUtil.replaceInEditor(getEd(), bot,
				"import static java.lang.annotation.ElementType.METHOD;", "");
		if (replacement.equals("")) {
			CDIUtil.replaceInEditor(getEd(), bot,
					"import java.lang.annotation.Target;", "");
			CDIUtil.replaceInEditor(getEd(), bot,
					"import static java.lang.annotation.ElementType.TYPE;", "");
			CDIUtil.replaceInEditor(getEd(), bot,
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
		CDIUtil.replaceInEditor(getEd(), bot, "@Retention(RUNTIME)", replacement);
		if (replacement.equals("@Retention(CLASS)")) {
			CDIUtil.replaceInEditor(getEd(), bot,
					"import static java.lang.annotation.RetentionPolicy.RUNTIME;",
					"import static java.lang.annotation.RetentionPolicy.CLASS;");
		} else {
			CDIUtil.replaceInEditor(getEd(), bot,
					"import static java.lang.annotation.RetentionPolicy.RUNTIME;",
					"");
			CDIUtil.replaceInEditor(getEd(), bot,
					"import java.lang.annotation.Retention;", "");
		}
		checkQuickFix(ANNOTATIONS.RETENTION, comp, className, replacement);
		if (replacement.equals("@Retention(CLASS)")) {
			CDIUtil.replaceInEditor(getEd(), bot, 
					"import static java.lang.annotation.RetentionPolicy.CLASS;","");
		}
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
		CDIUtil.insertInEditor(getEd(), bot, 6, 15, replacement + " ");
		dispObserCompletion(comp, className, replacement);
	}
	
	private void checkProducerMethod(CDICOMPONENT comp, ANNOTATIONS annonType, String className) {
		if (className.equals("InterDecor")) {
			prepareProducer();
			CDIUtil.replaceInEditor(getEd(), bot, "MyBean", "InterDecor");
			checkProducerWithAddon(comp, annonType, className, "@Interceptor");
			prepareProducer();
			CDIUtil.replaceInEditor(getEd(), bot, "MyBean", "InterDecor");
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
			CDIUtil.insertInEditor(getEd(), bot, 3, 0, annot + LINE_SEPARATOR);
			CDIUtil.insertInEditor(getEd(), bot, 1, 0,  importAnnot + LINE_SEPARATOR);
			checkQuickFix(replacement.equals("@Interceptor")?ANNOTATIONS.INTERCEPTOR:
				ANNOTATIONS.DECORATOR, comp, className, replacement);
		} else {
			CDIUtil.insertInEditor(getEd(), bot, 11, 29, replacement + " ");
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
		CDIUtil.insertInEditor(getEd(), bot, 10, 20, replacement + " ");
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
			CDIUtil.insertInEditor(getEd(), bot, 3, 0, "import javax.enterprise.inject.Disposes;");
			CDIUtil.insertInEditor(getEd(), bot, 6, 46, "@Disposes "); 
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
		CDIUtil.insertInEditor(getEd(), bot, 2 , 0, "import javax.enterprise." + 
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
		CDIUtil.insertInEditor(getEd(), bot, 2, 0, annot + LINE_SEPARATOR);
		CDIUtil.insertInEditor(getEd(), bot, 1, 0, importAnnot + LINE_SEPARATOR);
		checkQuickFix(ANNOTATIONS.DECORATOR, comp, className, replacement);
	}
	
	private void checkObserveAnnotation(CDICOMPONENT comp, String className) {
		checkObserveAnnotWithAddon(comp, className, "@Decorator");
		checkObserveAnnotWithAddon(comp, className, "@Interceptor");
		CDIUtil.replaceInEditor(getEd(), bot, "@Interceptor", "");
		CDIUtil.replaceInEditor(getEd(), bot, "import javax.interceptor.Interceptor;", "");
	}
	
	private void checkObserveAnnotWithAddon(CDICOMPONENT comp, String className, 
			String replacement) {
		prepareDisposesAnnot();
		CDIUtil.replaceInEditor(getEd(), bot, "@Disposes", "@Observes");
		CDIUtil.replaceInEditor(getEd(), bot, "import javax.enterprise.inject.Disposes;", 
				"import javax.enterprise.event.Observes;");
		CDIUtil.replaceInEditor(getEd(), bot, "dispose", "observe");
		String annot = replacement;
		String importAnnot = "import javax." + replacement.substring(1).toLowerCase() 
				+ "." + replacement.substring(1) + ";";
		CDIUtil.insertInEditor(getEd(), bot, 2, 0, annot + LINE_SEPARATOR);
		CDIUtil.insertInEditor(getEd(), bot, 1, 0, importAnnot + LINE_SEPARATOR);
		checkQuickFix(ANNOTATIONS.DECORATOR, comp, className, replacement);
	}
	
	private void checkSpecializeAnnotation(CDICOMPONENT comp, String className) {
		prepareComponentsForSpecializeAnnotation(className);
		checkSpecializeAnnotWithAddon(comp, className, "@Interceptor");
		checkSpecializeAnnotWithAddon(comp, className, "@Decorator");
		CDIUtil.copyResourceToClass(getEd(), 
				CDIQuickFixTest.class.getResourceAsStream("/resources/cdi/TestBean3.java.cdi"), false);
	}
	
	private void checkSpecializeAnnotWithAddon(CDICOMPONENT comp, String className, 
			String replacement) {		
		if (replacement.equals("@Decorator")) {
			CDIUtil.copyResourceToClass(getEd(), 
					CDIQuickFixTest.class.getResourceAsStream("/resources/cdi/TestBean2.java.cdi"), false);
		}
		checkQuickFix(ANNOTATIONS.SPECIALIZES, comp, className, replacement);
	}
	
	private void checkNonBindingAnnotation(CDICOMPONENT comp, String className) {
		checkNonBindingAnnotationWithAddon(comp, className, "Annotation");
		checkNonBindingAnnotationWithAddon(comp, className, "Array");
		
	}
	
	private void checkNonBindingAnnotationWithAddon(CDICOMPONENT comp, String className, 
			String replacement) {	
		if (comp == CDICOMPONENT.INTERBINDING) {
			boolean interceptorCreated = projectExplorer.isFilePresent(PROJECT_NAME, 
					"Java Resources", "src", PACKAGE_NAME, className + ".java"); 
			if (!interceptorCreated) {
				createComponent(CDICOMPONENT.INTERBINDING, className, PACKAGE_NAME, null);
			}
		}
		
		if (replacement.equals("Annotation")) {
			boolean annotationCreated = projectExplorer.isFilePresent(PROJECT_NAME, 
					"Java Resources", "src", PACKAGE_NAME, "AAnnotation.java"); 
			if (!annotationCreated) {
				createComponent(null, "AAnnotation", PACKAGE_NAME, null);				
			} 			
			bot.editorByTitle(className + ".java").show();
			setEd(bot.activeEditor().toTextEditor());
			CDIUtil.insertInEditor(getEd(), bot, getEd().getLineCount()-3, 1, "AAnnotation annotValue();" + LINE_SEPARATOR);			
		}else {
			CDIUtil.insertInEditor(getEd(), bot, getEd().getLineCount()-3, 1, "String[] array();" + LINE_SEPARATOR);
		}
		checkQuickFix(ANNOTATIONS.NONBINDING, comp, className, replacement);
	}
	
	/*
	 ****************************************************************
	 * 
	 * checkQuickFix is the most important method in this class. It
	 * gets validation error prior to component type, annotation and
	 * class name, then it resolve validation error through quick fix
	 * wizard and finally check if validation errors was fixed through
	 * this wizard
	 * 
	 ****************************************************************
	 */
	
	private void checkQuickFix(ANNOTATIONS annonType, CDICOMPONENT comp, 
			String className, String replacement) {
		problemsTrees = getProblems(annonType, comp, className + ".java");		
		assertTrue(problemsTrees.length != 0);
		resolveQuickFix(annonType, comp, replacement);
		problemsTrees = getProblems(annonType, comp, className + ".java");
		assertTrue(problemsTrees.length == 0);
	}
	
	private SWTBotTreeItem[] getProblems(ANNOTATIONS annonType, CDICOMPONENT comp, String className) {
		SWTBotTreeItem[] problemsTree;
		boolean warningType = true;
		switch (annonType) {		
		case NAMED:
		case SPECIALIZES:
		case SERIALIZABLE:
			warningType = ((comp == CDICOMPONENT.BEAN)?true:false);
			break;		
		case DISPOSES:
		case OBSERVES:
		case DECORATOR:
		case INTERCEPTOR:
			warningType = false;
			break;
		}
		String problemsContains = null;
		if (warningType) {
			switch (annonType) {
			case SPECIALIZES:
				problemsContains = "@Specializes";
				break;
			case NONBINDING:
				problemsContains  = "@Nonbinding";
				break;
			case SERIALIZABLE:
				problemsContains = "declares a passivating scope SessionScoped";
				break;
			case NAMED:
				problemsContains = "should not have a name";
			}
			
			if (className.equals("BrokenFarm.java")) {
				problemsContains = "Multiple beans are eligible";
			}
			problemsTree = ProblemsView.getFilteredWarningsTreeItems(bot, problemsContains, "/"
					+ PROJECT_NAME, className, "CDI Problem");
		} else {
			if (className.equals("InterDecor.java")) {
				if (getEd().toTextEditor().getText().contains("produceString")) {
					problemsContains = "Producer cannot be declared in";
				}
				if (getEd().toTextEditor().getText().contains("disposeMethod")) {
					problemsContains = "has a method annotated @Disposes";
				}
				if (getEd().toTextEditor().getText().contains("observeMethod")) {
					problemsContains = "have a method with a parameter annotated @Observes";
				}
			}
			problemsTree = ProblemsView.getFilteredErrorsTreeItems(bot, problemsContains, "/"
					+ PROJECT_NAME, className, "CDI Problem");
		}
		return problemsTree;
	}
	
	private void resolveQuickFix(ANNOTATIONS annonType, CDICOMPONENT comp, String replacement) {
		if (replacement.equals("MultipleBeans")) {
			resolveMultipleBeans();
		}else {
			int index = indexDetermine(annonType, comp, replacement);
			resolve(annonType, replacement, index);
		}
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
	}
		
	private void resolveMultipleBeans() {
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
		String code = getEd().toTextEditor().getText();
		assertTrue(code.contains("@Inject @Q1 private Animal animal;"));
		code = bot.editorByTitle(qualifBean + ".java").toTextEditor().getText();
		assertTrue(code.contains("@Q1"));
	}
	
	/*
	 * method gets all the problems by all classes in the project. 
	 * Type of problems (warning, error) is chosen by proper value in parameter
	 */
	
	private void checkProjectAllProblems() {
		problemsTrees = getAllProblems(PROBLEM_TYPE.WARNINGS);
		assertTrue(problemsTrees.length == 0);
		problemsTrees = getAllProblems(PROBLEM_TYPE.ERRORS);
		assertTrue(problemsTrees.length == 0);
	}
	
	private SWTBotTreeItem[] getAllProblems(PROBLEM_TYPE problemType) {
		SWTBotTreeItem[] problemsTree = null;
		if (problemType == PROBLEM_TYPE.WARNINGS) {
			problemsTree = ProblemsView.getFilteredWarningsTreeItems(bot, null, "/"
					+ PROJECT_NAME, null, null);
		}else if (problemType == PROBLEM_TYPE.ERRORS) {
			problemsTree = ProblemsView.getFilteredErrorsTreeItems(bot, null, "/"
					+ PROJECT_NAME, null, null);
		}
		return problemsTree;
	}
	
	private void cleanWarnings(String className) {
		problemsTrees = ProblemsView.getFilteredWarningsTreeItems(bot, null, "/"
				+ PROJECT_NAME, className + ".java", null);
		assertTrue(problemsTrees.length != 0);
		CDIUtil.openQuickFix(problemsTrees[0], bot);
		bot.clickButton("Finish");
		bot.sleep(Timing.time1S());
		bot.activeEditor().save();
		problemsTrees = ProblemsView.getFilteredWarningsTreeItems(bot, null, "/"
				+ PROJECT_NAME, className, "CDI Problem");
		assertTrue(problemsTrees.length == 0);
	}
}
