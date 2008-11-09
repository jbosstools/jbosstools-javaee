package org.jboss.tools.seam.ui.marker;

import org.eclipse.core.resources.IMarker;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.IMarkerResolutionGenerator2;

public class MarkerResolutionGenerator implements IMarkerResolutionGenerator2 {

	public boolean hasResolutions(IMarker marker) {
		return true;
	}

	public IMarkerResolution[] getResolutions(IMarker marker) {
		return new IMarkerResolution[] { new SeamRuntimeMarkerResolution() };
	}

}
