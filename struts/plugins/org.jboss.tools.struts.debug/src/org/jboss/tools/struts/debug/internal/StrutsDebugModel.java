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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.model.IBreakpoint;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IStackFrame;
import org.eclipse.jdt.debug.core.IJavaStackFrame;
import org.eclipse.jdt.debug.core.IJavaThread;

import org.jboss.tools.common.model.XModel;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.project.IModelNature;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.struts.StrutsProject;
import org.jboss.tools.struts.debug.internal.step.ActionEnterCheckElement;
import org.jboss.tools.struts.debug.internal.step.ICheckElement;
import org.jboss.tools.struts.model.helpers.StrutsBreakpointManager;
import org.jboss.tools.struts.model.helpers.StrutsProcessHelper;
import org.jboss.tools.struts.model.helpers.StrutsProcessStructureHelper;
import org.jboss.tools.jst.web.debug.IBreakpointPresentation;
import org.jboss.tools.jst.web.launching.sourcelookup.IBreakpointSourceFinder;

public class StrutsDebugModel {

	private static StrutsDebugModel instance = null;
	private ICheckElement[] checkElementCache = null;
	
	private StrutsDebugModel() {
		BreakpointsListener listener = new BreakpointsListener();
		DebugPlugin.getDefault().addDebugEventListener(listener);
	}

	public static StrutsDebugModel getInstance() {
		if (instance == null) instance = new StrutsDebugModel();
		return instance;
	}

	public static void init() {
		getInstance();
	}

	public ActionEnterBreakpoint createActionEnterBreakpoint(IResource resource, String modelPath, String actionClassName, String actionMappingPath) throws CoreException {
		Map attributes = new HashMap(10);
		attributes.put(ActionBreakpoint.ATTR_MODEL_PATH, modelPath);
		attributes.put(ActionBreakpoint.ATTR_ACTION_TYPE_NAME, actionClassName);
		attributes.put(ActionBreakpoint.ATTR_ACTION_MAPPING_PATH, actionMappingPath);

		return new ActionEnterBreakpoint(resource, actionClassName, attributes);
	}

	public ActionForwardBreakpoint createActionForwardBreakpoint(IResource resource, String modelPath, String actionClassName, String actionMappingPath, String forwardName) throws CoreException {
		Map attributes = new HashMap(10);

		attributes.put(ActionBreakpoint.ATTR_MODEL_PATH, modelPath);
		attributes.put(ActionBreakpoint.ATTR_ACTION_TYPE_NAME, actionClassName);
		attributes.put(ActionBreakpoint.ATTR_ACTION_MAPPING_PATH, actionMappingPath);
		attributes.put(ActionBreakpoint.ATTR_FORWARD_NAME, forwardName);

		return new ActionForwardBreakpoint(resource, attributes);
	}

	public ActionTilesDefinitionForwardBreakpoint createActionTilesDefinitionForwardBreakpoint(IResource resource, String modelPath, String actionClassName, String actionMappingPath, String forwardName) throws CoreException {
		Map attributes = new HashMap(10);

		attributes.put(ActionBreakpoint.ATTR_MODEL_PATH, modelPath);
		attributes.put(ActionBreakpoint.ATTR_ACTION_TYPE_NAME, actionClassName);
		attributes.put(ActionBreakpoint.ATTR_ACTION_MAPPING_PATH, actionMappingPath);
		attributes.put(ActionBreakpoint.ATTR_FORWARD_NAME, forwardName);

		return new ActionTilesDefinitionForwardBreakpoint(resource, attributes);
	}

	public ActionAttrForwardBreakpoint createActionAttrForwardBreakpoint(IResource resource, String modelPath, String actionMappingPath, String forwardName) throws CoreException {
		Map attributes = new HashMap(10);

		attributes.put(ActionBreakpoint.ATTR_MODEL_PATH, modelPath);
		attributes.put(ActionBreakpoint.ATTR_ACTION_MAPPING_PATH, actionMappingPath);
		attributes.put(ActionBreakpoint.ATTR_FORWARD_NAME, forwardName);

		return new ActionAttrForwardBreakpoint(resource, attributes);
	}

	public ActionIncludeBreakpoint createActionIncludeBreakpoint(IResource resource, String modelPath, String actionMappingPath, String includeName) throws CoreException {
		Map attributes = new HashMap(10);

		attributes.put(ActionBreakpoint.ATTR_MODEL_PATH, modelPath);
		attributes.put(ActionBreakpoint.ATTR_ACTION_MAPPING_PATH, actionMappingPath);
		attributes.put(ActionBreakpoint.ATTR_INCLUDE_NAME, includeName);

		return new ActionIncludeBreakpoint(resource, attributes);
	}

	public ActionFormPopulateBreakpoint createActionFormPopulateBreakpoint(IResource resource, String modelPath, String actionMappingPath) throws CoreException {
		Map attributes = new HashMap(10);

		attributes.put(ActionBreakpoint.ATTR_MODEL_PATH, modelPath);
		attributes.put(ActionBreakpoint.ATTR_ACTION_MAPPING_PATH, actionMappingPath);

		return new ActionFormPopulateBreakpoint(resource, attributes);
	}

	public ActionFormValidateBreakpoint createActionFormValidateBreakpoint(IResource resource, String modelPath, String actionMappingPath) throws CoreException	{
		Map attributes = new HashMap(10);

		attributes.put(ActionBreakpoint.ATTR_MODEL_PATH, modelPath);
		attributes.put(ActionBreakpoint.ATTR_ACTION_MAPPING_PATH, actionMappingPath);

		return new ActionFormValidateBreakpoint(resource, attributes);
	}

	public ActionExceptionBreakpoint createActionExceptionBreakpoint(IResource resource, String modelPath, String actionClassName, String actionMappingPath, String exceptionClassName) throws CoreException {

		Map attributes = new HashMap(10);
		attributes.put(ActionBreakpoint.ATTR_MODEL_PATH, modelPath);
		attributes.put(ActionBreakpoint.ATTR_ACTION_TYPE_NAME, actionClassName);
		attributes.put(ActionBreakpoint.ATTR_ACTION_MAPPING_PATH, actionMappingPath);
		attributes.put(ActionBreakpoint.ATTR_EXCEPTION_TYPE_NAME, exceptionClassName);

		return new ActionExceptionBreakpoint(resource, attributes);
	}

	public GlobalForwardBreakpoint createGlobalForwardBreakpoint(IResource resource, String modelPath, String forwardName) throws CoreException {
		Map attributes = new HashMap(10);
		attributes.put(ActionBreakpoint.ATTR_MODEL_PATH, modelPath);
		attributes.put(ActionBreakpoint.ATTR_FORWARD_NAME, forwardName);

		return new GlobalForwardBreakpoint(resource, attributes);
	}

	public TilesDefinitionGlobalForwardBreakpoint createTilesDefinitionGlobalForwardBreakpoint(IResource resource, String modelPath, String forwardName) throws CoreException {
		Map attributes = new HashMap(10);
		attributes.put(ActionBreakpoint.ATTR_MODEL_PATH, modelPath);
		attributes.put(ActionBreakpoint.ATTR_FORWARD_NAME, forwardName);

		return new TilesDefinitionGlobalForwardBreakpoint(resource, attributes);
	}

	public GlobalExceptionBreakpoint createGlobalExceptionBreakpoint(IResource resource, String modelPath, String exceptionClassName) throws CoreException {
		Map attributes = new HashMap(10);
		attributes.put(ActionBreakpoint.ATTR_MODEL_PATH, modelPath);
		attributes.put(ActionBreakpoint.ATTR_EXCEPTION_TYPE_NAME, exceptionClassName);

		return new GlobalExceptionBreakpoint(resource, attributes);
	}

	public PageEnterBreakpoint createPageEnterBreakpoint(IResource resource, String modelPath, String pageName) throws CoreException {
		Map attributes = new HashMap(10);
		attributes.put(ActionBreakpoint.ATTR_MODEL_PATH, modelPath);

		return new PageEnterBreakpoint(resource, pageName, attributes);
	}

	private class BreakpointsListener implements IDebugEventSetListener {
		public void handleDebugEvents(DebugEvent[] events) {
			for (int i = 0; i < events.length; i++) {
				if (events[i].getSource() instanceof IProcess) {
					switch (events[i].getKind()) {
						case DebugEvent.CREATE:
							IBreakpoint[] breakpoints = DebugPlugin.getDefault().getBreakpointManager().getBreakpoints();
							for(int j=0; j<breakpoints.length; j++) {
							    if(breakpoints[j] instanceof IBreakpointPresentation) {
							        ((IBreakpointPresentation)breakpoints[j]).setStartServerStatus(true);
							    }
							}
//							clearCheckElements();
							break;
						case DebugEvent.TERMINATE:
						    handleTerminateProcess();
							break;
					}
				} else if ((events[i].getSource() instanceof IJavaThread)) {
					switch (events[i].getKind()) {
						case DebugEvent.SUSPEND:
							handleSuspendEvent(events[i]);
							break;
						case DebugEvent.RESUME:
							handleResumeEvent(events[i]);
							break;
						case DebugEvent.CREATE:
							handleResumeEvent(events[i]);
							break;
					}
				}
			}
		}

		private void handleTerminateProcess() {
			XModelObject[] strutsProcesses = getAllStrutsProcesses();
			for (int j = 0; j < strutsProcesses.length; j++) {
			    StrutsBreakpointManager manager = StrutsProcessStructureHelper.instance.getBreakpointManager(strutsProcesses[j]);
			    int newStatus = StrutsBreakpointManager.STATUS_BREAKPOINT_DISABLED + StrutsBreakpointManager.STATUS_BREAKPOINT_ENABLED + StrutsBreakpointManager.STATUS_NO_BREAKPOINT;
			    manager.fireAllBreakpointStatusChanged(newStatus, true);
			}
		}

		private void handleSuspendEvent(DebugEvent event) {
			switch (event.getDetail()) {
				case DebugEvent.BREAKPOINT:
					breakpointActivated(event);
					break;
			}
		}

		private void handleResumeEvent(DebugEvent event) {
			executionResumed(event);
		}

		private void breakpointActivated(DebugEvent event) {
			IJavaThread thread = (IJavaThread)event.getSource();
			IBreakpoint currentBreakpoint = getCurrentBreakpoint(thread);
			if (currentBreakpoint != null) {
				XModel xModel = getXModel(currentBreakpoint);
				String xObjectPath = getXObjectPath(currentBreakpoint);
				if (xModel != null && xObjectPath != null) {
					XModelObject xObject = xModel.getByPath(xObjectPath);
					XModelObject process = StrutsProcessStructureHelper.instance.getProcess(xObject);
					StrutsBreakpointManager manager = StrutsProcessHelper.getHelper(process).getBreakpointManager();
					try {
						manager.fireBreakpointStatusChanged(
							xObject,
							currentBreakpoint.getMarker().getType(),
							manager.getBreakpointStatus(xObject, currentBreakpoint.getMarker().getType()) | StrutsBreakpointManager.STATUS_BREAKPOINT_ACTIVE
						);
					} catch (CoreException e) {
			            StrutsDebugPlugin.log(e);
					}
				}
			}
		}

		private void executionResumed(DebugEvent event) {
			if(event.isEvaluation()) {
				return;
			}
			XModelObject[] strutsProcesses = getAllStrutsProcesses();
			for (int j = 0; j < strutsProcesses.length; j++) {
				StrutsBreakpointManager manager = StrutsProcessStructureHelper.instance.getBreakpointManager(strutsProcesses[j]);
				XModelObject activeBreakpoints[] = manager.findObjectByStatus(StrutsBreakpointManager.STATUS_BREAKPOINT_ACTIVE);
				if (activeBreakpoints != null) {
					for (int k = 0; k < activeBreakpoints.length; k++) {
//						manager.fireAllBreakpointStatusChanged(
//							activeBreakpoints[k],
//							manager.getBreakpointStatus(activeBreakpoints[k]) & ~StrutsBreakpointManager.STATUS_BREAKPOINT_ACTIVE
//						);
					    String[] markerTypesEnabled = manager.getMarkerTypesByStatus(activeBreakpoints[k], StrutsBreakpointManager.STATUS_BREAKPOINT_ACTIVE + StrutsBreakpointManager.STATUS_BREAKPOINT_ENABLED);
					    String[] markerTypesDisabled = manager.getMarkerTypesByStatus(activeBreakpoints[k], StrutsBreakpointManager.STATUS_BREAKPOINT_ACTIVE + StrutsBreakpointManager.STATUS_BREAKPOINT_DISABLED);
					    String[] markerTypes = new String[markerTypesEnabled.length + markerTypesDisabled.length];
					    for(int i=0; i<markerTypes.length; i++) {
					        if(i<markerTypesEnabled.length) {
					            markerTypes[i] = markerTypesEnabled[i];
					        } else {
					            markerTypes[i] = markerTypesDisabled[i - markerTypesEnabled.length];
					        }
					    }
					    for(int i=0; i<markerTypes.length; i++) {
					        manager.fireBreakpointStatusChanged(
					            activeBreakpoints[k],
					            markerTypes[i],
					            manager.getBreakpointStatus(activeBreakpoints[k], markerTypes[i]) & ~StrutsBreakpointManager.STATUS_BREAKPOINT_ACTIVE
					        );
					    }
					}
					return;
				}
			}
		}

		private XModel getXModel(IBreakpoint breakpoint) {
			IModelNature modelNature =
				EclipseResourceUtil.getModelNature(
					breakpoint.getMarker().getResource().getProject(),
					StrutsProject.NATURE_ID
				);
			return (modelNature != null) ? modelNature.getModel() : null;
		}

		private String getXObjectPath(IBreakpoint breakpoint) {
			String modelPath = null;
			try	{
				modelPath = (String)breakpoint.getMarker().getAttribute(ActionBreakpoint.ATTR_MODEL_PATH);
			} catch (CoreException e) {
	            StrutsDebugPlugin.log(e);
			}
			return modelPath;
		}

		private IBreakpoint getCurrentBreakpoint(IJavaThread thread) {
			IBreakpoint result = null;

			IBreakpoint breakpoints[] = thread.getBreakpoints();
			for(int i = 0; i < breakpoints.length; i++) {
				if(breakpoints[i] instanceof IBreakpointSourceFinder) {
					if(breakpoints[i] instanceof ActionConditionBreakpoint) {
						ActionConditionBreakpoint actionConditionBreakpoint = (ActionConditionBreakpoint)breakpoints[i];
						ActionConditionBreakpoint breakpoint =  actionConditionBreakpoint.getActionConditionBreakpointManager().getCurentBreakpoint();
						if(actionConditionBreakpoint == breakpoint) {
							result = breakpoint;
							break;
						}
						continue;
					}
					result = breakpoints[i];
					break;
				}
			}
			return result;
		}
	}

	private XModelObject[] getAllStrutsProcesses() {
		List result = new ArrayList();
		IProject projects[] = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		for (int i = 0; i < projects.length; i++) {
			IModelNature modelNature = EclipseResourceUtil.getModelNature(projects[i], StrutsProject.NATURE_ID);
			if (modelNature != null) {
				XModel xModel = modelNature.getModel();
				XModelObject[] strutsProcesses = getStrutsProcesses(xModel);
				for (int j = 0; j < strutsProcesses.length; j++) {
				    result.add(strutsProcesses[j]);
				}
			}
		}
		return (XModelObject[])result.toArray(new XModelObject[result.size()]);
	}

	private XModelObject[] getStrutsProcesses(XModel xModel) {
		XModelObject result[] = new XModelObject[0];
		XModelObject xFolder = xModel.getByPath("FileSystems/WEB-INF");
		if (xFolder != null) {
			XModelObject xFiles10[] = xFolder.getChildren("StrutsConfig10");
			XModelObject xFiles11[] = xFolder.getChildren("StrutsConfig11");
			XModelObject xFiles12[] = xFolder.getChildren("StrutsConfig12");
			int arraySize = ((xFiles10 != null) ? xFiles10.length : 0) + ((xFiles11 != null) ? xFiles11.length : 0) + ((xFiles12 != null) ? xFiles12.length : 0);
			if (arraySize > 0) {
				result = new XModelObject[arraySize];
				int n = 0;
				if (xFiles10 != null) {
					for (int i = 0; i < xFiles10.length; i++, n++) {
					    XModelObject[] xmo = xFiles10[i].getChildren("StrutsProcess");
					    if(xmo.length>0) {
							result[n] = xFiles10[i].getChildren("StrutsProcess")[0];					        
					    }
					}
				}
				if (xFiles11 != null) {
					for (int i = 0; i < xFiles11.length; i++, n++) {
					    XModelObject[] xmo = xFiles11[i].getChildren("StrutsProcess");
					    if(xmo.length>0) {
							result[n] = xFiles11[i].getChildren("StrutsProcess")[0];					        
					    }
					}
				}
				if (xFiles12 != null) {
					for (int i = 0; i < xFiles12.length; i++, n++) {
					    XModelObject[] xmo = xFiles12[i].getChildren("StrutsProcess");
					    if(xmo.length>0) {
							result[n] = xFiles12[i].getChildren("StrutsProcess")[0];					        
					    }
					}
				}
			}
		}

		return result;
	}

	public void createHidenBreakpoints(IJavaThread thread) throws CoreException {
//		IBreakpoint breakpoint = getCurrentBreakpoint(thread);
//		if(breakpoint instanceof ActionBreakpoint) {
//			if(((ActionBreakpoint)breakpoint).isHiden()) {
//				DebugPlugin.getDefault().getBreakpointManager().removeBreakpoint(breakpoint, true);
//			}
			// TODO create hiden breakpoints.
//		}
	}

	public boolean canStop(IStackFrame[] stackFrames) {
		boolean result = false;

		return result;
	}

	private ICheckElement[] getCheckElements() {
/*
		if (checkElementCache == null) {
			createCheckElements();
		} 
		return checkElementCache;
*/
		return new ActionEnterCheckElement[0];
	}
/*
	private void createCheckElements() {

		List result = new ArrayList();

		XModelObject[] processes = getAllStrutsProcesses();
		for (int i = 0; i < processes.length; i++) {
			XModelObject[] kids = processes[i].getChildren(StrutsConstants.ENT_PROCESSITEM);
			for (int j = 0; j < kids.length; j++) {
				if (StrutsConstants.TYPE_ACTION.equals(kids[j].getAttributeValue("type"))) {
					result.addAll(createActionCheckElements(kids[j]));
				} else if(StrutsConstants.TYPE_FORWARD.equals(kids[j].getAttributeValue("type"))) {
				} else if(StrutsConstants.TYPE_EXCEPTION.equals(kids[j].getAttributeValue("type"))) {
				} else if(StrutsConstants.TYPE_PAGE.equals(kids[j].getAttributeValue("type"))) {
				}
			} 					
		}
		checkElementCache = (ICheckElement[])result.toArray(new ICheckElement[result.size()]);
	}

	private List createActionCheckElements(XModelObject xActionObject) {
		List result = new ArrayList();

		XModelObject xObject = StrutsProcessStructureHelper.instance.getReference(xActionObject);
		String typeName = xObject.getAttributeValue(StrutsConstants.ATT_TYPE);
		result.add(new ActionEnterCheckElement(xObject));

		XModelObject[] kids = xActionObject.getChildren(StrutsConstants.ENT_PROCESSITEMOUT);
		for (int i = 0; i < kids.length; i++) {
			xObject = StrutsProcessStructureHelper.instance.getReference(kids[i]);
			String xEntityName = xObject.getModelEntity().getName();
			if (xEntityName.startsWith(StrutsConstants.ENT_EXCEPTION)) {
				result.add(new ActionExceptionCheckElement(xObject, typeName));
			} else if (xEntityName.startsWith(StrutsConstants.ENT_FORWARD))	{
				result.add(new ActionForwardCheckElement(xObject, typeName));
			}
		} 					
		return result;
	}

	private void clearCheckElements() {
		checkElementCache = null;
	}
*/
	public static IJavaStackFrame findFrameByMethodName(List frames, String methodName) throws DebugException {
		return findFrameByMethodName((IStackFrame[])frames.toArray(new IStackFrame[frames.size()]), methodName);
	}

	public static IJavaStackFrame findFrameByMethodName(IStackFrame[] frames, String methodName) throws DebugException {
		IJavaStackFrame result = null;

		for (int i = 0; i < frames.length && result == null; i++) {
			if (frames[i] instanceof IJavaStackFrame) {
				IJavaStackFrame javaStackFrame = (IJavaStackFrame)frames[i];
				if (methodName.equals(javaStackFrame.getMethodName())) {
					result = javaStackFrame;
				}
			}
		}

		return result;
	}
}