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
package org.jboss.tools.jsf.ui.editor;

import org.jboss.tools.common.editor.AbstractSelectionProvider;
import org.jboss.tools.common.editor.ObjectMultiPageEditor;
import org.jboss.tools.common.editor.ObjectTextEditor;
import org.jboss.tools.common.model.ui.texteditors.XMLTextEditorComponent;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

import org.jboss.tools.common.gef.outline.xpl.DiagramContentOutlinePage;
import org.jboss.tools.jsf.messages.JSFUIMessages;
import org.jboss.tools.jsf.model.FacesConfigFilteredTreeConstraint;
import org.jboss.tools.jsf.model.JSFConstants;
import org.jboss.tools.jsf.project.JSFNature;
import org.jboss.tools.jsf.ui.IJSFHelpContextIds;
import org.jboss.tools.jsf.ui.JsfUiPlugin;
import org.jboss.tools.common.model.ui.editor.EditorDescriptor;

public class FacesConfigEditor extends ObjectMultiPageEditor {
	protected FacesConfigGuiEditor guiEditor;
	protected FacesConfigFilteredTreeConstraint constraint = new FacesConfigFilteredTreeConstraint();
	
	protected Composite createPageContainer(Composite parent) {
		Composite composite = super.createPageContainer(parent);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(parent, IJSFHelpContextIds.FACES_CONFIG_EDITOR);
		return composite;
	}
	
	public FacesConfigEditor() {
		constraint.setEditorEnvironment(true);
		FacesConfigFilteredTreeConstraint constraint2 = new FacesConfigFilteredTreeConstraint();
		outline.addFilter(constraint2);
	}

	protected boolean isWrongEntity(String entity) {
		return !entity.startsWith(JSFConstants.ENT_FACESCONFIG);
	}
	protected void doCreatePages() {
		if (isAppropriateNature() || true /* JBIDE-541 */) {
			createGuiPage(); 
			treeFormPage = createTreeFormPage();
			treeFormPage.setTitle(JSFUIMessages.FACES_CONFIG_EDITOR);
			treeFormPage.addFilter(constraint);
			treeFormPage.initialize(getModelObject());
			addFormPage(treeFormPage);
		}
		createTextPage();
		initEditors();
	}
	
	protected String[] getSupportedNatures() {
		return new String[]{JSFNature.NATURE_ID};
	}

	protected String getNatureWarningMessageKey() {
		return "SharableEditors.natureWarning.jsf.message"; //$NON-NLS-1$
	}

	protected void createGuiPage() {
		try{
			guiEditor = new FacesConfigGuiEditor();
			guiEditor.init(getEditorSite(), getEditorInput());
			int index = addPage(guiEditor, input);
			setPageText(index, JSFUIMessages.FacesConfigEditor_Diagram); 
			guiEditor.setInput(input);
			selectionProvider.setHost(guiEditor.getSelectionProvider());		
			guiEditor.addErrorSelectionListener(createErrorSelectionListener());
			selectionProvider.addHost("guiEditor", guiEditor.getSelectionProvider()); //$NON-NLS-1$
		} catch(PartInitException ex) {
			JsfUiPlugin.getPluginLog().logError(ex);
		}
	}
	
	protected ObjectTextEditor createTextEditor() {
		return new XMLTextEditorComponent();	
	}

	public void dispose() {
		super.dispose();
		if(guiEditor != null) {
			guiEditor.dispose();
			guiEditor = null;
		}
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
		if (treeFormPage != null) {
			treeFormPage.initialize(getModelObject());
			treeFormPage.setErrorMode(isErrorMode());
		}
	}
	
	protected int getGuiPageIndex() {
		return 0; 
	}
	
	public boolean isGuiEditorActive() {
		return getActivePage() == getGuiPageIndex();
	}
	
	protected void updateSelectionProvider() {
		if(guiEditor != null) selectionProvider.addHost("guiEditor", guiEditor.getSelectionProvider()); //$NON-NLS-1$
		if(textEditor != null) selectionProvider.addHost("textEditor", getTextSelectionProvider()); //$NON-NLS-1$
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
				selectionProvider.addHost("treeEditor", treeFormPage.getSelectionProvider(), true); //$NON-NLS-1$
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
		} else {
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
			return new EditorDescriptor("faces-config"); //$NON-NLS-1$

		if(adapter == FacesConfigEditor.class) return this;
		return super.getAdapter(adapter);
	}
	
}
