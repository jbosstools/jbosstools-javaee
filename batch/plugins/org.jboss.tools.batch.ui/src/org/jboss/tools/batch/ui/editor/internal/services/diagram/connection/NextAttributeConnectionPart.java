/******************************************************************************* 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Tomas Milata - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.batch.ui.editor.internal.services.diagram.connection;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.Event;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.ReferenceValue;
import org.eclipse.sapphire.services.ReferenceService;
import org.eclipse.sapphire.ui.Point;
import org.eclipse.sapphire.ui.SapphireActionSystem;
import org.eclipse.sapphire.ui.diagram.ConnectionAddEvent;
import org.eclipse.sapphire.ui.diagram.ConnectionBendpointsEvent;
import org.eclipse.sapphire.ui.diagram.ConnectionDeleteEvent;
import org.eclipse.sapphire.ui.diagram.ConnectionEndpointsEvent;
import org.eclipse.sapphire.ui.diagram.DiagramConnectionPart;
import org.eclipse.sapphire.ui.diagram.def.IDiagramConnectionDef;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodePart;
import org.jboss.tools.batch.ui.editor.internal.model.FlowElement;
import org.jboss.tools.batch.ui.editor.internal.model.NextAttributeElement;

/**
 * A custom implementation of Sapphire connection part to be used for connection
 * using {@code next} attribute in the {@link BatchDiagramConnectionService}.
 * 
 * @author Tomas Milata
 */
public class NextAttributeConnectionPart extends DiagramConnectionPart {

	private NextAttributeElement srcElement;
	private FlowElement targetElement;

	private List<Point> bendpoints = new ArrayList<>();

	private BatchDiagramConnectionEventHandler eventHandler;
	private Listener listener;

	private ReferenceService<?> referenceService;
	private BatchDiagramConnectionService connectionService;

	/**
	 * 
	 * @param node1
	 *            a {@code <step>}, {@code <split>} or {@code <flow>}
	 * @param node2
	 *            a {@code <step>}, {@code <split>}, {@code <flow>} or a
	 *            {@code <decision>}
	 * @param service
	 *            a connection service that uses this part
	 * @param eventHandler
	 *            for notification about changes
	 * @param srcElement
	 * @param targetElement
	 */
	public NextAttributeConnectionPart(NextAttributeElement srcElement, FlowElement targetElement,
			BatchDiagramConnectionService service, BatchDiagramConnectionEventHandler eventHandler) {
		this.srcElement = srcElement;
		this.targetElement = targetElement;
		this.connectionService = service;
		this.eventHandler = eventHandler;
	}

	@Override
	protected void init() {
		initializeListeners();

		eventHandler.onConnectionAddEvent(new ConnectionAddEvent(this));
	}

	/**
	 * Initializes listeners for changes of the target element.
	 */
	private void initializeListeners() {
		ReferenceValue<String, FlowElement> reference = srcElement.getNext();

		// may be null if id was not entered yet
		if (reference.target() != null) {
			// refresh, otherwise the reference changed event not fired
			reference.target().refresh();
		}
		referenceService = reference.service(ReferenceService.class);
		listener = new Listener() {
			@Override
			public void handle(Event event) {
				FlowElement newTarget = srcElement.getNext().target();

				if (newTarget == null) {
					referenceService.detach(this);
					eventHandler.onConnectionDeleteEvent(new ConnectionDeleteEvent(NextAttributeConnectionPart.this));
				} else if (newTarget != targetElement) {
					changeTargetElement(newTarget);
				}
			}
		};
		referenceService.attach(listener);
	}

	private void changeTargetElement(FlowElement newTarget) {
		targetElement = newTarget;
		srcElement.setNext(targetElement.getId().content());

		removeAllBendpoints();

		eventHandler.onConnectionEndpointsEvent(new ConnectionEndpointsEvent(this));
	}

	/**
	 * Declares action context so contexts so that actions are allowed for this
	 * part.
	 */
	@Override
	public Set<String> getActionContexts() {
		Set<String> contextSet = new HashSet<String>();
		contextSet.add(SapphireActionSystem.CONTEXT_DIAGRAM_CONNECTION);
		contextSet.add(SapphireActionSystem.CONTEXT_DIAGRAM_CONNECTION_HIDDEN);
		return contextSet;
	}

	@Override
	public boolean removable() {
		return true;
	}

	@Override
	public void remove() {
		srcElement.setNext(null);
		eventHandler.onConnectionDeleteEvent(new ConnectionDeleteEvent(this));
	}

	/**
	 * Creates a unique id using this part's id in the list of connections
	 * maintained by the connection service.
	 */
	@Override
	public String getId() {
		StringBuilder builder = new StringBuilder();
		builder.append(BachtConnectionIdConst.NEXT_ATTRIBUTE_CONNECTION_ID);
		builder.append(connectionService.list().indexOf(this));
		return builder.toString();
	}

	/**
	 * @return {@link BachtConnectionIdConst.NEXT_ATTRIBUTE_CONNECTION_ID}
	 */
	@Override
	public String getConnectionTypeId() {
		return BachtConnectionIdConst.NEXT_ATTRIBUTE_CONNECTION_ID;
	}

	@Override
	public IDiagramConnectionDef getConnectionDef() {
		return (IDiagramConnectionDef) definition;
	}

	@Override
	public DiagramConnectionPart reconnect(DiagramNodePart newSrc, DiagramNodePart newTargetNode) {
		changeTargetElement((FlowElement) newTargetNode.getLocalModelElement());
		return this;
	}

	/**
	 * Returns {@code false} as we do not need labels for this connection type.
	 * 
	 * @return {@code false}
	 */
	@Override
	public boolean canEditLabel() {
		return false;
	}

	@Override
	public List<Point> getBendpoints() {
		return new ArrayList<>(bendpoints);
	}

	@Override
	public void removeAllBendpoints() {
		bendpoints.clear();
		broadcast(new ConnectionBendpointsEvent(this));
	}

	@Override
	public void resetBendpoints(List<Point> bendpoints) {
		this.bendpoints = bendpoints;
		broadcast(new ConnectionBendpointsEvent(this, true));
	}

	@Override
	public void addBendpoint(int index, int x, int y) {
		bendpoints.add(index, new Point(x, y));
		broadcast(new ConnectionBendpointsEvent(this));
	}

	@Override
	public void updateBendpoint(int index, int x, int y) {
		bendpoints.set(index, new Point(x, y));
		broadcast(new ConnectionBendpointsEvent(this));
	}

	@Override
	public void removeBendpoint(int index) {
		bendpoints.remove(index);
		broadcast(new ConnectionBendpointsEvent(this));
	}

	/**
	 * Returns {@code null} as we do not need labels for this connection type.
	 * 
	 * @return {@code null}
	 */
	@Override
	public String getLabel() {
		return null;
	}

	/**
	 * Does nothing as we do not need labels for this connection type.
	 */
	@Override
	public void setLabel(String newValue) {
	}

	/**
	 * Returns {@code null} as we do not need labels for this connection type.
	 * 
	 * @return {@code null}
	 */
	@Override
	public Point getLabelPosition() {
		return null;
	}

	/**
	 * Does nothing as we do not need labels for this connection type.
	 */
	@Override
	public void setLabelPosition(Point newPos) {
	}

	@Override
	public Element getEndpoint1() {
		return srcElement;
	}

	@Override
	public Element getEndpoint2() {
		return targetElement;
	}

}
