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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.eclipse.gef.GraphicalViewer;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.printing.Printer;

public class Page {
	Printer printer;
	GraphicalViewer viewer = null;
	double scale = 1;
	int width = 0;
	int height = 0;
	int x = 0;
	int y = 0;
	int number = 1;
	Font font;
	int space = 0;
	String[] text;
	PageFormat pageFormat = null;
	Rectangle rect;
	boolean select = false;
	PropertyChangeSupport pcs = new PropertyChangeSupport(this);
	boolean textPrint = false;
	public Pages pages = null;

	public Page(Pages pages, String[] text, Font f, int space, PageFormat pf,
			int number) {
		this.textPrint = true;
		this.text = text;
		this.font = f;
		this.space = space;
		this.pageFormat = pf;
		this.number = number;
		this.pages = pages;
	}

	public Page(Pages pages, Rectangle r, GraphicalViewer viewer,
			PageFormat pf, double scale, int x, int y, int number) {
		this.rect = r;
		this.viewer = viewer;
		this.pageFormat = pf;
		this.scale = scale;
		this.x = x;
		this.y = y;
		this.number = number;
		this.textPrint = false;
		this.pages = pages;
	}

	public void printText() {

	}

	public void printImage() {

		PageFormat pf = this.getPageFormat();
		Printer printer = pf.getPrinter();
		Point screenDPI = pf.getDisplay().getDPI();
		Point printerDPI = printer.getDPI();
		int scaleFactorX = printerDPI.x / screenDPI.x;
		int scaleFactorY = printerDPI.y / screenDPI.y;
		Rectangle trim = printer.computeTrim(0, 0, 0, 0);

		if (printer.startPage()) {
			Image displayImage = this.pages.getPrintImage();
			ImageData imageData = displayImage.getImageData();
			Rectangle real = new Rectangle(this.x, this.y, pf
					.getImageableWidth(), pf.getImageableHeight());
			GC gp = new GC(printer);
			if ((int) (imageData.width * this.scale) <= real.width
					&& (int) (imageData.height * this.scale) <= real.height) {
				gp.drawImage(displayImage, 0, 0, imageData.width,
						imageData.height, -trim.x, -trim.y, (int) (scaleFactorX
								* imageData.width * this.scale),
						(int) (scaleFactorY * imageData.height * this.scale));
			} else {
				Rectangle scaleReal = new Rectangle(
						(int) (real.x / this.scale),
						(int) (real.y / this.scale),
						(int) (real.width / this.scale),
						(int) (real.height / this.scale));
				Image newImg = new Image(pf.getDisplay(), displayImage
						.getImageData());
				GC tgc = new GC(newImg);
				Image tmpImg = new Image(pf.getDisplay(), scaleReal.width,
						scaleReal.height);
				tgc.copyArea(tmpImg, scaleReal.x, scaleReal.y);
				tgc.dispose();
				displayImage = new Image(printer, tmpImg.getImageData());
				imageData = displayImage.getImageData();
				gp.drawImage(displayImage, 0, 0, imageData.width,
						imageData.height, -trim.x, -trim.y, (int) (scaleFactorX
								* imageData.width * this.scale),
						(int) (scaleFactorY * imageData.height * this.scale));
			}
			gp.dispose();
			printer.endPage();
		}

	}

	public void drawPageInfo(GC g2, PageFormat pf) {
	}

	public void setRectangle(Rectangle r) {
		this.rect = r;
	}

	public Rectangle getRectangle() {
		return this.rect;
	}

	public void setSelected(boolean select) {
		boolean oldValue = this.select;
		this.select = select;
		pcs.firePropertyChange("select", oldValue, select); //$NON-NLS-1$
	}

	public boolean isSelected() {
		return this.select;
	}

	public void addPropertyChangeListener(PropertyChangeListener pcl) {
		pcs.addPropertyChangeListener(pcl);
	}

	public void removePropertyChangeListener(PropertyChangeListener pcl) {
		pcs.removePropertyChangeListener(pcl);
	}

	public int getNumber() {
		return this.number;
	}

	public PageFormat getPageFormat() {
		return this.pageFormat;
	}

}
