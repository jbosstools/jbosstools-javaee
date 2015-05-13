/******************************************************************************* 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Tomas Milata - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.batch.ui.editor.internal.model;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;

/**
 * A FlowElementsContainer is an element that can contain any
 * {@link FlowElement}s. In JSR-352 model it corresponds to a {@code <job>} or a
 * {@code <flow>}. A {@code <split>} is not a flow element as it may contain
 * only flows and not any {@link FlowElement}s.
 * 
 * @author Tomas Milata
 */
public interface FlowElementsContainer extends Element {

	ElementType TYPE = new ElementType(FlowElementsContainer.class);

	@Type(base = FlowElement.class, possible = { Flow.class, Step.class, Decision.class, Split.class })
	@XmlListBinding(path = "")
	ListProperty PROP_FLOW_ELEMENTS = new ListProperty(TYPE, "FlowElements");

	ElementList<FlowElement> getFlowElements();

}
