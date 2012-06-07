/*******************************************************************************
 * Copyright (c) 2010 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.cdi.bot.test.uiutils;

import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.jboss.tools.cdi.bot.test.CDIConstants;
import org.jboss.tools.cdi.bot.test.CDITestBase;
import org.jboss.tools.cdi.bot.test.annotations.ProblemsType;
import org.jboss.tools.cdi.bot.test.uiutils.wizards.OpenOnOptionsDialog;
import org.jboss.tools.ui.bot.ext.SWTEclipseExt;
import org.jboss.tools.ui.bot.ext.SWTJBTExt;
import org.jboss.tools.ui.bot.ext.Timing;
import org.jboss.tools.ui.bot.ext.helper.ContextMenuHelper;
import org.jboss.tools.ui.bot.ext.types.IDELabel;
import org.jboss.tools.ui.bot.ext.types.ViewType;
import org.jboss.tools.ui.bot.ext.view.ProblemsView;

public class QuickFixHelper extends CDITestBase {
	
	/**
	 * Method select openOnString and then open proposal dialog which
	 * is returned as object
	 * @param openOnString
	 * @param titleName
	 * @return
	 */
	public OpenOnOptionsDialog openOnDialog(String openOnString, String titleName) {
		setEd(SWTJBTExt.selectTextInSourcePane(bot, titleName,
				openOnString, 0, openOnString.length()));
		bot.menu(IDELabel.Menu.EDIT).menu(IDELabel.Menu.QUICK_FIX).click();	
		bot.sleep(Timing.time1S());
		
		return new OpenOnOptionsDialog(bot);
	}
	
	/**
	 * Method open context menu for given tree item and opens Quick Fix option
	 * @param item
	 */
	public void openQuickFix(SWTBotTreeItem item) {
		SWTBotTree problemsTree = bot.viewByTitle(
				ViewType.PROBLEMS.getViewLabel()).bot().tree();
		ContextMenuHelper.prepareTreeItemForContextMenu(
				problemsTree, item);
		new SWTBotMenu(ContextMenuHelper.getContextMenu(problemsTree, 
				CDIConstants.QUICK_FIX, false)).click();
	}
	
	/**
	 * Methods gets all problems of given type
	 * @param problemType
	 * @return array of problems of given type
	 */
	public SWTBotTreeItem[] getProblems(ProblemsType problemType, String projectName) {
		SWTEclipseExt.showView(bot,ViewType.PROBLEMS);
		SWTBotTreeItem[] problemsTree = null;
		if (problemType == ProblemsType.WARNINGS) {
			problemsTree = ProblemsView.getFilteredWarningsTreeItems(bot, null, "/"
					+ projectName, null, null);
		}else if (problemType == ProblemsType.ERRORS) {
			problemsTree = ProblemsView.getFilteredErrorsTreeItems(bot, null, "/"
					+ projectName, null, null);
		}
		return problemsTree;
	}
	
	/**
	 * Method gets allProblems in problemsView as array of SWTBotTreeItem
	 * @return
	 */
	public SWTBotTreeItem[] getAllProblems(String projectName) {
		
		SWTBotTreeItem[] warningProblemsTree = getProblems(ProblemsType.WARNINGS, projectName);
		
		SWTBotTreeItem[] errorProblemsTree = getProblems(ProblemsType.ERRORS, projectName);
		
		return joinTwoArrays(warningProblemsTree, errorProblemsTree);
	}
	
	/**
	 * Method joins two arrays and returns them as one joined array
	 * @param aArray
	 * @param bArray
	 * @return
	 */
	private SWTBotTreeItem[] joinTwoArrays(SWTBotTreeItem[] aArray, SWTBotTreeItem[] bArray) {
		
		SWTBotTreeItem[] bigArray = new SWTBotTreeItem[aArray.length + bArray.length];
		
		for (int i = 0; i < aArray.length; i++) {
			bigArray[i] = aArray[i];
		}
		
		for (int i = aArray.length; i < aArray.length + bArray.length; i++) {
			bigArray[i] = bArray[i-aArray.length];
		}
		
		return bigArray;
	}
}
