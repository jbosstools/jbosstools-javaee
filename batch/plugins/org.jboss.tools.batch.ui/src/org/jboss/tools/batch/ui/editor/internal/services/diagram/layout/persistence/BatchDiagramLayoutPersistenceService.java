/******************************************************************************* 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Tomas Milata - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.batch.ui.editor.internal.services.diagram.layout.persistence;

import static org.eclipse.sapphire.FileUtil.mkdirs;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.Event;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.LoggingService;
import org.eclipse.sapphire.Sapphire;
import org.eclipse.sapphire.modeling.ByteArrayResourceStore;
import org.eclipse.sapphire.modeling.ResourceStoreException;
import org.eclipse.sapphire.modeling.ValidateEditException;
import org.eclipse.sapphire.modeling.util.MiscUtil;
import org.eclipse.sapphire.modeling.xml.RootXmlResource;
import org.eclipse.sapphire.modeling.xml.XmlResourceStore;
import org.eclipse.sapphire.ui.Point;
import org.eclipse.sapphire.ui.diagram.ConnectionAddEvent;
import org.eclipse.sapphire.ui.diagram.ConnectionBendpointsEvent;
import org.eclipse.sapphire.ui.diagram.ConnectionDeleteEvent;
import org.eclipse.sapphire.ui.diagram.ConnectionEvent;
import org.eclipse.sapphire.ui.diagram.ConnectionLabelEvent;
import org.eclipse.sapphire.ui.diagram.ConnectionService;
import org.eclipse.sapphire.ui.diagram.DiagramConnectionPart;
import org.eclipse.sapphire.ui.diagram.def.IDiagramNodeDef;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodeAddEvent;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodeBounds;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodeDeleteEvent;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodeEvent;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodeMoveEvent;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodePart;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodeTemplate;
import org.eclipse.sapphire.ui.diagram.editor.DiagramPageEvent;
import org.eclipse.sapphire.ui.diagram.editor.SapphireDiagramEditorPagePart;
import org.eclipse.sapphire.ui.diagram.editor.SapphireDiagramEditorPagePart.PostAutoLayoutEvent;
import org.eclipse.sapphire.ui.diagram.editor.SapphireDiagramEditorPagePart.PreAutoLayoutEvent;
//import org.eclipse.sapphire.ui.diagram.internal.StandardDiagramConnectionPart;
//import org.eclipse.sapphire.ui.diagram.internal.StandardEmbeddedConnectionPart;
import org.eclipse.sapphire.ui.diagram.layout.ConnectionHashKey;
import org.eclipse.sapphire.ui.diagram.layout.DiagramLayoutPersistenceService;
import org.eclipse.sapphire.ui.diagram.layout.standard.DiagramBendPointLayout;
import org.eclipse.sapphire.ui.diagram.layout.standard.DiagramConnectionLayout;
import org.eclipse.sapphire.ui.diagram.layout.standard.DiagramNodeLayout;
import org.eclipse.sapphire.ui.diagram.layout.standard.StandardDiagramLayout;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IStorageEditorInput;
import org.eclipse.ui.ide.FileStoreEditorInput;
import org.eclipse.ui.part.FileEditorInput;
import org.jboss.tools.batch.ui.BatchUIPlugin;
import org.jboss.tools.batch.ui.editor.internal.model.Flow;
import org.jboss.tools.batch.ui.editor.internal.model.FlowElement;
import org.jboss.tools.batch.ui.editor.internal.model.Job;
import org.jboss.tools.batch.ui.editor.internal.model.JobXMLEditor;
import org.jboss.tools.common.util.FileUtil;

/**
 * Saves layout to file in .metadata. One file contains all layouts for job and inner flow diagrams.
 * 
 * @author Viacheslav Kabanovich
 * @author Tomas Milata
 */
@SuppressWarnings("restriction")
public class BatchDiagramLayoutPersistenceService extends DiagramLayoutPersistenceService {

//	@Override
//	public DiagramConnectionInfo read(DiagramConnectionPart connection) {
//		return null;
//	}
//
//	public boolean dirty() {
//		return true;
//	}

	protected StandardDiagramLayout layoutModel;
	protected IEditorInput editorInput;
	private Listener diagramEditorPagePartListener;
	private Listener connectionPartListener;
	private Map<String, DiagramNodeBounds> nodeBounds;
	private Map<ConnectionHashKey, List<Point>> connectionBendPoints;
	private Map<ConnectionHashKey, Point> connectionLabelPositions;
	private boolean dirty;
	private Map<ConnectionHashKey, DiagramConnectionPart> connectionIdMap;
	private Map<String, DiagramNodePart> nodeIdMap;
	private boolean autoLayout = false;

	@Override
	protected void init() {
		super.init();
		this.editorInput = context( SapphireDiagramEditorPagePart.class ).getLocalModelElement().adapt(IEditorInput.class);
		this.nodeBounds = new HashMap<String, DiagramNodeBounds>();
		this.connectionBendPoints = new HashMap<ConnectionHashKey, List<Point>>();
		this.connectionLabelPositions = new HashMap<ConnectionHashKey, Point>();
		this.dirty = false;

		this.connectionPartListener = new FilteredListener<ConnectionEvent>() {
			@Override
			protected void handleTypedEvent(ConnectionEvent event) {
				DiagramConnectionPart connPart = (DiagramConnectionPart)event.part();
				if (event instanceof ConnectionLabelEvent) {
					if (((ConnectionLabelEvent)event).moveLabel()) {
						write(connPart);
					}
				} else if (event instanceof ConnectionBendpointsEvent) {
					ConnectionBendpointsEvent bpEvent = (ConnectionBendpointsEvent)event;
					if (bpEvent.reset()) {
						if (autoLayout) {
							addConnectionToPersistenceCache(event.part());
							refreshDirtyState();
						} else {
							write(event.part());
						}

					} else {
						write(event.part());
					}
				}
			}
		};
		try {
			load();
			refreshPersistedPartsCache();
		} catch (Exception e) {
			Sapphire.service( LoggingService.class ).log(e);
		}
		addDiagramPartListener();
	}
	
	@Override
	public void dispose() {
		if (diagramEditorPagePartListener != null) {
			context(SapphireDiagramEditorPagePart.class).detach(diagramEditorPagePartListener);
		}
	}

	protected StandardDiagramLayout initLayoutModel() {
		StandardDiagramLayout layoutModel = null;
		try {
			IEditorPart editor = 
					BatchUIPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage().findEditor(this.editorInput);
			String flow = null;
			Job job = (editor instanceof JobXMLEditor) ? ((JobXMLEditor)editor).getSchema() : null;
			if(editor instanceof JobXMLEditor && ((JobXMLEditor)editor).getCurrentDiagramModel() instanceof Flow) {
				flow = ((Flow)((JobXMLEditor)editor).getCurrentDiagramModel()).getId().content();
			} else {
				flow = FileResourceStore2.ROOT;
			}
			File layoutFile = getLayoutPersistenceFile();
			if (layoutFile != null) {
				final XmlResourceStore resourceStore = new XmlResourceStore( new FileResourceStore2(layoutFile, flow, job));
				layoutModel = StandardDiagramLayout.TYPE.instantiate(new RootXmlResource( resourceStore ));
			}
		} catch (Exception e) {
			Sapphire.service(LoggingService.class ).log(e);
		}
		return layoutModel;
	}

	private static final String WORKSPACE_LAYOUT_FOLDER = ".metadata/.plugins/org.jboss.tools.batch.ui/layouts";

	public File getLayoutPersistenceFile() throws IOException, CoreException {
		String fileName = computeLayoutFileName(this.editorInput);
		if (fileName != null) {
			return getLayoutPersistenceFile(fileName);
		}
		return null;
	}

	private File getLayoutPersistenceFile(String fileName) throws IOException {
		IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		File layoutFolder = workspaceRoot.getLocation().toFile();
		layoutFolder = new File(layoutFolder, WORKSPACE_LAYOUT_FOLDER);
		if (!layoutFolder.exists()) {
			mkdirs(layoutFolder);
		}
		File layoutFile = new File (layoutFolder, fileName);
		return layoutFile;
	}

	protected String computeLayoutFileName(IEditorInput editorInput) throws CoreException, IOException {
		// Compute a unique path for the layout file based on a hash associated with the editor input
		String uniquePath = null;
		if (editorInput instanceof FileEditorInput) {
			FileEditorInput fileEditorInput = (FileEditorInput)editorInput;
			IFile ifile = fileEditorInput.getFile();
			uniquePath = ifile.getLocation().toPortableString();
		} else if (editorInput instanceof FileStoreEditorInput) {
			FileStoreEditorInput fileStoreInput = (FileStoreEditorInput)editorInput;
			IFileStore store = EFS.getStore(fileStoreInput.getURI());
			File localFile = store.toLocalFile(EFS.NONE, null);
			//if no local file is available, obtain a cached file
			if (localFile == null)
				localFile = store.toLocalFile(EFS.CACHE, null);
			if (localFile == null)
				throw new IllegalArgumentException();
			uniquePath = localFile.getCanonicalPath();
		} else if (editorInput instanceof IStorageEditorInput) {
			IStorageEditorInput storageEditorInput = (IStorageEditorInput) editorInput;
			IPath storagePath = storageEditorInput.getStorage().getFullPath();
			if (storagePath != null) {
				uniquePath = storagePath.toPortableString();
			}
		}
		return uniquePath != null ? MiscUtil.createStringDigest(uniquePath) : null;
	}

	public void load() throws ResourceStoreException, CoreException, IOException {
		layoutModel = initLayoutModel();
		if (layoutModel == null) {
			return;
		}
		Boolean gridVisible = layoutModel.getGridLayout().isVisible().content();
		Boolean showGuides = layoutModel.getGuidesLayout().isVisible().content();

		// only set these if the layout file explicitly sets it.  
		// If absent then fallback to diagram-editor-def setting
		if (gridVisible != null) {
			context(SapphireDiagramEditorPagePart.class).setGridVisible(gridVisible);
		}

		if (showGuides != null) {
			context( SapphireDiagramEditorPagePart.class ).setShowGuides(showGuides);
		}

		ElementList<DiagramNodeLayout> nodes = layoutModel.getDiagramNodesLayout();
		ConnectionService connService = context(SapphireDiagramEditorPagePart.class).service(ConnectionService.class);
		for (DiagramNodeLayout node : nodes) {
			String nodeId = node.getNodeId().content();
			DiagramNodePart nodePart = getNodePart(nodeId);
			int x = node.getX().content();
			int y = node.getY().content();
			int width = node.getWidth().content();
			int height = node.getHeight().content();

			if (nodePart != null) {
				nodePart.setNodeBounds(new DiagramNodeBounds(x, y, width, height, false, false));
			}

			ElementList<DiagramConnectionLayout> connList = node.getEmbeddedConnectionsLayout();
			for (DiagramConnectionLayout connLayout : connList) {
				String connId = connLayout.getConnectionId().content();
				ElementList<DiagramBendPointLayout> bps = connLayout.getConnectionBendpoints();
				DiagramConnectionPart connPart = getConnectionPart(connService, ConnectionHashKey.createKey(nodeId, connId));
				if (connPart != null) {
					int index = 0;
					for (DiagramBendPointLayout pt : bps) {
						connPart.addBendpoint(index++, pt.getX().content(), pt.getY().content());
					}

					if (connLayout.getLabelX().content(false) != null && connLayout.getLabelY().content(false) != null) {
						Point labelPos = new Point(connLayout.getLabelX().content(), connLayout.getLabelY().content());
						connPart.setLabelPosition(labelPos);
					}
				}
			}
		}

		ElementList<DiagramConnectionLayout> connList = this.layoutModel.getDiagramConnectionsLayout();
		for (DiagramConnectionLayout connLayout : connList) {
			String connId = connLayout.getConnectionId().content();
			DiagramConnectionPart connPart = getConnectionPart(connService, ConnectionHashKey.createKey(null, connId));
			ElementList<DiagramBendPointLayout> bps = connLayout.getConnectionBendpoints();
			if (connPart != null) {
				int index = 0;
				for (DiagramBendPointLayout pt : bps) {
					connPart.addBendpoint(index++, pt.getX().content(), pt.getY().content());
				}
				List<Point> bendPoints = new ArrayList<Point>();
				bendPoints.addAll(connPart.getBendpoints());

				if (connLayout.getLabelX().content(false) != null && 
						connLayout.getLabelY().content(false) != null) {
					Point labelPos = new Point(connLayout.getLabelX().content(), 
							connLayout.getLabelY().content());
					connPart.setLabelPosition(labelPos);
				}
			}
		}

		// Listen on existing connection parts
		for (DiagramConnectionPart connPart : connService.list()) {
			connPart.attach(this.connectionPartListener);
		}
	}

	public void save() {
		if (layoutModel == null) {
			return;
		}
		addNodeBoundsToModel();
		addConnectionsToModel();
		refreshPersistedPartsCache();
		try	{
			layoutModel.resource().save();
		} catch (ResourceStoreException rse) {
			Sapphire.service(LoggingService.class).log(rse);
		}
		// Clear the dirty state
		dirty = false;
	}

	private void read(DiagramNodePart nodePart) {
		String id = nodePart.getId();
		if (this.nodeBounds.containsKey(id) && this.nodeBounds.get(id) != null)	{
			nodePart.setNodeBounds(this.nodeBounds.get(id));
		}
	}

	private void write(DiagramNodePart nodePart) {
		refreshDirtyState();
	}

	public DiagramConnectionInfo read(DiagramConnectionPart connPart) {
		ConnectionHashKey key = ConnectionHashKey.createKey(connPart);
		if (this.connectionBendPoints.containsKey(key))	{
			DiagramConnectionInfo connectionInfo =
					new DiagramConnectionInfo(this.connectionBendPoints.get(key),this.connectionLabelPositions.get(key));
			return connectionInfo;
		} else {
			return null;
		}
	}

	private void write(DiagramConnectionPart connPart) {
		refreshDirtyState();
	}

	private void setGridVisible(boolean visible) {
		if (this.layoutModel != null) {
			this.layoutModel.getGridLayout().setVisible(visible);
		}
	}

	private void setGuidesVisible(boolean visible) {	
		if (this.layoutModel != null) {
			this.layoutModel.getGuidesLayout().setVisible(visible);
		}
	}

	private void addNodeBoundsToModel() {
		this.layoutModel.getDiagramNodesLayout().clear();
		for (DiagramNodeTemplate nodeTemplate : context( SapphireDiagramEditorPagePart.class ).getNodeTemplates()) {
			for (DiagramNodePart nodePart : nodeTemplate.getDiagramNodes()) {
				String nodeId = nodePart.getId();
				DiagramNodeLayout diagramNode = this.layoutModel.getDiagramNodesLayout().insert();
				diagramNode.setNodeId(nodeId);
				DiagramNodeBounds bounds = nodePart.getNodeBounds();
				diagramNode.setX(bounds.getX());
				diagramNode.setY(bounds.getY());
				if (nodePart.canResizeShape()) {
					IDiagramNodeDef nodeDef = (IDiagramNodeDef)nodePart.definition();
					if (bounds.getHeight() != -1 &&
							((nodeDef.getHeight().content() != null && nodeDef.getHeight().content() != bounds.getHeight()) ||
									nodeDef.getHeight().content() == null)) {
						diagramNode.setHeight(bounds.getHeight());
					}
					if (bounds.getWidth() != -1 && 
							((nodeDef.getWidth().content() != null && nodeDef.getWidth().content() != bounds.getWidth()) ||
									nodeDef.getWidth().content() == null)) {
						diagramNode.setWidth(bounds.getWidth());
					}
				}
				// save the embedded connection bendpoints
				diagramNode.getEmbeddedConnectionsLayout().clear();
				if (nodePart.getDiagramNodeTemplate().getEmbeddedConnectionTemplate() != null) {
					List</*Standard*/DiagramConnectionPart> connParts = getEmbedded(nodePart);
					for (/*Standard*/DiagramConnectionPart connPart : connParts) {
						String connId = connPart.getId();

						DiagramConnectionLayout conn = null;
						List<Point> connBendPoints = connPart.getBendpoints();
						if (connBendPoints.size() > 0) {	
							conn = diagramNode.getEmbeddedConnectionsLayout().insert();
							conn.setConnectionId(connId);
							for (Point pt : connBendPoints)	{
								DiagramBendPointLayout pt2 = conn.getConnectionBendpoints().insert();
								pt2.setX(pt.getX());
								pt2.setY(pt.getY());
							}
						}
						if (connPart.getLabel() != null && connPart.getLabelPosition() != null) {
							if (conn == null) {
								conn = diagramNode.getEmbeddedConnectionsLayout().insert();
								conn.setConnectionId(connId);
							}
							conn.setLabelX(connPart.getLabelPosition().getX());
							conn.setLabelY(connPart.getLabelPosition().getY());
						}
					}
				}
			}
		}
	}

	private List<DiagramConnectionPart> getEmbedded(DiagramNodePart nodePart) {
		// Restricted!
//		List<StandardDiagramConnectionPart> connParts = 
//				nodePart.getDiagramNodeTemplate().getEmbeddedConnectionTemplate().getDiagramConnections(nodeElement);
		List</*Standard*/DiagramConnectionPart> connParts = new ArrayList<DiagramConnectionPart>();
		ConnectionService connService = context(SapphireDiagramEditorPagePart.class).service(ConnectionService.class);
		List<DiagramConnectionPart> list = connService.list();
		for (DiagramConnectionPart c: list) {
			if(c.getEndpoint1() == nodePart.getLocalModelElement()
					&& isEmbedded(c)) {
				connParts.add(c);
			}
		}

		return connParts;
	}

	private boolean isEmbedded(DiagramConnectionPart c) {
		/*(connPart instanceof StandardEmbeddedConnectionPart)*/
		return c != null && "org.eclipse.sapphire.ui.diagram.internal.StandardEmbeddedConnectionPart".equals(c.getClass().getName());
	}

	private void addConnectionsToModel() {
		layoutModel.getDiagramConnectionsLayout().clear();
		ConnectionService connService = context(SapphireDiagramEditorPagePart.class).service(ConnectionService.class);
		for (DiagramConnectionPart connPart : connService.list()) {
			if (!connPart.removable() || isEmbedded(connPart))
				continue;
			String id = connPart.getId();
			DiagramConnectionLayout conn = null;

			List<Point> connBendPoints = connPart.getBendpoints();
			if (connBendPoints.size() > 0) {
				conn = layoutModel.getDiagramConnectionsLayout().insert();
				conn.setConnectionId(id);
				for (Point pt : connBendPoints) {
					DiagramBendPointLayout pt2 = conn.getConnectionBendpoints().insert();
					pt2.setX(pt.getX());
					pt2.setY(pt.getY());
				}
			}
			if (connPart.getLabel() != null && connPart.getLabelPosition() != null) {
				if (conn == null) {
					conn = this.layoutModel.getDiagramConnectionsLayout().insert();
					conn.setConnectionId(id);
				}
				conn.setLabelX(connPart.getLabelPosition().getX());
				conn.setLabelY(connPart.getLabelPosition().getY());
			}
		}
	}

	private void addNodeToPersistenceCache(DiagramNodePart nodePart) {
		String nodeId = nodePart.getId();
		nodeBounds.put(nodeId, nodePart.getNodeBounds());
	}

	private void addConnectionToPersistenceCache(DiagramConnectionPart connPart) {
		ConnectionHashKey connKey = ConnectionHashKey.createKey(connPart);
		this.connectionBendPoints.put(connKey, connPart.getBendpoints());
		if (connPart.getLabel() != null && connPart.getLabelPosition() != null) {
			connectionLabelPositions.put(connKey, new Point(connPart.getLabelPosition()));
		}
	}

	private void refreshPersistedPartsCache() {
		nodeBounds.clear();
		connectionBendPoints.clear();
		ConnectionService connService = context(SapphireDiagramEditorPagePart.class).service(ConnectionService.class);
		for (DiagramConnectionPart connPart : connService.list()) {
			if (connPart.removable()) {
				addConnectionToPersistenceCache(connPart);
			}
		}
		for (DiagramNodePart nodePart : context( SapphireDiagramEditorPagePart.class ).getNodes()) {
			addNodeToPersistenceCache(nodePart);
		}
	}

	private void addDiagramPartListener() {
		diagramEditorPagePartListener = new Listener() {
			@Override
			public void handle( final Event event ) {
				if ( event instanceof DiagramNodeEvent ) {
					handleDiagramNodeEvent((DiagramNodeEvent)event);
				} else if ( event instanceof DiagramPageEvent ) {
					handleDiagramPageEvent((DiagramPageEvent)event);
				} else if (event instanceof PreAutoLayoutEvent) {
					autoLayout = true;
				} else if (event instanceof PostAutoLayoutEvent) {
					autoLayout = false;
				} else if (event instanceof ConnectionAddEvent) {
					handleConnectionAddEvent((ConnectionAddEvent)event);
				} else if (event instanceof ConnectionDeleteEvent) {
					handleConnectionDeleteEvent((ConnectionDeleteEvent)event);
				}
			}
		};
		context(SapphireDiagramEditorPagePart.class).attach(diagramEditorPagePartListener);
	}

	private void handleDiagramNodeEvent(DiagramNodeEvent event) {
		DiagramNodePart nodePart = (DiagramNodePart)event.part();
		if (event instanceof DiagramNodeAddEvent) {
			read(nodePart);
		} else if (event instanceof DiagramNodeDeleteEvent) {
			refreshDirtyState();
		} else if (event instanceof DiagramNodeMoveEvent) {
			DiagramNodeBounds nodeBounds = nodePart.getNodeBounds();
			if (nodeBounds.isAutoLayout()) {
				// need to add the node bounds to the persistence cache so that "revert" could work
				addNodeToPersistenceCache(nodePart);
				refreshDirtyState();
			} else if (!nodeBounds.isDefaultPosition()) {
				write((DiagramNodePart)event.part());
			}
		}
	}

	protected void handleConnectionAddEvent(ConnectionAddEvent event) {
		DiagramConnectionPart connPart = event.part();
		connPart.attach(this.connectionPartListener);
		DiagramConnectionInfo connInfo = read(connPart);
		if (connInfo != null) {
			connPart.resetBendpoints(connInfo.getBendPoints());
			if (connInfo.getLabelPosition() != null) {
				connPart.setLabelPosition(connInfo.getLabelPosition());
			}
		}
	}

	protected void handleConnectionDeleteEvent(ConnectionDeleteEvent event) {
		refreshDirtyState();
	}

	private void handleDiagramPageEvent(DiagramPageEvent event) {
		SapphireDiagramEditorPagePart diagramPart = (SapphireDiagramEditorPagePart)event.part();
		switch(event.getDiagramPageEventType()) {
			case GridStateChange:
				setGridVisible(diagramPart.isGridVisible());
				break;
			case GuideStateChange:
				setGuidesVisible(diagramPart.isShowGuides());
				break;
			case DiagramSave:
				save();
				break;
			default:
				break;
		}
	}

	private boolean isNodeLayoutChanged(DiagramNodePart nodePart) {
		DiagramNodeBounds newBounds = nodePart.getNodeBounds();
		boolean changed = false;
		String nodeId = nodePart.getId();
		if (nodeBounds.containsKey(nodeId)) {
			DiagramNodeBounds oldBounds = this.nodeBounds.get(nodeId);
			if (!newBounds.equals(oldBounds)) {
				changed = true;
			}
		} else {
			changed = true;
		}
		return changed;
	}

	private boolean isConnectionLayoutChanged(DiagramConnectionPart connPart) {
		// Detect whether the connection bendpoints or connection label have been changed.
		List<Point> bendpoints = connPart.getBendpoints();
		ConnectionHashKey key = ConnectionHashKey.createKey(connPart);
		boolean changed = false;
		if (this.connectionBendPoints.containsKey(key)) {
			List<Point> oldBendpoints = this.connectionBendPoints.get(key);
			if (bendpoints.size() != oldBendpoints.size()) {
				changed = true;
			} else {
				for (int i = 0; i < bendpoints.size(); i++) {
					Point newPt = bendpoints.get(i);
					Point oldPt = oldBendpoints.get(i);
					if (newPt.getX() != oldPt.getX() || newPt.getY() != oldPt.getY()) {
						changed = true;
						break;
					}
				}
			}
			if (!changed) {
				if (connPart.getLabel() != null) {
					Point newPos = connPart.getLabelPosition();
					Point oldPos = this.connectionLabelPositions.get(key);
					if ((newPos == null && oldPos != null) ||
							(newPos != null && oldPos == null) ||
							(newPos != null && oldPos != null && !newPos.equals(oldPos))) {
						changed = true;
					}
				}
			}
		} else {
			changed = true;
		}
		return changed;
	}

	private boolean isDiagramLayoutChanged() {
		boolean changed = false;
		if (!context(SapphireDiagramEditorPagePart.class).disposed()) {
			for (DiagramNodePart nodePart : context( SapphireDiagramEditorPagePart.class ).getNodes()) {
				if (!nodePart.getLocalModelElement().disposed() && isNodeLayoutChanged(nodePart)) {
					changed = true;
					break;
				}
			}
			ConnectionService connService = context(SapphireDiagramEditorPagePart.class).service(ConnectionService.class);
			for (DiagramConnectionPart connPart : connService.list()) {
				if (!connPart.getLocalModelElement().disposed() && connPart.removable() &&
						isConnectionLayoutChanged(connPart)) {
					changed = true;
					break;
				}
			}
		}
		return changed;
	}

	@Override
	public boolean dirty() {
		return dirty;
	}

	private void refreshDirtyState() {
		boolean after = isDiagramLayoutChanged();
		if( this.dirty != after ) {
			boolean before = this.dirty;
			this.dirty = after;
			broadcast(new DirtyStateEvent(this, before, after));
		}
	}

	private DiagramConnectionPart getConnectionPart(ConnectionService connService, ConnectionHashKey connId) {
		if (connectionIdMap == null) {
			connectionIdMap = new HashMap<ConnectionHashKey, DiagramConnectionPart>();
			for (DiagramConnectionPart connPart : connService.list()) {
				connectionIdMap.put(ConnectionHashKey.createKey(connPart), connPart);
			}
		}
		return connectionIdMap.get(connId);
	}

	private DiagramNodePart getNodePart(String nodeId) {
		if (nodeIdMap == null) {
			nodeIdMap = new HashMap<String, DiagramNodePart>();
			for (DiagramNodePart nodePart : context( SapphireDiagramEditorPagePart.class ).getNodes()) {
				nodeIdMap.put(nodePart.getId(), nodePart);
			}
		}
		return nodeIdMap.get(nodeId);
	}

}

class FileResourceStore2 extends ByteArrayResourceStore {
	static final String START = "{";
	static final String NAME_END = "-";
	static final String END = "}";
	static final String ROOT = "Root";

	private final File file;
	private String flowName;
	private Job job;

	public FileResourceStore2(final File file, String flowName, Job job) throws ResourceStoreException {
		this.file = file;
		this.flowName = flowName;
		this.job = job;

		if(this.file.exists() ) {
			String contents = FileUtil.readFile(file);
			int i = contents.indexOf(START + flowName + NAME_END);
			int j = (i < 0) ? -1 : contents.indexOf(END, i);
			if(j > 0) {
				String c = contents.substring(i + flowName.length() + 2, j);
				setContents(new ByteArrayInputStream(c.getBytes()));
			}
		}
	}

	public File getFile() {
		return this.file;
	}

	@Override
	public void save() throws ResourceStoreException {
		validateSave();

		try {
			mkdirs( this.file.getParentFile() );
		} catch( final IOException e ) {
			throw new ResourceStoreException( e );
		}

		String contents = FileUtil.readFile(file);
		contents = cleanContents(contents);
		try( OutputStream out = new FileOutputStream( this.file ) ) {
			int i = contents.indexOf(START + flowName + NAME_END);
			int j = (i < 0) ? -1 : contents.indexOf(END, i);
			if(j > 0) {
				contents = contents.substring(0, i + flowName.length() + 2) + new String(getContents()) + contents.substring(j);
			} else {
				contents = contents + "\n" + START + flowName + NAME_END + new String(getContents()) + END + "\n";
			}
			out.write(contents.getBytes());
			out.flush();
		} catch( final IOException e ) {
			throw new ResourceStoreException( e );
		}
	}

	@Override
	public void validateSave() {
		if(this.file.exists()) {
			if(!this.file.canWrite()) {
				// TODO: Add conditional call to Java 6 specific setWritable API.

				//if( ! this.file.setWritable( true ) )
				//{
				//	throw new ValidateEditException();
				//}
				throw new ValidateEditException();
			}
		}
	}

	@Override
	public <A> A adapt( final Class<A> adapterType ) {
		if(adapterType == File.class) {
			return adapterType.cast(this.file);
		} else {
			return super.adapt( adapterType );
		}
	}

	String cleanContents(String contents) {
		Set<String> flows = collectFlowIds(job);
		StringBuffer result = new StringBuffer();
		int i = 0;
		while(contents.indexOf(START, i) >= 0) {
			int i1 = contents.indexOf(START, i);
			int i2 = contents.indexOf(NAME_END, i1);
			int i3 = contents.indexOf(END, i1);
			if(i3 < 0) i3 = contents.length();
			String flow = contents.substring(i1 + 1, i2);
			if(flows.contains(flow)) {
				result.append(contents.substring(i, i3 + 1));
			}
			i = i3 + 1;
		}
		result.append(contents.substring(i));
		return result.toString();
	}

	Set<String> collectFlowIds(Job job) {
		Set<String> result = new HashSet<String>();
		result.add(ROOT);
		collectFlowIds(job.getFlowElements(), result);
		return result;
	}

	void collectFlowIds(ElementList<FlowElement> es, Set<String> result) {
		for (FlowElement f: es) {
			if(f instanceof Flow) {
				result.add(f.getId().content());
				collectFlowIds(((Flow) f).getFlowElements(), result);
			}
		}
	}
}
