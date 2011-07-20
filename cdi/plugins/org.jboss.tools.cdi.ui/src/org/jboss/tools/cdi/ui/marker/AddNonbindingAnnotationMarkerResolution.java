/*******************************************************************************
 * Copyright (c) 2011 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.cdi.ui.marker;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IMember;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IMarkerResolution2;
import org.jboss.tools.cdi.core.CDIConstants;
import org.jboss.tools.cdi.ui.CDIUIMessages;
import org.jboss.tools.cdi.ui.CDIUIPlugin;

public class AddNonbindingAnnotationMarkerResolution implements
		IMarkerResolution2 {
	private IMember member;
	private String label;
	
	public AddNonbindingAnnotationMarkerResolution(IMember member){
		this.member = member;
		label = NLS.bind(CDIUIMessages.ADD_NONBINDING_MARKER_RESOLUTION_TITLE, member.getElementName());
	}

	public String getLabel() {
		return label;
	}

	public void run(IMarker marker) {
		try{
			ICompilationUnit original = member.getCompilationUnit();
			ICompilationUnit compilationUnit = original.getWorkingCopy(new NullProgressMonitor());
			
			MarkerResolutionUtils.addAnnotation(CDIConstants.NON_BINDING_ANNOTATION_TYPE_NAME, compilationUnit, member);
			
			compilationUnit.commitWorkingCopy(false, new NullProgressMonitor());
			compilationUnit.discardWorkingCopy();
		}catch(CoreException ex){
			CDIUIPlugin.getDefault().logError(ex);
		}
	}
	
	public String getDescription() {
		return label;
	}

	public Image getImage() {
		return null;
	}
}
