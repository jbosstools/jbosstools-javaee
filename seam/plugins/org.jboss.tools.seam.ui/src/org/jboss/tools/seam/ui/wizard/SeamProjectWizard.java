/*******************************************************************************
 * Copyright (c) 2007 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.seam.ui.wizard;

import java.lang.reflect.InvocationTargetException;
import java.net.URL;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jst.servlet.ui.project.facet.WebProjectWizard;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.project.facet.core.IFacetedProjectTemplate;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.eclipse.wst.common.project.facet.ui.PresetSelectionPanel;
import org.jboss.tools.seam.ui.SeamUIMessages;
import org.osgi.framework.Bundle;

/**
 * 
 * @author eskimo
 *
 */
public class SeamProjectWizard extends WebProjectWizard {

	
	public SeamProjectWizard() {
		super();
		setWindowTitle(SeamUIMessages.SEAM_PROJECT_WIZARD_NEW_SEAM_PROJECT);
	}

	public SeamProjectWizard(IDataModel model) {
		super(model);
		setWindowTitle(SeamUIMessages.SEAM_PROJECT_WIZARD_NEW_SEAM_PROJECT);
	}

	@Override
	protected IWizardPage createFirstPage() {
		IWizardPage page = super.createFirstPage();
		page.setImageDescriptor(ImageDescriptor.createFromFile(SeamFormWizard.class, "SeamWebProjectWizBan.png"));  //$NON-NLS-1$
		page.setTitle(SeamUIMessages.SEAM_PROJECT_WIZARD_SEAM_WEB_PROJECT);
		page.setDescription(SeamUIMessages.SEAM_PROJECT_WIZARD_CREATE_STANDALONE_SEAM_WEB_PROJECT);
		return page;
	}

	@Override
	public void createPageControls(Composite container) {
		super.createPageControls(container);
		Control control = findGroupByText((Composite)getShell(), SeamUIMessages.SEAM_PROJECT_WIZARD_EAR_MEMBERSHIP);
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


	@Override
	protected String getFinalPerspectiveID() {
		// TODO Auto-generated method stub
		return "org.jboss.tools.seam.ui.SeamPerspective"; //$NON-NLS-1$
	}

	protected IFacetedProjectTemplate getTemplate() {
		return ProjectFacetsManager.getTemplate("template.jst.seam"); //$NON-NLS-1$
	}
}