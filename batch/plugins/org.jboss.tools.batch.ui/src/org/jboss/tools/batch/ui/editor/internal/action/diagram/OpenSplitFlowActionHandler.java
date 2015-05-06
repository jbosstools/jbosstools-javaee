/******************************************************************************* 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Tomas Milata - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.batch.ui.editor.internal.action.diagram;

import org.eclipse.sapphire.ui.Presentation;
import org.eclipse.sapphire.ui.SapphireActionHandler;
import org.eclipse.sapphire.ui.swt.gef.presentation.RectanglePresentation;
import org.jboss.tools.batch.ui.editor.internal.model.Flow;
import org.jboss.tools.batch.ui.editor.internal.model.JobXMLEditor;
import org.jboss.tools.batch.ui.editor.internal.model.Split;

/**
 * Handler for 'Open flow' action, i.e. an action that replaces current Batch
 * diagram editor input by a {@link Flow} which is contained in a {@link Split}.
 * See the similar {@link OpenFlowActionHandler}.
 * 
 * @author Tomas Milata
 */
public class OpenSplitFlowActionHandler extends SapphireActionHandler {

	@Override
	protected Object run(Presentation context) {
		RectanglePresentation diagramNodePresentation = (RectanglePresentation) context;
		JobXMLEditor editor = (JobXMLEditor) diagramNodePresentation.getConfigurationManager().getDiagramEditor()
				.getEditor();

		Flow flow = (Flow) context.part().getModelElement();
		editor.changeDiagramContent(flow);

		return null;
	}

}
