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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jst.servlet.ui.project.facet.WebProjectFirstPage;
import org.eclipse.jst.servlet.ui.project.facet.WebProjectWizard;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.common.frameworks.datamodel.DataModelFactory;
import org.eclipse.wst.common.frameworks.datamodel.DataModelPropertyDescriptor;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.project.facet.core.IFacetedProjectTemplate;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.eclipse.wst.common.project.facet.core.runtime.IRuntime;
import org.eclipse.wst.server.ui.ServerUIUtil;
import org.jboss.tools.seam.core.SeamCorePlugin;
import org.jboss.tools.seam.core.project.facet.SeamProjectPreferences;
import org.jboss.tools.seam.internal.core.project.facet.ISeamFacetDataModelProperties;
import org.jboss.tools.seam.internal.core.project.facet.SeamFacetProjectCreationDataModelProvider;
import org.jboss.tools.seam.ui.SeamUIMessages;
import org.jboss.tools.seam.ui.internal.project.facet.SeamInstallWizardPage;

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

	protected IDataModel createDataModel() {
		return DataModelFactory.createDataModel(new SeamFacetProjectCreationDataModelProvider());
	}

	@Override
	protected IWizardPage createFirstPage() {
//		IWizardPage page = super.createFirstPage();

		IWizardPage page = new SeamWebProjectFirstPage(model, "first.page"); //$NON-NLS-1$

		page.setImageDescriptor(ImageDescriptor.createFromFile(SeamFormWizard.class, "SeamWebProjectWizBan.png"));  //$NON-NLS-1$
		page.setTitle(SeamUIMessages.SEAM_PROJECT_WIZARD_SEAM_WEB_PROJECT);
		page.setDescription(SeamUIMessages.SEAM_PROJECT_WIZARD_CREATE_STANDALONE_SEAM_WEB_PROJECT);
		return page;
	}

	@Override
	public void createPageControls(Composite container) {
		super.createPageControls(container);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(container, "org.jboss.tools.seam.guide.new_seam_project");
		Control control = findGroupByText(getShell(), SeamUIMessages.SEAM_PROJECT_WIZARD_EAR_MEMBERSHIP);
		if (control != null)
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
		return "org.jboss.tools.seam.ui.SeamPerspective"; //$NON-NLS-1$
	}

	protected IFacetedProjectTemplate getTemplate() {
		return ProjectFacetsManager.getTemplate("template.jst.seam"); //$NON-NLS-1$
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.wst.web.ui.internal.wizards.NewProjectDataModelFacetWizard#performFinish()
	 */
	@Override
	public boolean performFinish() {
		SeamInstallWizardPage page = (SeamInstallWizardPage)getPage(SeamUIMessages.SEAM_INSTALL_WIZARD_PAGE_SEAM_FACET);
		page.finishPressed();
		return super.performFinish();
	}
	
	class SeamWebProjectFirstPage extends WebProjectFirstPage {
		
		public SeamWebProjectFirstPage(IDataModel model, String pageName ) {
			super(model, pageName);
		}

		protected Combo matchedServerTargetCombo;
		
		protected Composite createTopLevelComposite(Composite parent) {
			Composite top = new Composite(parent, SWT.NONE);
			PlatformUI.getWorkbench().getHelpSystem().setHelp(top, "org.jboss.tools.seam.guide.new_seam_project");
			top.setLayout(new GridLayout());
			top.setLayoutData(new GridData(GridData.FILL_BOTH));
			createProjectGroup(top);
			createServerTargetComposite(top);
			createSeamServerTargetComposite(top);
	        createPresetPanel(top);
	        return top;
		}

		
		protected void createSeamServerTargetComposite(Composite parent) {
//				super.createServerTargetComposite(parent);
	        Group group = new Group(parent, SWT.NONE);
	        group.setText(SeamUIMessages.SEAM_TARGET_SERVER);
	        group.setLayoutData(gdhfill());
	        group.setLayout(new GridLayout(2, false));

	        matchedServerTargetCombo = new Combo(group, SWT.BORDER | SWT.READ_ONLY);
			matchedServerTargetCombo.setLayoutData(gdhfill());
			Button newMatchedServerTargetButton = new Button(group, SWT.NONE);
			newMatchedServerTargetButton.setText(SeamUIMessages.SEAM_INSTALL_WIZARD_PAGE_NEW);
			newMatchedServerTargetButton.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					if (!SeamWebProjectFirstPage.this.internalLaunchNewServerWizard(getShell(), model)) {
						//Bugzilla 135288
						//setErrorMessage(ResourceHandler.InvalidServerTarget);
					}
				}
			});
			Control[] depsMatched = new Control[]{serverTargetCombo, newMatchedServerTargetButton};
			synchHelper.synchCombo(matchedServerTargetCombo, ISeamFacetDataModelProperties.JBOSS_AS_TARGET_SERVER, depsMatched);
			if (matchedServerTargetCombo.getSelectionIndex() == -1 && matchedServerTargetCombo.getVisibleItemCount() != 0)  
				matchedServerTargetCombo.select(0);
		}
		
		protected String[] getValidationPropertyNames() {
			String[] superProperties = super.getValidationPropertyNames();
			List list = Arrays.asList(superProperties);
			ArrayList arrayList = new ArrayList();
			arrayList.addAll( list );
			arrayList.add( ISeamFacetDataModelProperties.JBOSS_AS_TARGET_RUNTIME);
			arrayList.add( ISeamFacetDataModelProperties.JBOSS_AS_TARGET_SERVER);
			return (String[])arrayList.toArray( new String[0] );
		}	

		public boolean launchNewServerWizard(Shell shell, IDataModel model) {
			return launchNewServerWizard(shell, model, null);
		}
		
		public boolean launchNewServerWizard(Shell shell, IDataModel model, String serverTypeID) {
			DataModelPropertyDescriptor[] preAdditionDescriptors = model.getValidPropertyDescriptors(ISeamFacetDataModelProperties.JBOSS_AS_TARGET_SERVER);
			IRuntime rt = (IRuntime)model.getProperty(ISeamFacetDataModelProperties.JBOSS_AS_TARGET_RUNTIME);

			boolean isOK = ServerUIUtil.showNewServerWizard(shell, serverTypeID, null, (rt == null ? null : null));
			if (isOK && model != null) {

				DataModelPropertyDescriptor[] postAdditionDescriptors = model.getValidPropertyDescriptors(ISeamFacetDataModelProperties.JBOSS_AS_TARGET_SERVER);
				Object[] preAddition = new Object[preAdditionDescriptors.length];
				for (int i = 0; i < preAddition.length; i++) {
					preAddition[i] = preAdditionDescriptors[i].getPropertyValue();
				}
				Object[] postAddition = new Object[postAdditionDescriptors.length];
				for (int i = 0; i < postAddition.length; i++) {
					postAddition[i] = postAdditionDescriptors[i].getPropertyValue();
				}
				Object newAddition = null;

				if (preAddition != null && postAddition != null && preAddition.length < postAddition.length) {
					for (int i = 0; i < postAddition.length; i++) {
						boolean found = false;
						Object object = postAddition[i];
						for (int j = 0; j < preAddition.length; j++) {
							if (preAddition[j] == object) {
								found = true;
								break;
							}
						}
						if (!found) {
							newAddition = object;
						}
					}
				}
				if (preAddition == null && postAddition != null && postAddition.length == 1)
					newAddition = postAddition[0];
				
				model.notifyPropertyChange(ISeamFacetDataModelProperties.JBOSS_AS_TARGET_SERVER, IDataModel.VALID_VALUES_CHG);
				if (newAddition != null)
					model.setProperty(ISeamFacetDataModelProperties.JBOSS_AS_TARGET_SERVER, newAddition);
				else
					return false;
			}
			return isOK;
		}
		
		public boolean internalLaunchNewServerWizard(Shell shell, IDataModel model) {
			return launchNewServerWizard(shell, model, getModuleTypeID());
		}
		
	    public void restoreDefaultSettings() {
	    	super.restoreDefaultSettings();

	    	String lastServerName = SeamProjectPreferences
			.getStringPreference(SeamProjectPreferences.SEAM_LAST_SERVER_NAME);
	    	
	    	if (lastServerName != null && lastServerName.length() > 0) {
		    	SeamFacetProjectCreationDataModelProvider.setServerName(model,lastServerName);
	    	}
	    }

	    public void storeDefaultSettings() {
	    	super.storeDefaultSettings();
	    	String serverName = SeamFacetProjectCreationDataModelProvider.getServerName(model);
	    	if (serverName != null && serverName.length() > 0) {
				SeamCorePlugin.getDefault().getPluginPreferences().setValue(
						SeamProjectPreferences.SEAM_LAST_SERVER_NAME,
						serverName);
	    	}
	    }

	}
}