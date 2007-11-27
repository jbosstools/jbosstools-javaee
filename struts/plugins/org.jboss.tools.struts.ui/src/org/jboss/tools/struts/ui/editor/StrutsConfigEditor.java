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
package org.jboss.tools.struts.ui.editor;

import org.jboss.tools.common.editor.AbstractSelectionProvider;
import org.jboss.tools.common.editor.ObjectMultiPageEditor;
import org.jboss.tools.common.editor.ObjectTextEditor;
import org.jboss.tools.common.model.ui.texteditors.XMLTextEditorComponent;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

import org.jboss.tools.common.model.XFilteredTreeConstraint;
import org.jboss.tools.common.model.event.XModelTreeEvent;
import org.jboss.tools.common.model.event.XModelTreeListener;
import org.jboss.tools.common.model.options.PreferenceModelUtilities;
import org.jboss.tools.common.gef.outline.xpl.DiagramContentOutlinePage;
import org.jboss.tools.common.model.ui.editor.EditorDescriptor;
import org.jboss.tools.struts.StrutsConstants;
import org.jboss.tools.struts.StrutsPreference;
import org.jboss.tools.struts.StrutsProject;
import org.jboss.tools.struts.ui.StrutsUIPlugin;

public class StrutsConfigEditor extends ObjectMultiPageEditor {
	protected StrutsConfigGuiEditor guiEditor;
///	StrutsConfigPartListener partListener = new StrutsConfigPartListener();
	protected XFilteredTreeConstraint constraint = new org.jboss.tools.struts.model.FileSystemsTreeConstraint();
	
	public StrutsConfigEditor() {
///		constraint.setEditorEnvironment(true);
		XFilteredTreeConstraint constraint2 = new org.jboss.tools.struts.model.FileSystemsTreeConstraint();
		outline.addFilter(constraint2);
	}

	protected boolean isWrongEntity(String entity) {
		return !entity.startsWith(StrutsConstants.ENT_STRUTSCONFIG);
	}

	protected String[] getSupportedNatures() {
		return new String[]{StrutsProject.NATURE_ID};
	}
	
	protected String getNatureWarningMessageKey() {
		return "SharableEditors.natureWarning.struts.message";
	}

	protected void doCreatePages() {
		if (isAppropriateNature()) {
			if (!"yes".equals(StrutsPreference.DO_NOT_SHOW_DIAGRAM.getValue())) {
				createGuiPage(); 
			}
			treeFormPage = createTreeFormPage();
			treeFormPage.setTitle("Struts Config Editor");
			treeFormPage.addFilter(constraint);
			treeFormPage.initialize(getModelObject());
			addFormPage(treeFormPage);
			//createTreePage();
		}
		createTextPage();
		initEditors();
		initPreferenceListener();
		if(treeFormPage != null) selectionProvider.addHost("treeEditor", treeFormPage.getSelectionProvider());
///		if (e.getE()) getSite().getPage().addPartListener(partListener);
	}	
	
	
	
	protected void createGuiPage() {
//@S_CHECK@
		try{
			guiEditor = new StrutsConfigGuiEditor();
			guiEditor.init(getEditorSite(), getEditorInput());
			//Control control = guiEditor.createControl(getContainer());
			int index = addPage(guiEditor, input);
			setPageText(index, "Diagram"); 
			guiEditor.setInput(input);
			selectionProvider.setHost(guiEditor.getSelectionProvider());		
			guiEditor.addErrorSelectionListener(createErrorSelectionListener());
///			guiEditor.addFacesConfigGuiListener(new GL());
			selectionProvider.addHost("guiEditor", guiEditor.getSelectionProvider());
		}catch(Exception ex){
			StrutsUIPlugin.getPluginLog().logError(ex);
		}
	}
	
	protected ObjectTextEditor createTextEditor() {
		return new XMLTextEditorComponent();	
	}

	public void dispose() {
		if (input != null) {
			selectionProvider.setHost(null);
			
			try {
				getSite().setSelectionProvider(null);
			} catch (Exception e) {
				StrutsUIPlugin.getPluginLog().logError(e);
			}
			
			if (guiEditor != null) {
				guiEditor.dispose();
				guiEditor = null;
			}
		}
		
		if (optionListener != null) {
			PreferenceModelUtilities.getPreferenceModel()
					.removeModelTreeListener(optionListener);
			optionListener = null;
		}
		
		super.dispose();
	}
	
	protected void setErrorMode() {
		setNormalMode();
	}
	
	protected void setNormalMode() {
		if(guiEditor != null) {
			guiEditor.setObject(getModelObject(), isErrorMode());
			updateSelectionProvider();
		}
		if (treeEditor != null) {		
			treeEditor.setObject(object, isErrorMode());
		}
		if (treeFormPage!=null) { // AU added
			treeFormPage.initialize(getModelObject()); // AU added
			treeFormPage.setErrorMode(isErrorMode());
		} // AU added
	}
	
//	private XModelObject getInstalledObject() {
//		XModelObject o = getModelObject();
//		return (o == null) ? null : o.getChildByPath("process");
//	}

/*
	class GL implements StrutsConfigGuiListener {
		public void guiCreated() {
			if (e.getE()) {
				if(isGuiEditorActive()) 
					partListener.guiChanged(FacesConfigEditor.this);
			}
		}		
	}
*/	
	protected int getGuiPageIndex() {
		return 0; 
	}
	
	public boolean isGuiEditorActive() {
		return getActivePage() == getGuiPageIndex();
	}
	
	OptionListener optionListener = null;
    class OptionListener implements XModelTreeListener {
		public void nodeChanged(XModelTreeEvent event) {
			if(StrutsPreference.WEB_FLOW_DIAGRAM_PATH.equals(event.getModelObject().getPath())) {
				revalidateGuiPage();
				//outlinePage.revalidate();
				setActivePage(0);
				pageChange(0);
			}				
		}
		public void structureChanged(XModelTreeEvent event) {}
    }

    private void initPreferenceListener() {
		if(optionListener == null) {
			optionListener = new OptionListener();
			PreferenceModelUtilities.getPreferenceModel().addModelTreeListener(optionListener);
		}
    }
    
    protected void revalidateGuiPage() {
		boolean doNotShowDiagram = "yes".equals(StrutsPreference.DO_NOT_SHOW_DIAGRAM.getValue());
		if(doNotShowDiagram == (guiEditor == null)) return;
		if(doNotShowDiagram) {
			setActivePage(1);
			removePage(0);
			guiEditor.dispose();
			guiEditor = null;
		} else {
			if(getPageCount() > 0) {
				while(getPageCount() > 0) removePage(0);
				doCreatePages();
			} else {
				createGuiPage();
			}
		}
	}

	protected void updateSelectionProvider() {
		if(guiEditor != null) selectionProvider.addHost("guiEditor", guiEditor.getSelectionProvider());
		if(textEditor != null) selectionProvider.addHost("textEditor", getTextSelectionProvider());
		int index = getActivePage();
		if(index == getSourcePageIndex()) {
			if(textEditor != null) {
				selectionProvider.setHost(getTextSelectionProvider());
			}
			return;
		}
		if(index == 1 || guiEditor == null || guiEditor.getSelectionProvider() == null) {
			if (treeEditor != null) {
				selectionProvider.setHost(treeEditor.getSelectionProvider());
				treeEditor.fireEditorSelected();
			}
			if (treeFormPage != null) {
				selectionProvider.addHost("treeEditor", treeFormPage.getSelectionProvider(), true);
				//selectionProvider.setHost(treeFormPage.getSelectionProvider());
				//treeFormPage.getSelectionProvider();
			}
		} else {
			ISelectionProvider p = guiEditor.getSelectionProvider();
			selectionProvider.setHost(p);
			if(p instanceof AbstractSelectionProvider) {
				((AbstractSelectionProvider)p).fireSelectionChanged();
			}		
		}
	}
	
	protected void pageChange(int newPageIndex) {
		super.pageChange(newPageIndex);
		if(newPageIndex != getGuiPageIndex()) {
///			partListener.partDeactivated(StrutsConfigEditor.this);
		} else {
///			partListener.partActivated(StrutsConfigEditor.this);
		}
	}
	
	public Object getAdapter(Class adapter) {
		if(adapter == IContentOutlinePage.class){
			if(guiEditor == null || guiEditor.getGUI() == null) {
				return super.getAdapter(adapter);
			}
			Object o = guiEditor.getGUI().getAdapter(adapter);
			if(o instanceof DiagramContentOutlinePage) {
				DiagramContentOutlinePage g = (DiagramContentOutlinePage)o;
				g.setTreeOutline(outline);
			}
			return o;  
		}
		if(adapter == ActionRegistry.class || adapter == org.eclipse.gef.editparts.ZoomManager.class){
			 if(guiEditor != null)
			 	if(guiEditor.getGUI() != null)
			 		return guiEditor.getGUI().getAdapter(adapter);
		}
		if (adapter == EditorDescriptor.class)
			return new EditorDescriptor("faces-config");

		if(adapter == StrutsConfigEditor.class) return this;
		return super.getAdapter(adapter);
	}
}
