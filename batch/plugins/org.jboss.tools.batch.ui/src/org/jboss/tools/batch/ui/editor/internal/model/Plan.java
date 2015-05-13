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
@Label( standard = "plan" )
public interface Plan extends Element {

	ElementType TYPE = new ElementType( Plan.class );

	@Label( standard = "partitions" )
	@XmlBinding( path = "@partitions" )

	ValueProperty PROP_PARTITIONS = new ValueProperty( TYPE, "Partitions" );

	Value<String> getPartitions();
	void setPartitions( String value);

	@Label( standard = "threads" )
	@XmlBinding( path = "@threads" )

	ValueProperty PROP_THREADS = new ValueProperty( TYPE, "Threads" );

	Value<String> getThreads();
	void setThreads( String value);

	@Type( base = Properties.class )
	@Label( standard = "properties" )
	@XmlListBinding( mappings = @XmlListBinding.Mapping( element = "properties", type = Properties.class ) )

	ListProperty PROP_PROPERTIES = new ListProperty( TYPE, "Properties" );

	ElementList<Properties> getProperties();
}
