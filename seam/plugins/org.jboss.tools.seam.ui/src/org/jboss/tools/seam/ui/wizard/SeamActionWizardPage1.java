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
public class SeamActionWizardPage1 extends SeamBaseWizardPage {

	/**
	 * @param pageName
	 * @param title
	 * @param titleImage
	 */
	public SeamActionWizardPage1() {
		super("seam.new.action.page1", "Seam Action", null);
		setMessage("Select the name of the new Seam Conversation. A new Java interface and SLSB " +
				"with key Seam/EJB annotations will be created.");
	}
	
	protected void createEditors() {
		addEditors(SeamWizardFactory.createActionFormFieldEditors(SeamWizardUtils.getSelectedProjectName()));
	}
}
