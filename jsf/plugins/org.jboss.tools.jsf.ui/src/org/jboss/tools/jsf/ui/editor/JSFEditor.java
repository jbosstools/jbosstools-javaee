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

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.draw2d.ConnectionLayer;
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
import org.eclipse.gef.KeyHandler;
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
import org.eclipse.jface.commands.ActionHandler;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.TransferDropTargetListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.events.KeyEvent;
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
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.jboss.tools.common.editor.AbstractSelectionProvider;
import org.jboss.tools.common.gef.GEFConnectionCreationToolEntry;
import org.jboss.tools.common.gef.GEFEditor;
import org.jboss.tools.common.gef.action.DiagramAlignmentAction;
import org.jboss.tools.common.gef.action.IDiagramSelectionProvider;
import org.jboss.tools.common.gef.edit.GEFRootEditPart;
import org.jboss.tools.common.gef.editor.xpl.DefaultPaletteCustomizer;
import org.jboss.tools.common.gef.outline.xpl.DiagramContentOutlinePage;
import org.jboss.tools.common.meta.key.WizardKeys;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.XModelTransferBuffer;
import org.jboss.tools.common.reporting.ProblemReportingHelper;
import org.jboss.tools.jsf.messages.JSFUIMessages;
import org.jboss.tools.jsf.model.helpers.JSFProcessStructureHelper;
import org.jboss.tools.jsf.ui.JsfUiPlugin;
import org.jboss.tools.jsf.ui.editor.actions.JSFCopyAction;
import org.jboss.tools.jsf.ui.editor.actions.JSFCutAction;
import org.jboss.tools.jsf.ui.editor.actions.JSFDeleteAction;
import org.jboss.tools.jsf.ui.editor.actions.JSFPasteAction;
import org.jboss.tools.jsf.ui.editor.dnd.FileTransferDropTargetListener;
import org.jboss.tools.jsf.ui.editor.dnd.JSFTemplateTransferDropTargetListener;
import org.jboss.tools.jsf.ui.editor.dnd.XModelTransferDropTargetListener;
import org.jboss.tools.jsf.ui.editor.edit.GraphicalPartFactory;
import org.jboss.tools.jsf.ui.editor.edit.GroupEditPart;
import org.jboss.tools.jsf.ui.editor.edit.JSFDiagramEditPart;
import org.jboss.tools.jsf.ui.editor.edit.JSFEditPart;
import org.jboss.tools.jsf.ui.editor.edit.LinkEditPart;
import org.jboss.tools.jsf.ui.editor.edit.xpl.JSFConnectionRouter;
import org.jboss.tools.jsf.ui.editor.figures.GroupFigure;
import org.jboss.tools.jsf.ui.editor.model.IGroup;
import org.jboss.tools.jsf.ui.editor.model.IJSFElement;
import org.jboss.tools.jsf.ui.editor.model.IJSFModel;
import org.jboss.tools.jsf.ui.editor.model.IJSFModelListener;
import org.jboss.tools.jsf.ui.editor.model.ILink;
import org.jboss.tools.jsf.ui.editor.palette.JSFPaletteViewerPreferences;
import org.jboss.tools.jsf.ui.editor.print.Page;
import org.jboss.tools.jsf.ui.editor.print.PageFormat;
import org.jboss.tools.jsf.ui.editor.print.Pages;
import org.jboss.tools.jsf.ui.editor.print.PrintPreviewDialog;

public class JSFEditor extends GEFEditor implements IJSFModelListener {

	public static byte JSF_DIAGRAM_RENAME;

	public boolean isBordersPaint() {
		return getJSFModel().isBorderPaint();
	}

	protected void createPaletteViewer(Composite parent) {
		PaletteViewer viewer = new PaletteViewer();
		JSFPaletteViewerPreferences prefs = new JSFPaletteViewerPreferences(
				this);
		prefs.setUseLargeIcons(PaletteViewerPreferences.LAYOUT_COLUMNS, false);
		prefs.setUseLargeIcons(PaletteViewerPreferences.LAYOUT_LIST, false);
		prefs.setUseLargeIcons(PaletteViewerPreferences.LAYOUT_ICONS, false);
		prefs.setUseLargeIcons(PaletteViewerPreferences.LAYOUT_DETAILS, false);
		prefs.setLayoutSetting(PaletteViewerPreferences.LAYOUT_LIST);
		viewer.createControl(parent);
		setPaletteViewer(viewer);
		setPaletteLayout(prefs, loadPaletteSize());
		paletteViewer.setPaletteViewerPreferences(prefs);

		configurePaletteViewer();
		hookPaletteViewer();
		initializePaletteViewer();
	}

	private KeyHandler sharedKeyHandler;

	class ResourceTracker implements IResourceChangeListener,
			IResourceDeltaVisitor {
		public void resourceChanged(IResourceChangeEvent event) {
			IResourceDelta delta = event.getDelta();
			try {
				if (delta != null)
					delta.accept(this);
			} catch (CoreException exception) {
				JsfUiPlugin.getPluginLog().logError(exception);
				// What should be done here?
			}
		}

		public boolean visit(IResourceDelta delta) {
			if (delta == null
					|| !delta.getResource().equals(
							((FileEditorInput) getEditorInput()).getFile()))
				return true;

			if (delta.getKind() == IResourceDelta.REMOVED) {
				if ((IResourceDelta.MOVED_TO & delta.getFlags()) == 0) {
					if (!isDirty())
						closeEditor(false);
				} else { 
					final IFile newFile = ResourcesPlugin.getWorkspace()
							.getRoot().getFile(delta.getMovedToPath());
					Display display = getSite().getShell().getDisplay();
					display.asyncExec(new Runnable() {
						public void run() {
							superSetInput(new FileEditorInput(newFile));
						}
					});
				}
			}
			return false;
		}
	}

	private IJSFModel model;

	public JSFEditor(IEditorInput input) {
		super(input);
		setEditDomain(new DefaultEditDomain(this));
	}

	protected void closeEditor(boolean save) {
		getSite().getPage().closeEditor(JSFEditor.this, save);
	}

	public void commandStackChanged(EventObject event) {
		if (isDirty()) {
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
		PaletteViewer viewer = (PaletteViewer) getPaletteViewer();
		ContextMenuProvider provider = new PaletteContextMenuProvider(viewer);
		getPaletteViewer().setContextMenu(provider);
		viewer.setCustomizer(new DefaultPaletteCustomizer());
	}

	ScrollingGraphicalViewer viewer;

	public ScrollingGraphicalViewer getScrollingGraphicalViewer() {
		return viewer;
	}

	protected void configureGraphicalViewer() {

		viewer = (ScrollingGraphicalViewer) getGraphicalViewer();

		viewer.addSelectionChangedListener(modelSelectionProvider);

		ScalableFreeformRootEditPart root = new GEFRootEditPart();

		IAction zoomIn = new ZoomInAction(root.getZoomManager());
		IAction zoomOut = new ZoomOutAction(root.getZoomManager());

		root.getZoomManager().setZoomLevels(
				new double[] { .25, .5, .75, 1.0 /* , 2.0, 4.0 */});
		root.getZoomManager().setZoom(loadZoomSize());
		root.getZoomManager().addZoomListener(new ZoomListener() {
			public void zoomChanged(double zoom) {
				saveZoomSize(zoom);
			}
		});

		getActionRegistry().registerAction(zoomIn);
		getActionRegistry().registerAction(zoomOut);
		registerAction(zoomIn);
		registerAction(zoomOut);

		viewer.setRootEditPart(root);

		viewer.setEditPartFactory(new GraphicalPartFactory());
		ContextMenuProvider provider = new JSFContextMenuProvider(viewer,
				getActionRegistry());
		viewer.setContextMenu(provider);
		getSite().registerContextMenu("JSFContextmenu", //$NON-NLS-1$
				provider, viewer);
		viewer.setKeyHandler(new GraphicalViewerKeyHandler(viewer)
				.setParent(getCommonKeyHandler()));
	}

	public void mouseEnter(MouseEvent e) {
	}

	public void mouseExit(MouseEvent e) {
	}

	public void mouseHover(MouseEvent e) {
		boolean controlFlag = (e.stateMask & SWT.CONTROL) > 0;
		EditPart part = JSFEditor.this.getGraphicalViewer().findObjectAt(
				new Point(e.x, e.y));
		if (part instanceof JSFEditPart)
			((JSFEditPart) part).doMouseHover(controlFlag);
	}

	public void mouseDoubleClick(MouseEvent e) {

		boolean controlFlag = (e.stateMask & SWT.CONTROL) > 0;
		EditPart part = JSFEditor.this.getGraphicalViewer().findObjectAt(
				new Point(e.x, e.y));

		if (part instanceof JSFEditPart)
			((JSFEditPart) part).doDoubleClick(controlFlag);
		else if (part instanceof LinkEditPart)
			((LinkEditPart) part).doDoubleClick(controlFlag);
	}

	public void mouseDown(MouseEvent e) {

		boolean controlFlag = (e.stateMask & SWT.CONTROL) > 0;
		EditPart part = JSFEditor.this.getGraphicalViewer().findObjectAt(
				new Point(e.x, e.y));

		if (part instanceof JSFEditPart)
			((JSFEditPart) part).doMouseDown(controlFlag);
		else if (part instanceof LinkEditPart)
			((LinkEditPart) part).doMouseDown(controlFlag);
	}

	public void mouseUp(MouseEvent e) {

		boolean controlFlag = (e.stateMask & SWT.CONTROL) > 0;
		EditPart part = JSFEditor.this.getGraphicalViewer().findObjectAt(
				new Point(e.x, e.y));

		if (part instanceof JSFEditPart)
			((JSFEditPart) part).doMouseUp(controlFlag);
		else if (part instanceof LinkEditPart)
			((LinkEditPart) part).doMouseUp(controlFlag);
	}

	public void keyPressed(KeyEvent e) {
	}

	public void keyReleased(KeyEvent e) {
	}

	public Control getControl() {
		return this.getPaletteViewer().getControl();
	}

	protected void createOutputStream(OutputStream os) throws IOException {
	}

	public void dispose() {
		model.removeJSFModelListener(this);
		super.dispose();
	}

	public void doSave(IProgressMonitor progressMonitor) {
	}

	public void doSaveAs() {
	}

	public Object getAdapter(Class type) {
		if (type == IDiagramSelectionProvider.class) {
			if (getScrollingGraphicalViewer() == null)
				return null;
			return new IDiagramSelectionProvider() {
				public ISelection getSelection() {
					if (getScrollingGraphicalViewer() == null)
						return null;
					return getScrollingGraphicalViewer().getSelection();
				}
			};
		}
		if (type == CommandStackInspectorPage.class)
			return new CommandStackInspectorPage(getCommandStack());
		if (type == IContentOutlinePage.class) {
			if(outline != null) return outline;
			outline = new DiagramContentOutlinePage(
					new TreeViewer());
			outline.setGraphicalViewer(getGraphicalViewer());
			outline.setSelectionSynchronizer(getSelectionSynchronizer());
			return outline;
		}

		if (type == ZoomManager.class) {
			if (getGraphicalViewer() != null)
				return ((ScalableFreeformRootEditPart) getGraphicalViewer()
						.getRootEditPart()).getZoomManager();
		}
		return super.getAdapter(type);
	}

	/**
	 * @see org.eclipse.gef.ui.parts.GraphicalEditorWithPalette#getInitialPaletteSize()
	 */
	protected int getInitialPaletteSize() {
		return 22;
	}

	/**
	 * @see org.eclipse.gef.ui.parts.GraphicalEditorWithPalette#handlePaletteResized(int)
	 */
	protected void handlePaletteResized(int newSize) {
	}

	/**
	 * Returns the KeyHandler with common bindings for both the Outline and
	 * Graphical Views. For example, delete is a common action.
	 */
	protected KeyHandler getCommonKeyHandler() {
		return sharedKeyHandler;
	}

	public IJSFModel getJSFModel() {
		return model;
	}

	static private GEFConnectionCreationToolEntry connectionCreationTool = null;

	protected PaletteContainer createControlGroup(PaletteRoot root) {
		PaletteGroup controlGroup = new PaletteGroup("control"); //$NON-NLS-1$

		List<PaletteEntry> entries = new ArrayList<PaletteEntry>();

		ToolEntry tool = new SelectionToolEntry();
		tool.setDescription(FacesConfigEditorMessages.JSFDIAGRAM_SELECT);
		entries.add(tool);
		root.setDefaultEntry(tool);

		tool = new MarqueeToolEntry();
		tool.setDescription(FacesConfigEditorMessages.JSFDIAGRAM_MARQUEE);
		entries.add(tool);

		PaletteSeparator sep = new PaletteSeparator("separator"); //$NON-NLS-1$
		sep
				.setUserModificationPermission(PaletteSeparator.PERMISSION_NO_MODIFICATION);
		entries.add(sep);

		connectionCreationTool = new GEFConnectionCreationToolEntry(
				FacesConfigEditorMessages.JSFDIAGRAM_CREATE_NEW_CONNECTION,
				FacesConfigEditorMessages.JSFDIAGRAM_CREATE_NEW_CONNECTION,
				null, ImageDescriptor.createFromFile(JSFEditor.class,
						"icons/transition.gif"),//$NON-NLS-1$
				null
		) {
			protected void dragFinished() {
				XModelTransferBuffer.getInstance().disable();
			}
		};
		connectionCreationTool.setUnloadWhenFinished(switchToSelectionTool);
		entries.add(connectionCreationTool);

		entries.add(sep);

		CombinedTemplateCreationEntry combined = new CombinedTemplateCreationEntry(
				FacesConfigEditorMessages.JSFDIAGRAM_VIEW_TEMPLATE,
				FacesConfigEditorMessages.JSFDIAGRAM_VIEW_TEMPLATE,
				TemplateConstants.TEMPLATE_VIEW,
				new SimpleFactory(String.class), ImageDescriptor
						.createFromFile(JSFEditor.class, "icons/view.gif"), //$NON-NLS-1$
				null
		);
		entries.add(combined);

		controlGroup.addAll(entries);
		return controlGroup;
	}
	public void gotoMarker(IMarker marker) {
	}

	protected void initializeGraphicalViewer() {
		getGraphicalViewer().setContents(getJSFModel());

		getGraphicalViewer().addDropTargetListener(
				new XModelTransferDropTargetListener(this));

		getGraphicalViewer().addDropTargetListener(
				new FileTransferDropTargetListener(this));

		getGraphicalViewer()
				.addDropTargetListener(
						(TransferDropTargetListener) new JSFTemplateTransferDropTargetListener(
								getGraphicalViewer()));

		((ConnectionLayer) ((ScalableFreeformRootEditPart) getGraphicalViewer()
				.getRootEditPart())
				.getLayer(ScalableFreeformRootEditPart.CONNECTION_LAYER))
				.setConnectionRouter(new JSFConnectionRouter());
		getGraphicalViewer().setProperty(SnapToGrid.PROPERTY_GRID_VISIBLE,
				Boolean.TRUE);
		getGraphicalViewer().setProperty(SnapToGrid.PROPERTY_GRID_ENABLED,
				Boolean.TRUE);
		getGraphicalViewer().setProperty(SnapToGrid.PROPERTY_GRID_SPACING,
				new Dimension(8, 8));

	}

	protected void initializePaletteViewer() {
		getEditDomain().setPaletteRoot(getPaletteRoot());
		FigureCanvas canvas = (FigureCanvas) paletteViewer.getControl();
		makeUnwrapPaletteItems(canvas.getContents());
		canvas.getContents().revalidate();
		canvas.setHorizontalScrollBarVisibility(FigureCanvas.NEVER);
		canvas.setVerticalScrollBarVisibility(FigureCanvas.NEVER);

		getPaletteViewer().addDragSourceListener(
				new TemplateTransferDragSourceListener(getPaletteViewer()));
	}

	protected void createActions() {
		register(new JSFDeleteAction(this), true, false);
		register(new JSFCopyAction(this), true, false);
		register(new JSFPasteAction(this), true, false);
		register(new JSFCutAction(this), true, false);
		register(new MyPrintAction(this), false, true);
		register(new DiagramAlignmentAction(this, PositionConstants.LEFT), true, false);
		register(new DiagramAlignmentAction(this, PositionConstants.RIGHT), true, false);
		register(new DiagramAlignmentAction(this, PositionConstants.TOP), true, false);
		register(new DiagramAlignmentAction(this, PositionConstants.BOTTOM), true, false);
		register(new DiagramAlignmentAction(this, PositionConstants.CENTER), true, false);
		register(new DiagramAlignmentAction(this, PositionConstants.MIDDLE), true, false);
	}

	private void register(IAction action, boolean isSelectionAction, boolean isKeyBindingAction) {
		ActionRegistry registry = getActionRegistry();
		registry.registerAction(action);
		if(isSelectionAction) {
			getSelectionActions().add(action.getId());
		}
		if(isKeyBindingAction) {
			getSite().getKeyBindingService().registerAction(action);
		}
	}

	private void registerAction(IAction action) {
		if (action == null)
			return;
		IHandlerService handler = (IHandlerService) getSite().getService(
				IHandlerService.class);
		String id = action.getId();
		handler.activateHandler(id, new ActionHandler(action));

	}

	public static String PRINT_DIAGRAM = "Print_Diagram"; //$NON-NLS-1$

	public class MyPrintAction extends WorkbenchPartAction {
		private Insets printMargin = new Insets(1, 1, 1, 1);

		public MyPrintAction(IEditorPart editor) {
			super(editor);
		}

		protected boolean calculateEnabled() {
			return true;
		}

		protected void init() {
			super.init();
			setText(JSFUIMessages.PRINT_DIAGRAM);
			setToolTipText(JSFUIMessages.PRINT_DIAGRAM);
			setId(PRINT_DIAGRAM);
		}

		public org.eclipse.draw2d.geometry.Rectangle getPrintRegion(
				Printer printer) {
			org.eclipse.swt.graphics.Rectangle trim = printer.computeTrim(0, 0,
					0, 0);
			org.eclipse.swt.graphics.Rectangle clientArea = printer
					.getClientArea();
			org.eclipse.swt.graphics.Point printerDPI = printer.getDPI();

			org.eclipse.draw2d.geometry.Rectangle printRegion = new org.eclipse.draw2d.geometry.Rectangle();
			printRegion.x = Math.max((printMargin.left * printerDPI.x) / 72
					- trim.width, clientArea.x);
			printRegion.y = Math.max((printMargin.top * printerDPI.y) / 72
					- trim.height, clientArea.y);
			printRegion.width = (clientArea.x + clientArea.width)
					- printRegion.x
					- Math.max(0, (printMargin.right * printerDPI.x) / 72
							- trim.width);
			printRegion.height = (clientArea.y + clientArea.height)
					- printRegion.y
					- Math.max(0, (printMargin.bottom * printerDPI.y) / 72
							- trim.height);
			org.eclipse.draw2d.geometry.Rectangle r = new org.eclipse.draw2d.geometry.Rectangle(
					clientArea.x, clientArea.y, clientArea.width,
					clientArea.height);
			return r;
		}

		public void run() {
			GraphicalViewer viewer;
			viewer = (GraphicalViewer) getWorkbenchPart().getAdapter(
					GraphicalViewer.class);

			PrintPreviewDialog d = new PrintPreviewDialog(this
					.getWorkbenchPart().getSite().getShell(),
					SWT.APPLICATION_MODAL | SWT.DIALOG_TRIM);
			d.setPrintViewer(viewer);
			d.setEditor(JSFEditor.this);
			Printer printer = new Printer();
			Exception ex = null;
			try {
				printer.getDPI();
			} catch (SWTException ee) {
				ex = ee;
				printer.dispose();
				d = null;
				Status status = new Status(IStatus.ERROR,
						"org.jboss.tools.jsf.ui", 0, WizardKeys //$NON-NLS-1$
								.getString("PRN_ERROR"), ee); //$NON-NLS-1$
				ProblemReportingHelper.reportProblem(status);
			}
			//if ex is not null then d is set to null and we cannot access it.
			if(ex == null) {
				d.setPages(new Pages(viewer, new PageFormat(printer,
								this.getWorkbenchPart().getSite().getShell()
										.getDisplay())));
				String result = d.open();
				if (result != null && result.equals("ok")) { //$NON-NLS-1$
					LayerManager lm = (LayerManager) viewer
							.getEditPartRegistry().get(LayerManager.ID);
					IFigure figure = lm
							.getLayer(LayerConstants.PRINTABLE_LAYERS);
					PrintDialog dialog = new PrintDialog(viewer.getControl()
							.getShell(), SWT.NULL);
					PrinterData data = dialog.open();

					if (data != null) {
						printer = new Printer(data);
						double scale = d.getPages().getScale();
						double dpiScale = printer.getDPI().x
								/ Display.getCurrent().getDPI().x;
						GC printerGC = new GC(printer);
						SWTGraphics g = new SWTGraphics(printerGC);
						Graphics graphics = new PrinterGraphics(g, printer);
						if (printer.startJob(getWorkbenchPart().getTitle())) {
							Pages p = d.getPages();
							for (int i = 0; i < p.getNumberOfPages(); i++) {
								if (printer.startPage()) {
									graphics.pushState();
									Page pg = p.getPrintable(i);
									Rectangle r1 = pg.getRectangle();
									Rectangle r = new Rectangle(r1.x + p.ix,
											r1.y + p.iy, r1.width, r1.height);
									org.eclipse.draw2d.geometry.Rectangle clipRect = new org.eclipse.draw2d.geometry.Rectangle();
									graphics.translate(
											-(int) (r.x * dpiScale * scale),
											-(int) (r.y * dpiScale * scale));
									graphics.getClip(clipRect);
									clipRect.setLocation(
											(int) (r.x * dpiScale * scale),
											(int) (r.y * dpiScale * scale));
									graphics.clipRect(clipRect);
									graphics.scale(dpiScale * scale);
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

		public boolean isEnabled() {
			return true;
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

	static private boolean switchToSelectionTool = false;

	public void setJSFModel(IJSFModel diagram) {
		model = diagram;
		model.addJSFModelListener(this);
		switchToSelectionTool = model.getOptions().switchToSelectionTool();
	}

	private void setSavePreviouslyNeeded(boolean value) {
		savePreviouslyNeeded = value;
	}

	protected void superSetInput(IEditorInput input) {
		super.setInput(input);
	}

	public ISelectionProvider getModelSelectionProvider() {
		return modelSelectionProvider;
	}

	private ModelSelectionProvider modelSelectionProvider = new ModelSelectionProvider();

	class ModelSelectionProvider extends AbstractSelectionProvider implements
			ISelectionChangedListener {
		public void selectionChanged(SelectionChangedEvent event) {
			fireSelectionChanged();
			updateActions(getSelectionActions());
		}

		protected XModelObject getSelectedModelObject() {
			if (viewer == null)
				return null;
			XModelObject o = getTarget(viewer.getSelection());
			XModelObject ref = JSFProcessStructureHelper.instance
					.getReference(o);
			return ref;
		}

		public void scroll(FreeformViewport vp, GroupFigure figure) {
			int delta;
			int SCROLL_MARGIN = 20;

			Point origin = vp.getViewLocation();

			if ((figure.getLocation().x - SCROLL_MARGIN) < origin.x) {
				delta = origin.x - (figure.getLocation().x - SCROLL_MARGIN);
				origin.x -= delta;
			} else if ((figure.getLocation().x + figure.getSize().width + SCROLL_MARGIN) > (origin.x + vp
					.getSize().width)) {
				delta = figure.getLocation().x + figure.getSize().width
						+ SCROLL_MARGIN - (origin.x + vp.getSize().width);
				origin.x += delta;
			}

			if ((figure.getLocation().y - SCROLL_MARGIN) < origin.y) {
				delta = origin.y - (figure.getLocation().y - SCROLL_MARGIN);
				origin.y -= delta;
			} else if ((figure.getLocation().y + figure.getSize().height + SCROLL_MARGIN) > (origin.y + vp
					.getSize().height)) {
				delta = figure.getLocation().y + figure.getSize().height
						+ SCROLL_MARGIN - (origin.y + vp.getSize().height);
				origin.y += delta;
			}
			if (origin.x != vp.getViewLocation().x
					|| origin.y != vp.getViewLocation().y)
				vp.setViewLocation(origin);
		}

		protected void setSelectedModelObject(XModelObject object) {
			IJSFElement element = getJSFModel().findElement(object.getPath());
			if (element == null)
				return;
			EditPart part = (EditPart) viewer.getEditPartRegistry()
					.get(element);
			if (part != null) {
				viewer.setSelection(new StructuredSelection(part));
				JSFDiagramEditPart diagram = (JSFDiagramEditPart) getScrollingGraphicalViewer()
						.getRootEditPart().getChildren().get(0);
				FreeformViewport vp = diagram.getFreeformViewport();
				if (vp != null && part instanceof GroupEditPart) {
					GroupFigure fig = (GroupFigure) ((GroupEditPart) part)
							.getFigure();
					if (fig.getLocation().x == 0 && fig.getLocation().y == 0) {
						fig.setLocation(((GroupEditPart) part).getGroupModel()
								.getPosition());
					}
					scroll(vp, fig);
				}
			}
		}

	}

	private XModelObject getTarget(ISelection ss) {
		if (ss.isEmpty() || !(ss instanceof StructuredSelection))
			return null;
		return getTarget(((StructuredSelection) ss).getFirstElement());
	}

	private XModelObject getTarget(Object selected) {
		if (selected instanceof JSFEditPart) {
			JSFEditPart part = (JSFEditPart) selected;
			Object partModel = part.getModel();
			if (partModel instanceof IJSFElement) {
				return (XModelObject) ((IJSFElement) partModel).getSource();
			}
		}
		if (selected instanceof LinkEditPart) {
			LinkEditPart part = (LinkEditPart) selected;
			Object partModel = part.getModel();
			if (partModel instanceof IJSFElement) {
				return (XModelObject) ((IJSFElement) partModel).getSource();
			}
		}

		return null;
	}

	protected void hookGraphicalViewer() {
		getSelectionSynchronizer().addViewer(getGraphicalViewer());
	}

	public void groupAdd(IGroup group) {
	}

	public void groupRemove(IGroup group) {
	}

	public boolean isStrutsModelListenerEnabled() {
		return true;
	}

	public void linkAdd(ILink link) {
	}

	public void linkRemove(ILink link) {
	}

	public void processChanged(boolean flag) {
		if (switchToSelectionTool != model.getOptions().switchToSelectionTool()) {
			switchToSelectionTool = model.getOptions().switchToSelectionTool();
			connectionCreationTool.setUnloadWhenFinished(switchToSelectionTool);
		}
	}

}
