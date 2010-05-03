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
import org.jboss.tools.jsf.web.validation.jsf2.JSF2XMLValidator;
import org.jboss.tools.jsf.web.validation.jsf2.components.IJSF2ValidationComponent;

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
					|| !JSF2XMLValidator.JSF2_PROBLEM_ID.equals(markerType)) {
				return new IMarkerResolution[0];
			}
			String fixType = (String) marker
					.getAttribute(IJSF2ValidationComponent.JSF2_TYPE_KEY);
			if (fixType != null) {
				if (fixType
						.equals(IJSF2ValidationComponent.JSF2_COMPOSITE_COMPONENT_TYPE)) {
					return new IMarkerResolution[] { new CreateJSF2CompositeComponent() };
				}
				if (fixType
						.equals(IJSF2ValidationComponent.JSF2_FIXABLE_ATTR_TYPE)) {
					return new IMarkerResolution[] { new CreateJSF2CompositeAttrs() };
				}
				if (fixType.equals(IJSF2ValidationComponent.JSF2_URI_TYPE)) {
					return new IMarkerResolution[] { new CreateJSF2ResourcesFolder() };
				}
			}
		} catch (CoreException e) {
			JSFModelPlugin.getPluginLog().logError(e);
		}
		return new IMarkerResolution[0];
	}

}
