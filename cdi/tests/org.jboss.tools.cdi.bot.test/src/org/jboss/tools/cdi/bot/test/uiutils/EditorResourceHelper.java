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

package org.jboss.tools.cdi.bot.test.uiutils;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Scanner;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEclipseEditor;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.jboss.tools.cdi.bot.test.CDIBase;
import org.jboss.tools.cdi.bot.test.editor.BeansEditorTest;
import org.jboss.tools.ui.bot.ext.Timing;
import org.jboss.tools.ui.bot.ext.helper.TreeHelper;
import org.jboss.tools.ui.bot.ext.types.IDELabel;

public class EditorResourceHelper extends CDIBase {
	
	/**
	 * method replaces whole content of class "classEdit" by inputstream resource
	 * If closeEdit param is true, editor is not only saved but closed as well 
	 * Prerequisite: editor has been set 
	 * @param classEdit
	 * @param resource
	 * @param closeEdit
	 */
	public void replaceClassContentByResource(InputStream resource, boolean closeEdit) {
		SWTBotEclipseEditor st = getEd().toTextEditor();
		st.selectRange(0, 0, st.getText().length());
		String code = readStream(resource);
		st.setText(code);
		getEd().save();
		if (closeEdit) {
			getEd().close();
		}
	}
	
	/**
	 * Method copies resource to class opened in SWTBotEditor with entered parameters
	 * @param classEdit
	 * @param resource
	 * @param closeEdit
	 * @param param
	 */
	public void replaceClassContentByResource(InputStream resource, boolean closeEdit, Object... param) {
		SWTBotEclipseEditor classEdit = getEd().toTextEditor();
		String s = readStream(resource);
		String code = MessageFormat.format(s, param);
		classEdit.toTextEditor().selectRange(0, 0, classEdit.toTextEditor().getText().length());
		classEdit.toTextEditor().setText(code);
		classEdit.save();
		if (closeEdit) classEdit.close();
	}
	
	/**
	 * method copies resource from folder "src" param to folder "target" param
	 * @param src
	 * @param target
	 */
	public void copyResource(String src, String target) {
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProjects()[0];
		IFile f = project.getFile(target);
		if (f.exists()) {			
			try {
				f.delete(true, new NullProgressMonitor());
			} catch (CoreException ce) {				
			}
		}
		InputStream is = null;
		try {
			is = BeansEditorTest.class.getResourceAsStream(src);
			f.create(is, true, new NullProgressMonitor());
		} catch (CoreException ce) {			
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException ioe) {
					//ignore
				}
			}
		}
	}

	/**
	 * Method replaces string "target" by string "replacement.
	 * Prerequisite: editor has been set
	 * @param target
	 * @param replacement
	 */
	public void replaceInEditor(String target, String replacement) {
		getEd().selectRange(0, 0, getEd().getText().length());
		getEd().setText(getEd().getText().replace(
				target + (replacement.equals("") ? System
								.getProperty("line.separator") : ""),
				replacement));		
		getEd().save();
	}

	/**
	 * Method inserts the string "insertText" on location ("line", "column")
	 * Prerequisite: editor has been set
	 * @param line
	 * @param column
	 * @param insertText
	 */
	public void insertInEditor(int line, int column, String insertText) {
		getEd().toTextEditor().insertText(line, column, insertText);
		bot.sleep(Timing.time1S());
		getEd().save();
	}
	
	/**
	 * in Project Explorer View, the file which is located in "sourceFolder" 
	 * is moved to location "destFolder" 
	 * @param file
	 * @param sourceFolder
	 * @param destFolder
	 */
	public void moveFileInProjectExplorer(String file, String sourceFolder, String destFolder) {
		SWTBotTree tree = projectExplorer.bot().tree();

		bot.menu(IDELabel.Menu.FILE).menu(IDELabel.Menu.MOVE).click();
		bot.waitForShell(IDELabel.Shell.MOVE_RESOURCES);
		
		tree = bot.tree();	
		tree.collapseNode(destFolder.split("/")[0]);	
		
		TreeHelper.expandNode(bot, destFolder.split("/")).select();		
		
		bot.button(IDELabel.Button.OK).click();		
	}
	
	/**
	 * Method deletes whole package with given name for entered project
	 * @param projectName
	 * @param packageName
	 */
	public void deletePackage(String projectName, String packageName) {
		if (projectExplorer.isFilePresent(projectName, "Java Resources", "JavaSource")) {	
			String[] path = {projectName, "Java Resources", "JavaSource"};
			deleteFolderInProjectExplorer(packageName, path);
		}else {
			String[] path = {projectName, "Java Resources", "src"};
			deleteFolderInProjectExplorer(packageName, path);
		}		
	}
	
	/**
	 * Method deletes whole web folder with given name for entered project
	 * @param projectName
	 * @param packageName
	 */
	public void deleteWebFolder(String projectName, String folder) {
		
		String[] path = {projectName, "WebContent"};
		deleteFolderInProjectExplorer(folder, path);
		
	}
	
	/**
	 * Method deletes folder with given name and path
	 * @param folderName
	 * @param path
	 */
	public void deleteFolderInProjectExplorer(String folderName, String... path) {
				
		projectExplorer.selectTreeItem(folderName, path); 				
		
		bot.menu(IDELabel.Menu.EDIT).menu(IDELabel.Menu.DELETE).click();
		bot.waitForShell(IDELabel.Shell.CONFIRM_DELETE);
		bot.shell(IDELabel.Shell.CONFIRM_DELETE).bot().button(IDELabel.Button.OK).click();
		util.waitForNonIgnoredJobs();
	}

	/**
	 * Methods converts input stream to string component
	 * @param inputStream
	 * @return String - input stream converted to string
	 */
	public String readStream(InputStream inputStream) {
		// we don't care about performance in tests too much, so this should be
		// OK
		return new Scanner(inputStream).useDelimiter("\\A").next();
	}

}
