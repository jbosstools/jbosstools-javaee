/*************************************************************************************
 * Copyright (c) 2014 Red Hat, Inc. and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     JBoss by Red Hat - Initial implementation.
 *     Tomas Milata - Added Batch diagram editor (JBIDE-19717).
 ************************************************************************************/
package org.jboss.tools.batch.ui.editor.internal.model;

import org.eclipse.sapphire.ElementReference;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ReferenceValue;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.Image;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Reference;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
@Label(standard = "next")
@Image(path = "next.png")
@XmlBinding(path = "next")

public interface Next extends OutcomeElement {

	ElementType TYPE = new ElementType(Next.class);

	@Label(standard = "to")
	@XmlBinding(path = "@to")
	@Required
	@Reference(target = FlowElement.class)
	// The referenced element is one of parent's parent's flow elements.
	@ElementReference(list = "../../FlowElements", key = "id")

	ValueProperty PROP_TO = new ValueProperty(TYPE, "To");

	/**
	 * @return The referenced flow element. The target may be also the parent
	 *         element of this next element itself (Loops are not forbidden
	 *         here).
	 */
	ReferenceValue<String, FlowElement> getTo();

	void setTo(String value);

}
