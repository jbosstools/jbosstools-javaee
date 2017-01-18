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

import org.jboss.reddeer.jface.wizard.WizardPage;
import org.jboss.reddeer.swt.impl.text.LabeledText;

public class ImportWebWarWizardPage extends WizardPage {

	public void setWarLocation(String location) {
		new LabeledText("*.war Location:*").setText(location);
	}

	public void setName(String name) {
		new LabeledText("Name:*").setText(name);
	}

	public void setContextPath(String contextPath) {
		new LabeledText("Context Path:*").setText(contextPath);
	}

}
