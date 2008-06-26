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

public class Page{
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

   public Page(Pages pages,String[] text, Font f, int space,PageFormat pf, int number){
	  this.textPrint = true;
	  this.text = text;
	  this.font = f;
	  this.space = space;
	 // this.component = null;
	  this.pageFormat = pf;
	  this.number = number;
	  this.pages = pages;
}

   public Page(Pages pages,Rectangle r,GraphicalViewer viewer,PageFormat pf, double scale, int x, int y, int number){
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
   
   public void printText(){
   	
   	
   }
   
   public void printImage(){
   	 
   	  PageFormat pf = this.getPageFormat();
   	  Printer printer = pf.getPrinter();
	  Point screenDPI = pf.getDisplay().getDPI();
	  Point printerDPI = printer.getDPI();
	  int scaleFactorX = printerDPI.x / screenDPI.x;
	  int scaleFactorY = printerDPI.y / screenDPI.y;
	  Rectangle trim = printer.computeTrim(0, 0, 0, 0);
	//  Point size = this.component.getSize();
	
	  /*
	  Image displayImage = this.pages.getPrintImage();  
	  ImageData imageData = displayImage.getImageData();
	  Image printImage = new Image(printer, imageData);
	  */
	  
	  if (printer.startPage()) {
	  	  Image displayImage = this.pages.getPrintImage();  
		  ImageData imageData = displayImage.getImageData();
		  Rectangle real = new Rectangle(this.x,this.y,pf.getImageableWidth(),pf.getImageableHeight());
		  GC gp = new GC(printer);
		  if((int)(imageData.width*this.scale)<=real.width&&(int)(imageData.height*this.scale)<=real.height){
			gp.drawImage(displayImage,0,0,imageData.width,imageData.height,-trim.x,-trim.y,(int)(scaleFactorX*imageData.width*this.scale),(int)(scaleFactorY*imageData.height*this.scale));
		  }else{
		  	Rectangle scaleReal = new Rectangle((int)(real.x/this.scale),(int)(real.y/this.scale),(int)(real.width/this.scale),(int)(real.height/this.scale));
			Image newImg = new Image(pf.getDisplay(),displayImage.getImageData());
			GC tgc = new GC(newImg);
			Image tmpImg = new Image(pf.getDisplay(),scaleReal.width,scaleReal.height);
			tgc.copyArea(tmpImg,scaleReal.x,scaleReal.y);
			tgc.dispose();
			displayImage = new Image(printer,tmpImg.getImageData());
			imageData = displayImage.getImageData();
			gp.drawImage(displayImage,0,0,imageData.width,imageData.height,-trim.x,-trim.y,(int)(scaleFactorX*imageData.width*this.scale),(int)(scaleFactorY*imageData.height*this.scale));
			
			
			
			
			
			
			//Image pi = new Image(printer,tmpImg.getImageData());
			//imageData = displayImage.getImageData();
		  	//ImageData id = tmpImg.getImageData();
			//gp.drawImage(tmpImg,0,0,id.width,id.height,-trim.x,-trim.y,(int)(scaleFactorX*id.width*this.scale),(int)(scaleFactorY*id.height*this.scale));
			//gp.drawImage(pi,0,0,id.width,id.height,-trim.x,-trim.y,(int)(scaleFactorX*id.width),(int)(scaleFactorY*id.height));
			
			/*
			Rectangle scaleReal = new Rectangle((int)(real.x/this.scale),(int)(real.y/this.scale),(int)(real.width/this.scale),(int)(real.height/this.scale));
			displayImage = new Image(pf.getDisplay(),scaleReal.width,scaleReal.height);
			imageData = displayImage.getImageData();
			GC gcn = new GC(displayImage);
			gcn.setBackground(new Color(pf.getDisplay(),0,0,0));
			gcn.fillRectangle(0,0,200,200);
			gcn.dispose();
			displayImage = new Image(printer,displayImage.getImageData());
			imageData = displayImage.getImageData();
			gp.drawImage(displayImage,0,0,imageData.width,imageData.height,-trim.x,-trim.y,(int)(scaleFactorX*imageData.width),(int)(scaleFactorY*imageData.height));
			//gp.drawImage(displayImage,0,0);
			 
			 */
		  }
		  gp.dispose();
		  printer.endPage();
	  }
		  
		  
		  /*
		  Image tmp = new Image(printer,imageData);
		  GC dgc = new GC(tmp);
		  Image tmp2 = new Image(printer,this.getRectangle().width,this.getRectangle().height);
		  dgc.copyArea(tmp2,this.getRectangle().x,this.getRectangle().y);
		  dgc.dispose();
		imageData = tmp2.getImageData();
		  */
		//  Image printImage = new Image(printer, imageData);
		  //printImage.getImageData().width = this.getRectangle().width;
		  //printImage.getImageData().height = this.getRectangle().height;
		//Image pi = new Image(printer,imageData.scaledTo((int)(imageData.width*this.scale),(int)(imageData.height*this.scale)));
		//imageData = pi.getImageData();
		
		
		
		
		
		/*
		Rectangle real = new Rectangle(this.x,this.y,pf.getImageableWidth(),pf.getImageableHeight());
		Image realImg = new Image(pf.getDisplay(),real.width,real.height);
		
		Image pi = new Image(pf.getDisplay(),imageData.scaledTo((int)(imageData.width*this.scale),(int)(imageData.height*this.scale)));
		
		if(pi.getImageData().width<=real.width&&pi.getImageData().height<=real.height){
					GC realGC = new GC(realImg);
					realGC.drawImage(pi,0,0);
					realGC.dispose();
		}
		*/
		/*
		Image pi = new Image(pf.getDisplay(), (int)(imageData.width*this.scale),(int)(imageData.height*this.scale));
		GC ggg = new GC(pi);
		//ggg.setBackground(new Color(printer,0,0,0));
		//ggg.fillRectangle(0,0,100,100);
		ggg.drawImage(displayImage,0,0,imageData.width,imageData.height,0,0,(int)(imageData.width*this.scale),(int)(imageData.height*this.scale));
		ggg.dispose();
		Image realImg = new Image(pf.getDisplay(),real.width,real.height);
		if(pi.getImageData().width<=real.width&&pi.getImageData().height<=real.height){
			GC realGC = new GC(realImg);
			//Image npi = new Image(pf.getDisplay(),pi.getImageData());
			//realGC.drawImage(pi,0,0,pi.getImageData().width,pi.getImageData().height,0,0,pi.getImageData().width,pi.getImageData().height);
			realGC.drawImage(pi,0,0);
			realGC.dispose();
		}
		*/
		
	//	printImage = new Image(printer, pi.getImageData());
	//	imageData = printImage.getImageData();
		  //GC gcp2 = new GC(printer);
		  //Image ni = new Image(printer,pf.getImageableWidth(),pf.getImageableHeight());
		  //gcp2.copyArea(ni,this.x,this.y);
		  //gcp2.drawImage(printImage,this.x,this.y,pf.getImageableWidth(),pf.getImageableHeight(),this.getRectangle().x,this.getRectangle().y,this.getRectangle().width,this.getRectangle().height);
		 // gcp2.drawImage(printImage,0,0,imageData.width,imageData.height,-trim.x,-trim.y,scaleFactorX * imageData.width,scaleFactorY * imageData.height);
		//gcp2.drawImage(printImage,0,0,(int)(imageData.width*this.scale),(int)(imageData.height*this.scale),-trim.x,-trim.y,scaleFactorX * imageData.width,scaleFactorY * imageData.height);
		//gcp2.drawImage(printImage,0,0,imageData.width,imageData.height,-trim.x,-trim.y,scaleFactorX * imageData.width,scaleFactorY * imageData.height);
		
		//gcp2.drawImage(printImage,0,0,imageData.width,imageData.height,0,0,scaleFactorX*imageData.width,scaleFactorY*imageData.height);
		 //gcp2.drawImage(printImage,0,0,imageData.width,imageData.height,-trim.x,-trim.y,scaleFactorX*imageData.width,scaleFactorY*imageData.height);
		//gcp2.drawImage(printImage,0,0,imageData.width,imageData.height,-trim.x,-trim.y,(int)(this.getRectangle().width/scaleFactorX),(int)(this.getRectangle().height/scaleFactorY));
		  //gcp2.drawImage(printImage,0,0,this.getRectangle().width,this.getRectangle().height,-trim.x,-trim.y,(int)(this.getRectangle().width*scaleFactorX),(int)(this.getRectangle().height*scaleFactorY));
		 // gcp2.dispose();
		  
	  
	  
   }
   
   
   /*
   public int print(GC g, PageFormat pf, int pageIndex){
	 
	  Graphics2D printG = (Graphics2D)g;
	  if(this.component==null){
		 int dy = (int)pf.getImageableY();
		 int dx = (int)pf.getImageableX()+2;
		 printG.setColor(Color.black);
		 printG.setFont(new Font("Dialog",Font.PLAIN,10));
		 printG.drawString(String.valueOf(number),dx+(int)pf.getImageableWidth()-SwingUtilities.computeStringWidth(Toolkit.getDefaultToolkit().getFontMetrics(new Font("Dialog",Font.PLAIN,10)),String.valueOf(number)),dy+(int)pf.getImageableHeight());
		 dy=dy+font.getSize()+this.space;
		 printG.setColor(Color.black);
		 printG.setFont(font);
		 for(int i=0; i<text.length; i++){
			printG.drawString(text[i],dx,dy);
			dy=dy+font.getSize()+space;
		 }
	  }else{
		 printG = (Graphics2D)printG.create((int)pf.getImageableX(),(int)pf.getImageableY(),(int)pf.getImageableWidth(),(int)pf.getImageableHeight());
		 printG.setRenderingHints(this.qualityHints);
		 printG.translate(-x,-y);
		 printG.setClip(x,y,(int)pf.getImageableWidth(),(int)pf.getImageableHeight());
		 printG.scale(this.scale,this.scale);
		 this.component.print(printG);
		 printG.scale(1/this.scale,1/this.scale);
		 printG.setClip(printG.getClipBounds().x,printG.getClipBounds().y,printG.getClipBounds().width,printG.getClipBounds().height+10);
		 drawPageInfo(printG,pf);
	  }
	  return Printable.PAGE_EXISTS;
	  
	  return 0;
   }
*/
   public void drawPageInfo(GC g2, PageFormat pf){
/*
	  g2.setColor(Color.black);
	  g2.setStroke(this.stroke);
	  g2.drawLine(x,y,x,y+20);
	  g2.drawLine(x,y,x+20,y);
	  g2.drawLine(x+(int)pf.getImageableWidth()-20,y,x+(int)pf.getImageableWidth(),y);
	  g2.drawLine(x+(int)pf.getImageableWidth(),y,x+(int)pf.getImageableWidth(),y+20);
	  g2.drawLine(x+(int)((pf.getImageableWidth()/2)-10),y,x+(int)((pf.getImageableWidth()/2)+10),y);
	  g2.drawLine(x+(int)((pf.getImageableWidth()/2)-10),y+(int)pf.getImageableHeight(),x+(int)((pf.getImageableWidth()/2)+10),y+(int)pf.getImageableHeight());
	  g2.drawLine(x,y+(int)((pf.getImageableHeight()/2)-10),x,y+(int)((pf.getImageableHeight()/2)+10));
	  g2.drawLine(x+(int)pf.getImageableWidth(),y+(int)((pf.getImageableHeight()/2)-10),x+(int)pf.getImageableWidth(),y+(int)((pf.getImageableHeight()/2)+10));
	  g2.drawLine(x,y+(int)pf.getImageableHeight()-20,x,y+(int)pf.getImageableHeight());
	  g2.drawLine(x,y+(int)pf.getImageableHeight(),x+20,y+(int)pf.getImageableHeight());
	  g2.drawLine(x+(int)pf.getImageableWidth()-20,y+(int)pf.getImageableHeight(),x+(int)pf.getImageableWidth(),y+(int)pf.getImageableHeight());
	  g2.drawLine(x+(int)pf.getImageableWidth(),y+(int)pf.getImageableHeight(),x+(int)pf.getImageableWidth(),y+(int)pf.getImageableHeight()-20);
	  g2.setColor(Color.black);
	  g2.setFont(new Font("Dialog",Font.PLAIN,10));
	  g2.drawString(String.valueOf(number),x+(int)pf.getImageableWidth()-SwingUtilities.computeStringWidth(Toolkit.getDefaultToolkit().getFontMetrics(new Font("Dialog",Font.PLAIN,10)),String.valueOf(number)),y+(int)pf.getImageableHeight()+10);
  */
    }

   public void setRectangle(Rectangle r){
	  this.rect = r;
   }

   public Rectangle getRectangle(){
	  return this.rect;
   }

   public void setSelected(boolean select){
	  boolean oldValue = this.select;
	  this.select = select;
	  pcs.firePropertyChange("select",oldValue,select);
   }

   public boolean isSelected(){
	  return this.select;
   }

   public void addPropertyChangeListener(PropertyChangeListener pcl){
	  pcs.addPropertyChangeListener(pcl);
   }

   public void removePropertyChangeListener(PropertyChangeListener pcl){
	  pcs.removePropertyChangeListener(pcl);
   }

   public int getNumber() {
	  return this.number;
   }

   public PageFormat getPageFormat() {
	  return this.pageFormat;
   }
}