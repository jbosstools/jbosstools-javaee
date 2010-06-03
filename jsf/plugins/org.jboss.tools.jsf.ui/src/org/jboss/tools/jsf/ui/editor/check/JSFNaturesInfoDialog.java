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

import java.text.MessageFormat;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.ui.PlatformUI;
import org.jboss.tools.jsf.ui.editor.check.wizards.AddJSFCapabilitiesWizard;

/**
 * 
 * @author yzhishko
 * 
 */

public class JSFNaturesInfoDialog extends ProjectNaturesInfoDialog {

	private static final String fixButtonLabel = "Add JSF Capabilities..."; //$NON-NLS-1$

	public JSFNaturesInfoDialog(IProject project) {
		super(project, fixButtonLabel);
	}

	@Override
	protected void fixButtonPressed() {
		BusyIndicator.showWhile(PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getShell().getDisplay(),
				new Runnable() {
					public void run() {
						AddJSFCapabilitiesWizard.getInstance(project).run(
								null);
					}
				});
	}

	@Override
	protected String getMessageInfo() {
		String dialogMessage = MessageFormat.format("JBoss Tools Editor might not fully work in project \"{0}" + //$NON-NLS-1$
				"\" because it does not have JSF and code completion enabled completely.\n\n" //$NON-NLS-1$
				+ "Please use the Configure menu on the project or \"Add JSF Capabilities...\" fix button to enable JSF if " //$NON-NLS-1$
				+ "you want all features of the editor working.",project.getName()); //$NON-NLS-1$
		return dialogMessage;
	}

	@Override
	protected void skipButtonPressed() {
		try {
			project.setPersistentProperty(
					ProjectNaturesChecker.IS_JSF_NATURES_CHECK_NEED, Boolean
							.toString(!isRemember));
		} catch (CoreException e) {
		}
	}

}
