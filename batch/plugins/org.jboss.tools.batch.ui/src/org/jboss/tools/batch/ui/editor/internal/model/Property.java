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
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public interface Property extends Element {

	ElementType TYPE = new ElementType( Property.class );

	@Label( standard = "name" )
	@XmlBinding( path = "@name" )

	ValueProperty PROP_NAME = new ValueProperty( TYPE, "Name" );

	Value<String> getName();
	void setName( String name);

	@Label( standard = "value" )
	@XmlBinding( path = "@value" )

	ValueProperty PROP_VALUE = new ValueProperty( TYPE, "Value" );

	Value<String> getValue();
	void setValue( String value);

}

