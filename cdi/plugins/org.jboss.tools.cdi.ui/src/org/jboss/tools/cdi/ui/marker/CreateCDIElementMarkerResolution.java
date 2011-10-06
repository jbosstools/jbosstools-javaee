/*******************************************************************************
 * Copyright (c) 2011 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.cdi.ui.marker;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IMarkerResolution2;
import org.eclipse.ui.PlatformUI;
import org.jboss.tools.cdi.core.CDIImages;
import org.jboss.tools.cdi.ui.CDIUIMessages;
import org.jboss.tools.cdi.ui.wizard.NewBeanCreationWizard;
import org.jboss.tools.cdi.ui.wizard.NewCDIElementWizard;
import org.jboss.tools.cdi.ui.wizard.NewDecoratorCreationWizard;
import org.jboss.tools.cdi.ui.wizard.NewInterceptorCreationWizard;
import org.jboss.tools.cdi.ui.wizard.NewStereotypeCreationWizard;
import org.jboss.tools.common.model.ui.wizards.NewTypeWizardAdapter;

public class CreateCDIElementMarkerResolution implements IMarkerResolution2{
	private static final String OBJECT = "java.lang.Object";
	
	public static final int CREATE_BEAN_CLASS = 1;
	public static final int CREATE_STEREOTYPE = 2;
	public static final int CREATE_INTERCEPTOR = 3;
	public static final int CREATE_DECORATOR = 4;
	
	private IProject project;
	private String qualifiedName;
	private int id;
	
	public CreateCDIElementMarkerResolution(IProject project, String text, int id){
		this.project = project;
		this.qualifiedName = text;
		this.id = id;
	}

	@Override
	public String getLabel() {
		switch(id){
		case CREATE_BEAN_CLASS:
			return NLS.bind(CDIUIMessages.CREATE_BEAN_CLASS_TITLE, qualifiedName);
		case CREATE_STEREOTYPE:
			return NLS.bind(CDIUIMessages.CREATE_STEREOTYPE_TITLE, qualifiedName);
		case CREATE_DECORATOR:
			return NLS.bind(CDIUIMessages.CREATE_DECORATOR_TITLE, qualifiedName);
		case CREATE_INTERCEPTOR:
			return NLS.bind(CDIUIMessages.CREATE_INTERCEPTOR_TITLE, qualifiedName);
		}
		return "";
	}

	@Override
	public void run(IMarker marker) {
		Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		NewCDIElementWizard wizard = null;
		switch(id){
		case CREATE_BEAN_CLASS:
			wizard = new NewBeanCreationWizard();
			break;
		case CREATE_STEREOTYPE:
			wizard = new NewStereotypeCreationWizard();
			break;
		case CREATE_DECORATOR:
			wizard = new NewDecoratorCreationWizard();
			break;
		case CREATE_INTERCEPTOR:
			wizard = new NewInterceptorCreationWizard();
			break;
		default: return;
		}
		
		NewTypeWizardAdapter adapter = new NewTypeWizardAdapter(project);
		adapter.setRawPackageName(MarkerResolutionUtils.getPackageName(qualifiedName));
		adapter.setRawClassName(qualifiedName);
		adapter.setRawSuperClassName(OBJECT);
		wizard.setAdapter(adapter);
		
		wizard.init(PlatformUI.getWorkbench(), new StructuredSelection(new Object[]{}));
		WizardDialog dialog = new WizardDialog(shell, wizard);
		
		dialog.open();
	}

	@Override
	public String getDescription() {
		return getLabel();
	}

	@Override
	public Image getImage() {
		return CDIImages.QUICKFIX_EDIT;
	}

}
