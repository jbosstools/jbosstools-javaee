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
package org.jboss.tools.batch.ui.internal.wizard;

import org.eclipse.osgi.util.NLS;

/**
 * 
 * @author Viacheslav Kabanovich
 *
 */
public class WizardMessages {
	private static final String BUNDLE_NAME = "org.jboss.tools.batch.ui.internal.wizard.messages"; //$NON-NLS-1$

	public static String NEW_JOB_XML_WIZARD_TITLE;
	public static String NEW_JOB_XML_WIZARD_DESCRIPTION;

	public static String NEW_BATCH_ARTIFACT_WIZARD_TITLE;
	public static String NEW_BATCH_ARTIFACT_WIZARD_PAGE_NAME;
	public static String NEW_BATCH_ARTIFACT_WIZARD_DESCRIPTION;

	public static String NEW_BATCHLET_WIZARD_TITLE;
	public static String NEW_BATCHLET_WIZARD_PAGE_NAME;
	public static String NEW_BATCHLET_WIZARD_DESCRIPTION;

	public static String actionOpenArtifact;
	public static String actionCreateArtifact;

	public static String addPropertyDialogTitle;

	public static String artifactLabel;
	public static String artifactImplementInterfaceLabel;
	public static String artifactExtendAbstractClassLabel;

	public static String artifactLoaderLabel;
	public static String artifactNameLabel;
	public static String artifactLoaderAnnotationLabel;
	public static String artifactLoaderXMLLabel;
	public static String artifactLoaderQualifiedLabel;
	public static String artifactPropertiesLabel;

	public static String deriveFromLabel;
	public static String idLabel;
	public static String versionLabel;

	public static String errorIdIsRequired;
	public static String errorJobIdIsNotUnique;
	public static String errorFieldNameIsNotValid;
	public static String errorFieldNameIsNotUnique;
	public static String errorArtifactNameIsNotUnique;
	public static String errorArtifactNameIsEmpty;

	public static String batchletTypeLabel;
	public static String deciderTypeLabel;
	public static String itemReaderTypeLabel;
	public static String itemWriterTypeLabel;
	public static String itemProcessorTypeLabel;
	public static String checkpointAlgorithmTypeLabel;
	public static String partitionMapperTypeLabel;
	public static String partitionReducerTypeLabel;
	public static String partitionCollectorTypeLabel;
	public static String partitionAnalyzerTypeLabel;
	public static String jobListenerTypeLabel;
	public static String stepListenerTypeLabel;
	public static String chunkListenerTypeLabel;
	public static String itemReadListenerTypeLabel;
	public static String itemProcessListenerTypeLabel;
	public static String itemWriteListenerTypeLabel;
	public static String skipReadListenerTypeLabel;
	public static String skipProcessListenerTypeLabel;
	public static String skipWriteListenerTypeLabel;
	public static String retryReadListenerTypeLabel;
	public static String retryProcessListenerTypeLabel;
	public static String retryWriteListenerTypeLabel;

	static {
		NLS.initializeMessages(BUNDLE_NAME, WizardMessages.class);
	}
}
