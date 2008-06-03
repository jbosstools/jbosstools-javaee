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

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.geometry.Dimension;
import org.jboss.tools.common.model.ui.dnd.DnDUtil;

import org.jboss.tools.common.meta.action.XAction;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.struts.ui.StrutsUIPlugin;
import org.jboss.tools.struts.ui.editor.figures.NodeFigure;
import org.jboss.tools.struts.ui.editor.figures.PageFigure;

public class PageEditPart extends ProcessItemEditPart {
		
	public void setModel(Object model){
		prefferedSize = new Dimension(50, 25);
		super.setModel(model);
	}
	
	public void doDoubleClick(boolean cf){
		try {
			XModelObject source = (XModelObject)getProcessItemModel().getSource();
			XAction action = DnDUtil.getEnabledAction(source, null, "OpenPage");
			if(action == null) action = DnDUtil.getEnabledAction(source, null, "SelectTile");
			if(action != null) action.executeHandler(source, null);
		} catch (Exception e) {
			StrutsUIPlugin.getPluginLog().logError(e);
		}
	}
	
	protected IFigure createFigure() {
		fig = new PageFigure(getProcessItemModel(),this);
		return fig;
	}
	
	protected void layoutForwards(){
		//Dimension size=new Dimension();//= getGroupModel().getSize();
//		int start = 0;
//		int height;
		
		if(getProcessItemModel().getForwardList().size() == 0 && getProcessItemModel().hasPageHiddenLinks()) prefferedSize.height = 25+(NodeFigure.LINK_HEIGHT-1);
		else prefferedSize.height = 25+(NodeFigure.LINK_HEIGHT-1)*getProcessItemModel().getForwardList().size();
		//getProcessItemModel().setSize(size);
	}

}
