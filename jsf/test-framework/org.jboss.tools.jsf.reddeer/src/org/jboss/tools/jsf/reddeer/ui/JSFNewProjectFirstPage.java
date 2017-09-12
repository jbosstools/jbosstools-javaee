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

import org.eclipse.reddeer.core.reference.ReferencedComposite;
import org.eclipse.reddeer.jface.wizard.WizardPage;
import org.eclipse.reddeer.swt.impl.combo.DefaultCombo;
import org.eclipse.reddeer.swt.impl.text.DefaultText;

public class JSFNewProjectFirstPage extends WizardPage {

	public JSFNewProjectFirstPage(ReferencedComposite referencedComposite) {
		super(referencedComposite);
	}

	public void setProjectName(String name) {
		new DefaultText(0).setText(name);
	}

	public void setJSFEnvironment(String environment) {
		new DefaultCombo(0).setSelection(environment);
	}

	public void setProjectTemplate(String projectTemplate) {
		new DefaultCombo(1).setSelection(projectTemplate);
	}

}
