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
package org.jboss.tools.seam.ui.wizard;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.jboss.tools.seam.ui.SeamUIMessages;
import org.jboss.tools.seam.ui.widget.editor.LabelFieldEditor;

/**
 * @author Alexey Kazakov
 */
public class SeamGenerateEnitiesWizardPage extends WizardPage {

	public SeamGenerateEnitiesWizardPage() {
		super("seam.generate.entities.page", SeamUIMessages.GENERATE_SEAM_ENTITIES_WIZARD_TITLE, null);
		setMessage(SeamUIMessages.GENERATE_SEAM_ENTITIES_WIZARD_PAGE1_MESSAGE);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {
		Composite top = new Composite(parent, SWT.NONE);
		top.setLayout(new GridLayout());
		top.setLayoutData(new GridData(GridData.FILL_BOTH));

		LabelFieldEditor label = new LabelFieldEditor("test", "test");
		label.createLabelControl(top);
		setPageComplete(false);
		setControl(top);
	}
}