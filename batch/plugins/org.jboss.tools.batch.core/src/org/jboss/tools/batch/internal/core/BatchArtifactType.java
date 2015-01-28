/*************************************************************************************
 * Copyright (c) 2015 Red Hat, Inc. and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     JBoss by Red Hat - Initial implementation.
 ************************************************************************************/
package org.jboss.tools.batch.internal.core;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public enum BatchArtifactType implements BatchConstants {
	BATCHLET(TAG_BATCHLET, BATCHLET_TYPE, ABSTRACT_BATCHLET_TYPE),
	DECIDER(TAG_DECISION, DECIDER_TYPE, null),

	ITEM_READER(TAG_READER, ITEM_READER_TYPE, ABSTRACT_ITEM_READER_TYPE),
	ITEM_WRITER(TAG_WRITER, ITEM_WRITER_TYPE, ABSTRACT_ITEM_WRITER_TYPE),
	ITEM_PROCESSOR(TAG_PROCESSOR, ITEM_PROCESSOR_TYPE, null),
	CHECKPOINT_ALGORITHM(TAG_CHECKPOINT_ALGORITHM, CHECKPOINT_ALGORITHM_TYPE, ABSTRACT_CHECKPOINT_ALGORITHM_TYPE),
	
	PARTITION_MAPPER(TAG_MAPPER, PARTITION_MAPPER_TYPE, null),
	PARTITION_REDUCER(TAG_REDUCER, PARTITION_REDUCER_TYPE, ABSTRACT_PARTITION_REDUCER_TYPE),
	PARTITION_COLLECTOR(TAG_COLLECTOR, PARTITION_COLLECTOR_TYPE, null),
	PARTITION_ANALYZER(TAG_ANALYZER, PARTITION_ANALYZER_TYPE, ABSTRACT_PARTITION_ANALYZER_TYPE),

	JOB_LISTENER(TAG_JOB, JOB_LISTENER_TYPE, ABSTRACT_JOB_LISTENER_TYPE),
	STEP_LISTENER(TAG_STEP, STEP_LISTENER_TYPE, ABSTRACT_STEP_LISTENER_TYPE),
	CHUNK_LISTENER(TAG_STEP, CHUNK_LISTENER_TYPE, ABSTRACT_CHUNK_LISTENER_TYPE),

	ITEM_READ_LISTENER(TAG_STEP, ITEM_READ_LISTENER_TYPE, ABSTRACT_ITEM_READ_LISTENER_TYPE),
	ITEM_PROCESS_LISTENER(TAG_STEP, ITEM_PROCESS_LISTENER_TYPE, ABSTRACT_ITEM_PROCESS_LISTENER_TYPE),
	ITEM_WRITE_LISTENER(TAG_STEP, ITEM_WRITE_LISTENER_TYPE, ABSTRACT_ITEM_WRITE_LISTENER_TYPE),

	SKIP_READ_LISTENER(TAG_STEP, SKIP_READ_LISTENER_TYPE, null),
	SKIP_PROCESS_LISTENER(TAG_STEP, SKIP_PROCESS_LISTENER_TYPE, null),
	SKIP_WRITE_LISTENER(TAG_STEP, SKIP_WRITE_LISTENER_TYPE, null),

	RETRY_READ_LISTENER(TAG_STEP, RETRY_READ_LISTENER_TYPE, null),
	RETRY_PROCESS_LISTENER(TAG_STEP, RETRY_PROCESS_LISTENER_TYPE, null),
	RETRY_WRITE_LISTENER(TAG_STEP, RETRY_WRITE_LISTENER_TYPE, null);

	String tag;
	String interfaceName;
	String className;

	private BatchArtifactType(String tag, String interfaceName, String className) {
		this.tag = tag;
		this.interfaceName = interfaceName;
		this.className = className;
	}

	public String getInterfaceName() {
		return interfaceName;
	}

	public String getClassName() {
		return className;
	}
}
