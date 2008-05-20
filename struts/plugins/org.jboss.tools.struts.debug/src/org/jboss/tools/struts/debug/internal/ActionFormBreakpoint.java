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
import org.eclipse.debug.core.DebugException;
import org.eclipse.jdt.internal.debug.core.model.JDIStackFrame;
import org.eclipse.jdt.internal.debug.core.model.JDIThread;

import org.jboss.tools.struts.debug.internal.condition.ActionFormCondition;

/**
 * @author igels
 */
public abstract class ActionFormBreakpoint extends ActionConditionBreakpoint {

	private static final String BREAKPOINT_CLASS_NAME = "org.apache.struts.action.RequestProcessor";

	public ActionFormBreakpoint() {
	}

	protected ActionFormBreakpoint(final IResource resource, Map attributes, String breakPointMethodName, String breakPointMethodSignature) throws CoreException {
		super(resource, BREAKPOINT_CLASS_NAME, breakPointMethodName, breakPointMethodSignature, false, true, attributes);
	}

	protected JDIStackFrame computeNewStackFrame(JDIThread jdiThread) throws DebugException {
		JDIStackFrame frame = (JDIStackFrame)jdiThread.computeNewStackFrames().get(1);
		if("process".equals(frame.getMethodName())) {
			return frame;
		}
		return null;
	}

	protected String getActionCondition() throws CoreException {
		String condition = new ActionFormCondition(getActionMappingPath()).getCondition();
		return condition;
	}

	protected String getActionMappingPath() throws CoreException {
		return ensureMarker().getAttribute(ActionBreakpoint.ATTR_ACTION_MAPPING_PATH, "");
	}
}