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
import org.jboss.tools.batch.ui.editor.internal.model.FlowElement;
import org.jboss.tools.batch.ui.editor.internal.model.FlowElementsContainer;

/**
 * Moves the target element of this action to the first place of its enclosing
 * list of {@link FlowElement}s which causes that it will be executed as first
 * during the execution of the {@link Job} or {@link Flow}.
 * 
 * @author Tomas Milata
 *
 */
public class SetStartActionHandler extends SapphireActionHandler {

	@Override
	protected Object run(Presentation context) {

		Element element = context.part().getModelElement();
		FlowElementsContainer parent = (FlowElementsContainer) element.parent().element();

		parent.getFlowElements().move(element, 0);

		return null;
	}

}
