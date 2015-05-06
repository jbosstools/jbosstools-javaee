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
import org.eclipse.sapphire.ElementReference;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ReferenceValue;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Reference;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;

/**
 * Represents an element in the JSR-352 model which has a {@code next} attribute
 * referring to another flow element, which applies to a {@code <step>}, {@code 
 * <split>} and {@code <flow>}. The referenced flow elements are siblings of
 * this element.
 * 
 * @author Tomas Milata
 */
public interface NextAttributeElement extends Element {

	ElementType TYPE = new ElementType(NextAttributeElement.class);

	@Label(standard = "next")
	@XmlBinding(path = "@next")
	@Reference(target = FlowElement.class)
	// The referenced element is one of parent's flow elements.
	@ElementReference(list = "../FlowElements", key = "id")
	ValueProperty PROP_NEXT = new ValueProperty(TYPE, "Next");

	/**
	 * @return The referenced flow element. The target may be also the element
	 *         itself (Loops are not forbidden here).
	 */
	ReferenceValue<String, FlowElement> getNext();

	void setNext(String value);
}
