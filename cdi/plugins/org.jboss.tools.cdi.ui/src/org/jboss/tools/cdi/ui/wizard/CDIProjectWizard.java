/******************************************************************************* 
 * Copyright (c) 2011 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.cdi.ui.wizard;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jst.servlet.ui.project.facet.WebProjectWizard;
import org.eclipse.wst.common.project.facet.core.FacetedProjectFramework;
import org.eclipse.wst.common.project.facet.core.IFacetedProjectTemplate;
import org.eclipse.wst.common.project.facet.core.IFacetedProjectWorkingCopy;
import org.eclipse.wst.common.project.facet.core.IPreset;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.IProjectFacetVersion;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntime;
import org.jboss.tools.cdi.core.CDIImages;
import org.jboss.tools.cdi.ui.CDIUIMessages;

/**
 * @author Alexey Kazakov
 */
public class CDIProjectWizard extends WebProjectWizard {

	private static final String CDI_TEMPALTE = "template.jboss.tools.cdi10";
	private IPreset oldPreset;

	public CDIProjectWizard() {
		super();
		setWindowTitle(CDIUIMessages.CDI_PROJECT_WIZARD_NEW_PROJECT);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jst.servlet.ui.project.facet.WebProjectWizard#getTemplate()
	 */
	@Override
	protected IFacetedProjectTemplate getTemplate() {
		return ProjectFacetsManager.getTemplate(CDI_TEMPALTE);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.wst.web.ui.internal.wizards.NewProjectDataModelFacetWizard#setRuntimeAndDefaultFacets(org.eclipse.wst.common.project.facet.core.runtime.IRuntime)
	 */
	@Override
    protected void setRuntimeAndDefaultFacets(IRuntime runtime) {
		IPreset preset = getFacetedProjectWorkingCopy().getSelectedPreset();
		if(preset!=null) {
			oldPreset = preset;
		}
		IFacetedProjectWorkingCopy dm = getFacetedProjectWorkingCopy();
		dm.setTargetedRuntimes(Collections.<IRuntime> emptySet());
		boolean dontUseRuntimeConfig = false;
		if (runtime != null) {
	        if(oldPreset!=null) {
	            dm.setProjectFacets(oldPreset.getProjectFacets());
	            dontUseRuntimeConfig = true;
	        } else {
				Set<IProjectFacetVersion> minFacets = new HashSet<IProjectFacetVersion>();
				try {
					for (IProjectFacet f : dm.getFixedProjectFacets()) {
						minFacets.add(f.getLatestSupportedVersion(runtime));
					}
				} catch (CoreException e) {
					throw new RuntimeException(e);
				}
				dm.setProjectFacets(minFacets);
	        }
			dm.setTargetedRuntimes(Collections.singleton(runtime));
		}
		if(dontUseRuntimeConfig) {
			if(dm.getAvailablePresets().contains(oldPreset)) {
				dm.setSelectedPreset(oldPreset.getId());
			}
		} else if(dm.getAvailablePresets().contains(FacetedProjectFramework.DEFAULT_CONFIGURATION_PRESET_ID)) {
			dm.setSelectedPreset(FacetedProjectFramework.DEFAULT_CONFIGURATION_PRESET_ID);
		}
    }

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jst.servlet.ui.project.facet.WebProjectWizard#createFirstPage()
	 */
	@Override
	protected IWizardPage createFirstPage() {
		IWizardPage page = super.createFirstPage();

		page.setTitle(CDIUIMessages.CDI_PROJECT_WIZARD_NEW_PROJECT_TITLE);
		page.setDescription(CDIUIMessages.CDI_PROJECT_WIZARD_NEW_PROJECT_DESCRIPTION);
		page.setImageDescriptor(getDefaultPageImageDescriptor());

		return page;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jst.servlet.ui.project.facet.WebProjectWizard#getDefaultPageImageDescriptor()
	 */
	@Override
	protected ImageDescriptor getDefaultPageImageDescriptor() {
		return CDIImages.getImageDescriptor(CDIImages.WELD_WIZARD_IMAGE_PATH);
	}
}