/*******************************************************************************
 * Copyright (c) 2007 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.jsf.ui.preferences;

import org.jboss.tools.jsf.web.JSFTemplate;
import org.jboss.tools.jst.web.ui.internal.preferences.AbstractPagesPreferencesPage;
import org.jboss.tools.jst.web.project.helpers.AbstractWebProjectTemplate;

/**
 * @author Gavrs
 */
public class JSFPagesPreferencesPage extends AbstractPagesPreferencesPage {

	public static final String ID = "org.jboss.tools.jsf.ui.jsfpages"; //$NON-NLS-1$

	protected AbstractWebProjectTemplate createHelper() {
		return JSFTemplate.getInstance();
	}

	protected boolean isSetDefaultAllowed() {
		return true;
	}
}
