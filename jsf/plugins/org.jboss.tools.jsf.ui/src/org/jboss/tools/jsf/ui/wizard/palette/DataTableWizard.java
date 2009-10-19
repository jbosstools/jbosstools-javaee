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
import org.jboss.tools.jst.jsp.jspeditor.dnd.PaletteDropCommand;

public class DataTableWizard extends Wizard implements PropertyChangeListener,
		IDropWizard {
	
	PaletteDropCommand fDropCommand;
	IDropWizardModel fModel;
	private DataTableWizardPage page2 = null;	
	static String H_PREFIX  = "%prefix|http://java.sun.com/jsf/html|h%"; //$NON-NLS-1$
	
	public DataTableWizard () {
		setWindowTitle(DropWizardMessages.Wizard_Window_Title);
	}
	
	
	public void addPages() {
		super.addPages();
		page2 = new DataTableWizardPage();
		page2.setProperties(fDropCommand.getProperties());
		
		ITagProposal[] proposals = 
			TagProposalsComposite.getTagProposals(getMimeType(),getWizardModel().getDropData(), fDropCommand.getTagProposalFactory());
		
		if(TagProposalsComposite.areThereTagProposals(
			getMimeType(),getWizardModel().getDropData(), fDropCommand.getTagProposalFactory())
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
		String[] vs = page2.getSelectedProperties();
		StringBuffer text = new StringBuffer();
		text.append(properties.getProperty("start text")); //$NON-NLS-1$
		
		String value = null;
		if (page2.getValue().trim().length() > 0) {
			value = page2.getValue().trim();
			if(value.startsWith("#{") //$NON-NLS-1$
				|| value.startsWith("${")) { //$NON-NLS-1$
				value = value.substring(2);
			}
			if(value.endsWith("}")) { //$NON-NLS-1$
				value = value.substring(0, value.length() - 1);
			}
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
				fillin(text, vs,""); //$NON-NLS-1$
		
		fDropCommand.getProperties().setProperty("start text", text.toString());			 //$NON-NLS-1$
	}
	
	private void fillin(StringBuffer text, String[] vs, String val) {
		for (int i = 0; i < vs.length; i++){
			text.append("\n\t<" + H_PREFIX + "column>\n" + //$NON-NLS-1$ //$NON-NLS-2$
				"\t\t<"+ H_PREFIX + "outputText value=\"#{" //$NON-NLS-1$ //$NON-NLS-2$
				+ val + "." + vs[i] + "}\"/>\n" + //$NON-NLS-1$ //$NON-NLS-2$
				 		"\t</" + H_PREFIX + "column>"); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	public void dispose() {
		getWizardModel().removePropertyChangeListener(this);
		super.dispose();
	}

}
