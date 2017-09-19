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

import org.eclipse.reddeer.eclipse.selectionwizard.ImportMenuWizard;

public class ImportJSFWarWizard extends ImportMenuWizard {

	public ImportJSFWarWizard() {
		super("Import JSF Project", "Other", "JSF Project From *.war");
	}

}
