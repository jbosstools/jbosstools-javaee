/*******************************************************************************
 * Copyright (c) 2007-2010 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.jboss.tools.jsf.ui.editor.check;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.internal.views.markers.MarkerSupportInternalUtilities;
import org.eclipse.ui.views.markers.internal.MarkerMessages;
import org.jboss.tools.jsf.ui.JsfUiPlugin;
import org.jboss.tools.jsf.ui.editor.check.wizards.QuickFixWizard;
import org.jboss.tools.jst.web.kb.internal.KbProject;

/**
 * 
 * @author yzhishko
 * 
 */

@SuppressWarnings("restriction")
public class KBNaturesInfoDialog extends ProjectNaturesInfoDialog {

	private static final String fixButtonLabel = "Enable JSF Code Completion..."; //$NON-NLS-1$

	public KBNaturesInfoDialog(IProject project) {
		super(project, fixButtonLabel);
	}

	@Override
	protected void fixButtonPressed() {
		try {
			IMarker kbProblemMarker = null;
			IMarker[] markers = project.findMarkers(null, false, 1);
			for (int i = 0; i < markers.length; i++) {
				IMarker marker = markers[i];
				String _type = marker.getType();
				if (_type != null
						&& _type.equals(KbProject.KB_PROBLEM_MARKER_TYPE)) {
					kbProblemMarker = marker;
					break;
				}
			}
			if (kbProblemMarker != null) {
				Map<IMarkerResolution, List<IMarker>> resolutions = new LinkedHashMap<IMarkerResolution, List<IMarker>>(
						0);
				List<IMarker> markerList = new ArrayList<IMarker>(0);
				IMarkerResolution[] markerResolutions = IDE
						.getMarkerHelpRegistry()
						.getResolutions(kbProblemMarker);
				markerList.add(kbProblemMarker);
				resolutions.put(markerResolutions[0], markerList);
				String markerDescription = kbProblemMarker.getAttribute(
						IMarker.MESSAGE,
						MarkerSupportInternalUtilities.EMPTY_STRING);
				String description = NLS.bind(
						MarkerMessages.MarkerResolutionDialog_Description,
						markerDescription);
				QuickFixWizard fixWizard = new QuickFixWizard(description,
						resolutions);
				fixWizard
						.setWindowTitle(MarkerMessages.resolveMarkerAction_dialogTitle);
				WizardDialog dialog = new QuickFixWizardDialog(PlatformUI
						.getWorkbench().getActiveWorkbenchWindow().getShell(),
						fixWizard);
				dialog.open();
			}
		} catch (CoreException e) {
			JsfUiPlugin.getDefault().logError(e);
		}

	}

	@Override
	protected String getMessageInfo() {
		String dialogMessage = "The project \"" + project.getName() + //$NON-NLS-1$
				"\" does not have JSF code completion and validation enabled completely.\n\n" //$NON-NLS-1$
				+ "Please use \"Enabale JSF Code Completion...\" fix button if " //$NON-NLS-1$
				+ "you want these features working."; //$NON-NLS-1$
		return dialogMessage;
	}

	private class QuickFixWizardDialog extends WizardDialog {

		/**
		 * @param parentShell
		 * @param newWizard
		 */
		public QuickFixWizardDialog(Shell parentShell, IWizard newWizard) {
			super(parentShell, newWizard);
			setShellStyle(SWT.CLOSE | SWT.MAX | SWT.TITLE | SWT.BORDER
					| SWT.MODELESS | SWT.RESIZE | getDefaultOrientation());
		}

	}

	@Override
	protected void skipButtonPressed() {
		try {
			project.setPersistentProperty(
					ProjectNaturesChecker.IS_KB_NATURES_CHECK_NEED, Boolean
							.toString(!isRemember));
		} catch (CoreException e) {
		}
	}

}
