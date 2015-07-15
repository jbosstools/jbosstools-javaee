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
package org.jboss.tools.batch.ui;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.jboss.tools.batch.core.BatchArtifactType;
import org.jboss.tools.batch.core.BatchConstants;
import org.jboss.tools.batch.ui.editor.internal.model.Job;
import org.jboss.tools.common.ui.CommonUIImages;

/**
 * Builds images for Sapphire model of Batch Job XML. 
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class JobImages {
	public static final String ANALYZER_IMAGE = "partition.png"; //$NON-NLS-1$
	public static final String BATCHLET_IMAGE = "batchlet.png"; //$NON-NLS-1$
	public static final String CHECKPOINT_ALGORITHM_IMAGE = "checkpoint-algorithm.png"; //$NON-NLS-1$
	public static final String COLLECTOR_IMAGE = "partition.png"; //$NON-NLS-1$
	public static final String DECISION_IMAGE = "decision.png"; //$NON-NLS-1$
	public static final String FLOW_IMAGE = "flow.png"; //$NON-NLS-1$
	public static final String JOB_LISTENER_IMAGE = "listener.png"; //$NON-NLS-1$
	public static final String JOB_IMAGE = "job.png"; //$NON-NLS-1$
	public static final String MAPPER_IMAGE = "partition.png"; //$NON-NLS-1$
	public static final String PROCESSOR_IMAGE = "processor.png"; //$NON-NLS-1$
	public static final String PROPERTY_IMAGE = "property.png"; //$NON-NLS-1$
	public static final String READER_IMAGE = "reader.png"; //$NON-NLS-1$
	public static final String REDUCER_IMAGE = "partition.png"; //$NON-NLS-1$
	public static final String STEP_LISTENER_IMAGE = "listener.png"; //$NON-NLS-1$
	public static final String WRITER_IMAGE = "writer.png"; //$NON-NLS-1$

	public static final String NEW_JOB_XML_IMAGE = "BatchXMLWizBan.png"; //$NON-NLS-1$
	public static final String NEW_ARTIFACT_IMAGE = "BatchArtifactWizBan.png"; //$NON-NLS-1$
	
	public static final String QUICKFIX_EDIT_IMAGE = "batch_edit.png"; //$NON-NLS-1$
	

	private JobImages() {}
	/**
	 * Returns image by short name. Works only for images placed with Sapphire model interface Job.
	 * @see Job
	 * 
	 * @param key
	 * @return
	 */
	public static Image getImage(String key) {
		return getImage(getImageDescriptor(key));
	}

	public static ImageDescriptor getImageDescriptor(String key) {
		ImageDescriptor descriptor = getImageRegistry().getDescriptor(key);
		if(descriptor == null) {
			descriptor = ImageDescriptor.createFromFile(Job.class, key);
			getImageRegistry().put(key, descriptor);
		}
		return descriptor;
	}
	

	static ImageRegistry getImageRegistry() {
		return BatchUIPlugin.getDefault().getImageRegistry();
	}

	static Image getImage(ImageDescriptor descriptor) {
		return CommonUIImages.getImage(descriptor);
	}

	/**
	 * Returns image by batch artifact type.
	 * 
	 * @param element
	 * @return
	 */
	public static Image getImageByElement(BatchArtifactType element) {
		return getImage(getImageDescriptorByElement(element));
	}

	/**
	 * Returns image descriptor by batch artifact type.
	 * 
	 * @param element
	 * @return
	 */
	public static ImageDescriptor getImageDescriptorByElement(BatchArtifactType element) {
		if(element == BatchArtifactType.BATCHLET) {
			return getImageDescriptor(BATCHLET_IMAGE);
		} else if(element == BatchArtifactType.CHECKPOINT_ALGORITHM) {
			return getImageDescriptor(CHECKPOINT_ALGORITHM_IMAGE);
		} else if(element == BatchArtifactType.DECIDER) {
			return getImageDescriptor(DECISION_IMAGE);
		} else if(element == BatchArtifactType.ITEM_READER) {
			return getImageDescriptor(READER_IMAGE);
		} else if(element == BatchArtifactType.ITEM_WRITER) {
			return getImageDescriptor(WRITER_IMAGE);
		} else if(element == BatchArtifactType.ITEM_PROCESSOR) {
			return getImageDescriptor(PROCESSOR_IMAGE);
		} else if(element == BatchArtifactType.PARTITION_MAPPER) {
			return getImageDescriptor(MAPPER_IMAGE);
		} else if(element == BatchArtifactType.PARTITION_COLLECTOR) {
			return getImageDescriptor(COLLECTOR_IMAGE);
		} else if(element == BatchArtifactType.PARTITION_ANALYZER) {
			return getImageDescriptor(ANALYZER_IMAGE);
		} else if(element == BatchArtifactType.PARTITION_REDUCER) {
			return getImageDescriptor(REDUCER_IMAGE);
		} else if(element == BatchArtifactType.JOB_LISTENER) {
			return getImageDescriptor(JOB_LISTENER_IMAGE);
		} else if(element.getTag().equals(BatchConstants.TAG_STEP)) {
			return getImageDescriptor(STEP_LISTENER_IMAGE);
		}
		return null;
	}
}
