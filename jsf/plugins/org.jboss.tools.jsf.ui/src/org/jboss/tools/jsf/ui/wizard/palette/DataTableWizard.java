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
import org.jboss.tools.common.model.ui.editors.dnd.composite.TagProposalsComposite;
import org.jboss.tools.jst.web.ui.internal.editor.jspeditor.dnd.PaletteDropCommand;
import org.jboss.tools.jst.web.ui.palette.html.wizard.HTMLConstants;

public class DataTableWizard extends Wizard implements PropertyChangeListener,
		IDropWizard {
	
	PaletteDropCommand fDropCommand;
	IDropWizardModel fModel;
	private DataTableWizardPage page2 = null;	
	
	public DataTableWizard () {
		setWindowTitle(DropWizardMessages.Wizard_Window_Title);
		setDefaultPageImageDescriptor(ModelUIImages
				.getImageDescriptor(ModelUIImages.WIZARD_DEFAULT));
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
		getWizardModel().setElementGenerator(g);
	}
	
	
	public void propertyChange(PropertyChangeEvent evt) {
		IWizardPage[] pages = getPages();
		for (int i = 0; i < pages.length; i++) {
			DefaultDropWizardPage page = (DefaultDropWizardPage)pages[i];
			page.runValidation();
		}

	}
	
	public boolean performFinish() {
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

	DataTableElementGenerator g = new DataTableElementGenerator();

	class DataTableElementGenerator extends DefaultElementGenerator {

		@Override
		protected void generateChildren(ElementNode node) {
			String[] vs = page2.getSelectedProperties();
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
			 
			if (var != null) { 		
				fillin(node, vs, var);
			} else if (value != null) {
				fillin(node, vs, value);
			} else { 
				fillin(node, vs,""); //$NON-NLS-1$
			}
		}

		private void fillin(ElementNode node, String[] vs, String val) {
			String hPrefix = getDropData().getValueProvider().getPrefix(DropURI.JSF_HTML_URI, "h");
			String tagColumn = hPrefix + ":column";
			String tagOutputText = hPrefix + ":outputText";
			for (int i = 0; i < vs.length; i++){
				ElementNode c = node.addChild(tagColumn);
				ElementNode o = c.addChild(tagOutputText);
				o.addAttribute(HTMLConstants.ATTR_VALUE, "#{" + val + "." + vs[i] + "}"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			}
		}

	}
	
	public void dispose() {
		getWizardModel().removePropertyChangeListener(this);
		super.dispose();
	}

}
