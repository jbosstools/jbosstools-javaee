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

package org.jboss.tools.cdi.seam3.bot.test.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import org.eclipse.core.runtime.Platform;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotShell;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.jboss.tools.cdi.bot.test.CDIBase;
import org.jboss.tools.cdi.bot.test.CDIConstants;
import org.jboss.tools.cdi.seam3.bot.test.Activator;
import org.jboss.tools.ui.bot.ext.SWTUtilExt;
import org.jboss.tools.ui.bot.ext.helper.ContextMenuHelper;
import org.jboss.tools.ui.bot.ext.types.IDELabel;

public class LibraryHelper extends CDIBase{

	/**
	 * Method adds library named "libraryName" located in project folder
	 * to project's classpath
	 * @param projectName
	 * @param libraryName
	 */
	public void addLibraryToProjectsClassPath(String projectName, String libraryName) {
		SWTBotTree tree = projectExplorer.bot().tree();
			
		ContextMenuHelper.prepareTreeItemForContextMenu(tree);
	    new SWTBotMenu(ContextMenuHelper.getContextMenu(tree,CDIConstants.REFRESH,false)).click();
		
		ContextMenuHelper.prepareTreeItemForContextMenu(tree);
	    new SWTBotMenu(ContextMenuHelper.getContextMenu(tree,CDIConstants.PROPERTIES,false)).click();
	    
	    bot.tree().expandNode(CDIConstants.JAVA_BUILD_PATH).select();
	    bot.tabItem(CDIConstants.LIBRARIES).activate();
	    
	    bot.button(CDIConstants.ADD_JARS).click();
	    bot.waitForShell(IDELabel.Shell.JAR_SELECTION);
	    	    
	    String file = libraryName;
	    bot.tree().expandNode(projectName).expandNode(file).select();
	    
	    bot.button(IDELabel.Button.OK).click();
	    
	    bot.button(IDELabel.Button.OK).click();
	    util.waitForNonIgnoredJobs();
	}

	/**
	 * Method copies library named "libraryName" located in "projectName"/resources/libraries 
	 * into project default folder
	 * @param projectName
	 * @param libraryName
	 * @throws IOException
	 */
	public void addLibraryIntoProject(String projectName, String libraryName) throws IOException {
		File in = null;
		FileChannel inChannel = null;
		FileChannel outChannel = null;
		
		
		in = SWTUtilExt.getResourceFile(Activator.PLUGIN_ID, "libraries", libraryName);
		
		File out = new File(Platform.getLocation() + File.separator + projectName + 
				File.separator + File.separator + libraryName);
			
		inChannel = new FileInputStream(in).getChannel();
		outChannel = new FileOutputStream(out).getChannel();

		inChannel.transferTo(0, inChannel.size(),	outChannel);
						
		if (inChannel != null) inChannel.close();
		if (outChannel != null) outChannel.close();		 	    	   
	}
	
	/**
	 * Method checks if library named "libraryName" is set on classpath 
	 * of project named "projectName"
	 * @param projectName
	 * @param libraryName
	 * @return boolean - represents fact if library is/isn't set on project classpath
	 */
	public boolean isLibraryInProjectClassPath(String projectName, String libraryName) {
		SWTBotTree tree = projectExplorer.bot().tree();
					
		ContextMenuHelper.prepareTreeItemForContextMenu(tree);
	    new SWTBotMenu(ContextMenuHelper.getContextMenu(tree, CDIConstants.PROPERTIES, false)).click();
	    
	    SWTBotShell shell = bot.shell("Properties for " + projectName);
	    SWTBot bot = shell.bot();
	    	   
	    bot.tree().expandNode(CDIConstants.JAVA_BUILD_PATH).select();
	   
	    bot.tabItem(CDIConstants.LIBRARIES).activate();
	    	
	    boolean libraryInProject = false;
	    for (int i = 0; i < bot.tree(1).rowCount(); i++) {
	    	if (bot.tree(1).getAllItems()[i].getText().contains(libraryName)) {
	    		libraryInProject = true;
	    		break;
	    	}
	    }
	    bot.button(IDELabel.Button.CANCEL).click();	    	    
	    return libraryInProject;
	}
	
}
