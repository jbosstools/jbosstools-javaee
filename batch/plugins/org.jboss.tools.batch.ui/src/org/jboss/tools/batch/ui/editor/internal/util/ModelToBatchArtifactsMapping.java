/******************************************************************************* 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Tomas Milata - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.batch.ui.editor.internal.util;

import static org.jboss.tools.batch.core.BatchArtifactType.BATCHLET;
import static org.jboss.tools.batch.core.BatchArtifactType.CHECKPOINT_ALGORITHM;
import static org.jboss.tools.batch.core.BatchArtifactType.CHUNK_LISTENER;
import static org.jboss.tools.batch.core.BatchArtifactType.DECIDER;
import static org.jboss.tools.batch.core.BatchArtifactType.ITEM_PROCESSOR;
import static org.jboss.tools.batch.core.BatchArtifactType.ITEM_PROCESS_LISTENER;
import static org.jboss.tools.batch.core.BatchArtifactType.ITEM_READER;
import static org.jboss.tools.batch.core.BatchArtifactType.ITEM_READ_LISTENER;
import static org.jboss.tools.batch.core.BatchArtifactType.ITEM_WRITER;
import static org.jboss.tools.batch.core.BatchArtifactType.ITEM_WRITE_LISTENER;
import static org.jboss.tools.batch.core.BatchArtifactType.JOB_LISTENER;
import static org.jboss.tools.batch.core.BatchArtifactType.PARTITION_ANALYZER;
import static org.jboss.tools.batch.core.BatchArtifactType.PARTITION_COLLECTOR;
import static org.jboss.tools.batch.core.BatchArtifactType.PARTITION_MAPPER;
import static org.jboss.tools.batch.core.BatchArtifactType.PARTITION_REDUCER;
import static org.jboss.tools.batch.core.BatchArtifactType.RETRY_PROCESS_LISTENER;
import static org.jboss.tools.batch.core.BatchArtifactType.RETRY_READ_LISTENER;
import static org.jboss.tools.batch.core.BatchArtifactType.RETRY_WRITE_LISTENER;
import static org.jboss.tools.batch.core.BatchArtifactType.SKIP_PROCESS_LISTENER;
import static org.jboss.tools.batch.core.BatchArtifactType.SKIP_READ_LISTENER;
import static org.jboss.tools.batch.core.BatchArtifactType.SKIP_WRITE_LISTENER;
import static org.jboss.tools.batch.core.BatchArtifactType.STEP_LISTENER;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jboss.tools.batch.core.BatchArtifactType;
import org.jboss.tools.batch.ui.editor.internal.model.Analyzer;
import org.jboss.tools.batch.ui.editor.internal.model.Batchlet;
import org.jboss.tools.batch.ui.editor.internal.model.CheckpointAlgorithm;
import org.jboss.tools.batch.ui.editor.internal.model.Collector;
import org.jboss.tools.batch.ui.editor.internal.model.Decision;
import org.jboss.tools.batch.ui.editor.internal.model.JobListener;
import org.jboss.tools.batch.ui.editor.internal.model.Mapper;
import org.jboss.tools.batch.ui.editor.internal.model.Processor;
import org.jboss.tools.batch.ui.editor.internal.model.Reader;
import org.jboss.tools.batch.ui.editor.internal.model.Reducer;
import org.jboss.tools.batch.ui.editor.internal.model.RefAttributeElement;
import org.jboss.tools.batch.ui.editor.internal.model.StepListener;
import org.jboss.tools.batch.ui.editor.internal.model.Writer;

public class ModelToBatchArtifactsMapping {

	private static Map<Class<? extends RefAttributeElement>, List<BatchArtifactType>> mapping = new HashMap<>();

	static {
		mapping.put(Batchlet.class, one(BATCHLET));
		mapping.put(Decision.class, one(DECIDER));
		mapping.put(Analyzer.class, one(PARTITION_ANALYZER));
		mapping.put(Collector.class, one(PARTITION_COLLECTOR));
		mapping.put(Mapper.class, one(PARTITION_MAPPER));
		mapping.put(Reducer.class, one(PARTITION_REDUCER));
		mapping.put(Processor.class, one(ITEM_PROCESSOR));
		mapping.put(Reader.class, one(ITEM_READER));
		mapping.put(Writer.class, one(ITEM_WRITER));
		mapping.put(JobListener.class, one(JOB_LISTENER));
		mapping.put(CheckpointAlgorithm.class, one(CHECKPOINT_ALGORITHM));
		mapping.put(StepListener.class,
				all(STEP_LISTENER, CHUNK_LISTENER, ITEM_READ_LISTENER, ITEM_PROCESS_LISTENER, ITEM_WRITE_LISTENER,
						SKIP_READ_LISTENER, SKIP_PROCESS_LISTENER, SKIP_WRITE_LISTENER, RETRY_READ_LISTENER,
						RETRY_PROCESS_LISTENER, RETRY_WRITE_LISTENER));

	}

	/**
	 * Returns a list of batch artifact types mapped to the passes elementClass.
	 * 
	 * @param elementClass
	 *            the model class
	 * @return An unmodifiable list of artifact types or an empty list if the
	 *         {@code elemetClass} is null or no mapping found.
	 */
	public static List<BatchArtifactType> getBatchArtifactTypes(Class<? extends RefAttributeElement> elementClass) {
		if (elementClass == null) {
			return Collections.emptyList();
		}
		List<BatchArtifactType> types = mapping.get(elementClass);
		if (types == null) {
			return Collections.emptyList();
		}
		return types;
	}

	private static List<BatchArtifactType> all(BatchArtifactType... types) {
		return Collections.unmodifiableList(Arrays.asList(types));
	}

	private static List<BatchArtifactType> one(BatchArtifactType type) {
		return Collections.unmodifiableList(Collections.singletonList(type));
	}

}
