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

import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IMarkerResolution2;
import org.eclipse.ui.internal.Workbench;
import org.jboss.tools.cdi.core.IBean;
import org.jboss.tools.cdi.core.IInjectionPoint;
import org.jboss.tools.cdi.ui.CDIUIMessages;
import org.jboss.tools.cdi.ui.wizard.SelectBeanWizard;

/**
 * @author Daniel Azarov
 */
public class SelectBeanMarkerResolution implements IMarkerResolution2 {
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

	public void run(IMarker marker) {
		Shell shell = Workbench.getInstance().getActiveWorkbenchWindow().getShell();
		SelectBeanWizard wizard = new SelectBeanWizard(injectionPoint, beans);
		WizardDialog dialog = new WizardDialog(shell, wizard);
		int status = dialog.open();
		if(status != WizardDialog.OK)
			return;
			
	}
	
	public String getDescription() {
		return null;
	}

	public Image getImage() {
		return null;
	}
}
