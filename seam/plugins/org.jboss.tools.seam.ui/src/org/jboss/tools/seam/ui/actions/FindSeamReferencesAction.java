package org.jboss.tools.seam.ui.actions;

import org.jboss.tools.seam.ui.SeamUIMessages;
import org.jboss.tools.seam.ui.search.SeamSearchScope;

public class FindSeamReferencesAction extends FindSeamAction {

	public FindSeamReferencesAction() {
		setText(SeamUIMessages.FIND_REFERENCES_ACTION_ACTION_NAME);
		setDescription(SeamUIMessages.FIND_REFERENCES_ACTION_DESCRIPTION);
		setToolTipText(SeamUIMessages.FIND_REFERENCES_ACTION_TOOL_TIP);
	}

	protected int getLimitTo() {
		return SeamSearchScope.SEARCH_FOR_REFERENCES;
	}
}
