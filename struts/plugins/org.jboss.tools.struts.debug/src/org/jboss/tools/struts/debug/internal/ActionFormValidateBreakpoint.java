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

import org.jboss.tools.jst.web.debug.DebugMessages;

/**
 * @author igels
 */
public class ActionFormValidateBreakpoint extends ActionFormBreakpoint {

	private static IActionConditionBreakpointManager actionConditionBreakpointManager = new ActionConditionBreakpointManager();

	private static final String BREAKPOINT_METHOD_NAME = "processValidate";
	private static final String BREAKPOINT_METHOD_SIGNATURE = "(Ljavax/servlet/http/HttpServletRequest;Ljavax/servlet/http/HttpServletResponse;Lorg/apache/struts/action/ActionForm;Lorg/apache/struts/action/ActionMapping;)Z";
	private static final String BREAKPOINT_LABEL_TEXT_KEY = "ActionFormValidateBreakpoint.name";

	public ActionFormValidateBreakpoint() {
	}

	public ActionFormValidateBreakpoint(final IResource resource, Map attributes) throws CoreException {
		super(resource, attributes, BREAKPOINT_METHOD_NAME, BREAKPOINT_METHOD_SIGNATURE);
	}

	protected IActionConditionBreakpointManager getActionConditionBreakpointManager() {
		return actionConditionBreakpointManager;
	}

	public String getBreakpointType() {
		return ActionBreakpoint.ACTION_FORM_VALIDATE_BREAKPOINT;
	}

	public String getLabelText() {
		try {
			return DebugMessages.getString(BREAKPOINT_LABEL_TEXT_KEY, new String[]{getMarker().getResource().getName(), getActionMappingPath()});
		} catch (CoreException e) {
            StrutsDebugPlugin.log(e);
			return "error";
		}
	}
}