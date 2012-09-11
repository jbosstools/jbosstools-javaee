/*******************************************************************************
 * Copyright (c) 2010-2011 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.jboss.tools.cdi.bot.test.wizard;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.jboss.tools.cdi.bot.test.CDIAllBotTests;
import org.jboss.tools.cdi.bot.test.CDISmokeBotTests;
import org.jboss.tools.cdi.bot.test.CDITestBase;
import org.jboss.tools.cdi.bot.test.annotations.CDIWizardType;
import org.jboss.tools.cdi.bot.test.uiutils.actions.NewCDIFileWizard;
import org.jboss.tools.cdi.bot.test.uiutils.wizards.CDIWizardBase;
import org.jboss.tools.ui.bot.ext.Timing;
import org.junit.Test;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Test checks all CDI components wizardExts
 * 
 * @author Lukas Jungmann
 * @author jjankovi
 */

@SuiteClasses({ CDIAllBotTests.class, CDISmokeBotTests.class })
public class WizardTest extends CDITestBase {

	@Override
	public void waitForJobs() {
		util.waitForNonIgnoredJobs();
		/**
		 * needed for creating non-dependant components
		 */
		projectExplorer.selectProject(getProjectName());
	}
		
	@Test
	public void testComponentsWizards() {
		testQualifier();
		testScope();
		testIBinding();
		testStereotype();
		testDecorator();
		testInterceptor();
		testBeansXml();
		testBean();
		testAnnLiteral();
	}
	
	private void testQualifier() {
		wizardExt.qualifier(getPackageName(), "Q1", false, false).finish();
		util.waitForNonIgnoredJobs();
		SWTBotEditor ed = new SWTWorkbenchBot().activeEditor();
		assertTrue(("Q1.java").equals(ed.getTitle()));
		String code = ed.toTextEditor().getText();
		LOGGER.fine(code);
		assertTrue(code.contains("@Qualifier"));
		assertTrue(code.contains("@Retention(RUNTIME)"));
		assertTrue(code.contains("@Target({ TYPE, METHOD, PARAMETER, FIELD })"));
		assertFalse(code.contains("@Inherited"));
		assertFalse(code.startsWith("/**"));

		wizardExt.qualifier(getPackageName(), "Q2", true, true).finish();
		util.waitForNonIgnoredJobs();
		ed = new SWTWorkbenchBot().activeEditor();
		assertTrue(("Q2.java").equals(ed.getTitle()));
		code = ed.toTextEditor().getText();
		LOGGER.fine(code);
		assertTrue(code.contains("@Qualifier"));
		assertTrue(code.contains("@Retention(RUNTIME)"));
		assertTrue(code.contains("@Target({ TYPE, METHOD, PARAMETER, FIELD })"));
		assertTrue(code.contains("@Inherited"));
		assertTrue(code.startsWith("/**"));
	}
	
	private void testScope() {
		wizardExt.scope(getPackageName(), "Scope1", true, false, true, false).finish();
		util.waitForNonIgnoredJobs();
		SWTBotEditor ed = new SWTWorkbenchBot().activeEditor();
		assertTrue(("Scope1.java").equals(ed.getTitle()));
		String code = ed.toTextEditor().getText();
		LOGGER.fine(code);
		assertTrue(code.contains("@NormalScope"));
		assertFalse(code.contains("@Scope"));
		assertFalse(code.contains("passivating"));
		assertTrue(code.contains("@Retention(RUNTIME)"));
		assertTrue(code.contains("@Target({ TYPE, METHOD, FIELD })"));
		assertTrue(code.contains("@Inherited"));
		assertFalse(code.startsWith("/**"));

		wizardExt.scope(getPackageName(), "Scope2", false, true, true, true).finish();
		util.waitForNonIgnoredJobs();
		ed = new SWTWorkbenchBot().activeEditor();
		assertTrue(("Scope2.java").equals(ed.getTitle()));
		code = ed.toTextEditor().getText();
		LOGGER.fine(code);
		assertTrue(code.contains("@NormalScope(passivating = true)"));
		assertFalse(code.contains("@Scope"));
		assertTrue(code.contains("@Retention(RUNTIME)"));
		assertTrue(code.contains("@Target({ TYPE, METHOD, FIELD })"));
		assertFalse(code.contains("@Inherited"));
		assertTrue(code.startsWith("/**"));

		wizardExt.scope(getPackageName(), "Scope3", false, true, false, false).finish();
		util.waitForNonIgnoredJobs();
		ed = new SWTWorkbenchBot().activeEditor();
		assertTrue(("Scope3.java").equals(ed.getTitle()));
		code = ed.toTextEditor().getText();
		LOGGER.fine(code);
		assertTrue(code.contains("@Scope"));
		assertFalse(code.contains("@NormalScope"));
		assertTrue(code.contains("@Retention(RUNTIME)"));
		assertTrue(code.contains("@Target({ TYPE, METHOD, FIELD })"));
		assertFalse(code.contains("@Inherited"));
		assertTrue(code.startsWith("/**"));
	}
	
	private void testIBinding() {
		CDIWizardBase w = wizardExt.binding(getPackageName(), "B1", null, true, false);
		assertEquals(2, w.getTargets().size());
		w.finish();
		util.waitForNonIgnoredJobs();
		SWTBotEditor ed = new SWTWorkbenchBot().activeEditor();
		assertTrue(("B1.java").equals(ed.getTitle()));
		String code = ed.toTextEditor().getText();
		LOGGER.fine(code);
		assertTrue(code.contains("@InterceptorBinding"));
		assertTrue(code.contains("@Retention(RUNTIME)"));
		assertTrue(code.contains("@Target({ TYPE, METHOD })"));
		assertTrue(code.contains("@Inherited"));
		assertFalse(code.startsWith("/**"));

		wizardExt.binding(getPackageName(), "B2", "TYPE", false, true).finish();
		util.waitForNonIgnoredJobs();
		ed = new SWTWorkbenchBot().activeEditor();
		assertTrue(("B2.java").equals(ed.getTitle()));
		code = ed.toTextEditor().getText();
		LOGGER.fine(code);
		assertTrue(code.contains("@InterceptorBinding"));
		assertTrue(code.contains("@Retention(RUNTIME)"));
		assertTrue(code.contains("@Target({ TYPE })"));
		assertFalse(code.contains("@Inherited"));
		assertTrue(code.startsWith("/**"));

		wizardExt.binding(getPackageName(), "B3", "TYPE", false, true).finish();
		util.waitForNonIgnoredJobs();
		ed = new SWTWorkbenchBot().activeEditor();
		assertTrue(("B3.java").equals(ed.getTitle()));
		code = ed.toTextEditor().getText();
		LOGGER.fine(code);
		assertTrue(code.contains("@InterceptorBinding"));
		assertTrue(code.contains("@Retention(RUNTIME)"));
		assertTrue(code.contains("@Target({ TYPE })"));
		assertFalse(code.contains("@Inherited"));
		assertTrue(code.startsWith("/**"));

		w = wizardExt.binding(getPackageName(), "B4", "TYPE", true, false);
		w.addIBinding(getPackageName() + ".B2");
		w.finish();
		util.waitForNonIgnoredJobs();
		ed = new SWTWorkbenchBot().activeEditor();
		assertTrue(("B4.java").equals(ed.getTitle()));
		code = ed.toTextEditor().getText();
		LOGGER.fine(code);
		assertTrue(code.contains("@InterceptorBinding"));
		assertTrue(code.contains("@Retention(RUNTIME)"));
		assertTrue(code.contains("@Target({ TYPE })"));
		assertTrue(code.contains("@Inherited"));
		assertFalse(code.startsWith("/**"));
		assertTrue(code.contains("@B2"));
	}
	
	private void testStereotype() {
		CDIWizardBase w = wizardExt.stereotype(getPackageName(), "S1", null, null, false, false, false, false,
				false);
		assertEquals(9, w.getScopes().size());
		assertEquals(5, w.getTargets().size());
		w.finish();
		util.waitForNonIgnoredJobs();
		SWTBotEditor ed = new SWTWorkbenchBot().activeEditor();
		assertTrue(("S1.java").equals(ed.getTitle()));
		String code = ed.toTextEditor().getText();
		LOGGER.fine(code);
		assertTrue(code.contains("@Stereotype"));
		assertTrue(code.contains("@Retention(RUNTIME)"));
		assertTrue(code.contains("@Target({ TYPE, METHOD, FIELD })"));
		assertFalse(code.contains("@Named"));
		assertFalse(code.contains("@Alternative"));
		assertFalse(code.contains("@Inherited"));
		assertFalse(code.startsWith("/**"));

		wizardExt.stereotype(getPackageName(), "S2", "@Scope3", "FIELD", true, true, true, false, true)
				.finish();
		util.waitForNonIgnoredJobs();
		ed = new SWTWorkbenchBot().activeEditor();
		assertTrue(("S2.java").equals(ed.getTitle()));
		code = ed.toTextEditor().getText();
		LOGGER.fine(code);
		assertTrue(code.contains("@Stereotype"));
		assertTrue(code.contains("@Scope3"));
		assertTrue(code.contains("@Named"));
		assertTrue(code.contains("@Alternative"));
		assertTrue(code.contains("@Inherited"));
		assertTrue(code.contains("@Retention(RUNTIME)"));
		assertTrue(code.contains("@Target({ FIELD })"));
		assertTrue(code.startsWith("/**"));

		w = wizardExt.stereotype(getPackageName(), "S3", null, null, false, false, true, false, false);
		w.addIBinding(getPackageName() + ".B1");
		w.addStereotype(getPackageName() + ".S1");
		w.finish();
		util.waitForNonIgnoredJobs();
		ed = new SWTWorkbenchBot().activeEditor();
		assertTrue(("S3.java").equals(ed.getTitle()));
		code = ed.toTextEditor().getText();
		LOGGER.fine(code);
		assertTrue(code.contains("@Stereotype"));
		assertFalse(code.contains("@Scope3"));
		assertFalse(code.contains("@Named"));
		assertTrue(code.contains("@Alternative"));
		assertTrue(code.contains("@B1"));
		assertTrue(code.contains("@S1"));
		assertTrue(code.contains("@Retention(RUNTIME)"));
		assertTrue(code.contains("@Target({ TYPE })"));
		assertFalse(code.contains("@Inherited"));
		assertFalse(code.startsWith("/**"));
	}
	
	private void testDecorator() {
		bot.sleep(Timing.time1S());
		CDIWizardBase w = wizardExt.decorator(getPackageName(), "", "java.lang.Comparable", null, true, true, false, false);
		w.finish();
		util.waitForNonIgnoredJobs();
		SWTBotEditor ed = new SWTWorkbenchBot().editorByTitle("ComparableDecorator.java");
		assertTrue(("ComparableDecorator.java").equals(ed.getTitle()));
		String code = ed.toTextEditor().getText();
		LOGGER.fine(code);
		assertTrue(code.contains("@Decorator"));
		assertTrue(code.contains("abstract class"));
		assertTrue(code.contains("@Delegate"));
		assertTrue(code.contains("@Inject"));
		assertTrue(code.contains("@Any"));
		assertTrue(code.contains("private Comparable<T> comparable;"));
		assertFalse(code.contains("final"));
		assertFalse(code.startsWith("/**"));

		w = wizardExt.decorator(getPackageName(), "", "java.util.Map", "field", false, false, true, true);
		w.finish();
		util.waitForNonIgnoredJobs();
		ed = new SWTWorkbenchBot().editorByTitle("MapDecorator.java");
		assertTrue(("MapDecorator.java").equals(ed.getTitle()));
		code = ed.toTextEditor().getText();
		LOGGER.fine(code);
		assertTrue(code.contains("@Decorator"));
		assertFalse(code.contains("abstract"));
		assertTrue(code.contains("@Delegate"));
		assertTrue(code.contains("@Inject"));
		assertTrue(code.contains("@Any"));
		assertTrue(code.contains("private Map<K, V> field;"));
		assertTrue(code.contains("final class"));
		assertTrue(code.startsWith("/**"));
	}
	
	private void testInterceptor() {
		CDIWizardBase w = wizardExt.interceptor(getPackageName(), "I1", "B2", null, null, false);
		w.finish();
		util.waitForNonIgnoredJobs();
		SWTBotEditor ed = new SWTWorkbenchBot().editorByTitle("I1.java");
		assertTrue(("I1.java").equals(ed.getTitle()));
		String code = ed.toTextEditor().getText();
		LOGGER.fine(code);
		assertTrue(code.contains("@B2"));
		assertTrue(code.contains("@Interceptor"));
		assertTrue(code.contains("@AroundInvoke"));
		assertTrue(code.contains("public Object manage(InvocationContext ic) throws Exception {"));
		assertFalse(code.contains("final"));
		assertFalse(code.startsWith("/**"));
		
		w = wizardExt.interceptor(getPackageName(), "I2", "B4", "java.util.Date", "sample", true);
		w.finish();
		util.waitForNonIgnoredJobs();
		ed = new SWTWorkbenchBot().editorByTitle("I2.java");
		assertTrue(("I2.java").equals(ed.getTitle()));
		code = ed.toTextEditor().getText();
		LOGGER.fine(code);
		assertTrue(code.contains("@B4"));
		assertTrue(code.contains("@Interceptor"));
		assertTrue(code.contains("@AroundInvoke"));
		assertTrue(code.contains("public Object sample(InvocationContext ic) throws Exception {"));
		assertFalse(code.contains("final"));
		assertTrue(code.startsWith("/**"));
		assertTrue(code.contains("extends Date"));
	}
	
	private void testBeansXml() {
		CDIWizardBase w = new NewCDIFileWizard(CDIWizardType.BEANS_XML).run();
		w.setSourceFolder(getProjectName() + "/WebContent/WEB-INF");
		assertFalse(w.canFinish());		
		w.setSourceFolder(getProjectName() + "/src/" + getPackageName().replaceAll(".", "/"));
		assertTrue(w.canFinish());
		w.cancel();
		w = new NewCDIFileWizard(CDIWizardType.BEANS_XML).run();
		assertFalse(w.canFinish());		
		w.cancel();
	}
	
	private void testBean() {
		CDIWizardBase w = wizardExt.bean(getPackageName(), "Bean1", true, true, false, false, false, false, null, null, null, null);
		w.finish();
		util.waitForNonIgnoredJobs();
		SWTBotEditor ed = new SWTWorkbenchBot().activeEditor();
		assertTrue(("Bean1.java").equals(ed.getTitle()));
		String code = ed.toTextEditor().getText();
		LOGGER.fine(code);
		assertTrue(code.contains("package cdi;"));
		assertTrue(code.contains("public abstract class Bean1 {"));
		assertFalse(code.contains("@Named"));
		assertFalse(code.contains("final"));
		assertFalse(code.startsWith("/**"));
		
		w = wizardExt.bean(getPackageName(), "Bean2", false, false, true, true, false, false, "", null, "@Dependent", null);
		w.finish();
		util.waitForNonIgnoredJobs();
		ed = new SWTWorkbenchBot().activeEditor();
		assertTrue(("Bean2.java").equals(ed.getTitle()));
		code = ed.toTextEditor().getText();
		LOGGER.fine(code);
		assertTrue(code.contains("package cdi;"));
		assertTrue(code.contains("@Named"));
		assertFalse(code.contains("@Named("));
		assertTrue(code.contains("@Dependent"));
		assertTrue(code.contains("final class Bean2 {"));
		assertTrue(code.startsWith("/**"));

		w = wizardExt.bean(getPackageName(), "Bean3", true, false, false, true, false, false, "TestedBean", null, "@Scope2", "Q1");
		w.finish();
		util.waitForNonIgnoredJobs();
		ed = new SWTWorkbenchBot().activeEditor();
		assertTrue(("Bean3.java").equals(ed.getTitle()));
		code = ed.toTextEditor().getText();
		LOGGER.fine(code);
		assertTrue(code.contains("package cdi;"));
		assertTrue(code.contains("@Named(\"TestedBean\")"));
		assertTrue(code.contains("@Scope2"));
		assertTrue(code.contains("@Q1"));
		assertTrue(code.contains("public class Bean3 {"));
		assertFalse(code.contains("final"));
		assertTrue(code.startsWith("/**"));
	}
	
	private void testAnnLiteral() {
		CDIWizardBase w = wizardExt.annLiteral(getPackageName(), "AnnL1", true, false, true, false, getPackageName() + ".Q1");
		w.finish();
		util.waitForNonIgnoredJobs();
		SWTBotEditor ed = new SWTWorkbenchBot().activeEditor();
		assertTrue(("AnnL1.java").equals(ed.getTitle()));
		String code = ed.toTextEditor().getText();
		LOGGER.info(code);
		assertTrue(code.contains("package cdi;"));
		assertTrue(code.contains("public final class AnnL1 extends AnnotationLiteral<Q1> implements Q1"));
		assertTrue(code.contains("public static final Q1 INSTANCE = new AnnL1();"));
		assertFalse(code.contains("abstract"));
		assertFalse(code.startsWith("/**"));
		
		w = wizardExt.annLiteral(getPackageName(), "AnnL2", false, true, false, true, "Q2");
		w.finish();
		util.waitForNonIgnoredJobs();
		ed = new SWTWorkbenchBot().activeEditor();
		assertTrue(("AnnL2.java").equals(ed.getTitle()));
		code = ed.toTextEditor().getText();
		LOGGER.info(code);
		assertTrue(code.contains("package cdi;"));
		assertTrue(code.contains("abstract class AnnL2 extends AnnotationLiteral<Q2> implements Q2 {"));
		assertTrue(code.contains("public static final Q2 INSTANCE = new AnnL2();"));
		assertFalse(code.substring(code.indexOf("final") + 5).contains("final"));
		assertTrue(code.contains("abstract"));
		assertTrue(code.startsWith("/**"));
	}
}
