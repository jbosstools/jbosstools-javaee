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
package org.jboss.tools.struts.debug.internal;

import java.util.Map;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

import org.jboss.tools.struts.debug.internal.condition.GlobalForwardCondition;
import org.jboss.tools.jst.web.debug.DebugMessages;

/**
 * @author Igels
 */
public class TilesDefinitionGlobalForwardBreakpoint extends ActionTilesDefinitionForwardBreakpoint {

	private static final String BREAKPOINT_LABEL_TEXT_KEY = "TilesDefinitionGlobalForwardBreakpoint.name";

	public TilesDefinitionGlobalForwardBreakpoint(){
	}

	public TilesDefinitionGlobalForwardBreakpoint(final IResource resource, Map attributes) throws CoreException {
		super(resource, attributes);
	}

	protected String getActionCondition() throws CoreException {
		String condition = new GlobalForwardCondition(getForwardName()).getCondition();
		return condition;
	}

	public String getBreakpointType() {
		return ActionBreakpoint.TILES_DIFINITION_GLOBAL_FORWARD_BREAKPOINT;
	}

	public String getLabelText() {
		try {
			return DebugMessages.getString(BREAKPOINT_LABEL_TEXT_KEY, new String[]{getMarker().getResource().getName(), getForwardName()});
		} catch (CoreException e) {
            StrutsDebugPlugin.log(e);
			return "error";
		}
	}
}