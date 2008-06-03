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

import org.eclipse.draw2d.FigureUtilities;
import org.eclipse.draw2d.IFigure;
import org.jboss.tools.common.model.ui.dnd.DnDUtil;

import org.jboss.tools.common.meta.action.XAction;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.struts.ui.StrutsUIPlugin;
import org.jboss.tools.struts.ui.editor.figures.ActionFigure;
import org.jboss.tools.struts.ui.editor.figures.NodeFigure;
import org.jboss.tools.struts.ui.editor.model.IForward;

public class ActionEditPart extends ProcessItemEditPart {
	public void doDoubleClick(boolean cf) {
		try {
			XAction action = DnDUtil.getEnabledAction(
					(XModelObject) getProcessItemModel().getSource(), null,
					"OpenSource");
			
			if (action != null)
				action.executeHandler(
						(XModelObject) getProcessItemModel().getSource(),
						null);
			
		} catch (Exception e) {
			StrutsUIPlugin.getPluginLog().logError(e);
		}
	}

	protected IFigure createFigure() {
		fig = new ActionFigure(getProcessItemModel(), this);
		return fig;
	}
	
	protected void layoutForwards(){
		int start = 23;
		int width, maxWidth;
		int height= NodeFigure.LINK_HEIGHT;
		IForward forward;
		prefferedSize.height = 20;
		prefferedSize.width = getProcessItemModel().getBounds().width;
		
		if(prefferedSize.width == 0){
			maxWidth = FigureUtilities.getTextExtents(getProcessItemModel().getName(), getProcessItemModel().getStrutsModel().getOptions().getActionFont()).width+38;
			for(int i=0;i<getProcessItemModel().getForwardList().size();i++){
				forward = (IForward)getProcessItemModel().getForwardList().get(i);
				width = FigureUtilities.getTextExtents(forward.getName(), getProcessItemModel().getStrutsModel().getOptions().getForwardFont()).width+31;
				if(width > maxWidth)maxWidth = width;
			}
			if(maxWidth < 100)maxWidth = 100;
			prefferedSize.width = maxWidth;
		}
		
		for(int i=0;i<getProcessItemModel().getForwardList().size();i++){
			forward = (IForward)getProcessItemModel().getForwardList().get(i);
			forward.setBounds(start, prefferedSize.height, prefferedSize.width-24, height);
			prefferedSize.height += height;
		}
		prefferedSize.height += 5;
	}
	
}
