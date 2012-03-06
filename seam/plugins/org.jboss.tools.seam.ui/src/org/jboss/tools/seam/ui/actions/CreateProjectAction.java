/*******************************************************************************
 * Copyright (c) 2007 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/

package org.jboss.tools.seam.ui.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.ide.IIDEHelpContextIds;
import org.jboss.tools.common.model.ui.util.ExtensionPointUtils;
import org.jboss.tools.seam.core.SeamCoreMessages;
import org.jboss.tools.seam.ui.SeamGuiPlugin;
import org.jboss.tools.seam.ui.SeamUiImages;

/**
 * @author Jeremy
 * 
 */
public class CreateProjectAction extends Action implements
		IWorkbenchWindowActionDelegate {

	private final static String SEAM_CREATE_PROJECT_WIZARD_ID = "org.jboss.tools.seam.ui.wizards.SeamProjectWizard"; //$NON-NLS-1$
	/**
	 * The wizard dialog width
	 */
	private static final int SIZING_WIZARD_WIDTH = 500;

	/**
	 * The wizard dialog height
	 */
	private static final int SIZING_WIZARD_HEIGHT = 500;

	public CreateProjectAction() {
		super(SeamCoreMessages.CREATE_NEW_SEAM_PROJECT);
		setToolTipText(SeamCoreMessages.CREATE_NEW_SEAM_PROJECT);
		setImageDescriptor(SeamUiImages
				.getImageDescriptor(SeamUiImages.SEAM_CREATE_PROJECT_ACTION));
	}

	@Override
	public void run() {
		INewWizard wizard = ExtensionPointUtils.findNewWizardsItem(
				SeamGuiPlugin.PLUGIN_ID, SEAM_CREATE_PROJECT_WIZARD_ID);
		if (wizard != null) {
			wizard.init(PlatformUI.getWorkbench(), null);
			WizardDialog dialog = new WizardDialog(PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow().getShell(), wizard);
			dialog.create();
			dialog.getShell().setSize(
					Math
							.max(SIZING_WIZARD_WIDTH, dialog.getShell()
									.getSize().x), SIZING_WIZARD_HEIGHT);
			PlatformUI.getWorkbench().getHelpSystem().setHelp(
					dialog.getShell(), IIDEHelpContextIds.NEW_PROJECT_WIZARD);

			dialog.open();
		} else {
			SeamGuiPlugin
					.getPluginLog()
					.logError(
							NLS.bind(SeamCoreMessages.CREATE_PROJECT_ACTION_UNABLE_TO_CREATE_WIZARD,
									SEAM_CREATE_PROJECT_WIZARD_ID));

		}
	}

	public void dispose() {
	}

	public void init(IWorkbenchWindow window) {
	}

	public void run(IAction action) {
		run();
	}

	public void selectionChanged(IAction action, ISelection selection) {
	}

}
