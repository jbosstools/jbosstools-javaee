/*******************************************************************************
 * Copyright (c) 2007 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.jboss.tools.seam.ui.search;

import org.eclipse.jdt.ui.search.IMatchPresentation;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.search.internal.ui.text.FileLabelProvider;
import org.eclipse.search.ui.text.Match;
import org.eclipse.ui.PartInitException;

/**
 * Seam match presentation
 *  
 * @author Jeremy
 */
public class SeamMatchPresentation implements IMatchPresentation {

	/**
	 * @see org.eclipse.jdt.ui.search.IMatchPresentation#createLabelProvider()
	 */
	public ILabelProvider createLabelProvider() {
		return new SeamSearchViewLabelProvider(null, FileLabelProvider.SHOW_LABEL_PATH);
	}

	/** (non-Javadoc)
	 * @see org.eclipse.jdt.ui.search.IMatchPresentation#showMatch(org.eclipse.search.ui.text.Match, int, int, boolean)
	 */
	public void showMatch(Match match, int currentOffset, int currentLength, boolean activate) throws PartInitException {
	}
}
