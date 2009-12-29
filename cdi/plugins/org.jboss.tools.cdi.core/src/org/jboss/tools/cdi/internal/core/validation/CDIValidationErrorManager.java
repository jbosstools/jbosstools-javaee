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
package org.jboss.tools.cdi.internal.core.validation;

import org.eclipse.core.resources.IProject;
import org.jboss.tools.cdi.core.preferences.CDIPreferences;
import org.jboss.tools.jst.web.kb.internal.validation.ValidationErrorManager;

public class CDIValidationErrorManager extends ValidationErrorManager {

	@Override
	protected String getPreference(IProject project, String preferenceKey) {
		return CDIPreferences.getInstance().getProjectPreference(project, preferenceKey);
	}

}
