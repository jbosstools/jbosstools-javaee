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
import java.util.Properties;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;

import org.jboss.tools.common.model.ui.editors.dnd.*;
import org.jboss.tools.common.model.ui.editors.dnd.composite.TagProposalsComposite;

public class DataTableWizard extends Wizard implements PropertyChangeListener,
		IDropWizard {
	
	PaletteDropCommand fDropCommand;
	IDropWizardModel fModel;
	private DataTableWizardPage page2 = null;	
	static String H_PREFIX  = "%prefix|http://java.sun.com/jsf/html|h%";
	
	public DataTableWizard () {
		setWindowTitle(DropWizardMessages.Wizard_Window_Title);
	}
	
	
	public void addPages() {
		super.addPages();
		page2 = new DataTableWizardPage();
		page2.setProperties(fDropCommand.getProperties());
		
		TagProposal[] proposals = 
			TagProposalsComposite.getTagProposals(getMimeType(),getMimeData(), fDropCommand.getTagProposalFactory());
		
		if(TagProposalsComposite.areThereTagProposals(
			getMimeType(),getMimeData(), fDropCommand.getTagProposalFactory())
		) {
			if(proposals.length > 1) {
			}
		}
		this.addPage(page2);

		getWizardModel().addPropertyChangeListener(this);		
		
		if(proposals.length==1) { 
			getWizardModel().setTagProposal(proposals[0]);
		}
	}
	
	
	public void propertyChange(PropertyChangeEvent evt) {
		IWizardPage[] pages = getPages();
		for (int i = 0; i < pages.length; i++) {
			DefaultDropWizardPage page = (DefaultDropWizardPage)pages[i];
			page.runValidation();
		}

	}
	
	public boolean performFinish() {
		fillColumn();
		fDropCommand.execute();
		
		return true;
	}

	public void setCommand(IDropCommand command) {
		fDropCommand = (PaletteDropCommand)command;
	}

	public IDropWizardModel getWizardModel() {
		return fDropCommand.getDefaultModel();		
	}
	
	public boolean canFinish() {
		return getWizardModel().isValid();

	}
	public String getMimeData() {
		return  getWizardModel().getDropData().getMimeData();
	}

	public String getMimeType() {
		return  getWizardModel().getDropData().getMimeType();
	}
	
	public void fillColumn(){
		Properties properties = fDropCommand.getProperties();
		String[] vs =  page2.getSelectedProperties();
		StringBuffer text = new StringBuffer();
		text.append(properties.getProperty("start text"));
		
		String value = null;
		if (page2.getValue().length() > 0) {
			value  = page2.getValue().substring(2,page2.getValue().length()-1);
		}
		String var = null;
		if (page2.getVar().length() > 0) {
			var = page2.getVar();
		}
		 
		if (var != null) 		
			fillin(text, vs, var);
		else if (value != null)
			fillin(text, vs, value);
			else 
				fillin(text, vs,"");
		
		fDropCommand.getProperties().setProperty("start text", text.toString());			
	}
	
	private void fillin(StringBuffer text, String[] vs, String val) {
		for (int i = 0; i < vs.length; i++){
			text.append("\n\t<" + H_PREFIX + "column>\n" +
				"\t\t<"+ H_PREFIX + "outputText value=\"#{"
				+ val + "." + vs[i] + "}\"/>\n" +
				 		"\t</" + H_PREFIX + "column>");
		}
	}

}
