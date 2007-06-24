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

import org.eclipse.core.internal.resources.Workspace;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.ISourceLocator;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.jdt.debug.core.IJavaStackFrame;
import org.eclipse.jdt.internal.debug.core.breakpoints.JavaMethodBreakpoint;
import org.eclipse.jdt.internal.debug.core.model.JDIThread;

import org.jboss.tools.jst.web.debug.IBreakpointPresentation;
import org.jboss.tools.jst.web.debug.WebDebugPlugin;
import org.jboss.tools.jst.web.debug.xpl.DebugSupport;
import org.jboss.tools.jst.web.launching.sourcelookup.IBreakpointSourceFinder;
import org.jboss.tools.jst.web.launching.sourcelookup.xpl.WebSourceLocator;

import com.sun.jdi.event.Event;

public abstract class ActionBreakpoint extends JavaMethodBreakpoint implements IBreakpointSourceFinder, IBreakpointPresentation {

	public static final String ACTION_ENTER_BREAKPOINT = "org.jboss.tools.struts.debug.strutsActionEnterBreakpointMarker";
	public static final String ACTION_EXCEPTION_BREAKPOINT = "org.jboss.tools.struts.debug.strutsActionExceptionBreakpointMarker";
	public static final String ACTION_FORWARD_BREAKPOINT = "org.jboss.tools.struts.debug.strutsActionForwardBreakpointMarker";
	public static final String ACTION_TILES_DIFINITION_FORWARD_BREAKPOINT = "org.jboss.tools.struts.debug.strutsActionTilesDefinitionForwardBreakpointMarker";
	public static final String ACTION_ATTR_FORWARD_BREAKPOINT = "org.jboss.tools.struts.debug.strutsActionAttrForwardBreakpointMarker";
    public static final String ACTION_INCLUDE_BREAKPOINT = "org.jboss.tools.struts.debug.strutsActionIncludeBreakpointMarker";
	public static final String GLOBAL_FORWARD_BREAKPOINT = "org.jboss.tools.struts.debug.strutsGlobalForwardBreakpointMarker";
	public static final String TILES_DIFINITION_GLOBAL_FORWARD_BREAKPOINT = "org.jboss.tools.struts.debug.strutsTilesDefinitionGlobalForwardBreakpointMarker";
	public static final String GLOBAL_EXCEPTION_BREAKPOINT = "org.jboss.tools.struts.debug.strutsGlobalExceptionBreakpointMarker";
	public static final String PAGE_ENTER_BREAKPOINT = "org.jboss.tools.struts.debug.strutsPageEnterBreakpointMarker";
	public static final String ACTION_FORM_POPULATE_BREAKPOINT = "org.jboss.tools.struts.debug.actionFormPopulateMarker";
	public static final String ACTION_FORM_VALIDATE_BREAKPOINT = "org.jboss.tools.struts.debug.actionFormValidateMarker";

	public static final String ATTR_MODEL_PATH	= "org.jboss.tools.common.model.debug.modelPath";
	public static final String ATTR_ACTION_TYPE_NAME = "org.jboss.tools.struts.debug.actionTypeName";
	public static final String ATTR_EXCEPTION_TYPE_NAME = "org.jboss.tools.struts.debug.exceptionTypeName";
	public static final String ATTR_FORWARD_NAME = "org.jboss.tools.struts.debug.forwardName";
	public static final String ATTR_INCLUDE_NAME = "org.jboss.tools.struts.debug.includeName";
	public static final String ATTR_ACTION_MAPPING_PATH = "org.jboss.tools.struts.debug.actionMappingPath";
	public static final String ATTR_ACTION_MAPPING_NAME = "org.jboss.tools.struts.debug.actionMappingName";

	private boolean hiden = false;
	private boolean startServer = false;

	public ActionBreakpoint() {
	}

	public ActionBreakpoint(IResource resource, String typePattern, final String methodName, String methodSignature, boolean entry, boolean exit, Map attributes) throws CoreException {
		super(resource, typePattern, methodName, methodSignature, entry, exit, false, -1, 669, 676, 0, true, attributes);
	}

	public void setMarker(IMarker marker) throws CoreException {
		IResource resource = marker.getResource();
		((Workspace)resource.getWorkspace()).getMarkerManager().findMarkerInfo(resource, marker.getId()).setType(getBreakpointType());
		super.setMarker(marker);
	}

	abstract public String getBreakpointType();
	abstract public String getLabelText();

	public boolean isSource(IStackFrame stackFrame)	{
		boolean result = false;

		if (stackFrame instanceof IJavaStackFrame && stackFrame.getThread().isSuspended()) {
			IJavaStackFrame javaStackFrame = (IJavaStackFrame)stackFrame;
			String receivingTypeName = null;
			String methodName = null;
			try {
				receivingTypeName = javaStackFrame.getReceivingTypeName();
				methodName = javaStackFrame.getMethodName();
			} catch (DebugException ex)	{
	            StrutsDebugPlugin.log(ex);
			}

			try {
			    boolean matchConcretTypes = getTypeName().equals(receivingTypeName);
			    if(!matchConcretTypes && !getTypeName().equals(javaStackFrame.getDeclaringTypeName())) {
			        return false;
			    }
				result = getMethodName().equals(methodName);
			} catch (Exception ex) {
	            StrutsDebugPlugin.log(ex);
				result = false;
			}
		}

		return result;
	}

	public String getTypeName() throws CoreException {
		String result = super.getTypeName();
		return (result != null) ? result : "<unknown source>"; 
	}

    public boolean isHiden() {
        return hiden;
    }

    public void setHiden(boolean hiden) {
        this.hiden = hiden;
    }

	public String getModelIdentifier() {
		if(startServer) {
			setStartServerStatus(false);
			return super.getModelIdentifier();
		}
		return WebDebugPlugin.PLUGIN_ID;
	}

	public void setStartServerStatus(boolean status) {
		startServer = status;
	}

	protected boolean suspendForEvent(Event event, JDIThread thread) {
		DebugSupport.initWebSourceLocator(thread);
		return super.suspendForEvent(event, thread);
	}

	protected boolean suspendForCondition(Event event, JDIThread thread) {
		DebugSupport.initWebSourceLocator(thread);
		return super.suspendForCondition(event, thread);
	}
}