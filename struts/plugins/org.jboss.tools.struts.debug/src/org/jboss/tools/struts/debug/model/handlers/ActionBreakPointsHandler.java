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
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IBreakpointManager;
import org.eclipse.debug.core.model.IBreakpoint;

import org.jboss.tools.common.meta.action.XEntityData;
import org.jboss.tools.common.meta.action.impl.AbstractHandler;
import org.jboss.tools.common.meta.action.impl.XEntityDataImpl;
import org.jboss.tools.common.meta.action.impl.handlers.DefaultCreateHandler;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.struts.StrutsConstants;
import org.jboss.tools.struts.debug.internal.ActionBreakpoint;
import org.jboss.tools.struts.debug.internal.StrutsDebugModel;
import org.jboss.tools.struts.debug.internal.StrutsDebugPlugin;
import org.jboss.tools.struts.model.ReferenceObjectImpl;
import org.jboss.tools.struts.model.helpers.StrutsBreakpointManager;

public class ActionBreakPointsHandler extends AbstractHandler implements StrutsConstants {

	private static final String ATT_FILLING = "filling";
	private static final String ATT_VALIDATION = "validation";
	private static final String ATT_EXECUTE = "execute";

	private static final String ATT_TRUE_VALUE = "true";
	private static final String ATT_FALSE_VALUE = "false";
	
	XEntityData fve = null;
	XEntityData fv = null;

	public ActionBreakPointsHandler() {}

	public boolean isEnabled(XModelObject object) {
		return object != null 
				&& object.isActive() 
				&& TYPE_ACTION.equals(object.getAttributeValue(ATT_TYPE))
				&& getReference(object) != null;		
	}

	public XEntityData[] getEntityData(XModelObject object) {
		resetData(object);
		super.getEntityData(object);
		Properties eneblement = getBreakpointEnablement(object);
		data[0].setValue(ATT_FILLING, eneblement.getProperty(ATT_FILLING));
		data[0].setValue(ATT_VALIDATION, eneblement.getProperty(ATT_VALIDATION));
		data[0].setValue(ATT_EXECUTE, eneblement.getProperty(ATT_EXECUTE));
		return data;
	}
	
	void resetData(XModelObject object) {
		if(fve == null) {
			fve = data[0];
			fv = XEntityDataImpl.create(new String[][]{
			    {data[0].getModelEntity().getName(), ""}, 
			    {"filling"}, 
			    {"validation"}
			}); 
		}		
		XModelObject reference = getReference(object);
		if(reference == null) return;		
		boolean isAttForward = (reference.getAttributeValue(ATT_FORWARD).length() > 0);
		boolean isAttInclude = (reference.getAttributeValue(ATT_INCLUDE).length() > 0);
		boolean isStandard = isStandardActionType(reference);
		data[0] = (isAttForward || isAttInclude || isStandard) ? fv : fve;		
	}
	
	boolean isStandardActionType(XModelObject reference) {
		String type = reference.getAttributeValue(ATT_TYPE);
		if(type == null || type.length() == 0) return false;
		if(type.startsWith("org.apache.")) return true;
		return false;
	}

	public void executeHandler(XModelObject object, Properties p) throws Exception {
		Properties enablement = DefaultCreateHandler.extractProperties(data[0]);
		applyBreakpointEnablement(object, enablement);
	}

	private Properties getBreakpointEnablement(XModelObject object) {
		Properties enablement = new Properties();
		String modelPath = object.getPath();
		String attFilling = ATT_FALSE_VALUE;
		String attValidation = ATT_FALSE_VALUE;
		String attExecute = ATT_FALSE_VALUE;
		
		try {
			IFile file = (IFile)object.getAdapter(IFile.class);
			IMarker markers[] = file.findMarkers(StrutsBreakpointManager.MODEL_BREAKPOINT, true, IResource.DEPTH_INFINITE);
			IBreakpointManager breakpointManager = DebugPlugin.getDefault().getBreakpointManager();
			for (int i = 0; i < markers.length; i++) {
				if (modelPath.equals(markers[i].getAttribute(StrutsBreakpointManager.ATTR_MODEL_PATH)))	{
					IBreakpoint breakpoint = breakpointManager.getBreakpoint(markers[i]);
					if(breakpoint == null) continue;
					if(ActionBreakpoint.ACTION_ENTER_BREAKPOINT.equals(markers[i].getType())) {
						attExecute = ATT_TRUE_VALUE;
					} else if(ActionBreakpoint.ACTION_FORM_POPULATE_BREAKPOINT.equals(markers[i].getType())) {
						attFilling = ATT_TRUE_VALUE;
					} else if(ActionBreakpoint.ACTION_FORM_VALIDATE_BREAKPOINT.equals(markers[i].getType())) {
						attValidation = ATT_TRUE_VALUE;
					}
				}
			}
		} catch(CoreException e) {
            StrutsDebugPlugin.log(e);
		}
		enablement.setProperty(ATT_FILLING, attFilling);
		enablement.setProperty(ATT_VALIDATION, attValidation);
		enablement.setProperty(ATT_EXECUTE, attExecute);
		return enablement;
	}
	
	private XModelObject getReference(XModelObject object) {
		if(object instanceof ReferenceObjectImpl) { 
			return ((ReferenceObjectImpl)object).getReference();
		}
		return object;
	}

	private void applyBreakpointEnablement(XModelObject object, Properties enablement) {
		boolean isFillingEnabled = ATT_TRUE_VALUE.equals(enablement.getProperty(ATT_FILLING));
		boolean isValidationEnabled = ATT_TRUE_VALUE.equals(enablement.getProperty(ATT_VALIDATION));
		boolean isExecuteEnabled = ATT_TRUE_VALUE.equals(enablement.getProperty(ATT_EXECUTE));

		XModelObject reference = getReference(object);
		IFile file = (IFile)object.getAdapter(IFile.class);
		String modelPath = object.getPath();
		String actionPath = reference.getAttributeValue(StrutsConstants.ATT_PATH);
		String type = reference.getAttributeValue(StrutsConstants.ATT_TYPE);

		IBreakpoint fillingBreakpoint = null;
		IBreakpoint validationBreakpoint = null;
		IBreakpoint executeBreakpoint = null;

		try {
			IMarker markers[] = file.findMarkers(StrutsBreakpointManager.MODEL_BREAKPOINT, true, IResource.DEPTH_INFINITE);
			IBreakpointManager breakpointManager = DebugPlugin.getDefault().getBreakpointManager();
			for (int i = 0; i < markers.length; i++) {
				if (modelPath.equals(markers[i].getAttribute(StrutsBreakpointManager.ATTR_MODEL_PATH)))	{
					IBreakpoint breakpoint = breakpointManager.getBreakpoint(markers[i]);
					if(breakpoint == null) continue;
					if(ActionBreakpoint.ACTION_ENTER_BREAKPOINT.equals(markers[i].getType())) {
						executeBreakpoint = breakpoint;
					} else if(ActionBreakpoint.ACTION_FORM_POPULATE_BREAKPOINT.equals(markers[i].getType())) {
						fillingBreakpoint = breakpoint;
					} else if(ActionBreakpoint.ACTION_FORM_VALIDATE_BREAKPOINT.equals(markers[i].getType())) {
						validationBreakpoint = breakpoint;
					}
				}
			}

			IBreakpoint breakpoint = null;
			if(isFillingEnabled != (fillingBreakpoint != null)) {
				if(fillingBreakpoint == null) {
					breakpoint = StrutsDebugModel.getInstance().createActionFormPopulateBreakpoint(file, modelPath, actionPath);
				} else {
					DebugPlugin.getDefault().getBreakpointManager().removeBreakpoint(fillingBreakpoint,	true);
				}
			}
			if(isValidationEnabled != (validationBreakpoint != null)) {
				if(validationBreakpoint == null) {
					breakpoint = StrutsDebugModel.getInstance().createActionFormValidateBreakpoint(file, modelPath, actionPath);
				} else if(validationBreakpoint!=null) {
					DebugPlugin.getDefault().getBreakpointManager().removeBreakpoint(validationBreakpoint,	true);
				}
			}
			if(isExecuteEnabled != (executeBreakpoint != null)) {
				if(executeBreakpoint == null) {
					breakpoint = StrutsDebugModel.getInstance().createActionEnterBreakpoint(file, modelPath, type, actionPath);
				} else if(executeBreakpoint!=null) {
					DebugPlugin.getDefault().getBreakpointManager().removeBreakpoint(executeBreakpoint,	true);
				}
			}
		} catch(CoreException e) {
            StrutsDebugPlugin.log(e);
		}
	}
}