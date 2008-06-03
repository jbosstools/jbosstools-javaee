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
package org.jboss.tools.struts.ui.editor.print;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.printing.Printer;
import org.eclipse.swt.widgets.Display;

public class PageFormat {
	
	int imgWidth = 1;
	int imgHeight = 1;
	Printer printer;
	Display display;
	double scaleFactorX = 1;
	double scaleFactorY = 1;
	int fieldH = 40; 
	int fieldW = 40;
	public static double printScale = 1;
	
	public PageFormat(Printer printer, Display display){
		super();
		this.printer = printer;
		this.display = display;
		Point screenDPI = this.display.getDPI();
		Point printerDPI = this.printer.getDPI();
		this.imgWidth = printer.getClientArea().width;
		this.imgHeight = printer.getClientArea().height;
		if(screenDPI.x<=0)screenDPI.x=1;
		if(screenDPI.y<=0)screenDPI.y=1;
		this.scaleFactorX = printerDPI.x / screenDPI.x;
		this.scaleFactorY = printerDPI.y / screenDPI.y;
	}
	
	public PageFormat(int imgWidth, int imgHeight){
			super();
			this.imgWidth = imgWidth;
			this.imgHeight = imgHeight;
	}
		
	public int getImageableWidth(){
		return (int)(this.imgWidth/scaleFactorX)-this.fieldW;
	}
	
	public int getImageableHeight(){
		return (int)(this.imgHeight/scaleFactorY)-this.fieldH;
	}
	
	public Printer getPrinter(){
	 	return this.printer;
	}
	
	public void setPrinter(Printer printer){
		this.printer = printer;
	}
	
	public Display getDisplay(){
			return this.display;
		}
	
	public void setDisplay(Display display){
			this.display = display;
	}
}
