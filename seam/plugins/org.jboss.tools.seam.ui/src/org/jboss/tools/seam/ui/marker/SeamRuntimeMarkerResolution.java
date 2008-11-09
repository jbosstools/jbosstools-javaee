package org.jboss.tools.seam.ui.marker;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IMarkerResolution2;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.dialogs.PropertyDialog;
import org.jboss.tools.seam.ui.preferences.SeamSettingsPreferencePage;

public class SeamRuntimeMarkerResolution implements IMarkerResolution2 {

	public String getDescription() {
		return "Set Seam properties";
	}

	public Image getImage() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getLabel() {
		return "Set Seam properties";
	}

	public void run(IMarker marker) {
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		IResource resource = marker.getResource();
		PropertyDialog dialog = PropertyDialog.createDialogOn(shell, SeamSettingsPreferencePage.ID, resource);
		
		if (dialog != null) {
			dialog.open();
		}

	}

}
