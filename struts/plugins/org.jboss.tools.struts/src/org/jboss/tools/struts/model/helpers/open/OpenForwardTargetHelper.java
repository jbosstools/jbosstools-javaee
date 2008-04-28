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
package org.jboss.tools.struts.model.helpers.open;

import org.eclipse.core.resources.IFile;

import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.common.model.util.EclipseResourceUtil;
import org.jboss.tools.struts.messages.StrutsUIMessages;
import org.jboss.tools.struts.model.handlers.JumpByForwardPathHandler;

public class OpenForwardTargetHelper {
	public String run(IFile file, String objectPath, String targetPath) {
		if(file == null) return null;
		XModelObject f = EclipseResourceUtil.getObjectByResource(file);
		if(f == null) return null; //
		XModelObject context = f.getChildByPath(objectPath);
		if(context == null) return StrutsUIMessages.CANNOT_FIND_OBJECT + objectPath;
		XModelObject target = JumpByForwardPathHandler.findWithContext(targetPath, context);
		if(target == null) return StrutsUIMessages.CANNOT_FIND_RESOURCE + targetPath;
		JumpByForwardPathHandler.doOpenTarget(target);
		return null;
	}

}
