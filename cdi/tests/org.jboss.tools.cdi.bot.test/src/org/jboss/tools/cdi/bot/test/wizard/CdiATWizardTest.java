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

import java.util.logging.Logger;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.jboss.tools.cdi.bot.test.uiutils.actions.CDIUtil;
import org.jboss.tools.cdi.bot.test.uiutils.actions.NewCDIFileWizard;
import org.jboss.tools.cdi.bot.test.uiutils.actions.NewFileWizardAction;
import org.jboss.tools.cdi.bot.test.uiutils.wizards.CDIWizard;
import org.jboss.tools.cdi.bot.test.uiutils.wizards.CDIWizardType;
import org.jboss.tools.cdi.bot.test.uiutils.wizards.DynamicWebProjectWizard;
import org.jboss.tools.ui.bot.ext.RequirementAwareSuite;
import org.jboss.tools.ui.bot.ext.SWTEclipseExt;
import org.jboss.tools.ui.bot.ext.SWTTestExt;
import org.jboss.tools.ui.bot.ext.config.Annotations.SWTBotTestRequires;
import org.jboss.tools.ui.bot.ext.config.Annotations.Server;
import org.jboss.tools.ui.bot.ext.config.Annotations.ServerState;
import org.jboss.tools.ui.bot.ext.types.ViewType;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

@SWTBotTestRequires(perspective = "Java EE", server = @Server(state = ServerState.NotRunning, version = "6.0", operator = ">="))
@RunWith(RequirementAwareSuite.class)
@SuiteClasses({ CdiATWizardTest.class })
public class CdiATWizardTest extends SWTTestExt {

	private static final String PROJECT_NAME = "CDIProject";
	private static final Logger L = Logger.getLogger(CdiATWizardTest.class.getName());

	@After
	public void waitForJobs() {
		util.waitForNonIgnoredJobs();
	}

	@Test
	public void createProject() {
		new NewFileWizardAction().run()
				.selectTemplate("Web", "Dynamic Web Project").next();
		new DynamicWebProjectWizard().setProjectName(PROJECT_NAME).finish();
		util.waitForNonIgnoredJobs();
		SWTBot v = eclipse.showView(ViewType.PROJECT_EXPLORER);
		SWTBotTree tree = v.tree();
		tree.setFocus();
		assertTrue("Project " + PROJECT_NAME + " was not created properly.",
				SWTEclipseExt.treeContainsItemWithLabel(tree, PROJECT_NAME));
		SWTBotTreeItem t = tree.getTreeItem(PROJECT_NAME);
		t.expand();
		CDIUtil.addCDISupport(tree, t, bot, util);
	}

	@Test
	public void testQualifier() {
		qualifier("cdi", "Q1", false, false).finish();
		util.waitForNonIgnoredJobs();
		SWTBotEditor ed = new SWTWorkbenchBot().activeEditor();
		assertTrue(("Q1.java").equals(ed.getTitle()));
		String code = ed.toTextEditor().getText();
		L.fine(code);
		assertTrue(code.contains("@Qualifier"));
		assertTrue(code.contains("@Retention(RUNTIME)"));
		assertTrue(code.contains("@Target({ TYPE, METHOD, PARAMETER, FIELD })"));
		assertFalse(code.contains("@Inherited"));
		assertFalse(code.startsWith("/**"));

		qualifier("cdi", "Q2", true, true).finish();
		util.waitForNonIgnoredJobs();
		ed = new SWTWorkbenchBot().activeEditor();
		assertTrue(("Q2.java").equals(ed.getTitle()));
		code = ed.toTextEditor().getText();
		L.fine(code);
		assertTrue(code.contains("@Qualifier"));
		assertTrue(code.contains("@Retention(RUNTIME)"));
		assertTrue(code.contains("@Target({ TYPE, METHOD, PARAMETER, FIELD })"));
		assertTrue(code.contains("@Inherited"));
		assertTrue(code.startsWith("/**"));
	}

	@Test
	public void testScope() {
		scope("cdi", "Scope1", true, false, true, false).finish();
		util.waitForNonIgnoredJobs();
		SWTBotEditor ed = new SWTWorkbenchBot().activeEditor();
		assertTrue(("Scope1.java").equals(ed.getTitle()));
		String code = ed.toTextEditor().getText();
		L.fine(code);
		assertTrue(code.contains("@NormalScope"));
		assertFalse(code.contains("@Scope"));
		assertFalse(code.contains("passivating"));
		assertTrue(code.contains("@Retention(RUNTIME)"));
		assertTrue(code.contains("@Target({ TYPE, METHOD, FIELD })"));
		assertTrue(code.contains("@Inherited"));
		assertFalse(code.startsWith("/**"));

		scope("cdi", "Scope2", false, true, true, true).finish();
		util.waitForNonIgnoredJobs();
		ed = new SWTWorkbenchBot().activeEditor();
		assertTrue(("Scope2.java").equals(ed.getTitle()));
		code = ed.toTextEditor().getText();
		L.fine(code);
		assertTrue(code.contains("@NormalScope(passivating = true)"));
		assertFalse(code.contains("@Scope"));
		assertTrue(code.contains("@Retention(RUNTIME)"));
		assertTrue(code.contains("@Target({ TYPE, METHOD, FIELD })"));
		assertFalse(code.contains("@Inherited"));
		assertTrue(code.startsWith("/**"));

		scope("cdi", "Scope3", false, true, false, false).finish();
		util.waitForNonIgnoredJobs();
		ed = new SWTWorkbenchBot().activeEditor();
		assertTrue(("Scope3.java").equals(ed.getTitle()));
		code = ed.toTextEditor().getText();
		L.fine(code);
		assertTrue(code.contains("@Scope"));
		assertFalse(code.contains("@NormalScope"));
		assertTrue(code.contains("@Retention(RUNTIME)"));
		assertTrue(code.contains("@Target({ TYPE, METHOD, FIELD })"));
		assertFalse(code.contains("@Inherited"));
		assertTrue(code.startsWith("/**"));
	}

	@Test
	public void testIBinding() {
		CDIWizard w = binding("cdi", "B1", null, true, false);
		assertEquals(2, w.getTargets().size());
		w.finish();
		util.waitForNonIgnoredJobs();
		SWTBotEditor ed = new SWTWorkbenchBot().activeEditor();
		assertTrue(("B1.java").equals(ed.getTitle()));
		String code = ed.toTextEditor().getText();
		L.fine(code);
		assertTrue(code.contains("@InterceptorBinding"));
		assertTrue(code.contains("@Retention(RUNTIME)"));
		assertTrue(code.contains("@Target({ TYPE, METHOD })"));
		assertTrue(code.contains("@Inherited"));
		assertFalse(code.startsWith("/**"));

		binding("cdi", "B2", "TYPE", false, true).finish();
		util.waitForNonIgnoredJobs();
		ed = new SWTWorkbenchBot().activeEditor();
		assertTrue(("B2.java").equals(ed.getTitle()));
		code = ed.toTextEditor().getText();
		L.fine(code);
		assertTrue(code.contains("@InterceptorBinding"));
		assertTrue(code.contains("@Retention(RUNTIME)"));
		assertTrue(code.contains("@Target({ TYPE })"));
		assertFalse(code.contains("@Inherited"));
		assertTrue(code.startsWith("/**"));

		binding("cdi", "B3", "TYPE", false, true).finish();
		util.waitForNonIgnoredJobs();
		ed = new SWTWorkbenchBot().activeEditor();
		assertTrue(("B3.java").equals(ed.getTitle()));
		code = ed.toTextEditor().getText();
		L.fine(code);
		assertTrue(code.contains("@InterceptorBinding"));
		assertTrue(code.contains("@Retention(RUNTIME)"));
		assertTrue(code.contains("@Target({ TYPE })"));
		assertFalse(code.contains("@Inherited"));
		assertTrue(code.startsWith("/**"));

		w = binding("cdi", "B4", "TYPE", true, false);
		w.addIBinding("cdi.B2");
		w.finish();
		util.waitForNonIgnoredJobs();
		ed = new SWTWorkbenchBot().activeEditor();
		assertTrue(("B4.java").equals(ed.getTitle()));
		code = ed.toTextEditor().getText();
		L.fine(code);
		assertTrue(code.contains("@InterceptorBinding"));
		assertTrue(code.contains("@Retention(RUNTIME)"));
		assertTrue(code.contains("@Target({ TYPE })"));
		assertTrue(code.contains("@Inherited"));
		assertFalse(code.startsWith("/**"));
		assertTrue(code.contains("@B2"));
	}

	@Test
	public void testStereotype() {
		CDIWizard w = stereotype("cdi", "S1", null, null, false, false, false,
				false);
		assertEquals(9, w.getScopes().size());
		assertEquals(5, w.getTargets().size());
		w.finish();
		util.waitForNonIgnoredJobs();
		SWTBotEditor ed = new SWTWorkbenchBot().activeEditor();
		assertTrue(("S1.java").equals(ed.getTitle()));
		String code = ed.toTextEditor().getText();
		L.fine(code);
		assertTrue(code.contains("@Stereotype"));
		assertTrue(code.contains("@Retention(RUNTIME)"));
		assertTrue(code.contains("@Target({ TYPE, METHOD, FIELD })"));
		assertFalse(code.contains("@Named"));
		assertFalse(code.contains("@Alternative"));
		assertFalse(code.contains("@Inherited"));
		assertFalse(code.startsWith("/**"));

		stereotype("cdi", "S2", "@Scope3", "FIELD", true, true, true, true)
				.finish();
		util.waitForNonIgnoredJobs();
		ed = new SWTWorkbenchBot().activeEditor();
		assertTrue(("S2.java").equals(ed.getTitle()));
		code = ed.toTextEditor().getText();
		L.fine(code);
		assertTrue(code.contains("@Stereotype"));
		assertTrue(code.contains("@Scope3"));
		assertTrue(code.contains("@Named"));
		assertTrue(code.contains("@Alternative"));
		assertTrue(code.contains("@Inherited"));
		assertTrue(code.contains("@Retention(RUNTIME)"));
		assertTrue(code.contains("@Target({ FIELD })"));
		assertTrue(code.startsWith("/**"));

		w = stereotype("cdi", "S3", null, null, false, false, true, false);
		w.addIBinding("cdi.B1");
		w.addStereotype("cdi.S1");
		w.finish();
		util.waitForNonIgnoredJobs();
		ed = new SWTWorkbenchBot().activeEditor();
		assertTrue(("S3.java").equals(ed.getTitle()));
		code = ed.toTextEditor().getText();
		L.fine(code);
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

	@Test
	public void testDecorator() {
		CDIWizard w = decorator("cdi", "", "java.lang.Comparable", null, true, true, false, false);
		w.finish();
		util.waitForNonIgnoredJobs();
		SWTBotEditor ed = new SWTWorkbenchBot().editorByTitle("ComparableDecorator.java");
		assertTrue(("ComparableDecorator.java").equals(ed.getTitle()));
		String code = ed.toTextEditor().getText();
		L.fine(code);
		assertTrue(code.contains("@Decorator"));
		assertTrue(code.contains("abstract class"));
		assertTrue(code.contains("@Delegate"));
		assertTrue(code.contains("@Inject"));
		assertTrue(code.contains("@Any"));
		assertTrue(code.contains("private Comparable<T> comparable;"));
		assertFalse(code.contains("final"));
		assertFalse(code.startsWith("/**"));

		w = decorator("cdi", "", "java.util.Map", "field", false, false, true, true);
		w.finish();
		util.waitForNonIgnoredJobs();
		ed = new SWTWorkbenchBot().editorByTitle("MapDecorator.java");
		assertTrue(("MapDecorator.java").equals(ed.getTitle()));
		code = ed.toTextEditor().getText();
		L.fine(code);
		assertTrue(code.contains("@Decorator"));
		assertFalse(code.contains("abstract"));
		assertTrue(code.contains("@Delegate"));
		assertTrue(code.contains("@Inject"));
		assertTrue(code.contains("@Any"));
		assertTrue(code.contains("private Map<K, V> field;"));
		assertTrue(code.contains("final class"));
		assertTrue(code.startsWith("/**"));
	}
	
	@Test
	public void testInterceptor() {
		CDIWizard w = interceptor("cdi", "I1", "B2", null, null, false);
		w.finish();
		util.waitForNonIgnoredJobs();
		SWTBotEditor ed = new SWTWorkbenchBot().editorByTitle("I1.java");
		assertTrue(("I1.java").equals(ed.getTitle()));
		String code = ed.toTextEditor().getText();
		L.fine(code);
		assertTrue(code.contains("@B2"));
		assertTrue(code.contains("@Interceptor"));
		assertTrue(code.contains("@AroundInvoke"));
		assertTrue(code.contains("public Object manage(InvocationContext ic) throws Exception {"));
		assertFalse(code.contains("final"));
		assertFalse(code.startsWith("/**"));
		
		w = interceptor("cdi", "I2", "B4", "java.util.Date", "sample", true);
		w.finish();
		util.waitForNonIgnoredJobs();
		ed = new SWTWorkbenchBot().editorByTitle("I2.java");
		assertTrue(("I2.java").equals(ed.getTitle()));
		code = ed.toTextEditor().getText();
		L.fine(code);
		assertTrue(code.contains("@B4"));
		assertTrue(code.contains("@Interceptor"));
		assertTrue(code.contains("@AroundInvoke"));
		assertTrue(code.contains("public Object sample(InvocationContext ic) throws Exception {"));
		assertFalse(code.contains("final"));
		assertTrue(code.startsWith("/**"));
		assertTrue(code.contains("extends Date"));
	}
	
	@Test
	public void testBeansXml() {
		CDIWizard w = new NewCDIFileWizard(CDIWizardType.BEANS_XML).run();
		w.setSourceFolder(PROJECT_NAME + "/WebContent/WEB-INF");
		assertFalse(w.canFinish());
		w.setSourceFolder(PROJECT_NAME + "/src/cdi");
		assertTrue(w.canFinish());
		w.finish();
		w = new NewCDIFileWizard(CDIWizardType.BEANS_XML).run();
		assertFalse(w.canFinish());
		w.cancel();
	}
	
	@Test
	public void testBean() {
		CDIWizard w = bean("cdi", "Bean1", true, true, false, false, null, null, null, null);
		w.finish();
		util.waitForNonIgnoredJobs();
		SWTBotEditor ed = new SWTWorkbenchBot().activeEditor();
		assertTrue(("Bean1.java").equals(ed.getTitle()));
		String code = ed.toTextEditor().getText();
		L.fine(code);
		assertTrue(code.contains("package cdi;"));
		assertTrue(code.contains("public abstract class Bean1 {"));
		assertFalse(code.contains("@Named"));
		assertFalse(code.contains("final"));
		assertFalse(code.startsWith("/**"));
		
		w = bean("cdi", "Bean2", false, false, true, true, "", null, "@Dependent", null);
		w.finish();
		util.waitForNonIgnoredJobs();
		ed = new SWTWorkbenchBot().activeEditor();
		assertTrue(("Bean2.java").equals(ed.getTitle()));
		code = ed.toTextEditor().getText();
		L.fine(code);
		assertTrue(code.contains("package cdi;"));
		assertTrue(code.contains("@Named"));
		assertFalse(code.contains("@Named("));
		assertTrue(code.contains("@Dependent"));
		assertTrue(code.contains("final class Bean2 {"));
		assertTrue(code.startsWith("/**"));

		w = bean("cdi", "Bean3", true, false, false, true, "TestedBean", null, "@Scope2", "Q1");
		w.finish();
		util.waitForNonIgnoredJobs();
		ed = new SWTWorkbenchBot().activeEditor();
		assertTrue(("Bean3.java").equals(ed.getTitle()));
		code = ed.toTextEditor().getText();
		L.fine(code);
		assertTrue(code.contains("package cdi;"));
		assertTrue(code.contains("@Named(\"TestedBean\")"));
		assertTrue(code.contains("@Scope2"));
		assertTrue(code.contains("@Q1"));
		assertTrue(code.contains("public class Bean3 {"));
		assertFalse(code.contains("final"));
		assertTrue(code.startsWith("/**"));
	}
	
	@Test
	public void testAnnLiteral() {
		CDIWizard w = annLiteral("cdi", "AnnL1", true, false, true, false, "cdi.Q1");
		w.finish();
		util.waitForNonIgnoredJobs();
		SWTBotEditor ed = new SWTWorkbenchBot().activeEditor();
		assertTrue(("AnnL1.java").equals(ed.getTitle()));
		String code = ed.toTextEditor().getText();
		L.info(code);
		assertTrue(code.contains("package cdi;"));
		assertTrue(code.contains("public final class AnnL1 extends AnnotationLiteral<Q1> implements Q1"));
		assertTrue(code.contains("public static final Q1 INSTANCE = new AnnL1();"));
		assertFalse(code.contains("abstract"));
		assertFalse(code.startsWith("/**"));
		
		w = annLiteral("cdi", "AnnL2", false, true, false, true, "Q2");
		w.finish();
		util.waitForNonIgnoredJobs();
		ed = new SWTWorkbenchBot().activeEditor();
		assertTrue(("AnnL2.java").equals(ed.getTitle()));
		code = ed.toTextEditor().getText();
		L.info(code);
		assertTrue(code.contains("package cdi;"));
		assertTrue(code.contains("abstract class AnnL2 extends AnnotationLiteral<Q2> implements Q2 {"));
		assertTrue(code.contains("public static final Q2 INSTANCE = new AnnL2();"));
		assertFalse(code.substring(code.indexOf("final") + 5).contains("final"));
		assertTrue(code.contains("abstract"));
		assertTrue(code.startsWith("/**"));
	}

	

	private CDIWizard qualifier(String pkg, String name, boolean inherited,
			boolean comments) {
		return create(CDIWizardType.QUALIFIER, pkg, name, inherited, comments);
	}
	

	private CDIWizard scope(String pkg, String name, boolean inherited,
			boolean comments, boolean normalScope, boolean passivating) {
		CDIWizard w = create(CDIWizardType.SCOPE, pkg, name, inherited,
				comments);
		w = w.setNormalScope(normalScope);
		return normalScope ? w.setPassivating(passivating) : w;
	}
	

	private CDIWizard binding(String pkg, String name, String target,
			boolean inherited, boolean comments) {
		CDIWizard w = create(CDIWizardType.INTERCEPTOR_BINDING, pkg, name,
				inherited, comments);
		return target != null ? w.setTarget(target) : w;
	}
	

	private CDIWizard stereotype(String pkg, String name, String scope,
			String target, boolean inherited, boolean named,
			boolean alternative, boolean comments) {
		CDIWizard w = create(CDIWizardType.STEREOTYPE, pkg, name, inherited,
				comments).setAlternative(alternative).setNamed(named);
		if (scope != null) {
			w = w.setScope(scope);
		}
		return target != null ? w.setTarget(target) : w;
	}
	

	private CDIWizard decorator(String pkg, String name, String intf, String fieldName,
			boolean isPublic, boolean isAbstract, boolean isFinal, boolean comments) {
		CDIWizard w = create(CDIWizardType.DECORATOR, pkg, name, comments);
		w = w.addInterface(intf).setPublic(isPublic).setFinal(isFinal).setAbstract(isAbstract);
		return fieldName != null ? w.setFieldName(fieldName) : w;
	}
	
	
	private CDIWizard interceptor(String pkg, String name, String ibinding,
			String superclass, String method, boolean comments) {
		CDIWizard w = create(CDIWizardType.INTERCEPTOR, pkg, name, comments);
		if (superclass != null) {
			w = w.setSuperclass(superclass);
		}
		if (method != null) {
			w = w.setMethodName(method);
		}
		return w.addIBinding(ibinding);
	}
	
	
	private CDIWizard bean(String pkg, String name, boolean isPublic, boolean isAbstract,
			boolean isFinal, boolean comments, String named,
			String interfaces, String scope, String qualifier) {
		CDIWizard w = create(CDIWizardType.BEAN, pkg, name, comments);
		if (named != null) {
			w.setNamed(true);
			if (!"".equals(named.trim())) {
				w.setNamedName(named);
			}
		}
		w = w.setPublic(isPublic).setFinal(isFinal).setAbstract(isAbstract);
		if (interfaces != null && !"".equals(interfaces.trim())) {
			w.addInterface(interfaces);
		}
		if (scope != null && !"".equals(scope.trim())) {
			w.setScope(scope);
		}
		if (qualifier != null && !"".equals(qualifier.trim())) {
			w.addQualifier(qualifier);
		}
		return w;
	}
	
	private CDIWizard annLiteral(String pkg, String name, boolean isPublic, boolean isAbstract,
			boolean isFinal, boolean comments, String qualifier) {
		assert qualifier != null && !"".equals(qualifier.trim()) : "Qualifier has to be set"; 
		CDIWizard w = create(CDIWizardType.ANNOTATION_LITERAL, pkg, name, comments);
		return w.setPublic(isPublic).setFinal(isFinal).setAbstract(isAbstract).addQualifier(qualifier);
	}
	
	private CDIWizard create(CDIWizardType type, String pkg, String name,
			boolean inherited, boolean comments) {
		return create(type, pkg, name, comments).setInherited(inherited);
	}

	private CDIWizard create(CDIWizardType type, String pkg, String name, boolean comments) {
		CDIWizard p = new NewCDIFileWizard(type).run();
		return p.setPackage(pkg).setName(name).setGenerateComments(comments);
	}

}
