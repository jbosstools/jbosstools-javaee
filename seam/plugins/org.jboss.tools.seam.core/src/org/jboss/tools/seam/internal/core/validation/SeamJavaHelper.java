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
package org.jboss.tools.seam.internal.core.validation;

import org.eclipse.wst.validation.internal.operations.WorkbenchContext;
import org.jboss.tools.seam.core.ISeamProject;
import org.jboss.tools.seam.core.SeamCorePlugin;

public class SeamJavaHelper extends WorkbenchContext {

	public ISeamProject getSeamProject() {
		ISeamProject project = null;
		try {
			project = SeamCorePlugin.getSeamProject(getProject());
		} catch (Exception e) {
			SeamCorePlugin.getDefault().logError("Can't get Seam Project", e);
		}
		return project;
	}
}