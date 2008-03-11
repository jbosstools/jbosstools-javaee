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
package org.jboss.tools.struts.validator.ui.formset;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.widgets.*;
import org.jboss.tools.struts.validator.ui.formset.model.FModel;
import org.eclipse.jface.viewers.TreeViewer;

public class MenuInvoker {
	TreeViewer viewer;
	FormsetsBar bar = null;
	
	public MenuInvoker() {}
	
	public void setViewer(TreeViewer viewer) {
		this.viewer = viewer;
		viewer.getTree().addMouseListener(new M());
	}
	
	public void setBar(FormsetsBar bar) {
		this.bar = bar;
	}
	
	class M extends MouseAdapter {
		public void mouseUp(MouseEvent e) {
			if(e.button == 3) {
				Menu menu = createMenu(viewer.getControl());
				if(menu.getItemCount() == 0) return;
				menu.setVisible(true);
			} 
		}
	}
	
	Menu createMenu(Control parent) {
		Menu menu = new Menu(parent);
		fillMenu(menu);
		return menu;		
	}
	
	private void fillMenu(Menu menu) {
		String[] commands = bar.getMenu();
		MenuItem item;
		for (int i = 0; i < commands.length; i++) {
			if(FormsetsBar.SEPARATOR.equals(commands[i])) {
				item = new MenuItem(menu, SWT.SEPARATOR);
			} else {
				item = new MenuItem(menu, SWT.CASCADE);
				item.setText(commands[i]);
				item.addSelectionListener(new S(commands[i]));
			}
		}
	}
	
	class S extends SelectionAdapter {
		String command;
		public S(String command) {
			this.command = command;
		}
		public void widgetSelected(SelectionEvent e) {
			bar.action(command);
		}
	}
	
	public FModel getSelectedObject() {
		return getSelectedObject((TreeViewer)viewer);
	}
	
	public static FModel getSelectedObject(TreeViewer viewer) {
		TreeItem[] ti = viewer.getTree().getSelection();
		return (ti == null || ti.length == 0) ? null : getFModelByItem(ti[0]);
	}
	
	public static FModel getFModelByItem(TreeItem item) {
		if(item == null) return null;
		Object data = item.getData();
		return (!(data instanceof FModel)) ? null : (FModel)data;
	}

}
