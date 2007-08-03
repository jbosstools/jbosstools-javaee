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

import java.net.URL;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jst.servlet.ui.project.facet.WebProjectWizard;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.project.facet.ui.PresetSelectionPanel;
import org.osgi.framework.Bundle;

/**
 * 
 * @author eskimo
 *
 */
public class SeamProjectWizard extends WebProjectWizard {

	
	public SeamProjectWizard() {
		super();
		setWindowTitle("New Seam Project");
	}

	public SeamProjectWizard(IDataModel model) {
		super(model);
		setWindowTitle("New Seam Project");
	}

	@Override
	protected IWizardPage createFirstPage() {
		IWizardPage page = super.createFirstPage();
		page.setImageDescriptor(ImageDescriptor.createFromFile(SeamFormWizard.class, "SeamWebProjectWizBan.png")); 
		page.setTitle("Seam Web Project");
		page.setDescription("Create standalone Seam Web Project");
		return page;
	}

	@Override
	public void createPageControls(Composite container) {
		super.createPageControls(container);
		getModel().setSelectedPreset("preset.jst.seam.v1_2");
		Control control = findControlByClass((Composite)getShell(), PresetSelectionPanel.class);
		control.setVisible(false);
		control = findGroupByText((Composite)getShell(), "EAR Membership");
		control.setVisible(false);
	}
	
	
	Control findControlByClass(Composite comp, Class claz) {
		for (Control child : comp.getChildren()) {
			if(child.getClass()==claz) {
				return child;
			} else if(child instanceof Composite){
				Control control = findControlByClass((Composite)child, claz);
				if(control!=null) return control;
			}
		}
		return null;
	}
	
	
	Control findGroupByText(Composite comp, String text) {
		for (Control child : comp.getChildren()) {
			if(child instanceof Group && ((Group)child).getText().equals(text)) {
				return child;
			} else if(child instanceof Composite){
				Control control = findGroupByText((Composite)child, text);
				if(control!=null) return control;
			}
		}
		return null;
	}
}