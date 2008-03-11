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
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.model.IBreakpoint;

import org.jboss.tools.common.meta.action.XActionInvoker;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.struts.StrutsConstants;
import org.jboss.tools.struts.debug.internal.StrutsDebugModel;
import org.jboss.tools.struts.model.ReferenceObjectImpl;
import org.jboss.tools.struts.model.helpers.StrutsProcessStructureHelper;

public class AddBreakPointToGlobalHandler extends AddBreakPointHandler {
	
	public void executeHandler(XModelObject object, Properties p) throws Exception {
		if (!isEnabled(object)) return;
		String type = object.getAttributeValue(StrutsConstants.ATT_TYPE);
		boolean isGlobalForward = StrutsConstants.TYPE_FORWARD.equals(type);
		boolean isGlobalException = StrutsConstants.TYPE_EXCEPTION.equals(type);
		boolean isAction = StrutsConstants.TYPE_ACTION.equals(type);
		boolean isPage = StrutsConstants.TYPE_PAGE.equals(type);
		object.getAttributeValue(StrutsConstants.ATT_PATH);
		boolean isTilesDefinition = isForwardToTile(object);

		XModelObject reference = object;
		if(object instanceof ReferenceObjectImpl && ((ReferenceObjectImpl)object).getReference() != null) { 
			reference = ((ReferenceObjectImpl)object).getReference();
		}

		IFile file = (IFile)reference.getAdapter(IFile.class);
		String modelPath = object.getPath();

		reference.getAttributeValue(StrutsConstants.ATT_PATH);

		IBreakpoint breakpoint = null;		
		if (isTilesDefinition) {
			breakpoint = StrutsDebugModel.getInstance().createTilesDefinitionGlobalForwardBreakpoint(
				file,
				modelPath,
				reference.getAttributeValue(StrutsConstants.ATT_NAME)
			);
		} else if (isGlobalForward) {
			breakpoint = StrutsDebugModel.getInstance().createGlobalForwardBreakpoint(
				file,
				modelPath,
				reference.getAttributeValue(StrutsConstants.ATT_NAME)
			);
		} else if (isGlobalException) { 
			breakpoint = StrutsDebugModel.getInstance().createGlobalExceptionBreakpoint(
				file,
				modelPath,
				reference.getAttributeValue(StrutsConstants.ATT_TYPE)
			);
		} else if (isAction) {
			XActionInvoker.invoke("StrutsProcessItem_BreakpointActions", "Properties.BreakpointProperties", object, p);
			return;
		} else if (isPage) {
			String pagePath = StrutsProcessStructureHelper.instance.getContextRelativePath(object);
			breakpoint = StrutsDebugModel.getInstance().createPageEnterBreakpoint(
				file, modelPath, pagePath
			);
		}

		if (breakpoint != null)	{
			DebugPlugin.getDefault().getBreakpointManager().addBreakpoint(breakpoint);
		}
	}

}