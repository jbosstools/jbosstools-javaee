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

import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugException;
import org.eclipse.jdt.internal.debug.core.model.JDIStackFrame;
import org.eclipse.jdt.internal.debug.core.model.JDIThread;

import org.jboss.tools.struts.debug.internal.condition.ActionForwardCondition;
import org.jboss.tools.jst.web.debug.DebugMessages;

public class ActionForwardBreakpoint extends ActionConditionBreakpoint {

	private static IActionConditionBreakpointManager actionConditionBreakpointManager = new ActionConditionBreakpointManager();

	private static final String BREAKPOINT_CLASS_NAME = "org.apache.struts.action.RequestProcessor";
	private static final String BREAKPOINT_METHOD_NAME = "processForwardConfig";
	private static final String BREAKPOINT_METHOD_SIGNATURE = "(Ljavax/servlet/http/HttpServletRequest;Ljavax/servlet/http/HttpServletResponse;Lorg/apache/struts/config/ForwardConfig;)V";
	private static final String BREAKPOINT_LABEL_TEXT_KEY = "ActionForwardBreakpoint.name";

	public ActionForwardBreakpoint() {
	}

	public ActionForwardBreakpoint(final IResource resource, Map attributes) throws CoreException {
		super(resource, BREAKPOINT_CLASS_NAME, BREAKPOINT_METHOD_NAME, BREAKPOINT_METHOD_SIGNATURE, true, false, attributes);
	}

	protected ActionForwardBreakpoint(final IResource resource, Map attributes, String breakpointClassName, String breakpointMethodName, String breakpointSignature) throws CoreException {
		super(resource, breakpointClassName, breakpointMethodName, breakpointSignature, true, false, attributes);
	}

	protected IActionConditionBreakpointManager getActionConditionBreakpointManager() {
		return actionConditionBreakpointManager;
	}

	public String getBreakpointType() {
		return ActionBreakpoint.ACTION_FORWARD_BREAKPOINT;
	}

	protected String getActionCondition() throws CoreException {
		String condition = new ActionForwardCondition(getActionTypeName(), getForwardName()).getCondition();
		return condition;
	}

	protected JDIStackFrame computeNewStackFrame(JDIThread jdiThread) throws DebugException {
	    List frames = jdiThread.computeNewStackFrames();
		JDIStackFrame frame = (JDIStackFrame)frames.get(1);
		if("process".equals(frame.getMethodName())) {
		    // RequestProcessor
			return frame;
		} else if ("processForwardConfig".equals(frame.getMethodName())) {
		    // TilesRequestProcessor
		    frame = (JDIStackFrame)frames.get(2);
			if("process".equals(frame.getMethodName())) {
			    // RequestProcessor
				return frame;
			}
		}
		return null;
	}

	protected String getActionTypeName() throws CoreException {
		return ensureMarker().getAttribute(ATTR_ACTION_TYPE_NAME, "");
	}

	protected String getForwardName() throws CoreException {
		return ensureMarker().getAttribute(ATTR_FORWARD_NAME, "");
	}

	protected String getActionMappingPath() throws CoreException {
		return ensureMarker().getAttribute(ActionBreakpoint.ATTR_ACTION_MAPPING_PATH, "");
	}

	public String getLabelText() {
		try {
			return DebugMessages.getString(BREAKPOINT_LABEL_TEXT_KEY, new String[]{getMarker().getResource().getName(), getActionMappingPath(), getForwardName()});
		} catch (CoreException e) {
            StrutsDebugPlugin.log(e);
			return "error";
		}
	}
}