/*******************************************************************************
 * Copyright (c) 2007 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.struts;

import org.eclipse.core.resources.IProject;

public class StrutsProjectUtil {
	public static final String STRUTS_NATURE_ID = StrutsProject.NATURE_ID;
	public static final String STRUTS_PERSPECTIVE_ID = "org.jboss.tools.common.model.ui.XStudioPerspective";
	public static final String NATURE_NICK = "struts";
	
	public static boolean hasStrutsNature(IProject project) {
		try {
			return project != null && project.hasNature(StrutsProjectUtil.STRUTS_NATURE_ID);
		} catch (Exception e) {
            StrutsModelPlugin.getPluginLog().logError(e);
			return false;
		}
	}
}
