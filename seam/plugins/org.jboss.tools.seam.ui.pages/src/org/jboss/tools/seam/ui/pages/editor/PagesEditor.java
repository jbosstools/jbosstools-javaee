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
import org.eclipse.draw2d.ConnectionLayer;
import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.FreeformViewport;
import org.eclipse.draw2d.PositionConstants;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.DefaultEditDomain;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.KeyHandler;
import org.eclipse.gef.SnapToGrid;
import org.eclipse.gef.dnd.TemplateTransferDragSourceListener;
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
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseEvent;
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
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.XModelTransferBuffer;
import org.jboss.tools.common.model.event.XModelTreeEvent;
import org.jboss.tools.common.model.event.XModelTreeListener;
import org.jboss.tools.common.model.options.PreferenceModelUtilities;
import org.jboss.tools.jst.web.model.ReferenceObject;
import org.jboss.tools.seam.pages.xml.model.SeamPagesPreference;
import org.jboss.tools.seam.pages.xml.model.handlers.SelectOnDiagramHandler;
import org.jboss.tools.seam.pages.xml.model.helpers.SeamPagesDiagramStructureHelper;
import org.jboss.tools.seam.ui.pages.SeamUIPagesMessages;
import org.jboss.tools.seam.ui.pages.SeamUiPagesPlugin;
import org.jboss.tools.seam.ui.pages.editor.actions.PagesCopyAction;
import org.jboss.tools.seam.ui.pages.editor.actions.PagesCutAction;
import org.jboss.tools.seam.ui.pages.editor.actions.PagesDeleteAction;
import org.jboss.tools.seam.ui.pages.editor.actions.PagesPasteAction;
import org.jboss.tools.seam.ui.pages.editor.dnd.FileTransferDropTargetListener;
import org.jboss.tools.seam.ui.pages.editor.dnd.PagesTemplateTransferDropTargetListener;
import org.jboss.tools.seam.ui.pages.editor.dnd.XModelTransferDropTargetListener;
import org.jboss.tools.seam.ui.pages.editor.ecore.pages.PagesElement;
import org.jboss.tools.seam.ui.pages.editor.ecore.pages.PagesModel;
import org.jboss.tools.seam.ui.pages.editor.ecore.pages.PagesModelListener;
import org.jboss.tools.seam.ui.pages.editor.edit.GraphicalPartFactory;
import org.jboss.tools.seam.ui.pages.editor.edit.PagesDiagramEditPart;
import org.jboss.tools.seam.ui.pages.editor.edit.PagesEditPart;
import org.jboss.tools.seam.ui.pages.editor.edit.SelectionUtil;
import org.jboss.tools.seam.ui.pages.editor.edit.xpl.PagesConnectionRouter;
import org.jboss.tools.seam.ui.pages.editor.figures.NodeFigure;
import org.jboss.tools.seam.ui.pages.editor.palette.PagesPaletteViewerPreferences;

public class PagesEditor extends GEFEditor implements PagesModelListener, XModelTreeListener{

	protected void createPaletteViewer(Composite parent) {
		PaletteViewer viewer = new PaletteViewer();
		PagesPaletteViewerPreferences prefs = new PagesPaletteViewerPreferences(
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
				SeamUiPagesPlugin.log(exception);
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

	private PagesModel model;

	public PagesEditor(IEditorInput input) {
		super(input);
		setEditDomain(new DefaultEditDomain(this));
	}

	protected void closeEditor(boolean save) {
		getSite().getPage().closeEditor(PagesEditor.this, save);
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

		viewer.setEditPartFactory(new GraphicalPartFactory(this));
		ContextMenuProvider provider = new PagesContextMenuProvider(viewer,
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
	}

	public void mouseDoubleClick(MouseEvent e) {
	}

	public void mouseDown(MouseEvent e) {

		boolean controlFlag = (e.stateMask & SWT.CONTROL) > 0;
		EditPart part = getGraphicalViewer().findObjectAt(
				new Point(e.x, e.y));

		if (part instanceof PagesEditPart)
			((PagesEditPart) part).doMouseDown(new Point(e.x, e.y));
	    }

	public void mouseUp(MouseEvent e) {
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
		PreferenceModelUtilities.getPreferenceModel().removeModelTreeListener(this);
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

	public PagesModel getPagesModel() {
		return model;
	}

	static private GEFConnectionCreationToolEntry connectionCreationTool = null;

	protected PaletteContainer createControlGroup(PaletteRoot root) {
		PaletteGroup controlGroup = new PaletteGroup("control");

		List<PaletteEntry> entries = new ArrayList<PaletteEntry>();

		ToolEntry tool = new SelectionToolEntry();
		tool.setDescription(SeamUIPagesMessages.PAGESDIAGRAM_SELECT);
		entries.add(tool);
		root.setDefaultEntry(tool);

		tool = new MarqueeToolEntry();
		tool.setDescription(SeamUIPagesMessages.PAGESDIAGRAM_MARQUEE);
		entries.add(tool);

		PaletteSeparator sep = new PaletteSeparator("separator"); //$NON-NLS-1$
		sep
				.setUserModificationPermission(PaletteSeparator.PERMISSION_NO_MODIFICATION);
		entries.add(sep); //$NON-NLS-1$

		connectionCreationTool = new GEFConnectionCreationToolEntry(
				"New Link",
				"New Link",
				null, ImageDescriptor.createFromFile(PagesEditor.class,
						"icons/transition.gif"),//$NON-NLS-1$
				null//$NON-NLS-1$
		) {
			protected void dragFinished() {
				XModelTransferBuffer.getInstance().disable();
			}
		};
		connectionCreationTool.setUnloadWhenFinished(switchToSelectionTool);
		entries.add(connectionCreationTool);

		entries.add(sep);

		CombinedTemplateCreationEntry combined = new CombinedTemplateCreationEntry(
				SeamUIPagesMessages.PAGES_DIAGRAM_VIEW_TEMPLATE,
				SeamUIPagesMessages.PAGES_DIAGRAM_VIEW_TEMPLATE,
				TemplateConstants.TEMPLATE_PAGE,
				new SimpleFactory(PageTemplate.class), ImageDescriptor
						.createFromFile(PagesEditor.class, "icons/view.gif"),
				null//$NON-NLS-1$
		);
		entries.add(combined);

		combined = new CombinedTemplateCreationEntry(
				SeamUIPagesMessages.PAGES_DIAGRAM_EXCEPTION_TEMPLATE,
				SeamUIPagesMessages.PAGES_DIAGRAM_EXCEPTION_TEMPLATE,
				TemplateConstants.TEMPLATE_EXCEPTION,
				new SimpleFactory(ExceptionTemplate.class), ImageDescriptor
						.createFromFile(PagesEditor.class, "icons/exception.gif"),
				null//$NON-NLS-1$
		);
		entries.add(combined);

		controlGroup.addAll(entries);
		return controlGroup;
	}

	public void gotoMarker(IMarker marker) {
	}

	protected void initializeGraphicalViewer() {
		getGraphicalViewer().setContents(getPagesModel());

		getGraphicalViewer().addDropTargetListener(
				new XModelTransferDropTargetListener(this));

		getGraphicalViewer().addDropTargetListener(
				new FileTransferDropTargetListener(this));

		getGraphicalViewer()
				.addDropTargetListener(
						(TransferDropTargetListener) new PagesTemplateTransferDropTargetListener(
								getGraphicalViewer()));

		((ConnectionLayer) ((ScalableFreeformRootEditPart) getGraphicalViewer()
				.getRootEditPart())
				.getLayer(ScalableFreeformRootEditPart.CONNECTION_LAYER))
				.setConnectionRouter(new PagesConnectionRouter());
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
		register(new PagesDeleteAction(this), true, false);
		register(new PagesCopyAction(this), true, false);
		register(new PagesPasteAction(this), true, false);
		register(new PagesCutAction(this), true, false);
		//register(new MyPrintAction(this), false, true);
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

	public static String PRINT_DIAGRAM = "Print_Diagram";


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

	static private boolean switchToSelectionTool = SeamPagesPreference.ENABLE_CONTROL_MODE_ON_TRANSITION_COMPLETED.getValue().equals("yes");

	public void setPagesModel(PagesModel diagram) {
		model = diagram;
		PreferenceModelUtilities.getPreferenceModel().addModelTreeListener(this);
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

	public class ModelSelectionProvider extends AbstractSelectionProvider implements
			ISelectionChangedListener {
		public void selectionChanged(SelectionChangedEvent event) {
			fireSelectionChanged();
			updateActions(getSelectionActions());
		}

		protected XModelObject getSelectedModelObject() {
			if (viewer == null)
				return null;
			XModelObject o = getTarget(viewer.getSelection());
			if(!(o instanceof ReferenceObject)) {
				//Case of param object which does not have wrapper in diagram model.
				return o;
			}
			XModelObject ref = SeamPagesDiagramStructureHelper.instance.getReference(o);
			return ref;
		}

		public void scroll(FreeformViewport vp, NodeFigure figure) {
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

		public void setSelectedModelObject(XModelObject object) {
			if(object == null) return;

			// Make projection to diagram XML if necessary.
			XModelObject diagramXML = (XModelObject)getPagesModel().getData();
			if(diagramXML == null) return;
			XModelObject fileXML = diagramXML.getParent();
			if(fileXML == null || !object.getPath().startsWith(fileXML.getPath())) {
				return;
			}
			if(!object.getPath().startsWith(diagramXML.getPath())) {
				object = SelectOnDiagramHandler.getItemOnDiagram(object);
				if(object == null) return;
			}
			
			EObject element = getPagesModel().findElement(object);
			
			if(element == null) {
				element = getPagesModel().findLink(object);
			}
			
			if (element == null)
				return;
			EditPart part = (EditPart) viewer.getEditPartRegistry()
					.get(element);
			if (part != null) {
				viewer.setSelection(new StructuredSelection(part));
				PagesDiagramEditPart diagram = (PagesDiagramEditPart) getScrollingGraphicalViewer()
						.getRootEditPart().getChildren().get(0);
				FreeformViewport vp = diagram.getFreeformViewport();
				if (vp != null && part instanceof PagesEditPart && part != diagram) {
					PagesEditPart pagesPart = (PagesEditPart) part;
					NodeFigure fig = (NodeFigure)pagesPart.getFigure();
					if (fig.getLocation().x == 0 && fig.getLocation().y == 0) {
						fig.setLocation( ((PagesElement)pagesPart.getModel()).getLocation());
					}
					scroll(vp, fig);
				}
			}
		}

	}

	private XModelObject getTarget(ISelection ss) {
		if (ss.isEmpty() || !(ss instanceof StructuredSelection))
			return null;
		return SelectionUtil.getTarget(((StructuredSelection) ss).getFirstElement());
	}

	protected void hookGraphicalViewer() {
		getSelectionSynchronizer().addViewer(getGraphicalViewer());
	}
	
	public void nodeChanged(XModelTreeEvent event){
		String path = event.getModelObject().getPath();
		if(path.equals(SeamPagesPreference.SEAM_PAGES_EDITOR_PATH)){
			switchToSelectionTool = SeamPagesPreference.ENABLE_CONTROL_MODE_ON_TRANSITION_COMPLETED.getValue().equals("yes");
			connectionCreationTool.setUnloadWhenFinished(switchToSelectionTool);
		}
	}
	
    public void structureChanged(XModelTreeEvent event){
    	
    }
}
