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
package org.jboss.tools.struts.ui.wizard.selectpage;

import org.jboss.tools.common.model.ui.navigator.*;
import org.jboss.tools.common.model.ui.wizards.special.AbstractSpecialWizardStep;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import org.jboss.tools.common.model.*;
import org.jboss.tools.common.model.filesystems.XFileObject;

public class SelectPageStep extends AbstractSpecialWizardStep {
//	protected SpecialWizardControl control = new SpecialWizardControl();
	protected XModelObject process;
	protected TreeViewer tree;
	protected XFilteredTree fTree;
	protected Text selected;
	protected XModelObject selectedObject = null;
	protected boolean lock = false;
	protected FilteredTreeContentProvider contentProvider;
	
	public SelectPageStep() {}

	public void dispose() {
		super.dispose();
		if (fTree!=null) fTree.dispose();
		fTree = null;
		tree = null;
	}

	public Control createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		composite.setLayout(new GridLayout());
		process = (XModelObject)support.getTarget();
		createTree(composite);
		createText(composite);
		composite.pack();
		parent.layout();
		composite.layout();
		update0();		
		return composite;
	}

	protected void createTree(Composite parent) {
		tree = new TreeViewer(parent);
		GridData gd = new GridData(GridData.FILL_BOTH);
		tree.getTree().setLayoutData(gd);
		contentProvider = new FCM();
		contentProvider.setModel(support.getTarget().getModel());
		contentProvider.setFilteredTreeName("StrutsWeb");
		tree.setContentProvider(contentProvider);
		tree.setLabelProvider(new NavigatorLabelProvider());
		tree.setInput(contentProvider);
		tree.refresh();
		tree.getTree().addSelectionListener(new SL());
//		tree.getTree().addMouseListener(new ML());
		tree.getTree().setVisible(true);
		tree.setAutoExpandLevel(2);
	}
	
	protected void createText(Composite parent) {
		selected = new Text(parent, SWT.BORDER);
		selected.setEditable(false);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.heightHint = 18;
		selected.setLayoutData(gd);
		selected.setBackground(new Color(null, 255, 255, 255));
	}
	
	class FCM extends FilteredTreeContentProvider {
		public FCM() {}
		protected XFilteredTree createFilteredTree() {
			XFilteredTree tree = super.createFilteredTree();
			if("yes".equals(support.getProperties().get("isException")))
			  tree.setConstraint(new Boolean(true));
			tree.setConstraint(process);
			fTree = tree;
			return tree;
		}
	}

	public void update() {}
	
	public void update0() {
		process = (XModelObject)support.getTarget();
		lock = true;
		selectedObject = (XModelObject)support.getProperties().get("selectedObject");
		XModelObject o = selectedObject;
		if(o != null) {
			tree.setSelection(new StructuredSelection(o), true);
		} else {
			o = (XModelObject)support.getProperties().get("selectedFileSystem");
			if(o != null) {
				tree.setSelection(new StructuredSelection(o), true);
			} else {
				selected.setText("");
			}
		}
		selected.setEditable(false);
		if(o != null) selected.setText(fTree.getPath(o));
		lock = false;
	}

	public void save() {
		String sp = selected.getText();
		support.getProperties().setProperty("selectedPath", sp);
		if(sp.length() > 0 && selectedObject != null) {
			support.getProperties().put("selectedObject", selectedObject);
		} else {
			support.getProperties().remove("selectedObject");
		}
		XModelObject fs = selectedObject;
		while(fs != null && fs.getFileType() != XFileObject.SYSTEM) fs = fs.getParent();
		if(fs != null) {
			support.getProperties().put("selectedFileSystem", fs);
		} else {
			support.getProperties().remove("selectedFileSystem");
		}
	}
	
	class SL extends SelectionAdapter {
		public void widgetSelected(SelectionEvent e) {
			StructuredSelection s = (StructuredSelection)tree.getSelection();
			Object o = s.getFirstElement();
			if(!(o instanceof XModelObject)) return;
			XModelObject mo = (XModelObject)o;
			selected.setText(fTree.getPath(mo));
			selectedObject = mo;
			save();
			validate();			
		}		
	}

	public Point getMaximumSize() {
		return null;
	}

	public Point getMinimumSize() {
		return null;
	}

}
