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
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.jboss.tools.common.gef.figures.xpl.BaseNodeFigure;
import org.jboss.tools.common.model.ui.ModelUIImages;
import org.jboss.tools.seam.ui.pages.editor.figures.xpl.FixedConnectionAnchor;

public class NodeFigure extends
		BaseNodeFigure {
	public static final Color blackColor = new Color(null, 0x00, 0x00, 0x00);

	public static final Color whiteColor = new Color(null, 0xff, 0xff, 0xff);

	public static final Color orangeColor = new Color(null, 0xff, 0xea, 0x82);

	public static final Color yellowColor = new Color(null, 0xff, 0xf6, 0xcb);

	public static final Color brownColor = new Color(null, 0xf0, 0xe8, 0xbf);

	public static final Color lightGrayColor = new Color(null, 0xf1, 0xf1, 0xf1);

	public static final Color darkGrayColor = new Color(null, 0xb3, 0xb3, 0xb3);

	public static final Color lightBlueColor = new Color(null, 0xd4, 0xe6, 0xff);

	public static final Color darkBlueColor = new Color(null, 0x97, 0xc4, 0xff);

	public static final Color pattSelected = new Color(null, 0xc6, 0xda, 0xe8);

	public static final Color pattBorder = new Color(null, 0x3e, 0x75, 0x99);

	public static final Color errorColor = new Color(null, 0xff, 0xb9, 0xb9);

	public static final Color errorSelected = new Color(null, 0xff, 0xa2, 0xa2);

	public static final Color errorBorder = new Color(null, 0xc5, 0x63, 0x62);

	public static final Color selectedColor = new Color(null, 0xf0, 0xe8, 0xbf);

	public static final Color borderColor = new Color(null, 0x86, 0x7d, 0x51);

	public final static Color ghostFillColor = new Color(null, 31, 31, 31);
	
	public static final Color exceptionBackgroundColor = new Color(null, 0xff, 0xe3, 0xe7);
	
	public static final Color exceptionForegroundColor = blackColor; //new Color(null, 0x99, 0x07, 0x02);
	
	public static final Color button1Color = new Color(null, 0xff, 0xf6, 0xc8);
	public static final Color button2Color = new Color(null, 0x98, 0x99, 0x98);
	public static final Color button3Color = new Color(null, 0xff, 0xe8, 0x79);
	public static final Color button4Color = new Color(null, 0xdb, 0xc7, 0x68);
	
	public static final Font nodeLabelFont = new Font(null, "default", 10, SWT.BOLD); // TODO: use preference font mechanism for this
	
	public static final Font nameParamFont = new Font(null, "default", 10, SWT.BOLD); // TODO: use preference font mechanism for this
	public static final Font valueParamFont = new Font(null, "default", 10, SWT.NORMAL); // TODO: use preference font mechanism for this

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