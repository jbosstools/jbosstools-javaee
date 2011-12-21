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
package org.jboss.tools.seam.ui.marker;

import java.text.MessageFormat;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;

/**
 * @author Daniel Azarov
 */
public class ChangeScopeMarkerResolution extends
		AbstractSeamMarkerResolution {
	private String scopeName;

	public ChangeScopeMarkerResolution(String label, String scopeName,
			IFile file, int start, int end) {
		super(label, "org.jboss.seam.annotations.Scope", file, start, end); //$NON-NLS-1$
		this.label = MessageFormat.format(label, new Object[]{scopeName});
		this.scopeName = scopeName;
	}
	
	public void run(IMarker marker) {
		renameAnnotation("("+scopeName+")", "org.jboss.seam.ScopeType", false); //$NON-NLS-1$
	}
}
