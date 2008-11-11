/*******************************************************************************
 * Copyright (c) 2008 Exadel, Inc. and Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Exadel, Inc. and Red Hat, Inc. - initial API and implementation
 ******************************************************************************/ 
package org.jboss.tools.seam.ui.pages.editor.actions;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.internal.WorkbenchImages;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.RetargetAction;

/**
 * @author Eric Bordeau
 */
public class PagesCutRetargetAction extends RetargetAction {

	/**
	 * Constructs a new CopyRetargetAction with the default ID, label and image.
	 */
	public PagesCutRetargetAction() {
		super(ActionFactory.CUT.getId(), "&Copy"/* GEFMessages.CopyAction_Label */);
		setImageDescriptor(getImageDescriptor(ISharedImages.IMG_TOOL_CUT));
		setHoverImageDescriptor(getImageDescriptor(ISharedImages.IMG_TOOL_CUT));
		setDisabledImageDescriptor(getImageDescriptor(ISharedImages.IMG_TOOL_CUT_DISABLED));
	}

	private ImageDescriptor getImageDescriptor(String symbolicName) {
		return WorkbenchImages.getImageDescriptor(symbolicName);
	}

}
