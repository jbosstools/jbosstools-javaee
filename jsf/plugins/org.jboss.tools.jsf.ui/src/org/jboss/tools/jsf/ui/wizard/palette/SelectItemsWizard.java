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
package org.jboss.tools.jsf.ui.wizard.palette;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;

import org.jboss.tools.common.model.ui.editors.dnd.*;
import org.jboss.tools.common.model.ui.editors.dnd.composite.*;
import org.jboss.tools.jst.jsp.jspeditor.dnd.PaletteDropCommand;

/**
 *  @author erick 
 */

public class SelectItemsWizard extends Wizard implements PropertyChangeListener, IDropWizard {
	PaletteDropCommand fDropCommand;
	IDropWizardModel fModel;

	private SelectItemsWizardPage page2 = null;

	public SelectItemsWizard() {
		setWindowTitle(DropWizardMessages.Wizard_Window_Title);
	}

	public boolean canFinish() {
		return getWizardModel().isValid();
	}

	public boolean performFinish() {
		setText();
		fDropCommand.execute();
		return true;
	}

	public boolean performCancel() {
		return true;
	}

	public void propertyChange(PropertyChangeEvent evt) {
		IWizardPage[] pages = getPages();
		for (int i = 0; i < pages.length; i++) {
			DefaultDropWizardPage page = (DefaultDropWizardPage) pages[i];
			page.runValidation();
		}
	}

	public void setCommand(IDropCommand command) {
		fDropCommand = (PaletteDropCommand) command;
	}

	public IDropWizardModel getWizardModel() {
		return fDropCommand.getDefaultModel();
	}

	public String getMimeData() {
		return getWizardModel().getDropData().getMimeData();
	}

	public String getMimeType() {
		return getWizardModel().getDropData().getMimeType();
	}

	/**
	 * 
	 */
	public void addPages() {
		super.addPages();

		page2 = new SelectItemsWizardPage();

		ITagProposal[] proposals = TagProposalsComposite.getTagProposals(
				getMimeType(), getMimeData(), fDropCommand
						.getTagProposalFactory());

		this.addPage(page2);

		getWizardModel().addPropertyChangeListener(this);

		if (proposals.length == 1) {
			getWizardModel().setTagProposal(proposals[0]);
		}
	}

	static String F_PREFIX = "%prefix|http://java.sun.com/jsf/core|f%"; //$NON-NLS-1$

	public void setText() {
		StringBuffer text = new StringBuffer();
		if (page2.getText() != null && page2.getText().trim().length() > 0) {
			text.append(fDropCommand.getProperties().getProperty("start text")); //$NON-NLS-1$
			text.append("\n\t<" + F_PREFIX + "selectItems value=\"" //$NON-NLS-1$ //$NON-NLS-2$
					+ page2.getText() + "\"/>"); //$NON-NLS-1$
			fDropCommand.getProperties().setProperty("start text", //$NON-NLS-1$
					text.toString());
		}
	}
	
	public void dispose() {
		getWizardModel().removePropertyChangeListener(this);
		super.dispose();
	}
}
