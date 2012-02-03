/*******************************************************************************
 * Copyright (c) 2007-2012 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.jboss.tools.seam.ui.actions;

import org.jboss.tools.seam.ui.SeamUIMessages;
import org.jboss.tools.seam.ui.search.SeamSearchScope;

/**
 * Find Seam References action
 * 
 * @author Jeremy
 */
public class FindSeamReferencesAction extends FindSeamAction {

	/**
	 * Constructs the FindSeamReferencesAction
	 */
	public FindSeamReferencesAction() {
		setText(SeamUIMessages.FIND_REFERENCES_ACTION_ACTION_NAME);
		setDescription(SeamUIMessages.FIND_REFERENCES_ACTION_DESCRIPTION);
		setToolTipText(SeamUIMessages.FIND_REFERENCES_ACTION_TOOL_TIP);
	}

	/**
	 * Returns the search limitation to references 
	 */
	protected int getLimitTo() {
		return SeamSearchScope.SEARCH_FOR_REFERENCES;
	}
}
