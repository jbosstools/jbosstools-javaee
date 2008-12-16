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
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LineBorder;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.XYLayout;
import org.eclipse.draw2d.geometry.Dimension;
import org.eclipse.draw2d.geometry.Insets;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.handles.HandleBounds;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.jboss.tools.common.gef.GEFGraphicalViewer;
import org.jboss.tools.seam.pages.xml.model.SeamPagesPreference;
import org.jboss.tools.seam.ui.pages.editor.PagesEditor;
import org.jboss.tools.seam.ui.pages.editor.ecore.pages.PageException;
import org.jboss.tools.seam.ui.pages.editor.edit.ExceptionEditPart;
import org.jboss.tools.seam.ui.pages.editor.figures.xpl.CompressNameUtil;
import org.jboss.tools.seam.ui.pages.editor.figures.xpl.FixedConnectionAnchor;

public class ExceptionFigure extends NodeFigure implements HandleBounds {
	private static final Dimension SIZE = new Dimension(56, 100);
	
	private static final Color exceptionBackgroundColor = new Color(null, 0xea, 0xf3, 0xff);
	
	private static final Color exceptionForegroundColor = new Color(null, 0x41, 0x77, 0xa0);
	
	private static final Color borderColor = new Color(null, 0x67, 0x7f, 0x91);
	
	private static final Color greyForeground = new Color(null, 0x99, 0x95, 0x99);
	
	private static final Image exceptionImage = ImageDescriptor.createFromFile(
			PagesEditor.class, "icons/ico_exception.png").createImage();


	public PageException exc;

	ExceptionEditPart editPart;

	public void setEditPart(ExceptionEditPart part) {
		editPart = part;
	}

	public void setConstraint(IFigure child, Object constraint) {
		super.setConstraint(child, constraint);
	}

	public void setIcon(Image i) {
	}

	public ExceptionFigure(PageException group) {
		this.exc = group;

		setOpaque(false);
		setLayoutManager(new XYLayout());

		setBorder(new GroupBorder(ColorConstants.black));

		if (group != null) {
			FixedConnectionAnchor c;

			c = new FixedConnectionAnchor(this);
			c.offsetV = 10;
			c.offsetH = -1;
			c.leftToRight = false;
			connectionAnchors.put("1_OUT", c);
			outputConnectionAnchors.addElement(c);
		}
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

	int width, height;

	/**
	 * @see org.eclipse.draw2d.Figure#paintFigure(Graphics)
	 */
	protected void paintFigure(Graphics g) {
		Rectangle r = getBounds().getCopy();
		g.translate(r.getLocation());

		if (exc != null) {
			g.setBackgroundColor(exceptionBackgroundColor);
			g.setForegroundColor(exceptionForegroundColor);
		} else {
			g.setBackgroundColor(lightGrayColor);
		}
		
		Rectangle boundingRect = new Rectangle(1, 1, r.width, r.height);
		
		g.fillRectangle(boundingRect);
		
		g.drawImage(exceptionImage, 1, 1);
		
		if(exc != null){
			g.setFont(nodeLabelFont);
			g.drawString(getExceptionReadOnlyLabel(), 27, 3);			
		}
		

	}

	/** This returns the label to use when rendering the Exception in a readonly view.
	 *  Converts org.model.Exception to o.m.Exception to save visual space 
	 **/
	String getExceptionReadOnlyLabel() {
		if(exc==null || exc.getName() == null) {
			return "Unknown Exception";
		} else {
			return CompressNameUtil.getCompressedName(exc.getName());
		}
	}
	
		
	class GroupBorder extends LineBorder {
		public GroupBorder(Color color) {
			super(color);
		}

		public void paint(IFigure figure, Graphics graphics, Insets insets) {
			Rectangle r = getPaintRectangle(figure, insets).getCopy();
			graphics.translate(r.getLocation());
			int width = r.width - 1;
			int height = r.height - 1;
			
			if (exc != null)
				graphics.setForegroundColor(borderColor);
			else
				graphics.setForegroundColor(greyForeground);
			
			graphics.drawLine(1, 0, width-1, 0);
			graphics.drawLine(0, 1, 0, height - 1);
			graphics.drawLine(1, height, width-1, height);
			graphics.drawLine(width, 1, width, height - 1);
	}

	public void mouseDoubleClicked(MouseEvent me) {
	}

	public void mousePressed(MouseEvent me) {
		if (me.button == 3) {
			((GEFGraphicalViewer) editPart.getViewer()).setNoDeselect();
			editPart.getViewer().select(editPart);
		}
	}


}
}