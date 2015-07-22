/******************************************************************************* 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Tomas Milata - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.batch.ui.editor.internal.model;

import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.annotations.Services;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.jboss.tools.batch.ui.editor.internal.services.contentproposal.RefProposalService;

public interface RefAttributeElement {
	ElementType TYPE = new ElementType(RefAttributeElement.class);

	@Label(standard = "ref")
	@XmlBinding(path = "@ref")
	@Required
	@Services({
		@Service(impl = RefProposalService.class),
		@Service(impl = RefValidationService.class)
	})
	ValueProperty PROP_REF = new ValueProperty(TYPE, "Ref");

	Value<String> getRef();

	void setRef(String ref);
}
