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

import org.jboss.tools.jst.web.debug.DebugMessages;

/**
 * @author igels
 */
public class ActionFormValidateBreakpoint extends ActionFormBreakpoint {

	private static IActionConditionBreakpointManager actionConditionBreakpointManager = new ActionConditionBreakpointManager();

	private static final String BREAKPOINT_METHOD_NAME = "processValidate"; //$NON-NLS-1$
	private static final String BREAKPOINT_METHOD_SIGNATURE = "(Ljavax/servlet/http/HttpServletRequest;Ljavax/servlet/http/HttpServletResponse;Lorg/apache/struts/action/ActionForm;Lorg/apache/struts/action/ActionMapping;)Z"; //$NON-NLS-1$
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
			return NLS.bind(DebugMessages.ActionFormValidateBreakpoint_name, (new String[]{getMarker().getResource().getName(), getActionMappingPath()})); 
		} catch (CoreException e) {
            StrutsDebugPlugin.log(e);
			return "error";
		}
	}
}