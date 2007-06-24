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
package org.jboss.tools.seam.ui.wizard;

import java.net.URL;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jst.servlet.ui.project.facet.WebProjectWizard;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.osgi.framework.Bundle;

/**

 */

public class SeamProjectWizard extends WebProjectWizard {

	
	public SeamProjectWizard() {
		super();
		setWindowTitle("New Seam Project");
	}

	public SeamProjectWizard(IDataModel model) {
		super(model);
		setWindowTitle("New Seam Project");
	}

	protected ImageDescriptor getDefaultPageImageDescriptor() {
		final Bundle bundle = Platform.getBundle("org.jboss.tools.common.model.ui");
		final URL url = bundle.getEntry("images/xstudio/wizards/EclipseCreateNewProject.png");
		return ImageDescriptor.createFromURL(url);
	}

	@Override
	protected IWizardPage createFirstPage() {
		IWizardPage page = super.createFirstPage();
		page.setTitle("Seam Web Project");
		page.setDescription("TBD Description of wizard");
		return page;
	}
	
	
}