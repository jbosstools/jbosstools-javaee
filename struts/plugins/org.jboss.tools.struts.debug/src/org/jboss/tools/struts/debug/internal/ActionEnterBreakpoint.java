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

public class ActionEnterBreakpoint extends ActionBreakpoint {

	public static final String BREAKPOINT_METHOD_NAME = "execute";
	public static final String BREAKPOINT_METHOD_SIGNATURE = "(Lorg/apache/struts/action/ActionMapping;Lorg/apache/struts/action/ActionForm;Ljavax/servlet/http/HttpServletRequest;Ljavax/servlet/http/HttpServletResponse;)Lorg/apache/struts/action/ActionForward;";

	private static final String BREAKPOINT_LABEL_TEXT_KEY = "ActionEnterBreakpoint.name";

	public ActionEnterBreakpoint() {
	}

	public ActionEnterBreakpoint(final IResource resource, final String actionClassName, Map attributes) throws CoreException {
		super(resource, actionClassName, BREAKPOINT_METHOD_NAME, BREAKPOINT_METHOD_SIGNATURE, true, false, attributes);
	}

	public String getBreakpointType() {
		return ActionBreakpoint.ACTION_ENTER_BREAKPOINT;
	}

	protected String getActionMappingPath() throws CoreException {
		return ensureMarker().getAttribute(ActionBreakpoint.ATTR_ACTION_MAPPING_PATH, "");
	}

	protected String getActionTypeName() throws CoreException {
		return ensureMarker().getAttribute(ATTR_ACTION_TYPE_NAME, "");
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