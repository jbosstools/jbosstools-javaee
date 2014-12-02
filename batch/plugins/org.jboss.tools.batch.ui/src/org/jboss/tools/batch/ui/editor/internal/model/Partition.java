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
import org.eclipse.sapphire.ElementHandle;
import org.eclipse.sapphire.ElementProperty;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
@Label( standard = "partition" )
public interface Partition extends Element {

	ElementType TYPE = new ElementType( Partition.class );

	@Type( base = Mapper.class )
	@Label( standard = "mapper" )
	@XmlBinding( path = "mapper" )

	ElementProperty PROP_MAPPER = new ElementProperty( TYPE, "Mapper" );

	ElementHandle<Mapper> getMapper();

	@Type( base = Plan.class )
	@Label( standard = "plan" )
	@XmlBinding( path = "plan" )

	ElementProperty PROP_PLAN = new ElementProperty( TYPE, "Plan" );

	ElementHandle<Mapper> getPlan();

	@Type( base = ItemHandlingElement.class )
	@Label( standard = "collector" )
	@XmlBinding( path = "collector" )

	ElementProperty PROP_COLLECTOR = new ElementProperty( TYPE, "Collector" );

	ElementHandle<ItemHandlingElement> getCollector();

	@Type( base = Analyzer.class )
	@Label( standard = "analyzer" )
	@XmlBinding( path = "analyzer" )

	ElementProperty PROP_ANALYZER = new ElementProperty( TYPE, "Analyzer" );

	ElementHandle<Analyzer> getAnalyzer();

	@Type( base = Reducer.class )
	@Label( standard = "reducer" )
	@XmlBinding( path = "reducer" )

	ElementProperty PROP_REDUCER = new ElementProperty( TYPE, "Reducer" );

	ElementHandle<Reducer> getReducer();

}
