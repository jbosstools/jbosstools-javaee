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
package org.jboss.tools.struts.debug.model.handlers;

import java.util.Properties;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IBreakpointManager;
import org.eclipse.debug.core.model.IBreakpoint;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.struts.model.helpers.StrutsBreakpointManager;
import org.jboss.tools.struts.model.helpers.StrutsProcessStructureHelper;

public class EnableBreakPointHandler extends AddBreakPointHandler {
	public boolean isEnabled(XModelObject object) {
		if (object == null || !object.isActive()) return false;
		return hasBreakpointMarker(object) && (getNewValue() ^ isBreakpointEnabled(object));
	}

	public void executeHandler(XModelObject object, Properties p) throws Exception {
		if (!isEnabled(object)) return;
///		if (object instanceof ReferenceObjectImpl && ((ReferenceObjectImpl)object).getReference() != null) 
///			object = ((ReferenceObjectImpl)object).getReference();

		IFile file = (IFile)object.getAdapter(IFile.class);
		IMarker markers[] = file.findMarkers(StrutsBreakpointManager.MODEL_BREAKPOINT, true, IResource.DEPTH_INFINITE);
		IBreakpointManager breakpointManager = DebugPlugin.getDefault().getBreakpointManager();
		String modelPath = object.getPath();
		for (int i = 0; i < markers.length; i++)
			if (modelPath.equals(markers[i].getAttribute(StrutsBreakpointManager.ATTR_MODEL_PATH)))
			{
				IBreakpoint breakpoint = breakpointManager.getBreakpoint(markers[i]);
				if (breakpoint != null) breakpoint.setEnabled(getNewValue()); 
			}
	}	
	
	boolean getNewValue() {
		return true;
	}
	
	boolean isBreakpointEnabled(XModelObject object) {
		XModelObject process = StrutsProcessStructureHelper.instance.getProcess(object);
		StrutsBreakpointManager manager = StrutsProcessStructureHelper.instance.getBreakpointManager(process);
		int breakpointStatus = manager.getBreakpointStatus(object); 
		return (breakpointStatus & StrutsBreakpointManager.STATUS_BREAKPOINT_ENABLED) != 0; 
	}
}
