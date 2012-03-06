/*******************************************************************************
 * Copyright (c) 2007 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.seam.ui.views.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.actions.ActionGroup;
import org.eclipse.ui.navigator.IExtensionStateModel;
import org.jboss.tools.seam.core.SeamCoreMessages;
import org.jboss.tools.seam.ui.views.ViewConstants;

/**
 * @author Viacheslav Kabanovich
 */
public class SeamViewLayoutActionGroup extends ActionGroup implements ViewConstants {
	public static final String LAYOUT_GROUP_NAME = "layout"; //$NON-NLS-1$
	StructuredViewer structuredViewer;
	IExtensionStateModel stateModel;
	private boolean hasContributedToViewMenu = false;

	private IAction hierarchicalLayoutAction = null;
	private IAction flatLayoutAction = null;
	
	private IAction labelScopeAction = null;
	private IAction nodeScopeAction = null;

	private IMenuManager layoutSubMenu;
	private IMenuManager scopeSubMenu;
	
	private MenuItem hierarchicalLayoutItem = null;
	private MenuItem flatLayoutItem = null;
	private MenuItem labelScopeItem = null;
	private MenuItem nodeScopeItem = null;

	public SeamViewLayoutActionGroup(StructuredViewer structuredViewer,
			IExtensionStateModel stateModel) {
		this.structuredViewer = structuredViewer;
		this.stateModel = stateModel;
	}

	private class CommonLayoutAction extends Action implements IAction {

		private String property;
		private final boolean value;

		public CommonLayoutAction(String property, boolean value, String id) {
			super("", AS_RADIO_BUTTON); //$NON-NLS-1$
			this.property = property;
			this.value = value;
			this.setId(id);
		}

		@Override
		public void run() {
			if (stateModel.getBooleanProperty(property) != value) {
				stateModel.setBooleanProperty(property, value);
				structuredViewer.getControl().setRedraw(false);
				try {
					structuredViewer.refresh();
				} finally {
					structuredViewer.getControl().setRedraw(true);
				}
			}
		}
	}

	@Override
	public void fillActionBars(IActionBars actionBars) {
		if (!hasContributedToViewMenu) {

			IMenuManager viewMenu = actionBars.getMenuManager();
			// Create layout sub menu
			if (layoutSubMenu == null) {
				layoutSubMenu = new MenuManager(SeamCoreMessages.SEAM_VIEW_LAYOUT_ACTION_GROUP_SEAM_PACKAGES, SeamCoreMessages.SEAM_VIEW_LAYOUT_ACTION_GROUP_SEAM_PACKAGES);
				addLayoutActions(layoutSubMenu);
				viewMenu.insertAfter(IWorkbenchActionConstants.MB_ADDITIONS, new Separator(LAYOUT_GROUP_NAME));
			}
			
			if(scopeSubMenu == null) {
				scopeSubMenu = new MenuManager(SeamCoreMessages.SEAM_VIEW_LAYOUT_ACTION_GROUP_SCOPE_PRESENTATION, SeamCoreMessages.SEAM_VIEW_LAYOUT_ACTION_GROUP_SCOPE_PRESENTATION);
				addScopeActions(scopeSubMenu);
			}

			viewMenu.appendToGroup(LAYOUT_GROUP_NAME, layoutSubMenu);
			viewMenu.appendToGroup(LAYOUT_GROUP_NAME, scopeSubMenu);

			hasContributedToViewMenu = true;
		}
	}

	public void unfillActionBars(IActionBars actionBars) {
		if (hasContributedToViewMenu) {
			// Create layout sub menu
			if (layoutSubMenu != null) {
				actionBars.getMenuManager().remove(layoutSubMenu);
				layoutSubMenu.dispose();
				layoutSubMenu = null;
			}

			if (scopeSubMenu != null) {
				actionBars.getMenuManager().remove(scopeSubMenu);
				scopeSubMenu.dispose();
				scopeSubMenu = null;
			}

			hasContributedToViewMenu = false;
		}
	}

	boolean isFlatLayout = true;
	boolean isScopeLable = false;

	void setFlatLayout(boolean b) {
		isFlatLayout = b;
		if(flatLayoutAction == null) {
			createActions();
			flatLayoutAction.setChecked(b);
			hierarchicalLayoutAction.setChecked(!b);
		}
	}
	
	void setScopeLable(boolean b) {
		isScopeLable = b;
		if(labelScopeAction == null) {
			createActions();
			labelScopeAction.setChecked(b);
			nodeScopeAction.setChecked(!b);
		}
	}

	private void createActions() {
		flatLayoutAction = new CommonLayoutAction(PACKAGE_STRUCTURE, true, "package.flat"); //$NON-NLS-1$
		hierarchicalLayoutAction = new CommonLayoutAction(PACKAGE_STRUCTURE, false,"package.hierarchical"); //$NON-NLS-1$
		labelScopeAction = new CommonLayoutAction(SCOPE_PRESENTATION, true,"layout.label"); //$NON-NLS-1$
		nodeScopeAction = new CommonLayoutAction(SCOPE_PRESENTATION, false,"layout.node"); //$NON-NLS-1$
	}

	protected void addLayoutActions(IMenuManager viewMenu) {
		viewMenu.add(new SeamContributionItem(flatLayoutAction) {
			@Override
			public void fill(Menu menu, int index) {
				int style = SWT.RADIO;
				MenuItem mi = new MenuItem(menu, style, index);
				flatLayoutItem = mi;
				mi.setText(SeamCoreMessages.SEAM_VIEW_LAYOUT_ACTION_GROUP_FLAT);
				mi.setSelection(isFlatLayout);
				mi.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						if (isFlatLayout) {
							flatLayoutItem.setSelection(true);
							return;
						}
						flatLayoutAction.run();
						
						hierarchicalLayoutItem.setSelection(false);
						flatLayoutItem.setSelection(true);
						isFlatLayout = true;
						e.doit = false;
					}
				});
			}
			@Override
			public boolean isDynamic() {
				return false;
			}
		});

		viewMenu.add(new SeamContributionItem(hierarchicalLayoutAction) {
			@Override
			public void fill(Menu menu, int index) {
				int style = SWT.RADIO;
				MenuItem mi = new MenuItem(menu, style, index);
				hierarchicalLayoutItem = mi;
				mi.setText(SeamCoreMessages.SEAM_VIEW_LAYOUT_ACTION_GROUP_HIERARCHICAL);
				mi.setSelection(!isFlatLayout);
				mi.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						if (!isFlatLayout) {
							hierarchicalLayoutItem.setSelection(true);
							return;
						}
						hierarchicalLayoutAction.run();
						
						flatLayoutItem.setSelection(false);
						hierarchicalLayoutItem.setSelection(true);
						isFlatLayout = false;
						e.doit = false;
					}
				});
			}
			@Override
			public boolean isDynamic() {
				return false;
			}
		});

	}

	protected void addScopeActions(IMenuManager viewMenu) {
		viewMenu.add(new SeamContributionItem(labelScopeAction) {
			@Override
			public void fill(Menu menu, int index) {
				int style = SWT.RADIO;
				MenuItem mi = new MenuItem(menu, style, index);
				labelScopeItem = mi;
				mi.setText(SeamCoreMessages.SEAM_VIEW_LAYOUT_ACTION_GROUP_LABEL);
				mi.setSelection(isScopeLable);
				mi.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						if (isScopeLable) {
							labelScopeItem.setSelection(true);
							return;
						}
						labelScopeAction.run();
						
						nodeScopeItem.setSelection(false);
						labelScopeItem.setSelection(true);
						isScopeLable = true;
						e.doit = false;
					}
				});
			}
			@Override
			public boolean isDynamic() {
				return false;
			}
		});

		viewMenu.add(new SeamContributionItem(nodeScopeAction) {
			@Override
			public void fill(Menu menu, int index) {
				int style = SWT.RADIO;
				MenuItem mi = new MenuItem(menu, style, index);
				nodeScopeItem = mi;
				mi.setText(SeamCoreMessages.SEAM_VIEW_LAYOUT_ACTION_GROUP_NODE);
				mi.setSelection(!isScopeLable);
				mi.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						if (!isScopeLable) {
							nodeScopeItem.setSelection(true);
							return;
						}
						nodeScopeAction.run();
						
						labelScopeItem.setSelection(false);
						nodeScopeItem.setSelection(true);
						isScopeLable = false;
						e.doit = false;
					}
				});
			}
			@Override
			public boolean isDynamic() {
				return false;
			}
		});

	}
	public class SeamContributionItem extends ContributionItem{
		IAction action;
		
		public SeamContributionItem(IAction action){
			super(action.getId());
			this.action = action;
		}
		
		public IAction getAction(){
			return action;
		}
	}
}
