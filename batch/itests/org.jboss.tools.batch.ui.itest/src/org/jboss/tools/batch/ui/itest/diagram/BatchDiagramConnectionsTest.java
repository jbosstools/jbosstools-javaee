/******************************************************************************* 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Tomas Milata - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.batch.ui.itest.diagram;

import java.util.List;

import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ui.diagram.DiagramConnectionPart;
import org.jboss.tools.batch.ui.editor.internal.model.FlowElement;
import org.jboss.tools.batch.ui.editor.internal.model.Step;
import org.jboss.tools.batch.ui.editor.internal.services.diagram.connection.BatchDiagramConnectionService;
import org.jboss.tools.batch.ui.itest.AbstractBatchSapphireEditorTest;

/**
 * @author Tomas Milata
 */
public class BatchDiagramConnectionsTest extends AbstractBatchSapphireEditorTest {

	public void testNextAttributeConnections() {
		checkConnections("src/META-INF/batch-jobs/job-connections-next-step.xml", 4);
		checkConnections("src/META-INF/batch-jobs/job-connections-next-flow.xml", 4);
		checkConnections("src/META-INF/batch-jobs/job-connections-next-split.xml", 4);
	}

	public void testAddAndRemoveConnection() {
		editor = openEditor("src/META-INF/batch-jobs/job-connections-no-connections.xml");

		BatchDiagramConnectionService cs = getConnectionService();
		List<DiagramConnectionPart> connections = cs.list();
		assertEquals(0, connections.size());

		ElementList<FlowElement> elements = editor.getSchema().getFlowElements();
		Step step1 = (Step) elements.get(0);
		Step step2 = (Step) elements.get(1);
		step1.setNext(step2.getId().content());

		connections = cs.list();
		assertEquals(1, connections.size());

		step1.setNext(null);

		connections = cs.list();
		assertEquals(0, connections.size());
	}

	public void testConnectionsOnNewNodes() {
		editor = openEditor("src/META-INF/batch-jobs/job-connections-empty.xml");

		BatchDiagramConnectionService cs = getConnectionService();
		List<DiagramConnectionPart> connections = cs.list();
		assertEquals(0, connections.size());

		ElementList<FlowElement> elements = editor.getSchema().getFlowElements();
		Step step1 = elements.insert(Step.class);
		Step step2 = elements.insert(Step.class);
		step1.setId("step1");
		step2.setId("step2");
		step1.setNext("step2");

		connections = cs.list();
		assertEquals(1, connections.size());

		step1.setNext(null);

		connections = cs.list();
		assertEquals(0, connections.size());
	}

	private void checkConnections(String file, int count) {
		editor = openEditor(file);

		BatchDiagramConnectionService cs = getConnectionService();
		List<DiagramConnectionPart> connections = cs.list();
		assertNotNull(connections);
		assertEquals(count, connections.size());
	}

	private BatchDiagramConnectionService getConnectionService() {
		BatchDiagramConnectionService connService = getDiagramPage().service(BatchDiagramConnectionService.class);
		assertNotNull(connService);
		return connService;
	}

}
