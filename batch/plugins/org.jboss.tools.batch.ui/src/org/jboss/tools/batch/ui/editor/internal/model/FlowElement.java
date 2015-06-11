/*************************************************************************************
 * Copyright (c) 2014 Red Hat, Inc. and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     JBoss by Red Hat - Initial implementation.
 ************************************************************************************/
package org.jboss.tools.batch.ui.editor.internal.model;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public interface FlowElement extends Element {

	ElementType TYPE = new ElementType( FlowElement.class );
	
	@Label( standard = "id" )
	@XmlBinding( path = "@id" )
	@Required
	@Service(impl = IdValidationService.class)

	ValueProperty PROP_ID = new ValueProperty( TYPE, "Id" );

	Value<String> getId();
	void setId( String id);

}
