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
import org.eclipse.reddeer.swt.impl.button.CheckBox;
import org.eclipse.reddeer.swt.impl.combo.DefaultCombo;

public class JSFNewProjectSecondPage extends WizardPage {

	public JSFNewProjectSecondPage(ReferencedComposite referencedComposite) {
		super(referencedComposite);
	}

	public void setRuntime(String runtime) {
		new DefaultCombo(1).setSelection(runtime);
	}

	public void toggleServer(String serverName, boolean checked) {
		new CheckBox(serverName).toggle(checked);
	}

}
