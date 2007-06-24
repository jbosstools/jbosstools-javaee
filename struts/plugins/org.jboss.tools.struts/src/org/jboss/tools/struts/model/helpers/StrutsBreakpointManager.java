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
package org.jboss.tools.struts.model.helpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IBreakpointListener;
import org.eclipse.debug.core.model.IBreakpoint;

import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.impl.XModelObjectImpl;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.struts.StrutsConstants;
import org.jboss.tools.struts.StrutsModelPlugin;
import org.jboss.tools.struts.model.handlers.SelectOnDiagramHandler;

public class StrutsBreakpointManager implements IBreakpointListener {
	public static final int STATUS_NO_BREAKPOINT 		= 1;
	public static final int STATUS_BREAKPOINT_DISABLED	= 2;
	public static final int STATUS_BREAKPOINT_ENABLED	= 4;
	public static final int STATUS_BREAKPOINT_ACTIVE	= 8;
	public static final String MODEL_BREAKPOINT	 = "org.jboss.tools.common.model.modelBreakpointMarker";
	public static final String ATTR_MODEL_PATH	 = "org.jboss.tools.common.model.debug.modelPath";
	private XModelObject process = null;
	private StatusMap statusMap = new StatusMap();
	IBreakpointListener listener = null;

	public StrutsBreakpointManager(XModelObject process) {
		this.process = process;
	}
	
	public void activate() {
		if(listener != null) return;
		DebugPlugin.getDefault().getBreakpointManager().addBreakpointListener(listener = this);
		update();
	}
	
	public void deactivate() {
		if(listener == null) return;
		DebugPlugin.getDefault().getBreakpointManager().removeBreakpointListener(this);
		listener = null;
	}
	
	private boolean isValid() {
		if(process == null) return false;
		if(!process.isActive()) {
			deactivate();
			return false;
		}; 
		return true;		
	}
	
	public int getBreakpointStatus(XModelObject object)	{
///		if (object instanceof ReferenceObjectImpl && ((ReferenceObjectImpl)object).getReference() != null) { 
///			object = ((ReferenceObjectImpl)object).getReference();
///		}
		return statusMap.getSummaryBreakpointStatus(object.getPath());
	}

	public int getBreakpointStatus(XModelObject object, String markerType)	{
	    return statusMap.getBreakpointStatus(object.getPath(), markerType);
	}

	public int getActiveBreakpointNumber(XModelObject object) {
///		if (object instanceof ReferenceObjectImpl && ((ReferenceObjectImpl)object).getReference() != null) { 
///			object = ((ReferenceObjectImpl)object).getReference();
///		}
		return statusMap.getActiveBreakpointNumber(object.getPath());
	}

	public void update() {
		IResource file = getProcessResource();
		if (file == null || !file.exists()) return;

		IMarker markers[];
		try	{
			markers = file.findMarkers(MODEL_BREAKPOINT, true, IResource.DEPTH_INFINITE);
		} catch (CoreException ex) {
			StrutsModelPlugin.getPluginLog().logError(ex);
			return;
		}

		StatusMap newStatusMap = new StatusMap();
		for (int i = 0; i < markers.length; i++) {
			String modelPath = markers[i].getAttribute(ATTR_MODEL_PATH, null);
			if (modelPath != null) {
				IBreakpoint breakpoint = DebugPlugin.getDefault().getBreakpointManager().getBreakpoint(markers[i]);
				if (breakpoint != null)	{
					try {
						newStatusMap.changeBreakpointStatus(modelPath, markers[i].getType(), breakpoint.isEnabled() ? STATUS_BREAKPOINT_ENABLED : STATUS_BREAKPOINT_DISABLED);
					} catch (CoreException e) {
						StrutsModelPlugin.getPluginLog().logError(e);
					}
				}
			}
		}

		BreakpointId[] oldKeys = statusMap.getBreakpointIds();
		for(int i=0; i<oldKeys.length; i++) {
			if(!newStatusMap.containsKey(oldKeys[i])) {
				fireBreakpointStatusChanged(process.getModel().getByPath(oldKeys[i].getModelPath()), oldKeys[i].getMarkerType(), STATUS_NO_BREAKPOINT);
			}
		}

		BreakpointId[] newKeys = newStatusMap.getBreakpointIds();
		for(int i=0; i<newKeys.length; i++) {
			int newStatus = newStatusMap.getBreakpointStatus(newKeys[i]);
			if(newStatus!=statusMap.getBreakpointStatus(newKeys[i])) {
				XModelObject object = process.getModel().getByPath(newKeys[i].getModelPath());
				if (object != null) {
					fireBreakpointStatusChanged(object, newKeys[i].getMarkerType(), newStatus);
				} else {
//we must to delete the marker
				}
			}
		}
		statusMap = newStatusMap;
	}

	public boolean isDebugMode() {
		return false;
	}

	public void fireBreakpointStatusChanged(XModelObject object, String markerType, int newStatus, boolean mergeStatuses) {
		if (object != null)	{
			String modelPath = object.getPath();
			if(markerType==null) {
				statusMap.changeAllBreakpointStatus(modelPath, newStatus, mergeStatuses);
			} else {
				statusMap.changeBreakpointStatus(modelPath, markerType, newStatus, mergeStatuses);
			}

			XModelObjectImpl diagramObject = null;
			String entity = object.getModelEntity().getName();
			if (StrutsConstants.ENT_PROCESSITEM.equals(entity) || StrutsConstants.ENT_PROCESSITEMOUT.equals(entity)) {  
				diagramObject = (XModelObjectImpl)object;
			} else {
				diagramObject = (XModelObjectImpl)SelectOnDiagramHandler.getItemOnProcess(object);
			}

			if (diagramObject != null) {
				new DiagramChangeHandler(diagramObject, new Integer(newStatus)).start();
			}
		}
	}

	public void fireBreakpointStatusChanged(XModelObject object, String markerType, int newStatus) {
	    fireBreakpointStatusChanged(object, markerType, newStatus, false);
	}

	private static class DiagramChangeHandler implements Runnable {
		private XModelObjectImpl diagramObject;
		private Integer status;
		private Thread thread;

		public void start() {
			thread = new Thread(this);
			thread.setDaemon(false);
			thread.start();
		}

		public DiagramChangeHandler(XModelObjectImpl diagramObject, Integer status) {
			this.diagramObject = diagramObject;
			this.status = status;
		}

		public void run() {
			diagramObject.fireObjectChanged(status);
		}
	}

	public void fireAllBreakpointStatusChanged(int newStatus, boolean mergeStatuses) {
	    statusMap.changeAllBreakpointStatus(newStatus, mergeStatuses);
	}

	public void fireAllBreakpointStatusChanged(XModelObject object, int newStatus) {
		fireBreakpointStatusChanged(object, null, newStatus);
	}

	private IResource getProcessResource() {
		return EclipseResourceUtil.getResource(process); 
	}

	private void doBreakpointChanged(IBreakpoint breakpoint, int newStatus)	{
		if(!isValid()) return;
		IMarker marker = breakpoint.getMarker();
		XModelObject object = getXMO(breakpoint);
		try {
			if ((object != null) && marker.isSubtypeOf(MODEL_BREAKPOINT)) {
				fireBreakpointStatusChanged(object, marker.getType(), newStatus);
			}
		} catch (CoreException e) {
			StrutsModelPlugin.getPluginLog().logError(e);
		}
	}

	public void breakpointAdded(IBreakpoint breakpoint)	{
		doBreakpointChanged(breakpoint, STATUS_BREAKPOINT_ENABLED);
	}

	public void breakpointRemoved(IBreakpoint breakpoint, IMarkerDelta delta) {
		doBreakpointChanged(breakpoint, STATUS_NO_BREAKPOINT);
	}

	public void breakpointChanged(IBreakpoint breakpoint, IMarkerDelta delta) {
		if(!isValid()) return;
		try	{
			XModelObject object = getXMO(breakpoint);
			if(object!=null) {
			    String markerType = null;
			    if(delta!=null) {
			        markerType = delta.getMarker().getType();
			    }
				int status = getBreakpointStatus(object, markerType);
				int newStatus;
				if(breakpoint.isEnabled()) {
				    newStatus = STATUS_BREAKPOINT_ENABLED | (status & STATUS_BREAKPOINT_ACTIVE);
				} else {
				    newStatus = STATUS_BREAKPOINT_DISABLED | (status & STATUS_BREAKPOINT_ACTIVE);
				}
				doBreakpointChanged(breakpoint, newStatus);
			}
		} catch (CoreException e) {
			StrutsModelPlugin.getPluginLog().logError(e);
		}
	}	

	private XModelObject getXMO(IBreakpoint breakpoint)	{
		IResource processResource = getProcessResource(); 
		if (processResource.equals(breakpoint.getMarker().getResource())) {		
			IMarker marker = breakpoint.getMarker();
			try	{
				if (marker.isSubtypeOf(MODEL_BREAKPOINT)) {
					XModelObject object =
						EclipseResourceUtil.getObjectByPath(
							processResource.getProject(),
							marker.getAttribute(ATTR_MODEL_PATH, null) 
						);
					return object;
				}
			} catch (CoreException e) {
				StrutsModelPlugin.getPluginLog().logError(e);
			}
		}
		return null;
	}

	public XModelObject[] findObjectByStatus(int breakpointStatus) {
		List<XModelObject> result = new ArrayList<XModelObject>();
		BreakpointId[] keys = statusMap.getBreakpointIds();
		for(int i=0; i<keys.length; i++) {
			String modelPath = keys[i].getModelPath();
			if((statusMap.getSummaryBreakpointStatus(modelPath) & breakpointStatus) != 0) {
				XModelObject xObject = process.getModel().getByPath(modelPath);
				if (xObject != null) result.add(xObject);
			}
		}

		return (result.size() > 0) ? result.toArray(new XModelObject[result.size()]) : null;
	}

	public String[] getMarkerTypesByStatus(XModelObject object, int status) {
	    ArrayList<String> types = new ArrayList<String>();
		if (object != null)	{
			String modelPath = object.getPath();
			BreakpointId[] brIds = statusMap.getBreakpointIds();
			for(int i=0; i<brIds.length; i++) {
			    if(modelPath.equals(brIds[i].getModelPath())&&(status == statusMap.getBreakpointStatus(brIds[i]))) {
			        types.add(brIds[i].getMarkerType());
			    }
			}
		}
	    return types.toArray(new String[types.size()]);
	}

	private static class StatusMap {

		private HashMap<String,BreakpointsStatus> map;

		public StatusMap() {
			map = new HashMap<String,BreakpointsStatus>();
		}

		public void changeBreakpointStatus(String modelPath, String markerType, int status, boolean mergeStatuses) {
			BreakpointsStatus oldStatus = (BreakpointsStatus)map.get(modelPath);
			if(oldStatus==null) {
				BreakpointsStatus newStatus = new BreakpointsStatus();
				newStatus.setStatusAsInt(markerType, status);
				map.put(modelPath, newStatus);
			} else if(markerType!=null) {
				oldStatus.setStatusAsInt(markerType, status, mergeStatuses);
			} else {
				oldStatus.setAllStatusAsInt(status, mergeStatuses);
			}
		}

		public void changeBreakpointStatus(String modelPath, String markerType, int status) {
		    changeBreakpointStatus(modelPath, markerType, status, false);
		}

		public void changeBreakpointStatus(String modelPath, int status, boolean mergeStatuses) {
		    changeBreakpointStatus(modelPath, null, status, mergeStatuses);
		}

		public void changeAllBreakpointStatus(String modelPath, int status) {
			changeBreakpointStatus(modelPath, null, status);
		}

		public void changeAllBreakpointStatus(int status, boolean mergeStatuses) {
			Iterator keys = map.keySet().iterator();
			while(keys.hasNext()) {
				String key = (String)keys.next();
			    changeAllBreakpointStatus(key, status, mergeStatuses);
			}
		}

		public void changeAllBreakpointStatus(String modelPath, int status, boolean mergeStatuses) {
			changeBreakpointStatus(modelPath, null, status, mergeStatuses);
		}

		public void changeBreakpointStatus(BreakpointId id, int status) {
			changeBreakpointStatus(id.getModelPath(), id.getMarkerType(), status);
		}

		public BreakpointId[] getBreakpointIds() {
			if(map.size()<1) {
				return new BreakpointId[0];
			}
			ArrayList<BreakpointId> ids = new ArrayList<BreakpointId>(); 
			Iterator keys = map.keySet().iterator();
			while(keys.hasNext()) {
				String key = (String)keys.next();
				BreakpointId[] bIds = ((BreakpointsStatus)map.get(key)).getBreakpointIds();
				for(int i=0; i<bIds.length; i++) {
					bIds[i].setModelPath(key);
					ids.add(bIds[i]);
				}
			}
			return ids.toArray(new BreakpointId[ids.size()]);
		}

		public int getSummaryBreakpointStatus(String modelPath) {
			BreakpointsStatus status = (BreakpointsStatus)map.get(modelPath);
			int result = (status != null) ? status.getSummaryStatus() : STATUS_NO_BREAKPOINT;

			return result;
		}

		public int getBreakpointStatus(String modelPath, String markerType) {
			BreakpointsStatus brStatus = (BreakpointsStatus)map.get(modelPath);
			if(brStatus==null) {
				return STATUS_NO_BREAKPOINT;
			}
			return brStatus.getStatus(markerType);
		}

		public int getBreakpointStatus(BreakpointId id) {
			return getBreakpointStatus(id.getModelPath(), id.getMarkerType());
		}

		public boolean containsKey(String modelPath, String markerType) {
			return map.containsKey(modelPath) && ((BreakpointsStatus)map.get(modelPath)).containsKey(markerType);
		}

		public boolean containsKey(BreakpointId id) {
			return containsKey(id.getModelPath(), id.getMarkerType());
		}

		public void clear() {
			map.clear();
		}
		
		public int getActiveBreakpointNumber(String modelPath) {
			int result = -1;
		
			BreakpointsStatus breakpointsStatus = (BreakpointsStatus)map.get(modelPath);
			if (breakpointsStatus != null) {
				String markerTypes[] = new String[] {
						"org.jboss.tools.struts.debug.actionFormPopulateMarker",
						"org.jboss.tools.struts.debug.actionFormValidateMarker",
						"org.jboss.tools.struts.debug.strutsActionEnterBreakpointMarker"				
					};
				for (int i = 0; i < markerTypes.length && result <= 0; i++) {
					if ((breakpointsStatus.getStatus(markerTypes[i]) & StrutsBreakpointManager.STATUS_BREAKPOINT_ACTIVE) != 0) {
						result = i + 1;
					}
				}
			}

			return result;
		}
	}

	private static class BreakpointId {
		private String modelPath;
		private String markerType;

		public BreakpointId(String modelPath, String markerType) {
			this.modelPath = modelPath;
			this.markerType = markerType;
		}

		public String getMarkerType() {
			return markerType;
		}

		public String getModelPath() {
			return modelPath;
		}

		public void setMarkerType(String string) {
			markerType = string;
		}

		public void setModelPath(String string) {
			modelPath = string;
		}
	}

	private static class BreakpointsStatus {

		private HashMap<String,Integer> markersStatuses;

		public BreakpointsStatus() {
			markersStatuses = new HashMap<String,Integer>();
		}

		public int getSummaryStatus() {
			int maxPriority = -1;
			Iterator keys = markersStatuses.keySet().iterator();
			while(keys.hasNext()) {
				String key = (String)keys.next();
				Integer status = (Integer)markersStatuses.get(key);
				if(maxPriority < status.intValue()) {
					maxPriority = status.intValue();
				}
			}
			return maxPriority;
		}

		public int getStatus(String markerType) {
			Integer status = (Integer)markersStatuses.get(markerType); 
			return (status != null) ? status.intValue() : 0;
		}

		public void setStatusAsInt(String markerType, int status, boolean mergeStatuses) {
			Integer oldStatus = (Integer)markersStatuses.get(markerType);
			if(oldStatus==null || oldStatus.intValue() != status) {
		        markersStatuses.put(markerType, mergeStatuses? new Integer(status&oldStatus.intValue()):new Integer(status));
//				markersStatuses.put(markerType, new Integer(status));
			}
		}

		public void setStatusAsInt(String markerType, int status) {
		    setStatusAsInt(markerType, status, false);
		}

		public void setAllStatusAsInt(int status, boolean mergeStatuses) {
			Iterator keys = markersStatuses.keySet().iterator();
			while(keys.hasNext()) {
				String key = (String)keys.next();
				int oldStatus = getStatus(key);
		        markersStatuses.put(key, mergeStatuses? new Integer(status&oldStatus):new Integer(status));
//				markersStatuses.put(key, new Integer(status));
			}
		}

		public void setAllStatusAsInt(int status) {
		    setAllStatusAsInt(status, false);
		}

		public boolean containsKey(String markerType) {
			return markersStatuses.containsKey(markerType);
		}

		public BreakpointId[] getBreakpointIds() {
			Set<String> keySet = markersStatuses.keySet();
			String[] keys = keySet.toArray(new String[keySet.size()]);
			BreakpointId[] ids = new BreakpointId[keys.length];
			for(int i=0; i<keys.length; i++) {
				ids[i] = new BreakpointId(null, keys[i]);
			}
			return ids;
		}
	}
}