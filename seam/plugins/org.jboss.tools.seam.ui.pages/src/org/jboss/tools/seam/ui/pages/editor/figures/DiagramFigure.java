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
import org.eclipse.draw2d.FreeformLayer;
import org.eclipse.draw2d.FreeformLayout;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.MarginBorder;
import org.eclipse.draw2d.PrinterGraphics;
import org.eclipse.draw2d.geometry.Rectangle;
import org.jboss.tools.seam.ui.pages.editor.edit.PagesDiagramEditPart;



public class DiagramFigure extends FreeformLayer implements IFigure {
	
	private PagesDiagramEditPart editPart;
	
	public DiagramFigure(PagesDiagramEditPart editPart) {
		super();
		this.editPart = editPart;
		setLayoutManager(new FreeformLayout());
		setBorder(new MarginBorder(5));
		setBackgroundColor(ColorConstants.white);
		setOpaque(true);
	}

	protected void paintFigure(Graphics g) {
		super.paintFigure(g);
		Rectangle r = getBounds().getCopy();
		g.translate(r.getLocation());
		int width = r.width;
		int height = r.height;

		if (editPart.isGridVisible() && !(g instanceof PrinterGraphics)) {
			g.setLineStyle(Graphics.LINE_DOT);
			g.setForegroundColor(NodeFigure.lightGrayColor);
			g.setBackgroundColor(ColorConstants.white);
			for (int i = 0; i < width; i += editPart.getVisualGridStep()) {
				g.drawLine(i, 0, i, height - 1);
			}
			for (int i = 0; i < height; i += editPart.getVisualGridStep()) {
				g.drawLine(0, i, width, i);
			}
			g.setLineStyle(Graphics.LINE_SOLID);
		}
	}

}
