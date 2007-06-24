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

import org.jboss.tools.struts.debug.internal.condition.ActionExceptionCondition;
import org.jboss.tools.jst.web.debug.DebugMessages;

public class ActionExceptionBreakpoint extends ActionConditionBreakpoint {

	private static IActionConditionBreakpointManager actionConditionBreakpointManager = new ActionConditionBreakpointManager();

	private static final String BREAKPOINT_CLASS_NAME = "org.apache.struts.action.RequestProcessor";
	private static final String BREAKPOINT_METHOD_NAME = "processException";
	private static final String BREAKPOINT_METHOD_SIGNATURE = "(Ljavax/servlet/http/HttpServletRequest;Ljavax/servlet/http/HttpServletResponse;Ljava/lang/Exception;Lorg/apache/struts/action/ActionForm;Lorg/apache/struts/action/ActionMapping;)Lorg/apache/struts/action/ActionForward;";
	private static final String BREAKPOINT_LABEL_TEXT_KEY = "ActionExceptionBreakpoint.name";

	public ActionExceptionBreakpoint() { 
	}

	public ActionExceptionBreakpoint(IResource resource, Map attributes) throws CoreException {
		super(resource, BREAKPOINT_CLASS_NAME, BREAKPOINT_METHOD_NAME, BREAKPOINT_METHOD_SIGNATURE, true, false, attributes);
	}

	/* (non-Javadoc)
	 * @see org.jboss.tools.struts.debug.internal.ActionConditionBreakpoint#getActionCondition()
	 */
	protected String getActionCondition() throws CoreException {
		String condition = new ActionExceptionCondition(getActionTypeName(), getExceptionTypeName()).getCondition();
		return condition;
	}

	protected IActionConditionBreakpointManager getActionConditionBreakpointManager() {
		return actionConditionBreakpointManager;
	}

	public String getBreakpointType() {
		return ActionBreakpoint.ACTION_EXCEPTION_BREAKPOINT;
	}

	public String getActionTypeName() throws CoreException {
		return ensureMarker().getAttribute(ATTR_ACTION_TYPE_NAME, "");
	}

	public String getExceptionTypeName() throws CoreException {
		return ensureMarker().getAttribute(ATTR_EXCEPTION_TYPE_NAME, "");
	}

	protected String getActionMappingPath() throws CoreException {
		return ensureMarker().getAttribute(ActionBreakpoint.ATTR_ACTION_MAPPING_PATH, "");
	}

	public String getLabelText() {
		try {
			return DebugMessages.getString(BREAKPOINT_LABEL_TEXT_KEY, new String[]{getMarker().getResource().getName(), getActionMappingPath(), getExceptionTypeName()});
		} catch (CoreException e) {
            StrutsDebugPlugin.log(e);
			return "error";
		}
	}
}