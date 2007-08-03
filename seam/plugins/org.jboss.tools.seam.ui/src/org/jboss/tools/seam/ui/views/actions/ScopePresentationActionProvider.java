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
package org.jboss.tools.seam.ui.views.actions;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.jboss.tools.seam.core.SeamPreferences;

/**
 * Action provider for Seam Components view.
 * @author Viacheslav Kabanovich
 */
public class ScopePresentationActionProvider extends CommonActionProvider {
	public static String SCOPE_PRESENTATION = "seam.scopePresentation";
	
	public static boolean isScopePresentedAsLabel() {
		String s = SeamPreferences.getInstancePreference(SCOPE_PRESENTATION);
		return "label".equals(s);
	}
	
	public void setScopePresentedAsLabel(boolean s) {
		IEclipsePreferences p = SeamPreferences.getInstancePreferences();
		p.put(SCOPE_PRESENTATION, s ? "label" : "node");
	}
	
	public ScopePresentationActionProvider() {}

    public void fillContextMenu(IMenuManager menu) {
    }

    public void fillActionBars(IActionBars actionBars) {
    	if(scopePresentation == null) {
    		scopePresentation = new ScopePresentationContribution();
			IMenuManager menuManager = actionBars.getMenuManager();
			String SEP_NAME = "aaa";
	        if(menuManager.find(IWorkbenchActionConstants.MB_ADDITIONS) != null) 
	        	menuManager.insertAfter(IWorkbenchActionConstants.MB_ADDITIONS, new Separator(SEP_NAME));
	        else
	        	menuManager.add(new Separator(SEP_NAME));
	        menuManager.appendToGroup(SEP_NAME, scopePresentation);
	        actionBars.updateActionBars();
    	}
    }

    ScopePresentationContribution scopePresentation = null;
    boolean scopeAsNode = !isScopePresentedAsLabel();
    
    class ScopePresentationContribution extends ContributionItem {
    	boolean filled = false;
    	public void fill(Menu menu, int index) {
    		if(filled) return;
    		filled = true;
    		MenuItem item = new MenuItem(menu, SWT.CASCADE);
    		item.setText("Scope Presentation");
    		Menu smenu = new Menu(item);
    		item.setMenu(smenu);
    		final MenuItem item1 = new MenuItem(smenu, SWT.RADIO);
    		item1.setText("As node");
    		item1.setSelection(scopeAsNode);
    		item1.addSelectionListener(new SelectionListener() {
				public void widgetDefaultSelected(SelectionEvent e) {
					widgetSelected(e);
				}
				public void widgetSelected(SelectionEvent e) {
					if(item1.getSelection() && !scopeAsNode) {
						scopeAsNode = true;
						setUpViewer();
					}
				}
    			
    		});
    		final MenuItem item2 = new MenuItem(smenu, SWT.RADIO);
    		item2.setText("As label");
    		item2.setSelection(!scopeAsNode);
    		item2.addSelectionListener(new SelectionListener() {
				public void widgetDefaultSelected(SelectionEvent e) {
					widgetSelected(e);
				}
				public void widgetSelected(SelectionEvent e) {
					if(item2.getSelection() && scopeAsNode) {
						scopeAsNode = false;
						setUpViewer();
					}
				}
    			
    		});
    	}
    	
    }
    
    public void dispose() {
    	scopePresentation = null;
    	super.dispose();
    }

	void setUpViewer() {
		setScopePresentedAsLabel(!scopeAsNode);
		BusyIndicator.showWhile(PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getShell().getDisplay(),
			new Runnable() {
				public void run() {
					try {
						getActionSite().getStructuredViewer().refresh();
					} catch (Exception e2) {
						//ignore
					}
				}
			}
		);
	}

}
