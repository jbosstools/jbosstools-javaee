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
package org.jboss.tools.jsf.ui.preferences;

import org.jboss.tools.common.model.ui.objecteditor.XChildrenEditor;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import org.eclipse.ui.*;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.engines.impl.EnginesLoader;
import org.jboss.tools.common.model.util.AbstractTableHelper;
import org.jboss.tools.jsf.messages.JSFUIMessages;
import org.jboss.tools.jsf.project.capabilities.JSFCapabilities;

public class JSFCapabilitiesPreferencesPage extends PreferencePage implements IWorkbenchPreferencePage {
	static String[] CAPABILITIES_HEADER = new String[]{"name"};
	static String[] FILE_ADDITIONS_HEADER = new String[]{"label"};
	static String[] LIB_REFERENCE_HEADER = new String[]{"name"};
	JSFCapabilities capabilities;
	XModelObject copy;
	
	XChildrenEditor capabilityList;
	XChildrenEditor fileAdditionList;
	XChildrenEditor librarySetList;
	
	public JSFCapabilitiesPreferencesPage() {
		noDefaultAndApplyButton();
	}

	public void init(IWorkbench workbench) {
		capabilities = JSFCapabilities.getInstance();
		copy = capabilities.copy();
	}

	protected Control createContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(1, false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		composite.setLayout(layout);
		createCapabilityList();
		
		Control c = capabilityList.createControl(composite);
		c.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		createLibrarySetList();
		createFileAdditionList();
		
		TabFolder folder = new TabFolder(composite, SWT.NONE);
		folder.setLayoutData(new GridData(GridData.FILL_BOTH));

		TabItem item = new TabItem(folder, SWT.NONE);
		item.setText(JSFUIMessages.LIBRARY_SETS);
		c = librarySetList.createControl(folder);
		c.setLayoutData(new GridData(GridData.FILL_BOTH));
		item.setControl(c);
		
		item = new TabItem(folder, SWT.NONE);
		item.setText(JSFUIMessages.CONFIGURATION_FILE_ADDITIONS);
		c = fileAdditionList.createControl(folder);
		c.setLayoutData(new GridData(GridData.FILL_BOTH));
		item.setControl(c);

		initSelection();
		return composite;
	}
	
	private void initSelection() {
		if(copy == null || copy.getChildren().length == 0) return;
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				capabilityList.getSelectionProvider().setSelection(new StructuredSelection(copy.getChildren()[0]));
				capabilityList.update();
				capabilitySelectionChanged();
			}
		});
	}
	
	private void createCapabilityList() {
		capabilityList = new XChildrenEditor() {
			protected String getAddActionPath() {
				return "CreateActions.AddCapability";
			}
			protected AbstractTableHelper createHelper() {
				return new AbstractTableHelper() {
				    public String[] getHeader() {
				        return CAPABILITIES_HEADER;
				    }
				};
			}
		};
		capabilityList.setObject(copy);
		capabilityList.getSelectionProvider().addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				capabilitySelectionChanged();
			}
		});
		capabilityList.setHeaderVisible(false);
	}

	private void createLibrarySetList() {
		librarySetList = new XChildrenEditor() {
			protected String getAddActionPath() {
				return "CreateActions.AddLibraryReference";
			}
			protected void edit() {
				XModelObject o = helper.getModelObject(xtable.getSelectionIndex());
				if(o != null) callAction(o, "EditActions.Edit");
			}			
			protected AbstractTableHelper createHelper() {
				return new AbstractTableHelper() {
				    public int size() {
				        return (object == null) ? 0 : object.getChildren("JSFLibraryReference").length;
				    }
				    public XModelObject getModelObject(int r) {
				        if(object == null) return null;
				        XModelObject[] cs = object.getChildren("JSFLibraryReference");
				        return (r < 0 || r >= cs.length) ? null : cs[r];
				    }
				    public String[] getHeader() {
				        return LIB_REFERENCE_HEADER;
				    }
				};
			}
		};
		librarySetList.setHeaderVisible(false);
	}

	private void createFileAdditionList() {
		fileAdditionList = new XChildrenEditor() {
			protected String getAddActionPath() {
				return "CreateActions.AddFileAddition";
			}
			protected void edit() {
				XModelObject o = helper.getModelObject(xtable.getSelectionIndex());
				if(o != null) callAction(o, "EditActions.Edit");
			}			
			protected AbstractTableHelper createHelper() {
				return new AbstractTableHelper() {
				    public int size() {
				        return (object == null) ? 0 : object.getChildren("JSFFileAddition").length;
				    }

				    public XModelObject getModelObject(int r) {
				        if(object == null) return null;
				        XModelObject[] cs = object.getChildren("JSFFileAddition");
				        return (r < 0 || r >= cs.length) ? null : cs[r];
				    }

				    public String[] getHeader() {
				        return FILE_ADDITIONS_HEADER;
				    }
				};
			}
		};
		fileAdditionList.setHeaderVisible(false);
	}

	private void capabilitySelectionChanged() {
		XModelObject selection = capabilityList.getSelectedObject();
		librarySetList.setObject(selection);
		librarySetList.update();
		fileAdditionList.setObject(selection);
		fileAdditionList.update();
	}
	
	public boolean performCancel() {
		copy = capabilities.copy();
		return super.performCancel();
	}

    public boolean performOk() {
    	long ts = capabilities.getTimeStamp();
    	EnginesLoader.merge(capabilities, copy);
    	if(ts != capabilities.getTimeStamp()) {
    		capabilities.setModified(ts != capabilities.getTimeStamp());
    		capabilities.save();
    	}
        return super.performOk();
    }

}

