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
package org.jboss.tools.struts.ui.editor.figures;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Rectangle;

public class ActionFeedbackFigure extends ActionFigure {
	
	protected void paintChildren(Graphics graphics) {
	}
	public ActionFeedbackFigure(){
		super(null,null);
	}
	
	protected void paintFigure(Graphics g) {
		g.setXORMode(true);
		g.setForegroundColor(whiteColor);
		g.setBackgroundColor(ghostFillColor);
		Rectangle r = getBounds().getCopy();
		g.translate(r.getLocation());
		int width = r.width-1;
		int height = r.height-1;
		g.fillRectangle(1,1,width-1,height-1);
		
	}

}
