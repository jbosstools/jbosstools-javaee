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

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import org.eclipse.draw2d.ConnectionAnchor;
import org.eclipse.draw2d.FigureUtilities;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.jboss.tools.common.gef.figures.xpl.BaseNodeFigure;
import org.jboss.tools.common.model.ui.ModelUIImages;

public class NodeFigure 
	extends BaseNodeFigure
{
	public static final Color blackColor = new Color(null, 0x00, 0x00, 0x00);
	public static final Color whiteColor = new Color(null, 0xff, 0xff, 0xff);

	public static final Color orangeColor = new Color(null, 0xff, 0xea, 0x82);
	public static final Color yellowColor = new Color(null, 0xff, 0xf6, 0xcb);
	public static final Color brownColor = new Color(null, 0xf0, 0xe8, 0xbf);

	public static final Color lightGrayColor = new Color(null, 0xf1, 0xf1, 0xf1);
	public static final Color darkGrayColor = new Color(null, 0xb3, 0xb3, 0xb3);
    public static final Color mediumGrayColor = new Color(null, 0xb5,0xb5,0xb5);

	//public static final Color lightBlueColor = new Color(null, 0xcb, 0xeb, 0xff);
	//public static final Color darkBlueColor = new Color(null, 0x82, 0xcf, 0xff);
	public static final Color lightBlueColor = new Color(null, 0xd4, 0xe6, 0xff);
	public static final Color darkBlueColor = new Color(null, 0x97, 0xc4, 0xff);
	public static final Color pattSelected = new Color(null, 0xc6, 0xda, 0xe8);
	public static final Color pattBorder = new Color(null, 0x3e, 0x75, 0x99);

	public static final Color selectedColor = new Color(null, 0xf0, 0xe8, 0xbf);
	public static final Color borderColor = new Color(null, 0x86, 0x7d, 0x51);
	public final static Color ghostFillColor = new Color(null, 31, 31, 31);
	
	public final static Color anotherModuleColor = new Color(null, 0xec, 0xf4, 0xe7);
	
	public static final Image errorIcon = ModelUIImages.getImage("error_co.gif");
	
	public static final int LINK_HEIGHT = 16;

protected Hashtable<String,ConnectionAnchor> connectionAnchors = new Hashtable<String,ConnectionAnchor>(7);
protected Vector<ConnectionAnchor> inputConnectionAnchors = new Vector<ConnectionAnchor>(2,2);
protected Vector<ConnectionAnchor> outputConnectionAnchors = new Vector<ConnectionAnchor>(2,2);

public ConnectionAnchor connectionAnchorAt(Point p) {
	ConnectionAnchor closest = null;
	long min = Long.MAX_VALUE;

	Enumeration e = getSourceConnectionAnchors().elements();
	while (e.hasMoreElements()) {
		ConnectionAnchor c = (ConnectionAnchor) e.nextElement();
		Point p2 = c.getLocation(null);
		long d = p.getDistance2(p2);
		if (d < min) {
			min = d;
			closest = c;
		}
	}
	e = getTargetConnectionAnchors().elements();
	while (e.hasMoreElements()) {
		ConnectionAnchor c = (ConnectionAnchor) e.nextElement();
		Point p2 = c.getLocation(null);
		long d = p.getDistance2(p2);
		if (d < min) {
			min = d;
			closest = c;
		}
	}
	return closest;
}

public ConnectionAnchor getConnectionAnchor(String terminal) {
	return connectionAnchors.get(terminal);
}

public String getConnectionAnchorName(ConnectionAnchor c){
	Enumeration<String> enumeration = connectionAnchors.keys();
	String key;
	while (enumeration.hasMoreElements()){
		key = enumeration.nextElement();
		if (connectionAnchors.get(key).equals(c))
			return key;
	}
	return null;
}

public ConnectionAnchor getSourceConnectionAnchorAt(Point p) {
	ConnectionAnchor closest = null;
	long min = Long.MAX_VALUE;

	Enumeration e = getSourceConnectionAnchors().elements();
	while (e.hasMoreElements()) {
		ConnectionAnchor c = (ConnectionAnchor) e.nextElement();
		Point p2 = c.getLocation(null);
		long d = p.getDistance2(p2);
		if (d < min) {
			min = d;
			closest = c;
		}
	}
	return closest;
}

public Vector getSourceConnectionAnchors() {
	return outputConnectionAnchors;
}

public ConnectionAnchor getTargetConnectionAnchorAt(Point p) {
	ConnectionAnchor closest = null;
	long min = Long.MAX_VALUE;

	Enumeration e = getTargetConnectionAnchors().elements();
	while (e.hasMoreElements()) {
		ConnectionAnchor c = (ConnectionAnchor) e.nextElement();
		Point p2 = c.getLocation(null);
		long d = p.getDistance2(p2);
		if (d < min) {
			min = d;
			closest = c;
		}
	}
	return closest;
}
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

public Vector getTargetConnectionAnchors() {
	return inputConnectionAnchors;
}


}