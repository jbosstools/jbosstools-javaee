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
package org.jboss.tools.struts.ui.bot.test.tutorial;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.finders.WorkbenchContentsFinder;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEclipseEditor;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefContextMenu;
import org.eclipse.swtbot.eclipse.gef.finder.widgets.SWTBotGefEditPart;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.finders.UIThreadRunnable;
import org.eclipse.swtbot.swt.finder.matchers.AbstractMatcher;
import org.eclipse.swtbot.swt.finder.matchers.WidgetOfType;
import org.eclipse.swtbot.swt.finder.results.Result;
import org.eclipse.swtbot.swt.finder.results.VoidResult;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotBrowser;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotStyledText;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTable;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotToolbarButton;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.eclipse.ui.IEditorReference;
import org.hamcrest.Description;
import org.jboss.tools.struts.ui.bot.test.utils.DndSupport;
import org.jboss.tools.struts.ui.bot.test.utils.PartMatcher;
import org.jboss.tools.struts.ui.bot.test.utils.StrutsUIEditorBot;
import org.jboss.tools.struts.ui.bot.test.utils.ValidationUIEditorBot;
import org.jboss.tools.struts.ui.bot.test.utils.WizardBot;
import org.jboss.tools.ui.bot.ext.RequirementAwareSuite;
import org.jboss.tools.ui.bot.ext.SWTEclipseExt;
import org.jboss.tools.ui.bot.ext.SWTTestExt;
import org.jboss.tools.ui.bot.ext.config.Annotations.SWTBotTestRequires;
import org.jboss.tools.ui.bot.ext.config.Annotations.Server;
import org.jboss.tools.ui.bot.ext.config.Annotations.ServerState;
import org.jboss.tools.ui.bot.ext.helper.ContextMenuHelper;
import org.jboss.tools.ui.bot.ext.parts.SWTBotBrowserExt;
import org.jboss.tools.ui.bot.ext.types.EntityType;
import org.jboss.tools.ui.bot.ext.types.IDELabel;
import org.jboss.tools.ui.bot.ext.types.ViewType;
import org.jboss.tools.ui.bot.test.SWTBotJSPMultiPageEditor;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

/**
 * Sanity test for Struts tooling support in JBoss Tools based on the Struts Tools tutorial
 *
 * @author jlukas
 * @see <a href="http://download.jboss.org/jbosstools/nightly-docs/en/struts_tools_tutorial/html/index.html">Struts Tutorial</a>
 */
@SWTBotTestRequires(server = @Server(state = ServerState.NotRunning), perspective = "Web Development")
@RunWith(RequirementAwareSuite.class)
@SuiteClasses({TutorialTest.class})
public class TutorialTest extends SWTTestExt {

    private static final Logger L = Logger.getLogger(TutorialTest.class.getName());
    private static final String PROJECT_NAME = "StrutsHello";

    @Before
    public void waitForJobs() {
    	util.waitForNonIgnoredJobs();
    }
    
    /**
     * 2.1. Starting Up
     */
    @Test
    public void testCreateProject() {
        eclipse.createNew(EntityType.STRUTS_PROJECT);
        bot.shell(IDELabel.Shell.NEW_STRUTS_PROJECT).activate();
        bot.textWithLabel(IDELabel.NewStrutsProjectDialog.NAME).setText(
                PROJECT_NAME);
        bot.button(IDELabel.Button.NEXT).click();
        bot.button(IDELabel.Button.NEXT).click();
        SWTBotTreeItem[] ti = bot.tree().getAllItems();
        Assert.assertTrue("struts-html.tld checked", ti[1].isChecked());
        Assert.assertTrue("struts-logic.tld checked", ti[3].isChecked());
        Assert.assertTrue("struts-bean.tld checked", ti[4].isChecked());
        bot.button(IDELabel.Button.FINISH).click();
        bot.sleep(3000);
        SWTBot v = eclipse.showView(ViewType.PACKAGE_EXPLORER);
        SWTBotTree tree = v.tree();
        tree.setFocus();
        assertTrue("Project " + PROJECT_NAME + " was not created properly.",
                SWTEclipseExt.treeContainsItemWithLabel(tree, PROJECT_NAME));
        SWTBotTreeItem t = tree.getTreeItem(PROJECT_NAME);
        t.expand();
        t = t.expandNode("WebContent", "WEB-INF");
        assertNotNull("Project " + PROJECT_NAME + " was not created properly.",
                t.getNode("struts-config.xml"));
    }

    /**
     * 2.2. Creating the Application Components
     */
    @Test
    public void testAddJSPs() {
        //2.2.1.1. Creating the Page Placeholders
        SWTBotView v = bot.viewByTitle(IDELabel.View.WEB_PROJECTS);
        v.show();
        SWTBotTree tree = v.bot().tree();
        final SWTBotTreeItem projectNode = tree.getTreeItem(PROJECT_NAME);
        UIThreadRunnable.syncExec(new VoidResult() {

            public void run() {
                projectNode.expand();
            }
        });
        SWTBotTreeItem webRootNode = projectNode.getNode("WEB-ROOT (WebContent)");
        nodeContextMenu(tree, webRootNode, "New", "Folder...").click();
        handleWizard("pages");

        nodeContextMenu(tree, webRootNode.getNode("pages"), "New", "File", "JSP...").click();
        handleStandardWizard("inputname");

        nodeContextMenu(tree, webRootNode.getNode("pages"), "New", "File", "JSP...").click();
        handleStandardWizard("greeting");

        //2.2.1.2. Placing the Page Placeholders
        nodeContextMenu(
                tree,
                projectNode.expandNode("Configuration", "default", "struts-config.xml"), "Open").click();
        SWTBotEditor botEditor = bot.activeEditor();
        StrutsUIEditorBot ge = new StrutsUIEditorBot(botEditor.getReference());
        Control c = ge.getControl();

        SWTBotTreeItem s = webRootNode.expandNode("pages").getNode("inputname.jsp");
        Widget w1 = s.widget;
        s.select();

        DndSupport.dnd((TreeItem) w1, (FigureCanvas) c);
        s = webRootNode.getNode("pages").getNode("greeting.jsp");
        s.select();
        w1 = s.widget;
        DndSupport.dnd((TreeItem) w1, (FigureCanvas) c);
        bot.sleep(500);
        ge.clickContextMenu("Auto-Layout");
        SWTBotShell sh = bot.activeShell();
        sh.bot().button("OK").click();
        bot.sleep(500);

        //2.2.2. Creating an Action Mappings
        new SWTBotGefContextMenu(c, "Action...").click();
        sh = bot.activeShell();
        sh.bot().textWithLabel("Path:*").setText("/greeting");
        sh.bot().comboBoxWithLabel("Name:").setText("GetNameForm");
        sh.bot().comboBoxWithLabel("Scope:").setSelection("request");
        sh.bot().textWithLabel("Type:").setText("sample.GreetingAction");
        sh.bot().button("Finish").click();

        //2.2.3. Creating a Link
        ge.activateTool("Create New Connection");
        SWTBotGefEditPart part = ge.mainEditPart();
        part.descendants(new PartMatcher("inputname.jsp")).get(0).click();
        bot.sleep(500);
        part.descendants(new PartMatcher("GetNameForm")).get(0).click();
        bot.sleep(500);

        //2.2.4. Creating a Forward
        ge.activateTool("Create New Connection");
        part.descendants(new PartMatcher("GetNameForm")).get(0).click();
        bot.sleep(500);
        part.descendants(new PartMatcher("greeting.jsp")).get(0).click();
        bot.sleep(500);
        ge.clickContextMenu("Auto-Layout");
        sh = bot.activeShell();
        sh.bot().button("OK").click();
        bot.sleep(500);
        botEditor.save();

        ge.selectPage("Tree");
        SWTBotTreeItem item = botEditor.bot().tree().expandNode("struts-config.xml", "action-mappings", "/greeting");
        item.getNode("greeting").select();
        bot.activeEditor().bot().textWithLabel("Name:").setText("sayHello");
        ge.selectPage("Diagram");

        //2.2.5. Creating a Global Forward
        new SWTBotGefContextMenu(c, "Global Forward...").click();
        sh = bot.activeShell();
        sh.bot().textWithLabel("Name:*").setText("getName");
        sh.bot().button(0).click();
        SWTBotShell sh2 = sh.bot().activeShell();
        sh2.bot().tabItem("Pages").activate();
        sh2.bot().tree().expandNode("StrutsHello", "WEB-ROOT (WebContent)", "pages").getNode("inputname.jsp").select();
        sh2.bot().button(0).click();
        sh.bot().button("Finish").click();
        botEditor.save();

        //2.2.6. Creating a Form Bean
        ge.selectPage("Tree");
        tree = botEditor.bot().tree();
        item = tree.getTreeItem("struts-config.xml").getNode("form-beans");
        nodeContextMenu(tree, item, "Create Form Bean...").click();
        sh = bot.activeShell();
        sh.bot().textWithLabel("Name:*").setText("GetNameForm");
        sh.bot().textWithLabel("Type:*").setText("sample.GetNameForm");
        sh.bot().button("Finish").click();
        botEditor.save();
        util.waitForNonIgnoredJobs();
    }

    /**
     * Chapter 3. Generating Stub Coding
     */
    @Test
    public void testGenerateClasses() {
        StrutsUIEditorBot guiBot = new StrutsUIEditorBot(bot.activeEditor().getReference());
        guiBot.selectPage("Diagram");
        guiBot.clickContextMenu("Generate Java Code...");
        SWTBot sh = bot.activeShell().bot();
        sh.button(1).click();
        util.waitForNonIgnoredJobs();
        String status = sh.text().getText();
        sh.button("Finish").click();
        Assert.assertTrue(status.contains("Generated classes: 2"));
        Assert.assertTrue(status.contains("Actions: 1"));
        Assert.assertTrue(status.contains("Form beans: 1"));
    }

    /**
     * Chapter 4. Coding the Various Files
     */
    @Test
    public void testCoding() {
    	SWTBotView pe = new SWTWorkbenchBot().viewByTitle("Package Explorer");
    	pe.show();
        SWTBotTree fTree = pe.bot().tree();
        nodeContextMenu(fTree, fTree.getTreeItem(PROJECT_NAME), "Refresh");
        util.waitForNonIgnoredJobs();
        //4.1.1. GetNameForm.java
        SWTBotEditor editor = packageExplorer.openFile(PROJECT_NAME, "JavaSource", "sample", "GetNameForm.java");
        SWTBotEclipseEditor eeditor = editor.toTextEditor();
        eeditor.selectRange(0, 0, eeditor.getText().length());
        eeditor.setText(readResource(TutorialTest.class.getResourceAsStream("resources/GetNameForm.java.gf")));
        editor.saveAndClose();

        //4.1.2. GreetingAction.java
        editor = packageExplorer.openFile(PROJECT_NAME, "JavaSource", "sample", "GreetingAction.java");
        eeditor = editor.toTextEditor();
        eeditor.selectRange(0, 0, eeditor.getText().length());
        eeditor.setText(readResource(TutorialTest.class.getResourceAsStream("resources/GreetingAction.java.gf")));
        editor.saveAndClose();

        //4.2.1. inputname.jsp
        SWTBotView v = bot.viewByTitle(IDELabel.View.WEB_PROJECTS);
        v.show();
//TODO: D'n'D
//		SWTBotTree tree = v.bot().tree();
//		final SWTBotTreeItem projectNode = tree.getTreeItem(PROJECT_NAME);
//		SWTBotTreeItem item = projectNode.getNode("Configuration").expand().getNode("default").expand().getNode("struts-config.xml").expand().getNode("action-mappings").expand().getNode("/greeting");
//		item.select();

        editor = bot.editorByTitle("inputname.jsp");
        editor.show();
        SWTBotJSPMultiPageEditor editorA = new SWTBotJSPMultiPageEditor(bot.editorByTitle("inputname.jsp").getReference(), new SWTWorkbenchBot());
        editorA.selectTab("Source");
        SWTBotStyledText st = editorA.bot().styledText();
        st.selectRange(0, 0, st.getText().length());
        st.setText(readResource(TutorialTest.class.getResourceAsStream("resources/inputname.jsp.gf")));
        editor.saveAndClose();

//		st.navigateTo(7, 24);
//		st.typeText("x");
//		final StyledText text = st.widget;
//
//		Point p = UIThreadRunnable.syncExec(new Result<Point>() {
//
//			public Point run() {
//				int i = text.getCaretOffset();
//				L.info("caret offset: " + i);
//				L.info("caret locati: " + text.getLocationAtOffset(i).x + ":" + text.getLocationAtOffset(i).y);
//				return text.getLocationAtOffset(i);
//			}
//		});
//		DndSupport.dnd(item.widget, (Control) editorA.getWidget(), p.x + 2, p.y + 2);
//		bot.sleep(30000);

        //4.2.2. greeting.jsp
        editor = bot.editorByTitle("greeting.jsp");
        editor.show();
        editorA = new SWTBotJSPMultiPageEditor(bot.editorByTitle("greeting.jsp").getReference(), new SWTWorkbenchBot());
        editorA.selectTab("Source");
        st = editorA.bot().styledText();
        st.selectRange(0, 0, st.getText().length());
        st.setText(readResource(TutorialTest.class.getResourceAsStream("resources/greeting.jsp.gf")));
        editor.saveAndClose();

        //4.2.3. index.jsp
        SWTBotTree tree = v.bot().tree();
        final SWTBotTreeItem projectNode = tree.getTreeItem(PROJECT_NAME);
        nodeContextMenu(tree, projectNode.getNode("WEB-ROOT (WebContent)"), "New", "File", "JSP...").click();
        handleStandardWizard("index");
        editor = bot.editorByTitle("index.jsp");
        editor.show();
        editorA = new SWTBotJSPMultiPageEditor(bot.editorByTitle("index.jsp").getReference(), new SWTWorkbenchBot());
        editorA.selectTab("Source");
        st = bot.styledText();
        st.selectRange(0, 0, st.getText().length());
        st.setText(readResource(TutorialTest.class.getResourceAsStream("resources/index.jsp.gf")));
        editor.saveAndClose();
    }

    /**
     * Chapter 5. Compiling the Classes and Running the Application
     */
    @Test
    public void testStartServer() {
        servers.startServer(configuredState.getServer().name);
        configuredState.getServer().isRunning = true;
        StrutsUIEditorBot gui = new StrutsUIEditorBot(bot.activeEditor().getReference());
        gui.mainEditPart().descendants(new PartMatcher("getName")).get(0).select();
        gui.save();
        util.waitForNonIgnoredJobs();
        new SWTBotGefContextMenu(gui.getControl(), "Run on Server").click();
        SWTBotBrowser browser = bot.browser();
        bot.sleep(7500);
        browser.refresh();
        bot.sleep(5000);
        L.info(browser.getText());
        Assert.assertTrue(browser.getText().contains("Input name:"));
    }

    /**
     * Chapter 6. Struts Validation Examples
     */
    @Test
    public void testValidators() {
        //6.1. Starting Point
        SWTBotView v = bot.viewByTitle(IDELabel.View.WEB_PROJECTS);
        v.show();

        //6.2. Defining the Validation Rule
        SWTBotTree tree = v.bot().tree();
        SWTBotTreeItem projectNode = tree.getTreeItem(PROJECT_NAME);
        final SWTBotTreeItem config = projectNode.expandNode("Configuration", "default", "struts-config.xml");
        nodeContextMenu(
                tree, config.getNode("plug-ins"), "Create Special Plug-in", "Validators").click();
        nodeContextMenu(tree, projectNode.getNode("Resource Bundles"), "New", "Properties File...").click();
        SWTBotShell sh = bot.activeShell();
        sh.bot().button("Browse...").click();
        SWTBotShell sh2 = sh.bot().activeShell();
        sh2.bot().tree().getTreeItem("StrutsHello").expandNode("JavaSource", "sample").select();
        sh2.bot().button("OK").click();
        sh.bot().textWithLabel("Name:*").setText("applResources");
        sh.bot().button("Finish").click();
        SWTBotTreeItem item = projectNode.getNode("Resource Bundles").getNode("sample.applResources");
        nodeContextMenu(tree, item, "New", "Default Error Messages").click();
        DndSupport.dnd(item.widget, config.getNode("resources").widget);
        nodeContextMenu(tree, projectNode.expandNode("Validation").getNode("validation.xml"), "Open").click();
        
        SWTBotEditor editor = bot.activeEditor();
        ValidationUIEditorBot vb = new ValidationUIEditorBot(editor.getReference(), new SWTWorkbenchBot());
        vb.toolbarButton("Create Formset").click();

        sh = bot.activeShell();
        sh.bot().button("Finish").click();
        SWTBotTree t = vb.bot().tree();
        SWTBotTreeItem ti = t.getTreeItem("formset (default)").expand();
        item = UIThreadRunnable.syncExec(new Result<SWTBotTreeItem>() {

            public SWTBotTreeItem run() {
                return config.expandNode("form-beans");
            }
        }).getNode("GetNameForm").select();
        DndSupport.dnd(item.widget, ti.widget);
        ti = t.expandNode("formset (default)").getNode("GetNameForm").select();
        vb.toolbarButton("Create Field").click();
        sh = bot.activeShell();
        sh.bot().textWithLabel("Property:*").setText("name");
        sh.bot().button("Finish").click();
        ti = ti.expand().getNode("name").select();
        vb.toolbarButton("Edit").click();
        sh = bot.activeShell();
        SWTBotTable table = sh.bot().table();
        table.click(2, 1);
        sh.bot().button("...").click();
        sh2 = bot.activeShell();
        sh2.bot().table().select("required");
        sh2.bot().button("Add ->").click();
        sh2.bot().button("Ok").click();
        sh.bot().button("Close").click();
        vb.bot().button("Add", 1).click();
        sh = bot.activeShell();
        sh.bot().button("Browse...").click();
        sh2 = bot.activeShell();
        sh2.bot().button("Add").click();
        SWTBotShell sh3 = bot.activeShell();
        sh3.bot().textWithLabel("Name:*").setText("name.required");
        sh3.bot().textWithLabel("Value:").setText("Person's name");
        sh3.bot().button("Finish").click();
        sh2.bot().button("Ok").click();
        sh.bot().button("Finish").click();
        vb.saveAndClose();

        //6.3. Client-Side Validation
        v.show();
        ti = projectNode.expandNode("WEB-ROOT (WebContent)", "pages").getNode("inputname.jsp");
        nodeContextMenu(tree, ti, "Open").click();

        SWTBotJSPMultiPageEditor jspEditor = new SWTBotJSPMultiPageEditor(bot.editorByTitle("inputname.jsp").getReference(), new SWTWorkbenchBot());
        jspEditor.selectTab("Source");
        SWTBotStyledText st = jspEditor.bot().styledText();
        st.selectRange(0, 0, st.getText().length());
        st.setText(readResource(TutorialTest.class.getResourceAsStream("resources/inputname63.jsp.gf")));
        jspEditor.save();
        bot.editorByTitle("struts-config.xml").save();
        util.waitForNonIgnoredJobs();
        bot.sleep(2500);
        servers.show();
        SWTBotTree srvs = servers.tree();
        SWTBotTreeItem s = getProjectNodeFromServerView(srvs);
        nodeContextMenu(servers.tree(), s, "Full Publish").click();
        bot.sleep(2500);
        SWTBotToolbarButton tb = bot.activeShell().bot().toolbarButtonWithTooltip("Touch descriptors");
       	tb.click();
        bot.sleep(2500);
        SWTBotEditor ed = new SWTBotEditor(getBrowserReference(), new SWTWorkbenchBot());
        ed.show();
        Browser b = ed.bot().widget(WidgetOfType.widgetOfType(Browser.class));
        SWTBotBrowserExt browser = new SWTBotBrowserExt(b);
        String out1 = refreshBrowser(browser);
        L.info(out1);
        //6.4. Server Side Validation

        //6.5. Editing the JSP File
        jspEditor.show();
        st.selectRange(0, 0, st.getText().length());
        st.setText(readResource(TutorialTest.class.getResourceAsStream("resources/inputname65.jsp.gf")));
        jspEditor.saveAndClose();

        //6.6. Editing the Action
        nodeContextMenu(tree, config.expandNode("action-mappings").getNode("/greeting"), "Properties").click();
        sh = bot.activeShell();
        table = sh.bot().table();
        table.click(7, 1);
        sh.bot().button("...").click();
        sh2 = bot.activeShell();
        sh2.bot().tabItem("Pages").activate();
        sh2.bot().tree().expandNode("StrutsHello", "WEB-ROOT (WebContent)", "pages").getNode("inputname.jsp").select();
        sh2.bot().button("OK").click();
        sh.bot().button("Close").click();

        //6.7. Editing the Form Bean
        nodeContextMenu(tree, config.expandNode("action-mappings").getNode("/greeting"), "Open Form-bean Source").click();
        editor = bot.editorByTitle("GetNameForm.java");
        editor.show();
        st = editor.bot().styledText();
        st.selectRange(0, 0, st.getText().length());
        st.setText(readResource(TutorialTest.class.getResourceAsStream("resources/GetNameForm67.java.gf")));
        editor.saveAndClose();
        new StrutsUIEditorBot(bot.editorByTitle("struts-config.xml").getReference()).show();
        bot.activeShell().bot().menu("File").menu("Save All").click();
        bot.sleep(1000);
        util.waitForNonIgnoredJobs();
        tb = bot.activeShell().bot().toolbarButtonWithTooltip("Touch descriptors");
       	tb.click();
       	ed.show();
       	String out2 = refreshBrowser(browser);
        L.info(out2);
        boolean b1 = out1.contains("onsubmit=\"return validateGetNameForm(this)\"");
        boolean b2 = !out2.contains("onsubmit=\"return validateGetNameForm(this)\"");
        Assert.assertTrue(b1 || b2);
        Assert.assertTrue(b1);
        Assert.assertTrue(b2);
    }

    private SWTBotMenu nodeContextMenu(final SWTBotTree tree,
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

    private void handleWizard(String itemName) {
        assert itemName != null && itemName.trim().length() > 0;
        WizardBot sh = new WizardBot(bot.activeShell());
        sh.setName(itemName);
        sh.finish();
    }

    private void handleStandardWizard(String itemName) {
    	SWTBot sh = bot.activeShell().bot();
    	sh.textWithLabel("File name:").setText(itemName);
    	sh.button("Finish").click();
    	bot.sleep(1500);
    }
    
    
    private String readResource(InputStream is) {
        StringBuilder sb = new StringBuilder();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(is));
            String s;
            while ((s = br.readLine()) != null) {
                sb.append(s);
                sb.append('\n');
            }
        } catch (IOException e) {
            L.log(Level.WARNING, e.getMessage(), e);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    L.log(Level.FINEST, e.getMessage(), e);
                }
            }
        }
        return sb.toString();
    }
    
    private IEditorReference getBrowserReference() {
    	List<IEditorReference> refs = new WorkbenchContentsFinder().findEditors(new AbstractMatcher<IEditorReference>() {

			@Override
			protected boolean doMatch(Object item) {
				IEditorReference ref = (IEditorReference) item;
				return "Web Browser".equals(ref.getName());
			}

			public void describeTo(Description description) {
			}
		});
    	return refs.get(0);
    }
    
    private String refreshBrowser(SWTBotBrowserExt b) {
        util.waitForNonIgnoredJobs();
        bot.sleep(10000);
        b.refresh();
        bot.sleep(5000);
        b.refresh();
        util.waitForNonIgnoredJobs();
        bot.sleep(1000);
        return b.getText();
    }

    private SWTBotTreeItem getProjectNodeFromServerView(SWTBotTree serversTree) {
    	SWTBotTreeItem s = null;
        for (SWTBotTreeItem i: serversTree.getAllItems()) {
			if (i.getText().startsWith(configuredState.getServer().name)) {
				s = i;
				break;
			}
		}
        s.expand();
        for (SWTBotTreeItem i: s.getItems()) {
        	if (i.getText().contains(PROJECT_NAME)) {
        		s = i;
        		break;
        	}
        }
        return s;
    }
}
