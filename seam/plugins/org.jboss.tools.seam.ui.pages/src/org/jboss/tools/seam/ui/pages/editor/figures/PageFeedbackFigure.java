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
import org.eclipse.draw2d.geometry.Rectangle;

/**
 * @author Daniel
 *
 */
public class PageFeedbackFigure extends PageFigure {
	public PageFeedbackFigure(){
		super(null);
	}
	
	protected void paintFigure(Graphics g) {
		g.setXORMode(true);
		g.setForegroundColor(whiteColor);
		g.setBackgroundColor(ghostFillColor);

		Rectangle r = getBounds().getCopy();
		g.translate(r.getLocation());
		int start=0;
		
		g.fillRectangle(start+1,1,22,19);

		g.fillPolygon(fillPointlist);
	}

}
