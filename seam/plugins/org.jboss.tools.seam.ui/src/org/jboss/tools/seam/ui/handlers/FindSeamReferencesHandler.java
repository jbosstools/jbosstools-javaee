/*******************************************************************************
 * Copyright (c) 2008 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.seam.ui.handlers;

import org.jboss.tools.seam.ui.search.SeamSearchScope;


public class FindSeamReferencesHandler extends FindSeamHandler{

	/**
	 * Returns the search limitation to references 
	 */
	protected int getLimitTo() {
		return SeamSearchScope.SEARCH_FOR_REFERENCES;
	}
}
