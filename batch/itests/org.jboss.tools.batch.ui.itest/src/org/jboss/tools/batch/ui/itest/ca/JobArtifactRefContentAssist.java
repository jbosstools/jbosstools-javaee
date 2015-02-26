/******************************************************************************* 
 * Copyright (c) 2015 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.batch.ui.itest.ca;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class JobArtifactRefContentAssist extends ContentAssistantTestCase {
	private static final String FILE_NAME = "/src/META-INF/batch-jobs/job-ca-2.xml";

	public JobArtifactRefContentAssist() {}

	private static final String TEXT_TO_FIND_JOB_LISTENER = "<listener ref=\"my-j";

	private static final String[] PROPOSALS_JOB_LISTENER = {
		"myJobListener"
	};
	public void testJobListener() throws Exception {
		checkProposals(FILE_NAME, 
				TEXT_TO_FIND_JOB_LISTENER, 
				TEXT_TO_FIND_JOB_LISTENER.length() - 2, 
				PROPOSALS_JOB_LISTENER, 
				PROPOSALS_BATCHLET_STEP_LISTENER);
	}

	private static final String TEXT_TO_FIND_BATCHLET_STEP_LISTENER = "<listener ref=\"my-b";

	private static final String[] PROPOSALS_BATCHLET_STEP_LISTENER = {
		"myStepListener", "myStepListener2"
	};
	public void testBatchletStepListener() throws Exception {
		String[] noproposals = new String[PROPOSALS_CHUNK_STEP_LISTENER.length - 1];
		noproposals[0] = PROPOSALS_JOB_LISTENER[0];
		System.arraycopy(PROPOSALS_CHUNK_STEP_LISTENER, 2, noproposals, 1, PROPOSALS_CHUNK_STEP_LISTENER.length - 2);
		checkProposals(FILE_NAME, 
				TEXT_TO_FIND_BATCHLET_STEP_LISTENER, 
				TEXT_TO_FIND_BATCHLET_STEP_LISTENER.length() - 2, 
				PROPOSALS_BATCHLET_STEP_LISTENER,
				noproposals);
	}

	private static final String TEXT_TO_FIND_CHUNK_STEP_LISTENER = "<listener ref=\"my-c";

	private static final String[] PROPOSALS_CHUNK_STEP_LISTENER = {
		"myStepListener", "myStepListener2", 
		"myRetryProcessListener", "myRetryReadListener", "myRetryWriteListener",
		"mySkipProcessListener", "mySkipReadListener", "mySkipWriteListener",
		"myItemProcessListener", "myItemReadListener", "myItemWriteListener",
	};
	public void testChunkStepListener() throws Exception {
		checkProposals(FILE_NAME, 
				TEXT_TO_FIND_CHUNK_STEP_LISTENER, 
				TEXT_TO_FIND_CHUNK_STEP_LISTENER.length() - 2, 
				PROPOSALS_CHUNK_STEP_LISTENER,
				PROPOSALS_JOB_LISTENER);
	}

	private static final String TEXT_TO_FIND_BATCHLET = "<batchlet ref=\"";

	private static final String[] PROPOSALS_BATCHLET = {
		"batchlet1",
	};
	public void testBatchlet() throws Exception {
		checkProposals(FILE_NAME, 
				TEXT_TO_FIND_BATCHLET, 
				TEXT_TO_FIND_BATCHLET.length(), 
				PROPOSALS_BATCHLET, false);
	}

	private static final String TEXT_TO_FIND_READER = "<reader ref=\"";

	private static final String[] PROPOSALS_READER = {
		"myReader", "myReader2"
	};
	public void testReader() throws Exception {
		checkProposals(FILE_NAME, 
				TEXT_TO_FIND_READER, 
				TEXT_TO_FIND_READER.length(), 
				PROPOSALS_READER, false);
	}

	private static final String TEXT_TO_FIND_WRITER = "<writer ref=\"";

	private static final String[] PROPOSALS_WRITER = {
		"myWriter"
	};
	public void testWriter() throws Exception {
		checkProposals(FILE_NAME, 
				TEXT_TO_FIND_WRITER, 
				TEXT_TO_FIND_WRITER.length(), 
				PROPOSALS_WRITER, 
				PROPOSALS_READER);
	}

	private static final String TEXT_TO_FIND_PROCESSOR = "<processor ref=\"";

	private static final String[] PROPOSALS_PROCESSOR = {
		"myProcessor"
	};
	public void testProcessor() throws Exception {
		checkProposals(FILE_NAME, 
				TEXT_TO_FIND_PROCESSOR, 
				TEXT_TO_FIND_PROCESSOR.length(), 
				PROPOSALS_PROCESSOR, 
				PROPOSALS_READER);
	}

	private static final String TEXT_TO_FIND_CHECKPOINT_ALGORITHM = "<checkpoint-algorithm ref=\"";

	private static final String[] PROPOSALS_CHECKPOINT_ALGORITHM = {
		"myCheckpointAlgorithm"
	};
	public void testCheckpointAlgorithm() throws Exception {
		checkProposals(FILE_NAME, 
				TEXT_TO_FIND_CHECKPOINT_ALGORITHM, 
				TEXT_TO_FIND_CHECKPOINT_ALGORITHM.length(), 
				PROPOSALS_CHECKPOINT_ALGORITHM, 
				PROPOSALS_READER);
	}

	private static final String TEXT_TO_FIND_MAPPER = "<mapper ref=\"";

	private static final String[] PROPOSALS_MAPPER = {
		"myMapper"
	};
	public void testMapper() throws Exception {
		checkProposals(FILE_NAME, 
				TEXT_TO_FIND_MAPPER, 
				TEXT_TO_FIND_MAPPER.length(), 
				PROPOSALS_MAPPER, 
				PROPOSALS_READER);
	}

	private static final String TEXT_TO_FIND_COLLECTOR = "<collector ref=\"";

	private static final String[] PROPOSALS_COLLECTOR = {
		"myCollector"
	};
	public void testCollector() throws Exception {
		checkProposals(FILE_NAME, 
				TEXT_TO_FIND_COLLECTOR, 
				TEXT_TO_FIND_COLLECTOR.length(), 
				PROPOSALS_COLLECTOR, 
				PROPOSALS_READER);
	}

	private static final String TEXT_TO_FIND_ANALYZER = "<analyzer ref=\"";

	private static final String[] PROPOSALS_ANALYZER = {
		"myAnalyzer"
	};
	public void testAnalyzer() throws Exception {
		checkProposals(FILE_NAME, 
				TEXT_TO_FIND_ANALYZER, 
				TEXT_TO_FIND_ANALYZER.length(), 
				PROPOSALS_ANALYZER, 
				PROPOSALS_READER);
	}

	private static final String TEXT_TO_FIND_REDUCER = "<reducer ref=\"";

	private static final String[] PROPOSALS_REDUCER = {
		"myReducer"
	};
	public void testReducer() throws Exception {
		checkProposals(FILE_NAME, 
				TEXT_TO_FIND_REDUCER, 
				TEXT_TO_FIND_REDUCER.length(), 
				PROPOSALS_REDUCER, 
				PROPOSALS_READER);
	}

}
