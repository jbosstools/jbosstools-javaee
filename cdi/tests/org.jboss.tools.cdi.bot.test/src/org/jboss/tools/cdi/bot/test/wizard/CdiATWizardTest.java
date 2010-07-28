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
package org.jboss.tools.cdi.bot.test.wizard;

import java.util.logging.Logger;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.finders.UIThreadRunnable;
import org.eclipse.swtbot.swt.finder.results.Result;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
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
import org.jboss.tools.ui.bot.ext.helper.ContextMenuHelper;
import org.jboss.tools.ui.bot.ext.types.ViewType;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

@SWTBotTestRequires(perspective = "Java EE", server = @Server(state = ServerState.NotRunning, version = "6.0", operator = "<="))
@RunWith(RequirementAwareSuite.class)
@SuiteClasses({ CdiATWizardTest.class })
public class CdiATWizardTest extends SWTTestExt {

	private static final String PROJECT_NAME = "CDIProject";
	private static final Logger L = Logger.getLogger(CdiATWizardTest.class
			.getName());

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

		// Configure Add CDI Support...
		nodeContextMenu(tree, t, "Configure", "Add CDI support...").click();
		bot.activeShell().bot().button("OK").click();
		util.waitForNonIgnoredJobs();
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

	private static SWTBotMenu nodeContextMenu(final SWTBotTree tree,
			SWTBotTreeItem item, final String... menu) {
		assert menu.length > 0;
		ContextMenuHelper.prepareTreeItemForContextMenu(tree, item);
		return UIThreadRunnable.syncExec(new Result<SWTBotMenu>() {

			public SWTBotMenu run() {
				SWTBotMenu m = new SWTBotMenu(ContextMenuHelper.getContextMenu(
						tree, menu[0], false));
				for (int i = 1; i < menu.length; i++) {
					m = m.menu(menu[i]);
				}
				return m;
			}
		});
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

	private CDIWizard create(CDIWizardType type, String pkg, String name,
			boolean inherited, boolean comments) {
		CDIWizard p = new NewCDIFileWizard(type).run();
		return p.setPackage(pkg).setName(name).setInherited(inherited)
				.setGenerateComments(comments);
	}

}
