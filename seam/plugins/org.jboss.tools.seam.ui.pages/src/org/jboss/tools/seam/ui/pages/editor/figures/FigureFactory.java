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
import org.eclipse.draw2d.ManhattanConnectionRouter;
import org.eclipse.swt.graphics.Color;
import org.jboss.tools.seam.ui.pages.editor.ecore.pages.Link;
import org.jboss.tools.seam.ui.pages.editor.edit.LinkEditPart;
import org.jboss.tools.seam.ui.pages.editor.edit.xpl.PagesConnectionRouter;

public class FigureFactory {
	public static final Color normalColor = new Color(null, 0xb5, 0xb5, 0xb5);
	public static final Color selectedColor = new Color(null, 0x44, 0xa9, 0xf3);
	public static final Color highlightColor = ColorConstants.black;

	public static ConnectionFigure createNewBendableWire(LinkEditPart part,
			Link link) {
		ConnectionFigure conn = new ConnectionFigure(part);
		conn.setForegroundColor(normalColor);
		return conn;
	}

	public static ConnectionFigure createNewLink(Link link) {

		ConnectionFigure conn = new ConnectionFigure();
		conn.setConnectionRouter(new PagesConnectionRouter());
		conn.setForegroundColor(selectedColor);

		return conn;
	}
}