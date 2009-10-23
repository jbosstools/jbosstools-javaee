/******************************************************************************* 
 * Copyright (c) 2009 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/ 
package org.jboss.tools.seam.internal.core.validation;

import org.eclipse.core.resources.IProject;
import org.jboss.tools.jst.web.kb.internal.validation.ValidationErrorManager;
import org.jboss.tools.seam.core.SeamPreferences;

/**
 * @author Alexey Kazakov
 */
public class SeamValidationErrorManager extends ValidationErrorManager {

	public static final String MARKED_SEAM_PROJECT_MESSAGE_GROUP = "markedSeamProject"; //$NON-NLS-1$

	/* (non-Javadoc)
	 * @see org.jboss.tools.jst.web.kb.internal.validation.ValidationErrorManager#getPreference(org.eclipse.core.resources.IProject, java.lang.String)
	 */
	@Override
	protected String getPreference(IProject project, String preferenceKey) {
		return SeamPreferences.getInstance().getProjectPreference(project, preferenceKey);
	}
}