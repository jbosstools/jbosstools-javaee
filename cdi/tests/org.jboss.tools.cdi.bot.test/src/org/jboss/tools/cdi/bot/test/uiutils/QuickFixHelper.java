package org.jboss.tools.cdi.bot.test.uiutils;

import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.jboss.tools.cdi.bot.test.CDITestBase;
import org.jboss.tools.cdi.bot.test.annotations.ProblemsType;
import org.jboss.tools.cdi.bot.test.quickfix.validators.AbstractValidationProvider;
import org.jboss.tools.ui.bot.ext.view.ProblemsView;

public class QuickFixHelper extends CDITestBase{
	
	protected AbstractValidationProvider validationErrorsProvider;
	
	public static SWTBotTreeItem[] problemsTrees;	
	
	/**
	 * Method open context menu for given tree item and opens Quick Fix option
	 * @param item
	 */
	public void openQuickFix(SWTBotTreeItem item) {
		NodeContextUtil.nodeContextMenu(bot.tree(), item, "Quick Fix").click();
	}
	
	/**
	 * Methods gets all problems of given type
	 * @param problemType
	 * @return array of problems of given type
	 */
	public SWTBotTreeItem[] getProblems(ProblemsType problemType) {
		SWTBotTreeItem[] problemsTree = null;
		if (problemType == ProblemsType.WARNINGS) {
			problemsTree = ProblemsView.getFilteredWarningsTreeItems(bot, null, "/"
					+ getProjectName(), null, null);
		}else if (problemType == ProblemsType.ERRORS) {
			problemsTree = ProblemsView.getFilteredErrorsTreeItems(bot, null, "/"
					+ getProjectName(), null, null);
		}
		return problemsTree;
	}
	
	public SWTBotTreeItem[] getAllProblems() {
		
		SWTBotTreeItem[] warningProblemsTree = getProblems(ProblemsType.WARNINGS);
		
		SWTBotTreeItem[] errorProblemsTree = getProblems(ProblemsType.ERRORS);
		
		return joinTwoArrays(warningProblemsTree, errorProblemsTree);
	}
	
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
