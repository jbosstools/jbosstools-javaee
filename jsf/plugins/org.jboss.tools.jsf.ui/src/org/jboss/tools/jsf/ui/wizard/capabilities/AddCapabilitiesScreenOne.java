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

import java.util.Properties;

import org.jboss.tools.common.model.ui.wizards.query.list.TreeItemSelectionManager;
import org.jboss.tools.common.model.ui.wizards.special.AbstractSpecialWizardStep;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;

import org.jboss.tools.common.meta.action.impl.SpecialWizardSupport;
import org.jboss.tools.jsf.project.capabilities.IPerformerItem;

public class AddCapabilitiesScreenOne extends AbstractSpecialWizardStep {
	protected TreeViewer treeViewer;
	IPerformerItem performer;
	CapabilityPerformersProvider provider = new CapabilityPerformersProvider();

	public void setSupport(SpecialWizardSupport support, int i) {
		super.setSupport(support, i);
		performer = (IPerformerItem)support.getProperties().get("CapabilitiesPerformer"); //$NON-NLS-1$
		provider.setItems(performer.getChildren());
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
		treeViewer = new TreeViewer(composite, SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER | SWT.CHECK);
		treeViewer.setContentProvider(provider);
		treeViewer.setLabelProvider(provider);
		treeViewer.setInput(this);
		Control tc = treeViewer.getControl();
		tc.setLayoutData(new GridData(GridData.FILL_BOTH));
		new TreeItemSelectionManager(treeViewer, new Flipper());

		createProgressMonitorPart(composite);
		
		return composite;
	}
	
	class Flipper implements TreeItemSelectionManager.Listener {
		public void flip(TreeItem item) {
			IPerformerItem w = (IPerformerItem)item.getData();
			if(w == null || !w.isEnabled()) return;
			w.setSelected(!w.isSelected());
			treeViewer.refresh(w);
			wizard.dataChanged(validator, new Properties());
		}
		public boolean isSelected(Object data) {
			IPerformerItem w = (IPerformerItem)data;
			return w != null && w.isSelected();
		}
	}

}
