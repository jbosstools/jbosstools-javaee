/******************************************************************************* 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Tomas Milata - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.batch.ui.editor.internal.action.diagram;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ui.Presentation;
import org.eclipse.sapphire.ui.SapphireActionHandler;
import org.eclipse.sapphire.ui.swt.gef.presentation.DiagramPagePresentation;
import org.jboss.tools.batch.ui.editor.internal.model.Flow;
import org.jboss.tools.batch.ui.editor.internal.model.FlowElementsContainer;
import org.jboss.tools.batch.ui.editor.internal.model.JobXMLEditor;
import org.jboss.tools.batch.ui.editor.internal.model.Split;

/**
 * Returns current content of the Batch diagram editor back to the parent
 * {@link FlowElementsContainer} of the current model element (a {@link Flow}).
 * If the flow is contained in a {@link Split}, the content is set directly to
 * to parent of the split.
 * 
 * @author Tomas Milata
 *
 */
public class OpenParentActionHandler extends SapphireActionHandler {

	@Override
	protected Object run(Presentation context) {
		DiagramPagePresentation diagramNodePresentation = (DiagramPagePresentation) context;
		JobXMLEditor editor = (JobXMLEditor) diagramNodePresentation.getConfigurationManager().getDiagramEditor()
				.getEditor();

		Flow flow = (Flow) context.part().getModelElement();

		Element parent = flow.parent().element();
		if (parent instanceof Split) {
			// If the flow belongs to a split, we want to open split's parent.
			parent = (FlowElementsContainer) parent.parent().element();
		}
		editor.changeDiagramContent((FlowElementsContainer) parent);

		return null;
	}

}
