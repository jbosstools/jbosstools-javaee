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
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public interface Properties extends Element {

	ElementType TYPE = new ElementType( Properties.class );

	@Label( standard = "partition" )
	@XmlBinding( path = "@partition" )

	ValueProperty PROP_PARTITION = new ValueProperty( TYPE, "Partition" );

	Value<String> getPartition();
	void setPartition( String partition);


	@Type( base = Property.class )
	@Label( standard = "properties" )
	@XmlListBinding( mappings = @XmlListBinding.Mapping( element = "property", type = Property.class ) )

	ListProperty PROP_PROPERTIES = new ListProperty( TYPE, "Properties" );

	ElementList<Property> getProperties();

}
