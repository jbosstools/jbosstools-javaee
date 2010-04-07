/*******************************************************************************
 * Copyright (c) 2007-2010 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.jboss.tools.jsf.ui.editor.check.wizards;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;
import org.eclipse.ui.statushandlers.StatusAdapter;

/**
 * 
 * @author yzhishko
 *
 */

@SuppressWarnings("restriction")
public class MarkerUtils {

	static final IMarker[] EMPTY_MARKER_ARRAY = new IMarker[0];

	/**
	 * Return a StatusAdapter for the error
	 * 
	 * @param exception
	 * @return StatusAdapter
	 */
	static final StatusAdapter errorFor(Throwable exception) {
		IStatus status = new Status(IStatus.ERROR,
				IDEWorkbenchPlugin.IDE_WORKBENCH, IStatus.ERROR, exception
						.getLocalizedMessage(), exception);
		return new StatusAdapter(status);
	}
}
