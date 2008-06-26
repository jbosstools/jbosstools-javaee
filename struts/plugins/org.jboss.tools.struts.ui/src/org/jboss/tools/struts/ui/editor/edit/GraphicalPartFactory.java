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

import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartFactory;

import org.jboss.tools.struts.StrutsConstants;
import org.jboss.tools.struts.ui.editor.model.IForward;
import org.jboss.tools.struts.ui.editor.model.ILink;
import org.jboss.tools.struts.ui.editor.model.IProcessItem;
import org.jboss.tools.struts.ui.editor.model.IStrutsModel;


public class GraphicalPartFactory implements EditPartFactory {
	
public EditPart createEditPart(EditPart context, Object model) {
	EditPart child = null;
	
	if (model instanceof ILink){
		child = new LinkEditPart();
	}else if(model instanceof IForward){
		child = new ForwardEditPart();
	}else if (model instanceof IProcessItem){
		if(((IProcessItem)model).getType().equals(StrutsConstants.TYPE_ACTION)){
			child = new ActionEditPart();
		}else if(((IProcessItem)model).getType().equals(StrutsConstants.TYPE_PAGE)){
			child = new PageEditPart();
		}else if(((IProcessItem)model).getType().equals(StrutsConstants.TYPE_FORWARD)){
			child = new GlobalForwardEditPart();
		}else if(((IProcessItem)model).getType().equals(StrutsConstants.TYPE_EXCEPTION)){
			child = new GlobalExceptionEditPart();
		}else if(((IProcessItem)model).getType().equals(StrutsConstants.TYPE_COMMENT)){
			child = new CommentEditPart();
		}
	}else if (model instanceof IStrutsModel){
		child = new StrutsDiagramEditPart();
	}
		
	if(child != null)child.setModel(model);
	return child;
}

}
