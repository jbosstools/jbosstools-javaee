/******************************************************************************* 
 * Copyright (c) 2015 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.batch.ui.itest.diagram;

import java.io.File;
import java.util.List;

import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ui.diagram.DiagramConnectionPart;
import org.jboss.tools.batch.ui.editor.internal.model.FlowElement;
import org.jboss.tools.batch.ui.editor.internal.model.Step;
import org.jboss.tools.batch.ui.editor.internal.services.diagram.connection.BatchDiagramConnectionService;
import org.jboss.tools.batch.ui.editor.internal.services.diagram.layout.persistence.BatchDiagramLayoutPersistenceService;
import org.jboss.tools.batch.ui.itest.AbstractBatchSapphireEditorTest;
import org.jboss.tools.common.util.FileUtil;

/**
 * @author Viacheslav Kabanovich
 */
public class BatchDiagramLayoutTest extends AbstractBatchSapphireEditorTest {

	public void testAddAndRemoveConnection() throws Exception {
		editor = openEditor("src/META-INF/batch-jobs/job-layout.xml");

		BatchDiagramConnectionService cs = getConnectionService();
		List<DiagramConnectionPart> connections = cs.list();
		assertEquals(0, connections.size());

		ElementList<FlowElement> elements = editor.getSchema().getFlowElements();
		Step step1 = (Step) elements.get(0);
		Step step2 = (Step) elements.get(1);
		step1.setNext(step2.getId().content());

		connections = cs.list();
		assertEquals(1, connections.size());
		
		DiagramConnectionPart c = connections.get(0);
		c.addBendpoint(0, 101, 102);

		BatchDiagramLayoutPersistenceService s = getDiagramPage().service(BatchDiagramLayoutPersistenceService.class);
		
		assertNotNull(s);
		File file = s.getLayoutPersistenceFile();
		
		assertNotNull(s);
		s.save();
		assertTrue(file.exists());
		
		String content = FileUtil.readFile(file);
		assertTrue(content.indexOf("x=\"101\"") > 0);
		assertTrue(content.indexOf("y=\"102\"") > 0);
		
		file.delete();
	}


	private BatchDiagramConnectionService getConnectionService() {
		BatchDiagramConnectionService connService = getDiagramPage().service(BatchDiagramConnectionService.class);
		assertNotNull(connService);
		return connService;
	}

}
