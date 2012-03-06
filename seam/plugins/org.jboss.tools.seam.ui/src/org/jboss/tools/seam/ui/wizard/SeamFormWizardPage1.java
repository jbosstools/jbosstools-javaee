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

import org.eclipse.jface.viewers.IStructuredSelection;
import org.jboss.tools.seam.core.SeamCoreMessages;

/**
 * @author eskimo
 *
 */
public class SeamFormWizardPage1 extends SeamBaseWizardPage {

	/**
	 * @param is 
	 * @param pageName
	 */
	public SeamFormWizardPage1(IStructuredSelection is) {
		super("seam.new.form.page1",SeamCoreMessages.SEAM_FORM_WIZARD_PAGE1_SEAM_FORM,null, is); //$NON-NLS-1$
		setMessage(getDefaultMessageText());
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.seam.ui.wizard.SeamBaseWizardPage#getDefaultMessageText()
	 */
	@Override
	public String getDefaultMessageText() {
		return "Create a new Seam form";
	}
}