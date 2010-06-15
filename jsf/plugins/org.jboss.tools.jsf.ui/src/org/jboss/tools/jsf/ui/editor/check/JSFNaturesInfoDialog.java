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
import org.jboss.tools.jsf.ui.JsfUIMessages;
import org.jboss.tools.jsf.ui.editor.check.wizards.AddJSFCapabilitiesWizard;

/**
 * 
 * @author yzhishko
 * 
 */

public class JSFNaturesInfoDialog extends ProjectNaturesInfoDialog {

	public JSFNaturesInfoDialog(IProject project) {
		super(project, JsfUIMessages.ADD_JSF_CAPABILITIES_BUTTTON_LABEL);
	}

	@Override
	protected void fixButtonPressed() {
		BusyIndicator.showWhile(PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getShell().getDisplay(),
				new Runnable() {
					public void run() {
						AddJSFCapabilitiesWizard.getInstance(project).run(null);
					}
				});
	}

	@Override
	protected String getMessageInfo() {
		String dialogMessage = MessageFormat.format(
				JsfUIMessages.ENABLE_JSF_CAPABILITIES_TEXT, project.getName());
		return dialogMessage;
	}

	@Override
	protected void skipButtonPressed() {
		try {
			project.setPersistentProperty(
					ProjectNaturesChecker.IS_JSF_NATURES_CHECK_NEED,
					Boolean.toString(!isRemember));
		} catch (CoreException e) {
		}
	}

}
