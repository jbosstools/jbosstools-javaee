 /*******************************************************************************
  * Copyright (c) 2007-2010 Red Hat, Inc.
  * Distributed under license by Red Hat, Inc. All rights reserved.
  * This program is made available under the terms of the
  * Eclipse Public License v1.0 which accompanies this distribution,
  * and is available at http://www.eclipse.org/legal/epl-v10.html
  *
  * Contributors:
  *     Red Hat, Inc. - initial API and implementation
  ******************************************************************************/

package org.jboss.tools.jsf.web.validation.jsf2.action;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.IMarkerResolutionGenerator;
import org.jboss.tools.jsf.JSFModelPlugin;
import org.jboss.tools.jsf.web.validation.jsf2.JSF2Validator;
import org.jboss.tools.jsf.web.validation.jsf2.util.JSF2ResourceUtil;

/**
 * 
 * @author yzhishko
 *
 */

public class JSF2ComponentResolutionGenerator implements
		IMarkerResolutionGenerator {

	public IMarkerResolution[] getResolutions(IMarker marker) {
		try {
			String markerType = marker.getType();
			if (markerType == null
					|| !JSF2Validator.JSF2_PROBLEM_ID.equals(markerType)) {
				return new IMarkerResolution[0];
			}
			if (marker.getAttribute(JSF2ResourceUtil.COMPONENT_RESOURCE_PATH_KEY) == null) {
				return new IMarkerResolution[0];
			}
		} catch (CoreException e) {
			JSFModelPlugin.getPluginLog().logError(e);
		}		
		return new JSF2MarkerResolution[] { new JSF2MarkerResolution() };
	}

}
