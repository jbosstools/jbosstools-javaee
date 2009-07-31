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
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Layer;
import org.eclipse.gef.GraphicalViewer;
import org.eclipse.gef.LayerConstants;
import org.eclipse.gef.editparts.LayerManager;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;

import org.jboss.tools.jsf.ui.editor.figures.DiagramFigure;

public class Pages {
	public Image printImage = null;
	GraphicalViewer viewer = null;
	double scale = 1;
	int width = 0;
	int height = 0;
	PageFormat pageFormat = null;
	List<Page> mPages;
	List<Page> sPages = null;
	PropertyChangeSupport pcs = new PropertyChangeSupport(this);
	PropertyChangeListener pcl;
	int countX = 1;
	int countY = 1;
	boolean isText = false;
	String[] text;
	List<IFigure> figures = new Vector<IFigure>();
	public int ix = 0;
	public int iy = 0;

	public Pages(GraphicalViewer c, PageFormat pageFormat) {
		this(c, PageFormat.printScale, pageFormat, true);
	}

	public int computeStringWidth(Font f, String str) {
		/* METHOD NOT USED */
		/*
		 * FontData.data is platform dependent
		 * (org.eclipse.swt.internal.win32.LOGFONT)
		 */
		return 0;
	}

	public void setPrintImage(Image printImage) {
		this.printImage = printImage;
	}

	public Image getPrintImage() {
		return this.printImage;
	}

	public int calculateMaxWidth(String str, Font f, int width) {
		int w = computeStringWidth(f, str);
		while (w > width) {
			str = str.substring(0, str.length() - 1);
			w = computeStringWidth(f, str);
		}
		return str.length();
	}

	public int calculateMaxStringCount(int height, Font f, int spacing) {
		int hs = spacing + f.getFontData()[0].getHeight();
		int count = (int) Math.floor(height / hs);
		if (count > 1) {
			count--;
		}
		return count;
	}

	public void getChilds(IFigure f) {
		for (Iterator i = f.getChildren().iterator(); i.hasNext();) {

			IFigure o = (IFigure) i.next();
			if (!(o instanceof DiagramFigure) && !(o instanceof Layer)) {
				figures.add(o);
			}
			getChilds(o);

		}
	}

	public Dimension getTrueSize() {
		List v = figures;
		Dimension d = new Dimension(0, 0);
		for (int i = 0; i < v.size(); i++) {
			IFigure c = (IFigure) v.get(i);
			if (c.getBounds().x + c.getBounds().width > d.width) {
				d.setSize(c.getBounds().x + c.getBounds().width, d.height);
			}
			if (c.getBounds().y + c.getBounds().height > d.height) {
				d.setSize(d.width, c.getBounds().y + c.getBounds().height);
			}
			if (c.getBounds().x < ix)
				ix = c.getBounds().x;
			if (c.getBounds().y < iy)
				iy = c.getBounds().y;
		}
		d.width = d.width + Math.abs(ix);
		d.height = d.height + Math.abs(iy);
		return d;
	}

	public Pages(GraphicalViewer viewer, double scale, PageFormat pageFormat,
			boolean trueSize) {
		super();
		this.viewer = viewer;
		this.scale = scale;
		this.pageFormat = pageFormat;
		LayerManager lm = (LayerManager) viewer.getEditPartRegistry().get(
				LayerManager.ID);
		IFigure f = lm.getLayer(LayerConstants.PRINTABLE_LAYERS);
		getChilds(f);
		this.width = this.getTrueSize().width;
		this.height = this.getTrueSize().height;
		pcl = createSelectionListener();
		this.createPages();
	}

	public boolean isTextPrint() {
		return this.isText;
	}

	public PropertyChangeListener createSelectionListener() {
		return new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				if (evt.getPropertyName().equals("select")) { //$NON-NLS-1$
					if (((Boolean) evt.getNewValue()).booleanValue()) {
						if (!mPages.contains(evt.getSource()))
							mPages.add((Page) evt.getSource());
						pcs.firePropertyChange("PageSelection", false, true); //$NON-NLS-1$
						if (mPages.size() == sPages.size()) {
							pcs.firePropertyChange("selectAll", false, true); //$NON-NLS-1$
						}
					} else {
						mPages.remove((Page)evt.getSource());
						pcs.firePropertyChange("PageSelection", false, true); //$NON-NLS-1$
						if (mPages.size() == 0) {
							pcs.firePropertyChange("unSelectAll", false, true); //$NON-NLS-1$
						}
					}
				}
			}
		};
	}

	void createPages() {
		List<Page> oldPages = null;
		if (sPages != null) {
			oldPages = new ArrayList<Page>();
			oldPages.addAll(sPages);
		}
		this.mPages = new Vector<Page>();
		this.sPages = new Vector<Page>();

		int pWidth = (int) getPageFormat().getImageableWidth();
		int pHeight = (int) getPageFormat().getImageableHeight();
		int sWidth = (int) (this.getScale() * width);
		int sHeight = (int) (this.getScale() * height);
		int oldCountX = countX;
		int oldCountY = countY;
		countX = 1;
		countY = 1;
		if (sWidth > pWidth) {
			countX = (int) Math.round((sWidth / pWidth) + 0.5);
		}
		if (sHeight > pHeight) {
			countY = (int) Math.round((sHeight / pHeight) + 0.5);
		}
		for (int i = 0; i < countY; i++) {
			for (int j = 0; j < countX; j++) {
				Page newPage = new Page(this, new Rectangle((int) ((this
						.getPageFormat().getImageableWidth() * j) / this
						.getScale()), (int) ((this.getPageFormat()
						.getImageableHeight() * i) / this.getScale()),
						(int) (this.getPageFormat().getImageableWidth() / this
								.getScale()), (int) (this.getPageFormat()
								.getImageableHeight() / this.getScale())),
						this.viewer, this.getPageFormat(), this.getScale(),
						(int) (this.getPageFormat().getImageableWidth() * j),
						(int) (this.getPageFormat().getImageableHeight() * i),
						this.getSourcePagesCount() + 1);
				newPage.addPropertyChangeListener(pcl);
				sPages.add(newPage);
			}
		}
		if (oldCountX != countX || oldCountY != countY) {
			selectAll();
		} else {
			if (oldPages != null) {
				for (int i = 0; i < oldPages.size(); i++) {
					sPages.get(i).setSelected(oldPages.get(i).isSelected());
				}
			} else {
				selectAll();
			}
		}
	}

	public Page getSourcePage(int index) {
		return sPages.get(index);
	}

	public int getSourcePagesCount() {
		return sPages.size();
	}

	public PageFormat getPageFormat() {
		return this.pageFormat;
	}

	public double getScale() {
		return this.scale;
	}

	public void setScale(double scale) {
		double oldScale = this.scale;
		this.scale = scale;
		if (this.isTextPrint()) {
		} else {
			this.createPages();
		}
		pcs.firePropertyChange("scale", new Double(oldScale), new Double( //$NON-NLS-1$
						scale));
	}

	public void selectAll() {
		for (int i = 0; i < sPages.size(); i++) {
			sPages.get(i).setSelected(true);
		}
		pcs.firePropertyChange("selectAll", false, true); //$NON-NLS-1$
	}

	public void unSelectAll() {
		for (int i = 0; i < sPages.size(); i++) {
			sPages.get(i).setSelected(false);
		}
		pcs.firePropertyChange("unSelectAll", false, true); //$NON-NLS-1$
	}

	public void addPropertyChangeListener(PropertyChangeListener pcl) {
		pcs.addPropertyChangeListener(pcl);
	}

	public void removePropertyChangeListener(PropertyChangeListener pcl) {
		pcs.removePropertyChangeListener(pcl);
	}

	public GraphicalViewer getViewer() {
		return this.viewer;
	}

	public int getNumberOfPages() {
		return mPages.size();
	}

	public PageFormat getPageFormat(int pageIndex)
			throws IndexOutOfBoundsException {
		return mPages.get(pageIndex).getPageFormat();
	}

	public Page getPrintable(int pageIndex) throws IndexOutOfBoundsException {
		return mPages.get(pageIndex);
	}

	public Dimension getDimension() {
		return new Dimension(
				(int) (countX * pageFormat.getImageableWidth() / this
						.getScale()), (int) (countY
						* pageFormat.getImageableHeight() / this.getScale()));
	}

}