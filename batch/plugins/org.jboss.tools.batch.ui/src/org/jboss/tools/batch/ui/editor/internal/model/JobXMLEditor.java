/*************************************************************************************
 * Copyright (c) 2014 Red Hat, Inc. and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     JBoss by Red Hat - Initial implementation.
 *     Tomas Milata - Added Batch diagram editor (JBIDE-19717).
 ************************************************************************************/
package org.jboss.tools.batch.ui.editor.internal.model;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.gef.ui.views.palette.PaletteView;
import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.LoggingService;
import org.eclipse.sapphire.Sapphire;
import org.eclipse.sapphire.modeling.xml.RootXmlResource;
import org.eclipse.sapphire.ui.SapphireEditor;
import org.eclipse.sapphire.ui.def.DefinitionLoader;
import org.eclipse.sapphire.ui.forms.swt.MasterDetailsEditorPage;
import org.eclipse.sapphire.ui.swt.gef.SapphireDiagramEditor;
import org.eclipse.sapphire.ui.swt.xml.editor.XmlEditorResourceStore;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.sse.ui.StructuredTextEditor;
import org.jboss.tools.common.text.ext.IMultiPageEditor;

public class JobXMLEditor extends SapphireEditor implements IMultiPageEditor {

	public static final int DESIGN_PAGE_INDEX = 0;
	public static final int DIAGRAM_PAGE_INDEX = 1;

	private Job model;
	private FlowElementsContainer currentDiagramModel;

	private StructuredTextEditor schemaSourceEditor;
	private SapphireDiagramEditor schemaDiagram;
	private MasterDetailsEditorPage design;

	@Override
	protected void createSourcePages() throws PartInitException {
		this.schemaSourceEditor = new StructuredTextEditor();
		this.schemaSourceEditor.setEditorPart(this);
		int index = addPage(this.schemaSourceEditor, getEditorInput());
		setPageText(index, "Source");
	}

	@Override
	protected Element createModel() {
		this.model = Job.TYPE
				.instantiate(new RootXmlResource(new XmlEditorResourceStore(this, this.schemaSourceEditor)));
		this.currentDiagramModel = this.model;
		return this.model;
	}

	@Override
	protected void createDiagramPages() throws PartInitException {
		this.schemaDiagram = new SapphireDiagramEditor(this, this.currentDiagramModel,
				DefinitionLoader.sdef(getClass()).page("DiagramPage"));
		addEditorPage(DIAGRAM_PAGE_INDEX, this.schemaDiagram);
		preferFlyoutPalette();
	}

	@Override
	protected void createFormPages() throws PartInitException {
		createModel();
		this.design = new MasterDetailsEditorPage(this, this.model, DefinitionLoader.sdef(getClass()).page("design"));
		addPage(DESIGN_PAGE_INDEX, this.design);
	}

	public Job getSchema() {
		return this.model;
	}

	@Override
	public void doSave(final IProgressMonitor monitor) {
		this.schemaDiagram.doSave(monitor);
		super.doSave(monitor);
	}

	public StructuredTextEditor getSourceEditor() {
		return schemaSourceEditor;
	}

	public MasterDetailsEditorPage getFormEditor() {
		return design;
	}

	public void changeDiagramContent(FlowElementsContainer newRoot) {
		this.currentDiagramModel = newRoot;

		try {
			createDiagramPages();
		} catch (PartInitException e) {
			Sapphire.service(LoggingService.class).log(e);
		}
		removePage(JobXMLEditor.DIAGRAM_PAGE_INDEX + 1);
		setActiveEditor(this.schemaDiagram);
	}

	/**
	 * This is a workaround that ensures that the flyout pallete (inner
	 * composite inside the diagram editor) is used instead of a separate
	 * pallete view. The separate pallete view does not work with the diagram
	 * editor currently.
	 */
	private void preferFlyoutPalette() {
		IWorkbenchPage workbenchPage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		IViewPart paletteView = workbenchPage.findView(PaletteView.ID);
		// closes the view which activates the flyout composite
		workbenchPage.hideView(paletteView);
	}
}
