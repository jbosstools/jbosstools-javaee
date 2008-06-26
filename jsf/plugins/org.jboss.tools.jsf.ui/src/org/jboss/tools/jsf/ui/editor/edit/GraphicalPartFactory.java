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

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;

import org.jboss.tools.jsf.ui.editor.model.*;

public class GraphicalPartFactory implements EditPartFactory {
	
	public EditPart createEditPart(EditPart context, Object model) {
		EditPart child = null;

		if (model instanceof ILink)
			child = new LinkEditPart();
		else if (model instanceof IPage)
			child = new PageEditPart();
		else if (model instanceof IGroup)
			child = new GroupEditPart();
		else if (model instanceof IJSFModel)
			child = new JSFDiagramEditPart();

		if (child != null)
			child.setModel(model);
		return child;
	}

}
