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
import java.util.Scanner;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotEclipseEditor;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.jboss.tools.cdi.bot.test.CDIBase;
import org.jboss.tools.cdi.bot.test.editor.BeansEditorTest;
import org.jboss.tools.ui.bot.ext.Timing;
import org.jboss.tools.ui.bot.ext.helper.TreeHelper;

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
		SWTBotTreeItem item = projectExplorer.selectTreeItem(file, sourceFolder.split("/"));
		
		NodeContextUtil.nodeContextMenu(tree, item, "Move...").click();
		
		bot.sleep(Timing.time2S());
		tree = bot.tree();	
		tree.collapseNode(destFolder.split("/")[0]);	
		
		TreeHelper.expandNode(bot, destFolder.split("/")).select();		
		
		bot.button("OK").click();		
	}
	
	/**
	 * Method removes the object which is located in "sourceFolder" 
	 * is deleted 
	 * @param object
	 * @param sourceFolder
	 */
	public void removeObjectInProjectExplorer(String object, String sourceFolder) {
		SWTBotTree tree = projectExplorer.bot().tree();
		SWTBotTreeItem item = projectExplorer.selectTreeItem(object, sourceFolder.split("/"));
		
		NodeContextUtil.nodeContextMenu(tree, item, "Delete").click();
		
		assertTrue(bot.button("OK").isEnabled());
		
		bot.button("OK").click();
		
		bot.sleep(Timing.time2S());
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
