/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.jsf.ui.internal.handlers;

import org.eclipse.core.resources.IProject;
import org.jboss.tools.common.model.ui.internal.handlers.ProjectRootHandler;
import org.jboss.tools.common.model.util.EclipseResourceUtil;

public class AddCapabilitiesHandler extends ProjectRootHandler {

	protected String getActionPath() {
		return "EclipseActions.AddCustomJSFCapabilities"; //$NON-NLS-1$
	}
	
	protected boolean hasModelNature(IProject project) {
		return EclipseResourceUtil.getModelNature(project) != null;
	}
	
}
