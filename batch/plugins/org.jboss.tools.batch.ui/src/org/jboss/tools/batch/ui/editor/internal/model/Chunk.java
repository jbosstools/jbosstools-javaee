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

import org.eclipse.sapphire.ElementHandle;
import org.eclipse.sapphire.ElementProperty;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ImpliedElementProperty;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.CountConstraint;
import org.eclipse.sapphire.modeling.annotations.Image;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
@Label( standard = "chunk" )
//@Image ( path = "chunk.png" )
@XmlBinding( path = "chunk" )
public interface Chunk extends BatchletOrChunk {

	ElementType TYPE = new ElementType( Chunk.class );

	@Label( standard = "checkpoint-policy" )
	@XmlBinding( path = "@checkpoint-policy" )

	ValueProperty PROP_CHECKPOINT_POLICY = new ValueProperty( TYPE, "CheckpointPolicy" );

	Value<String> getCheckpointPolicy();
	void setCheckpointPolicy( String value);

	@Label( standard = "item-count" )
	@XmlBinding( path = "@item-count" )

	ValueProperty PROP_ITEM_COUNT = new ValueProperty( TYPE, "ItemCount" );

	Value<String> getItemCount();
	void setItemCount( String value);

	@Label( standard = "retry-limit" )
	@XmlBinding( path = "@retry-limit" )

	ValueProperty PROP_RETRY_LIMIT = new ValueProperty( TYPE, "RetryLimit" );

	Value<String> getRetryLimit();
	void setRetryLimit( String value);

	@Label( standard = "skip-limit" )
	@XmlBinding( path = "@skip-limit" )

	ValueProperty PROP_SKIP_LIMIT = new ValueProperty( TYPE, "SkipLimit" );

	Value<String> getSkipLimit();
	void setSkipLimit( String value);

	@Label( standard = "time-limit" )
	@XmlBinding( path = "@time-limit" )

	ValueProperty PROP_TIME_LIMIT = new ValueProperty( TYPE, "TimeLimit" );

	Value<String> getTimeLimit();
	void setTimeLimit( String value);

	@Type( base = Reader.class )
	@Label( standard = "Reader" )
	@XmlBinding( path = "reader" )
	@CountConstraint (min=1, max=1)
	@Required
	ImpliedElementProperty PROP_READER = new ImpliedElementProperty( TYPE, "Reader" );

	Reader getReader();

	@Type( base = Processor.class )
	@Label( standard = "Processor" )
	@XmlBinding( path = "processor" )
	@CountConstraint (max=1)
	ElementProperty PROP_PROCESSOR = new ElementProperty( TYPE, "Processor" );

	ElementHandle<Processor> getProcessor();

	@Type( base = Writer.class )
	@Label( standard = "Writer" )
	@XmlBinding( path = "writer" )
	@CountConstraint (min=1, max=1)
	@Required
	ImpliedElementProperty PROP_WRITER = new ImpliedElementProperty( TYPE, "Writer" );

	Writer getWriter();

	@Type( base = CheckpointAlgorithm.class )
	@Label( standard = "Checkpoint Algorithm" )
	@XmlBinding( path = "checkpoint-algorithm" )
	@CountConstraint (max=1)
	ElementProperty PROP_CHECKPOINT_ALGORITHM = new ElementProperty( TYPE, "CheckpointAlgorithm" );

	ElementHandle<CheckpointAlgorithm> getCheckpointAlgorithm();

	@Type( base = ExceptionClasses.class )
	@Label( standard = "skippable-exception-classes" )
	@XmlBinding( path = "skippable-exception-classes" )

	ElementProperty PROP_SKIPPABLE_EXCEPTION_CLASSES = new ElementProperty( TYPE, "SkippableExceptionClasses" );

	ElementHandle<ExceptionClasses> getSkippableExceptionClasses();

	
	@Type( base = ExceptionClasses.class )
	@Label( standard = "retryable-exception-classes" )
	@XmlBinding( path = "retryable-exception-classes" )

	ElementProperty PROP_RETRYABLE_EXCEPTION_CLASSES = new ElementProperty( TYPE, "RetryableExceptionClasses" );

	ElementHandle<ExceptionClasses> getRetryableExceptionClasses();

	
	@Type( base = ExceptionClasses.class )
	@Label( standard = "no-rollback-exception-classes" )
	@XmlBinding( path = "no-rollback-exception-classes" )

	ElementProperty PROP_NO_ROLLBACK_EXCEPTION_CLASSES = new ElementProperty( TYPE, "NoRollbackExceptionClasses" );

	ElementHandle<ExceptionClasses> getNoRollbackExceptionClasses();
}
