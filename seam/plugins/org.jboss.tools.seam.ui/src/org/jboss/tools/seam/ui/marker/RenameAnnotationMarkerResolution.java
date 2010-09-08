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

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;

/**
 * @author Daniel Azarov
 */
public class RenameAnnotationMarkerResolution extends
		AbstractSeamMarkerResolution {

	public RenameAnnotationMarkerResolution(String label, String qualifiedName,
			IFile file, int start, int end) {
		super(label, qualifiedName, file, start, end);
	}
	
	public void run(IMarker marker) {
		renameAnnotation("@"+getShortName()); //$NON-NLS-1$
	}
}
