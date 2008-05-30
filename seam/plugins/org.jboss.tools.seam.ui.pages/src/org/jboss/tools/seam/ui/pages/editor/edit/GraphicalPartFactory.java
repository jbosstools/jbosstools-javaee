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
package org.jboss.tools.seam.ui.pages.editor.edit;

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;
import org.jboss.tools.seam.ui.pages.editor.ecore.pages.Link;
import org.jboss.tools.seam.ui.pages.editor.ecore.pages.Page;
import org.jboss.tools.seam.ui.pages.editor.ecore.pages.PagesModel;
import org.jboss.tools.seam.ui.pages.editor.ecore.pages.Param;
import org.jboss.tools.seam.ui.pages.editor.ecore.pages.PgException;


public class GraphicalPartFactory implements EditPartFactory {
	
	public EditPart createEditPart(EditPart context, Object model) {
		EditPart child = null;

		//System.out.println("createEditPart model - "+model);
		if (model instanceof PagesModel)
			child = new PagesDiagramEditPart();
		else if (model instanceof Page)
			child = new PageEditPart();
		else if (model instanceof PgException)
			child = new ExceptionEditPart();
		else if (model instanceof PageWrapper)
			child = new ParamListEditPart();
		else if (model instanceof Param)
			child = new ParamEditPart();
		else if (model instanceof Link)
			child = new LinkEditPart();

		if (child != null)
			child.setModel(model);
		
		//System.out.println("editPart - "+child);
		return child;
	}

}
