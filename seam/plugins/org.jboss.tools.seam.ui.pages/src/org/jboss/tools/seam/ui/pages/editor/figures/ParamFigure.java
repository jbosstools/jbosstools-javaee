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
package org.jboss.tools.seam.ui.pages.editor.figures;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.handles.HandleBounds;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.jboss.tools.seam.ui.pages.editor.ecore.pages.Param;
import org.jboss.tools.seam.ui.pages.editor.edit.ParamEditPart;

public class ParamFigure extends NodeFigure implements HandleBounds {
	private static final Dimension SIZE = new Dimension(56, 100);
	
	private static final Font nameParamFont = new Font(null, "default", 10, SWT.BOLD); // TODO: use preference font mechanism for this

	private static final Font valueParamFont = new Font(null, "default", 10, SWT.NORMAL); // TODO: use preference font mechanism for this

	public Param param;

	ParamEditPart editPart;

	public void setEditPart(ParamEditPart part) {
		editPart = part;
	}

	public ParamFigure(Param group) {
		this.param = group;

		setOpaque(true);
	}

	/**
	 * @see org.eclipse.gef.handles.HandleBounds#getHandleBounds()
	 */
	public Rectangle getHandleBounds() {
		return getBounds().getCropped(new Insets(0, 0, 0, 0));
	}

	/**
	 * @see org.eclipse.draw2d.Figure#getPreferredSize(int, int)
	 */
	public Dimension getPreferredSize(int wHint, int hHint) {
		return SIZE;
	}
	
	private int getTextWidth(){
		return 90;
	}
	
	private int getTextInset(){
		return 5;
	}

	/**
	 * @see org.eclipse.draw2d.Figure#paintFigure(Graphics)
	 */
	protected void paintFigure(Graphics g) {
		Rectangle r = getBounds().getCopy();
		g.translate(r.getLocation());
		
		if (editPart.getSelected() == EditPart.SELECTED_PRIMARY
				|| editPart.getSelected() == EditPart.SELECTED) {
			    g.setBackgroundColor(ColorConstants.white);
			    g.fillRectangle(1, 1, r.width-2, r.height-2);
			    
		} else {
		    g.setBackgroundColor(lightGrayColor);
		    g.fillRectangle(1, 1, r.width-2, r.height-2);
		}
		String name;
		if(param.getName() != null){
			name = dottedString(param.getName(), getTextWidth()-getTextInset(), nameParamFont);
			name += ":";
		}else
			name = "Param:";
		
		String value;
		if(param.getValue() != null){
			value = dottedString(param.getValue(), getTextWidth()-getTextInset(), valueParamFont);
		}else
			value = "value";

		if(param != null){
			g.setFont(nameParamFont);
			g.drawString(name, getTextInset(), 1);
			
			g.setFont(valueParamFont);
			g.drawString(value, getTextWidth()+3*getTextInset(), 1);

		}
		

	}
}