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

import java.util.ArrayList;
import java.util.List;

/**
 * @author igels
 *
 */
public class ActionConditionBreakpointManager implements IActionConditionBreakpointManager {

	private List<ActionConditionBreakpoint> breakpoints = new ArrayList<ActionConditionBreakpoint>();
	private List<ActionConditionBreakpoint> usedBreakpoints = new ArrayList<ActionConditionBreakpoint>();
	private boolean stopHandleBreakpoints = false;

	public ActionConditionBreakpointManager() {
	}

	public synchronized void addToBreakpointList(ActionConditionBreakpoint breakpoint) {
		breakpoints.add(breakpoint);
	}

	public synchronized void addToUsedBreakpointList(ActionConditionBreakpoint breakpoint) {
		if(!usedBreakpoints.contains(breakpoint)) {
			usedBreakpoints.add(breakpoint);
		}
	}

	public synchronized void removeBreakpoint(ActionConditionBreakpoint breakpoint) {
		breakpoints.remove(breakpoint);
		usedBreakpoints.remove(breakpoint);
	}

	public synchronized ActionConditionBreakpoint getCurentBreakpoint() {
		if(usedBreakpoints.size()>0) {
			return (ActionConditionBreakpoint)usedBreakpoints.get(usedBreakpoints.size()-1);
		}
		return null;
	}

	public synchronized void setStopHandleBreakpoints(boolean stop) {
		if(!stop || (breakpoints.size() > usedBreakpoints.size())) {
			stopHandleBreakpoints = stop;
		}
	}

	public synchronized boolean isHandlingBreakpointStoped() {
		return stopHandleBreakpoints;
	}

	public synchronized boolean computeNotUsedBreakpoints() {
		if(breakpoints.size() > usedBreakpoints.size()) {
			return true;
		}
		stopHandleBreakpoints = false;
		usedBreakpoints.clear();
		return false;
	}

}