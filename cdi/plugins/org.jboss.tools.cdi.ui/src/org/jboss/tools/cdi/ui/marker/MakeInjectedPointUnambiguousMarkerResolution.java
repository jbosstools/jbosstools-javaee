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
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IMarkerResolution2;
import org.eclipse.ui.internal.Workbench;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.IQualifier;
import org.jboss.tools.cdi.ui.CDIUIMessages;
import org.jboss.tools.cdi.ui.wizard.AddQualifiersToBeanWizard;

/**
 * @author Daniel Azarov
 */
public class MakeInjectedPointUnambiguousMarkerResolution implements IMarkerResolution2 {
	private String label;
	private IFile file;
	private List<IBean> beans;
	private IBean selectedBean;
	
	public MakeInjectedPointUnambiguousMarkerResolution(List<IBean> beans, IFile file, int index){
		this.file = file;
		this.beans = beans;
		this.selectedBean = beans.get(index);
		this.label = MessageFormat.format(CDIUIMessages.MAKE_INJECTED_POINT_UNAMBIGUOUS_TITLE, new Object[]{selectedBean.getBeanClass().getElementName()});
	}

	public String getLabel() {
		return label;
	}

	public void run(IMarker marker) {
		if(checkBeans()){
			//System.out.println("Should show dialog here.");
			Shell shell = Workbench.getInstance().getActiveWorkbenchWindow().getShell();
			AddQualifiersToBeanWizard wizard = new AddQualifiersToBeanWizard(selectedBean);
			WizardDialog dialog = new WizardDialog(shell, wizard);
			dialog.open();
		}else{
			//System.out.println("Should update injected point without dialog");
		}
	}
	
	private boolean checkBeans(){
		Set<IQualifier> qualifiers = selectedBean.getQualifiers();
		if(qualifiers.size() == 0)
			return true;
		
		for(IBean bean: beans){
			if(bean.equals(selectedBean))
				continue;
			if(checkBeanQualifiers(bean, qualifiers))
				return true;
				
		}
		return false;
	}
	
	private boolean checkBeanQualifiers(IBean bean, Set<IQualifier> qualifiers){
		for(IQualifier qualifier : qualifiers){
			if(!isBeanContainQualifier(bean.getQualifiers(), qualifier)){
				return false;
			}
		}
		if(bean.getQualifiers().size() == qualifiers.size())
			return true;
		return false;
	}
	
	private boolean isBeanContainQualifier(Set<IQualifier> qualifiers, IQualifier qualifier){
		for(IQualifier q : qualifiers){
			if(q.getSourceType().getFullyQualifiedName().equals(qualifier.getSourceType().getFullyQualifiedName()))
				return true;
		}
		return false;
	}
	
	public String getDescription() {
		return null;
	}

	public Image getImage() {
		return null;
	}

}
