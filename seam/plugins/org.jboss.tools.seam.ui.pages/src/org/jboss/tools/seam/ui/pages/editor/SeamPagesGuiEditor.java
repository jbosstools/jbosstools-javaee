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
package org.jboss.tools.seam.ui.pages.editor;

import java.util.HashMap;
import java.util.Map;

import org.jboss.tools.common.editor.AbstractSectionEditor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.layout.GridData;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.ui.editor.IModelObjectEditorInput;
import org.jboss.tools.jst.web.model.WebProcess;
import org.jboss.tools.seam.pages.xml.model.SeamPagesConstants;
import org.jboss.tools.seam.pages.xml.model.helpers.SeamPagesProcessStructureHelper;
import org.jboss.tools.seam.pages.xml.model.impl.SeamPagesProcessImpl;
import org.jboss.tools.seam.ui.pages.SeamUiPagesPlugin;
import org.jboss.tools.seam.ui.pages.editor.ecore.pages.Link;
import org.jboss.tools.seam.ui.pages.editor.ecore.pages.Page;
import org.jboss.tools.seam.ui.pages.editor.ecore.pages.PagesElement;
import org.jboss.tools.seam.ui.pages.editor.ecore.pages.PagesFactory;
import org.jboss.tools.seam.ui.pages.editor.ecore.pages.PagesModel;
import org.jboss.tools.seam.ui.pages.editor.ecore.pages.PgException;

public class SeamPagesGuiEditor extends AbstractSectionEditor {
    private PagesEditor gui = null;
	private IModelObjectEditorInput input;
	private boolean isInitialized = false;
	private XModelObject installedProcess = null;
	private PagesModel model;

	public void dispose() {
		if(model == null) return; 
//		model.dispose();
		model = null;
		gui.dispose();
		disposeGui();
		gui = null;
		input = null;
		installedProcess = null;
		super.dispose();
	}
	
    public PagesEditor getGUI(){
    	return gui;
    }

	protected boolean isWrongEntity(String entity) {
		return !entity.startsWith(SeamPagesConstants.ENT_FILE_SEAM_PAGES);
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
            gui = new PagesEditor(input);
            model = createModel(); //getFakeModel();
            gui.setPagesModel(model);

			gui.init((IEditorSite)getSite(), (IEditorInput)input);
			gui.createPartControl(guiControl);
			control = guiControl.getChildren()[0];
			control.setLayoutData(new GridData(GridData.FILL_BOTH));
			guiControl.layout();
			wrapper.update();
			wrapper.layout();
			
			//TODO remove
			if(false) throw new CoreException(null);
		} catch (CoreException ex) {
			SeamUiPagesPlugin.getDefault().logError(ex);
		}
	}
	
	private PagesModel getFakeModel(){
		PagesModel model = PagesFactory.eINSTANCE.createPagesModel();
		Page page = PagesFactory.eINSTANCE.createPage();
		page.setName("page1");
		page.setLocation(new Point(10,10));
		page.setSize(new Dimension(100,100));
		model.getChildren().add(page);
		return model;
	}

	private PagesModel createModel() {
		PagesModel model = PagesFactory.eINSTANCE.createPagesModel();
		Map<XModelObject, PagesElement> elements = new HashMap<XModelObject, PagesElement>();
		SeamPagesProcessStructureHelper h = SeamPagesProcessStructureHelper.getInstance();
		XModelObject[] is = h.getItems(installedProcess);
		for (int i = 0; i < is.length; i++) {
			String type = is[i].getAttributeValue(SeamPagesConstants.ATTR_TYPE);
			if(SeamPagesConstants.TYPE_PAGE.equals(type)) {
				Page page = PagesFactory.eINSTANCE.createPage();
				page.setName(h.getPageTitle(is[i]));
				int[] shape = h.asIntArray(is[i], "shape");
				if(shape != null && shape.length >= 2) {
					page.setLocation(new Point(shape[0],shape[1]));
				}
				if(shape != null && shape.length >= 4) {
					page.setSize(new Dimension(shape[2],shape[3]));
				}
				//TODO pass is[i] to page
				model.getChildren().add(page);
				elements.put(is[i], page);
			} else if(SeamPagesConstants.TYPE_EXCEPTION.equals(type)) {
				PgException exc = PagesFactory.eINSTANCE.createPgException();
				exc.setName(h.getPageTitle(is[i]));
				int[] shape = h.asIntArray(is[i], "shape");
				if(shape != null && shape.length >= 2) {
					exc.setLocation(new Point(shape[0],shape[1]));
				}
				if(shape != null && shape.length >= 4) {
					exc.setSize(new Dimension(shape[2],shape[3]));
				}
				//TODO pass is[i] to exc
				model.getChildren().add(exc);
				//maybe we need other map for exceptions?
				elements.put(is[i], exc);
			} else {
				//TODO
			}
		}

		for (int i = 0; i < is.length; i++) {
			String type = is[i].getAttributeValue(SeamPagesConstants.ATTR_TYPE);
			if(SeamPagesConstants.TYPE_PAGE.equals(type)
				|| SeamPagesConstants.TYPE_EXCEPTION.equals(type)) {
				PagesElement from = elements.get(is[i]);
				if(from == null) {
					//TODO report failure
					continue;
				}
				XModelObject[] os = h.getOutputs(is[i]);
				for (int j = 0; j < os.length; j++) {
					XModelObject t = h.getItemOutputTarget(os[j]);
					if(t == null) {
						//TODO report failure
						continue;
					}
					PagesElement to = elements.get(t);
					if(to == null) {
						//TODO report failure
						continue;
					}
					Link link = PagesFactory.eINSTANCE.createLink();
					link.setFromElement(from);
					link.setToElement(to);
					link.setName(h.getItemOutputPresentation(os[j]));
					link.setShortcut(h.isShortcut(os[j]));
				}
			}
		}

		return model;
	}

	public Object getAdapter(Class adapter) {
		if(adapter == ActionRegistry.class || adapter == org.eclipse.gef.editparts.ZoomManager.class){
				if(getGUI() != null)
					return getGUI().getAdapter(adapter);
		}
		return super.getAdapter(adapter);
	}
	
	public String getTitle() {
		return "Diagram";
	}

}
