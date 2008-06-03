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
package org.jboss.tools.struts.ui.editor.actions;

import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.internal.WorkbenchImages;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.RetargetAction;

import org.eclipse.gef.internal.GEFMessages;

/**
 * @author Eric Bordeau
 */
public class StrutsCutRetargetAction extends RetargetAction {

/**
 * Constructs a new CopyRetargetAction with the default ID, label and image.
 */
public StrutsCutRetargetAction() {
	super(ActionFactory.CUT.getId(), GEFMessages.CopyAction_Label);
	setImageDescriptor(WorkbenchImages.getImageDescriptor(
								ISharedImages.IMG_TOOL_CUT));
	setHoverImageDescriptor(WorkbenchImages.getImageDescriptor(
								ISharedImages.IMG_TOOL_CUT));
	setDisabledImageDescriptor(WorkbenchImages.getImageDescriptor(
								ISharedImages.IMG_TOOL_CUT_DISABLED));
	//setAccelerator(convertAccelerator("CTRL+X"));
}								

}
