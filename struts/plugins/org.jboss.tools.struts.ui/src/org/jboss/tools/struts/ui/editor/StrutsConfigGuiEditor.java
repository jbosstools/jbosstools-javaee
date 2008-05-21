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

import org.eclipse.swt.layout.GridData;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.jface.viewers.*;
import org.jboss.tools.common.editor.*;
import org.eclipse.gef.ui.actions.ActionRegistry;

import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.ui.editor.IModelObjectEditorInput;
import org.jboss.tools.struts.StrutsConstants;
import org.jboss.tools.struts.ui.StrutsUIPlugin;
import org.jboss.tools.struts.ui.editor.model.impl.StrutsModel;
import org.jboss.tools.jst.web.model.WebProcess;

public class StrutsConfigGuiEditor extends AbstractSectionEditor {
    private StrutsEditor gui = null;
	private IModelObjectEditorInput input;
	private boolean isInitialized = false;
	private XModelObject installedProcess = null;
	private StrutsModel model;

    public StrutsEditor getGUI(){
    	return gui;
    }

	protected boolean isWrongEntity(String entity) {
		return !entity.startsWith(StrutsConstants.ENT_STRUTSCONFIG);
	}
	
	public void setInput(IEditorInput input) {
		super.setInput(input);
		this.input = (IModelObjectEditorInput)input;
	}
	
	public ISelectionProvider getSelectionProvider() {
		return (gui == null) ? null : gui.getModelSelectionProvider();
	}

	protected XModelObject getInstalledObject() {
		return installedProcess;
	}
	
	private WebProcess findInstalledObject() {
		XModelObject o1 = input.getXModelObject();
		if(o1 == null) return null;
		XModelObject c = o1.getChildByPath("process");
		if(!(c instanceof WebProcess)) return null;
		WebProcess f = (WebProcess)c;
		return (!f.isPrepared()) ? null : f;
	}
	
	protected void updateGui() {
		WebProcess f = findInstalledObject();
		if(f != installedProcess) disposeGui();
		else if(isInitialized) return;
		isInitialized = true;
		installedProcess = f;
		guiControl.setVisible(f != null);
		if(f == null) return;
		try {
			f.autolayout();
            gui = new StrutsEditor(input);
            model = new StrutsModel(f.getParent());
            model.updateLinks();

			gui.setStrutsModel(model);

			gui.init((IEditorSite)getSite(), (IEditorInput)input);
			gui.createPartControl(guiControl);
			control = guiControl.getChildren()[0];
			control.setLayoutData(new GridData(GridData.FILL_BOTH));
			guiControl.layout();
			wrapper.update();
			wrapper.layout();
		} catch (Exception ex) {
			StrutsUIPlugin.getPluginLog().logError(ex);
		}
	}
	
	public void dispose() {
		if(model == null) return; 
		model.disconnectFromModel();
		model = null;
		disposeGui();
		gui = null;
		input = null;
		installedProcess = null;
		super.dispose();
	}
	
	public Object getAdapter(Class adapter) {
		if(adapter == ActionRegistry.class || adapter == org.eclipse.gef.editparts.ZoomManager.class){
				if(getGUI() != null)
					return getGUI().getAdapter(adapter);
		}
		return super.getAdapter(adapter);
	}
	
}
