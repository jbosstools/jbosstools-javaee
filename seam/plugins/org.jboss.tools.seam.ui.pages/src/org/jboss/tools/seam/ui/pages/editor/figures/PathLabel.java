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
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.geometry.Rectangle;

public class PathLabel extends Label{
	
	public PathLabel(String name){
		super(name);
	}
	
	protected void paintFigure(Graphics graphics){
		Rectangle bounds = getBounds();
		graphics.translate(bounds.x, bounds.y);
		if(!graphics.getForegroundColor().equals(FigureFactory.selectedColor)) graphics.setForegroundColor(FigureFactory.pathColor);
		if(getIcon()!= null)graphics.drawImage(getIcon(), getIconLocation());
		graphics.drawText(getSubStringText(), getTextLocation());
		graphics.translate(-bounds.x, -bounds.y);
	}
	
}