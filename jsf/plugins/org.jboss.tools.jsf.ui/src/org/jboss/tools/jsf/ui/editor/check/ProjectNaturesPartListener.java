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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;
import org.jboss.tools.common.reporting.ProblemReportingHelper;
import org.jboss.tools.jst.web.ui.WebUiPlugin;
import org.jboss.tools.jst.web.ui.internal.editor.jspeditor.JSPMultiPageEditor;
import org.jboss.tools.jst.web.ui.internal.editor.preferences.IVpePreferencesPage;

/**
 * 
 * @author yzhishko
 *
 */

public class ProjectNaturesPartListener implements IPartListener {

	public void partActivated(IWorkbenchPart part) {

	}

	public void partBroughtToTop(IWorkbenchPart part) {

	}

	public void partClosed(IWorkbenchPart part) {

	}

	public void partDeactivated(IWorkbenchPart part) {

	}

	public void partOpened(IWorkbenchPart part) {
		boolean isCheck = true;
		String isCheckString = System
				.getProperty("org.jboss.tools.vpe.ENABLE_PROJECT_NATURES_CHECKER"); //$NON-NLS-1$
		if (isCheckString != null) {
			isCheck = Boolean.parseBoolean(isCheckString);
		}
		if (isCheck) {
			if (WebUiPlugin
					.getDefault()
					.getPreferenceStore()
					.getBoolean(
							IVpePreferencesPage.INFORM_WHEN_PROJECT_MIGHT_NOT_BE_CONFIGURED_PROPERLY_FOR_VPE)) {
				try {
					checkNaturesFromPart(part);
				} catch (CoreException e) {
					ProblemReportingHelper.reportProblem(
							WebUiPlugin.PLUGIN_ID, e);
				}
			}
		}
	}

	private void checkNaturesFromPart(IWorkbenchPart part) throws CoreException {
		if (part instanceof JSPMultiPageEditor) {
			IEditorInput editorInput = ((JSPMultiPageEditor)part).getEditorInput();
			if (editorInput instanceof IFileEditorInput) {
				ProjectNaturesChecker.getInstance()
						.checkNatures(
								((IFileEditorInput) editorInput).getFile()
										.getProject());
			}
		}
	}
	
}
