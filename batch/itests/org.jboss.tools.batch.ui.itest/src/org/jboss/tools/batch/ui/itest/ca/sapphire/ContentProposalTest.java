/******************************************************************************* 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Tomas Milata - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.batch.ui.itest.ca.sapphire;

import org.eclipse.sapphire.ElementList;
import org.jboss.tools.batch.ui.editor.internal.model.Batchlet;
import org.jboss.tools.batch.ui.editor.internal.model.Chunk;
import org.jboss.tools.batch.ui.editor.internal.model.FlowElement;
import org.jboss.tools.batch.ui.editor.internal.model.RefAttributeElement;
import org.jboss.tools.batch.ui.editor.internal.model.Step;
import org.jboss.tools.batch.ui.editor.internal.services.contentproposal.RefProposalService;
import org.jboss.tools.batch.ui.itest.AbstractBatchSapphireEditorTest;

/**
 * @author Tomas Milata
 */
public class ContentProposalTest extends AbstractBatchSapphireEditorTest {

	public void test() {
		editor = openEditor("/src/META-INF/batch-jobs/job-ca-2.xml");

		ElementList<FlowElement> elements = editor.getSchema().getFlowElements();
		for (FlowElement flowElement : elements) {
			if (flowElement.getId().content().equals("step1")) {
				checkStep1((Step) flowElement);
			} else if (flowElement.getId().content().equals("step2")) {
				checkStep2((Step) flowElement);
			}
		}

	}

	private void checkStep1(Step step) {
		checkProposals((Batchlet) step.getBatchletOrChunk().get(0), 5);
	}

	private void checkStep2(Step step) {
		Chunk chunk = (Chunk) step.getBatchletOrChunk().get(0);

		checkProposals(chunk.getReader(), 2);

		checkProposals(chunk.getWriter(), 2);

		checkProposals(chunk.getProcessor().content(), 1);
	}

	private void checkProposals(RefAttributeElement element, int count) {
		RefProposalService service = element.getRef().service(RefProposalService.class);
		assertNotNull(service);
		assertEquals(count, service.session().proposals().size());
	}
}
