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

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

import org.eclipse.draw2d.*;
import org.eclipse.draw2d.geometry.*;

import org.eclipse.gef.handles.HandleBounds;

import org.jboss.tools.common.gef.GEFGraphicalViewer;
import org.jboss.tools.seam.ui.pages.editor.ecore.pages.Page;
import org.jboss.tools.seam.ui.pages.editor.edit.PageEditPart;
import org.jboss.tools.seam.ui.pages.editor.figures.xpl.FixedConnectionAnchor;

public class PageFigure extends NodeFigure implements HandleBounds,
		FigureListener {
	private static final Dimension SIZE = new Dimension(56, 100);

	private Image icon = null;

	public Page page;

	private Label label = null;

	PointList fillPointlist, fill2Pointlist, shadowPointlist, shadow2Pointlist;

	String path;

	PageEditPart editPart;

	public void setGroupEditPart(PageEditPart part) {
		editPart = part;
	}

	public void setBounds(Rectangle rect) {
		super.setBounds(rect);
		resizeFigure();
	}

	public void setConstraint(IFigure child, Object constraint) {
		super.setConstraint(child, constraint);
	}

	public void setPath(String path) {
		this.path = path;
		if (label != null) {
			label.setText(path);
			label.setSize(label.getPreferredSize());
		}
	}

	public void refreshFont() {
		if (label != null) {
			//label.setFont(group.getJSFModel().getOptions().getViewPathFont());
			label.setSize(label.getPreferredSize());
			label.setLocation(new Point(getLocation().x - 5, getLocation().y
					- (12 + 10)));
		}
	}

	public void setIcon(Image i) {
		//icon = PrintIconHelper.getPrintImage(i);
	}

	public void addNotify() {
		if (page == null)
			return;
		label = new Label(path);
		//label.setFont(group.getJSFModel().getOptions().getViewPathFont());
		getParent().add(label);
		label.setForegroundColor(ColorConstants.black);
		label.setOpaque(false);
		label.setText(path);
		label.setVisible(true);
		label.setSize(label.getPreferredSize());
		label.setLocation(new Point(getLocation().x - 5, getLocation().y
				- (12 + 10)));
		//label.addMouseListener(this);
	}

	public void removeNotify() {
		if (page == null)
			return;
		//label.removeMouseListener(this);
		getParent().remove(label);
	}

	public void figureMoved(IFigure source) {
		if (page != null)
			label.setLocation(new Point(getLocation().x - 5,
					getLocation().y - 20));
	}

	public void init(int number) {
		FixedConnectionAnchor c;
		if (number == 0)
			number = 1;
		for (int i = 0; i < number; i++) {
			c = new FixedConnectionAnchor(this);
			c.offsetV = 32 + LINK_HEIGHT * i;
			c.leftToRight = false;
			connectionAnchors.put((i + 1) + "_OUT", c);
			outputConnectionAnchors.addElement(c);
		}
	}

	public void addConnectionAnchor(int number) {
		FixedConnectionAnchor c;
		//if (number == 1)
			//return;
		c = new FixedConnectionAnchor(this);
		c.offsetV = 32 + LINK_HEIGHT * number;
		c.leftToRight = false;
		connectionAnchors.put((number + 1) + "_OUT", c);
		outputConnectionAnchors.addElement(c);
	}

	public void removeConnectionAnchor() {
		if (outputConnectionAnchors.size() == 1)
			return;
		outputConnectionAnchors.remove(outputConnectionAnchors.size() - 1);
	}

	public void removeAllConnectionAnchor() {
		outputConnectionAnchors.removeAllElements();
	}

	public PageFigure(Page group) {
		this.page = group;

		if (group != null) {
//			setIcon(group.getImage());
			setPath(group.getName());
			init(group.getOutputLinks().size());
		}

		setOpaque(false);
		setLayoutManager(new XYLayout());

		resizeFigure();
		addFigureListener(this);
		//addKeyListener(this);
		setBorder(new GroupBorder(blackColor));

		if (group != null) {
			FixedConnectionAnchor c;
			c = new FixedConnectionAnchor(this);
			c.offsetV = 8;
			c.offsetH = -1;
			connectionAnchors.put("1_IN", c);
			inputConnectionAnchors.addElement(c);

		}
	}
	public ConnectionAnchor getConnectionAnchor(String terminal) {
		ConnectionAnchor anchor = (ConnectionAnchor)connectionAnchors.get(terminal);
		while(anchor == null){
			addConnectionAnchor(outputConnectionAnchors.size());
			anchor = (ConnectionAnchor)connectionAnchors.get(terminal);
		}
		return anchor;
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

	private void resizeFigure() {
		if (width == getSize().width && height == getSize().height)
			return;

		int start = 0;
		width = getSize().width - 1;
		height = getSize().height - 1;
		

		fillPointlist = new PointList();

		fillPointlist.addPoint(start, 20);
		fillPointlist.addPoint(start + 23, 20);
		fillPointlist.addPoint(start + 23, 0);
		fillPointlist.addPoint(width - 15, 0);
		fillPointlist.addPoint(width - 1, 14);
		fillPointlist.addPoint(width - 1, height - 1);
		fillPointlist.addPoint(start, height - 1);

		

		shadowPointlist = new PointList();

		shadowPointlist.addPoint(width - 15, 0);
		shadowPointlist.addPoint(width - 14, 4);
		shadowPointlist.addPoint(width - 15, 7);
		shadowPointlist.addPoint(width - 18, 10);
		shadowPointlist.addPoint(width - 1, 14);

		shadowPointlist.addPoint(width - 9, 14);
		shadowPointlist.addPoint(width - 16, 13);

		shadowPointlist.addPoint(width - 21, 11);
		shadowPointlist.addPoint(width - 18, 8);
		shadowPointlist.addPoint(width - 16, 4);

		shadow2Pointlist = new PointList();

		shadow2Pointlist.addPoint(width - 15, 0);
		shadow2Pointlist.addPoint(width - 1, 14);
		shadow2Pointlist.addPoint(width - 3, 14);
		shadow2Pointlist.addPoint(width - 15, 2);
	}

	/**
	 * @see org.eclipse.draw2d.Figure#paintFigure(Graphics)
	 */
	protected void paintFigure(Graphics g) {
		Rectangle r = getBounds().getCopy();
		g.translate(r.getLocation());

		int height = r.height - 1;
		int start = 0;
		

		g.setBackgroundColor(whiteColor);

		g.fillRectangle(start + 1, 1, 22, 19);

		if (icon != null)
			g.drawImage(icon, start + 4, 2);
		

		if (page != null /*&& group.isConfirmed()*/) {
			g.setBackgroundColor(yellowColor);
		} else {
			g.setBackgroundColor(lightGrayColor);
		}

		g.fillPolygon(fillPointlist);
		

		if (page != null /*&& group.isConfirmed()*/) {
			g.setBackgroundColor(orangeColor);
		} else {
			g.setBackgroundColor(lightGrayColor);
		}

		g.fillPolygon(shadowPointlist);
		g.fillPolygon(shadow2Pointlist);
		
		
	}

	class GroupBorder extends LineBorder {
		public GroupBorder(Color color) {
			super(color);
		}

		public void paint(IFigure figure, Graphics graphics, Insets insets) {
			Rectangle r = getPaintRectangle(figure, insets).getCopy();
			graphics.translate(r.getLocation());
			int start = 0;
			int width = r.width - 1;
			int height = r.height - 1;
			
			if (page != null /*&& group.isConfirmed()*/)
				graphics.setForegroundColor(blackColor);
			else
				graphics.setForegroundColor(darkGrayColor);

			graphics.drawLine(start + 1, 0, width - 15, 0);
			graphics.drawLine(start, 1, start, height - 2);
			graphics.drawLine(start + 1, height - 1, width - 2, height - 1);
			graphics.drawLine(width - 1, 14, width - 1, height - 2);
			graphics.drawLine(width - 15, 0, width - 1, 14);

			graphics.drawLine(start, 1, start + 1, 0);
			graphics.drawLine(start, height - 2, start + 1, height - 1);
			graphics.drawLine(width - 2, height - 1, width - 1, height - 2);

			graphics.drawLine(width - 15, 0, width - 14, 4);
			graphics.drawLine(width - 14, 4, width - 15, 7);
			graphics.drawLine(width - 15, 7, width - 18, 10);

			graphics.drawLine(width - 18, 10, width - 1, 14);

			graphics.drawLine(start + 23, 0, start + 23, 19);
			graphics.drawLine(start, 20, start + 22, 20);
			graphics.drawLine(start + 22, 20, start + 23, 19);

			
			if (page != null /*&& group.isConfirmed()*/)
				graphics.setForegroundColor(borderColor);
			else
				graphics.setForegroundColor(darkGrayColor);

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