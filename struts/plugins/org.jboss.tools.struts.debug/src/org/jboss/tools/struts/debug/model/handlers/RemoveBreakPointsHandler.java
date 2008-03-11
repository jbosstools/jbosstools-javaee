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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.struts.debug.internal.ActionBreakpoint;
import org.jboss.tools.struts.debug.internal.StrutsDebugPlugin;

public class RemoveBreakPointsHandler extends AddBreakPointHandler {

	public boolean isEnabled(XModelObject object) {
		if(object == null || !object.isActive()) return false;
		return hasBreakpointMarker(object);
	}

	public void executeHandler(XModelObject object, Properties p) throws Exception {
		if (!isEnabled(object)) return;
///		if(object instanceof ReferenceObjectImpl && ((ReferenceObjectImpl)object).getReference() != null) { 
///			object = ((ReferenceObjectImpl)object).getReference();
		IFile file = (IFile)object.getAdapter(IFile.class);
		String modelPath = object.getPath();
		String markerIds[] = getMarkerIdByXModelObject(object);

		try	{
			IMarker markers[];
			List markerList = new ArrayList();
			for (int i = 0; i < markerIds.length; i++) { 
				markers = file.findMarkers(markerIds[i], true, IResource.DEPTH_INFINITE);
				for (int j = 0; j < markers.length; j++) {
				    markerList.add(markers[j]);
				}
			}
			Iterator iterator = markerList.iterator();
			while (iterator.hasNext()) {
				IMarker marker = (IMarker)iterator.next();
				if (marker.getAttribute(ActionBreakpoint.ATTR_MODEL_PATH, "").equals(modelPath))
					DebugPlugin.getDefault().getBreakpointManager().removeBreakpoint(
						DebugPlugin.getDefault().getBreakpointManager().getBreakpoint(
							marker
						),
						true
					);
			}
		} catch (CoreException e) {
            StrutsDebugPlugin.log(e);
		}
	}

}
