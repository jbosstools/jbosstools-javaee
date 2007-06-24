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

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;
import java.util.Vector;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.draw2d.ConnectionLayer;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.FreeformViewport;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.PrinterGraphics;
import org.eclipse.draw2d.SWTGraphics;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.SnapToGrid;
import org.eclipse.gef.dnd.TemplateTransferDragSourceListener;
import org.eclipse.gef.editparts.LayerManager;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.editparts.ZoomListener;
import org.eclipse.gef.editparts.ZoomManager;
import org.eclipse.gef.palette.CombinedTemplateCreationEntry;
import org.eclipse.gef.palette.MarqueeToolEntry;
import org.eclipse.gef.palette.PaletteContainer;
import org.eclipse.gef.palette.PaletteEntry;
import org.eclipse.gef.palette.PaletteGroup;
import org.eclipse.gef.palette.PaletteRoot;
import org.eclipse.gef.palette.PaletteSeparator;
import org.eclipse.gef.palette.SelectionToolEntry;
import org.eclipse.gef.palette.ToolEntry;
import org.eclipse.gef.requests.SimpleFactory;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.gef.ui.actions.WorkbenchPartAction;
import org.eclipse.gef.ui.actions.ZoomInAction;
import org.eclipse.gef.ui.actions.ZoomOutAction;
import org.eclipse.gef.ui.palette.PaletteContextMenuProvider;
import org.eclipse.gef.ui.palette.PaletteViewer;
import org.eclipse.gef.ui.palette.PaletteViewerPreferences;
import org.eclipse.gef.ui.parts.GraphicalViewerKeyHandler;
import org.eclipse.gef.ui.parts.ScrollingGraphicalViewer;
import org.eclipse.gef.ui.parts.TreeViewer;
import org.eclipse.gef.ui.stackview.CommandStackInspectorPage;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.TransferDropTargetListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.printing.PrintDialog;
import org.eclipse.swt.printing.Printer;
import org.eclipse.swt.printing.PrinterData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.jboss.tools.common.editor.AbstractSelectionProvider;
import org.jboss.tools.common.gef.GEFConnectionCreationToolEntry;
import org.jboss.tools.common.gef.GEFEditor;
import org.jboss.tools.common.gef.action.DiagramAlignmentAction;
import org.jboss.tools.common.gef.action.DiagramMatchWidthAction;
import org.jboss.tools.common.gef.action.IDiagramSelectionProvider;
import org.jboss.tools.common.gef.edit.GEFRootEditPart;
import org.jboss.tools.common.gef.editor.xpl.DefaultPaletteCustomizer;
import org.jboss.tools.common.gef.outline.xpl.DiagramContentOutlinePage;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.XModelTransferBuffer;
import org.jboss.tools.common.model.ui.ModelUIPlugin;
import org.jboss.tools.common.model.ui.outline.XModelObjectContentOutlineProvider;
import org.jboss.tools.common.reporting.ProblemReportingHelper;
import org.jboss.tools.struts.messages.StrutsUIMessages;
import org.jboss.tools.struts.model.helpers.StrutsProcessStructureHelper;
import org.jboss.tools.struts.ui.StrutsUIPlugin;
import org.jboss.tools.struts.ui.editor.actions.StrutsCopyAction;
import org.jboss.tools.struts.ui.editor.actions.StrutsCutAction;
import org.jboss.tools.struts.ui.editor.actions.StrutsDeleteAction;
import org.jboss.tools.struts.ui.editor.actions.StrutsPasteAction;
import org.jboss.tools.struts.ui.editor.dnd.DndHelper;
import org.jboss.tools.struts.ui.editor.dnd.FileTransferDropTargetListener;
import org.jboss.tools.struts.ui.editor.dnd.StrutsTemplateTransferDropTargetListener;
import org.jboss.tools.struts.ui.editor.dnd.XModelTransferDropTargetListener;
import org.jboss.tools.struts.ui.editor.edit.CommentEditPart;
import org.jboss.tools.struts.ui.editor.edit.ForwardEditPart;
import org.jboss.tools.struts.ui.editor.edit.GraphicalPartFactory;
import org.jboss.tools.struts.ui.editor.edit.LinkEditPart;
import org.jboss.tools.struts.ui.editor.edit.ProcessItemEditPart;
import org.jboss.tools.struts.ui.editor.edit.StrutsConnectionRouter;
import org.jboss.tools.struts.ui.editor.edit.StrutsDiagramEditPart;
import org.jboss.tools.struts.ui.editor.edit.StrutsEditPart;
import org.jboss.tools.struts.ui.editor.figures.ProcessItemFigure;
import org.jboss.tools.struts.ui.editor.model.IForward;
import org.jboss.tools.struts.ui.editor.model.ILink;
import org.jboss.tools.struts.ui.editor.model.IProcessItem;
import org.jboss.tools.struts.ui.editor.model.IStrutsElement;
import org.jboss.tools.struts.ui.editor.model.IStrutsModel;
import org.jboss.tools.struts.ui.editor.model.IStrutsModelListener;
import org.jboss.tools.struts.ui.editor.palette.StrutsPaletteViewerPreferences;
import org.jboss.tools.struts.ui.editor.print.Page;
import org.jboss.tools.struts.ui.editor.print.PageFormat;
import org.jboss.tools.struts.ui.editor.print.Pages;
import org.jboss.tools.struts.ui.editor.print.PrintPreviewDialog;

public class StrutsEditor extends GEFEditor  implements IStrutsModelListener{
	public static byte JSF_DIAGRAM_RENAME;
	private boolean isCommentsVisible = false;
	
	public boolean isBordersPaint(){
		return getStrutsModel().isBorderPaint();
	}
    
	public XModelObjectContentOutlineProvider poutline;

	
	protected void createPaletteViewer(Composite parent) {
		PaletteViewer viewer = new PaletteViewer();
		viewer.createControl(parent);
		StrutsPaletteViewerPreferences prefs = new StrutsPaletteViewerPreferences(this);
		prefs.setUseLargeIcons(PaletteViewerPreferences.LAYOUT_COLUMNS, false);
		prefs.setUseLargeIcons(PaletteViewerPreferences.LAYOUT_LIST, false);
		prefs.setUseLargeIcons(PaletteViewerPreferences.LAYOUT_ICONS, false);
		prefs.setUseLargeIcons(PaletteViewerPreferences.LAYOUT_DETAILS, false);
		prefs.setLayoutSetting(PaletteViewerPreferences.LAYOUT_LIST);
		setPaletteViewer(viewer);
		setPaletteLayout(prefs, loadPaletteSize());
		paletteViewer.setPaletteViewerPreferences(prefs);
		configurePaletteViewer();
		hookPaletteViewer();
		initializePaletteViewer();
	}
	
	private IStrutsModel model; 

	public StrutsEditor(IEditorInput input) {
		super(input);
		setEditDomain(new DefaultEditDomain(this));
	}

	protected void closeEditor(boolean save) {
		getSite().getPage().closeEditor(StrutsEditor.this, save);
	}

	public void commandStackChanged(EventObject event) {
		if (isDirty()){
			if (!savePreviouslyNeeded()) {
				setSavePreviouslyNeeded(true);
				firePropertyChange(IEditorPart.PROP_DIRTY);
			}
		} else {
			setSavePreviouslyNeeded(false);
			firePropertyChange(IEditorPart.PROP_DIRTY);
		}
		super.commandStackChanged(event);
	}

	/**
	 * @see org.eclipse.gef.ui.parts.GraphicalEditorWithPalette#configurePaletteViewer()
	 */
	
	protected void configurePaletteViewer() {
		PaletteViewer viewer = (PaletteViewer)getPaletteViewer();
		ContextMenuProvider provider = new PaletteContextMenuProvider(viewer);
		getPaletteViewer().setContextMenu(provider);
		viewer.setCustomizer(new DefaultPaletteCustomizer());
	}

	ScrollingGraphicalViewer viewer;

	public ScrollingGraphicalViewer getScrollingGraphicalViewer(){
		return viewer;
	}

	protected void configureGraphicalViewer() {
		viewer = (ScrollingGraphicalViewer)getGraphicalViewer();
		viewer.addSelectionChangedListener(modelSelectionProvider);
		ScalableFreeformRootEditPart root = new GEFRootEditPart();
		IAction zoomIn = new ZoomInAction(root.getZoomManager());
		IAction zoomOut = new ZoomOutAction(root.getZoomManager());
		root.getZoomManager().setZoomLevels(new double[]{.25, .5, .75, 1.0/*, 2.0, 4.0*/});
		root.getZoomManager().setZoom(loadZoomSize());
		root.getZoomManager().addZoomListener(new ZoomListener(){
			public void zoomChanged(double zoom){
				saveZoomSize(zoom);
			}
		});
		
		getActionRegistry().registerAction(zoomIn);
		getActionRegistry().registerAction(zoomOut);
//		getSite().getKeyBindingService().registerAction(zoomIn);
//		getSite().getKeyBindingService().registerAction(zoomOut);
		
		viewer.setRootEditPart(root);
	
		viewer.setEditPartFactory(new GraphicalPartFactory());
		ContextMenuProvider provider = new StrutsContextMenuProvider(model, viewer, getActionRegistry());
		viewer.setContextMenu(provider);
		getSite().registerContextMenu("StrutsContextmenu", //$NON-NLS-1$
			provider, viewer);
		viewer.setKeyHandler(new GraphicalViewerKeyHandler(viewer)
			.setParent(getCommonKeyHandler()));
	}

		public void mouseDoubleClick(MouseEvent e){
			boolean controlFlag = (e.stateMask&SWT.CONTROL) > 0;
			EditPart part = StrutsEditor.this.getGraphicalViewer().findObjectAt(new Point(e.x, e.y));
			if(part instanceof StrutsEditPart) ((StrutsEditPart)part).doDoubleClick(controlFlag);
			else if(part instanceof LinkEditPart) ((LinkEditPart)part).doDoubleClick(controlFlag);
		}
	
		public void mouseDown(MouseEvent e){
			boolean controlFlag = (e.stateMask&SWT.CONTROL) > 0;
			EditPart part = StrutsEditor.this.getGraphicalViewer().findObjectAt(new Point(e.x, e.y));
			if(part instanceof StrutsEditPart) ((StrutsEditPart)part).doMouseDown(controlFlag);
			else if(part instanceof LinkEditPart) ((LinkEditPart)part).doMouseDown(controlFlag);
		}
	
		public void mouseUp(MouseEvent e){
			boolean controlFlag = (e.stateMask&SWT.CONTROL) > 0;
			EditPart part = StrutsEditor.this.getGraphicalViewer().findObjectAt(new Point(e.x, e.y));
			if(part instanceof StrutsEditPart) ((StrutsEditPart)part).doMouseUp(controlFlag);
			else if(part instanceof LinkEditPart) ((LinkEditPart)part).doMouseUp(controlFlag);
		}

	public Control getControl(){
		return this.getPaletteViewer().getControl();
	}

	protected void createOutputStream(OutputStream os)throws IOException {
	}

	public void dispose() {
		model.removeStrutsModelListener(this);
		super.dispose();
	}

	public void doSave(IProgressMonitor progressMonitor) {
	}

	public void doSaveAs() {
		performSaveAs();
	}
	
	public void setParentOutline(XModelObjectContentOutlineProvider poutline){
		this.poutline = poutline;
	}
	
	public Object getAdapter(Class type){
		if(type == IDiagramSelectionProvider.class) {
			if(getScrollingGraphicalViewer() == null) return null;
			return new IDiagramSelectionProvider() {
				public ISelection getSelection() {
					if(getScrollingGraphicalViewer() == null) return null;
					return getScrollingGraphicalViewer().getSelection();
				}
			};
		}
		if (type == CommandStackInspectorPage.class)
			return new CommandStackInspectorPage(getCommandStack());
		if (type == IContentOutlinePage.class) {
			DiagramContentOutlinePage outline = new DiagramContentOutlinePage(new TreeViewer());
			outline.setGraphicalViewer(getGraphicalViewer());
			outline.setSelectionSynchronizer(getSelectionSynchronizer());
			return outline;
		}
		
		if (type == ZoomManager.class){
			if(getGraphicalViewer() != null)
			return (
				(ScalableFreeformRootEditPart) getGraphicalViewer()
					.getRootEditPart())
				.getZoomManager();
		}
		return super.getAdapter(type);
	}

	/**
	 * @see org.eclipse.gef.ui.parts.GraphicalEditorWithPalette#getInitialPaletteSize()
	 */

	protected int getInitialPaletteSize() {
		return 23;
	}

	/**
	 * @see org.eclipse.gef.ui.parts.GraphicalEditorWithPalette#handlePaletteResized(int)
	 */

	protected void handlePaletteResized(int newSize) {

	}

	
	public IStrutsModel getStrutsModel() {
		return model;
	}
	
	protected List createCategories(PaletteRoot root){
		List<PaletteContainer> categories = new ArrayList<PaletteContainer>();
		
		categories.add(createControlGroup(root));

		return categories;
	}
	static private GEFConnectionCreationToolEntry connectionCreationTool=null;
	
	protected PaletteContainer createControlGroup(PaletteRoot root){
		PaletteGroup controlGroup = new PaletteGroup(
			"control"); //$NON-NLS-1$
	
		List<PaletteEntry> entries = new ArrayList<PaletteEntry>();
	
		ToolEntry tool = new SelectionToolEntry();
		tool.setDescription(StrutsUIMessages.DIAGRAM_TOOLBAR_SELECTION); //$NON-NLS-1$
		entries.add(tool);
		root.setDefaultEntry(tool);
	
		tool = new MarqueeToolEntry();
		tool.setDescription(StrutsUIMessages.DIAGRAM_TOOLBAR_MARQUEE); //$NON-NLS-1$
		entries.add(tool);
		
		PaletteSeparator sep = new PaletteSeparator(
				"separator");  //$NON-NLS-1$
		sep.setUserModificationPermission(PaletteSeparator.PERMISSION_NO_MODIFICATION);
		entries.add(sep); 
	
		connectionCreationTool = new GEFConnectionCreationToolEntry(
			StrutsUIMessages.DIAGRAM_TOOLBAR_CREATE_CONNECTION,
			StrutsUIMessages.DIAGRAM_TOOLBAR_CREATE_CONNECTION,
			null,
		    ImageDescriptor.createFromFile(StrutsEditor.class,"icons/transition.gif"),//$NON-NLS-1$
			null
		) {
			protected void dragFinished() {
				DndHelper.dragEnd();
			}
		};
		connectionCreationTool.setUnloadWhenFinished(switchToSelectionTool);
		entries.add(connectionCreationTool);
		
		entries.add(sep);
	
		CombinedTemplateCreationEntry combined = new CombinedTemplateCreationEntry(
			StrutsUIMessages.DIAGRAM_TOOLBAR_ADD_ACTION,
			StrutsUIMessages.DIAGRAM_TOOLBAR_ADD_ACTION,
			TemplateConstants.TEMPLATE_ACTION,
			new SimpleFactory(ActionTemplate.class),
			ImageDescriptor.createFromFile(StrutsEditor.class, "icons/new_action.gif"),  //$NON-NLS-1$
			null//$NON-NLS-1$
		);
		entries.add(combined);
	
		combined = new CombinedTemplateCreationEntry(
			StrutsUIMessages.DIAGRAM_TOOLBAR_ADD_GLOBAL_FORWARD,
			StrutsUIMessages.DIAGRAM_TOOLBAR_ADD_GLOBAL_FORWARD,
			TemplateConstants.TEMPLATE_GLOBAL_FORWARD,
			new SimpleFactory(GlobalForwardTemplate.class),
			ImageDescriptor.createFromFile(StrutsEditor.class, "icons/new_global_forward.gif"),  //$NON-NLS-1$
			null//$NON-NLS-1$
		);
		entries.add(combined);
		
		if(!model.getHelper().isStruts10((XModelObject)model.getSource())){
		combined = new CombinedTemplateCreationEntry(
			StrutsUIMessages.DIAGRAM_TOOLBAR_ADD_GLOBAL_EXCEPTION,
			StrutsUIMessages.DIAGRAM_TOOLBAR_ADD_GLOBAL_EXCEPTION,
			TemplateConstants.TEMPLATE_GLOBAL_EXCEPTION,
			new SimpleFactory(GlobalExceptionTemplate.class),
			ImageDescriptor.createFromFile(StrutsEditor.class, "icons/new_exception.gif"),  //$NON-NLS-1$
			null//$NON-NLS-1$
		);
		
		entries.add(combined);
		}
		combined = new CombinedTemplateCreationEntry(
			StrutsUIMessages.DIAGRAM_TOOLBAR_ADD_PAGE,
			StrutsUIMessages.DIAGRAM_TOOLBAR_ADD_PAGE,
			TemplateConstants.TEMPLATE_PAGE,
			new SimpleFactory(PageTemplate.class),
			ImageDescriptor.createFromFile(StrutsEditor.class, "icons/new_jsp_file.gif"),  //$NON-NLS-1$
			null//$NON-NLS-1$
		);
		entries.add(combined);
		
		controlGroup.addAll(entries);
		return controlGroup;
	}
	
//	static private PaletteContainer createComponentsDrawer() {	
//		PaletteDrawer drawer = 
//			new PaletteDrawer("",null);	
//		List entries = new ArrayList();		
//		CombinedTemplateCreationEntry combined = 
//			new CombinedTemplateCreationEntry(
//					Messages.getString("diagramtoolbar.view-template"),
//					Messages.getString("diagramtoolbar.view-template"),
//					null,
//					null,
//					ImageDescriptor.createFromFile(StrutsEditor.class, "icons/view.gif"), 
//					null
//			);
//		entries.add(combined);	
//		drawer.addAll(entries);
//		return drawer;
//	}
	
	protected PaletteRoot createPalette() {
		PaletteRoot JSFPalette = new PaletteRoot();
		JSFPalette.addAll(createCategories(JSFPalette));
		return JSFPalette;
	}
	
	
	public void gotoMarker(IMarker marker) {
	
	}
	
	protected void hookPaletteViewer() {
		getEditDomain().setPaletteViewer(paletteViewer);
		getPaletteViewer().getContextMenu().addMenuListener(
			new IMenuListener() {
				public void menuAboutToShow(IMenuManager manager) {
					manager.removeAll();
				}
			}
		);
	}
	
	protected void initializeGraphicalViewer() {
		getGraphicalViewer().setContents(getStrutsModel());
		
		getGraphicalViewer().addDropTargetListener(
			new XModelTransferDropTargetListener(this));
	
		getGraphicalViewer().addDropTargetListener(
			new FileTransferDropTargetListener(this));
	
		getGraphicalViewer().addDropTargetListener(
			(TransferDropTargetListener)new StrutsTemplateTransferDropTargetListener(getGraphicalViewer()));
			
		((ConnectionLayer)((ScalableFreeformRootEditPart)getGraphicalViewer().getRootEditPart()).getLayer(ScalableFreeformRootEditPart.CONNECTION_LAYER)).setConnectionRouter(new StrutsConnectionRouter());
		getGraphicalViewer().setProperty(SnapToGrid.PROPERTY_GRID_VISIBLE, new Boolean(true));
		getGraphicalViewer().setProperty(SnapToGrid.PROPERTY_GRID_ENABLED, new Boolean(true));
		getGraphicalViewer().setProperty(SnapToGrid.PROPERTY_GRID_SPACING, new Dimension(8,8));
		
	}
	
	protected void initializePaletteViewer() {
		getEditDomain().setPaletteRoot(getPaletteRoot());
		FigureCanvas canvas = (FigureCanvas)paletteViewer.getControl();
		makeUnwrapPaletteItems(canvas.getContents());
		canvas.getContents().revalidate();
		canvas.setHorizontalScrollBarVisibility(FigureCanvas.NEVER);
		canvas.setVerticalScrollBarVisibility(FigureCanvas.NEVER);
		
		getPaletteViewer().addDragSourceListener(
			new TemplateTransferDragSourceListener(getPaletteViewer()));
	}
	
	protected void createActions() {

		ActionRegistry registry = getActionRegistry();
		
		addAction(registry, new StrutsDeleteAction(this), true);
		addAction(registry, new StrutsCopyAction(this), true);
		
		/// Without this nothing works. Why it is needed I could not understand.
		getSite().getKeyBindingService();//.registerAction(action);
	
		addAction(registry, new StrutsPasteAction(this), true);
		addAction(registry, new StrutsCutAction(this), true);
		
		addAction(registry, new MyPrintAction(this), false);
	
		addAction(registry, new DiagramAlignmentAction((IWorkbenchPart)this, PositionConstants.LEFT), true);
		addAction(registry, new DiagramAlignmentAction((IWorkbenchPart)this, PositionConstants.RIGHT), true);
		addAction(registry, new DiagramAlignmentAction((IWorkbenchPart)this, PositionConstants.TOP), true);
		addAction(registry, new DiagramAlignmentAction((IWorkbenchPart)this, PositionConstants.BOTTOM), true);
		addAction(registry, new DiagramAlignmentAction((IWorkbenchPart)this, PositionConstants.CENTER), true);
		addAction(registry, new DiagramAlignmentAction((IWorkbenchPart)this, PositionConstants.MIDDLE), true);
		addAction(registry, new DiagramMatchWidthAction(this), true);
	}
	
	void addAction(ActionRegistry registry, IAction action, boolean isSelectionAction) {
		registry.registerAction(action);
		if(isSelectionAction) {
			getSelectionActions().add(action.getId());
		}
	}
	
	public class MyPrintAction extends WorkbenchPartAction{
		private Insets margin = new Insets(1, 1, 1, 1);
		
		public MyPrintAction(IEditorPart editor) {
			super(editor);
		}
		
		protected boolean calculateEnabled() {
			return true;
		}	

		protected void init() {
			super.init();
			setText(StrutsUIMessages.PRINT_DIAGRAMM);
			setToolTipText(StrutsUIMessages.PRINT_DIAGRAMM);
			setId("Print_Diagram"); //$NON-NLS-1$
		}
		
		public org.eclipse.draw2d.geometry.Rectangle getPrintRegion(Printer printer) {
			printer.computeTrim(0, 0, 0, 0);
			org.eclipse.swt.graphics.Rectangle client = printer.getClientArea();
			printer.getDPI();
			org.eclipse.draw2d.geometry.Rectangle r = new org.eclipse.draw2d.geometry.Rectangle(client.x,client.y,client.width,client.height);
			return r;
		}
		
		public void run() {
			GraphicalViewer viewer;
			viewer = (GraphicalViewer)getWorkbenchPart().getAdapter(GraphicalViewer.class);
				
		    PrintPreviewDialog d = new PrintPreviewDialog(this.getWorkbenchPart().getSite().getShell(), SWT.APPLICATION_MODAL | SWT.DIALOG_TRIM);
			d.setPrintViewer(viewer);
			d.setEditor(StrutsEditor.this);
			Printer printer = new Printer();
			Exception ex = null;
			try{
				printer.getDPI();
				/*int pw = */printer.getClientArea();//.width;
			}catch(Exception ee){
				ex = ee;
				printer.dispose();
				d = null;
				ProblemReportingHelper.reportProblem(ModelUIPlugin.PLUGIN_ID, ee);
			}
			if(ex==null){
				d.setPages(new Pages(viewer,new PageFormat(printer,this.getWorkbenchPart().getSite().getShell().getDisplay())));
				String result = d.open();
				if(result!=null&&result.equals("ok")){ //$NON-NLS-1$
					LayerManager lm = (LayerManager)viewer.getEditPartRegistry().get(LayerManager.ID);
					IFigure figure = lm.getLayer(LayerConstants.PRINTABLE_LAYERS);
					PrintDialog dialog = new PrintDialog(viewer.getControl().getShell(), SWT.NULL);
					PrinterData data = dialog.open();
					 
					if (data != null) {
						printer = new Printer(data);
//						PageFormat pf = 
							d.getPages().getPageFormat();
						double scale = d.getPages().getScale();
						
						double dpiScale = printer.getDPI().x / Display.getCurrent().getDPI().x;
						getPrintRegion(printer);

						GC printerGC = new GC(printer);
						SWTGraphics g = new SWTGraphics(printerGC);
						Graphics graphics = new PrinterGraphics(g, printer);			
						if (printer.startJob(getWorkbenchPart().getTitle())) {
								Pages p = d.getPages();
								for(int i=0; i<p.getNumberOfPages(); i++){
									if(printer.startPage()){
										graphics.pushState();
										Page pg = p.getPrintable(i);
										Rectangle r1 = pg.getRectangle();
										Rectangle r = new Rectangle(r1.x+p.ix,r1.y+p.iy,r1.width,r1.height);
										org.eclipse.draw2d.geometry.Rectangle clipRect = new org.eclipse.draw2d.geometry.Rectangle();
										graphics.translate(-(int)(r.x*dpiScale*scale), -(int)(r.y*dpiScale*scale));
										graphics.getClip(clipRect);
										clipRect.setLocation((int)(r.x*dpiScale*scale), (int)(r.y*dpiScale*scale));
										graphics.clipRect(clipRect);
										graphics.scale(dpiScale*scale);
										figure.paint(graphics);
										graphics.popState();
										printer.endPage();
										
									}
								}
								
								
								graphics.dispose();
								printer.endJob();
						}
					}	
				}	
			}
		}
	}
	
	public boolean isDirty() {
		return isSaveOnCloseNeeded();
	}
	
	public boolean isSaveAsAllowed() {
		return true;
	}
	
	public boolean isSaveOnCloseNeeded() {
		return getCommandStack().isDirty();
	}
	
	protected boolean performSaveAs() {
		return false;
	}
	
	private boolean savePreviouslyNeeded() {
		return savePreviouslyNeeded;
	}
	
	public void setInput(XModelObject input) {

	}
	static private boolean switchToSelectionTool=false;
	
	public void setStrutsModel(IStrutsModel diagram) {
		model = diagram;
		model.addStrutsModelListener(this);
		switchToSelectionTool = model.getOptions().switchToSelectionTool();
		isCommentsVisible = model.areCommentsVisible();
	}
	
	private void setSavePreviouslyNeeded(boolean value) {
		savePreviouslyNeeded = value;
	}
	
	protected void superSetInput(IEditorInput input) {
		// The workspace never changes for an editor.  So, removing and re-adding the 
		// resourceListener is not necessary.  But it is being done here for the sake
		// of proper implementation.  Plus, the resourceListener needs to be added 
		// to the workspace the first time around.
		
		super.setInput(input);
	}


	public ISelectionProvider getModelSelectionProvider() {
		return modelSelectionProvider;
	}
	
	private ModelSelectionProvider modelSelectionProvider = new ModelSelectionProvider();
	
	class ModelSelectionProvider extends AbstractSelectionProvider implements ISelectionChangedListener {
		public void selectionChanged(SelectionChangedEvent event) {
			fireSelectionChanged();
			updateActions(getSelectionActions());
			//JSFEditor.super.selectionChanged(JSFEditor.this, event.getSelection());
		}
		protected XModelObject getSelectedModelObject() {
			if(viewer == null) return null;
			XModelObject o = getTarget(viewer.getSelection());
			XModelObject ref = StrutsProcessStructureHelper.instance.getReferencedObject(o);
			return ref;
		}
		
		public void scroll(FreeformViewport vp, ProcessItemFigure figure){
		   int delta;
		   int SCROLL_MARGIN = 20;
		   
		   Point origin = vp.getViewLocation();
  	 	 
  	 	 
			 if((figure.getLocation().x-SCROLL_MARGIN) < origin.x){
			  delta = origin.x - (figure.getLocation().x-SCROLL_MARGIN);
			  origin.x -= delta;
			 }else if((figure.getLocation().x+figure.getSize().width+SCROLL_MARGIN) > (origin.x+vp.getSize().width)){
			  delta = figure.getLocation().x+figure.getSize().width+SCROLL_MARGIN - (origin.x+vp.getSize().width);
			  origin.x += delta;
			 }
   	  	 
			 if((figure.getLocation().y-SCROLL_MARGIN) < origin.y){
			  delta = origin.y - (figure.getLocation().y-SCROLL_MARGIN);
			  origin.y -= delta;
			 }else if((figure.getLocation().y+figure.getSize().height+SCROLL_MARGIN) > (origin.y+vp.getSize().height)){
			  delta = figure.getLocation().y+figure.getSize().height+SCROLL_MARGIN - (origin.y+vp.getSize().height);
			  origin.y += delta;
			 }
   	  	 	
		   //if(origin.x < 0)origin.x = 0;
		   //if(origin.y < 0)origin.y = 0;	
		   if(origin.x != vp.getViewLocation().x || origin.y != vp.getViewLocation().y) vp.setViewLocation(origin);
		}

		protected void setSelectedModelObject(XModelObject object) {
			IStrutsElement element = getStrutsModel().findElement(object.getPath());
			if(element == null) return;
			EditPart part = (EditPart)viewer.getEditPartRegistry().get(element);
			if(part == null){
				if(element instanceof IForward && ((IForward)element).getLink() != null){
					part = (EditPart)viewer.getEditPartRegistry().get(((IForward)element).getLink());
				}
			}
			if(part != null){
				if(part instanceof ForwardEditPart){
					part = (LinkEditPart)((ForwardEditPart)part).getSourceConnections().get(0);
				}
				viewer.setSelection(new StructuredSelection(part));
				StrutsDiagramEditPart diagram = (StrutsDiagramEditPart)getScrollingGraphicalViewer().getRootEditPart().getChildren().get(0);
				FreeformViewport vp = diagram.getFreeformViewport();
				if(vp != null && part instanceof ProcessItemEditPart){
					ProcessItemFigure fig = (ProcessItemFigure)((ProcessItemEditPart)part).getFigure();
					if(fig.getLocation().x == 0 && fig.getLocation().y == 0){
						fig.setLocation(((ProcessItemEditPart)part).getProcessItemModel().getPosition());
					}
					scroll(vp, fig);
				}
			}
		}
		
	}

	private XModelObject getTarget(ISelection ss) {
		if(ss.isEmpty() || !(ss instanceof StructuredSelection)) return null;
		return getTarget(((StructuredSelection)ss).getFirstElement());
	}
	
	private XModelObject getTarget(Object selected) {
		if(selected instanceof StrutsEditPart) {
			StrutsEditPart part = (StrutsEditPart)selected;
			Object partModel = part.getModel();
			if(partModel instanceof IStrutsElement) {
				return (XModelObject)((IStrutsElement)partModel).getSource();
			}
		}
		if(selected instanceof LinkEditPart) {
			LinkEditPart part = (LinkEditPart)selected;
			Object partModel = part.getModel();
			if(partModel instanceof IStrutsElement) {
				return (XModelObject)((IStrutsElement)partModel).getSource();
			}
		}

		return null;
	}
	
	protected void hookGraphicalViewer() {
		getSelectionSynchronizer().addViewer(getGraphicalViewer());
	}
	public void processItemAdd(IProcessItem processItem) {
	}

	public void processItemRemove(IProcessItem processItem) {
	}

	public boolean isStrutsModelListenerEnabled() {
		return true;
	}

	public void linkAdd(ILink link) {

	}

	public void linkRemove(ILink link) {

	}

	public void processChanged(boolean flag) {
		if(switchToSelectionTool != model.getOptions().switchToSelectionTool()){
			switchToSelectionTool = model.getOptions().switchToSelectionTool();
			connectionCreationTool.setUnloadWhenFinished(switchToSelectionTool);
		}
		if(isCommentsVisible != model.areCommentsVisible()){
			isCommentsVisible = model.areCommentsVisible();
			IProcessItem processItem;
			for(int i=0;i<model.getProcessItemList().size();i++){
				processItem = (IProcessItem)model.getProcessItemList().get(i);
				if(processItem.isComment()){
					((CommentEditPart)getGraphicalViewer().getEditPartRegistry().get(processItem)).getFigure().setVisible(isCommentsVisible);
				}
			}
		}
	}
	public static Blinker blinker = new Blinker();
	
	 public static class Blinker extends Thread{
	   	private static Vector<Figure> listeners = new Vector<Figure>();
	   	private static boolean blink=false;
	   	
	   	public Blinker(){
	   		super(""); //$NON-NLS-1$
			start();
	   	}
	   	
		public void run() {
			try{
				while(true){
					sleep(750);
					blink = true;
					fireRedraw();
					
					sleep(450);
					blink = false;
					fireRedraw();
				}
			}catch(Exception ex){
				StrutsUIPlugin.getPluginLog().logError(ex);
			}
		}
		
		public boolean isBlink(){
			return blink;
		}
		
		public void addRedrawListener(Figure figure){
			listeners.add(figure);
		}

		public void removeRedrawListener(Figure figure){
			listeners.remove(figure);
		}

		public void fireRedraw(){
			if(listeners.size() == 0 || Display.getDefault() == null)return;
			Display.getDefault().syncExec( 
				new Runnable() {
					public void run() {
						for(int i=0; i< listeners.size();i++){
								((Figure)listeners.get(i)).repaint();
						}
					}
				}
			);
			
		}

	}

}