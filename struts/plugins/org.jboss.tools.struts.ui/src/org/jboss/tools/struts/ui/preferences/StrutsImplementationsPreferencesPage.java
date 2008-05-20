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
package org.jboss.tools.struts.ui.preferences;

import org.eclipse.jface.resource.ImageDescriptor;
import org.jboss.tools.struts.StrutsUtils;
import org.jboss.tools.jst.web.ui.internal.preferences.AbstractImplementationsPreferencesPage;
import org.jboss.tools.jst.web.project.helpers.AbstractWebProjectTemplate;

public class StrutsImplementationsPreferencesPage extends AbstractImplementationsPreferencesPage {

	public StrutsImplementationsPreferencesPage() {
		super();
	}

	/**
	 * @param title
	 */
	public StrutsImplementationsPreferencesPage(String title) {
		super(title);
	}

	/**
	 * @param title
	 * @param image
	 */
	public StrutsImplementationsPreferencesPage(String title, ImageDescriptor image) {
		super(title, image);
	}

	protected AbstractWebProjectTemplate createHelper() {
		return StrutsUtils.getInstance();
	}

}
