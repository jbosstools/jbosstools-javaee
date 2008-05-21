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
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.GraphicalEditPart;

import org.jboss.tools.common.meta.action.XActionInvoker;
import org.jboss.tools.common.model.XModelObject;
import org.jboss.tools.struts.ui.editor.figures.CommentFigure;

public class CommentEditPart extends ProcessItemEditPart {
	public CommentEditPart(){
		super();
		prefferedSize = new Dimension (100,21);
	}
	
	public void processItemChange() {
		((CommentFigure)getFigure()).setText(((XModelObject)getProcessItemModel().getSource()).getAttributeValue("comment"));
		getFigure().getLayoutManager().layout(getFigure());
		super.processItemChange();
	}
	protected IFigure createFigure() {
		fig = new CommentFigure(getProcessItemModel(), this);
		fig.setVisible(getProcessItemModel().getStrutsModel().areCommentsVisible());
		return fig;
	}
	
	protected void refreshVisuals() {
		if(getParent() == null)return;
		Point loc = getProcessItemModel().getPosition();
		
		Dimension size= getProcessItemModel().getSize();
		boolean initial = size == null || size.width == 0;
		if(initial) {
			size.height = 24;
			size.width = getFigure().getLayoutManager().getPreferredSize(getFigure(), -1, size.height).width;
			if(size.width > 200) size.width = 90;
		}
		
//		if(size.width < 100)size = calculatePreffSize();
		if(size.width < 24) size.width = 24;

		size.height = getFigure().getLayoutManager().getPreferredSize(getFigure(), size.width,-1).height;
		
		if(size.width < 100 && size.height > size.width && size.height > 30) {
			size.width = 1 + (int)Math.sqrt(1d * size.width * size.height);
			size.width -= size.width % 4;
			if(size.width < 24) size.width = 24;
			size.height = getFigure().getLayoutManager().getPreferredSize(getFigure(), size.width,-1).height;
		}
		
		Rectangle r = new Rectangle(loc ,size);

		((GraphicalEditPart) getParent()).setLayoutConstraint(
			this,
			getFigure(),
			r);
	}
	
	public void doDoubleClick(){
		XActionInvoker.invoke("Edit", (XModelObject)getProcessItemModel().getSource(),null);
	}

	protected void layoutForwards(){
		/*prefferedSize.width = getProcessItemModel().getBounds().width;
		if(prefferedSize.width == 0)prefferedSize.width = 100;
		prefferedSize.height = ((TextFlow)((FlowPage)getFigure().getChildren().get(0)).getChildren().get(0)).getBounds().height;
		if(prefferedSize.height == 0) prefferedSize.height = 30;*/
	}
	
}
