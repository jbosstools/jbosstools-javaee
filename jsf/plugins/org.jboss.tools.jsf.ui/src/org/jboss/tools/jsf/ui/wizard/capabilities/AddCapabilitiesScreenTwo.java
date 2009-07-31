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
package org.jboss.tools.jsf.ui.wizard.capabilities;

import org.jboss.tools.common.model.ui.attribute.IListContentProvider;
import org.jboss.tools.common.model.ui.wizards.special.AbstractSpecialWizardStep;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import org.jboss.tools.common.meta.action.impl.SpecialWizardSupport;

public class AddCapabilitiesScreenTwo extends AbstractSpecialWizardStep {
	String[] items = new String[0];
	ListViewer viewer;
	ContentProvider provider = new ContentProvider();

	public void setSupport(SpecialWizardSupport support, int i) {
		super.setSupport(support, i);
		items = (String[])support.getProperties().get("addedCapabilities"); //$NON-NLS-1$
		if(items == null) items = new String[0];
	}

	public Control createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.horizontalSpacing = 10;
		layout.marginHeight = 10;
		layout.verticalSpacing = 10;
		layout.marginWidth = 10;
		composite.setLayout(layout);
		GridData gd = new GridData(GridData.FILL_BOTH);
		composite.setLayoutData(gd);
		viewer = new ListViewer(composite, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER);
		viewer.setContentProvider(provider);
		viewer.setLabelProvider(provider);
		viewer.setInput(this);
		Control tc = viewer.getControl();
		tc.setLayoutData(new GridData(GridData.FILL_BOTH));
		return composite;
	}

	class ContentProvider extends LabelProvider implements IListContentProvider {
		public Object[] getElements(Object inputElement) {
			return items;
		}
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}
	}

}
