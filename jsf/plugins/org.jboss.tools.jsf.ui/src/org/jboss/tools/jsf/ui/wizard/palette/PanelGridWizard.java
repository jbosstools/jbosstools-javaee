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

/**
 *  @author erick 
 */

public class PanelGridWizard extends Wizard implements PropertyChangeListener, IDropWizard {
	PaletteDropCommand fDropCommand;

	/**
	 * 
	 * @param command
	 */
	public PanelGridWizard() {
		setWindowTitle(DropWizardMessages.Wizard_Window_Title);
	}

	public void setCommand(IDropCommand command) {
		fDropCommand = (PaletteDropCommand) command;
	}

	private PanelGridWizardPage page2 = null;

	/**
	 * 
	 */
	public void addPages() {
		super.addPages();
		page2 = new PanelGridWizardPage();

		TagProposal[] proposals = TagProposalsComposite.getTagProposals(
				getMimeType(), getMimeData(), fDropCommand
						.getTagProposalFactory());

		if (TagProposalsComposite.areThereTagProposals(getMimeType(),
				getMimeData(), fDropCommand.getTagProposalFactory())) {
			if (proposals.length > 1) {
			}
		}
		this.addPage(page2);

		getWizardModel().addPropertyChangeListener(this);

		if (proposals.length == 1) {
			getWizardModel().setTagProposal(proposals[0]);
		}
	}

	/**
	 * 
	 */
	public boolean canFinish() {
		return getWizardModel().isValid();

	}

	/**
	 * 
	 */
	public boolean performFinish() {
		setOptionsString(page2.isOptionHeaderChecked(), page2
				.isOptionFooterChecked());
		fDropCommand.execute();
		return true;
	}

	/**
	 * 
	 */
	public boolean performCancel() {
		return true;
	}

	/**
	 * 
	 * @return
	 */
	public IDropWizardModel getWizardModel() {
		return fDropCommand.getDefaultModel();
	}

	/**
	 * 
	 * @return
	 */
	public String getMimeData() {
		return getWizardModel().getDropData().getMimeData();
	}

	/**
	 * 
	 * @return
	 */
	public String getMimeType() {
		return getWizardModel().getDropData().getMimeType();
	}

	/**
	 * Unexplainable update logic for wizard buttons
	 */
	public void propertyChange(PropertyChangeEvent evt) {
		IWizardPage[] pages = getPages();
		for (int i = 0; i < pages.length; i++) {
			DefaultDropWizardPage page = (DefaultDropWizardPage) pages[i];
			page.runValidation();
		}
	}

	static String F_PREFIX = "%prefix|http://java.sun.com/jsf/core|f%";

	public void setOptionsString(boolean header, boolean footer) {

		StringBuffer text = new StringBuffer();

		text.append(fDropCommand.getProperties().getProperty("start text"));

		if (header == true && footer == true) {
			text.append("\n\t<" + F_PREFIX + "facet name=\"header\"></"
					+ F_PREFIX + "facet>\n");
			if (page2.isColomns())
				text.append("\t\t" + '|');
			text.append("\n\t<" + F_PREFIX + "facet name=\"footer\"></"
					+ F_PREFIX + "facet>");
			fDropCommand.getProperties().setProperty("new line", "false");
		}

		if (header == true && footer == false) {
			text.append("\n\t<" + F_PREFIX + "facet name=\"header\"></"
					+ F_PREFIX + "facet>");
		}

		if (header == false && footer == true) {
			if (page2.isColomns()) {
				text.append("\n\t" + '|');
				text.append("\n\t<" + F_PREFIX + "facet name=\"footer\"></"
						+ F_PREFIX + "facet>");
			} else {
				text.append("\n\t" + " " + "\n\t<" + F_PREFIX
						+ "facet name=\"footer\"></" + F_PREFIX + "facet>");
			}
			fDropCommand.getProperties().setProperty("new line", "false");
		}
		fDropCommand.getProperties().setProperty("start text", text.toString());
	}

}
