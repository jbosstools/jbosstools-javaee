/*******************************************************************************
 * Copyright (c) 2016-2017 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.jsf.reddeer.ui;

import org.jboss.reddeer.common.wait.WaitWhile;
import org.jboss.reddeer.core.condition.JobIsRunning;
import org.jboss.reddeer.swt.impl.button.CheckBox;
import org.jboss.reddeer.swt.impl.button.FinishButton;
import org.jboss.reddeer.swt.impl.combo.LabeledCombo;
import org.jboss.reddeer.swt.impl.shell.DefaultShell;

public class WebComponentExportWizardPage extends DefaultShell {

	public WebComponentExportWizardPage() {
		super("Export");
	}

	public void setWebProject(String project) {
		new LabeledCombo("Web project:").setSelection(project);
	}

	public void setDestination(String destination) {
		new LabeledCombo("Destination:").setText(destination);
	}

	public void toggleOverwriteExistingFile(boolean checked) {
		new CheckBox("Overwrite existing file").toggle(checked);
	}

	public void finish() {
		new FinishButton().click();
		new WaitWhile(new JobIsRunning());
	}

}
