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

import org.eclipse.swt.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.Point;

import org.jboss.tools.common.model.*;
import org.jboss.tools.common.meta.action.impl.*;
import org.jboss.tools.common.model.ui.wizards.special.*;

public class TabbedStep extends AbstractSpecialWizardStep {
	protected Composite panel;
	static String TAB = "selectedTab";
	protected XModelObject process;
	protected AbstractSpecialWizardStep[] tabs = createTabs();
	protected TabFolder tabbedpane = null;
	protected boolean lock = false;

	protected AbstractSpecialWizardStep[] createTabs() {
		return new AbstractSpecialWizardStep[0];
	}

	public void dispose() {
		if (tabs!=null) {
			for (int i=0;i<tabs.length;++i) {
				if (tabs[i]!=null) tabs[i].dispose();
			}
			tabs = null;
		}
	}
	
	protected String getTabName(int i) {
		return "";
	}

	public void setSupport(SpecialWizardSupport support, int i) {
		super.setSupport(support, i);
		for (int q = 0; q < tabs.length; q++) {
			tabs[q].setSupport(support, i);
			tabs[q].setWizard(wizard);
		} 
	}
	
	public void save() {
		int is = tabbedpane.getSelectionIndex();
		if(is < 0) return;
		String sn = getTabName(is);
		for (int i = 0; i < tabs.length; i++) {
			String tn = getTabName(i);
			if(!tn.equals(sn)) continue;
			support.getProperties().setProperty("selectedTab", tn);
			tabs[i].save();
		}
	}
	
	public Control createControl(Composite parent) {
		panel = new Composite(parent, SWT.NONE);
		panel.setLayout(new GridLayout());
		panel.setLayoutData(new GridData(GridData.FILL_BOTH));
		if(tabbedpane != null) tabbedpane.dispose();
		tabbedpane = new TabFolder(panel, SWT.NONE);
		tabbedpane.addSelectionListener(new TCL());
		tabbedpane.setLayoutData(new GridData(GridData.FILL_BOTH));
		update0();
		panel.pack();
		parent.layout();
		panel.layout();
		return panel;
	}

	public void update0() {
		process = (XModelObject)support.getTarget();
		lock = true;
		for (int i = 0; i < tabs.length; i++) {
			TabItem item = new TabItem(tabbedpane, SWT.NONE);
			Control c = tabs[i].createControl(tabbedpane);
			item.setControl(c);
			item.setText(getTabName(i));
		}			 
		for (int i = 0; i < tabs.length; i++) tabs[i].update();
		String tab = support.getProperties().getProperty(TAB);
		if(tab == null) support.getProperties().setProperty(TAB, tab = getTabName(0));
		for (int i = 0; i < tabs.length; i++)
		  if(getTabName(i).equals(tab)) {
		  	tabbedpane.setSelection(i);
		  }
		  	 
		lock = false;
	}
	
	class TCL extends SelectionAdapter {
		public void widgetSelected(SelectionEvent e) {
			if(lock) return;
			support.getProperties().setProperty("selectedTab", getTabName(tabbedpane.getSelectionIndex()));
//			if(listener != null) listener.propertyChange(null);
		}
	}
	
	public Point getMaximumSize() {
		return null;
	}

	public Point getMinimumSize() {
		return null;
	}

}
