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
import org.eclipse.debug.core.model.IBreakpoint;

import org.jboss.tools.common.meta.action.impl.AbstractHandler;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.struts.StrutsConstants;
import org.jboss.tools.struts.debug.internal.ActionBreakpoint;
import org.jboss.tools.struts.debug.internal.StrutsDebugModel;
import org.jboss.tools.struts.debug.internal.StrutsDebugPlugin;
import org.jboss.tools.struts.model.ReferenceObjectImpl;
import org.jboss.tools.struts.model.handlers.page.create.CreatePageContext;
import org.jboss.tools.struts.model.helpers.StrutsProcessStructureHelper;

public class AddBreakPointHandler extends AbstractHandler implements StrutsConstants {

	public boolean isEnabled(XModelObject object) {
		if (object == null || !object.isActive()) return false;
		if (object instanceof ReferenceObjectImpl) {
			String type = object.getAttributeValue(ATT_TYPE);
			if(TYPE_ACTION.equals(type)) {
				XModelObject reference = ((ReferenceObjectImpl)object).getReference();
				if(reference == null) return false;
				if(reference.getAttributeValue(ATT_TYPE).length() == 0 && 
				   reference.getAttributeValue(ATT_NAME).length() == 0) return false;
			} else if(TYPE_PAGE.equals(type)) {
				XModelObject page = StrutsProcessStructureHelper.instance.getPhysicalPage(object);
				if(page == null) return false;			
			}
		}
		return !hasBreakpointMarker(object);
	}	

	protected boolean isForwardToTile(XModelObject object) {
		String type = object.getAttributeValue(StrutsConstants.ATT_TYPE);
		if(!StrutsConstants.TYPE_FORWARD.equals(type)) return false;
		String targetPath = object.getAttributeValue(StrutsConstants.ATT_TARGET);
		if(targetPath == null) return false;
		XModelObject target = StrutsProcessStructureHelper.instance.getParentFile(object).getChildByPath("process").getChildByPath(targetPath);
		if(target == null) return false;
		type = target.getAttributeValue(StrutsConstants.ATT_TYPE);
		if(!StrutsConstants.TYPE_PAGE.equals(type)) return false;
		String path = target.getAttributeValue(StrutsConstants.ATT_PATH);
		if(new CreatePageContext().isPage(path)) return false;
		return true;		
	}

	public void executeHandler(XModelObject object, Properties p) throws Exception {
		if (!isEnabled(object)) return;

		XModelObject reference = object;
		if (object instanceof ReferenceObjectImpl && ((ReferenceObjectImpl)object).getReference() != null) 
			reference = ((ReferenceObjectImpl)object).getReference();

		IFile file = (IFile)object.getAdapter(IFile.class);
		String modelPath = object.getPath();
		String entity = reference.getModelEntity().getName();

		boolean isForward = (entity.startsWith(ENT_FORWARD));
		boolean isException = (entity.startsWith(ENT_EXCEPTION));
		boolean isAttrForward = entity.startsWith(ENT_ACTION) &&
		            reference.getAttributeValue(ATT_FORWARD).length() > 0;
		boolean isAttrInclude = entity.startsWith(ENT_ACTION) &&
					reference.getAttributeValue(ATT_INCLUDE).length() > 0;
		boolean isTilesDefinitionForward = isForward && isForwardToTile(object);

		XModelObject actionObject = (isForward || isException) ? reference.getParent() : reference;
		String actionClassName = actionObject.getAttributeValue(ATT_TYPE);
		String actionPath = actionObject.getAttributeValue(ATT_PATH);

		IBreakpoint breakpoint = null;

		if (isTilesDefinitionForward) {
		    String forwardName = reference.getAttributeValue(ATT_NAME);
		    breakpoint = StrutsDebugModel.getInstance().createActionTilesDefinitionForwardBreakpoint(file, modelPath, actionClassName, actionPath, forwardName);
		} else if (isAttrForward) {
			String forwardName = reference.getAttributeValue(ATT_FORWARD);
			breakpoint = StrutsDebugModel.getInstance().createActionAttrForwardBreakpoint(file, modelPath, actionPath, forwardName);
		} else if(isAttrInclude) {
			String includeName = reference.getAttributeValue(ATT_INCLUDE);
			breakpoint = StrutsDebugModel.getInstance().createActionIncludeBreakpoint(file, modelPath, actionPath, includeName);
		} else if(isForward) {
			String forwardName = reference.getAttributeValue(ATT_NAME);
            breakpoint = StrutsDebugModel.getInstance().createActionForwardBreakpoint(file, modelPath, actionClassName, actionPath, forwardName);
		} else if (isException)
			breakpoint = StrutsDebugModel.getInstance().createActionExceptionBreakpoint(
				file, 
				modelPath, 
				actionClassName,
				actionPath, 
				reference.getAttributeValue(ATT_TYPE)
			);

		if (breakpoint != null)
			DebugPlugin.getDefault().getBreakpointManager().addBreakpoint(breakpoint);
	}

	boolean hasBreakpointMarker(XModelObject object) {
		boolean result = false;
		IFile file = (IFile)object.getAdapter(IFile.class);
		String modelPath = object.getPath();
		String markerIds[] = getMarkerIdByXModelObject(object);
		try {
			for (int i = 0; i < markerIds.length && !result; i++) { 
				IMarker markers[] = file.findMarkers(markerIds[i], true, IResource.DEPTH_INFINITE);
				for (int j = 0; j < markers.length && !result; j++)
					result = checkMarker(markers[j], modelPath);
			}
		} catch (CoreException e) {
            StrutsDebugPlugin.log(e);
		}
		return result;	
	}
	
	boolean checkMarker(IMarker marker, String modelPath) throws CoreException {
		boolean result = false;
		if (modelPath != null) {
			result = modelPath.equals(marker.getAttribute(ActionBreakpoint.ATTR_MODEL_PATH));
			if (result && DebugPlugin.getDefault().getBreakpointManager().getBreakpoint(marker) == null) {
				 marker.delete();
				 result = false;
			}
		}
		return result;
	}

	String[] getMarkerIdByXModelObject(XModelObject object) {
		XModelObject reference = object;
		if (object instanceof ReferenceObjectImpl && ((ReferenceObjectImpl)object).getReference() != null) 
			reference = ((ReferenceObjectImpl)object).getReference();
		String result[] = new String[0];

		String entityName = reference.getModelEntity().getName();
		String parentEntityName = reference.getParent().getModelEntity().getName(); 
		if (entityName.startsWith(ENT_ACTION)) {
			if(object.getModelEntity().getName().equals(ENT_PROCESSITEMOUT)) {
				result = new String[] {
					ActionBreakpoint.ACTION_ATTR_FORWARD_BREAKPOINT,
					ActionBreakpoint.ACTION_INCLUDE_BREAKPOINT
				};
			} else {
				result = new String[] {
					ActionBreakpoint.ACTION_ENTER_BREAKPOINT,
					ActionBreakpoint.ACTION_FORM_POPULATE_BREAKPOINT,
					ActionBreakpoint.ACTION_FORM_VALIDATE_BREAKPOINT 
				};
			}
		} else if (entityName.startsWith(ENT_FORWARD)) {
			if (parentEntityName.startsWith(ENT_ACTION))
				result = new String[] {
					ActionBreakpoint.ACTION_FORWARD_BREAKPOINT,
					ActionBreakpoint.ACTION_TILES_DIFINITION_FORWARD_BREAKPOINT
				};
			else
				result = new String[] {
					ActionBreakpoint.GLOBAL_FORWARD_BREAKPOINT,
					ActionBreakpoint.ACTION_TILES_DIFINITION_FORWARD_BREAKPOINT
				};
		} else if (entityName.startsWith(ENT_EXCEPTION)) {
			if (parentEntityName.startsWith(ENT_ACTION))
				result = new String[] {ActionBreakpoint.ACTION_EXCEPTION_BREAKPOINT};
			else
				result = new String[] {ActionBreakpoint.GLOBAL_EXCEPTION_BREAKPOINT};
		} else if (entityName.startsWith(ENT_PROCESSITEM)) {
			String type = reference.getAttributeValue(ATT_TYPE);
			if (TYPE_PAGE.equals(type))
				result = new String[] {ActionBreakpoint.PAGE_ENTER_BREAKPOINT};
		}
		
		return result;
	}
}