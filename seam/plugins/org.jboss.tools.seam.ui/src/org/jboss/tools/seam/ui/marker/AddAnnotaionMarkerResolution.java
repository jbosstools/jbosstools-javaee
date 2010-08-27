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
	private String label;
	private String qualifiedName;
	
	public AddAnnotaionMarkerResolution(String label, String qualifiedName, IFile file, int start, int end){
		super(file, start, end);
		this.label = label;
		this.qualifiedName = qualifiedName;
	}
	
	public String getLabel() {
		return label;
	}
	
	public void run(IMarker marker) {
		addAnnotation(qualifiedName,"@"+getShortName(qualifiedName)); //$NON-NLS-1$
	}

}
