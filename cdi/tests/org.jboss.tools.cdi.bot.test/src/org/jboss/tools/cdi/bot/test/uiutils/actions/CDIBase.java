package org.jboss.tools.cdi.bot.test.uiutils.actions;

import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEclipseEditor;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTable;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.jboss.tools.cdi.bot.test.uiutils.wizards.DynamicWebProjectWizard;
import org.jboss.tools.ui.bot.ext.SWTBotExt;
import org.jboss.tools.ui.bot.ext.SWTJBTExt;
import org.jboss.tools.ui.bot.ext.SWTTestExt;
import org.jboss.tools.ui.bot.ext.SWTUtilExt;
import org.jboss.tools.ui.bot.ext.Timing;
import org.jboss.tools.ui.bot.ext.view.ProjectExplorer;

public class CDIBase extends SWTTestExt {

	public enum CDICOMPONENT {
		STEREOSCOPE, QUALIFIER, SCOPE, BEAN, INTERBINDING, DECORATOR, INTERCEPTOR, ANNLITERAL, BEANSXML
	}
	public enum ANNOTATIONS {SERIALIZABLE, TARGET, RETENTION, NAMED, TYPED, DISPOSES, OBSERVES, INTERCEPTOR, 
		  SPECIALIZES, DECORATOR, NONBINDING}
	public enum PROBLEM_TYPE {WARNINGS, ERRORS}

	private SWTBotEclipseEditor ed;

	public SWTBotEclipseEditor getEd() {
		return ed;
	}

	public void setEd(SWTBotEclipseEditor ed) {
		this.ed = ed;
	}

	public void createComponent(CDICOMPONENT component, String name,
			String packageName, String necessaryParam) {
		if (component == null) {
			CDIUtil.annotation(open, util, packageName, name);
		} else {			
			createCDIComponent(component, name, packageName, necessaryParam);						
		}
		util.waitForNonIgnoredJobs();		
		setEd(bot.activeEditor().toTextEditor());
	}

	private void createCDIComponent(CDICOMPONENT component, String name,
			String packageName, String necessaryParam) {
		switch (component) {
		case STEREOSCOPE:
			CDIUtil.stereotype(packageName, name, null, null, false, false, false,
					false).finish();
			break;
		case QUALIFIER:
			CDIUtil.qualifier(packageName, name, false, false).finish();
			break;
		case SCOPE:
			CDIUtil.scope(packageName, name, false, false, true, false).finish();
			break;
		case BEAN:
			CDIUtil.bean(packageName, name, true, false, false, false, null, null,
					null, null).finish();
			break;
		case INTERCEPTOR:
			CDIUtil.interceptor(packageName, name, null, null, null, false).finish();
			break;
		case DECORATOR:
			CDIUtil.decorator(packageName, name, necessaryParam, null, true, false, false, false)
					.finish();
			break;
		case ANNLITERAL:
			CDIUtil.annLiteral(packageName, name, true, false, false, false, null).finish();
			break;
		case INTERBINDING:
			CDIUtil.binding(packageName, name, null, true, false).finish();
			break;
		case BEANSXML:
			CDIUtil.beansXML(packageName).finish();
			break;
		}		
		
	}

	public void createAndCheckCDIProject(SWTBotExt bot, SWTUtilExt util,
			ProjectExplorer projectExplorer, String projectName) {
		createCDIProject(util, projectName);
		projectExplorer.selectProject(projectName);
		SWTBotTree tree = projectExplorer.bot().tree();
		SWTBotTreeItem item = tree.getTreeItem(projectName);
		item.expand();
		addCDISupport(tree, item, bot, util);
	}

	public void createCDIProject(SWTUtilExt util, String projectName) {
		new NewFileWizardAction().run()
				.selectTemplate("Web", "Dynamic Web Project").next();
		new DynamicWebProjectWizard().setProjectName(projectName).finish();
		util.waitForNonIgnoredJobs();
	}

	public void addCDISupport(final SWTBotTree tree, SWTBotTreeItem item,
			SWTBotExt bot, SWTUtilExt util) {
		CDIUtil.nodeContextMenu(tree, item, "Configure",
				"Add CDI (Context and Dependency Injection) support...")
				.click();
		bot.activeShell().bot().button("OK").click();
		bot.sleep(Timing.time2S());
		util.waitForNonIgnoredJobs();
	}
	
	public void openOn(String openOnString, String titleName, boolean moreOptions) {
		SWTBotEditor ed = bot.editorByTitle(titleName);
		ed.show();
		ed.setFocus();		
		int offset = openOnString.contains("@")?1:0;		
		setEd(SWTJBTExt.selectTextInSourcePane(bot, titleName,
				openOnString, offset, openOnString.length() - offset));
		if (moreOptions) {			
			SWTBotMenu navigateMenu = bot.menu("Navigate");
			bot.sleep(TIME_500MS);
			navigateMenu.menu("Open Hyperlink").click();
			bot.sleep(TIME_500MS);
			SWTBotTable table = bot.activeShell().bot().table(0);
			for (int i = 0; i < table.rowCount(); i++) {
				if (table.getTableItem(i).getText().contains(openOnString)) {
					table.click(i, 0);					
					break;
				}
			}							
		} else {							
			getEd().setFocus();
			bot.sleep(TIME_500MS);
			//KeyboardHelper.pressKeyCodeUsingAWT(KeyEvent.VK_F3);			
		}		
		bot.sleep(Timing.time1S());
		setEd(bot.activeEditor().toTextEditor());		
	}

}