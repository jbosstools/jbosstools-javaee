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


import org.eclipse.draw2d.FigureUtilities;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.jboss.tools.common.gef.figures.xpl.BaseNodeFigure;
import org.jboss.tools.common.model.ui.ModelUIImages;
import org.jboss.tools.seam.pages.xml.model.SeamPagesPreference;
import org.jboss.tools.seam.ui.pages.editor.figures.xpl.FixedConnectionAnchor;

public class NodeFigure extends
		BaseNodeFigure {
	
	public final static Color ghostFillColor = new Color(null, 31, 31, 31);
	
	public static Font nodeLabelFont = SeamPagesPreference.getFont(SeamPagesPreference.VIEW_PATH_FONT.getValue(), null);
	
	public static final Color lightGrayColor = new Color(null, 0xf1, 0xf1, 0xf1);
	
	public static final Image errorIcon = ModelUIImages
			.getImage("error_co.gif");

	public static final int LINK_HEIGHT = 16;
	
	public String dottedString(String str, int availTextWidth, Font font){
		if(str == null) str = "";
		String text = new String(str);

		int tWidth = FigureUtilities.getTextExtents(text, font).width;

		if (tWidth > availTextWidth) {
			  String clipString = "...";
			  int totalWidth;
			  int clipWidth = FigureUtilities.getTextExtents(clipString, font).width;;
			  int nChars;
			  for(nChars = 1; nChars < text.length(); nChars++) {
				 totalWidth = FigureUtilities.getTextExtents(text.substring(0,nChars), font).width+clipWidth;
				 if (totalWidth > availTextWidth) {
					break;
				 }
			  }
			  text = text.substring(0, nChars) + clipString;
		 }
	   	 return text;
	}

	final public void initConnectionAnchors(int numberOfAnchors) {
		if (numberOfAnchors == 0)
			numberOfAnchors = 1;
		for (int i = 0; i < numberOfAnchors; i++) {
			addConnectionAnchor(i);
		}
	}

	final public void addConnectionAnchor(int index) {
		FixedConnectionAnchor c;
		c = new FixedConnectionAnchor(this);
		c.offsetV = 10 + LINK_HEIGHT * index;
		c.leftToRight = false;
		connectionAnchors.put((index + 1) + "_OUT", c);
		outputConnectionAnchors.addElement(c);
	}

	final public void removeConnectionAnchor() {
		if (outputConnectionAnchors.size() == 1)
			return;
		outputConnectionAnchors.remove(outputConnectionAnchors.size() - 1);
	}

	final public void removeAllConnectionAnchor() {
		outputConnectionAnchors.removeAllElements();
	}
}