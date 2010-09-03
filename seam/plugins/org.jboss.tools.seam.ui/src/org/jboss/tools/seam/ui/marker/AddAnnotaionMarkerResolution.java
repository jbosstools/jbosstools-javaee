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
public class AddAnnotaionMarkerResolution extends
		AbstractSeamMarkerResolution {
	boolean insertName;
	
	public AddAnnotaionMarkerResolution(String label, String qualifiedName, IFile file, int start, int end, boolean insertName){
		super(label, qualifiedName, file, start, end);
		this.insertName = insertName;
	}
	
	public void run(IMarker marker) {
		addAnnotation(qualifiedName,"@"+getShortName(qualifiedName), insertName); //$NON-NLS-1$
	}

}
