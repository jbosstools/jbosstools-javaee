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
import org.eclipse.sapphire.ui.swt.gef.presentation.DiagramNodePresentation;
import org.jboss.tools.batch.ui.editor.internal.model.Flow;
import org.jboss.tools.batch.ui.editor.internal.model.JobXMLEditor;
import org.jboss.tools.batch.ui.editor.internal.model.Step;

/**
 * Handler for 'Open flow' action, i.e. an action that replaces current Batch
 * diagram editor input by a {@link Flow} which is contained in a {@link Step}
 * or another {@link Flow}, unlike the similar
 * {@link OpenSplitFlowActionHandler}.
 * 
 * @author Tomas Milata
 */
public class OpenFlowActionHandler extends SapphireActionHandler {

	@Override
	protected Object run(Presentation context) {
		DiagramNodePresentation diagramNodePresentation = (DiagramNodePresentation) context;
		JobXMLEditor editor = (JobXMLEditor) diagramNodePresentation.getConfigurationManager().getDiagramEditor()
				.getEditor();

		Flow flow = (Flow) context.part().getModelElement();
		editor.changeDiagramContent(flow);

		return null;
	}

}
