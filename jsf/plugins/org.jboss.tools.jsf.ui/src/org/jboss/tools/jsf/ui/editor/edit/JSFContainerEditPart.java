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
package org.jboss.tools.jsf.ui.editor.edit;

import java.util.List;

import org.eclipse.gef.AccessibleEditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.swt.accessibility.AccessibleEvent;
import org.jboss.tools.jsf.ui.editor.model.IJSFModel;

/**
 * Support for containers.
 */
abstract public class JSFContainerEditPart extends JSFEditPart {
	protected AccessibleEditPart createAccessible() {
		return new AccessibleGraphicalEditPart() {
			public void getName(AccessibleEvent event) {
				event.result = getJSFModel().toString();
			}
		};
	}

	/**
	 * Installs EditPolicies for container.
	 */
	protected void createEditPolicies() {
		super.createEditPolicies();
		installEditPolicy(EditPolicy.CONTAINER_ROLE,
				new JSFContainerEditPolicy());
		installEditPolicy(EditPolicy.LAYOUT_ROLE, new JSFXYLayoutEditPolicy());
	}

	/**
	 * Returns the model as a IJSFModel.
	 * 
	 * @return IJSFModel.
	 */
	protected IJSFModel getJSFModel() {
		return (IJSFModel) getModel();
	}

	/**
	 * Returns list of container's children.
	 * 
	 * @return List of children.
	 */
	protected List getModelChildren() {
		return getJSFModel().getGroupList().getElements();
	}

}
