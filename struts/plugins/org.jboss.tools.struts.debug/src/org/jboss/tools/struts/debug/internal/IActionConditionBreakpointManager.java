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

/**
 * @author igels
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public interface IActionConditionBreakpointManager {

	public void setStopHandleBreakpoints(boolean stop);
	public boolean isHandlingBreakpointStoped();
	public void addToUsedBreakpointList(ActionConditionBreakpoint breakpoint);
	public void addToBreakpointList(ActionConditionBreakpoint breakpoint);
	public ActionConditionBreakpoint getCurentBreakpoint();
	public void removeBreakpoint(ActionConditionBreakpoint breakpoint);
	public boolean computeNotUsedBreakpoints();
}