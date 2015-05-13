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

import org.eclipse.sapphire.ElementHandle;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ElementProperty;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.Length;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.Image;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlElementBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlElementBinding.Mapping;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
@Label( standard = "step" )
@Image ( path = "step.png" )
@XmlBinding( path = "step" )
public interface Step extends FlowElement, NextAttributeElement {

	ElementType TYPE = new ElementType( Step.class );

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

	@Type( base = StepListener.class )
	@Label( standard = "listener" )
	@XmlListBinding( path = "listeners", mappings = @XmlListBinding.Mapping( element = "listener", type = StepListener.class ))

	ListProperty PROP_LISTENERS = new ListProperty( TYPE, "Listeners" ); //$NON-NLS-1$ 

	ElementList<StepListener> getListeners();

	@Label( standard = "batchlet or chunk" )
	@Type( base = BatchletOrChunk.class, 
			possible = {
				Batchlet.class,
				Chunk.class
			}
		)
	@XmlListBinding( path = "" )
	@XmlElementBinding(path = "", mappings = {
			@Mapping(type=Batchlet.class, element = "batchlet"),
			@Mapping(type=Chunk.class, element = "chunk"),
	})
	@Length(max = 1)
	ListProperty PROP_BATCHLET_OR_CHUNK = new ListProperty( TYPE, "BatchletOrChunk" );

	ElementList<BatchletOrChunk> getBatchletOrChunk();

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
	ListProperty PROP_OUTCOME_ELEMENTS = new ListProperty( TYPE, "OutcomeElements" );

	ElementList<OutcomeElement> getOutcomeElements();
	
	@Type( base = OutcomeElement.class,
			possible = {
				End.class,
				Fail.class,
				Stop.class
			}
	)
	@XmlListBinding(path = "")
	// Next vs terminating elements have to be in separate list becase the need
	// to be handled differently in diagram. (Next via connections).
	ListProperty PROP_TERMINATING_ELEMENTS = new ListProperty( TYPE, "TerminatingElements" );

	ElementList<OutcomeElement> getTerminatingElements();
	
	@Type( base = Next.class )
	@XmlListBinding( path = "" )
	// Next vs terminating elements have to be in separate list becase the need
	// to be handled differently in diagram. (Next via connections).
	ListProperty PROP_NEXT_ELEMENTS = new ListProperty( TYPE, "NextElements" );

	ElementList<Next> getNextElements();
	
}
