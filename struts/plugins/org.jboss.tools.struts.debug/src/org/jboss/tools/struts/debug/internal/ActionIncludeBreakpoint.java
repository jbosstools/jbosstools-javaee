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
import org.eclipse.osgi.util.NLS;

import org.jboss.tools.struts.debug.internal.condition.ActionIncludeCondition;
import org.jboss.tools.jst.web.debug.DebugMessages;

/**
 * @author Igels
 */
public class ActionIncludeBreakpoint extends ActionConditionBreakpoint {

	private static IActionConditionBreakpointManager actionConditionBreakpointManager = new ActionConditionBreakpointManager();

	private static final String BREAKPOINT_CLASS_NAME = "org.apache.struts.action.RequestProcessor"; //$NON-NLS-1$
	private static final String BREAKPOINT_METHOD_NAME = "processForward"; //$NON-NLS-1$
	private static final String BREAKPOINT_METHOD_SIGNATURE = "(Ljavax/servlet/http/HttpServletRequest;Ljavax/servlet/http/HttpServletResponse;Lorg/apache/struts/action/ActionMapping;)Z"; //$NON-NLS-1$
	public ActionIncludeBreakpoint() {
	}

	public ActionIncludeBreakpoint(final IResource resource, Map attributes) throws CoreException {
		super(resource, BREAKPOINT_CLASS_NAME, BREAKPOINT_METHOD_NAME, BREAKPOINT_METHOD_SIGNATURE, true, false, attributes);
	}

	protected IActionConditionBreakpointManager getActionConditionBreakpointManager() {
		return actionConditionBreakpointManager;
	}

	public String getBreakpointType() {
		return ActionBreakpoint.ACTION_INCLUDE_BREAKPOINT;
	}

	protected String getActionCondition() throws CoreException {
		String condition = new ActionIncludeCondition(getActionMappingPath(), getIncludeName()).getCondition();
		return condition;
	}

	protected String getIncludeName() throws CoreException {
		return ensureMarker().getAttribute(ATTR_INCLUDE_NAME, ""); //$NON-NLS-1$
	}

	protected String getActionMappingPath() throws CoreException {
		return ensureMarker().getAttribute(ActionBreakpoint.ATTR_ACTION_MAPPING_PATH, ""); //$NON-NLS-1$
	}

	public String getLabelText() {
		try {
			return NLS.bind(DebugMessages.ActionIncludeBreakpoint_name, (new String[]{getMarker().getResource().getName(), getActionMappingPath(), getIncludeName()})); 
		} catch (CoreException e) {
            StrutsDebugPlugin.log(e);
			return "error";
		}
	}
}