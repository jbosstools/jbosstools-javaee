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

import org.jboss.tools.jst.web.debug.internal.JspLineBreakpoint;
import org.jboss.tools.jst.web.debug.DebugMessages;

public class PageEnterBreakpoint extends JspLineBreakpoint {

	private static final String BREAKPOINT_LABEL_TEXT_KEY = "PageEnterBreakpoint.name";

	public PageEnterBreakpoint() throws CoreException {
	}

	public PageEnterBreakpoint(IResource resource, String pageName, Map attributes) throws CoreException {
		super(resource, pageName, 1, 1, 1, true, attributes, ActionBreakpoint.PAGE_ENTER_BREAKPOINT);
	}

	public String getLabelText() {
		try {
			return DebugMessages.getString(BREAKPOINT_LABEL_TEXT_KEY, new String[]{getPattern()});
		} catch (CoreException e) {
            StrutsDebugPlugin.log(e);
			return "error";
		}
	}
}