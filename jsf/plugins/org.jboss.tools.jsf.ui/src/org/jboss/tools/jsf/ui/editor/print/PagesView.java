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
package org.jboss.tools.jsf.ui.editor.print;

import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.eclipse.draw2d.FigureUtilities;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.PolylineConnection;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.editparts.LayerManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;

import org.jboss.tools.jsf.ui.editor.figures.GroupFigure;

public class PagesView extends Composite implements MouseListener,PaintListener{
	double viewScale = 0.25;
	int pageCount = 0;
	boolean selectionEnabled = false;
	Dimension containerSize;
	Pages pages;
	int pW = 0;
	int pH = 0;
	int zeroX = 100;
	int zeroY = 0;
	GraphicalViewer viewer;
	boolean recount = true;
	List<IFigure> figures = new Vector<IFigure>();

	public PagesView(Pages p, Dimension viewSize, Composite parent, int style) {
		super(parent, style);
		if (p.isTextPrint()) {
			this.containerSize = viewSize;
			this.pages = p;
		} else {
			this.containerSize = viewSize;
			this.pages = p;
			this.viewer = p.getViewer();
			this.addMouseListener(this);
			this.viewScale = calculateScale(containerSize, pages.getDimension());
			PropertyChangeListener pcl = new PropertyChangeListener() {
				public void propertyChange(PropertyChangeEvent e) {
					if (e.getPropertyName().equals("scale")) { //$NON-NLS-1$
						setViewScale(calculateScale(containerSize, pages
								.getDimension()));
						if (pageCount != pages.getSourcePagesCount()) {
							recount = true;
							pageCount = pages.getSourcePagesCount();
						} else {
							recount = false;
						}
						redraw();
					}
				}
			};
			pages.addPropertyChangeListener(pcl);
			this.addPaintListener(this);
			this.pageCount = pages.getSourcePagesCount();
		}
	}

	public double calculateScale(Dimension panelSize, Dimension componentSize) {
		double sX = 1;
		double sY = 1;
		double s = 1;
		if (panelSize.width > componentSize.width) {
			sX = (double) 1;
		} else {
			sX = (double) panelSize.width / componentSize.width;
		}
		if (panelSize.height > componentSize.height) {
			sY = (double) 1;
		} else {
			sY = (double) panelSize.height / componentSize.height;
		}
		if (sX < sY) {
			s = sX;
		} else {
			s = sY;
		}
		return s;
	}

	public void getChilds(IFigure f) {
		for (Iterator i = f.getChildren().iterator(); i.hasNext();) {
			IFigure o = (IFigure) i.next();
			figures.add(o);
			getChilds(o);
		}
	}

	public int getTrueHeight(Label label, double vScale) {
		String s = label.getText();
		Font ff = label.getFont();
		FontData fd = ff.getFontData()[0];
		int h = fd.getHeight();
		int nh = (int) (h * vScale);
		if (nh > 0) {
			fd.setHeight(nh);
			Font f2 = new Font(null, fd);
			int dmm = FigureUtilities.getTextWidth(s, f2);
			int labW = (int) (label.getBounds().width * vScale);
			while (labW > dmm) {
				nh++;
				fd.setHeight(nh);
				Font f3 = new Font(null, fd);
				dmm = FigureUtilities.getTextWidth(s, f3);
				f3.dispose();
			}

			f2.dispose();
			return nh;
		}
		return 0;
	}

	public void paintControl(PaintEvent pe) {

		GC g2 = pe.gc;
		Color white = new Color(pe.display, 0xff, 0xff, 0xff);
		Color black = new Color(pe.display, 0x00, 0x00, 0x00);
		Color gray = pe.display.getSystemColor(SWT.COLOR_GRAY);
		Color yellowColor = new Color(null, 0xff, 0xf6, 0xcb);
		Color lightGrayColor = new Color(null, 0xf1, 0xf1, 0xf1);
		Color lightBlueColor = new Color(null, 0xcb, 0xeb, 0xff);

		int xmax = 0;
		int ymax = 0;
		int cx = 0;
		int cy = 0;
		for (int i = 0; i < pages.getSourcePagesCount(); i++) {
			Rectangle nrz = new Rectangle((int) Math.round(pages.getSourcePage(
					i).getRectangle().x
					* viewScale), (int) Math.round(pages.getSourcePage(i)
					.getRectangle().y
					* viewScale), (int) Math.round(pages.getSourcePage(i)
					.getRectangle().width
					* viewScale), (int) Math.round(pages.getSourcePage(i)
					.getRectangle().height
					* viewScale));
			if ((nrz.y + nrz.height) > ymax) {
				ymax = nrz.y + nrz.height;
				if (recount)
					pH = nrz.height;
				cy++;
			}
			if ((nrz.x + nrz.width) > xmax) {
				xmax = nrz.x + nrz.width;
				if (recount)
					pW = nrz.width;
				cx++;
			}
		}

		recount = false;
		ymax = cy * pH;
		xmax = cx * pW;

		zeroX = (int) (this.containerSize.width - xmax) / 2;
		zeroY = (int) (this.containerSize.height - ymax) / 2;

		// --->paint viewer!!!!

		LayerManager lm = (LayerManager) viewer.getEditPartRegistry().get(
				LayerManager.ID);
		IFigure f = lm.getLayer(LayerConstants.PRINTABLE_LAYERS);
		if (figures.isEmpty())
			getChilds(f);

		int ix = 0;
		int iy = 0;
		for (int i = 0; i < figures.size(); i++) {
			IFigure f1 = (IFigure) figures.get(i);
			if (f1.getBounds().x < 0) {
				if (f1.getBounds().x < ix)
					ix = f1.getBounds().x;
			}
			if (f1.getBounds().y < 0) {
				if (f1.getBounds().y < iy)
					iy = f1.getBounds().y;
			}
		}

		for (int i = 0; i < figures.size(); i++) {
			IFigure f1 = (IFigure) figures.get(i);
			Rectangle r = new Rectangle(f1.getBounds().x + Math.abs(ix), f1
					.getBounds().y
					+ Math.abs(iy), f1.getBounds().width, f1.getBounds().height);
			Rectangle rs = new Rectangle((int) (r.x * viewScale) + zeroX,
					(int) (r.y * viewScale) + zeroY,
					(int) (r.width * viewScale), (int) (r.height * viewScale));

			if (f1 instanceof PolylineConnection) {
				PolylineConnection plc = (PolylineConnection) f1;
				PointList pl = plc.getPoints();
				for (int j = 0; j < pl.size() - 1; j++) {
					Point p = new Point(pl.getPoint(j).x + Math.abs(ix), pl
							.getPoint(j).y
							+ Math.abs(iy));
					Point p1 = new Point(pl.getPoint(j + 1).x + Math.abs(ix),
							pl.getPoint(j + 1).y + Math.abs(iy));
					g2.setBackground(white);
					g2.setForeground(gray);
					g2.setLineStyle(SWT.LINE_SOLID);
					g2.drawLine((int) (p.x * viewScale) + zeroX,
							(int) (p.y * viewScale) + zeroY,
							(int) (p1.x * viewScale) + zeroX,
							(int) (p1.y * viewScale) + zeroY);
				}
			} else {
				if (f1 instanceof GroupFigure) {
					GroupFigure group = (GroupFigure) f1;
					g2.setForeground(black);
					if (group.group.isPattern()) {
						g2.setBackground(lightBlueColor);
					} else if (group.group.isConfirmed()) {
						g2.setBackground(yellowColor);
					} else {
						g2.setBackground(lightGrayColor);
					}
					g2.fillRectangle(rs.x, rs.y, rs.width, rs.height);
					g2.drawRectangle(rs.x, rs.y, rs.width, rs.height);
				} else {
					if (f1 instanceof Label) {
						String s = ((Label) f1).getText();
						Font ff = f1.getFont();
						FontData fd = ff.getFontData()[0];
						int nn = this.getTrueHeight((Label) f1, viewScale);
						if (nn > 0) {
							fd.setHeight(nn);
							Font f2 = new Font(pe.display, fd);
							g2.setFont(f2);
							g2.setBackground(white);
							g2.drawString(s, rs.x, rs.y);
							f2.dispose();
						}
					}
				}
			}
		}

		g2.setBackground(black);
		g2.setForeground(black);
		g2.setLineStyle(SWT.LINE_DOT);

		g2.drawLine(xmax + zeroX, zeroY, xmax + zeroX, ymax + zeroY);
		g2.drawLine(zeroX, ymax + zeroY, xmax + zeroX, ymax + zeroY);

		int tmp = ymax;

		List<Integer> xx = new ArrayList<Integer>();
		List<Integer> yy = new ArrayList<Integer>();

		while (tmp > 0) {
			g2
					.drawLine(zeroX, tmp - pH + zeroY, xmax + zeroX, tmp - pH
							+ zeroY);
			tmp = tmp - pH;
			yy.add(Integer.valueOf(tmp));
		}

		tmp = xmax;

		while (tmp > 0) {
			g2
					.drawLine(tmp - pW + zeroX, zeroY, tmp - pW + zeroX, ymax
							+ zeroY);
			tmp = tmp - pW;
			xx.add(Integer.valueOf(tmp));
		}

		List<Rectangle> rec = new ArrayList<Rectangle>();

		for (int i = 0; i < yy.size(); i++) {
			for (int j = 0; j < xx.size(); j++) {
				rec.add(new Rectangle(((Integer) xx.get(j)).intValue() + zeroX,
						((Integer) yy.get(i)).intValue() + zeroY, pW, pH));
			}
		}
		Object[] rn = rec.toArray();

		for (int i = 0; i < pages.getSourcePagesCount(); i++) {
			if (!pages.getSourcePage(i).isSelected()) {
				Rectangle sr = pages.getSourcePage(i).getRectangle();
				Point p = new Point((int) Math.round(sr.x * viewScale + pW / 2
						+ zeroX), (int) Math.round(sr.y * viewScale + pH / 2
						+ zeroY));
				for (int j = 0; j < rn.length; j++) {
					Rectangle rnt = (Rectangle) rn[j];
					if (rnt.contains(p)) {
						g2.setXORMode(true);
						g2.setBackground(pe.display
								.getSystemColor(SWT.COLOR_DARK_GRAY));
						Rectangle nr = new Rectangle(rnt.x, rnt.y, pW + 1,
								pH + 1);
						g2.fillRectangle(nr);
						g2.setXORMode(false);
					}
				}
			}
		}

		g2.dispose();
		pe.gc.dispose();
	}

	public void setViewScale(double viewScale) {
		this.viewScale = viewScale;
	}

	public double getViewScale() {
		return this.viewScale;
	}

	public Dimension getMinimumSize() {
		return getPreferredSize();
	}

	public Dimension getMaximumSize() {
		return getPreferredSize();
	}

	public Dimension getPreferredSize() {
		return new Dimension((int) (pages.getDimension().width * viewScale),
				(int) (pages.getDimension().height * viewScale));
	}

	public Point getSize() {
		return new Point(this.getPreferredSize().width,
				this.getPreferredSize().height);
	}

	public int getWidth() {
		return this.getPreferredSize().width;
	}

	public int getHeight() {
		return this.getPreferredSize().height;
	}

	public Rectangle getBounds() {
		return new Rectangle(this.getLocation().x, this.getLocation().y, this
				.getPreferredSize().width, this.getPreferredSize().height);
	}

	public void setSelectionEnabled(boolean value) {
		this.selectionEnabled = value;
	}

	public void mouseDoubleClick(MouseEvent e) {
	}

	public void mouseDown(MouseEvent e) {
		if (selectionEnabled) {
			Point p = new Point(e.x, e.y);
			for (int i = 0; i < this.pages.getSourcePagesCount(); i++) {
				Rectangle nr = new Rectangle(
						(int) (pages.getSourcePage(i).getRectangle().x * viewScale)
								+ zeroX,
						(int) (pages.getSourcePage(i).getRectangle().y * viewScale)
								+ zeroY,
						(int) (pages.getSourcePage(i).getRectangle().width * viewScale),
						(int) (pages.getSourcePage(i).getRectangle().height * viewScale));
				if (nr.contains(p)) {
					pages.getSourcePage(i).setSelected(
							!pages.getSourcePage(i).isSelected());
					this.redraw();
				}
			}
		}
	}

	public void mouseUp(MouseEvent e) {
	}

}
