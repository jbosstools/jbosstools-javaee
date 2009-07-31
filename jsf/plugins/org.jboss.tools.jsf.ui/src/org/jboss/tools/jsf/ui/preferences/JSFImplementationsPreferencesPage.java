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

import org.eclipse.jface.resource.ImageDescriptor;
import org.jboss.tools.jsf.web.JSFTemplate;
import org.jboss.tools.jst.web.ui.internal.preferences.AbstractImplementationsPreferencesPage;
import org.jboss.tools.jst.web.project.helpers.AbstractWebProjectTemplate;

/**
 * @author Tau, Minsk
 */
public class JSFImplementationsPreferencesPage extends AbstractImplementationsPreferencesPage {

	public static final String ID = "org.jboss.tools.jsf.ui.jsfimplementations"; //$NON-NLS-1$

	public JSFImplementationsPreferencesPage() {
		super();
	}

	/**
	 * @param title
	 */
	public JSFImplementationsPreferencesPage(String title) {
		super(title);
	}

	/**
	 * @param title
	 * @param image
	 */
	public JSFImplementationsPreferencesPage(String title, ImageDescriptor image) {
		super(title, image);
	}

	protected AbstractWebProjectTemplate createHelper() {
		return JSFTemplate.getInstance();
	}

}
