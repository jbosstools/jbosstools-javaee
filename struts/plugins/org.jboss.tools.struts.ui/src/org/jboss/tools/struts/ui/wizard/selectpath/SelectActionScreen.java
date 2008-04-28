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
package org.jboss.tools.struts.ui.wizard.selectpath;

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
import org.jboss.tools.struts.model.helpers.path.ActionsTree;

public class SelectActionScreen extends AbstractSpecialWizardStep {
	protected TreeViewer tree;
	protected XFilteredTree fTree;
	protected Text selected;
	protected XModelObject selectedObject = null;
	protected boolean lock = false;
	protected FilteredTreeContentProvider contentProvider;
	private SelectionAdapter selectionListener = new SL();

	public void dispose() {
		super.dispose();
		if (fTree!=null) fTree.dispose();
		fTree = null;
		if (contentProvider!=null) contentProvider.dispose();
		contentProvider = null;
		selectionListener = null;
		tree = null;
	}
	
	public Control createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout());
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
		tree.setLabelProvider(new ALabelProvider());
		tree.setInput(contentProvider);
		tree.refresh();
		tree.getTree().addSelectionListener(selectionListener);
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
			XFilteredTree tree = new ActionsTree();
			tree.setModel(support.getTarget().getModel());
			if("yes".equals(support.getProperties().get("isException")))
			  tree.setConstraint(new Boolean(true));
			tree.setConstraint(support.getTarget());
			fTree = tree;
			return tree;
		}
	}

	public void update() {}
	
	public void update0() {
		selectedObject = (XModelObject)support.getProperties().get("selectedAction");        
		XModelObject po = (selectedObject == null) ? null : selectedObject.getParent().getParent();
		if(po != null) 
			tree.setSelection(new StructuredSelection(po), true);
		if(selectedObject != null)
		  tree.setSelection(new StructuredSelection(selectedObject), true);
		selected.setEditable(false);
		if(selectedObject != null) selected.setText(fTree.getPath(selectedObject));
	}
	
	public void save() {
		String sp = selected.getText();
		support.getProperties().setProperty("selectedPath", sp);
		if(sp.length() > 0 && selectedObject != null) {
			support.getProperties().put("selectedAction", selectedObject);
		} else {
			support.getProperties().remove("selectedAction");
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
	
	class ALabelProvider extends NavigatorLabelProvider {
		public String getText(Object element) {
			if(fTree == null || !(element instanceof XModelObject)) return super.getText(element);
			XModelObject o = (XModelObject)element;
			return ((ActionsTree)fTree).getPresentation(o);
		}		
	}

	public Point getMaximumSize() {
		return null;
	}

	public Point getMinimumSize() {
		return null;
	}

}
