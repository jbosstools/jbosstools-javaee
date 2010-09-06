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
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.IMarkerResolutionGenerator2;
import org.jboss.tools.seam.internal.core.validation.SeamProjectPropertyValidator;
import org.jboss.tools.seam.ui.SeamGuiPlugin;

public class MarkerResolutionGenerator implements IMarkerResolutionGenerator2 {

	public boolean hasResolutions(IMarker marker) {
		return findResolutions(marker);
	}
	
	public boolean findResolutions(IMarker marker) {
		if (marker == null) {
			return false;
		}
		try{
			Integer attribute = ((Integer)marker.getAttribute(SeamProjectPropertyValidator.MESSAGE_ID_ATTRIBUTE_NAME));
			if(attribute == null)
				return false;
			
			int messageId = attribute.intValue();
			
			if(messageId == SeamProjectPropertyValidator.INVALID_SEAM_RUNTIME_ID)
				return true;
		}catch(CoreException ex){
			SeamGuiPlugin.getPluginLog().logError(ex);
		}
		
		return false;
	}

	public IMarkerResolution[] getResolutions(IMarker marker) {
		if(findResolutions(marker))
			return new IMarkerResolution[] { new SeamRuntimeMarkerResolution() };
		else
			return new IMarkerResolution[]{};
	}
}