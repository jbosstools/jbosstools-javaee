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
package org.jboss.tools.batch.internal.core.preferences;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.jboss.tools.batch.core.BatchCorePlugin;
import org.jboss.tools.common.validation.ValidationSeverityPreferences;

/**
 * @author Viacheslav Kabanovich
 */
public class BatchSeverityPreferences extends ValidationSeverityPreferences {

	public static final Set<String> SEVERITY_OPTION_NAMES = new HashSet<String>();

	private static BatchSeverityPreferences INSTANCE = new BatchSeverityPreferences();

	public static final String WARNING_GROUP_ID = "batch";

	public static final String UNKNOWN_ARTIFACT_NAME = INSTANCE.createSeverityOption("unknownArtifactName", "unknown-artifact"); //$NON-NLS-1$
	public static final String WRONG_ARTIFACT_TYPE = INSTANCE.createSeverityOption("wrongArtifactType", "wrong-artifact"); //$NON-NLS-1$
	public static final String UNUSED_PROPERTY = INSTANCE.createSeverityOption("unusedProperty", "unused-property"); //$NON-NLS-1$
	public static final String UNKNOWN_PROPERTY = INSTANCE.createSeverityOption("unknownProperty", "unknown-property"); //$NON-NLS-1$

	public static final String TARGET_NOT_FOUND = INSTANCE.createSeverityOption("targetNotFound", "unfound-target"); //$NON-NLS-1$
	public static final String LOOP_IS_DETECTED = INSTANCE.createSeverityOption("loopIsDetected", "loop-detected"); //$NON-NLS-1$

	public static final String UNKNOWN_EXCEPTION_CLASS = INSTANCE.createSeverityOption("unknownExceptionClass", "unknown-exception"); //$NON-NLS-1$
	public static final String WRONG_EXCEPTION_CLASS = INSTANCE.createSeverityOption("wrongExceptionClass", "not-exception"); //$NON-NLS-1$

	/**
	 * @return the only instance of JSFSeverityPreferences
	 */
	public static BatchSeverityPreferences getInstance() {
		return INSTANCE;
	}

	private BatchSeverityPreferences() {
	}

	@Override
	protected String createSeverityOption(String shortName) {
		String name = getPluginId() + ".validator.problem." + shortName; //$NON-NLS-1$
		SEVERITY_OPTION_NAMES.add(name);
		return name;
	}

	@Override
	protected String getPluginId() {
		return BatchCorePlugin.PLUGIN_ID;
	}

	@Override
	protected Set<String> getSeverityOptionNames() {
		return SEVERITY_OPTION_NAMES;
	}

	@Override
	public String getWarningGroupID() {
		return WARNING_GROUP_ID;
	}

	public static boolean isValidationEnabled(IProject project) {
		return INSTANCE.isEnabled(project);
	}

	public static int getMaxNumberOfProblemMarkersPerFile(IProject project) {
		return INSTANCE.getMaxNumberOfProblemMarkersPerResource(project);
	}

//	public static boolean shouldValidateTagLibs(IProject project) {
//		return !(SeverityPreferences.IGNORE.equals(INSTANCE.getProjectPreference(project, UNKNOWN_TAGLIB_COMPONENT)) &&
//				SeverityPreferences.IGNORE.equals(INSTANCE.getProjectPreference(project, UNKNOWN_TAGLIB_ATTRIBUTE)));
//	}
}