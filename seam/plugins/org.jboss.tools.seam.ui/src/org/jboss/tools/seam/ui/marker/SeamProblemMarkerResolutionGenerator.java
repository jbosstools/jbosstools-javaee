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
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.IMarkerResolutionGenerator2;
import org.jboss.tools.seam.internal.core.validation.SeamCoreValidator;
import org.jboss.tools.seam.ui.SeamGuiPlugin;

/**
 * @author Daniel Azarov
 */
public class SeamProblemMarkerResolutionGenerator  implements IMarkerResolutionGenerator2 {

	public IMarkerResolution[] getResolutions(IMarker marker) {
		try{
			IMarkerResolution resolution = isOurCase(marker); 
			if(resolution != null){
				return new IMarkerResolution[] {resolution};
			}
		}catch(CoreException ex){
			SeamGuiPlugin.getPluginLog().logError(ex);
		}
		return new IMarkerResolution[]{};
	}
	
	private IMarkerResolution isOurCase(IMarker marker) throws CoreException{
		Integer attribute = ((Integer)marker.getAttribute(SeamCoreValidator.MESSAGE_ID_ATTRIBUTE_NAME));
		if(attribute == null)
			return null;
		
		int messageId = attribute.intValue();
		
		IFile file = (IFile)marker.getResource();
		
		attribute =  ((Integer)marker.getAttribute(IMarker.CHAR_START));
		if(attribute == null)
			return null;
		int start = attribute.intValue();
		
		attribute = ((Integer)marker.getAttribute(IMarker.CHAR_END));
		if(attribute == null)
			return null;
		int end = attribute.intValue();
		
		if(messageId == SeamCoreValidator.NONUNIQUE_COMPONENT_NAME_MESSAGE_ID)
			return new DeleteNameAnnotaionMarkerResolution(file, start, end);
		
		return null;
	}
	

	public boolean hasResolutions(IMarker marker) {
		try{
			if(isOurCase(marker) != null)
				return true;
		}catch(CoreException ex){
			SeamGuiPlugin.getPluginLog().logError(ex);
		}
		return false;
	}

}
