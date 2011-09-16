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

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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
import org.jboss.tools.cdi.ui.CDIUiImages;
import org.jboss.tools.cdi.ui.wizard.AddQualifiersToBeanWizard;
import org.jboss.tools.cdi.ui.wizard.xpl.AddQualifiersToBeanComposite;
import org.jboss.tools.cdi.ui.wizard.xpl.AddQualifiersToBeanComposite.ValuedQualifier;

/**
 * @author Daniel Azarov
 */
public class MakeInjectedPointUnambiguousMarkerResolution implements IMarkerResolution2, TestableResolutionWithDialog {
	private String label;
	private IInjectionPoint injectionPoint;
	private List<IBean> beans;
	private IBean selectedBean;
	
	public MakeInjectedPointUnambiguousMarkerResolution(IInjectionPoint injectionPoint, List<IBean> beans, int index){
		this.injectionPoint = injectionPoint;
		this.beans = beans;
		this.selectedBean = beans.get(index);
		this.label = MessageFormat.format(CDIUIMessages.MAKE_INJECTED_POINT_UNAMBIGUOUS_TITLE, new Object[]{selectedBean.getElementName()});
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
		if(checkBeans()){
			Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
			AddQualifiersToBeanWizard wizard = new AddQualifiersToBeanWizard(injectionPoint, beans, selectedBean);
			WizardDialog dialog = new WizardDialog(shell, wizard);
			
			List<ValuedQualifier> deployed;
			
			if(test){
				dialog.setBlockOnOpen(false);
				dialog.open();
				
				List<IQualifier> qualifiers = new ArrayList<IQualifier>();
				qualifiers.addAll(wizard.getAvailableQualifiers());
				if(qualifiers.isEmpty())
					return;
				for(IQualifier qualifier : qualifiers){
					if(wizard.checkBeans())
						break;
					wizard.deploy(new ValuedQualifier(qualifier));
				}
				deployed = wizard.getDeployedQualifiers();
				wizard.performCancel();
				dialog.close();
			}else{
				int status = dialog.open();
				
				if(status != WizardDialog.OK)
					return;
			
				deployed = wizard.getDeployedQualifiers();
			}

			
			deployed = wizard.getDeployedQualifiers();
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
				if(bean.getBeanClass().getFullyQualifiedName().equals(selectedBean.getBeanClass().getFullyQualifiedName()) && bean.getElementName().equals(selectedBean.getElementName())){
					selectedBean = bean;
					break;
				}
			}
			
		}
		MarkerResolutionUtils.addQualifiersToInjectionPoint(injectionPoint, selectedBean);
	}
	
	private boolean checkBeans(){
		Set<IQualifier> qualifiers = selectedBean.getQualifiers();
		if(qualifiers.size() == 0)
			return true;
		
		for(IBean bean: beans){
			if(bean.equals(selectedBean))
				continue;
			if(MarkerResolutionUtils.checkBeanQualifiers(selectedBean, bean, qualifiers))
				return true;
				
		}
		return false;
	}
	
	
	public String getDescription() {
		return label;
	}

	public Image getImage() {
		return CDIUiImages.QUICKFIX_EDIT;
	}

}
