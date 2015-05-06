/******************************************************************************* 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Tomas Milata - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.batch.ui.editor.internal.action;

import org.eclipse.sapphire.ui.SapphireActionHandler;
import org.eclipse.sapphire.ui.SapphireActionHandlerFilter;
import org.jboss.tools.batch.ui.editor.internal.model.Batchlet;
import org.jboss.tools.batch.ui.editor.internal.model.BatchletOrChunk;
import org.jboss.tools.batch.ui.editor.internal.model.Chunk;
import org.jboss.tools.batch.ui.editor.internal.model.Step;

/**
 * Filters {@code Sapphire.Add.Batchlet} and {@code Sapphire.Add.Chunk} actions
 * on a {@link Step} node on condition that a {@link BatchletOrChunk} is already
 * present.
 * 
 * @author Tomas Milata
 */
public class AddBatchletOrChunkActionFilter extends SapphireActionHandlerFilter {

	private static final String SAPPHIRE_ADD = "Sapphire.Add.";
	private static final String SAPPHIRE_ADD_CHUNK = SAPPHIRE_ADD + Chunk.class.getSimpleName();
	private static final String SAPPHIRE_ADD_BATCHLET = SAPPHIRE_ADD + Batchlet.class.getSimpleName();

	/**
	 * @param handler
	 *            action handler
	 * @return {@code false} iff handler's id is {@code Sapphire.Add.Chunk} or
	 *         {@code Sapphire.Add.Batchlet} and handler's
	 *         {@link SapphireActionHandler#getModelElement()} is a {@link Step}
	 *         with size of {@link Step#getBatchletOrChunk()} list lower than 1.
	 */
	@Override
	public boolean check(SapphireActionHandler handler) {
		String id = handler.getId();

		if (id.equals(SAPPHIRE_ADD_BATCHLET) || id.equals(SAPPHIRE_ADD_CHUNK)) {
			if (handler.getModelElement() instanceof Step) {
				Step step = (Step) handler.getModelElement();
				return step.getBatchletOrChunk().size() < 1;
			}
		}
		return true;
	}

}
