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
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ElementProperty;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.CountConstraint;
import org.eclipse.sapphire.modeling.annotations.Image;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.annotations.Services;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlElementBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlElementBinding.Mapping;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;
import org.jboss.tools.batch.ui.editor.internal.services.NextPossibleValuesService;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
@Label( standard = "step" )
//@Image ( path = "step.png" )
@XmlBinding( path = "step" )
public interface Step extends FlowElement {

	ElementType TYPE = new ElementType( Step.class );

	@Label( standard = "next" )
	@XmlBinding( path = "@next" )
	@Services ( {
		@Service( impl = NextPossibleValuesService.class )
	})

	ValueProperty PROP_NEXT = new ValueProperty( TYPE, "Next" );

	Value<String> getNext();
	void setNext( String next);

	@Label( standard = "allow-start-if-complete" )
	@XmlBinding( path = "@allow-start-if-complete" )

	ValueProperty PROP_ALLOW_START_IF_COMPLETE = new ValueProperty( TYPE, "AllowStartIfComplete" );

	Value<String> getAllowStartIfComplete();
	void setAllowStartIfComplete( String value);

	@Type( base = Properties.class )
	@Label( standard = "properties" )
	@XmlBinding( path = "properties" )

	ElementProperty PROP_PROPERTIES = new ElementProperty( TYPE, "Properties" );

	ElementHandle<Properties> getProperties();

	@Type( base = Listener.class )
	@Label( standard = "listener" )
	@XmlListBinding( path = "listeners", mappings = @XmlListBinding.Mapping( element = "listener", type = Listener.class ))

	ListProperty PROP_LISTENERS = new ListProperty( TYPE, "Listeners" ); //$NON-NLS-1$ 

	ElementList<Listener> getListeners();

	//TODO find a better way to add element [0..1] that has required children.
	//Question: why Add Chunk is always enabled despite of constraint?
	@Label( standard = "batchlet or chunk" )
	@Type( base = BatchletOrChunk.class, 
			possible = {
				Batchlet.class,
				Chunk.class
			}
		)
//	@XmlListBinding( path = "" )
	@XmlElementBinding(path = "", mappings = {
			@Mapping(type=Batchlet.class, element = "batchlet"),
			@Mapping(type=Chunk.class, element = "chunk"),
	})
	@CountConstraint(max = 1)
//	ListProperty PROP_BATCHLET_OR_CHUNK = new ListProperty( TYPE, "BatchletOrChunk" );
	ElementProperty PROP_BATCHLET_OR_CHUNK = new ElementProperty( TYPE, "BatchletOrChunk" );

//	ElementList<Element> getBatchletOrChunk();
	ElementHandle<BatchletOrChunk> getBatchletOrChunk();

	@Type( base = Partition.class )
	@Label( standard = "partition" )
	@XmlBinding( path = "partition" )

	ElementProperty PROP_PARTITION = new ElementProperty( TYPE, "Partition" );

	ElementHandle<Partition> getPartition();


	@Type( base = OutcomeElement.class,
			possible = {
				End.class,
				Fail.class,
				Stop.class,
				Next.class
			}
	)
	@XmlListBinding( path = "" )
	ListProperty PROP_OUTCOME_ELEMENTS = new ListProperty(TYPE, "OutcomeElements");

	ElementList<OutcomeElement> getOutcomeElements();
	
}
