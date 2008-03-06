package org.jboss.tools.seam.ui.actions;

import org.jboss.tools.seam.ui.SeamUIMessages;
import org.jboss.tools.seam.ui.search.SeamSearchScope;

public class FindSeamDeclarationsAction extends FindSeamAction {

	public FindSeamDeclarationsAction() {
		setText(SeamUIMessages.FIND_DECLARATIONS_ACTION_ACTION_NAME);
		setDescription(SeamUIMessages.FIND_DECLARATIONS_ACTION_DESCRIPTION);
		setToolTipText(SeamUIMessages.FIND_DECLARATIONS_ACTION_TOOL_TIP);
	}

	protected int getLimitTo() {
		return SeamSearchScope.SEARCH_FOR_DECLARATIONS;
	}
}
