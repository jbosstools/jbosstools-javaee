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

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.PolygonDecoration;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.swt.graphics.Color;

import org.jboss.tools.struts.ui.editor.edit.LinkEditPart;
import org.jboss.tools.struts.ui.editor.edit.StrutsConnectionRouter;
import org.jboss.tools.struts.ui.editor.figures.xpl.CustomLocator;
import org.jboss.tools.struts.ui.editor.model.ILink;

public class FigureFactory {
	//public static final Color normalColor = ColorConstants.black;
	public static final Color normalColor = new Color(null, 0xb5,0xb5,0xb5);
	public static final Color selectedColor = new Color(null, 0x44,0xa9,0xf3);
	public static final Color highlightColor = ColorConstants.black;
	//public static final Image icon = ImageDescriptor.createFromFile(StrutsEditor.class,"icons/shortcut.gif").createImage(); 
	
	public static final PointList TRIANGLE_TIP = new PointList();

	static {
		TRIANGLE_TIP.addPoint(0, 0);
		TRIANGLE_TIP.addPoint(-8, -4);
		TRIANGLE_TIP.addPoint(-10, -4);
		TRIANGLE_TIP.addPoint(-10, -3);
		TRIANGLE_TIP.addPoint(-8, -1);
		TRIANGLE_TIP.addPoint(-8, 1);
		TRIANGLE_TIP.addPoint(-10, 3);
		TRIANGLE_TIP.addPoint(-10, 4);
		TRIANGLE_TIP.addPoint(-8, 4);
	}

public static ConnectionFigure createNewBendableWire(LinkEditPart part, ILink link){
	ConnectionFigure conn = new ConnectionFigure(part);
	conn.setForegroundColor(normalColor);
	
	PolygonDecoration decor = new PolygonDecoration();
	decor.setTemplate(TRIANGLE_TIP);
	decor.setScale(1,1);

	conn.setTargetDecoration(decor);

	/*StrutsLabel label = new StrutsLabel(link.getLinkName());
	label.setFont(part.getLinkModel().getStrutsModel().getOptions().getLinkPathFont());
	label.setIcon(null);
	label.setTextAlignment(Label.LEFT);
	label.setLabelAlignment(Label.LEFT);
	label.setVisible(!link.isShortcut());
	
	CustomLocator locator =  new CustomLocator(conn, false);
	locator.setUDistance(5);
	locator.setVDistance(-13);
	conn.add(label, locator);*/
	
	part.getLinkModel().setTarget();
	
	
	BreakPointFigure bp = new BreakPointFigure(part, link.getHeadBreakPoint());
	CustomLocator locator = new CustomLocator(conn, true);
	locator.setUDistance(0);
	locator.setVDistance(-4);
	conn.add(bp, locator);

	bp = new BreakPointFigure(part, link.getTailBreakPoint());
	locator = new CustomLocator(conn, false);
	locator.setUDistance(1);
	locator.setVDistance(-4);
	conn.add(bp, locator);

	/*String text="";
	
    if(part.getLinkModel().getToProcessItem().isAction())
    	text = part.getLinkModel().getToProcessItem().getVisiblePath();
     else
     	text = part.getLinkModel().getToProcessItem().getName();
	

	StrutsLabel label = new StrutsLabel(text);
	label.setIcon(link.getToProcessItem().getImage());
	label.setFont(part.getLinkModel().getStrutsModel().getOptions().getPathFont());
	label.setTextAlignment(Label.LEFT);
	label.setLabelAlignment(Label.LEFT);
	label.setIconAlignment(Label.LEFT);
	label.setVisible(link.isShortcut());

	locator =  new CustomLocator(conn, false);
	locator.setUDistance(18);
	locator.setVDistance(-6);
	conn.add(label, locator);*/
	
	return conn;
}


public static ConnectionFigure createNewLink(ILink link){

	ConnectionFigure conn = new ConnectionFigure();
	conn.setConnectionRouter(new StrutsConnectionRouter());
	conn.setForegroundColor(selectedColor);
	
	PolygonDecoration decor = new PolygonDecoration();
	decor.setTemplate(TRIANGLE_TIP);
	decor.setScale(1,1);

	conn.setTargetDecoration(decor);

	return conn;
}
}