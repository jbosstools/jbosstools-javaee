/******************************************************************************* 
 * Copyright (c) 2009 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.seam.ui.marker;

import org.eclipse.core.resources.IMarker;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.IMarkerResolutionGenerator2;
import org.jboss.tools.seam.internal.core.validation.SeamValidationMessages;

public class MarkerResolutionGenerator implements IMarkerResolutionGenerator2 {

	/**  Copied from org.eclipse.wst.validation.internal.TaskListUtility */
	private static final String VALIDATION_MARKER_MESSAGEID = "messageId"; //$NON-NLS-1$

	public boolean hasResolutions(IMarker marker) {
		if (marker == null) {
			return false;
		}
		String runtime = marker.getAttribute(VALIDATION_MARKER_MESSAGEID, "");
		if (SeamValidationMessages.INVALID_SEAM_RUNTIME.equals(runtime)) {
			return true;
		}
		return false;
	}

	public IMarkerResolution[] getResolutions(IMarker marker) {
		return new IMarkerResolution[] { new SeamRuntimeMarkerResolution() };
	}
}