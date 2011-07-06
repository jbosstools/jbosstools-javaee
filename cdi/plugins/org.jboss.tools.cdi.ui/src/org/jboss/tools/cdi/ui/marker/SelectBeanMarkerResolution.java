/*******************************************************************************
 * Copyright (c) 2010 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.cdi.ui.marker;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IMarkerResolution2;
import org.eclipse.ui.PlatformUI;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.ICDIProject;
import org.jboss.tools.cdi.core.IInjectionPoint;
import org.jboss.tools.cdi.core.IQualifier;
import org.jboss.tools.cdi.ui.CDIUIMessages;
import org.jboss.tools.cdi.ui.wizard.SelectBeanWizard;

/**
 * @author Daniel Azarov
 */
public class SelectBeanMarkerResolution implements IMarkerResolution2, TestableResolutionWithDialog {
	private String label;
	private IInjectionPoint injectionPoint;
	private List<IBean> beans;
	
	public SelectBeanMarkerResolution(IInjectionPoint injectionPoint, List<IBean> beans){
		this.injectionPoint = injectionPoint;
		this.label = CDIUIMessages.SELECT_BEAN_TITLE;
		this.beans = beans;;
	}

	public String getLabel() {
		return label;
	}

	public void runForTest(IMarker marker){
		internal_run(marker, true);
	}
	
	public void run(IMarker marker) {
		internal_run(marker, false);
	}

	private void internal_run(IMarker marker, boolean test) {
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		SelectBeanWizard wizard = new SelectBeanWizard(injectionPoint, beans);
		WizardDialog dialog = new WizardDialog(shell, wizard);
		
		IBean selectedBean = null;
		List<IQualifier> deployed;
		
		if(test){
			if(beans.isEmpty())
				return;
			
			dialog.setBlockOnOpen(false);
			dialog.open();
			
			selectedBean = beans.get(0);
			
			wizard.init(selectedBean);
			
			List<IQualifier> qualifiers = new ArrayList<IQualifier>();
			qualifiers.addAll(wizard.getAvailableQualifiers());
			if(qualifiers.isEmpty())
				return;
			for(IQualifier qualifier : qualifiers){
				if(wizard.checkBeans())
					break;
				wizard.deploy(qualifier);
			}
			deployed = wizard.getDeployedQualifiers();
			wizard.performCancel();
			dialog.close();
		}else{
			int status = dialog.open();
			
			if(status != WizardDialog.OK)
				return;
		
			selectedBean = wizard.getBean();
			deployed = wizard.getDeployedQualifiers();
		}
		
		MarkerResolutionUtils.addQualifiersToBean(deployed, selectedBean);
		try {
			Job.getJobManager().join(ResourcesPlugin.FAMILY_AUTO_BUILD, null);
		} catch (InterruptedException e) {
			// do nothing
		}
		// reload selectedBean
		ICDIProject cdiProject = selectedBean.getCDIProject();
		IBean[] beans = cdiProject.getBeans();
		for(IBean bean : beans){
			if(bean.getBeanClass().getFullyQualifiedName().equals(selectedBean.getBeanClass().getFullyQualifiedName())){
				selectedBean = bean;
				break;
			}
		}
		MarkerResolutionUtils.addQualifiersToInjectedPoint(injectionPoint, selectedBean);
	}
	
	public String getDescription() {
		return label;
	}

	public Image getImage() {
		return null;
	}
}
