/*******************************************************************************
 * Copyright (c) 2010 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.cdi.bot.test.uiutils.actions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import org.eclipse.core.runtime.Platform;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEclipseEditor;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEditor;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.keyboard.Keystrokes;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTable;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.jboss.tools.cdi.bot.test.PluginActivator;
import org.jboss.tools.cdi.bot.test.uiutils.wizards.DynamicWebProjectWizard;
import org.jboss.tools.ui.bot.ext.SWTBotExt;
import org.jboss.tools.ui.bot.ext.SWTJBTExt;
import org.jboss.tools.ui.bot.ext.SWTTestExt;
import org.jboss.tools.ui.bot.ext.SWTUtilExt;
import org.jboss.tools.ui.bot.ext.Timing;
import org.jboss.tools.ui.bot.ext.helper.ContextMenuHelper;
import org.jboss.tools.ui.bot.ext.helper.TreeHelper;
import org.jboss.tools.ui.bot.ext.types.IDELabel;
import org.jboss.tools.ui.bot.ext.view.ProjectExplorer;
import org.junit.After;
import org.junit.Before;

public class CDIBase extends SWTTestExt {
	
	private String projectName = "CDIProject";
	private String packageName = "cdi";
	
	@Before
	public void checkAndCreateProject() {
		if (!projectExists(getProjectName())) {
			createAndCheckCDIProject(bot, util, projectExplorer, getProjectName());
		}
	}
	
	@After
	public void waitForJobs() {
		util.waitForNonIgnoredJobs();
	}
		
	public  String getProjectName() {
		return projectName;
	}
	
	public String getPackageName() {
		return packageName;
	}

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
		/*
		 * if beans.xml is created as first component in project,
		 * it is not opened as default ==> there is no active editor
		 */
		if (component != CDICOMPONENT.BEANSXML) {
			setEd(bot.activeEditor().toTextEditor());
		}		
	}

	private void createCDIComponent(CDICOMPONENT component, String name,
			String packageName, String necessaryParam) {
		switch (component) {
		case STEREOSCOPE:
			boolean alternative = false;
			boolean regInBeansXml = false;
			if (necessaryParam != null) {
				if (necessaryParam.equals("alternative+beansxml")) {
					alternative = true;
					regInBeansXml = true;
				} else if (necessaryParam.equals("alternative")) {
					alternative = true;
				}
			}
			CDIUtil.stereotype(packageName, name, null, null, false, false, alternative, regInBeansXml,
					false).finish();
			break;
		case QUALIFIER:
			CDIUtil.qualifier(packageName, name, false, false).finish();
			break;
		case SCOPE:
			CDIUtil.scope(packageName, name, false, false, true, false).finish();
			break;
		case BEAN:
			alternative = false;
			regInBeansXml = false;
			if (necessaryParam != null) {
				if (necessaryParam.equals("alternative+beansxml")) {
					alternative = true;
					regInBeansXml = true;
				} else if (necessaryParam.equals("alternative")) {
					alternative = true;
				}
			}
			CDIUtil.bean(packageName, name, true, false, false, false, alternative, regInBeansXml, null, null,
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
	
	public void createCDIProjectWithCDIPreset(SWTUtilExt util, String projectName) {
		new NewFileWizardAction().run()
				.selectTemplate("Web", "Dynamic Web Project").next();
		new DynamicWebProjectWizard().setProjectName(projectName).setCDIPreset().finish();
		util.waitForNonIgnoredJobs();		
	}
	
	public void createCDIProjectWithCDIFacets(SWTUtilExt util, String projectName) {
		new NewFileWizardAction().run()
				.selectTemplate("Web", "Dynamic Web Project").next();
		new DynamicWebProjectWizard().setProjectName(projectName).setCDIFacet().finish();
		bot.sleep(Timing.time5S());		
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
	
	public void openOn(String openOnString, String titleName, String chosenOption) {
		SWTBotEditor ed = bot.editorByTitle(titleName);
		ed.show();
		ed.setFocus();		
		int offset = openOnString.contains("@")?1:0;		
		setEd(SWTJBTExt.selectTextInSourcePane(bot, titleName,
				openOnString, offset, openOnString.length() - offset));
		if (chosenOption != null) {			
			SWTBotMenu navigateMenu = bot.menu("Navigate");
			bot.sleep(Timing.time500MS());
			navigateMenu.menu("Open Hyperlink").click();
			bot.sleep(Timing.time500MS());
			SWTBotTable table = bot.activeShell().bot().table(0);
			for (int i = 0; i < table.rowCount(); i++) {
				if (table.getTableItem(i).getText().contains(chosenOption)) {
					table.click(i, 0);					
					break;
				}
			}							
		} else {							
			getEd().setFocus();	
			bot.sleep(Timing.time2S());
			getEd().pressShortcut(Keystrokes.F3);			
		}		
		bot.sleep(Timing.time1S());
		setEd(bot.activeEditor().toTextEditor());		
	}
		
	public void addLibraryToProjectsClassPath(String projectName, String libraryName) {
		SWTBotTree tree = projectExplorer.bot().tree();
			
		ContextMenuHelper.prepareTreeItemForContextMenu(tree);
	    new SWTBotMenu(ContextMenuHelper.getContextMenu(tree,"Refresh",false)).click();
		
		ContextMenuHelper.prepareTreeItemForContextMenu(tree);
	    new SWTBotMenu(ContextMenuHelper.getContextMenu(tree,"Properties",false)).click();
	    
	    bot.tree().expandNode("Java Build Path").select();
	    bot.tabItem("Libraries").activate();
	    bot.button("Add JARs...").click();
	    bot.sleep(Timing.time500MS());
	    String file = libraryName;
	    bot.tree().expandNode(projectName).expandNode(file).select();
	    
	    bot.button(IDELabel.Button.OK).click();
	    bot.sleep(Timing.time1S());
	    bot.button(IDELabel.Button.OK).click();
	    bot.sleep(Timing.time1S());
	}

	/*
	 * copy library located in PROJECT_NAME/resources/libraries into project
	 * libraryName must include extension: seam-solder.jar
	 */
	public void addLibraryIntoProject(String projectName, String libraryName) throws IOException {
		File in = null;
		FileChannel inChannel = null;
		FileChannel outChannel = null;
		
		
		in = SWTUtilExt.getResourceFile(PluginActivator.PLUGIN_ID, "libraries", libraryName);
		
		File out = new File(Platform.getLocation() + File.separator + projectName + 
				File.separator + File.separator + libraryName);
			
		inChannel = new FileInputStream(in).getChannel();
		outChannel = new FileOutputStream(out).getChannel();

		inChannel.transferTo(0, inChannel.size(),	outChannel);
						
		if (inChannel != null) inChannel.close();
		if (outChannel != null) outChannel.close();		 	    	   
	}
	
	/*
	 * check if library with name libraryName is set on classpath of project with name
	 * projectName
	 */
	public void isLibraryInProjectClassPath(String projectName, String libraryName) {
		SWTBotTree tree = projectExplorer.bot().tree();
					
		ContextMenuHelper.prepareTreeItemForContextMenu(tree);
	    new SWTBotMenu(ContextMenuHelper.getContextMenu(tree,"Properties",false)).click();
	    
	    SWTBotShell shell = bot.shell("Properties for " + projectName);
	    SWTBot bot = shell.bot();
	    	   
	    bot.tree().expandNode("Java Build Path").select();
	   
	    bot.tabItem("Libraries").activate();
	    	
	    boolean libraryInProject = false;
	    for (int i = 0; i < bot.tree(1).rowCount(); i++) {
	    	if (bot.tree(1).getAllItems()[i].getText().contains(libraryName)) {
	    		libraryInProject = true;
	    		break;
	    	}
	    }	  	    	   
	    assertTrue("Library " + libraryName + "is not on classPath of project " 
	    			+ projectName,libraryInProject);
	    
	    bot.button(IDELabel.Button.CANCEL).click();	    
	    bot.sleep(Timing.time1S());
	}
	
	public void moveFileInProjectExplorer(String file, String sourceFolder, String destFolder) {
		SWTBotTree tree = projectExplorer.bot().tree();
		SWTBotTreeItem item = projectExplorer.selectTreeItem(file, sourceFolder.split("/"));
		
		CDIUtil.nodeContextMenu(tree, item, "Move...").click();
		
		assertFalse(bot.button("OK").isEnabled());
		
		tree = bot.tree();	
		tree.collapseNode(destFolder.split("/")[0]);	
		
		TreeHelper.expandNode(bot, destFolder.split("/")).select();		

		assertTrue(bot.button("OK").isEnabled());
		bot.button("OK").click();		
	}
	
	public void removeObjectInProjectExplorer(String object, String sourceFolder) {
		SWTBotTree tree = projectExplorer.bot().tree();
		SWTBotTreeItem item = projectExplorer.selectTreeItem(object, sourceFolder.split("/"));
		
		CDIUtil.nodeContextMenu(tree, item, "Delete").click();
		
		assertTrue(bot.button("OK").isEnabled());
		
		bot.button("OK").click();
		
		bot.sleep(Timing.time2S());
	}
	
	public boolean projectExists(String projectName) {
		SWTBotTree tree = projectExplorer.bot().tree();
		boolean projectExists = false;
		try {
			tree.getTreeItem(projectName);
			projectExists = true;
		}catch (WidgetNotFoundException exc) {
		}
		return projectExists;
	}

}