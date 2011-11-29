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
package org.jboss.tools.jsf.ui.marker;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.IMarkerResolutionGenerator2;
import org.eclipse.wst.validation.IMutableValidator;
import org.eclipse.wst.validation.MutableProjectSettings;
import org.eclipse.wst.validation.MutableWorkspaceSettings;
import org.eclipse.wst.validation.ValidationFramework;
import org.jboss.tools.jsf.ui.JsfUiPlugin;

/**
 * @author Daniel Azarov
 */
public class DisableFaceletHTMLValidatorResolutionGenerator implements
		IMarkerResolutionGenerator2 {
	private static final String ATTRIBUTE_NAME = "ValidationId";
	public static final String VALIDATOR_ID = "org.eclipse.jst.jsf.facelet.ui.FaceletHTMLValidator";

	public IMarkerResolution[] getResolutions(IMarker marker) {
		if(isNeedToCreate(marker)){
			boolean forProject = false; 
			IFile file = (IFile)marker.getResource();
			IMutableValidator[] validators;
			MutableProjectSettings projectSettings = ValidationFramework.getDefault().getProjectSettings(file.getProject());
			validators = projectSettings.getValidators();
			if(DisableFaceletHTMLValidatorMarkerResolution.findValidator(validators, VALIDATOR_ID) != null){
				forProject = true;
			}
			try {
				MutableWorkspaceSettings workspaceSettings = ValidationFramework.getDefault().getWorkspaceSettings();
				validators = workspaceSettings.getValidators();
				if(DisableFaceletHTMLValidatorMarkerResolution.findValidator(validators, VALIDATOR_ID) != null){
					return new IMarkerResolution[] {new DisableFaceletHTMLValidatorMarkerResolution((IFile)marker.getResource(), forProject)};
				}
			} catch (InvocationTargetException e) {
				JsfUiPlugin.getPluginLog().logError(e);
			}
			
		}
		return new IMarkerResolution[] {};
	}
	
	private boolean isNeedToCreate(IMarker marker){
		String attribute = marker.getAttribute(ATTRIBUTE_NAME, "");
		if(attribute.equals(VALIDATOR_ID) && marker.getResource() instanceof IFile){
			return true;
		}
		return false;
	}

	public boolean hasResolutions(IMarker marker) {
		return isNeedToCreate(marker);
	}
	
}
