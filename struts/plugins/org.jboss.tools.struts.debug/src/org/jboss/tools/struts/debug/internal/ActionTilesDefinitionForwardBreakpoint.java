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

import org.jboss.tools.jst.web.debug.DebugMessages;

/**
 * @author Igels
 */
public class ActionTilesDefinitionForwardBreakpoint extends ActionForwardBreakpoint {

	private static IActionConditionBreakpointManager actionConditionBreakpointManager = new ActionConditionBreakpointManager();

	private static final String BREAKPOINT_CLASS_NAME = "org.apache.struts.tiles.TilesRequestProcessor";
	private static final String BREAKPOINT_METHOD_NAME = "processTilesDefinition";
	private static final String BREAKPOINT_METHOD_SIGNATURE = "(Ljava/lang/String;ZLjavax/servlet/http/HttpServletRequest;Ljavax/servlet/http/HttpServletResponse;)Z";
	private static final String BREAKPOINT_LABEL_TEXT_KEY = "ActionTilesDefinitionForwardBreakpoint.name";

	public ActionTilesDefinitionForwardBreakpoint() {
	}

	public ActionTilesDefinitionForwardBreakpoint(final IResource resource, Map attributes) throws CoreException {
		super(resource, attributes, BREAKPOINT_CLASS_NAME, BREAKPOINT_METHOD_NAME, BREAKPOINT_METHOD_SIGNATURE);
	}

	protected IActionConditionBreakpointManager getActionConditionBreakpointManager() {
		return actionConditionBreakpointManager;
	}

	public String getBreakpointType() {
		return ActionBreakpoint.ACTION_TILES_DIFINITION_FORWARD_BREAKPOINT;
	}

	protected JDIStackFrame computeNewStackFrame(JDIThread jdiThread) throws DebugException {
	    List frames = jdiThread.computeNewStackFrames();
		JDIStackFrame frame = (JDIStackFrame)frames.get(2);
		if("process".equals(frame.getMethodName())) {
		    // RequestProcessor
			return frame;
		}
		return null;
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