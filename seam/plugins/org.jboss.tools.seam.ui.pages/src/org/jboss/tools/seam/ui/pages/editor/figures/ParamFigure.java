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

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.handles.HandleBounds;
import org.jboss.tools.seam.ui.pages.editor.ecore.pages.Param;
import org.jboss.tools.seam.ui.pages.editor.edit.ParamEditPart;

public class ParamFigure extends NodeFigure implements HandleBounds {
	private static final Dimension SIZE = new Dimension(56, 100);

	public Param exc;

	ParamEditPart editPart;

	public void setEditPart(ParamEditPart part) {
		editPart = part;
	}

	public ParamFigure(Param group) {
		this.exc = group;

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

	/**
	 * @see org.eclipse.draw2d.Figure#paintFigure(Graphics)
	 */
	protected void paintFigure(Graphics g) {
		Rectangle r = getBounds().getCopy();
		g.translate(r.getLocation());

		if(exc != null){
			g.setFont(nameParamFont);
			if(exc.getName() != null)
				g.drawString(exc.getName()+":", 5, 1);
			else
				g.drawString("Param:", 5, 1);
			
			g.setFont(valueParamFont);
			if(exc.getValue() != null)
				g.drawString(exc.getValue(), 105, 1);
			else
				g.drawString("value", 105, 1);

		}
		

	}
}