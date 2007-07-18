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


/**
 * @author eskimo
 *
 */
public class SeamFormWizardPage1 extends SeamBaseWizardPage {

	/**
	 * @param pageName
	 */
	public SeamFormWizardPage1() {
		super("seam.new.form.page1","Seam Form",null);
		setMessage("Select the name of the new Seam Form. A new Seam Form with a single input field and related " +
				"Java Interface, SLSB and key Seam/EJB3 annotations will be created.");
	}
	
	protected void createEditors() {
		addEditors(SeamWizardFactory.createActionFormFieldEditors(SeamWizardUtils.getSelectedProjectName()));
	}
}
