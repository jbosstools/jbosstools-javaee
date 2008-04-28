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
package org.jboss.tools.struts.ui.editor.edit;

import java.util.List;

import org.eclipse.gef.AccessibleEditPart;
import org.eclipse.gef.EditPolicy;
import org.eclipse.swt.accessibility.AccessibleEvent;
import org.jboss.tools.struts.ui.editor.model.IStrutsModel;

/**
 * 
 */
abstract public class StrutsContainerEditPart extends StrutsEditPart {
	protected AccessibleEditPart createAccessible() {
		return new AccessibleGraphicalEditPart() {
			public void getName(AccessibleEvent event) {
				event.result = getStrutsModel().toString();
			}
		};
	}

	/**
	 * 
	 */
	protected void createEditPolicies() {
		super.createEditPolicies();
		installEditPolicy(EditPolicy.CONTAINER_ROLE,
				new StrutsContainerEditPolicy());
		installEditPolicy(EditPolicy.LAYOUT_ROLE,
				new StrutsXYLayoutEditPolicy());
	}

	/**
	 * 
	 * @return IStrutsModel.
	 */
	protected IStrutsModel getStrutsModel() {
		return (IStrutsModel) getModel();
	}

	/**
	 * 
	 * @return List of children.
	 */
	protected List getModelChildren() {
		return getStrutsModel().getProcessItemList().getElements();
	}

}
