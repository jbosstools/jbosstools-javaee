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
import org.jboss.tools.common.model.ui.ModelUIImages;
import org.jboss.tools.common.model.ui.editors.dnd.*;
import org.jboss.tools.common.model.ui.editors.dnd.composite.*;
import org.jboss.tools.jst.jsp.jspeditor.dnd.PaletteDropCommand;
import org.jboss.tools.jst.web.ui.palette.html.wizard.HTMLConstants;

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
		setDefaultPageImageDescriptor(ModelUIImages
				.getImageDescriptor(ModelUIImages.WIZARD_DEFAULT));
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

		ITagProposal[] proposals = TagProposalsComposite.getTagProposals(
				getMimeType(), getWizardModel().getDropData(), fDropCommand
						.getTagProposalFactory());

		if (TagProposalsComposite.areThereTagProposals(getMimeType(),
				getWizardModel().getDropData(), fDropCommand.getTagProposalFactory())) {
			if (proposals.length > 1) {
			}
		}
		this.addPage(page2);

		getWizardModel().addPropertyChangeListener(this);

		if (proposals.length == 1) {
			getWizardModel().setTagProposal(proposals[0]);
		}
		getWizardModel().setElementGenerator(g);
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

	PanelGridElementGenerator g = new PanelGridElementGenerator();

	class PanelGridElementGenerator extends DefaultElementGenerator {
		@Override
		protected void generateChildren(ElementNode node) {
			boolean header = page2.isOptionHeaderChecked();
			boolean footer = page2.isOptionFooterChecked();
			String fPrefix = getDropData().getValueProvider().getPrefix(DropURI.JSF_CORE_URI, "f");
			String tagFacet = fPrefix + ":facet";
			if (header == true && footer == true) {
				ElementNode c = node.addChild(tagFacet, "");
				c.addAttribute(HTMLConstants.ATTR_NAME, "header");
				if (page2.isColomns()) {
//					node.getChildren().add(SEPARATOR);
				}
				c = node.addChild(tagFacet, "");
				c.addAttribute(HTMLConstants.ATTR_NAME, "footer");
//				fDropCommand.getProperties().setProperty("new line", "false"); //$NON-NLS-1$ //$NON-NLS-2$
			}

			if (header == true && footer == false) {
				ElementNode c = node.addChild(tagFacet, "");
				c.addAttribute(HTMLConstants.ATTR_NAME, "header");
			}

			if (header == false && footer == true) {
				if (page2.isColomns()) {
					if (page2.isColomns()) {
//						node.getChildren().add(SEPARATOR);
					}
					ElementNode c = node.addChild(tagFacet, "");
					c.addAttribute(HTMLConstants.ATTR_NAME, "footer");
				} else {
					ElementNode c = node.addChild(tagFacet, "");
					c.addAttribute(HTMLConstants.ATTR_NAME, "footer");
				}
				fDropCommand.getProperties().setProperty("new line", "false"); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}

	}

	public void dispose() {
		getWizardModel().removePropertyChangeListener(this);
		super.dispose();
	}
}
