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
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.IMarkerResolution2;
import org.eclipse.wst.validation.IMutableValidator;
import org.eclipse.wst.validation.MutableProjectSettings;
import org.eclipse.wst.validation.MutableWorkspaceSettings;
import org.eclipse.wst.validation.ValidationFramework;
import org.jboss.tools.jsf.ui.JsfUIMessages;
import org.jboss.tools.jsf.ui.JsfUiPlugin;

/**
 * @author Daniel Azarov
 */
public class DisableFaceletHTMLValidatorMarkerResolution implements
		IMarkerResolution2 {
	private static final String MARKER_TYPE = "org.eclipse.jst.jsf.facelet.ui.FaceletValidationMarker";
	private IFile file;
	private boolean global;
	private String label;
	
	public DisableFaceletHTMLValidatorMarkerResolution(IFile file, boolean global){
		this.file = file;
		this.global = global;
		if(global){
			label = JsfUIMessages.DISABLE_FACELET_HTML_VALIDATOR_MARKER_RESOLUTION_FOR_WORKSPACE;
		}else{
			label = NLS.bind(JsfUIMessages.DISABLE_FACELET_HTML_VALIDATOR_MARKER_RESOLUTION_FOR_PROJECT, file.getProject().getName());
		}
	}

	public String getLabel() {
		return label;
	}

	public void run(IMarker marker) {
		IMutableValidator[] validators;
		if(global){
			try {
				MutableWorkspaceSettings workspaceSettings = ValidationFramework.getDefault().getWorkspaceSettings();
				validators = workspaceSettings.getValidators();
				if(disableValidator(validators, DisableFaceletHTMLValidatorResolutionGenerator.VALIDATOR_ID)){
					ValidationFramework.getDefault().applyChanges(workspaceSettings, true);
					try {
						file.getProject().getParent().deleteMarkers(MARKER_TYPE, true, IResource.DEPTH_INFINITE);
					} catch (CoreException e) {
						JsfUiPlugin.getPluginLog().logError(e);
					}
				}
			} catch (InvocationTargetException e) {
				JsfUiPlugin.getPluginLog().logError(e);
			}
		}else{
			MutableProjectSettings projectSettings = ValidationFramework.getDefault().getProjectSettings(file.getProject());
			validators = projectSettings.getValidators();
			if(disableValidator(validators, DisableFaceletHTMLValidatorResolutionGenerator.VALIDATOR_ID)){
				ValidationFramework.getDefault().applyChanges(projectSettings, true);
				try {
					file.getProject().deleteMarkers(MARKER_TYPE, true, IResource.DEPTH_INFINITE);
				} catch (CoreException e) {
					JsfUiPlugin.getPluginLog().logError(e);
				}
			}
		}
	}
	
	public static boolean disableValidator(IMutableValidator[] validators, String id){
		IMutableValidator validator = findValidator(validators, id);
		if(validator != null){
			validator.setBuildValidation(false);
			validator.setManualValidation(false);
			return true;
		}
		return false;
	}
	
	public static IMutableValidator findValidator(IMutableValidator[] validators, String id){
		for(IMutableValidator validator : validators){
			if(validator.getId().equals(id))
				return validator;
		}
		return null;
	}

	public String getDescription() {
		return label;
	}

	public Image getImage() {
		return null;
	}

}
