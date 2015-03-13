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
package org.jboss.tools.batch.core;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public interface BatchConstants {
	public String JAVAEE_NAMESPACE = "http://xmlns.jcp.org/xml/ns/javaee";

	public String JOB_PROPERTIES_OPERATOR = "jobProperties";
	public String JOB_PARAMETERS_OPERATOR = "jobParameters";
	public String SYSTEM_PROPERTIES_OPERATOR = "systemProperties";
	public String PARTITION_PLAN_OPERATOR = "partitionPlan";
	
	public String API_PACKAGE = "javax.batch.api";
	public String CHUNK_PACKAGE = API_PACKAGE + ".chunk";
	public String LISTENER_PACKAGE = API_PACKAGE + ".listener";
	public String CHUNK_LISTENER_PACKAGE = CHUNK_PACKAGE + ".listener";
	public String PARTITION_PACKAGE = API_PACKAGE + ".partition";
	
	public String BATCHLET_TYPE = API_PACKAGE + ".Batchlet";
	public String ABSTRACT_BATCHLET_TYPE = API_PACKAGE + ".AbstractBatchlet";

	public String DECIDER_TYPE = API_PACKAGE + ".Decider";

	public String ITEM_READER_TYPE = CHUNK_PACKAGE + ".ItemReader";
	public String ABSTRACT_ITEM_READER_TYPE = CHUNK_PACKAGE + ".AbstractItemReader";
	
	public String ITEM_WRITER_TYPE = CHUNK_PACKAGE + ".ItemWriter";
	public String ABSTRACT_ITEM_WRITER_TYPE = CHUNK_PACKAGE + ".AbstractItemWriter";

	public String ITEM_PROCESSOR_TYPE = CHUNK_PACKAGE + ".ItemProcessor";

	public String CHECKPOINT_ALGORITHM_TYPE = CHUNK_PACKAGE + ".CheckpointAlgorithm";
	public String ABSTRACT_CHECKPOINT_ALGORITHM_TYPE = CHUNK_PACKAGE + ".AbstractCheckpointAlgorithm";

	public String PARTITION_MAPPER_TYPE = PARTITION_PACKAGE + ".PartitionMapper";

	public String PARTITION_REDUCER_TYPE = PARTITION_PACKAGE + ".PartitionReducer";
	public String ABSTRACT_PARTITION_REDUCER_TYPE = PARTITION_PACKAGE + ".AbstractPartitionReducer";

	public String PARTITION_COLLECTOR_TYPE = PARTITION_PACKAGE + ".PartitionCollector";

	public String PARTITION_ANALYZER_TYPE = PARTITION_PACKAGE + ".PartitionAnalyzer";
	public String ABSTRACT_PARTITION_ANALYZER_TYPE = PARTITION_PACKAGE + ".AbstractPartitionAnalyzer";

	public String JOB_LISTENER_TYPE = LISTENER_PACKAGE + ".JobListener";
	public String ABSTRACT_JOB_LISTENER_TYPE = LISTENER_PACKAGE + ".AbstractJobListener";

	public String STEP_LISTENER_TYPE = LISTENER_PACKAGE + ".StepListener";
	public String ABSTRACT_STEP_LISTENER_TYPE = LISTENER_PACKAGE + ".AbstractStepListener";

	public String CHUNK_LISTENER_TYPE = CHUNK_LISTENER_PACKAGE + ".ChunkListener";
	public String ABSTRACT_CHUNK_LISTENER_TYPE = CHUNK_LISTENER_PACKAGE + ".AbstractChunkListener";

	public String ITEM_READ_LISTENER_TYPE = CHUNK_LISTENER_PACKAGE + ".ItemReadListener";
	public String ABSTRACT_ITEM_READ_LISTENER_TYPE = CHUNK_LISTENER_PACKAGE + ".AbstractItemReadListener";

	public String ITEM_PROCESS_LISTENER_TYPE = CHUNK_LISTENER_PACKAGE + ".ItemProcessListener";
	public String ABSTRACT_ITEM_PROCESS_LISTENER_TYPE = CHUNK_LISTENER_PACKAGE + ".AbstractItemProcessListener";

	public String ITEM_WRITE_LISTENER_TYPE = CHUNK_LISTENER_PACKAGE + ".ItemWriteListener";
	//AbstractItemWriteListener is not mentioned in JSR-352 ?
	public String ABSTRACT_ITEM_WRITE_LISTENER_TYPE = CHUNK_LISTENER_PACKAGE + ".AbstractItemWriteListener";

	public String SKIP_READ_LISTENER_TYPE = CHUNK_LISTENER_PACKAGE + ".SkipReadListener";

	public String SKIP_PROCESS_LISTENER_TYPE = CHUNK_LISTENER_PACKAGE + ".SkipProcessListener";

	public String SKIP_WRITE_LISTENER_TYPE = CHUNK_LISTENER_PACKAGE + ".SkipWriteListener";

	public String RETRY_READ_LISTENER_TYPE = CHUNK_LISTENER_PACKAGE + ".RetryReadListener";

	public String RETRY_PROCESS_LISTENER_TYPE = CHUNK_LISTENER_PACKAGE + ".RetryProcessListener";

	public String RETRY_WRITE_LISTENER_TYPE = CHUNK_LISTENER_PACKAGE + ".RetryWriteListener";

	public String NAMED_QUALIFIER_TYPE = "javax.inject.Named";
	public String INJECT_ANNOTATION_TYPE = "javax.inject.Inject";
	public String BATCH_PROPERTY_QUALIFIER_TYPE = API_PACKAGE + ".BatchProperty";

	public String TAG_ANALYZER = "analyzer";
	public String TAG_BATCHLET = "batchlet";
	public String TAG_CHECKPOINT_ALGORITHM = "checkpoint-algorithm";
	public String TAG_CHUNK = "chunk";
	public String TAG_COLLECTOR = "collector";
	public String TAG_DECISION = "decision";
	public String TAG_EXCLUDE = "exclude";
	public String TAG_FLOW = "flow";
	public String TAG_INCLUDE = "include";
	public String TAG_JOB = "job";
	public String TAG_LISTENER = "listener";
	public String TAG_LISTENERS = "listeners";
	public String TAG_MAPPER = "mapper";
	public String TAG_NEXT = "next";
	public String TAG_NO_ROLLBACK_EXCEPTION_CLASSES = "no-rollback-exception-classes";
	public String TAG_PARTITION = "partition";
	public String TAG_PROCESSOR = "processor";
	public String TAG_PROPERTIES = "properties";
	public String TAG_PROPERTY = "property";
	public String TAG_READER = "reader";
	public String TAG_REDUCER = "reducer";
	public String TAG_RETRYABLE_EXCEPTION_CLASSES = "retryable-exception-classes";
	public String TAG_SKIPPABLE_EXCEPTION_CLASSES = "skippable-exception-classes";
	public String TAG_STOP = "stop";
	public String TAG_SPLIT = "split";
	public String TAG_STEP = "step";
	public String TAG_WRITER = "writer";

	public String ATTR_CLASS = "class";
	public String ATTR_ID = "id";
	public String ATTR_NAME = "name";
	public String ATTR_NEXT = "next";
	public String ATTR_REF = "ref";
	public String ATTR_RESTART = "restart";
	public String ATTR_TO = "to";
	public String ATTR_VALUE = "value";
}
