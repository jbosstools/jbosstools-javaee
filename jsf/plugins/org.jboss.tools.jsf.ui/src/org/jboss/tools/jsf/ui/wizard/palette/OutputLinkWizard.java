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
import org.jboss.tools.jst.web.ui.internal.editor.jspeditor.dnd.PaletteDropCommand;

/**
 *  @author erick 
 */

public class OutputLinkWizard extends Wizard implements PropertyChangeListener, IDropWizard {
	PaletteDropCommand fDropCommand;
	IDropWizardModel fModel;
	private OutputLinkWizardPage page2 = null;
	
	public OutputLinkWizard (){
		setWindowTitle(DropWizardMessages.Wizard_Window_Title);
		setDefaultPageImageDescriptor(ModelUIImages
				.getImageDescriptor(ModelUIImages.WIZARD_DEFAULT));
	}
	
	public boolean canFinish() {		
		return getWizardModel().isValid();
	}
	
	public boolean performFinish() {
		fDropCommand.execute();
		return true;
	}
	
	public boolean performCancel() {
		return true;
	}
	
	public void propertyChange(PropertyChangeEvent evt) {
		IWizardPage[] pages = getPages();
		for (int i = 0; i < pages.length; i++) {
			DefaultDropWizardPage page = (DefaultDropWizardPage)pages[i];
			page.runValidation();
		}
	}
	
	public void setCommand(IDropCommand command) {
		fDropCommand = (PaletteDropCommand)command;		
	}
	
	public IDropWizardModel getWizardModel() {
		return fDropCommand.getDefaultModel();		
	}
	
	public String getMimeData() {
		return  getWizardModel().getDropData().getMimeData();
	}
	
	public String getMimeType() {
		return  getWizardModel().getDropData().getMimeType();
	}		
	
	/**
	 * 
	 */
	public void addPages() {
		super.addPages();		

		page2 = new OutputLinkWizardPage();
		
		ITagProposal[] proposals = 
			TagProposalsComposite.getTagProposals(getMimeType(),getWizardModel().getDropData(), fDropCommand.getTagProposalFactory());
		
		this.addPage(page2);

		getWizardModel().addPropertyChangeListener(this);		
		
		if(proposals.length==1) { 
			getWizardModel().setTagProposal(proposals[0]);
		}
		getWizardModel().setElementGenerator(g);
	}
	
	OutputLinkElementGenerator g = new OutputLinkElementGenerator();

	class OutputLinkElementGenerator extends DefaultElementGenerator {

		@Override
		protected void generateChildren(ElementNode node) {
			String fPrefix = getDropData().getValueProvider().getPrefix(DropURI.JSF_CORE_URI, "f"); //$NON-NLS-1$
			String tag = fPrefix+ ":verbatim"; //$NON-NLS-1$

			if(page2.getText() != null && page2.getText().trim().length() > 0) {
				fDropCommand.getProperties().setProperty("new line", "false"); //$NON-NLS-1$ //$NON-NLS-2$
				node.addChild(tag, page2.getText());
			}
		}

		@Override
		public String generateEndTag() {
			if (page2.isValue()) {
				return generatedEndTag + '|';
			}			
			return generatedEndTag;
		}
		
	}

	public void dispose() {
		getWizardModel().removePropertyChangeListener(this);
		super.dispose();
	}

}
